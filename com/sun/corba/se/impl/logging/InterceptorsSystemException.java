package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.UNKNOWN;

public class InterceptorsSystemException extends LogWrapperBase {
  private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger param1Logger) { return new InterceptorsSystemException(param1Logger); }
    };
  
  public static final int TYPE_OUT_OF_RANGE = 1398080289;
  
  public static final int NAME_NULL = 1398080290;
  
  public static final int RIR_INVALID_PRE_INIT = 1398080289;
  
  public static final int BAD_STATE1 = 1398080290;
  
  public static final int BAD_STATE2 = 1398080291;
  
  public static final int IOEXCEPTION_DURING_CANCEL_REQUEST = 1398080289;
  
  public static final int EXCEPTION_WAS_NULL = 1398080289;
  
  public static final int OBJECT_HAS_NO_DELEGATE = 1398080290;
  
  public static final int DELEGATE_NOT_CLIENTSUB = 1398080291;
  
  public static final int OBJECT_NOT_OBJECTIMPL = 1398080292;
  
  public static final int EXCEPTION_INVALID = 1398080293;
  
  public static final int REPLY_STATUS_NOT_INIT = 1398080294;
  
  public static final int EXCEPTION_IN_ARGUMENTS = 1398080295;
  
  public static final int EXCEPTION_IN_EXCEPTIONS = 1398080296;
  
  public static final int EXCEPTION_IN_CONTEXTS = 1398080297;
  
  public static final int EXCEPTION_WAS_NULL_2 = 1398080298;
  
  public static final int SERVANT_INVALID = 1398080299;
  
  public static final int CANT_POP_ONLY_PICURRENT = 1398080300;
  
  public static final int CANT_POP_ONLY_CURRENT_2 = 1398080301;
  
  public static final int PI_DSI_RESULT_IS_NULL = 1398080302;
  
  public static final int PI_DII_RESULT_IS_NULL = 1398080303;
  
  public static final int EXCEPTION_UNAVAILABLE = 1398080304;
  
  public static final int CLIENT_INFO_STACK_NULL = 1398080305;
  
  public static final int SERVER_INFO_STACK_NULL = 1398080306;
  
  public static final int MARK_AND_RESET_FAILED = 1398080307;
  
  public static final int SLOT_TABLE_INVARIANT = 1398080308;
  
  public static final int INTERCEPTOR_LIST_LOCKED = 1398080309;
  
  public static final int SORT_SIZE_MISMATCH = 1398080310;
  
  public static final int PI_ORB_NOT_POLICY_BASED = 1398080289;
  
  public static final int ORBINITINFO_INVALID = 1398080289;
  
  public static final int UNKNOWN_REQUEST_INVOKE = 1398080289;
  
  public InterceptorsSystemException(Logger paramLogger) { super(paramLogger); }
  
  public static InterceptorsSystemException get(ORB paramORB, String paramString) { return (InterceptorsSystemException)paramORB.getLogWrapper(paramString, "INTERCEPTORS", factory); }
  
  public static InterceptorsSystemException get(String paramString) { return (InterceptorsSystemException)ORB.staticGetLogWrapper(paramString, "INTERCEPTORS", factory); }
  
  public BAD_PARAM typeOutOfRange(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080289, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "INTERCEPTORS.typeOutOfRange", arrayOfObject, InterceptorsSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM typeOutOfRange(CompletionStatus paramCompletionStatus, Object paramObject) { return typeOutOfRange(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM typeOutOfRange(Throwable paramThrowable, Object paramObject) { return typeOutOfRange(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM typeOutOfRange(Object paramObject) { return typeOutOfRange(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM nameNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080290, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.nameNull", arrayOfObject, InterceptorsSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM nameNull(CompletionStatus paramCompletionStatus) { return nameNull(paramCompletionStatus, null); }
  
  public BAD_PARAM nameNull(Throwable paramThrowable) { return nameNull(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM nameNull() { return nameNull(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER rirInvalidPreInit(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398080289, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.rirInvalidPreInit", arrayOfObject, InterceptorsSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER rirInvalidPreInit(CompletionStatus paramCompletionStatus) { return rirInvalidPreInit(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER rirInvalidPreInit(Throwable paramThrowable) { return rirInvalidPreInit(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER rirInvalidPreInit() { return rirInvalidPreInit(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badState1(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398080290, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "INTERCEPTORS.badState1", arrayOfObject, InterceptorsSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badState1(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return badState1(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public BAD_INV_ORDER badState1(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return badState1(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public BAD_INV_ORDER badState1(Object paramObject1, Object paramObject2) { return badState1(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public BAD_INV_ORDER badState2(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398080291, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      arrayOfObject[2] = paramObject3;
      doLog(Level.WARNING, "INTERCEPTORS.badState2", arrayOfObject, InterceptorsSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badState2(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2, Object paramObject3) { return badState2(paramCompletionStatus, null, paramObject1, paramObject2, paramObject3); }
  
  public BAD_INV_ORDER badState2(Throwable paramThrowable, Object paramObject1, Object paramObject2, Object paramObject3) { return badState2(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2, paramObject3); }
  
  public BAD_INV_ORDER badState2(Object paramObject1, Object paramObject2, Object paramObject3) { return badState2(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2, paramObject3); }
  
  public COMM_FAILURE ioexceptionDuringCancelRequest(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    COMM_FAILURE cOMM_FAILURE = new COMM_FAILURE(1398080289, paramCompletionStatus);
    if (paramThrowable != null)
      cOMM_FAILURE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.ioexceptionDuringCancelRequest", arrayOfObject, InterceptorsSystemException.class, cOMM_FAILURE);
    } 
    return cOMM_FAILURE;
  }
  
  public COMM_FAILURE ioexceptionDuringCancelRequest(CompletionStatus paramCompletionStatus) { return ioexceptionDuringCancelRequest(paramCompletionStatus, null); }
  
  public COMM_FAILURE ioexceptionDuringCancelRequest(Throwable paramThrowable) { return ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public COMM_FAILURE ioexceptionDuringCancelRequest() { return ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL exceptionWasNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080289, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.exceptionWasNull", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL exceptionWasNull(CompletionStatus paramCompletionStatus) { return exceptionWasNull(paramCompletionStatus, null); }
  
  public INTERNAL exceptionWasNull(Throwable paramThrowable) { return exceptionWasNull(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL exceptionWasNull() { return exceptionWasNull(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL objectHasNoDelegate(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080290, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.objectHasNoDelegate", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL objectHasNoDelegate(CompletionStatus paramCompletionStatus) { return objectHasNoDelegate(paramCompletionStatus, null); }
  
  public INTERNAL objectHasNoDelegate(Throwable paramThrowable) { return objectHasNoDelegate(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL objectHasNoDelegate() { return objectHasNoDelegate(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL delegateNotClientsub(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080291, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.delegateNotClientsub", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL delegateNotClientsub(CompletionStatus paramCompletionStatus) { return delegateNotClientsub(paramCompletionStatus, null); }
  
  public INTERNAL delegateNotClientsub(Throwable paramThrowable) { return delegateNotClientsub(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL delegateNotClientsub() { return delegateNotClientsub(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL objectNotObjectimpl(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080292, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.objectNotObjectimpl", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL objectNotObjectimpl(CompletionStatus paramCompletionStatus) { return objectNotObjectimpl(paramCompletionStatus, null); }
  
  public INTERNAL objectNotObjectimpl(Throwable paramThrowable) { return objectNotObjectimpl(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL objectNotObjectimpl() { return objectNotObjectimpl(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL exceptionInvalid(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080293, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.exceptionInvalid", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL exceptionInvalid(CompletionStatus paramCompletionStatus) { return exceptionInvalid(paramCompletionStatus, null); }
  
  public INTERNAL exceptionInvalid(Throwable paramThrowable) { return exceptionInvalid(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL exceptionInvalid() { return exceptionInvalid(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL replyStatusNotInit(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080294, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.replyStatusNotInit", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL replyStatusNotInit(CompletionStatus paramCompletionStatus) { return replyStatusNotInit(paramCompletionStatus, null); }
  
  public INTERNAL replyStatusNotInit(Throwable paramThrowable) { return replyStatusNotInit(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL replyStatusNotInit() { return replyStatusNotInit(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL exceptionInArguments(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080295, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.exceptionInArguments", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL exceptionInArguments(CompletionStatus paramCompletionStatus) { return exceptionInArguments(paramCompletionStatus, null); }
  
  public INTERNAL exceptionInArguments(Throwable paramThrowable) { return exceptionInArguments(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL exceptionInArguments() { return exceptionInArguments(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL exceptionInExceptions(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080296, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.exceptionInExceptions", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL exceptionInExceptions(CompletionStatus paramCompletionStatus) { return exceptionInExceptions(paramCompletionStatus, null); }
  
  public INTERNAL exceptionInExceptions(Throwable paramThrowable) { return exceptionInExceptions(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL exceptionInExceptions() { return exceptionInExceptions(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL exceptionInContexts(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080297, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.exceptionInContexts", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL exceptionInContexts(CompletionStatus paramCompletionStatus) { return exceptionInContexts(paramCompletionStatus, null); }
  
  public INTERNAL exceptionInContexts(Throwable paramThrowable) { return exceptionInContexts(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL exceptionInContexts() { return exceptionInContexts(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL exceptionWasNull2(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080298, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.exceptionWasNull2", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL exceptionWasNull2(CompletionStatus paramCompletionStatus) { return exceptionWasNull2(paramCompletionStatus, null); }
  
  public INTERNAL exceptionWasNull2(Throwable paramThrowable) { return exceptionWasNull2(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL exceptionWasNull2() { return exceptionWasNull2(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL servantInvalid(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080299, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.servantInvalid", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL servantInvalid(CompletionStatus paramCompletionStatus) { return servantInvalid(paramCompletionStatus, null); }
  
  public INTERNAL servantInvalid(Throwable paramThrowable) { return servantInvalid(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL servantInvalid() { return servantInvalid(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL cantPopOnlyPicurrent(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080300, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.cantPopOnlyPicurrent", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL cantPopOnlyPicurrent(CompletionStatus paramCompletionStatus) { return cantPopOnlyPicurrent(paramCompletionStatus, null); }
  
  public INTERNAL cantPopOnlyPicurrent(Throwable paramThrowable) { return cantPopOnlyPicurrent(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL cantPopOnlyPicurrent() { return cantPopOnlyPicurrent(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL cantPopOnlyCurrent2(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080301, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.cantPopOnlyCurrent2", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL cantPopOnlyCurrent2(CompletionStatus paramCompletionStatus) { return cantPopOnlyCurrent2(paramCompletionStatus, null); }
  
  public INTERNAL cantPopOnlyCurrent2(Throwable paramThrowable) { return cantPopOnlyCurrent2(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL cantPopOnlyCurrent2() { return cantPopOnlyCurrent2(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL piDsiResultIsNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080302, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.piDsiResultIsNull", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL piDsiResultIsNull(CompletionStatus paramCompletionStatus) { return piDsiResultIsNull(paramCompletionStatus, null); }
  
  public INTERNAL piDsiResultIsNull(Throwable paramThrowable) { return piDsiResultIsNull(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL piDsiResultIsNull() { return piDsiResultIsNull(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL piDiiResultIsNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080303, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.piDiiResultIsNull", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL piDiiResultIsNull(CompletionStatus paramCompletionStatus) { return piDiiResultIsNull(paramCompletionStatus, null); }
  
  public INTERNAL piDiiResultIsNull(Throwable paramThrowable) { return piDiiResultIsNull(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL piDiiResultIsNull() { return piDiiResultIsNull(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL exceptionUnavailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080304, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.exceptionUnavailable", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL exceptionUnavailable(CompletionStatus paramCompletionStatus) { return exceptionUnavailable(paramCompletionStatus, null); }
  
  public INTERNAL exceptionUnavailable(Throwable paramThrowable) { return exceptionUnavailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL exceptionUnavailable() { return exceptionUnavailable(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL clientInfoStackNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080305, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.clientInfoStackNull", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL clientInfoStackNull(CompletionStatus paramCompletionStatus) { return clientInfoStackNull(paramCompletionStatus, null); }
  
  public INTERNAL clientInfoStackNull(Throwable paramThrowable) { return clientInfoStackNull(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL clientInfoStackNull() { return clientInfoStackNull(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL serverInfoStackNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080306, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.serverInfoStackNull", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL serverInfoStackNull(CompletionStatus paramCompletionStatus) { return serverInfoStackNull(paramCompletionStatus, null); }
  
  public INTERNAL serverInfoStackNull(Throwable paramThrowable) { return serverInfoStackNull(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL serverInfoStackNull() { return serverInfoStackNull(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL markAndResetFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080307, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.markAndResetFailed", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL markAndResetFailed(CompletionStatus paramCompletionStatus) { return markAndResetFailed(paramCompletionStatus, null); }
  
  public INTERNAL markAndResetFailed(Throwable paramThrowable) { return markAndResetFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL markAndResetFailed() { return markAndResetFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL slotTableInvariant(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    INTERNAL iNTERNAL = new INTERNAL(1398080308, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "INTERCEPTORS.slotTableInvariant", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL slotTableInvariant(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return slotTableInvariant(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public INTERNAL slotTableInvariant(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return slotTableInvariant(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public INTERNAL slotTableInvariant(Object paramObject1, Object paramObject2) { return slotTableInvariant(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public INTERNAL interceptorListLocked(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080309, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.interceptorListLocked", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL interceptorListLocked(CompletionStatus paramCompletionStatus) { return interceptorListLocked(paramCompletionStatus, null); }
  
  public INTERNAL interceptorListLocked(Throwable paramThrowable) { return interceptorListLocked(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL interceptorListLocked() { return interceptorListLocked(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL sortSizeMismatch(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080310, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.sortSizeMismatch", arrayOfObject, InterceptorsSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL sortSizeMismatch(CompletionStatus paramCompletionStatus) { return sortSizeMismatch(paramCompletionStatus, null); }
  
  public INTERNAL sortSizeMismatch(Throwable paramThrowable) { return sortSizeMismatch(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL sortSizeMismatch() { return sortSizeMismatch(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT piOrbNotPolicyBased(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1398080289, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "INTERCEPTORS.piOrbNotPolicyBased", arrayOfObject, InterceptorsSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT piOrbNotPolicyBased(CompletionStatus paramCompletionStatus) { return piOrbNotPolicyBased(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT piOrbNotPolicyBased(Throwable paramThrowable) { return piOrbNotPolicyBased(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT piOrbNotPolicyBased() { return piOrbNotPolicyBased(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST orbinitinfoInvalid(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398080289, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "INTERCEPTORS.orbinitinfoInvalid", arrayOfObject, InterceptorsSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST orbinitinfoInvalid(CompletionStatus paramCompletionStatus) { return orbinitinfoInvalid(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST orbinitinfoInvalid(Throwable paramThrowable) { return orbinitinfoInvalid(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST orbinitinfoInvalid() { return orbinitinfoInvalid(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownRequestInvoke(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398080289, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "INTERCEPTORS.unknownRequestInvoke", arrayOfObject, InterceptorsSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownRequestInvoke(CompletionStatus paramCompletionStatus) { return unknownRequestInvoke(paramCompletionStatus, null); }
  
  public UNKNOWN unknownRequestInvoke(Throwable paramThrowable) { return unknownRequestInvoke(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownRequestInvoke() { return unknownRequestInvoke(CompletionStatus.COMPLETED_NO, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\logging\InterceptorsSystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */