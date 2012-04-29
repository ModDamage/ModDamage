package com.ModDamage.Variables.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.ModDamage;
import com.ModDamage.Utils;
import com.ModDamage.Alias.TypeNameAliaser;
import com.ModDamage.Backend.ArmorSet;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.StringExp;
import com.ModDamage.Matchables.EntityType;

public class EntityString extends StringExp
{
	private static Pattern entityStringPattern = Pattern.compile("([a-z]+)_("+ Utils.joinBy("|", EntityStringProperty.values()) +")", Pattern.CASE_INSENSITIVE);
	
	public enum EntityStringProperty
	{
		ARMORSET(true)
		{
			@Override protected String getString(Entity entity)
			{
				return new ArmorSet((Player) entity).toString();
			}
		},
		GROUP(true)
		{
			@Override protected String getString(Entity entity)
			{
				return ExternalPluginManager.getPermissionsManager().getGroups((Player) entity).toString();
			}
		},
		NAME(true)
		{
			@Override protected String getString(Entity entity)
			{
				return ((Player)entity).getName();
			}
		},
		REGIONS
		{
			@Override protected String getString(Entity entity)
			{
				return ExternalPluginManager.getRegionsManager().getRegions(entity.getLocation()).toString();
			}
		},
		TAGS
		{
			@Override protected String getString(Entity entity)
			{
				return ModDamage.getTagger().getTags(entity).toString();
			}
		},
		TYPE
		{
			@Override protected String getString(Entity entity)
			{
				return TypeNameAliaser.aliaser.toString(EntityType.get(entity));
			}
		},
		WIELDING(true)
		{
			@Override protected String getString(Entity entity)
			{
				return ((Player) entity).getItemInHand().getType().name();
			}
		},
		ENCHANTMENTS(true)
		{
			@Override protected String getString(Entity entity)
			{
				return HELD_ENCHANTMENTS.getString(entity) + " " + ARMOR_ENCHANTMENTS.getString(entity);
			}
		},
		HELD_ENCHANTMENTS(true)
		{
			@Override protected String getString(Entity entity)
			{
				return ((Player)entity).getItemInHand().getEnchantments().toString();
			}
		},
		ARMOR_ENCHANTMENTS(true)
		{
			@Override protected String getString(Entity entity)
			{
				return HELMET_ENCHANTMENTS.getString(entity) + " " + 
					   CHESTPLATE_ENCHANTMENTS.getString(entity) + " " + 
					   LEGGINGS_ENCHANTMENTS.getString(entity) + " " + 
					   BOOTS_ENCHANTMENTS.getString(entity);
			}
		},
		HELMET_ENCHANTMENTS(true)
		{
			@Override protected String getString(Entity entity)
			{
				return ((Player)entity).getInventory().getHelmet().getEnchantments().toString();
			}
		},
		CHESTPLATE_ENCHANTMENTS(true)
		{
			@Override protected String getString(Entity entity)
			{
				return ((Player)entity).getInventory().getChestplate().getEnchantments().toString();
			}
		},
		LEGGINGS_ENCHANTMENTS(true)
		{
			@Override protected String getString(Entity entity)
			{
				return ((Player)entity).getInventory().getLeggings().getEnchantments().toString();
			}
		},
		BOOTS_ENCHANTMENTS(true)
		{
			@Override protected String getString(Entity entity)
			{
				return ((Player)entity).getInventory().getBoots().getEnchantments().toString();
			}
		};
		
		
		boolean requiresPlayer = false;
		private EntityStringProperty(){}
		private EntityStringProperty(boolean requiresPlayer){ this.requiresPlayer = true; }
		
		abstract protected String getString(Entity entity);
	}
	

	private final DataRef<Entity> entityRef;
	private final EntityStringProperty propertyMatch;
	
	public EntityString(DataRef<Entity> entityRef, EntityStringProperty propertyMatch)
	{
		this.entityRef = entityRef;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public String getString(EventData data)
	{
		Entity entity = entityRef.get(data);
		if(entity != null && (!propertyMatch.requiresPlayer || entity instanceof Player))
			return propertyMatch.getString(entity);
		return null;
	}
	
	public static StringExp getNew(String string, EventInfo info)
	{
		Matcher matcher = entityStringPattern.matcher(string);
		if (matcher.matches())
		{
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			if (entityRef == null) return null;
			
			return new EntityString(entityRef, EntityStringProperty.valueOf(matcher.group(2).toUpperCase()));
		}
		
		return null;
	}

	@Override
	public String toString()
	{
		return entityRef + "_" + propertyMatch.name().toLowerCase();
	}
}
