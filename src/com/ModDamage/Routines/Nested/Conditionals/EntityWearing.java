package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.Backend.ArmorSet;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Aliasing.ArmorAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EntityWearing extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.wearing(only)?\\.([\\w*]+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<Entity> entityRef;
	private final boolean only;
	private final Collection<ArmorSet> armorSets;
	public EntityWearing(DataRef<Entity> entityRef, boolean only, Collection<ArmorSet> armorSets)
	{  
		this.entityRef = entityRef;
		this.only = only;
		this.armorSets = armorSets;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		ArmorSet playerSet = new ArmorSet((Player) entityRef.get(data));
		if(playerSet != null)
			for(ArmorSet armorSet : armorSets)
				if(only? armorSet.equals(playerSet) : armorSet.contains(playerSet))
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
		public EntityWearing getNew(Matcher matcher, EventInfo info)
		{
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			Collection<ArmorSet> armorSet = ArmorAliaser.match(matcher.group(3));
			if(entityRef != null && !armorSet.isEmpty())
				return new EntityWearing(entityRef, matcher.group(2) != null, armorSet);
			return null;
		}
	}
}
