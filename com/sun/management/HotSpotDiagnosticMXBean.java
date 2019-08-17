package com.sun.management;

import java.io.IOException;
import java.lang.management.PlatformManagedObject;
import java.util.List;
import jdk.Exported;

@Exported
public interface HotSpotDiagnosticMXBean extends PlatformManagedObject {
  void dumpHeap(String paramString, boolean paramBoolean) throws IOException;
  
  List<VMOption> getDiagnosticOptions();
  
  VMOption getVMOption(String paramString);
  
  void setVMOption(String paramString1, String paramString2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\HotSpotDiagnosticMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */