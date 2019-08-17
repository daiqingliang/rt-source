package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import sun.swing.DefaultLookup;

public class BasicSplitPaneDivider extends Container implements PropertyChangeListener {
  protected static final int ONE_TOUCH_SIZE = 6;
  
  protected static final int ONE_TOUCH_OFFSET = 2;
  
  protected DragController dragger;
  
  protected BasicSplitPaneUI splitPaneUI;
  
  protected int dividerSize = 0;
  
  protected Component hiddenDivider;
  
  protected JSplitPane splitPane;
  
  protected MouseHandler mouseHandler;
  
  protected int orientation;
  
  protected JButton leftButton;
  
  protected JButton rightButton;
  
  private Border border;
  
  private boolean mouseOver;
  
  private int oneTouchSize;
  
  private int oneTouchOffset;
  
  private boolean centerOneTouchButtons;
  
  public BasicSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI) {
    this.oneTouchSize = DefaultLookup.getInt(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.oneTouchButtonSize", 6);
    this.oneTouchOffset = DefaultLookup.getInt(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.oneTouchButtonOffset", 2);
    this.centerOneTouchButtons = DefaultLookup.getBoolean(paramBasicSplitPaneUI.getSplitPane(), paramBasicSplitPaneUI, "SplitPane.centerOneTouchButtons", true);
    setLayout(new DividerLayout());
    setBasicSplitPaneUI(paramBasicSplitPaneUI);
    this.orientation = this.splitPane.getOrientation();
    setCursor((this.orientation == 1) ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
    setBackground(UIManager.getColor("SplitPane.background"));
  }
  
  private void revalidateSplitPane() {
    invalidate();
    if (this.splitPane != null)
      this.splitPane.revalidate(); 
  }
  
  public void setBasicSplitPaneUI(BasicSplitPaneUI paramBasicSplitPaneUI) {
    if (this.splitPane != null) {
      this.splitPane.removePropertyChangeListener(this);
      if (this.mouseHandler != null) {
        this.splitPane.removeMouseListener(this.mouseHandler);
        this.splitPane.removeMouseMotionListener(this.mouseHandler);
        removeMouseListener(this.mouseHandler);
        removeMouseMotionListener(this.mouseHandler);
        this.mouseHandler = null;
      } 
    } 
    this.splitPaneUI = paramBasicSplitPaneUI;
    if (paramBasicSplitPaneUI != null) {
      this.splitPane = paramBasicSplitPaneUI.getSplitPane();
      if (this.splitPane != null) {
        if (this.mouseHandler == null)
          this.mouseHandler = new MouseHandler(); 
        this.splitPane.addMouseListener(this.mouseHandler);
        this.splitPane.addMouseMotionListener(this.mouseHandler);
        addMouseListener(this.mouseHandler);
        addMouseMotionListener(this.mouseHandler);
        this.splitPane.addPropertyChangeListener(this);
        if (this.splitPane.isOneTouchExpandable())
          oneTouchExpandableChanged(); 
      } 
    } else {
      this.splitPane = null;
    } 
  }
  
  public BasicSplitPaneUI getBasicSplitPaneUI() { return this.splitPaneUI; }
  
  public void setDividerSize(int paramInt) { this.dividerSize = paramInt; }
  
  public int getDividerSize() { return this.dividerSize; }
  
  public void setBorder(Border paramBorder) {
    Border border1 = this.border;
    this.border = paramBorder;
  }
  
  public Border getBorder() { return this.border; }
  
  public Insets getInsets() {
    Border border1 = getBorder();
    return (border1 != null) ? border1.getBorderInsets(this) : super.getInsets();
  }
  
  protected void setMouseOver(boolean paramBoolean) { this.mouseOver = paramBoolean; }
  
  public boolean isMouseOver() { return this.mouseOver; }
  
  public Dimension getPreferredSize() { return (this.orientation == 1) ? new Dimension(getDividerSize(), 1) : new Dimension(1, getDividerSize()); }
  
  public Dimension getMinimumSize() { return getPreferredSize(); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (paramPropertyChangeEvent.getSource() == this.splitPane)
      if (paramPropertyChangeEvent.getPropertyName() == "orientation") {
        this.orientation = this.splitPane.getOrientation();
        setCursor((this.orientation == 1) ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
        revalidateSplitPane();
      } else if (paramPropertyChangeEvent.getPropertyName() == "oneTouchExpandable") {
        oneTouchExpandableChanged();
      }  
  }
  
  public void paint(Graphics paramGraphics) {
    super.paint(paramGraphics);
    Border border1 = getBorder();
    if (border1 != null) {
      Dimension dimension = getSize();
      border1.paintBorder(this, paramGraphics, 0, 0, dimension.width, dimension.height);
    } 
  }
  
  protected void oneTouchExpandableChanged() {
    if (!DefaultLookup.getBoolean(this.splitPane, this.splitPaneUI, "SplitPane.supportsOneTouchButtons", true))
      return; 
    if (this.splitPane.isOneTouchExpandable() && this.leftButton == null && this.rightButton == null) {
      this.leftButton = createLeftOneTouchButton();
      if (this.leftButton != null)
        this.leftButton.addActionListener(new OneTouchActionHandler(true)); 
      this.rightButton = createRightOneTouchButton();
      if (this.rightButton != null)
        this.rightButton.addActionListener(new OneTouchActionHandler(false)); 
      if (this.leftButton != null && this.rightButton != null) {
        add(this.leftButton);
        add(this.rightButton);
      } 
    } 
    revalidateSplitPane();
  }
  
  protected JButton createLeftOneTouchButton() {
    JButton jButton = new JButton() {
        public void setBorder(Border param1Border) {}
        
        public void paint(Graphics param1Graphics) {
          if (BasicSplitPaneDivider.this.splitPane != null) {
            int[] arrayOfInt1 = new int[3];
            int[] arrayOfInt2 = new int[3];
            param1Graphics.setColor(getBackground());
            param1Graphics.fillRect(0, 0, getWidth(), getHeight());
            param1Graphics.setColor(Color.black);
            if (BasicSplitPaneDivider.this.orientation == 0) {
              int i = Math.min(getHeight(), BasicSplitPaneDivider.this.oneTouchSize);
              arrayOfInt1[0] = i;
              arrayOfInt1[1] = 0;
              arrayOfInt1[2] = i << 1;
              arrayOfInt2[0] = 0;
              arrayOfInt2[2] = i;
              arrayOfInt2[1] = i;
              param1Graphics.drawPolygon(arrayOfInt1, arrayOfInt2, 3);
            } else {
              int i = Math.min(getWidth(), BasicSplitPaneDivider.this.oneTouchSize);
              arrayOfInt1[2] = i;
              arrayOfInt1[0] = i;
              arrayOfInt1[1] = 0;
              arrayOfInt2[0] = 0;
              arrayOfInt2[1] = i;
              arrayOfInt2[2] = i << 1;
            } 
            param1Graphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
          } 
        }
        
        public boolean isFocusTraversable() { return false; }
      };
    jButton.setMinimumSize(new Dimension(this.oneTouchSize, this.oneTouchSize));
    jButton.setCursor(Cursor.getPredefinedCursor(0));
    jButton.setFocusPainted(false);
    jButton.setBorderPainted(false);
    jButton.setRequestFocusEnabled(false);
    return jButton;
  }
  
  protected JButton createRightOneTouchButton() {
    JButton jButton = new JButton() {
        public void setBorder(Border param1Border) {}
        
        public void paint(Graphics param1Graphics) {
          if (BasicSplitPaneDivider.this.splitPane != null) {
            int[] arrayOfInt1 = new int[3];
            int[] arrayOfInt2 = new int[3];
            param1Graphics.setColor(getBackground());
            param1Graphics.fillRect(0, 0, getWidth(), getHeight());
            if (BasicSplitPaneDivider.this.orientation == 0) {
              int i = Math.min(getHeight(), BasicSplitPaneDivider.this.oneTouchSize);
              arrayOfInt1[0] = i;
              arrayOfInt1[1] = i << 1;
              arrayOfInt1[2] = 0;
              arrayOfInt2[0] = i;
              arrayOfInt2[2] = 0;
              arrayOfInt2[1] = 0;
            } else {
              int i = Math.min(getWidth(), BasicSplitPaneDivider.this.oneTouchSize);
              arrayOfInt1[2] = 0;
              arrayOfInt1[0] = 0;
              arrayOfInt1[1] = i;
              arrayOfInt2[0] = 0;
              arrayOfInt2[1] = i;
              arrayOfInt2[2] = i << 1;
            } 
            param1Graphics.setColor(Color.black);
            param1Graphics.fillPolygon(arrayOfInt1, arrayOfInt2, 3);
          } 
        }
        
        public boolean isFocusTraversable() { return false; }
      };
    jButton.setMinimumSize(new Dimension(this.oneTouchSize, this.oneTouchSize));
    jButton.setCursor(Cursor.getPredefinedCursor(0));
    jButton.setFocusPainted(false);
    jButton.setBorderPainted(false);
    jButton.setRequestFocusEnabled(false);
    return jButton;
  }
  
  protected void prepareForDragging() { this.splitPaneUI.startDragging(); }
  
  protected void dragDividerTo(int paramInt) { this.splitPaneUI.dragDividerTo(paramInt); }
  
  protected void finishDraggingTo(int paramInt) { this.splitPaneUI.finishDraggingTo(paramInt); }
  
  protected class DividerLayout implements LayoutManager {
    public void layoutContainer(Container param1Container) {
      if (BasicSplitPaneDivider.this.leftButton != null && BasicSplitPaneDivider.this.rightButton != null && param1Container == BasicSplitPaneDivider.this)
        if (BasicSplitPaneDivider.this.splitPane.isOneTouchExpandable()) {
          Insets insets = BasicSplitPaneDivider.this.getInsets();
          if (BasicSplitPaneDivider.this.orientation == 0) {
            int i = (insets != null) ? insets.left : 0;
            int j = BasicSplitPaneDivider.this.getHeight();
            if (insets != null) {
              j -= insets.top + insets.bottom;
              j = Math.max(j, 0);
            } 
            j = Math.min(j, BasicSplitPaneDivider.this.oneTouchSize);
            int k = ((param1Container.getSize()).height - j) / 2;
            if (!BasicSplitPaneDivider.this.centerOneTouchButtons) {
              k = (insets != null) ? insets.top : 0;
              i = 0;
            } 
            BasicSplitPaneDivider.this.leftButton.setBounds(i + BasicSplitPaneDivider.this.oneTouchOffset, k, j * 2, j);
            BasicSplitPaneDivider.this.rightButton.setBounds(i + BasicSplitPaneDivider.this.oneTouchOffset + BasicSplitPaneDivider.this.oneTouchSize * 2, k, j * 2, j);
          } else {
            int i = (insets != null) ? insets.top : 0;
            int j = BasicSplitPaneDivider.this.getWidth();
            if (insets != null) {
              j -= insets.left + insets.right;
              j = Math.max(j, 0);
            } 
            j = Math.min(j, BasicSplitPaneDivider.this.oneTouchSize);
            int k = ((param1Container.getSize()).width - j) / 2;
            if (!BasicSplitPaneDivider.this.centerOneTouchButtons) {
              k = (insets != null) ? insets.left : 0;
              i = 0;
            } 
            BasicSplitPaneDivider.this.leftButton.setBounds(k, i + BasicSplitPaneDivider.this.oneTouchOffset, j, j * 2);
            BasicSplitPaneDivider.this.rightButton.setBounds(k, i + BasicSplitPaneDivider.this.oneTouchOffset + BasicSplitPaneDivider.this.oneTouchSize * 2, j, j * 2);
          } 
        } else {
          BasicSplitPaneDivider.this.leftButton.setBounds(-5, -5, 1, 1);
          BasicSplitPaneDivider.this.rightButton.setBounds(-5, -5, 1, 1);
        }  
    }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      if (param1Container != BasicSplitPaneDivider.this || BasicSplitPaneDivider.this.splitPane == null)
        return new Dimension(0, 0); 
      Dimension dimension = null;
      if (BasicSplitPaneDivider.this.splitPane.isOneTouchExpandable() && BasicSplitPaneDivider.this.leftButton != null)
        dimension = BasicSplitPaneDivider.this.leftButton.getMinimumSize(); 
      Insets insets = BasicSplitPaneDivider.this.getInsets();
      int i = BasicSplitPaneDivider.this.getDividerSize();
      int j = i;
      if (BasicSplitPaneDivider.this.orientation == 0) {
        if (dimension != null) {
          int k = dimension.height;
          if (insets != null)
            k += insets.top + insets.bottom; 
          j = Math.max(j, k);
        } 
        i = 1;
      } else {
        if (dimension != null) {
          int k = dimension.width;
          if (insets != null)
            k += insets.left + insets.right; 
          i = Math.max(i, k);
        } 
        j = 1;
      } 
      return new Dimension(i, j);
    }
    
    public Dimension preferredLayoutSize(Container param1Container) { return minimumLayoutSize(param1Container); }
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
  }
  
  protected class DragController {
    int initialX;
    
    int maxX;
    
    int minX;
    
    int offset;
    
    protected DragController(MouseEvent param1MouseEvent) {
      JSplitPane jSplitPane = BasicSplitPaneDivider.this.splitPaneUI.getSplitPane();
      Component component1 = jSplitPane.getLeftComponent();
      Component component2 = jSplitPane.getRightComponent();
      this.initialX = (this$0.getLocation()).x;
      if (param1MouseEvent.getSource() == this$0) {
        this.offset = param1MouseEvent.getX();
      } else {
        this.offset = param1MouseEvent.getX() - this.initialX;
      } 
      if (component1 == null || component2 == null || this.offset < -1 || this.offset >= (this$0.getSize()).width) {
        this.maxX = -1;
      } else {
        Insets insets = jSplitPane.getInsets();
        if (component1.isVisible()) {
          this.minX = (component1.getMinimumSize()).width;
          if (insets != null)
            this.minX += insets.left; 
        } else {
          this.minX = 0;
        } 
        if (component2.isVisible()) {
          int i = (insets != null) ? insets.right : 0;
          this.maxX = Math.max(0, (jSplitPane.getSize()).width - (this$0.getSize()).width + i - (component2.getMinimumSize()).width);
        } else {
          int i = (insets != null) ? insets.right : 0;
          this.maxX = Math.max(0, (jSplitPane.getSize()).width - (this$0.getSize()).width + i);
        } 
        if (this.maxX < this.minX)
          this.minX = this.maxX = 0; 
      } 
    }
    
    protected boolean isValid() { return (this.maxX > 0); }
    
    protected int positionForMouseEvent(MouseEvent param1MouseEvent) {
      null = (param1MouseEvent.getSource() == BasicSplitPaneDivider.this) ? (param1MouseEvent.getX() + (this.this$0.getLocation()).x) : param1MouseEvent.getX();
      return Math.min(this.maxX, Math.max(this.minX, null - this.offset));
    }
    
    protected int getNeededLocation(int param1Int1, int param1Int2) { return Math.min(this.maxX, Math.max(this.minX, param1Int1 - this.offset)); }
    
    protected void continueDrag(int param1Int1, int param1Int2) { BasicSplitPaneDivider.this.dragDividerTo(getNeededLocation(param1Int1, param1Int2)); }
    
    protected void continueDrag(MouseEvent param1MouseEvent) { BasicSplitPaneDivider.this.dragDividerTo(positionForMouseEvent(param1MouseEvent)); }
    
    protected void completeDrag(int param1Int1, int param1Int2) { BasicSplitPaneDivider.this.finishDraggingTo(getNeededLocation(param1Int1, param1Int2)); }
    
    protected void completeDrag(MouseEvent param1MouseEvent) { BasicSplitPaneDivider.this.finishDraggingTo(positionForMouseEvent(param1MouseEvent)); }
  }
  
  protected class MouseHandler extends MouseAdapter implements MouseMotionListener {
    public void mousePressed(MouseEvent param1MouseEvent) {
      if ((param1MouseEvent.getSource() == BasicSplitPaneDivider.this || param1MouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane) && BasicSplitPaneDivider.this.dragger == null && BasicSplitPaneDivider.this.splitPane.isEnabled()) {
        Component component = BasicSplitPaneDivider.this.splitPaneUI.getNonContinuousLayoutDivider();
        if (BasicSplitPaneDivider.this.hiddenDivider != component) {
          if (BasicSplitPaneDivider.this.hiddenDivider != null) {
            BasicSplitPaneDivider.this.hiddenDivider.removeMouseListener(this);
            BasicSplitPaneDivider.this.hiddenDivider.removeMouseMotionListener(this);
          } 
          BasicSplitPaneDivider.this.hiddenDivider = component;
          if (BasicSplitPaneDivider.this.hiddenDivider != null) {
            BasicSplitPaneDivider.this.hiddenDivider.addMouseMotionListener(this);
            BasicSplitPaneDivider.this.hiddenDivider.addMouseListener(this);
          } 
        } 
        if (BasicSplitPaneDivider.this.splitPane.getLeftComponent() != null && BasicSplitPaneDivider.this.splitPane.getRightComponent() != null) {
          if (BasicSplitPaneDivider.this.orientation == 1) {
            BasicSplitPaneDivider.this.dragger = new BasicSplitPaneDivider.DragController(BasicSplitPaneDivider.this, param1MouseEvent);
          } else {
            BasicSplitPaneDivider.this.dragger = new BasicSplitPaneDivider.VerticalDragController(BasicSplitPaneDivider.this, param1MouseEvent);
          } 
          if (!BasicSplitPaneDivider.this.dragger.isValid()) {
            BasicSplitPaneDivider.this.dragger = null;
          } else {
            BasicSplitPaneDivider.this.prepareForDragging();
            BasicSplitPaneDivider.this.dragger.continueDrag(param1MouseEvent);
          } 
        } 
        param1MouseEvent.consume();
      } 
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (BasicSplitPaneDivider.this.dragger != null) {
        if (param1MouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane) {
          BasicSplitPaneDivider.this.dragger.completeDrag(param1MouseEvent.getX(), param1MouseEvent.getY());
        } else if (param1MouseEvent.getSource() == BasicSplitPaneDivider.this) {
          Point point = BasicSplitPaneDivider.this.getLocation();
          BasicSplitPaneDivider.this.dragger.completeDrag(param1MouseEvent.getX() + point.x, param1MouseEvent.getY() + point.y);
        } else if (param1MouseEvent.getSource() == BasicSplitPaneDivider.this.hiddenDivider) {
          Point point = BasicSplitPaneDivider.this.hiddenDivider.getLocation();
          int i = param1MouseEvent.getX() + point.x;
          int j = param1MouseEvent.getY() + point.y;
          BasicSplitPaneDivider.this.dragger.completeDrag(i, j);
        } 
        BasicSplitPaneDivider.this.dragger = null;
        param1MouseEvent.consume();
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (BasicSplitPaneDivider.this.dragger != null) {
        if (param1MouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane) {
          BasicSplitPaneDivider.this.dragger.continueDrag(param1MouseEvent.getX(), param1MouseEvent.getY());
        } else if (param1MouseEvent.getSource() == BasicSplitPaneDivider.this) {
          Point point = BasicSplitPaneDivider.this.getLocation();
          BasicSplitPaneDivider.this.dragger.continueDrag(param1MouseEvent.getX() + point.x, param1MouseEvent.getY() + point.y);
        } else if (param1MouseEvent.getSource() == BasicSplitPaneDivider.this.hiddenDivider) {
          Point point = BasicSplitPaneDivider.this.hiddenDivider.getLocation();
          int i = param1MouseEvent.getX() + point.x;
          int j = param1MouseEvent.getY() + point.y;
          BasicSplitPaneDivider.this.dragger.continueDrag(i, j);
        } 
        param1MouseEvent.consume();
      } 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() == BasicSplitPaneDivider.this)
        BasicSplitPaneDivider.this.setMouseOver(true); 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() == BasicSplitPaneDivider.this)
        BasicSplitPaneDivider.this.setMouseOver(false); 
    }
  }
  
  private class OneTouchActionHandler implements ActionListener {
    private boolean toMinimum;
    
    OneTouchActionHandler(boolean param1Boolean) { this.toMinimum = param1Boolean; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      int k;
      Insets insets = BasicSplitPaneDivider.this.splitPane.getInsets();
      int i = BasicSplitPaneDivider.this.splitPane.getLastDividerLocation();
      int j = BasicSplitPaneDivider.this.splitPaneUI.getDividerLocation(BasicSplitPaneDivider.this.splitPane);
      if (this.toMinimum) {
        if (BasicSplitPaneDivider.this.orientation == 0) {
          if (j >= BasicSplitPaneDivider.this.splitPane.getHeight() - insets.bottom - BasicSplitPaneDivider.this.getHeight()) {
            int m = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
            k = Math.min(i, m);
            BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
          } else {
            k = insets.top;
            BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
          } 
        } else if (j >= BasicSplitPaneDivider.this.splitPane.getWidth() - insets.right - BasicSplitPaneDivider.this.getWidth()) {
          int m = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
          k = Math.min(i, m);
          BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
        } else {
          k = insets.left;
          BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
        } 
      } else if (BasicSplitPaneDivider.this.orientation == 0) {
        if (j == insets.top) {
          int m = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
          k = Math.min(i, m);
          BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
        } else {
          k = BasicSplitPaneDivider.this.splitPane.getHeight() - BasicSplitPaneDivider.this.getHeight() - insets.top;
          BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
        } 
      } else if (j == insets.left) {
        int m = BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation();
        k = Math.min(i, m);
        BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
      } else {
        k = BasicSplitPaneDivider.this.splitPane.getWidth() - BasicSplitPaneDivider.this.getWidth() - insets.left;
        BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
      } 
      if (j != k) {
        BasicSplitPaneDivider.this.splitPane.setDividerLocation(k);
        BasicSplitPaneDivider.this.splitPane.setLastDividerLocation(j);
      } 
    }
  }
  
  protected class VerticalDragController extends DragController {
    protected VerticalDragController(MouseEvent param1MouseEvent) {
      super(BasicSplitPaneDivider.this, param1MouseEvent);
      JSplitPane jSplitPane = BasicSplitPaneDivider.this.splitPaneUI.getSplitPane();
      Component component1 = jSplitPane.getLeftComponent();
      Component component2 = jSplitPane.getRightComponent();
      this.initialX = (this$0.getLocation()).y;
      if (param1MouseEvent.getSource() == this$0) {
        this.offset = param1MouseEvent.getY();
      } else {
        this.offset = param1MouseEvent.getY() - this.initialX;
      } 
      if (component1 == null || component2 == null || this.offset < -1 || this.offset > (this$0.getSize()).height) {
        this.maxX = -1;
      } else {
        Insets insets = jSplitPane.getInsets();
        if (component1.isVisible()) {
          this.minX = (component1.getMinimumSize()).height;
          if (insets != null)
            this.minX += insets.top; 
        } else {
          this.minX = 0;
        } 
        if (component2.isVisible()) {
          int i = (insets != null) ? insets.bottom : 0;
          this.maxX = Math.max(0, (jSplitPane.getSize()).height - (this$0.getSize()).height + i - (component2.getMinimumSize()).height);
        } else {
          int i = (insets != null) ? insets.bottom : 0;
          this.maxX = Math.max(0, (jSplitPane.getSize()).height - (this$0.getSize()).height + i);
        } 
        if (this.maxX < this.minX)
          this.minX = this.maxX = 0; 
      } 
    }
    
    protected int getNeededLocation(int param1Int1, int param1Int2) { return Math.min(this.maxX, Math.max(this.minX, param1Int2 - this.offset)); }
    
    protected int positionForMouseEvent(MouseEvent param1MouseEvent) {
      null = (param1MouseEvent.getSource() == BasicSplitPaneDivider.this) ? (param1MouseEvent.getY() + (this.this$0.getLocation()).y) : param1MouseEvent.getY();
      return Math.min(this.maxX, Math.max(this.minX, null - this.offset));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */