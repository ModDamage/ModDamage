package com.ModDamage.Parsing.Property;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.DataProvider.IDataTransformer;
import com.ModDamage.Parsing.IDataProvider;

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
