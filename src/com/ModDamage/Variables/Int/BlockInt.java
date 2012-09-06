package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Block;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;

public class BlockInt extends SettableIntegerExp<Block>
{
	public enum BlockProperty
	{
		POWER
		{
			@Override public int getValue(Block block)
			{
				return block.getBlockPower();
			}
		},
		LIGHT
		{
			@Override public int getValue(Block block)
			{
				return block.getLightLevel();
			}
		},
		BLOCKLIGHT
		{
			@Override public int getValue(Block block)
			{
				return block.getLightFromBlocks();
			}
		},
		SKYLIGHT
		{
			@Override public int getValue(Block block)
			{
				return block.getLightFromSky();
			}
		},
		TYPE(true)
		{
			@Override
			public int getValue(Block block)
			{
				return block.getTypeId();
			}
			
			@Override
			public void setValue(Block block, int value)
			{
				block.setTypeId(value);
			}
		},
		DATA(true)
		{
			@Override
			public int getValue(Block block)
			{
				return block.getData();
			}
			
			@Override
			public void setValue(Block block, int value)
			{
				block.setData((byte) value);
			}
		};
		
		public boolean settable = false;
		private BlockProperty(){}
		private BlockProperty(boolean settable)
		{
			this.settable = settable;
		}
		
		abstract public int getValue(Block block);
		public void setValue(Block block, int value){}
		
		@Override
		public String toString()
		{
			return name().toLowerCase();
		}
	}
	
	private final BlockProperty propertyMatch;
	
	public static void register()
	{
		DataProvider.register(Integer.class, Block.class, 
				Pattern.compile("_("+ Utils.joinBy("|", BlockProperty.values()) +")", Pattern.CASE_INSENSITIVE), 
				new IDataParser<Integer, Block>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, IDataProvider<Block> blockDP, Matcher m, StringMatcher sm)
				{
					return sm.acceptIf(new BlockInt(
							blockDP,
							BlockProperty.valueOf(m.group(1).toUpperCase())));
				}
			});
	}
	
	BlockInt(IDataProvider<Block> blockDP, BlockProperty propertyMatch)
	{
		super(Block.class, blockDP);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer myGet(Block block, EventData data) throws BailException
	{
		if(block != null)
			return propertyMatch.getValue(block);
		
		return 0;
	}
	
	@Override
	public void mySet(Block block, EventData data, Integer value)
	{
		if(!isSettable()) return;
		
		if (block != null)
			propertyMatch.setValue(block, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_" + propertyMatch.name().toLowerCase();
	}
}