package sun.management;

import java.lang.management.OperatingSystemMXBean;
import javax.management.ObjectName;
import sun.misc.Unsafe;

public class BaseOperatingSystemImpl implements OperatingSystemMXBean {
  private final VMManagement jvm;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private double[] loadavg = new double[1];
  
  protected BaseOperatingSystemImpl(VMManagement paramVMManagement) { this.jvm = paramVMManagement; }
  
  public String getName() { return this.jvm.getOsName(); }
  
  public String getArch() { return this.jvm.getOsArch(); }
  
  public String getVersion() { return this.jvm.getOsVersion(); }
  
  public int getAvailableProcessors() { return this.jvm.getAvailableProcessors(); }
  
  public double getSystemLoadAverage() { return (unsafe.getLoadAverage(this.loadavg, 1) == 1) ? this.loadavg[0] : -1.0D; }
  
  public ObjectName getObjectName() { return Util.newObjectName("java.lang:type=OperatingSystem"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\BaseOperatingSystemImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */