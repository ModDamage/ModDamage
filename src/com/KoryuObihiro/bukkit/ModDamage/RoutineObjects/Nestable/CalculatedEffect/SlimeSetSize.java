package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.CalculatedEffect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class SlimeSetSize extends CalculatedEffectRoutine<Slime>
{
	final boolean forAttacker;
	public SlimeSetSize(boolean forAttacker, List<Routine> calculations)
	{
		super(calculations);
		this.forAttacker = forAttacker;
	}

	@Override
	void applyEffect(Slime affectedObject, int input){ affectedObject.setSize(input);}

	@Override
	protected Slime getAffectedObject(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.element_attacker:eventInfo.element_target).equals(DamageElement.MOB_SLIME)?((Slime)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)):null;}

	@Override
	protected Slime getAffectedObject(SpawnEventInfo eventInfo){ return (eventInfo.element.equals(DamageElement.MOB_SLIME))?((Slime)eventInfo.entity):null;}	

	public static void register(RoutineUtility routineUtility)
	{
		routineUtility.registerBase(PlayerSetItem.class, Pattern.compile("(entity|attacker|target)effect\\.setSlimeSize\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
