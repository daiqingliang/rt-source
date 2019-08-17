package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import sun.management.snmp.jvmmib.JvmRTInputArgsEntryMBean;

public class JvmRTInputArgsEntryImpl implements JvmRTInputArgsEntryMBean, Serializable {
  static final long serialVersionUID = 1000306518436503395L;
  
  private final String item;
  
  private final int index;
  
  public JvmRTInputArgsEntryImpl(String paramString, int paramInt) {
    this.item = validArgValueTC(paramString);
    this.index = paramInt;
  }
  
  private String validArgValueTC(String paramString) { return JVM_MANAGEMENT_MIB_IMPL.validArgValueTC(paramString); }
  
  public String getJvmRTInputArgsItem() throws SnmpStatusException { return this.item; }
  
  public Integer getJvmRTInputArgsIndex() throws SnmpStatusException { return new Integer(this.index); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmRTInputArgsEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */