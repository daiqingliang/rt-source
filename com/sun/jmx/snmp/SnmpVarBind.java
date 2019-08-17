package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpVarBind implements SnmpDataTypeEnums, Cloneable, Serializable {
  private static final long serialVersionUID = 491778383240759376L;
  
  private static final String[] statusLegend = { "Status Mapper", "Value not initialized", "Valid Value", "No such object", "No such Instance", "End of Mib View" };
  
  public static final int stValueUnspecified = 1;
  
  public static final int stValueOk = 2;
  
  public static final int stValueNoSuchObject = 3;
  
  public static final int stValueNoSuchInstance = 4;
  
  public static final int stValueEndOfMibView = 5;
  
  public static final SnmpNull noSuchObject = new SnmpNull(128);
  
  public static final SnmpNull noSuchInstance = new SnmpNull(129);
  
  public static final SnmpNull endOfMibView = new SnmpNull(130);
  
  public SnmpOid oid = null;
  
  public SnmpValue value = null;
  
  public int status = 1;
  
  public SnmpVarBind() {}
  
  public SnmpVarBind(SnmpOid paramSnmpOid) { this.oid = paramSnmpOid; }
  
  public SnmpVarBind(SnmpOid paramSnmpOid, SnmpValue paramSnmpValue) {
    this.oid = paramSnmpOid;
    setSnmpValue(paramSnmpValue);
  }
  
  public SnmpVarBind(String paramString) throws SnmpStatusException {
    if (paramString.startsWith(".")) {
      this.oid = new SnmpOid(paramString);
    } else {
      try {
        int i = paramString.indexOf('.');
        handleLong(paramString, i);
        this.oid = new SnmpOid(paramString);
      } catch (NumberFormatException numberFormatException) {
        int i = paramString.indexOf('.');
        if (i <= 0) {
          SnmpOidRecord snmpOidRecord = resolveVarName(paramString);
          this.oid = new SnmpOid(snmpOidRecord.getName());
        } else {
          SnmpOidRecord snmpOidRecord = resolveVarName(paramString.substring(0, i));
          this.oid = new SnmpOid(snmpOidRecord.getName() + paramString.substring(i));
        } 
      } 
    } 
  }
  
  public final SnmpOid getOid() { return this.oid; }
  
  public final void setOid(SnmpOid paramSnmpOid) {
    this.oid = paramSnmpOid;
    clearValue();
  }
  
  public final SnmpValue getSnmpValue() { return this.value; }
  
  public final void setSnmpValue(SnmpValue paramSnmpValue) {
    this.value = paramSnmpValue;
    setValueValid();
  }
  
  public final SnmpCounter64 getSnmpCounter64Value() throws ClassCastException { return (SnmpCounter64)this.value; }
  
  public final void setSnmpCounter64Value(long paramLong) throws IllegalArgumentException {
    clearValue();
    this.value = new SnmpCounter64(paramLong);
    setValueValid();
  }
  
  public final SnmpInt getSnmpIntValue() throws ClassCastException { return (SnmpInt)this.value; }
  
  public final void setSnmpIntValue(long paramLong) throws IllegalArgumentException {
    clearValue();
    this.value = new SnmpInt(paramLong);
    setValueValid();
  }
  
  public final SnmpCounter getSnmpCounterValue() throws ClassCastException { return (SnmpCounter)this.value; }
  
  public final void setSnmpCounterValue(long paramLong) throws IllegalArgumentException {
    clearValue();
    this.value = new SnmpCounter(paramLong);
    setValueValid();
  }
  
  public final SnmpGauge getSnmpGaugeValue() throws ClassCastException { return (SnmpGauge)this.value; }
  
  public final void setSnmpGaugeValue(long paramLong) throws IllegalArgumentException {
    clearValue();
    this.value = new SnmpGauge(paramLong);
    setValueValid();
  }
  
  public final SnmpTimeticks getSnmpTimeticksValue() throws ClassCastException { return (SnmpTimeticks)this.value; }
  
  public final void setSnmpTimeticksValue(long paramLong) throws IllegalArgumentException {
    clearValue();
    this.value = new SnmpTimeticks(paramLong);
    setValueValid();
  }
  
  public final SnmpOid getSnmpOidValue() { return (SnmpOid)this.value; }
  
  public final void setSnmpOidValue(String paramString) throws SnmpStatusException {
    clearValue();
    this.value = new SnmpOid(paramString);
    setValueValid();
  }
  
  public final SnmpIpAddress getSnmpIpAddressValue() throws ClassCastException { return (SnmpIpAddress)this.value; }
  
  public final void setSnmpIpAddressValue(String paramString) throws SnmpStatusException {
    clearValue();
    this.value = new SnmpIpAddress(paramString);
    setValueValid();
  }
  
  public final SnmpString getSnmpStringValue() throws ClassCastException { return (SnmpString)this.value; }
  
  public final void setSnmpStringValue(String paramString) throws SnmpStatusException {
    clearValue();
    this.value = new SnmpString(paramString);
    setValueValid();
  }
  
  public final SnmpOpaque getSnmpOpaqueValue() throws ClassCastException { return (SnmpOpaque)this.value; }
  
  public final void setSnmpOpaqueValue(byte[] paramArrayOfByte) {
    clearValue();
    this.value = new SnmpOpaque(paramArrayOfByte);
    setValueValid();
  }
  
  public final SnmpStringFixed getSnmpStringFixedValue() throws ClassCastException { return (SnmpStringFixed)this.value; }
  
  public final void setSnmpStringFixedValue(String paramString) throws SnmpStatusException {
    clearValue();
    this.value = new SnmpStringFixed(paramString);
    setValueValid();
  }
  
  public SnmpOidRecord resolveVarName(String paramString) throws SnmpStatusException {
    SnmpOidTable snmpOidTable = SnmpOid.getSnmpOidTable();
    if (snmpOidTable == null)
      throw new SnmpStatusException(2); 
    int i = paramString.indexOf('.');
    return (i < 0) ? snmpOidTable.resolveVarName(paramString) : snmpOidTable.resolveVarOid(paramString);
  }
  
  public final int getValueStatus() { return this.status; }
  
  public final String getValueStatusLegend() { return statusLegend[this.status]; }
  
  public final boolean isValidValue() { return (this.status == 2); }
  
  public final boolean isUnspecifiedValue() { return (this.status == 1); }
  
  public final void clearValue() {
    this.value = null;
    this.status = 1;
  }
  
  public final boolean isOidEqual(SnmpVarBind paramSnmpVarBind) { return this.oid.equals(paramSnmpVarBind.oid); }
  
  public final void addInstance(long paramLong) throws IllegalArgumentException { this.oid.append(paramLong); }
  
  public final void addInstance(long[] paramArrayOfLong) throws SnmpStatusException { this.oid.addToOid(paramArrayOfLong); }
  
  public final void addInstance(String paramString) throws SnmpStatusException {
    if (paramString != null)
      this.oid.addToOid(paramString); 
  }
  
  public void insertInOid(int paramInt) { this.oid.insert(paramInt); }
  
  public void appendInOid(SnmpOid paramSnmpOid) { this.oid.append(paramSnmpOid); }
  
  public final boolean hasVarBindException() {
    switch (this.status) {
      case 1:
      case 3:
      case 4:
      case 5:
        return true;
    } 
    return false;
  }
  
  public void copyValueAndOid(SnmpVarBind paramSnmpVarBind) {
    setOid((SnmpOid)paramSnmpVarBind.oid.clone());
    copyValue(paramSnmpVarBind);
  }
  
  public void copyValue(SnmpVarBind paramSnmpVarBind) {
    if (paramSnmpVarBind.isValidValue()) {
      this.value = paramSnmpVarBind.getSnmpValue().duplicate();
      setValueValid();
    } else {
      this.status = paramSnmpVarBind.getValueStatus();
      if (this.status == 5) {
        this.value = endOfMibView;
      } else if (this.status == 3) {
        this.value = noSuchObject;
      } else if (this.status == 4) {
        this.value = noSuchInstance;
      } 
    } 
  }
  
  public Object cloneWithoutValue() {
    SnmpOid snmpOid = (SnmpOid)this.oid.clone();
    return new SnmpVarBind(snmpOid);
  }
  
  public SnmpVarBind clone() {
    SnmpVarBind snmpVarBind = new SnmpVarBind();
    snmpVarBind.copyValueAndOid(this);
    return snmpVarBind;
  }
  
  public final String getStringValue() { return this.value.toString(); }
  
  public final void setNoSuchObject() {
    this.value = noSuchObject;
    this.status = 3;
  }
  
  public final void setNoSuchInstance() {
    this.value = noSuchInstance;
    this.status = 4;
  }
  
  public final void setEndOfMibView() {
    this.value = endOfMibView;
    this.status = 5;
  }
  
  public final String toString() {
    StringBuilder stringBuilder = new StringBuilder(400);
    stringBuilder.append("Object ID : ").append(this.oid.toString());
    if (isValidValue()) {
      stringBuilder.append("  (Syntax : ").append(this.value.getTypeName()).append(")\n");
      stringBuilder.append("Value : ").append(this.value.toString());
    } else {
      stringBuilder.append("\nValue Exception : ").append(getValueStatusLegend());
    } 
    return stringBuilder.toString();
  }
  
  private void setValueValid() {
    if (this.value == endOfMibView) {
      this.status = 5;
    } else if (this.value == noSuchObject) {
      this.status = 3;
    } else if (this.value == noSuchInstance) {
      this.status = 4;
    } else {
      this.status = 2;
    } 
  }
  
  private void handleLong(String paramString, int paramInt) throws NumberFormatException, SnmpStatusException {
    String str;
    if (paramInt > 0) {
      str = paramString.substring(0, paramInt);
    } else {
      str = paramString;
    } 
    Long.parseLong(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpVarBind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */