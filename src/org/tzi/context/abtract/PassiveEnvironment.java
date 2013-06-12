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

/**
 * The PassiveEnvironment is meant to be used to mirror another Environment, e.g.
 * to create a consistent mapping between a server and a client.<br>
 * Unlike the Environment class the PassiveEnvironment does not create IDs but only
 * uses supplied IDs with special methods for injecting items.<br>
 * It is the developers responsibility to make sure supplied IDs do not conflict!<br>
 * Calling any method that would create an ID will result in a RuntimeException.<br>
 * @author hendrik
 *
 */
public class PassiveEnvironment extends Environment {
	
	public ContextAbstraction injectContext(int id, String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");
		
		ContextAbstraction c = getContextById(id);
		if(c != null)
			return c;
		
		ContextAbstraction ctx = new ContextAbstraction(this, id, name);
		
		addContext(ctx);
		sequentialExcecution.execute(new ContextCreationSignal(ctx));

		return ctx;
	}
	
	public void changeId(int oldId, int newId) {
		AbstractItem ai = getKnownItembyId(oldId);
		if(ai==null)
			return;
		removeKnownItem(ai);
		if(ai instanceof ContextAbstraction) {
			contexts.remove(ai.getId());
		}
		ai.setId(newId);
		addKnownItem(ai);
		if(ai instanceof ContextAbstraction) {
			contexts.put(newId, (ContextAbstraction)ai);
		}
	}
	
	public void changePropertyId(PropertyAbstraction pa, int newId) {
		removeKnownItem(pa);
		pa.setId(newId);
		addKnownItem(pa);
		
		SourceAbstraction sa = pa.getSource();
		
		if(sa==null)
			return;
		
		
	}
	
	public SourceAbstraction injectSource(ContextAbstraction ctx, int id, String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");
		
		// possible transformation from original environment
		ctx = getContextById(ctx.getId());
		
		SourceAbstraction s = ctx.getSourceByName(name);

		if(s!=null)
			return s;
		
		SourceAbstraction src = addKnownItem(new SourceAbstraction(ctx, id, name));
		sequentialExcecution.execute(new SourceCreationSignal(src));

		return src;
	}
	
	public PropertyAbstraction injectProperty(SourceAbstraction src, int id, String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");
		
		// possible transformation from original environment
		src = getSourceById(src.getId());

		PropertyAbstraction p = src.getPropertyByName(name);
		
		if(p!=null)
			return p;

		PropertyAbstraction prp = addKnownItem(new PropertyAbstraction(src, id, name));
		sequentialExcecution.execute(new PropertyCreationSignal(prp));
		
		return prp;
	}
	
	public PropertyAbstraction injectProperty(SourceAbstraction src, int id, String name, long timestamp, String value, Set<String> tags, boolean persistent) {
		if(name==null)
			throw new RuntimeException("name may not be null!");

		// possible transformation from original environment
		src = getSourceById(src.getId());
		
		PropertyAbstraction p = src.getPropertyByName(name);
		
		if(p!=null) {
			p.set(timestamp, value, tags, persistent);
			p.signalUpdate();
			return p;
		}

		PropertyAbstraction prp = addKnownItem(new PropertyAbstraction(src, id, name));
		sequentialExcecution.execute(new PropertyCreationSignal(prp));
		
		return prp;
	}
	
	public PropertyAbstraction injectProperty(SourceAbstraction src, int id, String name, PropertyValues v) {
		if(name==null)
			throw new RuntimeException("name may not be null!");

		// possible transformation from original environment
		src = getSourceById(src.getId());

		PropertyAbstraction p = src.getPropertyByName(name);
		
		if(p!=null) {
			p.setValues(v);
			p.signalUpdate();
			return p;
		}

		PropertyAbstraction prp = addKnownItem(new PropertyAbstraction(src, id, name));
		sequentialExcecution.execute(new PropertyCreationSignal(prp));
		
		return prp;
	}
	
	public void passiveRemoveProperty(PropertyAbstraction prp) {
		if(prp == null)
			return;

		prp = getPropertyById(prp.getId());
		
		if(prp == null)
			return;
		
		prp.getSource().removeProperty(prp);
	}
	
	public void passiveRemoveSource(SourceAbstraction src) {
		if(src == null)
			return;
		
		src = getSourceById(src.getId());
		
		if(src == null)
			return;
		
		src.getContext().removeSource(src);
	}
	
	public void passiveRemoveContext(ContextAbstraction ctx) {
		if(ctx == null)
			return;
		
		ctx = getContextById(ctx.getId());
		
		if(ctx == null)
			return;

		removeContext(ctx);
	}
	
	public void passivePropertyUpdate(PropertyAbstraction prp, PropertyValues v) {
		if(prp == null)
			return;
		
		prp = getPropertyById(prp.getId());
		
		if(prp == null)
			return;
		
		prp.setValues(v);
		prp.signalUpdate();
	}

	private void noSupportInPassive() {
		throw new RuntimeException("Method not supported in passive environment!");
	}
	
	@Override
	public ContextAbstraction createContext(String name) {
		noSupportInPassive();
		return null;
	}

	@Override
	public PropertyAbstraction createProperty(SourceAbstraction src,
			String name, long timestamp, String value, Set<String> tags,
			boolean persistent) {
		noSupportInPassive();
		return null;
	}

	@Override
	public PropertyAbstraction createProperty(SourceAbstraction src,
			String name, PropertyValues v) {
		noSupportInPassive();
		return null;
	}

	@Override
	public PropertyAbstraction createProperty(SourceAbstraction src, String name) {
		noSupportInPassive();
		return null;
	}

	@Override
	public SourceAbstraction createSource(ContextAbstraction ctx, String name) {
		noSupportInPassive();
		return null;
	}

	@Override
	public int getId() {
		noSupportInPassive();
		return -1;
	}

}
