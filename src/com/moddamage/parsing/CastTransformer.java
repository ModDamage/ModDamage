package com.moddamage.parsing;

import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider.IDataTransformer;

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
