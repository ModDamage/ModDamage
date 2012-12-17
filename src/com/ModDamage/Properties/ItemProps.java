package com.ModDamage.Properties;

import com.ModDamage.Backend.ItemEntityHolder;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.*;

import org.bukkit.entity.Item;

public class ItemProps
{
	public static void register()
	{
        Properties.register("item", Item.class, "getItemStack");

        Properties.register(new Property<ItemHolder, Item>("item", ItemHolder.class, Item.class) {
            public ItemHolder get(Item itemEntity, EventData data) {
                return new ItemEntityHolder(itemEntity);
            }
        });

        Properties.register("durability", ItemHolder.class, "getDurability", "setDurability");
        Properties.register("maxdurability", ItemHolder.class, "getMaxDurability");
        Properties.register("data", ItemHolder.class, "getData", "setData");
        Properties.register("amount", ItemHolder.class, "getAmount", "setAmount");
        Properties.register("maxamount", ItemHolder.class, "getMaxStackSize");
        Properties.register("type", ItemHolder.class, "getType", "setType");
        Properties.register("typeid", ItemHolder.class, "getTypeId", "setTypeId");
        
        // Meta
        Properties.register("name", ItemHolder.class, "getName", "setName");
	}

}
