package com.moddamage.variables.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.moddamage.ModDamage;
import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.alias.TypeNameAliaser;
import com.moddamage.backend.ExternalPluginManager;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.StringExp;
import com.moddamage.matchables.EntityType;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class EntityString extends StringExp<Entity>
{
	private static Pattern pattern = Pattern.compile("_("+ Utils.joinBy("|", EntityStringProperty.values()) +")", Pattern.CASE_INSENSITIVE);
	
	public enum EntityStringProperty
	{
		REGIONS
		{
			@Override protected String getString(Entity entity)
			{
				return ExternalPluginManager.getRegions(entity.getLocation()).toString();
			}
		},
		TAGS
		{
			@Override protected String getString(Entity entity)
			{
				return ModDamage.getTagger().numTags.onEntity.getTags(entity).toString();
			}
		},
		TYPENAME
		{
			@Override protected String getString(Entity entity)
			{
				return TypeNameAliaser.aliaser.toString(EntityType.get(entity));
			}
		},
		UID
		{
			@Override
			protected String getString(Entity entity) {
				return entity.getUniqueId().toString();
			}
		};
		
		abstract protected String getString(Entity entity);
	}
	

	private final EntityStringProperty propertyMatch;
	
	public EntityString(IDataProvider<Entity> entityDP, EntityStringProperty propertyMatch)
	{
		super(Entity.class, entityDP);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public String get(Entity entity, EventData data)
	{
		return propertyMatch.getString(entity);
	}
	
	public static void register()
	{
		DataProvider.register(String.class, Entity.class, pattern, new IDataParser<String, Entity>()
			{
				@Override
				public IDataProvider<String> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
				{
					return new EntityString(entityDP, EntityStringProperty.valueOf(m.group(1).toUpperCase()));
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_" + propertyMatch.name().toLowerCase();
	}
}
