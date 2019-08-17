package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Vector;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.UIResource;
import sun.swing.CachedPainter;

public class MetalIconFactory implements Serializable {
  private static Icon fileChooserDetailViewIcon;
  
  private static Icon fileChooserHomeFolderIcon;
  
  private static Icon fileChooserListViewIcon;
  
  private static Icon fileChooserNewFolderIcon;
  
  private static Icon fileChooserUpFolderIcon;
  
  private static Icon internalFrameAltMaximizeIcon;
  
  private static Icon internalFrameCloseIcon;
  
  private static Icon internalFrameDefaultMenuIcon;
  
  private static Icon internalFrameMaximizeIcon;
  
  private static Icon internalFrameMinimizeIcon;
  
  private static Icon radioButtonIcon;
  
  private static Icon treeComputerIcon;
  
  private static Icon treeFloppyDriveIcon;
  
  private static Icon treeHardDriveIcon;
  
  private static Icon menuArrowIcon;
  
  private static Icon menuItemArrowIcon;
  
  private static Icon checkBoxMenuItemIcon;
  
  private static Icon radioButtonMenuItemIcon;
  
  private static Icon checkBoxIcon;
  
  private static Icon oceanHorizontalSliderThumb;
  
  private static Icon oceanVerticalSliderThumb;
  
  public static final boolean DARK = false;
  
  public static final boolean LIGHT = true;
  
  private static final Dimension folderIcon16Size = new Dimension(16, 16);
  
  private static final Dimension fileIcon16Size = new Dimension(16, 16);
  
  private static final Dimension treeControlSize = new Dimension(18, 18);
  
  private static final Dimension menuArrowIconSize = new Dimension(4, 8);
  
  private static final Dimension menuCheckIconSize = new Dimension(10, 10);
  
  private static final int xOff = 4;
  
  public static Icon getFileChooserDetailViewIcon() {
    if (fileChooserDetailViewIcon == null)
      fileChooserDetailViewIcon = new FileChooserDetailViewIcon(null); 
    return fileChooserDetailViewIcon;
  }
  
  public static Icon getFileChooserHomeFolderIcon() {
    if (fileChooserHomeFolderIcon == null)
      fileChooserHomeFolderIcon = new FileChooserHomeFolderIcon(null); 
    return fileChooserHomeFolderIcon;
  }
  
  public static Icon getFileChooserListViewIcon() {
    if (fileChooserListViewIcon == null)
      fileChooserListViewIcon = new FileChooserListViewIcon(null); 
    return fileChooserListViewIcon;
  }
  
  public static Icon getFileChooserNewFolderIcon() {
    if (fileChooserNewFolderIcon == null)
      fileChooserNewFolderIcon = new FileChooserNewFolderIcon(null); 
    return fileChooserNewFolderIcon;
  }
  
  public static Icon getFileChooserUpFolderIcon() {
    if (fileChooserUpFolderIcon == null)
      fileChooserUpFolderIcon = new FileChooserUpFolderIcon(null); 
    return fileChooserUpFolderIcon;
  }
  
  public static Icon getInternalFrameAltMaximizeIcon(int paramInt) { return new InternalFrameAltMaximizeIcon(paramInt); }
  
  public static Icon getInternalFrameCloseIcon(int paramInt) { return new InternalFrameCloseIcon(paramInt); }
  
  public static Icon getInternalFrameDefaultMenuIcon() {
    if (internalFrameDefaultMenuIcon == null)
      internalFrameDefaultMenuIcon = new InternalFrameDefaultMenuIcon(null); 
    return internalFrameDefaultMenuIcon;
  }
  
  public static Icon getInternalFrameMaximizeIcon(int paramInt) { return new InternalFrameMaximizeIcon(paramInt); }
  
  public static Icon getInternalFrameMinimizeIcon(int paramInt) { return new InternalFrameMinimizeIcon(paramInt); }
  
  public static Icon getRadioButtonIcon() {
    if (radioButtonIcon == null)
      radioButtonIcon = new RadioButtonIcon(null); 
    return radioButtonIcon;
  }
  
  public static Icon getCheckBoxIcon() {
    if (checkBoxIcon == null)
      checkBoxIcon = new CheckBoxIcon(null); 
    return checkBoxIcon;
  }
  
  public static Icon getTreeComputerIcon() {
    if (treeComputerIcon == null)
      treeComputerIcon = new TreeComputerIcon(null); 
    return treeComputerIcon;
  }
  
  public static Icon getTreeFloppyDriveIcon() {
    if (treeFloppyDriveIcon == null)
      treeFloppyDriveIcon = new TreeFloppyDriveIcon(null); 
    return treeFloppyDriveIcon;
  }
  
  public static Icon getTreeFolderIcon() { return new TreeFolderIcon(); }
  
  public static Icon getTreeHardDriveIcon() {
    if (treeHardDriveIcon == null)
      treeHardDriveIcon = new TreeHardDriveIcon(null); 
    return treeHardDriveIcon;
  }
  
  public static Icon getTreeLeafIcon() { return new TreeLeafIcon(); }
  
  public static Icon getTreeControlIcon(boolean paramBoolean) { return new TreeControlIcon(paramBoolean); }
  
  public static Icon getMenuArrowIcon() {
    if (menuArrowIcon == null)
      menuArrowIcon = new MenuArrowIcon(null); 
    return menuArrowIcon;
  }
  
  public static Icon getMenuItemCheckIcon() { return null; }
  
  public static Icon getMenuItemArrowIcon() {
    if (menuItemArrowIcon == null)
      menuItemArrowIcon = new MenuItemArrowIcon(null); 
    return menuItemArrowIcon;
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
  
  public static Icon getHorizontalSliderThumbIcon() {
    if (MetalLookAndFeel.usingOcean()) {
      if (oceanHorizontalSliderThumb == null)
        oceanHorizontalSliderThumb = new OceanHorizontalSliderThumbIcon(); 
      return oceanHorizontalSliderThumb;
    } 
    return new HorizontalSliderThumbIcon();
  }
  
  public static Icon getVerticalSliderThumbIcon() {
    if (MetalLookAndFeel.usingOcean()) {
      if (oceanVerticalSliderThumb == null)
        oceanVerticalSliderThumb = new OceanVerticalSliderThumbIcon(); 
      return oceanVerticalSliderThumb;
    } 
    return new VerticalSliderThumbIcon();
  }
  
  private static class CheckBoxIcon implements Icon, UIResource, Serializable {
    private CheckBoxIcon() {}
    
    protected int getControlSize() { return 13; }
    
    private void paintOceanIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      ButtonModel buttonModel = ((JCheckBox)param1Component).getModel();
      param1Graphics.translate(param1Int1, param1Int2);
      int i = getIconWidth();
      int j = getIconHeight();
      if (buttonModel.isEnabled()) {
        if (buttonModel.isPressed() && buttonModel.isArmed()) {
          param1Graphics.setColor(MetalLookAndFeel.getControlShadow());
          param1Graphics.fillRect(0, 0, i, j);
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.fillRect(0, 0, i, 2);
          param1Graphics.fillRect(0, 2, 2, j - 2);
          param1Graphics.fillRect(i - 1, 1, 1, j - 1);
          param1Graphics.fillRect(1, j - 1, i - 2, 1);
        } else if (buttonModel.isRollover()) {
          MetalUtils.drawGradient(param1Component, param1Graphics, "CheckBox.gradient", 0, 0, i, j, true);
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawRect(0, 0, i - 1, j - 1);
          param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
          param1Graphics.drawRect(1, 1, i - 3, j - 3);
          param1Graphics.drawRect(2, 2, i - 5, j - 5);
        } else {
          MetalUtils.drawGradient(param1Component, param1Graphics, "CheckBox.gradient", 0, 0, i, j, true);
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawRect(0, 0, i - 1, j - 1);
        } 
        param1Graphics.setColor(MetalLookAndFeel.getControlInfo());
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        param1Graphics.drawRect(0, 0, i - 1, j - 1);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
      if (buttonModel.isSelected())
        drawCheck(param1Component, param1Graphics, param1Int1, param1Int2); 
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (MetalLookAndFeel.usingOcean()) {
        paintOceanIcon(param1Component, param1Graphics, param1Int1, param1Int2);
        return;
      } 
      ButtonModel buttonModel = ((JCheckBox)param1Component).getModel();
      int i = getControlSize();
      if (buttonModel.isEnabled()) {
        if (buttonModel.isPressed() && buttonModel.isArmed()) {
          param1Graphics.setColor(MetalLookAndFeel.getControlShadow());
          param1Graphics.fillRect(param1Int1, param1Int2, i - 1, i - 1);
          MetalUtils.drawPressed3DBorder(param1Graphics, param1Int1, param1Int2, i, i);
        } else {
          MetalUtils.drawFlush3DBorder(param1Graphics, param1Int1, param1Int2, i, i);
        } 
        param1Graphics.setColor(param1Component.getForeground());
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getControlShadow());
        param1Graphics.drawRect(param1Int1, param1Int2, i - 2, i - 2);
      } 
      if (buttonModel.isSelected())
        drawCheck(param1Component, param1Graphics, param1Int1, param1Int2); 
    }
    
    protected void drawCheck(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      int i = getControlSize();
      param1Graphics.fillRect(param1Int1 + 3, param1Int2 + 5, 2, i - 8);
      param1Graphics.drawLine(param1Int1 + i - 4, param1Int2 + 3, param1Int1 + 5, param1Int2 + i - 6);
      param1Graphics.drawLine(param1Int1 + i - 4, param1Int2 + 4, param1Int1 + 5, param1Int2 + i - 5);
    }
    
    public int getIconWidth() { return getControlSize(); }
    
    public int getIconHeight() { return getControlSize(); }
  }
  
  private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable {
    private CheckBoxMenuItemIcon() {}
    
    public void paintOceanIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      ButtonModel buttonModel = ((JMenuItem)param1Component).getModel();
      boolean bool1 = buttonModel.isSelected();
      boolean bool2 = buttonModel.isEnabled();
      boolean bool3 = buttonModel.isPressed();
      boolean bool4 = buttonModel.isArmed();
      param1Graphics.translate(param1Int1, param1Int2);
      if (bool2) {
        MetalUtils.drawGradient(param1Component, param1Graphics, "CheckBoxMenuItem.gradient", 1, 1, 7, 7, true);
        if (bool3 || bool4) {
          param1Graphics.setColor(MetalLookAndFeel.getControlInfo());
          param1Graphics.drawLine(0, 0, 8, 0);
          param1Graphics.drawLine(0, 0, 0, 8);
          param1Graphics.drawLine(8, 2, 8, 8);
          param1Graphics.drawLine(2, 8, 8, 8);
          param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
          param1Graphics.drawLine(9, 1, 9, 9);
          param1Graphics.drawLine(1, 9, 9, 9);
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawLine(0, 0, 8, 0);
          param1Graphics.drawLine(0, 0, 0, 8);
          param1Graphics.drawLine(8, 2, 8, 8);
          param1Graphics.drawLine(2, 8, 8, 8);
          param1Graphics.setColor(MetalLookAndFeel.getControlHighlight());
          param1Graphics.drawLine(9, 1, 9, 9);
          param1Graphics.drawLine(1, 9, 9, 9);
        } 
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        param1Graphics.drawRect(0, 0, 8, 8);
      } 
      if (bool1) {
        if (bool2) {
          if (bool4 || (param1Component instanceof javax.swing.JMenu && bool1)) {
            param1Graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
          } else {
            param1Graphics.setColor(MetalLookAndFeel.getControlInfo());
          } 
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        } 
        param1Graphics.drawLine(2, 2, 2, 6);
        param1Graphics.drawLine(3, 2, 3, 6);
        param1Graphics.drawLine(4, 4, 8, 0);
        param1Graphics.drawLine(4, 5, 9, 0);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (MetalLookAndFeel.usingOcean()) {
        paintOceanIcon(param1Component, param1Graphics, param1Int1, param1Int2);
        return;
      } 
      JMenuItem jMenuItem = (JMenuItem)param1Component;
      ButtonModel buttonModel = jMenuItem.getModel();
      boolean bool1 = buttonModel.isSelected();
      boolean bool2 = buttonModel.isEnabled();
      boolean bool3 = buttonModel.isPressed();
      boolean bool4 = buttonModel.isArmed();
      param1Graphics.translate(param1Int1, param1Int2);
      if (bool2) {
        if (bool3 || bool4) {
          param1Graphics.setColor(MetalLookAndFeel.getControlInfo());
          param1Graphics.drawLine(0, 0, 8, 0);
          param1Graphics.drawLine(0, 0, 0, 8);
          param1Graphics.drawLine(8, 2, 8, 8);
          param1Graphics.drawLine(2, 8, 8, 8);
          param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
          param1Graphics.drawLine(1, 1, 7, 1);
          param1Graphics.drawLine(1, 1, 1, 7);
          param1Graphics.drawLine(9, 1, 9, 9);
          param1Graphics.drawLine(1, 9, 9, 9);
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawLine(0, 0, 8, 0);
          param1Graphics.drawLine(0, 0, 0, 8);
          param1Graphics.drawLine(8, 2, 8, 8);
          param1Graphics.drawLine(2, 8, 8, 8);
          param1Graphics.setColor(MetalLookAndFeel.getControlHighlight());
          param1Graphics.drawLine(1, 1, 7, 1);
          param1Graphics.drawLine(1, 1, 1, 7);
          param1Graphics.drawLine(9, 1, 9, 9);
          param1Graphics.drawLine(1, 9, 9, 9);
        } 
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        param1Graphics.drawRect(0, 0, 8, 8);
      } 
      if (bool1) {
        if (bool2) {
          if (buttonModel.isArmed() || (param1Component instanceof javax.swing.JMenu && buttonModel.isSelected())) {
            param1Graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
          } else {
            param1Graphics.setColor(jMenuItem.getForeground());
          } 
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        } 
        param1Graphics.drawLine(2, 2, 2, 6);
        param1Graphics.drawLine(3, 2, 3, 6);
        param1Graphics.drawLine(4, 4, 8, 0);
        param1Graphics.drawLine(4, 5, 9, 0);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return menuCheckIconSize.width; }
    
    public int getIconHeight() { return menuCheckIconSize.height; }
  }
  
  private static class FileChooserDetailViewIcon implements Icon, UIResource, Serializable {
    private FileChooserDetailViewIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(2, 2, 5, 2);
      param1Graphics.drawLine(2, 3, 2, 7);
      param1Graphics.drawLine(3, 7, 6, 7);
      param1Graphics.drawLine(6, 6, 6, 3);
      param1Graphics.drawLine(2, 10, 5, 10);
      param1Graphics.drawLine(2, 11, 2, 15);
      param1Graphics.drawLine(3, 15, 6, 15);
      param1Graphics.drawLine(6, 14, 6, 11);
      param1Graphics.drawLine(8, 5, 15, 5);
      param1Graphics.drawLine(8, 13, 15, 13);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.drawRect(3, 3, 2, 3);
      param1Graphics.drawRect(3, 11, 2, 3);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      param1Graphics.drawLine(4, 4, 4, 5);
      param1Graphics.drawLine(4, 12, 4, 13);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 18; }
    
    public int getIconHeight() { return 18; }
  }
  
  private static class FileChooserHomeFolderIcon implements Icon, UIResource, Serializable {
    private FileChooserHomeFolderIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(8, 1, 1, 8);
      param1Graphics.drawLine(8, 1, 15, 8);
      param1Graphics.drawLine(11, 2, 11, 3);
      param1Graphics.drawLine(12, 2, 12, 4);
      param1Graphics.drawLine(3, 7, 3, 15);
      param1Graphics.drawLine(13, 7, 13, 15);
      param1Graphics.drawLine(4, 15, 12, 15);
      param1Graphics.drawLine(6, 9, 6, 14);
      param1Graphics.drawLine(10, 9, 10, 14);
      param1Graphics.drawLine(7, 9, 9, 9);
      param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      param1Graphics.fillRect(8, 2, 1, 1);
      param1Graphics.fillRect(7, 3, 3, 1);
      param1Graphics.fillRect(6, 4, 5, 1);
      param1Graphics.fillRect(5, 5, 7, 1);
      param1Graphics.fillRect(4, 6, 9, 2);
      param1Graphics.drawLine(9, 12, 9, 12);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.drawLine(4, 8, 12, 8);
      param1Graphics.fillRect(4, 9, 2, 6);
      param1Graphics.fillRect(11, 9, 2, 6);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 18; }
    
    public int getIconHeight() { return 18; }
  }
  
  private static class FileChooserListViewIcon implements Icon, UIResource, Serializable {
    private FileChooserListViewIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(2, 2, 5, 2);
      param1Graphics.drawLine(2, 3, 2, 7);
      param1Graphics.drawLine(3, 7, 6, 7);
      param1Graphics.drawLine(6, 6, 6, 3);
      param1Graphics.drawLine(10, 2, 13, 2);
      param1Graphics.drawLine(10, 3, 10, 7);
      param1Graphics.drawLine(11, 7, 14, 7);
      param1Graphics.drawLine(14, 6, 14, 3);
      param1Graphics.drawLine(2, 10, 5, 10);
      param1Graphics.drawLine(2, 11, 2, 15);
      param1Graphics.drawLine(3, 15, 6, 15);
      param1Graphics.drawLine(6, 14, 6, 11);
      param1Graphics.drawLine(10, 10, 13, 10);
      param1Graphics.drawLine(10, 11, 10, 15);
      param1Graphics.drawLine(11, 15, 14, 15);
      param1Graphics.drawLine(14, 14, 14, 11);
      param1Graphics.drawLine(8, 5, 8, 5);
      param1Graphics.drawLine(16, 5, 16, 5);
      param1Graphics.drawLine(8, 13, 8, 13);
      param1Graphics.drawLine(16, 13, 16, 13);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.drawRect(3, 3, 2, 3);
      param1Graphics.drawRect(11, 3, 2, 3);
      param1Graphics.drawRect(3, 11, 2, 3);
      param1Graphics.drawRect(11, 11, 2, 3);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      param1Graphics.drawLine(4, 4, 4, 5);
      param1Graphics.drawLine(12, 4, 12, 5);
      param1Graphics.drawLine(4, 12, 4, 13);
      param1Graphics.drawLine(12, 12, 12, 13);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 18; }
    
    public int getIconHeight() { return 18; }
  }
  
  private static class FileChooserNewFolderIcon implements Icon, UIResource, Serializable {
    private FileChooserNewFolderIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.fillRect(3, 5, 12, 9);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(1, 6, 1, 14);
      param1Graphics.drawLine(2, 14, 15, 14);
      param1Graphics.drawLine(15, 13, 15, 5);
      param1Graphics.drawLine(2, 5, 9, 5);
      param1Graphics.drawLine(10, 6, 14, 6);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      param1Graphics.drawLine(2, 6, 2, 13);
      param1Graphics.drawLine(3, 6, 9, 6);
      param1Graphics.drawLine(10, 7, 14, 7);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      param1Graphics.drawLine(11, 3, 15, 3);
      param1Graphics.drawLine(10, 4, 15, 4);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 18; }
    
    public int getIconHeight() { return 18; }
  }
  
  private static class FileChooserUpFolderIcon implements Icon, UIResource, Serializable {
    private FileChooserUpFolderIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.fillRect(3, 5, 12, 9);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(1, 6, 1, 14);
      param1Graphics.drawLine(2, 14, 15, 14);
      param1Graphics.drawLine(15, 13, 15, 5);
      param1Graphics.drawLine(2, 5, 9, 5);
      param1Graphics.drawLine(10, 6, 14, 6);
      param1Graphics.drawLine(8, 13, 8, 16);
      param1Graphics.drawLine(8, 9, 8, 9);
      param1Graphics.drawLine(7, 10, 9, 10);
      param1Graphics.drawLine(6, 11, 10, 11);
      param1Graphics.drawLine(5, 12, 11, 12);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      param1Graphics.drawLine(2, 6, 2, 13);
      param1Graphics.drawLine(3, 6, 9, 6);
      param1Graphics.drawLine(10, 7, 14, 7);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      param1Graphics.drawLine(11, 3, 15, 3);
      param1Graphics.drawLine(10, 4, 15, 4);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 18; }
    
    public int getIconHeight() { return 18; }
  }
  
  public static class FileIcon16 implements Icon, Serializable {
    MetalIconFactory.ImageCacher imageCacher;
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      GraphicsConfiguration graphicsConfiguration = param1Component.getGraphicsConfiguration();
      if (this.imageCacher == null)
        this.imageCacher = new MetalIconFactory.ImageCacher(); 
      Image image = this.imageCacher.getImage(graphicsConfiguration);
      if (image == null) {
        if (graphicsConfiguration != null) {
          image = graphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), 2);
        } else {
          image = new BufferedImage(getIconWidth(), getIconHeight(), 2);
        } 
        Graphics graphics = image.getGraphics();
        paintMe(param1Component, graphics);
        graphics.dispose();
        this.imageCacher.cacheImage(image, graphicsConfiguration);
      } 
      param1Graphics.drawImage(image, param1Int1, param1Int2 + getShift(), null);
    }
    
    private void paintMe(Component param1Component, Graphics param1Graphics) {
      int i = fileIcon16Size.width - 1;
      int j = fileIcon16Size.height - 1;
      param1Graphics.setColor(MetalLookAndFeel.getWindowBackground());
      param1Graphics.fillRect(4, 2, 9, 12);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(2, 0, 2, j);
      param1Graphics.drawLine(2, 0, i - 4, 0);
      param1Graphics.drawLine(2, j, i - 1, j);
      param1Graphics.drawLine(i - 1, 6, i - 1, j);
      param1Graphics.drawLine(i - 6, 2, i - 2, 6);
      param1Graphics.drawLine(i - 5, 1, i - 4, 1);
      param1Graphics.drawLine(i - 3, 2, i - 3, 3);
      param1Graphics.drawLine(i - 2, 4, i - 2, 5);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.drawLine(3, 1, 3, j - 1);
      param1Graphics.drawLine(3, 1, i - 6, 1);
      param1Graphics.drawLine(i - 2, 7, i - 2, j - 1);
      param1Graphics.drawLine(i - 5, 2, i - 3, 4);
      param1Graphics.drawLine(3, j - 1, i - 2, j - 1);
    }
    
    public int getShift() { return 0; }
    
    public int getAdditionalHeight() { return 0; }
    
    public int getIconWidth() { return fileIcon16Size.width; }
    
    public int getIconHeight() { return fileIcon16Size.height + getAdditionalHeight(); }
  }
  
  public static class FolderIcon16 implements Icon, Serializable {
    MetalIconFactory.ImageCacher imageCacher;
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      GraphicsConfiguration graphicsConfiguration = param1Component.getGraphicsConfiguration();
      if (this.imageCacher == null)
        this.imageCacher = new MetalIconFactory.ImageCacher(); 
      Image image = this.imageCacher.getImage(graphicsConfiguration);
      if (image == null) {
        if (graphicsConfiguration != null) {
          image = graphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), 2);
        } else {
          image = new BufferedImage(getIconWidth(), getIconHeight(), 2);
        } 
        Graphics graphics = image.getGraphics();
        paintMe(param1Component, graphics);
        graphics.dispose();
        this.imageCacher.cacheImage(image, graphicsConfiguration);
      } 
      param1Graphics.drawImage(image, param1Int1, param1Int2 + getShift(), null);
    }
    
    private void paintMe(Component param1Component, Graphics param1Graphics) {
      int i = folderIcon16Size.width - 1;
      int j = folderIcon16Size.height - 1;
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      param1Graphics.drawLine(i - 5, 3, i, 3);
      param1Graphics.drawLine(i - 6, 4, i, 4);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.fillRect(2, 7, 13, 8);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
      param1Graphics.drawLine(i - 6, 5, i - 1, 5);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(0, 6, 0, j);
      param1Graphics.drawLine(1, 5, i - 7, 5);
      param1Graphics.drawLine(i - 6, 6, i - 1, 6);
      param1Graphics.drawLine(i, 5, i, j);
      param1Graphics.drawLine(0, j, i, j);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      param1Graphics.drawLine(1, 6, 1, j - 1);
      param1Graphics.drawLine(1, 6, i - 7, 6);
      param1Graphics.drawLine(i - 6, 7, i - 1, 7);
    }
    
    public int getShift() { return 0; }
    
    public int getAdditionalHeight() { return 0; }
    
    public int getIconWidth() { return folderIcon16Size.width; }
    
    public int getIconHeight() { return folderIcon16Size.height + getAdditionalHeight(); }
  }
  
  private static class HorizontalSliderThumbIcon implements Icon, Serializable, UIResource {
    protected static MetalBumps controlBumps;
    
    protected static MetalBumps primaryBumps;
    
    public HorizontalSliderThumbIcon() {
      controlBumps = new MetalBumps(10, 6, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlInfo(), MetalLookAndFeel.getControl());
      primaryBumps = new MetalBumps(10, 6, MetalLookAndFeel.getPrimaryControl(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlShadow());
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      if (param1Component.hasFocus()) {
        param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      } else {
        param1Graphics.setColor(param1Component.isEnabled() ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
      } 
      param1Graphics.drawLine(1, 0, 13, 0);
      param1Graphics.drawLine(0, 1, 0, 8);
      param1Graphics.drawLine(14, 1, 14, 8);
      param1Graphics.drawLine(1, 9, 7, 15);
      param1Graphics.drawLine(7, 15, 14, 8);
      if (param1Component.hasFocus()) {
        param1Graphics.setColor(param1Component.getForeground());
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getControl());
      } 
      param1Graphics.fillRect(1, 1, 13, 8);
      param1Graphics.drawLine(2, 9, 12, 9);
      param1Graphics.drawLine(3, 10, 11, 10);
      param1Graphics.drawLine(4, 11, 10, 11);
      param1Graphics.drawLine(5, 12, 9, 12);
      param1Graphics.drawLine(6, 13, 8, 13);
      param1Graphics.drawLine(7, 14, 7, 14);
      if (param1Component.isEnabled())
        if (param1Component.hasFocus()) {
          primaryBumps.paintIcon(param1Component, param1Graphics, 2, 2);
        } else {
          controlBumps.paintIcon(param1Component, param1Graphics, 2, 2);
        }  
      if (param1Component.isEnabled()) {
        param1Graphics.setColor(param1Component.hasFocus() ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControlHighlight());
        param1Graphics.drawLine(1, 1, 13, 1);
        param1Graphics.drawLine(1, 1, 1, 8);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 15; }
    
    public int getIconHeight() { return 16; }
  }
  
  static class ImageCacher {
    Vector<ImageGcPair> images = new Vector(1, 1);
    
    ImageGcPair currentImageGcPair;
    
    Image getImage(GraphicsConfiguration param1GraphicsConfiguration) {
      if (this.currentImageGcPair == null || !this.currentImageGcPair.hasSameConfiguration(param1GraphicsConfiguration)) {
        for (ImageGcPair imageGcPair : this.images) {
          if (imageGcPair.hasSameConfiguration(param1GraphicsConfiguration)) {
            this.currentImageGcPair = imageGcPair;
            return imageGcPair.image;
          } 
        } 
        return null;
      } 
      return this.currentImageGcPair.image;
    }
    
    void cacheImage(Image param1Image, GraphicsConfiguration param1GraphicsConfiguration) {
      ImageGcPair imageGcPair = new ImageGcPair(param1Image, param1GraphicsConfiguration);
      this.images.addElement(imageGcPair);
      this.currentImageGcPair = imageGcPair;
    }
    
    class ImageGcPair {
      Image image;
      
      GraphicsConfiguration gc;
      
      ImageGcPair(Image param2Image, GraphicsConfiguration param2GraphicsConfiguration) {
        this.image = param2Image;
        this.gc = param2GraphicsConfiguration;
      }
      
      boolean hasSameConfiguration(GraphicsConfiguration param2GraphicsConfiguration) { return ((param2GraphicsConfiguration != null && param2GraphicsConfiguration.equals(this.gc)) || (param2GraphicsConfiguration == null && this.gc == null)); }
    }
  }
  
  private static class InternalFrameAltMaximizeIcon implements Icon, UIResource, Serializable {
    int iconSize = 16;
    
    public InternalFrameAltMaximizeIcon(int param1Int) { this.iconSize = param1Int; }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      JButton jButton = (JButton)param1Component;
      ButtonModel buttonModel = jButton.getModel();
      ColorUIResource colorUIResource1 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource3 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      ColorUIResource colorUIResource4 = MetalLookAndFeel.getBlack();
      ColorUIResource colorUIResource5 = MetalLookAndFeel.getWhite();
      ColorUIResource colorUIResource6 = MetalLookAndFeel.getWhite();
      if (jButton.getClientProperty("paintActive") != Boolean.TRUE) {
        colorUIResource1 = MetalLookAndFeel.getControl();
        colorUIResource2 = colorUIResource1;
        colorUIResource3 = MetalLookAndFeel.getControlDarkShadow();
        if (buttonModel.isPressed() && buttonModel.isArmed()) {
          colorUIResource2 = MetalLookAndFeel.getControlShadow();
          colorUIResource5 = colorUIResource2;
          colorUIResource3 = colorUIResource4;
        } 
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        colorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        colorUIResource5 = colorUIResource2;
        colorUIResource3 = colorUIResource4;
      } 
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.fillRect(0, 0, this.iconSize, this.iconSize);
      param1Graphics.setColor(colorUIResource2);
      param1Graphics.fillRect(3, 6, this.iconSize - 9, this.iconSize - 9);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawRect(1, 5, this.iconSize - 8, this.iconSize - 8);
      param1Graphics.drawLine(1, this.iconSize - 2, 1, this.iconSize - 2);
      param1Graphics.setColor(colorUIResource6);
      param1Graphics.drawRect(2, 6, this.iconSize - 7, this.iconSize - 7);
      param1Graphics.setColor(colorUIResource5);
      param1Graphics.drawRect(3, 7, this.iconSize - 9, this.iconSize - 9);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.drawRect(2, 6, this.iconSize - 8, this.iconSize - 8);
      param1Graphics.setColor(colorUIResource5);
      param1Graphics.drawLine(this.iconSize - 6, 8, this.iconSize - 6, 8);
      param1Graphics.drawLine(this.iconSize - 9, 6, this.iconSize - 7, 8);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawLine(this.iconSize - 6, 9, this.iconSize - 6, 9);
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.drawLine(this.iconSize - 9, 5, this.iconSize - 9, 5);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.fillRect(this.iconSize - 7, 3, 3, 5);
      param1Graphics.drawLine(this.iconSize - 6, 5, this.iconSize - 3, 2);
      param1Graphics.drawLine(this.iconSize - 6, 6, this.iconSize - 2, 2);
      param1Graphics.drawLine(this.iconSize - 6, 7, this.iconSize - 3, 7);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawLine(this.iconSize - 8, 2, this.iconSize - 7, 2);
      param1Graphics.drawLine(this.iconSize - 8, 3, this.iconSize - 8, 7);
      param1Graphics.drawLine(this.iconSize - 6, 4, this.iconSize - 3, 1);
      param1Graphics.drawLine(this.iconSize - 4, 6, this.iconSize - 3, 6);
      param1Graphics.setColor(colorUIResource6);
      param1Graphics.drawLine(this.iconSize - 6, 3, this.iconSize - 6, 3);
      param1Graphics.drawLine(this.iconSize - 4, 5, this.iconSize - 2, 3);
      param1Graphics.drawLine(this.iconSize - 4, 8, this.iconSize - 3, 8);
      param1Graphics.drawLine(this.iconSize - 2, 8, this.iconSize - 2, 7);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return this.iconSize; }
    
    public int getIconHeight() { return this.iconSize; }
  }
  
  private static class InternalFrameCloseIcon implements Icon, UIResource, Serializable {
    int iconSize = 16;
    
    public InternalFrameCloseIcon(int param1Int) { this.iconSize = param1Int; }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      JButton jButton = (JButton)param1Component;
      ButtonModel buttonModel = jButton.getModel();
      ColorUIResource colorUIResource1 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource3 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      ColorUIResource colorUIResource4 = MetalLookAndFeel.getBlack();
      ColorUIResource colorUIResource5 = MetalLookAndFeel.getWhite();
      ColorUIResource colorUIResource6 = MetalLookAndFeel.getWhite();
      if (jButton.getClientProperty("paintActive") != Boolean.TRUE) {
        colorUIResource1 = MetalLookAndFeel.getControl();
        colorUIResource2 = colorUIResource1;
        colorUIResource3 = MetalLookAndFeel.getControlDarkShadow();
        if (buttonModel.isPressed() && buttonModel.isArmed()) {
          colorUIResource2 = MetalLookAndFeel.getControlShadow();
          colorUIResource5 = colorUIResource2;
          colorUIResource3 = colorUIResource4;
        } 
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        colorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        colorUIResource5 = colorUIResource2;
        colorUIResource3 = colorUIResource4;
      } 
      int i = this.iconSize / 2;
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.fillRect(0, 0, this.iconSize, this.iconSize);
      param1Graphics.setColor(colorUIResource2);
      param1Graphics.fillRect(3, 3, this.iconSize - 6, this.iconSize - 6);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawRect(1, 1, this.iconSize - 3, this.iconSize - 3);
      param1Graphics.drawRect(2, 2, this.iconSize - 5, this.iconSize - 5);
      param1Graphics.setColor(colorUIResource6);
      param1Graphics.drawRect(2, 2, this.iconSize - 3, this.iconSize - 3);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.drawRect(2, 2, this.iconSize - 4, this.iconSize - 4);
      param1Graphics.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
      param1Graphics.drawLine(this.iconSize - 3, 3, this.iconSize - 3, 3);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawLine(4, 5, 5, 4);
      param1Graphics.drawLine(4, this.iconSize - 6, this.iconSize - 6, 4);
      param1Graphics.setColor(colorUIResource5);
      param1Graphics.drawLine(6, this.iconSize - 5, this.iconSize - 5, 6);
      param1Graphics.drawLine(i, i + 2, i + 2, i);
      param1Graphics.drawLine(this.iconSize - 5, this.iconSize - 5, this.iconSize - 4, this.iconSize - 5);
      param1Graphics.drawLine(this.iconSize - 5, this.iconSize - 4, this.iconSize - 5, this.iconSize - 4);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.drawLine(5, 5, this.iconSize - 6, this.iconSize - 6);
      param1Graphics.drawLine(6, 5, this.iconSize - 5, this.iconSize - 6);
      param1Graphics.drawLine(5, 6, this.iconSize - 6, this.iconSize - 5);
      param1Graphics.drawLine(5, this.iconSize - 5, this.iconSize - 5, 5);
      param1Graphics.drawLine(5, this.iconSize - 6, this.iconSize - 6, 5);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return this.iconSize; }
    
    public int getIconHeight() { return this.iconSize; }
  }
  
  private static class InternalFrameDefaultMenuIcon implements Icon, UIResource, Serializable {
    private InternalFrameDefaultMenuIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      ColorUIResource colorUIResource1 = MetalLookAndFeel.getWindowBackground();
      ColorUIResource colorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource3 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(colorUIResource2);
      param1Graphics.fillRect(0, 0, 16, 16);
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.fillRect(2, 6, 13, 9);
      param1Graphics.drawLine(2, 2, 2, 2);
      param1Graphics.drawLine(5, 2, 5, 2);
      param1Graphics.drawLine(8, 2, 8, 2);
      param1Graphics.drawLine(11, 2, 11, 2);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.drawRect(1, 1, 13, 13);
      param1Graphics.drawLine(1, 0, 14, 0);
      param1Graphics.drawLine(15, 1, 15, 14);
      param1Graphics.drawLine(1, 15, 14, 15);
      param1Graphics.drawLine(0, 1, 0, 14);
      param1Graphics.drawLine(2, 5, 13, 5);
      param1Graphics.drawLine(3, 3, 3, 3);
      param1Graphics.drawLine(6, 3, 6, 3);
      param1Graphics.drawLine(9, 3, 9, 3);
      param1Graphics.drawLine(12, 3, 12, 3);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 16; }
    
    public int getIconHeight() { return 16; }
  }
  
  private static class InternalFrameMaximizeIcon implements Icon, UIResource, Serializable {
    protected int iconSize = 16;
    
    public InternalFrameMaximizeIcon(int param1Int) { this.iconSize = param1Int; }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      JButton jButton = (JButton)param1Component;
      ButtonModel buttonModel = jButton.getModel();
      ColorUIResource colorUIResource1 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource3 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      ColorUIResource colorUIResource4 = MetalLookAndFeel.getBlack();
      ColorUIResource colorUIResource5 = MetalLookAndFeel.getWhite();
      ColorUIResource colorUIResource6 = MetalLookAndFeel.getWhite();
      if (jButton.getClientProperty("paintActive") != Boolean.TRUE) {
        colorUIResource1 = MetalLookAndFeel.getControl();
        colorUIResource2 = colorUIResource1;
        colorUIResource3 = MetalLookAndFeel.getControlDarkShadow();
        if (buttonModel.isPressed() && buttonModel.isArmed()) {
          colorUIResource2 = MetalLookAndFeel.getControlShadow();
          colorUIResource5 = colorUIResource2;
          colorUIResource3 = colorUIResource4;
        } 
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        colorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        colorUIResource5 = colorUIResource2;
        colorUIResource3 = colorUIResource4;
      } 
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.fillRect(0, 0, this.iconSize, this.iconSize);
      param1Graphics.setColor(colorUIResource2);
      param1Graphics.fillRect(3, 7, this.iconSize - 10, this.iconSize - 10);
      param1Graphics.setColor(colorUIResource5);
      param1Graphics.drawRect(3, 7, this.iconSize - 10, this.iconSize - 10);
      param1Graphics.setColor(colorUIResource6);
      param1Graphics.drawRect(2, 6, this.iconSize - 7, this.iconSize - 7);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawRect(1, 5, this.iconSize - 7, this.iconSize - 7);
      param1Graphics.drawRect(2, 6, this.iconSize - 9, this.iconSize - 9);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.drawRect(2, 6, this.iconSize - 8, this.iconSize - 8);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawLine(3, this.iconSize - 5, this.iconSize - 9, 7);
      param1Graphics.drawLine(this.iconSize - 6, 4, this.iconSize - 5, 3);
      param1Graphics.drawLine(this.iconSize - 7, 1, this.iconSize - 7, 2);
      param1Graphics.drawLine(this.iconSize - 6, 1, this.iconSize - 2, 1);
      param1Graphics.setColor(colorUIResource5);
      param1Graphics.drawLine(5, this.iconSize - 4, this.iconSize - 8, 9);
      param1Graphics.setColor(colorUIResource6);
      param1Graphics.drawLine(this.iconSize - 6, 3, this.iconSize - 4, 5);
      param1Graphics.drawLine(this.iconSize - 4, 5, this.iconSize - 4, 6);
      param1Graphics.drawLine(this.iconSize - 2, 7, this.iconSize - 1, 7);
      param1Graphics.drawLine(this.iconSize - 1, 2, this.iconSize - 1, 6);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.drawLine(3, this.iconSize - 4, this.iconSize - 3, 2);
      param1Graphics.drawLine(3, this.iconSize - 3, this.iconSize - 2, 2);
      param1Graphics.drawLine(4, this.iconSize - 3, 5, this.iconSize - 3);
      param1Graphics.drawLine(this.iconSize - 7, 8, this.iconSize - 7, 9);
      param1Graphics.drawLine(this.iconSize - 6, 2, this.iconSize - 4, 2);
      param1Graphics.drawRect(this.iconSize - 3, 3, 1, 3);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return this.iconSize; }
    
    public int getIconHeight() { return this.iconSize; }
  }
  
  private static class InternalFrameMinimizeIcon implements Icon, UIResource, Serializable {
    int iconSize = 16;
    
    public InternalFrameMinimizeIcon(int param1Int) { this.iconSize = param1Int; }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      JButton jButton = (JButton)param1Component;
      ButtonModel buttonModel = jButton.getModel();
      ColorUIResource colorUIResource1 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource2 = MetalLookAndFeel.getPrimaryControl();
      ColorUIResource colorUIResource3 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      ColorUIResource colorUIResource4 = MetalLookAndFeel.getBlack();
      ColorUIResource colorUIResource5 = MetalLookAndFeel.getWhite();
      ColorUIResource colorUIResource6 = MetalLookAndFeel.getWhite();
      if (jButton.getClientProperty("paintActive") != Boolean.TRUE) {
        colorUIResource1 = MetalLookAndFeel.getControl();
        colorUIResource2 = colorUIResource1;
        colorUIResource3 = MetalLookAndFeel.getControlDarkShadow();
        if (buttonModel.isPressed() && buttonModel.isArmed()) {
          colorUIResource2 = MetalLookAndFeel.getControlShadow();
          colorUIResource5 = colorUIResource2;
          colorUIResource3 = colorUIResource4;
        } 
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        colorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow();
        colorUIResource5 = colorUIResource2;
        colorUIResource3 = colorUIResource4;
      } 
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.fillRect(0, 0, this.iconSize, this.iconSize);
      param1Graphics.setColor(colorUIResource2);
      param1Graphics.fillRect(4, 11, this.iconSize - 13, this.iconSize - 13);
      param1Graphics.setColor(colorUIResource6);
      param1Graphics.drawRect(2, 10, this.iconSize - 10, this.iconSize - 11);
      param1Graphics.setColor(colorUIResource5);
      param1Graphics.drawRect(3, 10, this.iconSize - 12, this.iconSize - 12);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawRect(1, 8, this.iconSize - 10, this.iconSize - 10);
      param1Graphics.drawRect(2, 9, this.iconSize - 12, this.iconSize - 12);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.drawRect(2, 9, this.iconSize - 11, this.iconSize - 11);
      param1Graphics.drawLine(this.iconSize - 10, 10, this.iconSize - 10, 10);
      param1Graphics.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
      param1Graphics.setColor(colorUIResource3);
      param1Graphics.fillRect(this.iconSize - 7, 3, 3, 5);
      param1Graphics.drawLine(this.iconSize - 6, 5, this.iconSize - 3, 2);
      param1Graphics.drawLine(this.iconSize - 6, 6, this.iconSize - 2, 2);
      param1Graphics.drawLine(this.iconSize - 6, 7, this.iconSize - 3, 7);
      param1Graphics.setColor(colorUIResource4);
      param1Graphics.drawLine(this.iconSize - 8, 2, this.iconSize - 7, 2);
      param1Graphics.drawLine(this.iconSize - 8, 3, this.iconSize - 8, 7);
      param1Graphics.drawLine(this.iconSize - 6, 4, this.iconSize - 3, 1);
      param1Graphics.drawLine(this.iconSize - 4, 6, this.iconSize - 3, 6);
      param1Graphics.setColor(colorUIResource6);
      param1Graphics.drawLine(this.iconSize - 6, 3, this.iconSize - 6, 3);
      param1Graphics.drawLine(this.iconSize - 4, 5, this.iconSize - 2, 3);
      param1Graphics.drawLine(this.iconSize - 7, 8, this.iconSize - 3, 8);
      param1Graphics.drawLine(this.iconSize - 2, 8, this.iconSize - 2, 7);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return this.iconSize; }
    
    public int getIconHeight() { return this.iconSize; }
  }
  
  private static class MenuArrowIcon implements Icon, UIResource, Serializable {
    private MenuArrowIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      JMenuItem jMenuItem = (JMenuItem)param1Component;
      ButtonModel buttonModel = jMenuItem.getModel();
      param1Graphics.translate(param1Int1, param1Int2);
      if (!buttonModel.isEnabled()) {
        param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
      } else if (buttonModel.isArmed() || (param1Component instanceof javax.swing.JMenu && buttonModel.isSelected())) {
        param1Graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
      } else {
        param1Graphics.setColor(jMenuItem.getForeground());
      } 
      if (MetalUtils.isLeftToRight(jMenuItem)) {
        param1Graphics.drawLine(0, 0, 0, 7);
        param1Graphics.drawLine(1, 1, 1, 6);
        param1Graphics.drawLine(2, 2, 2, 5);
        param1Graphics.drawLine(3, 3, 3, 4);
      } else {
        param1Graphics.drawLine(4, 0, 4, 7);
        param1Graphics.drawLine(3, 1, 3, 6);
        param1Graphics.drawLine(2, 2, 2, 5);
        param1Graphics.drawLine(1, 3, 1, 4);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return menuArrowIconSize.width; }
    
    public int getIconHeight() { return menuArrowIconSize.height; }
  }
  
  private static class MenuItemArrowIcon implements Icon, UIResource, Serializable {
    private MenuItemArrowIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return menuArrowIconSize.width; }
    
    public int getIconHeight() { return menuArrowIconSize.height; }
  }
  
  private static class OceanHorizontalSliderThumbIcon extends CachedPainter implements Icon, Serializable, UIResource {
    private static Polygon THUMB_SHAPE = new Polygon(new int[] { 0, 14, 14, 7, 0 }, new int[] { 0, 0, 8, 15, 8 }, 5);
    
    OceanHorizontalSliderThumbIcon() { super(3); }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (!(param1Graphics instanceof Graphics2D))
        return; 
      paint(param1Component, param1Graphics, param1Int1, param1Int2, getIconWidth(), getIconHeight(), new Object[] { Boolean.valueOf(param1Component.hasFocus()), Boolean.valueOf(param1Component.isEnabled()), MetalLookAndFeel.getCurrentTheme() });
    }
    
    protected Image createImage(Component param1Component, int param1Int1, int param1Int2, GraphicsConfiguration param1GraphicsConfiguration, Object[] param1ArrayOfObject) { return (param1GraphicsConfiguration == null) ? new BufferedImage(param1Int1, param1Int2, 2) : param1GraphicsConfiguration.createCompatibleImage(param1Int1, param1Int2, 2); }
    
    protected void paintToImage(Component param1Component, Image param1Image, Graphics param1Graphics, int param1Int1, int param1Int2, Object[] param1ArrayOfObject) {
      Graphics2D graphics2D = (Graphics2D)param1Graphics;
      boolean bool1 = ((Boolean)param1ArrayOfObject[0]).booleanValue();
      boolean bool2 = ((Boolean)param1ArrayOfObject[1]).booleanValue();
      Rectangle rectangle = graphics2D.getClipBounds();
      graphics2D.clip(THUMB_SHAPE);
      if (!bool2) {
        graphics2D.setColor(MetalLookAndFeel.getControl());
        graphics2D.fillRect(1, 1, 13, 14);
      } else if (bool1) {
        MetalUtils.drawGradient(param1Component, graphics2D, "Slider.focusGradient", 1, 1, 13, 14, true);
      } else {
        MetalUtils.drawGradient(param1Component, graphics2D, "Slider.gradient", 1, 1, 13, 14, true);
      } 
      graphics2D.setClip(rectangle);
      if (bool1) {
        graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      } else {
        graphics2D.setColor(bool2 ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
      } 
      graphics2D.drawLine(1, 0, 13, 0);
      graphics2D.drawLine(0, 1, 0, 8);
      graphics2D.drawLine(14, 1, 14, 8);
      graphics2D.drawLine(1, 9, 7, 15);
      graphics2D.drawLine(7, 15, 14, 8);
      if (bool1 && bool2) {
        graphics2D.setColor(MetalLookAndFeel.getPrimaryControl());
        graphics2D.fillRect(1, 1, 13, 1);
        graphics2D.fillRect(1, 2, 1, 7);
        graphics2D.fillRect(13, 2, 1, 7);
        graphics2D.drawLine(2, 9, 7, 14);
        graphics2D.drawLine(8, 13, 12, 9);
      } 
    }
    
    public int getIconWidth() { return 15; }
    
    public int getIconHeight() { return 16; }
  }
  
  private static class OceanVerticalSliderThumbIcon extends CachedPainter implements Icon, Serializable, UIResource {
    private static Polygon LTR_THUMB_SHAPE = new Polygon(new int[] { 0, 8, 15, 8, 0 }, new int[] { 0, 0, 7, 14, 14 }, 5);
    
    private static Polygon RTL_THUMB_SHAPE = new Polygon(new int[] { 15, 15, 7, 0, 7 }, new int[] { 0, 14, 14, 7, 0 }, 5);
    
    OceanVerticalSliderThumbIcon() { super(3); }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (!(param1Graphics instanceof Graphics2D))
        return; 
      paint(param1Component, param1Graphics, param1Int1, param1Int2, getIconWidth(), getIconHeight(), new Object[] { Boolean.valueOf(MetalUtils.isLeftToRight(param1Component)), Boolean.valueOf(param1Component.hasFocus()), Boolean.valueOf(param1Component.isEnabled()), MetalLookAndFeel.getCurrentTheme() });
    }
    
    protected void paintToImage(Component param1Component, Image param1Image, Graphics param1Graphics, int param1Int1, int param1Int2, Object[] param1ArrayOfObject) {
      Graphics2D graphics2D = (Graphics2D)param1Graphics;
      boolean bool1 = ((Boolean)param1ArrayOfObject[0]).booleanValue();
      boolean bool2 = ((Boolean)param1ArrayOfObject[1]).booleanValue();
      boolean bool3 = ((Boolean)param1ArrayOfObject[2]).booleanValue();
      Rectangle rectangle = graphics2D.getClipBounds();
      if (bool1) {
        graphics2D.clip(LTR_THUMB_SHAPE);
      } else {
        graphics2D.clip(RTL_THUMB_SHAPE);
      } 
      if (!bool3) {
        graphics2D.setColor(MetalLookAndFeel.getControl());
        graphics2D.fillRect(1, 1, 14, 14);
      } else if (bool2) {
        MetalUtils.drawGradient(param1Component, graphics2D, "Slider.focusGradient", 1, 1, 14, 14, false);
      } else {
        MetalUtils.drawGradient(param1Component, graphics2D, "Slider.gradient", 1, 1, 14, 14, false);
      } 
      graphics2D.setClip(rectangle);
      if (bool2) {
        graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      } else {
        graphics2D.setColor(bool3 ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
      } 
      if (bool1) {
        graphics2D.drawLine(1, 0, 8, 0);
        graphics2D.drawLine(0, 1, 0, 13);
        graphics2D.drawLine(1, 14, 8, 14);
        graphics2D.drawLine(9, 1, 15, 7);
        graphics2D.drawLine(9, 13, 15, 7);
      } else {
        graphics2D.drawLine(7, 0, 14, 0);
        graphics2D.drawLine(15, 1, 15, 13);
        graphics2D.drawLine(7, 14, 14, 14);
        graphics2D.drawLine(0, 7, 6, 1);
        graphics2D.drawLine(0, 7, 6, 13);
      } 
      if (bool2 && bool3) {
        graphics2D.setColor(MetalLookAndFeel.getPrimaryControl());
        if (bool1) {
          graphics2D.drawLine(1, 1, 8, 1);
          graphics2D.drawLine(1, 1, 1, 13);
          graphics2D.drawLine(1, 13, 8, 13);
          graphics2D.drawLine(9, 2, 14, 7);
          graphics2D.drawLine(9, 12, 14, 7);
        } else {
          graphics2D.drawLine(7, 1, 14, 1);
          graphics2D.drawLine(14, 1, 14, 13);
          graphics2D.drawLine(7, 13, 14, 13);
          graphics2D.drawLine(1, 7, 7, 1);
          graphics2D.drawLine(1, 7, 7, 13);
        } 
      } 
    }
    
    public int getIconWidth() { return 16; }
    
    public int getIconHeight() { return 15; }
    
    protected Image createImage(Component param1Component, int param1Int1, int param1Int2, GraphicsConfiguration param1GraphicsConfiguration, Object[] param1ArrayOfObject) { return (param1GraphicsConfiguration == null) ? new BufferedImage(param1Int1, param1Int2, 2) : param1GraphicsConfiguration.createCompatibleImage(param1Int1, param1Int2, 2); }
  }
  
  public static class PaletteCloseIcon implements Icon, UIResource, Serializable {
    int iconSize = 7;
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      ColorUIResource colorUIResource1;
      JButton jButton = (JButton)param1Component;
      ButtonModel buttonModel = jButton.getModel();
      ColorUIResource colorUIResource2 = MetalLookAndFeel.getPrimaryControlHighlight();
      ColorUIResource colorUIResource3 = MetalLookAndFeel.getPrimaryControlInfo();
      if (buttonModel.isPressed() && buttonModel.isArmed()) {
        colorUIResource1 = colorUIResource3;
      } else {
        colorUIResource1 = MetalLookAndFeel.getPrimaryControlDarkShadow();
      } 
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(colorUIResource1);
      param1Graphics.drawLine(0, 1, 5, 6);
      param1Graphics.drawLine(1, 0, 6, 5);
      param1Graphics.drawLine(1, 1, 6, 6);
      param1Graphics.drawLine(6, 1, 1, 6);
      param1Graphics.drawLine(5, 0, 0, 5);
      param1Graphics.drawLine(5, 1, 1, 5);
      param1Graphics.setColor(colorUIResource2);
      param1Graphics.drawLine(6, 2, 5, 3);
      param1Graphics.drawLine(2, 6, 3, 5);
      param1Graphics.drawLine(6, 6, 6, 6);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return this.iconSize; }
    
    public int getIconHeight() { return this.iconSize; }
  }
  
  private static class RadioButtonIcon implements Icon, UIResource, Serializable {
    private RadioButtonIcon() {}
    
    public void paintOceanIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      ButtonModel buttonModel = ((JRadioButton)param1Component).getModel();
      boolean bool = buttonModel.isEnabled();
      boolean bool1 = (bool && buttonModel.isPressed() && buttonModel.isArmed()) ? 1 : 0;
      boolean bool2 = (bool && buttonModel.isRollover()) ? 1 : 0;
      param1Graphics.translate(param1Int1, param1Int2);
      if (bool && !bool1) {
        MetalUtils.drawGradient(param1Component, param1Graphics, "RadioButton.gradient", 1, 1, 10, 10, true);
        param1Graphics.setColor(param1Component.getBackground());
        param1Graphics.fillRect(1, 1, 1, 1);
        param1Graphics.fillRect(10, 1, 1, 1);
        param1Graphics.fillRect(1, 10, 1, 1);
        param1Graphics.fillRect(10, 10, 1, 1);
      } else if (bool1 || !bool) {
        if (bool1) {
          param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControl());
        } 
        param1Graphics.fillRect(2, 2, 8, 8);
        param1Graphics.fillRect(4, 1, 4, 1);
        param1Graphics.fillRect(4, 10, 4, 1);
        param1Graphics.fillRect(1, 4, 1, 4);
        param1Graphics.fillRect(10, 4, 1, 4);
      } 
      if (!bool) {
        param1Graphics.setColor(MetalLookAndFeel.getInactiveControlTextColor());
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      } 
      param1Graphics.drawLine(4, 0, 7, 0);
      param1Graphics.drawLine(8, 1, 9, 1);
      param1Graphics.drawLine(10, 2, 10, 3);
      param1Graphics.drawLine(11, 4, 11, 7);
      param1Graphics.drawLine(10, 8, 10, 9);
      param1Graphics.drawLine(9, 10, 8, 10);
      param1Graphics.drawLine(7, 11, 4, 11);
      param1Graphics.drawLine(3, 10, 2, 10);
      param1Graphics.drawLine(1, 9, 1, 8);
      param1Graphics.drawLine(0, 7, 0, 4);
      param1Graphics.drawLine(1, 3, 1, 2);
      param1Graphics.drawLine(2, 1, 3, 1);
      if (bool1) {
        param1Graphics.fillRect(1, 4, 1, 4);
        param1Graphics.fillRect(2, 2, 1, 2);
        param1Graphics.fillRect(3, 2, 1, 1);
        param1Graphics.fillRect(4, 1, 4, 1);
      } else if (bool2) {
        param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
        param1Graphics.fillRect(4, 1, 4, 2);
        param1Graphics.fillRect(8, 2, 2, 2);
        param1Graphics.fillRect(9, 4, 2, 4);
        param1Graphics.fillRect(8, 8, 2, 2);
        param1Graphics.fillRect(4, 9, 4, 2);
        param1Graphics.fillRect(2, 8, 2, 2);
        param1Graphics.fillRect(1, 4, 2, 4);
        param1Graphics.fillRect(2, 2, 2, 2);
      } 
      if (buttonModel.isSelected()) {
        if (bool) {
          param1Graphics.setColor(MetalLookAndFeel.getControlInfo());
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        } 
        param1Graphics.fillRect(4, 4, 4, 4);
        param1Graphics.drawLine(4, 3, 7, 3);
        param1Graphics.drawLine(8, 4, 8, 7);
        param1Graphics.drawLine(7, 8, 4, 8);
        param1Graphics.drawLine(3, 7, 3, 4);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (MetalLookAndFeel.usingOcean()) {
        paintOceanIcon(param1Component, param1Graphics, param1Int1, param1Int2);
        return;
      } 
      JRadioButton jRadioButton = (JRadioButton)param1Component;
      ButtonModel buttonModel = jRadioButton.getModel();
      boolean bool = buttonModel.isSelected();
      Color color1 = param1Component.getBackground();
      Color color2 = param1Component.getForeground();
      ColorUIResource colorUIResource = MetalLookAndFeel.getControlShadow();
      Color color3 = MetalLookAndFeel.getControlDarkShadow();
      Color color4 = MetalLookAndFeel.getControlHighlight();
      Color color5 = MetalLookAndFeel.getControlHighlight();
      Color color6 = color1;
      if (!buttonModel.isEnabled()) {
        Color color = color5 = color1;
        color3 = color2 = colorUIResource;
      } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
        color4 = color6 = colorUIResource;
      } 
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(color6);
      param1Graphics.fillRect(2, 2, 9, 9);
      param1Graphics.setColor(color3);
      param1Graphics.drawLine(4, 0, 7, 0);
      param1Graphics.drawLine(8, 1, 9, 1);
      param1Graphics.drawLine(10, 2, 10, 3);
      param1Graphics.drawLine(11, 4, 11, 7);
      param1Graphics.drawLine(10, 8, 10, 9);
      param1Graphics.drawLine(9, 10, 8, 10);
      param1Graphics.drawLine(7, 11, 4, 11);
      param1Graphics.drawLine(3, 10, 2, 10);
      param1Graphics.drawLine(1, 9, 1, 8);
      param1Graphics.drawLine(0, 7, 0, 4);
      param1Graphics.drawLine(1, 3, 1, 2);
      param1Graphics.drawLine(2, 1, 3, 1);
      param1Graphics.setColor(color4);
      param1Graphics.drawLine(2, 9, 2, 8);
      param1Graphics.drawLine(1, 7, 1, 4);
      param1Graphics.drawLine(2, 2, 2, 3);
      param1Graphics.drawLine(2, 2, 3, 2);
      param1Graphics.drawLine(4, 1, 7, 1);
      param1Graphics.drawLine(8, 2, 9, 2);
      param1Graphics.setColor(color5);
      param1Graphics.drawLine(10, 1, 10, 1);
      param1Graphics.drawLine(11, 2, 11, 3);
      param1Graphics.drawLine(12, 4, 12, 7);
      param1Graphics.drawLine(11, 8, 11, 9);
      param1Graphics.drawLine(10, 10, 10, 10);
      param1Graphics.drawLine(9, 11, 8, 11);
      param1Graphics.drawLine(7, 12, 4, 12);
      param1Graphics.drawLine(3, 11, 2, 11);
      if (bool) {
        param1Graphics.setColor(color2);
        param1Graphics.fillRect(4, 4, 4, 4);
        param1Graphics.drawLine(4, 3, 7, 3);
        param1Graphics.drawLine(8, 4, 8, 7);
        param1Graphics.drawLine(7, 8, 4, 8);
        param1Graphics.drawLine(3, 7, 3, 4);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 13; }
    
    public int getIconHeight() { return 13; }
  }
  
  private static class RadioButtonMenuItemIcon implements Icon, UIResource, Serializable {
    private RadioButtonMenuItemIcon() {}
    
    public void paintOceanIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      ButtonModel buttonModel = ((JMenuItem)param1Component).getModel();
      boolean bool1 = buttonModel.isSelected();
      boolean bool2 = buttonModel.isEnabled();
      boolean bool3 = buttonModel.isPressed();
      boolean bool4 = buttonModel.isArmed();
      param1Graphics.translate(param1Int1, param1Int2);
      if (bool2) {
        MetalUtils.drawGradient(param1Component, param1Graphics, "RadioButtonMenuItem.gradient", 1, 1, 7, 7, true);
        if (bool3 || bool4) {
          param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControlHighlight());
        } 
        param1Graphics.drawLine(2, 9, 7, 9);
        param1Graphics.drawLine(9, 2, 9, 7);
        param1Graphics.drawLine(8, 8, 8, 8);
        if (bool3 || bool4) {
          param1Graphics.setColor(MetalLookAndFeel.getControlInfo());
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        } 
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
      } 
      param1Graphics.drawLine(2, 0, 6, 0);
      param1Graphics.drawLine(2, 8, 6, 8);
      param1Graphics.drawLine(0, 2, 0, 6);
      param1Graphics.drawLine(8, 2, 8, 6);
      param1Graphics.drawLine(1, 1, 1, 1);
      param1Graphics.drawLine(7, 1, 7, 1);
      param1Graphics.drawLine(1, 7, 1, 7);
      param1Graphics.drawLine(7, 7, 7, 7);
      if (bool1) {
        if (bool2) {
          if (bool4 || (param1Component instanceof javax.swing.JMenu && buttonModel.isSelected())) {
            param1Graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
          } else {
            param1Graphics.setColor(MetalLookAndFeel.getControlInfo());
          } 
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        } 
        param1Graphics.drawLine(3, 2, 5, 2);
        param1Graphics.drawLine(2, 3, 6, 3);
        param1Graphics.drawLine(2, 4, 6, 4);
        param1Graphics.drawLine(2, 5, 6, 5);
        param1Graphics.drawLine(3, 6, 5, 6);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      if (MetalLookAndFeel.usingOcean()) {
        paintOceanIcon(param1Component, param1Graphics, param1Int1, param1Int2);
        return;
      } 
      JMenuItem jMenuItem = (JMenuItem)param1Component;
      ButtonModel buttonModel = jMenuItem.getModel();
      boolean bool1 = buttonModel.isSelected();
      boolean bool2 = buttonModel.isEnabled();
      boolean bool3 = buttonModel.isPressed();
      boolean bool4 = buttonModel.isArmed();
      param1Graphics.translate(param1Int1, param1Int2);
      if (bool2) {
        if (bool3 || bool4) {
          param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
          param1Graphics.drawLine(3, 1, 8, 1);
          param1Graphics.drawLine(2, 9, 7, 9);
          param1Graphics.drawLine(1, 3, 1, 8);
          param1Graphics.drawLine(9, 2, 9, 7);
          param1Graphics.drawLine(2, 2, 2, 2);
          param1Graphics.drawLine(8, 8, 8, 8);
          param1Graphics.setColor(MetalLookAndFeel.getControlInfo());
          param1Graphics.drawLine(2, 0, 6, 0);
          param1Graphics.drawLine(2, 8, 6, 8);
          param1Graphics.drawLine(0, 2, 0, 6);
          param1Graphics.drawLine(8, 2, 8, 6);
          param1Graphics.drawLine(1, 1, 1, 1);
          param1Graphics.drawLine(7, 1, 7, 1);
          param1Graphics.drawLine(1, 7, 1, 7);
          param1Graphics.drawLine(7, 7, 7, 7);
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getControlHighlight());
          param1Graphics.drawLine(3, 1, 8, 1);
          param1Graphics.drawLine(2, 9, 7, 9);
          param1Graphics.drawLine(1, 3, 1, 8);
          param1Graphics.drawLine(9, 2, 9, 7);
          param1Graphics.drawLine(2, 2, 2, 2);
          param1Graphics.drawLine(8, 8, 8, 8);
          param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
          param1Graphics.drawLine(2, 0, 6, 0);
          param1Graphics.drawLine(2, 8, 6, 8);
          param1Graphics.drawLine(0, 2, 0, 6);
          param1Graphics.drawLine(8, 2, 8, 6);
          param1Graphics.drawLine(1, 1, 1, 1);
          param1Graphics.drawLine(7, 1, 7, 1);
          param1Graphics.drawLine(1, 7, 1, 7);
          param1Graphics.drawLine(7, 7, 7, 7);
        } 
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        param1Graphics.drawLine(2, 0, 6, 0);
        param1Graphics.drawLine(2, 8, 6, 8);
        param1Graphics.drawLine(0, 2, 0, 6);
        param1Graphics.drawLine(8, 2, 8, 6);
        param1Graphics.drawLine(1, 1, 1, 1);
        param1Graphics.drawLine(7, 1, 7, 1);
        param1Graphics.drawLine(1, 7, 1, 7);
        param1Graphics.drawLine(7, 7, 7, 7);
      } 
      if (bool1) {
        if (bool2) {
          if (buttonModel.isArmed() || (param1Component instanceof javax.swing.JMenu && buttonModel.isSelected())) {
            param1Graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
          } else {
            param1Graphics.setColor(jMenuItem.getForeground());
          } 
        } else {
          param1Graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
        } 
        param1Graphics.drawLine(3, 2, 5, 2);
        param1Graphics.drawLine(2, 3, 6, 3);
        param1Graphics.drawLine(2, 4, 6, 4);
        param1Graphics.drawLine(2, 5, 6, 5);
        param1Graphics.drawLine(3, 6, 5, 6);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return menuCheckIconSize.width; }
    
    public int getIconHeight() { return menuCheckIconSize.height; }
  }
  
  private static class TreeComputerIcon implements Icon, UIResource, Serializable {
    private TreeComputerIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.fillRect(5, 4, 6, 4);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(2, 2, 2, 8);
      param1Graphics.drawLine(13, 2, 13, 8);
      param1Graphics.drawLine(3, 1, 12, 1);
      param1Graphics.drawLine(12, 9, 12, 9);
      param1Graphics.drawLine(3, 9, 3, 9);
      param1Graphics.drawLine(4, 4, 4, 7);
      param1Graphics.drawLine(5, 3, 10, 3);
      param1Graphics.drawLine(11, 4, 11, 7);
      param1Graphics.drawLine(5, 8, 10, 8);
      param1Graphics.drawLine(1, 10, 14, 10);
      param1Graphics.drawLine(14, 10, 14, 14);
      param1Graphics.drawLine(1, 14, 14, 14);
      param1Graphics.drawLine(1, 10, 1, 14);
      param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      param1Graphics.drawLine(6, 12, 8, 12);
      param1Graphics.drawLine(10, 12, 12, 12);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 16; }
    
    public int getIconHeight() { return 16; }
  }
  
  public static class TreeControlIcon implements Icon, Serializable {
    protected boolean isLight;
    
    MetalIconFactory.ImageCacher imageCacher;
    
    boolean cachedOrientation = true;
    
    public TreeControlIcon(boolean param1Boolean) { this.isLight = param1Boolean; }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      GraphicsConfiguration graphicsConfiguration = param1Component.getGraphicsConfiguration();
      if (this.imageCacher == null)
        this.imageCacher = new MetalIconFactory.ImageCacher(); 
      Image image = this.imageCacher.getImage(graphicsConfiguration);
      if (image == null || this.cachedOrientation != MetalUtils.isLeftToRight(param1Component)) {
        this.cachedOrientation = MetalUtils.isLeftToRight(param1Component);
        if (graphicsConfiguration != null) {
          image = graphicsConfiguration.createCompatibleImage(getIconWidth(), getIconHeight(), 2);
        } else {
          image = new BufferedImage(getIconWidth(), getIconHeight(), 2);
        } 
        Graphics graphics = image.getGraphics();
        paintMe(param1Component, graphics, param1Int1, param1Int2);
        graphics.dispose();
        this.imageCacher.cacheImage(image, graphicsConfiguration);
      } 
      if (MetalUtils.isLeftToRight(param1Component)) {
        if (this.isLight) {
          param1Graphics.drawImage(image, param1Int1 + 5, param1Int2 + 3, param1Int1 + 18, param1Int2 + 13, 4, 3, 17, 13, null);
        } else {
          param1Graphics.drawImage(image, param1Int1 + 5, param1Int2 + 3, param1Int1 + 18, param1Int2 + 17, 4, 3, 17, 17, null);
        } 
      } else if (this.isLight) {
        param1Graphics.drawImage(image, param1Int1 + 3, param1Int2 + 3, param1Int1 + 16, param1Int2 + 13, 4, 3, 17, 13, null);
      } else {
        param1Graphics.drawImage(image, param1Int1 + 3, param1Int2 + 3, param1Int1 + 16, param1Int2 + 17, 4, 3, 17, 17, null);
      } 
    }
    
    public void paintMe(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      boolean bool = MetalUtils.isLeftToRight(param1Component) ? 0 : 4;
      param1Graphics.drawLine(bool + 4, 6, bool + 4, 9);
      param1Graphics.drawLine(bool + 5, 5, bool + 5, 5);
      param1Graphics.drawLine(bool + 6, 4, bool + 9, 4);
      param1Graphics.drawLine(bool + 10, 5, bool + 10, 5);
      param1Graphics.drawLine(bool + 11, 6, bool + 11, 9);
      param1Graphics.drawLine(bool + 10, 10, bool + 10, 10);
      param1Graphics.drawLine(bool + 6, 11, bool + 9, 11);
      param1Graphics.drawLine(bool + 5, 10, bool + 5, 10);
      param1Graphics.drawLine(bool + 7, 7, bool + 8, 7);
      param1Graphics.drawLine(bool + 7, 8, bool + 8, 8);
      if (this.isLight) {
        if (MetalUtils.isLeftToRight(param1Component)) {
          param1Graphics.drawLine(12, 7, 15, 7);
          param1Graphics.drawLine(12, 8, 15, 8);
        } else {
          param1Graphics.drawLine(4, 7, 7, 7);
          param1Graphics.drawLine(4, 8, 7, 8);
        } 
      } else {
        param1Graphics.drawLine(bool + 7, 12, bool + 7, 15);
        param1Graphics.drawLine(bool + 8, 12, bool + 8, 15);
      } 
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      param1Graphics.drawLine(bool + 5, 6, bool + 5, 9);
      param1Graphics.drawLine(bool + 6, 5, bool + 9, 5);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
      param1Graphics.drawLine(bool + 6, 6, bool + 6, 6);
      param1Graphics.drawLine(bool + 9, 6, bool + 9, 6);
      param1Graphics.drawLine(bool + 6, 9, bool + 6, 9);
      param1Graphics.drawLine(bool + 10, 6, bool + 10, 9);
      param1Graphics.drawLine(bool + 6, 10, bool + 9, 10);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.drawLine(bool + 6, 7, bool + 6, 8);
      param1Graphics.drawLine(bool + 7, 6, bool + 8, 6);
      param1Graphics.drawLine(bool + 9, 7, bool + 9, 7);
      param1Graphics.drawLine(bool + 7, 9, bool + 7, 9);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      param1Graphics.drawLine(bool + 8, 9, bool + 9, 9);
      param1Graphics.drawLine(bool + 9, 8, bool + 9, 8);
    }
    
    public int getIconWidth() { return treeControlSize.width; }
    
    public int getIconHeight() { return treeControlSize.height; }
  }
  
  private static class TreeFloppyDriveIcon implements Icon, UIResource, Serializable {
    private TreeFloppyDriveIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControl());
      param1Graphics.fillRect(2, 2, 12, 12);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(1, 1, 13, 1);
      param1Graphics.drawLine(14, 2, 14, 14);
      param1Graphics.drawLine(1, 14, 14, 14);
      param1Graphics.drawLine(1, 1, 1, 14);
      param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      param1Graphics.fillRect(5, 2, 6, 5);
      param1Graphics.drawLine(4, 8, 11, 8);
      param1Graphics.drawLine(3, 9, 3, 13);
      param1Graphics.drawLine(12, 9, 12, 13);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
      param1Graphics.fillRect(8, 3, 2, 3);
      param1Graphics.fillRect(4, 9, 8, 5);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
      param1Graphics.drawLine(5, 10, 9, 10);
      param1Graphics.drawLine(5, 12, 8, 12);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 16; }
    
    public int getIconHeight() { return 16; }
  }
  
  public static class TreeFolderIcon extends FolderIcon16 {
    public int getShift() { return -1; }
    
    public int getAdditionalHeight() { return 2; }
  }
  
  private static class TreeHardDriveIcon implements Icon, UIResource, Serializable {
    private TreeHardDriveIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      param1Graphics.drawLine(1, 4, 1, 5);
      param1Graphics.drawLine(2, 3, 3, 3);
      param1Graphics.drawLine(4, 2, 11, 2);
      param1Graphics.drawLine(12, 3, 13, 3);
      param1Graphics.drawLine(14, 4, 14, 5);
      param1Graphics.drawLine(12, 6, 13, 6);
      param1Graphics.drawLine(4, 7, 11, 7);
      param1Graphics.drawLine(2, 6, 3, 6);
      param1Graphics.drawLine(1, 7, 1, 8);
      param1Graphics.drawLine(2, 9, 3, 9);
      param1Graphics.drawLine(4, 10, 11, 10);
      param1Graphics.drawLine(12, 9, 13, 9);
      param1Graphics.drawLine(14, 7, 14, 8);
      param1Graphics.drawLine(1, 10, 1, 11);
      param1Graphics.drawLine(2, 12, 3, 12);
      param1Graphics.drawLine(4, 13, 11, 13);
      param1Graphics.drawLine(12, 12, 13, 12);
      param1Graphics.drawLine(14, 10, 14, 11);
      param1Graphics.setColor(MetalLookAndFeel.getControlShadow());
      param1Graphics.drawLine(7, 6, 7, 6);
      param1Graphics.drawLine(9, 6, 9, 6);
      param1Graphics.drawLine(10, 5, 10, 5);
      param1Graphics.drawLine(11, 6, 11, 6);
      param1Graphics.drawLine(12, 5, 13, 5);
      param1Graphics.drawLine(13, 4, 13, 4);
      param1Graphics.drawLine(7, 9, 7, 9);
      param1Graphics.drawLine(9, 9, 9, 9);
      param1Graphics.drawLine(10, 8, 10, 8);
      param1Graphics.drawLine(11, 9, 11, 9);
      param1Graphics.drawLine(12, 8, 13, 8);
      param1Graphics.drawLine(13, 7, 13, 7);
      param1Graphics.drawLine(7, 12, 7, 12);
      param1Graphics.drawLine(9, 12, 9, 12);
      param1Graphics.drawLine(10, 11, 10, 11);
      param1Graphics.drawLine(11, 12, 11, 12);
      param1Graphics.drawLine(12, 11, 13, 11);
      param1Graphics.drawLine(13, 10, 13, 10);
      param1Graphics.setColor(MetalLookAndFeel.getControlHighlight());
      param1Graphics.drawLine(4, 3, 5, 3);
      param1Graphics.drawLine(7, 3, 9, 3);
      param1Graphics.drawLine(11, 3, 11, 3);
      param1Graphics.drawLine(2, 4, 6, 4);
      param1Graphics.drawLine(8, 4, 8, 4);
      param1Graphics.drawLine(2, 5, 3, 5);
      param1Graphics.drawLine(4, 6, 4, 6);
      param1Graphics.drawLine(2, 7, 3, 7);
      param1Graphics.drawLine(2, 8, 3, 8);
      param1Graphics.drawLine(4, 9, 4, 9);
      param1Graphics.drawLine(2, 10, 3, 10);
      param1Graphics.drawLine(2, 11, 3, 11);
      param1Graphics.drawLine(4, 12, 4, 12);
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 16; }
    
    public int getIconHeight() { return 16; }
  }
  
  public static class TreeLeafIcon extends FileIcon16 {
    public int getShift() { return 2; }
    
    public int getAdditionalHeight() { return 4; }
  }
  
  private static class VerticalSliderThumbIcon implements Icon, Serializable, UIResource {
    protected static MetalBumps controlBumps;
    
    protected static MetalBumps primaryBumps;
    
    public VerticalSliderThumbIcon() {
      controlBumps = new MetalBumps(6, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlInfo(), MetalLookAndFeel.getControl());
      primaryBumps = new MetalBumps(6, 10, MetalLookAndFeel.getPrimaryControl(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlShadow());
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      boolean bool = MetalUtils.isLeftToRight(param1Component);
      param1Graphics.translate(param1Int1, param1Int2);
      if (param1Component.hasFocus()) {
        param1Graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
      } else {
        param1Graphics.setColor(param1Component.isEnabled() ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
      } 
      if (bool) {
        param1Graphics.drawLine(1, 0, 8, 0);
        param1Graphics.drawLine(0, 1, 0, 13);
        param1Graphics.drawLine(1, 14, 8, 14);
        param1Graphics.drawLine(9, 1, 15, 7);
        param1Graphics.drawLine(9, 13, 15, 7);
      } else {
        param1Graphics.drawLine(7, 0, 14, 0);
        param1Graphics.drawLine(15, 1, 15, 13);
        param1Graphics.drawLine(7, 14, 14, 14);
        param1Graphics.drawLine(0, 7, 6, 1);
        param1Graphics.drawLine(0, 7, 6, 13);
      } 
      if (param1Component.hasFocus()) {
        param1Graphics.setColor(param1Component.getForeground());
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getControl());
      } 
      if (bool) {
        param1Graphics.fillRect(1, 1, 8, 13);
        param1Graphics.drawLine(9, 2, 9, 12);
        param1Graphics.drawLine(10, 3, 10, 11);
        param1Graphics.drawLine(11, 4, 11, 10);
        param1Graphics.drawLine(12, 5, 12, 9);
        param1Graphics.drawLine(13, 6, 13, 8);
        param1Graphics.drawLine(14, 7, 14, 7);
      } else {
        param1Graphics.fillRect(7, 1, 8, 13);
        param1Graphics.drawLine(6, 3, 6, 12);
        param1Graphics.drawLine(5, 4, 5, 11);
        param1Graphics.drawLine(4, 5, 4, 10);
        param1Graphics.drawLine(3, 6, 3, 9);
        param1Graphics.drawLine(2, 7, 2, 8);
      } 
      byte b = bool ? 2 : 8;
      if (param1Component.isEnabled())
        if (param1Component.hasFocus()) {
          primaryBumps.paintIcon(param1Component, param1Graphics, b, 2);
        } else {
          controlBumps.paintIcon(param1Component, param1Graphics, b, 2);
        }  
      if (param1Component.isEnabled()) {
        param1Graphics.setColor(param1Component.hasFocus() ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControlHighlight());
        if (bool) {
          param1Graphics.drawLine(1, 1, 8, 1);
          param1Graphics.drawLine(1, 1, 1, 13);
        } else {
          param1Graphics.drawLine(8, 1, 14, 1);
          param1Graphics.drawLine(1, 7, 7, 1);
        } 
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public int getIconWidth() { return 16; }
    
    public int getIconHeight() { return 15; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */