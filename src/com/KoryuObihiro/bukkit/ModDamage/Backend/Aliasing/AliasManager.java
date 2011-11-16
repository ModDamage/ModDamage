package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.bukkit.Material;
import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ConfigLibrary;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageItemStack;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Message.DynamicMessage;
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
	
	public static LoadState getState()
	{
		return state;
	}

	public static void setState(LoadState state)
	{
		AliasManager.state = state;
	}

	public static final String nodeName = "Aliases";
	public static void reload()
	{
		for(AliasManager aliasType : AliasManager.values())
			aliasType.getAliaser().clear();
		ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Loading aliases...", LoadState.SUCCESS);

		ModDamage.indentation++;
		for(String configKeys : ModDamage.getConfigMap().keySet())
			if(configKeys.equalsIgnoreCase(nodeName))
			{
				//TODO get aliases node
				LinkedHashMap<String, Object> aliasesNode = ConfigLibrary.getStringMap(nodeName, ModDamage.getConfigMap().get(nodeName));
				if(aliasesNode != null)
				{
					LinkedHashMap<String, Object> rawAliases;
					for(AliasManager aliasType : AliasManager.values())	
						for(String key : aliasesNode.keySet())
							if(key.equalsIgnoreCase(aliasType.name()))
							{
								rawAliases = ConfigLibrary.getStringMap(aliasType.name(), aliasesNode.get(key));
								aliasType.setSpecificLoadState(aliasType.getAliaser().load(rawAliases));
								setState(LoadState.combineStates(getState(), aliasType.getSpecificLoadState()));
								break;
							}
				}
				switch(getState())
				{
					case NOT_LOADED:
						ModDamage.addToLogRecord(DebugSetting.VERBOSE, "No aliases loaded! Are any aliases defined?", getState());
						break;
					case FAILURE:
						ModDamage.addToLogRecord(DebugSetting.QUIET, "One or more errors occurred while loading aliases.", getState());
						break;
					case SUCCESS:
						ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Aliases loaded!", getState());
						break;
				}
				return;
				//TODO Add log recording
			}
		ModDamage.indentation--;
		ModDamage.addToLogRecord(DebugSetting.VERBOSE, "No Aliases node found.", LoadState.NOT_LOADED);
	}

	public Aliaser<?, ?> getAliaser()
	{
		return aliaser;
	}

	@SuppressWarnings("unchecked")
	public static Collection<ArmorSet> matchArmorAlias(String key){ return (Collection<ArmorSet>)AliasManager.Armor.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<Biome> matchBiomeAlias(String key){ return (Collection<Biome>)AliasManager.Biome.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<ModDamageElement> matchElementAlias(String key){ return (Collection<ModDamageElement>)AliasManager.Element.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<String> matchGroupAlias(String key){ return (Collection<String>)AliasManager.Group.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<ModDamageItemStack> matchItemAlias(String key){ return (Collection<ModDamageItemStack>)AliasManager.Item.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<Material> matchMaterialAlias(String key){ return (Collection<Material>)AliasManager.Material.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<DynamicMessage> matchMessageAlias(String key){ return (Collection<DynamicMessage>)AliasManager.Message.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<String> matchRegionAlias(String key){ return (Collection<String>)AliasManager.Region.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<Routine> matchRoutineAlias(String key){ return (Collection<Routine>)AliasManager.Routine.getAliaser().matchAlias(key);}
	@SuppressWarnings("unchecked")
	public static Collection<String> matchWorldAlias(String key){ return (Collection<String>)AliasManager.World.getAliaser().matchAlias(key);}

	public LoadState getSpecificLoadState()
	{
		return specificLoadState;
	}

	public void setSpecificLoadState(LoadState specificLoadState)
	{
		this.specificLoadState = specificLoadState;
	}
}
