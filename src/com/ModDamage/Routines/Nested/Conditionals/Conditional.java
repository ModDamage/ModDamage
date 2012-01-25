package com.ModDamage.Routines.Nested.Conditionals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Routines.Nested.Conditionals.CompoundConditional.LogicalOperator;
import com.ModDamage.PluginConfiguration.OutputPreset;

abstract public class Conditional
{
	public static final Pattern whitespace = Pattern.compile("\\s*");
	
	public static Conditional getNew(String string)
	{
		if(string == null) return null;
		
		StringMatcher sm = new StringMatcher(string);
		
		Conditional conditional = getNewFromFront(sm.spawn());
		if (conditional == null)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unmatched conditional \"" + string + "\"");
			return null;
		}
		
		if (!sm.matchesAll(whitespace))
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unmatched stuff after conditional: \"" + sm.string + "\"");
			return null;
		}
		
		return conditional;
	}
	
	protected static Conditional getNewFromFront(StringMatcher sm)
	{
		if(sm == null) return null;
		
		Conditional conditional = null;
		for(ConditionalBuilder builder : registeredConditionals)
		{
			conditional = builder.getNewFromFront(sm.spawn());
			if (conditional != null) break;
		}
		
		if (conditional == null)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad conditional at the beginning of: \"" + sm.string + "\"");
			return null;
		}
		
		Matcher matcher = sm.matchFront(CompoundConditional.LogicalOperator.pattern);
		if (matcher != null)
		{
			LogicalOperator operator = LogicalOperator.match(matcher.group(1));
			
			Conditional right = getNewFromFront(sm);
			
			if (right == null)
				return null;
			
			return new CompoundConditional(conditional, operator, right);
		}
		
		sm.accept();
		return conditional;
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
		EntityType.register();
		EntityBiome.register();
		EntityBlockStatus.register();
		EntityRegion.register();
		EntityStatus.register();
		EntityTagged.register();
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
		public abstract Conditional getNewFromFront(StringMatcher sm);
	}
	
	abstract protected static class SimpleConditionalBuilder extends ConditionalBuilder
	{
		private Pattern pattern;
		
		public SimpleConditionalBuilder(Pattern pattern)
		{
			this.pattern = pattern;
		}
		
		public final Conditional getNewFromFront(StringMatcher sm)
		{
			Matcher matcher = sm.matchFront(pattern);
			if (matcher != null)
			{
				Conditional conditional = getNew(matcher);
				if (conditional != null)
				{
					sm.accept();
					return conditional;
				}
			}
			return null;
		}
		
		protected abstract Conditional getNew(Matcher matcher);
	}
}