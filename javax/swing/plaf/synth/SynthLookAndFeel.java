package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.AccessController;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.plaf.synth.SynthFileChooserUI;

public class SynthLookAndFeel extends BasicLookAndFeel {
  static final Insets EMPTY_UIRESOURCE_INSETS = new InsetsUIResource(0, 0, 0, 0);
  
  private static final Object STYLE_FACTORY_KEY = new StringBuffer("com.sun.java.swing.plaf.gtk.StyleCache");
  
  private static final Object SELECTED_UI_KEY = new StringBuilder("selectedUI");
  
  private static final Object SELECTED_UI_STATE_KEY = new StringBuilder("selectedUIState");
  
  private static SynthStyleFactory lastFactory;
  
  private static AppContext lastContext;
  
  private SynthStyleFactory factory = new DefaultSynthStyleFactory();
  
  private Map<String, Object> defaultsMap;
  
  private Handler _handler = new Handler(null);
  
  private static ReferenceQueue<LookAndFeel> queue = new ReferenceQueue();
  
  static ComponentUI getSelectedUI() { return (ComponentUI)AppContext.getAppContext().get(SELECTED_UI_KEY); }
  
  static void setSelectedUI(ComponentUI paramComponentUI, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) {
    char c = Character.MIN_VALUE;
    if (paramBoolean1) {
      c = 'Ȁ';
      if (paramBoolean2)
        c |= 0x100; 
    } else if (paramBoolean4 && paramBoolean3) {
      c |= 0x3;
      if (paramBoolean2)
        c |= 0x100; 
    } else if (paramBoolean3) {
      c |= 0x1;
      if (paramBoolean2)
        c |= 0x100; 
    } else {
      c |= 0x8;
    } 
    AppContext appContext = AppContext.getAppContext();
    appContext.put(SELECTED_UI_KEY, paramComponentUI);
    appContext.put(SELECTED_UI_STATE_KEY, Integer.valueOf(c));
  }
  
  static int getSelectedUIState() {
    Integer integer = (Integer)AppContext.getAppContext().get(SELECTED_UI_STATE_KEY);
    return (integer == null) ? 0 : integer.intValue();
  }
  
  static void resetSelectedUI() { AppContext.getAppContext().remove(SELECTED_UI_KEY); }
  
  public static void setStyleFactory(SynthStyleFactory paramSynthStyleFactory) {
    synchronized (SynthLookAndFeel.class) {
      AppContext appContext = AppContext.getAppContext();
      lastFactory = paramSynthStyleFactory;
      lastContext = appContext;
      appContext.put(STYLE_FACTORY_KEY, paramSynthStyleFactory);
    } 
  }
  
  public static SynthStyleFactory getStyleFactory() {
    synchronized (SynthLookAndFeel.class) {
      AppContext appContext = AppContext.getAppContext();
      if (lastContext == appContext)
        return lastFactory; 
      lastContext = appContext;
      lastFactory = (SynthStyleFactory)appContext.get(STYLE_FACTORY_KEY);
      return lastFactory;
    } 
  }
  
  static int getComponentState(Component paramComponent) { return paramComponent.isEnabled() ? (paramComponent.isFocusOwner() ? 257 : 1) : 8; }
  
  public static SynthStyle getStyle(JComponent paramJComponent, Region paramRegion) { return getStyleFactory().getStyle(paramJComponent, paramRegion); }
  
  static boolean shouldUpdateStyle(PropertyChangeEvent paramPropertyChangeEvent) {
    LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
    return (lookAndFeel instanceof SynthLookAndFeel && ((SynthLookAndFeel)lookAndFeel).shouldUpdateStyleOnEvent(paramPropertyChangeEvent));
  }
  
  static SynthStyle updateStyle(SynthContext paramSynthContext, SynthUI paramSynthUI) {
    SynthStyle synthStyle1 = getStyle(paramSynthContext.getComponent(), paramSynthContext.getRegion());
    SynthStyle synthStyle2 = paramSynthContext.getStyle();
    if (synthStyle1 != synthStyle2) {
      if (synthStyle2 != null)
        synthStyle2.uninstallDefaults(paramSynthContext); 
      paramSynthContext.setStyle(synthStyle1);
      synthStyle1.installDefaults(paramSynthContext, paramSynthUI);
    } 
    return synthStyle1;
  }
  
  public static void updateStyles(Component paramComponent) {
    if (paramComponent instanceof JComponent) {
      String str = paramComponent.getName();
      paramComponent.setName(null);
      if (str != null)
        paramComponent.setName(str); 
      ((JComponent)paramComponent).revalidate();
    } 
    Component[] arrayOfComponent = null;
    if (paramComponent instanceof JMenu) {
      arrayOfComponent = ((JMenu)paramComponent).getMenuComponents();
    } else if (paramComponent instanceof Container) {
      arrayOfComponent = ((Container)paramComponent).getComponents();
    } 
    if (arrayOfComponent != null)
      for (Component component : arrayOfComponent)
        updateStyles(component);  
    paramComponent.repaint();
  }
  
  public static Region getRegion(JComponent paramJComponent) { return Region.getRegion(paramJComponent); }
  
  static Insets getPaintingInsets(SynthContext paramSynthContext, Insets paramInsets) {
    if (paramSynthContext.isSubregion()) {
      paramInsets = paramSynthContext.getStyle().getInsets(paramSynthContext, paramInsets);
    } else {
      paramInsets = paramSynthContext.getComponent().getInsets(paramInsets);
    } 
    return paramInsets;
  }
  
  static void update(SynthContext paramSynthContext, Graphics paramGraphics) { paintRegion(paramSynthContext, paramGraphics, null); }
  
  static void updateSubregion(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle) { paintRegion(paramSynthContext, paramGraphics, paramRectangle); }
  
  private static void paintRegion(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle) {
    int m;
    int k;
    int j;
    int i;
    JComponent jComponent = paramSynthContext.getComponent();
    SynthStyle synthStyle = paramSynthContext.getStyle();
    if (paramRectangle == null) {
      i = 0;
      j = 0;
      k = jComponent.getWidth();
      m = jComponent.getHeight();
    } else {
      i = paramRectangle.x;
      j = paramRectangle.y;
      k = paramRectangle.width;
      m = paramRectangle.height;
    } 
    boolean bool = paramSynthContext.isSubregion();
    if ((bool && synthStyle.isOpaque(paramSynthContext)) || (!bool && jComponent.isOpaque())) {
      paramGraphics.setColor(synthStyle.getColor(paramSynthContext, ColorType.BACKGROUND));
      paramGraphics.fillRect(i, j, k, m);
    } 
  }
  
  static boolean isLeftToRight(Component paramComponent) { return paramComponent.getComponentOrientation().isLeftToRight(); }
  
  static Object getUIOfType(ComponentUI paramComponentUI, Class paramClass) { return paramClass.isInstance(paramComponentUI) ? paramComponentUI : null; }
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    String str = paramJComponent.getUIClassID().intern();
    return (str == "ButtonUI") ? SynthButtonUI.createUI(paramJComponent) : ((str == "CheckBoxUI") ? SynthCheckBoxUI.createUI(paramJComponent) : ((str == "CheckBoxMenuItemUI") ? SynthCheckBoxMenuItemUI.createUI(paramJComponent) : ((str == "ColorChooserUI") ? SynthColorChooserUI.createUI(paramJComponent) : ((str == "ComboBoxUI") ? SynthComboBoxUI.createUI(paramJComponent) : ((str == "DesktopPaneUI") ? SynthDesktopPaneUI.createUI(paramJComponent) : ((str == "DesktopIconUI") ? SynthDesktopIconUI.createUI(paramJComponent) : ((str == "EditorPaneUI") ? SynthEditorPaneUI.createUI(paramJComponent) : ((str == "FileChooserUI") ? SynthFileChooserUI.createUI(paramJComponent) : ((str == "FormattedTextFieldUI") ? SynthFormattedTextFieldUI.createUI(paramJComponent) : ((str == "InternalFrameUI") ? SynthInternalFrameUI.createUI(paramJComponent) : ((str == "LabelUI") ? SynthLabelUI.createUI(paramJComponent) : ((str == "ListUI") ? SynthListUI.createUI(paramJComponent) : ((str == "MenuBarUI") ? SynthMenuBarUI.createUI(paramJComponent) : ((str == "MenuUI") ? SynthMenuUI.createUI(paramJComponent) : ((str == "MenuItemUI") ? SynthMenuItemUI.createUI(paramJComponent) : ((str == "OptionPaneUI") ? SynthOptionPaneUI.createUI(paramJComponent) : ((str == "PanelUI") ? SynthPanelUI.createUI(paramJComponent) : ((str == "PasswordFieldUI") ? SynthPasswordFieldUI.createUI(paramJComponent) : ((str == "PopupMenuSeparatorUI") ? SynthSeparatorUI.createUI(paramJComponent) : ((str == "PopupMenuUI") ? SynthPopupMenuUI.createUI(paramJComponent) : ((str == "ProgressBarUI") ? SynthProgressBarUI.createUI(paramJComponent) : ((str == "RadioButtonUI") ? SynthRadioButtonUI.createUI(paramJComponent) : ((str == "RadioButtonMenuItemUI") ? SynthRadioButtonMenuItemUI.createUI(paramJComponent) : ((str == "RootPaneUI") ? SynthRootPaneUI.createUI(paramJComponent) : ((str == "ScrollBarUI") ? SynthScrollBarUI.createUI(paramJComponent) : ((str == "ScrollPaneUI") ? SynthScrollPaneUI.createUI(paramJComponent) : ((str == "SeparatorUI") ? SynthSeparatorUI.createUI(paramJComponent) : ((str == "SliderUI") ? SynthSliderUI.createUI(paramJComponent) : ((str == "SpinnerUI") ? SynthSpinnerUI.createUI(paramJComponent) : ((str == "SplitPaneUI") ? SynthSplitPaneUI.createUI(paramJComponent) : ((str == "TabbedPaneUI") ? SynthTabbedPaneUI.createUI(paramJComponent) : ((str == "TableUI") ? SynthTableUI.createUI(paramJComponent) : ((str == "TableHeaderUI") ? SynthTableHeaderUI.createUI(paramJComponent) : ((str == "TextAreaUI") ? SynthTextAreaUI.createUI(paramJComponent) : ((str == "TextFieldUI") ? SynthTextFieldUI.createUI(paramJComponent) : ((str == "TextPaneUI") ? SynthTextPaneUI.createUI(paramJComponent) : ((str == "ToggleButtonUI") ? SynthToggleButtonUI.createUI(paramJComponent) : ((str == "ToolBarSeparatorUI") ? SynthSeparatorUI.createUI(paramJComponent) : ((str == "ToolBarUI") ? SynthToolBarUI.createUI(paramJComponent) : ((str == "ToolTipUI") ? SynthToolTipUI.createUI(paramJComponent) : ((str == "TreeUI") ? SynthTreeUI.createUI(paramJComponent) : ((str == "ViewportUI") ? SynthViewportUI.createUI(paramJComponent) : null))))))))))))))))))))))))))))))))))))))))));
  }
  
  public void load(InputStream paramInputStream, Class<?> paramClass) throws ParseException {
    if (paramClass == null)
      throw new IllegalArgumentException("You must supply a valid resource base Class"); 
    if (this.defaultsMap == null)
      this.defaultsMap = new HashMap(); 
    (new SynthParser()).parse(paramInputStream, (DefaultSynthStyleFactory)this.factory, null, paramClass, this.defaultsMap);
  }
  
  public void load(URL paramURL) throws ParseException, IOException {
    if (paramURL == null)
      throw new IllegalArgumentException("You must supply a valid Synth set URL"); 
    if (this.defaultsMap == null)
      this.defaultsMap = new HashMap(); 
    InputStream inputStream = paramURL.openStream();
    (new SynthParser()).parse(inputStream, (DefaultSynthStyleFactory)this.factory, paramURL, null, this.defaultsMap);
  }
  
  public void initialize() {
    super.initialize();
    DefaultLookup.setDefaultLookup(new SynthDefaultLookup());
    setStyleFactory(this.factory);
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this._handler);
  }
  
  public void uninitialize() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(this._handler);
    super.uninitialize();
  }
  
  public UIDefaults getDefaults() {
    UIDefaults uIDefaults = new UIDefaults(60, 0.75F);
    Region.registerUIs(uIDefaults);
    uIDefaults.setDefaultLocale(Locale.getDefault());
    uIDefaults.addResourceBundle("com.sun.swing.internal.plaf.basic.resources.basic");
    uIDefaults.addResourceBundle("com.sun.swing.internal.plaf.synth.resources.synth");
    uIDefaults.put("TabbedPane.isTabRollover", Boolean.TRUE);
    uIDefaults.put("ColorChooser.swatchesRecentSwatchSize", new Dimension(10, 10));
    uIDefaults.put("ColorChooser.swatchesDefaultRecentColor", Color.RED);
    uIDefaults.put("ColorChooser.swatchesSwatchSize", new Dimension(10, 10));
    uIDefaults.put("html.pendingImage", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/image-delayed.png"));
    uIDefaults.put("html.missingImage", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/image-failed.png"));
    uIDefaults.put("PopupMenu.selectedWindowInputMapBindings", new Object[] { 
          "ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", 
          "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", 
          "SPACE", "return" });
    uIDefaults.put("PopupMenu.selectedWindowInputMapBindings.RightToLeft", new Object[] { "LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent" });
    flushUnreferenced();
    Object object = getAATextInfo();
    uIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, object);
    new AATextListener(this);
    if (this.defaultsMap != null)
      uIDefaults.putAll(this.defaultsMap); 
    return uIDefaults;
  }
  
  public boolean isSupportedLookAndFeel() { return true; }
  
  public boolean isNativeLookAndFeel() { return false; }
  
  public String getDescription() { return "Synth look and feel"; }
  
  public String getName() { return "Synth look and feel"; }
  
  public String getID() { return "Synth"; }
  
  public boolean shouldUpdateStyleOnAncestorChanged() { return false; }
  
  protected boolean shouldUpdateStyleOnEvent(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    return ("name" == str || "componentOrientation" == str) ? true : (("ancestor" == str && paramPropertyChangeEvent.getNewValue() != null) ? shouldUpdateStyleOnAncestorChanged() : 0);
  }
  
  private static Object getAATextInfo() {
    String str1 = Locale.getDefault().getLanguage();
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.desktop"));
    boolean bool = (Locale.CHINESE.getLanguage().equals(str1) || Locale.JAPANESE.getLanguage().equals(str1) || Locale.KOREAN.getLanguage().equals(str1)) ? 1 : 0;
    boolean bool1 = "gnome".equals(str2);
    boolean bool2 = SwingUtilities2.isLocalDisplay();
    boolean bool3 = (bool2 && (!bool1 || !bool));
    return SwingUtilities2.AATextInfo.getAATextInfo(bool3);
  }
  
  private static void flushUnreferenced() {
    AATextListener aATextListener;
    while ((aATextListener = (AATextListener)queue.poll()) != null)
      aATextListener.dispose(); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { throw new NotSerializableException(getClass().getName()); }
  
  private static class AATextListener extends WeakReference<LookAndFeel> implements PropertyChangeListener {
    private String key = "awt.font.desktophints";
    
    private static boolean updatePending;
    
    AATextListener(LookAndFeel param1LookAndFeel) {
      super(param1LookAndFeel, queue);
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      toolkit.addPropertyChangeListener(this.key, this);
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
      if (uIDefaults.getBoolean("Synth.doNotSetTextAA")) {
        dispose();
        return;
      } 
      LookAndFeel lookAndFeel = (LookAndFeel)get();
      if (lookAndFeel == null || lookAndFeel != UIManager.getLookAndFeel()) {
        dispose();
        return;
      } 
      Object object = SynthLookAndFeel.getAATextInfo();
      uIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, object);
      updateUI();
    }
    
    void dispose() {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      toolkit.removePropertyChangeListener(this.key, this);
    }
    
    private static void updateWindowUI(Window param1Window) {
      SynthLookAndFeel.updateStyles(param1Window);
      Window[] arrayOfWindow = param1Window.getOwnedWindows();
      for (Window window : arrayOfWindow)
        updateWindowUI(window); 
    }
    
    private static void updateAllUIs() {
      Frame[] arrayOfFrame = Frame.getFrames();
      for (Frame frame : arrayOfFrame)
        updateWindowUI(frame); 
    }
    
    private static void setUpdatePending(boolean param1Boolean) { updatePending = param1Boolean; }
    
    private static boolean isUpdatePending() { return updatePending; }
    
    protected void updateUI() {
      if (!isUpdatePending()) {
        setUpdatePending(true);
        Runnable runnable = new Runnable() {
            public void run() {
              SynthLookAndFeel.AATextListener.updateAllUIs();
              SynthLookAndFeel.AATextListener.setUpdatePending(false);
            }
          };
        SwingUtilities.invokeLater(runnable);
      } 
    }
  }
  
  private class Handler implements PropertyChangeListener {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      Object object1 = param1PropertyChangeEvent.getNewValue();
      Object object2 = param1PropertyChangeEvent.getOldValue();
      if ("focusOwner" == str) {
        if (object2 instanceof JComponent)
          repaintIfBackgroundsDiffer((JComponent)object2); 
        if (object1 instanceof JComponent)
          repaintIfBackgroundsDiffer((JComponent)object1); 
      } else if ("managingFocus" == str) {
        KeyboardFocusManager keyboardFocusManager = (KeyboardFocusManager)param1PropertyChangeEvent.getSource();
        if (object1.equals(Boolean.FALSE)) {
          keyboardFocusManager.removePropertyChangeListener(SynthLookAndFeel.this._handler);
        } else {
          keyboardFocusManager.addPropertyChangeListener(SynthLookAndFeel.this._handler);
        } 
      } 
    }
    
    private void repaintIfBackgroundsDiffer(JComponent param1JComponent) {
      ComponentUI componentUI = (ComponentUI)param1JComponent.getClientProperty(SwingUtilities2.COMPONENT_UI_PROPERTY_KEY);
      if (componentUI instanceof SynthUI) {
        SynthUI synthUI = (SynthUI)componentUI;
        SynthContext synthContext = synthUI.getContext(param1JComponent);
        SynthStyle synthStyle = synthContext.getStyle();
        int i = synthContext.getComponentState();
        Color color1 = synthStyle.getColor(synthContext, ColorType.BACKGROUND);
        i ^= 0x100;
        synthContext.setComponentState(i);
        Color color2 = synthStyle.getColor(synthContext, ColorType.BACKGROUND);
        i ^= 0x100;
        synthContext.setComponentState(i);
        if (color1 != null && !color1.equals(color2))
          param1JComponent.repaint(); 
        synthContext.dispose();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthLookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */