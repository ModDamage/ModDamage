package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Aliasing.MaterialAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class ItemMaterial extends Conditional 
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.material\\.([\\w,]+)", Pattern.CASE_INSENSITIVE);
	private final DataRef<ItemStack> itemRef;
	private final Collection<Material> materials;
	public ItemMaterial(DataRef<ItemStack> itemRef, Collection<Material> materials)
	{  
		this.itemRef = itemRef;
		this.materials = materials;
	}
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		ItemStack item = itemRef.get(data);
		return materials.contains(item.getType());
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}	
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public ItemMaterial getNew(Matcher matcher, EventInfo info)
		{
			DataRef<ItemStack> itemRef = info.get(ItemStack.class, matcher.group(1).toLowerCase());
			Collection<Material> matchedItems = MaterialAliaser.match(matcher.group(2));
			if(itemRef != null && !matchedItems.isEmpty())
				return new ItemMaterial(itemRef, matchedItems);
			return null;
		}
	}
}
