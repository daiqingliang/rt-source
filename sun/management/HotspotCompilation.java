package sun.management;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import sun.management.counter.Counter;
import sun.management.counter.LongCounter;
import sun.management.counter.StringCounter;

class HotspotCompilation implements HotspotCompilationMBean {
  private VMManagement jvm;
  
  private static final String JAVA_CI = "java.ci.";
  
  private static final String COM_SUN_CI = "com.sun.ci.";
  
  private static final String SUN_CI = "sun.ci.";
  
  private static final String CI_COUNTER_NAME_PATTERN = "java.ci.|com.sun.ci.|sun.ci.";
  
  private LongCounter compilerThreads;
  
  private LongCounter totalCompiles;
  
  private LongCounter totalBailouts;
  
  private LongCounter totalInvalidates;
  
  private LongCounter nmethodCodeSize;
  
  private LongCounter nmethodSize;
  
  private StringCounter lastMethod;
  
  private LongCounter lastSize;
  
  private LongCounter lastType;
  
  private StringCounter lastFailedMethod;
  
  private LongCounter lastFailedType;
  
  private StringCounter lastInvalidatedMethod;
  
  private LongCounter lastInvalidatedType;
  
  private CompilerThreadInfo[] threads;
  
  private int numActiveThreads;
  
  private Map<String, Counter> counters;
  
  HotspotCompilation(VMManagement paramVMManagement) {
    this.jvm = paramVMManagement;
    initCompilerCounters();
  }
  
  private Counter lookup(String paramString) {
    Counter counter = null;
    if ((counter = (Counter)this.counters.get("sun.ci." + paramString)) != null)
      return counter; 
    if ((counter = (Counter)this.counters.get("com.sun.ci." + paramString)) != null)
      return counter; 
    if ((counter = (Counter)this.counters.get("java.ci." + paramString)) != null)
      return counter; 
    throw new AssertionError("Counter " + paramString + " does not exist");
  }
  
  private void initCompilerCounters() {
    this.counters = new TreeMap();
    for (Counter counter : getInternalCompilerCounters())
      this.counters.put(counter.getName(), counter); 
    this.compilerThreads = (LongCounter)lookup("threads");
    this.totalCompiles = (LongCounter)lookup("totalCompiles");
    this.totalBailouts = (LongCounter)lookup("totalBailouts");
    this.totalInvalidates = (LongCounter)lookup("totalInvalidates");
    this.nmethodCodeSize = (LongCounter)lookup("nmethodCodeSize");
    this.nmethodSize = (LongCounter)lookup("nmethodSize");
    this.lastMethod = (StringCounter)lookup("lastMethod");
    this.lastSize = (LongCounter)lookup("lastSize");
    this.lastType = (LongCounter)lookup("lastType");
    this.lastFailedMethod = (StringCounter)lookup("lastFailedMethod");
    this.lastFailedType = (LongCounter)lookup("lastFailedType");
    this.lastInvalidatedMethod = (StringCounter)lookup("lastInvalidatedMethod");
    this.lastInvalidatedType = (LongCounter)lookup("lastInvalidatedType");
    this.numActiveThreads = (int)this.compilerThreads.longValue();
    this.threads = new CompilerThreadInfo[this.numActiveThreads + 1];
    if (this.counters.containsKey("sun.ci.adapterThread.compiles")) {
      this.threads[0] = new CompilerThreadInfo("adapterThread", 0);
      this.numActiveThreads++;
    } else {
      this.threads[0] = null;
    } 
    for (byte b = 1; b < this.threads.length; b++)
      this.threads[b] = new CompilerThreadInfo("compilerThread", b - true); 
  }
  
  public int getCompilerThreadCount() { return this.numActiveThreads; }
  
  public long getTotalCompileCount() { return this.totalCompiles.longValue(); }
  
  public long getBailoutCompileCount() { return this.totalBailouts.longValue(); }
  
  public long getInvalidatedCompileCount() { return this.totalInvalidates.longValue(); }
  
  public long getCompiledMethodCodeSize() { return this.nmethodCodeSize.longValue(); }
  
  public long getCompiledMethodSize() { return this.nmethodSize.longValue(); }
  
  public List<CompilerThreadStat> getCompilerThreadStats() {
    ArrayList arrayList = new ArrayList(this.threads.length);
    byte b = 0;
    if (this.threads[false] == null)
      b = 1; 
    while (b < this.threads.length) {
      arrayList.add(this.threads[b].getCompilerThreadStat());
      b++;
    } 
    return arrayList;
  }
  
  public MethodInfo getLastCompile() { return new MethodInfo(this.lastMethod.stringValue(), (int)this.lastType.longValue(), (int)this.lastSize.longValue()); }
  
  public MethodInfo getFailedCompile() { return new MethodInfo(this.lastFailedMethod.stringValue(), (int)this.lastFailedType.longValue(), -1); }
  
  public MethodInfo getInvalidatedCompile() { return new MethodInfo(this.lastInvalidatedMethod.stringValue(), (int)this.lastInvalidatedType.longValue(), -1); }
  
  public List<Counter> getInternalCompilerCounters() { return this.jvm.getInternalCounters("java.ci.|com.sun.ci.|sun.ci."); }
  
  private class CompilerThreadInfo {
    int index;
    
    String name;
    
    StringCounter method;
    
    LongCounter type;
    
    LongCounter compiles;
    
    LongCounter time;
    
    CompilerThreadInfo(String param1String, int param1Int) {
      String str = param1String + "." + param1Int + ".";
      this.name = param1String + "-" + param1Int;
      this.method = (StringCounter)this$0.lookup(str + "method");
      this.type = (LongCounter)this$0.lookup(str + "type");
      this.compiles = (LongCounter)this$0.lookup(str + "compiles");
      this.time = (LongCounter)this$0.lookup(str + "time");
    }
    
    CompilerThreadInfo(String param1String) {
      String str = param1String + ".";
      this.name = param1String;
      this.method = (StringCounter)this$0.lookup(str + "method");
      this.type = (LongCounter)this$0.lookup(str + "type");
      this.compiles = (LongCounter)this$0.lookup(str + "compiles");
      this.time = (LongCounter)this$0.lookup(str + "time");
    }
    
    CompilerThreadStat getCompilerThreadStat() {
      MethodInfo methodInfo = new MethodInfo(this.method.stringValue(), (int)this.type.longValue(), -1);
      return new CompilerThreadStat(this.name, this.compiles.longValue(), this.time.longValue(), methodInfo);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\HotspotCompilation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */