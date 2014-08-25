package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import com.ModDamage.LogUtil;
import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class LaunchProjectile extends NestedRoutine
{
	private final IDataProvider<Location> locDP;
	private final IDataProvider<LivingEntity> livingDP;
	private final Class<? extends Projectile> launchType;
	
	public LaunchProjectile(ScriptLine scriptLine, IDataProvider<Location> locDP, IDataProvider<LivingEntity> livingDP, Class<? extends Projectile> launchType)
	{
		super(scriptLine);
		this.locDP = locDP;
		this.livingDP = livingDP;
		this.launchType = launchType;
	}
	
	static EventInfo myInfo = new SimpleEventInfo(
			Projectile.class, "projectile",
			Number.class, "yaw",
			Number.class, "pitch",
			Number.class, "speed");
	static EventInfo explosiveInfo = new SimpleEventInfo(
			Number.class, "yield",
			Number.class, "incendiary");

	@Override
	public void run(EventData data) throws BailException 
	{
		LivingEntity entity = null;
		if (livingDP != null) {
			entity = livingDP.get(data);
			if (entity == null)
				return;
		}
		
		Location loc = null;
		if (locDP != null) {
			loc = locDP.get(data);
			if (loc == null)
				return;
		}
		else {
			if (entity == null) return;
			
			loc = entity.getEyeLocation();
		}

		Projectile projectile = loc.getWorld().spawn(loc, launchType);
		
		if (entity != null) {
			projectile.setShooter(entity); // this is automatically set for some projectiles, but not Fireball and possibly others
		}
		
		Explosive explosive = null;
		if (projectile instanceof Explosive)
			explosive = (Explosive) projectile;
		
		//Location loc = entity.getEyeLocation();
		
		double yaw = loc.getYaw();
		double pitch = loc.getPitch();
		double speed = projectile.getVelocity().length() * 10;
		
		EventData baseData = myInfo.makeChainedData(data, 
				projectile, yaw, pitch, speed);
		EventData newData = baseData;
		

		double yield = 0, incendiary = 0;
		if (explosive != null)
		{
			yield = explosive.getYield() * 10;
			incendiary = explosive.isIncendiary()? 1 : 0;
			
			newData = explosiveInfo.makeChainedData(baseData, yield, incendiary);
		}
		
		routines.run(newData);
		
		yaw = baseData.get(Number.class, baseData.start + 1).doubleValue();
		pitch = baseData.get(Number.class, baseData.start + 2).doubleValue();
		speed = baseData.get(Number.class, baseData.start + 3).doubleValue();
		if (explosive != null)
		{
			yield = newData.get(Number.class, newData.start + 0).doubleValue();
			incendiary = newData.get(Number.class, newData.start + 1).doubleValue();
		}
		
		loc.setYaw((float) yaw);
		loc.setPitch((float) pitch);
		projectile.teleport(loc);
		Vector direction = loc.getDirection().multiply(speed / 10.0);
		
		// This is what you're supposed to do with Fireballs, but this is broken
		// and setting velocity works just fine.
		//if (projectile instanceof Fireball)
		//	((Fireball)projectile).setDirection(direction);
		
		projectile.setVelocity(direction);
		
		if (explosive != null)
		{
			explosive.setYield((float) (yield / 10.0f));
			explosive.setIsIncendiary(incendiary != 0);
		}
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(?:(.*?)(?:effect)?\\.)?launch\\.(.*?)(?:\\.at\\.(.*?))?", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			EntityType launchType = EntityType.getElementNamed(matcher.group(2));
			if (launchType == null) return null;
			if (!Projectile.class.isAssignableFrom(launchType.myClass) || launchType == EntityType.POTION)
			{
				LogUtil.error(scriptLine, "Not a launchable projectile: "+matcher.group(2));
				return null;
			}
			
			if (matcher.group(1) == null && matcher.group(3) == null)
			{
				LogUtil.error(scriptLine, "Either a shooter or a launch location must be specified!");
				return null;
			}
			
			IDataProvider<LivingEntity> livingDP = null;
			if (matcher.group(1) != null) {
				livingDP = DataProvider.parse(scriptLine, info, LivingEntity.class, matcher.group(1));
				if (livingDP == null) return null;
			}

			IDataProvider<Location> locDP = null;
			if (matcher.group(3) != null) {
				locDP = DataProvider.parse(scriptLine, info, Location.class, matcher.group(3));
				if (locDP == null) return null;
			}

			EventInfo einfo = info.chain(myInfo);
			boolean isExplosive = Explosive.class.isAssignableFrom(launchType.myClass);
			if (isExplosive) einfo = einfo.chain(explosiveInfo);
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Projectile Launch: \"" + matcher.group(1) + "\", \"" + matcher.group(2) + "\"" + (isExplosive?" explosive":""));
			
			@SuppressWarnings("unchecked")
			LaunchProjectile routine = new LaunchProjectile(scriptLine, locDP, livingDP, (Class<? extends Projectile>)launchType.myClass);
			return new NestedRoutineBuilder(routine, routine.routines, einfo);
		}
	}
}
