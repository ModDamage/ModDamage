package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

public class EntityWielding extends EntityConditionalStatement 
{
	final Collection<Material> materials;
	public EntityWielding(boolean inverted, EntityReference entityReference, Collection<Material> materials)
	{  
		super(inverted, entityReference);
		this.materials = materials;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return materials.contains(entityReference.getMaterial(eventInfo));}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.wielding\\.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}	
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EntityWielding getNew(Matcher matcher)
		{
			Collection<Material> matchedItems = AliasManager.matchMaterialAlias(matcher.group(3));
			if(!matchedItems.isEmpty())
				return new EntityWielding(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), matchedItems);
			return null;
		}
	}
}
