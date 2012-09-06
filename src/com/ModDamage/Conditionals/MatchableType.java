package com.ModDamage.Conditionals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnumHelper;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Matchables.Matchable;

@SuppressWarnings("rawtypes")
public class MatchableType extends Conditional<Matchable>
{
	public static final Pattern pattern = Pattern.compile("\\.type\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	
	private final Collection<Matchable> types;
	
	public MatchableType(IDataProvider<Matchable> matchableDP, Collection<Matchable> types)
	{ 
		super(Matchable.class, matchableDP);
		this.types = types;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Boolean get(Matchable matchable, EventData data)
	{
		for(Matchable<?> type : types)
			if(matchable.matches(type))
				return true;
		return false;
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Matchable.class, pattern, new IDataParser<Boolean, Matchable>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Matchable> matchableDP, Matcher m, StringMatcher sm)
				{
					assert(Enum.class.isAssignableFrom(matchableDP.provides()));
					@SuppressWarnings("unchecked")
					Map<String, Matchable<?>> possibleTypes = (Map) EnumHelper.getTypeMapForEnum(matchableDP.provides());
					
					List<Matchable> types = new ArrayList<Matchable>(1);
					
					for (String typeStr : m.group(1).split(","))
					{
						Matchable type = possibleTypes.get(typeStr.toUpperCase());
						if (type == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: \"" + typeStr + "\" is not a valid " + matchableDP.provides().getSimpleName());
							return null;
						}
						types.add(type);
					}
					
					if(types == null || types.isEmpty()) return null;
					
					return new MatchableType(matchableDP, types);
				}
			});
		
		DataProvider.registerTransformer(EntityType.class, Entity.class, new IDataTransformer<EntityType, Entity>()
			{
				@Override
				public IDataProvider<EntityType> transform(EventInfo info, final IDataProvider<Entity> entityDP)
				{
					return new IDataProvider<EntityType>() // can't be DataProvider because we (probably?) never want to return
						{
							@Override
							public EntityType get(EventData data) throws BailException
							{
								return EntityType.get(entityDP.get(data));
							}

							@Override
							public Class<EntityType> provides()
							{
								return EntityType.class;
							}
							
							@Override
							public String toString()
							{
								return entityDP.toString();
							}
						};
				}
			});
	}
	
	@Override
	public String toString()
	{
		return startDP + ".type." + Utils.joinBy(",", types);
	}
}
