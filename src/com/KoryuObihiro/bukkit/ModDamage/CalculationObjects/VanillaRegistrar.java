package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Addition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DiceRollAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Division;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DivisionAddition;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.IntervalRange;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.LiteralRange;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Multiplication;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Set;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.EventMiss;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityAirTicksComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityCoordinateComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityDrowning;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityExposedToSky;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityFallComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityFalling;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityFireTicksComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityHealthComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityLightComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityOnBlock;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityOnFire;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityTargetedByOther;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EntityUnderwater;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.EventValueComparison;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWearing;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWearingOnly;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.PlayerWielding;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.ServerOnlineMode;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.ServerPlayerCount;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional.WorldTime;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntityHeal;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntityReflect;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntitySetAirTicks;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntitySetFireTicks;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.EntitySetHealth;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.PlayerSetItem;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect.SlimeSetSize;

public final class VanillaRegistrar extends ModDamageCalculationRegistrar 
{
	public VanillaRegistrar(){ super(ModDamage.plugin);}
	public void registerCalculations()
	{
//Base Calculations
		Addition.register();
		DiceRoll.register();
		DiceRollAddition.register();
		Division.register();
		DivisionAddition.register();
		IntervalRange.register();
		LiteralRange.register();
		Multiplication.register();
		Set.register();	
//Nestable Calculations
	//Conditionals
		EventMiss.register();
		//Entity
		EntityAirTicksComparison.register();
		EntityBiome.register();
		EntityCoordinateComparison.register();
		EntityDrowning.register();
		EntityExposedToSky.register();
		EntityFallComparison.register();
		EntityFalling.register();
		EntityFireTicksComparison.register();
		EntityHealthComparison.register();
		EntityLightComparison.register();
		EntityOnBlock.register();
		EntityOnFire.register();
		EntityTargetedByOther.register();
		EntityUnderwater.register();
		EventValueComparison.register();
		PlayerWearing.register();
		PlayerWearingOnly.register();
		PlayerWielding.register();
		//World
		WorldTime.register();
		WorldEnvironment.register();
		//Server
		ServerOnlineMode.register();
		ServerPlayerCount.register();
		//Event
		EventValueComparison.register();
	//Effects
		EntityExplode.register();
		EntityHeal.register();
		EntityReflect.register();
		EntitySetAirTicks.register();
		EntitySetFireTicks.register();
		EntitySetHealth.register();
		PlayerSetItem.register();
		SlimeSetSize.register();
	}
}
