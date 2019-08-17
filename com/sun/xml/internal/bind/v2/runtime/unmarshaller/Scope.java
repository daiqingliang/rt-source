package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import org.xml.sax.SAXException;

public final class Scope<BeanT, PropT, ItemT, PackT> extends Object {
  public final UnmarshallingContext context;
  
  private BeanT bean;
  
  private Accessor<BeanT, PropT> acc;
  
  private PackT pack;
  
  private Lister<BeanT, PropT, ItemT, PackT> lister;
  
  Scope(UnmarshallingContext paramUnmarshallingContext) { this.context = paramUnmarshallingContext; }
  
  public boolean hasStarted() { return (this.bean != null); }
  
  public void reset() {
    if (this.bean == null) {
      assert clean();
      return;
    } 
    this.bean = null;
    this.acc = null;
    this.pack = null;
    this.lister = null;
  }
  
  public void finish() {
    if (hasStarted()) {
      this.lister.endPacking(this.pack, this.bean, this.acc);
      reset();
    } 
    assert clean();
  }
  
  private boolean clean() { return (this.bean == null && this.acc == null && this.pack == null && this.lister == null); }
  
  public void add(Accessor<BeanT, PropT> paramAccessor, Lister<BeanT, PropT, ItemT, PackT> paramLister, ItemT paramItemT) throws SAXException {
    try {
      if (!hasStarted()) {
        this.bean = this.context.getCurrentState().getTarget();
        this.acc = paramAccessor;
        this.lister = paramLister;
        this.pack = paramLister.startPacking(this.bean, paramAccessor);
      } 
      paramLister.addToPack(this.pack, paramItemT);
    } catch (AccessorException accessorException) {
      Loader.handleGenericException(accessorException, true);
      this.lister = Lister.getErrorInstance();
      this.acc = Accessor.getErrorInstance();
    } 
  }
  
  public void start(Accessor<BeanT, PropT> paramAccessor, Lister<BeanT, PropT, ItemT, PackT> paramLister) throws SAXException {
    try {
      if (!hasStarted()) {
        this.bean = this.context.getCurrentState().getTarget();
        this.acc = paramAccessor;
        this.lister = paramLister;
        this.pack = paramLister.startPacking(this.bean, paramAccessor);
      } 
    } catch (AccessorException accessorException) {
      Loader.handleGenericException(accessorException, true);
      this.lister = Lister.getErrorInstance();
      this.acc = Accessor.getErrorInstance();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\Scope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */