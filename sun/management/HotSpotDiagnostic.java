package sun.management;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.VMOption;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import javax.management.ObjectName;

public class HotSpotDiagnostic implements HotSpotDiagnosticMXBean {
  public void dumpHeap(String paramString, boolean paramBoolean) throws IOException {
    String str = "jdk.management.heapdump.allowAnyFileSuffix";
    PrivilegedAction privilegedAction = () -> Boolean.valueOf(Boolean.parseBoolean(System.getProperty(paramString, "false")));
    boolean bool = ((Boolean)AccessController.doPrivileged(privilegedAction)).booleanValue();
    if (!bool && !paramString.endsWith(".hprof"))
      throw new IllegalArgumentException("heapdump file must have .hprof extention"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkWrite(paramString);
      Util.checkControlAccess();
    } 
    dumpHeap0(paramString, paramBoolean);
  }
  
  private native void dumpHeap0(String paramString, boolean paramBoolean) throws IOException;
  
  public List<VMOption> getDiagnosticOptions() {
    List list = Flag.getAllFlags();
    ArrayList arrayList = new ArrayList();
    for (Flag flag : list) {
      if (flag.isWriteable() && flag.isExternal())
        arrayList.add(flag.getVMOption()); 
    } 
    return arrayList;
  }
  
  public VMOption getVMOption(String paramString) {
    if (paramString == null)
      throw new NullPointerException("name cannot be null"); 
    Flag flag = Flag.getFlag(paramString);
    if (flag == null)
      throw new IllegalArgumentException("VM option \"" + paramString + "\" does not exist"); 
    return flag.getVMOption();
  }
  
  public void setVMOption(String paramString1, String paramString2) {
    if (paramString1 == null)
      throw new NullPointerException("name cannot be null"); 
    if (paramString2 == null)
      throw new NullPointerException("value cannot be null"); 
    Util.checkControlAccess();
    Flag flag = Flag.getFlag(paramString1);
    if (flag == null)
      throw new IllegalArgumentException("VM option \"" + paramString1 + "\" does not exist"); 
    if (!flag.isWriteable())
      throw new IllegalArgumentException("VM Option \"" + paramString1 + "\" is not writeable"); 
    Object object = flag.getValue();
    if (object instanceof Long) {
      try {
        long l = Long.parseLong(paramString2);
        Flag.setLongValue(paramString1, l);
      } catch (NumberFormatException numberFormatException) {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Invalid value: VM Option \"" + paramString1 + "\" expects numeric value");
        illegalArgumentException.initCause(numberFormatException);
        throw illegalArgumentException;
      } 
    } else if (object instanceof Boolean) {
      if (!paramString2.equalsIgnoreCase("true") && !paramString2.equalsIgnoreCase("false"))
        throw new IllegalArgumentException("Invalid value: VM Option \"" + paramString1 + "\" expects \"true\" or \"false\"."); 
      Flag.setBooleanValue(paramString1, Boolean.parseBoolean(paramString2));
    } else if (object instanceof String) {
      Flag.setStringValue(paramString1, paramString2);
    } else {
      throw new IllegalArgumentException("VM Option \"" + paramString1 + "\" is of an unsupported type: " + object.getClass().getName());
    } 
  }
  
  public ObjectName getObjectName() { return Util.newObjectName("com.sun.management:type=HotSpotDiagnostic"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\HotSpotDiagnostic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */