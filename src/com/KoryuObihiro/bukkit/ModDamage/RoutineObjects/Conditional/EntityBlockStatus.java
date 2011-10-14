package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityBlockStatus extends EntityConditionalStatement
{
	final BlockStatusType statusType;
	final List<Material> materials;
	protected EntityBlockStatus(boolean inverted, EntityReference entityReference, BlockStatusType statusType, List<Material> materials)
	{
		super(inverted, entityReference);
		this.statusType = statusType;
		this.materials = materials;
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		switch(statusType)
		{
			case OnBlock:
				return materials.contains(entityReference.getEntity(eventInfo).getLocation().add(0, -1, 0).getBlock().getType());
			case OverBlock:
			case UnderBlock:
			default: return false;
		}
	}
	
	private enum BlockStatusType
	{
		OnBlock,
		OverBlock,
		UnderBlock;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityBlockStatus.class, Pattern.compile("(!?)(\\w+)\\.is(\\w+)block\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityBlockStatus getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			BlockStatusType statusType = null;
			for(BlockStatusType type : BlockStatusType.values())
				if(matcher.group(3).equalsIgnoreCase(type.name()))
						statusType = type;
			List<Material> materials = ModDamage.matchMaterialAlias(matcher.group(4));
			if(EntityReference.isValid(matcher.group(2)) && statusType != null)
				return new EntityBlockStatus(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), statusType, materials);
		}
		return null;
	}

}
