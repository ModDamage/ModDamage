package com.ModDamage;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.DamageType;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.Backend.HealType;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;

enum ModDamageEventHandler
{
	Damage(
		new SimpleEventInfo(
			Entity.class, EntityType.class, 		"attacker", "-target-other",
			Projectile.class, EntityType.class, 	"projectile",
			Entity.class, EntityType.class, 		"target", "-attacker-other",
			World.class,							"world",
			DamageType.class, 						"damage", // e.g. damage.type.FIRE
			IntRef.class, 							"damage", "-default"),
			
		new Listener(){
			DataRef<IntRef> damageRef = new DataRef<IntRef>(IntRef.class, "damage", 8);
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityDamage(EntityDamageEvent event)
			{
				if(!ModDamage.isEnabled || event.isCancelled()) return;
				
				if(!event.isCancelled() && (event.getEntity() instanceof LivingEntity)) 
				{
					LivingEntity le = (LivingEntity)event.getEntity();
					if(le.getNoDamageTicks() <= le.getMaximumNoDamageTicks()/2)
					{
						EventData data = getDamageEventData(event);
						if(data != null)
						{
							Damage.runRoutines(data);
							event.setDamage(damageRef.get(data).value);
							//event.setCancelled(event.getDamage() <= 0);
						}
						else PluginConfiguration.log.severe("[" + Bukkit.getPluginManager().getPlugin("ModDamage").getDescription().getName() + 
								"] Error! Unhandled damage event. Is Bukkit and ModDamage up-to-date?");
					}
				}
			}
		}),
	
	Death(
		Damage.eventInfo.chain(new SimpleEventInfo(
			IntRef.class, "experience", "-default")),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityDeath(EntityDeathEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				if(disableDeathMessages && event instanceof PlayerDeathEvent)
					((PlayerDeathEvent)event).setDeathMessage(null);
					
				Entity entity = event.getEntity();
				
			    EventData damageData = getDamageEventData(((LivingEntity) entity).getLastDamageCause());
			    
				if(damageData == null) // for instance, /butcher often does this
					damageData = Damage.eventInfo.makeData(
							null, EntityType.UNKNOWN,
							null, null,
							entity, EntityType.get(entity),
							entity.getWorld(),
							DamageType.UNKNOWN,
							new IntRef(0)
							);
				
				IntRef experience = new IntRef(event.getDroppedExp());
				EventData data = Death.eventInfo.makeChainedData(damageData, experience);
				Death.runRoutines(data);
				event.setDroppedExp(experience.value);
			}
		}),
	
	Heal(
		new SimpleEventInfo(
			Entity.class, EntityType.class,	"entity",
			World.class,					"world",
			HealType.class, 				"heal", // e.g. heal.type.EATING
			IntRef.class, 					"heal_amount", "-default"),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityRegainHealth(EntityRegainHealthEvent event)
			{
				if(!ModDamage.isEnabled || event.isCancelled()) return;
				
				Entity entity = event.getEntity();
				IntRef heal_amount = new IntRef(event.getAmount());
				EventData data = Heal.eventInfo.makeData(
						entity, EntityType.get(entity),
						entity.getWorld(),
						HealType.get(event.getRegainReason()),
						heal_amount);
				
				Heal.runRoutines(data);
				
				if (heal_amount.value <= 0)
					event.setCancelled(true);
				else
					event.setAmount(heal_amount.value);
			}
		}),
	
	ProjectileHit(
		new SimpleEventInfo(
			Entity.class, EntityType.class, 	"shooter",
			Projectile.class, EntityType.class, "projectile",
			World.class,						"world"),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onProjectileHit(ProjectileHitEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				Projectile projectile = (Projectile)event.getEntity();
				LivingEntity shooter = projectile.getShooter();
				
				EventData data = ProjectileHit.eventInfo.makeData(
						shooter, (shooter != null)? EntityType.get(shooter)
												  : EntityType.DISPENSER,
						projectile, EntityType.get(projectile),
						projectile.getWorld());
				
				ProjectileHit.runRoutines(data);
			}
		}),
	
	Spawn(
		new SimpleEventInfo(
			Entity.class, EntityType.class, "entity",
			World.class,					"world",
			IntRef.class, 					"health", "-default"),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onPlayerRespawn(PlayerRespawnEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				Player player = event.getPlayer();
				IntRef health = new IntRef(player.getMaxHealth());
				EventData data = Spawn.eventInfo.makeData(
						player, EntityType.PLAYER, // entity
						player.getWorld(),
						health
						);
				
				Spawn.runRoutines(data);
				
				player.setHealth(health.value);
			}
			
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onCreatureSpawn(CreatureSpawnEvent event)
			{
				if(!ModDamage.isEnabled || event.isCancelled()) return;
				
				LivingEntity entity = (LivingEntity)event.getEntity();
				IntRef health = new IntRef(entity.getHealth());
				EventData data = Spawn.eventInfo.makeData(
						entity, EntityType.get(entity),
						entity.getWorld(),
						health);
				
				Spawn.runRoutines(data);
				
				if (health.value > 0)
					entity.setHealth(health.value);
				else
					event.setCancelled(true);
			}
		}),
			
	Tame(
		new SimpleEventInfo(
			Entity.class, EntityType.class, "entity",
			Entity.class, EntityType.class, "tamer",
			World.class,					"world"),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityTame(EntityTameEvent event)
			{
				if(!ModDamage.isEnabled || event.isCancelled()) return;
				
				LivingEntity entity = (LivingEntity)event.getEntity();
				LivingEntity owner = (LivingEntity)event.getOwner();
				EventData data = Tame.eventInfo.makeData(
						entity, EntityType.get(entity),
						owner, EntityType.get(owner),
						entity.getWorld());
				
				Tame.runRoutines(data);
			}
		}),
	
	PickupExp(
		new SimpleEventInfo(
				Player.class, EntityType.class, "player",
				World.class,					"world",
				IntRef.class,					"experience"),
				
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onPickupExperience(PlayerExpChangeEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				Player player = event.getPlayer();
				IntRef experience = new IntRef(event.getAmount());
				EventData data = PickupExp.eventInfo.makeData(
						player, EntityType.get(player),
						player.getWorld(),
						experience);
				
				PickupExp.runRoutines(data);
				
				event.setAmount(experience.value);
			}
		});
	
	private final EventInfo eventInfo;
	public final Listener listener;
	
	private ModDamageEventHandler(EventInfo eventInfo, Listener listener)
	{
		this.eventInfo = eventInfo;
		this.listener = listener;
	}

	protected static final String disableDeathMessages_configString = "disable-deathmessages";
	public static boolean disableDeathMessages = false;
	
	public void runRoutines(EventData data)
	{
		if (routines != null)
			routines.run(data);
	}
	protected Routines routines = null;
	protected LoadState specificLoadState = LoadState.NOT_LOADED;
	protected static LoadState state = LoadState.NOT_LOADED;
	
	protected LoadState getState(){ return specificLoadState; }
	
	protected static void reload()
	{
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Loading routines...");
		state = LoadState.NOT_LOADED;
		ModDamage.changeIndentation(true);
		for(ModDamageEventHandler eventType : ModDamageEventHandler.values())
		{
			Object nestedContent = PluginConfiguration.getCaseInsensitiveValue(ModDamage.getPluginConfiguration().getConfigMap(), eventType.name());
			if(nestedContent != null)
			{
				ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
				ModDamage.addToLogRecord(OutputPreset.INFO, eventType.name() + " configuration:");
				Routines routines = RoutineAliaser.parseRoutines(nestedContent, eventType.eventInfo);
				eventType.specificLoadState = routines != null? LoadState.SUCCESS : LoadState.FAILURE;
				if(eventType.specificLoadState.equals(LoadState.SUCCESS))
					eventType.routines = routines;
			}
			else eventType.specificLoadState = LoadState.NOT_LOADED;
			ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
			switch(eventType.specificLoadState)
			{
				case NOT_LOADED:
					ModDamage.addToLogRecord(OutputPreset.WARNING, eventType.name() + " configuration not found.");
					break;
				case FAILURE:
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error in " + eventType.name() + " configuration.");
					break;
				case SUCCESS:
					ModDamage.addToLogRecord(OutputPreset.INFO, "End " + eventType.name() + " configuration.");
					break;
			}
			state = LoadState.combineStates(state, eventType.specificLoadState);
		}
		ModDamage.changeIndentation(false);
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		switch(state)
		{
			case NOT_LOADED:
				ModDamage.addToLogRecord(OutputPreset.WARNING, "No routines loaded! Are any routines defined?");
				break;
			case FAILURE:
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "One or more errors occurred while loading routines.");
				break;
			case SUCCESS:
				ModDamage.addToLogRecord(OutputPreset.INFO, "Routines loaded!");
				break;
		}
	}
	
	static EventData getDamageEventData(EntityDamageEvent event)
	{
		if (event == null) return null;
		
		DamageType damageElement = DamageType.get(event.getCause());
		
		Entity attacker = null;
		EntityType attackerType = null;
		Projectile projectile = null;
		EntityType projectileType = null;
		Entity target = event.getEntity();
		EntityType targetType = EntityType.get(target);
		World world = target.getWorld();
		
		if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
			Entity damager = event_EE.getDamager();
			
			if(damager instanceof Projectile)
			{
				projectile = (Projectile)damager;
				
				attacker = projectile.getShooter();
				
				if(attacker == null)
					attackerType = EntityType.DISPENSER;
			}
			else
			{
				attacker = damager;
			}
		}
		
		if (attacker != null)
			attackerType = EntityType.get(attacker);
		if (projectile != null)
			projectileType = EntityType.get(projectile);
		
	    return Damage.eventInfo.makeData(
	    		attacker, attackerType,
	    		projectile, projectileType,
	    		target, targetType,
	    		world,
	    		damageElement,
	    		new IntRef(event.getDamage()));
	}
};