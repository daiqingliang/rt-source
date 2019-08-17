package java.util.logging;

public class MemoryHandler extends Handler {
  private static final int DEFAULT_SIZE = 1000;
  
  private int size;
  
  private Handler target;
  
  private LogRecord[] buffer;
  
  int start;
  
  int count;
  
  private void configure() {
    LogManager logManager = LogManager.getLogManager();
    String str = getClass().getName();
    this.pushLevel = logManager.getLevelProperty(str + ".push", Level.SEVERE);
    this.size = logManager.getIntProperty(str + ".size", 1000);
    if (this.size <= 0)
      this.size = 1000; 
    setLevel(logManager.getLevelProperty(str + ".level", Level.ALL));
    setFilter(logManager.getFilterProperty(str + ".filter", null));
    setFormatter(logManager.getFormatterProperty(str + ".formatter", new SimpleFormatter()));
  }
  
  public MemoryHandler() {
    this.sealed = false;
    configure();
    this.sealed = true;
    LogManager logManager = LogManager.getLogManager();
    String str1 = getClass().getName();
    String str2 = logManager.getProperty(str1 + ".target");
    if (str2 == null)
      throw new RuntimeException("The handler " + str1 + " does not specify a target"); 
    try {
      Class clazz = ClassLoader.getSystemClassLoader().loadClass(str2);
      this.target = (Handler)clazz.newInstance();
    } catch (ClassNotFoundException|InstantiationException|IllegalAccessException classNotFoundException) {
      throw new RuntimeException("MemoryHandler can't load handler target \"" + str2 + "\"", classNotFoundException);
    } 
    init();
  }
  
  private void init() {
    this.buffer = new LogRecord[this.size];
    this.start = 0;
    this.count = 0;
  }
  
  public MemoryHandler(Handler paramHandler, int paramInt, Level paramLevel) {
    if (paramHandler == null || paramLevel == null)
      throw new NullPointerException(); 
    if (paramInt <= 0)
      throw new IllegalArgumentException(); 
    this.sealed = false;
    configure();
    this.sealed = true;
    this.target = paramHandler;
    this.pushLevel = paramLevel;
    this.size = paramInt;
    init();
  }
  
  public void publish(LogRecord paramLogRecord) {
    if (!isLoggable(paramLogRecord))
      return; 
    int i = (this.start + this.count) % this.buffer.length;
    this.buffer[i] = paramLogRecord;
    if (this.count < this.buffer.length) {
      this.count++;
    } else {
      this.start++;
      this.start %= this.buffer.length;
    } 
    if (paramLogRecord.getLevel().intValue() >= this.pushLevel.intValue())
      push(); 
  }
  
  public void push() {
    for (int i = 0; i < this.count; i++) {
      int j = (this.start + i) % this.buffer.length;
      LogRecord logRecord = this.buffer[j];
      this.target.publish(logRecord);
    } 
    this.start = 0;
    this.count = 0;
  }
  
  public void flush() { this.target.flush(); }
  
  public void close() {
    this.target.close();
    setLevel(Level.OFF);
  }
  
  public void setPushLevel(Level paramLevel) throws SecurityException {
    if (paramLevel == null)
      throw new NullPointerException(); 
    checkPermission();
    this.pushLevel = paramLevel;
  }
  
  public Level getPushLevel() { return this.pushLevel; }
  
  public boolean isLoggable(LogRecord paramLogRecord) { return super.isLoggable(paramLogRecord); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\MemoryHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */