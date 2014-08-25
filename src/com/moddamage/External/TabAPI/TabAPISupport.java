package com.ModDamage.External.TabAPI;

public class TabAPISupport
{
	public static void register()
	{
		ClearTab.registerRoutine();
		SetTabPriority.registerNested();
		SetTabString.registerNested();
		UpdateTab.registerRoutine();
	}
}
