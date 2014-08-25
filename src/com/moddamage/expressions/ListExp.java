package com.moddamage.expressions;


import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.expressions.list.EntitiesInWorld;
import com.moddamage.expressions.list.ThingsTagged;
import com.moddamage.parsing.IDataProvider;

import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class ListExp<T> implements IDataProvider<List> {
    public List<T> get(EventData data) throws BailException {
        return null;
    }

	public final Class<List> provides() {
        return List.class;
    }

    public abstract Class<T> providesElement();


    public static void register() {
        EntitiesInWorld.register();
        ThingsTagged.register();
    }
}
