package com.sun.jmx.snmp.daemon;

final class SnmpRequestCounter {
  int reqid = 0;
  
  public int getNewId() {
    if (++this.reqid < 0)
      this.reqid = 1; 
    return this.reqid;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpRequestCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */