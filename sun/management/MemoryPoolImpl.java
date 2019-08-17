package sun.management;

import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import javax.management.ObjectName;

class MemoryPoolImpl implements MemoryPoolMXBean {
  private final String name;
  
  private final boolean isHeap;
  
  private final boolean isValid;
  
  private final boolean collectionThresholdSupported;
  
  private final boolean usageThresholdSupported;
  
  private MemoryManagerMXBean[] managers;
  
  private long usageThreshold;
  
  private long collectionThreshold;
  
  private boolean usageSensorRegistered;
  
  private boolean gcSensorRegistered;
  
  private Sensor usageSensor;
  
  private Sensor gcSensor;
  
  MemoryPoolImpl(String paramString, boolean paramBoolean, long paramLong1, long paramLong2) {
    this.name = paramString;
    this.isHeap = paramBoolean;
    this.isValid = true;
    this.managers = null;
    this.usageThreshold = paramLong1;
    this.collectionThreshold = paramLong2;
    this.usageThresholdSupported = (paramLong1 >= 0L);
    this.collectionThresholdSupported = (paramLong2 >= 0L);
    this.usageSensor = new PoolSensor(this, paramString + " usage sensor");
    this.gcSensor = new CollectionSensor(this, paramString + " collection sensor");
    this.usageSensorRegistered = false;
    this.gcSensorRegistered = false;
  }
  
  public String getName() { return this.name; }
  
  public boolean isValid() { return this.isValid; }
  
  public MemoryType getType() { return this.isHeap ? MemoryType.HEAP : MemoryType.NON_HEAP; }
  
  public MemoryUsage getUsage() { return getUsage0(); }
  
  public MemoryUsage getPeakUsage() { return getPeakUsage0(); }
  
  public long getUsageThreshold() {
    if (!isUsageThresholdSupported())
      throw new UnsupportedOperationException("Usage threshold is not supported"); 
    return this.usageThreshold;
  }
  
  public void setUsageThreshold(long paramLong) {
    if (!isUsageThresholdSupported())
      throw new UnsupportedOperationException("Usage threshold is not supported"); 
    Util.checkControlAccess();
    MemoryUsage memoryUsage = getUsage0();
    if (paramLong < 0L)
      throw new IllegalArgumentException("Invalid threshold: " + paramLong); 
    if (memoryUsage.getMax() != -1L && paramLong > memoryUsage.getMax())
      throw new IllegalArgumentException("Invalid threshold: " + paramLong + " must be <= maxSize. Committed = " + memoryUsage.getCommitted() + " Max = " + memoryUsage.getMax()); 
    synchronized (this) {
      if (!this.usageSensorRegistered) {
        this.usageSensorRegistered = true;
        setPoolUsageSensor(this.usageSensor);
      } 
      setUsageThreshold0(this.usageThreshold, paramLong);
      this.usageThreshold = paramLong;
    } 
  }
  
  private MemoryManagerMXBean[] getMemoryManagers() {
    if (this.managers == null)
      this.managers = getMemoryManagers0(); 
    return this.managers;
  }
  
  public String[] getMemoryManagerNames() {
    MemoryManagerMXBean[] arrayOfMemoryManagerMXBean = getMemoryManagers();
    String[] arrayOfString = new String[arrayOfMemoryManagerMXBean.length];
    for (byte b = 0; b < arrayOfMemoryManagerMXBean.length; b++)
      arrayOfString[b] = arrayOfMemoryManagerMXBean[b].getName(); 
    return arrayOfString;
  }
  
  public void resetPeakUsage() {
    Util.checkControlAccess();
    synchronized (this) {
      resetPeakUsage0();
    } 
  }
  
  public boolean isUsageThresholdExceeded() {
    if (!isUsageThresholdSupported())
      throw new UnsupportedOperationException("Usage threshold is not supported"); 
    if (this.usageThreshold == 0L)
      return false; 
    MemoryUsage memoryUsage = getUsage0();
    return (memoryUsage.getUsed() >= this.usageThreshold || this.usageSensor.isOn());
  }
  
  public long getUsageThresholdCount() {
    if (!isUsageThresholdSupported())
      throw new UnsupportedOperationException("Usage threshold is not supported"); 
    return this.usageSensor.getCount();
  }
  
  public boolean isUsageThresholdSupported() { return this.usageThresholdSupported; }
  
  public long getCollectionUsageThreshold() {
    if (!isCollectionUsageThresholdSupported())
      throw new UnsupportedOperationException("CollectionUsage threshold is not supported"); 
    return this.collectionThreshold;
  }
  
  public void setCollectionUsageThreshold(long paramLong) {
    if (!isCollectionUsageThresholdSupported())
      throw new UnsupportedOperationException("CollectionUsage threshold is not supported"); 
    Util.checkControlAccess();
    MemoryUsage memoryUsage = getUsage0();
    if (paramLong < 0L)
      throw new IllegalArgumentException("Invalid threshold: " + paramLong); 
    if (memoryUsage.getMax() != -1L && paramLong > memoryUsage.getMax())
      throw new IllegalArgumentException("Invalid threshold: " + paramLong + " > max (" + memoryUsage.getMax() + ")."); 
    synchronized (this) {
      if (!this.gcSensorRegistered) {
        this.gcSensorRegistered = true;
        setPoolCollectionSensor(this.gcSensor);
      } 
      setCollectionThreshold0(this.collectionThreshold, paramLong);
      this.collectionThreshold = paramLong;
    } 
  }
  
  public boolean isCollectionUsageThresholdExceeded() {
    if (!isCollectionUsageThresholdSupported())
      throw new UnsupportedOperationException("CollectionUsage threshold is not supported"); 
    if (this.collectionThreshold == 0L)
      return false; 
    MemoryUsage memoryUsage = getCollectionUsage0();
    return (this.gcSensor.isOn() || (memoryUsage != null && memoryUsage.getUsed() >= this.collectionThreshold));
  }
  
  public long getCollectionUsageThresholdCount() {
    if (!isCollectionUsageThresholdSupported())
      throw new UnsupportedOperationException("CollectionUsage threshold is not supported"); 
    return this.gcSensor.getCount();
  }
  
  public MemoryUsage getCollectionUsage() { return getCollectionUsage0(); }
  
  public boolean isCollectionUsageThresholdSupported() { return this.collectionThresholdSupported; }
  
  private native MemoryUsage getUsage0();
  
  private native MemoryUsage getPeakUsage0();
  
  private native MemoryUsage getCollectionUsage0();
  
  private native void setUsageThreshold0(long paramLong1, long paramLong2);
  
  private native void setCollectionThreshold0(long paramLong1, long paramLong2);
  
  private native void resetPeakUsage0();
  
  private native MemoryManagerMXBean[] getMemoryManagers0();
  
  private native void setPoolUsageSensor(Sensor paramSensor);
  
  private native void setPoolCollectionSensor(Sensor paramSensor);
  
  public ObjectName getObjectName() { return Util.newObjectName("java.lang:type=MemoryPool", getName()); }
  
  class CollectionSensor extends Sensor {
    MemoryPoolImpl pool;
    
    CollectionSensor(MemoryPoolImpl param1MemoryPoolImpl1, String param1String) {
      super(param1String);
      this.pool = param1MemoryPoolImpl1;
    }
    
    void triggerAction(MemoryUsage param1MemoryUsage) { MemoryImpl.createNotification("java.management.memory.collection.threshold.exceeded", this.pool.getName(), param1MemoryUsage, MemoryPoolImpl.this.gcSensor.getCount()); }
    
    void triggerAction() {}
    
    void clearAction() {}
  }
  
  class PoolSensor extends Sensor {
    MemoryPoolImpl pool;
    
    PoolSensor(MemoryPoolImpl param1MemoryPoolImpl1, String param1String) {
      super(param1String);
      this.pool = param1MemoryPoolImpl1;
    }
    
    void triggerAction(MemoryUsage param1MemoryUsage) { MemoryImpl.createNotification("java.management.memory.threshold.exceeded", this.pool.getName(), param1MemoryUsage, getCount()); }
    
    void triggerAction() {}
    
    void clearAction() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\MemoryPoolImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */