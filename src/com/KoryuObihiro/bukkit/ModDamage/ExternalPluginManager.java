package com.KoryuObihiro.bukkit.ModDamage;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionGroup;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.DelayedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Message;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Addition;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Division;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.Set;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.ChangeProperty;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityDropItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityHeal;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntitySpawn;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Binomial;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Comparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityFalling;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTypeEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventHasRangedElement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventRangedElementEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventWorldEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerSleeping;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerSneaking;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWearing;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.PlayerWielding;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.ServerOnlineMode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.WorldEnvironment;
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
import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import de.bananaco.permissions.Permissions;

public class ExternalPluginManager
{
	private static final List<String> emptyList = new ArrayList<String>();
	
	private static List<ModDamagePlugin> registeredPlugins = new ArrayList<ModDamagePlugin>();
	private static void reloadModDamageRoutines()
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
		EntityFalling.register();
		EntityOnBlock.register();
		EntityRegion.register();
		EntityTypeEvaluation.register();
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
		ChangeProperty.register();
		EntityDropItem.register();
		EntityExplode.register();
		EntityHeal.register();
		EntityHurt.register();
		EntitySpawn.register();
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
	
	private static mcMMO mcMMOplugin;
	public static mcMMO getMcMMOPlugin()
	{
		return mcMMOplugin;
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
					List<String> groupNames = new ArrayList<String>();
					for(PermissionGroup group : ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager().getGroups(player.getName()))
						groupNames.add(group.getName());
					return groupNames;
				case bPermissions:
					return Permissions.getWorldPermissionsManager().getPermissionSet(player.getWorld()).getGroups(player);
				case PermissionsBukkit:
					List<String> groupStrings = new ArrayList<String>();
					for(Group group : ((PermissionsPlugin)permissionsPlugin).getGroups(player.getName()))
						groupStrings.add(group.getName());
					return groupStrings;
				default: return emptyList;
			}
		}
		public boolean hasPermission(Player player, String permission)
		{
			switch(this)
			{
				case PermissionsEx:		return ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager().has(player, permission);
				case PermissionsBukkit:	return ((PermissionsPlugin)permissionsPlugin).getPlayerInfo(player.getName()).getPermissions().containsKey(permission);
				default:				return player.hasPermission(permission) || player.isOp();
			}
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
		
		reloadModDamageRoutines();
	}
}