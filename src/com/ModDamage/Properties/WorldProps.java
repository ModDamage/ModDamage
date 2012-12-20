package com.ModDamage.Properties;

import com.ModDamage.EventInfo.*;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.Property.Properties;
import com.ModDamage.Parsing.Property.Property;
import com.ModDamage.Parsing.Property.SettableProperty;

import org.bukkit.World;

public class WorldProps
{
	public static void register()
	{
        Properties.register(new Property<Integer, World>("onlineplayers", Integer.class, World.class) {
                public Integer get(World world, EventData data) { return world.getPlayers().size();  }
                public String toString(IDataProvider<World> worldDP) { return worldDP + "_onlineplayers"; }
            });
        Properties.register("time", World.class, "getTime", "setTime");
        Properties.register("fulltime", World.class, "getFullTime", "setFullTime");
        Properties.register(new SettableProperty<Integer, World>("day", Integer.class, World.class) {
                public Integer get(World world, EventData data) { return (int)(world.getFullTime() / 24000); }
                public void set(World world, EventData data, Integer value) { world.setFullTime(value * 24000 + world.getTime()); }
            });
        Properties.register(new Property<Integer, World>("moonphase", Integer.class, World.class) {
                public Integer get(World world, EventData data) { return (int)(world.getFullTime() / 24000) % 8; }
            });
        Properties.register("seed", World.class, "getSeed");

        Properties.register("name", World.class, "getName");
        Properties.register("environment", World.class, "getEnvironment");
	}
}