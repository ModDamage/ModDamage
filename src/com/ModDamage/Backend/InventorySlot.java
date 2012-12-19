package com.ModDamage.Backend;


import org.bukkit.inventory.Inventory;

public class InventorySlot extends ItemHolder {

    public final Inventory inventory;
    public final int slotId;


    public InventorySlot(Inventory inventory, int slotId) {
        super(inventory.getItem(slotId));
        this.inventory = inventory;
        this.slotId = slotId;

    }


    public Inventory getInventory() {
        return inventory;
    }

    public int getSlotId() {
        return slotId;
    }


    @Override
    public void eventFinished(boolean success) {
        inventory.setItem(slotId, getItem());
    }
}
