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
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;

enum ModDamageEventHandler
{
	Damage(
		new SimpleEventInfo(
			Entity.class, ModDamageElement.class, 		"attacker", "target-other",
			Projectile.class, ModDamageElement.class, 	"projectile",
			Entity.class, ModDamageElement.class, 		"target", "attacker-other",
			World.class,								"world",
			ModDamageElement.class, 					"damage", // e.g. damage.type.FIRE
			Integer.class, 								"damage", "-default"),
			
		new Listener(){
			@SuppressWarnings("unused")
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityDamage(EntityDamageEvent event)
			{
				if (!ModDamage.isEnabled) return;
				if(!event.isCancelled() && (event.getEntity() instanceof LivingEntity)) 
				{
					LivingEntity le = (LivingEntity)event.getEntity();
					if(le.getNoDamageTicks() <= le.getMaximumNoDamageTicks()/2)
					{
						EventData data = getDamageEventData(event);
						if(data != null)
						{
							Damage.runRoutines(data);
							event.setDamage(data.getMy(Integer.class, 8));
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
			Integer.class, "experience", "-default")),
			
		new Listener() {
			@SuppressWarnings("unused")
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityDeath(EntityDeathEvent event)
			{
				if(ModDamage.isEnabled)
				{
					if(disableDeathMessages && event instanceof PlayerDeathEvent)
						((PlayerDeathEvent)event).setDeathMessage(null);
						
					Entity entity = event.getEntity();
					
				    EventData damageData = getDamageEventData(((LivingEntity) entity).getLastDamageCause());
				    
					if(damageData == null) // for instance, /butcher often does this
						damageData = Damage.eventInfo.makeData(
								null, ModDamageElement.UNKNOWN,
								null, null,
								entity, ModDamageElement.getElementFor(entity),
								entity.getWorld(),
								ModDamageElement.UNKNOWN,
								0
								);
					
					EventData data = Death.eventInfo.makeChainedData(damageData, event.getDroppedExp());
					Death.runRoutines(data);
					event.setDroppedExp(data.getMy(Integer.class, 0));
				}
			}
		}),
	
	Heal(
		new SimpleEventInfo(
			Entity.class, ModDamageElement.class, 	"entity",
			World.class,							"world",
			RegainReason.class, 					"heal", // e.g. heal.type.EATING
			Integer.class, 							"heal_amount", "-default"),
			
		new Listener() {
			@SuppressWarnings("unused")
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityRegainHealth(EntityRegainHealthEvent event)
			{
				if(ModDamage.isEnabled && !event.isCancelled())
				{
					Entity entity = event.getEntity();
					EventData data = Heal.eventInfo.makeData(
							entity, ModDamageElement.getElementFor(entity),
							entity.getWorld(),
							event.getRegainReason(),
							event.getAmount());
					
					Heal.runRoutines(data);
					
					int amount = data.getMy(Integer.class, 4);
					if (amount <= 0)
						event.setCancelled(true);
					else
						event.setAmount(amount);
				}
			}
		}),
	
	ProjectileHit(
		new SimpleEventInfo(
			Entity.class, ModDamageElement.class, 		"shooter",
			Projectile.class, ModDamageElement.class, 	"projectile",
			World.class,								"world"),
			
		new Listener() {
			@SuppressWarnings("unused")
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onProjectileHit(ProjectileHitEvent event)
			{
				if(ModDamage.isEnabled)
				{
					Projectile projectile = (Projectile)event.getEntity();
					LivingEntity shooter = projectile.getShooter();
					
					EventData data = ProjectileHit.eventInfo.makeData(
							shooter, (shooter != null)? ModDamageElement.getElementFor(shooter)
													  : ModDamageElement.DISPENSER,
							projectile, ModDamageElement.getElementFor(projectile),
							projectile.getWorld());
					
					ProjectileHit.runRoutines(data);
				}
			}
		}),
	
	Spawn(
		new SimpleEventInfo(
			Entity.class, ModDamageElement.class, 	"entity",
			World.class,							"world",
			Integer.class, 							"health", "-default"),
			
		new Listener() {
			@SuppressWarnings("unused")
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onPlayerRespawn(PlayerRespawnEvent event)
			{
				if(ModDamage.isEnabled)
				{
					Player player = event.getPlayer();
					EventData data = Spawn.eventInfo.makeData(
							player, ModDamageElement.PLAYER, // entity
							player.getWorld(),
							player.getMaxHealth() // health
							);
					
					Spawn.runRoutines(data);
					
					player.setHealth(data.getMy(Integer.class, 2));
				}
			}
			
			@SuppressWarnings("unused")
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onCreatureSpawn(CreatureSpawnEvent event)
			{ 
				if(ModDamage.isEnabled && !event.isCancelled())
				{
					LivingEntity entity = (LivingEntity)event.getEntity();
					EventData data = Spawn.eventInfo.makeData(
							entity, ModDamageElement.getElementFor(entity),
							entity.getWorld(),
							entity.getHealth());
					
					Spawn.runRoutines(data);
					
					int newHealth = data.getMy(Integer.class, 3);
					if (newHealth > 0)
						entity.setHealth(newHealth);
					else
						event.setCancelled(true);
				}
			}
		}),
			
	Tame(
		new SimpleEventInfo(
			Entity.class, ModDamageElement.class, 	"entity",
			Entity.class, ModDamageElement.class, 	"tamer",
			World.class,							"world"),
			
		new Listener() {
			@SuppressWarnings("unused")
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityTame(EntityTameEvent event)
			{
				if(ModDamage.isEnabled)
				{
					LivingEntity entity = (LivingEntity)event.getEntity();
					LivingEntity owner = (LivingEntity)event.getOwner();
					EventData data = Tame.eventInfo.makeData(
							entity, ModDamageElement.getElementFor(entity),
							owner, ModDamageElement.getElementFor(owner),
							entity.getWorld());
					
					Tame.runRoutines(data);
				}
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
		
		ModDamageElement damageElement = ModDamageElement.getElementFor(event.getCause());
		
		Entity attacker = null;
		ModDamageElement attackerElement = null;
		Projectile projectile = null;
		ModDamageElement projectileElement = null;
		Entity target = event.getEntity();
		ModDamageElement targetElement = ModDamageElement.getElementFor(target);
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
					attackerElement = ModDamageElement.DISPENSER;
			}
			else
			{
				attacker = damager;
			}
		}
		
		if (attacker != null)
			attackerElement = ModDamageElement.getElementFor(attacker);
		if (projectile != null)
			projectileElement = ModDamageElement.getElementFor(projectile);
		
	    return Damage.eventInfo.makeData(
	    		attacker, attackerElement,
	    		projectile, projectileElement,
	    		target, targetElement,
	    		world,
	    		damageElement,
	    		event.getDamage());
	}
};