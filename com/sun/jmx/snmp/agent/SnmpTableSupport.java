package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

public abstract class SnmpTableSupport implements SnmpTableEntryFactory, SnmpTableCallbackHandler, Serializable {
  protected List<Object> entries;
  
  protected SnmpMibTable meta;
  
  protected SnmpMib theMib;
  
  private boolean registrationRequired = false;
  
  protected SnmpTableSupport(SnmpMib paramSnmpMib) {
    this.theMib = paramSnmpMib;
    this.meta = getRegisteredTableMeta(paramSnmpMib);
    bindWithTableMeta();
    this.entries = allocateTable();
  }
  
  public abstract void createNewEntry(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt, SnmpMibTable paramSnmpMibTable) throws SnmpStatusException;
  
  public Object getEntry(int paramInt) { return (this.entries == null) ? null : this.entries.get(paramInt); }
  
  public int getSize() { return this.meta.getSize(); }
  
  public void setCreationEnabled(boolean paramBoolean) { this.meta.setCreationEnabled(paramBoolean); }
  
  public boolean isCreationEnabled() { return this.meta.isCreationEnabled(); }
  
  public boolean isRegistrationRequired() { return this.registrationRequired; }
  
  public SnmpIndex buildSnmpIndex(SnmpOid paramSnmpOid) throws SnmpStatusException { return buildSnmpIndex(paramSnmpOid.longValue(false), 0); }
  
  public abstract SnmpOid buildOidFromIndex(SnmpIndex paramSnmpIndex) throws SnmpStatusException;
  
  public abstract ObjectName buildNameFromIndex(SnmpIndex paramSnmpIndex) throws SnmpStatusException;
  
  public void addEntryCb(int paramInt, SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject, SnmpMibTable paramSnmpMibTable) throws SnmpStatusException {
    try {
      if (this.entries != null)
        this.entries.add(paramInt, paramObject); 
    } catch (Exception exception) {
      throw new SnmpStatusException(2);
    } 
  }
  
  public void removeEntryCb(int paramInt, SnmpOid paramSnmpOid, ObjectName paramObjectName, Object paramObject, SnmpMibTable paramSnmpMibTable) throws SnmpStatusException {
    try {
      if (this.entries != null)
        this.entries.remove(paramInt); 
    } catch (Exception exception) {}
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) { this.meta.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject); }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException { this.meta.removeNotificationListener(paramNotificationListener); }
  
  public MBeanNotificationInfo[] getNotificationInfo() { return this.meta.getNotificationInfo(); }
  
  protected abstract SnmpIndex buildSnmpIndex(long[] paramArrayOfLong, int paramInt) throws SnmpStatusException;
  
  protected abstract SnmpMibTable getRegisteredTableMeta(SnmpMib paramSnmpMib);
  
  protected List<Object> allocateTable() { return new ArrayList(); }
  
  protected void addEntry(SnmpIndex paramSnmpIndex, Object paramObject) throws SnmpStatusException {
    SnmpOid snmpOid = buildOidFromIndex(paramSnmpIndex);
    ObjectName objectName = null;
    if (isRegistrationRequired())
      objectName = buildNameFromIndex(paramSnmpIndex); 
    this.meta.addEntry(snmpOid, objectName, paramObject);
  }
  
  protected void addEntry(SnmpIndex paramSnmpIndex, ObjectName paramObjectName, Object paramObject) throws SnmpStatusException {
    SnmpOid snmpOid = buildOidFromIndex(paramSnmpIndex);
    this.meta.addEntry(snmpOid, paramObjectName, paramObject);
  }
  
  protected void removeEntry(SnmpIndex paramSnmpIndex, Object paramObject) throws SnmpStatusException {
    SnmpOid snmpOid = buildOidFromIndex(paramSnmpIndex);
    this.meta.removeEntry(snmpOid, paramObject);
  }
  
  protected Object[] getBasicEntries() {
    if (this.entries == null)
      return null; 
    Object[] arrayOfObject = new Object[this.entries.size()];
    this.entries.toArray(arrayOfObject);
    return arrayOfObject;
  }
  
  protected void bindWithTableMeta() {
    if (this.meta == null)
      return; 
    this.registrationRequired = this.meta.isRegistrationRequired();
    this.meta.registerEntryFactory(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpTableSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */