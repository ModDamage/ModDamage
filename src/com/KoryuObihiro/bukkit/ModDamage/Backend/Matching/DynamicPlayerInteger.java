package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ExternalPluginManager;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicPlayerInteger extends DynamicInteger
{
	protected final EntityReference entityReference;
	protected final PlayerIntegerPropertyMatch propertyMatch;
	public enum PlayerIntegerPropertyMatch
	{
		BleedTicks(true, true),
		Exhaustion(true),
		Experience(true),
		FoodLevel(true),
		GameMode(true),
		Mana(true, true),
		MaxMana(false, true),
		Saturation(true),
		SleepTicks,
		TotalExperience(true),
		WieldMaterial(true),
		WieldQuantity(true);
		
		public boolean settable = false;
		public boolean usesMcMMO = false;
		private PlayerIntegerPropertyMatch(){}
		private PlayerIntegerPropertyMatch(boolean settable)
		{
			this.settable = settable;
		}
		private PlayerIntegerPropertyMatch(boolean settable, boolean usesMcMMO)
		{
			this.settable = settable;
			this.usesMcMMO = usesMcMMO;
		}
	}
	
	DynamicPlayerInteger(EntityReference reference, PlayerIntegerPropertyMatch propertyMatch)
	{
		super(propertyMatch.settable);
		this.entityReference = reference;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if(entity instanceof Player)
			switch(propertyMatch)
			{
				case BleedTicks:	return ExternalPluginManager.getMcMMOPlugin().getPlayerProfile((Player)entity).getBleedTicks();
				case Experience:	return ((Player)entity).getExperience();
				case Exhaustion:	return (int)((Player)entity).getExhaustion();
				case FoodLevel:		return ((Player)entity).getFoodLevel();
				case GameMode:		return ((Player)entity).getGameMode().getValue();
				case Mana:			return ExternalPluginManager.getMcMMOPlugin().getPlayerProfile((Player)entity).getCurrentMana();
				case MaxMana:		return ExternalPluginManager.getMcMMOPlugin().getPlayerProfile((Player)entity).getMaxMana();
				case Saturation:	return (int)((Player)entity).getSaturation();
				case SleepTicks:	return ((Player)entity).getSleepTicks();
				case TotalExperience:return ((Player)entity).getTotalExperience();
				case WieldMaterial:	return ((Player)entity).getItemInHand().getTypeId();
				case WieldQuantity:	return ((Player)entity).getItemInHand().getAmount();
			}
		return 0;//Shouldn't happen.
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value, boolean additive)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if(entity instanceof Player)
		{
			value += (additive?getValue(eventInfo):0);
			switch(propertyMatch)
			{
				case BleedTicks:	ExternalPluginManager.getMcMMOPlugin().getPlayerProfile((Player)entity).setBleedTicks(value);
				case Experience:	((Player)entity).setExperience(value);
				case Exhaustion:	((Player)entity).setExhaustion(value);
				case FoodLevel:		((Player)entity).setFoodLevel(value);
				case GameMode:		((Player)entity).setGameMode(org.bukkit.GameMode.getByValue(value));
				case Mana:			ExternalPluginManager.getMcMMOPlugin().getPlayerProfile((Player)entity).setMana(value);
				case Saturation:	((Player)entity).setSaturation(value);
				case TotalExperience:((Player)entity).setTotalExperience(value);
				case WieldMaterial:	((Player)entity).setItemInHand(new ItemStack(value, ((Player)entity).getItemInHand().getAmount()));
				case WieldQuantity:	((Player)entity).setItemInHand(new ItemStack(((Player)entity).getItemInHand().getType(), value));
			}	
		}
	}
	
	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "." + propertyMatch.name().toLowerCase();
	}

}
