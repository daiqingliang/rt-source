package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.JvmThreadInstanceTableMeta;
import sun.management.snmp.jvmmib.JvmThreadingMeta;

public class JvmThreadingMetaImpl extends JvmThreadingMeta {
  static final long serialVersionUID = -2104788458393251457L;
  
  public JvmThreadingMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) { super(paramSnmpMib, paramSnmpStandardObjectServer); }
  
  protected JvmThreadInstanceTableMeta createJvmThreadInstanceTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmThreadInstanceTableMetaImpl(paramSnmpMib, this.objectserver); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmThreadingMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */