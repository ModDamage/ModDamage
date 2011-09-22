package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

abstract public class Routine
{	
	public static final HashMap<Pattern, Method> registeredBaseRoutines = new HashMap<Pattern, Method>();
	public static final String dynamicIntegerPart;
	static
	{
		String tempString = "(?:";
		for(EntityReference reference : EntityReference.values())
			tempString += reference.name() + "|";
		tempString += "event|world|server)";
		dynamicIntegerPart = "(-?[0-9]+|" + tempString + "\\.\\w+|_\\w+)";
	}
	
	final String configString;
	protected Routine(String configString)
	{
		this.configString = configString;
	}
	public final String getConfigString(){ return configString;}
	abstract public void run(TargetEventInfo eventInfo);

	public static void registerBase(Class<? extends Routine> routineClass, Pattern syntax)
	{
		try
		{
			Method method = routineClass.getMethod("getNew", Matcher.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(routineClass));
				method.invoke(null, (Matcher)null);
				ModDamage.register(registeredBaseRoutines, method, syntax);
			}
			else ModDamage.log.severe("Method getNew not found for statement " + routineClass.getName());
		}
		catch(AssertionError e){ ModDamage.log.severe("[ModDamage] Error: getNew doesn't return class " + routineClass.getName() + "!");}
		catch(SecurityException e){ ModDamage.log.severe("[ModDamage] Error: getNew isn't public for class " + routineClass.getName() + "!");}
		catch(NullPointerException e){ ModDamage.log.severe("[ModDamage] Error: getNew for class " + routineClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch (IllegalAccessException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");}
	}
}