package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

final class PrimitiveArrayListerInteger<BeanT> extends Lister<BeanT, int[], Integer, PrimitiveArrayListerInteger.IntegerArrayPack> {
  static void register() { Lister.primitiveArrayListers.put(int.class, new PrimitiveArrayListerInteger()); }
  
  public ListIterator<Integer> iterator(final int[] objects, XMLSerializer paramXMLSerializer) { return new ListIterator<Integer>() {
        int idx = 0;
        
        public boolean hasNext() { return (this.idx < objects.length); }
        
        public Integer next() { return Integer.valueOf(objects[this.idx++]); }
      }; }
  
  public IntegerArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, int[]> paramAccessor) { return new IntegerArrayPack(); }
  
  public void addToPack(IntegerArrayPack paramIntegerArrayPack, Integer paramInteger) { paramIntegerArrayPack.add(paramInteger); }
  
  public void endPacking(IntegerArrayPack paramIntegerArrayPack, BeanT paramBeanT, Accessor<BeanT, int[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, paramIntegerArrayPack.build()); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, int[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, new int[0]); }
  
  static final class IntegerArrayPack {
    int[] buf = new int[16];
    
    int size;
    
    void add(Integer param1Integer) {
      if (this.buf.length == this.size) {
        int[] arrayOfInt = new int[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfInt, 0, this.buf.length);
        this.buf = arrayOfInt;
      } 
      if (param1Integer != null)
        this.buf[this.size++] = param1Integer.intValue(); 
    }
    
    int[] build() {
      if (this.buf.length == this.size)
        return this.buf; 
      int[] arrayOfInt = new int[this.size];
      System.arraycopy(this.buf, 0, arrayOfInt, 0, this.size);
      return arrayOfInt;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */