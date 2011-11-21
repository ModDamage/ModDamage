package com.KoryuObihiro.bukkit.ModDamage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import com.KoryuObihiro.bukkit.ModDamage.ModDamageEventHandler.ModDamageEntityListener;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;

/**
 * "ModDamage" for Bukkit
 * 
 * @author Erich Gubler
 * 
 */
public class ModDamage extends JavaPlugin
{
	public final static int oldestSupportedBuild = 1337;
	// TODO 0.9.6 Command for autogen world/entitytype switches?
	// TODO 0.9.6 Autogen empty aliasing nodes
	// FIXME Change conditional term builders to use aliasing!
	// -Triggered effects...should be a special type of tag! :D Credit: ricochet1k
	// -AoE clearance, block search nearby for Material?
	// -find a way to give players ownership of an explosion?
	// -Deregister when Bukkit supports!
	protected static PluginConfiguration configuration;

	public static boolean isEnabled = false;
	private static final String errorString_Permissions = chatPrepend(ChatColor.RED) + "You don't have access to that command.";

	private static ModDamageTagger tagger = null;

	// //////////////////////// INITIALIZATION
	@Override
	public void onEnable()
	{
		// register plugin-related stuff with the server's plugin manager
		ModDamageEntityListener entityListener = ModDamageEventHandler.entityListener;
		Bukkit.getPluginManager().registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.PROJECTILE_HIT, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_TAME, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_REGAIN_HEALTH, entityListener, Event.Priority.Highest, this);
		Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, ModDamageEventHandler.playerListener, Event.Priority.Highest, this);
		
		PluginCommand.setPlugin(this);
		configuration = new PluginConfiguration(this, oldestSupportedBuild);
		isEnabled = true;
		reload(true);
	}

	@Override
	public void onDisable()
	{
		tagger.close();
		PluginConfiguration.log.info("[" + this.getDescription().getName() + "] disabled.");
	}
	
	public void reload(boolean reloadingAll)
	{
		configuration.reload(reloadingAll);
		if(reloadingAll)
		{
			if(tagger != null) tagger.close();

			long[] tagConfigIntegers = { ModDamageTagger.defaultInterval, ModDamageTagger.defaultInterval };
			LinkedHashMap<String, Object> tagConfigurationTree = configuration.castToStringMap("Tagging", configuration.getConfigMap().get("Tagging"));
			if(tagConfigurationTree != null)
			{
				String[] tagConfigStrings = { PluginConfiguration.getCaseInsensitiveKey(tagConfigurationTree, "interval-save"), PluginConfiguration.getCaseInsensitiveKey(tagConfigurationTree, "interval-clean") };
				Object[] tagConfigObjects =	{ tagConfigurationTree.get(tagConfigStrings[0]), tagConfigurationTree.get(tagConfigStrings[1]) };
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
			tagger = new ModDamageTagger(new File(this.getDataFolder(), "tags.yml"), tagConfigIntegers[0], tagConfigIntegers[1]);
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

	private enum PluginCommand// FIXME TEST!
	{
		CHECK(false, "\\s(?:check|c)(\\s\\d+)?")
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
		DEBUG(false, "\\sdebug(\\s\\w+)?")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				if(matcher.group(1) != null)
				{
					DebugSetting matchedSetting = DebugSetting.valueOf(matcher.group(1).substring(1).toUpperCase());
					if(matchedSetting != null)
						configuration.setDebugging(player, matchedSetting);
					else sendMessage(player, "Invalid debugging mode \"" + matcher.group(1).substring(1) + "\" - modes are \"quiet\", \"normal\", and \"verbose\".", ChatColor.RED);
				}
				else configuration.toggleDebugging(player);
			}
		},
		RELOAD(false, "\\s(?:reload|r)(\\sall)?")
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
					}
			}
		},
		STATUS(false, "\\s(?:enable|disable)")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				ModDamage.setPluginStatus(player, matcher.group().equalsIgnoreCase(" enable"));
			}
		},
		TAGS(true, "\\s(?:tags|t)\\s(clear|save)")
		{
			@Override
			protected void handleCommand(Player player, Matcher matcher)
			{
				if(matcher.group(1).equalsIgnoreCase("clear"))
					tagger.clear();
				else tagger.save();
				sendMessage(player, "Tags " + matcher.group(1).toLowerCase() + "ed.", ChatColor.GREEN);
			}
		};
		
		final static List<String> commandInstructions = new ArrayList<String>();
		final boolean needsEnable;
		final Pattern pattern;

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
		protected static void setPlugin(ModDamage plugin){ PluginCommand.plugin = plugin;}
	}

	private static boolean hasPermission(Player player, String permission)
	{
		boolean has = player != null ? ExternalPluginManager.getPermissionsManager().hasPermission(player, "moddamage.reload") : true;
		if(!has) player.sendMessage(errorString_Permissions);
		return has;
	}

	static void sendMessage(Player player, String message, ChatColor color)
	{
		if(player != null)
			player.sendMessage(chatPrepend(color) + message);
		else configuration.printToLog(Level.INFO, message);
	}

	static String chatPrepend(ChatColor color){ return color + "[" + ChatColor.DARK_RED + "Mod" + ChatColor.DARK_BLUE + "Damage" + color + "] ";}

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
			if(forError) PluginConfiguration.log.info("Error: invalid command syntax.");
			PluginConfiguration.log.info("ModDamage commands:\n" + "/moddamage | /md - bring up this help message\n" + "/md check - check configuration\n" + "/md debug [debugType] - change debugging type (quiet, normal, verbose)\n" + "/md disable - disable ModDamage\n" + "/md enable - enable ModDamage\n" + "/md reload - reload configuration");
		}
	}

///////////////// HELPER FUNCTIONS
	public static void addToLogRecord(OutputPreset preset, String message){ configuration.addToLogRecord(preset, message);}
	
	public static void changeIndentation(boolean forward)
	{
		if(forward) PluginConfiguration.indentation++;
		else PluginConfiguration.indentation--;
	}

	public static DebugSetting getDebugSetting(){ return configuration.currentSetting;}

	public static ModDamageTagger getTagger(){ return tagger;}

	public static PluginConfiguration getPluginConfiguration(){ return configuration;}
}