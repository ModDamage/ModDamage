package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.IntegerExp;

public class PotionEffectInt extends IntegerExp<LivingEntity>
{	
	public enum PotionEffectProperty
	{
		DURATION {
			@Override int get(PotionEffect effect)
			{
				return effect.getDuration();
			}
		},
		AMPLIFIER {
			@Override int get(PotionEffect effect)
			{
				return effect.getAmplifier();
			}
		};
		
		abstract int get(PotionEffect effect);
		
		public static final String regexString = Utils.joinBy("|", PotionEffectProperty.values());
	}
	
	public static void register()
	{
		DataProvider.register(Integer.class, LivingEntity.class,
				Pattern.compile("_potioneffect_(\\w+?)_("+PotionEffectProperty.regexString+")", Pattern.CASE_INSENSITIVE),
				new IDataParser<Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<?> livingDP, Matcher m, StringMatcher sm)
					{
						PotionEffectType type = PotionEffectType.getByName(m.group(1).toUpperCase());
						if (type == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown potion effect type '"+m.group(1)+"'");
							return null;
						}
						
						return sm.acceptIf(new PotionEffectInt(
								livingDP,
								type,
								PotionEffectProperty.valueOf(m.group(2).toUpperCase())));
					}
				});
	}
	
	private final PotionEffectType type;
	private final PotionEffectProperty property;
	
	PotionEffectInt(IDataProvider<?> livingDP, PotionEffectType type, PotionEffectProperty property)
	{
		super(LivingEntity.class, livingDP);
		this.type = type;
		this.property = property;
	}
	
	@Override
	public Integer myGet(LivingEntity living, EventData data) throws BailException
	{
		for (PotionEffect effect : living.getActivePotionEffects())
		{
			if (effect.getType() == type)
				return property.get(effect);
		}
		
		return 0;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_potioneffect_" + type.getName() + "_" + property.name();
	}
}