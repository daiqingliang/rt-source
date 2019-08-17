package javax.swing.plaf.multi;

import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

public class MultiLookAndFeel extends LookAndFeel {
  public String getName() { return "Multiplexing Look and Feel"; }
  
  public String getID() { return "Multiplex"; }
  
  public String getDescription() { return "Allows multiple UI instances per component instance"; }
  
  public boolean isNativeLookAndFeel() { return false; }
  
  public boolean isSupportedLookAndFeel() { return true; }
  
  public UIDefaults getDefaults() {
    String str = "javax.swing.plaf.multi.Multi";
    Object[] arrayOfObject = { 
        "ButtonUI", str + "ButtonUI", "CheckBoxMenuItemUI", str + "MenuItemUI", "CheckBoxUI", str + "ButtonUI", "ColorChooserUI", str + "ColorChooserUI", "ComboBoxUI", str + "ComboBoxUI", 
        "DesktopIconUI", str + "DesktopIconUI", "DesktopPaneUI", str + "DesktopPaneUI", "EditorPaneUI", str + "TextUI", "FileChooserUI", str + "FileChooserUI", "FormattedTextFieldUI", str + "TextUI", 
        "InternalFrameUI", str + "InternalFrameUI", "LabelUI", str + "LabelUI", "ListUI", str + "ListUI", "MenuBarUI", str + "MenuBarUI", "MenuItemUI", str + "MenuItemUI", 
        "MenuUI", str + "MenuItemUI", "OptionPaneUI", str + "OptionPaneUI", "PanelUI", str + "PanelUI", "PasswordFieldUI", str + "TextUI", "PopupMenuSeparatorUI", str + "SeparatorUI", 
        "PopupMenuUI", str + "PopupMenuUI", "ProgressBarUI", str + "ProgressBarUI", "RadioButtonMenuItemUI", str + "MenuItemUI", "RadioButtonUI", str + "ButtonUI", "RootPaneUI", str + "RootPaneUI", 
        "ScrollBarUI", str + "ScrollBarUI", "ScrollPaneUI", str + "ScrollPaneUI", "SeparatorUI", str + "SeparatorUI", "SliderUI", str + "SliderUI", "SpinnerUI", str + "SpinnerUI", 
        "SplitPaneUI", str + "SplitPaneUI", "TabbedPaneUI", str + "TabbedPaneUI", "TableHeaderUI", str + "TableHeaderUI", "TableUI", str + "TableUI", "TextAreaUI", str + "TextUI", 
        "TextFieldUI", str + "TextUI", "TextPaneUI", str + "TextUI", "ToggleButtonUI", str + "ButtonUI", "ToolBarSeparatorUI", str + "SeparatorUI", "ToolBarUI", str + "ToolBarUI", 
        "ToolTipUI", str + "ToolTipUI", "TreeUI", str + "TreeUI", "ViewportUI", str + "ViewportUI" };
    MultiUIDefaults multiUIDefaults = new MultiUIDefaults(arrayOfObject.length / 2, 0.75F);
    multiUIDefaults.putDefaults(arrayOfObject);
    return multiUIDefaults;
  }
  
  public static ComponentUI createUIs(ComponentUI paramComponentUI, Vector paramVector, JComponent paramJComponent) {
    ComponentUI componentUI = UIManager.getDefaults().getUI(paramJComponent);
    if (componentUI != null) {
      paramVector.addElement(componentUI);
      LookAndFeel[] arrayOfLookAndFeel = UIManager.getAuxiliaryLookAndFeels();
      if (arrayOfLookAndFeel != null)
        for (byte b = 0; b < arrayOfLookAndFeel.length; b++) {
          componentUI = arrayOfLookAndFeel[b].getDefaults().getUI(paramJComponent);
          if (componentUI != null)
            paramVector.addElement(componentUI); 
        }  
    } else {
      return null;
    } 
    return (paramVector.size() == 1) ? (ComponentUI)paramVector.elementAt(0) : paramComponentUI;
  }
  
  protected static ComponentUI[] uisToArray(Vector paramVector) {
    if (paramVector == null)
      return new ComponentUI[0]; 
    int i = paramVector.size();
    if (i > 0) {
      ComponentUI[] arrayOfComponentUI = new ComponentUI[i];
      for (byte b = 0; b < i; b++)
        arrayOfComponentUI[b] = (ComponentUI)paramVector.elementAt(b); 
      return arrayOfComponentUI;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\multi\MultiLookAndFeel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */