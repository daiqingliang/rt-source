package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.awt.AWTAccessor;

public class MotifInternalFrameTitlePane extends BasicInternalFrameTitlePane implements LayoutManager, ActionListener, PropertyChangeListener {
  SystemButton systemButton;
  
  MinimizeButton minimizeButton;
  
  MaximizeButton maximizeButton;
  
  JPopupMenu systemMenu;
  
  Title title;
  
  Color color;
  
  Color highlight;
  
  Color shadow;
  
  public static final int BUTTON_SIZE = 19;
  
  static Dimension buttonDimension = new Dimension(19, 19);
  
  public MotifInternalFrameTitlePane(JInternalFrame paramJInternalFrame) { super(paramJInternalFrame); }
  
  protected void installDefaults() {
    setFont(UIManager.getFont("InternalFrame.titleFont"));
    setPreferredSize(new Dimension(100, 19));
  }
  
  protected void uninstallListeners() { super.uninstallListeners(); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return this; }
  
  protected LayoutManager createLayout() { return this; }
  
  JPopupMenu getSystemMenu() { return this.systemMenu; }
  
  protected void assembleSystemMenu() {
    this.systemMenu = new JPopupMenu();
    JMenuItem jMenuItem = this.systemMenu.add(this.restoreAction);
    jMenuItem.setMnemonic(getButtonMnemonic("restore"));
    jMenuItem = this.systemMenu.add(this.moveAction);
    jMenuItem.setMnemonic(getButtonMnemonic("move"));
    jMenuItem = this.systemMenu.add(this.sizeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("size"));
    jMenuItem = this.systemMenu.add(this.iconifyAction);
    jMenuItem.setMnemonic(getButtonMnemonic("minimize"));
    jMenuItem = this.systemMenu.add(this.maximizeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("maximize"));
    this.systemMenu.add(new JSeparator());
    jMenuItem = this.systemMenu.add(this.closeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("close"));
    this.systemButton = new SystemButton(null);
    this.systemButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) { MotifInternalFrameTitlePane.this.systemMenu.show(MotifInternalFrameTitlePane.this.systemButton, 0, 19); }
        });
    this.systemButton.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent param1MouseEvent) {
            try {
              MotifInternalFrameTitlePane.this.frame.setSelected(true);
            } catch (PropertyVetoException propertyVetoException) {}
            if (param1MouseEvent.getClickCount() == 2) {
              MotifInternalFrameTitlePane.this.closeAction.actionPerformed(new ActionEvent(param1MouseEvent.getSource(), 1001, null, param1MouseEvent.getWhen(), 0));
              MotifInternalFrameTitlePane.this.systemMenu.setVisible(false);
            } 
          }
        });
  }
  
  private static int getButtonMnemonic(String paramString) {
    try {
      return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + paramString + "Button.mnemonic"));
    } catch (NumberFormatException numberFormatException) {
      return -1;
    } 
  }
  
  protected void createButtons() {
    this.minimizeButton = new MinimizeButton(null);
    this.minimizeButton.addActionListener(this.iconifyAction);
    this.maximizeButton = new MaximizeButton(null);
    this.maximizeButton.addActionListener(this.maximizeAction);
  }
  
  protected void addSubComponents() {
    this.title = new Title(this.frame.getTitle());
    this.title.setFont(getFont());
    add(this.systemButton);
    add(this.title);
    add(this.minimizeButton);
    add(this.maximizeButton);
  }
  
  public void paintComponent(Graphics paramGraphics) {}
  
  void setColors(Color paramColor1, Color paramColor2, Color paramColor3) {
    this.color = paramColor1;
    this.highlight = paramColor2;
    this.shadow = paramColor3;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {}
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    JInternalFrame jInternalFrame = (JInternalFrame)paramPropertyChangeEvent.getSource();
    boolean bool = false;
    if ("selected".equals(str)) {
      repaint();
    } else if (str.equals("maximizable")) {
      if ((Boolean)paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
        add(this.maximizeButton);
      } else {
        remove(this.maximizeButton);
      } 
      revalidate();
      repaint();
    } else if (str.equals("iconable")) {
      if ((Boolean)paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
        add(this.minimizeButton);
      } else {
        remove(this.minimizeButton);
      } 
      revalidate();
      repaint();
    } else if (str.equals("title")) {
      repaint();
    } 
    enableActions();
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer) { return minimumLayoutSize(paramContainer); }
  
  public Dimension minimumLayoutSize(Container paramContainer) { return new Dimension(100, 19); }
  
  public void layoutContainer(Container paramContainer) {
    int i = getWidth();
    this.systemButton.setBounds(0, 0, 19, 19);
    int j = i - 19;
    if (this.frame.isMaximizable()) {
      this.maximizeButton.setBounds(j, 0, 19, 19);
      j -= 19;
    } else if (this.maximizeButton.getParent() != null) {
      this.maximizeButton.getParent().remove(this.maximizeButton);
    } 
    if (this.frame.isIconifiable()) {
      this.minimizeButton.setBounds(j, 0, 19, 19);
      j -= 19;
    } else if (this.minimizeButton.getParent() != null) {
      this.minimizeButton.getParent().remove(this.minimizeButton);
    } 
    this.title.setBounds(19, 0, j, 19);
  }
  
  protected void showSystemMenu() { this.systemMenu.show(this.systemButton, 0, 19); }
  
  protected void hideSystemMenu() { this.systemMenu.setVisible(false); }
  
  private abstract class FrameButton extends JButton {
    FrameButton() {
      setFocusPainted(false);
      setBorderPainted(false);
    }
    
    public boolean isFocusTraversable() { return false; }
    
    public void requestFocus() {}
    
    public Dimension getMinimumSize() { return MotifInternalFrameTitlePane.buttonDimension; }
    
    public Dimension getPreferredSize() { return MotifInternalFrameTitlePane.buttonDimension; }
    
    public void paintComponent(Graphics param1Graphics) {
      Dimension dimension = getSize();
      int i = dimension.width - 1;
      int j = dimension.height - 1;
      param1Graphics.setColor(MotifInternalFrameTitlePane.this.color);
      param1Graphics.fillRect(1, 1, dimension.width, dimension.height);
      boolean bool = getModel().isPressed();
      param1Graphics.setColor(bool ? MotifInternalFrameTitlePane.this.shadow : MotifInternalFrameTitlePane.this.highlight);
      param1Graphics.drawLine(0, 0, i, 0);
      param1Graphics.drawLine(0, 0, 0, j);
      param1Graphics.setColor(bool ? MotifInternalFrameTitlePane.this.highlight : MotifInternalFrameTitlePane.this.shadow);
      param1Graphics.drawLine(1, j, i, j);
      param1Graphics.drawLine(i, 1, i, j);
    }
  }
  
  private class MaximizeButton extends FrameButton {
    private MaximizeButton() { super(MotifInternalFrameTitlePane.this); }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      byte b = 14;
      boolean bool = MotifInternalFrameTitlePane.this.frame.isMaximum();
      param1Graphics.setColor(bool ? MotifInternalFrameTitlePane.this.shadow : MotifInternalFrameTitlePane.this.highlight);
      param1Graphics.drawLine(4, 4, 4, b);
      param1Graphics.drawLine(4, 4, b, 4);
      param1Graphics.setColor(bool ? MotifInternalFrameTitlePane.this.highlight : MotifInternalFrameTitlePane.this.shadow);
      param1Graphics.drawLine(5, b, b, b);
      param1Graphics.drawLine(b, 5, b, b);
    }
  }
  
  private class MinimizeButton extends FrameButton {
    private MinimizeButton() { super(MotifInternalFrameTitlePane.this); }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      param1Graphics.setColor(MotifInternalFrameTitlePane.this.highlight);
      param1Graphics.drawLine(7, 8, 7, 11);
      param1Graphics.drawLine(7, 8, 10, 8);
      param1Graphics.setColor(MotifInternalFrameTitlePane.this.shadow);
      param1Graphics.drawLine(8, 11, 10, 11);
      param1Graphics.drawLine(11, 9, 11, 11);
    }
  }
  
  private class SystemButton extends FrameButton {
    private SystemButton() { super(MotifInternalFrameTitlePane.this); }
    
    public boolean isFocusTraversable() { return false; }
    
    public void requestFocus() {}
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      param1Graphics.setColor(MotifInternalFrameTitlePane.this.highlight);
      param1Graphics.drawLine(4, 8, 4, 11);
      param1Graphics.drawLine(4, 8, 14, 8);
      param1Graphics.setColor(MotifInternalFrameTitlePane.this.shadow);
      param1Graphics.drawLine(5, 11, 14, 11);
      param1Graphics.drawLine(14, 9, 14, 11);
    }
  }
  
  private class Title extends FrameButton {
    Title(String param1String) {
      super(MotifInternalFrameTitlePane.this);
      setText(param1String);
      setHorizontalAlignment(0);
      setBorder(BorderFactory.createBevelBorder(0, UIManager.getColor("activeCaptionBorder"), UIManager.getColor("inactiveCaptionBorder")));
      addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent param2MouseEvent) { MotifInternalFrameTitlePane.Title.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseMoved(MouseEvent param2MouseEvent) { MotifInternalFrameTitlePane.Title.this.forwardEventToParent(param2MouseEvent); }
          });
      addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent param2MouseEvent) { MotifInternalFrameTitlePane.Title.this.forwardEventToParent(param2MouseEvent); }
            
            public void mousePressed(MouseEvent param2MouseEvent) { MotifInternalFrameTitlePane.Title.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseReleased(MouseEvent param2MouseEvent) { MotifInternalFrameTitlePane.Title.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseEntered(MouseEvent param2MouseEvent) { MotifInternalFrameTitlePane.Title.this.forwardEventToParent(param2MouseEvent); }
            
            public void mouseExited(MouseEvent param2MouseEvent) { MotifInternalFrameTitlePane.Title.this.forwardEventToParent(param2MouseEvent); }
          });
    }
    
    void forwardEventToParent(MouseEvent param1MouseEvent) {
      MouseEvent mouseEvent = new MouseEvent(getParent(), param1MouseEvent.getID(), param1MouseEvent.getWhen(), param1MouseEvent.getModifiers(), param1MouseEvent.getX(), param1MouseEvent.getY(), param1MouseEvent.getXOnScreen(), param1MouseEvent.getYOnScreen(), param1MouseEvent.getClickCount(), param1MouseEvent.isPopupTrigger(), 0);
      AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
      mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(param1MouseEvent));
      getParent().dispatchEvent(mouseEvent);
    }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      if (MotifInternalFrameTitlePane.this.frame.isSelected()) {
        param1Graphics.setColor(UIManager.getColor("activeCaptionText"));
      } else {
        param1Graphics.setColor(UIManager.getColor("inactiveCaptionText"));
      } 
      Dimension dimension = getSize();
      String str = MotifInternalFrameTitlePane.this.frame.getTitle();
      if (str != null)
        MotifGraphicsUtils.drawStringInRect(MotifInternalFrameTitlePane.this.frame, param1Graphics, str, 0, 0, dimension.width, dimension.height, 0); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */