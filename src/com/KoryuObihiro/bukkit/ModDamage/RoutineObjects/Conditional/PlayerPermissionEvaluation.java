package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class PlayerPermissionEvaluation extends EntityConditionalStatement
{
	final String permission;
	public PlayerPermissionEvaluation(boolean inverted, EntityReference entityReference, String permission)
	{  
		super(inverted, entityReference);
		this.permission = permission;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo) 
 	{
		return (entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))?ExternalPluginManager.getPermissionsManager().hasPermission(((Player)entityReference.getEntity(eventInfo)), permission):false;//XXX Include hasPermission in EntityReference?
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.haspermission\\.(.+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public PlayerPermissionEvaluation getNew(Matcher matcher)
		{
			EntityReference reference = EntityReference.match(matcher.group(2));
			if(reference != null)
				return new PlayerPermissionEvaluation(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), matcher.group(3));
			return null;
		}
	}
}
