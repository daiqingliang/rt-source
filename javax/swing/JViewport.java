package javax.swing;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.peer.ComponentPeer;
import java.beans.Transient;
import java.io.Serializable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ViewportUI;

public class JViewport extends JComponent implements Accessible {
  private static final String uiClassID = "ViewportUI";
  
  static final Object EnableWindowBlit = "EnableWindowBlit";
  
  protected boolean isViewSizeSet = false;
  
  protected Point lastPaintPosition = null;
  
  @Deprecated
  protected boolean backingStore = false;
  
  protected Image backingStoreImage = null;
  
  protected boolean scrollUnderway = false;
  
  private ComponentListener viewListener = null;
  
  private ChangeEvent changeEvent = null;
  
  public static final int BLIT_SCROLL_MODE = 1;
  
  public static final int BACKINGSTORE_SCROLL_MODE = 2;
  
  public static final int SIMPLE_SCROLL_MODE = 0;
  
  private int scrollMode = 1;
  
  private boolean repaintAll;
  
  private boolean waitingForRepaint;
  
  private Timer repaintTimer;
  
  private boolean inBlitPaint;
  
  private boolean hasHadValidView;
  
  private boolean viewChanged;
  
  public JViewport() {
    setLayout(createLayoutManager());
    setOpaque(true);
    updateUI();
    setInheritsPopupMenu(true);
  }
  
  public ViewportUI getUI() { return (ViewportUI)this.ui; }
  
  public void setUI(ViewportUI paramViewportUI) { setUI(paramViewportUI); }
  
  public void updateUI() { setUI((ViewportUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "ViewportUI"; }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt) { setView(paramComponent); }
  
  public void remove(Component paramComponent) {
    paramComponent.removeComponentListener(this.viewListener);
    super.remove(paramComponent);
  }
  
  public void scrollRectToVisible(Rectangle paramRectangle) {
    Component component = getView();
    if (component == null)
      return; 
    if (!component.isValid())
      validateView(); 
    int i = positionAdjustment(getWidth(), paramRectangle.width, paramRectangle.x);
    int j = positionAdjustment(getHeight(), paramRectangle.height, paramRectangle.y);
    if (i != 0 || j != 0) {
      Point point = getViewPosition();
      Dimension dimension1 = component.getSize();
      int k = point.x;
      int m = point.y;
      Dimension dimension2 = getExtentSize();
      point.x -= i;
      point.y -= j;
      if (component.isValid()) {
        if (getParent().getComponentOrientation().isLeftToRight()) {
          if (point.x + dimension2.width > dimension1.width) {
            point.x = Math.max(0, dimension1.width - dimension2.width);
          } else if (point.x < 0) {
            point.x = 0;
          } 
        } else if (dimension2.width > dimension1.width) {
          point.x = dimension1.width - dimension2.width;
        } else {
          point.x = Math.max(0, Math.min(dimension1.width - dimension2.width, point.x));
        } 
        if (point.y + dimension2.height > dimension1.height) {
          point.y = Math.max(0, dimension1.height - dimension2.height);
        } else if (point.y < 0) {
          point.y = 0;
        } 
      } 
      if (point.x != k || point.y != m) {
        setViewPosition(point);
        this.scrollUnderway = false;
      } 
    } 
  }
  
  private void validateView() {
    Container container = SwingUtilities.getValidateRoot(this, false);
    if (container == null)
      return; 
    container.validate();
    RepaintManager repaintManager = RepaintManager.currentManager(this);
    if (repaintManager != null)
      repaintManager.removeInvalidComponent((JComponent)container); 
  }
  
  private int positionAdjustment(int paramInt1, int paramInt2, int paramInt3) { return (paramInt3 >= 0 && paramInt2 + paramInt3 <= paramInt1) ? 0 : ((paramInt3 <= 0 && paramInt2 + paramInt3 >= paramInt1) ? 0 : ((paramInt3 > 0 && paramInt2 <= paramInt1) ? (-paramInt3 + paramInt1 - paramInt2) : ((paramInt3 >= 0 && paramInt2 >= paramInt1) ? -paramInt3 : ((paramInt3 <= 0 && paramInt2 <= paramInt1) ? -paramInt3 : ((paramInt3 < 0 && paramInt2 >= paramInt1) ? (-paramInt3 + paramInt1 - paramInt2) : 0))))); }
  
  public final void setBorder(Border paramBorder) {
    if (paramBorder != null)
      throw new IllegalArgumentException("JViewport.setBorder() not supported"); 
  }
  
  public final Insets getInsets() { return new Insets(0, 0, 0, 0); }
  
  public final Insets getInsets(Insets paramInsets) {
    paramInsets.left = paramInsets.top = paramInsets.right = paramInsets.bottom = 0;
    return paramInsets;
  }
  
  private Graphics getBackingStoreGraphics(Graphics paramGraphics) {
    Graphics graphics = this.backingStoreImage.getGraphics();
    graphics.setColor(paramGraphics.getColor());
    graphics.setFont(paramGraphics.getFont());
    graphics.setClip(paramGraphics.getClipBounds());
    return graphics;
  }
  
  private void paintViaBackingStore(Graphics paramGraphics) {
    graphics = getBackingStoreGraphics(paramGraphics);
    try {
      super.paint(graphics);
      paramGraphics.drawImage(this.backingStoreImage, 0, 0, this);
    } finally {
      graphics.dispose();
    } 
  }
  
  private void paintViaBackingStore(Graphics paramGraphics, Rectangle paramRectangle) {
    graphics = getBackingStoreGraphics(paramGraphics);
    try {
      super.paint(graphics);
      paramGraphics.setClip(paramRectangle);
      paramGraphics.drawImage(this.backingStoreImage, 0, 0, this);
    } finally {
      graphics.dispose();
    } 
  }
  
  public boolean isOptimizedDrawingEnabled() { return false; }
  
  protected boolean isPaintingOrigin() { return (this.scrollMode == 2); }
  
  private Point getViewLocation() {
    Component component = getView();
    return (component != null) ? component.getLocation() : new Point(0, 0);
  }
  
  public void paint(Graphics paramGraphics) {
    int i = getWidth();
    int j = getHeight();
    if (i <= 0 || j <= 0)
      return; 
    if (this.inBlitPaint) {
      super.paint(paramGraphics);
      return;
    } 
    if (this.repaintAll) {
      this.repaintAll = false;
      Rectangle rectangle1 = paramGraphics.getClipBounds();
      if (rectangle1.width < getWidth() || rectangle1.height < getHeight()) {
        this.waitingForRepaint = true;
        if (this.repaintTimer == null)
          this.repaintTimer = createRepaintTimer(); 
        this.repaintTimer.stop();
        this.repaintTimer.start();
      } else {
        if (this.repaintTimer != null)
          this.repaintTimer.stop(); 
        this.waitingForRepaint = false;
      } 
    } else if (this.waitingForRepaint) {
      Rectangle rectangle1 = paramGraphics.getClipBounds();
      if (rectangle1.width >= getWidth() && rectangle1.height >= getHeight()) {
        this.waitingForRepaint = false;
        this.repaintTimer.stop();
      } 
    } 
    if (!this.backingStore || isBlitting() || getView() == null) {
      super.paint(paramGraphics);
      this.lastPaintPosition = getViewLocation();
      return;
    } 
    Rectangle rectangle = getView().getBounds();
    if (!isOpaque())
      paramGraphics.clipRect(0, 0, rectangle.width, rectangle.height); 
    if (this.backingStoreImage == null) {
      this.backingStoreImage = createImage(i, j);
      Rectangle rectangle1 = paramGraphics.getClipBounds();
      if (rectangle1.width != i || rectangle1.height != j) {
        if (!isOpaque()) {
          paramGraphics.setClip(0, 0, Math.min(rectangle.width, i), Math.min(rectangle.height, j));
        } else {
          paramGraphics.setClip(0, 0, i, j);
        } 
        paintViaBackingStore(paramGraphics, rectangle1);
      } else {
        paintViaBackingStore(paramGraphics);
      } 
    } else if (!this.scrollUnderway || this.lastPaintPosition.equals(getViewLocation())) {
      paintViaBackingStore(paramGraphics);
    } else {
      Point point1 = new Point();
      Point point2 = new Point();
      Dimension dimension = new Dimension();
      Rectangle rectangle1 = new Rectangle();
      Point point3 = getViewLocation();
      int k = point3.x - this.lastPaintPosition.x;
      int m = point3.y - this.lastPaintPosition.y;
      boolean bool = computeBlit(k, m, point1, point2, dimension, rectangle1);
      if (!bool) {
        paintViaBackingStore(paramGraphics);
      } else {
        int n = point2.x - point1.x;
        int i1 = point2.y - point1.y;
        Rectangle rectangle2 = paramGraphics.getClipBounds();
        paramGraphics.setClip(0, 0, i, j);
        graphics = getBackingStoreGraphics(paramGraphics);
        try {
          graphics.copyArea(point1.x, point1.y, dimension.width, dimension.height, n, i1);
          paramGraphics.setClip(rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height);
          Rectangle rectangle3 = rectangle.intersection(rectangle1);
          graphics.setClip(rectangle3);
          super.paint(graphics);
          paramGraphics.drawImage(this.backingStoreImage, 0, 0, this);
        } finally {
          graphics.dispose();
        } 
      } 
    } 
    this.lastPaintPosition = getViewLocation();
    this.scrollUnderway = false;
  }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    boolean bool = (getWidth() != paramInt3 || getHeight() != paramInt4) ? 1 : 0;
    if (bool)
      this.backingStoreImage = null; 
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    if (bool || this.viewChanged) {
      this.viewChanged = false;
      fireStateChanged();
    } 
  }
  
  public void setScrollMode(int paramInt) {
    this.scrollMode = paramInt;
    this.backingStore = (paramInt == 2);
  }
  
  public int getScrollMode() { return this.scrollMode; }
  
  @Deprecated
  public boolean isBackingStoreEnabled() { return (this.scrollMode == 2); }
  
  @Deprecated
  public void setBackingStoreEnabled(boolean paramBoolean) {
    if (paramBoolean) {
      setScrollMode(2);
    } else {
      setScrollMode(1);
    } 
  }
  
  private boolean isBlitting() {
    Component component = getView();
    return (this.scrollMode == 1 && component instanceof JComponent && component.isOpaque());
  }
  
  public Component getView() { return (getComponentCount() > 0) ? getComponent(0) : null; }
  
  public void setView(Component paramComponent) {
    int i = getComponentCount();
    for (int j = i - 1; j >= 0; j--)
      remove(getComponent(j)); 
    this.isViewSizeSet = false;
    if (paramComponent != null) {
      super.addImpl(paramComponent, null, -1);
      this.viewListener = createViewListener();
      paramComponent.addComponentListener(this.viewListener);
    } 
    if (this.hasHadValidView) {
      fireStateChanged();
    } else if (paramComponent != null) {
      this.hasHadValidView = true;
    } 
    this.viewChanged = true;
    revalidate();
    repaint();
  }
  
  public Dimension getViewSize() {
    Component component = getView();
    return (component == null) ? new Dimension(0, 0) : (this.isViewSizeSet ? component.getSize() : component.getPreferredSize());
  }
  
  public void setViewSize(Dimension paramDimension) {
    Component component = getView();
    if (component != null) {
      Dimension dimension = component.getSize();
      if (!paramDimension.equals(dimension)) {
        this.scrollUnderway = false;
        component.setSize(paramDimension);
        this.isViewSizeSet = true;
        fireStateChanged();
      } 
    } 
  }
  
  public Point getViewPosition() {
    Component component = getView();
    if (component != null) {
      Point point = component.getLocation();
      point.x = -point.x;
      point.y = -point.y;
      return point;
    } 
    return new Point(0, 0);
  }
  
  public void setViewPosition(Point paramPoint) {
    int j;
    int i;
    Component component = getView();
    if (component == null)
      return; 
    int k = paramPoint.x;
    int m = paramPoint.y;
    if (component instanceof JComponent) {
      JComponent jComponent = (JComponent)component;
      i = jComponent.getX();
      j = jComponent.getY();
    } else {
      Rectangle rectangle = component.getBounds();
      i = rectangle.x;
      j = rectangle.y;
    } 
    int n = -k;
    int i1 = -m;
    if (i != n || j != i1) {
      if (!this.waitingForRepaint && isBlitting() && canUseWindowBlitter()) {
        repaintManager = RepaintManager.currentManager(this);
        JComponent jComponent = (JComponent)component;
        Rectangle rectangle = repaintManager.getDirtyRegion(jComponent);
        if (rectangle == null || !rectangle.contains(jComponent.getVisibleRect())) {
          repaintManager.beginPaint();
          try {
            Graphics graphics = JComponent.safelyGetGraphics(this);
            flushViewDirtyRegion(graphics, rectangle);
            component.setLocation(n, i1);
            Rectangle rectangle1 = new Rectangle(0, 0, getWidth(), Math.min(getHeight(), jComponent.getHeight()));
            graphics.setClip(rectangle1);
            this.repaintAll = (windowBlitPaint(graphics) && needsRepaintAfterBlit());
            graphics.dispose();
            repaintManager.notifyRepaintPerformed(this, rectangle1.x, rectangle1.y, rectangle1.width, rectangle1.height);
            repaintManager.markCompletelyClean((JComponent)getParent());
            repaintManager.markCompletelyClean(this);
            repaintManager.markCompletelyClean(jComponent);
          } finally {
            repaintManager.endPaint();
          } 
        } else {
          component.setLocation(n, i1);
          this.repaintAll = false;
        } 
      } else {
        this.scrollUnderway = true;
        component.setLocation(n, i1);
        this.repaintAll = false;
      } 
      revalidate();
      fireStateChanged();
    } 
  }
  
  public Rectangle getViewRect() { return new Rectangle(getViewPosition(), getExtentSize()); }
  
  protected boolean computeBlit(int paramInt1, int paramInt2, Point paramPoint1, Point paramPoint2, Dimension paramDimension, Rectangle paramRectangle) {
    int i = Math.abs(paramInt1);
    int j = Math.abs(paramInt2);
    Dimension dimension = getExtentSize();
    if (paramInt1 == 0 && paramInt2 != 0 && j < dimension.height) {
      if (paramInt2 < 0) {
        paramPoint1.y = -paramInt2;
        paramPoint2.y = 0;
        paramRectangle.y = dimension.height + paramInt2;
      } else {
        paramPoint1.y = 0;
        paramPoint2.y = paramInt2;
        paramRectangle.y = 0;
      } 
      paramRectangle.x = paramPoint2.x = 0;
      paramDimension.width = dimension.width;
      dimension.height -= j;
      paramRectangle.width = dimension.width;
      paramRectangle.height = j;
      return true;
    } 
    if (paramInt2 == 0 && paramInt1 != 0 && i < dimension.width) {
      if (paramInt1 < 0) {
        paramPoint1.x = -paramInt1;
        paramPoint2.x = 0;
        paramRectangle.x = dimension.width + paramInt1;
      } else {
        paramPoint1.x = 0;
        paramPoint2.x = paramInt1;
        paramRectangle.x = 0;
      } 
      paramRectangle.y = paramPoint2.y = 0;
      dimension.width -= i;
      paramDimension.height = dimension.height;
      paramRectangle.width = i;
      paramRectangle.height = dimension.height;
      return true;
    } 
    return false;
  }
  
  @Transient
  public Dimension getExtentSize() { return getSize(); }
  
  public Dimension toViewCoordinates(Dimension paramDimension) { return new Dimension(paramDimension); }
  
  public Point toViewCoordinates(Point paramPoint) { return new Point(paramPoint); }
  
  public void setExtentSize(Dimension paramDimension) {
    Dimension dimension = getExtentSize();
    if (!paramDimension.equals(dimension)) {
      setSize(paramDimension);
      fireStateChanged();
    } 
  }
  
  protected ViewListener createViewListener() { return new ViewListener(); }
  
  protected LayoutManager createLayoutManager() { return ViewportLayout.SHARED_INSTANCE; }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Container container = getParent();
    if (container != null) {
      container.repaint(paramLong, paramInt1 + getX(), paramInt2 + getY(), paramInt3, paramInt4);
    } else {
      super.repaint(paramLong, paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  protected String paramString() {
    String str1 = this.isViewSizeSet ? "true" : "false";
    String str2 = (this.lastPaintPosition != null) ? this.lastPaintPosition.toString() : "";
    String str3 = this.scrollUnderway ? "true" : "false";
    return super.paramString() + ",isViewSizeSet=" + str1 + ",lastPaintPosition=" + str2 + ",scrollUnderway=" + str3;
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    super.firePropertyChange(paramString, paramObject1, paramObject2);
    if (paramString.equals(EnableWindowBlit))
      if (paramObject2 != null) {
        setScrollMode(1);
      } else {
        setScrollMode(0);
      }  
  }
  
  private boolean needsRepaintAfterBlit() {
    Container container;
    for (container = getParent(); container != null && container.isLightweight(); container = container.getParent());
    if (container != null) {
      ComponentPeer componentPeer = container.getPeer();
      if (componentPeer != null && componentPeer.canDetermineObscurity() && !componentPeer.isObscured())
        return false; 
    } 
    return true;
  }
  
  private Timer createRepaintTimer() {
    Timer timer = new Timer(300, new ActionListener(this) {
          public void actionPerformed(ActionEvent param1ActionEvent) {
            if (JViewport.this.waitingForRepaint)
              JViewport.this.repaint(); 
          }
        });
    timer.setRepeats(false);
    return timer;
  }
  
  private void flushViewDirtyRegion(Graphics paramGraphics, Rectangle paramRectangle) {
    JComponent jComponent = (JComponent)getView();
    if (paramRectangle != null && paramRectangle.width > 0 && paramRectangle.height > 0) {
      paramRectangle.x += jComponent.getX();
      paramRectangle.y += jComponent.getY();
      Rectangle rectangle = paramGraphics.getClipBounds();
      if (rectangle == null)
        paramGraphics.setClip(0, 0, getWidth(), getHeight()); 
      paramGraphics.clipRect(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
      rectangle = paramGraphics.getClipBounds();
      if (rectangle.width > 0 && rectangle.height > 0)
        paintView(paramGraphics); 
    } 
  }
  
  private boolean windowBlitPaint(Graphics paramGraphics) {
    boolean bool;
    int i = getWidth();
    int j = getHeight();
    if (i == 0 || j == 0)
      return false; 
    RepaintManager repaintManager = RepaintManager.currentManager(this);
    JComponent jComponent = (JComponent)getView();
    if (this.lastPaintPosition == null || this.lastPaintPosition.equals(getViewLocation())) {
      paintView(paramGraphics);
      bool = false;
    } else {
      Point point1 = new Point();
      Point point2 = new Point();
      Dimension dimension = new Dimension();
      Rectangle rectangle = new Rectangle();
      Point point3 = getViewLocation();
      int k = point3.x - this.lastPaintPosition.x;
      int m = point3.y - this.lastPaintPosition.y;
      boolean bool1 = computeBlit(k, m, point1, point2, dimension, rectangle);
      if (!bool1) {
        paintView(paramGraphics);
        bool = false;
      } else {
        Rectangle rectangle1 = jComponent.getBounds().intersection(rectangle);
        rectangle1.x -= jComponent.getX();
        rectangle1.y -= jComponent.getY();
        blitDoubleBuffered(jComponent, paramGraphics, rectangle1.x, rectangle1.y, rectangle1.width, rectangle1.height, point1.x, point1.y, point2.x, point2.y, dimension.width, dimension.height);
        bool = true;
      } 
    } 
    this.lastPaintPosition = getViewLocation();
    return bool;
  }
  
  private void blitDoubleBuffered(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10) {
    RepaintManager repaintManager = RepaintManager.currentManager(this);
    int i = paramInt7 - paramInt5;
    int j = paramInt8 - paramInt6;
    Composite composite = null;
    if (paramGraphics instanceof Graphics2D) {
      Graphics2D graphics2D = (Graphics2D)paramGraphics;
      composite = graphics2D.getComposite();
      graphics2D.setComposite(AlphaComposite.Src);
    } 
    repaintManager.copyArea(this, paramGraphics, paramInt5, paramInt6, paramInt9, paramInt10, i, j, false);
    if (composite != null)
      ((Graphics2D)paramGraphics).setComposite(composite); 
    int k = paramJComponent.getX();
    int m = paramJComponent.getY();
    paramGraphics.translate(k, m);
    paramGraphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
    paramJComponent.paintForceDoubleBuffered(paramGraphics);
    paramGraphics.translate(-k, -m);
  }
  
  private void paintView(Graphics paramGraphics) {
    Rectangle rectangle = paramGraphics.getClipBounds();
    JComponent jComponent = (JComponent)getView();
    if (jComponent.getWidth() >= getWidth()) {
      int i = jComponent.getX();
      int j = jComponent.getY();
      paramGraphics.translate(i, j);
      paramGraphics.setClip(rectangle.x - i, rectangle.y - j, rectangle.width, rectangle.height);
      jComponent.paintForceDoubleBuffered(paramGraphics);
      paramGraphics.translate(-i, -j);
      paramGraphics.setClip(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } else {
      try {
        this.inBlitPaint = true;
        paintForceDoubleBuffered(paramGraphics);
      } finally {
        this.inBlitPaint = false;
      } 
    } 
  }
  
  private boolean canUseWindowBlitter() {
    if (!isShowing() || (!(getParent() instanceof JComponent) && !(getView() instanceof JComponent)))
      return false; 
    if (isPainting())
      return false; 
    Rectangle rectangle1 = RepaintManager.currentManager(this).getDirtyRegion((JComponent)getParent());
    if (rectangle1 != null && rectangle1.width > 0 && rectangle1.height > 0)
      return false; 
    Rectangle rectangle2 = new Rectangle(0, 0, getWidth(), getHeight());
    Rectangle rectangle3 = new Rectangle();
    Rectangle rectangle4 = null;
    JViewport jViewport = null;
    Container container;
    while (container != null && (container = this).isLightweightComponent(container)) {
      int i = container.getX();
      int j = container.getY();
      int k = container.getWidth();
      int m = container.getHeight();
      rectangle3.setBounds(rectangle2);
      SwingUtilities.computeIntersection(0, 0, k, m, rectangle2);
      if (!rectangle2.equals(rectangle3))
        return false; 
      if (jViewport != null && container instanceof JComponent && !((JComponent)container).isOptimizedDrawingEnabled()) {
        Component[] arrayOfComponent = container.getComponents();
        int n = 0;
        for (int i1 = arrayOfComponent.length - 1; i1 >= 0; i1--) {
          if (arrayOfComponent[i1] == jViewport) {
            n = i1 - 1;
            break;
          } 
        } 
        while (n >= 0) {
          rectangle4 = arrayOfComponent[n].getBounds(rectangle4);
          if (rectangle4.intersects(rectangle2))
            return false; 
          n--;
        } 
      } 
      rectangle2.x += i;
      rectangle2.y += j;
      jViewport = container;
      container = container.getParent();
    } 
    return !(container == null);
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJViewport(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJViewport extends JComponent.AccessibleJComponent {
    protected AccessibleJViewport() { super(JViewport.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.VIEWPORT; }
  }
  
  protected class ViewListener extends ComponentAdapter implements Serializable {
    public void componentResized(ComponentEvent param1ComponentEvent) {
      JViewport.this.fireStateChanged();
      JViewport.this.revalidate();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JViewport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */