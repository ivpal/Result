package com.github.ivpal;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void successReturnSuccess() {
        Result<Integer, ?> result = Result.success(1);
        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals(1, result.get().intValue());
    }

    @Test
    void failureReturnFailure() {
        Result<?, Exception> result = Result.failure(new Exception());
        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertNull(result.get());
    }

    @Test
    void ofWithFailureSupplier() {
        Supplier<?> supplier = () -> {
            throw new RuntimeException();
        };
        Result<?, RuntimeException> result = Result.of(supplier, RuntimeException.class);
        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertNull(result.get());
    }

    @Test
    void ofSuccessSupplier() {
        Supplier<Integer> supplier = () -> 1;
        Result<Integer, ?> result = Result.of(supplier, RuntimeException.class);
        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals(1, result.get().intValue());
    }

    @Test
    void ofWithNotNullValueReturnSuccess() {
        Supplier<RuntimeException> supplier = RuntimeException::new;
        Result<Integer, RuntimeException> result = Result.of(1, supplier);
        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals(1, result.get().intValue());
    }

    @Test
    void ofWithNullValueReturnFailure() {
        Supplier<RuntimeException> supplier = RuntimeException::new;
        Result<Integer, RuntimeException> result = Result.of(null, supplier);
        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertNull(result.get());
    }

    @Test
    void valueReturnValue() {
        Result<Integer, ?> result = Result.success(1);
        assertEquals(1, result.value().intValue());
    }

    @Test
    void errorReturnError() {
        IOException ex = new IOException();
        Result<?, IOException> result = Result.failure(ex);
        assertEquals(ex, result.error());
    }

    @Test
    void isSuccessShouldReturnTrue() {
        Result<Integer, ?> result = Result.success(1);
        assertTrue(result.isSuccess());
    }

    @Test
    void isSuccessShouldReturnFalse() {
        Result<?, RuntimeException> result = Result.failure(new RuntimeException());
        assertFalse(result.isSuccess());
    }

    @Test
    void isFailureShouldReturnTrue() {
        Result<?, RuntimeException> result = Result.failure(new RuntimeException());
        assertTrue(result.isFailure());
    }

    @Test
    void isFailureShouldReturnFalse() {
        Result<Integer, ?> result = Result.success(1);
        assertFalse(result.isFailure());
    }

    @Test
    void getShouldReturnValue() {
        Result<Integer, ?> result = Result.success(1);
        assertEquals(1, result.get().intValue());
        result = Result.failure(new RuntimeException());
        assertNull(result.get());
    }

    @Test
    void orElseShouldReturnValue() {
        Result<Integer, ?> result = Result.success(1);
        assertEquals(1, result.orElse(2).intValue());
    }

    @Test
    void orElseShouldReturnFallback() {
        Result<Integer, RuntimeException> result = Result.failure(new RuntimeException());
        assertEquals(2, result.orElse(2).intValue());
    }

    @Test
    void orElseGetShouldReturnValue() {
        Result<Integer, ?> result = Result.success(1);
        assertEquals(1, result.orElseGet(() -> 2).intValue());
    }

    @Test
    void orElseGetShouldReturnValueFromFallback() {
        Result<Integer, RuntimeException> result = Result.failure(new RuntimeException());
        assertEquals(2, result.orElseGet(() -> 2).intValue());
    }

    @Test
    void mapWithSuccessShouldMapValue() {
        Result<Integer, ?> result = Result.success(1);
        Result<String, ?> newResult = result.map(String::valueOf);
        assertEquals("1", newResult.get());
    }

    @Test
    void mapWithFailureShouldPassError() {
        RuntimeException ex = new RuntimeException();
        Result<Integer, ?> result = Result.failure(ex);
        Result<String, ?> newResult = result.map(String::valueOf);
        assertTrue(newResult.isFailure());
        assertEquals(ex, newResult.error());
    }

    @Test
    void flatMapWithSuccessShouldMapResult() {
        Result<Integer, ?> result = Result.success(1);
        Result<String, ?> newResult = result.flatMap(r -> Result.success(String.valueOf(r)));
        assertEquals("1", newResult.get());
    }

    @Test
    void flatMapWithFailureShouldPassError() {
        RuntimeException ex = new RuntimeException();
        Result<Integer, ?> result = Result.failure(ex);
        Result<String, ?> newResult = result.flatMap(r -> Result.success(String.valueOf(r)));
        assertTrue(newResult.isFailure());
        assertEquals(ex, newResult.error());
    }

    @Test
    void mapErrorWithSuccessPassValue() {
        Result<Integer, RuntimeException> result = Result.success(1);
        Result<Integer, TestException> newResult = result.mapError(TestException::new);
        assertTrue(newResult.isSuccess());
        assertEquals(1, newResult.get().intValue());
    }

    @Test
    void mapErrorWithFailureMapError() {
        Result<?, RuntimeException> result = Result.failure(new RuntimeException());
        Result<?, TestException> newResult = result.mapError(TestException::new);
        assertTrue(newResult.isFailure());
    }

    @Test
    void flatMapErrorWithSuccessPassValue() {
        Result<Integer, RuntimeException> result = Result.success(1);
        Result<Integer, TestException> newResult = result.flatMapError(e -> Result.failure(new TestException(e)));
        assertTrue(newResult.isSuccess());
        assertEquals(1, newResult.get().intValue());
    }

    @Test
    void flatMapErrorWithFailureMapError() {
        Result<?, RuntimeException> result = Result.failure(new RuntimeException());
        Result<?, TestException> newResult = result.flatMapError(e -> Result.failure(new TestException(e)));
        assertTrue(newResult.isFailure());
    }

    @Test
    void anyShouldTestPredicate() {
        Result<Integer, RuntimeException> result = Result.success(1);
        boolean anyRes = result.any(r -> r < 2);
        assertTrue(anyRes);
    }

    @Test
    void transformShouldChangeValue() {
        Result<Integer, RuntimeException> result = Result.success(1);
        Result<String, TestException> newResult = result.transform(String::valueOf, TestException::new);
        assertTrue(newResult.isSuccess());
        assertEquals("1", newResult.get());
    }

    @Test
    void transformShouldChangeError() {
        RuntimeException ex = new RuntimeException();
        Result<Integer, RuntimeException> result = Result.failure(ex);
        Result<String, TestException> newResult = result.transform(String::valueOf, TestException::new);
        assertTrue(newResult.isFailure());
        assertEquals(ex, newResult.error().getCause());
    }

    @Test
    void liftShouldApplyValueMapper() {
        Result<Integer, RuntimeException> result = Result.success(1);
        Result<String, TestException> newResult = result.lift(
                v -> Result.success(String.valueOf(v)),
                e -> Result.failure(new TestException(e))
        );

        assertTrue(newResult.isSuccess());
        assertEquals("1", newResult.get());
    }

    @Test
    void liftShouldApplyErrorMapper() {
        RuntimeException ex = new RuntimeException();
        Result<Integer, RuntimeException> result = Result.failure(ex);
        Result<String, TestException> newResult = result.lift(
                v -> Result.success(String.valueOf(v)),
                e -> Result.failure(new TestException(e))
        );

        assertTrue(newResult.isFailure());
        assertEquals(ex, newResult.error().getCause());
    }

    @Test
    void foldShouldApplySuccessMapper() {
        Result<Integer, RuntimeException> result = Result.success(1);
        String newResult = result.fold(String::valueOf, ex -> "fallback");
        assertEquals("1", newResult);
    }

    @Test
    void foldShouldApplyFailureMapper() {
        Result<Integer, RuntimeException> result = Result.failure(new RuntimeException());
        String newResult = result.fold(String::valueOf, ex -> "fallback");
        assertEquals("fallback", newResult);
    }

    @Test
    void orElseThrowShouldReturnValue() {
        Result<Integer, RuntimeException> result = Result.success(1);
        int v = result.orElseThrow(TestException::new);
        assertEquals(1, v);
    }

    @Test
    void orElseThrowShouldThrowException() {
        Result<Integer, RuntimeException> result = Result.failure(new RuntimeException());
        assertThrows(TestException.class, () -> result.orElseThrow(TestException::new));
    }

    private static class TestException extends RuntimeException {
        TestException() {}

        TestException(Throwable cause) {
            super(cause);
        }
    }

}
