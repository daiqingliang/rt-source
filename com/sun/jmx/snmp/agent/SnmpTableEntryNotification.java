package com.sun.jmx.snmp.agent;

import javax.management.Notification;
import javax.management.ObjectName;

public class SnmpTableEntryNotification extends Notification {
  public static final String SNMP_ENTRY_ADDED = "jmx.snmp.table.entry.added";
  
  public static final String SNMP_ENTRY_REMOVED = "jmx.snmp.table.entry.removed";
  
  private final Object entry;
  
  private final ObjectName name;
  
  private static final long serialVersionUID = 5832592016227890252L;
  
  SnmpTableEntryNotification(String paramString, Object paramObject1, long paramLong1, long paramLong2, Object paramObject2, ObjectName paramObjectName) {
    super(paramString, paramObject1, paramLong1, paramLong2);
    this.entry = paramObject2;
    this.name = paramObjectName;
  }
  
  public Object getEntry() { return this.entry; }
  
  public ObjectName getEntryName() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpTableEntryNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */