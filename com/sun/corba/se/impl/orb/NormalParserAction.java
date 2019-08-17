package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import java.util.Properties;

public class NormalParserAction extends ParserActionBase {
  public NormalParserAction(String paramString1, Operation paramOperation, String paramString2) { super(paramString1, false, paramOperation, paramString2); }
  
  public Object apply(Properties paramProperties) {
    String str = paramProperties.getProperty(getPropertyName());
    return (str != null) ? getOperation().operate(str) : null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\NormalParserAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */