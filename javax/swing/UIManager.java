package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.border.Border;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.OSInfo;
import sun.awt.PaintEventDispatcher;
import sun.awt.SunToolkit;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class UIManager implements Serializable {
  private static final Object classLock = new Object();
  
  private static final String defaultLAFKey = "swing.defaultlaf";
  
  private static final String auxiliaryLAFsKey = "swing.auxiliarylaf";
  
  private static final String multiplexingLAFKey = "swing.plaf.multiplexinglaf";
  
  private static final String installedLAFsKey = "swing.installedlafs";
  
  private static final String disableMnemonicKey = "swing.disablenavaids";
  
  private static LookAndFeelInfo[] installedLAFs;
  
  private static LAFState getLAFState() {
    LAFState lAFState = (LAFState)SwingUtilities.appContextGet(SwingUtilities2.LAF_STATE_KEY);
    if (lAFState == null)
      synchronized (classLock) {
        lAFState = (LAFState)SwingUtilities.appContextGet(SwingUtilities2.LAF_STATE_KEY);
        if (lAFState == null)
          SwingUtilities.appContextPut(SwingUtilities2.LAF_STATE_KEY, lAFState = new LAFState(null)); 
      }  
    return lAFState;
  }
  
  private static String makeInstalledLAFKey(String paramString1, String paramString2) { return "swing.installedlaf." + paramString1 + "." + paramString2; }
  
  private static String makeSwingPropertiesFilename() {
    String str1 = File.separator;
    String str2 = System.getProperty("java.home");
    if (str2 == null)
      str2 = "<java.home undefined>"; 
    return str2 + str1 + "lib" + str1 + "swing.properties";
  }
  
  public static LookAndFeelInfo[] getInstalledLookAndFeels() {
    maybeInitialize();
    LookAndFeelInfo[] arrayOfLookAndFeelInfo1 = (getLAFState()).installedLAFs;
    if (arrayOfLookAndFeelInfo1 == null)
      arrayOfLookAndFeelInfo1 = installedLAFs; 
    LookAndFeelInfo[] arrayOfLookAndFeelInfo2 = new LookAndFeelInfo[arrayOfLookAndFeelInfo1.length];
    System.arraycopy(arrayOfLookAndFeelInfo1, 0, arrayOfLookAndFeelInfo2, 0, arrayOfLookAndFeelInfo1.length);
    return arrayOfLookAndFeelInfo2;
  }
  
  public static void setInstalledLookAndFeels(LookAndFeelInfo[] paramArrayOfLookAndFeelInfo) throws SecurityException {
    maybeInitialize();
    LookAndFeelInfo[] arrayOfLookAndFeelInfo = new LookAndFeelInfo[paramArrayOfLookAndFeelInfo.length];
    System.arraycopy(paramArrayOfLookAndFeelInfo, 0, arrayOfLookAndFeelInfo, 0, paramArrayOfLookAndFeelInfo.length);
    (getLAFState()).installedLAFs = arrayOfLookAndFeelInfo;
  }
  
  public static void installLookAndFeel(LookAndFeelInfo paramLookAndFeelInfo) {
    LookAndFeelInfo[] arrayOfLookAndFeelInfo1 = getInstalledLookAndFeels();
    LookAndFeelInfo[] arrayOfLookAndFeelInfo2 = new LookAndFeelInfo[arrayOfLookAndFeelInfo1.length + 1];
    System.arraycopy(arrayOfLookAndFeelInfo1, 0, arrayOfLookAndFeelInfo2, 0, arrayOfLookAndFeelInfo1.length);
    arrayOfLookAndFeelInfo2[arrayOfLookAndFeelInfo1.length] = paramLookAndFeelInfo;
    setInstalledLookAndFeels(arrayOfLookAndFeelInfo2);
  }
  
  public static void installLookAndFeel(String paramString1, String paramString2) { installLookAndFeel(new LookAndFeelInfo(paramString1, paramString2)); }
  
  public static LookAndFeel getLookAndFeel() {
    maybeInitialize();
    return (getLAFState()).lookAndFeel;
  }
  
  public static void setLookAndFeel(LookAndFeel paramLookAndFeel) throws UnsupportedLookAndFeelException {
    if (paramLookAndFeel != null && !paramLookAndFeel.isSupportedLookAndFeel()) {
      String str = paramLookAndFeel.toString() + " not supported on this platform";
      throw new UnsupportedLookAndFeelException(str);
    } 
    LAFState lAFState = getLAFState();
    LookAndFeel lookAndFeel = lAFState.lookAndFeel;
    if (lookAndFeel != null)
      lookAndFeel.uninitialize(); 
    lAFState.lookAndFeel = paramLookAndFeel;
    if (paramLookAndFeel != null) {
      DefaultLookup.setDefaultLookup(null);
      paramLookAndFeel.initialize();
      lAFState.setLookAndFeelDefaults(paramLookAndFeel.getDefaults());
    } else {
      lAFState.setLookAndFeelDefaults(null);
    } 
    SwingPropertyChangeSupport swingPropertyChangeSupport = lAFState.getPropertyChangeSupport(false);
    if (swingPropertyChangeSupport != null)
      swingPropertyChangeSupport.firePropertyChange("lookAndFeel", lookAndFeel, paramLookAndFeel); 
  }
  
  public static void setLookAndFeel(String paramString) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    if ("javax.swing.plaf.metal.MetalLookAndFeel".equals(paramString)) {
      setLookAndFeel(new MetalLookAndFeel());
    } else {
      Class clazz = SwingUtilities.loadSystemClass(paramString);
      setLookAndFeel((LookAndFeel)clazz.newInstance());
    } 
  }
  
  public static String getSystemLookAndFeelClassName() {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("swing.systemlaf"));
    if (str1 != null)
      return str1; 
    OSInfo.OSType oSType = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
    if (oSType == OSInfo.OSType.WINDOWS)
      return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; 
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.desktop"));
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return ("gnome".equals(str2) && toolkit instanceof SunToolkit && ((SunToolkit)toolkit).isNativeGTKAvailable()) ? "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" : ((oSType == OSInfo.OSType.MACOSX && toolkit.getClass().getName().equals("sun.lwawt.macosx.LWCToolkit")) ? "com.apple.laf.AquaLookAndFeel" : ((oSType == OSInfo.OSType.SOLARIS) ? "com.sun.java.swing.plaf.motif.MotifLookAndFeel" : getCrossPlatformLookAndFeelClassName()));
  }
  
  public static String getCrossPlatformLookAndFeelClassName() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.crossplatformlaf"));
    return (str != null) ? str : "javax.swing.plaf.metal.MetalLookAndFeel";
  }
  
  public static UIDefaults getDefaults() {
    maybeInitialize();
    return (getLAFState()).multiUIDefaults;
  }
  
  public static Font getFont(Object paramObject) { return getDefaults().getFont(paramObject); }
  
  public static Font getFont(Object paramObject, Locale paramLocale) { return getDefaults().getFont(paramObject, paramLocale); }
  
  public static Color getColor(Object paramObject) { return getDefaults().getColor(paramObject); }
  
  public static Color getColor(Object paramObject, Locale paramLocale) { return getDefaults().getColor(paramObject, paramLocale); }
  
  public static Icon getIcon(Object paramObject) { return getDefaults().getIcon(paramObject); }
  
  public static Icon getIcon(Object paramObject, Locale paramLocale) { return getDefaults().getIcon(paramObject, paramLocale); }
  
  public static Border getBorder(Object paramObject) { return getDefaults().getBorder(paramObject); }
  
  public static Border getBorder(Object paramObject, Locale paramLocale) { return getDefaults().getBorder(paramObject, paramLocale); }
  
  public static String getString(Object paramObject) { return getDefaults().getString(paramObject); }
  
  public static String getString(Object paramObject, Locale paramLocale) { return getDefaults().getString(paramObject, paramLocale); }
  
  static String getString(Object paramObject, Component paramComponent) {
    Locale locale = (paramComponent == null) ? Locale.getDefault() : paramComponent.getLocale();
    return getString(paramObject, locale);
  }
  
  public static int getInt(Object paramObject) { return getDefaults().getInt(paramObject); }
  
  public static int getInt(Object paramObject, Locale paramLocale) { return getDefaults().getInt(paramObject, paramLocale); }
  
  public static boolean getBoolean(Object paramObject) { return getDefaults().getBoolean(paramObject); }
  
  public static boolean getBoolean(Object paramObject, Locale paramLocale) { return getDefaults().getBoolean(paramObject, paramLocale); }
  
  public static Insets getInsets(Object paramObject) { return getDefaults().getInsets(paramObject); }
  
  public static Insets getInsets(Object paramObject, Locale paramLocale) { return getDefaults().getInsets(paramObject, paramLocale); }
  
  public static Dimension getDimension(Object paramObject) { return getDefaults().getDimension(paramObject); }
  
  public static Dimension getDimension(Object paramObject, Locale paramLocale) { return getDefaults().getDimension(paramObject, paramLocale); }
  
  public static Object get(Object paramObject) { return getDefaults().get(paramObject); }
  
  public static Object get(Object paramObject, Locale paramLocale) { return getDefaults().get(paramObject, paramLocale); }
  
  public static Object put(Object paramObject1, Object paramObject2) { return getDefaults().put(paramObject1, paramObject2); }
  
  public static ComponentUI getUI(JComponent paramJComponent) {
    maybeInitialize();
    maybeInitializeFocusPolicy(paramJComponent);
    ComponentUI componentUI = null;
    LookAndFeel lookAndFeel = (getLAFState()).multiLookAndFeel;
    if (lookAndFeel != null)
      componentUI = lookAndFeel.getDefaults().getUI(paramJComponent); 
    if (componentUI == null)
      componentUI = getDefaults().getUI(paramJComponent); 
    return componentUI;
  }
  
  public static UIDefaults getLookAndFeelDefaults() {
    maybeInitialize();
    return getLAFState().getLookAndFeelDefaults();
  }
  
  private static LookAndFeel getMultiLookAndFeel() {
    LookAndFeel lookAndFeel = (getLAFState()).multiLookAndFeel;
    if (lookAndFeel == null) {
      String str1 = "javax.swing.plaf.multi.MultiLookAndFeel";
      String str2 = (getLAFState()).swingProps.getProperty("swing.plaf.multiplexinglaf", str1);
      try {
        Class clazz = SwingUtilities.loadSystemClass(str2);
        lookAndFeel = (LookAndFeel)clazz.newInstance();
      } catch (Exception exception) {
        System.err.println("UIManager: failed loading " + str2);
      } 
    } 
    return lookAndFeel;
  }
  
  public static void addAuxiliaryLookAndFeel(LookAndFeel paramLookAndFeel) throws UnsupportedLookAndFeelException {
    maybeInitialize();
    if (!paramLookAndFeel.isSupportedLookAndFeel())
      return; 
    Vector vector = (getLAFState()).auxLookAndFeels;
    if (vector == null)
      vector = new Vector(); 
    if (!vector.contains(paramLookAndFeel)) {
      vector.addElement(paramLookAndFeel);
      paramLookAndFeel.initialize();
      (getLAFState()).auxLookAndFeels = vector;
      if ((getLAFState()).multiLookAndFeel == null)
        (getLAFState()).multiLookAndFeel = getMultiLookAndFeel(); 
    } 
  }
  
  public static boolean removeAuxiliaryLookAndFeel(LookAndFeel paramLookAndFeel) {
    maybeInitialize();
    Vector vector = (getLAFState()).auxLookAndFeels;
    if (vector == null || vector.size() == 0)
      return false; 
    boolean bool = vector.removeElement(paramLookAndFeel);
    if (bool)
      if (vector.size() == 0) {
        (getLAFState()).auxLookAndFeels = null;
        (getLAFState()).multiLookAndFeel = null;
      } else {
        (getLAFState()).auxLookAndFeels = vector;
      }  
    paramLookAndFeel.uninitialize();
    return bool;
  }
  
  public static LookAndFeel[] getAuxiliaryLookAndFeels() {
    maybeInitialize();
    Vector vector = (getLAFState()).auxLookAndFeels;
    if (vector == null || vector.size() == 0)
      return null; 
    LookAndFeel[] arrayOfLookAndFeel = new LookAndFeel[vector.size()];
    for (byte b = 0; b < arrayOfLookAndFeel.length; b++)
      arrayOfLookAndFeel[b] = (LookAndFeel)vector.elementAt(b); 
    return arrayOfLookAndFeel;
  }
  
  public static void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    synchronized (classLock) {
      getLAFState().getPropertyChangeSupport(true).addPropertyChangeListener(paramPropertyChangeListener);
    } 
  }
  
  public static void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    synchronized (classLock) {
      getLAFState().getPropertyChangeSupport(true).removePropertyChangeListener(paramPropertyChangeListener);
    } 
  }
  
  public static PropertyChangeListener[] getPropertyChangeListeners() {
    synchronized (classLock) {
      return getLAFState().getPropertyChangeSupport(true).getPropertyChangeListeners();
    } 
  }
  
  private static Properties loadSwingProperties() {
    if (UIManager.class.getClassLoader() != null)
      return new Properties(); 
    final Properties props = new Properties();
    AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            OSInfo.OSType oSType = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
            if (oSType == OSInfo.OSType.MACOSX)
              props.put("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName()); 
            try {
              File file = new File(UIManager.makeSwingPropertiesFilename());
              if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                props.load(fileInputStream);
                fileInputStream.close();
              } 
            } catch (Exception exception) {}
            UIManager.checkProperty(props, "swing.defaultlaf");
            UIManager.checkProperty(props, "swing.auxiliarylaf");
            UIManager.checkProperty(props, "swing.plaf.multiplexinglaf");
            UIManager.checkProperty(props, "swing.installedlafs");
            UIManager.checkProperty(props, "swing.disablenavaids");
            return null;
          }
        });
    return properties;
  }
  
  private static void checkProperty(Properties paramProperties, String paramString) {
    String str = System.getProperty(paramString);
    if (str != null)
      paramProperties.put(paramString, str); 
  }
  
  private static void initializeInstalledLAFs(Properties paramProperties) {
    String str = paramProperties.getProperty("swing.installedlafs");
    if (str == null)
      return; 
    Vector vector1 = new Vector();
    StringTokenizer stringTokenizer = new StringTokenizer(str, ",", false);
    while (stringTokenizer.hasMoreTokens())
      vector1.addElement(stringTokenizer.nextToken()); 
    Vector vector2 = new Vector(vector1.size());
    for (String str1 : vector1) {
      String str2 = paramProperties.getProperty(makeInstalledLAFKey(str1, "name"), str1);
      String str3 = paramProperties.getProperty(makeInstalledLAFKey(str1, "class"));
      if (str3 != null)
        vector2.addElement(new LookAndFeelInfo(str2, str3)); 
    } 
    LookAndFeelInfo[] arrayOfLookAndFeelInfo = new LookAndFeelInfo[vector2.size()];
    for (byte b = 0; b < vector2.size(); b++)
      arrayOfLookAndFeelInfo[b] = (LookAndFeelInfo)vector2.elementAt(b); 
    (getLAFState()).installedLAFs = arrayOfLookAndFeelInfo;
  }
  
  private static void initializeDefaultLAF(Properties paramProperties) {
    if ((getLAFState()).lookAndFeel != null)
      return; 
    String str = null;
    HashMap hashMap = (HashMap)AppContext.getAppContext().remove("swing.lafdata");
    if (hashMap != null)
      str = (String)hashMap.remove("defaultlaf"); 
    if (str == null)
      str = getCrossPlatformLookAndFeelClassName(); 
    str = paramProperties.getProperty("swing.defaultlaf", str);
    try {
      setLookAndFeel(str);
    } catch (Exception exception) {
      throw new Error("Cannot load " + str);
    } 
    if (hashMap != null)
      for (Object object : hashMap.keySet())
        put(object, hashMap.get(object));  
  }
  
  private static void initializeAuxiliaryLAFs(Properties paramProperties) {
    String str = paramProperties.getProperty("swing.auxiliarylaf");
    if (str == null)
      return; 
    Vector vector = new Vector();
    StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
    while (stringTokenizer.hasMoreTokens()) {
      String str1 = stringTokenizer.nextToken();
      try {
        Class clazz = SwingUtilities.loadSystemClass(str1);
        LookAndFeel lookAndFeel = (LookAndFeel)clazz.newInstance();
        lookAndFeel.initialize();
        vector.addElement(lookAndFeel);
      } catch (Exception exception) {
        System.err.println("UIManager: failed loading auxiliary look and feel " + str1);
      } 
    } 
    if (vector.size() == 0) {
      vector = null;
    } else {
      (getLAFState()).multiLookAndFeel = getMultiLookAndFeel();
      if ((getLAFState()).multiLookAndFeel == null)
        vector = null; 
    } 
    (getLAFState()).auxLookAndFeels = vector;
  }
  
  private static void initializeSystemDefaults(Properties paramProperties) { (getLAFState()).swingProps = paramProperties; }
  
  private static void maybeInitialize() {
    synchronized (classLock) {
      if (!(getLAFState()).initialized) {
        (getLAFState()).initialized = true;
        initialize();
      } 
    } 
  }
  
  private static void maybeInitializeFocusPolicy(JComponent paramJComponent) {
    if (paramJComponent instanceof JRootPane)
      synchronized (classLock) {
        if (!(getLAFState()).focusPolicyInitialized) {
          (getLAFState()).focusPolicyInitialized = true;
          if (FocusManager.isFocusManagerEnabled())
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setDefaultFocusTraversalPolicy(new LayoutFocusTraversalPolicy()); 
        } 
      }  
  }
  
  private static void initialize() {
    Properties properties = loadSwingProperties();
    initializeSystemDefaults(properties);
    initializeDefaultLAF(properties);
    initializeAuxiliaryLAFs(properties);
    initializeInstalledLAFs(properties);
    if (RepaintManager.HANDLE_TOP_LEVEL_PAINT)
      PaintEventDispatcher.setPaintEventDispatcher(new SwingPaintEventDispatcher()); 
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
          public boolean postProcessKeyEvent(KeyEvent param1KeyEvent) {
            Component component = param1KeyEvent.getComponent();
            if ((!(component instanceof JComponent) || (component != null && !component.isEnabled())) && JComponent.KeyboardState.shouldProcess(param1KeyEvent) && SwingUtilities.processKeyBindings(param1KeyEvent)) {
              param1KeyEvent.consume();
              return true;
            } 
            return false;
          }
        });
    AWTAccessor.getComponentAccessor().setRequestFocusController(JComponent.focusController);
  }
  
  static  {
    ArrayList arrayList = new ArrayList(4);
    arrayList.add(new LookAndFeelInfo("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"));
    arrayList.add(new LookAndFeelInfo("Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel"));
    arrayList.add(new LookAndFeelInfo("CDE/Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"));
    OSInfo.OSType oSType = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
    if (oSType == OSInfo.OSType.WINDOWS) {
      arrayList.add(new LookAndFeelInfo("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"));
      if (Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive") != null)
        arrayList.add(new LookAndFeelInfo("Windows Classic", "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel")); 
    } else if (oSType == OSInfo.OSType.MACOSX) {
      arrayList.add(new LookAndFeelInfo("Mac OS X", "com.apple.laf.AquaLookAndFeel"));
    } else {
      arrayList.add(new LookAndFeelInfo("GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"));
    } 
    installedLAFs = (LookAndFeelInfo[])arrayList.toArray(new LookAndFeelInfo[arrayList.size()]);
  }
  
  private static class LAFState {
    Properties swingProps;
    
    private UIDefaults[] tables = new UIDefaults[2];
    
    boolean initialized = false;
    
    boolean focusPolicyInitialized = false;
    
    MultiUIDefaults multiUIDefaults = new MultiUIDefaults(this.tables);
    
    LookAndFeel lookAndFeel;
    
    LookAndFeel multiLookAndFeel = null;
    
    Vector<LookAndFeel> auxLookAndFeels = null;
    
    SwingPropertyChangeSupport changeSupport;
    
    UIManager.LookAndFeelInfo[] installedLAFs;
    
    private LAFState() {}
    
    UIDefaults getLookAndFeelDefaults() { return this.tables[0]; }
    
    void setLookAndFeelDefaults(UIDefaults param1UIDefaults) { this.tables[0] = param1UIDefaults; }
    
    UIDefaults getSystemDefaults() { return this.tables[1]; }
    
    void setSystemDefaults(UIDefaults param1UIDefaults) { this.tables[1] = param1UIDefaults; }
    
    public SwingPropertyChangeSupport getPropertyChangeSupport(boolean param1Boolean) {
      if (param1Boolean && this.changeSupport == null)
        this.changeSupport = new SwingPropertyChangeSupport(UIManager.class); 
      return this.changeSupport;
    }
  }
  
  public static class LookAndFeelInfo {
    private String name;
    
    private String className;
    
    public LookAndFeelInfo(String param1String1, String param1String2) {
      this.name = param1String1;
      this.className = param1String2;
    }
    
    public String getName() { return this.name; }
    
    public String getClassName() { return this.className; }
    
    public String toString() { return getClass().getName() + "[" + getName() + " " + getClassName() + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\UIManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */