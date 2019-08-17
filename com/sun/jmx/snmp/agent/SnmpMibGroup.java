package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public abstract class SnmpMibGroup extends SnmpMibOid implements Serializable {
  protected Hashtable<Long, Long> subgroups = null;
  
  public abstract boolean isTable(long paramLong);
  
  public abstract boolean isVariable(long paramLong);
  
  public abstract boolean isReadable(long paramLong);
  
  public abstract SnmpMibTable getTable(long paramLong);
  
  public void validateVarId(long paramLong, Object paramObject) throws SnmpStatusException {
    if (!isVariable(paramLong))
      throw new SnmpStatusException(225); 
  }
  
  public boolean isNestedArc(long paramLong) {
    if (this.subgroups == null)
      return false; 
    Object object = this.subgroups.get(new Long(paramLong));
    return (object != null);
  }
  
  public abstract void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
  
  public abstract void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
  
  public abstract void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
  
  public void getRootOid(Vector<Integer> paramVector) {}
  
  void registerNestedArc(long paramLong) {
    Long long = new Long(paramLong);
    if (this.subgroups == null)
      this.subgroups = new Hashtable(); 
    this.subgroups.put(long, long);
  }
  
  protected void registerObject(long paramLong) {
    long[] arrayOfLong = new long[1];
    arrayOfLong[0] = paramLong;
    super.registerNode(arrayOfLong, 0, null);
  }
  
  void registerNode(long[] paramArrayOfLong, int paramInt, SnmpMibNode paramSnmpMibNode) throws IllegalAccessException {
    super.registerNode(paramArrayOfLong, paramInt, paramSnmpMibNode);
    if (paramInt < 0)
      return; 
    if (paramInt >= paramArrayOfLong.length)
      return; 
    registerNestedArc(paramArrayOfLong[paramInt]);
  }
  
  void findHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt, SnmpRequestTree paramSnmpRequestTree) throws SnmpStatusException {
    int i = paramArrayOfLong.length;
    if (paramSnmpRequestTree == null)
      throw new SnmpStatusException(5); 
    Object object = paramSnmpRequestTree.getUserData();
    if (paramInt >= i)
      throw new SnmpStatusException(6); 
    long l = paramArrayOfLong[paramInt];
    if (isNestedArc(l)) {
      super.findHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt, paramSnmpRequestTree);
    } else if (isTable(l)) {
      SnmpMibTable snmpMibTable = getTable(l);
      snmpMibTable.findHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt + 1, paramSnmpRequestTree);
    } else {
      validateVarId(l, object);
      if (paramInt + 2 > i)
        throw new SnmpStatusException(224); 
      if (paramInt + 2 < i)
        throw new SnmpStatusException(224); 
      if (paramArrayOfLong[paramInt + 1] != 0L)
        throw new SnmpStatusException(224); 
      paramSnmpRequestTree.add(this, paramInt, paramSnmpVarBind);
    } 
  }
  
  long[] findNextHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt1, int paramInt2, SnmpRequestTree paramSnmpRequestTree, AcmChecker paramAcmChecker) throws SnmpStatusException {
    int i = paramArrayOfLong.length;
    Object object = null;
    if (paramSnmpRequestTree == null)
      throw new SnmpStatusException(225); 
    Object object1 = paramSnmpRequestTree.getUserData();
    int j = paramSnmpRequestTree.getRequestPduVersion();
    if (paramInt1 >= i)
      return super.findNextHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt1, paramInt2, paramSnmpRequestTree, paramAcmChecker); 
    long l = paramArrayOfLong[paramInt1];
    long[] arrayOfLong = null;
    try {
      if (isTable(l)) {
        SnmpMibTable snmpMibTable = getTable(l);
        paramAcmChecker.add(paramInt2, l);
        try {
          arrayOfLong = snmpMibTable.findNextHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt1 + 1, paramInt2 + 1, paramSnmpRequestTree, paramAcmChecker);
        } catch (SnmpStatusException snmpStatusException) {
          throw new SnmpStatusException(225);
        } finally {
          paramAcmChecker.remove(paramInt2);
        } 
        arrayOfLong[paramInt2] = l;
        return arrayOfLong;
      } 
      if (isReadable(l)) {
        if (paramInt1 == i - 1) {
          arrayOfLong = new long[paramInt2 + 2];
          arrayOfLong[paramInt2 + 1] = 0L;
          arrayOfLong[paramInt2] = l;
          paramAcmChecker.add(paramInt2, arrayOfLong, paramInt2, 2);
          try {
            paramAcmChecker.checkCurrentOid();
          } catch (SnmpStatusException snmpStatusException) {
            throw new SnmpStatusException(225);
          } finally {
            paramAcmChecker.remove(paramInt2, 2);
          } 
          paramSnmpRequestTree.add(this, paramInt2, paramSnmpVarBind);
          return arrayOfLong;
        } 
      } else if (isNestedArc(l)) {
        SnmpMibNode snmpMibNode = getChild(l);
        if (snmpMibNode != null) {
          paramAcmChecker.add(paramInt2, l);
          try {
            arrayOfLong = snmpMibNode.findNextHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt1 + 1, paramInt2 + 1, paramSnmpRequestTree, paramAcmChecker);
            arrayOfLong[paramInt2] = l;
            return arrayOfLong;
          } finally {
            paramAcmChecker.remove(paramInt2);
          } 
        } 
      } 
      throw new SnmpStatusException(225);
    } catch (SnmpStatusException snmpStatusException) {
      long[] arrayOfLong1 = new long[1];
      arrayOfLong1[0] = getNextVarId(l, object1, j);
      return findNextHandlingNode(paramSnmpVarBind, arrayOfLong1, 0, paramInt2, paramSnmpRequestTree, paramAcmChecker);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpMibGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */