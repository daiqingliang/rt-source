package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_CONTEXT;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INTF_REPOS;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NO_RESOURCES;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;

public class OMGSystemException extends LogWrapperBase {
  private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger param1Logger) { return new OMGSystemException(param1Logger); }
    };
  
  public static final int IDL_CONTEXT_NOT_FOUND = 1330446337;
  
  public static final int NO_MATCHING_IDL_CONTEXT = 1330446338;
  
  public static final int DEP_PREVENT_DESTRUCTION = 1330446337;
  
  public static final int DESTROY_INDESTRUCTIBLE = 1330446338;
  
  public static final int SHUTDOWN_WAIT_FOR_COMPLETION_DEADLOCK = 1330446339;
  
  public static final int BAD_OPERATION_AFTER_SHUTDOWN = 1330446340;
  
  public static final int BAD_INVOKE = 1330446341;
  
  public static final int BAD_SET_SERVANT_MANAGER = 1330446342;
  
  public static final int BAD_ARGUMENTS_CALL = 1330446343;
  
  public static final int BAD_CTX_CALL = 1330446344;
  
  public static final int BAD_RESULT_CALL = 1330446345;
  
  public static final int BAD_SEND = 1330446346;
  
  public static final int BAD_POLL_BEFORE = 1330446347;
  
  public static final int BAD_POLL_AFTER = 1330446348;
  
  public static final int BAD_POLL_SYNC = 1330446349;
  
  public static final int INVALID_PI_CALL1 = 1330446350;
  
  public static final int INVALID_PI_CALL2 = 1330446350;
  
  public static final int INVALID_PI_CALL3 = 1330446350;
  
  public static final int INVALID_PI_CALL4 = 1330446350;
  
  public static final int SERVICE_CONTEXT_ADD_FAILED = 1330446351;
  
  public static final int POLICY_FACTORY_REG_FAILED = 1330446352;
  
  public static final int CREATE_POA_DESTROY = 1330446353;
  
  public static final int PRIORITY_REASSIGN = 1330446354;
  
  public static final int XA_START_OUTSIZE = 1330446355;
  
  public static final int XA_START_PROTO = 1330446356;
  
  public static final int BAD_SERVANT_MANAGER_TYPE = 1330446337;
  
  public static final int OPERATION_UNKNOWN_TO_TARGET = 1330446338;
  
  public static final int UNABLE_REGISTER_VALUE_FACTORY = 1330446337;
  
  public static final int RID_ALREADY_DEFINED = 1330446338;
  
  public static final int NAME_USED_IFR = 1330446339;
  
  public static final int TARGET_NOT_CONTAINER = 1330446340;
  
  public static final int NAME_CLASH = 1330446341;
  
  public static final int NOT_SERIALIZABLE = 1330446342;
  
  public static final int SO_BAD_SCHEME_NAME = 1330446343;
  
  public static final int SO_BAD_ADDRESS = 1330446344;
  
  public static final int SO_BAD_SCHEMA_SPECIFIC = 1330446345;
  
  public static final int SO_NON_SPECIFIC = 1330446346;
  
  public static final int IR_DERIVE_ABS_INT_BASE = 1330446347;
  
  public static final int IR_VALUE_SUPPORT = 1330446348;
  
  public static final int INCOMPLETE_TYPECODE = 1330446349;
  
  public static final int INVALID_OBJECT_ID = 1330446350;
  
  public static final int TYPECODE_BAD_NAME = 1330446351;
  
  public static final int TYPECODE_BAD_REPID = 1330446352;
  
  public static final int TYPECODE_INV_MEMBER = 1330446353;
  
  public static final int TC_UNION_DUP_LABEL = 1330446354;
  
  public static final int TC_UNION_INCOMPATIBLE = 1330446355;
  
  public static final int TC_UNION_BAD_DISC = 1330446356;
  
  public static final int SET_EXCEPTION_BAD_ANY = 1330446357;
  
  public static final int SET_EXCEPTION_UNLISTED = 1330446358;
  
  public static final int NO_CLIENT_WCHAR_CODESET_CTX = 1330446359;
  
  public static final int ILLEGAL_SERVICE_CONTEXT = 1330446360;
  
  public static final int ENUM_OUT_OF_RANGE = 1330446361;
  
  public static final int INVALID_SERVICE_CONTEXT_ID = 1330446362;
  
  public static final int RIR_WITH_NULL_OBJECT = 1330446363;
  
  public static final int INVALID_COMPONENT_ID = 1330446364;
  
  public static final int INVALID_PROFILE_ID = 1330446365;
  
  public static final int POLICY_TYPE_DUPLICATE = 1330446366;
  
  public static final int BAD_ONEWAY_DEFINITION = 1330446367;
  
  public static final int DII_FOR_IMPLICIT_OPERATION = 1330446368;
  
  public static final int XA_CALL_INVAL = 1330446369;
  
  public static final int UNION_BAD_DISCRIMINATOR = 1330446370;
  
  public static final int CTX_ILLEGAL_PROPERTY_NAME = 1330446371;
  
  public static final int CTX_ILLEGAL_SEARCH_STRING = 1330446372;
  
  public static final int CTX_ILLEGAL_NAME = 1330446373;
  
  public static final int CTX_NON_EMPTY = 1330446374;
  
  public static final int INVALID_STREAM_FORMAT_VERSION = 1330446375;
  
  public static final int NOT_A_VALUEOUTPUTSTREAM = 1330446376;
  
  public static final int NOT_A_VALUEINPUTSTREAM = 1330446377;
  
  public static final int MARSHALL_INCOMPLETE_TYPECODE = 1330446337;
  
  public static final int BAD_MEMBER_TYPECODE = 1330446338;
  
  public static final int ILLEGAL_PARAMETER = 1330446339;
  
  public static final int CHAR_NOT_IN_CODESET = 1330446337;
  
  public static final int PRIORITY_MAP_FAILRE = 1330446338;
  
  public static final int NO_USABLE_PROFILE = 1330446337;
  
  public static final int PRIORITY_RANGE_RESTRICT = 1330446337;
  
  public static final int NO_SERVER_WCHAR_CODESET_CMP = 1330446337;
  
  public static final int CODESET_COMPONENT_REQUIRED = 1330446338;
  
  public static final int IOR_POLICY_RECONCILE_ERROR = 1330446337;
  
  public static final int POLICY_UNKNOWN = 1330446338;
  
  public static final int NO_POLICY_FACTORY = 1330446339;
  
  public static final int XA_RMERR = 1330446337;
  
  public static final int XA_RMFAIL = 1330446338;
  
  public static final int NO_IR = 1330446337;
  
  public static final int NO_INTERFACE_IN_IR = 1330446338;
  
  public static final int UNABLE_LOCATE_VALUE_FACTORY = 1330446337;
  
  public static final int SET_RESULT_BEFORE_CTX = 1330446338;
  
  public static final int BAD_NVLIST = 1330446339;
  
  public static final int NOT_AN_OBJECT_IMPL = 1330446340;
  
  public static final int WCHAR_BAD_GIOP_VERSION_SENT = 1330446341;
  
  public static final int WCHAR_BAD_GIOP_VERSION_RETURNED = 1330446342;
  
  public static final int UNSUPPORTED_FORMAT_VERSION = 1330446343;
  
  public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE1 = 1330446344;
  
  public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE2 = 1330446344;
  
  public static final int RMIIIOP_OPTIONAL_DATA_INCOMPATIBLE3 = 1330446344;
  
  public static final int MISSING_LOCAL_VALUE_IMPL = 1330446337;
  
  public static final int INCOMPATIBLE_VALUE_IMPL = 1330446338;
  
  public static final int NO_USABLE_PROFILE_2 = 1330446339;
  
  public static final int DII_LOCAL_OBJECT = 1330446340;
  
  public static final int BIO_RESET = 1330446341;
  
  public static final int BIO_META_NOT_AVAILABLE = 1330446342;
  
  public static final int BIO_GENOMIC_NO_ITERATOR = 1330446343;
  
  public static final int PI_OPERATION_NOT_SUPPORTED1 = 1330446337;
  
  public static final int PI_OPERATION_NOT_SUPPORTED2 = 1330446337;
  
  public static final int PI_OPERATION_NOT_SUPPORTED3 = 1330446337;
  
  public static final int PI_OPERATION_NOT_SUPPORTED4 = 1330446337;
  
  public static final int PI_OPERATION_NOT_SUPPORTED5 = 1330446337;
  
  public static final int PI_OPERATION_NOT_SUPPORTED6 = 1330446337;
  
  public static final int PI_OPERATION_NOT_SUPPORTED7 = 1330446337;
  
  public static final int PI_OPERATION_NOT_SUPPORTED8 = 1330446337;
  
  public static final int NO_CONNECTION_PRIORITY = 1330446338;
  
  public static final int XA_RB = 1330446337;
  
  public static final int XA_NOTA = 1330446338;
  
  public static final int XA_END_TRUE_ROLLBACK_DEFERRED = 1330446339;
  
  public static final int POA_REQUEST_DISCARD = 1330446337;
  
  public static final int NO_USABLE_PROFILE_3 = 1330446338;
  
  public static final int REQUEST_CANCELLED = 1330446339;
  
  public static final int POA_DESTROYED = 1330446340;
  
  public static final int UNREGISTERED_VALUE_AS_OBJREF = 1330446337;
  
  public static final int NO_OBJECT_ADAPTOR = 1330446338;
  
  public static final int BIO_NOT_AVAILABLE = 1330446339;
  
  public static final int OBJECT_ADAPTER_INACTIVE = 1330446340;
  
  public static final int ADAPTER_ACTIVATOR_EXCEPTION = 1330446337;
  
  public static final int BAD_SERVANT_TYPE = 1330446338;
  
  public static final int NO_DEFAULT_SERVANT = 1330446339;
  
  public static final int NO_SERVANT_MANAGER = 1330446340;
  
  public static final int BAD_POLICY_INCARNATE = 1330446341;
  
  public static final int PI_EXC_COMP_ESTABLISHED = 1330446342;
  
  public static final int NULL_SERVANT_RETURNED = 1330446343;
  
  public static final int UNKNOWN_USER_EXCEPTION = 1330446337;
  
  public static final int UNSUPPORTED_SYSTEM_EXCEPTION = 1330446338;
  
  public static final int PI_UNKNOWN_USER_EXCEPTION = 1330446339;
  
  public OMGSystemException(Logger paramLogger) { super(paramLogger); }
  
  public static OMGSystemException get(ORB paramORB, String paramString) { return (OMGSystemException)paramORB.getLogWrapper(paramString, "OMG", factory); }
  
  public static OMGSystemException get(String paramString) { return (OMGSystemException)ORB.staticGetLogWrapper(paramString, "OMG", factory); }
  
  public BAD_CONTEXT idlContextNotFound(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_CONTEXT bAD_CONTEXT = new BAD_CONTEXT(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_CONTEXT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.idlContextNotFound", arrayOfObject, OMGSystemException.class, bAD_CONTEXT);
    } 
    return bAD_CONTEXT;
  }
  
  public BAD_CONTEXT idlContextNotFound(CompletionStatus paramCompletionStatus) { return idlContextNotFound(paramCompletionStatus, null); }
  
  public BAD_CONTEXT idlContextNotFound(Throwable paramThrowable) { return idlContextNotFound(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_CONTEXT idlContextNotFound() { return idlContextNotFound(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_CONTEXT noMatchingIdlContext(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_CONTEXT bAD_CONTEXT = new BAD_CONTEXT(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_CONTEXT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noMatchingIdlContext", arrayOfObject, OMGSystemException.class, bAD_CONTEXT);
    } 
    return bAD_CONTEXT;
  }
  
  public BAD_CONTEXT noMatchingIdlContext(CompletionStatus paramCompletionStatus) { return noMatchingIdlContext(paramCompletionStatus, null); }
  
  public BAD_CONTEXT noMatchingIdlContext(Throwable paramThrowable) { return noMatchingIdlContext(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_CONTEXT noMatchingIdlContext() { return noMatchingIdlContext(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER depPreventDestruction(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.depPreventDestruction", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER depPreventDestruction(CompletionStatus paramCompletionStatus) { return depPreventDestruction(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER depPreventDestruction(Throwable paramThrowable) { return depPreventDestruction(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER depPreventDestruction() { return depPreventDestruction(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER destroyIndestructible(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.destroyIndestructible", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER destroyIndestructible(CompletionStatus paramCompletionStatus) { return destroyIndestructible(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER destroyIndestructible(Throwable paramThrowable) { return destroyIndestructible(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER destroyIndestructible() { return destroyIndestructible(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.shutdownWaitForCompletionDeadlock", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(CompletionStatus paramCompletionStatus) { return shutdownWaitForCompletionDeadlock(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER shutdownWaitForCompletionDeadlock(Throwable paramThrowable) { return shutdownWaitForCompletionDeadlock(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER shutdownWaitForCompletionDeadlock() { return shutdownWaitForCompletionDeadlock(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badOperationAfterShutdown(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446340, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badOperationAfterShutdown", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badOperationAfterShutdown(CompletionStatus paramCompletionStatus) { return badOperationAfterShutdown(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badOperationAfterShutdown(Throwable paramThrowable) { return badOperationAfterShutdown(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badOperationAfterShutdown() { return badOperationAfterShutdown(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badInvoke(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446341, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badInvoke", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badInvoke(CompletionStatus paramCompletionStatus) { return badInvoke(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badInvoke(Throwable paramThrowable) { return badInvoke(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badInvoke() { return badInvoke(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badSetServantManager(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446342, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badSetServantManager", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badSetServantManager(CompletionStatus paramCompletionStatus) { return badSetServantManager(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badSetServantManager(Throwable paramThrowable) { return badSetServantManager(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badSetServantManager() { return badSetServantManager(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badArgumentsCall(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446343, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badArgumentsCall", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badArgumentsCall(CompletionStatus paramCompletionStatus) { return badArgumentsCall(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badArgumentsCall(Throwable paramThrowable) { return badArgumentsCall(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badArgumentsCall() { return badArgumentsCall(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badCtxCall(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446344, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badCtxCall", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badCtxCall(CompletionStatus paramCompletionStatus) { return badCtxCall(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badCtxCall(Throwable paramThrowable) { return badCtxCall(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badCtxCall() { return badCtxCall(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badResultCall(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446345, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badResultCall", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badResultCall(CompletionStatus paramCompletionStatus) { return badResultCall(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badResultCall(Throwable paramThrowable) { return badResultCall(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badResultCall() { return badResultCall(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badSend(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446346, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badSend", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badSend(CompletionStatus paramCompletionStatus) { return badSend(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badSend(Throwable paramThrowable) { return badSend(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badSend() { return badSend(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badPollBefore(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446347, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badPollBefore", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badPollBefore(CompletionStatus paramCompletionStatus) { return badPollBefore(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badPollBefore(Throwable paramThrowable) { return badPollBefore(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badPollBefore() { return badPollBefore(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badPollAfter(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446348, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badPollAfter", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badPollAfter(CompletionStatus paramCompletionStatus) { return badPollAfter(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badPollAfter(Throwable paramThrowable) { return badPollAfter(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badPollAfter() { return badPollAfter(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER badPollSync(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446349, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badPollSync", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER badPollSync(CompletionStatus paramCompletionStatus) { return badPollSync(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER badPollSync(Throwable paramThrowable) { return badPollSync(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER badPollSync() { return badPollSync(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER invalidPiCall1(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446350, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.invalidPiCall1", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER invalidPiCall1(CompletionStatus paramCompletionStatus) { return invalidPiCall1(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER invalidPiCall1(Throwable paramThrowable) { return invalidPiCall1(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER invalidPiCall1() { return invalidPiCall1(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER invalidPiCall2(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446350, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.invalidPiCall2", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER invalidPiCall2(CompletionStatus paramCompletionStatus) { return invalidPiCall2(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER invalidPiCall2(Throwable paramThrowable) { return invalidPiCall2(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER invalidPiCall2() { return invalidPiCall2(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER invalidPiCall3(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446350, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.invalidPiCall3", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER invalidPiCall3(CompletionStatus paramCompletionStatus) { return invalidPiCall3(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER invalidPiCall3(Throwable paramThrowable) { return invalidPiCall3(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER invalidPiCall3() { return invalidPiCall3(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER invalidPiCall4(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446350, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.invalidPiCall4", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER invalidPiCall4(CompletionStatus paramCompletionStatus) { return invalidPiCall4(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER invalidPiCall4(Throwable paramThrowable) { return invalidPiCall4(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER invalidPiCall4() { return invalidPiCall4(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER serviceContextAddFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446351, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "OMG.serviceContextAddFailed", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER serviceContextAddFailed(CompletionStatus paramCompletionStatus, Object paramObject) { return serviceContextAddFailed(paramCompletionStatus, null, paramObject); }
  
  public BAD_INV_ORDER serviceContextAddFailed(Throwable paramThrowable, Object paramObject) { return serviceContextAddFailed(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_INV_ORDER serviceContextAddFailed(Object paramObject) { return serviceContextAddFailed(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_INV_ORDER policyFactoryRegFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446352, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "OMG.policyFactoryRegFailed", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER policyFactoryRegFailed(CompletionStatus paramCompletionStatus, Object paramObject) { return policyFactoryRegFailed(paramCompletionStatus, null, paramObject); }
  
  public BAD_INV_ORDER policyFactoryRegFailed(Throwable paramThrowable, Object paramObject) { return policyFactoryRegFailed(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_INV_ORDER policyFactoryRegFailed(Object paramObject) { return policyFactoryRegFailed(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_INV_ORDER createPoaDestroy(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446353, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.createPoaDestroy", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER createPoaDestroy(CompletionStatus paramCompletionStatus) { return createPoaDestroy(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER createPoaDestroy(Throwable paramThrowable) { return createPoaDestroy(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER createPoaDestroy() { return createPoaDestroy(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER priorityReassign(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446354, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.priorityReassign", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER priorityReassign(CompletionStatus paramCompletionStatus) { return priorityReassign(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER priorityReassign(Throwable paramThrowable) { return priorityReassign(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER priorityReassign() { return priorityReassign(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER xaStartOutsize(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446355, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.xaStartOutsize", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER xaStartOutsize(CompletionStatus paramCompletionStatus) { return xaStartOutsize(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER xaStartOutsize(Throwable paramThrowable) { return xaStartOutsize(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER xaStartOutsize() { return xaStartOutsize(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER xaStartProto(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1330446356, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.xaStartProto", arrayOfObject, OMGSystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER xaStartProto(CompletionStatus paramCompletionStatus) { return xaStartProto(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER xaStartProto(Throwable paramThrowable) { return xaStartProto(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER xaStartProto() { return xaStartProto(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION badServantManagerType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badServantManagerType", arrayOfObject, OMGSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION badServantManagerType(CompletionStatus paramCompletionStatus) { return badServantManagerType(paramCompletionStatus, null); }
  
  public BAD_OPERATION badServantManagerType(Throwable paramThrowable) { return badServantManagerType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION badServantManagerType() { return badServantManagerType(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION operationUnknownToTarget(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.operationUnknownToTarget", arrayOfObject, OMGSystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION operationUnknownToTarget(CompletionStatus paramCompletionStatus) { return operationUnknownToTarget(paramCompletionStatus, null); }
  
  public BAD_OPERATION operationUnknownToTarget(Throwable paramThrowable) { return operationUnknownToTarget(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION operationUnknownToTarget() { return operationUnknownToTarget(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM unableRegisterValueFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.unableRegisterValueFactory", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM unableRegisterValueFactory(CompletionStatus paramCompletionStatus) { return unableRegisterValueFactory(paramCompletionStatus, null); }
  
  public BAD_PARAM unableRegisterValueFactory(Throwable paramThrowable) { return unableRegisterValueFactory(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM unableRegisterValueFactory() { return unableRegisterValueFactory(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM ridAlreadyDefined(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.ridAlreadyDefined", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM ridAlreadyDefined(CompletionStatus paramCompletionStatus) { return ridAlreadyDefined(paramCompletionStatus, null); }
  
  public BAD_PARAM ridAlreadyDefined(Throwable paramThrowable) { return ridAlreadyDefined(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM ridAlreadyDefined() { return ridAlreadyDefined(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM nameUsedIfr(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.nameUsedIfr", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM nameUsedIfr(CompletionStatus paramCompletionStatus) { return nameUsedIfr(paramCompletionStatus, null); }
  
  public BAD_PARAM nameUsedIfr(Throwable paramThrowable) { return nameUsedIfr(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM nameUsedIfr() { return nameUsedIfr(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM targetNotContainer(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446340, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.targetNotContainer", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM targetNotContainer(CompletionStatus paramCompletionStatus) { return targetNotContainer(paramCompletionStatus, null); }
  
  public BAD_PARAM targetNotContainer(Throwable paramThrowable) { return targetNotContainer(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM targetNotContainer() { return targetNotContainer(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM nameClash(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446341, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.nameClash", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM nameClash(CompletionStatus paramCompletionStatus) { return nameClash(paramCompletionStatus, null); }
  
  public BAD_PARAM nameClash(Throwable paramThrowable) { return nameClash(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM nameClash() { return nameClash(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM notSerializable(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446342, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "OMG.notSerializable", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM notSerializable(CompletionStatus paramCompletionStatus, Object paramObject) { return notSerializable(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM notSerializable(Throwable paramThrowable, Object paramObject) { return notSerializable(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM notSerializable(Object paramObject) { return notSerializable(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM soBadSchemeName(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446343, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.soBadSchemeName", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM soBadSchemeName(CompletionStatus paramCompletionStatus) { return soBadSchemeName(paramCompletionStatus, null); }
  
  public BAD_PARAM soBadSchemeName(Throwable paramThrowable) { return soBadSchemeName(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM soBadSchemeName() { return soBadSchemeName(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM soBadAddress(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446344, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.soBadAddress", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM soBadAddress(CompletionStatus paramCompletionStatus) { return soBadAddress(paramCompletionStatus, null); }
  
  public BAD_PARAM soBadAddress(Throwable paramThrowable) { return soBadAddress(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM soBadAddress() { return soBadAddress(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM soBadSchemaSpecific(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446345, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.soBadSchemaSpecific", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM soBadSchemaSpecific(CompletionStatus paramCompletionStatus) { return soBadSchemaSpecific(paramCompletionStatus, null); }
  
  public BAD_PARAM soBadSchemaSpecific(Throwable paramThrowable) { return soBadSchemaSpecific(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM soBadSchemaSpecific() { return soBadSchemaSpecific(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM soNonSpecific(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446346, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.soNonSpecific", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM soNonSpecific(CompletionStatus paramCompletionStatus) { return soNonSpecific(paramCompletionStatus, null); }
  
  public BAD_PARAM soNonSpecific(Throwable paramThrowable) { return soNonSpecific(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM soNonSpecific() { return soNonSpecific(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM irDeriveAbsIntBase(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446347, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.irDeriveAbsIntBase", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM irDeriveAbsIntBase(CompletionStatus paramCompletionStatus) { return irDeriveAbsIntBase(paramCompletionStatus, null); }
  
  public BAD_PARAM irDeriveAbsIntBase(Throwable paramThrowable) { return irDeriveAbsIntBase(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM irDeriveAbsIntBase() { return irDeriveAbsIntBase(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM irValueSupport(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446348, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.irValueSupport", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM irValueSupport(CompletionStatus paramCompletionStatus) { return irValueSupport(paramCompletionStatus, null); }
  
  public BAD_PARAM irValueSupport(Throwable paramThrowable) { return irValueSupport(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM irValueSupport() { return irValueSupport(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM incompleteTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446349, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.incompleteTypecode", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM incompleteTypecode(CompletionStatus paramCompletionStatus) { return incompleteTypecode(paramCompletionStatus, null); }
  
  public BAD_PARAM incompleteTypecode(Throwable paramThrowable) { return incompleteTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM incompleteTypecode() { return incompleteTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM invalidObjectId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446350, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.invalidObjectId", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidObjectId(CompletionStatus paramCompletionStatus) { return invalidObjectId(paramCompletionStatus, null); }
  
  public BAD_PARAM invalidObjectId(Throwable paramThrowable) { return invalidObjectId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM invalidObjectId() { return invalidObjectId(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM typecodeBadName(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446351, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.typecodeBadName", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM typecodeBadName(CompletionStatus paramCompletionStatus) { return typecodeBadName(paramCompletionStatus, null); }
  
  public BAD_PARAM typecodeBadName(Throwable paramThrowable) { return typecodeBadName(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM typecodeBadName() { return typecodeBadName(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM typecodeBadRepid(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446352, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.typecodeBadRepid", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM typecodeBadRepid(CompletionStatus paramCompletionStatus) { return typecodeBadRepid(paramCompletionStatus, null); }
  
  public BAD_PARAM typecodeBadRepid(Throwable paramThrowable) { return typecodeBadRepid(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM typecodeBadRepid() { return typecodeBadRepid(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM typecodeInvMember(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446353, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.typecodeInvMember", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM typecodeInvMember(CompletionStatus paramCompletionStatus) { return typecodeInvMember(paramCompletionStatus, null); }
  
  public BAD_PARAM typecodeInvMember(Throwable paramThrowable) { return typecodeInvMember(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM typecodeInvMember() { return typecodeInvMember(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM tcUnionDupLabel(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446354, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.tcUnionDupLabel", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM tcUnionDupLabel(CompletionStatus paramCompletionStatus) { return tcUnionDupLabel(paramCompletionStatus, null); }
  
  public BAD_PARAM tcUnionDupLabel(Throwable paramThrowable) { return tcUnionDupLabel(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM tcUnionDupLabel() { return tcUnionDupLabel(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM tcUnionIncompatible(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446355, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.tcUnionIncompatible", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM tcUnionIncompatible(CompletionStatus paramCompletionStatus) { return tcUnionIncompatible(paramCompletionStatus, null); }
  
  public BAD_PARAM tcUnionIncompatible(Throwable paramThrowable) { return tcUnionIncompatible(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM tcUnionIncompatible() { return tcUnionIncompatible(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM tcUnionBadDisc(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446356, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.tcUnionBadDisc", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM tcUnionBadDisc(CompletionStatus paramCompletionStatus) { return tcUnionBadDisc(paramCompletionStatus, null); }
  
  public BAD_PARAM tcUnionBadDisc(Throwable paramThrowable) { return tcUnionBadDisc(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM tcUnionBadDisc() { return tcUnionBadDisc(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM setExceptionBadAny(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446357, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.setExceptionBadAny", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM setExceptionBadAny(CompletionStatus paramCompletionStatus) { return setExceptionBadAny(paramCompletionStatus, null); }
  
  public BAD_PARAM setExceptionBadAny(Throwable paramThrowable) { return setExceptionBadAny(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM setExceptionBadAny() { return setExceptionBadAny(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM setExceptionUnlisted(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446358, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.setExceptionUnlisted", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM setExceptionUnlisted(CompletionStatus paramCompletionStatus) { return setExceptionUnlisted(paramCompletionStatus, null); }
  
  public BAD_PARAM setExceptionUnlisted(Throwable paramThrowable) { return setExceptionUnlisted(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM setExceptionUnlisted() { return setExceptionUnlisted(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM noClientWcharCodesetCtx(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446359, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noClientWcharCodesetCtx", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM noClientWcharCodesetCtx(CompletionStatus paramCompletionStatus) { return noClientWcharCodesetCtx(paramCompletionStatus, null); }
  
  public BAD_PARAM noClientWcharCodesetCtx(Throwable paramThrowable) { return noClientWcharCodesetCtx(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM noClientWcharCodesetCtx() { return noClientWcharCodesetCtx(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM illegalServiceContext(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446360, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.illegalServiceContext", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM illegalServiceContext(CompletionStatus paramCompletionStatus) { return illegalServiceContext(paramCompletionStatus, null); }
  
  public BAD_PARAM illegalServiceContext(Throwable paramThrowable) { return illegalServiceContext(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM illegalServiceContext() { return illegalServiceContext(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM enumOutOfRange(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446361, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.enumOutOfRange", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM enumOutOfRange(CompletionStatus paramCompletionStatus) { return enumOutOfRange(paramCompletionStatus, null); }
  
  public BAD_PARAM enumOutOfRange(Throwable paramThrowable) { return enumOutOfRange(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM enumOutOfRange() { return enumOutOfRange(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM invalidServiceContextId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446362, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.invalidServiceContextId", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidServiceContextId(CompletionStatus paramCompletionStatus) { return invalidServiceContextId(paramCompletionStatus, null); }
  
  public BAD_PARAM invalidServiceContextId(Throwable paramThrowable) { return invalidServiceContextId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM invalidServiceContextId() { return invalidServiceContextId(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM rirWithNullObject(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446363, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.rirWithNullObject", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM rirWithNullObject(CompletionStatus paramCompletionStatus) { return rirWithNullObject(paramCompletionStatus, null); }
  
  public BAD_PARAM rirWithNullObject(Throwable paramThrowable) { return rirWithNullObject(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM rirWithNullObject() { return rirWithNullObject(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM invalidComponentId(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446364, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.FINE, "OMG.invalidComponentId", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidComponentId(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidComponentId(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM invalidComponentId(Throwable paramThrowable, Object paramObject) { return invalidComponentId(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM invalidComponentId(Object paramObject) { return invalidComponentId(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM invalidProfileId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446365, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.invalidProfileId", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidProfileId(CompletionStatus paramCompletionStatus) { return invalidProfileId(paramCompletionStatus, null); }
  
  public BAD_PARAM invalidProfileId(Throwable paramThrowable) { return invalidProfileId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM invalidProfileId() { return invalidProfileId(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM policyTypeDuplicate(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446366, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.policyTypeDuplicate", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM policyTypeDuplicate(CompletionStatus paramCompletionStatus) { return policyTypeDuplicate(paramCompletionStatus, null); }
  
  public BAD_PARAM policyTypeDuplicate(Throwable paramThrowable) { return policyTypeDuplicate(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM policyTypeDuplicate() { return policyTypeDuplicate(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM badOnewayDefinition(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446367, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badOnewayDefinition", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM badOnewayDefinition(CompletionStatus paramCompletionStatus) { return badOnewayDefinition(paramCompletionStatus, null); }
  
  public BAD_PARAM badOnewayDefinition(Throwable paramThrowable) { return badOnewayDefinition(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM badOnewayDefinition() { return badOnewayDefinition(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM diiForImplicitOperation(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446368, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.diiForImplicitOperation", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM diiForImplicitOperation(CompletionStatus paramCompletionStatus) { return diiForImplicitOperation(paramCompletionStatus, null); }
  
  public BAD_PARAM diiForImplicitOperation(Throwable paramThrowable) { return diiForImplicitOperation(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM diiForImplicitOperation() { return diiForImplicitOperation(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM xaCallInval(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446369, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.xaCallInval", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM xaCallInval(CompletionStatus paramCompletionStatus) { return xaCallInval(paramCompletionStatus, null); }
  
  public BAD_PARAM xaCallInval(Throwable paramThrowable) { return xaCallInval(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM xaCallInval() { return xaCallInval(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM unionBadDiscriminator(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446370, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.unionBadDiscriminator", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM unionBadDiscriminator(CompletionStatus paramCompletionStatus) { return unionBadDiscriminator(paramCompletionStatus, null); }
  
  public BAD_PARAM unionBadDiscriminator(Throwable paramThrowable) { return unionBadDiscriminator(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM unionBadDiscriminator() { return unionBadDiscriminator(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM ctxIllegalPropertyName(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446371, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.ctxIllegalPropertyName", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM ctxIllegalPropertyName(CompletionStatus paramCompletionStatus) { return ctxIllegalPropertyName(paramCompletionStatus, null); }
  
  public BAD_PARAM ctxIllegalPropertyName(Throwable paramThrowable) { return ctxIllegalPropertyName(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM ctxIllegalPropertyName() { return ctxIllegalPropertyName(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM ctxIllegalSearchString(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446372, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.ctxIllegalSearchString", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM ctxIllegalSearchString(CompletionStatus paramCompletionStatus) { return ctxIllegalSearchString(paramCompletionStatus, null); }
  
  public BAD_PARAM ctxIllegalSearchString(Throwable paramThrowable) { return ctxIllegalSearchString(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM ctxIllegalSearchString() { return ctxIllegalSearchString(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM ctxIllegalName(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446373, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.ctxIllegalName", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM ctxIllegalName(CompletionStatus paramCompletionStatus) { return ctxIllegalName(paramCompletionStatus, null); }
  
  public BAD_PARAM ctxIllegalName(Throwable paramThrowable) { return ctxIllegalName(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM ctxIllegalName() { return ctxIllegalName(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM ctxNonEmpty(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446374, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.ctxNonEmpty", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM ctxNonEmpty(CompletionStatus paramCompletionStatus) { return ctxNonEmpty(paramCompletionStatus, null); }
  
  public BAD_PARAM ctxNonEmpty(Throwable paramThrowable) { return ctxNonEmpty(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM ctxNonEmpty() { return ctxNonEmpty(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM invalidStreamFormatVersion(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446375, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "OMG.invalidStreamFormatVersion", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM invalidStreamFormatVersion(CompletionStatus paramCompletionStatus, Object paramObject) { return invalidStreamFormatVersion(paramCompletionStatus, null, paramObject); }
  
  public BAD_PARAM invalidStreamFormatVersion(Throwable paramThrowable, Object paramObject) { return invalidStreamFormatVersion(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public BAD_PARAM invalidStreamFormatVersion(Object paramObject) { return invalidStreamFormatVersion(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public BAD_PARAM notAValueoutputstream(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446376, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.notAValueoutputstream", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM notAValueoutputstream(CompletionStatus paramCompletionStatus) { return notAValueoutputstream(paramCompletionStatus, null); }
  
  public BAD_PARAM notAValueoutputstream(Throwable paramThrowable) { return notAValueoutputstream(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM notAValueoutputstream() { return notAValueoutputstream(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM notAValueinputstream(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1330446377, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.notAValueinputstream", arrayOfObject, OMGSystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM notAValueinputstream(CompletionStatus paramCompletionStatus) { return notAValueinputstream(paramCompletionStatus, null); }
  
  public BAD_PARAM notAValueinputstream(Throwable paramThrowable) { return notAValueinputstream(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM notAValueinputstream() { return notAValueinputstream(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_TYPECODE marshallIncompleteTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_TYPECODE bAD_TYPECODE = new BAD_TYPECODE(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_TYPECODE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.marshallIncompleteTypecode", arrayOfObject, OMGSystemException.class, bAD_TYPECODE);
    } 
    return bAD_TYPECODE;
  }
  
  public BAD_TYPECODE marshallIncompleteTypecode(CompletionStatus paramCompletionStatus) { return marshallIncompleteTypecode(paramCompletionStatus, null); }
  
  public BAD_TYPECODE marshallIncompleteTypecode(Throwable paramThrowable) { return marshallIncompleteTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_TYPECODE marshallIncompleteTypecode() { return marshallIncompleteTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_TYPECODE badMemberTypecode(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_TYPECODE bAD_TYPECODE = new BAD_TYPECODE(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_TYPECODE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badMemberTypecode", arrayOfObject, OMGSystemException.class, bAD_TYPECODE);
    } 
    return bAD_TYPECODE;
  }
  
  public BAD_TYPECODE badMemberTypecode(CompletionStatus paramCompletionStatus) { return badMemberTypecode(paramCompletionStatus, null); }
  
  public BAD_TYPECODE badMemberTypecode(Throwable paramThrowable) { return badMemberTypecode(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_TYPECODE badMemberTypecode() { return badMemberTypecode(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_TYPECODE illegalParameter(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_TYPECODE bAD_TYPECODE = new BAD_TYPECODE(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_TYPECODE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.illegalParameter", arrayOfObject, OMGSystemException.class, bAD_TYPECODE);
    } 
    return bAD_TYPECODE;
  }
  
  public BAD_TYPECODE illegalParameter(CompletionStatus paramCompletionStatus) { return illegalParameter(paramCompletionStatus, null); }
  
  public BAD_TYPECODE illegalParameter(Throwable paramThrowable) { return illegalParameter(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_TYPECODE illegalParameter() { return illegalParameter(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION charNotInCodeset(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.charNotInCodeset", arrayOfObject, OMGSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION charNotInCodeset(CompletionStatus paramCompletionStatus) { return charNotInCodeset(paramCompletionStatus, null); }
  
  public DATA_CONVERSION charNotInCodeset(Throwable paramThrowable) { return charNotInCodeset(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION charNotInCodeset() { return charNotInCodeset(CompletionStatus.COMPLETED_NO, null); }
  
  public DATA_CONVERSION priorityMapFailre(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    DATA_CONVERSION dATA_CONVERSION = new DATA_CONVERSION(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      dATA_CONVERSION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.priorityMapFailre", arrayOfObject, OMGSystemException.class, dATA_CONVERSION);
    } 
    return dATA_CONVERSION;
  }
  
  public DATA_CONVERSION priorityMapFailre(CompletionStatus paramCompletionStatus) { return priorityMapFailre(paramCompletionStatus, null); }
  
  public DATA_CONVERSION priorityMapFailre(Throwable paramThrowable) { return priorityMapFailre(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public DATA_CONVERSION priorityMapFailre() { return priorityMapFailre(CompletionStatus.COMPLETED_NO, null); }
  
  public IMP_LIMIT noUsableProfile(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    IMP_LIMIT iMP_LIMIT = new IMP_LIMIT(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      iMP_LIMIT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noUsableProfile", arrayOfObject, OMGSystemException.class, iMP_LIMIT);
    } 
    return iMP_LIMIT;
  }
  
  public IMP_LIMIT noUsableProfile(CompletionStatus paramCompletionStatus) { return noUsableProfile(paramCompletionStatus, null); }
  
  public IMP_LIMIT noUsableProfile(Throwable paramThrowable) { return noUsableProfile(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public IMP_LIMIT noUsableProfile() { return noUsableProfile(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE priorityRangeRestrict(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.priorityRangeRestrict", arrayOfObject, OMGSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE priorityRangeRestrict(CompletionStatus paramCompletionStatus) { return priorityRangeRestrict(paramCompletionStatus, null); }
  
  public INITIALIZE priorityRangeRestrict(Throwable paramThrowable) { return priorityRangeRestrict(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE priorityRangeRestrict() { return priorityRangeRestrict(CompletionStatus.COMPLETED_NO, null); }
  
  public INV_OBJREF noServerWcharCodesetCmp(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INV_OBJREF iNV_OBJREF = new INV_OBJREF(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_OBJREF.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noServerWcharCodesetCmp", arrayOfObject, OMGSystemException.class, iNV_OBJREF);
    } 
    return iNV_OBJREF;
  }
  
  public INV_OBJREF noServerWcharCodesetCmp(CompletionStatus paramCompletionStatus) { return noServerWcharCodesetCmp(paramCompletionStatus, null); }
  
  public INV_OBJREF noServerWcharCodesetCmp(Throwable paramThrowable) { return noServerWcharCodesetCmp(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INV_OBJREF noServerWcharCodesetCmp() { return noServerWcharCodesetCmp(CompletionStatus.COMPLETED_NO, null); }
  
  public INV_OBJREF codesetComponentRequired(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INV_OBJREF iNV_OBJREF = new INV_OBJREF(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_OBJREF.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.codesetComponentRequired", arrayOfObject, OMGSystemException.class, iNV_OBJREF);
    } 
    return iNV_OBJREF;
  }
  
  public INV_OBJREF codesetComponentRequired(CompletionStatus paramCompletionStatus) { return codesetComponentRequired(paramCompletionStatus, null); }
  
  public INV_OBJREF codesetComponentRequired(Throwable paramThrowable) { return codesetComponentRequired(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INV_OBJREF codesetComponentRequired() { return codesetComponentRequired(CompletionStatus.COMPLETED_NO, null); }
  
  public INV_POLICY iorPolicyReconcileError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INV_POLICY iNV_POLICY = new INV_POLICY(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_POLICY.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.iorPolicyReconcileError", arrayOfObject, OMGSystemException.class, iNV_POLICY);
    } 
    return iNV_POLICY;
  }
  
  public INV_POLICY iorPolicyReconcileError(CompletionStatus paramCompletionStatus) { return iorPolicyReconcileError(paramCompletionStatus, null); }
  
  public INV_POLICY iorPolicyReconcileError(Throwable paramThrowable) { return iorPolicyReconcileError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INV_POLICY iorPolicyReconcileError() { return iorPolicyReconcileError(CompletionStatus.COMPLETED_NO, null); }
  
  public INV_POLICY policyUnknown(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INV_POLICY iNV_POLICY = new INV_POLICY(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_POLICY.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.policyUnknown", arrayOfObject, OMGSystemException.class, iNV_POLICY);
    } 
    return iNV_POLICY;
  }
  
  public INV_POLICY policyUnknown(CompletionStatus paramCompletionStatus) { return policyUnknown(paramCompletionStatus, null); }
  
  public INV_POLICY policyUnknown(Throwable paramThrowable) { return policyUnknown(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INV_POLICY policyUnknown() { return policyUnknown(CompletionStatus.COMPLETED_NO, null); }
  
  public INV_POLICY noPolicyFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INV_POLICY iNV_POLICY = new INV_POLICY(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      iNV_POLICY.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noPolicyFactory", arrayOfObject, OMGSystemException.class, iNV_POLICY);
    } 
    return iNV_POLICY;
  }
  
  public INV_POLICY noPolicyFactory(CompletionStatus paramCompletionStatus) { return noPolicyFactory(paramCompletionStatus, null); }
  
  public INV_POLICY noPolicyFactory(Throwable paramThrowable) { return noPolicyFactory(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INV_POLICY noPolicyFactory() { return noPolicyFactory(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL xaRmerr(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.xaRmerr", arrayOfObject, OMGSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL xaRmerr(CompletionStatus paramCompletionStatus) { return xaRmerr(paramCompletionStatus, null); }
  
  public INTERNAL xaRmerr(Throwable paramThrowable) { return xaRmerr(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL xaRmerr() { return xaRmerr(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL xaRmfail(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.xaRmfail", arrayOfObject, OMGSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL xaRmfail(CompletionStatus paramCompletionStatus) { return xaRmfail(paramCompletionStatus, null); }
  
  public INTERNAL xaRmfail(Throwable paramThrowable) { return xaRmfail(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL xaRmfail() { return xaRmfail(CompletionStatus.COMPLETED_NO, null); }
  
  public INTF_REPOS noIr(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTF_REPOS iNTF_REPOS = new INTF_REPOS(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      iNTF_REPOS.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noIr", arrayOfObject, OMGSystemException.class, iNTF_REPOS);
    } 
    return iNTF_REPOS;
  }
  
  public INTF_REPOS noIr(CompletionStatus paramCompletionStatus) { return noIr(paramCompletionStatus, null); }
  
  public INTF_REPOS noIr(Throwable paramThrowable) { return noIr(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTF_REPOS noIr() { return noIr(CompletionStatus.COMPLETED_NO, null); }
  
  public INTF_REPOS noInterfaceInIr(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTF_REPOS iNTF_REPOS = new INTF_REPOS(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      iNTF_REPOS.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noInterfaceInIr", arrayOfObject, OMGSystemException.class, iNTF_REPOS);
    } 
    return iNTF_REPOS;
  }
  
  public INTF_REPOS noInterfaceInIr(CompletionStatus paramCompletionStatus) { return noInterfaceInIr(paramCompletionStatus, null); }
  
  public INTF_REPOS noInterfaceInIr(Throwable paramThrowable) { return noInterfaceInIr(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTF_REPOS noInterfaceInIr() { return noInterfaceInIr(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL unableLocateValueFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.unableLocateValueFactory", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unableLocateValueFactory(CompletionStatus paramCompletionStatus) { return unableLocateValueFactory(paramCompletionStatus, null); }
  
  public MARSHAL unableLocateValueFactory(Throwable paramThrowable) { return unableLocateValueFactory(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL unableLocateValueFactory() { return unableLocateValueFactory(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL setResultBeforeCtx(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.setResultBeforeCtx", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL setResultBeforeCtx(CompletionStatus paramCompletionStatus) { return setResultBeforeCtx(paramCompletionStatus, null); }
  
  public MARSHAL setResultBeforeCtx(Throwable paramThrowable) { return setResultBeforeCtx(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL setResultBeforeCtx() { return setResultBeforeCtx(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL badNvlist(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badNvlist", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL badNvlist(CompletionStatus paramCompletionStatus) { return badNvlist(paramCompletionStatus, null); }
  
  public MARSHAL badNvlist(Throwable paramThrowable) { return badNvlist(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL badNvlist() { return badNvlist(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL notAnObjectImpl(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446340, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.notAnObjectImpl", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL notAnObjectImpl(CompletionStatus paramCompletionStatus) { return notAnObjectImpl(paramCompletionStatus, null); }
  
  public MARSHAL notAnObjectImpl(Throwable paramThrowable) { return notAnObjectImpl(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL notAnObjectImpl() { return notAnObjectImpl(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL wcharBadGiopVersionSent(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446341, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.wcharBadGiopVersionSent", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL wcharBadGiopVersionSent(CompletionStatus paramCompletionStatus) { return wcharBadGiopVersionSent(paramCompletionStatus, null); }
  
  public MARSHAL wcharBadGiopVersionSent(Throwable paramThrowable) { return wcharBadGiopVersionSent(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL wcharBadGiopVersionSent() { return wcharBadGiopVersionSent(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL wcharBadGiopVersionReturned(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446342, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.wcharBadGiopVersionReturned", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL wcharBadGiopVersionReturned(CompletionStatus paramCompletionStatus) { return wcharBadGiopVersionReturned(paramCompletionStatus, null); }
  
  public MARSHAL wcharBadGiopVersionReturned(Throwable paramThrowable) { return wcharBadGiopVersionReturned(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL wcharBadGiopVersionReturned() { return wcharBadGiopVersionReturned(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL unsupportedFormatVersion(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446343, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.unsupportedFormatVersion", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL unsupportedFormatVersion(CompletionStatus paramCompletionStatus) { return unsupportedFormatVersion(paramCompletionStatus, null); }
  
  public MARSHAL unsupportedFormatVersion(Throwable paramThrowable) { return unsupportedFormatVersion(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL unsupportedFormatVersion() { return unsupportedFormatVersion(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible1(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446344, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.rmiiiopOptionalDataIncompatible1", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL rmiiiopOptionalDataIncompatible1(CompletionStatus paramCompletionStatus) { return rmiiiopOptionalDataIncompatible1(paramCompletionStatus, null); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible1(Throwable paramThrowable) { return rmiiiopOptionalDataIncompatible1(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible1() { return rmiiiopOptionalDataIncompatible1(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible2(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446344, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.rmiiiopOptionalDataIncompatible2", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL rmiiiopOptionalDataIncompatible2(CompletionStatus paramCompletionStatus) { return rmiiiopOptionalDataIncompatible2(paramCompletionStatus, null); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible2(Throwable paramThrowable) { return rmiiiopOptionalDataIncompatible2(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible2() { return rmiiiopOptionalDataIncompatible2(CompletionStatus.COMPLETED_NO, null); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible3(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    MARSHAL mARSHAL = new MARSHAL(1330446344, paramCompletionStatus);
    if (paramThrowable != null)
      mARSHAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.rmiiiopOptionalDataIncompatible3", arrayOfObject, OMGSystemException.class, mARSHAL);
    } 
    return mARSHAL;
  }
  
  public MARSHAL rmiiiopOptionalDataIncompatible3(CompletionStatus paramCompletionStatus) { return rmiiiopOptionalDataIncompatible3(paramCompletionStatus, null); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible3(Throwable paramThrowable) { return rmiiiopOptionalDataIncompatible3(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public MARSHAL rmiiiopOptionalDataIncompatible3() { return rmiiiopOptionalDataIncompatible3(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT missingLocalValueImpl(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.missingLocalValueImpl", arrayOfObject, OMGSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT missingLocalValueImpl(CompletionStatus paramCompletionStatus) { return missingLocalValueImpl(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT missingLocalValueImpl(Throwable paramThrowable) { return missingLocalValueImpl(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT missingLocalValueImpl() { return missingLocalValueImpl(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT incompatibleValueImpl(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.incompatibleValueImpl", arrayOfObject, OMGSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT incompatibleValueImpl(CompletionStatus paramCompletionStatus) { return incompatibleValueImpl(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT incompatibleValueImpl(Throwable paramThrowable) { return incompatibleValueImpl(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT incompatibleValueImpl() { return incompatibleValueImpl(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT noUsableProfile2(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noUsableProfile2", arrayOfObject, OMGSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT noUsableProfile2(CompletionStatus paramCompletionStatus) { return noUsableProfile2(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT noUsableProfile2(Throwable paramThrowable) { return noUsableProfile2(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT noUsableProfile2() { return noUsableProfile2(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT diiLocalObject(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1330446340, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.diiLocalObject", arrayOfObject, OMGSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT diiLocalObject(CompletionStatus paramCompletionStatus) { return diiLocalObject(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT diiLocalObject(Throwable paramThrowable) { return diiLocalObject(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT diiLocalObject() { return diiLocalObject(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT bioReset(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1330446341, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.bioReset", arrayOfObject, OMGSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT bioReset(CompletionStatus paramCompletionStatus) { return bioReset(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT bioReset(Throwable paramThrowable) { return bioReset(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT bioReset() { return bioReset(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT bioMetaNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1330446342, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.bioMetaNotAvailable", arrayOfObject, OMGSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT bioMetaNotAvailable(CompletionStatus paramCompletionStatus) { return bioMetaNotAvailable(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT bioMetaNotAvailable(Throwable paramThrowable) { return bioMetaNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT bioMetaNotAvailable() { return bioMetaNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT bioGenomicNoIterator(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1330446343, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.bioGenomicNoIterator", arrayOfObject, OMGSystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT bioGenomicNoIterator(CompletionStatus paramCompletionStatus) { return bioGenomicNoIterator(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT bioGenomicNoIterator(Throwable paramThrowable) { return bioGenomicNoIterator(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT bioGenomicNoIterator() { return bioGenomicNoIterator(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES piOperationNotSupported1(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.piOperationNotSupported1", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES piOperationNotSupported1(CompletionStatus paramCompletionStatus) { return piOperationNotSupported1(paramCompletionStatus, null); }
  
  public NO_RESOURCES piOperationNotSupported1(Throwable paramThrowable) { return piOperationNotSupported1(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES piOperationNotSupported1() { return piOperationNotSupported1(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES piOperationNotSupported2(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.piOperationNotSupported2", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES piOperationNotSupported2(CompletionStatus paramCompletionStatus) { return piOperationNotSupported2(paramCompletionStatus, null); }
  
  public NO_RESOURCES piOperationNotSupported2(Throwable paramThrowable) { return piOperationNotSupported2(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES piOperationNotSupported2() { return piOperationNotSupported2(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES piOperationNotSupported3(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.piOperationNotSupported3", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES piOperationNotSupported3(CompletionStatus paramCompletionStatus) { return piOperationNotSupported3(paramCompletionStatus, null); }
  
  public NO_RESOURCES piOperationNotSupported3(Throwable paramThrowable) { return piOperationNotSupported3(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES piOperationNotSupported3() { return piOperationNotSupported3(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES piOperationNotSupported4(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.piOperationNotSupported4", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES piOperationNotSupported4(CompletionStatus paramCompletionStatus) { return piOperationNotSupported4(paramCompletionStatus, null); }
  
  public NO_RESOURCES piOperationNotSupported4(Throwable paramThrowable) { return piOperationNotSupported4(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES piOperationNotSupported4() { return piOperationNotSupported4(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES piOperationNotSupported5(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.piOperationNotSupported5", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES piOperationNotSupported5(CompletionStatus paramCompletionStatus) { return piOperationNotSupported5(paramCompletionStatus, null); }
  
  public NO_RESOURCES piOperationNotSupported5(Throwable paramThrowable) { return piOperationNotSupported5(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES piOperationNotSupported5() { return piOperationNotSupported5(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES piOperationNotSupported6(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.piOperationNotSupported6", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES piOperationNotSupported6(CompletionStatus paramCompletionStatus) { return piOperationNotSupported6(paramCompletionStatus, null); }
  
  public NO_RESOURCES piOperationNotSupported6(Throwable paramThrowable) { return piOperationNotSupported6(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES piOperationNotSupported6() { return piOperationNotSupported6(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES piOperationNotSupported7(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.piOperationNotSupported7", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES piOperationNotSupported7(CompletionStatus paramCompletionStatus) { return piOperationNotSupported7(paramCompletionStatus, null); }
  
  public NO_RESOURCES piOperationNotSupported7(Throwable paramThrowable) { return piOperationNotSupported7(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES piOperationNotSupported7() { return piOperationNotSupported7(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES piOperationNotSupported8(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.piOperationNotSupported8", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES piOperationNotSupported8(CompletionStatus paramCompletionStatus) { return piOperationNotSupported8(paramCompletionStatus, null); }
  
  public NO_RESOURCES piOperationNotSupported8(Throwable paramThrowable) { return piOperationNotSupported8(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES piOperationNotSupported8() { return piOperationNotSupported8(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_RESOURCES noConnectionPriority(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_RESOURCES nO_RESOURCES = new NO_RESOURCES(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      nO_RESOURCES.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noConnectionPriority", arrayOfObject, OMGSystemException.class, nO_RESOURCES);
    } 
    return nO_RESOURCES;
  }
  
  public NO_RESOURCES noConnectionPriority(CompletionStatus paramCompletionStatus) { return noConnectionPriority(paramCompletionStatus, null); }
  
  public NO_RESOURCES noConnectionPriority(Throwable paramThrowable) { return noConnectionPriority(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_RESOURCES noConnectionPriority() { return noConnectionPriority(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSACTION_ROLLEDBACK xaRb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSACTION_ROLLEDBACK tRANSACTION_ROLLEDBACK = new TRANSACTION_ROLLEDBACK(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSACTION_ROLLEDBACK.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.xaRb", arrayOfObject, OMGSystemException.class, tRANSACTION_ROLLEDBACK);
    } 
    return tRANSACTION_ROLLEDBACK;
  }
  
  public TRANSACTION_ROLLEDBACK xaRb(CompletionStatus paramCompletionStatus) { return xaRb(paramCompletionStatus, null); }
  
  public TRANSACTION_ROLLEDBACK xaRb(Throwable paramThrowable) { return xaRb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSACTION_ROLLEDBACK xaRb() { return xaRb(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSACTION_ROLLEDBACK xaNota(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSACTION_ROLLEDBACK tRANSACTION_ROLLEDBACK = new TRANSACTION_ROLLEDBACK(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSACTION_ROLLEDBACK.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.xaNota", arrayOfObject, OMGSystemException.class, tRANSACTION_ROLLEDBACK);
    } 
    return tRANSACTION_ROLLEDBACK;
  }
  
  public TRANSACTION_ROLLEDBACK xaNota(CompletionStatus paramCompletionStatus) { return xaNota(paramCompletionStatus, null); }
  
  public TRANSACTION_ROLLEDBACK xaNota(Throwable paramThrowable) { return xaNota(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSACTION_ROLLEDBACK xaNota() { return xaNota(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSACTION_ROLLEDBACK tRANSACTION_ROLLEDBACK = new TRANSACTION_ROLLEDBACK(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSACTION_ROLLEDBACK.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.xaEndTrueRollbackDeferred", arrayOfObject, OMGSystemException.class, tRANSACTION_ROLLEDBACK);
    } 
    return tRANSACTION_ROLLEDBACK;
  }
  
  public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(CompletionStatus paramCompletionStatus) { return xaEndTrueRollbackDeferred(paramCompletionStatus, null); }
  
  public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred(Throwable paramThrowable) { return xaEndTrueRollbackDeferred(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSACTION_ROLLEDBACK xaEndTrueRollbackDeferred() { return xaEndTrueRollbackDeferred(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSIENT poaRequestDiscard(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSIENT tRANSIENT = new TRANSIENT(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSIENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.poaRequestDiscard", arrayOfObject, OMGSystemException.class, tRANSIENT);
    } 
    return tRANSIENT;
  }
  
  public TRANSIENT poaRequestDiscard(CompletionStatus paramCompletionStatus) { return poaRequestDiscard(paramCompletionStatus, null); }
  
  public TRANSIENT poaRequestDiscard(Throwable paramThrowable) { return poaRequestDiscard(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSIENT poaRequestDiscard() { return poaRequestDiscard(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSIENT noUsableProfile3(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSIENT tRANSIENT = new TRANSIENT(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSIENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noUsableProfile3", arrayOfObject, OMGSystemException.class, tRANSIENT);
    } 
    return tRANSIENT;
  }
  
  public TRANSIENT noUsableProfile3(CompletionStatus paramCompletionStatus) { return noUsableProfile3(paramCompletionStatus, null); }
  
  public TRANSIENT noUsableProfile3(Throwable paramThrowable) { return noUsableProfile3(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSIENT noUsableProfile3() { return noUsableProfile3(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSIENT requestCancelled(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSIENT tRANSIENT = new TRANSIENT(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSIENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.requestCancelled", arrayOfObject, OMGSystemException.class, tRANSIENT);
    } 
    return tRANSIENT;
  }
  
  public TRANSIENT requestCancelled(CompletionStatus paramCompletionStatus) { return requestCancelled(paramCompletionStatus, null); }
  
  public TRANSIENT requestCancelled(Throwable paramThrowable) { return requestCancelled(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSIENT requestCancelled() { return requestCancelled(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSIENT poaDestroyed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSIENT tRANSIENT = new TRANSIENT(1330446340, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSIENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.poaDestroyed", arrayOfObject, OMGSystemException.class, tRANSIENT);
    } 
    return tRANSIENT;
  }
  
  public TRANSIENT poaDestroyed(CompletionStatus paramCompletionStatus) { return poaDestroyed(paramCompletionStatus, null); }
  
  public TRANSIENT poaDestroyed(Throwable paramThrowable) { return poaDestroyed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSIENT poaDestroyed() { return poaDestroyed(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST unregisteredValueAsObjref(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.unregisteredValueAsObjref", arrayOfObject, OMGSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST unregisteredValueAsObjref(CompletionStatus paramCompletionStatus) { return unregisteredValueAsObjref(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST unregisteredValueAsObjref(Throwable paramThrowable) { return unregisteredValueAsObjref(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST unregisteredValueAsObjref() { return unregisteredValueAsObjref(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST noObjectAdaptor(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.noObjectAdaptor", arrayOfObject, OMGSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST noObjectAdaptor(CompletionStatus paramCompletionStatus) { return noObjectAdaptor(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST noObjectAdaptor(Throwable paramThrowable) { return noObjectAdaptor(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST noObjectAdaptor() { return noObjectAdaptor(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST bioNotAvailable(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.bioNotAvailable", arrayOfObject, OMGSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST bioNotAvailable(CompletionStatus paramCompletionStatus) { return bioNotAvailable(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST bioNotAvailable(Throwable paramThrowable) { return bioNotAvailable(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST bioNotAvailable() { return bioNotAvailable(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST objectAdapterInactive(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1330446340, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.objectAdapterInactive", arrayOfObject, OMGSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST objectAdapterInactive(CompletionStatus paramCompletionStatus) { return objectAdapterInactive(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST objectAdapterInactive(Throwable paramThrowable) { return objectAdapterInactive(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST objectAdapterInactive() { return objectAdapterInactive(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER adapterActivatorException(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject1, Object paramObject2) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = paramObject1;
      arrayOfObject[1] = paramObject2;
      doLog(Level.WARNING, "OMG.adapterActivatorException", arrayOfObject, OMGSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER adapterActivatorException(CompletionStatus paramCompletionStatus, Object paramObject1, Object paramObject2) { return adapterActivatorException(paramCompletionStatus, null, paramObject1, paramObject2); }
  
  public OBJ_ADAPTER adapterActivatorException(Throwable paramThrowable, Object paramObject1, Object paramObject2) { return adapterActivatorException(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject1, paramObject2); }
  
  public OBJ_ADAPTER adapterActivatorException(Object paramObject1, Object paramObject2) { return adapterActivatorException(CompletionStatus.COMPLETED_NO, null, paramObject1, paramObject2); }
  
  public OBJ_ADAPTER badServantType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badServantType", arrayOfObject, OMGSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER badServantType(CompletionStatus paramCompletionStatus) { return badServantType(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER badServantType(Throwable paramThrowable) { return badServantType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER badServantType() { return badServantType(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER noDefaultServant(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noDefaultServant", arrayOfObject, OMGSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER noDefaultServant(CompletionStatus paramCompletionStatus) { return noDefaultServant(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER noDefaultServant(Throwable paramThrowable) { return noDefaultServant(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER noDefaultServant() { return noDefaultServant(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER noServantManager(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1330446340, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.noServantManager", arrayOfObject, OMGSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER noServantManager(CompletionStatus paramCompletionStatus) { return noServantManager(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER noServantManager(Throwable paramThrowable) { return noServantManager(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER noServantManager() { return noServantManager(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER badPolicyIncarnate(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1330446341, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.badPolicyIncarnate", arrayOfObject, OMGSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER badPolicyIncarnate(CompletionStatus paramCompletionStatus) { return badPolicyIncarnate(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER badPolicyIncarnate(Throwable paramThrowable) { return badPolicyIncarnate(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER badPolicyIncarnate() { return badPolicyIncarnate(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER piExcCompEstablished(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1330446342, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.piExcCompEstablished", arrayOfObject, OMGSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER piExcCompEstablished(CompletionStatus paramCompletionStatus) { return piExcCompEstablished(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER piExcCompEstablished(Throwable paramThrowable) { return piExcCompEstablished(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER piExcCompEstablished() { return piExcCompEstablished(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER nullServantReturned(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1330446343, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.nullServantReturned", arrayOfObject, OMGSystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER nullServantReturned(CompletionStatus paramCompletionStatus) { return nullServantReturned(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER nullServantReturned(Throwable paramThrowable) { return nullServantReturned(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER nullServantReturned() { return nullServantReturned(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownUserException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1330446337, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "OMG.unknownUserException", arrayOfObject, OMGSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownUserException(CompletionStatus paramCompletionStatus) { return unknownUserException(paramCompletionStatus, null); }
  
  public UNKNOWN unknownUserException(Throwable paramThrowable) { return unknownUserException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownUserException() { return unknownUserException(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unsupportedSystemException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1330446338, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.unsupportedSystemException", arrayOfObject, OMGSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unsupportedSystemException(CompletionStatus paramCompletionStatus) { return unsupportedSystemException(paramCompletionStatus, null); }
  
  public UNKNOWN unsupportedSystemException(Throwable paramThrowable) { return unsupportedSystemException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unsupportedSystemException() { return unsupportedSystemException(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN piUnknownUserException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1330446339, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "OMG.piUnknownUserException", arrayOfObject, OMGSystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN piUnknownUserException(CompletionStatus paramCompletionStatus) { return piUnknownUserException(paramCompletionStatus, null); }
  
  public UNKNOWN piUnknownUserException(Throwable paramThrowable) { return piUnknownUserException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN piUnknownUserException() { return piUnknownUserException(CompletionStatus.COMPLETED_NO, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\logging\OMGSystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */