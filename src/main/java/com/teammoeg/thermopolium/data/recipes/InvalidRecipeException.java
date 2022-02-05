package com.teammoeg.thermopolium.data.recipes;

public class InvalidRecipeException extends RuntimeException {

	public InvalidRecipeException() {
	}

	public InvalidRecipeException(String message) {
		super(message);
	}

	public InvalidRecipeException(Throwable cause) {
		super(cause);
	}

	public InvalidRecipeException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidRecipeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
