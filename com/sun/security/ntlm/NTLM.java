package com.sun.security.ntlm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.HexDumpEncoder;
import sun.security.provider.MD4;

class NTLM {
  private final SecretKeyFactory fac;
  
  private final Cipher cipher;
  
  private final MessageDigest md4;
  
  private final Mac hmac;
  
  private final MessageDigest md5;
  
  private static final boolean DEBUG = (System.getProperty("ntlm.debug") != null);
  
  final Version v;
  
  final boolean writeLM;
  
  final boolean writeNTLM;
  
  protected NTLM(String paramString) throws NTLMException {
    if (paramString == null)
      paramString = "LMv2/NTLMv2"; 
    switch (paramString) {
      case "LM":
        this.v = Version.NTLM;
        this.writeLM = true;
        this.writeNTLM = false;
        break;
      case "NTLM":
        this.v = Version.NTLM;
        this.writeLM = false;
        this.writeNTLM = true;
        break;
      case "LM/NTLM":
        this.v = Version.NTLM;
        this.writeLM = this.writeNTLM = true;
        break;
      case "NTLM2":
        this.v = Version.NTLM2;
        this.writeLM = this.writeNTLM = true;
        break;
      case "LMv2":
        this.v = Version.NTLMv2;
        this.writeLM = true;
        this.writeNTLM = false;
        break;
      case "NTLMv2":
        this.v = Version.NTLMv2;
        this.writeLM = false;
        this.writeNTLM = true;
        break;
      case "LMv2/NTLMv2":
        this.v = Version.NTLMv2;
        this.writeLM = this.writeNTLM = true;
        break;
      default:
        throw new NTLMException(5, "Unknown version " + paramString);
    } 
    try {
      this.fac = SecretKeyFactory.getInstance("DES");
      this.cipher = Cipher.getInstance("DES/ECB/NoPadding");
      this.md4 = MD4.getInstance();
      this.hmac = Mac.getInstance("HmacMD5");
      this.md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchPaddingException noSuchPaddingException) {
      throw new AssertionError();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new AssertionError();
    } 
  }
  
  public void debug(String paramString, Object... paramVarArgs) {
    if (DEBUG)
      System.out.printf(paramString, paramVarArgs); 
  }
  
  public void debug(byte[] paramArrayOfByte) {
    if (DEBUG)
      try {
        (new HexDumpEncoder()).encodeBuffer(paramArrayOfByte, System.out);
      } catch (IOException iOException) {} 
  }
  
  byte[] makeDesKey(byte[] paramArrayOfByte, int paramInt) {
    int[] arrayOfInt = new int[paramArrayOfByte.length];
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfInt[b] = (paramArrayOfByte[b] < 0) ? (paramArrayOfByte[b] + 256) : paramArrayOfByte[b]; 
    byte[] arrayOfByte = new byte[8];
    arrayOfByte[0] = (byte)arrayOfInt[paramInt + 0];
    arrayOfByte[1] = (byte)(arrayOfInt[paramInt + 0] << 7 & 0xFF | arrayOfInt[paramInt + 1] >> 1);
    arrayOfByte[2] = (byte)(arrayOfInt[paramInt + 1] << 6 & 0xFF | arrayOfInt[paramInt + 2] >> 2);
    arrayOfByte[3] = (byte)(arrayOfInt[paramInt + 2] << 5 & 0xFF | arrayOfInt[paramInt + 3] >> 3);
    arrayOfByte[4] = (byte)(arrayOfInt[paramInt + 3] << 4 & 0xFF | arrayOfInt[paramInt + 4] >> 4);
    arrayOfByte[5] = (byte)(arrayOfInt[paramInt + 4] << 3 & 0xFF | arrayOfInt[paramInt + 5] >> 5);
    arrayOfByte[6] = (byte)(arrayOfInt[paramInt + 5] << 2 & 0xFF | arrayOfInt[paramInt + 6] >> 6);
    arrayOfByte[7] = (byte)(arrayOfInt[paramInt + 6] << 1 & 0xFF);
    return arrayOfByte;
  }
  
  byte[] calcLMHash(byte[] paramArrayOfByte) {
    byte[] arrayOfByte1 = { 75, 71, 83, 33, 64, 35, 36, 37 };
    byte[] arrayOfByte2 = new byte[14];
    int i = paramArrayOfByte.length;
    if (i > 14)
      i = 14; 
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte2, 0, i);
    try {
      DESKeySpec dESKeySpec1 = new DESKeySpec(makeDesKey(arrayOfByte2, 0));
      DESKeySpec dESKeySpec2 = new DESKeySpec(makeDesKey(arrayOfByte2, 7));
      SecretKey secretKey1 = this.fac.generateSecret(dESKeySpec1);
      SecretKey secretKey2 = this.fac.generateSecret(dESKeySpec2);
      this.cipher.init(1, secretKey1);
      byte[] arrayOfByte3 = this.cipher.doFinal(arrayOfByte1, 0, 8);
      this.cipher.init(1, secretKey2);
      byte[] arrayOfByte4 = this.cipher.doFinal(arrayOfByte1, 0, 8);
      byte[] arrayOfByte5 = new byte[21];
      System.arraycopy(arrayOfByte3, 0, arrayOfByte5, 0, 8);
      System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 8, 8);
      return arrayOfByte5;
    } catch (InvalidKeyException invalidKeyException) {
      assert false;
    } catch (InvalidKeySpecException invalidKeySpecException) {
      assert false;
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      assert false;
    } catch (BadPaddingException badPaddingException) {
      assert false;
    } 
    return null;
  }
  
  byte[] calcNTHash(byte[] paramArrayOfByte) {
    byte[] arrayOfByte1 = this.md4.digest(paramArrayOfByte);
    byte[] arrayOfByte2 = new byte[21];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, 16);
    return arrayOfByte2;
  }
  
  byte[] calcResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    try {
      assert paramArrayOfByte1.length == 21;
      DESKeySpec dESKeySpec1 = new DESKeySpec(makeDesKey(paramArrayOfByte1, 0));
      DESKeySpec dESKeySpec2 = new DESKeySpec(makeDesKey(paramArrayOfByte1, 7));
      DESKeySpec dESKeySpec3 = new DESKeySpec(makeDesKey(paramArrayOfByte1, 14));
      SecretKey secretKey1 = this.fac.generateSecret(dESKeySpec1);
      SecretKey secretKey2 = this.fac.generateSecret(dESKeySpec2);
      SecretKey secretKey3 = this.fac.generateSecret(dESKeySpec3);
      this.cipher.init(1, secretKey1);
      byte[] arrayOfByte1 = this.cipher.doFinal(paramArrayOfByte2, 0, 8);
      this.cipher.init(1, secretKey2);
      byte[] arrayOfByte2 = this.cipher.doFinal(paramArrayOfByte2, 0, 8);
      this.cipher.init(1, secretKey3);
      byte[] arrayOfByte3 = this.cipher.doFinal(paramArrayOfByte2, 0, 8);
      byte[] arrayOfByte4 = new byte[24];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte4, 0, 8);
      System.arraycopy(arrayOfByte2, 0, arrayOfByte4, 8, 8);
      System.arraycopy(arrayOfByte3, 0, arrayOfByte4, 16, 8);
      return arrayOfByte4;
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      assert false;
    } catch (BadPaddingException badPaddingException) {
      assert false;
    } catch (InvalidKeySpecException invalidKeySpecException) {
      assert false;
    } catch (InvalidKeyException invalidKeyException) {
      assert false;
    } 
    return null;
  }
  
  byte[] hmacMD5(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(Arrays.copyOf(paramArrayOfByte1, 16), "HmacMD5");
      this.hmac.init(secretKeySpec);
      return this.hmac.doFinal(paramArrayOfByte2);
    } catch (InvalidKeyException invalidKeyException) {
      assert false;
    } catch (RuntimeException runtimeException) {
      assert false;
    } 
    return null;
  }
  
  byte[] calcV2(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
    try {
      byte[] arrayOfByte1 = hmacMD5(paramArrayOfByte1, paramString.getBytes("UnicodeLittleUnmarked"));
      byte[] arrayOfByte2 = new byte[paramArrayOfByte2.length + 8];
      System.arraycopy(paramArrayOfByte3, 0, arrayOfByte2, 0, 8);
      System.arraycopy(paramArrayOfByte2, 0, arrayOfByte2, 8, paramArrayOfByte2.length);
      byte[] arrayOfByte3 = new byte[16 + paramArrayOfByte2.length];
      System.arraycopy(hmacMD5(arrayOfByte1, arrayOfByte2), 0, arrayOfByte3, 0, 16);
      System.arraycopy(paramArrayOfByte2, 0, arrayOfByte3, 16, paramArrayOfByte2.length);
      return arrayOfByte3;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      assert false;
      return null;
    } 
  }
  
  static byte[] ntlm2LM(byte[] paramArrayOfByte) { return Arrays.copyOf(paramArrayOfByte, 24); }
  
  byte[] ntlm2NTLM(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
    byte[] arrayOfByte1 = Arrays.copyOf(paramArrayOfByte3, 16);
    System.arraycopy(paramArrayOfByte2, 0, arrayOfByte1, 8, 8);
    byte[] arrayOfByte2 = Arrays.copyOf(this.md5.digest(arrayOfByte1), 8);
    return calcResponse(paramArrayOfByte1, arrayOfByte2);
  }
  
  static byte[] getP1(char[] paramArrayOfChar) {
    try {
      return (new String(paramArrayOfChar)).toUpperCase(Locale.ENGLISH).getBytes("ISO8859_1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return null;
    } 
  }
  
  static byte[] getP2(char[] paramArrayOfChar) {
    try {
      return (new String(paramArrayOfChar)).getBytes("UnicodeLittleUnmarked");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return null;
    } 
  }
  
  static class Reader {
    private final byte[] internal;
    
    Reader(byte[] param1ArrayOfByte) { this.internal = param1ArrayOfByte; }
    
    int readInt(int param1Int) throws NTLMException {
      try {
        return (this.internal[param1Int] & 0xFF) + ((this.internal[param1Int + 1] & 0xFF) << 8) + ((this.internal[param1Int + 2] & 0xFF) << 16) + ((this.internal[param1Int + 3] & 0xFF) << 24);
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new NTLMException(1, "Input message incorrect size");
      } 
    }
    
    int readShort(int param1Int) throws NTLMException {
      try {
        return (this.internal[param1Int] & 0xFF) + (this.internal[param1Int + 1] & 0xFF00);
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new NTLMException(1, "Input message incorrect size");
      } 
    }
    
    byte[] readBytes(int param1Int1, int param1Int2) throws NTLMException {
      try {
        return Arrays.copyOfRange(this.internal, param1Int1, param1Int1 + param1Int2);
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new NTLMException(1, "Input message incorrect size");
      } 
    }
    
    byte[] readSecurityBuffer(int param1Int) throws NTLMException {
      int i = readInt(param1Int + 4);
      if (i == 0)
        return null; 
      try {
        return Arrays.copyOfRange(this.internal, i, i + readShort(param1Int));
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new NTLMException(1, "Input message incorrect size");
      } 
    }
    
    String readSecurityBuffer(int param1Int, boolean param1Boolean) throws NTLMException {
      byte[] arrayOfByte = readSecurityBuffer(param1Int);
      try {
        return (arrayOfByte == null) ? null : new String(arrayOfByte, param1Boolean ? "UnicodeLittleUnmarked" : "ISO8859_1");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new NTLMException(1, "Invalid input encoding");
      } 
    }
  }
  
  static class Writer {
    private byte[] internal;
    
    private int current;
    
    Writer(int param1Int1, int param1Int2) {
      assert param1Int2 < 256;
      this.internal = new byte[256];
      this.current = param1Int2;
      System.arraycopy(new byte[] { 78, 84, 76, 77, 83, 83, 80, 0, (byte)param1Int1 }, 0, this.internal, 0, 9);
    }
    
    void writeShort(int param1Int1, int param1Int2) {
      this.internal[param1Int1] = (byte)param1Int2;
      this.internal[param1Int1 + 1] = (byte)(param1Int2 >> 8);
    }
    
    void writeInt(int param1Int1, int param1Int2) {
      this.internal[param1Int1] = (byte)param1Int2;
      this.internal[param1Int1 + 1] = (byte)(param1Int2 >> 8);
      this.internal[param1Int1 + 2] = (byte)(param1Int2 >> 16);
      this.internal[param1Int1 + 3] = (byte)(param1Int2 >> 24);
    }
    
    void writeBytes(int param1Int, byte[] param1ArrayOfByte) { System.arraycopy(param1ArrayOfByte, 0, this.internal, param1Int, param1ArrayOfByte.length); }
    
    void writeSecurityBuffer(int param1Int, byte[] param1ArrayOfByte) {
      if (param1ArrayOfByte == null) {
        writeShort(param1Int + 4, this.current);
      } else {
        int i = param1ArrayOfByte.length;
        if (this.current + i > this.internal.length)
          this.internal = Arrays.copyOf(this.internal, this.current + i + 256); 
        writeShort(param1Int, i);
        writeShort(param1Int + 2, i);
        writeShort(param1Int + 4, this.current);
        System.arraycopy(param1ArrayOfByte, 0, this.internal, this.current, i);
        this.current += i;
      } 
    }
    
    void writeSecurityBuffer(int param1Int, String param1String, boolean param1Boolean) {
      try {
        writeSecurityBuffer(param1Int, (param1String == null) ? null : param1String.getBytes(param1Boolean ? "UnicodeLittleUnmarked" : "ISO8859_1"));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        assert false;
      } 
    }
    
    byte[] getBytes() { return Arrays.copyOf(this.internal, this.current); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\ntlm\NTLM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */