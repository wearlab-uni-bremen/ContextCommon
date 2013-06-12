/*
   Copyright 2007-2013 Hendrik Iben, University Bremen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.tzi.context.abstractenv;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ContextAbstraction extends AbstractItem {

	private Map<Integer, SourceAbstraction> sources = new TreeMap<Integer, SourceAbstraction>();
	
	public ContextAbstraction(Environment env, int id, String name) {
		super(env, id, name);
	}
	
	public void addSource(SourceAbstraction src) {
		sources.put(src.getId(), src);
		
		ContextAbstraction previousContext = src.getContext();
		
		if(previousContext != null && previousContext != this) {
			previousContext.removeSource(src);
		}
		
		src.setContext(this);
	}
	
	// intentionally package private
	void changeSourceId(int oldId, int newId) {
		SourceAbstraction src = sources.remove(oldId);
		if(src!=null) {
			src.updateId(newId);
			sources.put(newId, src);
		}
	}
	
	public void removeSource(SourceAbstraction src) {
		sources.remove(src.getId());
		getEnvironment().signalSourceRemoval(src);
	}
	
	public Collection<SourceAbstraction> getSources() {
		return sources.values();
	}
	
	public SourceAbstraction getSourceById(int id) {
		return sources.get(id);
	}
	
	public SourceAbstraction getSourceByName(String name) {
		if(name==null)
			return null;
		
		for(SourceAbstraction src : getSources()) {
			if(name.equals(src.getName()))
				return src;
		}
		
		return null;
	}

}
