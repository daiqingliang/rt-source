package sun.management.snmp.util;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public abstract class SnmpTableCache implements Serializable {
  protected long validity;
  
  protected WeakReference<SnmpCachedData> datas;
  
  protected boolean isObsolete(SnmpCachedData paramSnmpCachedData) { return (paramSnmpCachedData == null) ? true : ((this.validity < 0L) ? false : ((System.currentTimeMillis() - paramSnmpCachedData.lastUpdated > this.validity))); }
  
  protected SnmpCachedData getCachedDatas() {
    if (this.datas == null)
      return null; 
    SnmpCachedData snmpCachedData = (SnmpCachedData)this.datas.get();
    return (snmpCachedData == null || isObsolete(snmpCachedData)) ? null : snmpCachedData;
  }
  
  protected SnmpCachedData getTableDatas(Object paramObject) {
    SnmpCachedData snmpCachedData1 = getCachedDatas();
    if (snmpCachedData1 != null)
      return snmpCachedData1; 
    SnmpCachedData snmpCachedData2 = updateCachedDatas(paramObject);
    if (this.validity != 0L)
      this.datas = new WeakReference(snmpCachedData2); 
    return snmpCachedData2;
  }
  
  protected abstract SnmpCachedData updateCachedDatas(Object paramObject);
  
  public abstract SnmpTableHandler getTableHandler();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snm\\util\SnmpTableCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */