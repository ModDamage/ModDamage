package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class PlayerPermissionEvaluation extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.haspermission\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<Entity> entityRef;
	private final DataRef<EntityType> entityElementRef;
	private final String permission;
	public PlayerPermissionEvaluation(DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, String permission)
	{  
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.permission = permission;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
 	{
		return (entityElementRef.get(data).matches(EntityType.PLAYER))?ExternalPluginManager.getPermissionsManager().hasPermission(((Player)entityRef.get(data)), permission):false;//XXX Include hasPermission in EntityReference?
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public PlayerPermissionEvaluation getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<Entity> entityRef = info.get(Entity.class, name);
			DataRef<EntityType> entityElementRef = info.get(EntityType.class, name);
			if(entityRef != null)
				return new PlayerPermissionEvaluation(entityRef, entityElementRef, matcher.group(2));
			return null;
		}
	}
}
