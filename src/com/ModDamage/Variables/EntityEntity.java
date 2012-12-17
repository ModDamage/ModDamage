package com.ModDamage.Variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.ISettableDataProvider;
import com.ModDamage.EventInfo.SettableDataProvider;

public class EntityEntity extends DataProvider<Entity, Entity>
{
	public static void register()
	{
		DataProvider.register(Entity.class, Entity.class, 
				Pattern.compile("_("+Utils.joinBy("|", EntityType.values()) +")", Pattern.CASE_INSENSITIVE),
				new IDataParser<Entity, Entity>()
				{
					@Override
					public IDataProvider<Entity> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
					{
						return sm.acceptIf(new EntityEntity(
								entityDP, 
								EntityType.valueOf(m.group(1).toUpperCase())));
					}
				});
		SettableDataProvider.register(LivingEntity.class, Creature.class, Pattern.compile("_target", Pattern.CASE_INSENSITIVE),
                new IDataParser<LivingEntity, Creature>() {
                    public ISettableDataProvider<LivingEntity> parse(EventInfo info, final IDataProvider<Creature> creatureDP, Matcher m, StringMatcher sm) {
                        return new SettableDataProvider<LivingEntity, Creature>(Creature.class, creatureDP) {
                            public LivingEntity get(Creature creature, EventData data) {
                                return creature.getTarget();
                            }

                            public void set(Creature creature, EventData data, LivingEntity target) throws BailException {
                                creature.setTarget(target);
                            }

                            public boolean isSettable() {
                                return true;
                            }

                            public Class<LivingEntity> provides() {
                                return LivingEntity.class;
                            }

                            public String toString() {
                                return creatureDP + "_target";
                            }
                        };
                    }
                });
		DataProvider.register(Player.class, LivingEntity.class, Pattern.compile("_killer", Pattern.CASE_INSENSITIVE),
				new IDataParser<Player, LivingEntity>() {
					public IDataProvider<Player> parse(EventInfo info, final IDataProvider<LivingEntity> livingDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Player, LivingEntity>(LivingEntity.class, livingDP) {
								public Player get(LivingEntity living, EventData data) { return living.getKiller(); }
								public Class<Player> provides() { return Player.class; }
								public String toString() { return livingDP + "_killer"; }
							};
					}
				});
		DataProvider.register(Player.class, Entity.class, Pattern.compile("_owner", Pattern.CASE_INSENSITIVE),
				new IDataParser<Player, Entity>() {
					public ISettableDataProvider<Player> parse(EventInfo info, final IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm) {
						return new SettableDataProvider<Player, Entity>(Entity.class, entityDP) {
                            public Player get(Entity entity, EventData data) {
                                if (!(entity instanceof Tameable)) return null;
                                AnimalTamer tamer = ((Tameable)entity).getOwner();
                                if (tamer == null) return null;
                                if (!(tamer instanceof Player))
                                {
                                    ModDamage.addToLogRecord(OutputPreset.WARNING, "Unknown tamer class: "+tamer.getClass().getName());
                                    return null;
                                }
                                return (Player) tamer;
                            }

                            public void set(Entity entity, EventData data, Player owner) throws BailException {
                                if (!(entity instanceof Tameable)) return;
                                ((Tameable)entity).setOwner(owner);
                            }

                            public boolean isSettable() {
                                return true;
                            }

                            public Class<Player> provides() { return Player.class; }
                            public String toString() { return entityDP + "_owner"; }
                        };
					}
				});
	}
	
	enum EntityType {
		PASSENGER {
			public Entity getItem(Entity entity) {
				return entity.getPassenger();
			}
		},
		VEHICLE {
			public Entity getItem(Entity entity) {
				return entity.getVehicle();
			}
		};
		
		public abstract Entity getItem(Entity entity);
	}
	

	private final EntityType entityType;

	public EntityEntity(IDataProvider<Entity> entityDP, EntityType entityType)
	{
		super(Entity.class, entityDP);
		this.entityType = entityType;
	}

	@Override
	public Entity get(Entity entity, EventData data) throws BailException
	{
		return entityType.getItem(entity);
	}

	@Override
	public Class<Entity> provides() { return Entity.class; }
	
	@Override
	public String toString()
	{
		return startDP + "_" + entityType.name().toLowerCase();
	}
}
