package com.sun.security.ntlm;

import java.util.Arrays;
import java.util.Locale;

public abstract class Server extends NTLM {
  private final String domain;
  
  private final boolean allVersion;
  
  public Server(String paramString1, String paramString2) throws NTLMException {
    super(paramString1);
    if (paramString2 == null)
      throw new NTLMException(6, "domain cannot be null"); 
    this.allVersion = (paramString1 == null);
    this.domain = paramString2;
    debug("NTLM Server: (t,version) = (%s,%s)\n", new Object[] { paramString2, paramString1 });
  }
  
  public byte[] type2(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws NTLMException {
    if (paramArrayOfByte2 == null)
      throw new NTLMException(6, "nonce cannot be null"); 
    debug("NTLM Server: Type 1 received\n", new Object[0]);
    if (paramArrayOfByte1 != null)
      debug(paramArrayOfByte1); 
    NTLM.Writer writer = new NTLM.Writer(2, 32);
    int i = 590341;
    writer.writeSecurityBuffer(12, this.domain, true);
    writer.writeInt(20, i);
    writer.writeBytes(24, paramArrayOfByte2);
    debug("NTLM Server: Type 2 created\n", new Object[0]);
    debug(writer.getBytes());
    return writer.getBytes();
  }
  
  public String[] verify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws NTLMException {
    if (paramArrayOfByte1 == null || paramArrayOfByte2 == null)
      throw new NTLMException(6, "type1 or nonce cannot be null"); 
    debug("NTLM Server: Type 3 received\n", new Object[0]);
    if (paramArrayOfByte1 != null)
      debug(paramArrayOfByte1); 
    NTLM.Reader reader = new NTLM.Reader(paramArrayOfByte1);
    String str1 = reader.readSecurityBuffer(36, true);
    String str2 = reader.readSecurityBuffer(44, true);
    String str3 = reader.readSecurityBuffer(28, true);
    boolean bool = false;
    char[] arrayOfChar = getPassword(str3, str1);
    if (arrayOfChar == null)
      throw new NTLMException(3, "Unknown user"); 
    byte[] arrayOfByte1 = reader.readSecurityBuffer(12);
    byte[] arrayOfByte2 = reader.readSecurityBuffer(20);
    if (!bool && (this.allVersion || this.v == Version.NTLM)) {
      if (arrayOfByte1.length > 0) {
        byte[] arrayOfByte3 = getP1(arrayOfChar);
        byte[] arrayOfByte4 = calcLMHash(arrayOfByte3);
        byte[] arrayOfByte5 = calcResponse(arrayOfByte4, paramArrayOfByte2);
        if (Arrays.equals(arrayOfByte5, arrayOfByte1))
          bool = true; 
      } 
      if (arrayOfByte2.length > 0) {
        byte[] arrayOfByte3 = getP2(arrayOfChar);
        byte[] arrayOfByte4 = calcNTHash(arrayOfByte3);
        byte[] arrayOfByte5 = calcResponse(arrayOfByte4, paramArrayOfByte2);
        if (Arrays.equals(arrayOfByte5, arrayOfByte2))
          bool = true; 
      } 
      debug("NTLM Server: verify using NTLM: " + bool + "\n", new Object[0]);
    } 
    if (!bool && (this.allVersion || this.v == Version.NTLM2)) {
      byte[] arrayOfByte3 = getP2(arrayOfChar);
      byte[] arrayOfByte4 = calcNTHash(arrayOfByte3);
      byte[] arrayOfByte5 = Arrays.copyOf(arrayOfByte1, 8);
      byte[] arrayOfByte6 = ntlm2NTLM(arrayOfByte4, arrayOfByte5, paramArrayOfByte2);
      if (Arrays.equals(arrayOfByte2, arrayOfByte6))
        bool = true; 
      debug("NTLM Server: verify using NTLM2: " + bool + "\n", new Object[0]);
    } 
    if (!bool && (this.allVersion || this.v == Version.NTLMv2)) {
      byte[] arrayOfByte3 = getP2(arrayOfChar);
      byte[] arrayOfByte4 = calcNTHash(arrayOfByte3);
      if (arrayOfByte1.length > 0) {
        byte[] arrayOfByte5 = Arrays.copyOfRange(arrayOfByte1, 16, arrayOfByte1.length);
        byte[] arrayOfByte6 = calcV2(arrayOfByte4, str1.toUpperCase(Locale.US) + str3, arrayOfByte5, paramArrayOfByte2);
        if (Arrays.equals(arrayOfByte6, arrayOfByte1))
          bool = true; 
      } 
      if (arrayOfByte2.length > 0) {
        byte[] arrayOfByte5 = Arrays.copyOfRange(arrayOfByte2, 16, arrayOfByte2.length);
        byte[] arrayOfByte6 = calcV2(arrayOfByte4, str1.toUpperCase(Locale.US) + str3, arrayOfByte5, paramArrayOfByte2);
        if (Arrays.equals(arrayOfByte6, arrayOfByte2))
          bool = true; 
      } 
      debug("NTLM Server: verify using NTLMv2: " + bool + "\n", new Object[0]);
    } 
    if (!bool)
      throw new NTLMException(4, "None of LM and NTLM verified"); 
    return new String[] { str1, str2, str3 };
  }
  
  public abstract char[] getPassword(String paramString1, String paramString2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\ntlm\Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */