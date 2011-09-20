package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityUnderwater extends EntityConditionalStatement
{
	static final List<Material> waterList = Arrays.asList(Material.WATER, Material.STATIONARY_WATER);
	public EntityUnderwater(boolean inverted, EntityReference entityReference)
	{ 
		super(inverted, entityReference);
	}
	@Override 
	protected boolean condition(TargetEventInfo eventInfo)
	{
		return waterList.contains(entityReference.getEntity(eventInfo).getLocation().getBlock().getType())
				&& (entityReference.getEntity(eventInfo) instanceof LivingEntity)?waterList.contains(((LivingEntity)entityReference.getEntity(eventInfo)).getEyeLocation().getBlock().getType()):true;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerStatement(EntityUnderwater.class, Pattern.compile("(!?)(\\w+)\\.underwater", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityUnderwater getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			return new EntityUnderwater(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)));
		}
		return null;
	}
}
