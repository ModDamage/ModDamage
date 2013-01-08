package com.ModDamage.Properties;

import java.util.regex.Pattern;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.FunctionParser;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.Parsing.SettableDataProvider;
import com.ModDamage.Parsing.Property.Properties;

public class BlockProps
{
	public static void register()
	{
		Properties.register("power", Block.class, "getBlockPower");
		Properties.register("light", Block.class, "getLightLevel");
		Properties.register("blocklight", Block.class, "getLightFromBlocks");
		Properties.register("skylight", Block.class, "getLightFromSky");
        Properties.register("type",	Block.class, "getType");
		Properties.register("typeid", Block.class, "getTypeId", "setTypeId");
		Properties.register("data", Block.class, "getData", "setData");
		
		DataProvider.registerTransformer(Block.class, "getState");
		DataProvider.registerTransformer(Sign.class, BlockState.class);
		
        DataProvider.register(String.class, Sign.class, Pattern.compile("_line", Pattern.CASE_INSENSITIVE),
                new FunctionParser<String, Sign>(Integer.class) {
                    @SuppressWarnings("rawtypes")
					protected ISettableDataProvider<String> makeProvider(IDataProvider<Sign> startDP, final IDataProvider[] arguments) {
                        return new SettableDataProvider<String, Sign>(Sign.class, startDP) {
                            public String get(Sign sign, EventData data) throws BailException {
                                Integer line = (Integer) arguments[0].get(data);
                                if (line == null || line < 1 || line > 4) return null;
                                
                                return sign.getLine(line - 1);
                            }
                            
                            public void set(Sign sign, EventData data, String value) throws BailException {
                                Integer line = (Integer) arguments[0].get(data);
                                if (line == null || line < 1 || line > 4) return;
                                
                                sign.setLine(line - 1, value);
                                sign.update();
                            }

                            public Class<? extends String> provides() {
                                return String.class;
                            }

							@Override
							public boolean isSettable()
							{
								return true;
							}
							
							public String toString() {
								return startDP + "_line(" + arguments[0] + ")";
							}
                        };
                    }
                });
        

		DataProvider.registerTransformer(InventoryHolder.class, BlockState.class);
		Properties.register("inventory", InventoryHolder.class, "getInventory");
	}
}