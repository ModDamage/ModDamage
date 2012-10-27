package com.ModDamage.Backend;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ModDamage.Variables.*;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.PermissionManager;

import com.ModDamage.ModDamage.ModDamageExtension;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Expressions.NestedExp;
import com.ModDamage.Expressions.StringExp;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Nested.NestedRoutine;
import com.ModDamage.Variables.Item.PlayerInvItem;
import com.ModDamage.Variables.Item.PlayerItem;
import com.elbukkit.api.elregions.elRegionsPlugin;
import com.elbukkit.api.elregions.region.Region;
import com.elbukkit.api.elregions.region.RegionManager;
import com.gmail.nossr50.mcMMO;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;

import de.bananaco.permissions.Permissions;

public class ExternalPluginManager
{
	private static List<ModDamageExtension> registeredPlugins = new ArrayList<ModDamageExtension>();
	private static void reloadModDamageRoutines()
	{
		DataProvider.clear();
		
		Routine.registerVanillaRoutines();
		NestedRoutine.registerVanillaRoutines();
		
		IntegerExp.registerAllIntegers();
		StringExp.register();
		NestedExp.register();
        TagValue.register();
		
		// ItemExps
		PlayerItem.register();
		PlayerInvItem.register();
		
		// Other
		Transformers.register();
		MiscProperties.register();
        OfflinePlayerProperties.register();
		
		EntityEntity.register();
		EntityBlockTarget.register();
		EntityWorld.register();
		LocationWorld.register();
        PlayerNamed.register();
        WorldNamed.register();
		
		for(ModDamageExtension plugin : registeredPlugins)
			plugin.reloadRoutines();
		
		DataProvider.compile();
	}
	
	private static mcMMO mcMMOplugin;
	public static mcMMO getMcMMOPlugin(){ return mcMMOplugin; }
	
	private static GroupsManager groupsManager = GroupsManager.None;
	public static GroupsManager getGroupsManager(){ return groupsManager; }
	public enum GroupsManager
	{
		None
		{
			@Override
			protected void reload(Plugin plugin) {}

			@Override
			public List<String> getGroups(Player player) { return Arrays.asList(); }
		},
		PermissionsEx
		{
			PermissionManager permissionManager = null;
			@Override
			public List<String> getGroups(Player player)
			{
				if(player != null)
					return Arrays.asList(permissionManager.getUser(player).getGroupsNames(player.getWorld().getName()));
				return Arrays.asList();
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
				return Arrays.asList();
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
					for(com.platymuus.bukkit.permissions.Group group : plugin.getGroups(player.getName()))
						groupStrings.add(group.getName());
					return groupStrings;
				}
				return Arrays.asList();
			}
			
			@Override
			protected void reload(Plugin plugin)
			{
				this.plugin = (PermissionsPlugin)plugin;
			}
		},
		GroupManager
		{
			GroupManager plugin = null;
			WorldsHolder wh = null;
			
			@Override
			public List<String> getGroups(Player player)
			{
				if(player != null)
				{
					OverloadedWorldHolder wd = wh.getWorldData(player);
					User user = wd.getUser(player.getName());
					
					List<String> groupStrings = new ArrayList<String>(1 + user.subGroupsSize());
					groupStrings.add(user.getGroupName());
					for(String group : user.subGroupListStringCopy())
						groupStrings.add(group);
					return groupStrings;
				}
				return Arrays.asList();
			}
			
			@Override
			protected void reload(Plugin plugin)
			{
				this.plugin = (GroupManager)plugin;
				if (this.plugin != null)
				{
					wh = this.plugin.getWorldsHolder();
				}
			}
		},
		SimpleClans
		{
			SimpleClans plugin = null;
			
			@Override
			public List<String> getGroups(Player player)
			{
				if(player != null)
				{
					ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
					if (cp != null) {
						return Arrays.asList(cp.getClan().getTag());
					}
				}
				return Arrays.asList();
			}
			
			@Override
			protected void reload(Plugin plugin)
			{
				this.plugin = (SimpleClans)plugin;
			}
		};

		private static String version;
		
		abstract public List<String> getGroups(Player player);

		public static GroupsManager reload()
		{
			for(GroupsManager groupsPlugin : GroupsManager.values())
			{
				if(groupsPlugin.equals(GroupsManager.None)) continue;
				Plugin plugin = Bukkit.getPluginManager().getPlugin(groupsPlugin.name());
				if (plugin != null)
				{
					groupsPlugin.reload(plugin);
					version = plugin.getDescription().getVersion();
					return groupsPlugin;
				}
			}
			version = null;
			return GroupsManager.None;
		}
		abstract protected void reload(Plugin plugin);
		
		public static String getVersion(){ return version; }
	}
	
	public static List<RegionsManager> regionsManagers = new ArrayList<RegionsManager>();
    public static List<String> getRegions(Location location) {
        if (regionsManagers.isEmpty()) return new ArrayList<String>(0);
        List<String> regions = null;
        for (RegionsManager rm : regionsManagers){
            if (regions == null)
                regions = rm.getRegions(location);
            else
                regions.addAll(rm.getRegions(location));
        }
        return regions;
    }
    public static List<String> getAllRegions() {
        if (regionsManagers.isEmpty()) return new ArrayList<String>(0);
        List<String> regions = null;
        for (RegionsManager rm : regionsManagers){
            if (regions == null)
                regions = rm.getAllRegions();
            else
                regions.addAll(rm.getAllRegions());
        }
        return regions;
    }
	public enum RegionsManager
	{
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
				return Arrays.asList();
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
			private WorldGuardPlugin worldGuardPlugin = null;

			@Override
			public List<String> getRegions(Location location)
			{
                com.sk89q.worldguard.protection.managers.RegionManager rm = worldGuardPlugin.getRegionManager(location.getWorld());
                if (rm == null) return Arrays.asList();
				return rm.getApplicableRegionsIDs(toVector(location));
			}
			
			@Override
			public List<String> getAllRegions()
			{
				List<String> regions = new ArrayList<String>();
				for(World world : Bukkit.getWorlds()) {
                    com.sk89q.worldguard.protection.managers.RegionManager rm = worldGuardPlugin.getRegionManager(world);
                    if (rm == null) return Arrays.asList();
                    regions.addAll(rm.getRegions().keySet());
                }
				return regions;
			}
			
			@Override
			protected void reload(Plugin plugin)
			{
                worldGuardPlugin = ((WorldGuardPlugin)plugin);
			}
		},
        Towny
        {
            public Towny towny;

            @Override
            public List<String> getRegions(Location location) {
                List<String> regions = new ArrayList<String>();
                String town = TownyUniverse.getTownName(location);
                if (town != null)
                    regions.add(town);
                if (TownyUniverse.isWilderness(location.getBlock()))
                    regions.add("wilderness");
                return regions;
            }

            @Override
            public List<String> getAllRegions() {
                List<String> regions = new ArrayList<String>();
                for (Town town : TownyUniverse.getDataSource().getTowns())
                    regions.add(town.getName());
                regions.add("wilderness");
                return regions;
            }

            @Override
            protected void reload(Plugin plugin) {
                towny = (Towny) plugin;
            }
        };

		abstract public List<String> getRegions(Location location);
		abstract public List<String> getAllRegions();
		
		public static List<RegionsManager> reload()
		{
            List<RegionsManager> rms = new ArrayList<RegionsManager>();

			for(RegionsManager regionalPlugin : RegionsManager.values())
			{
				Plugin plugin = Bukkit.getPluginManager().getPlugin(regionalPlugin.name());
				if (plugin != null)
				{
					regionalPlugin.reload(plugin);
					rms.add(regionalPlugin);
				}
			}
			return rms;
		}
		abstract protected void reload(Plugin plugin);
	}
	
	public static void reload()
	{
		groupsManager = GroupsManager.reload();
		regionsManagers = RegionsManager.reload();
		mcMMOplugin = (mcMMO) Bukkit.getPluginManager().getPlugin("mcMMO");
		reloadModDamageRoutines();
	}
}