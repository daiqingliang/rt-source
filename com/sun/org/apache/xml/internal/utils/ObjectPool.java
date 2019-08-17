package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.Serializable;
import java.util.ArrayList;

public class ObjectPool implements Serializable {
  static final long serialVersionUID = -8519013691660936643L;
  
  private final Class objectType;
  
  private final ArrayList freeStack;
  
  public ObjectPool(Class paramClass) {
    this.objectType = paramClass;
    this.freeStack = new ArrayList();
  }
  
  public ObjectPool(String paramString) {
    try {
      this.objectType = ObjectFactory.findProviderClass(paramString, true);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new WrappedRuntimeException(classNotFoundException);
    } 
    this.freeStack = new ArrayList();
  }
  
  public ObjectPool(Class paramClass, int paramInt) {
    this.objectType = paramClass;
    this.freeStack = new ArrayList(paramInt);
  }
  
  public ObjectPool() {
    this.objectType = null;
    this.freeStack = new ArrayList();
  }
  
  public Object getInstanceIfFree() { return !this.freeStack.isEmpty() ? this.freeStack.remove(this.freeStack.size() - 1) : null; }
  
  public Object getInstance() {
    if (this.freeStack.isEmpty()) {
      try {
        return this.objectType.newInstance();
      } catch (InstantiationException instantiationException) {
      
      } catch (IllegalAccessException illegalAccessException) {}
      throw new RuntimeException(XMLMessages.createXMLMessage("ER_EXCEPTION_CREATING_POOL", null));
    } 
    return this.freeStack.remove(this.freeStack.size() - 1);
  }
  
  public void freeInstance(Object paramObject) { this.freeStack.add(paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\ObjectPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */