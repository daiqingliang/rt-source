package sun.management.snmp.util;

import com.sun.jmx.snmp.SnmpOid;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

public class SnmpCachedData implements SnmpTableHandler {
  public static final Comparator<SnmpOid> oidComparator = new Comparator<SnmpOid>() {
      public int compare(SnmpOid param1SnmpOid1, SnmpOid param1SnmpOid2) { return param1SnmpOid1.compareTo(param1SnmpOid2); }
      
      public boolean equals(Object param1Object1, Object param1Object2) { return (param1Object1 == param1Object2) ? true : param1Object1.equals(param1Object2); }
    };
  
  public final long lastUpdated;
  
  public final SnmpOid[] indexes;
  
  public final Object[] datas;
  
  public SnmpCachedData(long paramLong, SnmpOid[] paramArrayOfSnmpOid, Object[] paramArrayOfObject) {
    this.lastUpdated = paramLong;
    this.indexes = paramArrayOfSnmpOid;
    this.datas = paramArrayOfObject;
  }
  
  public SnmpCachedData(long paramLong, TreeMap<SnmpOid, Object> paramTreeMap) { this(paramLong, paramTreeMap, true); }
  
  public SnmpCachedData(long paramLong, TreeMap<SnmpOid, Object> paramTreeMap, boolean paramBoolean) {
    int i = paramTreeMap.size();
    this.lastUpdated = paramLong;
    this.indexes = new SnmpOid[i];
    this.datas = new Object[i];
    if (paramBoolean) {
      paramTreeMap.keySet().toArray(this.indexes);
      paramTreeMap.values().toArray(this.datas);
    } else {
      paramTreeMap.values().toArray(this.datas);
    } 
  }
  
  public final int find(SnmpOid paramSnmpOid) { return Arrays.binarySearch(this.indexes, paramSnmpOid, oidComparator); }
  
  public Object getData(SnmpOid paramSnmpOid) {
    int i = find(paramSnmpOid);
    return (i < 0 || i >= this.datas.length) ? null : this.datas[i];
  }
  
  public SnmpOid getNext(SnmpOid paramSnmpOid) {
    if (paramSnmpOid == null)
      return (this.indexes.length > 0) ? this.indexes[0] : null; 
    int i = find(paramSnmpOid);
    if (i > -1)
      return (i < this.indexes.length - 1) ? this.indexes[i + 1] : null; 
    int j = -i - 1;
    return (j > -1 && j < this.indexes.length) ? this.indexes[j] : null;
  }
  
  public boolean contains(SnmpOid paramSnmpOid) {
    int i = find(paramSnmpOid);
    return (i > -1 && i < this.indexes.length);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snm\\util\SnmpCachedData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */