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
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Routines.Routines;

public class LaunchProjectile extends NestedRoutine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final Class<? extends Projectile> launchType;
	private final Routines routines;
	public LaunchProjectile(String configString, IDataProvider<LivingEntity> livingDP, Class<? extends Projectile> launchType, Routines routines)
	{
		super(configString);
		this.livingDP = livingDP;
		this.launchType = launchType;
		this.routines = routines;
	}
	
	static EventInfo myInfo = new SimpleEventInfo(
			Projectile.class, "projectile",
			EntityType.class, "projectile",
			Integer.class, "yaw",
			Integer.class, "pitch",
			Integer.class, "speed");
	static EventInfo explosiveInfo = new SimpleEventInfo(
			Integer.class, "yield",
			Integer.class, "incendiary");

	@Override
	public void run(EventData data) throws BailException 
	{
		LivingEntity entity = livingDP.get(data);
		if (entity == null)
			return;
		
		Projectile projectile = entity.launchProjectile(launchType);
		projectile.setShooter(entity); // this is automatically set for some projectiles, but not Fireball and possibly others
		
		Explosive explosive = null;
		if (projectile instanceof Explosive)
			explosive = (Explosive) projectile;
		
		Location loc = entity.getEyeLocation();
		
		int yaw = (int) loc.getYaw();
		int pitch = (int) loc.getPitch();
		int speed = (int) (projectile.getVelocity().length() * 10);
		
		EventData baseData = myInfo.makeChainedData(data, 
				projectile, EntityType.get(projectile), yaw, pitch, speed);
		EventData newData = baseData;
		

		int yield = 0, incendiary = 0;
		if (explosive != null)
		{
			yield = (int)(explosive.getYield() * 10);
			incendiary = explosive.isIncendiary()? 1 : 0;
			
			newData = explosiveInfo.makeChainedData(baseData, yield, incendiary);
		}
		
		routines.run(newData);
		
		yaw = baseData.get(Integer.class, baseData.start + 2);
		pitch = baseData.get(Integer.class, baseData.start + 3);
		speed = baseData.get(Integer.class, baseData.start + 4);
		if (explosive != null)
		{
			yield = newData.get(Integer.class, baseData.start + 0);
			incendiary = newData.get(Integer.class, baseData.start + 1);
			
		}
		
		loc.setYaw(yaw);
		loc.setPitch(pitch);
		projectile.teleport(loc);
		Vector direction = loc.getDirection().multiply(speed / 10.0);
		
		// This is what you're supposed to do with Fireballs, but this is broken
		// and setting velocity works just fine.
		//if (projectile instanceof Fireball)
		//	((Fireball)projectile).setDirection(direction);
		
		projectile.setVelocity(direction);
		
		if (explosive != null)
		{
			explosive.setYield(yield / 10.0f);
			explosive.setIsIncendiary(incendiary != 0);
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
			IDataProvider<LivingEntity> livingDP = DataProvider.parse(info, LivingEntity.class, matcher.group(1));
			if (livingDP == null) return null;

			EventInfo einfo = info.chain(myInfo);
			boolean isExplosive = Explosive.class.isAssignableFrom(launchType.myClass);
			if (isExplosive) einfo = einfo.chain(explosiveInfo);
			NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Projectile Launch: \"" + matcher.group(1) + "\", \"" + matcher.group(2) + "\"" + (isExplosive?" explosive":""));
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(routines == null) return null;
			
			NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End Projectile Launch");
			return new LaunchProjectile(matcher.group(), livingDP, (Class<? extends Projectile>)launchType.myClass, routines);
		}
	}
}
