package com.sun.org.apache.regexp.internal;

public class REUtil {
  private static final String complexPrefix = "complex:";
  
  public static RE createRE(String paramString, int paramInt) throws RESyntaxException { return paramString.startsWith("complex:") ? new RE(paramString.substring("complex:".length()), paramInt) : new RE(RE.simplePatternToFullRegularExpression(paramString), paramInt); }
  
  public static RE createRE(String paramString) throws RESyntaxException { return createRE(paramString, 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\REUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */