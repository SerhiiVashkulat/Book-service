package ua.vashkulat.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.vashkulat.exception.ApiError;
import ua.vashkulat.exception.BookNotFoundException;
import ua.vashkulat.exception.IsbnExistsException;

import java.util.Objects;

@RestControllerAdvice
public class ControllerAdvice {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception,
																		   HttpServletRequest request) {
		String detail = Objects.requireNonNull(Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());

		return ResponseEntity.badRequest()
				.body(ApiError.builder()
						.description(detail)
						.path(request.getRequestURI())
						.build());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException exception,
																	   HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(ApiError.builder()
						.description(exception.getMessage())
						.path(request.getRequestURI())
						.build());

	}

	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<ApiError> handleBookNotFoundException(BookNotFoundException exception,
																HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiError.builder()
						.description(exception.getMessage())
						.path(request.getRequestURI())
						.build());

	}

	@ExceptionHandler(IsbnExistsException.class)
	public ResponseEntity<ApiError> handleIsbnExistsException(IsbnExistsException exception,
															  HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(ApiError.builder()
						.description(exception.getMessage())
						.path(request.getRequestURI())
						.build());

	}

}
