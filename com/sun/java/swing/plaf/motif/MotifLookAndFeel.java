package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import javax.swing.UIDefaults;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicLookAndFeel;
import sun.awt.OSInfo;
import sun.swing.SwingUtilities2;

public class MotifLookAndFeel extends BasicLookAndFeel {
  public String getName() { return "CDE/Motif"; }
  
  public String getID() { return "Motif"; }
  
  public String getDescription() { return "The CDE/Motif Look and Feel"; }
  
  public boolean isNativeLookAndFeel() { return (OSInfo.getOSType() == OSInfo.OSType.SOLARIS); }
  
  public boolean isSupportedLookAndFeel() { return true; }
  
  protected void initSystemColorDefaults(UIDefaults paramUIDefaults) {
    String[] arrayOfString = { 
        "desktop", "#005C5C", "activeCaption", "#000080", "activeCaptionText", "#FFFFFF", "activeCaptionBorder", "#B24D7A", "inactiveCaption", "#AEB2C3", 
        "inactiveCaptionText", "#000000", "inactiveCaptionBorder", "#AEB2C3", "window", "#AEB2C3", "windowBorder", "#AEB2C3", "windowText", "#000000", 
        "menu", "#AEB2C3", "menuText", "#000000", "text", "#FFF7E9", "textText", "#000000", "textHighlight", "#000000", 
        "textHighlightText", "#FFF7E9", "textInactiveText", "#808080", "control", "#AEB2C3", "controlText", "#000000", "controlHighlight", "#DCDEE5", 
        "controlLtHighlight", "#DCDEE5", "controlShadow", "#63656F", "controlLightShadow", "#9397A5", "controlDkShadow", "#000000", "scrollbar", "#AEB2C3", 
        "info", "#FFF7E9", "infoText", "#000000" };
    loadSystemColors(paramUIDefaults, arrayOfString, false);
  }
  
  protected void initClassDefaults(UIDefaults paramUIDefaults) {
    super.initClassDefaults(paramUIDefaults);
    String str = "com.sun.java.swing.plaf.motif.";
    Object[] arrayOfObject = { 
        "ButtonUI", str + "MotifButtonUI", "CheckBoxUI", str + "MotifCheckBoxUI", "DirectoryPaneUI", str + "MotifDirectoryPaneUI", "FileChooserUI", str + "MotifFileChooserUI", "LabelUI", str + "MotifLabelUI", 
        "MenuBarUI", str + "MotifMenuBarUI", "MenuUI", str + "MotifMenuUI", "MenuItemUI", str + "MotifMenuItemUI", "CheckBoxMenuItemUI", str + "MotifCheckBoxMenuItemUI", "RadioButtonMenuItemUI", str + "MotifRadioButtonMenuItemUI", 
        "RadioButtonUI", str + "MotifRadioButtonUI", "ToggleButtonUI", str + "MotifToggleButtonUI", "PopupMenuUI", str + "MotifPopupMenuUI", "ProgressBarUI", str + "MotifProgressBarUI", "ScrollBarUI", str + "MotifScrollBarUI", 
        "ScrollPaneUI", str + "MotifScrollPaneUI", "SliderUI", str + "MotifSliderUI", "SplitPaneUI", str + "MotifSplitPaneUI", "TabbedPaneUI", str + "MotifTabbedPaneUI", "TextAreaUI", str + "MotifTextAreaUI", 
        "TextFieldUI", str + "MotifTextFieldUI", "PasswordFieldUI", str + "MotifPasswordFieldUI", "TextPaneUI", str + "MotifTextPaneUI", "EditorPaneUI", str + "MotifEditorPaneUI", "TreeUI", str + "MotifTreeUI", 
        "InternalFrameUI", str + "MotifInternalFrameUI", "DesktopPaneUI", str + "MotifDesktopPaneUI", "SeparatorUI", str + "MotifSeparatorUI", "PopupMenuSeparatorUI", str + "MotifPopupMenuSeparatorUI", "OptionPaneUI", str + "MotifOptionPaneUI", 
        "ComboBoxUI", str + "MotifComboBoxUI", "DesktopIconUI", str + "MotifDesktopIconUI" };
    paramUIDefaults.putDefaults(arrayOfObject);
  }
  
  private void initResourceBundle(UIDefaults paramUIDefaults) { paramUIDefaults.addResourceBundle("com.sun.java.swing.plaf.motif.resources.motif"); }
  
  protected void initComponentDefaults(UIDefaults paramUIDefaults) {
    super.initComponentDefaults(paramUIDefaults);
    initResourceBundle(paramUIDefaults);
    FontUIResource fontUIResource1 = new FontUIResource("Dialog", 0, 12);
    FontUIResource fontUIResource2 = new FontUIResource("Serif", 0, 12);
    FontUIResource fontUIResource3 = new FontUIResource("SansSerif", 0, 12);
    FontUIResource fontUIResource4 = new FontUIResource("Monospaced", 0, 12);
    ColorUIResource colorUIResource1 = new ColorUIResource(Color.red);
    ColorUIResource colorUIResource2 = new ColorUIResource(Color.black);
    ColorUIResource colorUIResource3 = new ColorUIResource(Color.white);
    ColorUIResource colorUIResource4 = new ColorUIResource(Color.lightGray);
    ColorUIResource colorUIResource5 = new ColorUIResource(147, 151, 165);
    ColorUIResource colorUIResource6 = colorUIResource5;
    ColorUIResource colorUIResource7 = new ColorUIResource(165, 165, 165);
    ColorUIResource colorUIResource8 = new ColorUIResource(0, 0, 0);
    MotifBorders.BevelBorder bevelBorder1 = new MotifBorders.BevelBorder(false, paramUIDefaults.getColor("controlShadow"), paramUIDefaults.getColor("controlLtHighlight"));
    MotifBorders.BevelBorder bevelBorder2 = new MotifBorders.BevelBorder(true, paramUIDefaults.getColor("controlShadow"), paramUIDefaults.getColor("controlLtHighlight"));
    BasicBorders.MarginBorder marginBorder = new BasicBorders.MarginBorder();
    MotifBorders.FocusBorder focusBorder = new MotifBorders.FocusBorder(paramUIDefaults.getColor("control"), paramUIDefaults.getColor("activeCaptionBorder"));
    BorderUIResource.CompoundBorderUIResource compoundBorderUIResource1 = new BorderUIResource.CompoundBorderUIResource(focusBorder, bevelBorder1);
    BorderUIResource.CompoundBorderUIResource compoundBorderUIResource2 = new BorderUIResource.CompoundBorderUIResource(focusBorder, bevelBorder2);
    BorderUIResource.CompoundBorderUIResource compoundBorderUIResource3 = new BorderUIResource.CompoundBorderUIResource(new MotifBorders.ButtonBorder(paramUIDefaults.getColor("Button.shadow"), paramUIDefaults.getColor("Button.highlight"), paramUIDefaults.getColor("Button.darkShadow"), paramUIDefaults.getColor("activeCaptionBorder")), marginBorder);
    BorderUIResource.CompoundBorderUIResource compoundBorderUIResource4 = new BorderUIResource.CompoundBorderUIResource(new MotifBorders.ToggleButtonBorder(paramUIDefaults.getColor("ToggleButton.shadow"), paramUIDefaults.getColor("ToggleButton.highlight"), paramUIDefaults.getColor("ToggleButton.darkShadow"), paramUIDefaults.getColor("activeCaptionBorder")), marginBorder);
    BorderUIResource.CompoundBorderUIResource compoundBorderUIResource5 = new BorderUIResource.CompoundBorderUIResource(compoundBorderUIResource1, marginBorder);
    BorderUIResource.CompoundBorderUIResource compoundBorderUIResource6 = new BorderUIResource.CompoundBorderUIResource(bevelBorder2, new MotifBorders.MotifPopupMenuBorder(paramUIDefaults.getFont("PopupMenu.font"), paramUIDefaults.getColor("PopupMenu.background"), paramUIDefaults.getColor("PopupMenu.foreground"), paramUIDefaults.getColor("controlShadow"), paramUIDefaults.getColor("controlLtHighlight")));
    UIDefaults.LazyValue lazyValue1 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return MotifIconFactory.getMenuItemCheckIcon(); }
      };
    UIDefaults.LazyValue lazyValue2 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return MotifIconFactory.getMenuItemArrowIcon(); }
      };
    UIDefaults.LazyValue lazyValue3 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return MotifIconFactory.getMenuArrowIcon(); }
      };
    UIDefaults.LazyValue lazyValue4 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return MotifIconFactory.getCheckBoxIcon(); }
      };
    UIDefaults.LazyValue lazyValue5 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return MotifIconFactory.getRadioButtonIcon(); }
      };
    UIDefaults.LazyValue lazyValue6 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) {
          Color color = param1UIDefaults.getColor("control");
          return new ColorUIResource(Math.max((int)(color.getRed() * 0.85D), 0), Math.max((int)(color.getGreen() * 0.85D), 0), Math.max((int)(color.getBlue() * 0.85D), 0));
        }
      };
    UIDefaults.LazyValue lazyValue7 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) {
          Color color = param1UIDefaults.getColor("controlText");
          return new ColorUIResource(Math.max((int)(color.getRed() * 0.85D), 0), Math.max((int)(color.getGreen() * 0.85D), 0), Math.max((int)(color.getBlue() * 0.85D), 0));
        }
      };
    UIDefaults.LazyValue lazyValue8 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) {
          Color color1 = param1UIDefaults.getColor("control");
          Color color2 = new Color(Math.max((int)(color1.getRed() * 0.85D), 0), Math.max((int)(color1.getGreen() * 0.85D), 0), Math.max((int)(color1.getBlue() * 0.85D), 0));
          return new ColorUIResource(color2.darker());
        }
      };
    UIDefaults.LazyValue lazyValue9 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) {
          Color color1 = param1UIDefaults.getColor("control");
          Color color2 = new Color(Math.max((int)(color1.getRed() * 0.85D), 0), Math.max((int)(color1.getGreen() * 0.85D), 0), Math.max((int)(color1.getBlue() * 0.85D), 0));
          return new ColorUIResource(color2.brighter());
        }
      };
    UIDefaults.LazyInputMap lazyInputMap1 = new UIDefaults.LazyInputMap(new Object[] { 
          "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", 
          "shift DELETE", "cut-to-clipboard", "control F", "caret-forward", "control B", "caret-backward", "control D", "delete-next", "BACK_SPACE", "delete-previous", 
          "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", 
          "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "shift LEFT", "selection-backward", 
          "shift RIGHT", "selection-forward", "control LEFT", "caret-previous-word", "control RIGHT", "caret-next-word", "control shift LEFT", "selection-previous-word", "control shift RIGHT", "selection-next-word", 
          "control SLASH", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", 
          "control BACK_SLASH", "unselect", "ENTER", "notify-field-accept", "control shift O", "toggle-componentOrientation" });
    UIDefaults.LazyInputMap lazyInputMap2 = new UIDefaults.LazyInputMap(new Object[] { 
          "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", 
          "shift DELETE", "cut-to-clipboard", "control F", "caret-forward", "control B", "caret-backward", "control D", "delete-next", "BACK_SPACE", "delete-previous", 
          "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "RIGHT", "caret-forward", "LEFT", "caret-backward", 
          "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-begin-line", 
          "control RIGHT", "caret-end-line", "control shift LEFT", "selection-begin-line", "control shift RIGHT", "selection-end-line", "control SLASH", "select-all", "HOME", "caret-begin-line", 
          "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "control BACK_SLASH", "unselect", "ENTER", "notify-field-accept", 
          "control shift O", "toggle-componentOrientation" });
    UIDefaults.LazyInputMap lazyInputMap3 = new UIDefaults.LazyInputMap(new Object[] { 
          "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", 
          "shift DELETE", "cut-to-clipboard", "control F", "caret-forward", "control B", "caret-backward", "control D", "delete-next", "BACK_SPACE", "delete-previous", 
          "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", 
          "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "shift LEFT", "selection-backward", 
          "shift RIGHT", "selection-forward", "control LEFT", "caret-previous-word", "control RIGHT", "caret-next-word", "control shift LEFT", "selection-previous-word", "control shift RIGHT", "selection-next-word", 
          "control SLASH", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", 
          "control N", "caret-down", "control P", "caret-up", "UP", "caret-up", "DOWN", "caret-down", "PAGE_UP", "page-up", 
          "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", 
          "shift UP", "selection-up", "shift DOWN", "selection-down", "ENTER", "insert-break", "TAB", "insert-tab", "control BACK_SLASH", "unselect", 
          "control HOME", "caret-begin", "control END", "caret-end", "control shift HOME", "selection-begin", "control shift END", "selection-end", "control T", "next-link-action", 
          "control shift T", "previous-link-action", "control SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation" });
    Object object1 = SwingUtilities2.makeIcon(getClass(), MotifLookAndFeel.class, "icons/TreeOpen.gif");
    Object object2 = SwingUtilities2.makeIcon(getClass(), MotifLookAndFeel.class, "icons/TreeClosed.gif");
    UIDefaults.LazyValue lazyValue10 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return MotifTreeCellRenderer.loadLeafIcon(); }
      };
    UIDefaults.LazyValue lazyValue11 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return MotifTreeUI.MotifExpandedIcon.createExpandedIcon(); }
      };
    UIDefaults.LazyValue lazyValue12 = new UIDefaults.LazyValue() {
        public Object createValue(UIDefaults param1UIDefaults) { return MotifTreeUI.MotifCollapsedIcon.createCollapsedIcon(); }
      };
    MotifBorders.MenuBarBorder menuBarBorder = new MotifBorders.MenuBarBorder(paramUIDefaults.getColor("MenuBar.shadow"), paramUIDefaults.getColor("MenuBar.highlight"), paramUIDefaults.getColor("MenuBar.darkShadow"), paramUIDefaults.getColor("activeCaptionBorder"));
    BorderUIResource.CompoundBorderUIResource compoundBorderUIResource7 = new BorderUIResource.CompoundBorderUIResource(bevelBorder1, marginBorder);
    BorderUIResource.LineBorderUIResource lineBorderUIResource = new BorderUIResource.LineBorderUIResource(paramUIDefaults.getColor("activeCaptionBorder"));
    InsetsUIResource insetsUIResource1 = new InsetsUIResource(0, 0, 0, 0);
    InsetsUIResource insetsUIResource2 = new InsetsUIResource(3, 4, 3, 4);
    InsetsUIResource insetsUIResource3 = new InsetsUIResource(3, 0, 1, 0);
    InsetsUIResource insetsUIResource4 = new InsetsUIResource(4, 2, 0, 8);
    InsetsUIResource insetsUIResource5 = new InsetsUIResource(2, 2, 2, 2);
    BorderUIResource.EmptyBorderUIResource emptyBorderUIResource1 = new BorderUIResource.EmptyBorderUIResource(10, 0, 0, 0);
    BorderUIResource.EmptyBorderUIResource emptyBorderUIResource2 = new BorderUIResource.EmptyBorderUIResource(10, 10, 10, 10);
    BorderUIResource.EmptyBorderUIResource emptyBorderUIResource3 = new BorderUIResource.EmptyBorderUIResource(10, 10, 12, 10);
    Object[] arrayOfObject = { 
        "Desktop.background", paramUIDefaults.get("desktop"), "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", 
            "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", 
            "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", 
            "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", 
            "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", 
            "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }), "Panel.background", paramUIDefaults.get("control"), "Panel.foreground", paramUIDefaults.get("textText"), "Panel.font", fontUIResource1, 
        "ProgressBar.font", fontUIResource1, "ProgressBar.foreground", colorUIResource5, "ProgressBar.background", paramUIDefaults.get("control"), "ProgressBar.selectionForeground", paramUIDefaults.get("control"), "ProgressBar.selectionBackground", paramUIDefaults.get("controlText"), 
        "ProgressBar.border", bevelBorder1, "ProgressBar.cellLength", new Integer(6), "ProgressBar.cellSpacing", Integer.valueOf(0), "Button.margin", new InsetsUIResource(2, 4, 2, 4), "Button.border", compoundBorderUIResource3, 
        "Button.background", paramUIDefaults.get("control"), "Button.foreground", paramUIDefaults.get("controlText"), "Button.select", paramUIDefaults.get("controlLightShadow"), "Button.font", fontUIResource1, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), 
        "CheckBox.textIconGap", new Integer(8), "CheckBox.margin", new InsetsUIResource(4, 2, 4, 2), "CheckBox.icon", lazyValue4, "CheckBox.focus", paramUIDefaults.get("activeCaptionBorder"), "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), 
        "RadioButton.margin", new InsetsUIResource(4, 2, 4, 2), "RadioButton.textIconGap", new Integer(8), "RadioButton.background", paramUIDefaults.get("control"), "RadioButton.foreground", paramUIDefaults.get("controlText"), "RadioButton.icon", lazyValue5, 
        "RadioButton.focus", paramUIDefaults.get("activeCaptionBorder"), "RadioButton.icon", lazyValue5, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "ToggleButton.border", compoundBorderUIResource4, "ToggleButton.background", paramUIDefaults.get("control"), 
        "ToggleButton.foreground", paramUIDefaults.get("controlText"), "ToggleButton.focus", paramUIDefaults.get("controlText"), "ToggleButton.select", paramUIDefaults.get("controlLightShadow"), "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "Menu.border", compoundBorderUIResource7, 
        "Menu.font", fontUIResource1, "Menu.acceleratorFont", fontUIResource1, "Menu.acceleratorSelectionForeground", colorUIResource8, "Menu.foreground", paramUIDefaults.get("menuText"), "Menu.background", paramUIDefaults.get("menu"), 
        "Menu.selectionForeground", colorUIResource8, "Menu.selectionBackground", colorUIResource7, "Menu.checkIcon", lazyValue1, "Menu.arrowIcon", lazyValue3, "Menu.menuPopupOffsetX", new Integer(0), 
        "Menu.menuPopupOffsetY", new Integer(0), "Menu.submenuPopupOffsetX", new Integer(-2), "Menu.submenuPopupOffsetY", new Integer(3), "Menu.shortcutKeys", { SwingUtilities2.getSystemMnemonicKeyMask(), 4 }, "Menu.cancelMode", "hideMenuTree", 
        "MenuBar.border", menuBarBorder, "MenuBar.background", paramUIDefaults.get("menu"), "MenuBar.foreground", paramUIDefaults.get("menuText"), "MenuBar.font", fontUIResource1, "MenuBar.windowBindings", { "F10", "takeFocus" }, 
        "MenuItem.border", compoundBorderUIResource7, "MenuItem.font", fontUIResource1, "MenuItem.acceleratorFont", fontUIResource1, "MenuItem.acceleratorSelectionForeground", colorUIResource8, "MenuItem.foreground", paramUIDefaults.get("menuText"), 
        "MenuItem.background", paramUIDefaults.get("menu"), "MenuItem.selectionForeground", colorUIResource8, "MenuItem.selectionBackground", colorUIResource7, "MenuItem.checkIcon", lazyValue1, "MenuItem.arrowIcon", lazyValue2, 
        "RadioButtonMenuItem.border", compoundBorderUIResource7, "RadioButtonMenuItem.font", fontUIResource1, "RadioButtonMenuItem.acceleratorFont", fontUIResource1, "RadioButtonMenuItem.acceleratorSelectionForeground", colorUIResource8, "RadioButtonMenuItem.foreground", paramUIDefaults.get("menuText"), 
        "RadioButtonMenuItem.background", paramUIDefaults.get("menu"), "RadioButtonMenuItem.selectionForeground", colorUIResource8, "RadioButtonMenuItem.selectionBackground", colorUIResource7, "RadioButtonMenuItem.checkIcon", lazyValue5, "RadioButtonMenuItem.arrowIcon", lazyValue2, 
        "CheckBoxMenuItem.border", compoundBorderUIResource7, "CheckBoxMenuItem.font", fontUIResource1, "CheckBoxMenuItem.acceleratorFont", fontUIResource1, "CheckBoxMenuItem.acceleratorSelectionForeground", colorUIResource8, "CheckBoxMenuItem.foreground", paramUIDefaults.get("menuText"), 
        "CheckBoxMenuItem.background", paramUIDefaults.get("menu"), "CheckBoxMenuItem.selectionForeground", colorUIResource8, "CheckBoxMenuItem.selectionBackground", colorUIResource7, "CheckBoxMenuItem.checkIcon", lazyValue4, "CheckBoxMenuItem.arrowIcon", lazyValue2, 
        "PopupMenu.background", paramUIDefaults.get("menu"), "PopupMenu.border", compoundBorderUIResource6, "PopupMenu.foreground", paramUIDefaults.get("menuText"), "PopupMenu.font", fontUIResource1, "PopupMenu.consumeEventOnClose", Boolean.TRUE, 
        "Label.font", fontUIResource1, "Label.background", paramUIDefaults.get("control"), "Label.foreground", paramUIDefaults.get("controlText"), "Separator.shadow", paramUIDefaults.get("controlShadow"), "Separator.highlight", paramUIDefaults.get("controlLtHighlight"), 
        "Separator.background", paramUIDefaults.get("controlLtHighlight"), "Separator.foreground", paramUIDefaults.get("controlShadow"), "List.focusCellHighlightBorder", lineBorderUIResource, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", 
            "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", 
            "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", 
            "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", 
            "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", 
            "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", 
            "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", 
            "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", 
            "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", 
            "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", 
            "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", 
            "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", 
            "ctrl shift SPACE", "moveSelectionTo" }), "DesktopIcon.icon", SwingUtilities2.makeIcon(getClass(), MotifLookAndFeel.class, "icons/DesktopIcon.gif"), 
        "DesktopIcon.border", null, "DesktopIcon.windowBindings", { "ESCAPE", "hideSystemMenu" }, "InternalFrame.activeTitleBackground", paramUIDefaults.get("activeCaptionBorder"), "InternalFrame.inactiveTitleBackground", paramUIDefaults.get("inactiveCaptionBorder"), "InternalFrame.windowBindings", { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" }, 
        "ScrollBar.background", colorUIResource6, "ScrollBar.foreground", paramUIDefaults.get("control"), "ScrollBar.track", colorUIResource6, "ScrollBar.trackHighlight", paramUIDefaults.get("controlDkShadow"), "ScrollBar.thumb", paramUIDefaults.get("control"), 
        "ScrollBar.thumbHighlight", paramUIDefaults.get("controlHighlight"), "ScrollBar.thumbDarkShadow", paramUIDefaults.get("controlDkShadow"), "ScrollBar.thumbShadow", paramUIDefaults.get("controlShadow"), "ScrollBar.border", bevelBorder1, "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE, 
        "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", 
            "ctrl PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", 
            "PAGE_UP", "negativeBlockIncrement", "ctrl PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "ScrollPane.font", fontUIResource1, "ScrollPane.background", paramUIDefaults.get("control"), "ScrollPane.foreground", paramUIDefaults.get("controlText"), "ScrollPane.border", null, 
        "ScrollPane.viewportBorder", bevelBorder1, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", 
            "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", 
            "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), "Slider.font", fontUIResource1, "Slider.border", compoundBorderUIResource1, "Slider.foreground", paramUIDefaults.get("control"), 
        "Slider.background", colorUIResource5, "Slider.highlight", paramUIDefaults.get("controlHighlight"), "Slider.shadow", paramUIDefaults.get("controlShadow"), "Slider.focus", paramUIDefaults.get("controlDkShadow"), "Slider.focusInsets", insetsUIResource1, 
        "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "ctrl PAGE_DOWN", "negativeBlockIncrement", 
            "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "ctrl PAGE_UP", "positiveBlockIncrement", 
            "HOME", "minScroll", "END", "maxScroll" }), "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "Spinner.border", compoundBorderUIResource5, "SplitPane.background", paramUIDefaults.get("control"), "SplitPane.highlight", paramUIDefaults.get("controlHighlight"), 
        "SplitPane.shadow", paramUIDefaults.get("controlShadow"), "SplitPane.dividerSize", Integer.valueOf(20), "SplitPane.activeThumb", paramUIDefaults.get("activeCaptionBorder"), "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", 
            "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", 
            "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), "TabbedPane.font", fontUIResource1, 
        "TabbedPane.background", paramUIDefaults.get("control"), "TabbedPane.foreground", paramUIDefaults.get("controlText"), "TabbedPane.light", paramUIDefaults.get("controlHighlight"), "TabbedPane.highlight", paramUIDefaults.get("controlLtHighlight"), "TabbedPane.shadow", paramUIDefaults.get("controlShadow"), 
        "TabbedPane.darkShadow", paramUIDefaults.get("controlShadow"), "TabbedPane.unselectedTabBackground", lazyValue6, "TabbedPane.unselectedTabForeground", lazyValue7, "TabbedPane.unselectedTabHighlight", lazyValue9, "TabbedPane.unselectedTabShadow", lazyValue8, 
        "TabbedPane.focus", paramUIDefaults.get("activeCaptionBorder"), "TabbedPane.tabInsets", insetsUIResource2, "TabbedPane.selectedTabPadInsets", insetsUIResource3, "TabbedPane.tabAreaInsets", insetsUIResource4, "TabbedPane.contentBorderInsets", insetsUIResource5, 
        "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", 
            "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }), "Tree.background", colorUIResource5, "Tree.hash", paramUIDefaults.get("controlDkShadow"), "Tree.iconShadow", paramUIDefaults.get("controlShadow"), 
        "Tree.iconHighlight", paramUIDefaults.get("controlHighlight"), "Tree.iconBackground", paramUIDefaults.get("control"), "Tree.iconForeground", paramUIDefaults.get("controlShadow"), "Tree.textBackground", colorUIResource5, "Tree.textForeground", paramUIDefaults.get("textText"), 
        "Tree.selectionBackground", paramUIDefaults.get("text"), "Tree.selectionForeground", paramUIDefaults.get("textText"), "Tree.selectionBorderColor", paramUIDefaults.get("activeCaptionBorder"), "Tree.openIcon", object1, "Tree.closedIcon", object2, 
        "Tree.leafIcon", lazyValue10, "Tree.expandedIcon", lazyValue11, "Tree.collapsedIcon", lazyValue12, "Tree.editorBorder", focusBorder, "Tree.editorBorderSelectionColor", paramUIDefaults.get("activeCaptionBorder"), 
        "Tree.rowHeight", new Integer(18), "Tree.drawsFocusBorderAroundIcon", Boolean.TRUE, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
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
            "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }), "Table.focusCellHighlightBorder", lineBorderUIResource, 
        "Table.scrollPaneBorder", null, "Table.dropLineShortColor", paramUIDefaults.get("activeCaptionBorder"), "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", 
            "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", 
            "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", 
            "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", 
            "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", 
            "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", 
            "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", 
            "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", 
            "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", 
            "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", 
            "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", 
            "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", 
            "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", 
            "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader" }), "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
            "CUT", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", 
            "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", 
            "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", 
            "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", 
            "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", 
            "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", 
            "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", 
            "KP_DOWN", "decrement" }), "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", 
            "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), 
        "ComboBox.control", paramUIDefaults.get("control"), "ComboBox.controlForeground", colorUIResource2, "ComboBox.background", paramUIDefaults.get("window"), "ComboBox.foreground", colorUIResource2, "ComboBox.border", compoundBorderUIResource2, 
        "ComboBox.selectionBackground", colorUIResource2, "ComboBox.selectionForeground", paramUIDefaults.get("text"), "ComboBox.disabledBackground", paramUIDefaults.get("control"), "ComboBox.disabledForeground", paramUIDefaults.get("textInactiveText"), "ComboBox.font", fontUIResource1, 
        "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", 
            "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", "SPACE", "spacePopup", 
            "ENTER", "enterPressed" }), "TextField.caretForeground", colorUIResource2, "TextField.caretBlinkRate", Integer.valueOf(500), "TextField.inactiveForeground", paramUIDefaults.get("textInactiveText"), "TextField.selectionBackground", paramUIDefaults.get("textHighlight"), 
        "TextField.selectionForeground", paramUIDefaults.get("textHighlightText"), "TextField.background", paramUIDefaults.get("window"), "TextField.foreground", paramUIDefaults.get("textText"), "TextField.font", fontUIResource3, "TextField.border", compoundBorderUIResource5, 
        "TextField.focusInputMap", lazyInputMap1, "PasswordField.caretForeground", colorUIResource2, "PasswordField.caretBlinkRate", Integer.valueOf(500), "PasswordField.inactiveForeground", paramUIDefaults.get("textInactiveText"), "PasswordField.selectionBackground", paramUIDefaults.get("textHighlight"), 
        "PasswordField.selectionForeground", paramUIDefaults.get("textHighlightText"), "PasswordField.background", paramUIDefaults.get("window"), "PasswordField.foreground", paramUIDefaults.get("textText"), "PasswordField.font", fontUIResource4, "PasswordField.border", compoundBorderUIResource5, 
        "PasswordField.focusInputMap", lazyInputMap2, "TextArea.caretForeground", colorUIResource2, "TextArea.caretBlinkRate", Integer.valueOf(500), "TextArea.inactiveForeground", paramUIDefaults.get("textInactiveText"), "TextArea.selectionBackground", paramUIDefaults.get("textHighlight"), 
        "TextArea.selectionForeground", paramUIDefaults.get("textHighlightText"), "TextArea.background", paramUIDefaults.get("window"), "TextArea.foreground", paramUIDefaults.get("textText"), "TextArea.font", fontUIResource4, "TextArea.border", marginBorder, 
        "TextArea.focusInputMap", lazyInputMap3, "TextPane.caretForeground", colorUIResource2, "TextPane.caretBlinkRate", Integer.valueOf(500), "TextPane.inactiveForeground", paramUIDefaults.get("textInactiveText"), "TextPane.selectionBackground", colorUIResource4, 
        "TextPane.selectionForeground", paramUIDefaults.get("textHighlightText"), "TextPane.background", colorUIResource3, "TextPane.foreground", paramUIDefaults.get("textText"), "TextPane.font", fontUIResource2, "TextPane.border", marginBorder, 
        "TextPane.focusInputMap", lazyInputMap3, "EditorPane.caretForeground", colorUIResource1, "EditorPane.caretBlinkRate", Integer.valueOf(500), "EditorPane.inactiveForeground", paramUIDefaults.get("textInactiveText"), "EditorPane.selectionBackground", colorUIResource4, 
        "EditorPane.selectionForeground", paramUIDefaults.get("textHighlightText"), "EditorPane.background", colorUIResource3, "EditorPane.foreground", paramUIDefaults.get("textText"), "EditorPane.font", fontUIResource2, "EditorPane.border", marginBorder, 
        "EditorPane.focusInputMap", lazyInputMap3, "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection" }), "ToolTip.border", bevelBorder2, "ToolTip.background", paramUIDefaults.get("info"), "ToolTip.foreground", paramUIDefaults.get("infoText"), 
        "PopupMenu.selectedWindowInputMapBindings", { 
          "ESCAPE", "cancel", "TAB", "cancel", "shift TAB", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", 
          "UP", "selectPrevious", "KP_UP", "selectPrevious", "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", 
          "KP_RIGHT", "selectChild", "ENTER", "return", "SPACE", "return" }, "OptionPane.border", emptyBorderUIResource1, "OptionPane.messageAreaBorder", emptyBorderUIResource3, "OptionPane.buttonAreaBorder", emptyBorderUIResource2, "OptionPane.errorIcon", SwingUtilities2.makeIcon(getClass(), MotifLookAndFeel.class, "icons/Error.gif"), 
        "OptionPane.informationIcon", SwingUtilities2.makeIcon(getClass(), MotifLookAndFeel.class, "icons/Inform.gif"), "OptionPane.warningIcon", SwingUtilities2.makeIcon(getClass(), MotifLookAndFeel.class, "icons/Warn.gif"), "OptionPane.questionIcon", SwingUtilities2.makeIcon(getClass(), MotifLookAndFeel.class, "icons/Question.gif"), "OptionPane.windowBindings", { "ESCAPE", "close" }, "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" } };
    paramUIDefaults.putDefaults(arrayOfObject);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifLookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */