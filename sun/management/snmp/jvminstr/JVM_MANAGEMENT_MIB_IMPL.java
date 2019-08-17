package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpCounter64;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpOidTable;
import com.sun.jmx.snmp.SnmpParameters;
import com.sun.jmx.snmp.SnmpPeer;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpVarBindList;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.daemon.SnmpAdaptorServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import sun.management.snmp.jvmmib.JVM_MANAGEMENT_MIB;
import sun.management.snmp.jvmmib.JVM_MANAGEMENT_MIBOidTable;
import sun.management.snmp.jvmmib.JvmCompilationMeta;
import sun.management.snmp.jvmmib.JvmMemoryMeta;
import sun.management.snmp.jvmmib.JvmRuntimeMeta;
import sun.management.snmp.jvmmib.JvmThreadingMeta;
import sun.management.snmp.util.MibLogger;
import sun.management.snmp.util.SnmpCachedData;
import sun.management.snmp.util.SnmpTableHandler;

public class JVM_MANAGEMENT_MIB_IMPL extends JVM_MANAGEMENT_MIB {
  private static final long serialVersionUID = -8104825586888859831L;
  
  private static final MibLogger log = new MibLogger(JVM_MANAGEMENT_MIB_IMPL.class);
  
  private static WeakReference<SnmpOidTable> tableRef;
  
  private ArrayList<NotificationTarget> notificationTargets = new ArrayList();
  
  private final NotificationEmitter emitter = (NotificationEmitter)ManagementFactory.getMemoryMXBean();
  
  private final NotificationHandler handler = new NotificationHandler(null);
  
  private static final int DISPLAY_STRING_MAX_LENGTH = 255;
  
  private static final int JAVA_OBJECT_NAME_MAX_LENGTH = 1023;
  
  private static final int PATH_ELEMENT_MAX_LENGTH = 1023;
  
  private static final int ARG_VALUE_MAX_LENGTH = 1023;
  
  private static final int DEFAULT_CACHE_VALIDITY_PERIOD = 1000;
  
  public static SnmpOidTable getOidTable() {
    JVM_MANAGEMENT_MIBOidTable jVM_MANAGEMENT_MIBOidTable = null;
    if (tableRef == null) {
      jVM_MANAGEMENT_MIBOidTable = new JVM_MANAGEMENT_MIBOidTable();
      tableRef = new WeakReference(jVM_MANAGEMENT_MIBOidTable);
      return jVM_MANAGEMENT_MIBOidTable;
    } 
    SnmpOidTable snmpOidTable = (SnmpOidTable)tableRef.get();
    if (snmpOidTable == null) {
      snmpOidTable = new JVM_MANAGEMENT_MIBOidTable();
      tableRef = new WeakReference(snmpOidTable);
    } 
    return snmpOidTable;
  }
  
  public JVM_MANAGEMENT_MIB_IMPL() { this.emitter.addNotificationListener(this.handler, null, null); }
  
  private void sendTrap(SnmpOid paramSnmpOid, SnmpVarBindList paramSnmpVarBindList) {
    Iterator iterator = this.notificationTargets.iterator();
    SnmpAdaptorServer snmpAdaptorServer = (SnmpAdaptorServer)getSnmpAdaptor();
    if (snmpAdaptorServer == null) {
      log.error("sendTrap", "Cannot send trap: adaptor is null.");
      return;
    } 
    if (!snmpAdaptorServer.isActive()) {
      log.config("sendTrap", "Adaptor is not active: trap not sent.");
      return;
    } 
    while (iterator.hasNext()) {
      NotificationTarget notificationTarget = null;
      try {
        notificationTarget = (NotificationTarget)iterator.next();
        SnmpPeer snmpPeer = new SnmpPeer(notificationTarget.getAddress(), notificationTarget.getPort());
        SnmpParameters snmpParameters = new SnmpParameters();
        snmpParameters.setRdCommunity(notificationTarget.getCommunity());
        snmpPeer.setParams(snmpParameters);
        log.debug("handleNotification", "Sending trap to " + notificationTarget.getAddress() + ":" + notificationTarget.getPort());
        snmpAdaptorServer.snmpV2Trap(snmpPeer, paramSnmpOid, paramSnmpVarBindList, null);
      } catch (Exception exception) {
        log.error("sendTrap", "Exception occurred while sending trap to [" + notificationTarget + "]. Exception : " + exception);
        log.debug("sendTrap", exception);
      } 
    } 
  }
  
  public void addTarget(NotificationTarget paramNotificationTarget) throws IllegalArgumentException {
    if (paramNotificationTarget == null)
      throw new IllegalArgumentException("Target is null"); 
    this.notificationTargets.add(paramNotificationTarget);
  }
  
  public void terminate() {
    try {
      this.emitter.removeNotificationListener(this.handler);
    } catch (ListenerNotFoundException listenerNotFoundException) {
      log.error("terminate", "Listener Not found : " + listenerNotFoundException);
    } 
  }
  
  public void addTargets(List<NotificationTarget> paramList) throws IllegalArgumentException {
    if (paramList == null)
      throw new IllegalArgumentException("Target list is null"); 
    this.notificationTargets.addAll(paramList);
  }
  
  protected Object createJvmMemoryMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return (paramMBeanServer != null) ? new JvmMemoryImpl(this, paramMBeanServer) : new JvmMemoryImpl(this); }
  
  protected JvmMemoryMeta createJvmMemoryMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmMemoryMetaImpl(this, this.objectserver); }
  
  protected JvmThreadingMeta createJvmThreadingMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmThreadingMetaImpl(this, this.objectserver); }
  
  protected Object createJvmThreadingMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return (paramMBeanServer != null) ? new JvmThreadingImpl(this, paramMBeanServer) : new JvmThreadingImpl(this); }
  
  protected JvmRuntimeMeta createJvmRuntimeMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return new JvmRuntimeMetaImpl(this, this.objectserver); }
  
  protected Object createJvmRuntimeMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return (paramMBeanServer != null) ? new JvmRuntimeImpl(this, paramMBeanServer) : new JvmRuntimeImpl(this); }
  
  protected JvmCompilationMeta createJvmCompilationMetaNode(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return (ManagementFactory.getCompilationMXBean() == null) ? null : super.createJvmCompilationMetaNode(paramString1, paramString2, paramObjectName, paramMBeanServer); }
  
  protected Object createJvmCompilationMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return (paramMBeanServer != null) ? new JvmCompilationImpl(this, paramMBeanServer) : new JvmCompilationImpl(this); }
  
  protected Object createJvmOSMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return (paramMBeanServer != null) ? new JvmOSImpl(this, paramMBeanServer) : new JvmOSImpl(this); }
  
  protected Object createJvmClassLoadingMBean(String paramString1, String paramString2, ObjectName paramObjectName, MBeanServer paramMBeanServer) { return (paramMBeanServer != null) ? new JvmClassLoadingImpl(this, paramMBeanServer) : new JvmClassLoadingImpl(this); }
  
  static String validDisplayStringTC(String paramString) { return (paramString == null) ? "" : ((paramString.length() > 255) ? paramString.substring(0, 255) : paramString); }
  
  static String validJavaObjectNameTC(String paramString) { return (paramString == null) ? "" : ((paramString.length() > 1023) ? paramString.substring(0, 1023) : paramString); }
  
  static String validPathElementTC(String paramString) { return (paramString == null) ? "" : ((paramString.length() > 1023) ? paramString.substring(0, 1023) : paramString); }
  
  static String validArgValueTC(String paramString) { return (paramString == null) ? "" : ((paramString.length() > 1023) ? paramString.substring(0, 1023) : paramString); }
  
  private SnmpTableHandler getJvmMemPoolTableHandler(Object paramObject) {
    SnmpMibTable snmpMibTable = getRegisteredTableMeta("JvmMemPoolTable");
    if (!(snmpMibTable instanceof JvmMemPoolTableMetaImpl)) {
      String str = (snmpMibTable == null) ? "No metadata for JvmMemPoolTable" : ("Bad metadata class for JvmMemPoolTable: " + snmpMibTable.getClass().getName());
      log.error("getJvmMemPoolTableHandler", str);
      return null;
    } 
    JvmMemPoolTableMetaImpl jvmMemPoolTableMetaImpl = (JvmMemPoolTableMetaImpl)snmpMibTable;
    return jvmMemPoolTableMetaImpl.getHandler(paramObject);
  }
  
  private int findInCache(SnmpTableHandler paramSnmpTableHandler, String paramString) {
    if (!(paramSnmpTableHandler instanceof SnmpCachedData)) {
      if (paramSnmpTableHandler != null) {
        String str = "Bad class for JvmMemPoolTable datas: " + paramSnmpTableHandler.getClass().getName();
        log.error("getJvmMemPoolEntry", str);
      } 
      return -1;
    } 
    SnmpCachedData snmpCachedData = (SnmpCachedData)paramSnmpTableHandler;
    int i = snmpCachedData.datas.length;
    for (byte b = 0; b < snmpCachedData.datas.length; b++) {
      MemoryPoolMXBean memoryPoolMXBean = (MemoryPoolMXBean)snmpCachedData.datas[b];
      if (paramString.equals(memoryPoolMXBean.getName()))
        return b; 
    } 
    return -1;
  }
  
  private SnmpOid getJvmMemPoolEntryIndex(SnmpTableHandler paramSnmpTableHandler, String paramString) {
    int i = findInCache(paramSnmpTableHandler, paramString);
    return (i < 0) ? null : ((SnmpCachedData)paramSnmpTableHandler).indexes[i];
  }
  
  private SnmpOid getJvmMemPoolEntryIndex(String paramString) { return getJvmMemPoolEntryIndex(getJvmMemPoolTableHandler(null), paramString); }
  
  public long validity() { return 1000L; }
  
  private class NotificationHandler implements NotificationListener {
    private NotificationHandler() {}
    
    public void handleNotification(Notification param1Notification, Object param1Object) {
      log.debug("handleNotification", "Received notification [ " + param1Notification.getType() + "]");
      String str = param1Notification.getType();
      if (str.equals("java.management.memory.threshold.exceeded") || str.equals("java.management.memory.collection.threshold.exceeded")) {
        MemoryNotificationInfo memoryNotificationInfo = MemoryNotificationInfo.from((CompositeData)param1Notification.getUserData());
        SnmpCounter64 snmpCounter641 = new SnmpCounter64(memoryNotificationInfo.getCount());
        SnmpCounter64 snmpCounter642 = new SnmpCounter64(memoryNotificationInfo.getUsage().getUsed());
        SnmpString snmpString = new SnmpString(memoryNotificationInfo.getPoolName());
        SnmpOid snmpOid1 = JVM_MANAGEMENT_MIB_IMPL.this.getJvmMemPoolEntryIndex(memoryNotificationInfo.getPoolName());
        if (snmpOid1 == null) {
          log.error("handleNotification", "Error: Can't find entry index for Memory Pool: " + memoryNotificationInfo.getPoolName() + ": No trap emitted for " + str);
          return;
        } 
        SnmpOid snmpOid2 = null;
        SnmpOidTable snmpOidTable = JVM_MANAGEMENT_MIB_IMPL.getOidTable();
        try {
          SnmpOid snmpOid3 = null;
          SnmpOid snmpOid4 = null;
          if (str.equals("java.management.memory.threshold.exceeded")) {
            snmpOid2 = new SnmpOid(snmpOidTable.resolveVarName("jvmLowMemoryPoolUsageNotif").getOid());
            snmpOid3 = new SnmpOid(snmpOidTable.resolveVarName("jvmMemPoolUsed").getOid() + "." + snmpOid1);
            snmpOid4 = new SnmpOid(snmpOidTable.resolveVarName("jvmMemPoolThreshdCount").getOid() + "." + snmpOid1);
          } else if (str.equals("java.management.memory.collection.threshold.exceeded")) {
            snmpOid2 = new SnmpOid(snmpOidTable.resolveVarName("jvmLowMemoryPoolCollectNotif").getOid());
            snmpOid3 = new SnmpOid(snmpOidTable.resolveVarName("jvmMemPoolCollectUsed").getOid() + "." + snmpOid1);
            snmpOid4 = new SnmpOid(snmpOidTable.resolveVarName("jvmMemPoolCollectThreshdCount").getOid() + "." + snmpOid1);
          } 
          SnmpVarBindList snmpVarBindList = new SnmpVarBindList();
          SnmpOid snmpOid5 = new SnmpOid(snmpOidTable.resolveVarName("jvmMemPoolName").getOid() + "." + snmpOid1);
          SnmpVarBind snmpVarBind1 = new SnmpVarBind(snmpOid4, snmpCounter641);
          SnmpVarBind snmpVarBind2 = new SnmpVarBind(snmpOid3, snmpCounter642);
          SnmpVarBind snmpVarBind3 = new SnmpVarBind(snmpOid5, snmpString);
          snmpVarBindList.add(snmpVarBind3);
          snmpVarBindList.add(snmpVarBind1);
          snmpVarBindList.add(snmpVarBind2);
          JVM_MANAGEMENT_MIB_IMPL.this.sendTrap(snmpOid2, snmpVarBindList);
        } catch (Exception exception) {
          log.error("handleNotification", "Exception occurred : " + exception);
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JVM_MANAGEMENT_MIB_IMPL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */