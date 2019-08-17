package com.sun.security.ntlm;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public final class Client extends NTLM {
  private final String hostname;
  
  private final String username;
  
  private String domain;
  
  private byte[] pw1;
  
  private byte[] pw2;
  
  public Client(String paramString1, String paramString2, String paramString3, String paramString4, char[] paramArrayOfChar) throws NTLMException {
    super(paramString1);
    if (paramString3 == null || paramArrayOfChar == null)
      throw new NTLMException(6, "username/password cannot be null"); 
    this.hostname = paramString2;
    this.username = paramString3;
    this.domain = (paramString4 == null) ? "" : paramString4;
    this.pw1 = getP1(paramArrayOfChar);
    this.pw2 = getP2(paramArrayOfChar);
    debug("NTLM Client: (h,u,t,version(v)) = (%s,%s,%s,%s(%s))\n", new Object[] { paramString2, paramString3, paramString4, paramString1, this.v.toString() });
  }
  
  public byte[] type1() {
    NTLM.Writer writer = new NTLM.Writer(1, 32);
    int i = 33287;
    if (this.v != Version.NTLM)
      i |= 0x80000; 
    writer.writeInt(12, i);
    debug("NTLM Client: Type 1 created\n", new Object[0]);
    debug(writer.getBytes());
    return writer.getBytes();
  }
  
  public byte[] type3(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws NTLMException {
    if (paramArrayOfByte1 == null || (this.v != Version.NTLM && paramArrayOfByte2 == null))
      throw new NTLMException(6, "type2 and nonce cannot be null"); 
    debug("NTLM Client: Type 2 received\n", new Object[0]);
    debug(paramArrayOfByte1);
    NTLM.Reader reader = new NTLM.Reader(paramArrayOfByte1);
    byte[] arrayOfByte1 = reader.readBytes(24, 8);
    int i = reader.readInt(20);
    boolean bool = ((i & true) == 1);
    int j = 0x88200 | i & 0x3;
    NTLM.Writer writer = new NTLM.Writer(3, 64);
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte3 = null;
    writer.writeSecurityBuffer(28, this.domain, bool);
    writer.writeSecurityBuffer(36, this.username, bool);
    writer.writeSecurityBuffer(44, this.hostname, bool);
    if (this.v == Version.NTLM) {
      byte[] arrayOfByte4 = calcLMHash(this.pw1);
      byte[] arrayOfByte5 = calcNTHash(this.pw2);
      if (this.writeLM)
        arrayOfByte2 = calcResponse(arrayOfByte4, arrayOfByte1); 
      if (this.writeNTLM)
        arrayOfByte3 = calcResponse(arrayOfByte5, arrayOfByte1); 
    } else if (this.v == Version.NTLM2) {
      byte[] arrayOfByte = calcNTHash(this.pw2);
      arrayOfByte2 = ntlm2LM(paramArrayOfByte2);
      arrayOfByte3 = ntlm2NTLM(arrayOfByte, paramArrayOfByte2, arrayOfByte1);
    } else {
      byte[] arrayOfByte = calcNTHash(this.pw2);
      if (this.writeLM)
        arrayOfByte2 = calcV2(arrayOfByte, this.username.toUpperCase(Locale.US) + this.domain, paramArrayOfByte2, arrayOfByte1); 
      if (this.writeNTLM) {
        byte[] arrayOfByte4 = ((i & 0x800000) != 0) ? reader.readSecurityBuffer(40) : new byte[0];
        byte[] arrayOfByte5 = new byte[32 + arrayOfByte4.length];
        System.arraycopy(new byte[] { 1, 1, 0, 0, 0, 0, 0, 0 }, 0, arrayOfByte5, 0, 8);
        byte[] arrayOfByte6 = BigInteger.valueOf((new Date()).getTime()).add(new BigInteger("11644473600000")).multiply(BigInteger.valueOf(10000L)).toByteArray();
        for (int k = 0; k < arrayOfByte6.length; k++)
          arrayOfByte5[8 + arrayOfByte6.length - k - 1] = arrayOfByte6[k]; 
        System.arraycopy(paramArrayOfByte2, 0, arrayOfByte5, 16, 8);
        System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, arrayOfByte5, 24, 4);
        System.arraycopy(arrayOfByte4, 0, arrayOfByte5, 28, arrayOfByte4.length);
        System.arraycopy(new byte[] { 0, 0, 0, 0 }, 0, arrayOfByte5, 28 + arrayOfByte4.length, 4);
        arrayOfByte3 = calcV2(arrayOfByte, this.username.toUpperCase(Locale.US) + this.domain, arrayOfByte5, arrayOfByte1);
      } 
    } 
    writer.writeSecurityBuffer(12, arrayOfByte2);
    writer.writeSecurityBuffer(20, arrayOfByte3);
    writer.writeSecurityBuffer(52, new byte[0]);
    writer.writeInt(60, j);
    debug("NTLM Client: Type 3 created\n", new Object[0]);
    debug(writer.getBytes());
    return writer.getBytes();
  }
  
  public String getDomain() { return this.domain; }
  
  public void dispose() {
    Arrays.fill(this.pw1, (byte)0);
    Arrays.fill(this.pw2, (byte)0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\ntlm\Client.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */