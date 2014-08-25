package com.moddamage.external.vault;

import com.moddamage.StringMatcher;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.external.vault.VaultConditional.VaultConditionalParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VaultConditionals
{
	public static void register()
	{

		///////////////////////////////// Players /////////////////////////////////
		
		DataProvider.register(Boolean.class, Player.class, Pattern.compile("\\.has\\.", Pattern.CASE_INSENSITIVE), new VaultConditionalParser<Player>() {
			protected IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, IDataProvider<Double> amountDP, Matcher m, StringMatcher sm) {
				return new VaultConditional<Player>(Player.class, playerDP, amountDP) {
						protected Boolean get(Player player, double amount, EventData data) {
							return VaultSupport.economy.has(player.getName(), amount);
						}

						public String toString() {
							return startDP + ".has." + amountDP;
						}
					};
			}
		});
		

		DataProvider.register(Boolean.class, Player.class, Pattern.compile("\\.withdraw\\.", Pattern.CASE_INSENSITIVE), new VaultConditionalParser<Player>() {
			protected IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, IDataProvider<Double> amountDP, Matcher m, StringMatcher sm) {
				return new VaultConditional<Player>(Player.class, playerDP, amountDP) {
						protected Boolean get(Player player, double amount, EventData data) {
							VaultSupport.lastResponse = VaultSupport.economy.withdrawPlayer(player.getName(), amount);
							return VaultSupport.lastResponse.transactionSuccess();
						}

						public String toString() {
							return startDP + ".withdraw." + amountDP;
						}
					};
			}
		});

		DataProvider.register(Boolean.class, Player.class, Pattern.compile("\\.deposit\\.", Pattern.CASE_INSENSITIVE), new VaultConditionalParser<Player>() {
			protected IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, IDataProvider<Double> amountDP, Matcher m, StringMatcher sm) {
				return new VaultConditional<Player>(Player.class, playerDP, amountDP) {
						protected Boolean get(Player player, double amount, EventData data) {
							VaultSupport.lastResponse = VaultSupport.economy.depositPlayer(player.getName(), amount);
							return VaultSupport.lastResponse.transactionSuccess();
						}

						public String toString() {
							return startDP + ".deposit." + amountDP;
						}
					};
			}
		});
		
		
		
		
		///////////////////////////////// Banks /////////////////////////////////

		DataProvider.register(Boolean.class, Bank.class, Pattern.compile("\\.has\\.", Pattern.CASE_INSENSITIVE), new VaultConditionalParser<Bank>() {
			protected IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Bank> bankDP, IDataProvider<Double> amountDP, Matcher m, StringMatcher sm) {
				return new VaultConditional<Bank>(Bank.class, bankDP, amountDP) {
						protected Boolean get(Bank bank, double amount, EventData data) {
							VaultSupport.lastResponse = VaultSupport.economy.bankHas(bank.name, amount);
							return VaultSupport.lastResponse.transactionSuccess();
						}

						public String toString() {
							return startDP + ".has." + amountDP;
						}
					};
			}
		});
		

		DataProvider.register(Boolean.class, Bank.class, Pattern.compile("\\.withdraw\\.", Pattern.CASE_INSENSITIVE), new VaultConditionalParser<Bank>() {
			protected IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Bank> bankDP, IDataProvider<Double> amountDP, Matcher m, StringMatcher sm) {
				return new VaultConditional<Bank>(Bank.class, bankDP, amountDP) {
						protected Boolean get(Bank bank, double amount, EventData data) {
							VaultSupport.lastResponse = VaultSupport.economy.bankWithdraw(bank.name, amount);
							return VaultSupport.lastResponse.transactionSuccess();
						}

						public String toString() {
							return startDP + ".withdraw." + amountDP;
						}
					};
			}
		});

		DataProvider.register(Boolean.class, Bank.class, Pattern.compile("\\.deposit\\.", Pattern.CASE_INSENSITIVE), new VaultConditionalParser<Bank>() {
			protected IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Bank> bankDP, IDataProvider<Double> amountDP, Matcher m, StringMatcher sm) {
				return new VaultConditional<Bank>(Bank.class, bankDP, amountDP) {
						protected Boolean get(Bank bank, double amount, EventData data) {
							VaultSupport.lastResponse = VaultSupport.economy.bankDeposit(bank.name, amount);
							return VaultSupport.lastResponse.transactionSuccess();
						}

						public String toString() {
							return startDP + ".deposit." + amountDP;
						}
					};
			}
		});
	}
}
