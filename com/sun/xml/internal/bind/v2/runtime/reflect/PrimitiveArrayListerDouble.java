package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

final class PrimitiveArrayListerDouble<BeanT> extends Lister<BeanT, double[], Double, PrimitiveArrayListerDouble.DoubleArrayPack> {
  static void register() { Lister.primitiveArrayListers.put(double.class, new PrimitiveArrayListerDouble()); }
  
  public ListIterator<Double> iterator(final double[] objects, XMLSerializer paramXMLSerializer) { return new ListIterator<Double>() {
        int idx = 0;
        
        public boolean hasNext() { return (this.idx < objects.length); }
        
        public Double next() { return Double.valueOf(objects[this.idx++]); }
      }; }
  
  public DoubleArrayPack startPacking(BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor) { return new DoubleArrayPack(); }
  
  public void addToPack(DoubleArrayPack paramDoubleArrayPack, Double paramDouble) { paramDoubleArrayPack.add(paramDouble); }
  
  public void endPacking(DoubleArrayPack paramDoubleArrayPack, BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, paramDoubleArrayPack.build()); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, double[]> paramAccessor) throws AccessorException { paramAccessor.set(paramBeanT, new double[0]); }
  
  static final class DoubleArrayPack {
    double[] buf = new double[16];
    
    int size;
    
    void add(Double param1Double) {
      if (this.buf.length == this.size) {
        double[] arrayOfDouble = new double[this.buf.length * 2];
        System.arraycopy(this.buf, 0, arrayOfDouble, 0, this.buf.length);
        this.buf = arrayOfDouble;
      } 
      if (param1Double != null)
        this.buf[this.size++] = param1Double.doubleValue(); 
    }
    
    double[] build() {
      if (this.buf.length == this.size)
        return this.buf; 
      double[] arrayOfDouble = new double[this.size];
      System.arraycopy(this.buf, 0, arrayOfDouble, 0, this.size);
      return arrayOfDouble;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\PrimitiveArrayListerDouble.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */