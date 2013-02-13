package com.ModDamage.Properties;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.ModDamage.EventInfo.EventData;
import com.ModDamage.Parsing.Property.Properties;
import com.ModDamage.Parsing.Property.SettableProperty;

public class CreatureProps
{
	public static void register()
	{
        Properties.register(new SettableProperty<Boolean, Creature>("angry", Boolean.class, Creature.class) {
                public Boolean get(Creature entity, EventData data) {
                    if (entity instanceof Wolf)
                    	return ((Wolf) entity).isAngry();
                    else if (entity instanceof PigZombie)
                    	return ((PigZombie) entity).isAngry();
                    return null;
                }
                
				public void set(Creature entity, EventData data, Boolean value) {
                    if (entity instanceof Wolf)
                    	((Wolf) entity).setAngry(value);
                    else if (entity instanceof PigZombie)
                    	((PigZombie) entity).setAngry(value);
				}
            });
        
        Properties.register("age", Ageable.class, "getAge", "setAge");
        Properties.register("agelock", Ageable.class, "getAgeLock", "setAgeLock");
        Properties.register("canbreed", Ageable.class, "canBreed", "setBreed");

        Properties.register(new SettableProperty<Boolean, Creature>("isbaby", Boolean.class, Creature.class) {
                public Boolean get(Creature entity, EventData data) {
                	if (entity instanceof Ageable)
                		return !((Ageable) entity).isAdult();
                	else if (entity instanceof Zombie)
                		return !((Zombie) entity).isBaby();
                	return null;
                }
                
				public void set(Creature entity, EventData data, Boolean value) {
                	if (entity instanceof Ageable) {
                		if (value)
							((Ageable) entity).setBaby();
						else
							((Ageable) entity).setAdult();
                	}
                	else if (entity instanceof Zombie)
                		((Zombie) entity).setBaby(value);
				}
            });
        

        Properties.register("isvillager", Zombie.class, "isVillager", "setVillager");
        Properties.register("powered", Creeper.class, "isPowered", "setPowered");
        
        Properties.register("profession", Villager.class, "getProfession", "setProfession");
        
        Properties.register("sitting", Wolf.class, "isSitting", "setSitting");
        Properties.register("collarcolor", Wolf.class, "getCollarColor", "setCollarColor");
        
        Properties.register("anger", PigZombie.class, "getAnger", "setAnger");
        
        Properties.register("playercreated", IronGolem.class, "isPlayerCreated", "setPlayerCreated");
	}

}
