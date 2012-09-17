package com.ModDamage.Conditionals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.EnumHelper;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Matchables.Matchable;

@SuppressWarnings("rawtypes")
public class EnumEquals extends Conditional<Enum>
{
	public static final Pattern pattern = Pattern.compile("\\.is\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	
	private final Collection<Enum> types;
	
	public EnumEquals(IDataProvider<Enum> enumDP, Collection<Enum> types)
	{ 
		super(Enum.class, enumDP);
		this.types = types;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Boolean get(Enum matchable, EventData data)
	{
		if (matchable instanceof Matchable) {
			Matchable m = (Matchable) matchable;

			for(Enum type : types)
				if(m.matches((Matchable) type))
					return true;
		}
		for(Enum type : types)
			if(matchable == type)
				return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".is." + Utils.joinBy(",", types);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Enum.class, pattern, new IDataParser<Boolean, Enum>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Enum> enumDP, Matcher m, StringMatcher sm)
				{
					Map<String, Enum<?>> possibleTypes = EnumHelper.getTypeMapForEnum(enumDP.provides());
					
					List<Enum> types = new ArrayList<Enum>();
					
					for (String typeStr : m.group(1).split(","))
					{
						if (typeStr.equalsIgnoreCase("none"))
						{
							types.add(null);
							continue;
						}
						Enum type = possibleTypes.get(typeStr.toUpperCase());
						if (type == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: \"" + typeStr + "\" is not a valid " + enumDP.provides().getSimpleName());
							return null;
						}
						types.add(type);
					}
					
					if(types == null || types.isEmpty()) return null;
					
					return new EnumEquals(enumDP, types);
				}
			});
	}
}
