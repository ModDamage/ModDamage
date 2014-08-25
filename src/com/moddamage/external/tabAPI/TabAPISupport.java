package com.moddamage.external.tabAPI;

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
