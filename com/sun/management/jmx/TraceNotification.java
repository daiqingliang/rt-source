package com.sun.management.jmx;

import javax.management.Notification;

@Deprecated
public class TraceNotification extends Notification {
  public int level;
  
  public int type;
  
  public String className;
  
  public String methodName;
  
  public String info;
  
  public Throwable exception;
  
  public long globalSequenceNumber;
  
  public long sequenceNumber;
  
  public TraceNotification(Object paramObject, long paramLong1, long paramLong2, int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, Throwable paramThrowable) {
    super(null, paramObject, paramLong1);
    this.sequenceNumber = paramLong1;
    this.globalSequenceNumber = paramLong2;
    this.level = paramInt1;
    this.type = paramInt2;
    this.className = (paramString1 != null) ? paramString1 : "";
    this.methodName = (paramString2 != null) ? paramString2 : "";
    this.info = (paramString3 != null) ? paramString3 : null;
    this.exception = paramThrowable;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\jmx\TraceNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */