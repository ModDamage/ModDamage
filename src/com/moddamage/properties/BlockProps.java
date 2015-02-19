package com.moddamage.properties;

import java.util.regex.Pattern;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;

import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.DataProvider.IDataTransformer;
import com.moddamage.parsing.FunctionParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.ISettableDataProvider;
import com.moddamage.parsing.SettableDataProvider;
import com.moddamage.parsing.property.Properties;

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
					protected ISettableDataProvider<String> makeProvider(EventInfo info, IDataProvider<Sign> startDP, final IDataProvider[] arguments) {
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

							public boolean isSettable() { return true; }
							
							public String toString() {
								return startDP + "_line(" + arguments[0] + ")";
							}
                        };
                    }
                });
        

		DataProvider.registerTransformer(InventoryHolder.class, Block.class, new IDataTransformer<InventoryHolder, Block>() {
			public IDataProvider<InventoryHolder> transform(EventInfo info, final IDataProvider<Block> blockDP)
			{
				return new IDataProvider<InventoryHolder>() {
						public InventoryHolder get(EventData data) throws BailException {
							Block block = blockDP.get(data);
							if (block == null) return null;
							
							BlockState state = block.getState();
							if (state == null) return null;
							if (state instanceof InventoryHolder)
								return (InventoryHolder) state;
							return null;
						}
						
						public Class<? extends InventoryHolder> provides() {
							return InventoryHolder.class;
						}
						
						public String toString() {
							return blockDP.toString();
						}
					};
			}
		});
		Properties.register("inventory", InventoryHolder.class, "getInventory");

		Properties.register("name", CommandBlock.class, "getName", "setName");
		Properties.register("command", CommandBlock.class, "getCommand", "setCommand");
	}
}