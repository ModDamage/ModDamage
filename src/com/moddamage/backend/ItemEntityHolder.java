package com.ModDamage.Backend;

import org.bukkit.entity.Item;

public class ItemEntityHolder extends ItemHolder {
    public final Item itemEntity;

    public ItemEntityHolder(Item itemEntity) {
        super(itemEntity.getItemStack());
        this.itemEntity = itemEntity;
    }

    public Item getItemEntity() {
        return itemEntity;
    }

    public int getPickupDelay() {
        return itemEntity.getPickupDelay();
    }

    public void setPickupDelay(int pickupDelay) {
        itemEntity.setPickupDelay(pickupDelay);
    }

    @Override
    public void save() {
        itemEntity.setItemStack(getItem());
    }
}
