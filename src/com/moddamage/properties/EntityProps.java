package com.moddamage.properties;

import org.bukkit.block.Block;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Colorable;
import org.bukkit.projectiles.ProjectileSource;

import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.magic.MagicStuff;
import com.moddamage.matchables.EntityType;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.property.Properties;
import com.moddamage.parsing.property.Property;
import com.moddamage.parsing.property.SettableProperty;

public class EntityProps
{
	public static void register()
	{
        Properties.register("world", Entity.class, "getWorld");
        Properties.register("passenger", Entity.class, "getPassenger", "setPassenger");
        Properties.register("vehicle", Entity.class, "getVehicle");
        Properties.register("owner", Tameable.class, "getOwner", "setOwner");
        Properties.register("killer", LivingEntity.class, "getKiller");
        Properties.register("target", Creature.class, "getTarget", "setTarget");

        Properties.register("customName", LivingEntity.class, "getCustomName", "setCustomName");
        Properties.register("customNameVisible", LivingEntity.class, "isCustomNameVisible", "setCustomNameVisible");
        Properties.register("airticks", LivingEntity.class, "getRemainingAir", "setRemainingAir");
        Properties.register("falldistance", LivingEntity.class, "getFallDistance");
        Properties.register("fireticks", LivingEntity.class, "getFireTicks", "setFireTicks");
        Properties.register("id", Entity.class, "getEntityId");
        Properties.register("lastdamage", LivingEntity.class, "getLastDamage", "setLastDamage");
        Properties.register("maxhealth", Damageable.class, "getMaxHealth", "setMaxHealth");
        Properties.register("nodamageticks", LivingEntity.class, "getNoDamageTicks");
        Properties.register("maxnodamageticks", LivingEntity.class, "getMaximumNoDamageTicks");
        Properties.register("size", Slime.class, "getSize", "setSize");
        Properties.register("velocity", Entity.class, "getVelocity", "setVelocity");

        Properties.register(new SettableProperty<Double, Damageable>("health", Double.class, Damageable.class) {
                public Double get(Damageable entity, EventData data) {
                    return entity.getHealth();
                }

				public void set(Damageable entity, EventData data, Double value) throws BailException
				{
					if (value != null) entity.setHealth(Math.min(value, entity.getMaxHealth()));
				}
            });

        Properties.register(new Property<EntityType, Entity>("type", EntityType.class, Entity.class) {
                public EntityType get(Entity entity, EventData data) {
                    return EntityType.get(entity);
                }
            });

        Properties.register(new Property<Block, LivingEntity>("blocktarget", Block.class, LivingEntity.class) {
            public Block get(LivingEntity entity, EventData data) {
                return entity.getTargetBlock(null, 100);
            }
        });
        
        DataProvider.registerTransformer(Tameable.class, Entity.class);
        DataProvider.registerTransformer(Player.class, AnimalTamer.class);
        
        Properties.register("isTamed", Tameable.class, "isTamed", "setTamed");
        
        
        DataProvider.registerTransformer(Colorable.class, Entity.class);
        Properties.register("color", Colorable.class, "getColor", "setColor");
        
        DataProvider.registerTransformer(InventoryHolder.class, Entity.class);
		DataProvider.registerTransformer(LivingEntity.class, ProjectileSource.class);

        registerRawTypes();
	}
	
	@SuppressWarnings("rawtypes")
	private static void registerRawTypes()
	{
        Properties.register(new Property<Class, Entity>("handleClass", Class.class, Entity.class) {
                public Class<?> get(Entity entity, EventData data) {
                    return MagicStuff.getHandleClass(entity);
                }
            });
	}
}
