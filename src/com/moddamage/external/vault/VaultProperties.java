package com.moddamage.external.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.entity.Player;

import com.moddamage.eventinfo.EventData;
import com.moddamage.parsing.property.Properties;
import com.moddamage.parsing.property.Property;

public class VaultProperties
{
	public static void register()
	{
		Properties.register("name", Economy.class, "getName");
		Properties.register("currencyNameSingular", Economy.class, "currencyNameSingular");
		Properties.register("currencyNamePlural", Economy.class, "currencyNamePlural");
		Properties.register("fractionalDigits", Economy.class, "fractionalDigits");
		

		Properties.register(new Property<Double, Economy>("lastAmount", Double.class, Economy.class) {
			public Double get(Economy economy, EventData data) {
				return VaultSupport.lastResponse.amount;
			}
		});
		
		Properties.register(new Property<Double, Economy>("lastBalance", Double.class, Economy.class) {
			public Double get(Economy economy, EventData data) {
				return VaultSupport.lastResponse.balance;
			}
		});
		
		Properties.register(new Property<String, Economy>("errorMessage", String.class, Economy.class) {
			public String get(Economy economy, EventData data) {
				return VaultSupport.lastResponse.errorMessage;
			}
		});
		
		Properties.register(new Property<ResponseType, Economy>("lastResult", ResponseType.class, Economy.class) {
			public ResponseType get(Economy economy, EventData data) {
				return VaultSupport.lastResponse.type;
			}
		});
		
		

		Properties.register(new Property<Double, Player>("balance", Double.class, Player.class) {
			public Double get(Player player, EventData data) {
				return VaultSupport.economy.getBalance(player.getName());
			}
		});
		
		
		
		Properties.register(new Property<Double, Bank>("balance", Double.class, Bank.class) {
			public Double get(Bank bank, EventData data) {
				VaultSupport.lastResponse = VaultSupport.economy.bankBalance(bank.name);
				return VaultSupport.lastResponse.balance;
			}
		});
	}
}
