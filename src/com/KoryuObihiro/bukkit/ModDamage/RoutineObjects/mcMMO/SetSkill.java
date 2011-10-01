package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.mcMMO;


public class SetSkill{}/* extends McMMOCalculationRoutine
{	
	protected SetSkill(String configString, EntityReference entityReference, IntegerMatch value, SkillType skillType)
	{
		super(configString, value, entityReference);
		
	}
	
	protected SetSkill(String configString, EntityReference entityReference, SkillType skillType)
	{
		super(configString, routines, entityReference);
		
	}

	@Override
	protected void applyEffect(Player affectedObject, int input) {
		// TODO Auto-generated method stub
		
	}

	public static void register()
	{
		Routine.registerBase(SetSkill.class, Pattern.compile("(\\w+)effect\\.setskill\\.(\\w+)\\." + IntegerMatch.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
		CalculationRoutine.registerCalculation(SetSkill.class, Pattern.compile("(\\w+)effect\\.setskill\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static SetSkill getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			SkillType skillType = null;
			for(SkillType skill : SkillType.values())
				if(matcher.group(2).equalsIgnoreCase(skill.name()))
					skillType = skill;
			IntegerMatch match = IntegerMatch.getNew(matcher.group(3));
			if(EntityReference.isValid(matcher.group(1)) && match != null && skillType != null);
				return new SetSkill(matcher.group(), EntityReference.match(matcher.group(1)), match, skillType);
		}
		return null;
	}
	
	public static SetSkill getNew(String configString, Object nestedContent)
	{
		if(configString != null && nestedContent != null)
		{
			//TODO Finish me!
		}
		return null;
	}

	@Override
	protected void applyEffect(Player affectedObject, int input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Player getAffectedObject(TargetEventInfo eventInfo) {
		// TODO Auto-generated method stub
		return null;
	}
}
	*/
