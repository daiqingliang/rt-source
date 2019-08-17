package com.sun.security.sasl.util;

import java.util.Map;

public final class PolicyUtils {
  public static final int NOPLAINTEXT = 1;
  
  public static final int NOACTIVE = 2;
  
  public static final int NODICTIONARY = 4;
  
  public static final int FORWARD_SECRECY = 8;
  
  public static final int NOANONYMOUS = 16;
  
  public static final int PASS_CREDENTIALS = 512;
  
  public static boolean checkPolicy(int paramInt, Map<String, ?> paramMap) { return (paramMap == null) ? true : (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.noplaintext")) && (paramInt & true) == 0) ? false : (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.noactive")) && (paramInt & 0x2) == 0) ? false : (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.nodictionary")) && (paramInt & 0x4) == 0) ? false : (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.noanonymous")) && (paramInt & 0x10) == 0) ? false : (("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.forward")) && (paramInt & 0x8) == 0) ? false : (!("true".equalsIgnoreCase((String)paramMap.get("javax.security.sasl.policy.credentials")) && (paramInt & 0x200) == 0))))))); }
  
  public static String[] filterMechs(String[] paramArrayOfString, int[] paramArrayOfInt, Map<String, ?> paramMap) {
    if (paramMap == null)
      return (String[])paramArrayOfString.clone(); 
    boolean[] arrayOfBoolean = new boolean[paramArrayOfString.length];
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramArrayOfString.length; b2++) {
      arrayOfBoolean[b2] = checkPolicy(paramArrayOfInt[b2], paramMap);
      if (checkPolicy(paramArrayOfInt[b2], paramMap))
        b1++; 
    } 
    String[] arrayOfString = new String[b1];
    byte b3 = 0;
    byte b4 = 0;
    while (b3 < paramArrayOfString.length) {
      if (arrayOfBoolean[b3])
        arrayOfString[b4++] = paramArrayOfString[b3]; 
      b3++;
    } 
    return arrayOfString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\sas\\util\PolicyUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */