package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.Backend.Aliasing.MaterialAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EntityWielding extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.wielding\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	final DataRef<Entity> entityRef;
	final Collection<Material> materials;
	public EntityWielding(DataRef<Entity> entityRef, Collection<Material> materials)
	{  
		this.entityRef = entityRef;
		this.materials = materials;
	}
	@Override
	public boolean evaluate(EventData data)
	{
		Entity entity = entityRef.get(data);
		if (entity instanceof Player)
			return materials.contains(((Player)entity).getItemInHand().getType());
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
		public EntityWielding getNew(Matcher matcher, EventInfo info)
		{
			Collection<Material> matchedItems = MaterialAliaser.match(matcher.group(2));
			if(!matchedItems.isEmpty())
				return new EntityWielding(info.get(Entity.class, matcher.group(1).toLowerCase()), matchedItems);
			return null;
		}
	}
}
