package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

public final class ListTransducedAccessorImpl<BeanT, ListT, ItemT, PackT> extends DefaultTransducedAccessor<BeanT> {
  private final Transducer<ItemT> xducer;
  
  private final Lister<BeanT, ListT, ItemT, PackT> lister;
  
  private final Accessor<BeanT, ListT> acc;
  
  public ListTransducedAccessorImpl(Transducer<ItemT> paramTransducer, Accessor<BeanT, ListT> paramAccessor, Lister<BeanT, ListT, ItemT, PackT> paramLister) {
    this.xducer = paramTransducer;
    this.lister = paramLister;
    this.acc = paramAccessor;
  }
  
  public boolean useNamespace() { return this.xducer.useNamespace(); }
  
  public void declareNamespace(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws AccessorException, SAXException {
    Object object = this.acc.get(paramBeanT);
    if (object != null) {
      ListIterator listIterator = this.lister.iterator(object, paramXMLSerializer);
      while (listIterator.hasNext()) {
        try {
          Object object1 = listIterator.next();
          if (object1 != null)
            this.xducer.declareNamespace(object1, paramXMLSerializer); 
        } catch (JAXBException jAXBException) {
          paramXMLSerializer.reportError(null, jAXBException);
        } 
      } 
    } 
  }
  
  public String print(BeanT paramBeanT) throws AccessorException, SAXException {
    Object object = this.acc.get(paramBeanT);
    if (object == null)
      return null; 
    StringBuilder stringBuilder = new StringBuilder();
    XMLSerializer xMLSerializer = XMLSerializer.getInstance();
    ListIterator listIterator = this.lister.iterator(object, xMLSerializer);
    while (listIterator.hasNext()) {
      try {
        Object object1 = listIterator.next();
        if (object1 != null) {
          if (stringBuilder.length() > 0)
            stringBuilder.append(' '); 
          stringBuilder.append(this.xducer.print(object1));
        } 
      } catch (JAXBException jAXBException) {
        xMLSerializer.reportError(null, jAXBException);
      } 
    } 
    return stringBuilder.toString();
  }
  
  private void processValue(BeanT paramBeanT, CharSequence paramCharSequence) throws AccessorException, SAXException {
    Object object = this.lister.startPacking(paramBeanT, this.acc);
    byte b = 0;
    int i = paramCharSequence.length();
    while (true) {
      byte b1;
      for (b1 = b; b1 < i && !WhiteSpaceProcessor.isWhiteSpace(paramCharSequence.charAt(b1)); b1++);
      CharSequence charSequence = paramCharSequence.subSequence(b, b1);
      if (!charSequence.equals(""))
        this.lister.addToPack(object, this.xducer.parse(charSequence)); 
      if (b1 == i)
        break; 
      while (b1 < i && WhiteSpaceProcessor.isWhiteSpace(paramCharSequence.charAt(b1)))
        b1++; 
      if (b1 == i)
        break; 
      b = b1;
    } 
    this.lister.endPacking(object, paramBeanT, this.acc);
  }
  
  public void parse(BeanT paramBeanT, CharSequence paramCharSequence) throws AccessorException, SAXException { processValue(paramBeanT, paramCharSequence); }
  
  public boolean hasValue(BeanT paramBeanT) throws AccessorException { return (this.acc.get(paramBeanT) != null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\ListTransducedAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */