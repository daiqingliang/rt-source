package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.UNKNOWN;

public class UtilSystemException extends LogWrapperBase {
  private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger param1Logger) { return new UtilSystemException(param1Logger); }
    };
  
  public static final int STUB_FACTORY_COULD_NOT_MAKE_STUB = 1398080889;
  
  public static final int ERROR_IN_MAKE_STUB_FROM_REPOSITORY_ID = 1398080890;
  
  public static final int CLASS_CAST_EXCEPTION_IN_LOAD_STUB = 1398080891;
  
  public static final int EXCEPTION_IN_LOAD_STUB = 1398080892;
  
  public static final int NO_POA = 1398080890;
  
  public static final int CONNECT_WRONG_ORB = 1398080891;
  
  public static final int CONNECT_NO_TIE = 1398080892;
  
  public static final int CONNECT_TIE_WRONG_ORB = 1398080893;
  
  public static final int CONNECT_TIE_NO_SERVANT = 1398080894;
  
  public static final int LOAD_TIE_FAILED = 1398080895;
  
  public static final int BAD_HEX_DIGIT = 1398080889;
  
  public static final int UNABLE_LOCATE_VALUE_HELPER = 1398080890;
  
  public static final int INVALID_INDIRECTION = 1398080891;
  
  public static final int OBJECT_NOT_CONNECTED = 1398080889;
  
  public static final int COULD_NOT_LOAD_STUB = 1398080890;
  
  public static final int OBJECT_NOT_EXPORTED = 1398080891;
  
  public static final int ERROR_SET_OBJECT_FIELD = 1398080889;
  
  public static final int ERROR_SET_BOOLEAN_FIELD = 1398080890;
  
  public static final int ERROR_SET_BYTE_FIELD = 1398080891;
  
  public static final int ERROR_SET_CHAR_FIELD = 1398080892;
  
  public static final int ERROR_SET_SHORT_FIELD = 1398080893;
  
  public static final int ERROR_SET_INT_FIELD = 1398080894;
  
  public static final int ERROR_SET_LONG_FIELD = 1398080895;
  
  public static final int ERROR_SET_FLOAT_FIELD = 1398080896;
  
  public static final int ERROR_SET_DOUBLE_FIELD = 1398080897;
  
  public static final int ILLEGAL_FIELD_ACCESS = 1398080898;
  
  public static final int BAD_BEGIN_UNMARSHAL_CUSTOM_VALUE = 1398080899;
  
  public static final int CLASS_NOT_FOUND = 1398080900;
  
  public static final int UNKNOWN_SYSEX = 1398080889;
  
  public UtilSystemException(Logger paramLogger) { super(paramLogger); }
  
  public static UtilSystemException get(ORB paramORB, String paramString) { return (UtilSystemException)paramORB.getLogWrapper(paramString, "UTIL", factory); }
  
  public static UtilSystemException get(String paramString) { return (UtilSystemException)ORB.staticGetLogWrapper(paramString, "UTIL", factory); }
  
  public BAD_OPERATION stubFactoryCouldNotMakeStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080889, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "UTIL.stubFactoryCouldNotMakeStub", arrayOfObject, UtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION stubFactoryCouldNotMakeStub(CompletionStatus paramCompletionStatus) { return stubFactoryCouldNotMakeStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION stubFactoryCouldNotMakeStub(Throwable paramThrowable) { return stubFactoryCouldNotMakeStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION stubFactoryCouldNotMakeStub() { return stubFactoryCouldNotMakeStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION errorInMakeStubFromRepositoryId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080890, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "UTIL.errorInMakeStubFromRepositoryId", arrayOfObject, UtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION errorInMakeStubFromRepositoryId(CompletionStatus paramCompletionStatus) { return errorInMakeStubFromRepositoryId(paramCompletionStatus, null); }
  
  public BAD_OPERATION errorInMakeStubFromRepositoryId(Throwable paramThrowable) { return errorInMakeStubFromRepositoryId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION errorInMakeStubFromRepositoryId() { return errorInMakeStubFromRepositoryId(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION classCastExceptionInLoadStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080891, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "UTIL.classCastExceptionInLoadStub", arrayOfObject, UtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION classCastExceptionInLoadStub(CompletionStatus paramCompletionStatus) { return classCastExceptionInLoadStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION classCastExceptionInLoadStub(Throwable paramThrowable) { return classCastExceptionInLoadStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION classCastExceptionInLoadStub() { return classCastExceptionInLoadStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION exceptionInLoadStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080892, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "UTIL.exceptionInLoadStub", arrayOfObject, UtilSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION exceptionInLoadStub(CompletionStatus paramCompletionStatus) { return exceptionInLoadStub(paramCompletionStatus, null); }
  
  public BAD_OPERATION exceptionInLoadStub(Throwable paramThrowable) { return exceptionInLoadStub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION exceptionInLoadStub() { return exceptionInLoadStub(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM noPoa(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080890, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "UTIL.noPoa", arrayOfObject, UtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM noPoa(CompletionStatus paramCompletionStatus) { return noPoa(paramCompletionStatus, null); }
  
  public BAD_PARAM noPoa(Throwable paramThrowable) { return noPoa(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM noPoa() { return noPoa(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM connectWrongOrb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080891, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "UTIL.connectWrongOrb", arrayOfObject, UtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM connectWrongOrb(CompletionStatus paramCompletionStatus) { return connectWrongOrb(paramCompletionStatus, null); }
  
  public BAD_PARAM connectWrongOrb(Throwable paramThrowable) { return connectWrongOrb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM connectWrongOrb() { return connectWrongOrb(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM connectNoTie(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080892, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "UTIL.connectNoTie", arrayOfObject, UtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM connectNoTie(CompletionStatus paramCompletionStatus) { return connectNoTie(paramCompletionStatus, null); }
  
  public BAD_PARAM connectNoTie(Throwable paramThrowable) { return connectNoTie(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM connectNoTie() { return connectNoTie(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM connectTieWrongOrb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080893, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "UTIL.connectTieWrongOrb", arrayOfObject, UtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM connectTieWrongOrb(CompletionStatus paramCompletionStatus) { return connectTieWrongOrb(paramCompletionStatus, null); }
  
  public BAD_PARAM connectTieWrongOrb(Throwable paramThrowable) { return connectTieWrongOrb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM connectTieWrongOrb() { return connectTieWrongOrb(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM connectTieNoServant(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080894, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "UTIL.connectTieNoServant", arrayOfObject, UtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM connectTieNoServant(CompletionStatus paramCompletionStatus) { return connectTieNoServant(paramCompletionStatus, null); }
  
  public BAD_PARAM connectTieNoServant(Throwable paramThrowable) { return connectTieNoServant(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM connectTieNoServant() { return connectTieNoServant(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM loadTieFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080895, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "UTIL.loadTieFailed", arrayOfObject, UtilSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM loadTieFailed(CompletionStatus paramCompletionStatus, Object paramObject) { return loadTieFailed(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM loadTieFailed(Throwable paramThrowable, Object paramObject) { return loadTieFailed(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM loadTieFailed(Object paramObject) { return loadTieFailed(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public DATA_CONVERSION badHexDigit(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1398080889, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "UTIL.badHexDigit", arrayOfObject, UtilSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION badHexDigit(CompletionStatus paramCompletionStatus) { return badHexDigit(paramCompletionStatus, null); }
  
  public DATA_CONVERSION badHexDigit(Throwable paramThrowable) { return badHexDigit(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION badHexDigit() { return badHexDigit(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL unableLocateValueHelper(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1398080890, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "UTIL.unableLocateValueHelper", arrayOfObject, UtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unableLocateValueHelper(CompletionStatus paramCompletionStatus) { return unableLocateValueHelper(paramCompletionStatus, null); }
  
  public MARSHAL unableLocateValueHelper(Throwable paramThrowable) { return unableLocateValueHelper(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL unableLocateValueHelper() { return unableLocateValueHelper(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL invalidIndirection(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    MARSHAL mARSHAL = new MARSHAL(1398080891, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "UTIL.invalidIndirection", arrayOfObject, UtilSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL invalidIndirection(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidIndirection(paramCompletionStatus, null, paramObject); }
  
  public MARSHAL invalidIndirection(Throwable paramThrowable, Object paramObject) { return invalidIndirection(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public MARSHAL invalidIndirection(Object paramObject) { return invalidIndirection(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INV_OBJREF objectNotConnected(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INV_OBJREF iNV_OBJREF = new INV_OBJREF(1398080889, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_OBJREF.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "UTIL.objectNotConnected", arrayOfObject, UtilSystemException.class, iNV_OBJREF);
    } 
    return iNV_OBJREF;
  }
  
  public INV_OBJREF objectNotConnected(CompletionStatus paramCompletionStatus, Object paramObject) { return objectNotConnected(paramCompletionStatus, null, paramObject); }
  
  public INV_OBJREF objectNotConnected(Throwable paramThrowable, Object paramObject) { return objectNotConnected(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INV_OBJREF objectNotConnected(Object paramObject) { return objectNotConnected(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INV_OBJREF couldNotLoadStub(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INV_OBJREF iNV_OBJREF = new INV_OBJREF(1398080890, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_OBJREF.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "UTIL.couldNotLoadStub", arrayOfObject, UtilSystemException.class, iNV_OBJREF);
    } 
    return iNV_OBJREF;
  }
  
  public INV_OBJREF couldNotLoadStub(CompletionStatus paramCompletionStatus, Object paramObject) { return couldNotLoadStub(paramCompletionStatus, null, paramObject); }
  
  public INV_OBJREF couldNotLoadStub(Throwable paramThrowable, Object paramObject) { return couldNotLoadStub(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INV_OBJREF couldNotLoadStub(Object paramObject) { return couldNotLoadStub(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INV_OBJREF objectNotExported(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INV_OBJREF iNV_OBJREF = new INV_OBJREF(1398080891, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_OBJREF.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "UTIL.objectNotExported", arrayOfObject, UtilSystemException.class, iNV_OBJREF);
    } 
    return iNV_OBJREF;
  }
  
  public INV_OBJREF objectNotExported(CompletionStatus paramCompletionStatus, Object paramObject) { return objectNotExported(paramCompletionStatus, null, paramObject); }
  
  public INV_OBJREF objectNotExported(Throwable paramThrowable, Object paramObject) { return objectNotExported(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INV_OBJREF objectNotExported(Object paramObject) { return objectNotExported(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL errorSetObjectField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080889, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetObjectField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetObjectField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetObjectField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetObjectField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetObjectField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetObjectField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetObjectField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetBooleanField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080890, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetBooleanField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetBooleanField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetBooleanField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetBooleanField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetBooleanField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetBooleanField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetBooleanField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetByteField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080891, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetByteField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetByteField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetByteField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetByteField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetByteField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetByteField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetByteField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetCharField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080892, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetCharField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetCharField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetCharField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetCharField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetCharField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetCharField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetCharField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetShortField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080893, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetShortField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetShortField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetShortField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetShortField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetShortField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetShortField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetShortField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetIntField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080894, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetIntField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetIntField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetIntField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetIntField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetIntField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetIntField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetIntField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetLongField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080895, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetLongField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetLongField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetLongField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetLongField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetLongField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetLongField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetLongField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetFloatField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080896, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetFloatField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetFloatField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetFloatField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetFloatField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetFloatField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetFloatField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetFloatField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetDoubleField(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    INTERNAL iNTERNAL = new INTERNAL(1398080897, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "UTIL.errorSetDoubleField", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL errorSetDoubleField(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetDoubleField(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetDoubleField(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetDoubleField(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL errorSetDoubleField(Object paramObject1, Object paramObject2, Object paramObject3) { return errorSetDoubleField(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public INTERNAL illegalFieldAccess(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398080898, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "UTIL.illegalFieldAccess", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL illegalFieldAccess(CompletionStatus paramCompletionStatus, Object paramObject) { return illegalFieldAccess(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL illegalFieldAccess(Throwable paramThrowable, Object paramObject) { return illegalFieldAccess(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL illegalFieldAccess(Object paramObject) { return illegalFieldAccess(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL badBeginUnmarshalCustomValue(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080899, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "UTIL.badBeginUnmarshalCustomValue", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badBeginUnmarshalCustomValue(CompletionStatus paramCompletionStatus) { return badBeginUnmarshalCustomValue(paramCompletionStatus, null); }
  
  public INTERNAL badBeginUnmarshalCustomValue(Throwable paramThrowable) { return badBeginUnmarshalCustomValue(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badBeginUnmarshalCustomValue() { return badBeginUnmarshalCustomValue(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL classNotFound(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398080900, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "UTIL.classNotFound", arrayOfObject, UtilSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL classNotFound(CompletionStatus paramCompletionStatus, Object paramObject) { return classNotFound(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL classNotFound(Throwable paramThrowable, Object paramObject) { return classNotFound(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL classNotFound(Object paramObject) { return classNotFound(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public UNKNOWN unknownSysex(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398080889, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "UTIL.unknownSysex", arrayOfObject, UtilSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownSysex(CompletionStatus paramCompletionStatus) { return unknownSysex(paramCompletionStatus, null); }
  
  public UNKNOWN unknownSysex(Throwable paramThrowable) { return unknownSysex(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownSysex() { return unknownSysex(CompletionStatus.COMPLETED_NO, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\logging\UtilSystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */