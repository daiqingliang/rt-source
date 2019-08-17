package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PageAttributes;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.im.spi.InputMethodDescriptor;
import java.awt.image.ColorModel;
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
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.swing.text.JTextComponent;
import sun.awt.AWTAccessor;
import sun.awt.AWTAutoShutdown;
import sun.awt.AppContext;
import sun.awt.LightweightFrame;
import sun.awt.SunToolkit;
import sun.awt.Win32GraphicsDevice;
import sun.awt.Win32GraphicsEnvironment;
import sun.awt.datatransfer.DataTransferer;
import sun.font.FontManager;
import sun.font.FontManagerFactory;
import sun.font.SunFontManager;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.d3d.D3DRenderQueue;
import sun.java2d.opengl.OGLRenderQueue;
import sun.misc.PerformanceLogger;
import sun.misc.ThreadGroupUtils;
import sun.print.PrintJob2D;
import sun.security.util.SecurityConstants;
import sun.util.logging.PlatformLogger;

public final class WToolkit extends SunToolkit implements Runnable {
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WToolkit");
  
  public static final String XPSTYLE_THEME_ACTIVE = "win.xpstyle.themeActive";
  
  static GraphicsConfiguration config;
  
  WClipboard clipboard;
  
  private Hashtable<String, FontPeer> cacheFontPeer;
  
  private WDesktopProperties wprops;
  
  protected boolean dynamicLayoutSetting = false;
  
  private static boolean areExtraMouseButtonsEnabled = true;
  
  private static boolean loaded = false;
  
  private final Object anchor = new Object();
  
  private boolean inited = false;
  
  static ColorModel screenmodel;
  
  private static final String prefix = "DnD.Cursor.";
  
  private static final String postfix = ".32x32";
  
  private static final String awtPrefix = "awt.";
  
  private static final String dndPrefix = "DnD.";
  
  private static final WeakReference<Component> NULL_COMPONENT_WR;
  
  private static native void initIDs();
  
  public static void loadLibraries() {
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
  
  private static native String getWindowsVersion();
  
  private static native void disableCustomPalette();
  
  public static void resetGC() {
    if (GraphicsEnvironment.isHeadless()) {
      config = null;
    } else {
      config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    } 
  }
  
  public static native boolean embeddedInit();
  
  public static native boolean embeddedDispose();
  
  public native void embeddedEventLoopIdleProcessing();
  
  private static native void postDispose();
  
  private static native boolean startToolkitThread(Runnable paramRunnable, ThreadGroup paramThreadGroup);
  
  public WToolkit() {
    if (PerformanceLogger.loggingEnabled())
      PerformanceLogger.setTime("WToolkit construction"); 
    Disposer.addRecord(this.anchor, new ToolkitDisposer());
    AWTAutoShutdown.notifyToolkitThreadBusy();
    ThreadGroup threadGroup = (ThreadGroup)AccessController.doPrivileged(ThreadGroupUtils::getRootThreadGroup);
    if (!startToolkitThread(this, threadGroup)) {
      Thread thread = new Thread(threadGroup, this, "AWT-Windows");
      thread.setDaemon(true);
      thread.start();
    } 
    try {
      synchronized (this) {
        while (!this.inited)
          wait(); 
      } 
    } catch (InterruptedException interruptedException) {}
    setDynamicLayout(true);
    areExtraMouseButtonsEnabled = Boolean.parseBoolean(System.getProperty("sun.awt.enableExtraMouseButtons", "true"));
    System.setProperty("sun.awt.enableExtraMouseButtons", "" + areExtraMouseButtonsEnabled);
    setExtraMouseButtonsEnabledNative(areExtraMouseButtonsEnabled);
  }
  
  private final void registerShutdownHook() { AccessController.doPrivileged(() -> {
          Thread thread = new Thread(ThreadGroupUtils.getRootThreadGroup(), this::shutdown);
          thread.setContextClassLoader(null);
          Runtime.getRuntime().addShutdownHook(thread);
          return null;
        }); }
  
  public void run() {
    AccessController.doPrivileged(() -> {
          Thread.currentThread().setContextClassLoader(null);
          return null;
        });
    Thread.currentThread().setPriority(6);
    boolean bool = init();
    if (bool)
      registerShutdownHook(); 
    synchronized (this) {
      this.inited = true;
      notifyAll();
    } 
    if (bool)
      eventLoop(); 
  }
  
  private native boolean init();
  
  private native void eventLoop();
  
  private native void shutdown();
  
  static native void startSecondaryEventLoop();
  
  static native void quitSecondaryEventLoop();
  
  public ButtonPeer createButton(Button paramButton) {
    WButtonPeer wButtonPeer = new WButtonPeer(paramButton);
    targetCreatedPeer(paramButton, wButtonPeer);
    return wButtonPeer;
  }
  
  public TextFieldPeer createTextField(TextField paramTextField) {
    WTextFieldPeer wTextFieldPeer = new WTextFieldPeer(paramTextField);
    targetCreatedPeer(paramTextField, wTextFieldPeer);
    return wTextFieldPeer;
  }
  
  public LabelPeer createLabel(Label paramLabel) {
    WLabelPeer wLabelPeer = new WLabelPeer(paramLabel);
    targetCreatedPeer(paramLabel, wLabelPeer);
    return wLabelPeer;
  }
  
  public ListPeer createList(List paramList) {
    WListPeer wListPeer = new WListPeer(paramList);
    targetCreatedPeer(paramList, wListPeer);
    return wListPeer;
  }
  
  public CheckboxPeer createCheckbox(Checkbox paramCheckbox) {
    WCheckboxPeer wCheckboxPeer = new WCheckboxPeer(paramCheckbox);
    targetCreatedPeer(paramCheckbox, wCheckboxPeer);
    return wCheckboxPeer;
  }
  
  public ScrollbarPeer createScrollbar(Scrollbar paramScrollbar) {
    WScrollbarPeer wScrollbarPeer = new WScrollbarPeer(paramScrollbar);
    targetCreatedPeer(paramScrollbar, wScrollbarPeer);
    return wScrollbarPeer;
  }
  
  public ScrollPanePeer createScrollPane(ScrollPane paramScrollPane) {
    WScrollPanePeer wScrollPanePeer = new WScrollPanePeer(paramScrollPane);
    targetCreatedPeer(paramScrollPane, wScrollPanePeer);
    return wScrollPanePeer;
  }
  
  public TextAreaPeer createTextArea(TextArea paramTextArea) {
    WTextAreaPeer wTextAreaPeer = new WTextAreaPeer(paramTextArea);
    targetCreatedPeer(paramTextArea, wTextAreaPeer);
    return wTextAreaPeer;
  }
  
  public ChoicePeer createChoice(Choice paramChoice) {
    WChoicePeer wChoicePeer = new WChoicePeer(paramChoice);
    targetCreatedPeer(paramChoice, wChoicePeer);
    return wChoicePeer;
  }
  
  public FramePeer createFrame(Frame paramFrame) {
    WFramePeer wFramePeer = new WFramePeer(paramFrame);
    targetCreatedPeer(paramFrame, wFramePeer);
    return wFramePeer;
  }
  
  public FramePeer createLightweightFrame(LightweightFrame paramLightweightFrame) {
    WLightweightFramePeer wLightweightFramePeer = new WLightweightFramePeer(paramLightweightFrame);
    targetCreatedPeer(paramLightweightFrame, wLightweightFramePeer);
    return wLightweightFramePeer;
  }
  
  public CanvasPeer createCanvas(Canvas paramCanvas) {
    WCanvasPeer wCanvasPeer = new WCanvasPeer(paramCanvas);
    targetCreatedPeer(paramCanvas, wCanvasPeer);
    return wCanvasPeer;
  }
  
  public void disableBackgroundErase(Canvas paramCanvas) {
    WCanvasPeer wCanvasPeer = (WCanvasPeer)paramCanvas.getPeer();
    if (wCanvasPeer == null)
      throw new IllegalStateException("Canvas must have a valid peer"); 
    wCanvasPeer.disableBackgroundErase();
  }
  
  public PanelPeer createPanel(Panel paramPanel) {
    WPanelPeer wPanelPeer = new WPanelPeer(paramPanel);
    targetCreatedPeer(paramPanel, wPanelPeer);
    return wPanelPeer;
  }
  
  public WindowPeer createWindow(Window paramWindow) {
    WWindowPeer wWindowPeer = new WWindowPeer(paramWindow);
    targetCreatedPeer(paramWindow, wWindowPeer);
    return wWindowPeer;
  }
  
  public DialogPeer createDialog(Dialog paramDialog) {
    WDialogPeer wDialogPeer = new WDialogPeer(paramDialog);
    targetCreatedPeer(paramDialog, wDialogPeer);
    return wDialogPeer;
  }
  
  public FileDialogPeer createFileDialog(FileDialog paramFileDialog) {
    WFileDialogPeer wFileDialogPeer = new WFileDialogPeer(paramFileDialog);
    targetCreatedPeer(paramFileDialog, wFileDialogPeer);
    return wFileDialogPeer;
  }
  
  public MenuBarPeer createMenuBar(MenuBar paramMenuBar) {
    WMenuBarPeer wMenuBarPeer = new WMenuBarPeer(paramMenuBar);
    targetCreatedPeer(paramMenuBar, wMenuBarPeer);
    return wMenuBarPeer;
  }
  
  public MenuPeer createMenu(Menu paramMenu) {
    WMenuPeer wMenuPeer = new WMenuPeer(paramMenu);
    targetCreatedPeer(paramMenu, wMenuPeer);
    return wMenuPeer;
  }
  
  public PopupMenuPeer createPopupMenu(PopupMenu paramPopupMenu) {
    WPopupMenuPeer wPopupMenuPeer = new WPopupMenuPeer(paramPopupMenu);
    targetCreatedPeer(paramPopupMenu, wPopupMenuPeer);
    return wPopupMenuPeer;
  }
  
  public MenuItemPeer createMenuItem(MenuItem paramMenuItem) {
    WMenuItemPeer wMenuItemPeer = new WMenuItemPeer(paramMenuItem);
    targetCreatedPeer(paramMenuItem, wMenuItemPeer);
    return wMenuItemPeer;
  }
  
  public CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem paramCheckboxMenuItem) {
    WCheckboxMenuItemPeer wCheckboxMenuItemPeer = new WCheckboxMenuItemPeer(paramCheckboxMenuItem);
    targetCreatedPeer(paramCheckboxMenuItem, wCheckboxMenuItemPeer);
    return wCheckboxMenuItemPeer;
  }
  
  public RobotPeer createRobot(Robot paramRobot, GraphicsDevice paramGraphicsDevice) { return new WRobotPeer(paramGraphicsDevice); }
  
  public WEmbeddedFramePeer createEmbeddedFrame(WEmbeddedFrame paramWEmbeddedFrame) {
    WEmbeddedFramePeer wEmbeddedFramePeer = new WEmbeddedFramePeer(paramWEmbeddedFrame);
    targetCreatedPeer(paramWEmbeddedFrame, wEmbeddedFramePeer);
    return wEmbeddedFramePeer;
  }
  
  WPrintDialogPeer createWPrintDialog(WPrintDialog paramWPrintDialog) {
    WPrintDialogPeer wPrintDialogPeer = new WPrintDialogPeer(paramWPrintDialog);
    targetCreatedPeer(paramWPrintDialog, wPrintDialogPeer);
    return wPrintDialogPeer;
  }
  
  WPageDialogPeer createWPageDialog(WPageDialog paramWPageDialog) {
    WPageDialogPeer wPageDialogPeer = new WPageDialogPeer(paramWPageDialog);
    targetCreatedPeer(paramWPageDialog, wPageDialogPeer);
    return wPageDialogPeer;
  }
  
  public TrayIconPeer createTrayIcon(TrayIcon paramTrayIcon) {
    WTrayIconPeer wTrayIconPeer = new WTrayIconPeer(paramTrayIcon);
    targetCreatedPeer(paramTrayIcon, wTrayIconPeer);
    return wTrayIconPeer;
  }
  
  public SystemTrayPeer createSystemTray(SystemTray paramSystemTray) { return new WSystemTrayPeer(paramSystemTray); }
  
  public boolean isTraySupported() { return true; }
  
  public DataTransferer getDataTransferer() { return WDataTransferer.getInstanceImpl(); }
  
  public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() throws HeadlessException { return WKeyboardFocusManagerPeer.getInstance(); }
  
  private native void setDynamicLayoutNative(boolean paramBoolean);
  
  public void setDynamicLayout(boolean paramBoolean) {
    if (paramBoolean == this.dynamicLayoutSetting)
      return; 
    this.dynamicLayoutSetting = paramBoolean;
    setDynamicLayoutNative(paramBoolean);
  }
  
  protected boolean isDynamicLayoutSet() { return this.dynamicLayoutSetting; }
  
  private native boolean isDynamicLayoutSupportedNative();
  
  public boolean isDynamicLayoutActive() { return (isDynamicLayoutSet() && isDynamicLayoutSupported()); }
  
  public boolean isFrameStateSupported(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
      case 6:
        return true;
    } 
    return false;
  }
  
  static native ColorModel makeColorModel();
  
  static ColorModel getStaticColorModel() {
    if (GraphicsEnvironment.isHeadless())
      throw new IllegalArgumentException(); 
    if (config == null)
      resetGC(); 
    return config.getColorModel();
  }
  
  public ColorModel getColorModel() { return getStaticColorModel(); }
  
  public Insets getScreenInsets(GraphicsConfiguration paramGraphicsConfiguration) { return getScreenInsets(((Win32GraphicsDevice)paramGraphicsConfiguration.getDevice()).getScreen()); }
  
  public int getScreenResolution() {
    Win32GraphicsEnvironment win32GraphicsEnvironment = (Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
    return win32GraphicsEnvironment.getXResolution();
  }
  
  protected native int getScreenWidth();
  
  protected native int getScreenHeight();
  
  private native Insets getScreenInsets(int paramInt);
  
  public FontMetrics getFontMetrics(Font paramFont) {
    FontManager fontManager = FontManagerFactory.getInstance();
    return (fontManager instanceof SunFontManager && ((SunFontManager)fontManager).usePlatformFontMetrics()) ? WFontMetrics.getFontMetrics(paramFont) : super.getFontMetrics(paramFont);
  }
  
  public FontPeer getFontPeer(String paramString, int paramInt) {
    FontPeer fontPeer = null;
    String str = paramString.toLowerCase();
    if (null != this.cacheFontPeer) {
      fontPeer = (FontPeer)this.cacheFontPeer.get(str + paramInt);
      if (null != fontPeer)
        return fontPeer; 
    } 
    fontPeer = new WFontPeer(paramString, paramInt);
    if (fontPeer != null) {
      if (null == this.cacheFontPeer)
        this.cacheFontPeer = new Hashtable(5, 0.9F); 
      if (null != this.cacheFontPeer)
        this.cacheFontPeer.put(str + paramInt, fontPeer); 
    } 
    return fontPeer;
  }
  
  private native void nativeSync();
  
  public void sync() {
    nativeSync();
    OGLRenderQueue.sync();
    D3DRenderQueue.sync();
  }
  
  public PrintJob getPrintJob(Frame paramFrame, String paramString, Properties paramProperties) { return getPrintJob(paramFrame, paramString, null, null); }
  
  public PrintJob getPrintJob(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes) {
    if (paramFrame == null)
      throw new NullPointerException("frame must not be null"); 
    PrintJob2D printJob2D = new PrintJob2D(paramFrame, paramString, paramJobAttributes, paramPageAttributes);
    if (!printJob2D.printDialog())
      printJob2D = null; 
    return printJob2D;
  }
  
  public native void beep();
  
  public boolean getLockingKeyState(int paramInt) {
    if (paramInt != 20 && paramInt != 144 && paramInt != 145 && paramInt != 262)
      throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState"); 
    return getLockingKeyStateNative(paramInt);
  }
  
  private native boolean getLockingKeyStateNative(int paramInt);
  
  public void setLockingKeyState(int paramInt, boolean paramBoolean) {
    if (paramInt != 20 && paramInt != 144 && paramInt != 145 && paramInt != 262)
      throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState"); 
    setLockingKeyStateNative(paramInt, paramBoolean);
  }
  
  private native void setLockingKeyStateNative(int paramInt, boolean paramBoolean);
  
  public Clipboard getSystemClipboard() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION); 
    synchronized (this) {
      if (this.clipboard == null)
        this.clipboard = new WClipboard(); 
    } 
    return this.clipboard;
  }
  
  protected native void loadSystemColors(int[] paramArrayOfInt);
  
  public static final Object targetToPeer(Object paramObject) { return SunToolkit.targetToPeer(paramObject); }
  
  public static final void targetDisposedPeer(Object paramObject1, Object paramObject2) { SunToolkit.targetDisposedPeer(paramObject1, paramObject2); }
  
  public InputMethodDescriptor getInputMethodAdapterDescriptor() { return new WInputMethodDescriptor(); }
  
  public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) { return WInputMethod.mapInputMethodHighlight(paramInputMethodHighlight); }
  
  public boolean enableInputMethodsForTextComponent() { return true; }
  
  public Locale getDefaultKeyboardLocale() {
    Locale locale = WInputMethod.getNativeLocale();
    return (locale == null) ? super.getDefaultKeyboardLocale() : locale;
  }
  
  public Cursor createCustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException { return new WCustomCursor(paramImage, paramPoint, paramString); }
  
  public Dimension getBestCursorSize(int paramInt1, int paramInt2) { return new Dimension(WCustomCursor.getCursorWidth(), WCustomCursor.getCursorHeight()); }
  
  public native int getMaximumCursorColors();
  
  static void paletteChanged() { ((Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).paletteChanged(); }
  
  public static void displayChanged() { EventQueue.invokeLater(new Runnable() {
          public void run() { ((Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment()).displayChanged(); }
        }); }
  
  public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException {
    LightweightFrame lightweightFrame = SunToolkit.getLightweightFrame(paramDragGestureEvent.getComponent());
    return (lightweightFrame != null) ? lightweightFrame.createDragSourceContextPeer(paramDragGestureEvent) : WDragSourceContextPeer.createDragSourceContextPeer(paramDragGestureEvent);
  }
  
  public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener) {
    LightweightFrame lightweightFrame = SunToolkit.getLightweightFrame(paramComponent);
    return (lightweightFrame != null) ? (T)lightweightFrame.createDragGestureRecognizer(paramClass, paramDragSource, paramComponent, paramInt, paramDragGestureListener) : (java.awt.dnd.MouseDragGestureRecognizer.class.equals(paramClass) ? (T)new WMouseDragGestureRecognizer(paramDragSource, paramComponent, paramInt, paramDragGestureListener) : null);
  }
  
  protected Object lazilyLoadDesktopProperty(String paramString) {
    if (paramString.startsWith("DnD.Cursor.")) {
      String str = paramString.substring("DnD.Cursor.".length(), paramString.length()) + ".32x32";
      try {
        return Cursor.getSystemCustomCursor(str);
      } catch (AWTException aWTException) {
        throw new RuntimeException("cannot load system cursor: " + str, aWTException);
      } 
    } 
    if (paramString.equals("awt.dynamicLayoutSupported"))
      return Boolean.valueOf(isDynamicLayoutSupported()); 
    if (WDesktopProperties.isWindowsProperty(paramString) || paramString.startsWith("awt.") || paramString.startsWith("DnD."))
      synchronized (this) {
        lazilyInitWProps();
        return this.desktopProperties.get(paramString);
      }  
    return super.lazilyLoadDesktopProperty(paramString);
  }
  
  private void lazilyInitWProps() {
    if (this.wprops == null) {
      this.wprops = new WDesktopProperties(this);
      updateProperties(this.wprops.getProperties());
    } 
  }
  
  private boolean isDynamicLayoutSupported() {
    boolean bool = isDynamicLayoutSupportedNative();
    lazilyInitWProps();
    Boolean bool1 = (Boolean)this.desktopProperties.get("awt.dynamicLayoutSupported");
    if (log.isLoggable(PlatformLogger.Level.FINER))
      log.finer("In WTK.isDynamicLayoutSupported()   nativeDynamic == " + bool + "   wprops.dynamic == " + bool1); 
    if (bool1 == null || bool != bool1.booleanValue()) {
      windowsSettingChange();
      return bool;
    } 
    return bool1.booleanValue();
  }
  
  private void windowsSettingChange() {
    Map map = getWProps();
    if (map == null)
      return; 
    updateXPStyleEnabled(map.get("win.xpstyle.themeActive"));
    if (AppContext.getAppContext() == null) {
      updateProperties(map);
    } else {
      EventQueue.invokeLater(() -> updateProperties(paramMap));
    } 
  }
  
  private void updateProperties(Map<String, Object> paramMap) {
    if (null == paramMap)
      return; 
    updateXPStyleEnabled(paramMap.get("win.xpstyle.themeActive"));
    for (String str : paramMap.keySet()) {
      Object object = paramMap.get(str);
      if (log.isLoggable(PlatformLogger.Level.FINER))
        log.finer("changed " + str + " to " + object); 
      setDesktopProperty(str, object);
    } 
  }
  
  private Map<String, Object> getWProps() { return (this.wprops != null) ? this.wprops.getProperties() : null; }
  
  private void updateXPStyleEnabled(Object paramObject) { ThemeReader.xpStyleEnabled = Boolean.TRUE.equals(paramObject); }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    if (paramString == null)
      return; 
    if (WDesktopProperties.isWindowsProperty(paramString) || paramString.startsWith("awt.") || paramString.startsWith("DnD."))
      lazilyInitWProps(); 
    super.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  protected void initializeDesktopProperties() {
    this.desktopProperties.put("DnD.Autoscroll.initialDelay", Integer.valueOf(50));
    this.desktopProperties.put("DnD.Autoscroll.interval", Integer.valueOf(50));
    this.desktopProperties.put("DnD.isDragImageSupported", Boolean.TRUE);
    this.desktopProperties.put("Shell.shellFolderManager", "sun.awt.shell.Win32ShellFolderManager2");
  }
  
  protected RenderingHints getDesktopAAHints() { return (this.wprops == null) ? null : this.wprops.getDesktopAAHints(); }
  
  public boolean isModalityTypeSupported(Dialog.ModalityType paramModalityType) { return (paramModalityType == null || paramModalityType == Dialog.ModalityType.MODELESS || paramModalityType == Dialog.ModalityType.DOCUMENT_MODAL || paramModalityType == Dialog.ModalityType.APPLICATION_MODAL || paramModalityType == Dialog.ModalityType.TOOLKIT_MODAL); }
  
  public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType paramModalExclusionType) { return (paramModalExclusionType == null || paramModalExclusionType == Dialog.ModalExclusionType.NO_EXCLUDE || paramModalExclusionType == Dialog.ModalExclusionType.APPLICATION_EXCLUDE || paramModalExclusionType == Dialog.ModalExclusionType.TOOLKIT_EXCLUDE); }
  
  public static WToolkit getWToolkit() { return (WToolkit)Toolkit.getDefaultToolkit(); }
  
  public boolean useBufferPerWindow() { return !Win32GraphicsEnvironment.isDWMCompositionEnabled(); }
  
  public void grab(Window paramWindow) {
    if (paramWindow.getPeer() != null)
      ((WWindowPeer)paramWindow.getPeer()).grab(); 
  }
  
  public void ungrab(Window paramWindow) {
    if (paramWindow.getPeer() != null)
      ((WWindowPeer)paramWindow.getPeer()).ungrab(); 
  }
  
  private boolean isComponentValidForTouchKeyboard(Component paramComponent) { return (paramComponent != null && paramComponent.isEnabled() && paramComponent.isFocusable() && ((paramComponent instanceof TextComponent && ((TextComponent)paramComponent).isEditable()) || (paramComponent instanceof JTextComponent && ((JTextComponent)paramComponent).isEditable()))); }
  
  public void showOrHideTouchKeyboard(Component paramComponent, AWTEvent paramAWTEvent) {
    if (!(paramComponent instanceof TextComponent) && !(paramComponent instanceof JTextComponent))
      return; 
    if (paramAWTEvent instanceof MouseEvent && isComponentValidForTouchKeyboard(paramComponent)) {
      MouseEvent mouseEvent = (MouseEvent)paramAWTEvent;
      if (mouseEvent.getID() == 501) {
        if (AWTAccessor.getMouseEventAccessor().isCausedByTouchEvent(mouseEvent)) {
          this.compOnTouchDownEvent = new WeakReference(paramComponent);
        } else {
          this.compOnMousePressedEvent = new WeakReference(paramComponent);
        } 
      } else if (mouseEvent.getID() == 502) {
        if (AWTAccessor.getMouseEventAccessor().isCausedByTouchEvent(mouseEvent)) {
          if (this.compOnTouchDownEvent.get() == paramComponent)
            showTouchKeyboard(true); 
          this.compOnTouchDownEvent = NULL_COMPONENT_WR;
        } else {
          if (this.compOnMousePressedEvent.get() == paramComponent)
            showTouchKeyboard(false); 
          this.compOnMousePressedEvent = NULL_COMPONENT_WR;
        } 
      } 
    } else if (paramAWTEvent instanceof FocusEvent) {
      FocusEvent focusEvent = (FocusEvent)paramAWTEvent;
      if (focusEvent.getID() == 1005 && !isComponentValidForTouchKeyboard(focusEvent.getOppositeComponent()))
        hideTouchKeyboard(); 
    } 
  }
  
  private native void showTouchKeyboard(boolean paramBoolean);
  
  private native void hideTouchKeyboard();
  
  public native boolean syncNativeQueue(long paramLong);
  
  public boolean isDesktopSupported() { return true; }
  
  public DesktopPeer createDesktopPeer(Desktop paramDesktop) { return new WDesktopPeer(); }
  
  private static native void setExtraMouseButtonsEnabledNative(boolean paramBoolean);
  
  public boolean areExtraMouseButtonsEnabled() { return areExtraMouseButtonsEnabled; }
  
  private native int getNumberOfButtonsImpl();
  
  public int getNumberOfButtons() {
    if (numberOfButtons == 0)
      numberOfButtons = getNumberOfButtonsImpl(); 
    return (numberOfButtons > 20) ? 20 : numberOfButtons;
  }
  
  public boolean isWindowOpacitySupported() { return true; }
  
  public boolean isWindowShapingSupported() { return true; }
  
  public boolean isWindowTranslucencySupported() { return true; }
  
  public boolean isTranslucencyCapable(GraphicsConfiguration paramGraphicsConfiguration) { return true; }
  
  public boolean needUpdateWindow() { return true; }
  
  static  {
    loadLibraries();
    initIDs();
    if (log.isLoggable(PlatformLogger.Level.FINE))
      log.fine("Win version: " + getWindowsVersion()); 
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            String str = System.getProperty("browser");
            if (str != null && str.equals("sun.plugin"))
              WToolkit.disableCustomPalette(); 
            return null;
          }
        });
    NULL_COMPONENT_WR = new WeakReference(null);
  }
  
  static class ToolkitDisposer implements DisposerRecord {
    public void dispose() { WToolkit.postDispose(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WToolkit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */