package org.ow2.play.srbench;

public class SrbenchSimulatorException extends Exception {

	private static final long serialVersionUID = -6457123186980135755L;

	public SrbenchSimulatorException() {
		super();
	}

	public SrbenchSimulatorException(String message) {
		super(message);
	}

	public SrbenchSimulatorException(Throwable cause) {
		super(cause);
	}

	public SrbenchSimulatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public SrbenchSimulatorException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
