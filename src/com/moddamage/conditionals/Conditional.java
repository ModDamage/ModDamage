package com.moddamage.conditionals;

import com.moddamage.expressions.function.PlayerCanSeeFunction;
import com.moddamage.expressions.function.PlayerNamedFunction;
import com.moddamage.expressions.function.WorldNamedFunction;
import com.moddamage.external.mcMMO.AbilityConditional;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;


public abstract class Conditional<S> extends DataProvider<Boolean, S>
{
	public static void register()
	{
		Chance.register();
		Comparison.register();
		Equality.register();
		CompoundConditional.register();
		InvertBoolean.register();
		//Entity
		LocationBiome.register();
		EntityBlockStatus.register();
		EntityHasPotionEffect.register();
		LocationRegion.register();
		EntityStatus.register();
		IsTagged.register();
		PlayerWearing.register();
		PlayerWielding.register();
		//Player
		PlayerHasEnchantment.register();
		PlayerHasItem.register();
		PlayerHasPermission.register();
		PlayerInGroup.register();
        PlayerNamed.register();
		PlayerStatus.register();
        PlayerGameMode.register();
        PlayerCanSee.register();
        PlayerCanSeeFunction.register();
		PlayerNamedFunction.register();
		//Server
		ServerOnlineMode.register();
		//World
		WorldEnvironment.register();
		WorldNamed.register();
		WorldStatus.register();
		WorldNamedFunction.register();
		//Other
		EnumEquals.register();
		ItemMatches.register();
		LivingEntityStatus.register();
		StringConditionals.register();
		StringMatches.register();
		
		//mcMMO
		AbilityConditional.register();
	}
	
	
	protected Conditional(Class<S> wantStart, IDataProvider<S> startDP)
	{
		super(wantStart, startDP);
		defaultValue = false;
	}

	@Override
	public Class<Boolean> provides() { return Boolean.class; }
	
	public abstract String toString();
}
