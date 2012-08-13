package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityTargetBlock
{
	public static void register()
	{
		DataProvider.register(Block.class, LivingEntity.class, Pattern.compile("_targetblock", Pattern.CASE_INSENSITIVE),
				new IDataParser<Block, LivingEntity>() {
					public IDataProvider<Block> parse(EventInfo info, Class<?> want, IDataProvider<LivingEntity> entityDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Block, LivingEntity>(LivingEntity.class, entityDP) {
								public Block get(LivingEntity entity, EventData data) { return entity.getTargetBlock(null, 100); }
								public Class<Block> provides() { return Block.class; }
							};
					}
				});
	}
}
