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
import java.util.Set;
import java.util.TreeSet;

public class ContextElement {
	private Set<String> typeTags;
	private String sourceIdentifier;
	private String propertyIdentifier;
	private String value;
	private long timestamp;
	private boolean persistent;
	
	public ContextElement(String sourceIdentifier, String propertyIdentifier, String value, long timestamp, boolean persistent, Set<String> typeTags) {
		this.typeTags = new TreeSet<String>();
		if(typeTags!=null)
			this.typeTags.addAll(typeTags);
		this.sourceIdentifier = new String(sourceIdentifier);
		this.propertyIdentifier = new String(propertyIdentifier);
		this.value = new String(value);
		this.timestamp = timestamp;
		this.persistent = persistent;
	}
	
	public ContextElement(String sourceIdentifier, String propertyIdentifier, String value, long timestamp, boolean persistent, String...typeTags) {
		this(sourceIdentifier, propertyIdentifier, value, timestamp, persistent, Util.listToSet(typeTags));
	}
	
	public Set<String> getTypeTags() {
		return typeTags;
	}
	
	public boolean hasTypeTag(String tag) {
		return typeTags.contains(tag);
	}
	
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}
	
	public String getPropertyIdentifier() {
		return propertyIdentifier;
	}
	
	public String getValue() {
		return value;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public boolean isPersistent() {
		return persistent;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(Util.urlencode(sourceIdentifier));
		sb.append(" ");
		sb.append(Util.urlencode(propertyIdentifier));

		sb.append(" ");
		
		sb.append(Integer.toString(typeTags.size()));
		
		for(Iterator<String> ti = typeTags.iterator(); ti.hasNext();) {
			sb.append(" ");
			sb.append(Util.urlencode(ti.next()));
		}

		sb.append(" ");
		sb.append(Util.urlencode(value));
		
		sb.append(" ");
		sb.append(Long.toString(timestamp));
		
		if(persistent)
			sb.append(" P");
		
		return sb.toString();
	}
	
	/**
	 * Create a string representation without source and property
	 * identifiers but with a possible prefix
	 * @param prefix a prefix to append (will be followed by space); use empty string or null for no prefix
	 * @return same as toString but without source and property identifiers
	 */
	public String toShortString(String prefix) {
		StringBuilder sb = new StringBuilder();
		
		if(prefix != null && prefix.length() > 0) {
			sb.append(Util.urlencode(prefix));
			sb.append(" ");
		}
		
		sb.append(Integer.toString(typeTags.size()));
		
		for(Iterator<String> ti = typeTags.iterator(); ti.hasNext();) {
			sb.append(" ");
			sb.append(Util.urlencode(ti.next()));
		}

		sb.append(" ");
		sb.append(Util.urlencode(value));
		
		sb.append(" ");
		sb.append(Long.toString(timestamp));
		
		if(persistent)
			sb.append(" P");
		
		return sb.toString();
	}

	
	static public ContextElement fromString(String s) {
		return fromWords(Util.splitWS(s), 0);
	}
	
	static public ContextElement fromWords(String [] words, int offs) {
		int len = words.length - offs;
		
		if(len<5)
			return null;
		
		int wIndex = offs;

		String srcName = Util.urldecode(words[wIndex++]);
		String prpName = Util.urldecode(words[wIndex++]);
		
		int numTags = 0;
		
		try {
			numTags = Integer.parseInt(words[wIndex++]);
			if(numTags<0)
				throw new NumberFormatException();
			
		} catch(NumberFormatException nfe) {
			return null;
		}
		
		if(len < (5 + numTags))
			return null;
		
		Set<String> typeTags = new TreeSet<String>();
		
		for(int i=0; i<numTags; i++) {
			typeTags.add(Util.urldecode(words[wIndex++]));
		};
		
		String value = Util.urldecode(words[wIndex++]);
		
		long timestamp = 0L;
		
		try {
			timestamp = Long.parseLong(words[wIndex++]);
		} catch(NumberFormatException nfe) {
			return null;
		}
		
		boolean persistent = false;
		if(wIndex<words.length) {
			persistent = words[wIndex].startsWith("P") || words[wIndex].startsWith("p"); 
		}
		
		return new ContextElement(srcName, prpName, value, timestamp, persistent, typeTags);
	}
	
	static public ContextElement fromShortString(String srcName, String prpName, String s) {
		return fromShortWords(srcName, prpName, Util.splitWS(s), 0);
	}
	
	static public ContextElement fromShortWords(String srcName, String prpName, String [] words, int offs) {
		int len = words.length - offs;
		
		if(len<3)
			return null;
		
		int wIndex = offs;
		
		int numTags = 0;
		
		try {
			numTags = Integer.parseInt(words[wIndex++]);
			if(numTags<0)
				throw new NumberFormatException();
			
		} catch(NumberFormatException nfe) {
			return null;
		}
		
		if(len < (3 + numTags))
			return null;
		
		Set<String> typeTags = new TreeSet<String>();
		
		for(int i=0; i<numTags; i++) {
			typeTags.add(Util.urldecode(words[wIndex++]));
		};
		
		String value = Util.urldecode(words[wIndex++]);
		
		long timestamp = 0L;
		
		try {
			timestamp = Long.parseLong(words[wIndex++]);
		} catch(NumberFormatException nfe) {
			return null;
		}
		
		boolean persistent = false;
		if(wIndex<words.length) {
			persistent = words[wIndex].startsWith("P") || words[wIndex].startsWith("p"); 
		}
		
		return new ContextElement(srcName, prpName, value, timestamp, persistent, typeTags);
	}

}
