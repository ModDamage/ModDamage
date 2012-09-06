package com.ModDamage.Variables;

import org.bukkit.Location;
import org.bukkit.World;

import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataTransformer;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class LocationWorld
{
	public static void register()
	{
		DataProvider.registerTransformer(World.class, Location.class,
				new IDataTransformer<World, Location>() {
					public IDataProvider<World> transform(EventInfo info, IDataProvider<Location> locDP) {
						return new DataProvider<World, Location>(Location.class, locDP) {
								public World get(Location loc, EventData data) { return loc.getWorld(); }
								public Class<World> provides() { return World.class; }
							};
					}
				});
	}
}
