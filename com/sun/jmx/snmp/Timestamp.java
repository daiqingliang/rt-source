package com.sun.jmx.snmp;

import java.io.Serializable;
import java.util.Date;

public class Timestamp implements Serializable {
  private static final long serialVersionUID = -242456119149401823L;
  
  private long sysUpTime;
  
  private long crtime;
  
  private SnmpTimeticks uptimeCache = null;
  
  public Timestamp() { this.crtime = System.currentTimeMillis(); }
  
  public Timestamp(long paramLong1, long paramLong2) {
    this.sysUpTime = paramLong1;
    this.crtime = paramLong2;
  }
  
  public Timestamp(long paramLong) {
    this.sysUpTime = paramLong;
    this.crtime = System.currentTimeMillis();
  }
  
  public final SnmpTimeticks getTimeTicks() {
    if (this.uptimeCache == null)
      this.uptimeCache = new SnmpTimeticks((int)this.sysUpTime); 
    return this.uptimeCache;
  }
  
  public final long getSysUpTime() { return this.sysUpTime; }
  
  public final Date getDate() { return new Date(this.crtime); }
  
  public final long getDateTime() { return this.crtime; }
  
  public final String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("{SysUpTime = " + SnmpTimeticks.printTimeTicks(this.sysUpTime));
    stringBuffer.append("} {Timestamp = " + getDate().toString() + "}");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\Timestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */