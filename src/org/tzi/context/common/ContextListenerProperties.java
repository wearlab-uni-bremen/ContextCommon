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
package org.tzi.context.common;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ContextListenerProperties implements ContextListenerInterface {
	
	public NewElementPolicy newSourcePolicy = NewElementPolicy.Always;
	public NewElementPolicy newPropertyPolicy = NewElementPolicy.Always;
	
	public Map<String, Map<String, Set<String>>> sourcePropTagMap = new TreeMap<String, Map<String,Set<String>>>();
	
	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#matchesSource(java.lang.String)
	 */
	public boolean matchesSource(String source) {
		return sourcePropTagMap.containsKey(source) || sourcePropTagMap.containsKey(Context.ALL_SOURCES);
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#notifyNewSource(java.lang.String)
	 */
	public boolean notifyNewSource(String source) {
		if(newSourcePolicy == NewElementPolicy.Always)
			return true;
		if(newSourcePolicy == NewElementPolicy.Never)
			return false;
		return matchesSource(source);
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#notifyNewProperty(java.lang.String, java.lang.String)
	 */
	public boolean notifyNewProperty(String source, String property) {
		if(newPropertyPolicy == NewElementPolicy.Always)
			return true;
		if(newPropertyPolicy == NewElementPolicy.Never)
			return false;
		return matchesSourceProperty(source, property);
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#matchesSourceProperty(java.lang.String, java.lang.String)
	 */
	public boolean matchesSourceProperty(String source, String property) {
		Map<String, Set<String>> propMap = sourcePropTagMap.get(source);
		if(propMap==null) {
			propMap = sourcePropTagMap.get(Context.ALL_SOURCES);
		}
		
		if(propMap!=null) {
			return propMap.containsKey(property) || propMap.containsKey(Context.ALL_PROPERTIES);
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#getNewSourcePolicy()
	 */
	public NewElementPolicy getNewSourcePolicy() {
		return newSourcePolicy;
	}

	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#getNewPropertyPolicy()
	 */
	public NewElementPolicy getNewPropertyPolicy() {
		return newPropertyPolicy;
	}

	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#setNewSourcePolicy(org.tzi.context.common.ContextListenerProperties.NewElementPolicy)
	 */
	public void setNewSourcePolicy(NewElementPolicy nep) {
		newSourcePolicy = (nep==null)?NewElementPolicy.Always:nep;
	}

	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#setNewPropertyPolicy(org.tzi.context.common.ContextListenerProperties.NewElementPolicy)
	 */
	public void setNewPropertyPolicy(NewElementPolicy nep) {
		newPropertyPolicy = (nep==null)?NewElementPolicy.Always:nep;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.context.common.ContextListenerInterface#matches(org.tzi.context.common.ContextElement)
	 */
	public boolean matches(ContextElement ce) {
		Map<String, Set<String>> propMap = sourcePropTagMap.get(ce.getSourceIdentifier());
		if(propMap==null) {
			propMap = sourcePropTagMap.get(Context.ALL_SOURCES);
		}
		
		if(propMap!=null) {
			Set<String> tags = propMap.get(ce.getPropertyIdentifier());
			if(tags==null) {
				tags = propMap.get(Context.ALL_PROPERTIES);
			}
			
			if(tags!=null) {
				if(tags.contains(Context.ALL_TAGS))
					return true;
				for(String tag : ce.getTypeTags()) {
					if(tags.contains(tag))
						return true;
				}
			}
		}
		
		return false;
	}
	
	public void merge(ContextListenerProperties clp) {
		if(clp==null)
			return;
		
		for(Map.Entry<String, Map<String, Set<String>>> clpSourceEntry : clp.sourcePropTagMap.entrySet()) {
			String clpSource = clpSourceEntry.getKey();
			Map<String, Set<String>> clpPropMap = clpSourceEntry.getValue();
			Map<String, Set<String>> propMap = sourcePropTagMap.get(clpSource);
			// no such source before
			if(propMap == null) {
				if(Context.debug) {
					System.out.println("Merge: My prop-map for " + clpSource + " is null");
					if(clpPropMap==null) {
						System.out.println("Sources map also...");
					} else {
						System.out.println("Sources is usable...");
					}
				}
				sourcePropTagMap.put(clpSource, clpPropMap);
				propMap = clpPropMap;
			} else { // source exists
				for(Map.Entry<String, Set<String>> clpPropEntry : clpPropMap.entrySet()) {
					String clpProp = clpPropEntry.getKey();
					Set<String> clpTags = clpPropEntry.getValue();
					Set<String> tags = propMap.get(clpProp);
					// no such property before
					if(tags == null) {
						propMap.put(clpProp, clpTags);
						tags = clpTags;
					} else { // property exists
						tags.addAll(clpTags);
					}
					
					if(tags.contains(Context.ALL_TAGS)) {
						tags.clear();
						tags.add(Context.ALL_TAGS);
					}
				}
				
			}
			// check for ALL_PROPERTIES
			if(propMap.containsKey(Context.ALL_PROPERTIES)) {
				Iterator<Map.Entry<String, Set<String>>> pi =
					propMap.entrySet().iterator();
				while(pi.hasNext()) {
					Map.Entry<String, Set<String>> pe = pi.next();
					if(pe.getKey() == Context.ALL_PROPERTIES)
						continue;
					// remove property when it catches all tags
					if(pe.getValue().contains(Context.ALL_TAGS))
						pi.remove();
				}
			}
		}
		
		// check for ALL_SOURCES
		if(sourcePropTagMap.containsKey(Context.ALL_SOURCES)) {
			Iterator<Map.Entry<String, Map<String, Set<String>>>> si =
				sourcePropTagMap.entrySet().iterator();
			while(si.hasNext()) {
				Map.Entry<String, Map<String, Set<String>>> se = si.next();
				if(se.getKey()==Context.ALL_SOURCES)
					continue;
				// remove source if it catches all properties and also all tags
				if(se.getValue().containsKey(Context.ALL_PROPERTIES) && se.getValue().containsValue(Context.ALL_TAGS))
					si.remove();
			}
		}
	}
}