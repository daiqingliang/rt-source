package com.sun.jmx.remote.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassLogger {
  private static final boolean ok;
  
  private final String className;
  
  private final Logger logger;
  
  public ClassLogger(String paramString1, String paramString2) {
    if (ok) {
      this.logger = Logger.getLogger(paramString1);
    } else {
      this.logger = null;
    } 
    this.className = paramString2;
  }
  
  public final boolean traceOn() { return finerOn(); }
  
  public final boolean debugOn() { return finestOn(); }
  
  public final boolean warningOn() { return (ok && this.logger.isLoggable(Level.WARNING)); }
  
  public final boolean infoOn() { return (ok && this.logger.isLoggable(Level.INFO)); }
  
  public final boolean configOn() { return (ok && this.logger.isLoggable(Level.CONFIG)); }
  
  public final boolean fineOn() { return (ok && this.logger.isLoggable(Level.FINE)); }
  
  public final boolean finerOn() { return (ok && this.logger.isLoggable(Level.FINER)); }
  
  public final boolean finestOn() { return (ok && this.logger.isLoggable(Level.FINEST)); }
  
  public final void debug(String paramString1, String paramString2) { finest(paramString1, paramString2); }
  
  public final void debug(String paramString, Throwable paramThrowable) { finest(paramString, paramThrowable); }
  
  public final void debug(String paramString1, String paramString2, Throwable paramThrowable) { finest(paramString1, paramString2, paramThrowable); }
  
  public final void trace(String paramString1, String paramString2) { finer(paramString1, paramString2); }
  
  public final void trace(String paramString, Throwable paramThrowable) { finer(paramString, paramThrowable); }
  
  public final void trace(String paramString1, String paramString2, Throwable paramThrowable) { finer(paramString1, paramString2, paramThrowable); }
  
  public final void error(String paramString1, String paramString2) { severe(paramString1, paramString2); }
  
  public final void error(String paramString, Throwable paramThrowable) { severe(paramString, paramThrowable); }
  
  public final void error(String paramString1, String paramString2, Throwable paramThrowable) { severe(paramString1, paramString2, paramThrowable); }
  
  public final void finest(String paramString1, String paramString2) {
    if (ok)
      this.logger.logp(Level.FINEST, this.className, paramString1, paramString2); 
  }
  
  public final void finest(String paramString, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.FINEST, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public final void finest(String paramString1, String paramString2, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.FINEST, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public final void finer(String paramString1, String paramString2) {
    if (ok)
      this.logger.logp(Level.FINER, this.className, paramString1, paramString2); 
  }
  
  public final void finer(String paramString, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.FINER, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public final void finer(String paramString1, String paramString2, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.FINER, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public final void fine(String paramString1, String paramString2) {
    if (ok)
      this.logger.logp(Level.FINE, this.className, paramString1, paramString2); 
  }
  
  public final void fine(String paramString, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.FINE, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public final void fine(String paramString1, String paramString2, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.FINE, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public final void config(String paramString1, String paramString2) {
    if (ok)
      this.logger.logp(Level.CONFIG, this.className, paramString1, paramString2); 
  }
  
  public final void config(String paramString, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.CONFIG, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public final void config(String paramString1, String paramString2, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.CONFIG, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public final void info(String paramString1, String paramString2) {
    if (ok)
      this.logger.logp(Level.INFO, this.className, paramString1, paramString2); 
  }
  
  public final void info(String paramString, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.INFO, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public final void info(String paramString1, String paramString2, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.INFO, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public final void warning(String paramString1, String paramString2) {
    if (ok)
      this.logger.logp(Level.WARNING, this.className, paramString1, paramString2); 
  }
  
  public final void warning(String paramString, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.WARNING, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public final void warning(String paramString1, String paramString2, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.WARNING, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  public final void severe(String paramString1, String paramString2) {
    if (ok)
      this.logger.logp(Level.SEVERE, this.className, paramString1, paramString2); 
  }
  
  public final void severe(String paramString, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.SEVERE, this.className, paramString, paramThrowable.toString(), paramThrowable); 
  }
  
  public final void severe(String paramString1, String paramString2, Throwable paramThrowable) {
    if (ok)
      this.logger.logp(Level.SEVERE, this.className, paramString1, paramString2, paramThrowable); 
  }
  
  static  {
    boolean bool = false;
    try {
      Class clazz = Logger.class;
      bool = true;
    } catch (Error error) {}
    ok = bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remot\\util\ClassLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */