/*-
 * >===license-start
 * RemoteLight
 * ===
 * Copyright (C) 2019 - 2020 Lars O.
 * ===
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * <===license-end
 */

package de.lars.remotelightcore.lua;

import java.util.concurrent.atomic.AtomicInteger;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.OrphanedThread;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;

public class CustomLuaDebugLib extends DebugLib {
	
	private boolean interrupted = false;
	private Globals globals;
	private AtomicInteger instructionsLeft;
	
	public CustomLuaDebugLib(Globals globals, int maxInstructions) {
		this.globals = globals;
		this.instructionsLeft = new AtomicInteger(maxInstructions);
	}
	
	@Override
	public void onInstruction(int pc, Varargs v, int top) {
		if(interrupted) {
			throw new OrphanedThread();
		} else {
			synchronized (this) {
				while(instructionsLeft.get() == 0 && !globals.running.isMainThread()) {	// force the thread to wait if no instructions left
					globals.yield(LuaValue.NIL);
				}
			}
		}
		super.onInstruction(pc, v, top);
		instructionsLeft.decrementAndGet();
	}
	
	/**
	 * @param instructions Amount of instructions
	 */
	public void addInstructions(int instructions) {
		instructionsLeft.addAndGet(instructions);
	}
	
	public int getInstructionsLeft() {
		return instructionsLeft.get();
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}
	
	public boolean isInterrupted() {
		return interrupted;
	}
	
}
