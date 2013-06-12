package com.ModDamage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.ModDamage.PluginConfiguration.DebugSetting;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.Events.Init;
import com.ModDamage.Magic.MagicStuff;
import com.ModDamage.Server.MDServer;
import com.ModDamage.Tags.TagManager;

/**
 * "ModDamage" for Bukkit
 * 
 * @authors Erich Gubler, Matt Peterson <ricochet1k@gmail.com>
 * 
 */
public class ModDamage extends JavaPlugin
{
	protected static PluginConfiguration configuration;

	public static boolean isEnabled = false;
	private static final String errorString_Permissions = chatPrepend(ChatColor.RED) + "You don't have access to that command.";

	private static TagManager tagger = null;
	

	// //////////////////////// INITIALIZATION
	@Override
	public void onLoad() {
		super.onLoad(); //Just in case bukkit loads stuff in here.
		configuration = new PluginConfiguration(this); //Fixes NPE on registering extensions from onLoad in other plugins.
	}

	@Override
	public void onEnable()
	{
		PluginCommand.setPlugin(this);
		isEnabled = true;
		MagicStuff.init();
		reload(true);
		
		try
		{
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} 
		catch (IOException e) 
		{
		    // Failed to submit the stats :-(
		}
	}

	@Override
	public void onDisable()
	{
		MDServer.stopServer();
		
		if (tagger != null) tagger.close();
		isEnabled = false;
		configuration.printToLog(Level.INFO, "Disabled.");
		PluginCommand.setPlugin(null); //Prevents possible memory leaks on /reload command
	}

	public void reload(boolean reloadingAll)
	{
		File taggerFile = (tagger != null)? tagger.file : new File(this.getDataFolder(), "tags.yml");
		
		if((configuration.reload(reloadingAll) && reloadingAll) || !taggerFile.exists())
		{
			if(tagger != null) tagger.close();

			long[] tagConfigIntegers = { TagManager.defaultInterval, TagManager.defaultInterval * 4 };
			LinkedHashMap<String, Object> tagConfigurationTree = configuration.castToStringMap("Tagging", configuration.getConfigMap().get("Tagging"));
			if(tagConfigurationTree != null)
			{
				String[] tagConfigStrings = { TagManager.configString_save, TagManager.configString_clean };
				Object[] tagConfigObjects =	{PluginConfiguration.getCaseInsensitiveValue(tagConfigurationTree, tagConfigStrings[0]), PluginConfiguration.getCaseInsensitiveValue(tagConfigurationTree, tagConfigStrings[1]) };
				for(int i = 0; i < tagConfigObjects.length; i++)
				{
					if(tagConfigObjects[i] != null)
					{
						if(tagConfigObjects[i] instanceof Integer)
							tagConfigIntegers[i] = (Integer)tagConfigObjects[i];
						else configuration.addToLogRecord(OutputPreset.FAILURE, "Error: Could not read value for Tagging setting \"" + tagConfigStrings[i] + "\"");
					}
				}
			}
			tagger = new TagManager(taggerFile, tagConfigIntegers[0], tagConfigIntegers[1]);
			
		}

        Init.initAll();
	}

	// //COMMAND PARSING ////
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args)
	{
		Player player = ((sender instanceof Player) ? ((Player) sender) : null);
		if(args.length == 0) sendCommandUsage(player, false);
		else if(args.length >= 0)
		{
			String commandString = "";
			for(String arg : args)
				commandString += " " + arg;
			PluginCommand.handleCommand(player, commandString);
		}
		return true;
	}

	private enum PluginCommand
	{
//		CHECK(false, "\\sc(?:heck)?(\\s\\d+)?", "/md (check | c) - check configuration")
//		{
//			@Override
//			protected void handleCommand(Player player, Matcher matcher)
//			{
//				if(player == null)
//				{
//					configuration.printToLog(Level.INFO, "Complete log record for this server:");
//					configuration.sendLogRecord(null, 9001);
//					configuration.printToLog(Level.INFO, "End of log record.");
//				}
//				else
//				{
//					if(matcher.group(1) == null)
//					{
//						if(hasPermission(player, "moddamage.check"))
//							configuration.sendLogRecord(player, 0);
//					}
//					else configuration.sendLogRecord(player, Integer.parseInt(matcher.group(1).substring(1)));
//				}
//			}
//		},
		DEBUG(false, "\\sd(?:ebug)?(?:\\s(\\w+))?", "/md (debug | d) [debugType] - change debug type")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				if(matcher.group(1) != null)
				{
					for(DebugSetting setting : DebugSetting.values())
						if(matcher.group(1).equalsIgnoreCase(setting.name()))
						{
							configuration.setDebugging(player, setting);
							return;
						}
					sendMessage(player, "Invalid debugging mode \"" + matcher.group(1).substring(1) + "\" - modes are \"quiet\", \"normal\", and \"verbose\".", ChatColor.RED);
				}
				else configuration.toggleDebugging(player);
			}
		},
		RELOAD(false, "\\sr(?:eload)?(\\sall)?", "/md (reload | r) [all] - reload configuration.")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				boolean reloadingAll = matcher.group(1) != null;
				if(player != null) configuration.printToLog(Level.INFO, "Reload initiated by user " + player.getName() + "...");
				plugin.reload(reloadingAll);
				if(player != null)
					switch(LoadState.pluginState)
					{
						case SUCCESS:
							int worstValue = configuration.worstLogMessageLevel.intValue();
							
							if (worstValue >= Level.SEVERE.intValue()) {
								player.sendMessage(chatPrepend(ChatColor.YELLOW) + "Reloaded with errors.");
							}
							else if (worstValue >= Level.WARNING.intValue()) {
								player.sendMessage(chatPrepend(ChatColor.YELLOW) + "Reloaded with warnings.");
							}
							else if (worstValue >= Level.INFO.intValue()) {
								player.sendMessage(chatPrepend(ChatColor.GREEN) + "Reloaded!");
							}
							else {
								player.sendMessage(chatPrepend(ChatColor.YELLOW) + "Weird reload: " + configuration.worstLogMessageLevel);
							}
							
							break;
						case FAILURE:
							player.sendMessage(chatPrepend(ChatColor.YELLOW) + "Reloaded with errors.");
							break;
						case NOT_LOADED:
							player.sendMessage(chatPrepend(ChatColor.GRAY) + "No configuration loaded! Are any routines defined?");
							break;
							
						default: throw new Error("Unknown state: "+LoadState.pluginState+" $MD176");
					}
			}
		},
		STATUS(false, "\\s(?:en|dis)able", "/md (disable|enable) - disable/enable ModDamage")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				ModDamage.setPluginStatus(player, matcher.group().equalsIgnoreCase(" enable"));
			}
		},
		TAGS(true, "\\st(?:ags)?\\s(clear|save)", "/md tags (save|clear) - save/clear tags")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				if(matcher.group(1).equalsIgnoreCase("clear"))
				{
					tagger.clear();
					sendMessage(player, "Tags cleared.", ChatColor.GREEN);
				}
				else
				{
					tagger.save();
					sendMessage(player, "Tags saved.", ChatColor.GREEN);
				}
			}
		};
		
		private final boolean needsEnable;
		private final Pattern pattern;
		protected final String help;

		private PluginCommand(boolean needsEnable, String pattern, String help)
		{
			this.needsEnable = needsEnable;
			this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			this.help = help;
		}

		public static void handleCommand(Player player, String commandString)
		{
			for(PluginCommand command : PluginCommand.values())
			{
				Matcher matcher = command.pattern.matcher(commandString);
				if(matcher.matches() && hasPermission(player, "moddamage." + command.name().toLowerCase()))
				{
					if(!command.needsEnable || isEnabled)
						command.handleCommand(player, matcher);
					else sendMessage(player, "ModDamage must be enabled to use that command.", ChatColor.RED);
					return;
				}
			}
			sendCommandUsage(player, true);
		}

		abstract protected void handleCommand(Player player, Matcher matcher);

		private static ModDamage plugin;
		protected static void setPlugin(ModDamage plugin){ PluginCommand.plugin = plugin; }
	}

	private static boolean hasPermission(Player player, String permission)
	{
		if (player == null) return true; // console
		
		boolean has = player.hasPermission(permission);
		if(!has) player.sendMessage(errorString_Permissions);
		return has;
	}

	static void sendMessage(Player player, String message, ChatColor color)
	{
		if(player != null)
			player.sendMessage(chatPrepend(color) + message);
		else configuration.printToLog(Level.INFO, message);
	}

	static String chatPrepend(ChatColor color){ return color + "[" + ChatColor.DARK_RED + "Mod" + ChatColor.DARK_BLUE + "Damage" + color + "] "; }

	private static void setPluginStatus(Player player, boolean status)
	{
		if(status != isEnabled)
		{
			isEnabled = status;
			configuration.printToLog(Level.INFO, "Plugin " + (isEnabled ? "en" : "dis") + "abled.");
			if(player != null)
				player.sendMessage(chatPrepend(ChatColor.GREEN) + "Plugin " + (isEnabled ? "en" : "dis") + "abled.");
		}
		else sendMessage(player, "Already " + (isEnabled ? "en" : "dis") + "abled!", ChatColor.RED);
	}

	private static void sendCommandUsage(Player player, boolean forError)
	{
		if(player != null)
		{
			if(forError) player.sendMessage(ChatColor.RED + "Error: invalid command syntax.");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "ModDamage commands:");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "[] optional | () required");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/moddamage | /md - bring up this help message");
			for (PluginCommand cmd:PluginCommand.values())
				player.sendMessage(ChatColor.LIGHT_PURPLE +cmd.help);
		}
		else
		{
			StringBuffer sb = new StringBuffer().append("ModDamage commands:\n").append("/moddamage | /md - bring up this help message");
			for (PluginCommand cmd:PluginCommand.values())
				sb.append("\n").append(cmd.help);
			if(forError) configuration.printToLog(Level.SEVERE, "Error: invalid command syntax.");
			configuration.printToLog(Level.INFO, sb.toString());
		}
	}

///////////////// HELPER FUNCTIONS
	public static void addToLogRecord(OutputPreset preset, String message){ configuration.addToLogRecord(preset, message); }
	
	public static void changeIndentation(boolean forward)
	{
		if(forward) PluginConfiguration.indentation++;
		else PluginConfiguration.indentation--;
	}

	public static DebugSetting getDebugSetting(){ return configuration.currentSetting; }

	public static TagManager getTagger(){ return tagger; }

	public static PluginConfiguration getPluginConfiguration(){ return configuration; }

	public static final HashSet<Material> goThroughThese = new HashSet<Material>(Arrays.asList(
			Material.AIR,
			Material.GLASS,
			Material.LADDER,
			Material.TORCH,
			Material.REDSTONE_TORCH_ON,
			Material.REDSTONE_TORCH_OFF,
			Material.STONE_BUTTON,
			Material.SIGN_POST,
			Material.WALL_SIGN,
			Material.FIRE,
			Material.LEVER));
	
///////////////// EXTERNAL PLUGINS

	public interface ModDamageExtension
	{
		public PluginDescriptionFile getDescription(); // For reload data purpose
		public void reloadRoutines();//Register routines with the Routine/NestedRoutine libraries.
	}
	
	public static void registerExtension(ModDamageExtension extension)
	{
		ExternalPluginManager.registerExtension(extension);
	}

	public static void reportBailException(BailException bailException)
	{
		if (!bailException.suppress)
		{
			System.err.println("A serious error has occurred in ModDamage:\n"+bailException.toString());
			System.err.println("Please report this error.");
		}
	}
}