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

import java.util.HashSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class Context {
	public static final boolean debug = false;
	
	/**
	 * Tag for generated/computed information (e.g. application messages)
	 */
	public static final String T_ARTIFICIAL = "Artificial";
	/**
	 * Tag for information from the environment (e.g. time, temperature)
	 */
	public static final String T_ENVIRONMENT = "Environment";
	/**
	 * Tag for implicit derived information (e.g. light threshold reached)
	 */
	public static final String T_IMPLICIT_ACTION = "Implicit";
	/**
	 * Tag for information from a user (e.g. button press) 
	 */
	public static final String T_USER_ACTION = "User";
	/**
	 * Tag for virtual information (e.g. free space on media server)
	 */
	public static final String T_VIRTUAL = "Virtual";
	
	private String name = "Context";
	
	private String currentIdentifier = null;
	private Object currentKey = null;
	
	public Context(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static final String ALL_CONTEXTS = "<AllContexts>";
	public static final String ALL_SOURCES = "<AllSources>";
	public static final String ALL_PROPERTIES = "<AllProperties>";
	public static final String ALL_TAGS = "<AllTags>";
	
	public static ContextListenerInterface createCLPSources(String...sources) {
		ContextListenerProperties clp = new ContextListenerProperties();
		
		Set<String> sourceSet = Util.listToSet(sources);
		if(sourceSet.contains(ALL_SOURCES) && sourceSet.size()>1) {
			sourceSet.clear();
			sourceSet.add(ALL_SOURCES);
		}
		
		Map<String, Set<String>> props = new TreeMap<String, Set<String>>();
		props.put(ALL_PROPERTIES, Util.listToSet(ALL_TAGS));

		// give new copy to each source to avoid problems when merging
		for(String source : sourceSet)
			clp.sourcePropTagMap.put(source, new TreeMap<String, Set<String>>(props));
		
		return clp;
	}

	public static ContextListenerInterface createCLPTags(String...tags) {
		ContextListenerProperties clp = new ContextListenerProperties();
		
		Map<String, Set<String>> props = new TreeMap<String, Set<String>>();
		Set<String> tagSet = Util.listToSet(tags);
		if(tagSet.contains(ALL_TAGS) && tagSet.size()>1) {
			tagSet.clear();
			tagSet.add(ALL_TAGS);
		}
		props.put(ALL_PROPERTIES, tagSet);

		clp.sourcePropTagMap.put(ALL_SOURCES, props);
		
		return clp;
	}
	
	public static ContextListenerInterface createCLPFor(String source, String property, String...tags) {
		ContextListenerProperties clp = new ContextListenerProperties();
		
		Map<String, Set<String>> props = new TreeMap<String, Set<String>>();
		Set<String> tagSet = Util.listToSet(tags);
		if(tagSet.contains(ALL_TAGS) && tagSet.size()>1) {
			tagSet.clear();
			tagSet.add(ALL_TAGS);
		}
		props.put(property, tagSet);

		clp.sourcePropTagMap.put(source, props);
		
		return clp;
	}

	public static ContextListenerInterface createCLPAll() {
		ContextListenerProperties clp = new ContextListenerProperties();
		
		Map<String, Set<String>> props = new TreeMap<String, Set<String>>();
		props.put(ALL_PROPERTIES, Util.listToSet(ALL_TAGS));

		clp.sourcePropTagMap.put(ALL_SOURCES, props);
		
		return clp;
	}
	
	public static ContextListenerInterface createCLP(Map<String, Map<String, Set<String>>> m) {
		ContextListenerProperties clp = new ContextListenerProperties();
		ContextListenerProperties clp_m = new ContextListenerProperties();
		clp_m.sourcePropTagMap.putAll(m);
		// merging performs additional checks that will handle problematic
		// entries in the map.
		clp.merge(clp_m);
		return clp;
	}
	
	/**
	 * Known ContextElementS
	 */
	private List<ContextElement> ceList = new LinkedList<ContextElement>();

	/**
	 * Mapping from Source to map from Property to CE
	 */
	private Map<String, Map<String, ContextElement>> sPCEMapMap = new TreeMap<String, Map<String, ContextElement>>();
	
	private List<ContextListener> listener =
		new Vector<ContextListener>();
	
	public void addContextListener(ContextListener cl) {
		listener.add(cl);
	}
	
	public void removeContextListener(ContextListener cl) {
		listener.remove(cl);
	}
	
	public List<ContextListener> getContextListeners() {
		return listener;
	}
	
 	/**
	 * Merge a piece of context information into the context</br>
	 * @param ce
	 */
	public void mergeContextElement(ContextElement ce) {
		boolean newSource = false;
		boolean newProperty = false;
		
		Map<String, ContextElement> pCEMap = sPCEMapMap.get(ce.getSourceIdentifier());
		
		if(pCEMap==null) {
			pCEMap = new TreeMap<String, ContextElement>();
			sPCEMapMap.put(ce.getSourceIdentifier(), pCEMap);
			newSource = true;
		} else {
			ContextElement oldce = pCEMap.get(ce.getPropertyIdentifier());
			if(oldce!=null) {
				ceList.remove(oldce);
			} else {
				newProperty = true;
			}
		}
		
		ceList.add(ce);
		pCEMap.put(ce.getPropertyIdentifier(), ce);

		// create copy to avoid concurrent modification
		Set<ContextListener> currentListener =
			new HashSet<ContextListener>(listener);
		
		if(newSource) {
			for(ContextListener cl : currentListener) {
				if(cl.getProperties()==null || cl.getProperties().notifyNewSource(ce.getSourceIdentifier()))
					cl.sourceAdded(this, ce.getSourceIdentifier(), ce.getPropertyIdentifier());
			}
		}

		if(newProperty) {
			for(ContextListener cl : currentListener) {
				if(cl.getProperties()==null || cl.getProperties().notifyNewProperty(ce.getSourceIdentifier(), ce.getSourceIdentifier()))
					cl.propertyAdded(this, ce.getSourceIdentifier(), ce.getPropertyIdentifier());
			}
		}

		for(ContextListener cl : currentListener) {
			if(cl.getProperties()==null || cl.getProperties().matches(ce))
				cl.processContext(this, ce);
		}
	}
	
	/**
	 * Get all context information for a given source.
	 * @param source the source in question
	 * @return all context elements for the source
	 */
	public List<ContextElement> getSourceContext(String source) {
		Map<String, ContextElement> pCEMap = sPCEMapMap.get(source);
		
		if(pCEMap==null)
			return null;
		
		LinkedList<ContextElement> r = new LinkedList<ContextElement>();
		return r;
	}

	/**
	 * Retrieve the latest context element for a given source
	 * and property. 
	 * @param source
	 * @param property
	 * @return
	 */
	public ContextElement getSourceProperty(String source, String property) {
		Map<String, ContextElement> pCEMap = sPCEMapMap.get(source);

		if(pCEMap==null)
			return null;
		
		return pCEMap.get(property);
	}
	
	/**
	 * Removes a source (with all properties) from the context</br>
	 * All listeners for this source will be notified by
	 * {@link ContextListener#sourceRemoved(Context, String)} after removing.
	 * @param source source to remove
	 */
	public void removeSource(String source) {
		if(sPCEMapMap.remove(source)!=null) {

			for(ContextListener cl : listener) {
				if(cl.getProperties()==null || cl.getProperties().matchesSource(source))
					cl.sourceRemoved(this, source);
			}
		}
	}
	
	/**
	 * Removes a property from the context</br>
	 * All listeners for this property will be notified
	 * by {@link ContextListener#propertyRemoved(Context, String, String)} 
	 * after removing.
	 * @param source source having the property
	 * @param property the property to remove
	 */
	public void removeSourceProperty(String source, String property) {
		Map<String, ContextElement> pCEMap = sPCEMapMap.get(source);
		
		if(pCEMap==null)
			return;
		
		pCEMap.remove(property);
		
		for(ContextListener cl : listener) {
			if(cl.getProperties()==null || cl.getProperties().matchesSourceProperty(source, property))
				cl.propertyRemoved(this, source, property);
		}
	}
	
	public Set<String> getSources() {
		return new TreeSet<String>(sPCEMapMap.keySet());
	}
	
	/**
	 * @return all context elements currently present
	 */
	public List<ContextElement> getContextElements() {
		return ceList;
	}
	
	/**
	 * prints the current state to stdout
	 */
	public void printContext() {
		System.out.println("Current State for Context \"" + name + "\"");
		if(sPCEMapMap.isEmpty()) {
			System.out.println("<no context information present>");
		}
		for(Map.Entry<String, Map<String, ContextElement>> sourceEntry : sPCEMapMap.entrySet()) {
			for(ContextElement ce : sourceEntry.getValue().values()) {
				System.out.println(ce);
			}
		}
	}
	
	public void setCurrentIdentifier(String id) {
		currentIdentifier = id;
	}
	
	public void setCurrentKey(Object key) {
		currentKey = key;
	}
	
	public String getCurrentIdentifier() {
		return currentIdentifier;
	}
	
	public Object getCurrentKey() {
		return currentKey;
	}
}
