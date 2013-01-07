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

        DataProvider.registerTransformer(HumanEntity.class, "getInventory");
        DataProvider.registerTransformer(PlayerInventory.class, EntityEquipment.class);
	}
}
