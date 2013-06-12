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

public interface ContextListener {
	public ContextListenerInterface getProperties();
	
	public void processContext(Context ctx, ContextElement ce);
	
	public void sourceAdded(Context ctx, String source, String property);
	public void propertyAdded(Context ctx, String source, String property);
	
	public void sourceRemoved(Context ctx, String source);
	public void propertyRemoved(Context ctx, String source, String property);
}
