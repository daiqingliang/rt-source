package com.sun.xml.internal.bind.v2.runtime.reflect;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class AdaptedAccessor<BeanT, InMemValueT, OnWireValueT> extends Accessor<BeanT, OnWireValueT> {
  private final Accessor<BeanT, InMemValueT> core;
  
  private final Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> adapter;
  
  private XmlAdapter<OnWireValueT, InMemValueT> staticAdapter;
  
  AdaptedAccessor(Class<OnWireValueT> paramClass1, Accessor<BeanT, InMemValueT> paramAccessor, Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> paramClass2) {
    super(paramClass1);
    this.core = paramAccessor;
    this.adapter = paramClass2;
  }
  
  public boolean isAdapted() { return true; }
  
  public OnWireValueT get(BeanT paramBeanT) throws AccessorException {
    Object object = this.core.get(paramBeanT);
    XmlAdapter xmlAdapter = getAdapter();
    try {
      return (OnWireValueT)xmlAdapter.marshal(object);
    } catch (Exception exception) {
      throw new AccessorException(exception);
    } 
  }
  
  public void set(BeanT paramBeanT, OnWireValueT paramOnWireValueT) throws AccessorException {
    XmlAdapter xmlAdapter = getAdapter();
    try {
      this.core.set(paramBeanT, (paramOnWireValueT == null) ? null : xmlAdapter.unmarshal(paramOnWireValueT));
    } catch (Exception exception) {
      throw new AccessorException(exception);
    } 
  }
  
  public Object getUnadapted(BeanT paramBeanT) throws AccessorException { return this.core.getUnadapted(paramBeanT); }
  
  public void setUnadapted(BeanT paramBeanT, Object paramObject) throws AccessorException { this.core.setUnadapted(paramBeanT, paramObject); }
  
  private XmlAdapter<OnWireValueT, InMemValueT> getAdapter() {
    Coordinator coordinator = Coordinator._getInstance();
    if (coordinator != null)
      return coordinator.getAdapter(this.adapter); 
    synchronized (this) {
      if (this.staticAdapter == null)
        this.staticAdapter = (XmlAdapter)ClassFactory.create(this.adapter); 
    } 
    return this.staticAdapter;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\AdaptedAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */