package com.ModDamage;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionManager;

import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.ModDamage.ModDamageExtension;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Nested.NestedRoutine;
import com.elbukkit.api.elregions.elRegionsPlugin;
import com.elbukkit.api.elregions.region.Region;
import com.elbukkit.api.elregions.region.RegionManager;
import com.gmail.nossr50.mcMMO;
import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;

import de.bananaco.permissions.Permissions;

public class ExternalPluginManager
{
	private static final List<String> emptyList = Arrays.asList();
	
	private static List<ModDamageExtension> registeredPlugins = new ArrayList<ModDamageExtension>();
	private static void reloadModDamageRoutines()
	{
		Routine.registerVanillaRoutines();
		NestedRoutine.registerVanillaRoutines();
		DynamicInteger.registerAllIntegers();
		
		for(ModDamageExtension plugin : registeredPlugins)
			plugin.reloadRoutines();
	}
	
	private static mcMMO mcMMOplugin;
	public static mcMMO getMcMMOPlugin(){ return mcMMOplugin;}
	
	private static PermissionsManager permissionsManager = PermissionsManager.SUPERPERMS;
	public static PermissionsManager getPermissionsManager(){ return permissionsManager;}
	public enum PermissionsManager
	{
		SUPERPERMS
		{
			@Override
			protected void reload(Plugin plugin){}
		},
		PermissionsEx
		{
			PermissionManager permissionManager = null;
			@Override
			public List<String> getGroups(Player player)
			{
				if(player != null)
					return Arrays.asList(permissionManager.getUser(player).getGroupsNames(player.getWorld().getName()));
				return emptyList;
			}
			
			@Override
			protected void reload(Plugin plugin)
			{
				permissionManager = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
			}
		},
		bPermissions
		{
			@Override
			public List<String> getGroups(Player player)
			{
				if(player != null)
					return Permissions.getWorldPermissionsManager().getPermissionSet(player.getWorld()).getGroups(player);
				return emptyList;
			}
			
			@Override
			public void reload(Plugin plugin){}
		},
		PermissionsBukkit
		{
			PermissionsPlugin plugin = null;
			
			@Override
			public List<String> getGroups(Player player)
			{
				if(player != null)
				{
					List<String> groupStrings = new ArrayList<String>();
					for(Group group : plugin.getGroups(player.getName()))
						groupStrings.add(group.getName());
					return groupStrings;
				}
				return emptyList;
			}
			
			@Override
			public boolean hasPermission(Player player, String permission)
			{
				if(player != null)
					return player.isOp() || plugin.getPlayerInfo(player.getName()).getPermissions().containsKey(permission);
				return false;
			}
			
			@Override
			protected void reload(Plugin plugin)
			{
				this.plugin = (PermissionsPlugin)plugin;
			}
		};

		private static String version;
		public boolean hasPermission(Player player, String permission)
		{
			if(player != null)
				return player.isOp() || player.hasPermission(permission);
			return false;
		}
		public List<String> getGroups(Player player){ return emptyList;}

		public static PermissionsManager reload()
		{
			for(PermissionsManager permsPlugin : PermissionsManager.values())
			{
				if(permsPlugin.equals(PermissionsManager.SUPERPERMS)) continue;
				Plugin plugin = Bukkit.getPluginManager().getPlugin(permsPlugin.name());
				if (plugin != null)
				{
					permsPlugin.reload(plugin);
					version = plugin.getDescription().getVersion();
					return permsPlugin;
				}
			}
			version = null;
			return PermissionsManager.SUPERPERMS;
		}
		abstract protected void reload(Plugin plugin);
		
		public String getVersion(){ return version;}
	}
	
	private static RegionsManager regionsManager = null;
	public static RegionsManager getRegionsManager(){ return regionsManager;}
	public enum RegionsManager
	{
		NONE
		{
			@Override
			public void reload(Plugin plugin){}

			@Override
			public List<String> getRegions(Location location){ return emptyList;}
			
			@Override
			public List<String> getAllRegions(){ return emptyList;}
		},
		elRegions
		{
			elRegionsPlugin regionsPlugin = null;
			
			@Override
			public List<String> getRegions(Location location)
			{
				RegionManager erManager = regionsPlugin.getRegionManager(location.getWorld());
				if(erManager != null)
				{
					List<String> regionNames = new ArrayList<String>();
					for(Region region : erManager.getRegions(location))
						regionNames.add(region.getName());
					return regionNames;
				}
				return emptyList;
			}
			
			@Override
			public List<String> getAllRegions()
			{
				List<String> regions = new ArrayList<String>();
				for(World world : Bukkit.getWorlds())
				{
					RegionManager erManager = regionsPlugin.getRegionManager(world);
					if(erManager != null)
						for(Region region : erManager.getRegions())
							regions.add(region.getName());
				}
				return regions;
			}
			
			@Override
			protected void reload(Plugin plugin)
			{
				regionsPlugin = ((elRegionsPlugin)plugin);
			}
		},
		WorldGuard
		{
			private GlobalRegionManager regionManager = null;
			
			@Override
			public List<String> getRegions(Location location)
			{
				return regionManager.get(location.getWorld()).getApplicableRegionsIDs(toVector(location));
			}
			
			@Override
			public List<String> getAllRegions()
			{
				List<String> regions = new ArrayList<String>();
				for(World world : Bukkit.getWorlds())
					regions.addAll(regionManager.get(world).getRegions().keySet());
				return regions;
			}
			
			@Override
			protected void reload(Plugin plugin)
			{
				regionManager = ((WorldGuardPlugin)plugin).getGlobalRegionManager();
			}
		};

		private static String version = null;
		abstract public List<String> getRegions(Location location);
		abstract public List<String> getAllRegions();
		
		public static RegionsManager reload()
		{
			for(RegionsManager regionalPlugin : RegionsManager.values())
			{
				if(regionalPlugin.equals(RegionsManager.NONE)) continue;
				Plugin plugin = Bukkit.getPluginManager().getPlugin(regionalPlugin.name());
				if (plugin != null)
				{
					regionalPlugin.reload(plugin);
					version = plugin.getDescription().getVersion();
					return regionalPlugin;
				}
			}
			version = null;
			return RegionsManager.NONE;
		}
		abstract protected void reload(Plugin plugin);
		public String getVersion(){ return version;}
	}
	
	public static void reload()
	{
		permissionsManager = PermissionsManager.reload();
		regionsManager = RegionsManager.reload();
		mcMMOplugin = (mcMMO) Bukkit.getPluginManager().getPlugin("mcMMO");
		reloadModDamageRoutines();
	}
}