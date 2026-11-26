package labs.common;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * One place that turns exceptions into safe, generic API responses (Section 3.6).
 *
 * <p>Extending {@link ResponseEntityExceptionHandler} keeps Spring's standard status codes
 * (400 for invalid input, 404 for unknown paths, 405, 413 for oversized uploads ...).
 * Without it, the catch-all handler below would turn all of those into 500 errors.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // Keep 403 as 403, otherwise the catch-all below turns it into a 500
    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail forbidden(AccessDeniedException e) {
        return ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(Exception e) {
        String ref = UUID.randomUUID().toString();
        log.error("event=unhandled.exception ref={}", ref, e);
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error. Reference: " + ref);
    }
}
