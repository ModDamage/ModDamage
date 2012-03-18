package com.ModDamage.Variables.Ints;

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
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class PotionEffectInt extends IntegerExp
{	
	public enum PotionEffectProperty
	{
		DURATION {
			@Override int get(LivingEntity player, PotionEffect effect)
			{
				return effect.getDuration();
			}
		},
		AMPLIFIER {
			@Override int get(LivingEntity player, PotionEffect effect)
			{
				return effect.getAmplifier();
			}
		};
		
		abstract int get(LivingEntity player, PotionEffect effect);
		
		public static final String regexString = Utils.joinBy("|", PotionEffectProperty.values());
	}
	
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("(\\w+?)_potioneffect_(\\w+?)_("+ PotionEffectProperty.regexString
						+")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, matcher.group(1).toLowerCase());
						if (entityRef == null) return null;
						
						PotionEffectType type = PotionEffectType.getByName(matcher.group(2).toUpperCase());
						if (type == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown potion effect type '"+matcher.group(2)+"'");
							return null;
						}
						
						return sm.acceptIf(new PotionEffectInt(
								entityRef,
								type,
								PotionEffectProperty.valueOf(matcher.group(3).toUpperCase())));
					}
				});
	}
	
	private final DataRef<LivingEntity> entityRef;
	private final PotionEffectType type;
	private final PotionEffectProperty property;
	
	PotionEffectInt(DataRef<LivingEntity> entityRef, PotionEffectType type, PotionEffectProperty property)
	{
		this.entityRef = entityRef;
		this.type = type;
		this.property = property;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		LivingEntity entity = entityRef.get(data);
		if(entity == null) return 0;
		
		for (PotionEffect effect : entity.getActivePotionEffects())
		{
			if (effect.getType() == type)
				return property.get(entity, effect);
		}
		
		return 0;
	}
	@Override
	public String toString()
	{
		return entityRef + "_potioneffect_" + type.getName() + "_" + property.name();
	}
}