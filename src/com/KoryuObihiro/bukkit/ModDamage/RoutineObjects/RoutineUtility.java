package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.mysql.jdbc.AssertionFailedException;

public class RoutineUtility
{
	private static Logger log;
	protected static int configPages = 0;
	protected List<String> configStrings = new ArrayList<String>();
	protected static int additionalConfigChecks = 0;
	
	private static HashMap<Pattern, Method> registeredBaseRoutines = new HashMap<Pattern, Method>();
	private static Pattern conditionalPattern;
	private static Pattern effectPattern;
	private static Pattern switchPattern;
	
	public static final String numberPart = "(?:[0-9]+)";
	public static final String alphanumericPart = "(?:[a-z0-9]+)";
	public static final String potentialAliasPart = "(?:_[a-z0-9]+)";
	public static final String statementPart = "((?:" + RoutineUtility.alphanumericPart + ")(?:\\." + RoutineUtility.alphanumericPart +")*)";
	public static final String entityPart = "(attacker|target)";
	public static String comparisonRegex;
	public static String biomeRegex;
	public static String environmentRegex;
	public static String elementRegex;
	public static String materialRegex;
	public static String armorRegex;
	public static String logicalRegex;
	
	public LogSetting logSetting = LogSetting.NORMAL;
	public enum LogSetting
	{ 
		QUIET, NORMAL, VERBOSE;
		public static LogSetting matchSetting(String key)
		{
			for(LogSetting setting : LogSetting.values())
				if(key.equalsIgnoreCase(setting.name()))
						return setting;
				return null;
		}
		private boolean shouldOutput(LogSetting setting)
		{
			if(setting.ordinal() <= this.ordinal())
				return true;
			return false;
		}
	}	
	
	public RoutineUtility(Logger log){ RoutineUtility.log = log;}
	
	static
	{
		biomeRegex = "(";
		for(Biome biome : Biome.values())
			biomeRegex += biome.name() + "|";
		biomeRegex += potentialAliasPart + ")";
		
		environmentRegex = "(";
		for(Environment environment : Environment.values())
			environmentRegex += environment.name() + "|";
		environmentRegex += potentialAliasPart + ")";

		elementRegex = "(";
		for(ModDamageElement element : ModDamageElement.values())
			elementRegex += element.getReference() + "|";
		elementRegex += potentialAliasPart + ")";
		
		String[] armorParts = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS" };
		materialRegex = armorRegex = "(";
		for(Material material : Material.values())
		{
			materialRegex += material.name() + "|";
			for(String part : armorParts)
				if(material.name().endsWith(part))
					armorRegex += material.name() + "|";
					
		}
		materialRegex += potentialAliasPart + ")";
		armorRegex += potentialAliasPart + "){1,4}";
		
		logicalRegex = "(";
		for(LogicalOperation operation : LogicalOperation.values())
			logicalRegex += operation.name() + "|" + operation.getShortHand() + "|";
		logicalRegex += potentialAliasPart + ")";		
		
		comparisonRegex = "(";
		for(ComparisonType type : ComparisonType.values())
			comparisonRegex += type.name() + "|" + type.getShortHand() + "|";
		comparisonRegex += ")\\.";
		
		conditionalPattern = Pattern.compile("(if|if_not|if!)\\s+(!)?" + statementPart + "(?:\\s*" + logicalRegex + "\\s*" + statementPart + ")*", Pattern.CASE_INSENSITIVE);
		switchPattern = Pattern.compile("switch\\." + statementPart, Pattern.CASE_INSENSITIVE);
		effectPattern = Pattern.compile(entityPart + "effect\\." + statementPart);
	}
	
	//Parse commands recursively for different command strings the handlers pass
	//TODO Put these into config strings, color according to output type
	public List<Routine> parse(List<Object> routineStrings, String loadType){ return parse(routineStrings, loadType, 0);}
	private List<Routine> parse(Object object, String loadType, int nestCount)
	{
		List<Routine> routines = new ArrayList<Routine>();
		String nestIndentation = "";
		for(int i = 0; i < nestCount; i++)
			nestIndentation += "    ";
		if(object instanceof String)
		{
			Routine routine = null;
			for(Pattern pattern : registeredBaseRoutines.keySet())
			{
				Matcher matcher = pattern.matcher((String)object);
				if(matcher.matches())
				{
					try
					{
						routine = (Routine)registeredBaseRoutines.get(pattern).invoke(null, matcher);
						break;
					}
					catch(Exception e){ e.printStackTrace();}
				}
			}
			if(routine != null)
			{
				log.info(nestIndentation + "Routine: \"" + (String)object + "\"");
				routines.add(routine);
			}
			else log.info(nestIndentation + "Couldn't match base routine string \"" + (String)object +"\"");
			
		}
		else if(object instanceof LinkedHashMap)
		{
			HashMap<String, Object> someHashMap = (HashMap<String, Object>)object;//A properly-formatted nested routine is a LinkedHashMap with only one key.
			if(someHashMap.keySet().size() == 1)
				for(String key : someHashMap.keySet())
				{
					Matcher conditionalMatcher = conditionalPattern.matcher(key);
					Matcher switchMatcher = switchPattern.matcher(key);
					Matcher effectMatcher = effectPattern.matcher(key);
					if(conditionalMatcher.matches())
					{
						if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info("");
						if(logSetting.shouldOutput(LogSetting.NORMAL)) log.info(nestIndentation + "Conditional: \"" + key + "\"");
						ConditionalRoutine routine = ConditionalRoutine.getNew(conditionalMatcher, parse(someHashMap.get(key), loadType, nestCount + 1));
						if(routine != null)
						{
							routines.add(routine);
							if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info(nestIndentation + "End conditional statement \"" + key + "\"\n");
						}
						else log.severe("[ModDamage] Error: invalid Conditional " + key);
					}
					else if(effectMatcher.matches())
					{
						CalculatedEffectRoutine<?> routine = CalculatedEffectRoutine.getNew(conditionalMatcher, parse(someHashMap.get(key), loadType, nestCount + 1));
						if(routine != null)
						{
							if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info("");
							if(logSetting.shouldOutput(LogSetting.NORMAL)) log.info(nestIndentation + "CalculatedEffect: \"" + key + "\"");
							routines.add(routine);
							if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info(nestIndentation + "End effect routine \"" + key + "\"\n");
						}
						else log.severe("[ModDamage] Error: invalid CalculatedEffect " + key);
					}
					else if(switchMatcher.matches())
					{
						if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info("");
						if(logSetting.shouldOutput(LogSetting.NORMAL)) log.info(nestIndentation + "Switch: \"" + key + "\"");
						
						LinkedHashMap<String, Object> anotherHashMap = (someHashMap.get(key) instanceof LinkedHashMap?(LinkedHashMap<String, Object>)someHashMap.get(key):null);
						if(anotherHashMap != null)
						{
							LinkedHashMap<String, List<Routine>> routineHashMap = new LinkedHashMap<String, List<Routine>>();
							for(String anotherKey : anotherHashMap.keySet())
							{
								if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info("");
								if(logSetting.shouldOutput(LogSetting.NORMAL)) log.info(nestIndentation + " case: \"" + anotherKey + "\"");
								routineHashMap.put(anotherKey, parse(anotherHashMap.get(anotherKey), loadType, nestCount + 1));
								SwitchRoutine<?> routine = SwitchRoutine.getNew(switchMatcher, routineHashMap);
								if(routine != null)
								{
									if(routine.isLoaded)
									{
										routines.add(routine);
										if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info(nestIndentation + "End case \"" + anotherKey + "\"\n");
									}
									else log.severe("[ModDamage] Error: invalid case " + routine.failedCase);
								}
								else log.severe("[ModDamage] Error: invalid Switch " + key);
							}
						}
						if(logSetting.shouldOutput(LogSetting.VERBOSE)) log.info(nestIndentation + "End switch statement \"" + key + "\"\n");
					}
					else log.severe(nestIndentation + " No match found for nested node \"" + key + "\"");
				}
			else log.info(nestIndentation + "[ModDamage] Parse error: bad nestable found in configuration.");
		}
		else if(object instanceof List)
		{
			for(Object nestedObject : (List<Object>)object)
				routines.addAll(parse(nestedObject, loadType, nestCount));
		}
		else log.info(nestIndentation + "[ModDamage] Error: could not parse object " + object.toString() + " of type " + object.getClass().getName());
		return routines;
	}
	public void registerBase(Class<? extends Routine> routineClass, Pattern syntax)
	{
		try
		{
			Method method = routineClass.getMethod("getNew", Matcher.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(routineClass));
				method.invoke(null, (Matcher)null);
				register(registeredBaseRoutines, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + routineClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + routineClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + routineClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + routineClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");}
	}

	public void registerConditional(Class<? extends ConditionalStatement> statementClass, Pattern syntax)
	{
		try
		{
			Method method = statementClass.getMethod("getNew", Matcher.class, List.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(statementClass));
				method.invoke(null, (Matcher)null);
				register(ConditionalRoutine.registeredStatements, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + statementClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
	}

	public void registerSwitch(Class<? extends SwitchRoutine<?>> statementClass, Pattern syntax)
	{
		try
		{
			Method method = statementClass.getMethod("getNew", Matcher.class, LinkedHashMap.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(statementClass));
				method.invoke(null, (Matcher)null, (LinkedHashMap<String, List<Routine>>)null);
				register(SwitchRoutine.registeredStatements, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + statementClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher, LinkedHashMap)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
	}
	
	public void registerEffect(Class<? extends CalculatedEffectRoutine<?>> routineClass, Pattern syntax)
	{
		try
		{
			Method method = routineClass.getMethod("getNew", Matcher.class, List.class);
			if(method != null)//XXX Is this necessary?
			{
				assert(method.getReturnType().equals(routineClass));
				method.invoke(null, (Matcher)null);
				register(CalculatedEffectRoutine.registeredStatements, method, syntax);
			}
			else log.severe("Method getNew not found for statement " + routineClass.getName());
		}
		catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return class " + routineClass.getName() + "!");}
		catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for class " + routineClass.getName() + "!");}
		catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + routineClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Class \"" + routineClass.toString() + "\" does not have valid getNew() method!");} 
	
		
	}
	
	public void register(HashMap<Pattern, Method> registry, Method method, Pattern syntax)
	{
		boolean successfullyRegistered = false;
		if(syntax != null)
		{
			registry.put(syntax, method);	
			successfullyRegistered = true;
		}
		else log.severe("[ModDamage] Error: Bad regex for registering class \"" + method.getClass().getName() + "\"!");
		if(successfullyRegistered)
		{
			if(shouldOutput(LogSetting.VERBOSE)) log.info("[ModDamage] Registering class " + method.getClass().getName() + " with pattern " + syntax.pattern());
		}
	}

//// HELPER FUNCTIONS ////
	public void clearConfig(){ configStrings.clear();}
		
//// LOGGING ////
	public void setLogging(LogSetting setting)
	{ 
		if(setting != null) 
			this.logSetting = setting;
		else log.severe("[ModDamage] Error: bad debug setting. Valid settings: normal, quiet, verbose");
	}
	
	public void toggleLogging(Player player) 
	{
		LogSetting nextSetting = null; //shouldn't stay like this.
		switch(logSetting)
		{
			case QUIET: 
				nextSetting = LogSetting.NORMAL;
				break;
				
			case NORMAL:
				nextSetting = LogSetting.VERBOSE;
				break;
				
			case VERBOSE:
				nextSetting = LogSetting.QUIET;
				break;
		}
		String sendThis = "Changed debug from " + logSetting.name().toLowerCase() + " to " + nextSetting.name().toLowerCase();
		log.info("[ModDamage] " + sendThis);
		if(player != null) player.sendMessage(ChatColor.GREEN + sendThis);
		logSetting = nextSetting;
	}
	
	public boolean shouldOutput(LogSetting setting){ return logSetting.shouldOutput(setting);}

//// CONFIGURATION REPORTING ////
	public boolean sendConfig(Player player, int pageNumber)
	{
		if(player == null)
		{
			String printString = "Complete configuration for this server:";
			for(String configString : configStrings)
				printString += "\n" + configString;
			
			log.info(printString);
			
			return true;
		}
		else if(pageNumber > 0)
		{
			if(pageNumber <= configPages)
			{
				player.sendMessage(ModDamage.ModDamageString(ChatColor.GOLD) + " Configuration: (" + pageNumber + "/" + (configPages + additionalConfigChecks) + ")");
				for(int i = (9 * (pageNumber - 1)); i < (configStrings.size() < (9 * pageNumber)
															?configStrings.size()
															:(9 * pageNumber)); i++)
					player.sendMessage(ChatColor.DARK_AQUA + configStrings.get(i));
				return true;
			}
		}
		return false;
	}
	
//// INGAME MATCHING ////	
	//Frankly, most of the stuff below should be considered for implementation into Bukkit. :<
	public static Biome matchBiome(String biomeName)
	{
		for(Biome biome : Biome.values())
			if(biomeName.equalsIgnoreCase(biome.name()))
				return biome;
		return null;
	}
	
	public static Environment matchEnvironment(String environmentName)
	{
		for(Environment environment : Environment.values())
			if(environmentName.equalsIgnoreCase(environment.name()))
				return environment;
		return null;
	}
}