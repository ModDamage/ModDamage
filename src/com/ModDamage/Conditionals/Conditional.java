package com.ModDamage.Conditionals;

import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.External.mcMMO.AbilityConditional;


public abstract class Conditional<S> extends DataProvider<Boolean, S>
{
	public static void register()
	{
		Chance.register();
		Comparison.register();
		CompoundConditional.register();
		InvertBoolean.register();
		NestedBool.register();
		//Entity
		EntityBiome.register();
		EntityBlockStatus.register();
		EntityHasPotionEffect.register();
		EntityRegion.register();
		EntityStatus.register();
		EntityTagged.register();
		PlayerWearing.register();
		PlayerWielding.register();
		//Player
		PlayerHasEnchantment.register();
		PlayerHasItem.register();
		PlayerHasPermission.register();
		PlayerInGroup.register();
		PlayerStatus.register();
		//Server
		ServerOnlineMode.register();
		//World
		WorldEnvironment.register();
		WorldNamed.register();
		//Other
		MatchableType.register();
		EnumEquals.register();
		ItemMatches.register();
		LivingEntityStatus.register();
		
		//mcMMO
		AbilityConditional.register();
	}
	
	
	protected Conditional(Class<S> wantStart, IDataProvider<S> startDP)
	{
		super(wantStart, startDP);
	}

	@Override
	public Class<Boolean> provides() { return Boolean.class; }
}
