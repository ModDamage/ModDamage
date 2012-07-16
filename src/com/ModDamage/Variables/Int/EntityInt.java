package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;
import com.ModDamage.Matchables.EntityType;

public class EntityInt extends SettableIntegerExp<Entity>
{
	public enum EntityProperty
	{
		AIRTICKS(true, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getRemainingAir();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setRemainingAir(value);
			}
		},
		FALLDISTANCE(false, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return (int) ((LivingEntity)entity).getFallDistance();
			}
		},
		FIRETICKS(true, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getFireTicks();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setFireTicks(value);
			}
		},
		HEALTH(true, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getHealth();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setHealth(value);
			}
		},
		ID
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getEntityId();
			}
		},
		LASTDAMAGE(true, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getLastDamage();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setLastDamage(value);
			}
		},
		LIGHT
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getLocation().getBlock().getLightLevel();
			}
		},
		MAXHEALTH(false, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getMaxHealth();
			}
		},
		NODAMAGETICKS(false, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getNoDamageTicks();
			}
		},
		MAXNODAMAGETICKS(false, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getMaximumNoDamageTicks();
			}
		},
		SIZE(true, EntityType.SLIME)
		{
			@Override public int getValue(Entity entity)
			{
				return (entity instanceof Slime?((Slime)entity).getSize():0);
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				if(entity instanceof Slime) ((Slime)entity).setSize(value);
			}
		},
		X
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockX();
			}
		},
		Y
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockY();
			}
		},
		Z
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockZ();
			}
		};
		
		public boolean settable = false;
		private EntityType requiredElement = EntityType.ENTITY;
		private EntityProperty(){}
		private EntityProperty(boolean settable)
		{
			this.settable = settable;
		}
		private EntityProperty(boolean settable, EntityType requiredElement)
		{
			this.settable = settable;
			this.requiredElement = requiredElement;
		}
		
		abstract public int getValue(Entity entity);
		public void setValue(Entity entity, int value){}
		
		@Override
		public String toString()
		{
			return name().toLowerCase();
		}
	}
	
	private final EntityProperty propertyMatch;
	
	public static void register()
	{
		DataProvider.register(Integer.class, Entity.class, Pattern.compile("_("+ Utils.joinBy("|", EntityProperty.values()) +")", Pattern.CASE_INSENSITIVE), new IDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, IDataProvider<?> entityDP, Matcher m, StringMatcher sm)
				{
					return sm.acceptIf(new EntityInt(
							entityDP,
							EntityProperty.valueOf(m.group(1).toUpperCase())));
				}
			});
	}
	
	EntityInt(IDataProvider<?> entityDP, EntityProperty propertyMatch)
	{
		super(Entity.class, entityDP);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer myGet(Entity entity, EventData data) throws BailException
	{
		EntityType element = EntityType.get(entity);
		
		if(element != null && entity != null && element.matches(propertyMatch.requiredElement))
			return propertyMatch.getValue(entity);
		
		return 0; //Shouldn't happen.
	}
	
	@Override
	public void mySet(Entity entity, EventData data, Integer value)
	{
		if(!isSettable()) return;
		
		EntityType element = EntityType.get(entity);
		
		if (element != null && element.matches(propertyMatch.requiredElement))
			propertyMatch.setValue(entity, value);
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