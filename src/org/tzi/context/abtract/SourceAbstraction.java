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
package org.tzi.context.abtract;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class SourceAbstraction extends AbstractItem {

	private ContextAbstraction context = null;
	
	private Map<Integer, PropertyAbstraction> properties = new TreeMap<Integer, PropertyAbstraction>();
	
	public SourceAbstraction(ContextAbstraction ctx, int id, String name) {
		super(ctx.getEnvironment(), id, name);
		if(ctx!=null)
			ctx.addSource(this);
	}
	
	public ContextAbstraction getContext() {
		return context;
	}
	
	public void setContext(ContextAbstraction ctx) {
		this.context = ctx;
	}
	
	public void addProperty(PropertyAbstraction prp) {
		properties.put(prp.getId(), prp);
		
		SourceAbstraction previousSource = prp.getSource();
		
		if(previousSource != null && previousSource != this) {
			previousSource.removeProperty(prp);
		}
		
		prp.setSource(this);
	}
	
	// intentionally package private
	void changePropertyId(int oldId, int newId) {
		PropertyAbstraction prp = properties.remove(oldId);
		if(prp!=null) {
			prp.updateId(newId);
			properties.put(newId, prp);
		}
	}
	
	public void removeProperty(PropertyAbstraction prp) {
		properties.remove(prp.getId());
		getEnvironment().signalPropertyRemove(prp);
	}
	
	public Collection<PropertyAbstraction> getProperties() {
		return properties.values();
	}
	
	public PropertyAbstraction getPropertyById(int id) {
		return properties.get(id);
	}
	
	public PropertyAbstraction getPropertyByName(String name) {
		if(name==null)
			return null;
		
		for(PropertyAbstraction prp : getProperties()) {
			if(name.equals(prp.getName()))
				return prp;
		}
		
		return null;
	}

}
