package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.lang.management.GarbageCollectorMXBean;
import sun.management.snmp.jvmmib.JvmMemGCEntryMBean;

public class JvmMemGCEntryImpl implements JvmMemGCEntryMBean {
  protected final int JvmMemManagerIndex;
  
  protected final GarbageCollectorMXBean gcm;
  
  public JvmMemGCEntryImpl(GarbageCollectorMXBean paramGarbageCollectorMXBean, int paramInt) {
    this.gcm = paramGarbageCollectorMXBean;
    this.JvmMemManagerIndex = paramInt;
  }
  
  public Long getJvmMemGCTimeMs() throws SnmpStatusException { return new Long(this.gcm.getCollectionTime()); }
  
  public Long getJvmMemGCCount() throws SnmpStatusException { return new Long(this.gcm.getCollectionCount()); }
  
  public Integer getJvmMemManagerIndex() throws SnmpStatusException { return new Integer(this.JvmMemManagerIndex); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmMemGCEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */