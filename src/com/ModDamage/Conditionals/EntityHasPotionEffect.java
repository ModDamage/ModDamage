package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityHasPotionEffect extends Conditional<LivingEntity>
{
	public static final Pattern pattern = Pattern.compile("\\.haspotioneffect\\.([\\w.]+)", Pattern.CASE_INSENSITIVE);
	
	protected final PotionEffectType[] effectTypes;
	
	public EntityHasPotionEffect(IDataProvider<?> livingDP, PotionEffectType[] effectTypes)
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
	
	public static void register()
	{
		DataProvider.register(Boolean.class, LivingEntity.class, pattern, new IDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<?> livingDP, Matcher m, StringMatcher sm)
				{
					String[] effectTypeStrs = m.group(1).split(",");
					PotionEffectType[] effectTypes = new PotionEffectType[effectTypeStrs.length];
					
					for (int i = 0; i < effectTypes.length; i++)
					{
						effectTypes[i] = PotionEffectType.getByName(effectTypeStrs[i].toUpperCase());
						if (effectTypes[i] == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown potion effect type '"+effectTypeStrs[i]+"'");
							return null;
						}
					}
					if(effectTypes.length == 0) return null;
					
					return new EntityHasPotionEffect(livingDP, effectTypes);
				}
			});
	}
}
