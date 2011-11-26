package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageItemStack;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized.Message.DynamicMessage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public enum AliasManager
{
	Armor(new ArmorAliaser()),
	Biome(new BiomeAliaser()),
	Condition(new ConditionAliaser()),
	Element(new ElementAliaser()),
	Item(new ItemAliaser()),
	Group(new GroupAliaser()),
	Material(new MaterialAliaser()),
	Message(new MessageAliaser()),
	Region(new RegionAliaser()),
	Routine(new RoutineAliaser()),
	World(new WorldAliaser());
	
	private final Aliaser<?, ?> aliaser;
	private LoadState specificLoadState;
	private static LoadState state;
	private AliasManager(Aliaser<?, ?> aliaser){ this.aliaser = aliaser;}
	
	public static LoadState getState(){ return state;}

	public static final String nodeName = "Aliases";
	public static void reload()
	{
		for(AliasManager aliasType : AliasManager.values())
		{
			aliasType.aliaser.clear();
			state = LoadState.NOT_LOADED;
			aliasType.specificLoadState = LoadState.NOT_LOADED;
		}
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading aliases...");

		for(String configKeys : ModDamage.getPluginConfiguration().getConfigMap().keySet())
			if(configKeys.equalsIgnoreCase(nodeName))
			{
				ModDamage.changeIndentation(true);
				LinkedHashMap<String, Object> aliasesMap = ModDamage.getPluginConfiguration().castToStringMap(nodeName, ModDamage.getPluginConfiguration().getConfigMap().get(nodeName));
				if(aliasesMap != null)
					for(AliasManager aliasType : AliasManager.values())
					{
						aliasType.specificLoadState = aliasType.aliaser.load(ModDamage.getPluginConfiguration().castToStringMap(aliasType.name(), PluginConfiguration.getCaseInsensitiveValue(aliasesMap, aliasType.name())));
						state = LoadState.combineStates(state, aliasType.getSpecificLoadState());
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
				}
				return;
				//TODO Add log recording
			}
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.WARNING, "No Aliases node found.");
	}

	@SuppressWarnings("unchecked")
	public static Collection<ArmorSet> matchArmorAlias(String key){ return (Collection<ArmorSet>)AliasManager.Armor.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<Biome> matchBiomeAlias(String key){ return (Collection<Biome>)AliasManager.Biome.aliaser.matchAlias(key);}
	public static ConditionalStatement matchConditionAlias(String key){ return (ConditionalStatement)AliasManager.Condition.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<ModDamageElement> matchElementAlias(String key){ return (Collection<ModDamageElement>)AliasManager.Element.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<String> matchGroupAlias(String key){ return (Collection<String>)AliasManager.Group.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<ModDamageItemStack> matchItemAlias(String key){ return (Collection<ModDamageItemStack>)AliasManager.Item.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<Material> matchMaterialAlias(String key){ return (Collection<Material>)AliasManager.Material.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<DynamicMessage> matchMessageAlias(String key){ return (Collection<DynamicMessage>)AliasManager.Message.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<String> matchRegionAlias(String key){ return (Collection<String>)AliasManager.Region.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<Routine> matchRoutineAlias(String key){ return (Collection<Routine>)AliasManager.Routine.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<String> matchWorldAlias(String key){ return (Collection<String>)AliasManager.World.aliaser.matchAlias(key);}

	public LoadState getSpecificLoadState(){ return specificLoadState;}
}
