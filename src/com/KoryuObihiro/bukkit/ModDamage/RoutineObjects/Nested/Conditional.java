package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.Chance;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.Comparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.CompoundConditional;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.CompoundConditional.LogicalOperator;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EntityBlockStatus;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EntityRegion;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EntityStatus;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EntityTagged;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EntityType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EntityWearing;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EntityWielding;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EventEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EventHasProjectile;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.EventWorld;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.InvertConditional;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.NestedConditional;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.PlayerGroupEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.PlayerHasEnchantment;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.PlayerHasItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.PlayerPermissionEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals.ServerOnlineMode;

abstract public class Conditional
{
	public static final Pattern whitespace = Pattern.compile("\\s*");
	
	public static Conditional getNew(String string)
	{
		if(string == null) return null;
		
		CResult res = getNewFromFront(string);
		if (res == null)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unmatched conditional \"" + string + "\"");
			return null;
		}
		
		if (!whitespace.matcher(res.rest).matches())
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unmatched stuff after conditional: \"" + res.rest + "\"");
			return null;
		}
		
		return res.conditional;
	}
	
	protected static CResult getNewFromFront(String string)
	{
		if(string == null) return null;
		
		CResult res = null;
		for(ConditionalBuilder builder : registeredConditionals)
		{
			res = builder.getNewFromFront(string);
			if (res != null) break;
		}
		
		if (res == null)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad conditional at the beginning of: \"" + string + "\"");
			return null;
		}
		
		Matcher matcher = CompoundConditional.LogicalOperator.pattern.matcher(res.rest);
		if (matcher.lookingAt())
		{
			LogicalOperator operator = LogicalOperator.match(matcher.group(1));
			
			CResult res2 = getNewFromFront(res.rest.substring(matcher.end()));
			
			if (res2 == null)
				return null;
			
			res2.conditional = new CompoundConditional(res.conditional, operator, res2.conditional);
			return res2;
		}
		
		return res;
	}
	
	private static List<ConditionalBuilder> registeredConditionals = new ArrayList<ConditionalBuilder>();
	
	public static void register(ConditionalBuilder builder)
	{
		registeredConditionals.add(builder);
	}
	
	public static void register()
	{
		registeredConditionals.clear();
		Chance.register();
		InvertConditional.register();
		NestedConditional.register();
		//Entity
		EntityBiome.register();
		EntityBlockStatus.register();
		EntityRegion.register();
		EntityStatus.register();
		EntityTagged.register();
		EntityType.register();
		EntityWearing.register();
		EntityWielding.register();
		PlayerGroupEvaluation.register();
		PlayerHasItem.register();
		PlayerHasEnchantment.register();
		PlayerPermissionEvaluation.register();
		//Event
		EventHasProjectile.register();
		EventWorld.register();
		//World
		EventEnvironment.register();
		//Server
		ServerOnlineMode.register();
		//Other
		Comparison.register();
	}
	
	public static class CResult
	{
		public Conditional conditional;
		public String rest;
		
		public CResult(Conditional conditional, String rest)
		{
			this.conditional = conditional;
			this.rest = rest;
		}
	}
	
	protected Conditional()
	{
	}
	
	public abstract boolean evaluate(TargetEventInfo eventInfo);
	
	
	abstract protected static class ConditionalBuilder
	{
		public abstract CResult getNewFromFront(String string);
	}
	
	abstract protected static class SimpleConditionalBuilder extends ConditionalBuilder
	{
		Pattern pattern;
		
		public SimpleConditionalBuilder(Pattern pattern)
		{
			this.pattern = pattern;
		}
		
		public final CResult getNewFromFront(String string)
		{
			Matcher matcher = pattern.matcher(string);
			if (matcher.lookingAt())
			{
				Conditional conditional = getNew(matcher);
				if (conditional != null)
					return new CResult(conditional, string.substring(matcher.end()));
			}
			return null;
		}
		
		protected abstract Conditional getNew(Matcher matcher);
	}
}
