package sun.swing.plaf;

import javax.swing.UIDefaults;

public class GTKKeybindings {
  public static void installKeybindings(UIDefaults paramUIDefaults) {
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
    Object[] arrayOfObject = { 
        "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released", "ENTER", "pressed", "released ENTER", "released" }), "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", 
            "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", 
            "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious" }), "EditorPane.focusInputMap", lazyInputMap3, "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up", "ENTER", "approveSelection", 
            "ctrl ENTER", "approveSelection" }), 
        "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", 
            "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", 
            "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", 
            "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", 
            "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", 
            "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", 
            "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", 
            "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", 
            "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "InternalFrame.windowBindings", { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" }, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
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
            "ctrl KP_RIGHT", "selectPreviousColumnChangeLead" }), "MenuBar.windowBindings", { "F10", "takeFocus" }, 
        "OptionPane.windowBindings", { "ESCAPE", "close" }, "PasswordField.focusInputMap", lazyInputMap2, "PopupMenu.selectedWindowInputMapBindings", { 
          "ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", 
          "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", 
          "SPACE", "return" }, "PopupMenu.selectedWindowInputMapBindings.RightToLeft", { "LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent" }, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released", "RETURN", "pressed" }), 
        "RootPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "shift F10", "postPopup", "CONTEXT_MENU", "postPopup" }), "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" }, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", 
            "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", 
            "HOME", "minScroll", "END", "maxScroll" }), "ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }), "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", 
            "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", 
            "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), 
        "ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft" }), "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", 
            "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", 
            "HOME", "minScroll", "END", "maxScroll" }), "Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }), "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", 
            "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", 
            "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), 
        "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", 
            "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent", 
            "SPACE", "selectTabWithFocus" }), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl TAB", "navigateNext", "ctrl shift TAB", "navigatePrevious", "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", 
            "ctrl KP_UP", "requestFocus" }), "TableHeader.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "SPACE", "toggleSortOrder", "LEFT", "selectColumnToLeft", "KP_LEFT", "selectColumnToLeft", "RIGHT", "selectColumnToRight", "KP_RIGHT", "selectColumnToRight", 
            "alt LEFT", "moveColumnLeft", "alt KP_LEFT", "moveColumnLeft", "alt RIGHT", "moveColumnRight", "alt KP_RIGHT", "moveColumnRight", "alt shift LEFT", "resizeLeft", 
            "alt shift KP_LEFT", "resizeLeft", "alt shift RIGHT", "resizeRight", "alt shift KP_RIGHT", "resizeRight", "ESCAPE", "focusTable" }), "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
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
            "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "shift RIGHT", "selectPreviousColumnChangeLead", "shift KP_RIGHT", "selectPreviousColumnChangeLead", "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", 
            "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", 
            "ctrl KP_LEFT", "selectNextColumnChangeLead", "ctrl PAGE_UP", "scrollRightChangeSelection", "ctrl PAGE_DOWN", "scrollLeftChangeSelection", "ctrl shift PAGE_UP", "scrollRightExtendSelection", "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection" }), 
        "TextArea.focusInputMap", lazyInputMap3, "TextField.focusInputMap", lazyInputMap1, "TextPane.focusInputMap", lazyInputMap3, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", 
            "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), 
        "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { 
            "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", 
            "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", 
            "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", 
            "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", 
            "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", 
            "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "typed +", "expand", 
            "typed -", "collapse", "BACK_SPACE", "moveSelectionToParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", 
            "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", 
            "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", 
            "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", 
            "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", 
            "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }) };
    paramUIDefaults.putDefaults(arrayOfObject);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\plaf\GTKKeybindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */