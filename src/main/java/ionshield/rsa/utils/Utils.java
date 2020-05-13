package ionshield.rsa.utils;

import java.math.BigInteger;
import java.util.List;

public abstract class Utils {
    
    public static int getSize(List<String> lines) {
        int size = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            size += line.length();
            if (i < lines.size() - 1) {
                size += System.lineSeparator().length();
            }
        }
        /*for (String line : lines) {
            size += line.length() + System.lineSeparator().length();
        }
        size -= System.lineSeparator().length();*/
        return size;
    }
    
    public static byte[] charsToBytes(char[] c) {
        byte[] b = new byte[c.length];
        for (int i = 0; i < c.length; i++) {
            b[i] = (byte)c[i];
        }
        return b;
    }
    
    public static char[] bytesToChars(byte[] b) {
        char[] c = new char[b.length];
        for (int i = 0; i < b.length; i++) {
            c[i] = (char)b[i];
        }
        return c;
    }
    
    public static BigInteger inverseMod(BigInteger val, BigInteger mod) {
        if (val.compareTo(BigInteger.ZERO) == 0) return BigInteger.ONE;
        BigInteger x2 = BigInteger.ONE;
        BigInteger y2 = BigInteger.ZERO;
        BigInteger x1 = BigInteger.ZERO;
        BigInteger y1 = BigInteger.ONE;
        BigInteger mod0 = mod;
        BigInteger q, r, x, y;
        while (val.compareTo(BigInteger.ZERO) > 0) {
            q = mod.divide(val);
            r = mod.subtract(val.multiply(q));
            x = x2.subtract(q.multiply(x1));
            y = y2.subtract(q.multiply(y1));
            x2 = x1;
            y2 = y1;
            x1 = x;
            y1 = y;
            mod = val;
            val = r;
        }
        
        if (y2.compareTo(BigInteger.ZERO) < 0) y2 = y2.add(mod0);
        return y2;
    }
    
    public static BigInteger modPow(BigInteger b, BigInteger e, BigInteger m) {
        if (m.compareTo(BigInteger.ONE) == 0) return BigInteger.ZERO;
        BigInteger result = BigInteger.ONE;
        b = b.mod(m);
        while (e.compareTo(BigInteger.ZERO) > 0) {
            if (e.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ONE) == 0) {
                result = result.multiply(b).mod(m);
            }
            e = e.shiftRight(1);
            b = b.multiply(b).mod(m);
        }
        return result;
    }
}
