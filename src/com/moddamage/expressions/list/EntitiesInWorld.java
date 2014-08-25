package com.moddamage.expressions.list;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.ListExp;
import com.moddamage.matchables.EntityType;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

@SuppressWarnings("rawtypes")
public class EntitiesInWorld extends ListExp {
    public final EntityType entityType;
    public final IDataProvider<World> worldDP;

    public EntitiesInWorld(EntityType entityType, IDataProvider<World> worldDP) {
        this.entityType = entityType;
        this.worldDP = worldDP;
    }

    @SuppressWarnings("unchecked")
    public List get(EventData data) throws BailException {
        Class cls = entityType.myClass;
        List entities;

        if (worldDP == null) {
            entities = new ArrayList();
            for (World world : Bukkit.getServer().getWorlds())
                entities.addAll(world.getEntitiesByClass(cls));
        }
        else {
            World world = worldDP.get(data);
            if (world == null) return null;

            entities = Utils.asList(world.getEntitiesByClass(cls));
        }

        return entities;
    }

    public Class<?> providesElement() {
        return entityType.myClass;
    }

    public String toString() {
        return entityType + " in " + (worldDP == null? "server" : worldDP);
    }

    public static final Pattern serverPattern = Pattern.compile("server", Pattern.CASE_INSENSITIVE);

    public static void register()
    {
        DataProvider.register(List.class, Pattern.compile("(?:all\\s+)?(\\w+) in ", Pattern.CASE_INSENSITIVE), new BaseDataParser<List>() {
            public EntitiesInWorld parse(EventInfo info, Matcher m, StringMatcher sm) {
                EntityType entityType = EntityType.getElementNamed(m.group(1));
                if (entityType == null) return null;

                IDataProvider<World> worldDP;
                if (sm.matchesFront(serverPattern))
                    worldDP = null;
                else {
                	worldDP = DataProvider.parse(info, World.class, sm.spawn());
                	if (worldDP == null) return null;
                }

                sm.accept();
                return new EntitiesInWorld(entityType, worldDP);
            }
        });
    }
}
