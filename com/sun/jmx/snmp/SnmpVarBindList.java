package com.sun.jmx.snmp;

import java.util.Enumeration;
import java.util.Vector;

public class SnmpVarBindList extends Vector<SnmpVarBind> {
  private static final long serialVersionUID = -7203997794636430321L;
  
  public String identity = "VarBindList ";
  
  Timestamp timestamp;
  
  public SnmpVarBindList() { super(5, 5); }
  
  public SnmpVarBindList(int paramInt) { super(paramInt); }
  
  public SnmpVarBindList(String paramString) {
    super(5, 5);
    this.identity = paramString;
  }
  
  public SnmpVarBindList(SnmpVarBindList paramSnmpVarBindList) {
    super(paramSnmpVarBindList.size(), 5);
    paramSnmpVarBindList.copyInto(this.elementData);
    this.elementCount = paramSnmpVarBindList.size();
  }
  
  public SnmpVarBindList(Vector<SnmpVarBind> paramVector) {
    super(paramVector.size(), 5);
    Enumeration enumeration = paramVector.elements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      addElement(snmpVarBind.clone());
    } 
  }
  
  public SnmpVarBindList(String paramString, Vector<SnmpVarBind> paramVector) {
    this(paramVector);
    this.identity = paramString;
  }
  
  public Timestamp getTimestamp() { return this.timestamp; }
  
  public void setTimestamp(Timestamp paramTimestamp) { this.timestamp = paramTimestamp; }
  
  public final SnmpVarBind getVarBindAt(int paramInt) { return (SnmpVarBind)elementAt(paramInt); }
  
  public int getVarBindCount() { return size(); }
  
  public Enumeration<SnmpVarBind> getVarBindList() { return elements(); }
  
  public final void setVarBindList(Vector<SnmpVarBind> paramVector) { setVarBindList(paramVector, false); }
  
  public final void setVarBindList(Vector<SnmpVarBind> paramVector, boolean paramBoolean) {
    synchronized (paramVector) {
      int i = paramVector.size();
      setSize(i);
      paramVector.copyInto(this.elementData);
      if (paramBoolean)
        for (byte b = 0; b < i; b++) {
          SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[b];
          this.elementData[b] = snmpVarBind.clone();
        }  
    } 
  }
  
  public void addVarBindList(SnmpVarBindList paramSnmpVarBindList) {
    ensureCapacity(paramSnmpVarBindList.size() + size());
    for (byte b = 0; b < paramSnmpVarBindList.size(); b++)
      addElement(paramSnmpVarBindList.getVarBindAt(b)); 
  }
  
  public boolean removeVarBindList(SnmpVarBindList paramSnmpVarBindList) {
    boolean bool = true;
    for (byte b = 0; b < paramSnmpVarBindList.size(); b++)
      bool = removeElement(paramSnmpVarBindList.getVarBindAt(b)); 
    return bool;
  }
  
  public final void replaceVarBind(SnmpVarBind paramSnmpVarBind, int paramInt) { setElementAt(paramSnmpVarBind, paramInt); }
  
  public final void addVarBind(String[] paramArrayOfString, String paramString) throws SnmpStatusException {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      SnmpVarBind snmpVarBind = new SnmpVarBind(paramArrayOfString[b]);
      snmpVarBind.addInstance(paramString);
      addElement(snmpVarBind);
    } 
  }
  
  public boolean removeVarBind(String[] paramArrayOfString, String paramString) throws SnmpStatusException {
    boolean bool = true;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      SnmpVarBind snmpVarBind = new SnmpVarBind(paramArrayOfString[b]);
      snmpVarBind.addInstance(paramString);
      int i = indexOfOid(snmpVarBind);
      try {
        removeElementAt(i);
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        bool = false;
      } 
    } 
    return bool;
  }
  
  public void addVarBind(String[] paramArrayOfString) throws SnmpStatusException { addVarBind(paramArrayOfString, null); }
  
  public boolean removeVarBind(String[] paramArrayOfString) throws SnmpStatusException { return removeVarBind(paramArrayOfString, null); }
  
  public void addVarBind(String paramString) {
    SnmpVarBind snmpVarBind = new SnmpVarBind(paramString);
    addVarBind(snmpVarBind);
  }
  
  public boolean removeVarBind(String paramString) throws SnmpStatusException {
    SnmpVarBind snmpVarBind = new SnmpVarBind(paramString);
    int i = indexOfOid(snmpVarBind);
    try {
      removeElementAt(i);
      return true;
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return false;
    } 
  }
  
  public void addVarBind(SnmpVarBind paramSnmpVarBind) { addElement(paramSnmpVarBind); }
  
  public boolean removeVarBind(SnmpVarBind paramSnmpVarBind) { return removeElement(paramSnmpVarBind); }
  
  public void addInstance(String paramString) {
    int i = size();
    for (byte b = 0; b < i; b++)
      ((SnmpVarBind)this.elementData[b]).addInstance(paramString); 
  }
  
  public final void concat(Vector<SnmpVarBind> paramVector) {
    ensureCapacity(size() + paramVector.size());
    Enumeration enumeration = paramVector.elements();
    while (enumeration.hasMoreElements())
      addElement(enumeration.nextElement()); 
  }
  
  public boolean checkForValidValues() {
    int i = size();
    for (byte b = 0; b < i; b++) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[b];
      if (!snmpVarBind.isValidValue())
        return false; 
    } 
    return true;
  }
  
  public boolean checkForUnspecifiedValue() {
    int i = size();
    for (byte b = 0; b < i; b++) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[b];
      if (snmpVarBind.isUnspecifiedValue())
        return true; 
    } 
    return false;
  }
  
  public SnmpVarBindList splitAt(int paramInt) {
    SnmpVarBindList snmpVarBindList = null;
    if (paramInt > this.elementCount)
      return snmpVarBindList; 
    snmpVarBindList = new SnmpVarBindList();
    int i = size();
    for (int j = paramInt; j < i; j++)
      snmpVarBindList.addElement((SnmpVarBind)this.elementData[j]); 
    this.elementCount = paramInt;
    trimToSize();
    return snmpVarBindList;
  }
  
  public int indexOfOid(SnmpVarBind paramSnmpVarBind, int paramInt1, int paramInt2) {
    SnmpOid snmpOid = paramSnmpVarBind.getOid();
    for (int i = paramInt1; i < paramInt2; i++) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[i];
      if (snmpOid.equals(snmpVarBind.getOid()))
        return i; 
    } 
    return -1;
  }
  
  public int indexOfOid(SnmpVarBind paramSnmpVarBind) { return indexOfOid(paramSnmpVarBind, 0, size()); }
  
  public int indexOfOid(SnmpOid paramSnmpOid) {
    int i = size();
    for (byte b = 0; b < i; b++) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[b];
      if (paramSnmpOid.equals(snmpVarBind.getOid()))
        return b; 
    } 
    return -1;
  }
  
  public SnmpVarBindList cloneWithValue() {
    SnmpVarBindList snmpVarBindList = new SnmpVarBindList();
    snmpVarBindList.setTimestamp(getTimestamp());
    snmpVarBindList.ensureCapacity(size());
    for (byte b = 0; b < size(); b++) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[b];
      snmpVarBindList.addElement(snmpVarBind.clone());
    } 
    return snmpVarBindList;
  }
  
  public SnmpVarBindList cloneWithoutValue() {
    SnmpVarBindList snmpVarBindList = new SnmpVarBindList();
    int i = size();
    snmpVarBindList.ensureCapacity(i);
    for (byte b = 0; b < i; b++) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[b];
      snmpVarBindList.addElement((SnmpVarBind)snmpVarBind.cloneWithoutValue());
    } 
    return snmpVarBindList;
  }
  
  public SnmpVarBindList clone() { return cloneWithValue(); }
  
  public Vector<SnmpVarBind> toVector(boolean paramBoolean) {
    int i = this.elementCount;
    if (!paramBoolean)
      return new Vector(this); 
    Vector vector = new Vector(i, 5);
    for (byte b = 0; b < i; b++) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[b];
      vector.addElement(snmpVarBind.clone());
    } 
    return vector;
  }
  
  public String oidListToString() {
    StringBuilder stringBuilder = new StringBuilder(300);
    for (byte b = 0; b < this.elementCount; b++) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)this.elementData[b];
      stringBuilder.append(snmpVarBind.getOid().toString()).append("\n");
    } 
    return stringBuilder.toString();
  }
  
  public String varBindListToString() {
    StringBuilder stringBuilder = new StringBuilder(300);
    for (byte b = 0; b < this.elementCount; b++)
      stringBuilder.append(this.elementData[b].toString()).append("\n"); 
    return stringBuilder.toString();
  }
  
  protected void finalize() { removeAllElements(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpVarBindList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */