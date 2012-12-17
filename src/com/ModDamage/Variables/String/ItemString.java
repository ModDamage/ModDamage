package com.ModDamage.Variables.String;


import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.*;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import net.minecraft.server.NBTBase;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemString {
    public static NBTTagCompound getDisplay(ItemStack item)
    {
        try {
            CraftItemStack citem = ((CraftItemStack)item);
            net.minecraft.server.ItemStack nmsitem = citem.getHandle();
            NBTTagCompound tag = nmsitem.tag;
            if (tag == null) {
                nmsitem.tag = tag = new NBTTagCompound();
                tag.setCompound("display", new NBTTagCompound());
            }
            NBTBase display = tag.get("display");
            return (NBTTagCompound) display;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void register()
    {
        SettableDataProvider.register(String.class, ItemStack.class, Pattern.compile("_name", Pattern.CASE_INSENSITIVE),
                new DataProvider.IDataParser<String, ItemStack>() {
                    public IDataProvider<String> parse(EventInfo info, IDataProvider<ItemStack> itemDP, Matcher m, StringMatcher sm) {
                        return new SettableDataProvider<String, ItemStack>(ItemStack.class, itemDP) {
                            public String get(ItemStack item, EventData data) {
                                NBTTagCompound display = ItemString.getDisplay(item);
                                if (display == null) return null;

                                return display.getString("Name");
                            }

                            public void set(ItemStack item, EventData data, String value) {
                                NBTTagCompound display = ItemString.getDisplay(item);
                                if (display == null) return;

                                display.setString("Name", value);
                            }

                            public boolean isSettable() {
                                return true;
                            }

                            public Class<String> provides() {
                                return String.class;
                            }

                            public String toString() {
                                return startDP.toString() + "_name";
                            }
                        };
                    }
                });


        SettableDataProvider.register(String.class, ItemStack.class, Pattern.compile("_lore_", Pattern.CASE_INSENSITIVE),
                new DataProvider.IDataParser<String, ItemStack>() {
                    public IDataProvider<String> parse(EventInfo info, IDataProvider<ItemStack> itemDP, Matcher m, StringMatcher sm) {
                        final IDataProvider<Integer> lineDP = IntegerExp.parse(sm, info);
                        if (lineDP == null) return null;

                        sm.accept();
                        return new SettableDataProvider<String, ItemStack>(ItemStack.class, itemDP) {
                            public String get(ItemStack item, EventData data) throws BailException {
                                NBTTagCompound display = ItemString.getDisplay(item);
                                if (display == null) return null;

                                NBTTagList lore = display.getList("Lore");
                                if (lore == null) return null;

                                Integer line = lineDP.get(data);
                                if (line == null) return null;

                                if (lore.size() <= line) return null;

                                return lore.get(line).toString();
                            }

                            public void set(ItemStack item, EventData data, String value) throws BailException {
                                NBTTagCompound display = ItemString.getDisplay(item);
                                if (display == null) return;

                                NBTTagList lore = display.getList("Lore");
                                if (lore == null)
                                {
                                    lore = new NBTTagList();
                                    display.set("Lore", lore);
                                }

                                Integer line = lineDP.get(data);
                                if (line == null) return;

                                if (lore.size() <= line) {
                                    for (int i = lore.size(); i < line; i++)
                                        lore.add(new NBTTagString(""));
                                    lore.add(new NBTTagString(value));

                                    System.out.println(display.get("Lore"));
                                    return;
                                }

                                NBTTagList newLore = new NBTTagList();

                                for (int i = 0; i < lore.size(); i++) {
                                    if (i == line)
                                        newLore.add(new NBTTagString(value));
                                    else
                                        newLore.add(lore.get(i));
                                }

                                display.set("Lore", newLore);


                                System.out.println(display.get("Lore"));
                            }

                            public boolean isSettable() {
                                return true;
                            }

                            public Class<String> provides() {
                                return String.class;
                            }

                            public String toString() {
                                return startDP.toString() + "_lore_" + lineDP;
                            }
                        };
                    }
                });
    }
}
