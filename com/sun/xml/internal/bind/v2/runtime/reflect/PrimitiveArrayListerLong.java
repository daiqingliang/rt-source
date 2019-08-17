package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

final class PrimitiveArrayListerLong<BeanT> extends Lister<BeanT, long[], Long, PrimitiveArrayListerLong.LongArrayPack> {
  static void register() { Lister.primitiveArrayListers.put(long.class, new PrimitiveArrayListerLong()); }
  
  public ListIterator<Long> iterator(final long[] objects, XMLSerializer paramXMLSerializer) { return new ListIterator<Long>() {
        int idx = 0;
        
        public boolean hasNext() { return (this.idx < objects.length); }
        
        public Long next() { return Long.valueOf(objects[this.idx++]); }
      }; }
  
  public LongArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, long[]> paramAccessor) { return new LongArrayPack(); }
  
  public void addToPack(LongArrayPack paramLongArrayPack, Long paramLong) { paramLongArrayPack.add(paramLong); }
  
  public void endPacking(LongArrayPack paramLongArrayPack, BeanT paramBeanT, Accessor<BeanT, long[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, paramLongArrayPack.build()); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, long[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, new long[0]); }
  
  static final class LongArrayPack {
    long[] buf = new long[16];
    
    int size;
    
    void add(Long param1Long) {
      if (this.buf.length == this.size) {
        long[] arrayOfLong = new long[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfLong, 0, this.buf.length);
        this.buf = arrayOfLong;
      } 
      if (param1Long != null)
        this.buf[this.size++] = param1Long.longValue(); 
    }
    
    long[] build() {
      if (this.buf.length == this.size)
        return this.buf; 
      long[] arrayOfLong = new long[this.size];
      System.arraycopy(this.buf, 0, arrayOfLong, 0, this.size);
      return arrayOfLong;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerLong.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */