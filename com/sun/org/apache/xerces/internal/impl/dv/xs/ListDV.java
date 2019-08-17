package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.util.AbstractList;

public class ListDV extends TypeValidator {
  public short getAllowedFacets() { return 2079; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException { return paramString; }
  
  public int getDataLength(Object paramObject) { return ((ListData)paramObject).getLength(); }
  
  static final class ListData extends AbstractList implements ObjectList {
    final Object[] data;
    
    private String canonical;
    
    public ListData(Object[] param1ArrayOfObject) { this.data = param1ArrayOfObject; }
    
    public String toString() {
      if (this.canonical == null) {
        int i = this.data.length;
        StringBuffer stringBuffer = new StringBuffer();
        if (i > 0)
          stringBuffer.append(this.data[0].toString()); 
        for (byte b = 1; b < i; b++) {
          stringBuffer.append(' ');
          stringBuffer.append(this.data[b].toString());
        } 
        this.canonical = stringBuffer.toString();
      } 
      return this.canonical;
    }
    
    public int getLength() { return this.data.length; }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof ListData))
        return false; 
      Object[] arrayOfObject = ((ListData)param1Object).data;
      int i = this.data.length;
      if (i != arrayOfObject.length)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (!this.data[b].equals(arrayOfObject[b]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int i = 0;
      for (byte b = 0; b < this.data.length; b++)
        i ^= this.data[b].hashCode(); 
      return i;
    }
    
    public boolean contains(Object param1Object) {
      for (byte b = 0; b < this.data.length; b++) {
        if (param1Object == this.data[b])
          return true; 
      } 
      return false;
    }
    
    public Object item(int param1Int) { return (param1Int < 0 || param1Int >= this.data.length) ? null : this.data[param1Int]; }
    
    public Object get(int param1Int) {
      if (param1Int >= 0 && param1Int < this.data.length)
        return this.data[param1Int]; 
      throw new IndexOutOfBoundsException("Index: " + param1Int);
    }
    
    public int size() { return getLength(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\ListDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */