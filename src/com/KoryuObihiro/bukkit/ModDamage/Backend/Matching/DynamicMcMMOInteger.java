package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.SkillType;

public class DynamicMcMMOInteger extends DynamicInteger
{
	protected static mcMMO mcMMOplugin;
	protected final SkillType skillType;
	protected final EntityReference entityReference;
	
	DynamicMcMMOInteger(EntityReference reference, SkillType skillType, boolean isNegative)
	{
		super(isNegative, false);
		this.entityReference = reference;
		this.skillType = skillType;
	}
	
	@Override
	public Integer getValue(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			mcMMOplugin = ExternalPluginManager.getMcMMOPlugin();
			if(mcMMOplugin != null)
				return (isNegative?-1:1) * mcMMOplugin.getPlayerProfile(((Player)entityReference.getEntity(eventInfo))).getSkillLevel(skillType);
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return isNegative?"-":"" + entityReference.name().toLowerCase() + "." + skillType.name().toLowerCase();
	}
}
