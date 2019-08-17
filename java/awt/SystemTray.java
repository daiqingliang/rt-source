package java.awt;

import java.awt.peer.SystemTrayPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.SunToolkit;
import sun.security.util.SecurityConstants;

public class SystemTray {
  private static SystemTray systemTray;
  
  private int currentIconID = 0;
  
  private SystemTrayPeer peer;
  
  private static final TrayIcon[] EMPTY_TRAY_ARRAY = new TrayIcon[0];
  
  private SystemTray() { addNotify(); }
  
  public static SystemTray getSystemTray() {
    checkSystemTrayAllowed();
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    initializeSystemTrayIfNeeded();
    if (!isSupported())
      throw new UnsupportedOperationException("The system tray is not supported on the current platform."); 
    return systemTray;
  }
  
  public static boolean isSupported() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (toolkit instanceof SunToolkit) {
      initializeSystemTrayIfNeeded();
      return ((SunToolkit)toolkit).isTraySupported();
    } 
    return (toolkit instanceof HeadlessToolkit) ? ((HeadlessToolkit)toolkit).isTraySupported() : 0;
  }
  
  public void add(TrayIcon paramTrayIcon) throws AWTException {
    if (paramTrayIcon == null)
      throw new NullPointerException("adding null TrayIcon"); 
    TrayIcon[] arrayOfTrayIcon1 = null;
    TrayIcon[] arrayOfTrayIcon2 = null;
    Vector vector = null;
    synchronized (this) {
      arrayOfTrayIcon1 = systemTray.getTrayIcons();
      vector = (Vector)AppContext.getAppContext().get(TrayIcon.class);
      if (vector == null) {
        vector = new Vector(3);
        AppContext.getAppContext().put(TrayIcon.class, vector);
      } else if (vector.contains(paramTrayIcon)) {
        throw new IllegalArgumentException("adding TrayIcon that is already added");
      } 
      vector.add(paramTrayIcon);
      arrayOfTrayIcon2 = systemTray.getTrayIcons();
      paramTrayIcon.setID(++this.currentIconID);
    } 
    try {
      paramTrayIcon.addNotify();
    } catch (AWTException aWTException) {
      vector.remove(paramTrayIcon);
      throw aWTException;
    } 
    firePropertyChange("trayIcons", arrayOfTrayIcon1, arrayOfTrayIcon2);
  }
  
  public void remove(TrayIcon paramTrayIcon) throws AWTException {
    if (paramTrayIcon == null)
      return; 
    TrayIcon[] arrayOfTrayIcon1 = null;
    TrayIcon[] arrayOfTrayIcon2 = null;
    synchronized (this) {
      arrayOfTrayIcon1 = systemTray.getTrayIcons();
      Vector vector = (Vector)AppContext.getAppContext().get(TrayIcon.class);
      if (vector == null || !vector.remove(paramTrayIcon))
        return; 
      paramTrayIcon.removeNotify();
      arrayOfTrayIcon2 = systemTray.getTrayIcons();
    } 
    firePropertyChange("trayIcons", arrayOfTrayIcon1, arrayOfTrayIcon2);
  }
  
  public TrayIcon[] getTrayIcons() {
    Vector vector = (Vector)AppContext.getAppContext().get(TrayIcon.class);
    return (vector != null) ? (TrayIcon[])vector.toArray(new TrayIcon[vector.size()]) : EMPTY_TRAY_ARRAY;
  }
  
  public Dimension getTrayIconSize() { return this.peer.getTrayIconSize(); }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null)
      return; 
    getCurrentChangeSupport().addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramPropertyChangeListener == null)
      return; 
    getCurrentChangeSupport().removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString) { return getCurrentChangeSupport().getPropertyChangeListeners(paramString); }
  
  private void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (paramObject1 != null && paramObject2 != null && paramObject1.equals(paramObject2))
      return; 
    getCurrentChangeSupport().firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  private PropertyChangeSupport getCurrentChangeSupport() {
    PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(SystemTray.class);
    if (propertyChangeSupport == null) {
      propertyChangeSupport = new PropertyChangeSupport(this);
      AppContext.getAppContext().put(SystemTray.class, propertyChangeSupport);
    } 
    return propertyChangeSupport;
  }
  
  void addNotify() {
    if (this.peer == null) {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      if (toolkit instanceof SunToolkit) {
        this.peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
      } else if (toolkit instanceof HeadlessToolkit) {
        this.peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createSystemTray(this);
      } 
    } 
  }
  
  static void checkSystemTrayAllowed() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.ACCESS_SYSTEM_TRAY_PERMISSION); 
  }
  
  private static void initializeSystemTrayIfNeeded() {
    synchronized (SystemTray.class) {
      if (systemTray == null)
        systemTray = new SystemTray(); 
    } 
  }
  
  static  {
    AWTAccessor.setSystemTrayAccessor(new AWTAccessor.SystemTrayAccessor() {
          public void firePropertyChange(SystemTray param1SystemTray, String param1String, Object param1Object1, Object param1Object2) { param1SystemTray.firePropertyChange(param1String, param1Object1, param1Object2); }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\SystemTray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */