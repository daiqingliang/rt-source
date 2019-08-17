package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class StubInvocationHandlerImpl implements LinkedInvocationHandler {
  private PresentationManager.ClassData classData;
  
  private PresentationManager pm;
  
  private Object stub;
  
  private Proxy self;
  
  public void setProxy(Proxy paramProxy) { this.self = paramProxy; }
  
  public Proxy getProxy() { return this.self; }
  
  public StubInvocationHandlerImpl(PresentationManager paramPresentationManager, PresentationManager.ClassData paramClassData, Object paramObject) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new DynamicAccessPermission("access")); 
    this.classData = paramClassData;
    this.pm = paramPresentationManager;
    this.stub = paramObject;
  }
  
  private boolean isLocal() {
    boolean bool = false;
    Delegate delegate = StubAdapter.getDelegate(this.stub);
    if (delegate instanceof CorbaClientDelegate) {
      CorbaClientDelegate corbaClientDelegate = (CorbaClientDelegate)delegate;
      ContactInfoList contactInfoList = corbaClientDelegate.getContactInfoList();
      if (contactInfoList instanceof CorbaContactInfoList) {
        CorbaContactInfoList corbaContactInfoList = (CorbaContactInfoList)contactInfoList;
        LocalClientRequestDispatcher localClientRequestDispatcher = corbaContactInfoList.getLocalClientRequestDispatcher();
        bool = localClientRequestDispatcher.useLocalInvocation(null);
      } 
    } 
    return bool;
  }
  
  public Object invoke(Object paramObject, final Method method, Object[] paramArrayOfObject) throws Throwable {
    String str = this.classData.getIDLNameTranslator().getIDLName(paramMethod);
    DynamicMethodMarshaller dynamicMethodMarshaller = this.pm.getDynamicMethodMarshaller(paramMethod);
    delegate = null;
    try {
      delegate = StubAdapter.getDelegate(this.stub);
    } catch (SystemException systemException) {
      throw Util.mapSystemException(systemException);
    } 
    if (!isLocal())
      try {
        inputStream = null;
        try {
          OutputStream outputStream = (OutputStream)delegate.request(this.stub, str, true);
          dynamicMethodMarshaller.writeArguments(outputStream, paramArrayOfObject);
          inputStream = (InputStream)delegate.invoke(this.stub, outputStream);
          return dynamicMethodMarshaller.readResult(inputStream);
        } catch (ApplicationException applicationException) {
          throw dynamicMethodMarshaller.readException(applicationException);
        } catch (RemarshalException remarshalException) {
          return invoke(paramObject, paramMethod, paramArrayOfObject);
        } finally {
          delegate.releaseReply(this.stub, inputStream);
        } 
      } catch (SystemException systemException) {
        throw Util.mapSystemException(systemException);
      }  
    ORB oRB = (ORB)delegate.orb(this.stub);
    servantObject = delegate.servant_preinvoke(this.stub, str, paramMethod.getDeclaringClass());
    if (servantObject == null)
      return invoke(this.stub, paramMethod, paramArrayOfObject); 
    try {
      Object[] arrayOfObject = dynamicMethodMarshaller.copyArguments(paramArrayOfObject, oRB);
      if (!paramMethod.isAccessible())
        AccessController.doPrivileged(new PrivilegedAction() {
              public Object run() {
                method.setAccessible(true);
                return null;
              }
            }); 
      Object object = paramMethod.invoke(servantObject.servant, arrayOfObject);
      return dynamicMethodMarshaller.copyResult(object, oRB);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable1 = invocationTargetException.getCause();
      Throwable throwable2 = (Throwable)Util.copyObject(throwable1, oRB);
      if (dynamicMethodMarshaller.isDeclaredException(throwable2))
        throw throwable2; 
      throw Util.wrapException(throwable2);
    } catch (Throwable throwable) {
      if (throwable instanceof ThreadDeath)
        throw (ThreadDeath)throwable; 
      throw Util.wrapException(throwable);
    } finally {
      delegate.servant_postinvoke(this.stub, servantObject);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubInvocationHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */