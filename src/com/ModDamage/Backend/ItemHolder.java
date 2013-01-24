package com.ModDamage.Backend;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ModDamage.MDEvent;
import com.ModDamage.MagicStuff;


public class ItemHolder implements EventFinishedListener {
    private ItemStack item;
    private boolean dirty = false;

    public ItemHolder(ItemStack item) {
        this.item = item;
    }

    public boolean isDirty() {
        return dirty;
    }

    public ItemStack getItem() {
        return item;
    }

    protected void setItem(ItemStack item) {
        this.item = item;
        dirty();
    }

    public Material getType() {
        return item.getType();
    }

    public void setType(Material material) {
        item.setType(material);
        if (!dirty) dirty();
    }

    public int getTypeId() {
        return item.getTypeId();
    }

    public void setTypeId(int type) {
        item.setTypeId(type);
        if (!dirty) dirty();
    }

    public byte getData() {
        return item.getData().getData();
    }

    public void setData(byte data) {
        item.getData().setData(data);
        if (!dirty) dirty();
    }

    public short getDurability() {
       return item.getDurability();
    }

    public void setDurability(short durability) {
        item.setDurability(durability);
        if (!dirty) dirty();
    }

    public int getMaxDurability() {
        return MagicStuff.getMaxDurability(item);
    }

    public int getAmount() {
        return item.getAmount();
    }

    public void setAmount(int amount) {
        item.setAmount(amount);
        if (!dirty) dirty();
    }

    public int getMaxStackSize() {
        return item.getMaxStackSize();
    }

    public int getEnchantmentLevel(Enchantment enchantment) {
        return item.getEnchantmentLevel(enchantment);
    }

    public void setEnchantmentLevel(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        if (!dirty) dirty();
    }

    public void clearEnchantments() {
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            item.removeEnchantment(enchantment);
        }
        if (!dirty) dirty();
    }
    
    /// Meta info ///
    
    public String getName() {
    	return item.getItemMeta().getDisplayName();
    }
    
    public void setName(String name) {
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName(name);
    	item.setItemMeta(meta);
    }
    
    public String getLore(int index) {
    	List<String> lore = item.getItemMeta().getLore();
    	if (index < 0 || index >= lore.size()) return null;
    	
    	return lore.get(index);
    }
    
    public void setLore(int index, String text) {
    	ItemMeta meta = item.getItemMeta();

    	List<String> lore = item.getItemMeta().getLore();
    	if (index < 0 || index > lore.size()) return;
    	
    	if (text == null || text.equals(""))
    		lore.remove(index);
    	else if (index == lore.size())
    		lore.add(text);
    	else
    		lore.set(index, text);

    	meta.setLore(lore);
    	item.setItemMeta(meta);
    }
    
    

    private void dirty() {
        if (!dirty) {
            dirty = true;
            MDEvent.whenEventFinishes(this);
        }
    }

    @Override
    public void eventFinished(boolean success) {}
}
