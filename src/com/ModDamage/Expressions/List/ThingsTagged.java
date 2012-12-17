package com.ModDamage.Expressions.List;


import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import net.minecraft.server.EnumMobType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThingsTagged {
    enum TagType {
        ENTITY,
        PLAYER,
        BLOCK,
        LOCATION,
        CHUNK,
        WORLD
    }

    public static void register()
    {
        DataProvider.register(List.class, Pattern.compile("("+ Utils.joinBy("|", TagType.values())+") (s?)tagged (\\w+) "), new DataProvider.BaseDataParser<List>() {
            @Override
            public IDataProvider<List> parse(EventInfo info, Matcher m, StringMatcher sm) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
}
