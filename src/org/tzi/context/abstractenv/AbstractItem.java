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

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractItem {
	private Environment env;
	
	private int id;
	private String name;
	
	private Map<Object, Object> _data;
	
	protected AbstractItem(Environment env, int id, String name) {
		this.env = env;
		this.id = id;
		this.name = name;
	}
	
	public Environment getEnvironment() {
		return env;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getData(Object key) {
		if(key==null)
			return null;
		
		if(_data==null)
			return null;
		
		return _data.get(key);
	}
	
	protected void setId(int id) {
		this.id = id;
	}
	
	protected void updateId(int newId) {
		int oldId = getId();
		setId(newId);
		Environment env = getEnvironment();
		if(env!=null)
			env.knownItemIdChange(oldId);
	}
	
	public Object setData(Object key, Object value) {
		if(key == null)
			throw new IllegalArgumentException("keys may not be null!");
		
		if(_data == null)
			_data = new HashMap<Object, Object>();
		
		if(value==null) {
			return _data.remove(key);
		}
		
		return _data.put(key, value);
	}
	
}
 