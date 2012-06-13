package com.ModDamage.Conditionals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnumHelper;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

@SuppressWarnings("rawtypes")
public class EnumEquals extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.is\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<Enum> matchableRef;
	private final Collection<Enum> types;
	
	public EnumEquals(String configString, DataRef<Enum> matchableRef, Collection<Enum> types)
	{ 
		super(configString);
		this.matchableRef = matchableRef;
		this.types = types;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		Enum matchable = matchableRef.get(data);
		if(matchable != null)
			for(Enum type : types)
				if(matchable == type)
					return true;
		return false;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EnumEquals getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			
			
			DataRef<Enum> enumRef = (DataRef<Enum>) info.get(Enum.class, name);
			if (enumRef == null) return null;
			
			Map<String, Enum<?>> possibleTypes = EnumHelper.getTypeMapForEnum(enumRef.infoCls);
			
			List<Enum> types = new ArrayList<Enum>();
			
			for (String typeStr : matcher.group(2).split(","))
			{
				Enum type = possibleTypes.get(typeStr.toUpperCase());
				if (type == null)
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: \"" + typeStr + "\" is not a valid " + enumRef.infoCls.getSimpleName());
					return null;
				}
				types.add(type);
			}
			
			if(types != null && !types.isEmpty())
				return new EnumEquals(matcher.group(), enumRef, types);
			return null;
		}
	}
}
