package com.ModDamage.Variables.Ints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.ModDamage;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class McMMOInt extends IntegerExp
{
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("([a-z]+)_SKILL_(\\w+)", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						try
						{
							String name = matcher.group(1).toLowerCase();
							DataRef<Entity> entityRef = info.get(Entity.class, name);
							DataRef<EntityType> entityElementRef = info.get(EntityType.class, name);
							if (entityRef == null || entityElementRef == null) return null;
							
							return sm.acceptIf(new McMMOInt(
									entityRef, entityElementRef,
									SkillType.valueOf(matcher.group(2).toUpperCase())));
						}
						catch (IllegalArgumentException e) { }
						catch (NoClassDefFoundError e) {
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "McMMO has changed. Please notify the ModDamage developers.");
						}
						return null;
					}
				});
	}
	
	//protected static mcMMO mcMMOplugin;
	protected final SkillType skillType;
	protected final DataRef<Entity> entityRef;
	protected final DataRef<EntityType> entityElementRef;
	
	McMMOInt(DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, SkillType skillType)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.skillType = skillType;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		if(entityElementRef.get(data).matches(EntityType.PLAYER))
		{
			mcMMO mcMMOplugin = ExternalPluginManager.getMcMMOPlugin();
			if(mcMMOplugin != null)
				return mcMMOplugin.getPlayerProfile(((Player)entityRef.get(data))).getSkillLevel(skillType);
		}
		return 0;
	}
	
	@Override
	public String toString()
	{
		return /*FIXME entityIndex.name().toLowerCase() +*/ "_skill_" + skillType.name().toLowerCase();
	}
}
