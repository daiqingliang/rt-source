package com.sun.management.jmx;

import javax.management.Notification;
import javax.management.NotificationFilter;

@Deprecated
public class TraceFilter implements NotificationFilter {
  protected int levels;
  
  protected int types;
  
  public TraceFilter(int paramInt1, int paramInt2) throws IllegalArgumentException {
    this.levels = paramInt1;
    this.types = paramInt2;
  }
  
  public boolean isNotificationEnabled(Notification paramNotification) { return false; }
  
  public int getLevels() { return this.levels; }
  
  public int getTypes() { return this.types; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\jmx\TraceFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */