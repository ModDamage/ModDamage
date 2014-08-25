package com.moddamage.properties;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.backend.BailException;
import com.moddamage.backend.InventorySlot;
import com.moddamage.backend.ItemHolder;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.FunctionParser;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.SettableDataProvider;
import com.moddamage.parsing.property.Properties;
import com.moddamage.parsing.property.SettableProperty;

public class InventoryProps
{
    public static void register() {
        Properties.register("invname", Inventory.class, "getName");
        Properties.register("title", Inventory.class, "getTitle");
        Properties.register("type", Inventory.class, "getType");
        Properties.register("size", Inventory.class, "getSize");
        Properties.register("maxstacksize", Inventory.class, "getMaxStackSize", "setMaxStackSize");

        Properties.register("firstempty", Inventory.class, "firstEmpty");
        
        Properties.register("held_slot", PlayerInventory.class, "getHeldItemSlot", "setHeldItemSlot");

        DataProvider.register(ItemHolder.class, Inventory.class, Pattern.compile("_item", Pattern.CASE_INSENSITIVE),
                new FunctionParser<ItemHolder, Inventory>(Integer.class) {
                    @SuppressWarnings("rawtypes")
					protected IDataProvider<ItemHolder> makeProvider(EventInfo info, IDataProvider<Inventory> startDP, final IDataProvider[] arguments) {
                        return new SettableDataProvider<ItemHolder, Inventory>(Inventory.class, startDP) {
                            public ItemHolder get(Inventory inv, EventData data) throws BailException {
                                Integer slot = (Integer) arguments[0].get(data);
                                if (slot == null) return null;
                                return new InventorySlot(inv, slot);
                            }

							public void set(Inventory inv, EventData data, ItemHolder value) throws BailException {
                                Integer slot = (Integer) arguments[0].get(data);
                                if (slot == null) return;
                                
                                inv.setItem(slot, value == null? null : value.getItem());
							}

							public boolean isSettable() {
								return true;
							}

                            public Class<? extends ItemHolder> provides() {
                                return InventorySlot.class;
                            }
                            
                            public String toString() {
                            	return startDP + "_item(" + arguments[0] + ")";
                            }
                        };
                    }
                });
        
        Properties.register("type", InventoryView.class, "getType");
        Properties.register("size", InventoryView.class, "countSlots");

        DataProvider.register(ItemHolder.class, InventoryView.class, Pattern.compile("_slot", Pattern.CASE_INSENSITIVE),
                new FunctionParser<ItemHolder, InventoryView>(Integer.class) {
                    @SuppressWarnings("rawtypes")
					protected IDataProvider<ItemHolder> makeProvider(EventInfo info, IDataProvider<InventoryView> startDP, final IDataProvider[] arguments) {
                        return new SettableDataProvider<ItemHolder, InventoryView>(InventoryView.class, startDP) {
                            public ItemHolder get(final InventoryView view, EventData data) throws BailException {
                                final Integer slot = (Integer) arguments[0].get(data);
                                if (slot == null) return null;
                                return new ItemHolder(view.getItem(slot)) {
        							public void save() {
        								view.setItem(slot, getItem());
        							}
        						};
                            }
                            
							public void set(InventoryView view, EventData data, ItemHolder value) throws BailException {
                                final Integer slot = (Integer) arguments[0].get(data);
                                if (slot == null) return;
                                
                                view.setItem(slot, value == null? null : value.getItem());
							}

							public boolean isSettable() {
								return false;
							}

                            public Class<? extends ItemHolder> provides() {
                                return InventorySlot.class;
                            }
                            
                            public String toString() {
                            	return startDP + "_slot(" + arguments[0] + ")";
                            }
                        };
                    }
                });
        
        Properties.register(new SettableProperty<ItemHolder, InventoryView>("cursor", ItemHolder.class, InventoryView.class) {
					public ItemHolder get(final InventoryView view, EventData data) {
						return new ItemHolder(view.getCursor()) {
							public void save() {
								view.setCursor(getItem());
							}
						};
					}
					
					public void set(InventoryView view, EventData data, ItemHolder value) {
						view.setCursor(value == null? null : value.getItem());
					}
                });
        

        Properties.register("top", InventoryView.class, "getTopInventory");
        Properties.register("bottom", InventoryView.class, "getBottomInventory");

        DataProvider.register(Integer.class, InventoryView.class, Pattern.compile("_("+ Utils.joinBy("|", InventoryView.Property.values()) + ")"),
                new IDataParser<Integer, InventoryView>() {
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
                            
                            public String toString() {
                            	return startDP + "_" + property.name().toLowerCase();
                            }
                        };
                    }
                });
    }
}
