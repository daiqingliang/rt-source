package com.sun.corba.se.impl.orb;

import java.util.Properties;

public interface ParserAction {
  String getPropertyName();
  
  boolean isPrefix();
  
  String getFieldName();
  
  Object apply(Properties paramProperties);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orb\ParserAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */