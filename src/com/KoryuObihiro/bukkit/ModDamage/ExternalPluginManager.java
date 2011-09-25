package com.KoryuObihiro.bukkit.ModDamage;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Addition;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Division;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Set;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityAddAirTicks;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityAddFireTicks;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityDropItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityHeal;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntitySetAirTicks;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntitySetFireTicks;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntitySetHealth;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntitySpawn;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.PlayerAddItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.PlayerSetItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.SlimeSetSize;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.WorldTime;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Binomial;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Comparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityExposedToSky;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityFalling;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityOnFire;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTypeEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventHasRangedElement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventRangedElementEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventWorldEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerSleeping;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerSneaking;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWearing;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWielding;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.ServerOnlineMode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.DelayedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Message;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions.PlayerGroupEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions.PlayerGroupSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions.PlayerPermissionEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Regions.EntityRegion;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.ArmorSetSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.BiomeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EntityTypeSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EnvironmentSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.PlayerWieldSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.RangedElementSwitch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.WorldSwitch;
import com.elbukkit.api.elregions.elRegionsPlugin;
import com.elbukkit.api.elregions.region.Region;
import com.gmail.nossr50.mcMMO;
import com.nijiko.permissions.PermissionHandler;
import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.bananaco.permissions.Permissions;

public class ExternalPluginManager
{
	private static final List<String> emptyList = new ArrayList<String>();
	
	private static List<ModDamagePlugin> registeredPlugins = new ArrayList<ModDamagePlugin>();
	private static void reloadModDamagePlugins()
	{
		Routine.registeredBaseRoutines.clear();
		NestedRoutine.registeredNestedRoutines.clear();
		CalculationRoutine.registeredCalculations.clear();
		ConditionalRoutine.registeredConditionalStatements.clear();
		SwitchRoutine.registeredSwitchRoutines.clear();
	//register vanilla MD routines
		Addition.register();
		DelayedRoutine.register();
		DiceRoll.register();
		Division.register();
		IntervalRange.register();
		LiteralRange.register();
		Multiplication.register();
		Set.register();
		Message.register();
	ConditionalRoutine.register();
		Binomial.register();
		Comparison.register();
		//Entity
		EntityBiome.register();
		EntityDrowning.register();
		EntityExposedToSky.register();
		EntityFalling.register();
		EntityOnBlock.register();
		EntityOnFire.register();
		EntityRegion.register();
		EntityTypeEvaluation.register();
		EntityUnderwater.register();
		EventWorldEvaluation.register();
		PlayerGroupEvaluation.register();
		PlayerPermissionEvaluation.register();
		PlayerSleeping.register();
		PlayerSneaking.register();
		PlayerWearing.register();
		PlayerWielding.register();
		//World
		WorldEnvironment.register();
		//Server
		ServerOnlineMode.register();
		//Event
		EventHasRangedElement.register();
		EventRangedElementEvaluation.register();
		EventWorldEvaluation.register();
	CalculationRoutine.register();
		EntityAddAirTicks.register();
		EntityAddFireTicks.register();
		EntityDropItem.register();
		EntityExplode.register();
		EntityHeal.register();
		EntityHurt.register();
		EntitySetAirTicks.register();
		EntitySetFireTicks.register();
		EntitySetHealth.register();
		EntitySpawn.register();
		PlayerAddItem.register();
		PlayerSetItem.register();
		SlimeSetSize.register();
		WorldTime.register();
	SwitchRoutine.register();
		ArmorSetSwitch.register();
		BiomeSwitch.register();
		EntityTypeSwitch.register();
		EnvironmentSwitch.register();
		PlayerGroupSwitch.register();
		PlayerWieldSwitch.register();
		RangedElementSwitch.register();
		WorldSwitch.register();
		
		for(ModDamagePlugin plugin : registeredPlugins)
			plugin.registerRoutines();
	}
	
	private static mcMMO plugin_mcMMO;
	public mcMMO getPlugin_mcMMO()
	{
		return plugin_mcMMO; //FIXME
		/* TODO 0.9.6 - make routines outta these.
		plugin_mcMMO.getPlayerProfile(null).addBleedTicks(0);
		plugin_mcMMO.getPlayerProfile(null).addXP(null, 0);
		plugin_mcMMO.getPlayerProfile(null).getBerserkMode();
		plugin_mcMMO.getPlayerProfile(null).getBleedTicks();
		plugin_mcMMO.getPlayerProfile(null).getCurrentMana();
		plugin_mcMMO.getPlayerProfile(null).getGigaDrillBreakerMode();
		plugin_mcMMO.getPlayerProfile(null).getGodMode();
		plugin_mcMMO.getPlayerProfile(null).getGreenTerraMode();
		plugin_mcMMO.getPlayerProfile(null).getLastGained();
		plugin_mcMMO.getPlayerProfile(null).getMaxMana();
		plugin_mcMMO.getPlayerProfile(null).setXpBarInc(0);
		plugin_mcMMO.getPlayerProfile(null).getSkullSplitterMode();
		plugin_mcMMO.getPlayerProfile(null).getSerratedStrikesMode();
		plugin_mcMMO.getPlayerProfile(null).getSuperBreakerMode();
		plugin_mcMMO.getPlayerProfile(null).getSwordsPreparationMode();
		plugin_mcMMO.getPlayerProfile(null).getTreeFellerMode();
		plugin_mcMMO.getPlayerProfile(null).getXpBarInc();
		plugin_mcMMO.getPlayerProfile(null).hasPartyInvite();
		plugin_mcMMO.getPlayerProfile(null).modifyskill(null, 0);
		plugin_mcMMO.mob.assignDifficulty(null);//Assigns to a UUID?
		plugin_mcMMO.inSameParty(null, null);
		mcMMO.inParty(null);
		mcMMO.getPartyName(null);//aliases?
		*/
	}
	
	private static Plugin permissionsPlugin = null;
	public static Plugin getPermissionsPlugin(){ return permissionsPlugin;}
	private static PermissionsManager permissionsManager = PermissionsManager.SUPERPERMS;
	public static PermissionsManager getPermissionsManager(){ return permissionsManager;}
	public enum PermissionsManager
	{
		SUPERPERMS,
		PermissionsEx,
		bPermissions,
		PermissionsBukkit;
		public List<String> getGroups(Player player)
		{
			if(player == null) return emptyList;
			switch(this)
			{
				case PermissionsEx:
					return Arrays.asList(((PermissionHandler)permissionsPlugin).getGroups(player.getWorld().getName(), player.getName()));
				case bPermissions:
					return Permissions.getWorldPermissionsManager().getPermissionSet(player.getWorld()).getGroups(player);
				case PermissionsBukkit:
					List<String> groupNames = new ArrayList<String>();
					for(Group group : ((PermissionsPlugin)permissionsPlugin).getGroups(player.getName()))
						groupNames.add(group.getName());
					return groupNames;
				default: return emptyList;
			}
		}
		public boolean hasPermission(Player player, String permission)
		{
			if(player == null) return false;
			switch(this)
			{
				case PermissionsEx:		return ((PermissionHandler)permissionsPlugin).has(player, permission);
				case PermissionsBukkit:	return ((PermissionsPlugin)permissionsPlugin).getPlayerInfo(player.getName()).getPermissions().containsKey(permission);
			}
			return player.hasPermission(permission);
		}
	}
	
	private static Plugin regionsPlugin = null;
	public static Plugin getRegionsPlugin(){ return regionsPlugin;}
	private static RegionsManager regionsManager = null;
	public static RegionsManager getRegionsManager(){ return regionsManager;}
	public enum RegionsManager
	{
		NONE, WorldGuard, elRegions;
		public List<String> getRegions(Location location)
		{
			switch(this)
			{
				case elRegions:
					com.elbukkit.api.elregions.region.RegionManager erManager = ((elRegionsPlugin)regionsPlugin).getRegionManager(location.getWorld());
					if(erManager != null)
					{
						List<String> regionNames = new ArrayList<String>();
						for(Region region : erManager.getRegions(location))
							regionNames.add(region.getName());
						return regionNames;
					}
				case WorldGuard:
					com.sk89q.worldguard.protection.managers.RegionManager wgManager = ((WorldGuardPlugin)regionsPlugin).getGlobalRegionManager().get(location.getWorld());
					if(wgManager != null)
						return wgManager.getApplicableRegionsIDs(toVector(location));
					break;
			}
			return emptyList;
		}

		public List<String> getAllRegions() 
		{
			switch(this)
			{
				case elRegions:
					for(World world : regionsPlugin.getServer().getWorlds())
					{
						com.elbukkit.api.elregions.region.RegionManager erManager = ((elRegionsPlugin)regionsPlugin).getRegionManager(world);
						if(erManager != null)
						{
							List<String> regionNames = new ArrayList<String>();
							for(Region region : erManager.getRegions())
								regionNames.add(region.getName());
							return regionNames;
						}
					}
				case WorldGuard:
					for(World world : regionsPlugin.getServer().getWorlds())
					{
						com.sk89q.worldguard.protection.managers.RegionManager wgManager = ((WorldGuardPlugin)regionsPlugin).getGlobalRegionManager().get(world);
						if(wgManager != null)
							return new ArrayList<String>(wgManager.getRegions().keySet());
					}
			}
			return emptyList;
		}
	}
	
	public static void reload(Plugin plugin)
	{
		permissionsManager = PermissionsManager.SUPERPERMS;
		for(PermissionsManager permsPlugin : PermissionsManager.values())
		{
			if(permsPlugin.equals(PermissionsManager.SUPERPERMS)) continue;
			permissionsPlugin = plugin.getServer().getPluginManager().getPlugin(permsPlugin.name());
			if (permissionsPlugin != null)
			{
				permissionsManager = permsPlugin;
				break;
			}
		}
		regionsPlugin = null;
		for(RegionsManager regionalPlugin : RegionsManager.values())
		{
			if(regionalPlugin.equals(RegionsManager.NONE)) continue;
			regionsPlugin = plugin.getServer().getPluginManager().getPlugin(regionalPlugin.name());
			if (regionsPlugin != null)
			{
				regionsManager = regionalPlugin;
				break;
			}
		}
		if(regionsPlugin == null)
			regionsManager = RegionsManager.NONE;
		
		reloadModDamagePlugins();
	}
}