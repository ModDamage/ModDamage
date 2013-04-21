package com.ModDamage.Properties;

import org.bukkit.entity.Item;

import com.ModDamage.Backend.ItemEntityHolder;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Parsing.Property.Properties;
import com.ModDamage.Parsing.Property.Property;

public class ItemProps
{
	public static void register()
	{
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
