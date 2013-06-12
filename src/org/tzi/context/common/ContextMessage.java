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

public class ContextMessage {
	public static enum Type { Context, SourceAdded, SourceRemoved, PropertyAdded, PropertyRemoved, ShortContext };
	
	public static Type typeFromString(String s) {
		if(s.equalsIgnoreCase(Protocol.CTX))
			return Type.Context;
		if(s.equalsIgnoreCase(Protocol.NEWSRC))
			return Type.SourceAdded;
		if(s.equalsIgnoreCase(Protocol.REMSRC))
			return Type.SourceRemoved;
		if(s.equalsIgnoreCase(Protocol.NEWPRP))
			return Type.PropertyAdded;
		if(s.equalsIgnoreCase(Protocol.REMPRP))
			return Type.PropertyRemoved;
		if(s.equalsIgnoreCase(Protocol.SCTX))
			return Type.ShortContext;
		
		return null;
	}
	
	public String typeToString(Type t)
	{
		if(t == Type.Context || t == Type.ShortContext) {
			if(shortFormat)
				return Protocol.SCTX;
			return Protocol.CTX;
		}
		if(t == Type.SourceAdded)
			return Protocol.NEWSRC;
		if(t == Type.SourceRemoved)
			return Protocol.REMSRC;
		if(t == Type.PropertyAdded)
			return Protocol.NEWPRP;
		if(t == Type.PropertyRemoved)
			return Protocol.REMPRP;
		
		return null;
	}
	
	private Type type;
	
	private String listenerId = null;
	private String contextName = null;
	private String contextInformation = null;
	
	private ContextElement ce = null;
	private String sourceName = null;
	private String propertyName = null;
	
	private boolean shortFormat = false;
	private String shortPrefix = null;
	
	public String getListenerId() {
		return listenerId;
	}
	
	public Type getType() {
		return type;
	}
	public ContextElement getCE() {
		return ce;
	}
	public String getContextName() {
		return contextName;
	}
	public String getContextInformation() {
		return contextInformation;
	}
	public String getSourceName() {
		return sourceName;
	}
	public String getPropertyName() {
		return propertyName;
	}
	
	public boolean isShortFormat() {
		return shortFormat;
	}
	
	public void setShortFormat(boolean shortFormat) {
		this.shortFormat = shortFormat;
	}
	
	public String getShortPrefix() {
		return shortPrefix;
	}
	
	public void setShortPrefix(String prefix) {
		this.shortPrefix = prefix;
	}
	
	public ContextMessage(String listenerId, String contextName, String contextInformation, ContextElement ce)
	{
		this.type = Type.Context;
		this.contextName = contextName;
		this.contextInformation = contextInformation;
		this.listenerId = listenerId;
		this.ce = ce;
	}

	public ContextMessage(String listenerId, String contextName, String contextInformation, String sourceName, String propertyName)
	{
		this.type = (propertyName!=null)?Type.SourceAdded:Type.SourceRemoved;
		this.contextName = contextName;
		this.contextInformation = contextInformation;
		this.listenerId = listenerId;
		this.sourceName = sourceName;
		this.propertyName = propertyName;
	}

	public ContextMessage(String listenerId, String contextName, String contextInformation, String sourceName, String propertyName, boolean added)
	{
		this.type = added?Type.PropertyAdded:Type.PropertyRemoved;
		this.contextName = contextName;
		this.contextInformation = contextInformation;
		this.listenerId = listenerId;
		this.sourceName = sourceName;
		this.propertyName = propertyName;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		String types = typeToString(type);
		
		if(types==null)
			return null;
		
		sb.append(types);

		sb.append(" ");
		
		if(listenerId.length()==0)
			throw new RuntimeException("ContextMessage without identifier!");
		
		sb.append(listenerId);
		
		sb.append(" ");
		
		if(!shortFormat) {
			sb.append(Util.urlencode(contextName.length()==0?"<unnamed>":contextName));

			if(contextInformation!=null)
			{
				sb.append(";");
				sb.append(Util.urlencode(contextInformation));
			}

			sb.append(" ");
		}
		
		switch(type)
		{
		case Context:
		case ShortContext:
			sb.append(shortFormat ? ce.toShortString(shortPrefix) : ce.toString());
			break;
		case SourceRemoved:
			sb.append(Util.urlencode(sourceName));
			break;
		case SourceAdded:
		case PropertyAdded:
		case PropertyRemoved:
			sb.append(Util.urlencode(sourceName));
			sb.append(" ");
			sb.append(Util.urlencode(propertyName));
			break;
		}
		
		return sb.toString();
	}
	
	public static ContextMessage fromString(String s) {
		return fromWords(Util.splitWS(s), 0);
	}
	
	public static ContextMessage fromWords(String [] words, int offs)
	{
		if((words.length - offs) < 4)
			return null;

		String types = words[offs];
		
		String identifier = words[offs+1];

		String contextNameInfo = words[offs+2];
		String contextName = null;
		String contextInformation = null;
		
		int infoMarker = contextNameInfo.indexOf(';');
		
		if(infoMarker!=-1)
		{
			contextName = Util.urldecode(contextNameInfo.substring(0, infoMarker));
			contextInformation = Util.urldecode(contextNameInfo.substring(infoMarker+1)); 
		}
		else
		{
			contextName = Util.urldecode(contextNameInfo);
		}
		
		Type t = typeFromString(types);
		
		if(t==null)
			return null;
		
		ContextElement ce;
		String sourceName, propertyName;
		
		switch(t)
		{
		case Context:
			ce = ContextElement.fromWords(words, offs+3);
			if(ce!=null) {
				return new ContextMessage(identifier, contextName, contextInformation, ce);
			}
			break;
		case ShortContext:
			String [] sinfo = new String [(words.length - (offs + 3))+2];
			sinfo[0] = "";
			sinfo[1] = words[offs+2];
			for(int i=3; i<words.length; i++) {
				sinfo[i-1] = words[i];
			}
			ce = ContextElement.fromWords(sinfo, 0);
			if(ce!=null) {
				ContextMessage cm = new ContextMessage(identifier, contextName, contextInformation, ce);
				cm.setShortPrefix(sinfo[1]);
				cm.setShortFormat(true);
				return cm;
			}
			break;
		case SourceRemoved:
			sourceName = Util.urldecode(words[offs+3]);
			return new ContextMessage(identifier, contextName, sourceName, null);
		case SourceAdded:
			if((words.length - offs) < 5)
				return null;
			sourceName = Util.urldecode(words[offs+3]);
			propertyName = Util.urldecode(words[offs+4]);
			return new ContextMessage(identifier, contextName, contextInformation, sourceName, propertyName);
		case PropertyAdded:
		case PropertyRemoved:
			if((words.length - offs) < 5)
				return null;
			sourceName = Util.urldecode(words[offs+3]);
			propertyName = Util.urldecode(words[offs+4]);
			return new ContextMessage(identifier, contextName, contextInformation, sourceName, propertyName, t == Type.PropertyAdded);
		}
		
		return null;
	}
}
