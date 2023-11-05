package it.univaq.easier;

public class Call {

	private String caller;
	private String called;
	
	public Call(final String caller, final String called) {
		super();
		this.caller = caller;
		this.called = called;
	}

	public String getCaller() {
		return caller;
	}
	
	public void setCaller(final String caller) {
		this.caller = caller;
	}
	
	public String getCalled() {
		return called;
	}
	
	public void setCalled(final String called) {
		this.called = called;
	}
}
