package sun.rmi.runtime;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.rmi.server.LogStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import sun.security.action.GetPropertyAction;

public abstract class Log {
  public static final Level BRIEF = Level.FINE;
  
  public static final Level VERBOSE = Level.FINER;
  
  private static final LogFactory logFactory;
  
  public abstract boolean isLoggable(Level paramLevel);
  
  public abstract void log(Level paramLevel, String paramString);
  
  public abstract void log(Level paramLevel, String paramString, Throwable paramThrowable);
  
  public abstract void setOutputStream(OutputStream paramOutputStream);
  
  public abstract PrintStream getPrintStream();
  
  public static Log getLog(String paramString1, String paramString2, int paramInt) {
    Level level;
    if (paramInt < 0) {
      level = null;
    } else if (paramInt == 0) {
      level = Level.OFF;
    } else if (paramInt > 0 && paramInt <= 10) {
      level = BRIEF;
    } else if (paramInt > 10 && paramInt <= 20) {
      level = VERBOSE;
    } else {
      level = Level.FINEST;
    } 
    return logFactory.createLog(paramString1, paramString2, level);
  }
  
  public static Log getLog(String paramString1, String paramString2, boolean paramBoolean) {
    Level level = paramBoolean ? VERBOSE : null;
    return logFactory.createLog(paramString1, paramString2, level);
  }
  
  private static String[] getSource() {
    StackTraceElement[] arrayOfStackTraceElement = (new Exception()).getStackTrace();
    return new String[] { arrayOfStackTraceElement[3].getClassName(), arrayOfStackTraceElement[3].getMethodName() };
  }
  
  static  {
    boolean bool = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.log.useOld"))).booleanValue();
    logFactory = bool ? new LogStreamLogFactory() : new LoggerLogFactory();
  }
  
  private static class InternalStreamHandler extends StreamHandler {
    InternalStreamHandler(OutputStream param1OutputStream) { super(param1OutputStream, new SimpleFormatter()); }
    
    public void publish(LogRecord param1LogRecord) {
      super.publish(param1LogRecord);
      flush();
    }
    
    public void close() { flush(); }
  }
  
  private static interface LogFactory {
    Log createLog(String param1String1, String param1String2, Level param1Level);
  }
  
  private static class LogStreamLog extends Log {
    private final LogStream stream;
    
    private int levelValue = Level.OFF.intValue();
    
    private LogStreamLog(LogStream param1LogStream, Level param1Level) {
      if (param1LogStream != null && param1Level != null)
        this.levelValue = param1Level.intValue(); 
      this.stream = param1LogStream;
    }
    
    public boolean isLoggable(Level param1Level) { return (param1Level.intValue() >= this.levelValue); }
    
    public void log(Level param1Level, String param1String) {
      if (isLoggable(param1Level)) {
        String[] arrayOfString = Log.getSource();
        this.stream.println(unqualifiedName(arrayOfString[0]) + "." + arrayOfString[1] + ": " + param1String);
      } 
    }
    
    public void log(Level param1Level, String param1String, Throwable param1Throwable) {
      if (isLoggable(param1Level))
        synchronized (this.stream) {
          String[] arrayOfString = Log.getSource();
          this.stream.println(unqualifiedName(arrayOfString[0]) + "." + arrayOfString[1] + ": " + param1String);
          param1Throwable.printStackTrace(this.stream);
        }  
    }
    
    public PrintStream getPrintStream() { return this.stream; }
    
    public void setOutputStream(OutputStream param1OutputStream) {
      if (param1OutputStream != null) {
        if (VERBOSE.intValue() < this.levelValue)
          this.levelValue = VERBOSE.intValue(); 
        this.stream.setOutputStream(param1OutputStream);
      } else {
        this.levelValue = Level.OFF.intValue();
      } 
    }
    
    private static String unqualifiedName(String param1String) {
      int i = param1String.lastIndexOf(".");
      if (i >= 0)
        param1String = param1String.substring(i + 1); 
      return param1String.replace('$', '.');
    }
  }
  
  private static class LogStreamLogFactory implements LogFactory {
    public Log createLog(String param1String1, String param1String2, Level param1Level) {
      LogStream logStream = null;
      if (param1String2 != null)
        logStream = LogStream.log(param1String2); 
      return new Log.LogStreamLog(logStream, param1Level, null);
    }
  }
  
  private static class LoggerLog extends Log {
    private static final Handler alternateConsole = (Handler)AccessController.doPrivileged(new PrivilegedAction<Handler>() {
          public Handler run() {
            Log.InternalStreamHandler internalStreamHandler = new Log.InternalStreamHandler(System.err);
            internalStreamHandler.setLevel(Level.ALL);
            return internalStreamHandler;
          }
        });
    
    private Log.InternalStreamHandler copyHandler = null;
    
    private final Logger logger;
    
    private Log.LoggerPrintStream loggerSandwich;
    
    private LoggerLog(final Logger logger, final Level level) {
      this.logger = param1Logger;
      if (param1Level != null)
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                if (!logger.isLoggable(level))
                  logger.setLevel(level); 
                logger.addHandler(alternateConsole);
                return null;
              }
            }); 
    }
    
    public boolean isLoggable(Level param1Level) { return this.logger.isLoggable(param1Level); }
    
    public void log(Level param1Level, String param1String) {
      if (isLoggable(param1Level)) {
        String[] arrayOfString = Log.getSource();
        this.logger.logp(param1Level, arrayOfString[0], arrayOfString[1], Thread.currentThread().getName() + ": " + param1String);
      } 
    }
    
    public void log(Level param1Level, String param1String, Throwable param1Throwable) {
      if (isLoggable(param1Level)) {
        String[] arrayOfString = Log.getSource();
        this.logger.logp(param1Level, arrayOfString[0], arrayOfString[1], Thread.currentThread().getName() + ": " + param1String, param1Throwable);
      } 
    }
    
    public void setOutputStream(OutputStream param1OutputStream) {
      if (param1OutputStream != null) {
        if (!this.logger.isLoggable(VERBOSE))
          this.logger.setLevel(VERBOSE); 
        this.copyHandler = new Log.InternalStreamHandler(param1OutputStream);
        this.copyHandler.setLevel(Log.VERBOSE);
        this.logger.addHandler(this.copyHandler);
      } else {
        if (this.copyHandler != null)
          this.logger.removeHandler(this.copyHandler); 
        this.copyHandler = null;
      } 
    }
    
    public PrintStream getPrintStream() {
      if (this.loggerSandwich == null)
        this.loggerSandwich = new Log.LoggerPrintStream(this.logger, null); 
      return this.loggerSandwich;
    }
  }
  
  private static class LoggerLogFactory implements LogFactory {
    public Log createLog(String param1String1, String param1String2, Level param1Level) {
      Logger logger = Logger.getLogger(param1String1);
      return new Log.LoggerLog(logger, param1Level, null);
    }
  }
  
  private static class LoggerPrintStream extends PrintStream {
    private final Logger logger;
    
    private int last = -1;
    
    private final ByteArrayOutputStream bufOut = (ByteArrayOutputStream)this.out;
    
    private LoggerPrintStream(Logger param1Logger) {
      super(new ByteArrayOutputStream());
      this.logger = param1Logger;
    }
    
    public void write(int param1Int) {
      if (this.last == 13 && param1Int == 10) {
        this.last = -1;
        return;
      } 
      if (param1Int == 10 || param1Int == 13) {
        try {
          String str = Thread.currentThread().getName() + ": " + this.bufOut.toString();
          this.logger.logp(Level.INFO, "LogStream", "print", str);
        } finally {
          this.bufOut.reset();
        } 
      } else {
        super.write(param1Int);
      } 
      this.last = param1Int;
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      if (param1Int2 < 0)
        throw new ArrayIndexOutOfBoundsException(param1Int2); 
      for (int i = 0; i < param1Int2; i++)
        write(param1ArrayOfByte[param1Int1 + i]); 
    }
    
    public String toString() { return "RMI"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\runtime\Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */