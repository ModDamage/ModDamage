package com.moddamage.properties;


import org.bukkit.Chunk;
import org.bukkit.Location;

import com.moddamage.parsing.property.Properties;

public class ChunkProps {

    public static void register()
    {
    	Properties.register("chunk", Location.class, "getChunk");
    	Properties.register("x", Chunk.class, "getX");
    	Properties.register("z", Chunk.class, "getZ");
    	Properties.register("world", Chunk.class, "getWorld");
    	Properties.register("isLoaded", Chunk.class, "isLoaded");
    }
}
