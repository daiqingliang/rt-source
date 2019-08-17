package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class SnmpIndex implements Serializable {
  private static final long serialVersionUID = 8712159739982192146L;
  
  private Vector<SnmpOid> oids = new Vector();
  
  private int size = 0;
  
  public SnmpIndex(SnmpOid[] paramArrayOfSnmpOid) {
    this.size = paramArrayOfSnmpOid.length;
    for (byte b = 0; b < this.size; b++)
      this.oids.addElement(paramArrayOfSnmpOid[b]); 
  }
  
  public SnmpIndex(SnmpOid paramSnmpOid) {
    this.oids.addElement(paramSnmpOid);
    this.size = 1;
  }
  
  public int getNbComponents() { return this.size; }
  
  public Vector<SnmpOid> getComponents() { return this.oids; }
  
  public boolean equals(SnmpIndex paramSnmpIndex) {
    if (this.size != paramSnmpIndex.getNbComponents())
      return false; 
    Vector vector = paramSnmpIndex.getComponents();
    for (byte b = 0; b < this.size; b++) {
      SnmpOid snmpOid1 = (SnmpOid)this.oids.elementAt(b);
      SnmpOid snmpOid2 = (SnmpOid)vector.elementAt(b);
      if (!snmpOid1.equals(snmpOid2))
        return false; 
    } 
    return true;
  }
  
  public int compareTo(SnmpIndex paramSnmpIndex) {
    int i = paramSnmpIndex.getNbComponents();
    Vector vector = paramSnmpIndex.getComponents();
    byte b = 0;
    while (b < this.size) {
      if (b > i)
        return 1; 
      SnmpOid snmpOid1 = (SnmpOid)this.oids.elementAt(b);
      SnmpOid snmpOid2 = (SnmpOid)vector.elementAt(b);
      int j = snmpOid1.compareTo(snmpOid2);
      if (j == 0) {
        b++;
        continue;
      } 
      return j;
    } 
    return 0;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    Enumeration enumeration = this.oids.elements();
    while (enumeration.hasMoreElements()) {
      SnmpOid snmpOid = (SnmpOid)enumeration.nextElement();
      stringBuilder.append("//").append(snmpOid.toString());
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */