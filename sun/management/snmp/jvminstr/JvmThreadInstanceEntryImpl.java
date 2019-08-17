package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpOidRecord;
import com.sun.jmx.snmp.SnmpStatusException;
import java.io.Serializable;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import sun.management.snmp.jvmmib.JVM_MANAGEMENT_MIBOidTable;
import sun.management.snmp.jvmmib.JvmThreadInstanceEntryMBean;
import sun.management.snmp.util.MibLogger;

public class JvmThreadInstanceEntryImpl implements JvmThreadInstanceEntryMBean, Serializable {
  static final long serialVersionUID = 910173589985461347L;
  
  private final ThreadInfo info;
  
  private final Byte[] index;
  
  private static String jvmThreadInstIndexOid = null;
  
  static final MibLogger log = new MibLogger(JvmThreadInstanceEntryImpl.class);
  
  public JvmThreadInstanceEntryImpl(ThreadInfo paramThreadInfo, Byte[] paramArrayOfByte) {
    this.info = paramThreadInfo;
    this.index = paramArrayOfByte;
  }
  
  public static String getJvmThreadInstIndexOid() throws SnmpStatusException {
    if (jvmThreadInstIndexOid == null) {
      JVM_MANAGEMENT_MIBOidTable jVM_MANAGEMENT_MIBOidTable = new JVM_MANAGEMENT_MIBOidTable();
      SnmpOidRecord snmpOidRecord = jVM_MANAGEMENT_MIBOidTable.resolveVarName("jvmThreadInstIndex");
      jvmThreadInstIndexOid = snmpOidRecord.getOid();
    } 
    return jvmThreadInstIndexOid;
  }
  
  public String getJvmThreadInstLockOwnerPtr() throws SnmpStatusException {
    long l = this.info.getLockOwnerId();
    if (l == -1L)
      return new String("0.0"); 
    SnmpOid snmpOid = JvmThreadInstanceTableMetaImpl.makeOid(l);
    return getJvmThreadInstIndexOid() + "." + snmpOid.toString();
  }
  
  private String validDisplayStringTC(String paramString) { return JVM_MANAGEMENT_MIB_IMPL.validDisplayStringTC(paramString); }
  
  private String validJavaObjectNameTC(String paramString) { return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(paramString); }
  
  private String validPathElementTC(String paramString) { return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(paramString); }
  
  public String getJvmThreadInstLockName() throws SnmpStatusException { return validJavaObjectNameTC(this.info.getLockName()); }
  
  public String getJvmThreadInstName() throws SnmpStatusException { return validJavaObjectNameTC(this.info.getThreadName()); }
  
  public Long getJvmThreadInstCpuTimeNs() throws SnmpStatusException {
    long l = 0L;
    ThreadMXBean threadMXBean = JvmThreadingImpl.getThreadMXBean();
    try {
      if (threadMXBean.isThreadCpuTimeSupported()) {
        l = threadMXBean.getThreadCpuTime(this.info.getThreadId());
        log.debug("getJvmThreadInstCpuTimeNs", "Cpu time ns : " + l);
        if (l == -1L)
          l = 0L; 
      } 
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      log.debug("getJvmThreadInstCpuTimeNs", "Operation not supported: " + unsatisfiedLinkError);
    } 
    return new Long(l);
  }
  
  public Long getJvmThreadInstBlockTimeMs() throws SnmpStatusException {
    long l = 0L;
    ThreadMXBean threadMXBean = JvmThreadingImpl.getThreadMXBean();
    if (threadMXBean.isThreadContentionMonitoringSupported()) {
      l = this.info.getBlockedTime();
      if (l == -1L)
        l = 0L; 
    } 
    return new Long(l);
  }
  
  public Long getJvmThreadInstBlockCount() throws SnmpStatusException { return new Long(this.info.getBlockedCount()); }
  
  public Long getJvmThreadInstWaitTimeMs() throws SnmpStatusException {
    long l = 0L;
    ThreadMXBean threadMXBean = JvmThreadingImpl.getThreadMXBean();
    if (threadMXBean.isThreadContentionMonitoringSupported()) {
      l = this.info.getWaitedTime();
      if (l == -1L)
        l = 0L; 
    } 
    return new Long(l);
  }
  
  public Long getJvmThreadInstWaitCount() throws SnmpStatusException { return new Long(this.info.getWaitedCount()); }
  
  public Byte[] getJvmThreadInstState() throws SnmpStatusException { return ThreadStateMap.getState(this.info); }
  
  public Long getJvmThreadInstId() throws SnmpStatusException { return new Long(this.info.getThreadId()); }
  
  public Byte[] getJvmThreadInstIndex() throws SnmpStatusException { return this.index; }
  
  private String getJvmThreadInstStackTrace() throws SnmpStatusException {
    StackTraceElement[] arrayOfStackTraceElement = this.info.getStackTrace();
    StringBuffer stringBuffer = new StringBuffer();
    int i = arrayOfStackTraceElement.length;
    log.debug("getJvmThreadInstStackTrace", "Stack size : " + i);
    for (byte b = 0; b < i; b++) {
      log.debug("getJvmThreadInstStackTrace", "Append " + arrayOfStackTraceElement[b].toString());
      stringBuffer.append(arrayOfStackTraceElement[b].toString());
      if (b < i)
        stringBuffer.append("\n"); 
    } 
    return validPathElementTC(stringBuffer.toString());
  }
  
  public static final class ThreadStateMap {
    public static final byte mask0 = 63;
    
    public static final byte mask1 = -128;
    
    private static void setBit(byte[] param1ArrayOfByte, int param1Int, byte param1Byte) { param1ArrayOfByte[param1Int] = (byte)(param1ArrayOfByte[param1Int] | param1Byte); }
    
    public static void setNative(byte[] param1ArrayOfByte) { setBit(param1ArrayOfByte, 0, -128); }
    
    public static void setSuspended(byte[] param1ArrayOfByte) { setBit(param1ArrayOfByte, 0, (byte)64); }
    
    public static void setState(byte[] param1ArrayOfByte, Thread.State param1State) { // Byte code:
      //   0: getstatic sun/management/snmp/jvminstr/JvmThreadInstanceEntryImpl$1.$SwitchMap$java$lang$Thread$State : [I
      //   3: aload_1
      //   4: invokevirtual ordinal : ()I
      //   7: iaload
      //   8: tableswitch default -> 93, 1 -> 48, 2 -> 56, 3 -> 64, 4 -> 72, 5 -> 79, 6 -> 86
      //   48: aload_0
      //   49: iconst_0
      //   50: bipush #8
      //   52: invokestatic setBit : ([BIB)V
      //   55: return
      //   56: aload_0
      //   57: iconst_0
      //   58: bipush #32
      //   60: invokestatic setBit : ([BIB)V
      //   63: return
      //   64: aload_0
      //   65: iconst_0
      //   66: bipush #16
      //   68: invokestatic setBit : ([BIB)V
      //   71: return
      //   72: aload_0
      //   73: iconst_0
      //   74: iconst_4
      //   75: invokestatic setBit : ([BIB)V
      //   78: return
      //   79: aload_0
      //   80: iconst_0
      //   81: iconst_1
      //   82: invokestatic setBit : ([BIB)V
      //   85: return
      //   86: aload_0
      //   87: iconst_0
      //   88: iconst_2
      //   89: invokestatic setBit : ([BIB)V
      //   92: return
      //   93: return }
    
    public static void checkOther(byte[] param1ArrayOfByte) {
      if ((param1ArrayOfByte[0] & 0x3F) == 0 && (param1ArrayOfByte[1] & 0xFFFFFF80) == 0)
        setBit(param1ArrayOfByte, 1, -128); 
    }
    
    public static Byte[] getState(ThreadInfo param1ThreadInfo) {
      byte[] arrayOfByte = { 0, 0 };
      try {
        Thread.State state = param1ThreadInfo.getThreadState();
        boolean bool1 = param1ThreadInfo.isInNative();
        boolean bool2 = param1ThreadInfo.isSuspended();
        JvmThreadInstanceEntryImpl.log.debug("getJvmThreadInstState", "[State=" + state + ",isInNative=" + bool1 + ",isSuspended=" + bool2 + "]");
        setState(arrayOfByte, state);
        if (bool1)
          setNative(arrayOfByte); 
        if (bool2)
          setSuspended(arrayOfByte); 
        checkOther(arrayOfByte);
      } catch (RuntimeException runtimeException) {
        arrayOfByte[0] = 0;
        arrayOfByte[1] = Byte.MIN_VALUE;
        JvmThreadInstanceEntryImpl.log.trace("getJvmThreadInstState", "Unexpected exception: " + runtimeException);
        JvmThreadInstanceEntryImpl.log.debug("getJvmThreadInstState", runtimeException);
      } 
      return new Byte[] { new Byte(arrayOfByte[0]), new Byte(arrayOfByte[1]) };
    }
    
    public static final class Byte0 {
      public static final byte inNative = -128;
      
      public static final byte suspended = 64;
      
      public static final byte newThread = 32;
      
      public static final byte runnable = 16;
      
      public static final byte blocked = 8;
      
      public static final byte terminated = 4;
      
      public static final byte waiting = 2;
      
      public static final byte timedWaiting = 1;
    }
    
    public static final class Byte1 {
      public static final byte other = -128;
      
      public static final byte reserved10 = 64;
      
      public static final byte reserved11 = 32;
      
      public static final byte reserved12 = 16;
      
      public static final byte reserved13 = 8;
      
      public static final byte reserved14 = 4;
      
      public static final byte reserved15 = 2;
      
      public static final byte reserved16 = 1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvminstr\JvmThreadInstanceEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */