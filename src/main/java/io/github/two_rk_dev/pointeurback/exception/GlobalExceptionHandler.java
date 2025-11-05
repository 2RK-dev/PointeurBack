package io.github.two_rk_dev.pointeurback.exception;

import io.github.two_rk_dev.pointeurback.dto.ErrorDetails;
import io.github.two_rk_dev.pointeurback.dto.ValidationError;
import io.github.two_rk_dev.pointeurback.dto.ValidationErrorDetails;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<ErrorDetails> handleScheduleConflictException(@NotNull ScheduleConflictException ex,
                                                                        @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "SCHEDULE_CONFLICT");
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleGroupNotFoundException(@NotNull GroupNotFoundException ex,
                                                                     @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "GROUP_NOT_FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LevelNameNotUniqueException.class)
    public ResponseEntity<ErrorDetails> handleLevelNameNotUniqueException(@NotNull LevelNameNotUniqueException ex,
                                                                          @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "LEVEL_NAME_NOT_UNIQUE");
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LevelNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleLevelNotFoundException(@NotNull LevelNotFoundException ex,
                                                                     @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "LEVEL_NOT_FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomNameNotUniqueException.class)
    public ResponseEntity<ErrorDetails> handleRoomNameNotUniqueException(@NotNull RoomNameNotUniqueException ex,
                                                                         @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "ROOM_NAME_NOT_UNIQUE");
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleRoomNotFoundException(@NotNull RoomNotFoundException ex,
                                                                    @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "ROOM_NOT_FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TeacherAbbreviationNotUniqueException.class)
    public ResponseEntity<ErrorDetails> handleTeacherAbbreviationNotUniqueException(
            @NotNull TeacherAbbreviationNotUniqueException ex, @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "TEACHER_ABBREVIATION_NOT_UNIQUE");
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleTeacherNotFoundException(@NotNull TeacherNotFoundException ex,
                                                                       @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "TEACHER_NOT_FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TeachingUnitAbbreviationNotUniqueException.class)
    public ResponseEntity<ErrorDetails> handleTeachingUnitAbbreviationNotUniqueException(
            @NotNull TeachingUnitAbbreviationNotUniqueException ex, @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "TEACHING_UNIT_ABBREVIATION_NOT_UNIQUE");
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TeachingUnitNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleTeachingUnitNotFoundException(@NotNull TeachingUnitNotFoundException ex,
                                                                            @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "TEACHING_UNIT_NOT_FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ScheduleItemNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleScheduleItemNotFoundException(@NotNull ScheduleItemNotFoundException ex,
                                                                            @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "SCHEDULE_ITEM_NOT_FOUND");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(@NotNull Exception ex, @NotNull WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                request.getDescription(false),
                "INTERNAL_SERVER_ERROR");
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex,
                                                                  @NotNull HttpHeaders headers,
                                                                  @NotNull HttpStatusCode status,
                                                                  @NotNull WebRequest request) {
        List<ValidationError> errors = ex.getBindingResult().getAllErrors().stream()
                .map(ValidationError::fromFieldError)
                .toList();

        ValidationErrorDetails validationErrorResponse = new ValidationErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                errors,
                "VALIDATION_FAILED");
        return new ResponseEntity<>(validationErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ErrorDetails> handleInvalidFileCodecException(@NotNull InvalidFileFormatException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                ex.getMessage(),
                "INVALID_FILE_CODEC");
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownEntityException.class)
    public ResponseEntity<ErrorDetails> handleUnknownEntityException(@NotNull UnknownEntityException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                ex.getMessage(),
                "UNKNOWN_ENTITY");
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(UnsupportedCodecException.class)
    public ResponseEntity<ErrorDetails> handleUnsupportedCodecException(@NotNull UnsupportedCodecException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                ex.getMessage(),
                "UNSUPPORTED_CODEC");
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public ResponseEntity<ErrorDetails> handleUnsupportedMediaTypeStatusException(@NotNull UnsupportedMediaTypeStatusException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                "Supported media types: %s".formatted(ex.getSupportedMediaTypes()),
                "UNSUPPORTED_MEDIA_TYPE"
        );
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(@NotNull HandlerMethodValidationException ex,
                                                                            @NotNull HttpHeaders headers,
                                                                            @NotNull HttpStatusCode status,
                                                                            @NotNull WebRequest request) {
        String errors = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                errors,
                "HANDLER_METHOD_VALIDATION_FAILED"
        );
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorDetails> handleInvalidDateRangeException(@NotNull InvalidDateRangeException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                ex.getMessage(),
                "INVALID_DATE_RANGE"
        );
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorDetails> handleDateTimeParseException(@NotNull DateTimeParseException ex) {
        ErrorDetails errorDetails = new ErrorDetails(
                OffsetDateTime.now(ZoneOffset.UTC),
                ex.getMessage(),
                ex.getMessage(),
                "INVALID_DATE_TIME_FORMAT"
        );
        return ResponseEntity.badRequest().body(errorDetails);
    }
}
