package com.ModDamage.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Multimap<K, V> implements Iterable<Multimap.Entry<K, V>>
{
	private HashMap<K, ArrayList<V>> map;
	
	public Multimap()
	{
		map = new HashMap<K, ArrayList<V>>();
	}

	public void clear()
	{
		map.clear();
	}

	public boolean containsKey(K key)
	{
		return map.containsKey(key);
	}
	
	private ArrayList<V> getOrCreate(K key)
	{
		ArrayList<V> list = map.get(key);
		if (list == null)
		{
			list = new ArrayList<V>();
			map.put(key, list);
		}
		return list;
	}

	public List<V> getAll(K key)
	{
		return getOrCreate(key);
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public void put(K key, V value)
	{
		getOrCreate(key).add(value);
	}

	public <K2 extends K, V2 extends V> void putAll(Multimap<K2, V2> mm)
	{
		for (java.util.Map.Entry<K2, ArrayList<V2>> entry : mm.map.entrySet())
		{
			ArrayList<V> list = getOrCreate(entry.getKey());
			
			for (V2 v : entry.getValue())
			{
				list.add(v);
			}
		}
	}
	
	public interface Entry<K, V>
	{
		public K getKey();
		public V getValue();
	}

	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		final Iterator<java.util.Map.Entry<K, ArrayList<V>>> startMapEntryIterator = map.entrySet().iterator();
		K currentKey = null;
		Iterator<V> itemIterator = null;

		if (startMapEntryIterator.hasNext())
		{
			java.util.Map.Entry<K, ArrayList<V>> first = startMapEntryIterator.next();
			currentKey = first.getKey();
			itemIterator = first.getValue().iterator();
			
			while (!itemIterator.hasNext() && startMapEntryIterator.hasNext())
			{
				java.util.Map.Entry<K, ArrayList<V>> next = startMapEntryIterator.next();
				currentKey = next.getKey();
				itemIterator = next.getValue().iterator();
			}
		}
		
		final K startKey = currentKey;
		final Iterator<V> startItemIterator = itemIterator;
		
		Iterator<Entry<K, V>> it = new Iterator<Entry<K,V>>()
			{
				Iterator<java.util.Map.Entry<K, ArrayList<V>>> mapEntryIterator = startMapEntryIterator;
				Iterator<V> itemIterator = startItemIterator;
				K currentKey = startKey;
				K entryKey;
				V entryValue;
				
				Entry<K, V> entry = new Entry<K, V>()
						{
							@Override
							public K getKey()
							{
								return entryKey;
							}
		
							@Override
							public V getValue()
							{
								return entryValue;
							}
						};
				
				private void advance()
				{
					while (!itemIterator.hasNext() && mapEntryIterator.hasNext())
					{
						java.util.Map.Entry<K, ArrayList<V>> next = mapEntryIterator.next();
						currentKey = next.getKey();
						itemIterator = next.getValue().iterator();
					}
				}
				
				@Override
				public boolean hasNext()
				{
					return itemIterator != null && itemIterator.hasNext();
				}

				@Override
				public Entry<K, V> next()
				{
					entryKey = currentKey;
					entryValue = itemIterator.next();
					advance();
					return entry;
				}

				@Override
				public void remove()
				{
					throw new NotImplementedException(); // LazinessException?
				}
			};
			
		return it;
	}
}
