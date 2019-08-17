package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Vector;

public abstract class SnmpMibNode implements Serializable {
  protected int[] varList;
  
  public long getNextVarId(long paramLong, Object paramObject) throws SnmpStatusException { return getNextIdentifier(this.varList, paramLong); }
  
  public long getNextVarId(long paramLong, Object paramObject, int paramInt) throws SnmpStatusException {
    long l = paramLong;
    do {
      l = getNextVarId(l, paramObject);
    } while (skipVariable(l, paramObject, paramInt));
    return l;
  }
  
  protected boolean skipVariable(long paramLong, Object paramObject, int paramInt) { return false; }
  
  void findHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt, SnmpRequestTree paramSnmpRequestTree) throws SnmpStatusException { throw new SnmpStatusException(225); }
  
  long[] findNextHandlingNode(SnmpVarBind paramSnmpVarBind, long[] paramArrayOfLong, int paramInt1, int paramInt2, SnmpRequestTree paramSnmpRequestTree, AcmChecker paramAcmChecker) throws SnmpStatusException { throw new SnmpStatusException(225); }
  
  public abstract void get(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
  
  public abstract void set(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
  
  public abstract void check(SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException;
  
  public static void sort(int[] paramArrayOfInt) { QuickSort(paramArrayOfInt, 0, paramArrayOfInt.length - 1); }
  
  public void getRootOid(Vector<Integer> paramVector) {}
  
  static void QuickSort(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramInt1;
    int j = paramInt2;
    if (paramInt2 > paramInt1) {
      int k = paramArrayOfInt[(paramInt1 + paramInt2) / 2];
      while (i <= j) {
        while (i < paramInt2 && paramArrayOfInt[i] < k)
          i++; 
        while (j > paramInt1 && paramArrayOfInt[j] > k)
          j--; 
        if (i <= j) {
          swap(paramArrayOfInt, i, j);
          i++;
          j--;
        } 
      } 
      if (paramInt1 < j)
        QuickSort(paramArrayOfInt, paramInt1, j); 
      if (i < paramInt2)
        QuickSort(paramArrayOfInt, i, paramInt2); 
    } 
  }
  
  protected static final int getNextIdentifier(int[] paramArrayOfInt, long paramLong) throws SnmpStatusException {
    int[] arrayOfInt = paramArrayOfInt;
    int i = (int)paramLong;
    if (arrayOfInt == null)
      throw new SnmpStatusException(225); 
    int j = 0;
    int k = arrayOfInt.length;
    int m = j + (k - j) / 2;
    int n = 0;
    if (k < 1)
      throw new SnmpStatusException(225); 
    if (arrayOfInt[k - 1] <= i)
      throw new SnmpStatusException(225); 
    while (j <= k) {
      n = arrayOfInt[m];
      if (i == n)
        return arrayOfInt[++m]; 
      if (n < i) {
        j = m + 1;
      } else {
        k = m - 1;
      } 
      m = j + (k - j) / 2;
    } 
    return arrayOfInt[m];
  }
  
  private static final void swap(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramArrayOfInt[paramInt1];
    paramArrayOfInt[paramInt1] = paramArrayOfInt[paramInt2];
    paramArrayOfInt[paramInt2] = i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpMibNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */