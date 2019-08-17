package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;

public class POASystemException extends LogWrapperBase {
  private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger param1Logger) { return new POASystemException(param1Logger); }
    };
  
  public static final int SERVANT_MANAGER_ALREADY_SET = 1398080489;
  
  public static final int DESTROY_DEADLOCK = 1398080490;
  
  public static final int SERVANT_ORB = 1398080489;
  
  public static final int BAD_SERVANT = 1398080490;
  
  public static final int ILLEGAL_FORWARD_REQUEST = 1398080491;
  
  public static final int BAD_TRANSACTION_CONTEXT = 1398080489;
  
  public static final int BAD_REPOSITORY_ID = 1398080490;
  
  public static final int INVOKESETUP = 1398080489;
  
  public static final int BAD_LOCALREPLYSTATUS = 1398080490;
  
  public static final int PERSISTENT_SERVERPORT_ERROR = 1398080491;
  
  public static final int SERVANT_DISPATCH = 1398080492;
  
  public static final int WRONG_CLIENTSC = 1398080493;
  
  public static final int CANT_CLONE_TEMPLATE = 1398080494;
  
  public static final int POACURRENT_UNBALANCED_STACK = 1398080495;
  
  public static final int POACURRENT_NULL_FIELD = 1398080496;
  
  public static final int POA_INTERNAL_GET_SERVANT_ERROR = 1398080497;
  
  public static final int MAKE_FACTORY_NOT_POA = 1398080498;
  
  public static final int DUPLICATE_ORB_VERSION_SC = 1398080499;
  
  public static final int PREINVOKE_CLONE_ERROR = 1398080500;
  
  public static final int PREINVOKE_POA_DESTROYED = 1398080501;
  
  public static final int PMF_CREATE_RETAIN = 1398080502;
  
  public static final int PMF_CREATE_NON_RETAIN = 1398080503;
  
  public static final int POLICY_MEDIATOR_BAD_POLICY_IN_FACTORY = 1398080504;
  
  public static final int SERVANT_TO_ID_OAA = 1398080505;
  
  public static final int SERVANT_TO_ID_SAA = 1398080506;
  
  public static final int SERVANT_TO_ID_WP = 1398080507;
  
  public static final int CANT_RESOLVE_ROOT_POA = 1398080508;
  
  public static final int SERVANT_MUST_BE_LOCAL = 1398080509;
  
  public static final int NO_PROFILES_IN_IOR = 1398080510;
  
  public static final int AOM_ENTRY_DEC_ZERO = 1398080511;
  
  public static final int ADD_POA_INACTIVE = 1398080512;
  
  public static final int ILLEGAL_POA_STATE_TRANS = 1398080513;
  
  public static final int UNEXPECTED_EXCEPTION = 1398080514;
  
  public static final int SINGLE_THREAD_NOT_SUPPORTED = 1398080489;
  
  public static final int METHOD_NOT_IMPLEMENTED = 1398080490;
  
  public static final int POA_LOOKUP_ERROR = 1398080489;
  
  public static final int POA_INACTIVE = 1398080490;
  
  public static final int POA_NO_SERVANT_MANAGER = 1398080491;
  
  public static final int POA_NO_DEFAULT_SERVANT = 1398080492;
  
  public static final int POA_SERVANT_NOT_UNIQUE = 1398080493;
  
  public static final int POA_WRONG_POLICY = 1398080494;
  
  public static final int FINDPOA_ERROR = 1398080495;
  
  public static final int POA_SERVANT_ACTIVATOR_LOOKUP_FAILED = 1398080497;
  
  public static final int POA_BAD_SERVANT_MANAGER = 1398080498;
  
  public static final int POA_SERVANT_LOCATOR_LOOKUP_FAILED = 1398080499;
  
  public static final int POA_UNKNOWN_POLICY = 1398080500;
  
  public static final int POA_NOT_FOUND = 1398080501;
  
  public static final int SERVANT_LOOKUP = 1398080502;
  
  public static final int LOCAL_SERVANT_LOOKUP = 1398080503;
  
  public static final int SERVANT_MANAGER_BAD_TYPE = 1398080504;
  
  public static final int DEFAULT_POA_NOT_POAIMPL = 1398080505;
  
  public static final int WRONG_POLICIES_FOR_THIS_OBJECT = 1398080506;
  
  public static final int THIS_OBJECT_SERVANT_NOT_ACTIVE = 1398080507;
  
  public static final int THIS_OBJECT_WRONG_POLICY = 1398080508;
  
  public static final int NO_CONTEXT = 1398080509;
  
  public static final int INCARNATE_RETURNED_NULL = 1398080510;
  
  public static final int JTS_INIT_ERROR = 1398080489;
  
  public static final int PERSISTENT_SERVERID_NOT_SET = 1398080490;
  
  public static final int PERSISTENT_SERVERPORT_NOT_SET = 1398080491;
  
  public static final int ORBD_ERROR = 1398080492;
  
  public static final int BOOTSTRAP_ERROR = 1398080493;
  
  public static final int POA_DISCARDING = 1398080489;
  
  public static final int OTSHOOKEXCEPTION = 1398080489;
  
  public static final int UNKNOWN_SERVER_EXCEPTION = 1398080490;
  
  public static final int UNKNOWN_SERVERAPP_EXCEPTION = 1398080491;
  
  public static final int UNKNOWN_LOCALINVOCATION_ERROR = 1398080492;
  
  public static final int ADAPTER_ACTIVATOR_NONEXISTENT = 1398080489;
  
  public static final int ADAPTER_ACTIVATOR_FAILED = 1398080490;
  
  public static final int BAD_SKELETON = 1398080491;
  
  public static final int NULL_SERVANT = 1398080492;
  
  public static final int ADAPTER_DESTROYED = 1398080493;
  
  public POASystemException(Logger paramLogger) { super(paramLogger); }
  
  public static POASystemException get(ORB paramORB, String paramString) { return (POASystemException)paramORB.getLogWrapper(paramString, "POA", factory); }
  
  public static POASystemException get(String paramString) { return (POASystemException)ORB.staticGetLogWrapper(paramString, "POA", factory); }
  
  public BAD_INV_ORDER servantManagerAlreadySet(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantManagerAlreadySet", arrayOfObject, POASystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER servantManagerAlreadySet(CompletionStatus paramCompletionStatus) { return servantManagerAlreadySet(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER servantManagerAlreadySet(Throwable paramThrowable) { return servantManagerAlreadySet(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER servantManagerAlreadySet() { return servantManagerAlreadySet(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_INV_ORDER destroyDeadlock(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_INV_ORDER bAD_INV_ORDER = new BAD_INV_ORDER(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_INV_ORDER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.destroyDeadlock", arrayOfObject, POASystemException.class, bAD_INV_ORDER);
    } 
    return bAD_INV_ORDER;
  }
  
  public BAD_INV_ORDER destroyDeadlock(CompletionStatus paramCompletionStatus) { return destroyDeadlock(paramCompletionStatus, null); }
  
  public BAD_INV_ORDER destroyDeadlock(Throwable paramThrowable) { return destroyDeadlock(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_INV_ORDER destroyDeadlock() { return destroyDeadlock(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION servantOrb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantOrb", arrayOfObject, POASystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION servantOrb(CompletionStatus paramCompletionStatus) { return servantOrb(paramCompletionStatus, null); }
  
  public BAD_OPERATION servantOrb(Throwable paramThrowable) { return servantOrb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION servantOrb() { return servantOrb(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION badServant(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.badServant", arrayOfObject, POASystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION badServant(CompletionStatus paramCompletionStatus) { return badServant(paramCompletionStatus, null); }
  
  public BAD_OPERATION badServant(Throwable paramThrowable) { return badServant(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION badServant() { return badServant(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_OPERATION illegalForwardRequest(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_OPERATION bAD_OPERATION = new BAD_OPERATION(1398080491, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_OPERATION.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.illegalForwardRequest", arrayOfObject, POASystemException.class, bAD_OPERATION);
    } 
    return bAD_OPERATION;
  }
  
  public BAD_OPERATION illegalForwardRequest(CompletionStatus paramCompletionStatus) { return illegalForwardRequest(paramCompletionStatus, null); }
  
  public BAD_OPERATION illegalForwardRequest(Throwable paramThrowable) { return illegalForwardRequest(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_OPERATION illegalForwardRequest() { return illegalForwardRequest(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM badTransactionContext(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.badTransactionContext", arrayOfObject, POASystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM badTransactionContext(CompletionStatus paramCompletionStatus) { return badTransactionContext(paramCompletionStatus, null); }
  
  public BAD_PARAM badTransactionContext(Throwable paramThrowable) { return badTransactionContext(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM badTransactionContext() { return badTransactionContext(CompletionStatus.COMPLETED_NO, null); }
  
  public BAD_PARAM badRepositoryId(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    BAD_PARAM bAD_PARAM = new BAD_PARAM(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      bAD_PARAM.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.badRepositoryId", arrayOfObject, POASystemException.class, bAD_PARAM);
    } 
    return bAD_PARAM;
  }
  
  public BAD_PARAM badRepositoryId(CompletionStatus paramCompletionStatus) { return badRepositoryId(paramCompletionStatus, null); }
  
  public BAD_PARAM badRepositoryId(Throwable paramThrowable) { return badRepositoryId(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public BAD_PARAM badRepositoryId() { return badRepositoryId(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL invokesetup(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.invokesetup", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL invokesetup(CompletionStatus paramCompletionStatus) { return invokesetup(paramCompletionStatus, null); }
  
  public INTERNAL invokesetup(Throwable paramThrowable) { return invokesetup(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL invokesetup() { return invokesetup(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL badLocalreplystatus(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.badLocalreplystatus", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL badLocalreplystatus(CompletionStatus paramCompletionStatus) { return badLocalreplystatus(paramCompletionStatus, null); }
  
  public INTERNAL badLocalreplystatus(Throwable paramThrowable) { return badLocalreplystatus(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL badLocalreplystatus() { return badLocalreplystatus(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL persistentServerportError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080491, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.persistentServerportError", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL persistentServerportError(CompletionStatus paramCompletionStatus) { return persistentServerportError(paramCompletionStatus, null); }
  
  public INTERNAL persistentServerportError(Throwable paramThrowable) { return persistentServerportError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL persistentServerportError() { return persistentServerportError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL servantDispatch(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080492, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantDispatch", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL servantDispatch(CompletionStatus paramCompletionStatus) { return servantDispatch(paramCompletionStatus, null); }
  
  public INTERNAL servantDispatch(Throwable paramThrowable) { return servantDispatch(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL servantDispatch() { return servantDispatch(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL wrongClientsc(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080493, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.wrongClientsc", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL wrongClientsc(CompletionStatus paramCompletionStatus) { return wrongClientsc(paramCompletionStatus, null); }
  
  public INTERNAL wrongClientsc(Throwable paramThrowable) { return wrongClientsc(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL wrongClientsc() { return wrongClientsc(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL cantCloneTemplate(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080494, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.cantCloneTemplate", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL cantCloneTemplate(CompletionStatus paramCompletionStatus) { return cantCloneTemplate(paramCompletionStatus, null); }
  
  public INTERNAL cantCloneTemplate(Throwable paramThrowable) { return cantCloneTemplate(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL cantCloneTemplate() { return cantCloneTemplate(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL poacurrentUnbalancedStack(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080495, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poacurrentUnbalancedStack", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL poacurrentUnbalancedStack(CompletionStatus paramCompletionStatus) { return poacurrentUnbalancedStack(paramCompletionStatus, null); }
  
  public INTERNAL poacurrentUnbalancedStack(Throwable paramThrowable) { return poacurrentUnbalancedStack(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL poacurrentUnbalancedStack() { return poacurrentUnbalancedStack(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL poacurrentNullField(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080496, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poacurrentNullField", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL poacurrentNullField(CompletionStatus paramCompletionStatus) { return poacurrentNullField(paramCompletionStatus, null); }
  
  public INTERNAL poacurrentNullField(Throwable paramThrowable) { return poacurrentNullField(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL poacurrentNullField() { return poacurrentNullField(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL poaInternalGetServantError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080497, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaInternalGetServantError", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL poaInternalGetServantError(CompletionStatus paramCompletionStatus) { return poaInternalGetServantError(paramCompletionStatus, null); }
  
  public INTERNAL poaInternalGetServantError(Throwable paramThrowable) { return poaInternalGetServantError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL poaInternalGetServantError() { return poaInternalGetServantError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL makeFactoryNotPoa(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398080498, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "POA.makeFactoryNotPoa", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL makeFactoryNotPoa(CompletionStatus paramCompletionStatus, Object paramObject) { return makeFactoryNotPoa(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL makeFactoryNotPoa(Throwable paramThrowable, Object paramObject) { return makeFactoryNotPoa(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL makeFactoryNotPoa(Object paramObject) { return makeFactoryNotPoa(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public INTERNAL duplicateOrbVersionSc(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080499, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.duplicateOrbVersionSc", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL duplicateOrbVersionSc(CompletionStatus paramCompletionStatus) { return duplicateOrbVersionSc(paramCompletionStatus, null); }
  
  public INTERNAL duplicateOrbVersionSc(Throwable paramThrowable) { return duplicateOrbVersionSc(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL duplicateOrbVersionSc() { return duplicateOrbVersionSc(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL preinvokeCloneError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080500, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.preinvokeCloneError", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL preinvokeCloneError(CompletionStatus paramCompletionStatus) { return preinvokeCloneError(paramCompletionStatus, null); }
  
  public INTERNAL preinvokeCloneError(Throwable paramThrowable) { return preinvokeCloneError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL preinvokeCloneError() { return preinvokeCloneError(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL preinvokePoaDestroyed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080501, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.preinvokePoaDestroyed", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL preinvokePoaDestroyed(CompletionStatus paramCompletionStatus) { return preinvokePoaDestroyed(paramCompletionStatus, null); }
  
  public INTERNAL preinvokePoaDestroyed(Throwable paramThrowable) { return preinvokePoaDestroyed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL preinvokePoaDestroyed() { return preinvokePoaDestroyed(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL pmfCreateRetain(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080502, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.pmfCreateRetain", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL pmfCreateRetain(CompletionStatus paramCompletionStatus) { return pmfCreateRetain(paramCompletionStatus, null); }
  
  public INTERNAL pmfCreateRetain(Throwable paramThrowable) { return pmfCreateRetain(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL pmfCreateRetain() { return pmfCreateRetain(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL pmfCreateNonRetain(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080503, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.pmfCreateNonRetain", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL pmfCreateNonRetain(CompletionStatus paramCompletionStatus) { return pmfCreateNonRetain(paramCompletionStatus, null); }
  
  public INTERNAL pmfCreateNonRetain(Throwable paramThrowable) { return pmfCreateNonRetain(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL pmfCreateNonRetain() { return pmfCreateNonRetain(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL policyMediatorBadPolicyInFactory(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080504, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.policyMediatorBadPolicyInFactory", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL policyMediatorBadPolicyInFactory(CompletionStatus paramCompletionStatus) { return policyMediatorBadPolicyInFactory(paramCompletionStatus, null); }
  
  public INTERNAL policyMediatorBadPolicyInFactory(Throwable paramThrowable) { return policyMediatorBadPolicyInFactory(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL policyMediatorBadPolicyInFactory() { return policyMediatorBadPolicyInFactory(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL servantToIdOaa(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080505, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantToIdOaa", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL servantToIdOaa(CompletionStatus paramCompletionStatus) { return servantToIdOaa(paramCompletionStatus, null); }
  
  public INTERNAL servantToIdOaa(Throwable paramThrowable) { return servantToIdOaa(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL servantToIdOaa() { return servantToIdOaa(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL servantToIdSaa(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080506, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantToIdSaa", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL servantToIdSaa(CompletionStatus paramCompletionStatus) { return servantToIdSaa(paramCompletionStatus, null); }
  
  public INTERNAL servantToIdSaa(Throwable paramThrowable) { return servantToIdSaa(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL servantToIdSaa() { return servantToIdSaa(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL servantToIdWp(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080507, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantToIdWp", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL servantToIdWp(CompletionStatus paramCompletionStatus) { return servantToIdWp(paramCompletionStatus, null); }
  
  public INTERNAL servantToIdWp(Throwable paramThrowable) { return servantToIdWp(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL servantToIdWp() { return servantToIdWp(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL cantResolveRootPoa(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080508, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.cantResolveRootPoa", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL cantResolveRootPoa(CompletionStatus paramCompletionStatus) { return cantResolveRootPoa(paramCompletionStatus, null); }
  
  public INTERNAL cantResolveRootPoa(Throwable paramThrowable) { return cantResolveRootPoa(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL cantResolveRootPoa() { return cantResolveRootPoa(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL servantMustBeLocal(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080509, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantMustBeLocal", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL servantMustBeLocal(CompletionStatus paramCompletionStatus) { return servantMustBeLocal(paramCompletionStatus, null); }
  
  public INTERNAL servantMustBeLocal(Throwable paramThrowable) { return servantMustBeLocal(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL servantMustBeLocal() { return servantMustBeLocal(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL noProfilesInIor(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080510, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.noProfilesInIor", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL noProfilesInIor(CompletionStatus paramCompletionStatus) { return noProfilesInIor(paramCompletionStatus, null); }
  
  public INTERNAL noProfilesInIor(Throwable paramThrowable) { return noProfilesInIor(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL noProfilesInIor() { return noProfilesInIor(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL aomEntryDecZero(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080511, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.aomEntryDecZero", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL aomEntryDecZero(CompletionStatus paramCompletionStatus) { return aomEntryDecZero(paramCompletionStatus, null); }
  
  public INTERNAL aomEntryDecZero(Throwable paramThrowable) { return aomEntryDecZero(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL aomEntryDecZero() { return aomEntryDecZero(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL addPoaInactive(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080512, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.addPoaInactive", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL addPoaInactive(CompletionStatus paramCompletionStatus) { return addPoaInactive(paramCompletionStatus, null); }
  
  public INTERNAL addPoaInactive(Throwable paramThrowable) { return addPoaInactive(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL addPoaInactive() { return addPoaInactive(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL illegalPoaStateTrans(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398080513, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.illegalPoaStateTrans", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL illegalPoaStateTrans(CompletionStatus paramCompletionStatus) { return illegalPoaStateTrans(paramCompletionStatus, null); }
  
  public INTERNAL illegalPoaStateTrans(Throwable paramThrowable) { return illegalPoaStateTrans(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL illegalPoaStateTrans() { return illegalPoaStateTrans(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL unexpectedException(CompletionStatus paramCompletionStatus, Throwable paramThrowable, Object paramObject) {
    INTERNAL iNTERNAL = new INTERNAL(1398080514, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramObject;
      doLog(Level.WARNING, "POA.unexpectedException", arrayOfObject, POASystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unexpectedException(CompletionStatus paramCompletionStatus, Object paramObject) { return unexpectedException(paramCompletionStatus, null, paramObject); }
  
  public INTERNAL unexpectedException(Throwable paramThrowable, Object paramObject) { return unexpectedException(CompletionStatus.COMPLETED_NO, paramThrowable, paramObject); }
  
  public INTERNAL unexpectedException(Object paramObject) { return unexpectedException(CompletionStatus.COMPLETED_NO, null, paramObject); }
  
  public NO_IMPLEMENT singleThreadNotSupported(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.singleThreadNotSupported", arrayOfObject, POASystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT singleThreadNotSupported(CompletionStatus paramCompletionStatus) { return singleThreadNotSupported(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT singleThreadNotSupported(Throwable paramThrowable) { return singleThreadNotSupported(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT singleThreadNotSupported() { return singleThreadNotSupported(CompletionStatus.COMPLETED_NO, null); }
  
  public NO_IMPLEMENT methodNotImplemented(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    NO_IMPLEMENT nO_IMPLEMENT = new NO_IMPLEMENT(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      nO_IMPLEMENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.methodNotImplemented", arrayOfObject, POASystemException.class, nO_IMPLEMENT);
    } 
    return nO_IMPLEMENT;
  }
  
  public NO_IMPLEMENT methodNotImplemented(CompletionStatus paramCompletionStatus) { return methodNotImplemented(paramCompletionStatus, null); }
  
  public NO_IMPLEMENT methodNotImplemented(Throwable paramThrowable) { return methodNotImplemented(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public NO_IMPLEMENT methodNotImplemented() { return methodNotImplemented(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaLookupError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaLookupError", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaLookupError(CompletionStatus paramCompletionStatus) { return poaLookupError(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaLookupError(Throwable paramThrowable) { return poaLookupError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaLookupError() { return poaLookupError(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaInactive(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "POA.poaInactive", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaInactive(CompletionStatus paramCompletionStatus) { return poaInactive(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaInactive(Throwable paramThrowable) { return poaInactive(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaInactive() { return poaInactive(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaNoServantManager(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080491, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaNoServantManager", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaNoServantManager(CompletionStatus paramCompletionStatus) { return poaNoServantManager(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaNoServantManager(Throwable paramThrowable) { return poaNoServantManager(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaNoServantManager() { return poaNoServantManager(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaNoDefaultServant(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080492, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaNoDefaultServant", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaNoDefaultServant(CompletionStatus paramCompletionStatus) { return poaNoDefaultServant(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaNoDefaultServant(Throwable paramThrowable) { return poaNoDefaultServant(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaNoDefaultServant() { return poaNoDefaultServant(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaServantNotUnique(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080493, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaServantNotUnique", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaServantNotUnique(CompletionStatus paramCompletionStatus) { return poaServantNotUnique(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaServantNotUnique(Throwable paramThrowable) { return poaServantNotUnique(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaServantNotUnique() { return poaServantNotUnique(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaWrongPolicy(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080494, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaWrongPolicy", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaWrongPolicy(CompletionStatus paramCompletionStatus) { return poaWrongPolicy(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaWrongPolicy(Throwable paramThrowable) { return poaWrongPolicy(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaWrongPolicy() { return poaWrongPolicy(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER findpoaError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080495, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.findpoaError", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER findpoaError(CompletionStatus paramCompletionStatus) { return findpoaError(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER findpoaError(Throwable paramThrowable) { return findpoaError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER findpoaError() { return findpoaError(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaServantActivatorLookupFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080497, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaServantActivatorLookupFailed", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaServantActivatorLookupFailed(CompletionStatus paramCompletionStatus) { return poaServantActivatorLookupFailed(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaServantActivatorLookupFailed(Throwable paramThrowable) { return poaServantActivatorLookupFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaServantActivatorLookupFailed() { return poaServantActivatorLookupFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaBadServantManager(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080498, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaBadServantManager", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaBadServantManager(CompletionStatus paramCompletionStatus) { return poaBadServantManager(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaBadServantManager(Throwable paramThrowable) { return poaBadServantManager(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaBadServantManager() { return poaBadServantManager(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaServantLocatorLookupFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080499, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaServantLocatorLookupFailed", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaServantLocatorLookupFailed(CompletionStatus paramCompletionStatus) { return poaServantLocatorLookupFailed(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaServantLocatorLookupFailed(Throwable paramThrowable) { return poaServantLocatorLookupFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaServantLocatorLookupFailed() { return poaServantLocatorLookupFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaUnknownPolicy(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080500, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaUnknownPolicy", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaUnknownPolicy(CompletionStatus paramCompletionStatus) { return poaUnknownPolicy(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaUnknownPolicy(Throwable paramThrowable) { return poaUnknownPolicy(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaUnknownPolicy() { return poaUnknownPolicy(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER poaNotFound(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080501, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.poaNotFound", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER poaNotFound(CompletionStatus paramCompletionStatus) { return poaNotFound(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER poaNotFound(Throwable paramThrowable) { return poaNotFound(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER poaNotFound() { return poaNotFound(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER servantLookup(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080502, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantLookup", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER servantLookup(CompletionStatus paramCompletionStatus) { return servantLookup(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER servantLookup(Throwable paramThrowable) { return servantLookup(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER servantLookup() { return servantLookup(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER localServantLookup(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080503, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.localServantLookup", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER localServantLookup(CompletionStatus paramCompletionStatus) { return localServantLookup(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER localServantLookup(Throwable paramThrowable) { return localServantLookup(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER localServantLookup() { return localServantLookup(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER servantManagerBadType(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080504, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.servantManagerBadType", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER servantManagerBadType(CompletionStatus paramCompletionStatus) { return servantManagerBadType(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER servantManagerBadType(Throwable paramThrowable) { return servantManagerBadType(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER servantManagerBadType() { return servantManagerBadType(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER defaultPoaNotPoaimpl(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080505, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.defaultPoaNotPoaimpl", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER defaultPoaNotPoaimpl(CompletionStatus paramCompletionStatus) { return defaultPoaNotPoaimpl(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER defaultPoaNotPoaimpl(Throwable paramThrowable) { return defaultPoaNotPoaimpl(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER defaultPoaNotPoaimpl() { return defaultPoaNotPoaimpl(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER wrongPoliciesForThisObject(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080506, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.wrongPoliciesForThisObject", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER wrongPoliciesForThisObject(CompletionStatus paramCompletionStatus) { return wrongPoliciesForThisObject(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER wrongPoliciesForThisObject(Throwable paramThrowable) { return wrongPoliciesForThisObject(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER wrongPoliciesForThisObject() { return wrongPoliciesForThisObject(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER thisObjectServantNotActive(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080507, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.thisObjectServantNotActive", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER thisObjectServantNotActive(CompletionStatus paramCompletionStatus) { return thisObjectServantNotActive(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER thisObjectServantNotActive(Throwable paramThrowable) { return thisObjectServantNotActive(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER thisObjectServantNotActive() { return thisObjectServantNotActive(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER thisObjectWrongPolicy(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080508, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.thisObjectWrongPolicy", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER thisObjectWrongPolicy(CompletionStatus paramCompletionStatus) { return thisObjectWrongPolicy(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER thisObjectWrongPolicy(Throwable paramThrowable) { return thisObjectWrongPolicy(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER thisObjectWrongPolicy() { return thisObjectWrongPolicy(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER noContext(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080509, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "POA.noContext", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER noContext(CompletionStatus paramCompletionStatus) { return noContext(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER noContext(Throwable paramThrowable) { return noContext(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER noContext() { return noContext(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJ_ADAPTER incarnateReturnedNull(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJ_ADAPTER oBJ_ADAPTER = new OBJ_ADAPTER(1398080510, paramCompletionStatus);
    if (paramThrowable != null)
      oBJ_ADAPTER.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.incarnateReturnedNull", arrayOfObject, POASystemException.class, oBJ_ADAPTER);
    } 
    return oBJ_ADAPTER;
  }
  
  public OBJ_ADAPTER incarnateReturnedNull(CompletionStatus paramCompletionStatus) { return incarnateReturnedNull(paramCompletionStatus, null); }
  
  public OBJ_ADAPTER incarnateReturnedNull(Throwable paramThrowable) { return incarnateReturnedNull(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJ_ADAPTER incarnateReturnedNull() { return incarnateReturnedNull(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE jtsInitError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.jtsInitError", arrayOfObject, POASystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE jtsInitError(CompletionStatus paramCompletionStatus) { return jtsInitError(paramCompletionStatus, null); }
  
  public INITIALIZE jtsInitError(Throwable paramThrowable) { return jtsInitError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE jtsInitError() { return jtsInitError(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE persistentServeridNotSet(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.persistentServeridNotSet", arrayOfObject, POASystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE persistentServeridNotSet(CompletionStatus paramCompletionStatus) { return persistentServeridNotSet(paramCompletionStatus, null); }
  
  public INITIALIZE persistentServeridNotSet(Throwable paramThrowable) { return persistentServeridNotSet(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE persistentServeridNotSet() { return persistentServeridNotSet(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE persistentServerportNotSet(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398080491, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.persistentServerportNotSet", arrayOfObject, POASystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE persistentServerportNotSet(CompletionStatus paramCompletionStatus) { return persistentServerportNotSet(paramCompletionStatus, null); }
  
  public INITIALIZE persistentServerportNotSet(Throwable paramThrowable) { return persistentServerportNotSet(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE persistentServerportNotSet() { return persistentServerportNotSet(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE orbdError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398080492, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.orbdError", arrayOfObject, POASystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE orbdError(CompletionStatus paramCompletionStatus) { return orbdError(paramCompletionStatus, null); }
  
  public INITIALIZE orbdError(Throwable paramThrowable) { return orbdError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE orbdError() { return orbdError(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE bootstrapError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398080493, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.bootstrapError", arrayOfObject, POASystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE bootstrapError(CompletionStatus paramCompletionStatus) { return bootstrapError(paramCompletionStatus, null); }
  
  public INITIALIZE bootstrapError(Throwable paramThrowable) { return bootstrapError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE bootstrapError() { return bootstrapError(CompletionStatus.COMPLETED_NO, null); }
  
  public TRANSIENT poaDiscarding(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    TRANSIENT tRANSIENT = new TRANSIENT(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      tRANSIENT.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "POA.poaDiscarding", arrayOfObject, POASystemException.class, tRANSIENT);
    } 
    return tRANSIENT;
  }
  
  public TRANSIENT poaDiscarding(CompletionStatus paramCompletionStatus) { return poaDiscarding(paramCompletionStatus, null); }
  
  public TRANSIENT poaDiscarding(Throwable paramThrowable) { return poaDiscarding(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public TRANSIENT poaDiscarding() { return poaDiscarding(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN otshookexception(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.otshookexception", arrayOfObject, POASystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN otshookexception(CompletionStatus paramCompletionStatus) { return otshookexception(paramCompletionStatus, null); }
  
  public UNKNOWN otshookexception(Throwable paramThrowable) { return otshookexception(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN otshookexception() { return otshookexception(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownServerException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.unknownServerException", arrayOfObject, POASystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownServerException(CompletionStatus paramCompletionStatus) { return unknownServerException(paramCompletionStatus, null); }
  
  public UNKNOWN unknownServerException(Throwable paramThrowable) { return unknownServerException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownServerException() { return unknownServerException(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownServerappException(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398080491, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.unknownServerappException", arrayOfObject, POASystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownServerappException(CompletionStatus paramCompletionStatus) { return unknownServerappException(paramCompletionStatus, null); }
  
  public UNKNOWN unknownServerappException(Throwable paramThrowable) { return unknownServerappException(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownServerappException() { return unknownServerappException(CompletionStatus.COMPLETED_NO, null); }
  
  public UNKNOWN unknownLocalinvocationError(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    UNKNOWN uNKNOWN = new UNKNOWN(1398080492, paramCompletionStatus);
    if (paramThrowable != null)
      uNKNOWN.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.unknownLocalinvocationError", arrayOfObject, POASystemException.class, uNKNOWN);
    } 
    return uNKNOWN;
  }
  
  public UNKNOWN unknownLocalinvocationError(CompletionStatus paramCompletionStatus) { return unknownLocalinvocationError(paramCompletionStatus, null); }
  
  public UNKNOWN unknownLocalinvocationError(Throwable paramThrowable) { return unknownLocalinvocationError(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public UNKNOWN unknownLocalinvocationError() { return unknownLocalinvocationError(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST adapterActivatorNonexistent(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398080489, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.adapterActivatorNonexistent", arrayOfObject, POASystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST adapterActivatorNonexistent(CompletionStatus paramCompletionStatus) { return adapterActivatorNonexistent(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST adapterActivatorNonexistent(Throwable paramThrowable) { return adapterActivatorNonexistent(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST adapterActivatorNonexistent() { return adapterActivatorNonexistent(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST adapterActivatorFailed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398080490, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.adapterActivatorFailed", arrayOfObject, POASystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST adapterActivatorFailed(CompletionStatus paramCompletionStatus) { return adapterActivatorFailed(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST adapterActivatorFailed(Throwable paramThrowable) { return adapterActivatorFailed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST adapterActivatorFailed() { return adapterActivatorFailed(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST badSkeleton(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398080491, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.badSkeleton", arrayOfObject, POASystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST badSkeleton(CompletionStatus paramCompletionStatus) { return badSkeleton(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST badSkeleton(Throwable paramThrowable) { return badSkeleton(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST badSkeleton() { return badSkeleton(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST nullServant(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398080492, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.FINE)) {
      Object[] arrayOfObject = null;
      doLog(Level.FINE, "POA.nullServant", arrayOfObject, POASystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST nullServant(CompletionStatus paramCompletionStatus) { return nullServant(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST nullServant(Throwable paramThrowable) { return nullServant(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST nullServant() { return nullServant(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST adapterDestroyed(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398080493, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "POA.adapterDestroyed", arrayOfObject, POASystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST adapterDestroyed(CompletionStatus paramCompletionStatus) { return adapterDestroyed(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST adapterDestroyed(Throwable paramThrowable) { return adapterDestroyed(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST adapterDestroyed() { return adapterDestroyed(CompletionStatus.COMPLETED_NO, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\logging\POASystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */