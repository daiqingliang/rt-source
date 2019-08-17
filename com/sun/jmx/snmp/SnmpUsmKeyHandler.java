package com.sun.jmx.snmp;

public interface SnmpUsmKeyHandler {
  public static final int DES_KEY_SIZE = 16;
  
  public static final int DES_DELTA_SIZE = 16;
  
  byte[] password_to_key(String paramString1, String paramString2) throws IllegalArgumentException;
  
  byte[] localizeAuthKey(String paramString, byte[] paramArrayOfByte, SnmpEngineId paramSnmpEngineId) throws IllegalArgumentException;
  
  byte[] localizePrivKey(String paramString, byte[] paramArrayOfByte, SnmpEngineId paramSnmpEngineId, int paramInt) throws IllegalArgumentException;
  
  byte[] calculateAuthDelta(String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) throws IllegalArgumentException;
  
  byte[] calculatePrivDelta(String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) throws IllegalArgumentException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpUsmKeyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */