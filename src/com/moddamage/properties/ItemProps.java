package com.moddamage.properties;

import org.bukkit.entity.Item;

import com.moddamage.backend.ItemEntityHolder;
import com.moddamage.backend.ItemHolder;
import com.moddamage.eventinfo.EventData;
import com.moddamage.parsing.property.Properties;
import com.moddamage.parsing.property.Property;

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
        Properties.register("isenchanted", ItemHolder.class, "isEnchanted");
        
        // Meta
        Properties.register("name", ItemHolder.class, "getName", "setName");
        Properties.register("owner", ItemHolder.class, "getOwner", "setOwner");
	}

}
