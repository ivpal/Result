package com.github.ivpal;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Result<V, E extends Throwable> {

    public static <V, E extends Throwable> Success<V, E> success(V value) {
        return new Success<>(value);
    }

    public static <V, E extends Throwable> Failure<V, E> failure(E error) {
        return new Failure<>(error);
    }

    public static <V, E extends Throwable> Result<V, E> of(Supplier<? extends V> supplier, Class<E> clazz) {
        try {
            return Result.success(supplier.get());
        } catch (Exception ex) {
            if (clazz.isInstance(ex)) {
                return Result.failure(clazz.cast(ex));
            }

            throw ex;
        }
    }

    public static <V, X extends Throwable> Result<V, X> of(V value, Supplier<? extends X> supplier) {
        return value == null ? Result.failure(supplier.get()) : Result.success(value);
    }

    public abstract V value();

    public abstract E error();

    public boolean isSuccess() {
        return error() == null && value() != null;
    }

    public boolean isFailure() {
        return error() != null;
    }

    public V get() {
        return value();
    }

    public V orElse(V fallback) {
        return this instanceof Success ? value() : fallback;
    }

    public V orElseGet(Supplier<? extends V> supplier) {
        Objects.requireNonNull(supplier);
        return  this instanceof Success ? value() : supplier.get();
    }

    public <U> Result<U, E> map(Function<? super V, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return this instanceof Success ? Result.success(mapper.apply(value())) : Result.failure(error());
    }

    public <U> Result<U, E> flatMap(Function<? super V, ? extends Result<U, E>> mapper) {
        Objects.requireNonNull(mapper);
        return this instanceof Success ? mapper.apply(value()) : Result.failure(error());
    }

    public <X extends Throwable> Result<V, X> mapError(Function<? super E, ? extends X> mapper) {
        Objects.requireNonNull(mapper);
        return this instanceof Success ? Result.success(value()) : Result.failure(mapper.apply(error()));
    }

    public <X extends Throwable> Result<V, X> flatMapError(Function<? super E, ? extends Result<V, X>> mapper) {
        Objects.requireNonNull(mapper);
        return this instanceof Success ? Result.success(value()) : mapper.apply(error());
    }

    public boolean any(Predicate<? super V> predicate) {
        Objects.requireNonNull(predicate);
        return this instanceof Success && predicate.test(value());
    }

    public <U, X extends Throwable> Result<U, X> transform(Function<? super V, ? extends U> valueMapper,
                                                           Function<? super E, ? extends X> errorMapper) {
        Objects.requireNonNull(valueMapper);
        Objects.requireNonNull(errorMapper);
        return this instanceof Success ?
                Result.success(valueMapper.apply(value())) :
                Result.failure(errorMapper.apply(error()));
    }

    public <U, X extends Throwable> Result<U, X> lift(Function<? super V, ? extends Result<U, X>> valueMapper,
                                                      Function<? super E, ? extends Result<U, X>> errorMapper) {
        Objects.requireNonNull(valueMapper);
        Objects.requireNonNull(errorMapper);
        return this instanceof Success ? valueMapper.apply(value()) : errorMapper.apply(error());
    }

    public <U> U fold(Function<? super V, ? extends U> success, Function<? super E, ? extends U> failure) {
        Objects.requireNonNull(success);
        Objects.requireNonNull(failure);
        return this instanceof Success ? success.apply(value()) : failure.apply(error());
    }

    public <X extends Throwable> V orElseThrow(Supplier<? extends X> supplier) throws X {
        Objects.requireNonNull(supplier);
        if (this instanceof Failure) {
            throw supplier.get();
        }

        return value();
    }

    public static class Success<V, E extends Throwable> extends Result<V, E> {
        private final V value;

        Success(V value) {
            this.value = value;
        }

        @Override
        public V value() {
            return value;
        }

        @Override
        public E error() {
            return null;
        }
    }

    public static class Failure<V, E extends Throwable> extends Result<V, E> {
        private final E error;

        Failure(E error) {
            this.error = error;
        }

        @Override
        public V value() {
            return null;
        }

        @Override
        public E error() {
            return error;
        }
    }

}
