package org.rapidoid.docs.injection;

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

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.GET;

import javax.inject.Inject;

@Controller("/foo")
public class Foo {

	@Inject
	public Bar bar;

	private int count;

	@GET("/hi")
	public String hello() {
		return ++count + ": " + bar.msg();
	}

	public String msg() {
		return "Hello from Foo!";
	}

}
