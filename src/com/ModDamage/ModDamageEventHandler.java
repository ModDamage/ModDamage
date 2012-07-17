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
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.DamageType;
import com.ModDamage.Matchables.HealType;
import com.ModDamage.Routines.Routines;

enum ModDamageEventHandler
{
	Damage(
		new SimpleEventInfo(
			Entity.class, 		"attacker", "-target-other",
			Projectile.class, 	"projectile",
			Entity.class, 		"target", "-attacker-other",
			World.class,		"world",
			DamageType.class, 	"damage", // e.g. damage.type.FIRE
			Integer.class, 		"damage", "-default"),
			
		new Listener(){
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
						event.setDamage(data.get(Integer.class, data.start + 5));
						if (event.getDamage() <= 0)
							event.setCancelled(true);
					}
					else PluginConfiguration.log.severe("[" + Bukkit.getPluginManager().getPlugin("ModDamage").getDescription().getName() + 
							"] Error! Unhandled damage event. Is Bukkit and ModDamage up-to-date?");
				}
			}
		}),
	
	Death(
		Damage.eventInfo.chain(new SimpleEventInfo(
			Integer.class, "experience", "-default")),
			
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
							null,
							null,
							entity,
							entity.getWorld(),
							DamageType.UNKNOWN,
							0
							);
				
				EventData data = Death.eventInfo.makeChainedData(damageData, event.getDroppedExp());
				Death.runRoutines(data);
				event.setDroppedExp(data.get(Integer.class, data.start + 1));
			}
		}),
	
	Heal(
		new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			HealType.class,	"heal", // e.g. heal.type.EATING
			Integer.class, 	"heal_amount", "-default"),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityRegainHealth(EntityRegainHealthEvent event)
			{
				if(!ModDamage.isEnabled || event.isCancelled()) return;
				
				Entity entity = event.getEntity();
				EventData data = Heal.eventInfo.makeData(
						entity,
						entity.getWorld(),
						HealType.get(event.getRegainReason()),
						event.getAmount());
				
				Heal.runRoutines(data);
				
				int newHealAmount = data.get(Integer.class, data.start + 4);
				
				if (newHealAmount <= 0)
					event.setCancelled(true);
				else
					event.setAmount(newHealAmount);
			}
		}),
	
	ProjectileHit(
		new SimpleEventInfo(
			Entity.class, 		"shooter",
			Projectile.class,	"projectile",
			World.class,		"world"),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onProjectileHit(ProjectileHitEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				Projectile projectile = (Projectile)event.getEntity();
				LivingEntity shooter = projectile.getShooter();
				
				EventData data = ProjectileHit.eventInfo.makeData(
						shooter,
						projectile,
						projectile.getWorld());
				
				ProjectileHit.runRoutines(data);
			}
		}),
	
	Spawn(
		new SimpleEventInfo(
			Entity.class,	"entity",
			World.class,	"world",
			Integer.class,	"health", "-default"),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onPlayerRespawn(PlayerRespawnEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				Player player = event.getPlayer();
				EventData data = Spawn.eventInfo.makeData(
						player, // entity
						player.getWorld(),
						player.getMaxHealth()
						);
				
				Spawn.runRoutines(data);
				
				player.setHealth(data.get(Integer.class, data.start + 3));
			}
			
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onCreatureSpawn(CreatureSpawnEvent event)
			{
				if(!ModDamage.isEnabled || event.isCancelled()) return;
				
				LivingEntity entity = (LivingEntity)event.getEntity();
				EventData data = Spawn.eventInfo.makeData(
						entity,
						entity.getWorld(),
						entity.getHealth());
				
				Spawn.runRoutines(data);
				
				int health = data.get(Integer.class, data.start + 2);
				
				if (health > 0)
					entity.setHealth(health);
				else
					event.setCancelled(true);
			}
		}),
			
	Tame(
		new SimpleEventInfo(
			Entity.class,	"entity",
			Entity.class,	"tamer",
			World.class,	"world"),
			
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onEntityTame(EntityTameEvent event)
			{
				if(!ModDamage.isEnabled || event.isCancelled()) return;
				
				LivingEntity entity = (LivingEntity)event.getEntity();
				LivingEntity owner = (LivingEntity)event.getOwner();
				EventData data = Tame.eventInfo.makeData(
						entity,
						owner,
						entity.getWorld());
				
				Tame.runRoutines(data);
			}
		}),
	
	PickupExp(
		new SimpleEventInfo(
				Player.class,	"player",
				World.class,	"world",
				Integer.class,	"experience"),
				
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onPickupExperience(PlayerExpChangeEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				Player player = event.getPlayer();
				EventData data = PickupExp.eventInfo.makeData(
						player,
						player.getWorld(),
						event.getAmount());
				
				PickupExp.runRoutines(data);
				
				int experience = data.get(Integer.class, data.start + 3);
				
				event.setAmount(experience);
			}
		}),
	
	PrepareEnchant(
		new SimpleEventInfo(
				Player.class,		"player",
				World.class,		"world",
				ItemStack.class, 	"item",
				Integer.class,		"bonus",
				Integer.class,		"level_1",
				Integer.class,		"level_2",
				Integer.class,		"level_3"),
				
		new Listener() {
			@EventHandler(priority=EventPriority.HIGHEST)
			public void onPrepareItemEnchant(PrepareItemEnchantEvent event)
			{
				if(!ModDamage.isEnabled) return;
				
				Player player = event.getEnchanter();
				Integer bonus = event.getEnchantmentBonus();
				int[] levels = event.getExpLevelCostsOffered();
				EventData data = PrepareEnchant.eventInfo.makeData(
						player,
						player.getWorld(),
						event.getItem(),
						bonus,
						levels[0], levels[1], levels[2]
						);
				
				PrepareEnchant.runRoutines(data);
				
				
				
				levels[0] = data.get(Integer.class, data.start + 5);
				levels[1] = data.get(Integer.class, data.start + 6);
				levels[2] = data.get(Integer.class, data.start + 7);
			}
		}),
	
	Enchant(
			new SimpleEventInfo(
					Player.class, 			"player",
					World.class,			"world",
					ItemStack.class, 		"item",
					EnchantmentsRef.class,	"enchantments",
					Integer.class,			"level"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onEnchantItem(EnchantItemEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getEnchanter();
					EventData data = Enchant.eventInfo.makeData(
							player,
							player.getWorld(),
							event.getItem(),
							new EnchantmentsRef(event.getEnchantsToAdd()),
							event.getExpLevelCost()
							);
					
					Enchant.runRoutines(data);
					
					int level = data.get(Integer.class, data.start + 5);
					
					event.setExpLevelCost(level);
				}
			}),
	
	Interact(
			new SimpleEventInfo(
					Player.class,		"player",
					World.class,		"world",
					ItemStack.class, 	"item",
					Boolean.class,		"interact_left",
					Boolean.class,		"interact_right",
					Boolean.class,		"interact_block",
					Boolean.class,		"interact_air",
					Integer.class,		"interact_block_type",
					Integer.class,		"interact_block_data"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onInteract(PlayerInteractEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getPlayer();
					Action action = event.getAction();
					
					Block clickedBlock = event.getClickedBlock();
					int block_type = 0;
					int block_data = 0;
					if (clickedBlock != null)
					{
						block_type = clickedBlock.getTypeId();
						block_data = clickedBlock.getData();
					}
					
					EventData data = Interact.eventInfo.makeData(
							player,
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
					Player.class,	"player",
					World.class,	"world",
					Entity.class,	"target"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onInteractEntity(PlayerInteractEntityEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getPlayer();
					Entity target = event.getRightClicked();
					EventData data = InteractEntity.eventInfo.makeData(
							player,
							player.getWorld(),
							target);
					
					InteractEntity.runRoutines(data);
				}
			}),
			
	ItemHeld(
			new SimpleEventInfo(
					Player.class,	"player",
					World.class,	"world",
					Integer.class,	"prevslot",
					Integer.class,	"newslot"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onItemHeld(PlayerItemHeldEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Player player = event.getPlayer();
					EventData data = ItemHeld.eventInfo.makeData(
							player,
							player.getWorld(),
							event.getPreviousSlot(),
							event.getNewSlot());
					
					ItemHeld.runRoutines(data);
				}
			}),
			
	Join(
			new SimpleEventInfo(
					Player.class,	"player",
					World.class,	"world"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onJoin(PlayerJoinEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					if (disableJoinMessages)
						event.setJoinMessage(null);
					
					Player player = event.getPlayer();
					EventData data = Join.eventInfo.makeData(
							player,
							player.getWorld());
					
					Join.runRoutines(data);
				}
			}),
			
	Quit(
			new SimpleEventInfo(
					Player.class,	"player",
					World.class,	"world"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onQuit(PlayerQuitEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					if (disableQuitMessages)
						event.setQuitMessage(null);
					
					Player player = event.getPlayer();
					EventData data = Quit.eventInfo.makeData(
							player,
							player.getWorld());
					
					Quit.runRoutines(data);
				}
			}),
			
	Target(
			new SimpleEventInfo(
					Entity.class,	"entity",
					World.class,	"world",
					Entity.class,	"target",
					EntityTargetEvent.TargetReason.class, "reason"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onTarget(EntityTargetEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Entity entity = event.getEntity();
					Entity target = event.getTarget();
					EventData data = Target.eventInfo.makeData(
							entity,
							entity.getWorld(),
							target,
							event.getReason());
					
					Target.runRoutines(data);
				}
			}),
			
	Combust(
			new SimpleEventInfo(
					Entity.class,	"entity",
					World.class,	"world",
					Integer.class,	"duration",
					Entity.class,	"combustor",
					Integer.class,	"block_type",
					Integer.class,	"block_data"),
					
			new Listener() {
				@EventHandler(priority=EventPriority.HIGHEST)
				public void onCombust(EntityCombustEvent event)
				{
					if(!ModDamage.isEnabled) return;
					
					Entity entity = event.getEntity();
					Integer duration = event.getDuration();
					Entity combustor = null;
					Integer block_type = 0;
					Integer block_data = 0;
					
					if (event instanceof EntityCombustByEntityEvent)
						combustor = ((EntityCombustByEntityEvent)event).getCombuster();
					
					if (event instanceof EntityCombustByBlockEvent)
					{
						Block combustorBlock = ((EntityCombustByBlockEvent)event).getCombuster();
						if (combustorBlock != null)
						{
							block_type = combustorBlock.getTypeId();
							block_data = (int) combustorBlock.getData();
						}
					}
					
					EventData data = Combust.eventInfo.makeData(
							entity,
							entity.getWorld(),
							duration,
							combustor,
							block_type,
							block_data);
					
					Combust.runRoutines(data);
					
					event.setDuration((Integer) data.get(data.start + 4));
				}
			});
	
	private final EventInfo eventInfo;
	public final Listener listener;
	
	private ModDamageEventHandler(EventInfo eventInfo, Listener listener)
	{
		this.eventInfo = eventInfo;
		this.listener = listener;
	}

	public static boolean disableDeathMessages = false;
	public static boolean disableJoinMessages = false;
	public static boolean disableQuitMessages = false;
	
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
		Projectile projectile = null;
		Entity target = event.getEntity();
		World world = target.getWorld();
		
		if(event instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent event_EE = (EntityDamageByEntityEvent)event;
			Entity damager = event_EE.getDamager();
			
			if(damager instanceof Projectile)
			{
				projectile = (Projectile)damager;
				
				attacker = projectile.getShooter();
			}
			else
			{
				attacker = damager;
			}
		}
		
	    return Damage.eventInfo.makeData(
	    		attacker,
	    		projectile,
	    		target,
	    		world,
	    		damageElement,
	    		event.getDamage());
	}
};