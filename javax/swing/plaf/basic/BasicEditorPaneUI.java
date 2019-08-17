package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import sun.swing.SwingUtilities2;

public class BasicEditorPaneUI extends BasicTextUI {
  private static final String FONT_ATTRIBUTE_KEY = "FONT_ATTRIBUTE_KEY";
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicEditorPaneUI(); }
  
  protected String getPropertyPrefix() { return "EditorPane"; }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    updateDisplayProperties(paramJComponent.getFont(), paramJComponent.getForeground());
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    cleanDisplayProperties();
    super.uninstallUI(paramJComponent);
  }
  
  public EditorKit getEditorKit(JTextComponent paramJTextComponent) {
    JEditorPane jEditorPane = (JEditorPane)getComponent();
    return jEditorPane.getEditorKit();
  }
  
  ActionMap getActionMap() {
    ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
    actionMapUIResource.put("requestFocus", new BasicTextUI.FocusAction(this));
    EditorKit editorKit = getEditorKit(getComponent());
    if (editorKit != null) {
      Action[] arrayOfAction = editorKit.getActions();
      if (arrayOfAction != null)
        addActions(actionMapUIResource, arrayOfAction); 
    } 
    actionMapUIResource.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
    actionMapUIResource.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
    actionMapUIResource.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
    return actionMapUIResource;
  }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    super.propertyChange(paramPropertyChangeEvent);
    String str = paramPropertyChangeEvent.getPropertyName();
    if ("editorKit".equals(str)) {
      ActionMap actionMap = SwingUtilities.getUIActionMap(getComponent());
      if (actionMap != null) {
        Object object1 = paramPropertyChangeEvent.getOldValue();
        if (object1 instanceof EditorKit) {
          Action[] arrayOfAction = ((EditorKit)object1).getActions();
          if (arrayOfAction != null)
            removeActions(actionMap, arrayOfAction); 
        } 
        Object object2 = paramPropertyChangeEvent.getNewValue();
        if (object2 instanceof EditorKit) {
          Action[] arrayOfAction = ((EditorKit)object2).getActions();
          if (arrayOfAction != null)
            addActions(actionMap, arrayOfAction); 
        } 
      } 
      updateFocusTraversalKeys();
    } else if ("editable".equals(str)) {
      updateFocusTraversalKeys();
    } else if ("foreground".equals(str) || "font".equals(str) || "document".equals(str) || "JEditorPane.w3cLengthUnits".equals(str) || "JEditorPane.honorDisplayProperties".equals(str)) {
      JTextComponent jTextComponent = getComponent();
      updateDisplayProperties(jTextComponent.getFont(), jTextComponent.getForeground());
      if ("JEditorPane.w3cLengthUnits".equals(str) || "JEditorPane.honorDisplayProperties".equals(str))
        modelChanged(); 
      if ("foreground".equals(str)) {
        Object object = jTextComponent.getClientProperty("JEditorPane.honorDisplayProperties");
        boolean bool = false;
        if (object instanceof Boolean)
          bool = ((Boolean)object).booleanValue(); 
        if (bool)
          modelChanged(); 
      } 
    } 
  }
  
  void removeActions(ActionMap paramActionMap, Action[] paramArrayOfAction) {
    int i = paramArrayOfAction.length;
    for (byte b = 0; b < i; b++) {
      Action action = paramArrayOfAction[b];
      paramActionMap.remove(action.getValue("Name"));
    } 
  }
  
  void addActions(ActionMap paramActionMap, Action[] paramArrayOfAction) {
    int i = paramArrayOfAction.length;
    for (byte b = 0; b < i; b++) {
      Action action = paramArrayOfAction[b];
      paramActionMap.put(action.getValue("Name"), action);
    } 
  }
  
  void updateDisplayProperties(Font paramFont, Color paramColor) {
    JTextComponent jTextComponent = getComponent();
    Object object1 = jTextComponent.getClientProperty("JEditorPane.honorDisplayProperties");
    boolean bool1 = false;
    Object object2 = jTextComponent.getClientProperty("JEditorPane.w3cLengthUnits");
    boolean bool2 = false;
    if (object1 instanceof Boolean)
      bool1 = ((Boolean)object1).booleanValue(); 
    if (object2 instanceof Boolean)
      bool2 = ((Boolean)object2).booleanValue(); 
    if (this instanceof BasicTextPaneUI || bool1) {
      Document document = getComponent().getDocument();
      if (document instanceof StyledDocument)
        if (document instanceof HTMLDocument && bool1) {
          updateCSS(paramFont, paramColor);
        } else {
          updateStyle(paramFont, paramColor);
        }  
    } else {
      cleanDisplayProperties();
    } 
    if (bool2) {
      Document document = getComponent().getDocument();
      if (document instanceof HTMLDocument) {
        StyleSheet styleSheet = ((HTMLDocument)document).getStyleSheet();
        styleSheet.addRule("W3C_LENGTH_UNITS_ENABLE");
      } 
    } else {
      Document document = getComponent().getDocument();
      if (document instanceof HTMLDocument) {
        StyleSheet styleSheet = ((HTMLDocument)document).getStyleSheet();
        styleSheet.addRule("W3C_LENGTH_UNITS_DISABLE");
      } 
    } 
  }
  
  void cleanDisplayProperties() {
    Document document = getComponent().getDocument();
    if (document instanceof HTMLDocument) {
      StyleSheet styleSheet = ((HTMLDocument)document).getStyleSheet();
      StyleSheet[] arrayOfStyleSheet = styleSheet.getStyleSheets();
      if (arrayOfStyleSheet != null)
        for (StyleSheet styleSheet1 : arrayOfStyleSheet) {
          if (styleSheet1 instanceof StyleSheetUIResource) {
            styleSheet.removeStyleSheet(styleSheet1);
            styleSheet.addRule("BASE_SIZE_DISABLE");
            break;
          } 
        }  
      Style style = ((StyledDocument)document).getStyle("default");
      if (style.getAttribute("FONT_ATTRIBUTE_KEY") != null)
        style.removeAttribute("FONT_ATTRIBUTE_KEY"); 
    } 
  }
  
  private void updateCSS(Font paramFont, Color paramColor) {
    JTextComponent jTextComponent = getComponent();
    Document document = jTextComponent.getDocument();
    if (document instanceof HTMLDocument) {
      StyleSheetUIResource styleSheetUIResource = new StyleSheetUIResource();
      StyleSheet styleSheet = ((HTMLDocument)document).getStyleSheet();
      StyleSheet[] arrayOfStyleSheet = styleSheet.getStyleSheets();
      if (arrayOfStyleSheet != null)
        for (StyleSheet styleSheet1 : arrayOfStyleSheet) {
          if (styleSheet1 instanceof StyleSheetUIResource)
            styleSheet.removeStyleSheet(styleSheet1); 
        }  
      String str = SwingUtilities2.displayPropertiesToCSS(paramFont, paramColor);
      styleSheetUIResource.addRule(str);
      styleSheet.addStyleSheet(styleSheetUIResource);
      styleSheet.addRule("BASE_SIZE " + jTextComponent.getFont().getSize());
      Style style = ((StyledDocument)document).getStyle("default");
      if (!paramFont.equals(style.getAttribute("FONT_ATTRIBUTE_KEY")))
        style.addAttribute("FONT_ATTRIBUTE_KEY", paramFont); 
    } 
  }
  
  private void updateStyle(Font paramFont, Color paramColor) {
    updateFont(paramFont);
    updateForeground(paramColor);
  }
  
  private void updateForeground(Color paramColor) {
    StyledDocument styledDocument = (StyledDocument)getComponent().getDocument();
    Style style = styledDocument.getStyle("default");
    if (style == null)
      return; 
    if (paramColor == null) {
      if (style.getAttribute(StyleConstants.Foreground) != null)
        style.removeAttribute(StyleConstants.Foreground); 
    } else if (!paramColor.equals(StyleConstants.getForeground(style))) {
      StyleConstants.setForeground(style, paramColor);
    } 
  }
  
  private void updateFont(Font paramFont) {
    StyledDocument styledDocument = (StyledDocument)getComponent().getDocument();
    Style style = styledDocument.getStyle("default");
    if (style == null)
      return; 
    String str = (String)style.getAttribute(StyleConstants.FontFamily);
    Integer integer = (Integer)style.getAttribute(StyleConstants.FontSize);
    Boolean bool1 = (Boolean)style.getAttribute(StyleConstants.Bold);
    Boolean bool2 = (Boolean)style.getAttribute(StyleConstants.Italic);
    Font font = (Font)style.getAttribute("FONT_ATTRIBUTE_KEY");
    if (paramFont == null) {
      if (str != null)
        style.removeAttribute(StyleConstants.FontFamily); 
      if (integer != null)
        style.removeAttribute(StyleConstants.FontSize); 
      if (bool1 != null)
        style.removeAttribute(StyleConstants.Bold); 
      if (bool2 != null)
        style.removeAttribute(StyleConstants.Italic); 
      if (font != null)
        style.removeAttribute("FONT_ATTRIBUTE_KEY"); 
    } else {
      if (!paramFont.getName().equals(str))
        StyleConstants.setFontFamily(style, paramFont.getName()); 
      if (integer == null || integer.intValue() != paramFont.getSize())
        StyleConstants.setFontSize(style, paramFont.getSize()); 
      if (bool1 == null || bool1.booleanValue() != paramFont.isBold())
        StyleConstants.setBold(style, paramFont.isBold()); 
      if (bool2 == null || bool2.booleanValue() != paramFont.isItalic())
        StyleConstants.setItalic(style, paramFont.isItalic()); 
      if (!paramFont.equals(font))
        style.addAttribute("FONT_ATTRIBUTE_KEY", paramFont); 
    } 
  }
  
  static class StyleSheetUIResource extends StyleSheet implements UIResource {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicEditorPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */