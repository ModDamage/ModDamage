package com.moddamage.parsing.property;

import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.DataProvider.IDataTransformer;
import com.moddamage.parsing.IDataProvider;

public class PropertyTransformer<T, S> implements IDataTransformer<T, S>
{
	final Property<T, S> property;
	
	public PropertyTransformer(Property<T, S> property) {
		this.property = property;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IDataProvider<T> transform(EventInfo info, final IDataProvider<S> dp)
	{
		return new DataProvider<T, S>((Class<S>) dp.provides(), dp)
			{
				@Override
				public T get(S start, EventData data) throws BailException
				{
					return property.get(start, data);
				}

				@Override
				public Class<? extends T> provides()
				{
					return property.provides;
				}
				
				@Override
				public String toString()
				{
					return startDP.toString();
				}
			};
	}
}
