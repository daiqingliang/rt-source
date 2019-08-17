package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class StubFactoryFactoryProxyImpl extends StubFactoryFactoryDynamicBase {
  public PresentationManager.StubFactory makeDynamicStubFactory(PresentationManager paramPresentationManager, final PresentationManager.ClassData classData, final ClassLoader classLoader) { return (PresentationManager.StubFactory)AccessController.doPrivileged(new PrivilegedAction<StubFactoryProxyImpl>() {
          public StubFactoryProxyImpl run() { return new StubFactoryProxyImpl(classData, classLoader); }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryFactoryProxyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */