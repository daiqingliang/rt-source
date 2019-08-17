package com.sun.org.apache.xerces.internal.xs.datatypes;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public interface XSDateTime {
  int getYears();
  
  int getMonths();
  
  int getDays();
  
  int getHours();
  
  int getMinutes();
  
  double getSeconds();
  
  boolean hasTimeZone();
  
  int getTimeZoneHours();
  
  int getTimeZoneMinutes();
  
  String getLexicalValue();
  
  XSDateTime normalize();
  
  boolean isNormalized();
  
  XMLGregorianCalendar getXMLGregorianCalendar();
  
  Duration getDuration();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\datatypes\XSDateTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */