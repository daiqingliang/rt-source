package com.sun.corba.se.spi.orb;

import java.util.Properties;

public interface ParserData {
  String getPropertyName();
  
  Operation getOperation();
  
  String getFieldName();
  
  Object getDefaultValue();
  
  Object getTestValue();
  
  void addToParser(PropertyParser paramPropertyParser);
  
  void addToProperties(Properties paramProperties);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orb\ParserData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */