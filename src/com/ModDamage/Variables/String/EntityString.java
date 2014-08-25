package com.ModDamage.Variables.String;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.TypeNameAliaser;
import com.ModDamage.Backend.ExternalPluginManager;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.StringExp;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

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
				public IDataProvider<String> parse(ScriptLine scriptLine, EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
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
