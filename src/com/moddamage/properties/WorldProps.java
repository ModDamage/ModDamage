package com.moddamage.properties;

import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.World;

import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.FunctionParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.ISettableDataProvider;
import com.moddamage.parsing.SettableDataProvider;
import com.moddamage.parsing.property.Properties;
import com.moddamage.parsing.property.Property;
import com.moddamage.parsing.property.SettableProperty;

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

        DataProvider.register(Boolean.class, World.class, Pattern.compile("_isGameRule"), new FunctionParser<Boolean, World>(String.class) {

			@Override
			protected IDataProvider<Boolean> makeProvider(EventInfo info, IDataProvider<World> startDP, final IDataProvider[] arguments) {
				return new DataProvider<Boolean, World>(World.class, startDP) {
						@Override
						public Class<? extends Boolean> provides() {
							return Boolean.class;
						}
						
						@Override
						public Boolean get(World start, EventData data) throws BailException {
							return start.isGameRule((String)arguments[0].get(data));
						}
						
						public String toString() {
							return startDP.toString() + "_isGameRule";
						};
					};
				};
			}
        	
		);
        
        DataProvider.register(String.class, World.class, Pattern.compile("_gameRule"), new FunctionParser<String, World>(String.class) {
        	@Override
        	protected ISettableDataProvider<String> makeProvider(EventInfo info, IDataProvider<World> startDP, final IDataProvider[] arguments) {
        		return new SettableDataProvider<String, World>(World.class, startDP) {

					@Override
					public boolean isSettable() {
						return true;
					}

					@Override
					public void set(World start, EventData data, String value) throws BailException {
						start.setGameRuleValue((String)arguments[0].get(data), value);
					}

					@Override
					public String get(World start, EventData data) throws BailException {
						return start.getGameRuleValue((String)arguments[0].get(data));
					}

					@Override
					public Class<? extends String> provides() {
						return String.class;
					}
        			
					@Override
					public String toString() {
						return startDP.toString() + "_gameRule";
					}
				};
        	}
        });
	}
	
}