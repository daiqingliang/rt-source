package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.lang.reflect.Proxy;
import org.omg.CORBA.Object;

public class StubFactoryProxyImpl extends StubFactoryDynamicBase {
  public StubFactoryProxyImpl(PresentationManager.ClassData paramClassData, ClassLoader paramClassLoader) { super(paramClassData, paramClassLoader); }
  
  public Object makeStub() {
    InvocationHandlerFactory invocationHandlerFactory = this.classData.getInvocationHandlerFactory();
    LinkedInvocationHandler linkedInvocationHandler = (LinkedInvocationHandler)invocationHandlerFactory.getInvocationHandler();
    Class[] arrayOfClass = invocationHandlerFactory.getProxyInterfaces();
    DynamicStub dynamicStub = (DynamicStub)Proxy.newProxyInstance(this.loader, arrayOfClass, linkedInvocationHandler);
    linkedInvocationHandler.setProxy((Proxy)dynamicStub);
    return dynamicStub;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubFactoryProxyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */