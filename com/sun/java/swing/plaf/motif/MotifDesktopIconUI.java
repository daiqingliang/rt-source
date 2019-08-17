package com.sun.java.swing.plaf.motif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyVetoException;
import java.util.EventListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;
import sun.awt.AWTAccessor;
import sun.swing.SwingUtilities2;

public class MotifDesktopIconUI extends BasicDesktopIconUI {
  protected DesktopIconActionListener desktopIconActionListener;
  
  protected DesktopIconMouseListener desktopIconMouseListener;
  
  protected Icon defaultIcon;
  
  protected IconButton iconButton;
  
  protected IconLabel iconLabel;
  
  private MotifInternalFrameTitlePane sysMenuTitlePane;
  
  JPopupMenu systemMenu;
  
  EventListener mml;
  
  static final int LABEL_HEIGHT = 18;
  
  static final int LABEL_DIVIDER = 4;
  
  static final Font defaultTitleFont = new Font("SansSerif", 0, 12);
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifDesktopIconUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    setDefaultIcon(UIManager.getIcon("DesktopIcon.icon"));
    this.iconButton = createIconButton(this.defaultIcon);
    this.sysMenuTitlePane = new MotifInternalFrameTitlePane(this.frame);
    this.systemMenu = this.sysMenuTitlePane.getSystemMenu();
    MotifBorders.FrameBorder frameBorder = new MotifBorders.FrameBorder(this.desktopIcon);
    this.desktopIcon.setLayout(new BorderLayout());
    this.iconButton.setBorder(frameBorder);
    this.desktopIcon.add(this.iconButton, "Center");
    this.iconLabel = createIconLabel(this.frame);
    this.iconLabel.setBorder(frameBorder);
    this.desktopIcon.add(this.iconLabel, "South");
    this.desktopIcon.setSize(this.desktopIcon.getPreferredSize());
    this.desktopIcon.validate();
    JLayeredPane.putLayer(this.desktopIcon, JLayeredPane.getLayer(this.frame));
  }
  
  protected void installComponents() {}
  
  protected void uninstallComponents() {}
  
  protected void installListeners() {
    super.installListeners();
    this.desktopIconActionListener = createDesktopIconActionListener();
    this.desktopIconMouseListener = createDesktopIconMouseListener();
    this.iconButton.addActionListener(this.desktopIconActionListener);
    this.iconButton.addMouseListener(this.desktopIconMouseListener);
    this.iconLabel.addMouseListener(this.desktopIconMouseListener);
  }
  
  JInternalFrame.JDesktopIcon getDesktopIcon() { return this.desktopIcon; }
  
  void setDesktopIcon(JInternalFrame.JDesktopIcon paramJDesktopIcon) { this.desktopIcon = paramJDesktopIcon; }
  
  JInternalFrame getFrame() { return this.frame; }
  
  void setFrame(JInternalFrame paramJInternalFrame) { this.frame = paramJInternalFrame; }
  
  protected void showSystemMenu() { this.systemMenu.show(this.iconButton, 0, getDesktopIcon().getHeight()); }
  
  protected void hideSystemMenu() { this.systemMenu.setVisible(false); }
  
  protected IconLabel createIconLabel(JInternalFrame paramJInternalFrame) { return new IconLabel(paramJInternalFrame); }
  
  protected IconButton createIconButton(Icon paramIcon) { return new IconButton(paramIcon); }
  
  protected DesktopIconActionListener createDesktopIconActionListener() { return new DesktopIconActionListener(); }
  
  protected DesktopIconMouseListener createDesktopIconMouseListener() { return new DesktopIconMouseListener(); }
  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    this.desktopIcon.setLayout(null);
    this.desktopIcon.remove(this.iconButton);
    this.desktopIcon.remove(this.iconLabel);
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.iconButton.removeActionListener(this.desktopIconActionListener);
    this.iconButton.removeMouseListener(this.desktopIconMouseListener);
    this.sysMenuTitlePane.uninstallListeners();
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    JInternalFrame jInternalFrame = this.desktopIcon.getInternalFrame();
    int i = this.defaultIcon.getIconWidth();
    int j = this.defaultIcon.getIconHeight() + 18 + 4;
    Border border = jInternalFrame.getBorder();
    if (border != null) {
      i += (border.getBorderInsets(jInternalFrame)).left + (border.getBorderInsets(jInternalFrame)).right;
      j += (border.getBorderInsets(jInternalFrame)).bottom + (border.getBorderInsets(jInternalFrame)).top;
    } 
    return new Dimension(i, j);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return getMinimumSize(paramJComponent); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return getMinimumSize(paramJComponent); }
  
  public Icon getDefaultIcon() { return this.defaultIcon; }
  
  public void setDefaultIcon(Icon paramIcon) { this.defaultIcon = paramIcon; }
  
  protected class DesktopIconActionListener implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) { MotifDesktopIconUI.this.systemMenu.show(MotifDesktopIconUI.this.iconButton, 0, MotifDesktopIconUI.this.getDesktopIcon().getHeight()); }
  }
  
  protected class DesktopIconMouseListener extends MouseAdapter {
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getClickCount() > 1) {
        try {
          MotifDesktopIconUI.this.getFrame().setIcon(false);
        } catch (PropertyVetoException propertyVetoException) {}
        MotifDesktopIconUI.this.systemMenu.setVisible(false);
        MotifDesktopIconUI.this.getFrame().getDesktopPane().getDesktopManager().endDraggingFrame((JComponent)param1MouseEvent.getSource());
      } 
    }
  }
  
  protected class IconButton extends JButton {
    Icon icon;
    
    IconButton(Icon param1Icon) {
      super(param1Icon);
      this.icon = param1Icon;
      addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconButton.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseMoved(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconButton.this.forwardEventToParent(param2MouseEvent); }
          });
      addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconButton.this.forwardEventToParent(param2MouseEvent); }
            
            public void mousePressed(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconButton.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseReleased(MouseEvent param2MouseEvent) {
              if (!MotifDesktopIconUI.this.systemMenu.isShowing())
                MotifDesktopIconUI.IconButton.this.forwardEventToParent(param2MouseEvent); 
            }
            
            public void mouseEntered(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconButton.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseExited(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconButton.this.forwardEventToParent(param2MouseEvent); }
          });
    }
    
    void forwardEventToParent(MouseEvent param1MouseEvent) {
      MouseEvent mouseEvent = new MouseEvent(getParent(), param1MouseEvent.getID(), param1MouseEvent.getWhen(), param1MouseEvent.getModifiers(), param1MouseEvent.getX(), param1MouseEvent.getY(), param1MouseEvent.getXOnScreen(), param1MouseEvent.getYOnScreen(), param1MouseEvent.getClickCount(), param1MouseEvent.isPopupTrigger(), 0);
      AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
      mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(param1MouseEvent));
      getParent().dispatchEvent(mouseEvent);
    }
    
    public boolean isFocusTraversable() { return false; }
  }
  
  protected class IconLabel extends JPanel {
    JInternalFrame frame;
    
    IconLabel(JInternalFrame param1JInternalFrame) {
      this.frame = param1JInternalFrame;
      setFont(MotifDesktopIconUI.defaultTitleFont);
      addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconLabel.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseMoved(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconLabel.this.forwardEventToParent(param2MouseEvent); }
          });
      addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconLabel.this.forwardEventToParent(param2MouseEvent); }
            
            public void mousePressed(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconLabel.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseReleased(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconLabel.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseEntered(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconLabel.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseExited(MouseEvent param2MouseEvent) { MotifDesktopIconUI.IconLabel.this.forwardEventToParent(param2MouseEvent); }
          });
    }
    
    void forwardEventToParent(MouseEvent param1MouseEvent) {
      MouseEvent mouseEvent = new MouseEvent(getParent(), param1MouseEvent.getID(), param1MouseEvent.getWhen(), param1MouseEvent.getModifiers(), param1MouseEvent.getX(), param1MouseEvent.getY(), param1MouseEvent.getXOnScreen(), param1MouseEvent.getYOnScreen(), param1MouseEvent.getClickCount(), param1MouseEvent.isPopupTrigger(), 0);
      AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
      mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(param1MouseEvent));
      getParent().dispatchEvent(mouseEvent);
    }
    
    public boolean isFocusTraversable() { return false; }
    
    public Dimension getMinimumSize() { return new Dimension(MotifDesktopIconUI.this.defaultIcon.getIconWidth() + 1, 22); }
    
    public Dimension getPreferredSize() {
      String str = this.frame.getTitle();
      FontMetrics fontMetrics = this.frame.getFontMetrics(MotifDesktopIconUI.defaultTitleFont);
      int i = 4;
      if (str != null)
        i += SwingUtilities2.stringWidth(this.frame, fontMetrics, str); 
      return new Dimension(i, 22);
    }
    
    public void paint(Graphics param1Graphics) {
      super.paint(param1Graphics);
      int i = getWidth() - 1;
      Color color = UIManager.getColor("inactiveCaptionBorder").darker().darker();
      param1Graphics.setColor(color);
      param1Graphics.setClip(0, 0, getWidth(), getHeight());
      param1Graphics.drawLine(i - 1, 1, i - 1, 1);
      param1Graphics.drawLine(i, 0, i, 0);
      param1Graphics.setColor(UIManager.getColor("inactiveCaption"));
      param1Graphics.fillRect(2, 1, i - 3, 19);
      param1Graphics.setClip(2, 1, i - 4, 18);
      int j = 18 - SwingUtilities2.getFontMetrics(this.frame, param1Graphics).getDescent();
      param1Graphics.setColor(UIManager.getColor("inactiveCaptionText"));
      String str = this.frame.getTitle();
      if (str != null)
        SwingUtilities2.drawString(this.frame, param1Graphics, str, 4, j); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */