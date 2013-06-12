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

import javax.script.Invocable;
import javax.script.ScriptException;

public class ContextListenerScript implements ContextListenerInterface {
	
	private Invocable engine;
	
	public ContextListenerScript(Invocable engine) {
		this.engine = engine;
	}
	
	private NewElementPolicy resultToPolicy(Object r) {
		if(r instanceof NewElementPolicy) {
			return (NewElementPolicy)r;
		}
		if(r instanceof String) {
			String s = (String)r;
			
			if("always".equalsIgnoreCase(s))
				return NewElementPolicy.Always;
			if("match".equalsIgnoreCase(s))
				return NewElementPolicy.IfMatch;
			// never
		}
		
		return NewElementPolicy.Never;
	}
	
	private boolean resultToBoolean(Object r) {
		if(r instanceof Boolean) {
			return (Boolean)r;
		}
		if(r instanceof String) {
			return Boolean.valueOf((String)r);
		}
		
		return false;
	}

	@Override
	public NewElementPolicy getNewPropertyPolicy() {
		try {
			return resultToPolicy(engine.invokeFunction("getNewPropertyPolicy"));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return NewElementPolicy.Never;
	}

	@Override
	public NewElementPolicy getNewSourcePolicy() {
		try {
			return resultToPolicy(engine.invokeFunction("getNewSourcePolicy"));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return NewElementPolicy.Never;
	}

	@Override
	public boolean matches(ContextElement ce) {
		String [] tags = new String [ce.getTypeTags().size()];
		int index = 0;
		for(String tag : ce.getTypeTags()) {
			tags[index++] = tag;
		}
		
		try {
			return resultToBoolean(engine.invokeFunction("matches", tags, ce.getSourceIdentifier(), ce.getPropertyIdentifier(), ce.getValue(), ce.getTimestamp(), ce.isPersistent()));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean matchesSource(String source) {
		try {
			return resultToBoolean(engine.invokeFunction("matchesSource", source));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean matchesSourceProperty(String source, String property) {
		try {
			return resultToBoolean(engine.invokeFunction("matchesSourceProperty", source, property));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean notifyNewProperty(String source, String property) {
		try {
			return resultToBoolean(engine.invokeFunction("notifyNewProperty", source, property));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean notifyNewSource(String source) {
		try {
			return resultToBoolean(engine.invokeFunction("notifyNewSource", source));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String policyToString(NewElementPolicy nep) {
		switch(nep) {
		case Never: return "never";
		case Always: return "always";
		case IfMatch: return "match";
		}
		return "never";
	}

	@Override
	public void setNewPropertyPolicy(NewElementPolicy nep) {
		try {
			engine.invokeFunction("setNewPropertyPolicy", policyToString(nep));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setNewSourcePolicy(NewElementPolicy nep) {
		try {
			engine.invokeFunction("setNewSourcePolicy", policyToString(nep));
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
