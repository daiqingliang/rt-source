package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

final class PrimitiveArrayListerShort<BeanT> extends Lister<BeanT, short[], Short, PrimitiveArrayListerShort.ShortArrayPack> {
  static void register() { Lister.primitiveArrayListers.put(short.class, new PrimitiveArrayListerShort()); }
  
  public ListIterator<Short> iterator(final short[] objects, XMLSerializer paramXMLSerializer) { return new ListIterator<Short>() {
        int idx = 0;
        
        public boolean hasNext() { return (this.idx < objects.length); }
        
        public Short next() { return Short.valueOf(objects[this.idx++]); }
      }; }
  
  public ShortArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, short[]> paramAccessor) { return new ShortArrayPack(); }
  
  public void addToPack(ShortArrayPack paramShortArrayPack, Short paramShort) { paramShortArrayPack.add(paramShort); }
  
  public void endPacking(ShortArrayPack paramShortArrayPack, BeanT paramBeanT, Accessor<BeanT, short[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, paramShortArrayPack.build()); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, short[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, new short[0]); }
  
  static final class ShortArrayPack {
    short[] buf = new short[16];
    
    int size;
    
    void add(Short param1Short) {
      if (this.buf.length == this.size) {
        short[] arrayOfShort = new short[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfShort, 0, this.buf.length);
        this.buf = arrayOfShort;
      } 
      if (param1Short != null)
        this.buf[this.size++] = param1Short.shortValue(); 
    }
    
    short[] build() {
      if (this.buf.length == this.size)
        return this.buf; 
      short[] arrayOfShort = new short[this.size];
      System.arraycopy(this.buf, 0, arrayOfShort, 0, this.size);
      return arrayOfShort;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerShort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */