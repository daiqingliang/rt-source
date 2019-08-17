package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

final class PrimitiveArrayListerByte<BeanT> extends Lister<BeanT, byte[], Byte, PrimitiveArrayListerByte.ByteArrayPack> {
  static void register() { Lister.primitiveArrayListers.put(byte.class, new PrimitiveArrayListerByte()); }
  
  public ListIterator<Byte> iterator(final byte[] objects, XMLSerializer paramXMLSerializer) { return new ListIterator<Byte>() {
        int idx = 0;
        
        public boolean hasNext() { return (this.idx < objects.length); }
        
        public Byte next() { return Byte.valueOf(objects[this.idx++]); }
      }; }
  
  public ByteArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, byte[]> paramAccessor) { return new ByteArrayPack(); }
  
  public void addToPack(ByteArrayPack paramByteArrayPack, Byte paramByte) { paramByteArrayPack.add(paramByte); }
  
  public void endPacking(ByteArrayPack paramByteArrayPack, BeanT paramBeanT, Accessor<BeanT, byte[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, paramByteArrayPack.build()); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, byte[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, new byte[0]); }
  
  static final class ByteArrayPack {
    byte[] buf = new byte[16];
    
    int size;
    
    void add(Byte param1Byte) {
      if (this.buf.length == this.size) {
        byte[] arrayOfByte = new byte[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfByte, 0, this.buf.length);
        this.buf = arrayOfByte;
      } 
      if (param1Byte != null)
        this.buf[this.size++] = param1Byte.byteValue(); 
    }
    
    byte[] build() {
      if (this.buf.length == this.size)
        return this.buf; 
      byte[] arrayOfByte = new byte[this.size];
      System.arraycopy(this.buf, 0, arrayOfByte, 0, this.size);
      return arrayOfByte;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerByte.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */