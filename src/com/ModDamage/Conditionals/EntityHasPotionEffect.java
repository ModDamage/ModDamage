package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EntityHasPotionEffect extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.haspotioneffect\\.([\\w.]+)", Pattern.CASE_INSENSITIVE);
	
	private final DataRef<LivingEntity> entityRef;
	protected final PotionEffectType[] effectTypes;
	
	public EntityHasPotionEffect(String configString, DataRef<LivingEntity> entityRef, PotionEffectType[] effectTypes)
	{
		super(configString);
		this.entityRef = entityRef;
		this.effectTypes = effectTypes;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		LivingEntity entity = entityRef.get(data);
		if (entity == null) return false;
		for(PotionEffectType effectType : effectTypes)
			if(entity.hasPotionEffect(effectType))
				return true;
		return false;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityHasPotionEffect getNew(Matcher matcher, EventInfo info)
		{
			String[] effectTypeStrs = matcher.group(2).split(",");
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
			
			DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, matcher.group(1).toLowerCase());
			if(entityRef != null && effectTypes.length > 0)
				return new EntityHasPotionEffect(matcher.group(), entityRef, effectTypes);
			return null;
		}
	}
}
