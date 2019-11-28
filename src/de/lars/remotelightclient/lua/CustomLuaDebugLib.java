package de.lars.remotelightclient.lua;

import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;

public class CustomLuaDebugLib extends DebugLib {
	
	private boolean interrupted = false;
	
	@Override
	public void onInstruction(int pc, Varargs v, int top) {
		if(interrupted) {
			throw new ScriptInterruptException();
		}
		super.onInstruction(pc, v, top);
	}
	
	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}
	
	public boolean isInterrupted() {
		return interrupted;
	}
	
	public static class ScriptInterruptException extends RuntimeException {
		private static final long serialVersionUID = -409241147537467501L;}
}
