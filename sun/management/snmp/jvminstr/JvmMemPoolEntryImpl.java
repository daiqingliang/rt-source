package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Map;
import sun.management.snmp.jvmmib.EnumJvmMemPoolCollectThreshdSupport;
import sun.management.snmp.jvmmib.EnumJvmMemPoolState;
import sun.management.snmp.jvmmib.EnumJvmMemPoolThreshdSupport;
import sun.management.snmp.jvmmib.EnumJvmMemPoolType;
import sun.management.snmp.jvmmib.JvmMemPoolEntryMBean;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;

public class JvmMemPoolEntryImpl implements JvmMemPoolEntryMBean {
  protected final int jvmMemPoolIndex;
  
  static final String memoryTag = "jvmMemPoolEntry.getUsage";
  
  static final String peakMemoryTag = "jvmMemPoolEntry.getPeakUsage";
  
  static final String collectMemoryTag = "jvmMemPoolEntry.getCollectionUsage";
  
  static final MemoryUsage ZEROS = new MemoryUsage(0L, 0L, 0L, 0L);
  
  final String entryMemoryTag;
  
  final String entryPeakMemoryTag;
  
  final String entryCollectMemoryTag;
  
  final MemoryPoolMXBean pool;
  
  private long jvmMemPoolPeakReset = 0L;
  
  private static final EnumJvmMemPoolState JvmMemPoolStateValid = new EnumJvmMemPoolState("valid");
  
  private static final EnumJvmMemPoolState JvmMemPoolStateInvalid = new EnumJvmMemPoolState("invalid");
  
  private static final EnumJvmMemPoolType EnumJvmMemPoolTypeHeap = new EnumJvmMemPoolType("heap");
  
  private static final EnumJvmMemPoolType EnumJvmMemPoolTypeNonHeap = new EnumJvmMemPoolType("nonheap");
  
  private static final EnumJvmMemPoolThreshdSupport EnumJvmMemPoolThreshdSupported = new EnumJvmMemPoolThreshdSupport("supported");
  
  private static final EnumJvmMemPoolThreshdSupport EnumJvmMemPoolThreshdUnsupported = new EnumJvmMemPoolThreshdSupport("unsupported");
  
  private static final EnumJvmMemPoolCollectThreshdSupport EnumJvmMemPoolCollectThreshdSupported = new EnumJvmMemPoolCollectThreshdSupport("supported");
  
  private static final EnumJvmMemPoolCollectThreshdSupport EnumJvmMemPoolCollectThreshdUnsupported = new EnumJvmMemPoolCollectThreshdSupport("unsupported");
  
  static final MibLogger log = new MibLogger(JvmMemPoolEntryImpl.class);
  
  MemoryUsage getMemoryUsage() {
    try {
      Map map = JvmContextFactory.getUserData();
      if (map != null) {
        MemoryUsage memoryUsage1 = (MemoryUsage)map.get(this.entryMemoryTag);
        if (memoryUsage1 != null) {
          log.debug("getMemoryUsage", this.entryMemoryTag + " found in cache.");
          return memoryUsage1;
        } 
        MemoryUsage memoryUsage2 = this.pool.getUsage();
        if (memoryUsage2 == null)
          memoryUsage2 = ZEROS; 
        map.put(this.entryMemoryTag, memoryUsage2);
        return memoryUsage2;
      } 
      log.trace("getMemoryUsage", "ERROR: should never come here!");
      return this.pool.getUsage();
    } catch (RuntimeException runtimeException) {
      log.trace("getMemoryUsage", "Failed to get MemoryUsage: " + runtimeException);
      log.debug("getMemoryUsage", runtimeException);
      throw runtimeException;
    } 
  }
  
  MemoryUsage getPeakMemoryUsage() {
    try {
      Map map = JvmContextFactory.getUserData();
      if (map != null) {
        MemoryUsage memoryUsage1 = (MemoryUsage)map.get(this.entryPeakMemoryTag);
        if (memoryUsage1 != null) {
          if (log.isDebugOn())
            log.debug("getPeakMemoryUsage", this.entryPeakMemoryTag + " found in cache."); 
          return memoryUsage1;
        } 
        MemoryUsage memoryUsage2 = this.pool.getPeakUsage();
        if (memoryUsage2 == null)
          memoryUsage2 = ZEROS; 
        map.put(this.entryPeakMemoryTag, memoryUsage2);
        return memoryUsage2;
      } 
      log.trace("getPeakMemoryUsage", "ERROR: should never come here!");
      return ZEROS;
    } catch (RuntimeException runtimeException) {
      log.trace("getPeakMemoryUsage", "Failed to get MemoryUsage: " + runtimeException);
      log.debug("getPeakMemoryUsage", runtimeException);
      throw runtimeException;
    } 
  }
  
  MemoryUsage getCollectMemoryUsage() {
    try {
      Map map = JvmContextFactory.getUserData();
      if (map != null) {
        MemoryUsage memoryUsage1 = (MemoryUsage)map.get(this.entryCollectMemoryTag);
        if (memoryUsage1 != null) {
          if (log.isDebugOn())
            log.debug("getCollectMemoryUsage", this.entryCollectMemoryTag + " found in cache."); 
          return memoryUsage1;
        } 
        MemoryUsage memoryUsage2 = this.pool.getCollectionUsage();
        if (memoryUsage2 == null)
          memoryUsage2 = ZEROS; 
        map.put(this.entryCollectMemoryTag, memoryUsage2);
        return memoryUsage2;
      } 
      log.trace("getCollectMemoryUsage", "ERROR: should never come here!");
      return ZEROS;
    } catch (RuntimeException runtimeException) {
      log.trace("getPeakMemoryUsage", "Failed to get MemoryUsage: " + runtimeException);
      log.debug("getPeakMemoryUsage", runtimeException);
      throw runtimeException;
    } 
  }
  
  public JvmMemPoolEntryImpl(MemoryPoolMXBean paramMemoryPoolMXBean, int paramInt) {
    this.pool = paramMemoryPoolMXBean;
    this.jvmMemPoolIndex = paramInt;
    this.entryMemoryTag = "jvmMemPoolEntry.getUsage." + paramInt;
    this.entryPeakMemoryTag = "jvmMemPoolEntry.getPeakUsage." + paramInt;
    this.entryCollectMemoryTag = "jvmMemPoolEntry.getCollectionUsage." + paramInt;
  }
  
  public Long getJvmMemPoolMaxSize() throws SnmpStatusException {
    long l = getMemoryUsage().getMax();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolUsed() throws SnmpStatusException {
    long l = getMemoryUsage().getUsed();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolInitSize() throws SnmpStatusException {
    long l = getMemoryUsage().getInit();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCommitted() throws SnmpStatusException {
    long l = getMemoryUsage().getCommitted();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolPeakMaxSize() throws SnmpStatusException {
    long l = getPeakMemoryUsage().getMax();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolPeakUsed() throws SnmpStatusException {
    long l = getPeakMemoryUsage().getUsed();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolPeakCommitted() throws SnmpStatusException {
    long l = getPeakMemoryUsage().getCommitted();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCollectMaxSize() throws SnmpStatusException {
    long l = getCollectMemoryUsage().getMax();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCollectUsed() throws SnmpStatusException {
    long l = getCollectMemoryUsage().getUsed();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCollectCommitted() throws SnmpStatusException {
    long l = getCollectMemoryUsage().getCommitted();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolThreshold() throws SnmpStatusException {
    if (!this.pool.isUsageThresholdSupported())
      return JvmMemoryImpl.Long0; 
    long l = this.pool.getUsageThreshold();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public void setJvmMemPoolThreshold(Long paramLong) throws SnmpStatusException {
    long l = paramLong.longValue();
    if (l < 0L)
      throw new SnmpStatusException(10); 
    this.pool.setUsageThreshold(l);
  }
  
  public void checkJvmMemPoolThreshold(Long paramLong) throws SnmpStatusException {
    if (!this.pool.isUsageThresholdSupported())
      throw new SnmpStatusException(12); 
    long l = paramLong.longValue();
    if (l < 0L)
      throw new SnmpStatusException(10); 
  }
  
  public EnumJvmMemPoolThreshdSupport getJvmMemPoolThreshdSupport() throws SnmpStatusException { return this.pool.isUsageThresholdSupported() ? EnumJvmMemPoolThreshdSupported : EnumJvmMemPoolThreshdUnsupported; }
  
  public Long getJvmMemPoolThreshdCount() throws SnmpStatusException {
    if (!this.pool.isUsageThresholdSupported())
      return JvmMemoryImpl.Long0; 
    long l = this.pool.getUsageThresholdCount();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public Long getJvmMemPoolCollectThreshold() throws SnmpStatusException {
    if (!this.pool.isCollectionUsageThresholdSupported())
      return JvmMemoryImpl.Long0; 
    long l = this.pool.getCollectionUsageThreshold();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public void setJvmMemPoolCollectThreshold(Long paramLong) throws SnmpStatusException {
    long l = paramLong.longValue();
    if (l < 0L)
      throw new SnmpStatusException(10); 
    this.pool.setCollectionUsageThreshold(l);
  }
  
  public void checkJvmMemPoolCollectThreshold(Long paramLong) throws SnmpStatusException {
    if (!this.pool.isCollectionUsageThresholdSupported())
      throw new SnmpStatusException(12); 
    long l = paramLong.longValue();
    if (l < 0L)
      throw new SnmpStatusException(10); 
  }
  
  public EnumJvmMemPoolCollectThreshdSupport getJvmMemPoolCollectThreshdSupport() throws SnmpStatusException { return this.pool.isCollectionUsageThresholdSupported() ? EnumJvmMemPoolCollectThreshdSupported : EnumJvmMemPoolCollectThreshdUnsupported; }
  
  public Long getJvmMemPoolCollectThreshdCount() throws SnmpStatusException {
    if (!this.pool.isCollectionUsageThresholdSupported())
      return JvmMemoryImpl.Long0; 
    long l = this.pool.getCollectionUsageThresholdCount();
    return (l > -1L) ? new Long(l) : JvmMemoryImpl.Long0;
  }
  
  public static EnumJvmMemPoolType jvmMemPoolType(MemoryType paramMemoryType) throws SnmpStatusException {
    if (paramMemoryType.equals(MemoryType.HEAP))
      return EnumJvmMemPoolTypeHeap; 
    if (paramMemoryType.equals(MemoryType.NON_HEAP))
      return EnumJvmMemPoolTypeNonHeap; 
    throw new SnmpStatusException(10);
  }
  
  public EnumJvmMemPoolType getJvmMemPoolType() throws SnmpStatusException { return jvmMemPoolType(this.pool.getType()); }
  
  public String getJvmMemPoolName() throws SnmpStatusException { return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(this.pool.getName()); }
  
  public Integer getJvmMemPoolIndex() throws SnmpStatusException { return new Integer(this.jvmMemPoolIndex); }
  
  public EnumJvmMemPoolState getJvmMemPoolState() throws SnmpStatusException { return this.pool.isValid() ? JvmMemPoolStateValid : JvmMemPoolStateInvalid; }
  
  public Long getJvmMemPoolPeakReset() throws SnmpStatusException { return new Long(this.jvmMemPoolPeakReset); }
  
  public void setJvmMemPoolPeakReset(Long paramLong) throws SnmpStatusException {
    long l = paramLong.longValue();
    if (l > this.jvmMemPoolPeakReset) {
      long l1 = System.currentTimeMillis();
      this.pool.resetPeakUsage();
      this.jvmMemPoolPeakReset = l1;
      log.debug("setJvmMemPoolPeakReset", "jvmMemPoolPeakReset=" + l1);
    } 
  }
  
  public void checkJvmMemPoolPeakReset(Long paramLong) throws SnmpStatusException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmMemPoolEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */