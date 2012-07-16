package com.ModDamage.Conditionals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class LivingEntityStatus extends Conditional<LivingEntity>
{
	public static final Pattern pattern = Pattern.compile("\\.is("+ Utils.joinBy("|", StatusType.values()) +")", Pattern.CASE_INSENSITIVE);
	
	private enum StatusType
	{
		Drowning
		{
			@Override
			public boolean isTrue(LivingEntity entity){ return entity.getRemainingAir() <= 0; }
		};
		
		abstract public boolean isTrue(LivingEntity entity);
	}

	private final StatusType statusType;
	
	protected LivingEntityStatus(IDataProvider<?> entityDP, StatusType statusType)
	{
		super(LivingEntity.class, entityDP);
		this.statusType = statusType;
	}

	@Override
	public Boolean get(LivingEntity entity, EventData data)
	{
		return statusType.isTrue(entity);
	}
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<?> entityDP, Matcher m, StringMatcher sm)
				{
					StatusType statusType = null;
					for(StatusType type : StatusType.values())
						if(m.group(1).equalsIgnoreCase(type.name()))
								statusType = type;
					if(statusType == null) return null;
					
					return new LivingEntityStatus(entityDP, statusType);
				}
			});
	}
}
