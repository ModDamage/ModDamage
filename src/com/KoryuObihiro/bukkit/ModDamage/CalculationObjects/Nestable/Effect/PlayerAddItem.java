package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public class PlayerAddItem extends PlayerEffectCalculation<Integer>
{
	protected Material material;
	public PlayerAddItem(boolean forAttacker, int quantity) 
	{
		super(forAttacker, quantity);
	}
	@Override
	void applyEffect(Player affectedObject, Integer input) 
	{
		affectedObject.setItemInHand(new ItemStack(affectedObject.getItemInHand().getType(), affectedObject.getItemInHand().getAmount() + input));
	}
	
	@Override
	protected Integer calculateInputValue(DamageEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventDamage, temp2;
		eventInfo.eventDamage = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventDamage;
		eventInfo.eventDamage = temp1;
		return temp2;
	}

	@Override
	protected Integer calculateInputValue(SpawnEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventHealth, temp2;
		eventInfo.eventHealth = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventHealth;
		eventInfo.eventHealth = temp1;
		return temp2;
	}
}
