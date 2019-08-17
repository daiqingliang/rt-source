package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;

public class BasicIconFactory implements Serializable {
  private static Icon frame_icon;
  
  private static Icon checkBoxIcon;
  
  private static Icon radioButtonIcon;
  
  private static Icon checkBoxMenuItemIcon;
  
  private static Icon radioButtonMenuItemIcon;
  
  private static Icon menuItemCheckIcon;
  
  private static Icon menuItemArrowIcon;
  
  private static Icon menuArrowIcon;
  
  public static Icon getMenuItemCheckIcon() {
    if (menuItemCheckIcon == null)
      menuItemCheckIcon = new MenuItemCheckIcon(null); 
    return menuItemCheckIcon;
  }
  
  public static Icon getMenuItemArrowIcon() {
    if (menuItemArrowIcon == null)
      menuItemArrowIcon = new MenuItemArrowIcon(null); 
    return menuItemArrowIcon;
  }
  
  public static Icon getMenuArrowIcon() {
    if (menuArrowIcon == null)
      menuArrowIcon = new MenuArrowIcon(null); 
    return menuArrowIcon;
  }
  
  public static Icon getCheckBoxIcon() {
    if (checkBoxIcon == null)
      checkBoxIcon = new CheckBoxIcon(null); 
    return checkBoxIcon;
  }
  
  public static Icon getRadioButtonIcon() {
    if (radioButtonIcon == null)
      radioButtonIcon = new RadioButtonIcon(null); 
    return radioButtonIcon;
  }
  
  public static Icon getCheckBoxMenuItemIcon() {
    if (checkBoxMenuItemIcon == null)
      checkBoxMenuItemIcon = new CheckBoxMenuItemIcon(null); 
    return checkBoxMenuItemIcon;
  }
  
  public static Icon getRadioButtonMenuItemIcon() {
    if (radioButtonMenuItemIcon == null)
      radioButtonMenuItemIcon = new RadioButtonMenuItemIcon(null); 
    return radioButtonMenuItemIcon;
  }
  
  public static Icon createEmptyFrameIcon() {
    if (frame_icon == null)
      frame_icon = new EmptyFrameIcon(null); 
    return frame_icon;
  }
  
  private static class CheckBoxIcon implements Icon, Serializable {
    static final int csize = 13;
    
    private CheckBoxIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return 13; }
    
    public int getIconHeight() { return 13; }
  }
  
  private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable {
    private CheckBoxMenuItemIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      boolean bool = buttonModel.isSelected();
      if (bool) {
        param1Graphics.drawLine(param1Int1 + 7, param1Int2 + 1, param1Int1 + 7, param1Int2 + 3);
        param1Graphics.drawLine(param1Int1 + 6, param1Int2 + 2, param1Int1 + 6, param1Int2 + 4);
        param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 3, param1Int1 + 5, param1Int2 + 5);
        param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 4, param1Int1 + 4, param1Int2 + 6);
        param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 5, param1Int1 + 3, param1Int2 + 7);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 4, param1Int1 + 2, param1Int2 + 6);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 3, param1Int1 + 1, param1Int2 + 5);
      } 
    }
    
    public int getIconWidth() { return 9; }
    
    public int getIconHeight() { return 9; }
  }
  
  private static class EmptyFrameIcon implements Icon, Serializable {
    int height = 16;
    
    int width = 14;
    
    private EmptyFrameIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return this.width; }
    
    public int getIconHeight() { return this.height; }
  }
  
  private static class MenuArrowIcon implements Icon, UIResource, Serializable {
    private MenuArrowIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      Polygon polygon = new Polygon();
      polygon.addPoint(param1Int1, param1Int2);
      polygon.addPoint(param1Int1 + getIconWidth(), param1Int2 + getIconHeight() / 2);
      polygon.addPoint(param1Int1, param1Int2 + getIconHeight());
      param1Graphics.fillPolygon(polygon);
    }
    
    public int getIconWidth() { return 4; }
    
    public int getIconHeight() { return 8; }
  }
  
  private static class MenuItemArrowIcon implements Icon, UIResource, Serializable {
    private MenuItemArrowIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return 4; }
    
    public int getIconHeight() { return 8; }
  }
  
  private static class MenuItemCheckIcon implements Icon, UIResource, Serializable {
    private MenuItemCheckIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return 9; }
    
    public int getIconHeight() { return 9; }
  }
  
  private static class RadioButtonIcon implements Icon, UIResource, Serializable {
    private RadioButtonIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return 13; }
    
    public int getIconHeight() { return 13; }
  }
  
  private static class RadioButtonMenuItemIcon implements Icon, UIResource, Serializable {
    private RadioButtonMenuItemIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      if (abstractButton.isSelected() == true)
        param1Graphics.fillOval(param1Int1 + 1, param1Int2 + 1, getIconWidth(), getIconHeight()); 
    }
    
    public int getIconWidth() { return 6; }
    
    public int getIconHeight() { return 6; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */