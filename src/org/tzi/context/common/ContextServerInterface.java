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

import java.util.Set;



public interface ContextServerInterface extends UniqueIdProvider {
	public Set<Integer> getUsedIds();
	
	public Integer addContext(String ctxName);
	public Integer addSource(int ctxId, String source);
	public void removeSource(int srcId);
	public Integer addProperty(int srcId, String property);
	public void removeProperty(int prpId);
	public void removeContext(int ctxId);
	
	public void propertyChange(int prpId, ContextElement ce);
	
	public String getStringId(String idS);
	public Integer getCtxId(String idS);
	public Integer getSrcId(Integer ctxId, String idS);
	public Integer getPrpId(Integer srcId, String idS);
	
	public Integer getContextId(Context ctx);
	
	public long getStartTime();
}
