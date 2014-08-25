package com.ModDamage.Properties;

import com.ModDamage.Parsing.Property.Properties;

public class MiscProps
{
	public static void register() {
		Properties.register("length", String.class, "length");
		
		Properties.register("class", Object.class, "getClass");
		Properties.register("name", Class.class, "getName");
		Properties.register("simplename", Class.class, "getSimpleName");
	}
}
