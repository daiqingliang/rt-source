package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTBootClassPathEntryMBean;

public class JvmRTBootClassPathEntryImpl implements JvmRTBootClassPathEntryMBean, Serializable {
  static final long serialVersionUID = -2282652055235913013L;
  
  private final String item;
  
  private final int index;
  
  public JvmRTBootClassPathEntryImpl(String paramString, int paramInt) {
    this.item = validPathElementTC(paramString);
    this.index = paramInt;
  }
  
  private String validPathElementTC(String paramString) { return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString); }
  
  public String getJvmRTBootClassPathItem() throws SnmpStatusException { return this.item; }
  
  public Integer getJvmRTBootClassPathIndex() throws SnmpStatusException { return new Integer(this.index); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmRTBootClassPathEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */