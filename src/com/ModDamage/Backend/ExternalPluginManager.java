package com.ModDamage.Backend;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.bukkit.plugin.PluginDescriptionFile;

import ru.tehkode.permissions.PermissionManager;

import com.ModDamage.MDEvent;
import com.ModDamage.LogUtil;
import com.ModDamage.ModDamage;
import com.ModDamage.ModDamage.ModDamageExtension;
import com.ModDamage.Expressions.ListExp;
import com.ModDamage.Expressions.NestedExp;
import com.ModDamage.Expressions.NumberExp;
import com.ModDamage.Expressions.StringExp;
import com.ModDamage.Expressions.Function.NewVectorFunction;
import com.ModDamage.External.TabAPI.TabAPISupport;
import com.ModDamage.External.Vault.VaultSupport;
import com.ModDamage.External.Votifier.Vote;
import com.ModDamage.Magic.MagicStuff;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Properties.BlockProps;
import com.ModDamage.Properties.ChunkProps;
import com.ModDamage.Properties.CreatureProps;
import com.ModDamage.Properties.EntityProps;
import com.ModDamage.Properties.EquipmentProps;
import com.ModDamage.Properties.InventoryProps;
import com.ModDamage.Properties.ItemProps;
import com.ModDamage.Properties.LocationProps;
import com.ModDamage.Properties.MaterialProps;
import com.ModDamage.Properties.MiscProps;
import com.ModDamage.Properties.OfflinePlayerProps;
import com.ModDamage.Properties.PlayerProps;
import com.ModDamage.Properties.ScoreboardProps;
import com.ModDamage.Properties.ServerProps;
import com.ModDamage.Properties.WorldProps;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Nested.NestedRoutine;
import com.ModDamage.Variables.PlayerNamed;
import com.ModDamage.Variables.TagValue;
import com.ModDamage.Variables.Transformers;
import com.ModDamage.Variables.WorldNamed;
import com.gmail.nossr50.mcMMO;
import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ExternalPluginManager
{
	private static List<ModDamageExtension> registeredPlugins = new ArrayList<ModDamageExtension>();

	private static void reloadPluginExtensions()
	{
		String prefix = ModDamage.getPluginConfiguration().getLog().logPrepend();
		if (registeredPlugins.isEmpty())
		{
			LogUtil.info_verbose(prefix + "Extensions: No extensions found.");
		} else {
			LogUtil.info_verbose(prefix + "Extensions: Loading...");
			boolean anyFailed = false;
			for(ModDamageExtension pluginExtension : registeredPlugins)
			{
				boolean currentFailed = false;
				try {
					pluginExtension.reloadRoutines();
				} catch (Throwable t) {
					anyFailed = true;
					currentFailed = true;
				}
				try {
					PluginDescriptionFile description = pluginExtension.getDescription(); //Don't waste processing time on fetching description file per call.
					if (!currentFailed)
						LogUtil.info_verbose(prefix + "Extension '" + description.getName() + " v" + description.getVersion()+ "' has been reloaded successfully.");
					else
						LogUtil.error(prefix + "Extension '" + description.getName() + " v" + description.getVersion() + "' failed to reload.");
				} catch (Throwable e) { 
					LogUtil.error(prefix + "An extension failed to load:" + pluginExtension.toString()); 
					e.printStackTrace();
				}
			}
			if (!anyFailed)	LogUtil.constant(prefix + "Extensions: Successfully loaded all extensions.");
			else LogUtil.warning_strong(prefix + "Extensions: Some extensions failed to load.");
		}
	}

	private static void reloadModDamageRoutines()
	{
		DataProvider.clear();

		Routine.registerVanillaRoutines();
		NestedRoutine.registerVanillaRoutines();

		NumberExp.registerAllNumbers();
		StringExp.register();
		NestedExp.register();
		TagValue.register();
		ListExp.register();

		// Other
		Transformers.register();
		
		BlockProps.register();
		ChunkProps.register();
		CreatureProps.register();
		EntityProps.register();
		EquipmentProps.register();
		InventoryProps.register();
		ItemProps.register();
		LocationProps.register();
		NewVectorFunction.register();
		
		MaterialProps.register();
		MiscProps.register();
		OfflinePlayerProps.register();
		PlayerProps.register();
		ScoreboardProps.register();
		ServerProps.register();
		WorldProps.register();

		PlayerNamed.register();
		WorldNamed.register();

		try {
			VaultSupport.register();
		}
		catch (NoClassDefFoundError e) {
			LogUtil.info("Vault not found: "+e.getMessage());
		}
		
		try {
			TabAPISupport.register();
		}
		catch (NoClassDefFoundError e) {
			LogUtil.info("TabAPI not found: "+e.getMessage());
			}
		
		reloadPluginExtensions();
		
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
				if(player != null){
					ArrayList<String> list = new ArrayList<String>();
					list.addAll(de.bananaco.bpermissions.api.WorldManager.getInstance().getWorld(player.getWorld().getName()).getUser(player.getName()).getGroupsAsString());
					return list;
				}
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
				TownyDataSource tds = TownyUniverse.getDataSource();
				if (tds != null)
					for (Town town : tds.getTowns())
						regions.add(town.getName());
				regions.add("wilderness");
				return regions;
			}

			@Override
			protected void reload(Plugin plugin) {
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
		
		if (Bukkit.getServer().getPluginManager().getPlugin("Votifier") != null)// Check if loaded.
			if (MagicStuff.safeClassForName("com.vexsoftware.votifier.model.VotifierEvent") != null) //See if the event class still exists (In case an update breaks)
				MDEvent.addEvent("Votifier", new Vote());
	}
	
	public static void registerExtension(ModDamageExtension extension)
	{
		if(!registeredPlugins.contains(extension))
			registeredPlugins.add(extension);
	}
}