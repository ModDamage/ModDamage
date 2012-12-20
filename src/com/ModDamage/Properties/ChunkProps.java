package com.ModDamage.Properties;


import org.bukkit.Chunk;
import org.bukkit.Location;

import com.ModDamage.Parsing.Property.Properties;

public class ChunkProps {

    public static void register()
    {
    	Properties.register("chunk", Location.class, "getChunk");
    	Properties.register("x", Chunk.class, "getX");
    	Properties.register("z", Chunk.class, "getZ");
    	Properties.register("world", Chunk.class, "getWorld");
    }
}
