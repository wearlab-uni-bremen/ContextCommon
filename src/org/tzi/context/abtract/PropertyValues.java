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

import java.util.Set;
import java.util.TreeSet;

import org.tzi.context.common.Util;

public class PropertyValues implements Cloneable {
	
	protected long timestamp = -1;
	protected String value = null;
	protected Set<String> tags = new TreeSet<String>();
	protected boolean persistent = false;
	
	public PropertyValues() { }
	
	public PropertyValues(long timestamp, String value, Set<String> tags, boolean persistent) {
		set(timestamp, value, tags, persistent);
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public String getValue() {
		return value;
	}
	
	public Set<String> getTags() {
		return tags;
	}
	
	public boolean isPersistent() {
		return persistent;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setTags(Set<String> tags) {
		this.tags.clear();
		if(tags!=null)
			this.tags.addAll(tags);
	}
	
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	
	public void set(long timestamp, String value, Set<String> tags, boolean persistent) {
		setTimestamp(timestamp);
		setValue(value);
		setTags(tags);
		setPersistent(persistent);
	}
	
	public Object clone() {
		return new PropertyValues(timestamp, value, tags, persistent);
	}
	
	public PropertyValues copy() {
		return (PropertyValues)clone();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(tags.size());
		
		for(String t : tags) {
			sb.append(' ');
			sb.append(Util.urlencode(t));
		}
		
		sb.append(' ');
		sb.append(Util.urldecode(value));
		
		sb.append(' ');
		sb.append(Long.toString(timestamp));
		
		if(persistent)
			sb.append(" P");
		
		return sb.toString();
	}
}
