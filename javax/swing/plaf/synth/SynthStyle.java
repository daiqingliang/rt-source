package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

public abstract class SynthStyle {
  private static Map<Object, Object> DEFAULT_VALUES;
  
  private static final SynthGraphicsUtils SYNTH_GRAPHICS = new SynthGraphicsUtils();
  
  private static void populateDefaultValues() {
    UIDefaults.LazyInputMap lazyInputMap1 = new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" });
    DEFAULT_VALUES.put("Button.focusInputMap", lazyInputMap1);
    DEFAULT_VALUES.put("CheckBox.focusInputMap", lazyInputMap1);
    DEFAULT_VALUES.put("RadioButton.focusInputMap", lazyInputMap1);
    DEFAULT_VALUES.put("ToggleButton.focusInputMap", lazyInputMap1);
    DEFAULT_VALUES.put("SynthArrowButton.focusInputMap", lazyInputMap1);
    DEFAULT_VALUES.put("List.dropLineColor", Color.BLACK);
    DEFAULT_VALUES.put("Tree.dropLineColor", Color.BLACK);
    DEFAULT_VALUES.put("Table.dropLineColor", Color.BLACK);
    DEFAULT_VALUES.put("Table.dropLineShortColor", Color.RED);
    UIDefaults.LazyInputMap lazyInputMap2 = new UIDefaults.LazyInputMap(new Object[] { 
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
    DEFAULT_VALUES.put("EditorPane.focusInputMap", lazyInputMap2);
    DEFAULT_VALUES.put("TextArea.focusInputMap", lazyInputMap2);
    DEFAULT_VALUES.put("TextPane.focusInputMap", lazyInputMap2);
    UIDefaults.LazyInputMap lazyInputMap3 = new UIDefaults.LazyInputMap(new Object[] { 
          "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
          "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", 
          "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", 
          "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", 
          "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", 
          "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", 
          "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", 
          "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation" });
    DEFAULT_VALUES.put("TextField.focusInputMap", lazyInputMap3);
    DEFAULT_VALUES.put("PasswordField.focusInputMap", lazyInputMap3);
    DEFAULT_VALUES.put("ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", 
            "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", 
            "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious" }));
    DEFAULT_VALUES.put("Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", 
            "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", 
            "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", 
            "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", 
            "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", 
            "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }));
    DEFAULT_VALUES.put("FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up", "ENTER", "approveSelection", 
            "ctrl ENTER", "approveSelection" }));
    DEFAULT_VALUES.put("FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
            "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", 
            "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", 
            "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", 
            "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", 
            "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", 
            "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", 
            "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", 
            "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }));
    DEFAULT_VALUES.put("InternalFrame.icon", LookAndFeel.makeIcon(javax.swing.plaf.basic.BasicLookAndFeel.class, "icons/JavaCup16.png"));
    DEFAULT_VALUES.put("InternalFrame.windowBindings", new Object[] { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" });
    DEFAULT_VALUES.put("List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
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
            "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }));
    DEFAULT_VALUES.put("List.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { 
            "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", 
            "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", 
            "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", 
            "ctrl KP_RIGHT", "selectPreviousColumnChangeLead" }));
    DEFAULT_VALUES.put("MenuBar.windowBindings", new Object[] { "F10", "takeFocus" });
    DEFAULT_VALUES.put("OptionPane.windowBindings", new Object[] { "ESCAPE", "close" });
    DEFAULT_VALUES.put("RootPane.defaultButtonWindowKeyBindings", new Object[] { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" });
    DEFAULT_VALUES.put("RootPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "shift F10", "postPopup" }));
    DEFAULT_VALUES.put("ScrollBar.anecstorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", 
            "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", 
            "HOME", "minScroll", "END", "maxScroll" }));
    DEFAULT_VALUES.put("ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }));
    DEFAULT_VALUES.put("ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", 
            "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", 
            "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }));
    DEFAULT_VALUES.put("ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft" }));
    DEFAULT_VALUES.put("SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", 
            "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", 
            "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }));
    DEFAULT_VALUES.put("Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }));
    DEFAULT_VALUES.put("Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", 
            "ctrl PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", 
            "PAGE_UP", "positiveBlockIncrement", "ctrl PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }));
    DEFAULT_VALUES.put("Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }));
    DEFAULT_VALUES.put("TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }));
    DEFAULT_VALUES.put("TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", 
            "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }));
    DEFAULT_VALUES.put("Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
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
            "F8", "focusHeader" }));
    DEFAULT_VALUES.put("TableHeader.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "SPACE", "toggleSortOrder", "LEFT", "selectColumnToLeft", "KP_LEFT", "selectColumnToLeft", "RIGHT", "selectColumnToRight", "KP_RIGHT", "selectColumnToRight", 
            "alt LEFT", "moveColumnLeft", "alt KP_LEFT", "moveColumnLeft", "alt RIGHT", "moveColumnRight", "alt KP_RIGHT", "moveColumnRight", "alt shift LEFT", "resizeLeft", 
            "alt shift KP_LEFT", "resizeLeft", "alt shift RIGHT", "resizeRight", "alt shift KP_RIGHT", "resizeRight", "ESCAPE", "focusTable" }));
    DEFAULT_VALUES.put("Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }));
    DEFAULT_VALUES.put("Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
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
            "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }));
    DEFAULT_VALUES.put("Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild" }));
  }
  
  private static Object getDefaultValue(Object paramObject) {
    synchronized (SynthStyle.class) {
      if (DEFAULT_VALUES == null) {
        DEFAULT_VALUES = new HashMap();
        populateDefaultValues();
      } 
      Object object = DEFAULT_VALUES.get(paramObject);
      if (object instanceof UIDefaults.LazyValue) {
        object = ((UIDefaults.LazyValue)object).createValue(null);
        DEFAULT_VALUES.put(paramObject, object);
      } 
      return object;
    } 
  }
  
  public SynthGraphicsUtils getGraphicsUtils(SynthContext paramSynthContext) { return SYNTH_GRAPHICS; }
  
  public Color getColor(SynthContext paramSynthContext, ColorType paramColorType) {
    JComponent jComponent = paramSynthContext.getComponent();
    Region region = paramSynthContext.getRegion();
    if ((paramSynthContext.getComponentState() & 0x8) != 0)
      if (jComponent instanceof JTextComponent) {
        JTextComponent jTextComponent = (JTextComponent)jComponent;
        Color color1 = jTextComponent.getDisabledTextColor();
        if (color1 == null || color1 instanceof javax.swing.plaf.UIResource)
          return getColorForState(paramSynthContext, paramColorType); 
      } else if (jComponent instanceof javax.swing.JLabel && (paramColorType == ColorType.FOREGROUND || paramColorType == ColorType.TEXT_FOREGROUND)) {
        return getColorForState(paramSynthContext, paramColorType);
      }  
    Color color = null;
    if (!region.isSubregion())
      if (paramColorType == ColorType.BACKGROUND) {
        color = jComponent.getBackground();
      } else if (paramColorType == ColorType.FOREGROUND) {
        color = jComponent.getForeground();
      } else if (paramColorType == ColorType.TEXT_FOREGROUND) {
        color = jComponent.getForeground();
      }  
    if (color == null || color instanceof javax.swing.plaf.UIResource)
      color = getColorForState(paramSynthContext, paramColorType); 
    if (color == null) {
      if (paramColorType == ColorType.BACKGROUND || paramColorType == ColorType.TEXT_BACKGROUND)
        return jComponent.getBackground(); 
      if (paramColorType == ColorType.FOREGROUND || paramColorType == ColorType.TEXT_FOREGROUND)
        return jComponent.getForeground(); 
    } 
    return color;
  }
  
  protected abstract Color getColorForState(SynthContext paramSynthContext, ColorType paramColorType);
  
  public Font getFont(SynthContext paramSynthContext) {
    JComponent jComponent = paramSynthContext.getComponent();
    if (paramSynthContext.getComponentState() == 1)
      return jComponent.getFont(); 
    Font font = jComponent.getFont();
    return (font != null && !(font instanceof javax.swing.plaf.UIResource)) ? font : getFontForState(paramSynthContext);
  }
  
  protected abstract Font getFontForState(SynthContext paramSynthContext);
  
  public Insets getInsets(SynthContext paramSynthContext, Insets paramInsets) {
    if (paramInsets == null)
      paramInsets = new Insets(0, 0, 0, 0); 
    paramInsets.top = paramInsets.bottom = paramInsets.left = paramInsets.right = 0;
    return paramInsets;
  }
  
  public SynthPainter getPainter(SynthContext paramSynthContext) { return null; }
  
  public boolean isOpaque(SynthContext paramSynthContext) { return true; }
  
  public Object get(SynthContext paramSynthContext, Object paramObject) { return getDefaultValue(paramObject); }
  
  void installDefaults(SynthContext paramSynthContext, SynthUI paramSynthUI) {
    if (!paramSynthContext.isSubregion()) {
      JComponent jComponent = paramSynthContext.getComponent();
      Border border = jComponent.getBorder();
      if (border == null || border instanceof javax.swing.plaf.UIResource)
        jComponent.setBorder(new SynthBorder(paramSynthUI, getInsets(paramSynthContext, null))); 
    } 
    installDefaults(paramSynthContext);
  }
  
  public void installDefaults(SynthContext paramSynthContext) {
    if (!paramSynthContext.isSubregion()) {
      JComponent jComponent = paramSynthContext.getComponent();
      Region region = paramSynthContext.getRegion();
      Font font = jComponent.getFont();
      if (font == null || font instanceof javax.swing.plaf.UIResource)
        jComponent.setFont(getFontForState(paramSynthContext)); 
      Color color1 = jComponent.getBackground();
      if (color1 == null || color1 instanceof javax.swing.plaf.UIResource)
        jComponent.setBackground(getColorForState(paramSynthContext, ColorType.BACKGROUND)); 
      Color color2 = jComponent.getForeground();
      if (color2 == null || color2 instanceof javax.swing.plaf.UIResource)
        jComponent.setForeground(getColorForState(paramSynthContext, ColorType.FOREGROUND)); 
      LookAndFeel.installProperty(jComponent, "opaque", Boolean.valueOf(isOpaque(paramSynthContext)));
    } 
  }
  
  public void uninstallDefaults(SynthContext paramSynthContext) {
    if (!paramSynthContext.isSubregion()) {
      JComponent jComponent = paramSynthContext.getComponent();
      Border border = jComponent.getBorder();
      if (border instanceof javax.swing.plaf.UIResource)
        jComponent.setBorder(null); 
    } 
  }
  
  public int getInt(SynthContext paramSynthContext, Object paramObject, int paramInt) {
    Object object = get(paramSynthContext, paramObject);
    return (object instanceof Number) ? ((Number)object).intValue() : paramInt;
  }
  
  public boolean getBoolean(SynthContext paramSynthContext, Object paramObject, boolean paramBoolean) {
    Object object = get(paramSynthContext, paramObject);
    return (object instanceof Boolean) ? ((Boolean)object).booleanValue() : paramBoolean;
  }
  
  public Icon getIcon(SynthContext paramSynthContext, Object paramObject) {
    Object object = get(paramSynthContext, paramObject);
    return (object instanceof Icon) ? (Icon)object : null;
  }
  
  public String getString(SynthContext paramSynthContext, Object paramObject, String paramString) {
    Object object = get(paramSynthContext, paramObject);
    return (object instanceof String) ? (String)object : paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */