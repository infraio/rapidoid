package org.rapidoid.docs.httpguipage;

import org.rapidoid.setup.On;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

public class Main {

	public static void main(String[] args) {
		/* A wrapper executes before the handler */

		On.defaults().wrappers((req, next) -> {
			return next.invokeAndTransformResult(result -> "Hey: " + result);
		});

		/* and provides transformation for the result */

		On.get("/size").json((String s) -> s.length());
		On.get("/upper").json((String s) -> s.toUpperCase());
	}

}
