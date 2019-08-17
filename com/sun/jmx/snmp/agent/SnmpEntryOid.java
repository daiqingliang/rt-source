package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;

class SnmpEntryOid extends SnmpOid {
  private static final long serialVersionUID = 9212653887791059564L;
  
  public SnmpEntryOid(long[] paramArrayOfLong, int paramInt) {
    int i = paramArrayOfLong.length - paramInt;
    long[] arrayOfLong = new long[i];
    System.arraycopy(paramArrayOfLong, paramInt, arrayOfLong, 0, i);
    this.components = arrayOfLong;
    this.componentCount = i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpEntryOid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */