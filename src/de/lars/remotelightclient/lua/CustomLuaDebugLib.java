package de.lars.remotelightclient.lua;

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
				while(instructionsLeft.get() == 0) {	// force the thread to wait if no instructions left
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
