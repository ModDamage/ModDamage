package com.ModDamage.Properties;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.ModDamage.Scoreboards;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.FunctionParser;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.Parsing.SettableDataProvider;
import com.ModDamage.Parsing.Property.Properties;

public class ScoreboardProps
{
	public static void register()
	{
		DataProvider.register(Scoreboard.class, null, Pattern.compile("scoreboardnamed"), new FunctionParser<Scoreboard, Object>(String.class)
				{
					@Override
					public IDataProvider<Scoreboard> makeProvider(EventInfo info, IDataProvider<Object> nullDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
					{
						if (nullDP != null) return null;
						
						@SuppressWarnings("unchecked")
						final IDataProvider<String> nameDP = (IDataProvider<String>)arguments[0];
						
						return new IDataProvider<Scoreboard>() {
							@Override
							public Scoreboard get(EventData data) throws BailException
							{
								String name = nameDP.get(data);
								if (name == null) return null;
								
								return Scoreboards.getNamed(name);
							}
							
							public Class<? extends Scoreboard> provides() {  return Scoreboard.class;  }
						};
					}
				});
		
        Properties.register("scoreboard", Player.class, "getScoreboard", "setScoreboard");
        
        SettableDataProvider.register(Team.class, OfflinePlayer.class, Pattern.compile("_team", Pattern.CASE_INSENSITIVE), new IDataParser<Team, OfflinePlayer>()
			{
				@Override
				public IDataProvider<Team> parse(ScriptLine scriptLine, EventInfo info, final IDataProvider<OfflinePlayer> playerDP, Matcher m, StringMatcher sm)
				{
					final IDataProvider<Scoreboard> scoreboardDP = Scoreboards.getCurrent(info);
					
					return new ISettableDataProvider<Team>() {
							@Override
							public Team get(EventData data) throws BailException
							{
								Scoreboard sb = scoreboardDP.get(data);
								if (sb == null) return null;
								
								OfflinePlayer player = playerDP.get(data);
								
								return sb.getPlayerTeam(player);
							}
							
							@Override
							public void set(EventData data, Team team) throws BailException
							{
								Scoreboard sb = scoreboardDP.get(data);
								if (sb == null) return;
								
								OfflinePlayer player = playerDP.get(data);
								
								if (team == null)
								{
									team = sb.getPlayerTeam(player);
									if (team != null)
										team.removePlayer(player);
								}
								else
									team.addPlayer(player);
							}
							
							public Class<? extends Team> provides() {  return Team.class;  }
							public boolean isSettable() {  return true;  }
						};
				}
			});
        
        DataProvider.register(Team.class, null, Pattern.compile("teamnamed"), new FunctionParser<Team, Object>(String.class)
			{
				@Override
				public IDataProvider<Team> makeProvider(EventInfo info, IDataProvider<Object> nullDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
				{
					if (nullDP != null) return null;
					
					final IDataProvider<Scoreboard> scoreboardDP = Scoreboards.getCurrent(info);
					
					@SuppressWarnings("unchecked")
					final IDataProvider<String> nameDP = (IDataProvider<String>)arguments[0];

					return new IDataProvider<Team>() {
						@Override
						public Team get(EventData data) throws BailException
						{
							Scoreboard sb = scoreboardDP.get(data);
							if (sb == null) return null;
							
							String name = nameDP.get(data);
							if (name == null) return null;
							
							Team team = sb.getTeam(name);
							if (team == null)
								team = sb.registerNewTeam(name);
							return team;
						}
						
						public Class<? extends Team> provides() {  return Team.class;  }
					};
				}
			});
        
        DataProvider.register(Objective.class, null, Pattern.compile("objectivenamed"), new FunctionParser<Objective, Object>(String.class, String.class)
			{
				@Override
				public IDataProvider<Objective> makeProvider(EventInfo info, IDataProvider<Object> nullDP, @SuppressWarnings("rawtypes") IDataProvider[] arguments)
				{
					if (nullDP != null) return null;
					
					final IDataProvider<Scoreboard> scoreboardDP = Scoreboards.getCurrent(info);
					
					@SuppressWarnings("unchecked")
					final IDataProvider<String> nameDP = (IDataProvider<String>)arguments[0];
					@SuppressWarnings("unchecked")
					final IDataProvider<String> criteriaDP = (IDataProvider<String>)arguments[2];

					return new IDataProvider<Objective>() {
						@Override
						public Objective get(EventData data) throws BailException
						{
							Scoreboard sb = scoreboardDP.get(data);
							if (sb == null) return null;
							
							String name = nameDP.get(data);
							if (name == null) return null;
							
							String criteria = criteriaDP.get(data);
							if (criteria == null) return null;
							
							Objective objective = sb.getObjective(name);
							if (objective == null)
								objective = sb.registerNewObjective(name, criteria);
							return objective;
						}
						
						public Class<? extends Objective> provides() {  return Objective.class;  }
					};
				}
			});
        
        DataProvider.register(Integer.class, OfflinePlayer.class, Pattern.compile("objectivenamed"), new IDataParser<Integer, OfflinePlayer>()
			{
				@Override
				public IDataProvider<Integer> parse(ScriptLine scriptLine, EventInfo info, final IDataProvider<OfflinePlayer> playerDP, Matcher m, StringMatcher sm)
				{
					final IDataProvider<Scoreboard> scoreboardDP = Scoreboards.getCurrent(info);
					
					final IDataProvider<String> nameDP = InterpolatedString.parseWord(scriptLine, InterpolatedString.word, sm.spawn(), info);
					if (nameDP == null) return null;

					return new ISettableDataProvider<Integer>() {
						@Override
						public Integer get(EventData data) throws BailException
						{
							Scoreboard sb = scoreboardDP.get(data);
							if (sb == null) return null;
							
							OfflinePlayer player = playerDP.get(data);
							if (player == null) return null;
							
							String name = nameDP.get(data);
							if (name == null) return null;
							
							Objective objective = sb.getObjective(name);
							if (objective == null)
								objective = sb.registerNewObjective(name, "dummy");
							
							return objective.getScore(player).getScore();
						}
						
						public Class<? extends Integer> provides() {  return Integer.class;  }

						@Override
						public void set(EventData data, Integer value) throws BailException
						{
							Scoreboard sb = scoreboardDP.get(data);
							if (sb == null) return;
							
							OfflinePlayer player = playerDP.get(data);
							if (player == null) return;
							
							String name = nameDP.get(data);
							if (name == null) return;
							
							Objective objective = sb.getObjective(name);
							if (objective == null)
								objective = sb.registerNewObjective(name, "dummy");
							
							objective.getScore(player).setScore(value);
						}

						@Override
						public boolean isSettable()
						{
							return true;
						}
					};
				}
			});
        
        Properties.register("name", Team.class, "getName");
        Properties.register("displayName", Team.class, "getDisplayName", "setDisplayName");
        Properties.register("prefix", Team.class, "getPrefix", "setPrefix");
        Properties.register("suffix", Team.class, "getSuffix", "setSuffix");
        

        Properties.register("allowFriendlyFire", Team.class, "allowFriendlyFire", "setAllowFriendlyFire");
        Properties.register("canSeeFriendlyInvisibles", Team.class, "canSeeFriendlyInvisibles", "setCanSeeFriendlyInvisibles");
	}
}
