package com.ModDamage.Backend.Aliasing;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration;
import com.ModDamage.Backend.ArmorSet;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.ModDamageItemStack;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.RoutineObjects.Routine;
import com.ModDamage.RoutineObjects.Nested.Conditional;
import com.ModDamage.RoutineObjects.Nested.Parameterized.Message.DynamicMessage;

public enum AliasManager
{
	Armor(ArmorAliaser.class),
	Biome(BiomeAliaser.class),
	Condition(ConditionAliaser.class),
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
	
	private Aliaser<?, ?> aliaser;
	private Class<? extends Aliaser<?, ?>> clazz;
	private static LoadState state;
	private AliasManager(Class<? extends Aliaser<?, ?>> clazz){ this.clazz = clazz;}
	
	static
	{
		for(AliasManager aliasType : AliasManager.values())
			try{ aliasType.aliaser = aliasType.clazz.newInstance();}
			catch (InstantiationException e){ e.printStackTrace();}
			catch (IllegalAccessException e){ e.printStackTrace();}
	}
	
	public static LoadState getState(){ return state;}

	public static final String nodeName = "Aliases";
	public static void reload()
	{
		state = LoadState.NOT_LOADED;
		
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading aliases...");

		for(Entry<String, Object> configEntry : ModDamage.getPluginConfiguration().getConfigMap().entrySet())
			if(configEntry.getKey().equalsIgnoreCase(nodeName))
			{
				ModDamage.changeIndentation(true);
				LinkedHashMap<String, Object> aliasesMap = ModDamage.getPluginConfiguration().castToStringMap(nodeName, configEntry.getValue());
				if(aliasesMap != null)
					for(AliasManager aliasType : AliasManager.values())
					{
						LinkedHashMap<String, Object> aliasEntry = ModDamage.getPluginConfiguration().castToStringMap(aliasType.name(), PluginConfiguration.getCaseInsensitiveValue(aliasesMap, aliasType.name()));
						if(aliasEntry != null)
							aliasType.aliaser.load(ModDamage.getPluginConfiguration().castToStringMap(aliasType.name(), aliasEntry));
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
			}
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.WARNING, "No Aliases node found.");
	}

	@SuppressWarnings("unchecked")
	public static Collection<ArmorSet> matchArmorAlias(String key){ return (Collection<ArmorSet>)AliasManager.Armor.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<Biome> matchBiomeAlias(String key){ return (Collection<Biome>)AliasManager.Biome.aliaser.matchAlias(key);}
	public static Conditional matchConditionAlias(String key){ return (Conditional)AliasManager.Condition.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<Enchantment> matchEnchantmentAlias(String key){ return (Collection<Enchantment>)AliasManager.Enchantment.aliaser.matchAlias(key);}
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
	public static Collection<ModDamageElement> matchTypeAlias(String key){ return (Collection<ModDamageElement>)AliasManager.Type.aliaser.matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<String> matchWorldAlias(String key){ return (Collection<String>)AliasManager.World.aliaser.matchAlias(key);}

	public LoadState getSpecificLoadState(){ return aliaser.loadState;}
}
