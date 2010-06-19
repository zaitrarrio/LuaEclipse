/******************************************************************************
 * Copyright (c) 2009 KeplerProject, Sierra Wireless.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 *          - initial API and implementation and initial documentation
 *****************************************************************************/

package org.keplerproject.luaeclipse.lua.internal.tests;

import junit.framework.TestCase;

import org.junit.Test;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

public class ConcurrencyTest extends TestCase {
	private final static int THREAD_COUNT = 20;

	private class LuaJavaUse extends Thread {

		private LuaState state = null;

		public LuaJavaUse(LuaState l) {
			state = l;
		}

		public void run() {
			String code = "funtion fibo(n) if n< 2 then return 1 end return fibo(n-1) + fibo(n-2) end local var = fibo( 123450000 )";
			state.LdoString(code);
		}
	}

	@Test
	public void testLuaJavaUse() {
		Thread[] threads = new Thread[THREAD_COUNT];
		for (int k = 0; k < THREAD_COUNT; k++) {
			threads[k] = new LuaJavaUse(LuaStateFactory.newLuaState());
		}
		for (Thread thread : threads) {
			thread.run();
		}
		int count = 0;
		for (Thread thread : threads) {
			try {
				thread.join();
				count++;
			} catch (InterruptedException e) {
			}
		}
		assertEquals("A thread encounter an error", count, THREAD_COUNT);
	}

}
