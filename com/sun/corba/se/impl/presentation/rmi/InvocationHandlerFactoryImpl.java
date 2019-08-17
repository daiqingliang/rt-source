package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandler;
import com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl;
import com.sun.corba.se.spi.orbutil.proxy.DelegateInvocationHandlerImpl;
import com.sun.corba.se.spi.orbutil.proxy.InvocationHandlerFactory;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class InvocationHandlerFactoryImpl implements InvocationHandlerFactory {
  private final PresentationManager.ClassData classData;
  
  private final PresentationManager pm;
  
  private Class[] proxyInterfaces;
  
  public InvocationHandlerFactoryImpl(PresentationManager paramPresentationManager, PresentationManager.ClassData paramClassData) {
    this.classData = paramClassData;
    this.pm = paramPresentationManager;
    Class[] arrayOfClass = paramClassData.getIDLNameTranslator().getInterfaces();
    this.proxyInterfaces = new Class[arrayOfClass.length + 1];
    for (byte b = 0; b < arrayOfClass.length; b++)
      this.proxyInterfaces[b] = arrayOfClass[b]; 
    this.proxyInterfaces[arrayOfClass.length] = DynamicStub.class;
  }
  
  public InvocationHandler getInvocationHandler() {
    DynamicStubImpl dynamicStubImpl = new DynamicStubImpl(this.classData.getTypeIds());
    return getInvocationHandler(dynamicStubImpl);
  }
  
  InvocationHandler getInvocationHandler(DynamicStub paramDynamicStub) {
    final InvocationHandler dynamicStubHandler = DelegateInvocationHandlerImpl.create(paramDynamicStub);
    StubInvocationHandlerImpl stubInvocationHandlerImpl = new StubInvocationHandlerImpl(this.pm, this.classData, paramDynamicStub);
    final CustomCompositeInvocationHandlerImpl handler = new CustomCompositeInvocationHandlerImpl(paramDynamicStub);
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            handler.addInvocationHandler(DynamicStub.class, dynamicStubHandler);
            handler.addInvocationHandler(org.omg.CORBA.Object.class, dynamicStubHandler);
            handler.addInvocationHandler(Object.class, dynamicStubHandler);
            return null;
          }
        });
    customCompositeInvocationHandlerImpl.setDefaultHandler(stubInvocationHandlerImpl);
    return customCompositeInvocationHandlerImpl;
  }
  
  public Class[] getProxyInterfaces() { return this.proxyInterfaces; }
  
  private class CustomCompositeInvocationHandlerImpl extends CompositeInvocationHandlerImpl implements LinkedInvocationHandler, Serializable {
    private DynamicStub stub;
    
    public void setProxy(Proxy param1Proxy) { ((DynamicStubImpl)this.stub).setSelf((DynamicStub)param1Proxy); }
    
    public Proxy getProxy() { return (Proxy)((DynamicStubImpl)this.stub).getSelf(); }
    
    public CustomCompositeInvocationHandlerImpl(DynamicStub param1DynamicStub) { this.stub = param1DynamicStub; }
    
    public Object writeReplace() { return this.stub; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\InvocationHandlerFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */