package com.sun.management.jmx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.management.Notification;
import javax.management.NotificationListener;

@Deprecated
public class TraceListener implements NotificationListener {
  protected PrintStream out;
  
  protected boolean needTobeClosed = false;
  
  protected boolean formated = false;
  
  public TraceListener() { this.out = System.out; }
  
  public TraceListener(PrintStream paramPrintStream) throws IllegalArgumentException {
    if (paramPrintStream == null)
      throw new IllegalArgumentException("An PrintStream object should be specified."); 
    this.out = paramPrintStream;
  }
  
  public TraceListener(String paramString) throws IOException {
    this.out = new PrintStream(new FileOutputStream(paramString, true));
    this.needTobeClosed = true;
  }
  
  public void setFormated(boolean paramBoolean) { this.formated = paramBoolean; }
  
  public void handleNotification(Notification paramNotification, Object paramObject) {
    if (paramNotification instanceof TraceNotification) {
      TraceNotification traceNotification = (TraceNotification)paramNotification;
      if (this.formated) {
        this.out.print("\nGlobal sequence number: " + traceNotification.globalSequenceNumber + "     Sequence number: " + traceNotification.sequenceNumber + "\nLevel: " + Trace.getLevel(traceNotification.level) + "     Type: " + Trace.getType(traceNotification.type) + "\nClass  Name: " + new String(traceNotification.className) + "\nMethod Name: " + new String(traceNotification.methodName) + "\n");
        if (traceNotification.exception != null) {
          traceNotification.exception.printStackTrace(this.out);
          this.out.println();
        } 
        if (traceNotification.info != null)
          this.out.println("Information: " + traceNotification.info); 
      } else {
        this.out.print("(" + traceNotification.className + " " + traceNotification.methodName + ") ");
        if (traceNotification.exception != null) {
          traceNotification.exception.printStackTrace(this.out);
          this.out.println();
        } 
        if (traceNotification.info != null)
          this.out.println(traceNotification.info); 
      } 
    } 
  }
  
  public void setFile(String paramString) throws IOException {
    PrintStream printStream = new PrintStream(new FileOutputStream(paramString, true));
    if (this.needTobeClosed)
      this.out.close(); 
    this.out = printStream;
    this.needTobeClosed = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\jmx\TraceListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */