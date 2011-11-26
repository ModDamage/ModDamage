package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EntityWearing extends EntityConditionalStatement
{
	final boolean inclusiveComparison;
	final Collection<ArmorSet> armorSets;
	public EntityWearing(boolean inverted, boolean inclusiveComparison, EntityReference entityReference, Collection<ArmorSet> armorSets)
	{  
		super(inverted, entityReference);
		this.inclusiveComparison = inclusiveComparison;
		this.armorSets = armorSets;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo)
	{
		ArmorSet playerSet = entityReference.getArmorSet(eventInfo);
		if(playerSet != null)
			for(ArmorSet armorSet : armorSets)
				if(inclusiveComparison?armorSet.equals(playerSet):armorSet.contains(playerSet))
					return true;
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.(wearing|wearingonly)\\.([\\*\\w]+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EntityWearing getNew(Matcher matcher)
		{
			Collection<ArmorSet> armorSet = AliasManager.matchArmorAlias(matcher.group(4));
			EntityReference reference = EntityReference.match(matcher.group(2));
			if(!armorSet.isEmpty() && reference != null)
				return new EntityWearing(matcher.group(1).equalsIgnoreCase("!"), matcher.group(3).endsWith("only"), reference, armorSet);
			return null;
		}
	}
}
