package com.ModDamage.Alias;

import java.util.Map;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.BaseConfig.LoadState;
import com.ModDamage.Utils;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.Backend.ScriptLineHandler;

public enum AliasManager
{
	Armor(ArmorAliaser.class),
	Biome(BiomeAliaser.class),
	Command(CommandAliaser.class),
	Enchantment(EnchantmentAliaser.class),
	Item(ItemAliaser.class),
	Group(GroupAliaser.class),
	Material(MaterialAliaser.class),
	Message(MessageAliaser.class),
	Region(RegionAliaser.class),
	Routine(RoutineAliaser.class),
	Type(TypeAliaser.class),
	TypeName(TypeNameAliaser.class),
	World(WorldAliaser.class);
	
	private static Map<String, AliasManager> typeMap = Utils.getTypeMapForEnum(AliasManager.class, true);
	
	private static LoadState state = LoadState.NOT_LOADED;
	public static LoadState getState() { return state; }
	
	private final Class<? extends Aliaser<?, ?>> aliaserClass;
	private AliasManager(Class<? extends Aliaser<?, ?>> aliaserClass) { this.aliaserClass = aliaserClass; }
	
	private Aliaser<?, ?> getAliaser()
	{
		try { return (Aliaser<?, ?>) aliaserClass.getField("aliaser").get(null); }
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (SecurityException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		catch (NoSuchFieldException e) { e.printStackTrace(); }
		return null;
	}
	
	public static final Pattern aliasPattern = Pattern.compile("_\\w+");
	


	public static ScriptLineHandler getLineHandler()
	{
		return new ScriptLineHandler()
			{
				@Override
				public ScriptLineHandler handleLine(ScriptLine line, boolean hasChildren)
				{
					AliasManager a = typeMap.get(line.line.toUpperCase());
					if (a == null) {
						LogUtil.error(line, "Illegal alias name: \""+line.line+"\"");
						return null;
					}
					return a.getAliaser();
				}
				
				@Override
				public void done()
				{
				}
			};
	}

	
	public LoadState getSpecificLoadState(){ return getAliaser().loadState; }
}
