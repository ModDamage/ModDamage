package com.ModDamage.Properties;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.PlayerInventory;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.Property.Properties;
import com.ModDamage.Parsing.Property.SettableProperty;

public class PlayerProps
{
	public static void register()
	{
        Properties.register("exhaustion", Player.class, "getExhaustion", "setExhaustion");
        Properties.register(new SettableProperty<Integer, Player>("experience", Integer.class, Player.class) {
                public Integer get(Player player, EventData data) { return (int) (player.getExp() * 100); }
                public void set(Player player, EventData data, Integer value) {  player.setExp((float) (value / 100.0)); }
            });
        Properties.register("foodlevel", Player.class, "getFoodLevel", "setFoodLevel");
        Properties.register("gamemode", Player.class, "getGameMode", "setGameMode");
        Properties.register("level", Player.class, "getLevel", "setLevel");
        Properties.register("saturation", Player.class, "getSaturation", "setSaturation");
        Properties.register("sleepticks", Player.class, "getSleepTicks");
        Properties.register("totalexperience", Player.class, "getTotalExperience", "setTotalExperience");
        Properties.register("displayname", Player.class, "getDisplayName", "setDisplayName");
        Properties.register("playerlistname", Player.class, "getPlayerListName", "setPlayerListName");
        Properties.register("compasstarget", Player.class, "getCompassTarget", "setCompassTarget");

        Properties.register("allowflight", Player.class, "getAllowFlight", "setAllowFlight");
        Properties.register("isflying", Player.class, "isFlying", "setFlying");
        Properties.register("issprinting", Player.class, "isSprinting", "setSprinting");
        Properties.register("issneaking", Player.class, "isSneaking", "setSneaking");
        
        Properties.register(new SettableProperty<Integer, Player>("flyspeed", Integer.class, Player.class) {
				public Integer get(Player player, EventData data) {
					return (int)(player.getFlySpeed() * 100);
				}
				public void set(Player player, EventData data, Integer value) {
					if (value > 100) value = 100;
					if (value < -100) value = -100;
					player.setFlySpeed(value / 100.0f);
				}
			});
        Properties.register(new SettableProperty<Integer, Player>("walkspeed", Integer.class, Player.class) {
				public Integer get(Player player, EventData data) {
					return (int)(player.getWalkSpeed() * 100);
				}
				public void set(Player player, EventData data, Integer value) {
					if (value > 100) value = 100;
					if (value < -100) value = -100;
					player.setWalkSpeed(value / 100.0f);
				}
			});
        
        Properties.register("ishealthscaled", Player.class, "isHealthScaled", "setHealthScaled");
        Properties.register("healthscale", Player.class, "getHealthScale", "setHealthScale");

        DataProvider.registerTransformer(HumanEntity.class, "getInventory");
        Properties.register("enderchest", HumanEntity.class, "getEnderChest");
        DataProvider.registerTransformer(PlayerInventory.class, EntityEquipment.class);
	}
}
