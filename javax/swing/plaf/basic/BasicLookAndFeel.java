package javax.swing.plaf.basic;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Locale;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

public abstract class BasicLookAndFeel extends LookAndFeel implements Serializable {
  static boolean needsEventHelper;
  
  private Object audioLock = new Object();
  
  private Clip clipPlaying;
  
  AWTEventHelper invocator = null;
  
  private PropertyChangeListener disposer = null;
  
  public UIDefaults getDefaults() {
    UIDefaults uIDefaults = new UIDefaults(610, 0.75F);
    initClassDefaults(uIDefaults);
    initSystemColorDefaults(uIDefaults);
    initComponentDefaults(uIDefaults);
    return uIDefaults;
  }
  
  public void initialize() {
    if (needsEventHelper)
      installAWTEventListener(); 
  }
  
  void installAWTEventListener() {
    if (this.invocator == null) {
      this.invocator = new AWTEventHelper();
      needsEventHelper = true;
      this.disposer = new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicLookAndFeel.this.uninitialize(); }
        };
      AppContext.getAppContext().addPropertyChangeListener("guidisposed", this.disposer);
    } 
  }
  
  public void uninitialize() {
    AppContext appContext = AppContext.getAppContext();
    synchronized (BasicPopupMenuUI.MOUSE_GRABBER_KEY) {
      Object object = appContext.get(BasicPopupMenuUI.MOUSE_GRABBER_KEY);
      if (object != null)
        ((BasicPopupMenuUI.MouseGrabber)object).uninstall(); 
    } 
    synchronized (BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY) {
      Object object = appContext.get(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY);
      if (object != null)
        ((BasicPopupMenuUI.MenuKeyboardHelper)object).uninstall(); 
    } 
    if (this.invocator != null) {
      AccessController.doPrivileged(this.invocator);
      this.invocator = null;
    } 
    if (this.disposer != null) {
      appContext.removePropertyChangeListener("guidisposed", this.disposer);
      this.disposer = null;
    } 
  }
  
  protected void initClassDefaults(UIDefaults paramUIDefaults) {
    Object[] arrayOfObject = { 
        "ButtonUI", "javax.swing.plaf.basic.BasicButtonUI", "CheckBoxUI", "javax.swing.plaf.basic.BasicCheckBoxUI", "ColorChooserUI", "javax.swing.plaf.basic.BasicColorChooserUI", "FormattedTextFieldUI", "javax.swing.plaf.basic.BasicFormattedTextFieldUI", "MenuBarUI", "javax.swing.plaf.basic.BasicMenuBarUI", 
        "MenuUI", "javax.swing.plaf.basic.BasicMenuUI", "MenuItemUI", "javax.swing.plaf.basic.BasicMenuItemUI", "CheckBoxMenuItemUI", "javax.swing.plaf.basic.BasicCheckBoxMenuItemUI", "RadioButtonMenuItemUI", "javax.swing.plaf.basic.BasicRadioButtonMenuItemUI", "RadioButtonUI", "javax.swing.plaf.basic.BasicRadioButtonUI", 
        "ToggleButtonUI", "javax.swing.plaf.basic.BasicToggleButtonUI", "PopupMenuUI", "javax.swing.plaf.basic.BasicPopupMenuUI", "ProgressBarUI", "javax.swing.plaf.basic.BasicProgressBarUI", "ScrollBarUI", "javax.swing.plaf.basic.BasicScrollBarUI", "ScrollPaneUI", "javax.swing.plaf.basic.BasicScrollPaneUI", 
        "SplitPaneUI", "javax.swing.plaf.basic.BasicSplitPaneUI", "SliderUI", "javax.swing.plaf.basic.BasicSliderUI", "SeparatorUI", "javax.swing.plaf.basic.BasicSeparatorUI", "SpinnerUI", "javax.swing.plaf.basic.BasicSpinnerUI", "ToolBarSeparatorUI", "javax.swing.plaf.basic.BasicToolBarSeparatorUI", 
        "PopupMenuSeparatorUI", "javax.swing.plaf.basic.BasicPopupMenuSeparatorUI", "TabbedPaneUI", "javax.swing.plaf.basic.BasicTabbedPaneUI", "TextAreaUI", "javax.swing.plaf.basic.BasicTextAreaUI", "TextFieldUI", "javax.swing.plaf.basic.BasicTextFieldUI", "PasswordFieldUI", "javax.swing.plaf.basic.BasicPasswordFieldUI", 
        "TextPaneUI", "javax.swing.plaf.basic.BasicTextPaneUI", "EditorPaneUI", "javax.swing.plaf.basic.BasicEditorPaneUI", "TreeUI", "javax.swing.plaf.basic.BasicTreeUI", "LabelUI", "javax.swing.plaf.basic.BasicLabelUI", "ListUI", "javax.swing.plaf.basic.BasicListUI", 
        "ToolBarUI", "javax.swing.plaf.basic.BasicToolBarUI", "ToolTipUI", "javax.swing.plaf.basic.BasicToolTipUI", "ComboBoxUI", "javax.swing.plaf.basic.BasicComboBoxUI", "TableUI", "javax.swing.plaf.basic.BasicTableUI", "TableHeaderUI", "javax.swing.plaf.basic.BasicTableHeaderUI", 
        "InternalFrameUI", "javax.swing.plaf.basic.BasicInternalFrameUI", "DesktopPaneUI", "javax.swing.plaf.basic.BasicDesktopPaneUI", "DesktopIconUI", "javax.swing.plaf.basic.BasicDesktopIconUI", "FileChooserUI", "javax.swing.plaf.basic.BasicFileChooserUI", "OptionPaneUI", "javax.swing.plaf.basic.BasicOptionPaneUI", 
        "PanelUI", "javax.swing.plaf.basic.BasicPanelUI", "ViewportUI", "javax.swing.plaf.basic.BasicViewportUI", "RootPaneUI", "javax.swing.plaf.basic.BasicRootPaneUI" };
    paramUIDefaults.putDefaults(arrayOfObject);
  }
  
  protected void initSystemColorDefaults(UIDefaults paramUIDefaults) {
    String[] arrayOfString = { 
        "desktop", "#005C5C", "activeCaption", "#000080", "activeCaptionText", "#FFFFFF", "activeCaptionBorder", "#C0C0C0", "inactiveCaption", "#808080", 
        "inactiveCaptionText", "#C0C0C0", "inactiveCaptionBorder", "#C0C0C0", "window", "#FFFFFF", "windowBorder", "#000000", "windowText", "#000000", 
        "menu", "#C0C0C0", "menuText", "#000000", "text", "#C0C0C0", "textText", "#000000", "textHighlight", "#000080", 
        "textHighlightText", "#FFFFFF", "textInactiveText", "#808080", "control", "#C0C0C0", "controlText", "#000000", "controlHighlight", "#C0C0C0", 
        "controlLtHighlight", "#FFFFFF", "controlShadow", "#808080", "controlDkShadow", "#000000", "scrollbar", "#E0E0E0", "info", "#FFFFE1", 
        "infoText", "#000000" };
    loadSystemColors(paramUIDefaults, arrayOfString, isNativeLookAndFeel());
  }
  
  protected void loadSystemColors(UIDefaults paramUIDefaults, String[] paramArrayOfString, boolean paramBoolean) {
    if (paramBoolean) {
      for (boolean bool = false; bool < paramArrayOfString.length; bool += true) {
        Color color = Color.black;
        try {
          String str = paramArrayOfString[bool];
          color = (Color)java.awt.SystemColor.class.getField(str).get(null);
        } catch (Exception exception) {}
        paramUIDefaults.put(paramArrayOfString[bool], new ColorUIResource(color));
      } 
    } else {
      for (boolean bool = false; bool < paramArrayOfString.length; bool += true) {
        Color color = Color.black;
        try {
          color = Color.decode(paramArrayOfString[bool + true]);
        } catch (NumberFormatException numberFormatException) {
          numberFormatException.printStackTrace();
        } 
        paramUIDefaults.put(paramArrayOfString[bool], new ColorUIResource(color));
      } 
    } 
  }
  
  private void initResourceBundle(UIDefaults paramUIDefaults) {
    paramUIDefaults.setDefaultLocale(Locale.getDefault());
    paramUIDefaults.addResourceBundle("com.sun.swing.internal.plaf.basic.resources.basic");
  }
  
  protected void initComponentDefaults(UIDefaults paramUIDefaults) {
    initResourceBundle(paramUIDefaults);
    Integer integer1 = new Integer(500);
    Long long = new Long(1000L);
    Integer integer2 = new Integer(12);
    Integer integer3 = new Integer(0);
    Integer integer4 = new Integer(1);
    SwingLazyValue swingLazyValue1 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Dialog", integer3, integer2 });
    SwingLazyValue swingLazyValue2 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Serif", integer3, integer2 });
    SwingLazyValue swingLazyValue3 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "SansSerif", integer3, integer2 });
    SwingLazyValue swingLazyValue4 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Monospaced", integer3, integer2 });
    SwingLazyValue swingLazyValue5 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Dialog", integer4, integer2 });
    ColorUIResource colorUIResource1 = new ColorUIResource(Color.red);
    ColorUIResource colorUIResource2 = new ColorUIResource(Color.black);
    ColorUIResource colorUIResource3 = new ColorUIResource(Color.white);
    ColorUIResource colorUIResource4 = new ColorUIResource(Color.yellow);
    ColorUIResource colorUIResource5 = new ColorUIResource(Color.gray);
    ColorUIResource colorUIResource6 = new ColorUIResource(Color.lightGray);
    ColorUIResource colorUIResource7 = new ColorUIResource(Color.darkGray);
    ColorUIResource colorUIResource8 = new ColorUIResource(224, 224, 224);
    Color color1 = paramUIDefaults.getColor("control");
    Color color2 = paramUIDefaults.getColor("controlDkShadow");
    Color color3 = paramUIDefaults.getColor("controlHighlight");
    Color color4 = paramUIDefaults.getColor("controlLtHighlight");
    Color color5 = paramUIDefaults.getColor("controlShadow");
    Color color6 = paramUIDefaults.getColor("controlText");
    Color color7 = paramUIDefaults.getColor("menu");
    Color color8 = paramUIDefaults.getColor("menuText");
    Color color9 = paramUIDefaults.getColor("textHighlight");
    Color color10 = paramUIDefaults.getColor("textHighlightText");
    Color color11 = paramUIDefaults.getColor("textInactiveText");
    Color color12 = paramUIDefaults.getColor("textText");
    Color color13 = paramUIDefaults.getColor("window");
    InsetsUIResource insetsUIResource1 = new InsetsUIResource(0, 0, 0, 0);
    InsetsUIResource insetsUIResource2 = new InsetsUIResource(2, 2, 2, 2);
    InsetsUIResource insetsUIResource3 = new InsetsUIResource(3, 3, 3, 3);
    SwingLazyValue swingLazyValue6 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders$MarginBorder");
    SwingLazyValue swingLazyValue7 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getEtchedBorderUIResource");
    SwingLazyValue swingLazyValue8 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource");
    SwingLazyValue swingLazyValue9 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder");
    SwingLazyValue swingLazyValue10 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getBlackLineBorderUIResource");
    SwingLazyValue swingLazyValue11 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", null, new Object[] { colorUIResource4 });
    BorderUIResource.EmptyBorderUIResource emptyBorderUIResource = new BorderUIResource.EmptyBorderUIResource(1, 1, 1, 1);
    SwingLazyValue swingLazyValue12 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$BevelBorderUIResource", null, new Object[] { new Integer(0), color4, color1, color2, color5 });
    SwingLazyValue swingLazyValue13 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getButtonBorder");
    SwingLazyValue swingLazyValue14 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getToggleButtonBorder");
    SwingLazyValue swingLazyValue15 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getRadioButtonBorder");
    Object object1 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/NewFolder.gif");
    Object object2 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/UpFolder.gif");
    Object object3 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/HomeFolder.gif");
    Object object4 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/DetailsView.gif");
    Object object5 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/ListView.gif");
    Object object6 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/Directory.gif");
    Object object7 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/File.gif");
    Object object8 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/Computer.gif");
    Object object9 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/HardDrive.gif");
    Object object10 = SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/FloppyDrive.gif");
    SwingLazyValue swingLazyValue16 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder");
    UIDefaults.ActiveValue activeValue = new UIDefaults.ActiveValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return new DefaultListCellRenderer.UIResource(); }
      };
    SwingLazyValue swingLazyValue17 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getMenuBarBorder");
    SwingLazyValue swingLazyValue18 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuItemCheckIcon");
    SwingLazyValue swingLazyValue19 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuItemArrowIcon");
    SwingLazyValue swingLazyValue20 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuArrowIcon");
    SwingLazyValue swingLazyValue21 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getCheckBoxIcon");
    SwingLazyValue swingLazyValue22 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getRadioButtonIcon");
    SwingLazyValue swingLazyValue23 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getCheckBoxMenuItemIcon");
    SwingLazyValue swingLazyValue24 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getRadioButtonMenuItemIcon");
    String str = "+";
    DimensionUIResource dimensionUIResource1 = new DimensionUIResource(262, 90);
    Integer integer5 = new Integer(0);
    SwingLazyValue swingLazyValue25 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[] { integer5, integer5, integer5, integer5 });
    Integer integer6 = new Integer(10);
    SwingLazyValue swingLazyValue26 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[] { integer6, integer6, integer2, integer6 });
    SwingLazyValue swingLazyValue27 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[] { new Integer(6), integer5, integer5, integer5 });
    SwingLazyValue swingLazyValue28 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getProgressBarBorder");
    DimensionUIResource dimensionUIResource2 = new DimensionUIResource(8, 8);
    DimensionUIResource dimensionUIResource3 = new DimensionUIResource(4096, 4096);
    InsetsUIResource insetsUIResource4 = insetsUIResource2;
    DimensionUIResource dimensionUIResource4 = new DimensionUIResource(10, 10);
    SwingLazyValue swingLazyValue29 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getSplitPaneBorder");
    SwingLazyValue swingLazyValue30 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getSplitPaneDividerBorder");
    InsetsUIResource insetsUIResource5 = new InsetsUIResource(0, 4, 1, 4);
    InsetsUIResource insetsUIResource6 = new InsetsUIResource(2, 2, 2, 1);
    InsetsUIResource insetsUIResource7 = new InsetsUIResource(3, 2, 0, 2);
    InsetsUIResource insetsUIResource8 = new InsetsUIResource(2, 2, 3, 3);
    SwingLazyValue swingLazyValue31 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getTextFieldBorder");
    InsetsUIResource insetsUIResource9 = insetsUIResource3;
    Integer integer7 = integer1;
    Integer integer8 = new Integer(4);
    Object[] arrayOfObject1 = { 
        "CheckBoxMenuItem.commandSound", "InternalFrame.closeSound", "InternalFrame.maximizeSound", "InternalFrame.minimizeSound", "InternalFrame.restoreDownSound", "InternalFrame.restoreUpSound", "MenuItem.commandSound", "OptionPane.errorSound", "OptionPane.informationSound", "OptionPane.questionSound", 
        "OptionPane.warningSound", "PopupMenu.popupSound", "RadioButtonMenuItem.commandSound" };
    Object[] arrayOfObject2 = { "mute" };
    Object[] arrayOfObject3 = { 
        "AuditoryCues.cueList", arrayOfObject1, "AuditoryCues.allAuditoryCues", arrayOfObject1, "AuditoryCues.noAuditoryCues", arrayOfObject2, "AuditoryCues.playList", null, "Button.defaultButtonFollowsFocus", Boolean.TRUE, 
        "Button.font", swingLazyValue1, "Button.background", color1, "Button.foreground", color6, "Button.shadow", color5, "Button.darkShadow", color2, 
        "Button.light", color3, "Button.highlight", color4, "Button.border", swingLazyValue13, "Button.margin", new InsetsUIResource(2, 14, 2, 14), "Button.textIconGap", integer8, 
        "Button.textShiftOffset", integer5, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released", "ENTER", "pressed", "released ENTER", "released" }), "ToggleButton.font", swingLazyValue1, "ToggleButton.background", color1, "ToggleButton.foreground", color6, 
        "ToggleButton.shadow", color5, "ToggleButton.darkShadow", color2, "ToggleButton.light", color3, "ToggleButton.highlight", color4, "ToggleButton.border", swingLazyValue14, 
        "ToggleButton.margin", new InsetsUIResource(2, 14, 2, 14), "ToggleButton.textIconGap", integer8, "ToggleButton.textShiftOffset", integer5, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "RadioButton.font", swingLazyValue1, 
        "RadioButton.background", color1, "RadioButton.foreground", color6, "RadioButton.shadow", color5, "RadioButton.darkShadow", color2, "RadioButton.light", color3, 
        "RadioButton.highlight", color4, "RadioButton.border", swingLazyValue15, "RadioButton.margin", insetsUIResource2, "RadioButton.textIconGap", integer8, "RadioButton.textShiftOffset", integer5, 
        "RadioButton.icon", swingLazyValue22, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released", "RETURN", "pressed" }), "CheckBox.font", swingLazyValue1, "CheckBox.background", color1, "CheckBox.foreground", color6, 
        "CheckBox.border", swingLazyValue15, "CheckBox.margin", insetsUIResource2, "CheckBox.textIconGap", integer8, "CheckBox.textShiftOffset", integer5, "CheckBox.icon", swingLazyValue21, 
        "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "FileChooser.useSystemExtensionHiding", Boolean.FALSE, "ColorChooser.font", swingLazyValue1, "ColorChooser.background", color1, "ColorChooser.foreground", color6, 
        "ColorChooser.swatchesSwatchSize", new Dimension(10, 10), "ColorChooser.swatchesRecentSwatchSize", new Dimension(10, 10), "ColorChooser.swatchesDefaultRecentColor", color1, "ComboBox.font", swingLazyValue3, "ComboBox.background", color13, 
        "ComboBox.foreground", color12, "ComboBox.buttonBackground", color1, "ComboBox.buttonShadow", color5, "ComboBox.buttonDarkShadow", color2, "ComboBox.buttonHighlight", color4, 
        "ComboBox.selectionBackground", color9, "ComboBox.selectionForeground", color10, "ComboBox.disabledBackground", color1, "ComboBox.disabledForeground", color11, "ComboBox.timeFactor", long, 
        "ComboBox.isEnterSelectablePopup", Boolean.FALSE, "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", 
            "ENTER", "enterPressed" }), "ComboBox.noActionOnKeyNavigation", Boolean.FALSE, "FileChooser.newFolderIcon", object1, "FileChooser.upFolderIcon", object2, 
        "FileChooser.homeFolderIcon", object3, "FileChooser.detailsViewIcon", object4, "FileChooser.listViewIcon", object5, "FileChooser.readOnly", Boolean.FALSE, "FileChooser.usesSingleFilePane", Boolean.FALSE, 
        "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F5", "refresh" }), "FileView.directoryIcon", object6, "FileView.fileIcon", object7, "FileView.computerIcon", object8, "FileView.hardDriveIcon", object9, 
        "FileView.floppyDriveIcon", object10, "InternalFrame.titleFont", swingLazyValue5, "InternalFrame.borderColor", color1, "InternalFrame.borderShadow", color5, "InternalFrame.borderDarkShadow", color2, 
        "InternalFrame.borderHighlight", color4, "InternalFrame.borderLight", color3, "InternalFrame.border", swingLazyValue16, "InternalFrame.icon", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/JavaCup16.png"), "InternalFrame.maximizeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), 
        "InternalFrame.minimizeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.iconifyIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.closeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.closeSound", null, "InternalFrame.maximizeSound", null, 
        "InternalFrame.minimizeSound", null, "InternalFrame.restoreDownSound", null, "InternalFrame.restoreUpSound", null, "InternalFrame.activeTitleBackground", paramUIDefaults.get("activeCaption"), "InternalFrame.activeTitleForeground", paramUIDefaults.get("activeCaptionText"), 
        "InternalFrame.inactiveTitleBackground", paramUIDefaults.get("inactiveCaption"), "InternalFrame.inactiveTitleForeground", paramUIDefaults.get("inactiveCaptionText"), "InternalFrame.windowBindings", { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" }, "InternalFrameTitlePane.iconifyButtonOpacity", Boolean.TRUE, "InternalFrameTitlePane.maximizeButtonOpacity", Boolean.TRUE, 
        "InternalFrameTitlePane.closeButtonOpacity", Boolean.TRUE, "DesktopIcon.border", swingLazyValue16, "Desktop.minOnScreenInsets", insetsUIResource3, "Desktop.background", paramUIDefaults.get("desktop"), "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", 
            "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", 
            "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", 
            "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", 
            "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", 
            "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }), 
        "Label.font", swingLazyValue1, "Label.background", color1, "Label.foreground", color6, "Label.disabledForeground", colorUIResource3, "Label.disabledShadow", color5, 
        "Label.border", null, "List.font", swingLazyValue1, "List.background", color13, "List.foreground", color12, "List.selectionBackground", color9, 
        "List.selectionForeground", color10, "List.noFocusBorder", emptyBorderUIResource, "List.focusCellHighlightBorder", swingLazyValue11, "List.dropLineColor", color5, "List.border", null, 
        "List.cellRenderer", activeValue, "List.timeFactor", long, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", 
            "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", 
            "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", 
            "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", 
            "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", 
            "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", 
            "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", 
            "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", 
            "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", 
            "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", 
            "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", 
            "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", 
            "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "List.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { 
            "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", 
            "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", 
            "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", 
            "ctrl KP_RIGHT", "selectPreviousColumnChangeLead" }), "MenuBar.font", swingLazyValue1, 
        "MenuBar.background", color7, "MenuBar.foreground", color8, "MenuBar.shadow", color5, "MenuBar.highlight", color4, "MenuBar.border", swingLazyValue17, 
        "MenuBar.windowBindings", { "F10", "takeFocus" }, "MenuItem.font", swingLazyValue1, "MenuItem.acceleratorFont", swingLazyValue1, "MenuItem.background", color7, "MenuItem.foreground", color8, 
        "MenuItem.selectionForeground", color10, "MenuItem.selectionBackground", color9, "MenuItem.disabledForeground", null, "MenuItem.acceleratorForeground", color8, "MenuItem.acceleratorSelectionForeground", color10, 
        "MenuItem.acceleratorDelimiter", str, "MenuItem.border", swingLazyValue6, "MenuItem.borderPainted", Boolean.FALSE, "MenuItem.margin", insetsUIResource2, "MenuItem.checkIcon", swingLazyValue18, 
        "MenuItem.arrowIcon", swingLazyValue19, "MenuItem.commandSound", null, "RadioButtonMenuItem.font", swingLazyValue1, "RadioButtonMenuItem.acceleratorFont", swingLazyValue1, "RadioButtonMenuItem.background", color7, 
        "RadioButtonMenuItem.foreground", color8, "RadioButtonMenuItem.selectionForeground", color10, "RadioButtonMenuItem.selectionBackground", color9, "RadioButtonMenuItem.disabledForeground", null, "RadioButtonMenuItem.acceleratorForeground", color8, 
        "RadioButtonMenuItem.acceleratorSelectionForeground", color10, "RadioButtonMenuItem.border", swingLazyValue6, "RadioButtonMenuItem.borderPainted", Boolean.FALSE, "RadioButtonMenuItem.margin", insetsUIResource2, "RadioButtonMenuItem.checkIcon", swingLazyValue24, 
        "RadioButtonMenuItem.arrowIcon", swingLazyValue19, "RadioButtonMenuItem.commandSound", null, "CheckBoxMenuItem.font", swingLazyValue1, "CheckBoxMenuItem.acceleratorFont", swingLazyValue1, "CheckBoxMenuItem.background", color7, 
        "CheckBoxMenuItem.foreground", color8, "CheckBoxMenuItem.selectionForeground", color10, "CheckBoxMenuItem.selectionBackground", color9, "CheckBoxMenuItem.disabledForeground", null, "CheckBoxMenuItem.acceleratorForeground", color8, 
        "CheckBoxMenuItem.acceleratorSelectionForeground", color10, "CheckBoxMenuItem.border", swingLazyValue6, "CheckBoxMenuItem.borderPainted", Boolean.FALSE, "CheckBoxMenuItem.margin", insetsUIResource2, "CheckBoxMenuItem.checkIcon", swingLazyValue23, 
        "CheckBoxMenuItem.arrowIcon", swingLazyValue19, "CheckBoxMenuItem.commandSound", null, "Menu.font", swingLazyValue1, "Menu.acceleratorFont", swingLazyValue1, "Menu.background", color7, 
        "Menu.foreground", color8, "Menu.selectionForeground", color10, "Menu.selectionBackground", color9, "Menu.disabledForeground", null, "Menu.acceleratorForeground", color8, 
        "Menu.acceleratorSelectionForeground", color10, "Menu.border", swingLazyValue6, "Menu.borderPainted", Boolean.FALSE, "Menu.margin", insetsUIResource2, "Menu.checkIcon", swingLazyValue18, 
        "Menu.arrowIcon", swingLazyValue20, "Menu.menuPopupOffsetX", new Integer(0), "Menu.menuPopupOffsetY", new Integer(0), "Menu.submenuPopupOffsetX", new Integer(0), "Menu.submenuPopupOffsetY", new Integer(0), 
        "Menu.shortcutKeys", { SwingUtilities2.getSystemMnemonicKeyMask() }, "Menu.crossMenuMnemonic", Boolean.TRUE, "Menu.cancelMode", "hideLastSubmenu", "Menu.preserveTopLevelSelection", Boolean.FALSE, "PopupMenu.font", swingLazyValue1, 
        "PopupMenu.background", color7, "PopupMenu.foreground", color8, "PopupMenu.border", swingLazyValue9, "PopupMenu.popupSound", null, "PopupMenu.selectedWindowInputMapBindings", { 
          "ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", 
          "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", 
          "ctrl ENTER", "return", "SPACE", "return" }, 
        "PopupMenu.selectedWindowInputMapBindings.RightToLeft", { "LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent" }, "PopupMenu.consumeEventOnClose", Boolean.FALSE, "OptionPane.font", swingLazyValue1, "OptionPane.background", color1, "OptionPane.foreground", color6, 
        "OptionPane.messageForeground", color6, "OptionPane.border", swingLazyValue26, "OptionPane.messageAreaBorder", swingLazyValue25, "OptionPane.buttonAreaBorder", swingLazyValue27, "OptionPane.minimumSize", dimensionUIResource1, 
        "OptionPane.errorIcon", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/Error.gif"), "OptionPane.informationIcon", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/Inform.gif"), "OptionPane.warningIcon", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/Warn.gif"), "OptionPane.questionIcon", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/Question.gif"), "OptionPane.windowBindings", { "ESCAPE", "close" }, 
        "OptionPane.errorSound", null, "OptionPane.informationSound", null, "OptionPane.questionSound", null, "OptionPane.warningSound", null, "OptionPane.buttonClickThreshhold", integer1, 
        "Panel.font", swingLazyValue1, "Panel.background", color1, "Panel.foreground", color12, "ProgressBar.font", swingLazyValue1, "ProgressBar.foreground", color9, 
        "ProgressBar.background", color1, "ProgressBar.selectionForeground", color1, "ProgressBar.selectionBackground", color9, "ProgressBar.border", swingLazyValue28, "ProgressBar.cellLength", new Integer(1), 
        "ProgressBar.cellSpacing", integer5, "ProgressBar.repaintInterval", new Integer(50), "ProgressBar.cycleTime", new Integer(3000), "ProgressBar.horizontalSize", new DimensionUIResource(146, 12), "ProgressBar.verticalSize", new DimensionUIResource(12, 146), 
        "Separator.shadow", color5, "Separator.highlight", color4, "Separator.background", color4, "Separator.foreground", color5, "ScrollBar.background", colorUIResource8, 
        "ScrollBar.foreground", color1, "ScrollBar.track", paramUIDefaults.get("scrollbar"), "ScrollBar.trackHighlight", color2, "ScrollBar.thumb", color1, "ScrollBar.thumbHighlight", color4, 
        "ScrollBar.thumbDarkShadow", color2, "ScrollBar.thumbShadow", color5, "ScrollBar.border", null, "ScrollBar.minimumThumbSize", dimensionUIResource2, "ScrollBar.maximumThumbSize", dimensionUIResource3, 
        "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", 
            "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", 
            "HOME", "minScroll", "END", "maxScroll" }), "ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }), "ScrollBar.width", new Integer(16), "ScrollPane.font", swingLazyValue1, "ScrollPane.background", color1, 
        "ScrollPane.foreground", color6, "ScrollPane.border", swingLazyValue31, "ScrollPane.viewportBorder", null, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", 
            "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", 
            "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), "ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft" }), 
        "Viewport.font", swingLazyValue1, "Viewport.background", color1, "Viewport.foreground", color12, "Slider.font", swingLazyValue1, "Slider.foreground", color1, 
        "Slider.background", color1, "Slider.highlight", color4, "Slider.tickColor", Color.black, "Slider.shadow", color5, "Slider.focus", color2, 
        "Slider.border", null, "Slider.horizontalSize", new Dimension(200, 21), "Slider.verticalSize", new Dimension(21, 200), "Slider.minimumHorizontalSize", new Dimension(36, 21), "Slider.minimumVerticalSize", new Dimension(21, 36), 
        "Slider.focusInsets", insetsUIResource4, "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", 
            "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", 
            "HOME", "minScroll", "END", "maxScroll" }), "Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }), "Slider.onlyLeftMouseButtonDrag", Boolean.TRUE, "Spinner.font", swingLazyValue4, 
        "Spinner.background", color1, "Spinner.foreground", color1, "Spinner.border", swingLazyValue31, "Spinner.arrowButtonBorder", null, "Spinner.arrowButtonInsets", null, 
        "Spinner.arrowButtonSize", new Dimension(16, 5), "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "Spinner.editorBorderPainted", Boolean.FALSE, "Spinner.editorAlignment", Integer.valueOf(11), "SplitPane.background", color1, 
        "SplitPane.highlight", color4, "SplitPane.shadow", color5, "SplitPane.darkShadow", color2, "SplitPane.border", swingLazyValue29, "SplitPane.dividerSize", new Integer(7), 
        "SplitPaneDivider.border", swingLazyValue30, "SplitPaneDivider.draggingColor", colorUIResource7, "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", 
            "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", 
            "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), "TabbedPane.font", swingLazyValue1, "TabbedPane.background", color1, 
        "TabbedPane.foreground", color6, "TabbedPane.highlight", color4, "TabbedPane.light", color3, "TabbedPane.shadow", color5, "TabbedPane.darkShadow", color2, 
        "TabbedPane.selected", null, "TabbedPane.focus", color6, "TabbedPane.textIconGap", integer8, "TabbedPane.tabsOverlapBorder", Boolean.FALSE, "TabbedPane.selectionFollowsFocus", Boolean.TRUE, 
        "TabbedPane.labelShift", Integer.valueOf(1), "TabbedPane.selectedLabelShift", Integer.valueOf(-1), "TabbedPane.tabInsets", insetsUIResource5, "TabbedPane.selectedTabPadInsets", insetsUIResource6, "TabbedPane.tabAreaInsets", insetsUIResource7, 
        "TabbedPane.contentBorderInsets", insetsUIResource8, "TabbedPane.tabRunOverlay", new Integer(2), "TabbedPane.tabsOpaque", Boolean.TRUE, "TabbedPane.contentOpaque", Boolean.TRUE, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", 
            "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }), 
        "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }), "Table.font", swingLazyValue1, "Table.foreground", color6, "Table.background", color13, "Table.selectionForeground", color10, 
        "Table.selectionBackground", color9, "Table.dropLineColor", color5, "Table.dropLineShortColor", colorUIResource2, "Table.gridColor", colorUIResource5, "Table.focusCellBackground", color13, 
        "Table.focusCellForeground", color6, "Table.focusCellHighlightBorder", swingLazyValue11, "Table.scrollPaneBorder", swingLazyValue8, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", 
            "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", 
            "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", 
            "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", 
            "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", 
            "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", 
            "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", 
            "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", 
            "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", 
            "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", 
            "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", 
            "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", 
            "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", 
            "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", 
            "F8", "focusHeader" }), "Table.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", 
            "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead", "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", 
            "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", 
            "ctrl KP_LEFT", "selectNextColumnChangeLead", "ctrl PAGE_UP", "scrollRightChangeSelection", "ctrl PAGE_DOWN", "scrollLeftChangeSelection", "ctrl shift PAGE_UP", "scrollRightExtendSelection", "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection" }), 
        "Table.ascendingSortIcon", new SwingLazyValue("sun.swing.icon.SortArrowIcon", null, new Object[] { Boolean.TRUE, "Table.sortIconColor" }), "Table.descendingSortIcon", new SwingLazyValue("sun.swing.icon.SortArrowIcon", null, new Object[] { Boolean.FALSE, "Table.sortIconColor" }), "Table.sortIconColor", color5, "TableHeader.font", swingLazyValue1, "TableHeader.foreground", color6, 
        "TableHeader.background", color1, "TableHeader.cellBorder", swingLazyValue12, "TableHeader.focusCellBackground", paramUIDefaults.getColor("text"), "TableHeader.focusCellForeground", null, "TableHeader.focusCellBorder", null, 
        "TableHeader.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "SPACE", "toggleSortOrder", "LEFT", "selectColumnToLeft", "KP_LEFT", "selectColumnToLeft", "RIGHT", "selectColumnToRight", "KP_RIGHT", "selectColumnToRight", 
            "alt LEFT", "moveColumnLeft", "alt KP_LEFT", "moveColumnLeft", "alt RIGHT", "moveColumnRight", "alt KP_RIGHT", "moveColumnRight", "alt shift LEFT", "resizeLeft", 
            "alt shift KP_LEFT", "resizeLeft", "alt shift RIGHT", "resizeRight", "alt shift KP_RIGHT", "resizeRight", "ESCAPE", "focusTable" }), "TextField.font", swingLazyValue3, "TextField.background", color13, "TextField.foreground", color12, "TextField.shadow", color5, 
        "TextField.darkShadow", color2, "TextField.light", color3, "TextField.highlight", color4, "TextField.inactiveForeground", color11, "TextField.inactiveBackground", color1, 
        "TextField.selectionBackground", color9, "TextField.selectionForeground", color10, "TextField.caretForeground", color12, "TextField.caretBlinkRate", integer7, "TextField.border", swingLazyValue31, 
        "TextField.margin", insetsUIResource1, "FormattedTextField.font", swingLazyValue3, "FormattedTextField.background", color13, "FormattedTextField.foreground", color12, "FormattedTextField.inactiveForeground", color11, 
        "FormattedTextField.inactiveBackground", color1, "FormattedTextField.selectionBackground", color9, "FormattedTextField.selectionForeground", color10, "FormattedTextField.caretForeground", color12, "FormattedTextField.caretBlinkRate", integer7, 
        "FormattedTextField.border", swingLazyValue31, "FormattedTextField.margin", insetsUIResource1, "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
            "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", 
            "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", 
            "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", 
            "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", 
            "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", 
            "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", 
            "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", 
            "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "PasswordField.font", swingLazyValue4, "PasswordField.background", color13, 
        "PasswordField.foreground", color12, "PasswordField.inactiveForeground", color11, "PasswordField.inactiveBackground", color1, "PasswordField.selectionBackground", color9, "PasswordField.selectionForeground", color10, 
        "PasswordField.caretForeground", color12, "PasswordField.caretBlinkRate", integer7, "PasswordField.border", swingLazyValue31, "PasswordField.margin", insetsUIResource1, "PasswordField.echoChar", Character.valueOf('*'), 
        "TextArea.font", swingLazyValue4, "TextArea.background", color13, "TextArea.foreground", color12, "TextArea.inactiveForeground", color11, "TextArea.selectionBackground", color9, 
        "TextArea.selectionForeground", color10, "TextArea.caretForeground", color12, "TextArea.caretBlinkRate", integer7, "TextArea.border", swingLazyValue6, "TextArea.margin", insetsUIResource1, 
        "TextPane.font", swingLazyValue2, "TextPane.background", colorUIResource3, "TextPane.foreground", color12, "TextPane.selectionBackground", color9, "TextPane.selectionForeground", color10, 
        "TextPane.caretForeground", color12, "TextPane.caretBlinkRate", integer7, "TextPane.inactiveForeground", color11, "TextPane.border", swingLazyValue6, "TextPane.margin", insetsUIResource9, 
        "EditorPane.font", swingLazyValue2, "EditorPane.background", colorUIResource3, "EditorPane.foreground", color12, "EditorPane.selectionBackground", color9, "EditorPane.selectionForeground", color10, 
        "EditorPane.caretForeground", color12, "EditorPane.caretBlinkRate", integer7, "EditorPane.inactiveForeground", color11, "EditorPane.border", swingLazyValue6, "EditorPane.margin", insetsUIResource9, 
        "html.pendingImage", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/image-delayed.png"), "html.missingImage", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/image-failed.png"), "TitledBorder.font", swingLazyValue1, "TitledBorder.titleColor", color6, "TitledBorder.border", swingLazyValue7, 
        "ToolBar.font", swingLazyValue1, "ToolBar.background", color1, "ToolBar.foreground", color6, "ToolBar.shadow", color5, "ToolBar.darkShadow", color2, 
        "ToolBar.light", color3, "ToolBar.highlight", color4, "ToolBar.dockingBackground", color1, "ToolBar.dockingForeground", colorUIResource1, "ToolBar.floatingBackground", color1, 
        "ToolBar.floatingForeground", colorUIResource7, "ToolBar.border", swingLazyValue7, "ToolBar.separatorSize", dimensionUIResource4, "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", 
            "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), "ToolTip.font", swingLazyValue3, 
        "ToolTip.background", paramUIDefaults.get("info"), "ToolTip.foreground", paramUIDefaults.get("infoText"), "ToolTip.border", swingLazyValue10, "ToolTipManager.enableToolTipMode", "allWindows", "Tree.paintLines", Boolean.TRUE, 
        "Tree.lineTypeDashed", Boolean.FALSE, "Tree.font", swingLazyValue1, "Tree.background", color13, "Tree.foreground", color12, "Tree.hash", colorUIResource5, 
        "Tree.textForeground", color12, "Tree.textBackground", paramUIDefaults.get("text"), "Tree.selectionForeground", color10, "Tree.selectionBackground", color9, "Tree.selectionBorderColor", colorUIResource2, 
        "Tree.dropLineColor", color5, "Tree.editorBorder", swingLazyValue10, "Tree.leftChildIndent", new Integer(7), "Tree.rightChildIndent", new Integer(13), "Tree.rowHeight", new Integer(16), 
        "Tree.scrollsOnExpand", Boolean.TRUE, "Tree.openIcon", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/TreeOpen.gif"), "Tree.closedIcon", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/TreeClosed.gif"), "Tree.leafIcon", SwingUtilities2.makeIcon(getClass(), BasicLookAndFeel.class, "icons/TreeLeaf.gif"), "Tree.expandedIcon", null, 
        "Tree.collapsedIcon", null, "Tree.changeSelectionWithFocus", Boolean.TRUE, "Tree.drawsFocusBorderAroundIcon", Boolean.FALSE, "Tree.timeFactor", long, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", 
            "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", 
            "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", 
            "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", 
            "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", 
            "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", 
            "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", 
            "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", 
            "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", 
            "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", 
            "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", 
            "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), 
        "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }), "RootPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "shift F10", "postPopup", "CONTEXT_MENU", "postPopup" }), "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" } };
    paramUIDefaults.putDefaults(arrayOfObject3);
  }
  
  static int getFocusAcceleratorKeyMask() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return (toolkit instanceof SunToolkit) ? ((SunToolkit)toolkit).getFocusAcceleratorKeyMask() : 8;
  }
  
  static Object getUIOfType(ComponentUI paramComponentUI, Class paramClass) { return paramClass.isInstance(paramComponentUI) ? paramComponentUI : null; }
  
  protected ActionMap getAudioActionMap() {
    ActionMap actionMap = (ActionMap)UIManager.get("AuditoryCues.actionMap");
    if (actionMap == null) {
      Object[] arrayOfObject = (Object[])UIManager.get("AuditoryCues.cueList");
      if (arrayOfObject != null) {
        actionMap = new ActionMapUIResource();
        for (int i = arrayOfObject.length - 1; i >= 0; i--)
          actionMap.put(arrayOfObject[i], createAudioAction(arrayOfObject[i])); 
      } 
      UIManager.getLookAndFeelDefaults().put("AuditoryCues.actionMap", actionMap);
    } 
    return actionMap;
  }
  
  protected Action createAudioAction(Object paramObject) {
    if (paramObject != null) {
      String str1 = (String)paramObject;
      String str2 = (String)UIManager.get(paramObject);
      return new AudioAction(str1, str2);
    } 
    return null;
  }
  
  private byte[] loadAudioData(final String soundFile) {
    if (paramString == null)
      return null; 
    byte[] arrayOfByte = (byte[])AccessController.doPrivileged(new PrivilegedAction<byte[]>() {
          public byte[] run() {
            try {
              InputStream inputStream = BasicLookAndFeel.this.getClass().getResourceAsStream(soundFile);
              if (inputStream == null)
                return null; 
              BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
              ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
              null = new byte[1024];
              int i;
              while ((i = bufferedInputStream.read(null)) > 0)
                byteArrayOutputStream.write(null, 0, i); 
              bufferedInputStream.close();
              byteArrayOutputStream.flush();
              return byteArrayOutputStream.toByteArray();
            } catch (IOException iOException) {
              System.err.println(iOException.toString());
              return null;
            } 
          }
        });
    if (arrayOfByte == null) {
      System.err.println(getClass().getName() + "/" + paramString + " not found.");
      return null;
    } 
    if (arrayOfByte.length == 0) {
      System.err.println("warning: " + paramString + " is zero-length");
      return null;
    } 
    return arrayOfByte;
  }
  
  protected void playSound(Action paramAction) {
    if (paramAction != null) {
      Object[] arrayOfObject = (Object[])UIManager.get("AuditoryCues.playList");
      if (arrayOfObject != null) {
        HashSet hashSet = new HashSet();
        for (Object object : arrayOfObject)
          hashSet.add(object); 
        String str = (String)paramAction.getValue("Name");
        if (hashSet.contains(str))
          paramAction.actionPerformed(new ActionEvent(this, 1001, str)); 
      } 
    } 
  }
  
  static void installAudioActionMap(ActionMap paramActionMap) {
    LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
    if (lookAndFeel instanceof BasicLookAndFeel)
      paramActionMap.setParent(((BasicLookAndFeel)lookAndFeel).getAudioActionMap()); 
  }
  
  static void playSound(JComponent paramJComponent, Object paramObject) {
    LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
    if (lookAndFeel instanceof BasicLookAndFeel) {
      ActionMap actionMap = paramJComponent.getActionMap();
      if (actionMap != null) {
        Action action = actionMap.get(paramObject);
        if (action != null)
          ((BasicLookAndFeel)lookAndFeel).playSound(action); 
      } 
    } 
  }
  
  class AWTEventHelper extends Object implements AWTEventListener, PrivilegedAction<Object> {
    AWTEventHelper() { AccessController.doPrivileged(this); }
    
    public Object run() {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      if (BasicLookAndFeel.this.invocator == null) {
        toolkit.addAWTEventListener(this, 16L);
      } else {
        toolkit.removeAWTEventListener(BasicLookAndFeel.this.invocator);
      } 
      return null;
    }
    
    public void eventDispatched(AWTEvent param1AWTEvent) {
      int i = param1AWTEvent.getID();
      if ((i & 0x10L) != 0L) {
        MouseEvent mouseEvent = (MouseEvent)param1AWTEvent;
        if (mouseEvent.isPopupTrigger()) {
          MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
          if (arrayOfMenuElement != null && arrayOfMenuElement.length != 0)
            return; 
          Object object = mouseEvent.getSource();
          JComponent jComponent = null;
          if (object instanceof JComponent) {
            jComponent = (JComponent)object;
          } else if (object instanceof BasicSplitPaneDivider) {
            jComponent = (JComponent)((BasicSplitPaneDivider)object).getParent();
          } 
          if (jComponent != null && jComponent.getComponentPopupMenu() != null) {
            Point point = jComponent.getPopupLocation(mouseEvent);
            if (point == null) {
              point = mouseEvent.getPoint();
              point = SwingUtilities.convertPoint((Component)object, point, jComponent);
            } 
            jComponent.getComponentPopupMenu().show(jComponent, point.x, point.y);
            mouseEvent.consume();
          } 
        } 
      } 
      if (i == 501) {
        Object object = param1AWTEvent.getSource();
        if (!(object instanceof Component))
          return; 
        Component component = (Component)object;
        if (component != null)
          for (Component component1 = component; component1 != null && !(component1 instanceof java.awt.Window); component1 = component1.getParent()) {
            if (component1 instanceof JInternalFrame)
              try {
                ((JInternalFrame)component1).setSelected(true);
              } catch (PropertyVetoException propertyVetoException) {} 
          }  
      } 
    }
  }
  
  private class AudioAction extends AbstractAction implements LineListener {
    private String audioResource;
    
    private byte[] audioBuffer;
    
    public AudioAction(String param1String1, String param1String2) {
      super(param1String1);
      this.audioResource = param1String2;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.audioBuffer == null)
        this.audioBuffer = BasicLookAndFeel.this.loadAudioData(this.audioResource); 
      if (this.audioBuffer != null) {
        cancelCurrentSound(null);
        try {
          AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(this.audioBuffer));
          DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
          Clip clip = (Clip)AudioSystem.getLine(info);
          clip.open(audioInputStream);
          clip.addLineListener(this);
          synchronized (BasicLookAndFeel.this.audioLock) {
            BasicLookAndFeel.this.clipPlaying = clip;
          } 
          clip.start();
        } catch (Exception exception) {}
      } 
    }
    
    public void update(LineEvent param1LineEvent) {
      if (param1LineEvent.getType() == LineEvent.Type.STOP)
        cancelCurrentSound((Clip)param1LineEvent.getLine()); 
    }
    
    private void cancelCurrentSound(Clip param1Clip) {
      Clip clip = null;
      synchronized (BasicLookAndFeel.this.audioLock) {
        if (param1Clip == null || param1Clip == BasicLookAndFeel.this.clipPlaying) {
          clip = BasicLookAndFeel.this.clipPlaying;
          BasicLookAndFeel.this.clipPlaying = null;
        } 
      } 
      if (clip != null) {
        clip.removeLineListener(this);
        clip.close();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicLookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */