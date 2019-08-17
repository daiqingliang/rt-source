package sun.misc;

import sun.usagetracker.UsageTrackerClient;

public class PostVMInitHook {
  public static void run() { trackJavaUsage(); }
  
  private static void trackJavaUsage() {
    UsageTrackerClient usageTrackerClient = new UsageTrackerClient();
    usageTrackerClient.run("VM start", System.getProperty("sun.java.command"));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\PostVMInitHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */