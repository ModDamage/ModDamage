package com.KoryuObihiro.bukkit.ModDamage;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
	}
}