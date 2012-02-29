package com.ModDamage.Routines.Nested.Conditionals;

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
import com.ModDamage.Backend.Matchable;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

@SuppressWarnings("rawtypes")
public class MatchableType extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.type\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<Matchable> matchableRef;
	private final Collection<Matchable> types;
	
	public MatchableType(DataRef<Matchable> matchableRef, Collection<Matchable> types)
	{ 
		this.matchableRef = matchableRef;
		this.types = types;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		Matchable<?> matchable = matchableRef.get(data);
		if(matchable != null)
			for(Matchable<?> type : types)
				if(matchable.matches(type))
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
		public MatchableType getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			
			
			DataRef<Matchable> matchableRef = (DataRef<Matchable>) info.get(Matchable.class, name);
			if (matchableRef == null) return null;
			
			Map<String, Matchable<?>> possibleTypes = EnumHelper.getTypeMapForEnum(matchableRef.cls);
			
			List<Matchable> types = new ArrayList<Matchable>();
			
			for (String typeStr : matcher.group(2).split(","))
			{
				Matchable type = possibleTypes.get(typeStr.toUpperCase());
				if (type == null)
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: \"" + typeStr + "\" is not a valid " + matchableRef.cls.getSimpleName());
					return null;
				}
				types.add(type);
			}
			
			if(types != null && !types.isEmpty())
				return new MatchableType(matchableRef, types);
			return null;
		}
	}
}
