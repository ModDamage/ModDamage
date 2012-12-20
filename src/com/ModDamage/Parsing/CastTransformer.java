package com.ModDamage.Parsing;

import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider.IDataTransformer;

public class CastTransformer<T, S> implements IDataTransformer<T, S>
{
	final Class<T> provides;
	
	public CastTransformer(Class<T> provides)
	{
		this.provides = provides;
	}
	
	@Override
	public IDataProvider<T> transform(EventInfo info, IDataProvider<S> dp)
	{
		return new CastDataProvider<T>(dp, provides);
	}

}
