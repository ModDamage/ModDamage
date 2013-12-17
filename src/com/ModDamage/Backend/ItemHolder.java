package com.ModDamage.Backend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ModDamage.Magic.MagicStuff;


public class ItemHolder {
    private ItemStack item;

    public ItemHolder(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    protected void setItem(ItemStack item) {
        this.item = item;
    	
    	save();
    }

    public Material getType() {
    	if (item == null) return null;
    	else return item.getType();
    }

    public void setType(Material material) {
    	if (item == null) item = new ItemStack(material);
    	else item.setType(material);
    	
    	save();
    }

    public int getTypeId() {
    	if (item == null) return 0;
    	else return item.getTypeId();
    }

    public void setTypeId(int type) {
    	if (item == null) item = new ItemStack(type);
    	else item.setTypeId(type);
    	
    	save();
    }

    public byte getData() {
    	if (item == null) return 0;
    	else return item.getData().getData();
    }

    public void setData(byte data) {
    	if (item == null) item = new ItemStack(1);
        item.getData().setData(data);
    	
    	save();
    }

    public short getDurability() {
    	if (item == null) return 0;
       return item.getDurability();
    }

    public void setDurability(short durability) {
    	if (item == null) item = new ItemStack(1);
        item.setDurability(durability);
    	
    	save();
    }

    public int getMaxDurability() {
    	if (item == null) return 0;
        return MagicStuff.getMaxDurability(item);
    }

    public int getAmount() {
    	if (item == null) return 0;
        return item.getAmount();
    }

    public void setAmount(int amount) {
    	if (item == null) item = new ItemStack(1);
        item.setAmount(amount);
    	
    	save();
    }

    public int getMaxStackSize() {
    	if (item == null) return 0;
        return item.getMaxStackSize();
    }
    
    public boolean isEnchanted() {
    	if (item == null) return false;
    	return !item.getEnchantments().isEmpty();
    }

    public int getEnchantmentLevel(Enchantment enchantment) {
    	if (item == null) return 0;
        return item.getEnchantmentLevel(enchantment);
    }

    public void setEnchantmentLevel(Enchantment enchantment, int level) {
    	if (item == null) item = new ItemStack(1);
        item.addUnsafeEnchantment(enchantment, level);
    	
    	save();
    }

    public void clearEnchantments() {
    	if (item == null) return;
        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            item.removeEnchantment(enchantment);
        }
    	
    	save();
    }
    
    /// Meta info ///
    
    public String getName() {
    	if (item == null) return null;
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null) return null;
    	
    	return meta.getDisplayName();
    }
    
    public void setName(String name) {
    	if (item == null) item = new ItemStack(1);
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null) return;
    	
    	meta.setDisplayName(name);
    	item.setItemMeta(meta);
    	
    	save();
    }
    
    public String getLore(int index) {
    	if (item == null) return null;
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null) return null;
    	List<String> lore = meta.getLore();
    	if (lore == null || index < 0 || index >= lore.size()) return null;
    	
    	return lore.get(index);
    }
    
    public void setLore(int index, String text) {
    	if (item == null) item = new ItemStack(1);
    	ItemMeta meta = item.getItemMeta();
    	if (meta == null) return;

    	List<String> lore = meta.getLore();
    	if (lore == null) lore = new ArrayList<String>(1);
    	if (index < 0 || index > lore.size()) return;
    	
    	if (text == null || text.equals("")) {
    		if (index >= 0 && index < lore.size())
    			lore.remove(index);
    	} else if (index == lore.size())
    		lore.add(text);
    	else
    		lore.set(index, text);

    	meta.setLore(lore);
    	item.setItemMeta(meta);
    	
    	save();
    }

    public void save() {}
    
    public String toString() {
    	if (item == null)
    		return "none";
    	return item.toString();
    }
}
