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

public interface EnvironmentListener {
	public void contextCreated(ContextAbstraction ctx);
	public void contextRemoved(ContextAbstraction ctx);
	
	public void sourceCreated(SourceAbstraction src);
	public void sourceRemoved(SourceAbstraction src);
	
	public void propertyCreated(PropertyAbstraction prp, PropertyValues v);
	public void propertyRemoved(PropertyAbstraction prp);
}