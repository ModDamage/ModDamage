package com.ModDamage.Properties;

import org.bukkit.Location;
import org.bukkit.World;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Parsing.Property.Properties;
import com.ModDamage.Parsing.Property.Property;
import com.ModDamage.Parsing.Property.SettableProperty;

public class WorldProps
{
	public static void register()
	{
        Properties.register(new Property<Integer, World>("onlineplayers", Integer.class, World.class) {
                public Integer get(World world, EventData data) { return world.getPlayers().size();  }
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
        Properties.register("difficulty", World.class, "getDifficulty", "setDifficulty");
        Properties.register("maxheight", World.class, "getMaxHeight");
        Properties.register("sealevel", World.class, "getSeaLevel");
        Properties.register(new SettableProperty<Location, World>("spawnLocation", Location.class, World.class) {
        	public Location get(World world, EventData data) {
        		return world.getSpawnLocation();
        	}
        	public void set(World world, EventData data, Location value) {
        		world.setSpawnLocation(value.getBlockX(), value.getBlockY(), value.getBlockZ());
        	}
        });
        
        Properties.register("storm", World.class, "hasStorm", "setStorm");
        Properties.register("weatherDuration", World.class, "getWeatherDuration", "setWeatherDuration");
        Properties.register("thundering", World.class, "isThundering", "setThundering");
        Properties.register("thunderDuration", World.class, "getThunderDuration", "setThunderDuration");
        
        Properties.register(new SettableProperty<Integer, World>("ticksPerAnimalSpawns", Integer.class, World.class) {
				public Integer get(World world, EventData data) {
					return (int) world.getTicksPerAnimalSpawns();
				}
				public void set(World world, EventData data, Integer value) {
					world.setTicksPerAnimalSpawns(value);
				}
			});
        Properties.register(new SettableProperty<Integer, World>("ticksPerMonsterSpawns", Integer.class, World.class) {
				public Integer get(World world, EventData data) {
					return (int) world.getTicksPerMonsterSpawns();
				}
				public void set(World world, EventData data, Integer value) {
					world.setTicksPerMonsterSpawns(value);
				}
			});
        Properties.register("monsterSpawnLimit", World.class, "getMonsterSpawnLimit", "setMonsterSpawnLimit");
        Properties.register("animalSpawnLimit", World.class, "getAnimalSpawnLimit", "setAnimalSpawnLimit");
        Properties.register("waterAnimalSpawnLimit", World.class, "getWaterAnimalSpawnLimit", "setWaterAnimalSpawnLimit");
        Properties.register("ambientSpawnLimit", World.class, "getAmbientSpawnLimit", "setAmbientSpawnLimit");
	}
}