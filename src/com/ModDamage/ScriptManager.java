package com.ModDamage;

import static java.util.Collections.*;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ModDamage.Backend.Nullable;

public class ScriptManager {
	
	public static enum LoadMethod {
		PRIORITY_PARSE, ENABLED_SETTING, MASTER_LIST; 
	}
	
	private PluginConfiguration globalConfig;
	private List<String> scriptOrderCached = new LinkedList<String>();
	private Map<String, BaseConfig> scripts = new HashMap<String, BaseConfig>();
	
	protected List<String> _getScriptNames() {
		return scriptOrderCached;
	}
	
	public PluginConfiguration getMasterConfig() {
		return globalConfig;
	}
	
	public void add(BaseConfig config) {
		if (config instanceof PluginConfiguration)
			if (globalConfig == null)
				globalConfig = (PluginConfiguration) config;
		
		if (config instanceof MDScript) {
			MDScript script = (MDScript)config;
			scripts.put(script.getName(), script);
			update();
		} else
			throw new IllegalArgumentException("The script manager cannot handle stuff other than PluginConfiguration and MDScript objects." + System.lineSeparator() + "The supplied object is " + config.getClass().getSimpleName());
	}
	
	public BaseConfig get(String name) {
		if (scripts.containsKey(name))
			return scripts.get(name);
		else
			return null;
	}
	
	protected List<BaseConfig> getAllScripts() {
		List<BaseConfig> ret = new ArrayList<BaseConfig>(scripts.values());
		Collections.sort(ret);
		
		return ret;
	}
	
	public List<BaseConfig> getDisabledScripts() {
		List<BaseConfig> ret = getAllScripts();
		for (Iterator<BaseConfig> iterator = ret.iterator(); iterator.hasNext();) {
			BaseConfig baseConfig = iterator.next();
			if (baseConfig.isEnabled())
				iterator.remove();
		}
		
		return ret;
	}
	
	public List<BaseConfig> getEnabledScripts() {
		List<BaseConfig> ret = getAllScripts();
		for (Iterator<BaseConfig> iterator = ret.iterator(); iterator.hasNext();) {
			BaseConfig baseConfig = iterator.next();
			if (!baseConfig.isEnabled())
				iterator.remove();
		}
		
		Collections.sort(ret);
		return ret;
	}
	
	public List<String> getScriptNames() {
		return unmodifiableList(scriptOrderCached);
	}
	
	public boolean reload(BaseConfig config, boolean reloadAll) {
		if (config == null)
			return false;
		
		return config.reload(reloadAll);
	}
	
	public boolean reload(boolean reloadAll) {
		boolean ret = true;
		for (String name : _getScriptNames())
			ret = (reload(name, reloadAll) == true && ret == true);
		
		return ret;
	}
	
	public boolean reload(String name, boolean reloadAll) {
		return reload(get(name), reloadAll);
	}
	
	public void remove(BaseConfig config) {
		if (config == null)
			return;
		
		if (config instanceof PluginConfiguration)
			throw new IllegalArgumentException("Cannot remove the primary config!");
		
		_getScriptNames().remove(config.getName());
		scripts.remove(config.getName());
	}
	
	
	public void remove(String name) {
		remove(get(name));
	}
	
	private File[] listFilesRecursively(@Nullable FileFilter filter) {
		return listFilesRecursively(filter, ModDamage.getInstance().getDataFolder());
	}
	
	private File[] listFilesRecursively(FileFilter filter, File file) {
		
		List<File> files = new ArrayList<File>();
		addFilesRecursively(filter, files, file);
					
		return files.toArray(new File[files.size()]);
	}
	
	private void addFilesRecursively(FileFilter filter, List<File> files, File file) {
		if (file.isDirectory()) {
			for (File f : file.listFiles())
				if (f.isDirectory())
					addFilesRecursively(filter, files, f);
				else if (filter == null)
					files.add(f);
				else if (filter.accept(f))
					files.add(f);
		} else
			files.add(file);
	}
	
	public void scanForScripts() {
		//TODO: Finish
	}
	
	private void update() {
		scriptOrderCached.clear();
		for (BaseConfig co : getEnabledScripts())
			scriptOrderCached.add(co.getName());
	}
}
