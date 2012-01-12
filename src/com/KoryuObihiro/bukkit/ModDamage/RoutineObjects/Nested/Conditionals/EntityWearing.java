package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public class EntityWearing extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(!?)(\\w+)\\.wearing(only)?\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	final boolean inclusiveComparison;
	final Collection<ArmorSet> armorSets;
	public EntityWearing(boolean inclusiveComparison, EntityReference entityReference, Collection<ArmorSet> armorSets)
	{  
		this.entityReference = entityReference;
		this.inclusiveComparison = inclusiveComparison;
		this.armorSets = armorSets;
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
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
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityWearing getNew(Matcher matcher)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			Collection<ArmorSet> armorSet = AliasManager.matchArmorAlias(matcher.group(3));
			if(!armorSet.isEmpty() && reference != null)
				return new EntityWearing(matcher.group(2) != null, reference, armorSet);
			return null;
		}
	}
}
