package com.ModDamage.Properties;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.InventorySlot;
import com.ModDamage.Backend.ItemHolder;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.FunctionParser;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.Properties;
import com.ModDamage.EventInfo.SettableDataProvider;

public class InventoryProps
{
    public static void register() {
        Properties.register("name", Inventory.class, "getName");
        Properties.register("type", Inventory.class, "getType");
        Properties.register("size", Inventory.class, "getSize");
        Properties.register("maxstacksize", Inventory.class, "getMaxStackSize", "setMaxStackSize");

        Properties.register("firstempty", Inventory.class, "firstEmpty");

        DataProvider.register(ItemHolder.class, Inventory.class, Pattern.compile("_item", Pattern.CASE_INSENSITIVE),
                new FunctionParser<ItemHolder, Inventory>(Integer.class) {
                    @SuppressWarnings("rawtypes")
					protected IDataProvider<ItemHolder> makeProvider(IDataProvider<Inventory> startDP, final IDataProvider[] arguments) {
                        return new DataProvider<ItemHolder, Inventory>(Inventory.class, startDP) {
                            public ItemHolder get(Inventory inv, EventData data) throws BailException {
                                Integer slot = (Integer) arguments[0].get(data);
                                if (slot == null) return null;
                                return new InventorySlot(inv, slot);
                            }

                            public Class<? extends ItemHolder> provides() {
                                return InventorySlot.class;
                            }
                        };
                    }
                });

        DataProvider.register(Integer.class, InventoryView.class, Pattern.compile("_("+ Utils.joinBy("|", InventoryView.Property.values()) + ")"),
                new DataProvider.IDataParser<Integer, InventoryView>() {
                    public IDataProvider<Integer> parse(EventInfo info, IDataProvider<InventoryView> startDP, Matcher m, StringMatcher sm) {
                        final InventoryView.Property property = InventoryView.Property.valueOf(m.group(1).toUpperCase());
                        return new SettableDataProvider<Integer, InventoryView>(InventoryView.class, startDP) {
                            public Integer get(InventoryView view, EventData data) {
                                return -1; // can't get this value :(
                            }
                            public void set(InventoryView view, EventData data, Integer value) throws BailException {
                                view.setProperty(property, value);
                            }


                            public Class<? extends Integer> provides() {
                                return Integer.class;
                            }

                            public boolean isSettable() {
                                return true;
                            }
                        };
                    }
                });
    }
}
