package sun.management;

import java.io.Serializable;

public class CompilerThreadStat implements Serializable {
  private String name;
  
  private long taskCount;
  
  private long compileTime;
  
  private MethodInfo lastMethod;
  
  private static final long serialVersionUID = 6992337162326171013L;
  
  CompilerThreadStat(String paramString, long paramLong1, long paramLong2, MethodInfo paramMethodInfo) {
    this.name = paramString;
    this.taskCount = paramLong1;
    this.compileTime = paramLong2;
    this.lastMethod = paramMethodInfo;
  }
  
  public String getName() { return this.name; }
  
  public long getCompileTaskCount() { return this.taskCount; }
  
  public long getCompileTime() { return this.compileTime; }
  
  public MethodInfo getLastCompiledMethodInfo() { return this.lastMethod; }
  
  public String toString() { return getName() + " compileTasks = " + getCompileTaskCount() + " compileTime = " + getCompileTime(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\CompilerThreadStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */