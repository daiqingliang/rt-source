package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InputMapUIResource;
import javax.swing.plaf.LabelUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicLabelUI extends LabelUI implements PropertyChangeListener {
  protected static BasicLabelUI labelUI = new BasicLabelUI();
  
  private static final Object BASIC_LABEL_UI_KEY = new Object();
  
  private Rectangle paintIconR = new Rectangle();
  
  private Rectangle paintTextR = new Rectangle();
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("press"));
    paramLazyActionMap.put(new Actions("release"));
  }
  
  protected String layoutCL(JLabel paramJLabel, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3) { return SwingUtilities.layoutCompoundLabel(paramJLabel, paramFontMetrics, paramString, paramIcon, paramJLabel.getVerticalAlignment(), paramJLabel.getHorizontalAlignment(), paramJLabel.getVerticalTextPosition(), paramJLabel.getHorizontalTextPosition(), paramRectangle1, paramRectangle2, paramRectangle3, paramJLabel.getIconTextGap()); }
  
  protected void paintEnabledText(JLabel paramJLabel, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2) {
    int i = paramJLabel.getDisplayedMnemonicIndex();
    paramGraphics.setColor(paramJLabel.getForeground());
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
  }
  
  protected void paintDisabledText(JLabel paramJLabel, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2) {
    int i = paramJLabel.getDisplayedMnemonicIndex();
    Color color = paramJLabel.getBackground();
    paramGraphics.setColor(color.brighter());
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1 + 1, paramInt2 + 1);
    paramGraphics.setColor(color.darker());
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    JLabel jLabel = (JLabel)paramJComponent;
    String str1 = jLabel.getText();
    Icon icon = jLabel.isEnabled() ? jLabel.getIcon() : jLabel.getDisabledIcon();
    if (icon == null && str1 == null)
      return; 
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(jLabel, paramGraphics);
    String str2 = layout(jLabel, fontMetrics, paramJComponent.getWidth(), paramJComponent.getHeight());
    if (icon != null)
      icon.paintIcon(paramJComponent, paramGraphics, this.paintIconR.x, this.paintIconR.y); 
    if (str1 != null) {
      View view = (View)paramJComponent.getClientProperty("html");
      if (view != null) {
        view.paint(paramGraphics, this.paintTextR);
      } else {
        int i = this.paintTextR.x;
        int j = this.paintTextR.y + fontMetrics.getAscent();
        if (jLabel.isEnabled()) {
          paintEnabledText(jLabel, paramGraphics, str2, i, j);
        } else {
          paintDisabledText(jLabel, paramGraphics, str2, i, j);
        } 
      } 
    } 
  }
  
  private String layout(JLabel paramJLabel, FontMetrics paramFontMetrics, int paramInt1, int paramInt2) {
    Insets insets = paramJLabel.getInsets(null);
    String str = paramJLabel.getText();
    Icon icon = paramJLabel.isEnabled() ? paramJLabel.getIcon() : paramJLabel.getDisabledIcon();
    Rectangle rectangle = new Rectangle();
    rectangle.x = insets.left;
    rectangle.y = insets.top;
    rectangle.width = paramInt1 - insets.left + insets.right;
    rectangle.height = paramInt2 - insets.top + insets.bottom;
    this.paintIconR.x = this.paintIconR.y = this.paintIconR.width = this.paintIconR.height = 0;
    this.paintTextR.x = this.paintTextR.y = this.paintTextR.width = this.paintTextR.height = 0;
    return layoutCL(paramJLabel, paramFontMetrics, str, icon, rectangle, this.paintIconR, this.paintTextR);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    JLabel jLabel = (JLabel)paramJComponent;
    String str = jLabel.getText();
    Icon icon = jLabel.isEnabled() ? jLabel.getIcon() : jLabel.getDisabledIcon();
    Insets insets = jLabel.getInsets(null);
    Font font = jLabel.getFont();
    int i = insets.left + insets.right;
    int j = insets.top + insets.bottom;
    if (icon == null && (str == null || (str != null && font == null)))
      return new Dimension(i, j); 
    if (str == null || (icon != null && font == null))
      return new Dimension(icon.getIconWidth() + i, icon.getIconHeight() + j); 
    FontMetrics fontMetrics = jLabel.getFontMetrics(font);
    Rectangle rectangle1 = new Rectangle();
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = new Rectangle();
    rectangle1.x = rectangle1.y = rectangle1.width = rectangle1.height = 0;
    rectangle2.x = rectangle2.y = rectangle2.width = rectangle2.height = 0;
    rectangle3.x = i;
    rectangle3.y = j;
    rectangle3.width = rectangle3.height = 32767;
    layoutCL(jLabel, fontMetrics, str, icon, rectangle3, rectangle1, rectangle2);
    int k = Math.min(rectangle1.x, rectangle2.x);
    int m = Math.max(rectangle1.x + rectangle1.width, rectangle2.x + rectangle2.width);
    int n = Math.min(rectangle1.y, rectangle2.y);
    int i1 = Math.max(rectangle1.y + rectangle1.height, rectangle2.y + rectangle2.height);
    Dimension dimension = new Dimension(m - k, i1 - n);
    dimension.width += i;
    dimension.height += j;
    return dimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension = getPreferredSize(paramJComponent);
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null)
      dimension.width = (int)(dimension.width - view.getPreferredSpan(0) - view.getMinimumSpan(0)); 
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    Dimension dimension = getPreferredSize(paramJComponent);
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null)
      dimension.width = (int)(dimension.width + view.getMaximumSpan(0) - view.getPreferredSpan(0)); 
    return dimension;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    JLabel jLabel = (JLabel)paramJComponent;
    String str = jLabel.getText();
    if (str == null || "".equals(str) || jLabel.getFont() == null)
      return -1; 
    FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
    layout(jLabel, fontMetrics, paramInt1, paramInt2);
    return BasicHTML.getBaseline(jLabel, this.paintTextR.y, fontMetrics.getAscent(), this.paintTextR.width, this.paintTextR.height);
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    if (paramJComponent.getClientProperty("html") != null)
      return Component.BaselineResizeBehavior.OTHER; 
    switch (((JLabel)paramJComponent).getVerticalAlignment()) {
      case 1:
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
      case 3:
        return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
      case 0:
        return Component.BaselineResizeBehavior.CENTER_OFFSET;
    } 
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  public void installUI(JComponent paramJComponent) {
    installDefaults((JLabel)paramJComponent);
    installComponents((JLabel)paramJComponent);
    installListeners((JLabel)paramJComponent);
    installKeyboardActions((JLabel)paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults((JLabel)paramJComponent);
    uninstallComponents((JLabel)paramJComponent);
    uninstallListeners((JLabel)paramJComponent);
    uninstallKeyboardActions((JLabel)paramJComponent);
  }
  
  protected void installDefaults(JLabel paramJLabel) {
    LookAndFeel.installColorsAndFont(paramJLabel, "Label.background", "Label.foreground", "Label.font");
    LookAndFeel.installProperty(paramJLabel, "opaque", Boolean.FALSE);
  }
  
  protected void installListeners(JLabel paramJLabel) { paramJLabel.addPropertyChangeListener(this); }
  
  protected void installComponents(JLabel paramJLabel) {
    BasicHTML.updateRenderer(paramJLabel, paramJLabel.getText());
    paramJLabel.setInheritsPopupMenu(true);
  }
  
  protected void installKeyboardActions(JLabel paramJLabel) {
    int i = paramJLabel.getDisplayedMnemonic();
    Component component = paramJLabel.getLabelFor();
    if (i != 0 && component != null) {
      LazyActionMap.installLazyActionMap(paramJLabel, BasicLabelUI.class, "Label.actionMap");
      InputMap inputMap = SwingUtilities.getUIInputMap(paramJLabel, 2);
      if (inputMap == null) {
        inputMap = new ComponentInputMapUIResource(paramJLabel);
        SwingUtilities.replaceUIInputMap(paramJLabel, 2, inputMap);
      } 
      inputMap.clear();
      inputMap.put(KeyStroke.getKeyStroke(i, BasicLookAndFeel.getFocusAcceleratorKeyMask(), false), "press");
    } else {
      InputMap inputMap = SwingUtilities.getUIInputMap(paramJLabel, 2);
      if (inputMap != null)
        inputMap.clear(); 
    } 
  }
  
  protected void uninstallDefaults(JLabel paramJLabel) {}
  
  protected void uninstallListeners(JLabel paramJLabel) { paramJLabel.removePropertyChangeListener(this); }
  
  protected void uninstallComponents(JLabel paramJLabel) { BasicHTML.updateRenderer(paramJLabel, ""); }
  
  protected void uninstallKeyboardActions(JLabel paramJLabel) {
    SwingUtilities.replaceUIInputMap(paramJLabel, 0, null);
    SwingUtilities.replaceUIInputMap(paramJLabel, 2, null);
    SwingUtilities.replaceUIActionMap(paramJLabel, null);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    if (System.getSecurityManager() != null) {
      AppContext appContext = AppContext.getAppContext();
      BasicLabelUI basicLabelUI = (BasicLabelUI)appContext.get(BASIC_LABEL_UI_KEY);
      if (basicLabelUI == null) {
        basicLabelUI = new BasicLabelUI();
        appContext.put(BASIC_LABEL_UI_KEY, basicLabelUI);
      } 
      return basicLabelUI;
    } 
    return labelUI;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == "text" || "font" == str || "foreground" == str) {
      JLabel jLabel = (JLabel)paramPropertyChangeEvent.getSource();
      String str1 = jLabel.getText();
      BasicHTML.updateRenderer(jLabel, str1);
    } else if (str == "labelFor" || str == "displayedMnemonic") {
      installKeyboardActions((JLabel)paramPropertyChangeEvent.getSource());
    } 
  }
  
  private static class Actions extends UIAction {
    private static final String PRESS = "press";
    
    private static final String RELEASE = "release";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JLabel jLabel = (JLabel)param1ActionEvent.getSource();
      String str = getName();
      if (str == "press") {
        doPress(jLabel);
      } else if (str == "release") {
        doRelease(jLabel, (param1ActionEvent.getActionCommand() != null));
      } 
    }
    
    private void doPress(JLabel param1JLabel) {
      Component component = param1JLabel.getLabelFor();
      if (component != null && component.isEnabled()) {
        InputMap inputMap = SwingUtilities.getUIInputMap(param1JLabel, 0);
        if (inputMap == null) {
          inputMap = new InputMapUIResource();
          SwingUtilities.replaceUIInputMap(param1JLabel, 0, inputMap);
        } 
        int i = param1JLabel.getDisplayedMnemonic();
        putOnRelease(inputMap, i, BasicLookAndFeel.getFocusAcceleratorKeyMask());
        putOnRelease(inputMap, i, 0);
        putOnRelease(inputMap, 18, 0);
        param1JLabel.requestFocus();
      } 
    }
    
    private void doRelease(JLabel param1JLabel, boolean param1Boolean) {
      Component component = param1JLabel.getLabelFor();
      if (component != null && component.isEnabled())
        if (param1JLabel.hasFocus()) {
          InputMap inputMap = SwingUtilities.getUIInputMap(param1JLabel, 0);
          if (inputMap != null) {
            int j = param1JLabel.getDisplayedMnemonic();
            removeOnRelease(inputMap, j, BasicLookAndFeel.getFocusAcceleratorKeyMask());
            removeOnRelease(inputMap, j, 0);
            removeOnRelease(inputMap, 18, 0);
          } 
          inputMap = SwingUtilities.getUIInputMap(param1JLabel, 2);
          if (inputMap == null) {
            inputMap = new InputMapUIResource();
            SwingUtilities.replaceUIInputMap(param1JLabel, 2, inputMap);
          } 
          int i = param1JLabel.getDisplayedMnemonic();
          if (param1Boolean) {
            putOnRelease(inputMap, 18, 0);
          } else {
            putOnRelease(inputMap, i, BasicLookAndFeel.getFocusAcceleratorKeyMask());
            putOnRelease(inputMap, i, 0);
          } 
          if (component instanceof Container && ((Container)component).isFocusCycleRoot()) {
            component.requestFocus();
          } else {
            SwingUtilities2.compositeRequestFocus(component);
          } 
        } else {
          InputMap inputMap = SwingUtilities.getUIInputMap(param1JLabel, 2);
          int i = param1JLabel.getDisplayedMnemonic();
          if (inputMap != null)
            if (param1Boolean) {
              removeOnRelease(inputMap, i, BasicLookAndFeel.getFocusAcceleratorKeyMask());
              removeOnRelease(inputMap, i, 0);
            } else {
              removeOnRelease(inputMap, 18, 0);
            }  
        }  
    }
    
    private void putOnRelease(InputMap param1InputMap, int param1Int1, int param1Int2) { param1InputMap.put(KeyStroke.getKeyStroke(param1Int1, param1Int2, true), "release"); }
    
    private void removeOnRelease(InputMap param1InputMap, int param1Int1, int param1Int2) { param1InputMap.remove(KeyStroke.getKeyStroke(param1Int1, param1Int2, true)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicLabelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */