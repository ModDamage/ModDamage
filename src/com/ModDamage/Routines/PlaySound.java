package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class PlaySound extends Routine
{
    private final IDataProvider<Player> playerDP;
	private final IDataProvider<Location> locDP;
	private final Sound sound;
	private final IDataProvider<Integer> volumeDP;
	private final IDataProvider<Integer> pitchDP;

	protected PlaySound(ScriptLine scriptLine, IDataProvider<Player> playerDP, IDataProvider<Location> locDP, Sound sound, IDataProvider<Integer> volumeDP, IDataProvider<Integer> pitchDP)
	{
		super(scriptLine);
        this.playerDP = playerDP;
		this.locDP = locDP;
        this.sound = sound;
        this.volumeDP = volumeDP;
        this.pitchDP = pitchDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
        Player player = playerDP.get(data);
		Location loc = locDP.get(data);
		Integer volume = volumeDP.get(data);
		Integer pitch = pitchDP.get(data);
		if (player == null || loc == null || volume == null || pitch == null) return;

        player.playSound(loc, sound, volume / 100.0f,  pitch / 100.0f);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)\\.playsound\\.(\\w+)(?:\\.at\\.([^\\.]+))?\\.([^\\.]+)\\.([^\\.]+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
            IDataProvider<Player> playerDP = DataProvider.parse(info, Player.class, matcher.group(1));
            if (playerDP == null) return null;
            String locStr = matcher.group(3);
            if (locStr == null) locStr = matcher.group(1);
			IDataProvider<Location> locDP = DataProvider.parse(info, Location.class, locStr);
			if (locDP == null) return null;
			
			Sound sound;
			try
			{
				sound = Sound.valueOf(matcher.group(2).toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad sound: \""+matcher.group(2)+"\"");
				return null;
			}

			
			IDataProvider<Integer> volumeDP = DataProvider.parse(info, Integer.class, matcher.group(4));
            IDataProvider<Integer> pitchDP = DataProvider.parse(info, Integer.class, matcher.group(5));
            if (volumeDP == null || pitchDP == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "PlaySound: " + sound + " for:" + playerDP + " at:" + locDP + " volume:" + volumeDP + " pitch:" + pitchDP);
			return new RoutineBuilder(new PlaySound(scriptLine, playerDP, locDP, sound, volumeDP, pitchDP));
		}
	}
}
