package com.ModDamage.Backend.Matching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.ModDamage.ExternalPluginManager;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Utils;
import com.ModDamage.Backend.ArmorSet;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Aliasing.TypeNameAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicEntityString extends DynamicString
{
	private static Pattern entityStringPattern = Pattern.compile("([a-z]+)_("+ Utils.joinBy("|", EntityStringPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE);
	
	public enum EntityStringPropertyMatch
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
				return TypeNameAliaser.getStaticInstance().toString(ModDamageElement.getElementFor(entity));
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
		private EntityStringPropertyMatch(){}
		private EntityStringPropertyMatch(boolean requiresPlayer){ this.requiresPlayer = true; }
		
		abstract protected String getString(Entity entity);
	}
	

	final DataRef<Entity> entityRef;
	final EntityStringPropertyMatch propertyMatch;
	
	public DynamicEntityString(DataRef<Entity> entityRef, EntityStringPropertyMatch propertyMatch)
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
	
	public static DynamicString getNew(String string, EventInfo info)
	{
		Matcher matcher = entityStringPattern.matcher(string);
		if (matcher.matches())
		{
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			if (entityRef != null)
				return new DynamicEntityString(entityRef, EntityStringPropertyMatch.valueOf(matcher.group(2).toUpperCase()));
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown entity reference: "+matcher.group(1));
			return null;
		}
		
		return null;
	}

	@Override
	public String toString()
	{
		return /*FIXME entityIndex.name().toLowerCase() +*/ "_" + propertyMatch.name().toLowerCase();
	}
}
