package sun.applet;

public class AppletEventMulticaster implements AppletListener {
  private final AppletListener a;
  
  private final AppletListener b;
  
  public AppletEventMulticaster(AppletListener paramAppletListener1, AppletListener paramAppletListener2) {
    this.a = paramAppletListener1;
    this.b = paramAppletListener2;
  }
  
  public void appletStateChanged(AppletEvent paramAppletEvent) {
    this.a.appletStateChanged(paramAppletEvent);
    this.b.appletStateChanged(paramAppletEvent);
  }
  
  public static AppletListener add(AppletListener paramAppletListener1, AppletListener paramAppletListener2) { return addInternal(paramAppletListener1, paramAppletListener2); }
  
  public static AppletListener remove(AppletListener paramAppletListener1, AppletListener paramAppletListener2) { return removeInternal(paramAppletListener1, paramAppletListener2); }
  
  private static AppletListener addInternal(AppletListener paramAppletListener1, AppletListener paramAppletListener2) { return (paramAppletListener1 == null) ? paramAppletListener2 : ((paramAppletListener2 == null) ? paramAppletListener1 : new AppletEventMulticaster(paramAppletListener1, paramAppletListener2)); }
  
  protected AppletListener remove(AppletListener paramAppletListener) {
    if (paramAppletListener == this.a)
      return this.b; 
    if (paramAppletListener == this.b)
      return this.a; 
    AppletListener appletListener1 = removeInternal(this.a, paramAppletListener);
    AppletListener appletListener2 = removeInternal(this.b, paramAppletListener);
    return (appletListener1 == this.a && appletListener2 == this.b) ? this : addInternal(appletListener1, appletListener2);
  }
  
  private static AppletListener removeInternal(AppletListener paramAppletListener1, AppletListener paramAppletListener2) { return (paramAppletListener1 == paramAppletListener2 || paramAppletListener1 == null) ? null : ((paramAppletListener1 instanceof AppletEventMulticaster) ? ((AppletEventMulticaster)paramAppletListener1).remove(paramAppletListener2) : paramAppletListener1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletEventMulticaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */