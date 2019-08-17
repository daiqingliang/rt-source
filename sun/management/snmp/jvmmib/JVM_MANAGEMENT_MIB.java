package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import java.io.Serializable;
import java.util.Hashtable;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract class JVM_MANAGEMENT_MIB extends SnmpMib implements Serializable {
  static final long serialVersionUID = 6895037919735816732L;
  
  private boolean isInitialized = false;
  
  protected SnmpStandardObjectServer objectserver;
  
  protected final Hashtable<String, SnmpMibTable> metadatas = new Hashtable();
  
  public void init() {
    if (this.isInitialized == true)
      return; 
    try {
      populate(null, null);
    } catch (IllegalAccessException illegalAccessException) {
      throw illegalAccessException;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      throw new Error(exception.getMessage());
    } 
    this.isInitialized = true;
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    if (this.isInitialized == true)
      throw new InstanceAlreadyExistsException(); 
    this.server = paramMBeanServer;
    populate(paramMBeanServer, paramObjectName);
    this.isInitialized = true;
    return paramObjectName;
  }
  
  public void populate(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    if (this.isInitialized == true)
      return; 
    if (this.objectserver == null)
      this.objectserver = new SnmpStandardObjectServer(); 
    initJvmOS(paramMBeanServer);
    initJvmCompilation(paramMBeanServer);
    initJvmRuntime(paramMBeanServer);
    initJvmThreading(paramMBeanServer);
    initJvmMemory(paramMBeanServer);
    initJvmClassLoading(paramMBeanServer);
    this.isInitialized = true;
  }
  
  protected void initJvmOS(MBeanServer paramMBeanServer) throws Exception {
    String str = getGroupOid("JvmOS", "1.3.6.1.4.1.42.2.145.3.163.1.1.6");
    ObjectName objectName = null;
    if (paramMBeanServer != null)
      objectName = getGroupObjectName("JvmOS", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmOS"); 
    JvmOSMeta jvmOSMeta = createJvmOSMetaNode("JvmOS", str, objectName, paramMBeanServer);
    if (jvmOSMeta != null) {
      jvmOSMeta.registerTableNodes(this, paramMBeanServer);
      JvmOSMBean jvmOSMBean = (JvmOSMBean)createJvmOSMBean("JvmOS", str, objectName, paramMBeanServer);
      jvmOSMeta.setInstance(jvmOSMBean);
      registerGroupNode("JvmOS", str, objectName, jvmOSMeta, jvmOSMBean, paramMBeanServer);
    } 
  }
  
  protected JvmOSMeta createJvmOSMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmOSMeta(this, this.objectserver); }
  
  protected abstract Object createJvmOSMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmCompilation(MBeanServer paramMBeanServer) throws Exception {
    String str = getGroupOid("JvmCompilation", "1.3.6.1.4.1.42.2.145.3.163.1.1.5");
    ObjectName objectName = null;
    if (paramMBeanServer != null)
      objectName = getGroupObjectName("JvmCompilation", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmCompilation"); 
    JvmCompilationMeta jvmCompilationMeta = createJvmCompilationMetaNode("JvmCompilation", str, objectName, paramMBeanServer);
    if (jvmCompilationMeta != null) {
      jvmCompilationMeta.registerTableNodes(this, paramMBeanServer);
      JvmCompilationMBean jvmCompilationMBean = (JvmCompilationMBean)createJvmCompilationMBean("JvmCompilation", str, objectName, paramMBeanServer);
      jvmCompilationMeta.setInstance(jvmCompilationMBean);
      registerGroupNode("JvmCompilation", str, objectName, jvmCompilationMeta, jvmCompilationMBean, paramMBeanServer);
    } 
  }
  
  protected JvmCompilationMeta createJvmCompilationMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmCompilationMeta(this, this.objectserver); }
  
  protected abstract Object createJvmCompilationMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmRuntime(MBeanServer paramMBeanServer) throws Exception {
    String str = getGroupOid("JvmRuntime", "1.3.6.1.4.1.42.2.145.3.163.1.1.4");
    ObjectName objectName = null;
    if (paramMBeanServer != null)
      objectName = getGroupObjectName("JvmRuntime", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmRuntime"); 
    JvmRuntimeMeta jvmRuntimeMeta = createJvmRuntimeMetaNode("JvmRuntime", str, objectName, paramMBeanServer);
    if (jvmRuntimeMeta != null) {
      jvmRuntimeMeta.registerTableNodes(this, paramMBeanServer);
      JvmRuntimeMBean jvmRuntimeMBean = (JvmRuntimeMBean)createJvmRuntimeMBean("JvmRuntime", str, objectName, paramMBeanServer);
      jvmRuntimeMeta.setInstance(jvmRuntimeMBean);
      registerGroupNode("JvmRuntime", str, objectName, jvmRuntimeMeta, jvmRuntimeMBean, paramMBeanServer);
    } 
  }
  
  protected JvmRuntimeMeta createJvmRuntimeMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmRuntimeMeta(this, this.objectserver); }
  
  protected abstract Object createJvmRuntimeMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmThreading(MBeanServer paramMBeanServer) throws Exception {
    String str = getGroupOid("JvmThreading", "1.3.6.1.4.1.42.2.145.3.163.1.1.3");
    ObjectName objectName = null;
    if (paramMBeanServer != null)
      objectName = getGroupObjectName("JvmThreading", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmThreading"); 
    JvmThreadingMeta jvmThreadingMeta = createJvmThreadingMetaNode("JvmThreading", str, objectName, paramMBeanServer);
    if (jvmThreadingMeta != null) {
      jvmThreadingMeta.registerTableNodes(this, paramMBeanServer);
      JvmThreadingMBean jvmThreadingMBean = (JvmThreadingMBean)createJvmThreadingMBean("JvmThreading", str, objectName, paramMBeanServer);
      jvmThreadingMeta.setInstance(jvmThreadingMBean);
      registerGroupNode("JvmThreading", str, objectName, jvmThreadingMeta, jvmThreadingMBean, paramMBeanServer);
    } 
  }
  
  protected JvmThreadingMeta createJvmThreadingMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmThreadingMeta(this, this.objectserver); }
  
  protected abstract Object createJvmThreadingMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmMemory(MBeanServer paramMBeanServer) throws Exception {
    String str = getGroupOid("JvmMemory", "1.3.6.1.4.1.42.2.145.3.163.1.1.2");
    ObjectName objectName = null;
    if (paramMBeanServer != null)
      objectName = getGroupObjectName("JvmMemory", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmMemory"); 
    JvmMemoryMeta jvmMemoryMeta = createJvmMemoryMetaNode("JvmMemory", str, objectName, paramMBeanServer);
    if (jvmMemoryMeta != null) {
      jvmMemoryMeta.registerTableNodes(this, paramMBeanServer);
      JvmMemoryMBean jvmMemoryMBean = (JvmMemoryMBean)createJvmMemoryMBean("JvmMemory", str, objectName, paramMBeanServer);
      jvmMemoryMeta.setInstance(jvmMemoryMBean);
      registerGroupNode("JvmMemory", str, objectName, jvmMemoryMeta, jvmMemoryMBean, paramMBeanServer);
    } 
  }
  
  protected JvmMemoryMeta createJvmMemoryMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmMemoryMeta(this, this.objectserver); }
  
  protected abstract Object createJvmMemoryMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  protected void initJvmClassLoading(MBeanServer paramMBeanServer) throws Exception {
    String str = getGroupOid("JvmClassLoading", "1.3.6.1.4.1.42.2.145.3.163.1.1.1");
    ObjectName objectName = null;
    if (paramMBeanServer != null)
      objectName = getGroupObjectName("JvmClassLoading", str, this.mibName + ":name=sun.management.snmp.jvmmib.JvmClassLoading"); 
    JvmClassLoadingMeta jvmClassLoadingMeta = createJvmClassLoadingMetaNode("JvmClassLoading", str, objectName, paramMBeanServer);
    if (jvmClassLoadingMeta != null) {
      jvmClassLoadingMeta.registerTableNodes(this, paramMBeanServer);
      JvmClassLoadingMBean jvmClassLoadingMBean = (JvmClassLoadingMBean)createJvmClassLoadingMBean("JvmClassLoading", str, objectName, paramMBeanServer);
      jvmClassLoadingMeta.setInstance(jvmClassLoadingMBean);
      registerGroupNode("JvmClassLoading", str, objectName, jvmClassLoadingMeta, jvmClassLoadingMBean, paramMBeanServer);
    } 
  }
  
  protected JvmClassLoadingMeta createJvmClassLoadingMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmClassLoadingMeta(this, this.objectserver); }
  
  protected abstract Object createJvmClassLoadingMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer);
  
  public void registerTableMeta(String paramString, SnmpMibTable paramSnmpMibTable) {
    if (this.metadatas == null)
      return; 
    if (paramString == null)
      return; 
    this.metadatas.put(paramString, paramSnmpMibTable);
  }
  
  public SnmpMibTable getRegisteredTableMeta(String paramString) { return (this.metadatas == null) ? null : ((paramString == null) ? null : (SnmpMibTable)this.metadatas.get(paramString)); }
  
  public SnmpStandardObjectServer getStandardObjectServer() {
    if (this.objectserver == null)
      this.objectserver = new SnmpStandardObjectServer(); 
    return this.objectserver;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JVM_MANAGEMENT_MIB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */