package com.moddamage.conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.moddamage.LogUtil;
import com.moddamage.Utils;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.StringMatcher;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;

public class EntityHasPotionEffect extends Conditional<LivingEntity>
{
	public static final Pattern pattern = Pattern.compile("\\.haspotioneffect\\.([\\w.]+)", Pattern.CASE_INSENSITIVE);
	
	protected final PotionEffectType[] effectTypes;
	
	public EntityHasPotionEffect(IDataProvider<LivingEntity> livingDP, PotionEffectType[] effectTypes)
	{
		super(LivingEntity.class, livingDP);
		this.effectTypes = effectTypes;
	}

	@Override
	public Boolean get(LivingEntity entity, EventData data)
	{
		if (entity == null) return false;
		for(PotionEffectType effectType : effectTypes)
			if(entity.hasPotionEffect(effectType))
				return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".haspotioneffect." + Utils.joinBy(",", effectTypes);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, LivingEntity.class, pattern, new IDataParser<Boolean, LivingEntity>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<LivingEntity> livingDP, Matcher m, StringMatcher sm)
				{
					String[] effectTypeStrs = m.group(1).split(",");
					PotionEffectType[] effectTypes = new PotionEffectType[effectTypeStrs.length];
					
					for (int i = 0; i < effectTypes.length; i++)
					{
						effectTypes[i] = PotionEffectType.getByName(effectTypeStrs[i].toUpperCase());
						if (effectTypes[i] == null)
						{
							LogUtil.error("Unknown potion effect type '"+effectTypeStrs[i]+"'");
							return null;
						}
					}
					if(effectTypes.length == 0) return null;
					
					return new EntityHasPotionEffect(livingDP, effectTypes);
				}
			});
	}
}
