package java.awt;

import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.AWTEventListener;
import java.awt.event.AWTEventListenerProxy;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.LightweightPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.HeadlessToolkit;
import sun.awt.NullComponentPeer;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.security.util.SecurityConstants;
import sun.util.CoreResourceBundleControl;

public abstract class Toolkit {
  private static LightweightPeer lightweightMarker;
  
  private static Toolkit toolkit;
  
  private static String atNames;
  
  private static ResourceBundle resources;
  
  private static ResourceBundle platformResources;
  
  private static boolean loaded = false;
  
  protected final Map<String, Object> desktopProperties = new HashMap();
  
  protected final PropertyChangeSupport desktopPropsSupport = createPropertyChangeSupport(this);
  
  private static final int LONG_BITS = 64;
  
  private int[] calls = new int[64];
  
  private AWTEventListener eventListener = null;
  
  private WeakHashMap<AWTEventListener, SelectiveAWTEventListener> listener2SelectiveListener = new WeakHashMap();
  
  protected abstract DesktopPeer createDesktopPeer(Desktop paramDesktop) throws HeadlessException;
  
  protected abstract ButtonPeer createButton(Button paramButton) throws HeadlessException;
  
  protected abstract TextFieldPeer createTextField(TextField paramTextField) throws HeadlessException;
  
  protected abstract LabelPeer createLabel(Label paramLabel) throws HeadlessException;
  
  protected abstract ListPeer createList(List paramList) throws HeadlessException;
  
  protected abstract CheckboxPeer createCheckbox(Checkbox paramCheckbox) throws HeadlessException;
  
  protected abstract ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) throws HeadlessException;
  
  protected abstract ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) throws HeadlessException;
  
  protected abstract TextAreaPeer createTextArea(TextArea paramTextArea) throws HeadlessException;
  
  protected abstract ChoicePeer createChoice(Choice paramChoice) throws HeadlessException;
  
  protected abstract FramePeer createFrame(Frame paramFrame) throws HeadlessException;
  
  protected abstract CanvasPeer createCanvas(Canvas paramCanvas);
  
  protected abstract PanelPeer createPanel(Panel paramPanel);
  
  protected abstract WindowPeer createWindow(Window paramWindow) throws HeadlessException;
  
  protected abstract DialogPeer createDialog(Dialog paramDialog) throws HeadlessException;
  
  protected abstract MenuBarPeer createMenuBar(MenuBar paramMenuBar) throws HeadlessException;
  
  protected abstract MenuPeer createMenu(Menu paramMenu) throws HeadlessException;
  
  protected abstract PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) throws HeadlessException;
  
  protected abstract MenuItemPeer createMenuItem(MenuItem paramMenuItem) throws HeadlessException;
  
  protected abstract FileDialogPeer createFileDialog(FileDialog paramFileDialog) throws HeadlessException;
  
  protected abstract CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) throws HeadlessException;
  
  protected MouseInfoPeer getMouseInfoPeer() { throw new UnsupportedOperationException("Not implemented"); }
  
  protected LightweightPeer createComponent(Component paramComponent) {
    if (lightweightMarker == null)
      lightweightMarker = new NullComponentPeer(); 
    return lightweightMarker;
  }
  
  @Deprecated
  protected abstract FontPeer getFontPeer(String paramString, int paramInt);
  
  protected void loadSystemColors(int[] paramArrayOfInt) throws HeadlessException { GraphicsEnvironment.checkHeadless(); }
  
  public void setDynamicLayout(boolean paramBoolean) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    if (this != getDefaultToolkit())
      getDefaultToolkit().setDynamicLayout(paramBoolean); 
  }
  
  protected boolean isDynamicLayoutSet() throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    return (this != getDefaultToolkit()) ? getDefaultToolkit().isDynamicLayoutSet() : 0;
  }
  
  public boolean isDynamicLayoutActive() throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    return (this != getDefaultToolkit()) ? getDefaultToolkit().isDynamicLayoutActive() : 0;
  }
  
  public abstract Dimension getScreenSize() throws HeadlessException;
  
  public abstract int getScreenResolution() throws HeadlessException;
  
  public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    return (this != getDefaultToolkit()) ? getDefaultToolkit().getScreenInsets(paramGraphicsConfiguration) : new Insets(0, 0, 0, 0);
  }
  
  public abstract ColorModel getColorModel() throws HeadlessException;
  
  @Deprecated
  public abstract String[] getFontList();
  
  @Deprecated
  public abstract FontMetrics getFontMetrics(Font paramFont);
  
  public abstract void sync();
  
  private static void initAssistiveTechnologies() {
    final String sep = File.separator;
    final Properties properties = new Properties();
    atNames = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            try {
              File file = new File(System.getProperty("user.home") + sep + ".accessibility.properties");
              FileInputStream fileInputStream = new FileInputStream(file);
              properties.load(fileInputStream);
              fileInputStream.close();
            } catch (Exception exception) {}
            if (properties.size() == 0)
              try {
                File file = new File(System.getProperty("java.home") + sep + "lib" + sep + "accessibility.properties");
                FileInputStream fileInputStream = new FileInputStream(file);
                properties.load(fileInputStream);
                fileInputStream.close();
              } catch (Exception exception) {} 
            String str1 = System.getProperty("javax.accessibility.screen_magnifier_present");
            if (str1 == null) {
              str1 = properties.getProperty("screen_magnifier_present", null);
              if (str1 != null)
                System.setProperty("javax.accessibility.screen_magnifier_present", str1); 
            } 
            String str2 = System.getProperty("javax.accessibility.assistive_technologies");
            if (str2 == null) {
              str2 = properties.getProperty("assistive_technologies", null);
              if (str2 != null)
                System.setProperty("javax.accessibility.assistive_technologies", str2); 
            } 
            return str2;
          }
        });
  }
  
  private static void loadAssistiveTechnologies() {
    if (atNames != null) {
      ClassLoader classLoader = ClassLoader.getSystemClassLoader();
      StringTokenizer stringTokenizer = new StringTokenizer(atNames, " ,");
      while (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken();
        try {
          Class clazz;
          if (classLoader != null) {
            clazz = classLoader.loadClass(str);
          } else {
            clazz = Class.forName(str);
          } 
          clazz.newInstance();
        } catch (ClassNotFoundException classNotFoundException) {
          throw new AWTError("Assistive Technology not found: " + str);
        } catch (InstantiationException instantiationException) {
          throw new AWTError("Could not instantiate Assistive Technology: " + str);
        } catch (IllegalAccessException illegalAccessException) {
          throw new AWTError("Could not access Assistive Technology: " + str);
        } catch (Exception exception) {
          throw new AWTError("Error trying to install Assistive Technology: " + str + " " + exception);
        } 
      } 
    } 
  }
  
  public static Toolkit getDefaultToolkit() {
    if (toolkit == null) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              Class clazz = null;
              String str = System.getProperty("awt.toolkit");
              try {
                clazz = Class.forName(str);
              } catch (ClassNotFoundException classNotFoundException) {
                ClassLoader classLoader = ClassLoader.getSystemClassLoader();
                if (classLoader != null)
                  try {
                    clazz = classLoader.loadClass(str);
                  } catch (ClassNotFoundException classNotFoundException1) {
                    throw new AWTError("Toolkit not found: " + str);
                  }  
              } 
              try {
                if (clazz != null) {
                  toolkit = (Toolkit)clazz.newInstance();
                  if (GraphicsEnvironment.isHeadless())
                    toolkit = new HeadlessToolkit(toolkit); 
                } 
              } catch (InstantiationException instantiationException) {
                throw new AWTError("Could not instantiate Toolkit: " + str);
              } catch (IllegalAccessException illegalAccessException) {
                throw new AWTError("Could not access Toolkit: " + str);
              } 
              return null;
            }
          });
      loadAssistiveTechnologies();
    } 
    return toolkit;
  }
  
  public abstract Image getImage(String paramString);
  
  public abstract Image getImage(URL paramURL);
  
  public abstract Image createImage(String paramString);
  
  public abstract Image createImage(URL paramURL);
  
  public abstract boolean prepareImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
  
  public abstract int checkImage(Image paramImage, int paramInt1, int paramInt2, ImageObserver paramImageObserver);
  
  public abstract Image createImage(ImageProducer paramImageProducer);
  
  public Image createImage(byte[] paramArrayOfByte) { return createImage(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public abstract Image createImage(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties);
  
  public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes) { return (this != getDefaultToolkit()) ? getDefaultToolkit().getPrintJob(paramFrame, paramString, paramJobAttributes, paramPageAttributes) : getPrintJob(paramFrame, paramString, null); }
  
  public abstract void beep();
  
  public abstract Clipboard getSystemClipboard() throws HeadlessException;
  
  public Clipboard getSystemSelection() throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    if (this != getDefaultToolkit())
      return getDefaultToolkit().getSystemSelection(); 
    GraphicsEnvironment.checkHeadless();
    return null;
  }
  
  public int getMenuShortcutKeyMask() throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    return 2;
  }
  
  public boolean getLockingKeyState(int paramInt) throws UnsupportedOperationException {
    GraphicsEnvironment.checkHeadless();
    if (paramInt != 20 && paramInt != 144 && paramInt != 145 && paramInt != 262)
      throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState"); 
    throw new UnsupportedOperationException("Toolkit.getLockingKeyState");
  }
  
  public void setLockingKeyState(int paramInt, boolean paramBoolean) throws UnsupportedOperationException {
    GraphicsEnvironment.checkHeadless();
    if (paramInt != 20 && paramInt != 144 && paramInt != 145 && paramInt != 262)
      throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState"); 
    throw new UnsupportedOperationException("Toolkit.setLockingKeyState");
  }
  
  protected static Container getNativeContainer(Component paramComponent) { return paramComponent.getNativeContainer(); }
  
  public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException, HeadlessException { return (this != getDefaultToolkit()) ? getDefaultToolkit().createCustomCursor(paramImage, paramPoint, paramString) : new Cursor(0); }
  
  public Dimension getBestCursorSize(int paramInt1, int paramInt2) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    return (this != getDefaultToolkit()) ? getDefaultToolkit().getBestCursorSize(paramInt1, paramInt2) : new Dimension(0, 0);
  }
  
  public int getMaximumCursorColors() throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    return (this != getDefaultToolkit()) ? getDefaultToolkit().getMaximumCursorColors() : 0;
  }
  
  public boolean isFrameStateSupported(int paramInt) throws UnsupportedOperationException {
    GraphicsEnvironment.checkHeadless();
    return (this != getDefaultToolkit()) ? getDefaultToolkit().isFrameStateSupported(paramInt) : ((paramInt == 0) ? 1 : 0);
  }
  
  private static void setPlatformResources(ResourceBundle paramResourceBundle) { platformResources = paramResourceBundle; }
  
  private static native void initIDs();
  
  static void loadLibraries() {
    if (!loaded) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              System.loadLibrary("awt");
              return null;
            }
          });
      loaded = true;
    } 
  }
  
  public static String getProperty(String paramString1, String paramString2) {
    if (platformResources != null)
      try {
        return platformResources.getString(paramString1);
      } catch (MissingResourceException missingResourceException) {} 
    if (resources != null)
      try {
        return resources.getString(paramString1);
      } catch (MissingResourceException missingResourceException) {} 
    return paramString2;
  }
  
  public final EventQueue getSystemEventQueue() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION); 
    return getSystemEventQueueImpl();
  }
  
  protected abstract EventQueue getSystemEventQueueImpl();
  
  static EventQueue getEventQueue() { return getDefaultToolkit().getSystemEventQueueImpl(); }
  
  public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException;
  
  public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener) { return null; }
  
  public final Object getDesktopProperty(String paramString) {
    if (this instanceof HeadlessToolkit)
      return ((HeadlessToolkit)this).getUnderlyingToolkit().getDesktopProperty(paramString); 
    if (this.desktopProperties.isEmpty())
      initializeDesktopProperties(); 
    if (paramString.equals("awt.dynamicLayoutSupported"))
      return getDefaultToolkit().lazilyLoadDesktopProperty(paramString); 
    Object object = this.desktopProperties.get(paramString);
    if (object == null) {
      object = lazilyLoadDesktopProperty(paramString);
      if (object != null)
        setDesktopProperty(paramString, object); 
    } 
    if (object instanceof RenderingHints)
      object = ((RenderingHints)object).clone(); 
    return object;
  }
  
  protected final void setDesktopProperty(String paramString, Object paramObject) {
    Object object;
    if (this instanceof HeadlessToolkit) {
      ((HeadlessToolkit)this).getUnderlyingToolkit().setDesktopProperty(paramString, paramObject);
      return;
    } 
    synchronized (this) {
      object = this.desktopProperties.get(paramString);
      this.desktopProperties.put(paramString, paramObject);
    } 
    if (object != null || paramObject != null)
      this.desktopPropsSupport.firePropertyChange(paramString, object, paramObject); 
  }
  
  protected Object lazilyLoadDesktopProperty(String paramString) { return null; }
  
  protected void initializeDesktopProperties() {}
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { this.desktopPropsSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) { this.desktopPropsSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener); }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return this.desktopPropsSupport.getPropertyChangeListeners(); }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString) { return this.desktopPropsSupport.getPropertyChangeListeners(paramString); }
  
  public boolean isAlwaysOnTopSupported() throws HeadlessException { return true; }
  
  public abstract boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType);
  
  public abstract boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType);
  
  private static AWTEventListener deProxyAWTEventListener(AWTEventListener paramAWTEventListener) {
    AWTEventListener aWTEventListener = paramAWTEventListener;
    if (aWTEventListener == null)
      return null; 
    if (paramAWTEventListener instanceof AWTEventListenerProxy)
      aWTEventListener = (AWTEventListener)((AWTEventListenerProxy)paramAWTEventListener).getListener(); 
    return aWTEventListener;
  }
  
  public void addAWTEventListener(AWTEventListener paramAWTEventListener, long paramLong) {
    AWTEventListener aWTEventListener = deProxyAWTEventListener(paramAWTEventListener);
    if (aWTEventListener == null)
      return; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION); 
    synchronized (this) {
      SelectiveAWTEventListener selectiveAWTEventListener = (SelectiveAWTEventListener)this.listener2SelectiveListener.get(aWTEventListener);
      if (selectiveAWTEventListener == null) {
        selectiveAWTEventListener = new SelectiveAWTEventListener(aWTEventListener, paramLong);
        this.listener2SelectiveListener.put(aWTEventListener, selectiveAWTEventListener);
        this.eventListener = ToolkitEventMulticaster.add(this.eventListener, selectiveAWTEventListener);
      } 
      selectiveAWTEventListener.orEventMasks(paramLong);
      enabledOnToolkitMask |= paramLong;
      long l = paramLong;
      for (byte b = 0; b < 64 && l != 0L; b++) {
        if ((l & 0x1L) != 0L)
          this.calls[b] = this.calls[b] + 1; 
        l >>>= true;
      } 
    } 
  }
  
  public void removeAWTEventListener(AWTEventListener paramAWTEventListener) {
    AWTEventListener aWTEventListener = deProxyAWTEventListener(paramAWTEventListener);
    if (paramAWTEventListener == null)
      return; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION); 
    synchronized (this) {
      SelectiveAWTEventListener selectiveAWTEventListener = (SelectiveAWTEventListener)this.listener2SelectiveListener.get(aWTEventListener);
      if (selectiveAWTEventListener != null) {
        this.listener2SelectiveListener.remove(aWTEventListener);
        int[] arrayOfInt = selectiveAWTEventListener.getCalls();
        for (byte b = 0; b < 64; b++) {
          this.calls[b] = this.calls[b] - arrayOfInt[b];
          assert this.calls[b] >= 0 : "Negative Listeners count";
          if (this.calls[b] == 0)
            enabledOnToolkitMask &= (1L << b ^ 0xFFFFFFFFFFFFFFFFL); 
        } 
      } 
      this.eventListener = ToolkitEventMulticaster.remove(this.eventListener, (selectiveAWTEventListener == null) ? aWTEventListener : selectiveAWTEventListener);
    } 
  }
  
  static boolean enabledOnToolkit(long paramLong) { return ((enabledOnToolkitMask & paramLong) != 0L); }
  
  int countAWTEventListeners(long paramLong) {
    byte b;
    for (b = 0; paramLong != 0L; b++)
      paramLong >>>= true; 
    return this.calls[--b];
  }
  
  public AWTEventListener[] getAWTEventListeners() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION); 
    synchronized (this) {
      EventListener[] arrayOfEventListener = ToolkitEventMulticaster.getListeners(this.eventListener, AWTEventListener.class);
      AWTEventListener[] arrayOfAWTEventListener = new AWTEventListener[arrayOfEventListener.length];
      for (byte b = 0; b < arrayOfEventListener.length; b++) {
        SelectiveAWTEventListener selectiveAWTEventListener = (SelectiveAWTEventListener)arrayOfEventListener[b];
        AWTEventListener aWTEventListener = selectiveAWTEventListener.getListener();
        arrayOfAWTEventListener[b] = new AWTEventListenerProxy(selectiveAWTEventListener.getEventMask(), aWTEventListener);
      } 
      return arrayOfAWTEventListener;
    } 
  }
  
  public AWTEventListener[] getAWTEventListeners(long paramLong) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.ALL_AWT_EVENTS_PERMISSION); 
    synchronized (this) {
      EventListener[] arrayOfEventListener = ToolkitEventMulticaster.getListeners(this.eventListener, AWTEventListener.class);
      ArrayList arrayList = new ArrayList(arrayOfEventListener.length);
      for (byte b = 0; b < arrayOfEventListener.length; b++) {
        SelectiveAWTEventListener selectiveAWTEventListener = (SelectiveAWTEventListener)arrayOfEventListener[b];
        if ((selectiveAWTEventListener.getEventMask() & paramLong) == paramLong)
          arrayList.add(new AWTEventListenerProxy(selectiveAWTEventListener.getEventMask(), selectiveAWTEventListener.getListener())); 
      } 
      return (AWTEventListener[])arrayList.toArray(new AWTEventListener[0]);
    } 
  }
  
  void notifyAWTEventListeners(AWTEvent paramAWTEvent) {
    if (this instanceof HeadlessToolkit) {
      ((HeadlessToolkit)this).getUnderlyingToolkit().notifyAWTEventListeners(paramAWTEvent);
      return;
    } 
    AWTEventListener aWTEventListener = this.eventListener;
    if (aWTEventListener != null)
      aWTEventListener.eventDispatched(paramAWTEvent); 
  }
  
  public abstract Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) throws HeadlessException;
  
  private static PropertyChangeSupport createPropertyChangeSupport(Toolkit paramToolkit) { return (paramToolkit instanceof SunToolkit || paramToolkit instanceof HeadlessToolkit) ? new DesktopPropertyChangeSupport(paramToolkit) : new PropertyChangeSupport(paramToolkit); }
  
  public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    return getDefaultToolkit().areExtraMouseButtonsEnabled();
  }
  
  static  {
    AWTAccessor.setToolkitAccessor(new AWTAccessor.ToolkitAccessor() {
          public void setPlatformResources(ResourceBundle param1ResourceBundle) { Toolkit.setPlatformResources(param1ResourceBundle); }
        });
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              resources = ResourceBundle.getBundle("sun.awt.resources.awt", Locale.getDefault(), ClassLoader.getSystemClassLoader(), CoreResourceBundleControl.getRBControlInstance());
            } catch (MissingResourceException missingResourceException) {}
            return null;
          }
        });
    loadLibraries();
    initAssistiveTechnologies();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
  }
  
  private static class DesktopPropertyChangeSupport extends PropertyChangeSupport {
    private static final StringBuilder PROP_CHANGE_SUPPORT_KEY = new StringBuilder("desktop property change support key");
    
    private final Object source;
    
    public DesktopPropertyChangeSupport(Object param1Object) {
      super(param1Object);
      this.source = param1Object;
    }
    
    public void addPropertyChangeListener(String param1String, PropertyChangeListener param1PropertyChangeListener) {
      PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null == propertyChangeSupport) {
        propertyChangeSupport = new PropertyChangeSupport(this.source);
        AppContext.getAppContext().put(PROP_CHANGE_SUPPORT_KEY, propertyChangeSupport);
      } 
      propertyChangeSupport.addPropertyChangeListener(param1String, param1PropertyChangeListener);
    }
    
    public void removePropertyChangeListener(String param1String, PropertyChangeListener param1PropertyChangeListener) {
      PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null != propertyChangeSupport)
        propertyChangeSupport.removePropertyChangeListener(param1String, param1PropertyChangeListener); 
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners() {
      PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      return (null != propertyChangeSupport) ? propertyChangeSupport.getPropertyChangeListeners() : new PropertyChangeListener[0];
    }
    
    public PropertyChangeListener[] getPropertyChangeListeners(String param1String) {
      PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      return (null != propertyChangeSupport) ? propertyChangeSupport.getPropertyChangeListeners(param1String) : new PropertyChangeListener[0];
    }
    
    public void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {
      PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null == propertyChangeSupport) {
        propertyChangeSupport = new PropertyChangeSupport(this.source);
        AppContext.getAppContext().put(PROP_CHANGE_SUPPORT_KEY, propertyChangeSupport);
      } 
      propertyChangeSupport.addPropertyChangeListener(param1PropertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {
      PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
      if (null != propertyChangeSupport)
        propertyChangeSupport.removePropertyChangeListener(param1PropertyChangeListener); 
    }
    
    public void firePropertyChange(final PropertyChangeEvent evt) {
      Object object1 = param1PropertyChangeEvent.getOldValue();
      Object object2 = param1PropertyChangeEvent.getNewValue();
      String str = param1PropertyChangeEvent.getPropertyName();
      if (object1 != null && object2 != null && object1.equals(object2))
        return; 
      Runnable runnable = new Runnable() {
          public void run() {
            PropertyChangeSupport propertyChangeSupport = (PropertyChangeSupport)AppContext.getAppContext().get(PROP_CHANGE_SUPPORT_KEY);
            if (null != propertyChangeSupport)
              propertyChangeSupport.firePropertyChange(evt); 
          }
        };
      AppContext appContext;
      for (AppContext appContext1 : (appContext = AppContext.getAppContext()).getAppContexts()) {
        if (null == appContext1 || appContext1.isDisposed())
          continue; 
        if (appContext == appContext1) {
          runnable.run();
          continue;
        } 
        PeerEvent peerEvent = new PeerEvent(this.source, runnable, 2L);
        SunToolkit.postEvent(appContext1, peerEvent);
      } 
    }
  }
  
  private class SelectiveAWTEventListener implements AWTEventListener {
    AWTEventListener listener;
    
    private long eventMask;
    
    int[] calls = new int[64];
    
    public AWTEventListener getListener() { return this.listener; }
    
    public long getEventMask() { return this.eventMask; }
    
    public int[] getCalls() { return this.calls; }
    
    public void orEventMasks(long param1Long) {
      this.eventMask |= param1Long;
      for (byte b = 0; b < 64 && param1Long != 0L; b++) {
        if ((param1Long & 0x1L) != 0L)
          this.calls[b] = this.calls[b] + 1; 
        param1Long >>>= true;
      } 
    }
    
    SelectiveAWTEventListener(AWTEventListener param1AWTEventListener, long param1Long) {
      this.listener = param1AWTEventListener;
      this.eventMask = param1Long;
    }
    
    public void eventDispatched(AWTEvent param1AWTEvent) {
      long l = 0L;
      if (((l = this.eventMask & 0x1L) != 0L && param1AWTEvent.id >= 100 && param1AWTEvent.id <= 103) || ((l = this.eventMask & 0x2L) != 0L && param1AWTEvent.id >= 300 && param1AWTEvent.id <= 301) || ((l = this.eventMask & 0x4L) != 0L && param1AWTEvent.id >= 1004 && param1AWTEvent.id <= 1005) || ((l = this.eventMask & 0x8L) != 0L && param1AWTEvent.id >= 400 && param1AWTEvent.id <= 402) || ((l = this.eventMask & 0x20000L) != 0L && param1AWTEvent.id == 507) || ((l = this.eventMask & 0x20L) != 0L && (param1AWTEvent.id == 503 || param1AWTEvent.id == 506)) || ((l = this.eventMask & 0x10L) != 0L && param1AWTEvent.id != 503 && param1AWTEvent.id != 506 && param1AWTEvent.id != 507 && param1AWTEvent.id >= 500 && param1AWTEvent.id <= 507) || ((l = this.eventMask & 0x40L) != 0L && param1AWTEvent.id >= 200 && param1AWTEvent.id <= 209) || ((l = this.eventMask & 0x80L) != 0L && param1AWTEvent.id >= 1001 && param1AWTEvent.id <= 1001) || ((l = this.eventMask & 0x100L) != 0L && param1AWTEvent.id >= 601 && param1AWTEvent.id <= 601) || ((l = this.eventMask & 0x200L) != 0L && param1AWTEvent.id >= 701 && param1AWTEvent.id <= 701) || ((l = this.eventMask & 0x400L) != 0L && param1AWTEvent.id >= 900 && param1AWTEvent.id <= 900) || ((l = this.eventMask & 0x800L) != 0L && param1AWTEvent.id >= 1100 && param1AWTEvent.id <= 1101) || ((l = this.eventMask & 0x2000L) != 0L && param1AWTEvent.id >= 800 && param1AWTEvent.id <= 801) || ((l = this.eventMask & 0x4000L) != 0L && param1AWTEvent.id >= 1200 && param1AWTEvent.id <= 1200) || ((l = this.eventMask & 0x8000L) != 0L && param1AWTEvent.id == 1400) || ((l = this.eventMask & 0x10000L) != 0L && (param1AWTEvent.id == 1401 || param1AWTEvent.id == 1402)) || ((l = this.eventMask & 0x40000L) != 0L && param1AWTEvent.id == 209) || ((l = this.eventMask & 0x80000L) != 0L && (param1AWTEvent.id == 207 || param1AWTEvent.id == 208)) || ((l = this.eventMask & 0xFFFFFFFF80000000L) != 0L && param1AWTEvent instanceof sun.awt.UngrabEvent)) {
        byte b1 = 0;
        long l1 = l;
        while (l1 != 0L) {
          l1 >>>= true;
          b1++;
        } 
        b1--;
        for (byte b2 = 0; b2 < this.calls[b1]; b2++)
          this.listener.eventDispatched(param1AWTEvent); 
      } 
    }
  }
  
  private static class ToolkitEventMulticaster extends AWTEventMulticaster implements AWTEventListener {
    ToolkitEventMulticaster(AWTEventListener param1AWTEventListener1, AWTEventListener param1AWTEventListener2) { super(param1AWTEventListener1, param1AWTEventListener2); }
    
    static AWTEventListener add(AWTEventListener param1AWTEventListener1, AWTEventListener param1AWTEventListener2) { return (param1AWTEventListener1 == null) ? param1AWTEventListener2 : ((param1AWTEventListener2 == null) ? param1AWTEventListener1 : new ToolkitEventMulticaster(param1AWTEventListener1, param1AWTEventListener2)); }
    
    static AWTEventListener remove(AWTEventListener param1AWTEventListener1, AWTEventListener param1AWTEventListener2) { return (AWTEventListener)removeInternal(param1AWTEventListener1, param1AWTEventListener2); }
    
    protected EventListener remove(EventListener param1EventListener) {
      if (param1EventListener == this.a)
        return this.b; 
      if (param1EventListener == this.b)
        return this.a; 
      AWTEventListener aWTEventListener1 = (AWTEventListener)removeInternal(this.a, param1EventListener);
      AWTEventListener aWTEventListener2 = (AWTEventListener)removeInternal(this.b, param1EventListener);
      return (aWTEventListener1 == this.a && aWTEventListener2 == this.b) ? this : add(aWTEventListener1, aWTEventListener2);
    }
    
    public void eventDispatched(AWTEvent param1AWTEvent) {
      ((AWTEventListener)this.a).eventDispatched(param1AWTEvent);
      ((AWTEventListener)this.b).eventDispatched(param1AWTEvent);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Toolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */