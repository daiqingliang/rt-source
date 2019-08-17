package com.sun.management;

import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import jdk.Exported;
import sun.management.GcInfoBuilder;
import sun.management.GcInfoCompositeData;

@Exported
public class GcInfo implements CompositeData, CompositeDataView {
  private final long index;
  
  private final long startTime;
  
  private final long endTime;
  
  private final Map<String, MemoryUsage> usageBeforeGc;
  
  private final Map<String, MemoryUsage> usageAfterGc;
  
  private final Object[] extAttributes;
  
  private final CompositeData cdata;
  
  private final GcInfoBuilder builder;
  
  private GcInfo(GcInfoBuilder paramGcInfoBuilder, long paramLong1, long paramLong2, long paramLong3, MemoryUsage[] paramArrayOfMemoryUsage1, MemoryUsage[] paramArrayOfMemoryUsage2, Object[] paramArrayOfObject) {
    this.builder = paramGcInfoBuilder;
    this.index = paramLong1;
    this.startTime = paramLong2;
    this.endTime = paramLong3;
    String[] arrayOfString = paramGcInfoBuilder.getPoolNames();
    this.usageBeforeGc = new HashMap(arrayOfString.length);
    this.usageAfterGc = new HashMap(arrayOfString.length);
    for (byte b = 0; b < arrayOfString.length; b++) {
      this.usageBeforeGc.put(arrayOfString[b], paramArrayOfMemoryUsage1[b]);
      this.usageAfterGc.put(arrayOfString[b], paramArrayOfMemoryUsage2[b]);
    } 
    this.extAttributes = paramArrayOfObject;
    this.cdata = new GcInfoCompositeData(this, paramGcInfoBuilder, paramArrayOfObject);
  }
  
  private GcInfo(CompositeData paramCompositeData) {
    GcInfoCompositeData.validateCompositeData(paramCompositeData);
    this.index = GcInfoCompositeData.getId(paramCompositeData);
    this.startTime = GcInfoCompositeData.getStartTime(paramCompositeData);
    this.endTime = GcInfoCompositeData.getEndTime(paramCompositeData);
    this.usageBeforeGc = GcInfoCompositeData.getMemoryUsageBeforeGc(paramCompositeData);
    this.usageAfterGc = GcInfoCompositeData.getMemoryUsageAfterGc(paramCompositeData);
    this.extAttributes = null;
    this.builder = null;
    this.cdata = paramCompositeData;
  }
  
  public long getId() { return this.index; }
  
  public long getStartTime() { return this.startTime; }
  
  public long getEndTime() { return this.endTime; }
  
  public long getDuration() { return this.endTime - this.startTime; }
  
  public Map<String, MemoryUsage> getMemoryUsageBeforeGc() { return Collections.unmodifiableMap(this.usageBeforeGc); }
  
  public Map<String, MemoryUsage> getMemoryUsageAfterGc() { return Collections.unmodifiableMap(this.usageAfterGc); }
  
  public static GcInfo from(CompositeData paramCompositeData) { return (paramCompositeData == null) ? null : ((paramCompositeData instanceof GcInfoCompositeData) ? ((GcInfoCompositeData)paramCompositeData).getGcInfo() : new GcInfo(paramCompositeData)); }
  
  public boolean containsKey(String paramString) { return this.cdata.containsKey(paramString); }
  
  public boolean containsValue(Object paramObject) { return this.cdata.containsValue(paramObject); }
  
  public boolean equals(Object paramObject) { return this.cdata.equals(paramObject); }
  
  public Object get(String paramString) { return this.cdata.get(paramString); }
  
  public Object[] getAll(String[] paramArrayOfString) { return this.cdata.getAll(paramArrayOfString); }
  
  public CompositeType getCompositeType() { return this.cdata.getCompositeType(); }
  
  public int hashCode() { return this.cdata.hashCode(); }
  
  public String toString() { return this.cdata.toString(); }
  
  public Collection values() { return this.cdata.values(); }
  
  public CompositeData toCompositeData(CompositeType paramCompositeType) { return this.cdata; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\GcInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */