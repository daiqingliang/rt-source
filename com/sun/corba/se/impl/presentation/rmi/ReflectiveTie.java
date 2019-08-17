package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;

public final class ReflectiveTie extends Servant implements Tie {
  private Remote target = null;
  
  private PresentationManager pm;
  
  private PresentationManager.ClassData classData = null;
  
  private ORBUtilSystemException wrapper = null;
  
  public ReflectiveTie(PresentationManager paramPresentationManager, ORBUtilSystemException paramORBUtilSystemException) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new DynamicAccessPermission("access")); 
    this.pm = paramPresentationManager;
    this.wrapper = paramORBUtilSystemException;
  }
  
  public String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte) { return this.classData.getTypeIds(); }
  
  public void setTarget(Remote paramRemote) {
    this.target = paramRemote;
    if (paramRemote == null) {
      this.classData = null;
    } else {
      Class clazz = paramRemote.getClass();
      this.classData = this.pm.getClassData(clazz);
    } 
  }
  
  public Remote getTarget() { return this.target; }
  
  public Object thisObject() { return _this_object(); }
  
  public void deactivate() {
    try {
      _poa().deactivate_object(_poa().servant_to_id(this));
    } catch (WrongPolicy wrongPolicy) {
    
    } catch (ObjectNotActive objectNotActive) {
    
    } catch (ServantNotActive servantNotActive) {}
  }
  
  public ORB orb() { return _orb(); }
  
  public void orb(ORB paramORB) {
    try {
      ORB oRB = (ORB)paramORB;
      ((ORB)paramORB).set_delegate(this);
    } catch (ClassCastException classCastException) {
      throw this.wrapper.badOrbForServant(classCastException);
    } 
  }
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler) {
    Method method = null;
    DynamicMethodMarshaller dynamicMethodMarshaller = null;
    try {
      InputStream inputStream = (InputStream)paramInputStream;
      method = this.classData.getIDLNameTranslator().getMethod(paramString);
      if (method == null)
        throw this.wrapper.methodNotFoundInTie(paramString, this.target.getClass().getName()); 
      dynamicMethodMarshaller = this.pm.getDynamicMethodMarshaller(method);
      Object[] arrayOfObject = dynamicMethodMarshaller.readArguments(inputStream);
      Object object = method.invoke(this.target, arrayOfObject);
      OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
      dynamicMethodMarshaller.writeResult(outputStream, object);
      return outputStream;
    } catch (IllegalAccessException illegalAccessException) {
      throw this.wrapper.invocationErrorInReflectiveTie(illegalAccessException, method.getName(), method.getDeclaringClass().getName());
    } catch (IllegalArgumentException illegalArgumentException) {
      throw this.wrapper.invocationErrorInReflectiveTie(illegalArgumentException, method.getName(), method.getDeclaringClass().getName());
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof SystemException)
        throw (SystemException)throwable; 
      if (throwable instanceof Exception && dynamicMethodMarshaller.isDeclaredException(throwable)) {
        OutputStream outputStream = (OutputStream)paramResponseHandler.createExceptionReply();
        dynamicMethodMarshaller.writeException(outputStream, (Exception)throwable);
        return outputStream;
      } 
      throw new UnknownException(throwable);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\ReflectiveTie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */