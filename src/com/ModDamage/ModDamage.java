package com.ModDamage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.ModDamage.PluginConfiguration.DebugSetting;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;

/**
 * "ModDamage" for Bukkit
 * 
 * @authors Erich Gubler, Matt Peterson <ricochet1k@gmail.com>
 * 
 */
public class ModDamage extends JavaPlugin
{
	public final static int oldestSupportedBuild = 1597;
	// -Triggered effects...should be a special type of tag! :D Credit: ricochet1k
	// -AoE clearance, block search nearby for Material?
	// -find a way to give players ownership of an explosion?
	// -Deregister when Bukkit supports!
	protected static PluginConfiguration configuration;
	protected static List<ModDamageExtension> extensions = new ArrayList<ModDamageExtension>();

	public static boolean isEnabled = false;
	private static final String errorString_Permissions = chatPrepend(ChatColor.RED) + "You don't have access to that command.";

	private static ModDamageTagger tagger = null;

	// //////////////////////// INITIALIZATION
	@Override
	public void onEnable()
	{
		// register plugin-related stuff with the server's plugin manager
		for (ModDamageEventHandler eventHandler : ModDamageEventHandler.values())
			Bukkit.getPluginManager().registerEvents(eventHandler.listener, this);
		
		PluginCommand.setPlugin(this);
		configuration = new PluginConfiguration(this, oldestSupportedBuild);
		isEnabled = true;
		reload(true);
	}

	@Override
	public void onDisable()
	{
		if (tagger != null) tagger.close();
		configuration.printToLog(Level.INFO, "Disabled.");
	}
	
	public void reload(boolean reloadingAll)
	{
		File taggerFile = (tagger != null)? tagger.file : new File(this.getDataFolder(), "tags.yml");
		
		if((configuration.reload(reloadingAll) && reloadingAll) || !taggerFile.exists())
		{
			if(tagger != null) tagger.close();

			long[] tagConfigIntegers = { ModDamageTagger.defaultInterval, ModDamageTagger.defaultInterval * 4 };
			LinkedHashMap<String, Object> tagConfigurationTree = configuration.castToStringMap("Tagging", configuration.getConfigMap().get("Tagging"));
			if(tagConfigurationTree != null)
			{
				String[] tagConfigStrings = { ModDamageTagger.configString_save, ModDamageTagger.configString_clean };
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
			tagger = new ModDamageTagger(taggerFile, tagConfigIntegers[0], tagConfigIntegers[1]);
			
			for(ModDamageExtension extension : extensions)
				extension.reloadRoutines();
		}
	}

	// //COMMAND PARSING ////
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
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
		CHECK(false, "\\sc(?:heck)?(\\s\\d+)?")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				if(player == null)
				{
					configuration.printToLog(Level.INFO, "Complete log record for this server:");
					configuration.sendLogRecord(null, 9001);
					configuration.printToLog(Level.INFO, "End of log record.");
				}
				else
				{
					if(matcher.group(1) == null)
					{
						if(hasPermission(player, "moddamage.check"))
							configuration.sendLogRecord(player, 0);
					}
					else configuration.sendLogRecord(player, Integer.parseInt(matcher.group(1).substring(1)));
				}
			}
		},
		DEBUG(false, "\\sd(?:ebug)?(?:\\s(\\w+))?")
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
		RELOAD(false, "\\sr(?:eload)?(\\sall)?")
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
							player.sendMessage(chatPrepend(ChatColor.GREEN) + "Reloaded!");
							break;
						case FAILURE:
							player.sendMessage(chatPrepend(ChatColor.YELLOW) + "Reloaded with errors.");
							break;
						case NOT_LOADED:
							player.sendMessage(chatPrepend(ChatColor.GRAY) + "No configuration loaded! Are any routines defined?");
							break;
							
						default: assert(false);
					}
			}
		},
		STATUS(false, "\\s(?:en|dis)able")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				ModDamage.setPluginStatus(player, matcher.group().equalsIgnoreCase(" enable"));
			}
		},
		TAGS(true, "\\st(?:ags)?\\s(clear|save)")
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

		private PluginCommand(boolean needsEnable, String pattern)
		{
			this.needsEnable = needsEnable;
			this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
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
		boolean has = player != null ? player.hasPermission("moddamage.reload") : true;
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
		// TODO Use the PluginCommand enum
		if(player != null)
		{
			if(forError) player.sendMessage(ChatColor.RED + "Error: invalid command syntax.");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "ModDamage commands:");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/moddamage | /md - bring up this help message");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (check | c) - check configuration");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (debug | d) [debugType] - change debug type");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (disable|enable) - disable/enable ModDamage");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "/md (reload | r) - reload configuration");
		}
		else
		{
			if(forError) configuration.printToLog(Level.SEVERE, "Error: invalid command syntax.");
			configuration.printToLog(Level.INFO, "ModDamage commands:\n" + "/moddamage | /md - bring up this help message\n" + "/md check - check configuration\n" + "/md debug [debugType] - change debugging type (quiet, normal, verbose)\n" + "/md disable - disable ModDamage\n" + "/md enable - enable ModDamage\n" + "/md reload - reload configuration");
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

	public static ModDamageTagger getTagger(){ return tagger; }

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
		public void reloadRoutines();//Register routines with the Routine/NestedRoutine libraries.
	}
	public static void registerExtension(ModDamageExtension extension, PluginDescriptionFile description)
	{
		if(!extensions.contains(extension))
			extensions.add(extension);
		configuration.addToLogRecord(OutputPreset.CONSTANT, configuration.logPrepend() + "Found extension: " + description.getName() + " version " + description.getVersion() + ", by " + description.getAuthors().toString());
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