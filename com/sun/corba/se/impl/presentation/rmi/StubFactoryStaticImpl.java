package com.sun.corba.se.impl.presentation.rmi;

import org.omg.CORBA.Object;

public class StubFactoryStaticImpl extends StubFactoryBase {
  private Class stubClass;
  
  public StubFactoryStaticImpl(Class paramClass) {
    super(null);
    this.stubClass = paramClass;
  }
  
  public Object makeStub() {
    Object object = null;
    try {
      object = (Object)this.stubClass.newInstance();
    } catch (InstantiationException instantiationException) {
      throw new RuntimeException(instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException);
    } 
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryStaticImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */