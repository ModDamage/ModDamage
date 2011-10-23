package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.mcMMO;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;

public enum McMMOAbility
{
	Berserk
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getBerserkMode();
		}
	},
	GigaDrillBreaker
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getGigaDrillBreakerMode(); 
		}
	},
	God
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getGodMode(); 
		}
	},
	GreenTerra
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getGreenTerraMode(); 
		}
	},
	SkullSplitter
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getSkullSplitterMode(); 
		}
	},
	SerratedStrikes
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getSerratedStrikesMode();
		}
	},
	SuperBreaker
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getSuperBreakerMode(); 
		}
	},
	SwordsPreparation
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getSwordsPreparationMode();
		}
	},
	TreeFeller
	{
		@Override
		public boolean isActivated(mcMMO mcMMOplugin, Player player)
		{
			return mcMMOplugin.getPlayerProfile(player).getTreeFellerMode(); 
		}
	};

	abstract public boolean isActivated(mcMMO mcMMOplugin, Player player);
}
