package ionshield.rsa.utils;

public interface RNG<T> {
    T getInRange(T start, T end);
}
