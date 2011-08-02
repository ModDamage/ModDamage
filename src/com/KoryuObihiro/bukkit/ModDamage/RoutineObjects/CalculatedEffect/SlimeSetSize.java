package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffectRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class SlimeSetSize extends CalculatedEffectRoutine<Slime>
{
	final boolean forAttacker;
	public SlimeSetSize(boolean forAttacker, List<Routine> routines)
	{
		super(routines);
		this.forAttacker = forAttacker;
	}
	@Override
	protected void applyEffect(Slime affectedObject, int input){ affectedObject.setSize(input);}
	@Override
	protected Slime getAffectedObject(TargetEventInfo eventInfo){ return (getAffectedObject(eventInfo) instanceof Slime)?((Slime)getAffectedObject(eventInfo)):null;}	

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(SlimeSetSize.class, Pattern.compile(ModDamage.entityPart + "effect\\.setSlimeSize", Pattern.CASE_INSENSITIVE));
	}
	
	public static SlimeSetSize getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new SlimeSetSize(matcher.group(1).equalsIgnoreCase("attacker"), routines);
		return null;
	}
}
