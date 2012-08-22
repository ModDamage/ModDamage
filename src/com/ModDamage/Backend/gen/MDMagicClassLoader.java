package com.ModDamage.Backend.gen;


public class MDMagicClassLoader extends ClassLoader
{
	//Map<String, byte[]> myClassTemplates = new HashMap<String, byte[]>();
	
	
	
	public MDMagicClassLoader()
	{
		super();
	}

	public MDMagicClassLoader(ClassLoader parent)
	{
		super(parent);
	}

	public void addClassCode(String name, byte[] data)
	{
		//assert(!myClassTemplates.containsKey(name));
		
		//System.out.println("added class " + name);
		//myClassTemplates.put(name, data);
		defineClass(name, data, 0, data.length);
	}

	/*@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		if (myClassTemplates.containsKey(name))
		{
			byte[] data = myClassTemplates.remove(name);
			
			Class<?> cls = defineClass(name, data, 0, data.length);

			//System.out.println("loaded class " + name + ": " + cls);
			
			return cls;
		}
		
		return super.loadClass(name);
	}*/

}
