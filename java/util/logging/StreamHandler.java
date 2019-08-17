package java.util.logging;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class StreamHandler extends Handler {
  private OutputStream output;
  
  private boolean doneHeader;
  
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
  
  public StreamHandler() {
    configure();
    this.sealed = true;
  }
  
  public StreamHandler(OutputStream paramOutputStream, Formatter paramFormatter) {
    configure();
    setFormatter(paramFormatter);
    setOutputStream(paramOutputStream);
    this.sealed = true;
  }
  
  protected void setOutputStream(OutputStream paramOutputStream) throws SecurityException {
    if (paramOutputStream == null)
      throw new NullPointerException(); 
    flushAndClose();
    this.output = paramOutputStream;
    this.doneHeader = false;
    String str = getEncoding();
    if (str == null) {
      this.writer = new OutputStreamWriter(this.output);
    } else {
      try {
        this.writer = new OutputStreamWriter(this.output, str);
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        throw new Error("Unexpected exception " + unsupportedEncodingException);
      } 
    } 
  }
  
  public void setEncoding(String paramString) throws SecurityException, UnsupportedEncodingException {
    super.setEncoding(paramString);
    if (this.output == null)
      return; 
    flush();
    if (paramString == null) {
      this.writer = new OutputStreamWriter(this.output);
    } else {
      this.writer = new OutputStreamWriter(this.output, paramString);
    } 
  }
  
  public void publish(LogRecord paramLogRecord) {
    String str;
    if (!isLoggable(paramLogRecord))
      return; 
    try {
      str = getFormatter().format(paramLogRecord);
    } catch (Exception exception) {
      reportError(null, exception, 5);
      return;
    } 
    try {
      if (!this.doneHeader) {
        this.writer.write(getFormatter().getHead(this));
        this.doneHeader = true;
      } 
      this.writer.write(str);
    } catch (Exception exception) {
      reportError(null, exception, 1);
    } 
  }
  
  public boolean isLoggable(LogRecord paramLogRecord) { return (this.writer == null || paramLogRecord == null) ? false : super.isLoggable(paramLogRecord); }
  
  public void flush() {
    if (this.writer != null)
      try {
        this.writer.flush();
      } catch (Exception exception) {
        reportError(null, exception, 2);
      }  
  }
  
  private void flushAndClose() {
    checkPermission();
    if (this.writer != null) {
      try {
        if (!this.doneHeader) {
          this.writer.write(getFormatter().getHead(this));
          this.doneHeader = true;
        } 
        this.writer.write(getFormatter().getTail(this));
        this.writer.flush();
        this.writer.close();
      } catch (Exception exception) {
        reportError(null, exception, 3);
      } 
      this.writer = null;
      this.output = null;
    } 
  }
  
  public void close() { flushAndClose(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\StreamHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */