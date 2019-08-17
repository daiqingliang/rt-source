package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;

public class RawAccessorWrapper implements PropertyAccessor {
  private RawAccessor accessor;
  
  public RawAccessorWrapper(RawAccessor paramRawAccessor) { this.accessor = paramRawAccessor; }
  
  public boolean equals(Object paramObject) { return this.accessor.equals(paramObject); }
  
  public Object get(Object paramObject) throws DatabindingException {
    try {
      return this.accessor.get(paramObject);
    } catch (AccessorException accessorException) {
      throw new DatabindingException(accessorException);
    } 
  }
  
  public int hashCode() { return this.accessor.hashCode(); }
  
  public void set(Object paramObject1, Object paramObject2) throws DatabindingException {
    try {
      this.accessor.set(paramObject1, paramObject2);
    } catch (AccessorException accessorException) {
      throw new DatabindingException(accessorException);
    } 
  }
  
  public String toString() { return this.accessor.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\db\glassfish\RawAccessorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */