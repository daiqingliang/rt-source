package java.util.logging;

public class ConsoleHandler extends StreamHandler {
  private void configure() {
    LogManager logManager = LogManager.getLogManager();
    String str = getClass().getName();
    setLevel(logManager.getLevelProperty(str + ".level", Level.INFO));
    setFilter(logManager.getFilterProperty(str + ".filter", null));
    setFormatter(logManager.getFormatterProperty(str + ".formatter", new SimpleFormatter()));
    try {
      setEncoding(logManager.getStringProperty(str + ".encoding", null));
    } catch (Exception exception) {
      try {
        setEncoding(null);
      } catch (Exception exception1) {}
    } 
  }
  
  public ConsoleHandler() {
    configure();
    setOutputStream(System.err);
    this.sealed = true;
  }
  
  public void publish(LogRecord paramLogRecord) {
    super.publish(paramLogRecord);
    flush();
  }
  
  public void close() { flush(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\ConsoleHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */