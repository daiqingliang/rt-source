package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;

public class IORSystemException extends LogWrapperBase {
  private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger param1Logger) { return new IORSystemException(param1Logger); }
    };
  
  public static final int ORT_NOT_INITIALIZED = 1398080689;
  
  public static final int NULL_POA = 1398080690;
  
  public static final int BAD_MAGIC = 1398080691;
  
  public static final int STRINGIFY_WRITE_ERROR = 1398080692;
  
  public static final int TAGGED_PROFILE_TEMPLATE_FACTORY_NOT_FOUND = 1398080693;
  
  public static final int INVALID_JDK1_3_1_PATCH_LEVEL = 1398080694;
  
  public static final int GET_LOCAL_SERVANT_FAILURE = 1398080695;
  
  public static final int ADAPTER_ID_NOT_AVAILABLE = 1398080689;
  
  public static final int SERVER_ID_NOT_AVAILABLE = 1398080690;
  
  public static final int ORB_ID_NOT_AVAILABLE = 1398080691;
  
  public static final int OBJECT_ADAPTER_ID_NOT_AVAILABLE = 1398080692;
  
  public static final int BAD_OID_IN_IOR_TEMPLATE_LIST = 1398080689;
  
  public static final int INVALID_TAGGED_PROFILE = 1398080690;
  
  public static final int BAD_IIOP_ADDRESS_PORT = 1398080691;
  
  public static final int IOR_MUST_HAVE_IIOP_PROFILE = 1398080689;
  
  public IORSystemException(Logger paramLogger) { super(paramLogger); }
  
  public static IORSystemException get(ORB paramORB, String paramString) { return (IORSystemException)paramORB.getLogWrapper(paramString, "IOR", factory); }
  
  public static IORSystemException get(String paramString) { return (IORSystemException)ORB.staticGetLogWrapper(paramString, "IOR", factory); }
  
  public INTERNAL ortNotInitialized(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080689, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.ortNotInitialized", arrayOfObject, IORSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL ortNotInitialized(CompletionStatus paramCompletionStatus) { return ortNotInitialized(paramCompletionStatus, null); }
  
  public INTERNAL ortNotInitialized(Throwable paramThrowable) { return ortNotInitialized(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL ortNotInitialized() { return ortNotInitialized(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL nullPoa(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080690, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.nullPoa", arrayOfObject, IORSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL nullPoa(CompletionStatus paramCompletionStatus) { return nullPoa(paramCompletionStatus, null); }
  
  public INTERNAL nullPoa(Throwable paramThrowable) { return nullPoa(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL nullPoa() { return nullPoa(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badMagic(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398080691, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "IOR.badMagic", arrayOfObject, IORSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badMagic(CompletionStatus paramCompletionStatus, Object paramObject) { return badMagic(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL badMagic(Throwable paramThrowable, Object paramObject) { return badMagic(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL badMagic(Object paramObject) { return badMagic(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL stringifyWriteError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080692, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.stringifyWriteError", arrayOfObject, IORSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL stringifyWriteError(CompletionStatus paramCompletionStatus) { return stringifyWriteError(paramCompletionStatus, null); }
  
  public INTERNAL stringifyWriteError(Throwable paramThrowable) { return stringifyWriteError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL stringifyWriteError() { return stringifyWriteError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL taggedProfileTemplateFactoryNotFound(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398080693, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "IOR.taggedProfileTemplateFactoryNotFound", arrayOfObject, IORSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL taggedProfileTemplateFactoryNotFound(CompletionStatus paramCompletionStatus, Object paramObject) { return taggedProfileTemplateFactoryNotFound(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL taggedProfileTemplateFactoryNotFound(Throwable paramThrowable, Object paramObject) { return taggedProfileTemplateFactoryNotFound(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL taggedProfileTemplateFactoryNotFound(Object paramObject) { return taggedProfileTemplateFactoryNotFound(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL invalidJdk131PatchLevel(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398080694, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "IOR.invalidJdk131PatchLevel", arrayOfObject, IORSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invalidJdk131PatchLevel(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidJdk131PatchLevel(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL invalidJdk131PatchLevel(Throwable paramThrowable, Object paramObject) { return invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL invalidJdk131PatchLevel(Object paramObject) { return invalidJdk131PatchLevel(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL getLocalServantFailure(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398080695, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "IOR.getLocalServantFailure", arrayOfObject, IORSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL getLocalServantFailure(CompletionStatus paramCompletionStatus, Object paramObject) { return getLocalServantFailure(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL getLocalServantFailure(Throwable paramThrowable, Object paramObject) { return getLocalServantFailure(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL getLocalServantFailure(Object paramObject) { return getLocalServantFailure(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_OPERATION adapterIdNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080689, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.adapterIdNotAvailable", arrayOfObject, IORSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION adapterIdNotAvailable(CompletionStatus paramCompletionStatus) { return adapterIdNotAvailable(paramCompletionStatus, null); }
  
  public BAD_OPERATION adapterIdNotAvailable(Throwable paramThrowable) { return adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION adapterIdNotAvailable() { return adapterIdNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION serverIdNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080690, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.serverIdNotAvailable", arrayOfObject, IORSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION serverIdNotAvailable(CompletionStatus paramCompletionStatus) { return serverIdNotAvailable(paramCompletionStatus, null); }
  
  public BAD_OPERATION serverIdNotAvailable(Throwable paramThrowable) { return serverIdNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION serverIdNotAvailable() { return serverIdNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION orbIdNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080691, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.orbIdNotAvailable", arrayOfObject, IORSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION orbIdNotAvailable(CompletionStatus paramCompletionStatus) { return orbIdNotAvailable(paramCompletionStatus, null); }
  
  public BAD_OPERATION orbIdNotAvailable(Throwable paramThrowable) { return orbIdNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION orbIdNotAvailable() { return orbIdNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION objectAdapterIdNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080692, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.objectAdapterIdNotAvailable", arrayOfObject, IORSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION objectAdapterIdNotAvailable(CompletionStatus paramCompletionStatus) { return objectAdapterIdNotAvailable(paramCompletionStatus, null); }
  
  public BAD_OPERATION objectAdapterIdNotAvailable(Throwable paramThrowable) { return objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION objectAdapterIdNotAvailable() { return objectAdapterIdNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM badOidInIorTemplateList(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080689, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.badOidInIorTemplateList", arrayOfObject, IORSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM badOidInIorTemplateList(CompletionStatus paramCompletionStatus) { return badOidInIorTemplateList(paramCompletionStatus, null); }
  
  public BAD_PARAM badOidInIorTemplateList(Throwable paramThrowable) { return badOidInIorTemplateList(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM badOidInIorTemplateList() { return badOidInIorTemplateList(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM invalidTaggedProfile(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080690, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.invalidTaggedProfile", arrayOfObject, IORSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidTaggedProfile(CompletionStatus paramCompletionStatus) { return invalidTaggedProfile(paramCompletionStatus, null); }
  
  public BAD_PARAM invalidTaggedProfile(Throwable paramThrowable) { return invalidTaggedProfile(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM invalidTaggedProfile() { return invalidTaggedProfile(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM badIiopAddressPort(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080691, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "IOR.badIiopAddressPort", arrayOfObject, IORSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM badIiopAddressPort(CompletionStatus paramCompletionStatus, Object paramObject) { return badIiopAddressPort(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM badIiopAddressPort(Throwable paramThrowable, Object paramObject) { return badIiopAddressPort(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM badIiopAddressPort(Object paramObject) { return badIiopAddressPort(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INV_OBJREF iorMustHaveIiopProfile(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INV_OBJREF iNV_OBJREF = new INV_OBJREF(1398080689, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_OBJREF.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "IOR.iorMustHaveIiopProfile", arrayOfObject, IORSystemException.class, iNV_OBJREF);
    } 
    return iNV_OBJREF;
  }
  
  public INV_OBJREF iorMustHaveIiopProfile(CompletionStatus paramCompletionStatus) { return iorMustHaveIiopProfile(paramCompletionStatus, null); }
  
  public INV_OBJREF iorMustHaveIiopProfile(Throwable paramThrowable) { return iorMustHaveIiopProfile(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INV_OBJREF iorMustHaveIiopProfile() { return iorMustHaveIiopProfile(CompletionStatus.COMPLETED_NO, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\logging\IORSystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */