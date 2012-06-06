package com.ModDamage.Alias;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;

public enum AliasManager
{
	Armor(ArmorAliaser.class),
	Biome(BiomeAliaser.class),
	Command(CommandAliaser.class),
	Enchantment(EnchantmentAliaser.class),
	Item(ItemAliaser.class),
	Group(GroupAliaser.class),
	Material(MaterialAliaser.class),
	Message(MessageAliaser.class),
	Region(RegionAliaser.class),
	Routine(RoutineAliaser.class),
	Type(TypeAliaser.class),
	TypeName(TypeNameAliaser.class),
	World(WorldAliaser.class);
	
	private static LoadState state = LoadState.NOT_LOADED;
	public static LoadState getState() { return state; }
	
	private final Class<? extends Aliaser<?, ?>> aliaserClass;
	private AliasManager(Class<? extends Aliaser<?, ?>> aliaserClass) { this.aliaserClass = aliaserClass; }
	
	private Aliaser<?, ?> getAliaser()
	{
		try { return (Aliaser<?, ?>) aliaserClass.getField("aliaser").get(null); }
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (SecurityException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		catch (NoSuchFieldException e) { e.printStackTrace(); }
		return null;
	}
	
	public static final Pattern aliasPattern = Pattern.compile("_\\w+");

	public static void reload()
	{
		state = LoadState.NOT_LOADED;
		
		PluginConfiguration pluginConfig = ModDamage.getPluginConfiguration();
		
		LinkedHashMap<String, Object> entries = pluginConfig.getConfigMap();
		Object aliases = PluginConfiguration.getCaseInsensitiveValue(entries, "Aliases");
		
		if(aliases != null)
		{
			ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading aliases...");
			
			ModDamage.changeIndentation(true);
			LinkedHashMap<String, Object> aliasesMap = pluginConfig.castToStringMap("Aliases", aliases);
			if(aliasesMap != null)
			{
				for(AliasManager aliasType : AliasManager.values())
				{
					LinkedHashMap<String, Object> aliasEntry = pluginConfig.castToStringMap(aliasType.name(), PluginConfiguration.getCaseInsensitiveValue(aliasesMap, aliasType.name()));
					if(aliasEntry != null)
						aliasType.getAliaser().load(pluginConfig.castToStringMap(aliasType.name(), aliasEntry));
				}
				state = LoadState.SUCCESS;
			}
			ModDamage.changeIndentation(false);
			ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
			switch(state)
			{
				case NOT_LOADED:
					ModDamage.addToLogRecord(OutputPreset.WARNING, "No aliases loaded! Are any aliases defined?");
					break;
				case FAILURE:
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "One or more errors occurred while loading aliases.");
					break;
				case SUCCESS:
					ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Aliases loaded!");
					break;
				
				default: assert(false);
			}
			return;
		}
		else
		{
			ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
			ModDamage.addToLogRecord(OutputPreset.WARNING, "No Aliases node found.");
		}
	}
	
	public LoadState getSpecificLoadState(){ return getAliaser().loadState; }
}
