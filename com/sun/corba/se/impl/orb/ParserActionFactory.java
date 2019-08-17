package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;

public class ParserActionFactory {
  public static ParserAction makeNormalAction(String paramString1, Operation paramOperation, String paramString2) { return new NormalParserAction(paramString1, paramOperation, paramString2); }
  
  public static ParserAction makePrefixAction(String paramString1, Operation paramOperation, String paramString2, Class paramClass) { return new PrefixParserAction(paramString1, paramOperation, paramString2, paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ParserActionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */