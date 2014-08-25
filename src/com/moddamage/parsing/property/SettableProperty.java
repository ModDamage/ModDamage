package com.moddamage.parsing.property;


import com.moddamage.backend.BailException;
import com.moddamage.backend.Nullable;
import com.moddamage.eventinfo.EventData;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.ISettableDataProvider;

public abstract class SettableProperty<T, S> extends Property<T, S>
{
    public SettableProperty(String name, Class<T> provides, Class<S> startsWith) {
        super(name, provides, startsWith);
    }


    public abstract void set(S start, EventData data, @Nullable T value) throws BailException;

    @Override
    public ISettableDataProvider<T> provider(IDataProvider<S> startDP)
    {
        return new SettableProvider(startDP);
    }


    public class SettableProvider extends Provider implements ISettableDataProvider<T>
    {
        public SettableProvider(IDataProvider<S> startDP)
        {
            super(startDP);
        }


        @Override
        public void set(EventData data, T value) throws BailException {
            S start = startDP.get(data);
            if (start == null) return;

            SettableProperty.this.set(start, data, value);
        }

        @Override
        public boolean isSettable() {
            return true;
        }
    }
}
