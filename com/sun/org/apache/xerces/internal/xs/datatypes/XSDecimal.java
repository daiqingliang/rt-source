package com.sun.org.apache.xerces.internal.xs.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface XSDecimal {
  BigDecimal getBigDecimal();
  
  BigInteger getBigInteger() throws NumberFormatException;
  
  long getLong() throws NumberFormatException;
  
  int getInt() throws NumberFormatException;
  
  short getShort() throws NumberFormatException;
  
  byte getByte() throws NumberFormatException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\datatypes\XSDecimal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */