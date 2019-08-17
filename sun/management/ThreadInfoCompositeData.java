package sun.management;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

public class ThreadInfoCompositeData extends LazyCompositeData {
  private final ThreadInfo threadInfo;
  
  private final CompositeData cdata;
  
  private final boolean currentVersion;
  
  private static final String THREAD_ID = "threadId";
  
  private static final String THREAD_NAME = "threadName";
  
  private static final String THREAD_STATE = "threadState";
  
  private static final String BLOCKED_TIME = "blockedTime";
  
  private static final String BLOCKED_COUNT = "blockedCount";
  
  private static final String WAITED_TIME = "waitedTime";
  
  private static final String WAITED_COUNT = "waitedCount";
  
  private static final String LOCK_INFO = "lockInfo";
  
  private static final String LOCK_NAME = "lockName";
  
  private static final String LOCK_OWNER_ID = "lockOwnerId";
  
  private static final String LOCK_OWNER_NAME = "lockOwnerName";
  
  private static final String STACK_TRACE = "stackTrace";
  
  private static final String SUSPENDED = "suspended";
  
  private static final String IN_NATIVE = "inNative";
  
  private static final String LOCKED_MONITORS = "lockedMonitors";
  
  private static final String LOCKED_SYNCS = "lockedSynchronizers";
  
  private static final String[] threadInfoItemNames = { 
      "threadId", "threadName", "threadState", "blockedTime", "blockedCount", "waitedTime", "waitedCount", "lockInfo", "lockName", "lockOwnerId", 
      "lockOwnerName", "stackTrace", "suspended", "inNative", "lockedMonitors", "lockedSynchronizers" };
  
  private static final String[] threadInfoV6Attributes = { "lockInfo", "lockedMonitors", "lockedSynchronizers" };
  
  private static final CompositeType threadInfoCompositeType;
  
  private static final CompositeType threadInfoV5CompositeType;
  
  private static final CompositeType lockInfoCompositeType;
  
  private static final long serialVersionUID = 2464378539119753175L;
  
  private ThreadInfoCompositeData(ThreadInfo paramThreadInfo) {
    this.threadInfo = paramThreadInfo;
    this.currentVersion = true;
    this.cdata = null;
  }
  
  private ThreadInfoCompositeData(CompositeData paramCompositeData) {
    this.threadInfo = null;
    this.currentVersion = isCurrentVersion(paramCompositeData);
    this.cdata = paramCompositeData;
  }
  
  public ThreadInfo getThreadInfo() { return this.threadInfo; }
  
  public boolean isCurrentVersion() { return this.currentVersion; }
  
  public static ThreadInfoCompositeData getInstance(CompositeData paramCompositeData) {
    validateCompositeData(paramCompositeData);
    return new ThreadInfoCompositeData(paramCompositeData);
  }
  
  public static CompositeData toCompositeData(ThreadInfo paramThreadInfo) {
    ThreadInfoCompositeData threadInfoCompositeData = new ThreadInfoCompositeData(paramThreadInfo);
    return threadInfoCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData() {
    StackTraceElement[] arrayOfStackTraceElement = this.threadInfo.getStackTrace();
    CompositeData[] arrayOfCompositeData1 = new CompositeData[arrayOfStackTraceElement.length];
    for (byte b1 = 0; b1 < arrayOfStackTraceElement.length; b1++) {
      StackTraceElement stackTraceElement = arrayOfStackTraceElement[b1];
      arrayOfCompositeData1[b1] = StackTraceElementCompositeData.toCompositeData(stackTraceElement);
    } 
    CompositeData compositeData = LockInfoCompositeData.toCompositeData(this.threadInfo.getLockInfo());
    LockInfo[] arrayOfLockInfo = this.threadInfo.getLockedSynchronizers();
    CompositeData[] arrayOfCompositeData2 = new CompositeData[arrayOfLockInfo.length];
    for (byte b2 = 0; b2 < arrayOfLockInfo.length; b2++) {
      LockInfo lockInfo = arrayOfLockInfo[b2];
      arrayOfCompositeData2[b2] = LockInfoCompositeData.toCompositeData(lockInfo);
    } 
    MonitorInfo[] arrayOfMonitorInfo = this.threadInfo.getLockedMonitors();
    CompositeData[] arrayOfCompositeData3 = new CompositeData[arrayOfMonitorInfo.length];
    for (byte b3 = 0; b3 < arrayOfMonitorInfo.length; b3++) {
      MonitorInfo monitorInfo = arrayOfMonitorInfo[b3];
      arrayOfCompositeData3[b3] = MonitorInfoCompositeData.toCompositeData(monitorInfo);
    } 
    Object[] arrayOfObject = { 
        new Long(this.threadInfo.getThreadId()), this.threadInfo.getThreadName(), this.threadInfo.getThreadState().name(), new Long(this.threadInfo.getBlockedTime()), new Long(this.threadInfo.getBlockedCount()), new Long(this.threadInfo.getWaitedTime()), new Long(this.threadInfo.getWaitedCount()), compositeData, this.threadInfo.getLockName(), new Long(this.threadInfo.getLockOwnerId()), 
        this.threadInfo.getLockOwnerName(), arrayOfCompositeData1, new Boolean(this.threadInfo.isSuspended()), new Boolean(this.threadInfo.isInNative()), arrayOfCompositeData3, arrayOfCompositeData2 };
    try {
      return new CompositeDataSupport(threadInfoCompositeType, threadInfoItemNames, arrayOfObject);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
  }
  
  private static boolean isV5Attribute(String paramString) {
    for (String str : threadInfoV6Attributes) {
      if (paramString.equals(str))
        return false; 
    } 
    return true;
  }
  
  public static boolean isCurrentVersion(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    return isTypeMatched(threadInfoCompositeType, paramCompositeData.getCompositeType());
  }
  
  public long threadId() { return getLong(this.cdata, "threadId"); }
  
  public String threadName() {
    String str = getString(this.cdata, "threadName");
    if (str == null)
      throw new IllegalArgumentException("Invalid composite data: Attribute threadName has null value"); 
    return str;
  }
  
  public Thread.State threadState() { return Thread.State.valueOf(getString(this.cdata, "threadState")); }
  
  public long blockedTime() { return getLong(this.cdata, "blockedTime"); }
  
  public long blockedCount() { return getLong(this.cdata, "blockedCount"); }
  
  public long waitedTime() { return getLong(this.cdata, "waitedTime"); }
  
  public long waitedCount() { return getLong(this.cdata, "waitedCount"); }
  
  public String lockName() { return getString(this.cdata, "lockName"); }
  
  public long lockOwnerId() { return getLong(this.cdata, "lockOwnerId"); }
  
  public String lockOwnerName() { return getString(this.cdata, "lockOwnerName"); }
  
  public boolean suspended() { return getBoolean(this.cdata, "suspended"); }
  
  public boolean inNative() { return getBoolean(this.cdata, "inNative"); }
  
  public StackTraceElement[] stackTrace() {
    CompositeData[] arrayOfCompositeData = (CompositeData[])this.cdata.get("stackTrace");
    StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[arrayOfCompositeData.length];
    for (byte b = 0; b < arrayOfCompositeData.length; b++) {
      CompositeData compositeData = arrayOfCompositeData[b];
      arrayOfStackTraceElement[b] = StackTraceElementCompositeData.from(compositeData);
    } 
    return arrayOfStackTraceElement;
  }
  
  public LockInfo lockInfo() {
    CompositeData compositeData = (CompositeData)this.cdata.get("lockInfo");
    return LockInfo.from(compositeData);
  }
  
  public MonitorInfo[] lockedMonitors() {
    CompositeData[] arrayOfCompositeData = (CompositeData[])this.cdata.get("lockedMonitors");
    MonitorInfo[] arrayOfMonitorInfo = new MonitorInfo[arrayOfCompositeData.length];
    for (byte b = 0; b < arrayOfCompositeData.length; b++) {
      CompositeData compositeData = arrayOfCompositeData[b];
      arrayOfMonitorInfo[b] = MonitorInfo.from(compositeData);
    } 
    return arrayOfMonitorInfo;
  }
  
  public LockInfo[] lockedSynchronizers() {
    CompositeData[] arrayOfCompositeData = (CompositeData[])this.cdata.get("lockedSynchronizers");
    LockInfo[] arrayOfLockInfo = new LockInfo[arrayOfCompositeData.length];
    for (byte b = 0; b < arrayOfCompositeData.length; b++) {
      CompositeData compositeData = arrayOfCompositeData[b];
      arrayOfLockInfo[b] = LockInfo.from(compositeData);
    } 
    return arrayOfLockInfo;
  }
  
  public static void validateCompositeData(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    CompositeType compositeType = paramCompositeData.getCompositeType();
    boolean bool = true;
    if (!isTypeMatched(threadInfoCompositeType, compositeType)) {
      bool = false;
      if (!isTypeMatched(threadInfoV5CompositeType, compositeType))
        throw new IllegalArgumentException("Unexpected composite type for ThreadInfo"); 
    } 
    CompositeData[] arrayOfCompositeData = (CompositeData[])paramCompositeData.get("stackTrace");
    if (arrayOfCompositeData == null)
      throw new IllegalArgumentException("StackTraceElement[] is missing"); 
    if (arrayOfCompositeData.length > 0)
      StackTraceElementCompositeData.validateCompositeData(arrayOfCompositeData[0]); 
    if (bool) {
      CompositeData compositeData = (CompositeData)paramCompositeData.get("lockInfo");
      if (compositeData != null && !isTypeMatched(lockInfoCompositeType, compositeData.getCompositeType()))
        throw new IllegalArgumentException("Unexpected composite type for \"lockInfo\" attribute."); 
      CompositeData[] arrayOfCompositeData1 = (CompositeData[])paramCompositeData.get("lockedMonitors");
      if (arrayOfCompositeData1 == null)
        throw new IllegalArgumentException("MonitorInfo[] is null"); 
      if (arrayOfCompositeData1.length > 0)
        MonitorInfoCompositeData.validateCompositeData(arrayOfCompositeData1[0]); 
      CompositeData[] arrayOfCompositeData2 = (CompositeData[])paramCompositeData.get("lockedSynchronizers");
      if (arrayOfCompositeData2 == null)
        throw new IllegalArgumentException("LockInfo[] is null"); 
      if (arrayOfCompositeData2.length > 0 && !isTypeMatched(lockInfoCompositeType, arrayOfCompositeData2[0].getCompositeType()))
        throw new IllegalArgumentException("Unexpected composite type for \"lockedSynchronizers\" attribute."); 
    } 
  }
  
  static  {
    try {
      threadInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(ThreadInfo.class);
      String[] arrayOfString1 = (String[])threadInfoCompositeType.keySet().toArray(new String[0]);
      int i = threadInfoItemNames.length - threadInfoV6Attributes.length;
      String[] arrayOfString2 = new String[i];
      String[] arrayOfString3 = new String[i];
      OpenType[] arrayOfOpenType = new OpenType[i];
      byte b = 0;
      for (String str : arrayOfString1) {
        if (isV5Attribute(str)) {
          arrayOfString2[b] = str;
          arrayOfString3[b] = threadInfoCompositeType.getDescription(str);
          arrayOfOpenType[b] = threadInfoCompositeType.getType(str);
          b++;
        } 
      } 
      threadInfoV5CompositeType = new CompositeType("java.lang.management.ThreadInfo", "J2SE 5.0 java.lang.management.ThreadInfo", arrayOfString2, arrayOfString3, arrayOfOpenType);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
    Object object = new Object();
    LockInfo lockInfo = new LockInfo(object.getClass().getName(), System.identityHashCode(object));
    CompositeData compositeData = LockInfoCompositeData.toCompositeData(lockInfo);
    lockInfoCompositeType = compositeData.getCompositeType();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\ThreadInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */