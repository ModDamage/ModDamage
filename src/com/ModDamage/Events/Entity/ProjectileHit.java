package com.ModDamage.Events.Entity;

import java.lang.reflect.Field;

import net.minecraft.server.EntityArrow;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.ModDamage.MDEvent;
import com.ModDamage.ModDamage;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;

public class ProjectileHit extends MDEvent implements Listener
{
	public ProjectileHit() { super(myInfo); }
	
	// Try the magic way to get hit block from EntityArrow
	private static Field inGroundProp, xprop, yprop, zprop;
	private static boolean tryMagic = false;
	static {
		try
		{
			inGroundProp = EntityArrow.class.getDeclaredField("inGround");
			xprop = EntityArrow.class.getDeclaredField("d");
			yprop = EntityArrow.class.getDeclaredField("e");
			zprop = EntityArrow.class.getDeclaredField("f");

			inGroundProp.setAccessible(true);
			xprop.setAccessible(true);
			yprop.setAccessible(true);
			zprop.setAccessible(true);
			
			System.out.println("Trying ProjectileHit magic for arrow hit block");
			
			tryMagic = true;
		}
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchFieldException e) { e.printStackTrace(); }
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Entity.class, 		"shooter", "entity",
			Block.class, 		"hitblock",
			Projectile.class,	"projectile",
			World.class,		"world");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(!ModDamage.isEnabled) return;
		
		final Projectile projectile = event.getEntity();
		final LivingEntity shooter = projectile.getShooter();
		
		if (tryMagic && projectile instanceof Arrow) {
			magicWay(shooter, (Arrow) projectile);
		}
		else
		{
			normalWay(shooter, projectile);
		}
		
	}
	

	private void normalWay(LivingEntity shooter, Projectile projectile)
	{
		EventData data = myInfo.makeData(
				shooter,
				null,
				projectile,
				projectile.getWorld());
		
		runRoutines(data);
	}
	
	private void magicWay(final LivingEntity shooter, final Arrow arrow)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(ModDamage.getPluginConfiguration().plugin, new Runnable() {
			public void run()
			{
				if (arrow.isDead()) return;
				
				EntityArrow earrow = ((CraftArrow) arrow).getHandle();
				
				
				boolean inGround;
				int x, y, z;
				
				try
				{
					inGround = inGroundProp.getBoolean(earrow);
					x = xprop.getInt(earrow);
					y = yprop.getInt(earrow);
					z = zprop.getInt(earrow);
				}
				catch (IllegalArgumentException e) { magicFail(e); normalWay(shooter, arrow); return; }
				catch (IllegalAccessException e) {magicFail(e); normalWay(shooter, arrow); return;}
				
				Block block = null;
				
				if (inGround) 
					block = arrow.getWorld().getBlockAt(x, y, z);
				
				EventData data = myInfo.makeData(
						shooter,
						block,
						arrow,
						arrow.getWorld());
				
				runRoutines(data);
			}
		});
	}
	
	private void magicFail(Object e) {
		tryMagic = false;
		System.err.println("ProjectileHit magic failed: "+e.toString());
	}
}
