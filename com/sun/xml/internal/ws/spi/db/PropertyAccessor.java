package com.sun.xml.internal.ws.spi.db;

public interface PropertyAccessor<B, V> {
  V get(B paramB) throws DatabindingException;
  
  void set(B paramB, V paramV) throws DatabindingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\PropertyAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */