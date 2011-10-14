package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityConditionalStatement;

public class PlayerPermissionEvaluation extends EntityConditionalStatement
{
	final String permissionsString;
	public PlayerPermissionEvaluation(boolean inverted, EntityReference entityReference, String permissionsString)
	{  
		super(inverted, entityReference);
		this.permissionsString = permissionsString;
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo) 
 	{
		return (entityReference.getEntity(eventInfo) instanceof Player)?((Player)entityReference.getEntity(eventInfo)).hasPermission(permissionsString):false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(PlayerPermissionEvaluation.class, Pattern.compile("(!?)(\\w+)\\.hasPermission\\.(.+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerPermissionEvaluation getNew(Matcher matcher)
	{
		if(matcher != null)
			if(EntityReference.isValid(matcher.group(2)))
				return new PlayerPermissionEvaluation(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), matcher.group(3));
		return null;
	}
}
