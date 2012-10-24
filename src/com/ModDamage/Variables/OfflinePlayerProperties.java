package com.ModDamage.Variables;

import com.ModDamage.EventInfo.*;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.StringMatcher;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OfflinePlayerProperties
{
	public static void register()
	{
		DataProvider.register(Boolean.class, OfflinePlayer.class, Pattern.compile("_isOnline", Pattern.CASE_INSENSITIVE),
				new IDataParser<Boolean, OfflinePlayer>() {
					public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<OfflinePlayer> opDP, Matcher m, StringMatcher sm) {
						return new DataProvider<Boolean, OfflinePlayer>(OfflinePlayer.class, opDP) {
                            public Boolean get(OfflinePlayer op, EventData data) { return op.isOnline(); }
                            public Class<Boolean> provides() { return Boolean.class; }
                            public String toString() { return startDP.toString() + "_isOnline"; }
                        };
					}
				});

        SettableDataProvider.register(Boolean.class, OfflinePlayer.class, Pattern.compile("_isBanned", Pattern.CASE_INSENSITIVE),
                new IDataParser<Boolean, OfflinePlayer>() {
                    public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<OfflinePlayer> opDP, Matcher m, StringMatcher sm) {
                        return new SettableDataProvider<Boolean, OfflinePlayer>(OfflinePlayer.class, opDP) {
                            public Boolean get(OfflinePlayer op, EventData data) { return op.isBanned(); }
                            public Class<Boolean> provides() { return Boolean.class; }
                            public String toString() { return startDP.toString() + "_isBanned"; }

                            public void set(OfflinePlayer op, EventData data, Boolean value) {
                                op.setBanned(value);
                            }

                            public boolean isSettable() {
                                return true;
                            }
                        };
                    }
                });

        SettableDataProvider.register(Boolean.class, OfflinePlayer.class, Pattern.compile("_isWhitelisted", Pattern.CASE_INSENSITIVE),
                new IDataParser<Boolean, OfflinePlayer>() {
                    public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<OfflinePlayer> opDP, Matcher m, StringMatcher sm) {
                        return new SettableDataProvider<Boolean, OfflinePlayer>(OfflinePlayer.class, opDP) {
                            public Boolean get(OfflinePlayer op, EventData data) { return op.isWhitelisted(); }
                            public Class<Boolean> provides() { return Boolean.class; }
                            public String toString() { return startDP.toString() + "_isWhitelisted"; }

                            public void set(OfflinePlayer op, EventData data, Boolean value) {
                                op.setWhitelisted(value);
                            }

                            public boolean isSettable() {
                                return true;
                            }
                        };
                    }
                });

        SettableDataProvider.register(Boolean.class, OfflinePlayer.class, Pattern.compile("_isOp", Pattern.CASE_INSENSITIVE),
                new IDataParser<Boolean, OfflinePlayer>() {
                    public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<OfflinePlayer> opDP, Matcher m, StringMatcher sm) {
                        return new SettableDataProvider<Boolean, OfflinePlayer>(OfflinePlayer.class, opDP) {
                            public Boolean get(OfflinePlayer op, EventData data) { return op.isOp(); }
                            public Class<Boolean> provides() { return Boolean.class; }
                            public String toString() { return startDP.toString() + "_isOp"; }

                            public void set(OfflinePlayer op, EventData data, Boolean value) {
                                op.setOp(value);
                            }

                            public boolean isSettable() {
                                return true;
                            }
                        };
                    }
                });

        DataProvider.register(Boolean.class, OfflinePlayer.class, Pattern.compile("_hasPlayedBefore", Pattern.CASE_INSENSITIVE),
                new IDataParser<Boolean, OfflinePlayer>() {
                    public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<OfflinePlayer> opDP, Matcher m, StringMatcher sm) {
                        return new DataProvider<Boolean, OfflinePlayer>(OfflinePlayer.class, opDP) {
                            public Boolean get(OfflinePlayer op, EventData data) { return op.hasPlayedBefore(); }
                            public Class<Boolean> provides() { return Boolean.class; }
                            public String toString() { return startDP.toString() + "_hasPlayedBefore"; }
                        };
                    }
                });




        DataProvider.registerTransformer(Player.class, OfflinePlayer.class,
                new DataProvider.IDataTransformer<Player, OfflinePlayer>() {
                    public IDataProvider<Player> transform(EventInfo info, IDataProvider<OfflinePlayer> blockDP) {
                        return new DataProvider<Player, OfflinePlayer>(OfflinePlayer.class, blockDP) {
                            public Player get(OfflinePlayer op, EventData data) { return op.getPlayer(); }
                            public Class<Player> provides() { return Player.class; }
                            public String toString() { return startDP.toString(); }
                        };
                    }
                });
	}
}
