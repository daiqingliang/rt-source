package javax.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ToolTipUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class BasicToolTipUI extends ToolTipUI {
  static BasicToolTipUI sharedInstance = new BasicToolTipUI();
  
  private static PropertyChangeListener sharedPropertyChangedListener;
  
  private PropertyChangeListener propertyChangeListener;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return sharedInstance; }
  
  public void installUI(JComponent paramJComponent) {
    installDefaults(paramJComponent);
    installComponents(paramJComponent);
    installListeners(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults(paramJComponent);
    uninstallComponents(paramJComponent);
    uninstallListeners(paramJComponent);
  }
  
  protected void installDefaults(JComponent paramJComponent) {
    LookAndFeel.installColorsAndFont(paramJComponent, "ToolTip.background", "ToolTip.foreground", "ToolTip.font");
    LookAndFeel.installProperty(paramJComponent, "opaque", Boolean.TRUE);
    componentChanged(paramJComponent);
  }
  
  protected void uninstallDefaults(JComponent paramJComponent) { LookAndFeel.uninstallBorder(paramJComponent); }
  
  private void installComponents(JComponent paramJComponent) { BasicHTML.updateRenderer(paramJComponent, ((JToolTip)paramJComponent).getTipText()); }
  
  private void uninstallComponents(JComponent paramJComponent) { BasicHTML.updateRenderer(paramJComponent, ""); }
  
  protected void installListeners(JComponent paramJComponent) {
    this.propertyChangeListener = createPropertyChangeListener(paramJComponent);
    paramJComponent.addPropertyChangeListener(this.propertyChangeListener);
  }
  
  protected void uninstallListeners(JComponent paramJComponent) {
    paramJComponent.removePropertyChangeListener(this.propertyChangeListener);
    this.propertyChangeListener = null;
  }
  
  private PropertyChangeListener createPropertyChangeListener(JComponent paramJComponent) {
    if (sharedPropertyChangedListener == null)
      sharedPropertyChangedListener = new PropertyChangeHandler(null); 
    return sharedPropertyChangedListener;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    Font font = paramJComponent.getFont();
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, font);
    Dimension dimension = paramJComponent.getSize();
    paramGraphics.setColor(paramJComponent.getForeground());
    String str = ((JToolTip)paramJComponent).getTipText();
    if (str == null)
      str = ""; 
    Insets insets = paramJComponent.getInsets();
    Rectangle rectangle = new Rectangle(insets.left + 3, insets.top, dimension.width - insets.left + insets.right - 6, dimension.height - insets.top + insets.bottom);
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null) {
      view.paint(paramGraphics, rectangle);
    } else {
      paramGraphics.setFont(font);
      SwingUtilities2.drawString(paramJComponent, paramGraphics, str, rectangle.x, rectangle.y + fontMetrics.getAscent());
    } 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Font font = paramJComponent.getFont();
    FontMetrics fontMetrics = paramJComponent.getFontMetrics(font);
    Insets insets = paramJComponent.getInsets();
    Dimension dimension = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
    String str = ((JToolTip)paramJComponent).getTipText();
    if (str == null || str.equals("")) {
      str = "";
    } else {
      View view = (paramJComponent != null) ? (View)paramJComponent.getClientProperty("html") : null;
      if (view != null) {
        dimension.width += (int)view.getPreferredSpan(0) + 6;
        dimension.height += (int)view.getPreferredSpan(1);
      } else {
        dimension.width += SwingUtilities2.stringWidth(paramJComponent, fontMetrics, str) + 6;
        dimension.height += fontMetrics.getHeight();
      } 
    } 
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
  
  private void componentChanged(JComponent paramJComponent) {
    JComponent jComponent = ((JToolTip)paramJComponent).getComponent();
    if (jComponent != null && !jComponent.isEnabled()) {
      if (UIManager.getBorder("ToolTip.borderInactive") != null) {
        LookAndFeel.installBorder(paramJComponent, "ToolTip.borderInactive");
      } else {
        LookAndFeel.installBorder(paramJComponent, "ToolTip.border");
      } 
      if (UIManager.getColor("ToolTip.backgroundInactive") != null) {
        LookAndFeel.installColors(paramJComponent, "ToolTip.backgroundInactive", "ToolTip.foregroundInactive");
      } else {
        LookAndFeel.installColors(paramJComponent, "ToolTip.background", "ToolTip.foreground");
      } 
    } else {
      LookAndFeel.installBorder(paramJComponent, "ToolTip.border");
      LookAndFeel.installColors(paramJComponent, "ToolTip.background", "ToolTip.foreground");
    } 
  }
  
  private static class PropertyChangeHandler implements PropertyChangeListener {
    private PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str.equals("tiptext") || "font".equals(str) || "foreground".equals(str)) {
        JToolTip jToolTip = (JToolTip)param1PropertyChangeEvent.getSource();
        String str1 = jToolTip.getTipText();
        BasicHTML.updateRenderer(jToolTip, str1);
      } else if ("component".equals(str)) {
        JToolTip jToolTip = (JToolTip)param1PropertyChangeEvent.getSource();
        if (jToolTip.getUI() instanceof BasicToolTipUI)
          ((BasicToolTipUI)jToolTip.getUI()).componentChanged(jToolTip); 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicToolTipUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */