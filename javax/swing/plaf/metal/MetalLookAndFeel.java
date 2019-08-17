package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.LayoutStyle;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.awt.AppContext;
import sun.awt.OSInfo;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLayoutStyle;
import sun.swing.SwingLazyValue;
import sun.swing.SwingUtilities2;

public class MetalLookAndFeel extends BasicLookAndFeel {
  private static boolean METAL_LOOK_AND_FEEL_INITED = false;
  
  private static boolean checkedWindows;
  
  private static boolean isWindows;
  
  private static boolean checkedSystemFontSettings;
  
  private static boolean useSystemFonts;
  
  static ReferenceQueue<LookAndFeel> queue = new ReferenceQueue();
  
  static boolean isWindows() {
    if (!checkedWindows) {
      OSInfo.OSType oSType = (OSInfo.OSType)AccessController.doPrivileged(OSInfo.getOSTypeAction());
      if (oSType == OSInfo.OSType.WINDOWS) {
        isWindows = true;
        String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.useSystemFontSettings"));
        useSystemFonts = (str != null && Boolean.valueOf(str).booleanValue());
      } 
      checkedWindows = true;
    } 
    return isWindows;
  }
  
  static boolean useSystemFonts() {
    if (isWindows() && useSystemFonts) {
      if (METAL_LOOK_AND_FEEL_INITED) {
        Object object = UIManager.get("Application.useSystemFontSettings");
        return (object == null || Boolean.TRUE.equals(object));
      } 
      return true;
    } 
    return false;
  }
  
  private static boolean useHighContrastTheme() {
    if (isWindows() && useSystemFonts()) {
      Boolean bool = (Boolean)Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
      return (bool == null) ? false : bool.booleanValue();
    } 
    return false;
  }
  
  static boolean usingOcean() { return getCurrentTheme() instanceof OceanTheme; }
  
  public String getName() { return "Metal"; }
  
  public String getID() { return "Metal"; }
  
  public String getDescription() { return "The Java(tm) Look and Feel"; }
  
  public boolean isNativeLookAndFeel() { return false; }
  
  public boolean isSupportedLookAndFeel() { return true; }
  
  public boolean getSupportsWindowDecorations() { return true; }
  
  protected void initClassDefaults(UIDefaults paramUIDefaults) {
    super.initClassDefaults(paramUIDefaults);
    Object[] arrayOfObject = { 
        "ButtonUI", "javax.swing.plaf.metal.MetalButtonUI", "CheckBoxUI", "javax.swing.plaf.metal.MetalCheckBoxUI", "ComboBoxUI", "javax.swing.plaf.metal.MetalComboBoxUI", "DesktopIconUI", "javax.swing.plaf.metal.MetalDesktopIconUI", "FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI", 
        "InternalFrameUI", "javax.swing.plaf.metal.MetalInternalFrameUI", "LabelUI", "javax.swing.plaf.metal.MetalLabelUI", "PopupMenuSeparatorUI", "javax.swing.plaf.metal.MetalPopupMenuSeparatorUI", "ProgressBarUI", "javax.swing.plaf.metal.MetalProgressBarUI", "RadioButtonUI", "javax.swing.plaf.metal.MetalRadioButtonUI", 
        "ScrollBarUI", "javax.swing.plaf.metal.MetalScrollBarUI", "ScrollPaneUI", "javax.swing.plaf.metal.MetalScrollPaneUI", "SeparatorUI", "javax.swing.plaf.metal.MetalSeparatorUI", "SliderUI", "javax.swing.plaf.metal.MetalSliderUI", "SplitPaneUI", "javax.swing.plaf.metal.MetalSplitPaneUI", 
        "TabbedPaneUI", "javax.swing.plaf.metal.MetalTabbedPaneUI", "TextFieldUI", "javax.swing.plaf.metal.MetalTextFieldUI", "ToggleButtonUI", "javax.swing.plaf.metal.MetalToggleButtonUI", "ToolBarUI", "javax.swing.plaf.metal.MetalToolBarUI", "ToolTipUI", "javax.swing.plaf.metal.MetalToolTipUI", 
        "TreeUI", "javax.swing.plaf.metal.MetalTreeUI", "RootPaneUI", "javax.swing.plaf.metal.MetalRootPaneUI" };
    paramUIDefaults.putDefaults(arrayOfObject);
  }
  
  protected void initSystemColorDefaults(UIDefaults paramUIDefaults) {
    MetalTheme metalTheme = getCurrentTheme();
    ColorUIResource colorUIResource = metalTheme.getControl();
    Object[] arrayOfObject = { 
        "desktop", metalTheme.getDesktopColor(), "activeCaption", metalTheme.getWindowTitleBackground(), "activeCaptionText", metalTheme.getWindowTitleForeground(), "activeCaptionBorder", metalTheme.getPrimaryControlShadow(), "inactiveCaption", metalTheme.getWindowTitleInactiveBackground(), 
        "inactiveCaptionText", metalTheme.getWindowTitleInactiveForeground(), "inactiveCaptionBorder", metalTheme.getControlShadow(), "window", metalTheme.getWindowBackground(), "windowBorder", colorUIResource, "windowText", metalTheme.getUserTextColor(), 
        "menu", metalTheme.getMenuBackground(), "menuText", metalTheme.getMenuForeground(), "text", metalTheme.getWindowBackground(), "textText", metalTheme.getUserTextColor(), "textHighlight", metalTheme.getTextHighlightColor(), 
        "textHighlightText", metalTheme.getHighlightedTextColor(), "textInactiveText", metalTheme.getInactiveSystemTextColor(), "control", colorUIResource, "controlText", metalTheme.getControlTextColor(), "controlHighlight", metalTheme.getControlHighlight(), 
        "controlLtHighlight", metalTheme.getControlHighlight(), "controlShadow", metalTheme.getControlShadow(), "controlDkShadow", metalTheme.getControlDarkShadow(), "scrollbar", colorUIResource, "info", metalTheme.getPrimaryControl(), 
        "infoText", metalTheme.getPrimaryControlInfo() };
    paramUIDefaults.putDefaults(arrayOfObject);
  }
  
  private void initResourceBundle(UIDefaults paramUIDefaults) { paramUIDefaults.addResourceBundle("com.sun.swing.internal.plaf.metal.resources.metal"); }
  
  protected void initComponentDefaults(UIDefaults paramUIDefaults) {
    super.initComponentDefaults(paramUIDefaults);
    initResourceBundle(paramUIDefaults);
    ColorUIResource colorUIResource1 = getAcceleratorForeground();
    ColorUIResource colorUIResource2 = getAcceleratorSelectedForeground();
    ColorUIResource colorUIResource3 = getControl();
    ColorUIResource colorUIResource4 = getControlHighlight();
    ColorUIResource colorUIResource5 = getControlShadow();
    ColorUIResource colorUIResource6 = getControlDarkShadow();
    ColorUIResource colorUIResource7 = getControlTextColor();
    ColorUIResource colorUIResource8 = getFocusColor();
    ColorUIResource colorUIResource9 = getInactiveControlTextColor();
    ColorUIResource colorUIResource10 = getMenuBackground();
    ColorUIResource colorUIResource11 = getMenuSelectedBackground();
    ColorUIResource colorUIResource12 = getMenuDisabledForeground();
    ColorUIResource colorUIResource13 = getMenuSelectedForeground();
    ColorUIResource colorUIResource14 = getPrimaryControl();
    ColorUIResource colorUIResource15 = getPrimaryControlDarkShadow();
    ColorUIResource colorUIResource16 = getPrimaryControlShadow();
    ColorUIResource colorUIResource17 = getSystemTextColor();
    InsetsUIResource insetsUIResource1 = new InsetsUIResource(0, 0, 0, 0);
    Integer integer = Integer.valueOf(0);
    SwingLazyValue swingLazyValue1 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getTextFieldBorder");
    UIDefaults.LazyValue lazyValue1 = paramUIDefaults -> new MetalBorders.DialogBorder();
    UIDefaults.LazyValue lazyValue2 = paramUIDefaults -> new MetalBorders.QuestionDialogBorder();
    UIDefaults.LazyInputMap lazyInputMap1 = new UIDefaults.LazyInputMap(new Object[] { 
          "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
          "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", 
          "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", 
          "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", 
          "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", 
          "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", 
          "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", 
          "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation" });
    UIDefaults.LazyInputMap lazyInputMap2 = new UIDefaults.LazyInputMap(new Object[] { 
          "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
          "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", 
          "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-begin-line", "ctrl KP_LEFT", "caret-begin-line", 
          "ctrl RIGHT", "caret-end-line", "ctrl KP_RIGHT", "caret-end-line", "ctrl shift LEFT", "selection-begin-line", "ctrl shift KP_LEFT", "selection-begin-line", "ctrl shift RIGHT", "selection-end-line", 
          "ctrl shift KP_RIGHT", "selection-end-line", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", 
          "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", 
          "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", 
          "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation" });
    UIDefaults.LazyInputMap lazyInputMap3 = new UIDefaults.LazyInputMap(new Object[] { 
          "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
          "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", 
          "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", 
          "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", 
          "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", 
          "shift END", "selection-end-line", "UP", "caret-up", "KP_UP", "caret-up", "DOWN", "caret-down", "KP_DOWN", "caret-down", 
          "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", 
          "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift KP_UP", "selection-up", "shift DOWN", "selection-down", "shift KP_DOWN", "selection-down", 
          "ENTER", "insert-break", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", 
          "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", 
          "KP_LEFT", "caret-backward", "TAB", "insert-tab", "ctrl BACK_SLASH", "unselect", "ctrl HOME", "caret-begin", "ctrl END", "caret-end", 
          "ctrl shift HOME", "selection-begin", "ctrl shift END", "selection-end", "ctrl T", "next-link-action", "ctrl shift T", "previous-link-action", "ctrl SPACE", "activate-link-action", 
          "control shift O", "toggle-componentOrientation" });
    SwingLazyValue swingLazyValue2 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$ScrollPaneBorder");
    SwingLazyValue swingLazyValue3 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getButtonBorder");
    SwingLazyValue swingLazyValue4 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getToggleButtonBorder");
    SwingLazyValue swingLazyValue5 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { colorUIResource5 });
    SwingLazyValue swingLazyValue6 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getDesktopIconBorder");
    SwingLazyValue swingLazyValue7 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$MenuBarBorder");
    SwingLazyValue swingLazyValue8 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$PopupMenuBorder");
    SwingLazyValue swingLazyValue9 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$MenuItemBorder");
    String str = "-";
    SwingLazyValue swingLazyValue10 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$ToolBarBorder");
    SwingLazyValue swingLazyValue11 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { colorUIResource6, new Integer(1) });
    SwingLazyValue swingLazyValue12 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { colorUIResource15 });
    SwingLazyValue swingLazyValue13 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { colorUIResource6 });
    SwingLazyValue swingLazyValue14 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { colorUIResource8 });
    InsetsUIResource insetsUIResource2 = new InsetsUIResource(4, 2, 0, 6);
    InsetsUIResource insetsUIResource3 = new InsetsUIResource(0, 9, 1, 9);
    Object[] arrayOfObject1 = new Object[1];
    arrayOfObject1[0] = new Integer(16);
    Object[] arrayOfObject2 = { "OptionPane.errorSound", "OptionPane.informationSound", "OptionPane.questionSound", "OptionPane.warningSound" };
    MetalTheme metalTheme = getCurrentTheme();
    FontActiveValue fontActiveValue1 = new FontActiveValue(metalTheme, 3);
    FontActiveValue fontActiveValue2 = new FontActiveValue(metalTheme, 0);
    FontActiveValue fontActiveValue3 = new FontActiveValue(metalTheme, 2);
    FontActiveValue fontActiveValue4 = new FontActiveValue(metalTheme, 4);
    FontActiveValue fontActiveValue5 = new FontActiveValue(metalTheme, 5);
    FontActiveValue fontActiveValue6 = new FontActiveValue(metalTheme, 1);
    Object[] arrayOfObject3 = { 
        "AuditoryCues.defaultCueList", arrayOfObject2, "AuditoryCues.playList", null, "TextField.border", swingLazyValue1, "TextField.font", fontActiveValue3, "PasswordField.border", swingLazyValue1, 
        "PasswordField.font", fontActiveValue3, "PasswordField.echoChar", Character.valueOf('•'), "TextArea.font", fontActiveValue3, "TextPane.background", paramUIDefaults.get("window"), "TextPane.font", fontActiveValue3, 
        "EditorPane.background", paramUIDefaults.get("window"), "EditorPane.font", fontActiveValue3, "TextField.focusInputMap", lazyInputMap1, "PasswordField.focusInputMap", lazyInputMap2, "TextArea.focusInputMap", lazyInputMap3, 
        "TextPane.focusInputMap", lazyInputMap3, "EditorPane.focusInputMap", lazyInputMap3, "FormattedTextField.border", swingLazyValue1, "FormattedTextField.font", fontActiveValue3, "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
            "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", 
            "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", 
            "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", 
            "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", 
            "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", 
            "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", 
            "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", 
            "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), 
        "Button.defaultButtonFollowsFocus", Boolean.FALSE, "Button.disabledText", colorUIResource9, "Button.select", colorUIResource5, "Button.border", swingLazyValue3, "Button.font", fontActiveValue2, 
        "Button.focus", colorUIResource8, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "CheckBox.disabledText", colorUIResource9, "Checkbox.select", colorUIResource5, "CheckBox.font", fontActiveValue2, 
        "CheckBox.focus", colorUIResource8, "CheckBox.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxIcon"), "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "CheckBox.totalInsets", new Insets(4, 4, 4, 4), "RadioButton.disabledText", colorUIResource9, 
        "RadioButton.select", colorUIResource5, "RadioButton.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonIcon"), "RadioButton.font", fontActiveValue2, "RadioButton.focus", colorUIResource8, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), 
        "RadioButton.totalInsets", new Insets(4, 4, 4, 4), "ToggleButton.select", colorUIResource5, "ToggleButton.disabledText", colorUIResource9, "ToggleButton.focus", colorUIResource8, "ToggleButton.border", swingLazyValue4, 
        "ToggleButton.font", fontActiveValue2, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "FileView.directoryIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), "FileView.fileIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"), "FileView.computerIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeComputerIcon"), 
        "FileView.hardDriveIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeHardDriveIcon"), "FileView.floppyDriveIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFloppyDriveIcon"), "FileChooser.detailsViewIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserDetailViewIcon"), "FileChooser.homeFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserHomeFolderIcon"), "FileChooser.listViewIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserListViewIcon"), 
        "FileChooser.newFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserNewFolderIcon"), "FileChooser.upFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserUpFolderIcon"), "FileChooser.usesSingleFilePane", Boolean.TRUE, "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up" }), "ToolTip.font", fontActiveValue6, 
        "ToolTip.border", swingLazyValue12, "ToolTip.borderInactive", swingLazyValue13, "ToolTip.backgroundInactive", colorUIResource3, "ToolTip.foregroundInactive", colorUIResource6, "ToolTip.hideAccelerator", Boolean.FALSE, 
        "ToolTipManager.enableToolTipMode", "activeApplication", "Slider.font", fontActiveValue2, "Slider.border", null, "Slider.foreground", colorUIResource16, "Slider.focus", colorUIResource8, 
        "Slider.focusInsets", insetsUIResource1, "Slider.trackWidth", new Integer(7), "Slider.majorTickLength", new Integer(6), "Slider.horizontalThumbIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getHorizontalSliderThumbIcon"), "Slider.verticalThumbIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getVerticalSliderThumbIcon"), 
        "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", 
            "ctrl PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", 
            "PAGE_UP", "positiveBlockIncrement", "ctrl PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "ProgressBar.font", fontActiveValue2, "ProgressBar.foreground", colorUIResource16, "ProgressBar.selectionBackground", colorUIResource15, "ProgressBar.border", swingLazyValue11, 
        "ProgressBar.cellSpacing", integer, "ProgressBar.cellLength", Integer.valueOf(1), "ComboBox.background", colorUIResource3, "ComboBox.foreground", colorUIResource7, "ComboBox.selectionBackground", colorUIResource16, 
        "ComboBox.selectionForeground", colorUIResource7, "ComboBox.font", fontActiveValue2, "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", 
            "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", 
            "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious" }), "InternalFrame.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameDefaultMenuIcon"), "InternalFrame.border", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$InternalFrameBorder"), 
        "InternalFrame.optionDialogBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$OptionDialogBorder"), "InternalFrame.paletteBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$PaletteBorder"), "InternalFrame.paletteTitleHeight", new Integer(11), "InternalFrame.paletteCloseIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory$PaletteCloseIcon"), "InternalFrame.closeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameCloseIcon", arrayOfObject1), 
        "InternalFrame.maximizeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameMaximizeIcon", arrayOfObject1), "InternalFrame.iconifyIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameMinimizeIcon", arrayOfObject1), "InternalFrame.minimizeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameAltMaximizeIcon", arrayOfObject1), "InternalFrame.titleFont", fontActiveValue4, "InternalFrame.windowBindings", null, 
        "InternalFrame.closeSound", "sounds/FrameClose.wav", "InternalFrame.maximizeSound", "sounds/FrameMaximize.wav", "InternalFrame.minimizeSound", "sounds/FrameMinimize.wav", "InternalFrame.restoreDownSound", "sounds/FrameRestoreDown.wav", "InternalFrame.restoreUpSound", "sounds/FrameRestoreUp.wav", 
        "DesktopIcon.border", swingLazyValue6, "DesktopIcon.font", fontActiveValue2, "DesktopIcon.foreground", colorUIResource7, "DesktopIcon.background", colorUIResource3, "DesktopIcon.width", Integer.valueOf(160), 
        "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", 
            "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", 
            "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", 
            "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", 
            "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", 
            "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }), "TitledBorder.font", fontActiveValue2, "TitledBorder.titleColor", colorUIResource17, "TitledBorder.border", swingLazyValue5, "Label.font", fontActiveValue2, 
        "Label.foreground", colorUIResource17, "Label.disabledForeground", getInactiveSystemTextColor(), "List.font", fontActiveValue2, "List.focusCellHighlightBorder", swingLazyValue14, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
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
            "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), 
        "ScrollBar.background", colorUIResource3, "ScrollBar.highlight", colorUIResource4, "ScrollBar.shadow", colorUIResource5, "ScrollBar.darkShadow", colorUIResource6, "ScrollBar.thumb", colorUIResource16, 
        "ScrollBar.thumbShadow", colorUIResource15, "ScrollBar.thumbHighlight", colorUIResource14, "ScrollBar.width", new Integer(17), "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", 
            "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", 
            "HOME", "minScroll", "END", "maxScroll" }), 
        "ScrollPane.border", swingLazyValue2, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", 
            "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", 
            "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), "TabbedPane.font", fontActiveValue2, "TabbedPane.tabAreaBackground", colorUIResource3, "TabbedPane.background", colorUIResource5, 
        "TabbedPane.light", colorUIResource3, "TabbedPane.focus", colorUIResource15, "TabbedPane.selected", colorUIResource3, "TabbedPane.selectHighlight", colorUIResource4, "TabbedPane.tabAreaInsets", insetsUIResource2, 
        "TabbedPane.tabInsets", insetsUIResource3, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", 
            "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }), "Table.font", fontActiveValue3, "Table.focusCellHighlightBorder", swingLazyValue14, 
        "Table.scrollPaneBorder", swingLazyValue2, "Table.dropLineColor", colorUIResource8, "Table.dropLineShortColor", colorUIResource15, "Table.gridColor", colorUIResource5, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
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
            "F8", "focusHeader" }), 
        "Table.ascendingSortIcon", SwingUtilities2.makeIcon(getClass(), MetalLookAndFeel.class, "icons/sortUp.png"), "Table.descendingSortIcon", SwingUtilities2.makeIcon(getClass(), MetalLookAndFeel.class, "icons/sortDown.png"), "TableHeader.font", fontActiveValue3, "TableHeader.cellBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$TableHeaderBorder"), "MenuBar.border", swingLazyValue7, 
        "MenuBar.font", fontActiveValue1, "MenuBar.windowBindings", { "F10", "takeFocus" }, "Menu.border", swingLazyValue9, "Menu.borderPainted", Boolean.TRUE, "Menu.menuPopupOffsetX", integer, 
        "Menu.menuPopupOffsetY", integer, "Menu.submenuPopupOffsetX", new Integer(-4), "Menu.submenuPopupOffsetY", new Integer(-3), "Menu.font", fontActiveValue1, "Menu.selectionForeground", colorUIResource13, 
        "Menu.selectionBackground", colorUIResource11, "Menu.disabledForeground", colorUIResource12, "Menu.acceleratorFont", fontActiveValue5, "Menu.acceleratorForeground", colorUIResource1, "Menu.acceleratorSelectionForeground", colorUIResource2, 
        "Menu.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"), "Menu.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuArrowIcon"), "MenuItem.border", swingLazyValue9, "MenuItem.borderPainted", Boolean.TRUE, "MenuItem.font", fontActiveValue1, 
        "MenuItem.selectionForeground", colorUIResource13, "MenuItem.selectionBackground", colorUIResource11, "MenuItem.disabledForeground", colorUIResource12, "MenuItem.acceleratorFont", fontActiveValue5, "MenuItem.acceleratorForeground", colorUIResource1, 
        "MenuItem.acceleratorSelectionForeground", colorUIResource2, "MenuItem.acceleratorDelimiter", str, "MenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"), "MenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), "MenuItem.commandSound", "sounds/MenuItemCommand.wav", 
        "OptionPane.windowBindings", { "ESCAPE", "close" }, "OptionPane.informationSound", "sounds/OptionPaneInformation.wav", "OptionPane.warningSound", "sounds/OptionPaneWarning.wav", "OptionPane.errorSound", "sounds/OptionPaneError.wav", "OptionPane.questionSound", "sounds/OptionPaneQuestion.wav", 
        "OptionPane.errorDialog.border.background", new ColorUIResource(153, 51, 51), "OptionPane.errorDialog.titlePane.foreground", new ColorUIResource(51, 0, 0), "OptionPane.errorDialog.titlePane.background", new ColorUIResource(255, 153, 153), "OptionPane.errorDialog.titlePane.shadow", new ColorUIResource(204, 102, 102), "OptionPane.questionDialog.border.background", new ColorUIResource(51, 102, 51), 
        "OptionPane.questionDialog.titlePane.foreground", new ColorUIResource(0, 51, 0), "OptionPane.questionDialog.titlePane.background", new ColorUIResource(153, 204, 153), "OptionPane.questionDialog.titlePane.shadow", new ColorUIResource(102, 153, 102), "OptionPane.warningDialog.border.background", new ColorUIResource(153, 102, 51), "OptionPane.warningDialog.titlePane.foreground", new ColorUIResource(102, 51, 0), 
        "OptionPane.warningDialog.titlePane.background", new ColorUIResource(255, 204, 153), "OptionPane.warningDialog.titlePane.shadow", new ColorUIResource(204, 153, 102), "Separator.background", getSeparatorBackground(), "Separator.foreground", getSeparatorForeground(), "PopupMenu.border", swingLazyValue8, 
        "PopupMenu.popupSound", "sounds/PopupMenuPopup.wav", "PopupMenu.font", fontActiveValue1, "CheckBoxMenuItem.border", swingLazyValue9, "CheckBoxMenuItem.borderPainted", Boolean.TRUE, "CheckBoxMenuItem.font", fontActiveValue1, 
        "CheckBoxMenuItem.selectionForeground", colorUIResource13, "CheckBoxMenuItem.selectionBackground", colorUIResource11, "CheckBoxMenuItem.disabledForeground", colorUIResource12, "CheckBoxMenuItem.acceleratorFont", fontActiveValue5, "CheckBoxMenuItem.acceleratorForeground", colorUIResource1, 
        "CheckBoxMenuItem.acceleratorSelectionForeground", colorUIResource2, "CheckBoxMenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxMenuItemIcon"), "CheckBoxMenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), "CheckBoxMenuItem.commandSound", "sounds/MenuItemCommand.wav", "RadioButtonMenuItem.border", swingLazyValue9, 
        "RadioButtonMenuItem.borderPainted", Boolean.TRUE, "RadioButtonMenuItem.font", fontActiveValue1, "RadioButtonMenuItem.selectionForeground", colorUIResource13, "RadioButtonMenuItem.selectionBackground", colorUIResource11, "RadioButtonMenuItem.disabledForeground", colorUIResource12, 
        "RadioButtonMenuItem.acceleratorFont", fontActiveValue5, "RadioButtonMenuItem.acceleratorForeground", colorUIResource1, "RadioButtonMenuItem.acceleratorSelectionForeground", colorUIResource2, "RadioButtonMenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonMenuItemIcon"), "RadioButtonMenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), 
        "RadioButtonMenuItem.commandSound", "sounds/MenuItemCommand.wav", "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "Spinner.arrowButtonInsets", insetsUIResource1, "Spinner.border", swingLazyValue1, "Spinner.arrowButtonBorder", swingLazyValue3, 
        "Spinner.font", fontActiveValue2, "SplitPane.dividerSize", new Integer(10), "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", 
            "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", 
            "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), "SplitPane.centerOneTouchButtons", Boolean.FALSE, "SplitPane.dividerFocusColor", colorUIResource14, 
        "Tree.font", fontActiveValue3, "Tree.textBackground", getWindowBackground(), "Tree.selectionBorderColor", colorUIResource8, "Tree.openIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), "Tree.closedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), 
        "Tree.leafIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"), "Tree.expandedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeControlIcon", new Object[] { Boolean.valueOf(false) }), "Tree.collapsedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeControlIcon", new Object[] { Boolean.valueOf(true) }), "Tree.line", colorUIResource14, "Tree.hash", colorUIResource14, 
        "Tree.rowHeight", integer, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ADD", "expand", "SUBTRACT", "collapse", "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", 
            "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", 
            "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", 
            "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", 
            "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", 
            "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", 
            "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", 
            "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", 
            "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", 
            "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", 
            "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", 
            "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }), "ToolBar.border", swingLazyValue10, "ToolBar.background", colorUIResource10, 
        "ToolBar.foreground", getMenuForeground(), "ToolBar.font", fontActiveValue1, "ToolBar.dockingBackground", colorUIResource10, "ToolBar.floatingBackground", colorUIResource10, "ToolBar.dockingForeground", colorUIResource15, 
        "ToolBar.floatingForeground", colorUIResource14, "ToolBar.rolloverBorder", paramUIDefaults -> MetalBorders.getToolBarRolloverBorder(), "ToolBar.nonrolloverBorder", paramUIDefaults -> MetalBorders.getToolBarNonrolloverBorder(), "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", 
            "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), "RootPane.frameBorder", paramUIDefaults -> new MetalBorders.FrameBorder(), 
        "RootPane.plainDialogBorder", lazyValue1, "RootPane.informationDialogBorder", lazyValue1, "RootPane.errorDialogBorder", paramUIDefaults -> new MetalBorders.ErrorDialogBorder(), "RootPane.colorChooserDialogBorder", lazyValue2, "RootPane.fileChooserDialogBorder", lazyValue2, 
        "RootPane.questionDialogBorder", lazyValue2, "RootPane.warningDialogBorder", paramUIDefaults -> new MetalBorders.WarningDialogBorder(), "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" } };
    paramUIDefaults.putDefaults(arrayOfObject3);
    if (isWindows() && useSystemFonts() && metalTheme.isSystemTheme()) {
      MetalFontDesktopProperty metalFontDesktopProperty = new MetalFontDesktopProperty("win.messagebox.font.height", 0);
      arrayOfObject3 = new Object[] { "OptionPane.messageFont", metalFontDesktopProperty, "OptionPane.buttonFont", metalFontDesktopProperty };
      paramUIDefaults.putDefaults(arrayOfObject3);
    } 
    flushUnreferenced();
    boolean bool = SwingUtilities2.isLocalDisplay();
    SwingUtilities2.AATextInfo aATextInfo = SwingUtilities2.AATextInfo.getAATextInfo(bool);
    paramUIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aATextInfo);
    new AATextListener(this);
  }
  
  protected void createDefaultTheme() { getCurrentTheme(); }
  
  public UIDefaults getDefaults() {
    METAL_LOOK_AND_FEEL_INITED = true;
    createDefaultTheme();
    UIDefaults uIDefaults = super.getDefaults();
    MetalTheme metalTheme = getCurrentTheme();
    metalTheme.addCustomEntriesToTable(uIDefaults);
    metalTheme.install();
    return uIDefaults;
  }
  
  public void provideErrorFeedback(Component paramComponent) { super.provideErrorFeedback(paramComponent); }
  
  public static void setCurrentTheme(MetalTheme paramMetalTheme) {
    if (paramMetalTheme == null)
      throw new NullPointerException("Can't have null theme"); 
    AppContext.getAppContext().put("currentMetalTheme", paramMetalTheme);
  }
  
  public static MetalTheme getCurrentTheme() {
    AppContext appContext = AppContext.getAppContext();
    MetalTheme metalTheme = (MetalTheme)appContext.get("currentMetalTheme");
    if (metalTheme == null) {
      if (useHighContrastTheme()) {
        metalTheme = new MetalHighContrastTheme();
      } else {
        String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.metalTheme"));
        if ("steel".equals(str)) {
          metalTheme = new DefaultMetalTheme();
        } else {
          metalTheme = new OceanTheme();
        } 
      } 
      setCurrentTheme(metalTheme);
    } 
    return metalTheme;
  }
  
  public Icon getDisabledIcon(JComponent paramJComponent, Icon paramIcon) { return (paramIcon instanceof ImageIcon && usingOcean()) ? MetalUtils.getOceanDisabledButtonIcon(((ImageIcon)paramIcon).getImage()) : super.getDisabledIcon(paramJComponent, paramIcon); }
  
  public Icon getDisabledSelectedIcon(JComponent paramJComponent, Icon paramIcon) { return (paramIcon instanceof ImageIcon && usingOcean()) ? MetalUtils.getOceanDisabledButtonIcon(((ImageIcon)paramIcon).getImage()) : super.getDisabledSelectedIcon(paramJComponent, paramIcon); }
  
  public static FontUIResource getControlTextFont() { return getCurrentTheme().getControlTextFont(); }
  
  public static FontUIResource getSystemTextFont() { return getCurrentTheme().getSystemTextFont(); }
  
  public static FontUIResource getUserTextFont() { return getCurrentTheme().getUserTextFont(); }
  
  public static FontUIResource getMenuTextFont() { return getCurrentTheme().getMenuTextFont(); }
  
  public static FontUIResource getWindowTitleFont() { return getCurrentTheme().getWindowTitleFont(); }
  
  public static FontUIResource getSubTextFont() { return getCurrentTheme().getSubTextFont(); }
  
  public static ColorUIResource getDesktopColor() { return getCurrentTheme().getDesktopColor(); }
  
  public static ColorUIResource getFocusColor() { return getCurrentTheme().getFocusColor(); }
  
  public static ColorUIResource getWhite() { return getCurrentTheme().getWhite(); }
  
  public static ColorUIResource getBlack() { return getCurrentTheme().getBlack(); }
  
  public static ColorUIResource getControl() { return getCurrentTheme().getControl(); }
  
  public static ColorUIResource getControlShadow() { return getCurrentTheme().getControlShadow(); }
  
  public static ColorUIResource getControlDarkShadow() { return getCurrentTheme().getControlDarkShadow(); }
  
  public static ColorUIResource getControlInfo() { return getCurrentTheme().getControlInfo(); }
  
  public static ColorUIResource getControlHighlight() { return getCurrentTheme().getControlHighlight(); }
  
  public static ColorUIResource getControlDisabled() { return getCurrentTheme().getControlDisabled(); }
  
  public static ColorUIResource getPrimaryControl() { return getCurrentTheme().getPrimaryControl(); }
  
  public static ColorUIResource getPrimaryControlShadow() { return getCurrentTheme().getPrimaryControlShadow(); }
  
  public static ColorUIResource getPrimaryControlDarkShadow() { return getCurrentTheme().getPrimaryControlDarkShadow(); }
  
  public static ColorUIResource getPrimaryControlInfo() { return getCurrentTheme().getPrimaryControlInfo(); }
  
  public static ColorUIResource getPrimaryControlHighlight() { return getCurrentTheme().getPrimaryControlHighlight(); }
  
  public static ColorUIResource getSystemTextColor() { return getCurrentTheme().getSystemTextColor(); }
  
  public static ColorUIResource getControlTextColor() { return getCurrentTheme().getControlTextColor(); }
  
  public static ColorUIResource getInactiveControlTextColor() { return getCurrentTheme().getInactiveControlTextColor(); }
  
  public static ColorUIResource getInactiveSystemTextColor() { return getCurrentTheme().getInactiveSystemTextColor(); }
  
  public static ColorUIResource getUserTextColor() { return getCurrentTheme().getUserTextColor(); }
  
  public static ColorUIResource getTextHighlightColor() { return getCurrentTheme().getTextHighlightColor(); }
  
  public static ColorUIResource getHighlightedTextColor() { return getCurrentTheme().getHighlightedTextColor(); }
  
  public static ColorUIResource getWindowBackground() { return getCurrentTheme().getWindowBackground(); }
  
  public static ColorUIResource getWindowTitleBackground() { return getCurrentTheme().getWindowTitleBackground(); }
  
  public static ColorUIResource getWindowTitleForeground() { return getCurrentTheme().getWindowTitleForeground(); }
  
  public static ColorUIResource getWindowTitleInactiveBackground() { return getCurrentTheme().getWindowTitleInactiveBackground(); }
  
  public static ColorUIResource getWindowTitleInactiveForeground() { return getCurrentTheme().getWindowTitleInactiveForeground(); }
  
  public static ColorUIResource getMenuBackground() { return getCurrentTheme().getMenuBackground(); }
  
  public static ColorUIResource getMenuForeground() { return getCurrentTheme().getMenuForeground(); }
  
  public static ColorUIResource getMenuSelectedBackground() { return getCurrentTheme().getMenuSelectedBackground(); }
  
  public static ColorUIResource getMenuSelectedForeground() { return getCurrentTheme().getMenuSelectedForeground(); }
  
  public static ColorUIResource getMenuDisabledForeground() { return getCurrentTheme().getMenuDisabledForeground(); }
  
  public static ColorUIResource getSeparatorBackground() { return getCurrentTheme().getSeparatorBackground(); }
  
  public static ColorUIResource getSeparatorForeground() { return getCurrentTheme().getSeparatorForeground(); }
  
  public static ColorUIResource getAcceleratorForeground() { return getCurrentTheme().getAcceleratorForeground(); }
  
  public static ColorUIResource getAcceleratorSelectedForeground() { return getCurrentTheme().getAcceleratorSelectedForeground(); }
  
  public LayoutStyle getLayoutStyle() { return INSTANCE; }
  
  static void flushUnreferenced() {
    AATextListener aATextListener;
    while ((aATextListener = (AATextListener)queue.poll()) != null)
      aATextListener.dispose(); 
  }
  
  static class AATextListener extends WeakReference<LookAndFeel> implements PropertyChangeListener {
    private String key = "awt.font.desktophints";
    
    private static boolean updatePending;
    
    AATextListener(LookAndFeel param1LookAndFeel) {
      super(param1LookAndFeel, MetalLookAndFeel.queue);
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      toolkit.addPropertyChangeListener(this.key, this);
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      LookAndFeel lookAndFeel = (LookAndFeel)get();
      if (lookAndFeel == null || lookAndFeel != UIManager.getLookAndFeel()) {
        dispose();
        return;
      } 
      UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
      boolean bool = SwingUtilities2.isLocalDisplay();
      SwingUtilities2.AATextInfo aATextInfo = SwingUtilities2.AATextInfo.getAATextInfo(bool);
      uIDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, aATextInfo);
      updateUI();
    }
    
    void dispose() {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      toolkit.removePropertyChangeListener(this.key, this);
    }
    
    private static void updateWindowUI(Window param1Window) {
      SwingUtilities.updateComponentTreeUI(param1Window);
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
              MetalLookAndFeel.AATextListener.updateAllUIs();
              MetalLookAndFeel.AATextListener.setUpdatePending(false);
            }
          };
        SwingUtilities.invokeLater(runnable);
      } 
    }
  }
  
  private static class FontActiveValue implements UIDefaults.ActiveValue {
    private int type;
    
    private MetalTheme theme;
    
    FontActiveValue(MetalTheme param1MetalTheme, int param1Int) {
      this.theme = param1MetalTheme;
      this.type = param1Int;
    }
    
    public Object createValue(UIDefaults param1UIDefaults) {
      FontUIResource fontUIResource = null;
      switch (this.type) {
        case 0:
          fontUIResource = this.theme.getControlTextFont();
          break;
        case 1:
          fontUIResource = this.theme.getSystemTextFont();
          break;
        case 2:
          fontUIResource = this.theme.getUserTextFont();
          break;
        case 3:
          fontUIResource = this.theme.getMenuTextFont();
          break;
        case 4:
          fontUIResource = this.theme.getWindowTitleFont();
          break;
        case 5:
          fontUIResource = this.theme.getSubTextFont();
          break;
      } 
      return fontUIResource;
    }
  }
  
  private static class MetalLayoutStyle extends DefaultLayoutStyle {
    private static MetalLayoutStyle INSTANCE = new MetalLayoutStyle();
    
    public int getPreferredGap(JComponent param1JComponent1, JComponent param1JComponent2, LayoutStyle.ComponentPlacement param1ComponentPlacement, int param1Int, Container param1Container) {
      super.getPreferredGap(param1JComponent1, param1JComponent2, param1ComponentPlacement, param1Int, param1Container);
      byte b = 0;
      switch (MetalLookAndFeel.null.$SwitchMap$javax$swing$LayoutStyle$ComponentPlacement[param1ComponentPlacement.ordinal()]) {
        case 1:
          if (param1Int == 3 || param1Int == 7) {
            int i = getIndent(param1JComponent1, param1Int);
            return (i > 0) ? i : 12;
          } 
        case 2:
          if (param1JComponent1.getUIClassID() == "ToggleButtonUI" && param1JComponent2.getUIClassID() == "ToggleButtonUI") {
            ButtonModel buttonModel1 = ((JToggleButton)param1JComponent1).getModel();
            ButtonModel buttonModel2 = ((JToggleButton)param1JComponent2).getModel();
            return (buttonModel1 instanceof DefaultButtonModel && buttonModel2 instanceof DefaultButtonModel && ((DefaultButtonModel)buttonModel1).getGroup() == ((DefaultButtonModel)buttonModel2).getGroup() && ((DefaultButtonModel)buttonModel1).getGroup() != null) ? 2 : (MetalLookAndFeel.usingOcean() ? 6 : 5);
          } 
          b = 6;
          break;
        case 3:
          b = 12;
          break;
      } 
      return isLabelAndNonlabel(param1JComponent1, param1JComponent2, param1Int) ? getButtonGap(param1JComponent1, param1JComponent2, param1Int, b + 6) : getButtonGap(param1JComponent1, param1JComponent2, param1Int, b);
    }
    
    public int getContainerGap(JComponent param1JComponent, int param1Int, Container param1Container) {
      super.getContainerGap(param1JComponent, param1Int, param1Container);
      return getButtonGap(param1JComponent, param1Int, 12 - getButtonAdjustment(param1JComponent, param1Int));
    }
    
    protected int getButtonGap(JComponent param1JComponent1, JComponent param1JComponent2, int param1Int1, int param1Int2) {
      param1Int2 = super.getButtonGap(param1JComponent1, param1JComponent2, param1Int1, param1Int2);
      if (param1Int2 > 0) {
        int i = getButtonAdjustment(param1JComponent1, param1Int1);
        if (i == 0)
          i = getButtonAdjustment(param1JComponent2, flipDirection(param1Int1)); 
        param1Int2 -= i;
      } 
      return (param1Int2 < 0) ? 0 : param1Int2;
    }
    
    private int getButtonAdjustment(JComponent param1JComponent, int param1Int) {
      String str = param1JComponent.getUIClassID();
      if (str == "ButtonUI" || str == "ToggleButtonUI") {
        if (!MetalLookAndFeel.usingOcean() && (param1Int == 3 || param1Int == 5) && param1JComponent.getBorder() instanceof javax.swing.plaf.UIResource)
          return 1; 
      } else if (param1Int == 5 && (str == "RadioButtonUI" || str == "CheckBoxUI") && !MetalLookAndFeel.usingOcean()) {
        return 1;
      } 
      return 0;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalLookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */