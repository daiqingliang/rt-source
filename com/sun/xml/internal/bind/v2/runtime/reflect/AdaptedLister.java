package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.xml.sax.SAXException;

final class AdaptedLister<BeanT, PropT, InMemItemT, OnWireItemT, PackT> extends Lister<BeanT, PropT, OnWireItemT, PackT> {
  private final Lister<BeanT, PropT, InMemItemT, PackT> core;
  
  private final Class<? extends XmlAdapter<OnWireItemT, InMemItemT>> adapter;
  
  AdaptedLister(Lister<BeanT, PropT, InMemItemT, PackT> paramLister, Class<? extends XmlAdapter<OnWireItemT, InMemItemT>> paramClass) {
    this.core = paramLister;
    this.adapter = paramClass;
  }
  
  private XmlAdapter<OnWireItemT, InMemItemT> getAdapter() { return Coordinator._getInstance().getAdapter(this.adapter); }
  
  public ListIterator<OnWireItemT> iterator(PropT paramPropT, XMLSerializer paramXMLSerializer) { return new ListIteratorImpl(this.core.iterator(paramPropT, paramXMLSerializer), paramXMLSerializer); }
  
  public PackT startPacking(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor) throws AccessorException { return (PackT)this.core.startPacking(paramBeanT, paramAccessor); }
  
  public void addToPack(PackT paramPackT, OnWireItemT paramOnWireItemT) throws AccessorException {
    Object object;
    try {
      object = getAdapter().unmarshal(paramOnWireItemT);
    } catch (Exception exception) {
      throw new AccessorException(exception);
    } 
    this.core.addToPack(paramPackT, object);
  }
  
  public void endPacking(PackT paramPackT, BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor) throws AccessorException { this.core.endPacking(paramPackT, paramBeanT, paramAccessor); }
  
  public void reset(BeanT paramBeanT, Accessor<BeanT, PropT> paramAccessor) throws AccessorException { this.core.reset(paramBeanT, paramAccessor); }
  
  private final class ListIteratorImpl extends Object implements ListIterator<OnWireItemT> {
    private final ListIterator<InMemItemT> core;
    
    private final XMLSerializer serializer;
    
    public ListIteratorImpl(ListIterator<InMemItemT> param1ListIterator, XMLSerializer param1XMLSerializer) {
      this.core = param1ListIterator;
      this.serializer = param1XMLSerializer;
    }
    
    public boolean hasNext() { return this.core.hasNext(); }
    
    public OnWireItemT next() throws SAXException, JAXBException {
      Object object = this.core.next();
      try {
        return (OnWireItemT)AdaptedLister.this.getAdapter().marshal(object);
      } catch (Exception exception) {
        this.serializer.reportError(null, exception);
        return null;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\AdaptedLister.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */