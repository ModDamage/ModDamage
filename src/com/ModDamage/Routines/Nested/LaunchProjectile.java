package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;

public class LaunchProjectile extends NestedRoutine
{
	private final DataRef<LivingEntity> entityRef;
	private final Class<? extends Projectile> launchType;
	private final Routines routines;
	public LaunchProjectile(String configString, DataRef<LivingEntity> entityRef, Class<? extends Projectile> launchType, Routines routines)
	{
		super(configString);
		this.entityRef = entityRef;
		this.launchType = launchType;
		this.routines = routines;
	}
	
	static EventInfo myInfo = new SimpleEventInfo(
			Projectile.class, "projectile",
			EntityType.class, "projectile",
			IntRef.class, "yaw",
			IntRef.class, "pitch",
			IntRef.class, "speed");
	static EventInfo explosiveInfo = new SimpleEventInfo(
			IntRef.class, "yield",
			IntRef.class, "incendiary");

	@Override
	public void run(EventData data) throws BailException 
	{
		LivingEntity entity = entityRef.get(data);
		if (entity == null)
			return;
		
		Projectile projectile = entity.launchProjectile(launchType);
		projectile.setShooter(entity); // this is automatically set for some projectiles, but not Fireball and possibly others
		
		Explosive explosive = null;
		if (projectile instanceof Explosive)
			explosive = (Explosive) projectile;
		
		Location loc = entity.getEyeLocation();
		
		IntRef yaw = new IntRef((int) loc.getYaw());
		IntRef pitch = new IntRef((int) loc.getPitch());
		IntRef speed = new IntRef((int) (projectile.getVelocity().length() * 10));
		
		EventData newData = myInfo.makeChainedData(data, 
				projectile, EntityType.get(projectile), yaw, pitch, speed);
		

		IntRef yield = null, incendiary = null;
		if (explosive != null)
		{
			yield = new IntRef((int)(explosive.getYield() * 10));
			incendiary = new IntRef(explosive.isIncendiary()? 1 : 0);
			
			newData = explosiveInfo.makeChainedData(newData, yield, incendiary);
		}
		
		routines.run(newData);
		
		loc.setYaw(yaw.value);
		loc.setPitch(pitch.value);
		projectile.teleport(loc);
		Vector direction = loc.getDirection().multiply(speed.value / 10.0);
		
		// This is what you're supposed to do with Fireballs, but this is broken
		// and setting velocity works just fine.
		//if (projectile instanceof Fireball)
		//	((Fireball)projectile).setDirection(direction);
		
		projectile.setVelocity(direction);
		
		if (explosive != null)
		{
			explosive.setYield(yield.value / 10.0f);
			explosive.setIsIncendiary(incendiary.value != 0);
		}
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.launch\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public LaunchProjectile getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			EntityType launchType = EntityType.getElementNamed(matcher.group(2));
			if (launchType == null) return null;
			if (!Projectile.class.isAssignableFrom(launchType.myClass) || launchType == EntityType.POTION)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Not a launchable projectile: "+matcher.group(2));
				return null;
			}
			DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, matcher.group(1).toLowerCase());
			if (entityRef == null) return null;

			EventInfo einfo = info.chain(myInfo);
			boolean isExplosive = Explosive.class.isAssignableFrom(launchType.myClass);
			if (isExplosive) einfo = einfo.chain(explosiveInfo);
			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Projectile Launch: \"" + matcher.group(1) + "\", \"" + matcher.group(2) + "\"" + (isExplosive?" explosive":""));
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(routines == null) return null;
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End Projectile Launch");
			return new LaunchProjectile(matcher.group(), entityRef, (Class<? extends Projectile>)launchType.myClass, routines);
		}
	}
}
