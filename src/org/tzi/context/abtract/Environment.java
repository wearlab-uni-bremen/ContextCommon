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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Environment {
	protected Random rnd = new Random();
	protected Set<Integer> usedIds = new TreeSet<Integer>();
	
	protected Map<Integer, ContextAbstraction> contexts = new TreeMap<Integer, ContextAbstraction>();
	
	protected Map<Integer, AbstractItem> knownItems = new TreeMap<Integer, AbstractItem>();
	
	protected List<EnvironmentListener> listeners = new LinkedList<EnvironmentListener>();
	protected List<EnvironmentListener> listeners_tmp = new LinkedList<EnvironmentListener>();
	
	protected ExecutorService sequentialExcecution;
	
	protected Map<Object, Object> _data;
	
	public Object getData(Object key) {
		if(key==null)
			return null;
		
		if(_data==null)
			return null;
		
		return _data.get(key);
	}
	
	public void changeContextId(int oldId, int newId) {
		ContextAbstraction ctx = contexts.remove(oldId);
		if(ctx!=null) {
			ctx.updateId(newId);
			contexts.put(newId, ctx);
		}
	}
	
	public void changeSourceId(int oldId, int newId) {
		SourceAbstraction src = getSourceById(oldId);
		if(src!=null)
			src.getContext().changeSourceId(oldId, newId);
	}
	
	public void changePropertyId(int oldId, int newId) {
		PropertyAbstraction prp = getPropertyById(oldId);
		if(prp!=null)
			prp.getSource().changePropertyId(oldId, newId);
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
	
	public Environment() {
		// the worker thread needs to be set as a daemon to make the VM quit later
		sequentialExcecution = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("EnvironmentSignalDispatcher");
				t.setDaemon(true);
				return t;
			}
		});
	}
	
	public int getId() {
		synchronized (usedIds) {
			Integer newid;
			do {
				newid = rnd.nextInt();
			} while(newid <= 0 || usedIds.contains(newid));
			
			usedIds.add(newid);
			
			return newid;
		}
	}
	
	public void freeId(Integer id) {
		synchronized(usedIds) {
			usedIds.remove(id);
		}
	}
	
	public void free(AbstractItem ai) {
		removeKnownItem(ai);
		freeId(ai.getId());
	}
	
	public Collection<ContextAbstraction> getContexts() {
		return contexts.values();
	}
	
	public ContextAbstraction getContextById(int id) {
		return contexts.get(id);
	}
	
	public ContextAbstraction getContextByName(String name) {
		if(name==null)
			return null;
		
		for(ContextAbstraction ctx : getContexts()) {
			if(name.equals(ctx.getName()))
				return ctx;
		}
		
		return null;
	}
	
	// intentionally package private
	void knownItemIdChange(int oldId) {
		AbstractItem ai = knownItems.remove(oldId);
		if(ai!=null) {
			knownItems.put(ai.getId(), ai);
		}
	}
	
	public <A extends AbstractItem> A addKnownItem(A ai) {
		knownItems.put(ai.getId(), ai);
		return ai;
	}
	
	public void removeKnownItem(AbstractItem ai) {
		knownItems.remove(ai.getId());
	}
	
	protected ContextAbstraction addContext(ContextAbstraction ctx) {
		ContextAbstraction existingCtx = getContextByName(ctx.getName());
		if(existingCtx != null && existingCtx != ctx)
			throw new RuntimeException("a context by the same name exists already!");
		
		contexts.put(ctx.getId(), ctx);
		addKnownItem(ctx);
		
		return ctx;
	}
	
	public ContextAbstraction createSpecialContext(int id, String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");
		
		ContextAbstraction c = getContextByName(name);
		if(c != null)
			return c;
		
		if(id >= 0)
			throw new RuntimeException("Invalid special id: " + id);

		ContextAbstraction ctx = addContext(new ContextAbstraction(this, id, name));
		sequentialExcecution.execute(new ContextCreationSignal(ctx));
		return ctx;
	}
	
	public ContextAbstraction removeContext(ContextAbstraction ctx) {
		contexts.remove(ctx.getId());
		removeKnownItem(ctx);
		
		sequentialExcecution.execute(new ContextRemovalSignal(ctx));
		
		return ctx;
	}
	
	public AbstractItem getKnownItembyId(int id) {
		return knownItems.get(id);
	}
	
	public PropertyAbstraction getPropertyById(int id) {
		AbstractItem ai = getKnownItembyId(id);
		if(ai instanceof PropertyAbstraction) {
			return (PropertyAbstraction)ai;
		}
		return null;
	}
	
	public SourceAbstraction getSourceById(int id) {
		AbstractItem ai = getKnownItembyId(id);
		if(ai instanceof SourceAbstraction) {
			return (SourceAbstraction)ai;
		}
		return null;
	}
	
	public ContextAbstraction createContext(String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");
		
		ContextAbstraction c = getContextByName(name);
		if(c != null)
			return c;
		
		int id = getId();
		ContextAbstraction ctx = addContext(new ContextAbstraction(this, id, name));
		sequentialExcecution.execute(new ContextCreationSignal(ctx));
		return ctx;
	}
	
	public void freeRecursive(AbstractItem ai) {
		if(ai instanceof ContextAbstraction) {
			for(SourceAbstraction src : ((ContextAbstraction)ai).getSources()) {
				freeRecursive(src);
				((ContextAbstraction)ai).removeSource(src);
			}
			removeContext((ContextAbstraction)ai);
		}
		if(ai instanceof SourceAbstraction) {
			for(PropertyAbstraction prp : ((SourceAbstraction)ai).getProperties()) {
				freeRecursive(prp);
				((SourceAbstraction)ai).removeProperty(prp);
			}
		}
		free(ai);
	}

	public SourceAbstraction createSource(ContextAbstraction ctx, String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");
		
		SourceAbstraction s = ctx.getSourceByName(name);
		
		if(s!=null)
			return s;
		
		int id = getId();
		SourceAbstraction src = addKnownItem(new SourceAbstraction(ctx, id, name));
		sequentialExcecution.execute(new SourceCreationSignal(src));
		return src;
	}
	
	public SourceAbstraction createSpecialSource(ContextAbstraction ctx, int id, String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");
		
		SourceAbstraction s = ctx.getSourceByName(name);
		
		if(s!=null)
			return s;

		if(id >= 0)
			throw new RuntimeException("Invalid special id: " + id);

		SourceAbstraction src = addKnownItem(new SourceAbstraction(ctx, id, name));
		sequentialExcecution.execute(new SourceCreationSignal(src));
		return src;
	}
	
	public PropertyAbstraction createProperty(SourceAbstraction src, String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");

		PropertyAbstraction p = src.getPropertyByName(name);
		
		if(p!=null)
			return p;
		
		int id = getId();
		PropertyAbstraction prp = addKnownItem(new PropertyAbstraction(src, id, name));
		sequentialExcecution.execute(new PropertyCreationSignal(prp));
		return prp;
	}
	
	public PropertyAbstraction createSpecialProperty(SourceAbstraction src, int id, String name) {
		if(name==null)
			throw new RuntimeException("name may not be null!");

		PropertyAbstraction p = src.getPropertyByName(name);
		
		if(p!=null)
			return p;
	
		if(id >= 0)
			throw new RuntimeException("Invalid special id: " + id);

		PropertyAbstraction prp = addKnownItem(new PropertyAbstraction(src, id, name));
		sequentialExcecution.execute(new PropertyCreationSignal(prp));
		return prp;
	}
	
	public PropertyAbstraction createProperty(SourceAbstraction src, String name, PropertyValues v) {
		if(name==null)
			throw new RuntimeException("name may not be null!");

		PropertyAbstraction p = src.getPropertyByName(name);
		
		if(p!=null) {
			p.setValues(v);
			p.signalUpdate();
			return p;
		}
		
		int id = getId();
		PropertyAbstraction prp = addKnownItem(new PropertyAbstraction(src, id, name, v));
		sequentialExcecution.execute(new PropertyCreationSignal(prp));
		return prp;
	}

	public PropertyAbstraction createProperty(SourceAbstraction src, String name, long timestamp, String value, Set<String> tags, boolean persistent) {
		if(name==null)
			throw new RuntimeException("name may not be null!");

		PropertyAbstraction p = src.getPropertyByName(name);
		
		if(p!=null) {
			p.set(timestamp, value, tags, persistent);
			p.signalUpdate();
			return p;
		}
		
		int id = getId();
		PropertyAbstraction prp =  addKnownItem(new PropertyAbstraction(src, id, name, timestamp, value, tags, persistent));
		sequentialExcecution.execute(new PropertyCreationSignal(prp));
		return prp;
	}
	
	public void addEnvironmentListener(EnvironmentListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	public void removeEnvironmentListener(EnvironmentListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	protected class ContextCreationSignal implements Runnable {
		private ContextAbstraction ctx;
		
		public ContextCreationSignal(ContextAbstraction ctx) {
			this.ctx = ctx;
		}
		
		public void run() {
			synchronized(listeners_tmp) {
				synchronized (listeners) {
					listeners_tmp.addAll(listeners);
				}
				
				for(EnvironmentListener el : listeners_tmp) {
					el.contextCreated(ctx);
				}
				
				listeners_tmp.clear();
			}

		}
	}
	
	protected class ContextRemovalSignal implements Runnable {
		private ContextAbstraction ctx;
		
		public ContextRemovalSignal(ContextAbstraction ctx) {
			this.ctx = ctx;
		}
		
		public void run() {
			synchronized(listeners_tmp) {
				synchronized (listeners) {
					listeners_tmp.addAll(listeners);
				}
				
				for(EnvironmentListener el : listeners_tmp) {
					el.contextRemoved(ctx);
				}
				
				listeners_tmp.clear();
			}

		}
	}

	protected class SourceCreationSignal implements Runnable {
		private SourceAbstraction src;
		
		public SourceCreationSignal(SourceAbstraction src) {
			this.src = src;
		}
		
		public void run() {
			synchronized(listeners_tmp) {
				synchronized (listeners) {
					listeners_tmp.addAll(listeners);
				}
				
				for(EnvironmentListener el : listeners_tmp) {
					el.sourceCreated(src);
				}
				
				listeners_tmp.clear();
			}

		}
	}
	
	protected class SourceRemovalSignal implements Runnable {
		private SourceAbstraction src;
		
		public SourceRemovalSignal(SourceAbstraction src) {
			this.src = src;
		}
		
		public void run() {
			synchronized(listeners_tmp) {
				synchronized (listeners) {
					listeners_tmp.addAll(listeners);
				}
				
				for(EnvironmentListener el : listeners_tmp) {
					el.sourceRemoved(src);
				}
				
				listeners_tmp.clear();
			}

		}
	}

	protected class PropertyCreationSignal implements Runnable {
		private PropertyAbstraction prp;
		private PropertyValues snapshot;
		
		
		public PropertyCreationSignal(PropertyAbstraction prp) {
			this.prp = prp;
			snapshot = prp.getValues().copy();
		}
		
		public void run() {
			synchronized(listeners_tmp) {
				synchronized (listeners) {
					listeners_tmp.addAll(listeners);
				}
				
				for(EnvironmentListener el : listeners_tmp) {
					el.propertyCreated(prp, snapshot);
				}
				
				listeners_tmp.clear();
			}

		}
	}
	
	protected class PropertyRemovalSignal implements Runnable {
		private PropertyAbstraction prp;
		
		public PropertyRemovalSignal(PropertyAbstraction prp) {
			this.prp = prp;
		}
		
		public void run() {
			synchronized(listeners_tmp) {
				synchronized (listeners) {
					listeners_tmp.addAll(listeners);
				}
				
				for(EnvironmentListener el : listeners_tmp) {
					el.propertyRemoved(prp);
				}
				
				listeners_tmp.clear();
			}

		}
	}
	
	public void signalSourceRemoval(SourceAbstraction src) {
		free(src);
		sequentialExcecution.execute(new SourceRemovalSignal(src));
	}
	
	public void signalPropertyRemove(PropertyAbstraction prp) {
		free(prp);
		sequentialExcecution.execute(new PropertyRemovalSignal(prp));
	}
	
	public void dispatchSignal(Runnable r) {
		sequentialExcecution.execute(r);
	}


}
