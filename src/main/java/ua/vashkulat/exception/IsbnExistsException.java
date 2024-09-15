package ua.vashkulat.exception;

public class IsbnExistsException extends RuntimeException{
	public IsbnExistsException(String message) {
		super(message);
	}
}
