package com.ModDamage.Conditionals;

import com.ModDamage.External.mcMMO.AbilityConditional;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;


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
		//Server
		ServerOnlineMode.register();
		//World
		WorldEnvironment.register();
		WorldNamed.register();
		WorldStatus.register();
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
