package com.sun.security.sasl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.sasl.SaslException;
import sun.misc.HexDumpEncoder;

public abstract class AbstractSaslImpl {
  protected boolean completed = false;
  
  protected boolean privacy = false;
  
  protected boolean integrity = false;
  
  protected byte[] qop;
  
  protected byte allQop;
  
  protected byte[] strength;
  
  protected int sendMaxBufSize = 0;
  
  protected int recvMaxBufSize = 65536;
  
  protected int rawSendSize;
  
  protected String myClassName;
  
  private static final String SASL_LOGGER_NAME = "javax.security.sasl";
  
  protected static final String MAX_SEND_BUF = "javax.security.sasl.sendmaxbuffer";
  
  protected static final Logger logger = Logger.getLogger("javax.security.sasl");
  
  protected static final byte NO_PROTECTION = 1;
  
  protected static final byte INTEGRITY_ONLY_PROTECTION = 2;
  
  protected static final byte PRIVACY_PROTECTION = 4;
  
  protected static final byte LOW_STRENGTH = 1;
  
  protected static final byte MEDIUM_STRENGTH = 2;
  
  protected static final byte HIGH_STRENGTH = 4;
  
  private static final byte[] DEFAULT_QOP = { 1 };
  
  private static final String[] QOP_TOKENS = { "auth-conf", "auth-int", "auth" };
  
  private static final byte[] QOP_MASKS = { 4, 2, 1 };
  
  private static final byte[] DEFAULT_STRENGTH = { 4, 2, 1 };
  
  private static final String[] STRENGTH_TOKENS = { "low", "medium", "high" };
  
  private static final byte[] STRENGTH_MASKS = { 1, 2, 4 };
  
  protected AbstractSaslImpl(Map<String, ?> paramMap, String paramString) throws SaslException {
    this.myClassName = paramString;
    if (paramMap != null) {
      String str;
      this.qop = parseQop(str = (String)paramMap.get("javax.security.sasl.qop"));
      logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL01:Preferred qop property: {0}", str);
      this.allQop = combineMasks(this.qop);
      if (logger.isLoggable(Level.FINE)) {
        logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL02:Preferred qop mask: {0}", new Byte(this.allQop));
        if (this.qop.length > 0) {
          StringBuffer stringBuffer = new StringBuffer();
          for (byte b = 0; b < this.qop.length; b++) {
            stringBuffer.append(Byte.toString(this.qop[b]));
            stringBuffer.append(' ');
          } 
          logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL03:Preferred qops : {0}", stringBuffer.toString());
        } 
      } 
      this.strength = parseStrength(str = (String)paramMap.get("javax.security.sasl.strength"));
      logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL04:Preferred strength property: {0}", str);
      if (logger.isLoggable(Level.FINE) && this.strength.length > 0) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 0; b < this.strength.length; b++) {
          stringBuffer.append(Byte.toString(this.strength[b]));
          stringBuffer.append(' ');
        } 
        logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL05:Cipher strengths: {0}", stringBuffer.toString());
      } 
      str = (String)paramMap.get("javax.security.sasl.maxbuffer");
      if (str != null)
        try {
          logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL06:Max receive buffer size: {0}", str);
          this.recvMaxBufSize = Integer.parseInt(str);
        } catch (NumberFormatException numberFormatException) {
          throw new SaslException("Property must be string representation of integer: javax.security.sasl.maxbuffer");
        }  
      str = (String)paramMap.get("javax.security.sasl.sendmaxbuffer");
      if (str != null)
        try {
          logger.logp(Level.FINE, this.myClassName, "constructor", "SASLIMPL07:Max send buffer size: {0}", str);
          this.sendMaxBufSize = Integer.parseInt(str);
        } catch (NumberFormatException numberFormatException) {
          throw new SaslException("Property must be string representation of integer: javax.security.sasl.sendmaxbuffer");
        }  
    } else {
      this.qop = DEFAULT_QOP;
      this.allQop = 1;
      this.strength = STRENGTH_MASKS;
    } 
  }
  
  public boolean isComplete() { return this.completed; }
  
  public Object getNegotiatedProperty(String paramString) {
    if (!this.completed)
      throw new IllegalStateException("SASL authentication not completed"); 
    switch (paramString) {
      case "javax.security.sasl.qop":
        return this.privacy ? "auth-conf" : (this.integrity ? "auth-int" : "auth");
      case "javax.security.sasl.maxbuffer":
        return Integer.toString(this.recvMaxBufSize);
      case "javax.security.sasl.rawsendsize":
        return Integer.toString(this.rawSendSize);
      case "javax.security.sasl.sendmaxbuffer":
        return Integer.toString(this.sendMaxBufSize);
    } 
    return null;
  }
  
  protected static final byte combineMasks(byte[] paramArrayOfByte) {
    byte b = 0;
    for (byte b1 = 0; b1 < paramArrayOfByte.length; b1++)
      b = (byte)(b | paramArrayOfByte[b1]); 
    return b;
  }
  
  protected static final byte findPreferredMask(byte paramByte, byte[] paramArrayOfByte) {
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      if ((paramArrayOfByte[b] & paramByte) != 0)
        return paramArrayOfByte[b]; 
    } 
    return 0;
  }
  
  private static final byte[] parseQop(String paramString) throws SaslException { return parseQop(paramString, null, false); }
  
  protected static final byte[] parseQop(String paramString, String[] paramArrayOfString, boolean paramBoolean) throws SaslException { return (paramString == null) ? DEFAULT_QOP : parseProp("javax.security.sasl.qop", paramString, QOP_TOKENS, QOP_MASKS, paramArrayOfString, paramBoolean); }
  
  private static final byte[] parseStrength(String paramString) throws SaslException { return (paramString == null) ? DEFAULT_STRENGTH : parseProp("javax.security.sasl.strength", paramString, STRENGTH_TOKENS, STRENGTH_MASKS, null, false); }
  
  private static final byte[] parseProp(String paramString1, String paramString2, String[] paramArrayOfString1, byte[] paramArrayOfByte, String[] paramArrayOfString2, boolean paramBoolean) throws SaslException {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString2, ", \t\n");
    byte[] arrayOfByte = new byte[paramArrayOfString1.length];
    byte b1 = 0;
    while (stringTokenizer.hasMoreTokens() && b1 < arrayOfByte.length) {
      String str = stringTokenizer.nextToken();
      boolean bool = false;
      for (byte b = 0; !bool && b < paramArrayOfString1.length; b++) {
        if (str.equalsIgnoreCase(paramArrayOfString1[b])) {
          bool = true;
          arrayOfByte[b1++] = paramArrayOfByte[b];
          if (paramArrayOfString2 != null)
            paramArrayOfString2[b] = str; 
        } 
      } 
      if (!bool && !paramBoolean)
        throw new SaslException("Invalid token in " + paramString1 + ": " + paramString2); 
    } 
    for (byte b2 = b1; b2 < arrayOfByte.length; b2++)
      arrayOfByte[b2] = 0; 
    return arrayOfByte;
  }
  
  protected static final void traceOutput(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte) { traceOutput(paramString1, paramString2, paramString3, paramArrayOfByte, 0, (paramArrayOfByte == null) ? 0 : paramArrayOfByte.length); }
  
  protected static final void traceOutput(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    try {
      String str;
      Level level;
      int i = paramInt2;
      if (!logger.isLoggable(Level.FINEST)) {
        paramInt2 = Math.min(16, paramInt2);
        level = Level.FINER;
      } else {
        level = Level.FINEST;
      } 
      if (paramArrayOfByte != null) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(paramInt2);
        (new HexDumpEncoder()).encodeBuffer(new ByteArrayInputStream(paramArrayOfByte, paramInt1, paramInt2), byteArrayOutputStream);
        str = byteArrayOutputStream.toString();
      } else {
        str = "NULL";
      } 
      logger.logp(level, paramString1, paramString2, "{0} ( {1} ): {2}", new Object[] { paramString3, new Integer(i), str });
    } catch (Exception exception) {
      logger.logp(Level.WARNING, paramString1, paramString2, "SASLIMPL09:Error generating trace output: {0}", exception);
    } 
  }
  
  protected static final int networkByteOrderToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt2 > 4)
      throw new IllegalArgumentException("Cannot handle more than 4 bytes"); 
    byte b = 0;
    for (int i = 0; i < paramInt2; i++) {
      b <<= 8;
      b |= paramArrayOfByte[paramInt1 + i] & 0xFF;
    } 
    return b;
  }
  
  protected static final void intToNetworkByteOrder(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
    if (paramInt3 > 4)
      throw new IllegalArgumentException("Cannot handle more than 4 bytes"); 
    for (int i = paramInt3 - 1; i >= 0; i--) {
      paramArrayOfByte[paramInt2 + i] = (byte)(paramInt1 & 0xFF);
      paramInt1 >>>= 8;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sas\\util\AbstractSaslImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */