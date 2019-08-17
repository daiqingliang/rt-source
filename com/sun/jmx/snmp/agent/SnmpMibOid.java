package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class SnmpMibOid extends SnmpMibNode implements Serializable {
  private static final long serialVersionUID = 5012254771107446812L;
  
  private NonSyncVector<SnmpMibNode> children = new NonSyncVector(1);
  
  private int nbChildren = 0;
  
  public void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    Enumeration enumeration = paramSnmpMibSubRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      SnmpStatusException snmpStatusException = new SnmpStatusException(225);
      paramSnmpMibSubRequest.registerGetException(snmpVarBind, snmpStatusException);
    } 
  }
  
  public void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    Enumeration enumeration = paramSnmpMibSubRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      SnmpStatusException snmpStatusException = new SnmpStatusException(6);
      paramSnmpMibSubRequest.registerSetException(snmpVarBind, snmpStatusException);
    } 
  }
  
  public void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    Enumeration enumeration = paramSnmpMibSubRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      SnmpStatusException snmpStatusException = new SnmpStatusException(6);
      paramSnmpMibSubRequest.registerCheckException(snmpVarBind, snmpStatusException);
    } 
  }
  
  void findHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt, SnmpRequestTree paramSnmpRequestTree) throws SnmpStatusException {
    int i = paramArrayOfLong.length;
    Object object = null;
    if (paramSnmpRequestTree == null)
      throw new SnmpStatusException(5); 
    if (paramInt > i)
      throw new SnmpStatusException(225); 
    if (paramInt == i)
      throw new SnmpStatusException(224); 
    SnmpMibNode snmpMibNode = getChild(paramArrayOfLong[paramInt]);
    if (snmpMibNode == null) {
      paramSnmpRequestTree.add(this, paramInt, paramSnmpVarBind);
    } else {
      snmpMibNode.findHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt + 1, paramSnmpRequestTree);
    } 
  }
  
  long[] findNextHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt1, int paramInt2, SnmpRequestTree paramSnmpRequestTree, AcmChecker paramAcmChecker) throws SnmpStatusException {
    int i = paramArrayOfLong.length;
    Object object = null;
    long[] arrayOfLong1 = null;
    if (paramSnmpRequestTree == null)
      throw new SnmpStatusException(225); 
    Object object1 = paramSnmpRequestTree.getUserData();
    int j = paramSnmpRequestTree.getRequestPduVersion();
    if (paramInt1 >= i) {
      long[] arrayOfLong = new long[1];
      arrayOfLong[0] = getNextVarId(-1L, object1, j);
      return findNextHandlingNode(paramSnmpVarBind, arrayOfLong, 0, paramInt2, paramSnmpRequestTree, paramAcmChecker);
    } 
    long[] arrayOfLong2 = new long[1];
    long l = paramArrayOfLong[paramInt1];
    while (true) {
      try {
        SnmpMibNode snmpMibNode = getChild(l);
        if (snmpMibNode == null)
          throw new SnmpStatusException(225); 
        paramAcmChecker.add(paramInt2, l);
        try {
          arrayOfLong1 = snmpMibNode.findNextHandlingNode(paramSnmpVarBind, paramArrayOfLong, paramInt1 + 1, paramInt2 + 1, paramSnmpRequestTree, paramAcmChecker);
        } finally {
          paramAcmChecker.remove(paramInt2);
        } 
        arrayOfLong1[paramInt2] = l;
        return arrayOfLong1;
      } catch (SnmpStatusException snmpStatusException) {
        l = getNextVarId(l, object1, j);
        arrayOfLong2[0] = l;
        paramInt1 = 1;
        paramArrayOfLong = arrayOfLong2;
      } 
    } 
  }
  
  public void getRootOid(Vector<Integer> paramVector) {
    if (this.nbChildren != 1)
      return; 
    paramVector.addElement(Integer.valueOf(this.varList[0]));
    ((SnmpMibNode)this.children.firstElement()).getRootOid(paramVector);
  }
  
  public void registerNode(String paramString, SnmpMibNode paramSnmpMibNode) throws IllegalAccessException {
    SnmpOid snmpOid = new SnmpOid(paramString);
    registerNode(snmpOid.longValue(), 0, paramSnmpMibNode);
  }
  
  void registerNode(long[] paramArrayOfLong, int paramInt, SnmpMibNode paramSnmpMibNode) throws IllegalAccessException {
    if (paramInt >= paramArrayOfLong.length)
      throw new IllegalAccessException(); 
    long l = paramArrayOfLong[paramInt];
    int i = retrieveIndex(l);
    if (i == this.nbChildren) {
      this.nbChildren++;
      this.varList = new int[this.nbChildren];
      this.varList[0] = (int)l;
      i = 0;
      if (paramInt + 1 == paramArrayOfLong.length) {
        this.children.insertElementAt(paramSnmpMibNode, i);
        return;
      } 
      SnmpMibOid snmpMibOid = new SnmpMibOid();
      this.children.insertElementAt(snmpMibOid, i);
      snmpMibOid.registerNode(paramArrayOfLong, paramInt + 1, paramSnmpMibNode);
      return;
    } 
    if (i == -1) {
      int[] arrayOfInt = new int[this.nbChildren + 1];
      arrayOfInt[this.nbChildren] = (int)l;
      System.arraycopy(this.varList, 0, arrayOfInt, 0, this.nbChildren);
      this.varList = arrayOfInt;
      this.nbChildren++;
      SnmpMibNode.sort(this.varList);
      int j = retrieveIndex(l);
      this.varList[j] = (int)l;
      if (paramInt + 1 == paramArrayOfLong.length) {
        this.children.insertElementAt(paramSnmpMibNode, j);
        return;
      } 
      SnmpMibOid snmpMibOid = new SnmpMibOid();
      this.children.insertElementAt(snmpMibOid, j);
      snmpMibOid.registerNode(paramArrayOfLong, paramInt + 1, paramSnmpMibNode);
    } else {
      SnmpMibNode snmpMibNode = (SnmpMibNode)this.children.elementAt(i);
      if (paramInt + 1 == paramArrayOfLong.length) {
        if (snmpMibNode == paramSnmpMibNode)
          return; 
        if (snmpMibNode != null && paramSnmpMibNode != null) {
          if (paramSnmpMibNode instanceof SnmpMibGroup) {
            ((SnmpMibOid)snmpMibNode).exportChildren((SnmpMibOid)paramSnmpMibNode);
            this.children.setElementAt(paramSnmpMibNode, i);
            return;
          } 
          if (paramSnmpMibNode instanceof SnmpMibOid && snmpMibNode instanceof SnmpMibGroup) {
            ((SnmpMibOid)paramSnmpMibNode).exportChildren((SnmpMibOid)snmpMibNode);
            return;
          } 
          if (paramSnmpMibNode instanceof SnmpMibOid) {
            ((SnmpMibOid)snmpMibNode).exportChildren((SnmpMibOid)paramSnmpMibNode);
            this.children.setElementAt(paramSnmpMibNode, i);
            return;
          } 
        } 
        this.children.setElementAt(paramSnmpMibNode, i);
      } else {
        if (snmpMibNode == null)
          throw new IllegalAccessException(); 
        ((SnmpMibOid)snmpMibNode).registerNode(paramArrayOfLong, paramInt + 1, paramSnmpMibNode);
      } 
    } 
  }
  
  void exportChildren(SnmpMibOid paramSnmpMibOid) throws IllegalAccessException {
    if (paramSnmpMibOid == null)
      return; 
    long[] arrayOfLong = new long[1];
    for (byte b = 0; b < this.nbChildren; b++) {
      SnmpMibNode snmpMibNode = (SnmpMibNode)this.children.elementAt(b);
      if (snmpMibNode != null) {
        arrayOfLong[0] = this.varList[b];
        paramSnmpMibOid.registerNode(arrayOfLong, 0, snmpMibNode);
      } 
    } 
  }
  
  SnmpMibNode getChild(long paramLong) throws SnmpStatusException {
    int i = getInsertAt(paramLong);
    if (i >= this.nbChildren)
      throw new SnmpStatusException(225); 
    if (this.varList[i] != (int)paramLong)
      throw new SnmpStatusException(225); 
    SnmpMibNode snmpMibNode = null;
    try {
      snmpMibNode = (SnmpMibNode)this.children.elementAtNonSync(i);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new SnmpStatusException(225);
    } 
    if (snmpMibNode == null)
      throw new SnmpStatusException(224); 
    return snmpMibNode;
  }
  
  private int retrieveIndex(long paramLong) {
    int i = 0;
    int j = (int)paramLong;
    if (this.varList == null || this.varList.length < 1)
      return this.nbChildren; 
    int k = this.varList.length - 1;
    int m;
    for (m = i + (k - i) / 2; i <= k; m = i + (k - i) / 2) {
      int n = this.varList[m];
      if (j == n)
        return m; 
      if (n < j) {
        i = m + 1;
      } else {
        k = m - 1;
      } 
    } 
    return -1;
  }
  
  private int getInsertAt(long paramLong) {
    int i = 0;
    int j = (int)paramLong;
    if (this.varList == null)
      return -1; 
    int k = this.varList.length - 1;
    int m;
    for (m = i + (k - i) / 2; i <= k; m = i + (k - i) / 2) {
      int n = this.varList[m];
      if (j == n)
        return m; 
      if (n < j) {
        i = m + 1;
      } else {
        k = m - 1;
      } 
    } 
    return m;
  }
  
  class NonSyncVector<E> extends Vector<E> {
    public NonSyncVector(int param1Int) { super(param1Int); }
    
    final void addNonSyncElement(E param1E) {
      ensureCapacity(this.elementCount + 1);
      this.elementData[this.elementCount++] = param1E;
    }
    
    final E elementAtNonSync(int param1Int) { return (E)this.elementData[param1Int]; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpMibOid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */