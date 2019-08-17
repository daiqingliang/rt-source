package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;

public class SnmpOidTableSupport implements SnmpOidTable {
  private Hashtable<String, SnmpOidRecord> oidStore = new Hashtable();
  
  private String myName;
  
  public SnmpOidTableSupport(String paramString) { this.myName = paramString; }
  
  public SnmpOidRecord resolveVarName(String paramString) throws SnmpStatusException {
    SnmpOidRecord snmpOidRecord = (SnmpOidRecord)this.oidStore.get(paramString);
    if (snmpOidRecord != null)
      return snmpOidRecord; 
    throw new SnmpStatusException("Variable name <" + paramString + "> not found in Oid repository");
  }
  
  public SnmpOidRecord resolveVarOid(String paramString) throws SnmpStatusException {
    int i = paramString.indexOf('.');
    if (i < 0)
      throw new SnmpStatusException("Variable oid <" + paramString + "> not found in Oid repository"); 
    if (i == 0)
      paramString = paramString.substring(1, paramString.length()); 
    Enumeration enumeration = this.oidStore.elements();
    while (enumeration.hasMoreElements()) {
      SnmpOidRecord snmpOidRecord = (SnmpOidRecord)enumeration.nextElement();
      if (snmpOidRecord.getOid().equals(paramString))
        return snmpOidRecord; 
    } 
    throw new SnmpStatusException("Variable oid <" + paramString + "> not found in Oid repository");
  }
  
  public Vector<SnmpOidRecord> getAllEntries() {
    Vector vector = new Vector();
    Enumeration enumeration = this.oidStore.elements();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return vector;
  }
  
  public void loadMib(SnmpOidRecord[] paramArrayOfSnmpOidRecord) {
    try {
      for (byte b = 0;; b++) {
        SnmpOidRecord snmpOidRecord = paramArrayOfSnmpOidRecord[b];
        if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
          JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpOidTableSupport.class.getName(), "loadMib", "Load " + snmpOidRecord.getName()); 
        this.oidStore.put(snmpOidRecord.getName(), snmpOidRecord);
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof SnmpOidTableSupport))
      return false; 
    SnmpOidTableSupport snmpOidTableSupport = (SnmpOidTableSupport)paramObject;
    return this.myName.equals(snmpOidTableSupport.getName());
  }
  
  public int hashCode() { return Objects.hashCode(this.myName); }
  
  public String getName() { return this.myName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpOidTableSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */