package com.sun.management;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import jdk.Exported;
import sun.management.GarbageCollectionNotifInfoCompositeData;

@Exported
public class GarbageCollectionNotificationInfo implements CompositeDataView {
  private final String gcName;
  
  private final String gcAction;
  
  private final String gcCause;
  
  private final GcInfo gcInfo;
  
  private final CompositeData cdata;
  
  public static final String GARBAGE_COLLECTION_NOTIFICATION = "com.sun.management.gc.notification";
  
  public GarbageCollectionNotificationInfo(String paramString1, String paramString2, String paramString3, GcInfo paramGcInfo) {
    if (paramString1 == null)
      throw new NullPointerException("Null gcName"); 
    if (paramString2 == null)
      throw new NullPointerException("Null gcAction"); 
    if (paramString3 == null)
      throw new NullPointerException("Null gcCause"); 
    this.gcName = paramString1;
    this.gcAction = paramString2;
    this.gcCause = paramString3;
    this.gcInfo = paramGcInfo;
    this.cdata = new GarbageCollectionNotifInfoCompositeData(this);
  }
  
  GarbageCollectionNotificationInfo(CompositeData paramCompositeData) {
    GarbageCollectionNotifInfoCompositeData.validateCompositeData(paramCompositeData);
    this.gcName = GarbageCollectionNotifInfoCompositeData.getGcName(paramCompositeData);
    this.gcAction = GarbageCollectionNotifInfoCompositeData.getGcAction(paramCompositeData);
    this.gcCause = GarbageCollectionNotifInfoCompositeData.getGcCause(paramCompositeData);
    this.gcInfo = GarbageCollectionNotifInfoCompositeData.getGcInfo(paramCompositeData);
    this.cdata = paramCompositeData;
  }
  
  public String getGcName() { return this.gcName; }
  
  public String getGcAction() { return this.gcAction; }
  
  public String getGcCause() { return this.gcCause; }
  
  public GcInfo getGcInfo() { return this.gcInfo; }
  
  public static GarbageCollectionNotificationInfo from(CompositeData paramCompositeData) { return (paramCompositeData == null) ? null : ((paramCompositeData instanceof GarbageCollectionNotifInfoCompositeData) ? ((GarbageCollectionNotifInfoCompositeData)paramCompositeData).getGarbageCollectionNotifInfo() : new GarbageCollectionNotificationInfo(paramCompositeData)); }
  
  public CompositeData toCompositeData(CompositeType paramCompositeType) { return this.cdata; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\GarbageCollectionNotificationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */