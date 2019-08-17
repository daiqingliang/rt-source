package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

final class PrimitiveArrayListerFloat<BeanT> extends Lister<BeanT, float[], Float, PrimitiveArrayListerFloat.FloatArrayPack> {
  static void register() { Lister.primitiveArrayListers.put(float.class, new PrimitiveArrayListerFloat()); }
  
  public ListIterator<Float> iterator(final float[] objects, XMLSerializer paramXMLSerializer) { return new ListIterator<Float>() {
        int idx = 0;
        
        public boolean hasNext() { return (this.idx < objects.length); }
        
        public Float next() { return Float.valueOf(objects[this.idx++]); }
      }; }
  
  public FloatArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, float[]> paramAccessor) { return new FloatArrayPack(); }
  
  public void addToPack(FloatArrayPack paramFloatArrayPack, Float paramFloat) { paramFloatArrayPack.add(paramFloat); }
  
  public void endPacking(FloatArrayPack paramFloatArrayPack, BeanT paramBeanT, Accessor<BeanT, float[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, paramFloatArrayPack.build()); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, float[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, new float[0]); }
  
  static final class FloatArrayPack {
    float[] buf = new float[16];
    
    int size;
    
    void add(Float param1Float) {
      if (this.buf.length == this.size) {
        float[] arrayOfFloat = new float[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfFloat, 0, this.buf.length);
        this.buf = arrayOfFloat;
      } 
      if (param1Float != null)
        this.buf[this.size++] = param1Float.floatValue(); 
    }
    
    float[] build() {
      if (this.buf.length == this.size)
        return this.buf; 
      float[] arrayOfFloat = new float[this.size];
      System.arraycopy(this.buf, 0, arrayOfFloat, 0, this.size);
      return arrayOfFloat;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */