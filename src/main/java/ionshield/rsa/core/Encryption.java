package ionshield.rsa.core;

import java.util.List;

public interface Encryption {
    byte[] encrypt(byte[] bytes);
    byte[] decrypt(byte[] bytes);
}
