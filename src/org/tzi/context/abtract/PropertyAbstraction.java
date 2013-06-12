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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PropertyAbstraction extends AbstractItem {

	private SourceAbstraction source = null;
	private PropertyValues v = new PropertyValues();
	
	private List<PropertyListener> listeners  = new LinkedList<PropertyListener>();
	private List<PropertyListener> listeners_tmp  = new LinkedList<PropertyListener>();

	public PropertyAbstraction(SourceAbstraction src, int id, String name) {
		super(src.getEnvironment(), id, name);
		if(src!=null) {
			src.addProperty(this);
		}
	}

	public PropertyAbstraction(SourceAbstraction src, int id, String name, PropertyValues v) {
		super(src.getEnvironment(), id, name);
		if(v!=null)
			setValues(v);
		if(src!=null) {
			src.addProperty(this);
		}
	}
	
	public PropertyAbstraction(SourceAbstraction src, int id, String name, long timestamp, String value, Set<String> tags, boolean persistent) {
		super(src.getEnvironment(), id, name);
		set(timestamp, value, tags, persistent);
		if(src!=null) {
			src.addProperty(this);
		}
	}	
	
	public SourceAbstraction getSource() {
		return source;
	}
	
	public void setSource(SourceAbstraction src) {
		this.source = src;
	}
	
	public PropertyValues getValues() {
		return v;
	}
	
	// convenience delegate methods
	
	public void setValues(PropertyValues v) {
		if(v==null)
			return;
		
		set(v.timestamp, v.value, v.tags, v.persistent);
	}
	
	public long getTimestamp() {
		return v.timestamp;
	}
	
	public String getValue() {
		return v.value;
	}
	
	public Set<String> getTags() {
		return v.tags;
	}
	
	public boolean isPersistent() {
		return v.persistent;
	}
	
	public void setTimestamp(long timestamp) {
		v.timestamp = timestamp;
	}
	
	public void setValue(String value) {
		v.value = value;
	}
	
	public void setTags(Set<String> tags) {
		v.tags.clear();
		if(tags!=null)
			v.tags.addAll(tags);
	}
	
	public void setPersistent(boolean persistent) {
		v.persistent = persistent;
	}
	
	public void set(long timestamp, String value, Set<String> tags, boolean persistent) {
		setTimestamp(timestamp);
		setValue(value);
		setTags(tags);
		setPersistent(persistent);
	}
	
	public void addPropertyListener(PropertyListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	public void removePropertyListener(PropertyListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	private class UpdateSignal implements Runnable {
		private PropertyValues snapshot;
		
		public UpdateSignal() {
			snapshot = v.copy();
		}
		
		public void run() {
			synchronized(listeners_tmp) {
				synchronized (listeners) {
					listeners_tmp.addAll(listeners);
				}
				
				for(PropertyListener pl : listeners_tmp) {
					pl.propertyChange(PropertyAbstraction.this, snapshot);
				}
				
				listeners_tmp.clear();
			}
		}
	}
	
	/**
	 * This method will signal a change of the property<br>
	 * A new thread is started for reporting and this method will return quickly
	 */
	public void signalUpdate() {
		getEnvironment().dispatchSignal(new UpdateSignal());
	}
}
