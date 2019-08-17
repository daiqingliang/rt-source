package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTClassPathEntryMBean;

public class JvmRTClassPathEntryImpl implements JvmRTClassPathEntryMBean, Serializable {
  static final long serialVersionUID = 8524792845083365742L;
  
  private final String item;
  
  private final int index;
  
  public JvmRTClassPathEntryImpl(String paramString, int paramInt) {
    this.item = validPathElementTC(paramString);
    this.index = paramInt;
  }
  
  private String validPathElementTC(String paramString) { return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString); }
  
  public String getJvmRTClassPathItem() throws SnmpStatusException { return this.item; }
  
  public Integer getJvmRTClassPathIndex() throws SnmpStatusException { return new Integer(this.index); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmRTClassPathEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */