package com.sun.corba.se.spi.presentation.rmi;

import com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryProxyImpl;
import com.sun.corba.se.impl.presentation.rmi.StubFactoryFactoryStaticImpl;
import com.sun.corba.se.impl.presentation.rmi.StubFactoryStaticImpl;

public abstract class PresentationDefaults {
  private static StubFactoryFactoryStaticImpl staticImpl = null;
  
  public static PresentationManager.StubFactoryFactory getStaticStubFactoryFactory() {
    if (staticImpl == null)
      staticImpl = new StubFactoryFactoryStaticImpl(); 
    return staticImpl;
  }
  
  public static PresentationManager.StubFactoryFactory getProxyStubFactoryFactory() { return new StubFactoryFactoryProxyImpl(); }
  
  public static PresentationManager.StubFactory makeStaticStubFactory(Class paramClass) { return new StubFactoryStaticImpl(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\presentation\rmi\PresentationDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */