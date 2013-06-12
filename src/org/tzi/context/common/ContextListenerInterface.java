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

public interface ContextListenerInterface {
	public static enum NewElementPolicy { Always, Never, IfMatch };

	/**
	 * Checks whether these properties accept elements for the given source</br> 
	 * @param source source to match
	 * @return <em>true</em> if in principle elements from source are accepted.
	 */
	public abstract boolean matchesSource(String source);

	public abstract boolean notifyNewSource(String source);

	public abstract boolean notifyNewProperty(String source, String property);

	/**
	 * Checks whether these properties accept elements from the given sources'
	 * property</br>
	 * @param source source to match
	 * @param property property to match
	 * @return <em>true</em> if in principle elements from source and property are accepted.
	 */
	public abstract boolean matchesSourceProperty(String source, String property);

	public abstract NewElementPolicy getNewSourcePolicy();

	public abstract NewElementPolicy getNewPropertyPolicy();

	public abstract void setNewSourcePolicy(NewElementPolicy nep);

	public abstract void setNewPropertyPolicy(NewElementPolicy nep);

	/**
	 * Checks whether these properties accept the given context element
	 * @param ce context element to check
	 * @return <em>true</em> if ce is accepted.
	 */
	public abstract boolean matches(ContextElement ce);

}