package com.ModDamage.External.Vault;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.FunctionParser;
import com.ModDamage.Parsing.IDataProvider;

public class VaultSupport
{
	public static Economy economy;
	public static EconomyResponse lastResponse;
	
	
	public static void register()
	{
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider == null) {
        	return;
        }
        
        economy = economyProvider.getProvider();

		DataProvider.register(Economy.class, Pattern.compile("economy", Pattern.CASE_INSENSITIVE), 
				new BaseDataParser<Economy>() {
					public IDataProvider<Economy> parse(EventInfo info, Matcher m, StringMatcher sm) {
						return sm.acceptIf(new IDataProvider<Economy>() {
							public Economy get(EventData data) {
								return economy;
							}
							
							public Class<? extends Economy> provides() {
								return Economy.class;
							}
							
							public String toString() {
								return "economy";
							}
						});
					}
				});
		
		DataProvider.register(String.class, Economy.class, Pattern.compile("_format", Pattern.CASE_INSENSITIVE), new FunctionParser<String, Economy>(Double.class) {
			@SuppressWarnings("rawtypes")
			protected IDataProvider<String> makeProvider(IDataProvider<Economy> economyDP, IDataProvider[] arguments) {
				@SuppressWarnings("unchecked")
				final IDataProvider<Double> balanceDP = (IDataProvider<Double>) arguments[0];
				
				return new IDataProvider<String>() {
						public String get(EventData data) throws BailException {
							Double balance = balanceDP.get(data);
							if (balance == null) return null;
							
							return economy.format(balance);
						}
						
						public Class<? extends String> provides() {
							return String.class;
						}
					};
			}
		});
		

		DataProvider.register(Bank.class, Object.class, Pattern.compile("bank"), new FunctionParser<Bank, Object>(String.class) {
			@SuppressWarnings("rawtypes")
			protected IDataProvider<Bank> makeProvider(IDataProvider<Object> startDP, IDataProvider[] arguments) {
				@SuppressWarnings("unchecked")
				final IDataProvider<String> nameDP = (IDataProvider<String>) arguments[0];
				
				return new IDataProvider<Bank>() {
						public Bank get(EventData data) throws BailException {
							String name = nameDP.get(data);
							if (name == null) return null;
							
							return new Bank(name);
						}
						
						public Class<? extends Bank> provides() {
							return Bank.class;
						}
					};
			}
		});
		
		VaultProperties.register();
		VaultConditionals.register();
	}
}
