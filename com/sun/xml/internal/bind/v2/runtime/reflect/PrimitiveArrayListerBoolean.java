package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

final class PrimitiveArrayListerBoolean<BeanT> extends Lister<BeanT, boolean[], Boolean, PrimitiveArrayListerBoolean.BooleanArrayPack> {
  static void register() { Lister.primitiveArrayListers.put(boolean.class, new PrimitiveArrayListerBoolean()); }
  
  public ListIterator<Boolean> iterator(final boolean[] objects, XMLSerializer paramXMLSerializer) { return new ListIterator<Boolean>() {
        int idx = 0;
        
        public boolean hasNext() { return (this.idx < objects.length); }
        
        public Boolean next() { return Boolean.valueOf(objects[this.idx++]); }
      }; }
  
  public BooleanArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, boolean[]> paramAccessor) { return new BooleanArrayPack(); }
  
  public void addToPack(BooleanArrayPack paramBooleanArrayPack, Boolean paramBoolean) { paramBooleanArrayPack.add(paramBoolean); }
  
  public void endPacking(BooleanArrayPack paramBooleanArrayPack, BeanT paramBeanT, Accessor<BeanT, boolean[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, paramBooleanArrayPack.build()); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, boolean[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, new boolean[0]); }
  
  static final class BooleanArrayPack {
    boolean[] buf = new boolean[16];
    
    int size;
    
    void add(Boolean param1Boolean) {
      if (this.buf.length == this.size) {
        boolean[] arrayOfBoolean = new boolean[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfBoolean, 0, this.buf.length);
        this.buf = arrayOfBoolean;
      } 
      if (param1Boolean != null)
        this.buf[this.size++] = param1Boolean.booleanValue(); 
    }
    
    boolean[] build() {
      if (this.buf.length == this.size)
        return this.buf; 
      boolean[] arrayOfBoolean = new boolean[this.size];
      System.arraycopy(this.buf, 0, arrayOfBoolean, 0, this.size);
      return arrayOfBoolean;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */