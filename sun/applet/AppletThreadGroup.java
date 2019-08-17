package sun.applet;

public class AppletThreadGroup extends ThreadGroup {
  public AppletThreadGroup(String paramString) { this(Thread.currentThread().getThreadGroup(), paramString); }
  
  public AppletThreadGroup(ThreadGroup paramThreadGroup, String paramString) {
    super(paramThreadGroup, paramString);
    setMaxPriority(4);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletThreadGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */