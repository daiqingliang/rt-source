package java.util.logging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class LogRecord implements Serializable {
  private static final AtomicLong globalSequenceNumber = new AtomicLong(0L);
  
  private static final int MIN_SEQUENTIAL_THREAD_ID = 1073741823;
  
  private static final AtomicInteger nextThreadId = new AtomicInteger(1073741823);
  
  private static final ThreadLocal<Integer> threadIds = new ThreadLocal();
  
  private Level level;
  
  private long sequenceNumber;
  
  private String sourceClassName;
  
  private String sourceMethodName;
  
  private String message;
  
  private int threadID;
  
  private long millis;
  
  private Throwable thrown;
  
  private String loggerName;
  
  private String resourceBundleName;
  
  private boolean needToInferCaller;
  
  private Object[] parameters;
  
  private ResourceBundle resourceBundle;
  
  private static final long serialVersionUID = 5372048053134512534L;
  
  private int defaultThreadID() {
    long l = Thread.currentThread().getId();
    if (l < 1073741823L)
      return (int)l; 
    Integer integer = (Integer)threadIds.get();
    if (integer == null) {
      integer = Integer.valueOf(nextThreadId.getAndIncrement());
      threadIds.set(integer);
    } 
    return integer.intValue();
  }
  
  public LogRecord(Level paramLevel, String paramString) {
    paramLevel.getClass();
    this.level = paramLevel;
    this.message = paramString;
    this.sequenceNumber = globalSequenceNumber.getAndIncrement();
    this.threadID = defaultThreadID();
    this.millis = System.currentTimeMillis();
    this.needToInferCaller = true;
  }
  
  public String getLoggerName() { return this.loggerName; }
  
  public void setLoggerName(String paramString) { this.loggerName = paramString; }
  
  public ResourceBundle getResourceBundle() { return this.resourceBundle; }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle) { this.resourceBundle = paramResourceBundle; }
  
  public String getResourceBundleName() { return this.resourceBundleName; }
  
  public void setResourceBundleName(String paramString) { this.resourceBundleName = paramString; }
  
  public Level getLevel() { return this.level; }
  
  public void setLevel(Level paramLevel) {
    if (paramLevel == null)
      throw new NullPointerException(); 
    this.level = paramLevel;
  }
  
  public long getSequenceNumber() { return this.sequenceNumber; }
  
  public void setSequenceNumber(long paramLong) { this.sequenceNumber = paramLong; }
  
  public String getSourceClassName() {
    if (this.needToInferCaller)
      inferCaller(); 
    return this.sourceClassName;
  }
  
  public void setSourceClassName(String paramString) {
    this.sourceClassName = paramString;
    this.needToInferCaller = false;
  }
  
  public String getSourceMethodName() {
    if (this.needToInferCaller)
      inferCaller(); 
    return this.sourceMethodName;
  }
  
  public void setSourceMethodName(String paramString) {
    this.sourceMethodName = paramString;
    this.needToInferCaller = false;
  }
  
  public String getMessage() { return this.message; }
  
  public void setMessage(String paramString) { this.message = paramString; }
  
  public Object[] getParameters() { return this.parameters; }
  
  public void setParameters(Object[] paramArrayOfObject) { this.parameters = paramArrayOfObject; }
  
  public int getThreadID() { return this.threadID; }
  
  public void setThreadID(int paramInt) { this.threadID = paramInt; }
  
  public long getMillis() { return this.millis; }
  
  public void setMillis(long paramLong) { this.millis = paramLong; }
  
  public Throwable getThrown() { return this.thrown; }
  
  public void setThrown(Throwable paramThrowable) { this.thrown = paramThrowable; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeByte(1);
    paramObjectOutputStream.writeByte(0);
    if (this.parameters == null) {
      paramObjectOutputStream.writeInt(-1);
      return;
    } 
    paramObjectOutputStream.writeInt(this.parameters.length);
    for (byte b = 0; b < this.parameters.length; b++) {
      if (this.parameters[b] == null) {
        paramObjectOutputStream.writeObject(null);
      } else {
        paramObjectOutputStream.writeObject(this.parameters[b].toString());
      } 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    byte b1 = paramObjectInputStream.readByte();
    byte b2 = paramObjectInputStream.readByte();
    if (b1 != 1)
      throw new IOException("LogRecord: bad version: " + b1 + "." + b2); 
    int i = paramObjectInputStream.readInt();
    if (i < -1)
      throw new NegativeArraySizeException(); 
    if (i == -1) {
      this.parameters = null;
    } else if (i < 255) {
      this.parameters = new Object[i];
      for (byte b = 0; b < this.parameters.length; b++)
        this.parameters[b] = paramObjectInputStream.readObject(); 
    } else {
      ArrayList arrayList = new ArrayList(Math.min(i, 1024));
      for (byte b = 0; b < i; b++)
        arrayList.add(paramObjectInputStream.readObject()); 
      this.parameters = arrayList.toArray(new Object[arrayList.size()]);
    } 
    if (this.resourceBundleName != null)
      try {
        ResourceBundle resourceBundle1 = ResourceBundle.getBundle(this.resourceBundleName, Locale.getDefault(), ClassLoader.getSystemClassLoader());
        this.resourceBundle = resourceBundle1;
      } catch (MissingResourceException missingResourceException) {
        this.resourceBundle = null;
      }  
    this.needToInferCaller = false;
  }
  
  private void inferCaller() {
    this.needToInferCaller = false;
    JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
    Throwable throwable = new Throwable();
    int i = javaLangAccess.getStackTraceDepth(throwable);
    boolean bool = true;
    for (byte b = 0; b < i; b++) {
      StackTraceElement stackTraceElement = javaLangAccess.getStackTraceElement(throwable, b);
      String str = stackTraceElement.getClassName();
      boolean bool1 = isLoggerImplFrame(str);
      if (bool) {
        if (bool1)
          bool = false; 
      } else if (!bool1 && !str.startsWith("java.lang.reflect.") && !str.startsWith("sun.reflect.")) {
        setSourceClassName(str);
        setSourceMethodName(stackTraceElement.getMethodName());
        return;
      } 
    } 
  }
  
  private boolean isLoggerImplFrame(String paramString) { return (paramString.equals("java.util.logging.Logger") || paramString.startsWith("java.util.logging.LoggingProxyImpl") || paramString.startsWith("sun.util.logging.")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\LogRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */