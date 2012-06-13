package com.ModDamage;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnchantmentsRef;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.DamageType;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Matchables.HealType;
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
			DataRef<IntRef> damageRef = new DataRef<IntRef>(IntRef.class, IntRef.class, "damage", 8);
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityDamage(EntityDamageEvent event)
			{
				if(!ModDamage.isEnabled || event.isCancelled()) return;
				
				if(!event.isCancelled() && (event.getEntity() instanceof LivingEntity)) 
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
		}),
	
	PrepareEnchant(
		new SimpleEventInfo(
				Player.class, EntityType.class, "player",
				World.class,					"world",
				ItemStack.class, 				"item",
				IntRef.class,					"bonus",
				IntRef.class,					"level_1",
				IntRef.class,					"level_2",
				IntRef.class,					"level_3"),
				
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onPrepareItemEnchant(PrepareItemEnchantEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				Player player = event.getEnchanter();
				IntRef bonus = new IntRef(event.getEnchantmentBonus());
				int[] levels = event.getExpLevelCostsOffered();
				IntRef level_1 = new IntRef(levels[0]);
				IntRef level_2 = new IntRef(levels[1]);
				IntRef level_3 = new IntRef(levels[2]);
				EventData data = PrepareEnchant.eventInfo.makeData(
						player, EntityType.get(player),
						player.getWorld(),
						event.getItem(),
						bonus,
						level_1, level_2, level_3
						);
				
				PrepareEnchant.runRoutines(data);
				
				levels[0] = level_1.value;
				levels[1] = level_2.value;
				levels[2] = level_3.value;
			}
		}),
	
	Enchant(
			new SimpleEventInfo(
					Player.class, EntityType.class, "player",
					World.class,					"world",
					ItemStack.class, 				"item",
					EnchantmentsRef.class,			"-enchantments",
					IntRef.class,					"level"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onEnchantItem(EnchantItemEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getEnchanter();
					IntRef level = new IntRef(event.getExpLevelCost());
					EventData data = Enchant.eventInfo.makeData(
							player, EntityType.get(player),
							player.getWorld(),
							event.getItem(),
							new EnchantmentsRef(event.getEnchantsToAdd()),
							level
							);
					
					Enchant.runRoutines(data);
					
					event.setExpLevelCost(level.value);
				}
			}),
	
	Interact(
			new SimpleEventInfo(
					Player.class, EntityType.class, "player",
					World.class,					"world",
					ItemStack.class, 				"item",
					Boolean.class,					"interact_left",
					Boolean.class,					"interact_right",
					Boolean.class,					"interact_block",
					Boolean.class,					"interact_air",
					IntRef.class,					"interact_block_type",
					IntRef.class,					"interact_block_data"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onInteract(PlayerInteractEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getPlayer();
					Action action = event.getAction();
					
					Block clickedBlock = event.getClickedBlock();
					IntRef block_type = new IntRef(0);
					IntRef block_data = new IntRef(0);
					if (clickedBlock != null)
					{
						block_type.value = clickedBlock.getTypeId();
						block_data.value = clickedBlock.getData();
					}
					
					EventData data = Interact.eventInfo.makeData(
							player, EntityType.get(player),
							player.getWorld(),
							event.getItem(),
							action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK,
							action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK,
							action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK,
							action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR,
							block_type,
							block_data);
					
					Interact.runRoutines(data);
				}
			}),
			
	InteractEntity(
			new SimpleEventInfo(
					Player.class, EntityType.class, "player",
					World.class,					"world",
					Entity.class, EntityType.class, "target"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onInteractEntity(PlayerInteractEntityEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getPlayer();
					Entity target = event.getRightClicked();
					EventData data = InteractEntity.eventInfo.makeData(
							player, EntityType.get(player),
							player.getWorld(),
							target, EntityType.get(target));
					
					InteractEntity.runRoutines(data);
				}
			}),
			
	ItemHeld(
			new SimpleEventInfo(
					Player.class, EntityType.class, "player",
					World.class,					"world",
					IntRef.class,					"prevslot",
					IntRef.class,					"newslot"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onItemHeld(PlayerItemHeldEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getPlayer();
					EventData data = ItemHeld.eventInfo.makeData(
							player, EntityType.get(player),
							player.getWorld(),
							new IntRef(event.getPreviousSlot()),
							new IntRef(event.getNewSlot()));
					
					ItemHeld.runRoutines(data);
				}
			}),
			
	Join(
			new SimpleEventInfo(
					Player.class, EntityType.class, "player",
					World.class,					"world"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onJoin(PlayerJoinEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getPlayer();
					EventData data = Join.eventInfo.makeData(
							player, EntityType.get(player),
							player.getWorld());
					
					Join.runRoutines(data);
				}
			}),
			
	Quit(
			new SimpleEventInfo(
					Player.class, EntityType.class, "player",
					World.class,					"world"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onQuit(PlayerQuitEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getPlayer();
					EventData data = Quit.eventInfo.makeData(
							player, EntityType.get(player),
							player.getWorld());
					
					Quit.runRoutines(data);
				}
			}),
			
	Target(
			new SimpleEventInfo(
					Entity.class, EntityType.class, "entity",
					World.class,					"world",
					Entity.class, EntityType.class, "target",
					EntityTargetEvent.TargetReason.class, "reason"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onTarget(EntityTargetEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Entity entity = event.getEntity();
					Entity target = event.getTarget();
					EventData data = Target.eventInfo.makeData(
							entity, EntityType.get(entity),
							entity.getWorld(),
							target, EntityType.get(target),
							event.getReason());
					
					Target.runRoutines(data);
				}
			}),
			
	Combust(
			new SimpleEventInfo(
					Entity.class, EntityType.class, "entity",
					World.class,					"world",
					IntRef.class,					"duration",
					Entity.class, EntityType.class, "combustor",
					IntRef.class,					"block_type",
					IntRef.class,					"block_data"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onCombust(EntityCombustEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Entity entity = event.getEntity();
					IntRef duration = new IntRef(event.getDuration());
					Entity combustor = null;
					IntRef block_type = new IntRef(0);
					IntRef block_data = new IntRef(0);
					
					if (event instanceof EntityCombustByEntityEvent)
						combustor = ((EntityCombustByEntityEvent)event).getCombuster();
					
					if (event instanceof EntityCombustByBlockEvent)
					{
						block_type.value = ((EntityCombustByBlockEvent)event).getCombuster().getTypeId();
						block_data.value = ((EntityCombustByBlockEvent)event).getCombuster().getData();
					}
					
					EventData data = Combust.eventInfo.makeData(
							entity, EntityType.get(entity),
							entity.getWorld(),
							duration,
							combustor, EntityType.get(combustor),
							block_type,
							block_data);
					
					Combust.runRoutines(data);
					
					event.setDuration(duration.value);
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
		try
		{
			if (routines != null)
				routines.run(data);
		}
		catch (BailException e)
		{
			ModDamage.reportBailException(e);
		}
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
			else
			{
				eventType.specificLoadState = LoadState.NOT_LOADED;
				eventType.routines = new Routines();
			}
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
					
				default: assert(false);
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
				
			default: assert(false);
		}
	}
	
	static EventData getDamageEventData(EntityDamageEvent event)
	{
		if (event == null) return null;
		
		DamageType damageElement = DamageType.get(event.getCause());
		
		Entity attacker = null;
		EntityType attackerType = EntityType.NONE;
		Projectile projectile = null;
		EntityType projectileType = EntityType.NONE;
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