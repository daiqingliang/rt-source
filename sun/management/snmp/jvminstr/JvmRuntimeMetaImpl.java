package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.JvmRTBootClassPathTableMeta;
import sun.management.snmp.jvmmib.JvmRTClassPathTableMeta;
import sun.management.snmp.jvmmib.JvmRTInputArgsTableMeta;
import sun.management.snmp.jvmmib.JvmRTLibraryPathTableMeta;
import sun.management.snmp.jvmmib.JvmRuntimeMeta;

public class JvmRuntimeMetaImpl extends JvmRuntimeMeta {
  static final long serialVersionUID = -6570428414857608618L;
  
  public JvmRuntimeMetaImpl(SnmpMib paramSnmpMib, SnmpStandardObjectServer paramSnmpStandardObjectServer) { super(paramSnmpMib, paramSnmpStandardObjectServer); }
  
  protected JvmRTInputArgsTableMeta createJvmRTInputArgsTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmRTInputArgsTableMetaImpl(paramSnmpMib, this.objectserver); }
  
  protected JvmRTLibraryPathTableMeta createJvmRTLibraryPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmRTLibraryPathTableMetaImpl(paramSnmpMib, this.objectserver); }
  
  protected JvmRTClassPathTableMeta createJvmRTClassPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmRTClassPathTableMetaImpl(paramSnmpMib, this.objectserver); }
  
  protected JvmRTBootClassPathTableMeta createJvmRTBootClassPathTableMetaNode(String paramString1, String paramString2, SnmpMib paramSnmpMib, MBeanServer paramMBeanServer) { return new JvmRTBootClassPathTableMetaImpl(paramSnmpMib, this.objectserver); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmRuntimeMetaImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */