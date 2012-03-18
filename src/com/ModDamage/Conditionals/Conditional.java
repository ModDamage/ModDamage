package com.ModDamage.Conditionals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Conditionals.CompoundConditional.LogicalOperator;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

abstract public class Conditional
{
	public static final Pattern whitespace = Pattern.compile("\\s*");
	
	public static Conditional getNew(String string, EventInfo info)
	{
		if(string == null) return null;
		
		StringMatcher sm = new StringMatcher(string);
		
		Conditional conditional = getNewFromFront(sm.spawn(), info);
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
	
	protected static Conditional getNewFromFront(StringMatcher sm, EventInfo info)
	{
		if(sm == null) return null;
		
		Conditional conditional = null;
		for(ConditionalBuilder builder : registeredConditionals)
		{
			conditional = builder.getNewFromFront(sm.spawn(), info);
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
			
			Conditional right = getNewFromFront(sm, info);
			
			if (right == null)
				return null;
			
			return new CompoundConditional(matcher.group(), conditional, operator, right);
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
		MatchableType.register();
		EntityBiome.register();
		EntityBlockStatus.register();
		EntityRegion.register();
		EntityStatus.register();
		EntityTagged.register();
		EntityWearing.register();
		EntityWielding.register();
		EntityHasPotionEffect.register();
		PlayerGroupEvaluation.register();
		PlayerHasItem.register();
		PlayerHasEnchantment.register();
		PlayerHasPermission.register();
		PlayerItemInSlotMatches.register();
		//Event
		EventHasProjectile.register();
		EventWorld.register();
		//World
		EventEnvironment.register();
		//Server
		ServerOnlineMode.register();
		//Other
		ItemMatches.register();
		BooleanData.register();
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
	
	protected final String configString;
	
	protected Conditional(String configString)
	{
		this.configString = configString;
	}
	
	public String toString()
	{
		return configString;
	}
	
	public final boolean evaluate(EventData data) throws BailException
	{
		try
		{
			return myEvaluate(data);
		}
		catch (Throwable t)
		{
			throw new BailException(this, t);
		}
	}
	protected abstract boolean myEvaluate(EventData data) throws BailException;
	
	
	abstract protected static class ConditionalBuilder
	{
		public abstract Conditional getNewFromFront(StringMatcher sm, EventInfo info);
	}
	
	abstract protected static class SimpleConditionalBuilder extends ConditionalBuilder
	{
		private Pattern pattern;
		
		public SimpleConditionalBuilder(Pattern pattern)
		{
			this.pattern = pattern;
		}
		
		public final Conditional getNewFromFront(StringMatcher sm, EventInfo info)
		{
			Matcher matcher = sm.matchFront(pattern);
			if (matcher != null)
			{
				Conditional conditional = getNew(matcher, info);
				if (conditional != null)
				{
					sm.accept();
					return conditional;
				}
			}
			return null;
		}
		
		protected abstract Conditional getNew(Matcher matcher, EventInfo info);
	}
}
