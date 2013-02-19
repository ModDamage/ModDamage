package com.ModDamage.Properties;

import org.bukkit.block.Block;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.Property.Properties;
import com.ModDamage.Parsing.Property.Property;

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
        
        Properties.register("airticks", LivingEntity.class, "getRemainingAir", "setRemainingAir");
        Properties.register("falldistance", LivingEntity.class, "getFallDistance");
        Properties.register("fireticks", LivingEntity.class, "getFireTicks", "setFireTicks");
        Properties.register("health", LivingEntity.class, "getHealth", "setHealth");
        Properties.register("id", Entity.class, "getEntityId");
        Properties.register("lastdamage", LivingEntity.class, "getLastDamage", "setLastDamage");
        Properties.register("maxhealth", LivingEntity.class, "getMaxHealth");
        Properties.register("nodamageticks", LivingEntity.class, "getNoDamageTicks");
        Properties.register("maxnodamageticks", LivingEntity.class, "getMaximumNoDamageTicks");
        Properties.register("size", Slime.class, "getSize", "setSize");

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
	}
}