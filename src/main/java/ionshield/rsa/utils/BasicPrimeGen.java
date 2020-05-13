package ionshield.rsa.utils;

import java.math.BigInteger;
import java.util.Random;

public class BasicPrimeGen implements RNG<BigInteger>
{
    private Random random = new Random();
    
    @Override
    public BigInteger getInRange(BigInteger start, BigInteger end) {
        BigInteger range = end.subtract(start);
        int bits = range.bitLength();
        while(true) {
            BigInteger value = new BigInteger(bits, random);
            if (value.compareTo(range) < 0 && start.add(value).isProbablePrime(256)) {
                return start.add(value);
            }
        }
    }
}
