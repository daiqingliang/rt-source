package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA_2_3.portable.InputStream;

public class ServiceContextData {
  private Class scClass;
  
  private Constructor scConstructor;
  
  private int scId;
  
  private void dprint(String paramString) { ORBUtility.dprint(this, paramString); }
  
  private void throwBadParam(String paramString, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(paramString);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    throw bAD_PARAM;
  }
  
  public ServiceContextData(Class paramClass) {
    if (ORB.ORBInitDebug)
      dprint("ServiceContextData constructor called for class " + paramClass); 
    this.scClass = paramClass;
    try {
      if (ORB.ORBInitDebug)
        dprint("Finding constructor for " + paramClass); 
      Class[] arrayOfClass = new Class[2];
      arrayOfClass[0] = InputStream.class;
      arrayOfClass[1] = GIOPVersion.class;
      try {
        this.scConstructor = paramClass.getConstructor(arrayOfClass);
      } catch (NoSuchMethodException noSuchMethodException) {
        throwBadParam("Class does not have an InputStream constructor", noSuchMethodException);
      } 
      if (ORB.ORBInitDebug)
        dprint("Finding SERVICE_CONTEXT_ID field in " + paramClass); 
      Field field = null;
      try {
        field = paramClass.getField("SERVICE_CONTEXT_ID");
      } catch (NoSuchFieldException noSuchFieldException) {
        throwBadParam("Class does not have a SERVICE_CONTEXT_ID member", noSuchFieldException);
      } catch (SecurityException securityException) {
        throwBadParam("Could not access SERVICE_CONTEXT_ID member", securityException);
      } 
      if (ORB.ORBInitDebug)
        dprint("Checking modifiers of SERVICE_CONTEXT_ID field in " + paramClass); 
      int i = field.getModifiers();
      if (!Modifier.isPublic(i) || !Modifier.isStatic(i) || !Modifier.isFinal(i))
        throwBadParam("SERVICE_CONTEXT_ID field is not public static final", null); 
      if (ORB.ORBInitDebug)
        dprint("Getting value of SERVICE_CONTEXT_ID in " + paramClass); 
      try {
        this.scId = field.getInt(null);
      } catch (IllegalArgumentException illegalArgumentException) {
        throwBadParam("SERVICE_CONTEXT_ID not convertible to int", illegalArgumentException);
      } catch (IllegalAccessException illegalAccessException) {
        throwBadParam("Could not access value of SERVICE_CONTEXT_ID", illegalAccessException);
      } 
    } catch (BAD_PARAM bAD_PARAM) {
      if (ORB.ORBInitDebug)
        dprint("Exception in ServiceContextData constructor: " + bAD_PARAM); 
      throw bAD_PARAM;
    } catch (Throwable throwable) {
      if (ORB.ORBInitDebug)
        dprint("Unexpected Exception in ServiceContextData constructor: " + throwable); 
    } 
    if (ORB.ORBInitDebug)
      dprint("ServiceContextData constructor completed"); 
  }
  
  public ServiceContext makeServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion) {
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramInputStream;
    arrayOfObject[1] = paramGIOPVersion;
    ServiceContext serviceContext = null;
    try {
      serviceContext = (ServiceContext)this.scConstructor.newInstance(arrayOfObject);
    } catch (IllegalArgumentException illegalArgumentException) {
      throwBadParam("InputStream constructor argument error", illegalArgumentException);
    } catch (IllegalAccessException illegalAccessException) {
      throwBadParam("InputStream constructor argument error", illegalAccessException);
    } catch (InstantiationException instantiationException) {
      throwBadParam("InputStream constructor called for abstract class", instantiationException);
    } catch (InvocationTargetException invocationTargetException) {
      throwBadParam("InputStream constructor threw exception " + invocationTargetException.getTargetException(), invocationTargetException);
    } 
    return serviceContext;
  }
  
  int getId() { return this.scId; }
  
  public String toString() { return "ServiceContextData[ scClass=" + this.scClass + " scConstructor=" + this.scConstructor + " scId=" + this.scId + " ]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\servicecontext\ServiceContextData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */