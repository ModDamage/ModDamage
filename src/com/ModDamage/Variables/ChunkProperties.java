package com.ModDamage.Variables;


import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.StringMatcher;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkProperties {

    public static void register()
    {
        DataProvider.register(Chunk.class, Location.class, Pattern.compile("_chunk", Pattern.CASE_INSENSITIVE),
                    new DataProvider.IDataParser<Chunk, Location>() {
                public IDataProvider<Chunk> parse(EventInfo info, IDataProvider<Location> locDP, Matcher m, StringMatcher sm) {
                    return new DataProvider<Chunk, Location>(Location.class, locDP) {
                        public Chunk get(Location loc, EventData data) { return loc.getChunk(); }
                        public Class<Chunk> provides() { return Chunk.class; }
                        public String toString() { return startDP.toString() + "_chunk"; }
                    };
                }
            });


        DataProvider.register(Integer.class, Chunk.class, Pattern.compile("_x", Pattern.CASE_INSENSITIVE),
                new DataProvider.IDataParser<Integer, Chunk>() {
                    public IDataProvider<Integer> parse(EventInfo info, IDataProvider<Chunk> chunkDP, Matcher m, StringMatcher sm) {
                        return new DataProvider<Integer, Chunk>(Chunk.class, chunkDP) {
                            public Integer get(Chunk chunk, EventData data) { return chunk.getX(); }
                            public Class<Integer> provides() { return Integer.class; }
                            public String toString() { return startDP.toString() + "_x"; }
                        };
                    }
                });


        DataProvider.register(Integer.class, Chunk.class, Pattern.compile("_z", Pattern.CASE_INSENSITIVE),
                new DataProvider.IDataParser<Integer, Chunk>() {
                    public IDataProvider<Integer> parse(EventInfo info, IDataProvider<Chunk> chunkDP, Matcher m, StringMatcher sm) {
                        return new DataProvider<Integer, Chunk>(Chunk.class, chunkDP) {
                            public Integer get(Chunk chunk, EventData data) { return chunk.getZ(); }
                            public Class<Integer> provides() { return Integer.class; }
                            public String toString() { return startDP.toString() + "_z"; }
                        };
                    }
                });


        DataProvider.register(World.class, Chunk.class, Pattern.compile("_world", Pattern.CASE_INSENSITIVE),
                new DataProvider.IDataParser<World, Chunk>() {
                    public IDataProvider<World> parse(EventInfo info, IDataProvider<Chunk> chunkDP, Matcher m, StringMatcher sm) {
                        return new DataProvider<World, Chunk>(Chunk.class, chunkDP) {
                            public World get(Chunk chunk, EventData data) { return chunk.getWorld(); }
                            public Class<World> provides() { return World.class; }
                            public String toString() { return startDP.toString() + "_world"; }
                        };
                    }
                });
    }
}
