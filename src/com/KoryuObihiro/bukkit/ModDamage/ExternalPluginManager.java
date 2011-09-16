package com.KoryuObihiro.bukkit.ModDamage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.elbukkit.api.elregions.elRegionsPlugin;
import com.elbukkit.api.elregions.region.Region;
import com.gmail.nossr50.mcMMO;
import com.nijiko.permissions.PermissionHandler;
import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ExternalPluginManager
{
	private static mcMMO plugin_mcMMO;
	public mcMMO getPlugin_mcMMO()
	{
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
		return plugin_mcMMO; //FIXME
	}
	
	private static Plugin permissionsPlugin = null;
	public static PermissionsManager permissionsManager = PermissionsManager.SuperPerms;
	public enum PermissionsManager
	{
		SuperPerms,
		PermissionsEx,
		//bPermissions,
		PermissionsBukkit;
		public List<String> getGroups(Player player)
		{
			if(player == null) return ModDamage.emptyList;
			switch(this)
			{
				case PermissionsEx:
					return Arrays.asList(((PermissionHandler)permissionsPlugin).getGroups(player.getWorld().getName(), player.getName()));
				case PermissionsBukkit:
					List<String> groupNames = new ArrayList<String>();
					for(Group group : ((PermissionsPlugin)permissionsPlugin).getGroups(player.getName()))
						groupNames.add(group.getName());
					return groupNames;
				default: return ModDamage.emptyList;
			}
		}
		public boolean hasPermission(Player player, String permission)
		{
			if(player == null) return false;
			switch(this)
			{
				case SuperPerms:		return player.hasPermission(permission);
				case PermissionsEx:		return ((PermissionHandler)permissionsPlugin).has(player, permission);
				//case bPermissions:		return ((de.bananaco.permissions.SuperPermissionHandler)ModDamage.permissionsPlugin)..
				case PermissionsBukkit:	return ((PermissionsPlugin)permissionsPlugin).getPlayerInfo(player.getName()).getPermissions().containsKey(permission);
				default:				return player.isOp();
			}
		}
	}
	
	private static Plugin regionsPlugin = null;
	public static RegionsManager regionsManager = null;
	public enum RegionsManager
	{
		WorldGuard, elRegions;//TODO 0.9.6 - Override what sort of permissions/regions plugin to look for?
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
						return new ArrayList<String>(wgManager.getRegions().keySet());
					break;
			}
			return ModDamage.emptyList;
		}

		public List<String> getAllRegions() 
		{
			switch(this)
			{
				case elRegions:
					for(World world : ModDamage.server.getWorlds())//TODO Potential refactor: store list of all regions and worlds temporarily for alias verification?
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
					for(World world : ModDamage.server.getWorlds())
					{
						com.sk89q.worldguard.protection.managers.RegionManager wgManager = ((WorldGuardPlugin)regionsPlugin).getGlobalRegionManager().get(world);
						if(wgManager != null)
							return new ArrayList<String>(wgManager.getRegions().keySet());
					}
			}
			return ModDamage.emptyList;
		}
	}
	
	public static void reload(ModDamage plugin) 
	{
		permissionsManager = PermissionsManager.SuperPerms;
		for(PermissionsManager permsPlugin : PermissionsManager.values())
		{
			if(permsPlugin.equals(PermissionsManager.SuperPerms)) continue;
			permissionsPlugin = plugin.getServer().getPluginManager().getPlugin(permsPlugin.name());
			if (permissionsPlugin != null)
				permissionsManager = permsPlugin;
		}
		if(permissionsPlugin != null)
			ModDamage.addToLogRecord(DebugSetting.QUIET, 0, "[" + plugin.getDescription().getName() + "] " + plugin.getDescription().getVersion() + " enabled [" + permissionsPlugin.getDescription().getName() + " v" + permissionsPlugin.getDescription().getVersion() + " active]", LoadState.SUCCESS);
		else ModDamage.addToLogRecord(DebugSetting.QUIET, 0, "[" + plugin.getDescription().getName() + "] " + plugin.getDescription().getVersion() + " enabled [Permissions plugin not found]", LoadState.NOT_LOADED);
	
		for(RegionsManager regionalPlugin : RegionsManager.values())
		{
			regionsPlugin = plugin.getServer().getPluginManager().getPlugin(regionalPlugin.name());
			if (regionsPlugin != null)
				regionsManager = regionalPlugin;
		}
		if(regionsPlugin != null)
		{
			ModDamage.addToLogRecord(DebugSetting.QUIET, 0, "[" + plugin.getDescription().getName() + "] Found " + regionsPlugin.getDescription().getName() + " v" + ExternalPluginManager.regionsPlugin.getDescription().getVersion(), LoadState.SUCCESS);
		}
		else ModDamage.addToLogRecord(DebugSetting.VERBOSE, 0, "[" + plugin.getDescription().getName() + "] No regional plugins found.", LoadState.NOT_LOADED);
		
	}
}