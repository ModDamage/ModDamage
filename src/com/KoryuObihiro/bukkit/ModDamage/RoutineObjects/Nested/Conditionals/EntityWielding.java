package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public class EntityWielding extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.wielding\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	final Collection<Material> materials;
	public EntityWielding(EntityReference entityReference, Collection<Material> materials)
	{  
		this.entityReference = entityReference;
		this.materials = materials;
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo){ return materials.contains(entityReference.getMaterial(eventInfo));}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}	
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityWielding getNew(Matcher matcher)
		{
			Collection<Material> matchedItems = AliasManager.matchMaterialAlias(matcher.group(2));
			if(!matchedItems.isEmpty())
				return new EntityWielding(EntityReference.match(matcher.group(1)), matchedItems);
			return null;
		}
	}
}
