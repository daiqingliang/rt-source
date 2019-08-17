package com.sun.corba.se.impl.logging;

import com.sun.corba.se.spi.logging.LogWrapperBase;
import com.sun.corba.se.spi.logging.LogWrapperFactory;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;

public class ActivationSystemException extends LogWrapperBase {
  private static LogWrapperFactory factory = new LogWrapperFactory() {
      public LogWrapperBase create(Logger param1Logger) { return new ActivationSystemException(param1Logger); }
    };
  
  public static final int CANNOT_READ_REPOSITORY_DB = 1398079889;
  
  public static final int CANNOT_ADD_INITIAL_NAMING = 1398079890;
  
  public static final int CANNOT_WRITE_REPOSITORY_DB = 1398079889;
  
  public static final int SERVER_NOT_EXPECTED_TO_REGISTER = 1398079891;
  
  public static final int UNABLE_TO_START_PROCESS = 1398079892;
  
  public static final int SERVER_NOT_RUNNING = 1398079894;
  
  public static final int ERROR_IN_BAD_SERVER_ID_HANDLER = 1398079889;
  
  public ActivationSystemException(Logger paramLogger) { super(paramLogger); }
  
  public static ActivationSystemException get(ORB paramORB, String paramString) { return (ActivationSystemException)paramORB.getLogWrapper(paramString, "ACTIVATION", factory); }
  
  public static ActivationSystemException get(String paramString) { return (ActivationSystemException)ORB.staticGetLogWrapper(paramString, "ACTIVATION", factory); }
  
  public INITIALIZE cannotReadRepositoryDb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398079889, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ACTIVATION.cannotReadRepositoryDb", arrayOfObject, ActivationSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE cannotReadRepositoryDb(CompletionStatus paramCompletionStatus) { return cannotReadRepositoryDb(paramCompletionStatus, null); }
  
  public INITIALIZE cannotReadRepositoryDb(Throwable paramThrowable) { return cannotReadRepositoryDb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE cannotReadRepositoryDb() { return cannotReadRepositoryDb(CompletionStatus.COMPLETED_NO, null); }
  
  public INITIALIZE cannotAddInitialNaming(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INITIALIZE iNITIALIZE = new INITIALIZE(1398079890, paramCompletionStatus);
    if (paramThrowable != null)
      iNITIALIZE.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ACTIVATION.cannotAddInitialNaming", arrayOfObject, ActivationSystemException.class, iNITIALIZE);
    } 
    return iNITIALIZE;
  }
  
  public INITIALIZE cannotAddInitialNaming(CompletionStatus paramCompletionStatus) { return cannotAddInitialNaming(paramCompletionStatus, null); }
  
  public INITIALIZE cannotAddInitialNaming(Throwable paramThrowable) { return cannotAddInitialNaming(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INITIALIZE cannotAddInitialNaming() { return cannotAddInitialNaming(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL cannotWriteRepositoryDb(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079889, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ACTIVATION.cannotWriteRepositoryDb", arrayOfObject, ActivationSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL cannotWriteRepositoryDb(CompletionStatus paramCompletionStatus) { return cannotWriteRepositoryDb(paramCompletionStatus, null); }
  
  public INTERNAL cannotWriteRepositoryDb(Throwable paramThrowable) { return cannotWriteRepositoryDb(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL cannotWriteRepositoryDb() { return cannotWriteRepositoryDb(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL serverNotExpectedToRegister(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079891, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ACTIVATION.serverNotExpectedToRegister", arrayOfObject, ActivationSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL serverNotExpectedToRegister(CompletionStatus paramCompletionStatus) { return serverNotExpectedToRegister(paramCompletionStatus, null); }
  
  public INTERNAL serverNotExpectedToRegister(Throwable paramThrowable) { return serverNotExpectedToRegister(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL serverNotExpectedToRegister() { return serverNotExpectedToRegister(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL unableToStartProcess(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079892, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ACTIVATION.unableToStartProcess", arrayOfObject, ActivationSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL unableToStartProcess(CompletionStatus paramCompletionStatus) { return unableToStartProcess(paramCompletionStatus, null); }
  
  public INTERNAL unableToStartProcess(Throwable paramThrowable) { return unableToStartProcess(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL unableToStartProcess() { return unableToStartProcess(CompletionStatus.COMPLETED_NO, null); }
  
  public INTERNAL serverNotRunning(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    INTERNAL iNTERNAL = new INTERNAL(1398079894, paramCompletionStatus);
    if (paramThrowable != null)
      iNTERNAL.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ACTIVATION.serverNotRunning", arrayOfObject, ActivationSystemException.class, iNTERNAL);
    } 
    return iNTERNAL;
  }
  
  public INTERNAL serverNotRunning(CompletionStatus paramCompletionStatus) { return serverNotRunning(paramCompletionStatus, null); }
  
  public INTERNAL serverNotRunning(Throwable paramThrowable) { return serverNotRunning(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public INTERNAL serverNotRunning() { return serverNotRunning(CompletionStatus.COMPLETED_NO, null); }
  
  public OBJECT_NOT_EXIST errorInBadServerIdHandler(CompletionStatus paramCompletionStatus, Throwable paramThrowable) {
    OBJECT_NOT_EXIST oBJECT_NOT_EXIST = new OBJECT_NOT_EXIST(1398079889, paramCompletionStatus);
    if (paramThrowable != null)
      oBJECT_NOT_EXIST.initCause(paramThrowable); 
    if (this.logger.isLoggable(Level.WARNING)) {
      Object[] arrayOfObject = null;
      doLog(Level.WARNING, "ACTIVATION.errorInBadServerIdHandler", arrayOfObject, ActivationSystemException.class, oBJECT_NOT_EXIST);
    } 
    return oBJECT_NOT_EXIST;
  }
  
  public OBJECT_NOT_EXIST errorInBadServerIdHandler(CompletionStatus paramCompletionStatus) { return errorInBadServerIdHandler(paramCompletionStatus, null); }
  
  public OBJECT_NOT_EXIST errorInBadServerIdHandler(Throwable paramThrowable) { return errorInBadServerIdHandler(CompletionStatus.COMPLETED_NO, paramThrowable); }
  
  public OBJECT_NOT_EXIST errorInBadServerIdHandler() { return errorInBadServerIdHandler(CompletionStatus.COMPLETED_NO, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\logging\ActivationSystemException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */