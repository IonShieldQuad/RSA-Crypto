package ionshield.rsa.core;

import ionshield.rsa.utils.BitInputStream;
import ionshield.rsa.utils.BitOutputStream;
import ionshield.rsa.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class RSAEncryption implements Encryption {
    
    private BigInteger n = BigInteger.ONE;
    private BigInteger e = BigInteger.ONE;
    private BigInteger d = BigInteger.ONE;
    private int bitLength = 8;
    
    public RSAEncryption() {};
    
    public RSAEncryption(BigInteger n, BigInteger e, BigInteger d, int bitLength) {
        this.n = n;
        this.e = e;
        this.d = d;
        this.bitLength = bitLength;
    }
    
    @Override
    public byte[] encrypt(byte[] bytes) {
        BitInputStream bis = new BitInputStream(new ByteArrayInputStream(bytes));
        bis.setSkipHighestBit(false);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BitOutputStream bos = new BitOutputStream(out);
        bos.setSkipHighestBit(false);
        
        StringBuilder isb = new StringBuilder();
        for (int i = 0; i < bytes.length * 8; i++) {
            try {
                isb.append(bis.readBit() ? "1" : "0");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                break;
            }
            if (isb.length() >= bitLength - 1) {
                BigInteger value = new BigInteger(isb.toString(), 2);
                isb = new StringBuilder();
                
                value = Utils.modPow(value, e, n);
                StringBuilder writeString = new StringBuilder(value.toString(2));
                int missing = (bitLength - writeString.length());
                while (missing > 0) {
                    writeString.insert(0, "0");
                    missing--;
                }
                for (int j = 0; j < writeString.length(); j++) {
                    try {
                        bos.writeBit(writeString.charAt(j) != '0');
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        break;
                    }
                }
            }
            
        }
        //System.out.println("E0: " + isb);
        if (isb.length() > 0) {
            while (isb.length() < bitLength - 1) {
                isb.append("0");
            }
            BigInteger value = new BigInteger(isb.toString(), 2);
            isb = new StringBuilder();
        
            value = Utils.modPow(value, e, n);
            StringBuilder writeString = new StringBuilder(value.toString(2));
            int missing = (bitLength - writeString.length());
            while (missing > 0) {
                writeString.insert(0, "0");
                missing--;
            }
            //System.out.println("E1: " + writeString.toString());
            for (int j = 0; j < writeString.length(); j++) {
                try {
                    bos.writeBit(writeString.charAt(j) != '0');
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    break;
                }
            }
        }
    
        try {
            bos.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    
        return out.toByteArray();
    }
    
    @Override
    public byte[] decrypt(byte[] bytes) {
        BitInputStream bis = new BitInputStream(new ByteArrayInputStream(bytes));
        bis.setSkipHighestBit(false);
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BitOutputStream bos = new BitOutputStream(out);
        bos.setSkipHighestBit(false);
    
        StringBuilder isb = new StringBuilder();
        for (int i = 0; i < bytes.length * 8; i++) {
            try {
                isb.append(bis.readBit() ? "1" : "0");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                break;
            }
            
            if (isb.length() >= bitLength) {
                BigInteger value = new BigInteger(isb.toString(), 2);
                isb = new StringBuilder();
            
                value = Utils.modPow(value, d, n);
                StringBuilder writeString = new StringBuilder(value.toString(2));
                int missing = (bitLength - 1 - writeString.length());
                while (missing > 0) {
                    writeString.insert(0, "0");
                    missing--;
                }
                for (int j = 0; j < writeString.length(); j++) {
                    try {
                        bos.writeBit(writeString.charAt(j) != '0');
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        break;
                    }
                }
            }
        
        }
        //System.out.println("D0: " + isb);
    
        try {
            bos.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    
        return out.toByteArray();
    }
    
    public BigInteger getN() {
        return n;
    }
    
    public void setN(BigInteger n) {
        this.n = n;
    }
    
    public BigInteger getE() {
        return e;
    }
    
    public void setE(BigInteger e) {
        this.e = e;
    }
    
    public BigInteger getD() {
        return d;
    }
    
    public void setD(BigInteger d) {
        this.d = d;
    }
    
    public int getBitLength() {
        return bitLength;
    }
    
    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }
}
