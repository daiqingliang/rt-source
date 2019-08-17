package javax.swing;

import com.sun.java.swing.SwingUtilities3;
import java.applet.Applet;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.awt.image.VolatileImage;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.DisplayChangedListener;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingAccessor;
import sun.swing.SwingUtilities2;

public class RepaintManager {
  static final boolean HANDLE_TOP_LEVEL_PAINT;
  
  private static final short BUFFER_STRATEGY_NOT_SPECIFIED = 0;
  
  private static final short BUFFER_STRATEGY_SPECIFIED_ON = 1;
  
  private static final short BUFFER_STRATEGY_SPECIFIED_OFF = 2;
  
  private static final short BUFFER_STRATEGY_TYPE;
  
  private Map<GraphicsConfiguration, VolatileImage> volatileMap = new HashMap(1);
  
  private Map<Container, Rectangle> hwDirtyComponents;
  
  private Map<Component, Rectangle> dirtyComponents;
  
  private Map<Component, Rectangle> tmpDirtyComponents;
  
  private List<Component> invalidComponents;
  
  private List<Runnable> runnableList;
  
  boolean doubleBufferingEnabled = true;
  
  private Dimension doubleBufferMaxSize;
  
  DoubleBufferInfo standardDoubleBuffer;
  
  private PaintManager paintManager;
  
  private static final Object repaintManagerKey = RepaintManager.class;
  
  static boolean volatileImageBufferEnabled = true;
  
  private static final int volatileBufferType;
  
  private static boolean nativeDoubleBuffering;
  
  private static final int VOLATILE_LOOP_MAX = 2;
  
  private int paintDepth = 0;
  
  private short bufferStrategyType;
  
  private boolean painting;
  
  private JComponent repaintRoot;
  
  private Thread paintThread;
  
  private final ProcessingRunnable processingRunnable;
  
  private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
  
  private static final DisplayChangedListener displayChangedHandler = new DisplayChangedHandler();
  
  Rectangle tmp = new Rectangle();
  
  private List<SwingUtilities2.RepaintListener> repaintListeners = new ArrayList(1);
  
  public static RepaintManager currentManager(Component paramComponent) { return currentManager(AppContext.getAppContext()); }
  
  static RepaintManager currentManager(AppContext paramAppContext) {
    RepaintManager repaintManager = (RepaintManager)paramAppContext.get(repaintManagerKey);
    if (repaintManager == null) {
      repaintManager = new RepaintManager(BUFFER_STRATEGY_TYPE);
      paramAppContext.put(repaintManagerKey, repaintManager);
    } 
    return repaintManager;
  }
  
  public static RepaintManager currentManager(JComponent paramJComponent) { return currentManager(paramJComponent); }
  
  public static void setCurrentManager(RepaintManager paramRepaintManager) {
    if (paramRepaintManager != null) {
      SwingUtilities.appContextPut(repaintManagerKey, paramRepaintManager);
    } else {
      SwingUtilities.appContextRemove(repaintManagerKey);
    } 
  }
  
  public RepaintManager() { this((short)2); }
  
  private RepaintManager(short paramShort) {
    this.doubleBufferingEnabled = !nativeDoubleBuffering;
    synchronized (this) {
      this.dirtyComponents = new IdentityHashMap();
      this.tmpDirtyComponents = new IdentityHashMap();
      this.bufferStrategyType = paramShort;
      this.hwDirtyComponents = new IdentityHashMap();
    } 
    this.processingRunnable = new ProcessingRunnable(null);
  }
  
  private void displayChanged() { clearImages(); }
  
  public void addInvalidComponent(JComponent paramJComponent) {
    RepaintManager repaintManager = getDelegate(paramJComponent);
    if (repaintManager != null) {
      repaintManager.addInvalidComponent(paramJComponent);
      return;
    } 
    Container container = SwingUtilities.getValidateRoot(paramJComponent, true);
    if (container == null)
      return; 
    if (this.invalidComponents == null) {
      this.invalidComponents = new ArrayList();
    } else {
      int i = this.invalidComponents.size();
      for (byte b = 0; b < i; b++) {
        if (container == this.invalidComponents.get(b))
          return; 
      } 
    } 
    this.invalidComponents.add(container);
    scheduleProcessingRunnable(SunToolkit.targetToAppContext(paramJComponent));
  }
  
  public void removeInvalidComponent(JComponent paramJComponent) {
    RepaintManager repaintManager = getDelegate(paramJComponent);
    if (repaintManager != null) {
      repaintManager.removeInvalidComponent(paramJComponent);
      return;
    } 
    if (this.invalidComponents != null) {
      int i = this.invalidComponents.indexOf(paramJComponent);
      if (i != -1)
        this.invalidComponents.remove(i); 
    } 
  }
  
  private void addDirtyRegion0(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt3 <= 0 || paramInt4 <= 0 || paramContainer == null)
      return; 
    if (paramContainer.getWidth() <= 0 || paramContainer.getHeight() <= 0)
      return; 
    if (extendDirtyRegion(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4))
      return; 
    Container container1 = null;
    for (Container container2 = paramContainer; container2 != null; container2 = container2.getParent()) {
      if (!container2.isVisible() || container2.getPeer() == null)
        return; 
      if (container2 instanceof Window || container2 instanceof Applet) {
        if (container2 instanceof Frame && (((Frame)container2).getExtendedState() & true) == 1)
          return; 
        container1 = container2;
        break;
      } 
    } 
    if (container1 == null)
      return; 
    synchronized (this) {
      if (extendDirtyRegion(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4))
        return; 
      this.dirtyComponents.put(paramContainer, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
    } 
    scheduleProcessingRunnable(SunToolkit.targetToAppContext(paramContainer));
  }
  
  public void addDirtyRegion(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    RepaintManager repaintManager = getDelegate(paramJComponent);
    if (repaintManager != null) {
      repaintManager.addDirtyRegion(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    } 
    addDirtyRegion0(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void addDirtyRegion(Window paramWindow, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { addDirtyRegion0(paramWindow, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void addDirtyRegion(Applet paramApplet, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { addDirtyRegion0(paramApplet, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  void scheduleHeavyWeightPaints() {
    Map map;
    synchronized (this) {
      if (this.hwDirtyComponents.size() == 0)
        return; 
      map = this.hwDirtyComponents;
      this.hwDirtyComponents = new IdentityHashMap();
    } 
    for (Container container : map.keySet()) {
      Rectangle rectangle = (Rectangle)map.get(container);
      if (container instanceof Window) {
        addDirtyRegion((Window)container, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        continue;
      } 
      if (container instanceof Applet) {
        addDirtyRegion((Applet)container, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        continue;
      } 
      addDirtyRegion0(container, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  void nativeAddDirtyRegion(AppContext paramAppContext, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt3 > 0 && paramInt4 > 0) {
      synchronized (this) {
        Rectangle rectangle = (Rectangle)this.hwDirtyComponents.get(paramContainer);
        if (rectangle == null) {
          this.hwDirtyComponents.put(paramContainer, new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4));
        } else {
          this.hwDirtyComponents.put(paramContainer, SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, rectangle));
        } 
      } 
      scheduleProcessingRunnable(paramAppContext);
    } 
  }
  
  void nativeQueueSurfaceDataRunnable(AppContext paramAppContext, final Component c, final Runnable r) {
    synchronized (this) {
      if (this.runnableList == null)
        this.runnableList = new LinkedList(); 
      this.runnableList.add(new Runnable() {
            public void run() {
              AccessControlContext accessControlContext1 = AccessController.getContext();
              AccessControlContext accessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(c);
              javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
                    public Void run() {
                      r.run();
                      return null;
                    }
                  },  accessControlContext1, accessControlContext2);
            }
          });
    } 
    scheduleProcessingRunnable(paramAppContext);
  }
  
  private boolean extendDirtyRegion(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Rectangle rectangle = (Rectangle)this.dirtyComponents.get(paramComponent);
    if (rectangle != null) {
      SwingUtilities.computeUnion(paramInt1, paramInt2, paramInt3, paramInt4, rectangle);
      return true;
    } 
    return false;
  }
  
  public Rectangle getDirtyRegion(JComponent paramJComponent) {
    Rectangle rectangle;
    RepaintManager repaintManager = getDelegate(paramJComponent);
    if (repaintManager != null)
      return repaintManager.getDirtyRegion(paramJComponent); 
    synchronized (this) {
      rectangle = (Rectangle)this.dirtyComponents.get(paramJComponent);
    } 
    return (rectangle == null) ? new Rectangle(0, 0, 0, 0) : new Rectangle(rectangle);
  }
  
  public void markCompletelyDirty(JComponent paramJComponent) {
    RepaintManager repaintManager = getDelegate(paramJComponent);
    if (repaintManager != null) {
      repaintManager.markCompletelyDirty(paramJComponent);
      return;
    } 
    addDirtyRegion(paramJComponent, 0, 0, 2147483647, 2147483647);
  }
  
  public void markCompletelyClean(JComponent paramJComponent) {
    RepaintManager repaintManager = getDelegate(paramJComponent);
    if (repaintManager != null) {
      repaintManager.markCompletelyClean(paramJComponent);
      return;
    } 
    synchronized (this) {
      this.dirtyComponents.remove(paramJComponent);
    } 
  }
  
  public boolean isCompletelyDirty(JComponent paramJComponent) {
    RepaintManager repaintManager = getDelegate(paramJComponent);
    if (repaintManager != null)
      return repaintManager.isCompletelyDirty(paramJComponent); 
    Rectangle rectangle = getDirtyRegion(paramJComponent);
    return (rectangle.width == Integer.MAX_VALUE && rectangle.height == Integer.MAX_VALUE);
  }
  
  public void validateInvalidComponents() {
    List list;
    synchronized (this) {
      if (this.invalidComponents == null)
        return; 
      list = this.invalidComponents;
      this.invalidComponents = null;
    } 
    int i = list.size();
    for (byte b = 0; b < i; b++) {
      final Component c = (Component)list.get(b);
      AccessControlContext accessControlContext1 = AccessController.getContext();
      AccessControlContext accessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(component);
      javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
            public Void run() {
              c.validate();
              return null;
            }
          },  accessControlContext1, accessControlContext2);
    } 
  }
  
  private void prePaintDirtyRegions() {
    List list;
    Map map;
    synchronized (this) {
      map = this.dirtyComponents;
      list = this.runnableList;
      this.runnableList = null;
    } 
    if (list != null)
      for (Runnable runnable : list)
        runnable.run();  
    paintDirtyRegions();
    if (map.size() > 0)
      paintDirtyRegions(map); 
  }
  
  private void updateWindows(Map<Component, Rectangle> paramMap) {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (!(toolkit instanceof SunToolkit) || !((SunToolkit)toolkit).needUpdateWindow())
      return; 
    HashSet hashSet = new HashSet();
    Set set = paramMap.keySet();
    for (Component component : set) {
      Window window = (component instanceof Window) ? (Window)component : SwingUtilities.getWindowAncestor(component);
      if (window != null && !window.isOpaque())
        hashSet.add(window); 
    } 
    for (Window window : hashSet)
      AWTAccessor.getWindowAccessor().updateWindow(window); 
  }
  
  boolean isPainting() { return this.painting; }
  
  public void paintDirtyRegions() {
    synchronized (this) {
      Map map = this.tmpDirtyComponents;
      this.tmpDirtyComponents = this.dirtyComponents;
      this.dirtyComponents = map;
      this.dirtyComponents.clear();
    } 
    paintDirtyRegions(this.tmpDirtyComponents);
  }
  
  private void paintDirtyRegions(final Map<Component, Rectangle> tmpDirtyComponents) {
    if (paramMap.isEmpty())
      return; 
    final ArrayList roots = new ArrayList(paramMap.size());
    for (Component component : paramMap.keySet())
      collectDirtyComponents(paramMap, component, arrayList); 
    final AtomicInteger count = new AtomicInteger(arrayList.size());
    this.painting = true;
    try {
      for (byte b = 0; b < atomicInteger.get(); b++) {
        final byte i = b;
        final Component dirtyComponent = (Component)arrayList.get(b);
        AccessControlContext accessControlContext1 = AccessController.getContext();
        AccessControlContext accessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext(component);
        javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
              public Void run() {
                Rectangle rectangle = (Rectangle)tmpDirtyComponents.get(dirtyComponent);
                if (rectangle == null)
                  return null; 
                int i = dirtyComponent.getHeight();
                int j = dirtyComponent.getWidth();
                SwingUtilities.computeIntersection(0, 0, j, i, rectangle);
                if (dirtyComponent instanceof JComponent) {
                  ((JComponent)dirtyComponent).paintImmediately(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                } else if (dirtyComponent.isShowing()) {
                  graphics = JComponent.safelyGetGraphics(dirtyComponent, dirtyComponent);
                  if (graphics != null) {
                    graphics.setClip(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                    try {
                      dirtyComponent.paint(graphics);
                    } finally {
                      graphics.dispose();
                    } 
                  } 
                } 
                if (RepaintManager.this.repaintRoot != null) {
                  RepaintManager.this.adjustRoots(RepaintManager.this.repaintRoot, roots, i + 1);
                  count.set(roots.size());
                  this.this$0.paintManager.isRepaintingRoot = true;
                  RepaintManager.this.repaintRoot.paintImmediately(0, 0, RepaintManager.this.repaintRoot.getWidth(), RepaintManager.this.repaintRoot.getHeight());
                  this.this$0.paintManager.isRepaintingRoot = false;
                  RepaintManager.this.repaintRoot = null;
                } 
                return null;
              }
            }accessControlContext1, accessControlContext2);
      } 
    } finally {
      this.painting = false;
    } 
    updateWindows(paramMap);
    paramMap.clear();
  }
  
  private void adjustRoots(JComponent paramJComponent, List<Component> paramList, int paramInt) {
    for (int i = paramList.size() - 1; i >= paramInt; i--) {
      Component component;
      for (component = (Component)paramList.get(i); component != paramJComponent && component != null && component instanceof JComponent; component = component.getParent());
      if (component == paramJComponent)
        paramList.remove(i); 
    } 
  }
  
  void collectDirtyComponents(Map<Component, Rectangle> paramMap, Component paramComponent, List<Component> paramList) {
    Component component2 = paramComponent;
    Component component1 = component2;
    int n = paramComponent.getX();
    int i1 = paramComponent.getY();
    int i2 = paramComponent.getWidth();
    int i3 = paramComponent.getHeight();
    int k = 0;
    int i = k;
    int m = 0;
    int j = m;
    this.tmp.setBounds((Rectangle)paramMap.get(paramComponent));
    SwingUtilities.computeIntersection(0, 0, i2, i3, this.tmp);
    if (this.tmp.isEmpty())
      return; 
    while (component1 instanceof JComponent) {
      Container container = component1.getParent();
      if (container == null)
        break; 
      component1 = container;
      i += n;
      j += i1;
      this.tmp.setLocation(this.tmp.x + n, this.tmp.y + i1);
      n = component1.getX();
      i1 = component1.getY();
      i2 = component1.getWidth();
      i3 = component1.getHeight();
      this.tmp = SwingUtilities.computeIntersection(0, 0, i2, i3, this.tmp);
      if (this.tmp.isEmpty())
        return; 
      if (paramMap.get(component1) != null) {
        component2 = component1;
        k = i;
        m = j;
      } 
    } 
    if (paramComponent != component2) {
      this.tmp.setLocation(this.tmp.x + k - i, this.tmp.y + m - j);
      Rectangle rectangle = (Rectangle)paramMap.get(component2);
      SwingUtilities.computeUnion(this.tmp.x, this.tmp.y, this.tmp.width, this.tmp.height, rectangle);
    } 
    if (!paramList.contains(component2))
      paramList.add(component2); 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.dirtyComponents != null)
      stringBuffer.append("" + this.dirtyComponents); 
    return stringBuffer.toString();
  }
  
  public Image getOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2) {
    RepaintManager repaintManager = getDelegate(paramComponent);
    return (repaintManager != null) ? repaintManager.getOffscreenBuffer(paramComponent, paramInt1, paramInt2) : _getOffscreenBuffer(paramComponent, paramInt1, paramInt2);
  }
  
  public Image getVolatileOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2) {
    RepaintManager repaintManager = getDelegate(paramComponent);
    if (repaintManager != null)
      return repaintManager.getVolatileOffscreenBuffer(paramComponent, paramInt1, paramInt2); 
    Window window = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
    if (!window.isOpaque()) {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      if (toolkit instanceof SunToolkit && ((SunToolkit)toolkit).needUpdateWindow())
        return null; 
    } 
    GraphicsConfiguration graphicsConfiguration = paramComponent.getGraphicsConfiguration();
    if (graphicsConfiguration == null)
      graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); 
    Dimension dimension = getDoubleBufferMaximumSize();
    byte b1 = (paramInt1 < 1) ? 1 : ((paramInt1 > dimension.width) ? dimension.width : paramInt1);
    byte b2 = (paramInt2 < 1) ? 1 : ((paramInt2 > dimension.height) ? dimension.height : paramInt2);
    VolatileImage volatileImage = (VolatileImage)this.volatileMap.get(graphicsConfiguration);
    if (volatileImage == null || volatileImage.getWidth() < b1 || volatileImage.getHeight() < b2) {
      if (volatileImage != null)
        volatileImage.flush(); 
      volatileImage = graphicsConfiguration.createCompatibleVolatileImage(b1, b2, volatileBufferType);
      this.volatileMap.put(graphicsConfiguration, volatileImage);
    } 
    return volatileImage;
  }
  
  private Image _getOffscreenBuffer(Component paramComponent, int paramInt1, int paramInt2) {
    Dimension dimension = getDoubleBufferMaximumSize();
    Window window = (paramComponent instanceof Window) ? (Window)paramComponent : SwingUtilities.getWindowAncestor(paramComponent);
    if (!window.isOpaque()) {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      if (toolkit instanceof SunToolkit && ((SunToolkit)toolkit).needUpdateWindow())
        return null; 
    } 
    if (this.standardDoubleBuffer == null)
      this.standardDoubleBuffer = new DoubleBufferInfo(null); 
    DoubleBufferInfo doubleBufferInfo = this.standardDoubleBuffer;
    int i = (paramInt1 < 1) ? 1 : ((paramInt1 > dimension.width) ? dimension.width : paramInt1);
    int j = (paramInt2 < 1) ? 1 : ((paramInt2 > dimension.height) ? dimension.height : paramInt2);
    if (doubleBufferInfo.needsReset || (doubleBufferInfo.image != null && (doubleBufferInfo.size.width < i || doubleBufferInfo.size.height < j))) {
      doubleBufferInfo.needsReset = false;
      if (doubleBufferInfo.image != null) {
        doubleBufferInfo.image.flush();
        doubleBufferInfo.image = null;
      } 
      i = Math.max(doubleBufferInfo.size.width, i);
      j = Math.max(doubleBufferInfo.size.height, j);
    } 
    Image image = doubleBufferInfo.image;
    if (doubleBufferInfo.image == null) {
      image = paramComponent.createImage(i, j);
      doubleBufferInfo.size = new Dimension(i, j);
      if (paramComponent instanceof JComponent) {
        ((JComponent)paramComponent).setCreatedDoubleBuffer(true);
        doubleBufferInfo.image = image;
      } 
    } 
    return image;
  }
  
  public void setDoubleBufferMaximumSize(Dimension paramDimension) {
    this.doubleBufferMaxSize = paramDimension;
    if (this.doubleBufferMaxSize == null) {
      clearImages();
    } else {
      clearImages(paramDimension.width, paramDimension.height);
    } 
  }
  
  private void clearImages() { clearImages(0, 0); }
  
  private void clearImages(int paramInt1, int paramInt2) {
    if (this.standardDoubleBuffer != null && this.standardDoubleBuffer.image != null && (this.standardDoubleBuffer.image.getWidth(null) > paramInt1 || this.standardDoubleBuffer.image.getHeight(null) > paramInt2)) {
      this.standardDoubleBuffer.image.flush();
      this.standardDoubleBuffer.image = null;
    } 
    Iterator iterator = this.volatileMap.keySet().iterator();
    while (iterator.hasNext()) {
      GraphicsConfiguration graphicsConfiguration = (GraphicsConfiguration)iterator.next();
      VolatileImage volatileImage = (VolatileImage)this.volatileMap.get(graphicsConfiguration);
      if (volatileImage.getWidth() > paramInt1 || volatileImage.getHeight() > paramInt2) {
        volatileImage.flush();
        iterator.remove();
      } 
    } 
  }
  
  public Dimension getDoubleBufferMaximumSize() {
    if (this.doubleBufferMaxSize == null)
      try {
        Rectangle rectangle = new Rectangle();
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice graphicsDevice : graphicsEnvironment.getScreenDevices()) {
          GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
          rectangle = rectangle.union(graphicsConfiguration.getBounds());
        } 
        this.doubleBufferMaxSize = new Dimension(rectangle.width, rectangle.height);
      } catch (HeadlessException headlessException) {
        this.doubleBufferMaxSize = new Dimension(2147483647, 2147483647);
      }  
    return this.doubleBufferMaxSize;
  }
  
  public void setDoubleBufferingEnabled(boolean paramBoolean) {
    this.doubleBufferingEnabled = paramBoolean;
    PaintManager paintManager1 = getPaintManager();
    if (!paramBoolean && paintManager1.getClass() != PaintManager.class)
      setPaintManager(new PaintManager()); 
  }
  
  public boolean isDoubleBufferingEnabled() { return this.doubleBufferingEnabled; }
  
  void resetDoubleBuffer() {
    if (this.standardDoubleBuffer != null)
      this.standardDoubleBuffer.needsReset = true; 
  }
  
  void resetVolatileDoubleBuffer(GraphicsConfiguration paramGraphicsConfiguration) {
    Image image = (Image)this.volatileMap.remove(paramGraphicsConfiguration);
    if (image != null)
      image.flush(); 
  }
  
  boolean useVolatileDoubleBuffer() { return volatileImageBufferEnabled; }
  
  private boolean isPaintingThread() { return (Thread.currentThread() == this.paintThread); }
  
  void paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    PaintManager paintManager1 = getPaintManager();
    if (!isPaintingThread() && paintManager1.getClass() != PaintManager.class) {
      paintManager1 = new PaintManager();
      paintManager1.repaintManager = this;
    } 
    if (!paintManager1.paint(paramJComponent1, paramJComponent2, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4)) {
      paramGraphics.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
      paramJComponent1.paintToOffscreen(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
    } 
  }
  
  void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) { getPaintManager().copyArea(paramJComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean); }
  
  private void addRepaintListener(SwingUtilities2.RepaintListener paramRepaintListener) { this.repaintListeners.add(paramRepaintListener); }
  
  private void removeRepaintListener(SwingUtilities2.RepaintListener paramRepaintListener) { this.repaintListeners.remove(paramRepaintListener); }
  
  void notifyRepaintPerformed(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    for (SwingUtilities2.RepaintListener repaintListener : this.repaintListeners)
      repaintListener.repaintPerformed(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  void beginPaint() {
    int i;
    boolean bool = false;
    Thread thread = Thread.currentThread();
    synchronized (this) {
      i = this.paintDepth;
      if (this.paintThread == null || thread == this.paintThread) {
        this.paintThread = thread;
        this.paintDepth++;
      } else {
        bool = true;
      } 
    } 
    if (!bool && i == 0)
      getPaintManager().beginPaint(); 
  }
  
  void endPaint() {
    if (isPaintingThread()) {
      PaintManager paintManager1 = null;
      synchronized (this) {
        if (--this.paintDepth == 0)
          paintManager1 = getPaintManager(); 
      } 
      if (paintManager1 != null) {
        paintManager1.endPaint();
        synchronized (this) {
          this.paintThread = null;
        } 
      } 
    } 
  }
  
  boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return getPaintManager().show(paramContainer, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  void doubleBufferingChanged(JRootPane paramJRootPane) { getPaintManager().doubleBufferingChanged(paramJRootPane); }
  
  void setPaintManager(PaintManager paramPaintManager) {
    PaintManager paintManager1;
    if (paramPaintManager == null)
      paramPaintManager = new PaintManager(); 
    synchronized (this) {
      paintManager1 = this.paintManager;
      this.paintManager = paramPaintManager;
      paramPaintManager.repaintManager = this;
    } 
    if (paintManager1 != null)
      paintManager1.dispose(); 
  }
  
  private PaintManager getPaintManager() {
    if (this.paintManager == null) {
      BufferStrategyPaintManager bufferStrategyPaintManager = null;
      if (this.doubleBufferingEnabled && !nativeDoubleBuffering) {
        Toolkit toolkit;
        switch (this.bufferStrategyType) {
          case 0:
            toolkit = Toolkit.getDefaultToolkit();
            if (toolkit instanceof SunToolkit) {
              SunToolkit sunToolkit = (SunToolkit)toolkit;
              if (sunToolkit.useBufferPerWindow())
                bufferStrategyPaintManager = new BufferStrategyPaintManager(); 
            } 
            break;
          case 1:
            bufferStrategyPaintManager = new BufferStrategyPaintManager();
            break;
        } 
      } 
      setPaintManager(bufferStrategyPaintManager);
    } 
    return this.paintManager;
  }
  
  private void scheduleProcessingRunnable(AppContext paramAppContext) {
    if (this.processingRunnable.markPending()) {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      if (toolkit instanceof SunToolkit) {
        SunToolkit.getSystemEventQueueImplPP(paramAppContext).postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
      } else {
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), this.processingRunnable));
      } 
    } 
  }
  
  private RepaintManager getDelegate(Component paramComponent) {
    RepaintManager repaintManager = SwingUtilities3.getDelegateRepaintManager(paramComponent);
    if (this == repaintManager)
      repaintManager = null; 
    return repaintManager;
  }
  
  static  {
    SwingAccessor.setRepaintManagerAccessor(new SwingAccessor.RepaintManagerAccessor() {
          public void addRepaintListener(RepaintManager param1RepaintManager, SwingUtilities2.RepaintListener param1RepaintListener) { param1RepaintManager.addRepaintListener(param1RepaintListener); }
          
          public void removeRepaintListener(RepaintManager param1RepaintManager, SwingUtilities2.RepaintListener param1RepaintListener) { param1RepaintManager.removeRepaintListener(param1RepaintListener); }
        });
    volatileImageBufferEnabled = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.volatileImageBufferEnabled", "true")));
    boolean bool = GraphicsEnvironment.isHeadless();
    if (volatileImageBufferEnabled && bool)
      volatileImageBufferEnabled = false; 
    nativeDoubleBuffering = "true".equals(AccessController.doPrivileged(new GetPropertyAction("awt.nativeDoubleBuffering")));
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("swing.bufferPerWindow"));
    if (bool) {
      BUFFER_STRATEGY_TYPE = 2;
    } else if (str == null) {
      BUFFER_STRATEGY_TYPE = 0;
    } else if ("true".equals(str)) {
      BUFFER_STRATEGY_TYPE = 1;
    } else {
      BUFFER_STRATEGY_TYPE = 2;
    } 
    HANDLE_TOP_LEVEL_PAINT = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.handleTopLevelPaint", "true")));
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (graphicsEnvironment instanceof SunGraphicsEnvironment)
      ((SunGraphicsEnvironment)graphicsEnvironment).addDisplayChangedListener(displayChangedHandler); 
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (toolkit instanceof SunToolkit && ((SunToolkit)toolkit).isSwingBackbufferTranslucencySupported()) {
      volatileBufferType = 3;
    } else {
      volatileBufferType = 1;
    } 
  }
  
  private static final class DisplayChangedHandler implements DisplayChangedListener {
    public void displayChanged() { scheduleDisplayChanges(); }
    
    public void paletteChanged() {}
    
    private static void scheduleDisplayChanges() {
      for (AppContext appContext : AppContext.getAppContexts()) {
        synchronized (appContext) {
          if (!appContext.isDisposed()) {
            EventQueue eventQueue = (EventQueue)appContext.get(AppContext.EVENT_QUEUE_KEY);
            if (eventQueue != null)
              eventQueue.postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), new RepaintManager.DisplayChangedRunnable(null))); 
          } 
        } 
      } 
    }
  }
  
  private static final class DisplayChangedRunnable implements Runnable {
    private DisplayChangedRunnable() {}
    
    public void run() { RepaintManager.currentManager((JComponent)null).displayChanged(); }
  }
  
  private class DoubleBufferInfo {
    public Image image;
    
    public Dimension size;
    
    public boolean needsReset = false;
    
    private DoubleBufferInfo() {}
  }
  
  static class PaintManager {
    protected RepaintManager repaintManager;
    
    boolean isRepaintingRoot;
    
    public boolean paint(JComponent param1JComponent1, JComponent param1JComponent2, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      boolean bool = false;
      Image image;
      if (this.repaintManager.useVolatileDoubleBuffer() && (image = getValidImage(this.repaintManager.getVolatileOffscreenBuffer(param1JComponent2, param1Int3, param1Int4))) != null) {
        VolatileImage volatileImage = (VolatileImage)image;
        GraphicsConfiguration graphicsConfiguration = param1JComponent2.getGraphicsConfiguration();
        for (byte b = 0; !bool && b < 2; b++) {
          if (volatileImage.validate(graphicsConfiguration) == 2) {
            this.repaintManager.resetVolatileDoubleBuffer(graphicsConfiguration);
            image = this.repaintManager.getVolatileOffscreenBuffer(param1JComponent2, param1Int3, param1Int4);
            volatileImage = (VolatileImage)image;
          } 
          paintDoubleBuffered(param1JComponent1, volatileImage, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
          bool = !volatileImage.contentsLost();
        } 
      } 
      if (!bool && (image = getValidImage(this.repaintManager.getOffscreenBuffer(param1JComponent2, param1Int3, param1Int4))) != null) {
        paintDoubleBuffered(param1JComponent1, image, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4);
        bool = true;
      } 
      return bool;
    }
    
    public void copyArea(JComponent param1JComponent, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, boolean param1Boolean) { param1Graphics.copyArea(param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6); }
    
    public void beginPaint() {}
    
    public void endPaint() {}
    
    public boolean show(Container param1Container, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { return false; }
    
    public void doubleBufferingChanged(JRootPane param1JRootPane) {}
    
    protected void paintDoubleBuffered(JComponent param1JComponent, Image param1Image, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      graphics = param1Image.getGraphics();
      int i = Math.min(param1Int3, param1Image.getWidth(null));
      int j = Math.min(param1Int4, param1Image.getHeight(null));
      try {
        int k = param1Int1;
        int m = param1Int1 + param1Int3;
        while (k < m) {
          int n = param1Int2;
          int i1 = param1Int2 + param1Int4;
          while (n < i1) {
            graphics.translate(-k, -n);
            graphics.setClip(k, n, i, j);
            if (volatileBufferType != 1 && graphics instanceof Graphics2D) {
              Graphics2D graphics2D = (Graphics2D)graphics;
              Color color = graphics2D.getBackground();
              graphics2D.setBackground(param1JComponent.getBackground());
              graphics2D.clearRect(k, n, i, j);
              graphics2D.setBackground(color);
            } 
            param1JComponent.paintToOffscreen(graphics, k, n, i, j, m, i1);
            param1Graphics.setClip(k, n, i, j);
            if (volatileBufferType != 1 && param1Graphics instanceof Graphics2D) {
              Graphics2D graphics2D = (Graphics2D)param1Graphics;
              Composite composite = graphics2D.getComposite();
              graphics2D.setComposite(AlphaComposite.Src);
              graphics2D.drawImage(param1Image, k, n, param1JComponent);
              graphics2D.setComposite(composite);
            } else {
              param1Graphics.drawImage(param1Image, k, n, param1JComponent);
            } 
            graphics.translate(k, n);
            n += j;
          } 
          k += i;
        } 
      } finally {
        graphics.dispose();
      } 
    }
    
    private Image getValidImage(Image param1Image) { return (param1Image != null && param1Image.getWidth(null) > 0 && param1Image.getHeight(null) > 0) ? param1Image : null; }
    
    protected void repaintRoot(JComponent param1JComponent) {
      assert this.repaintManager.repaintRoot == null;
      if (this.repaintManager.painting) {
        this.repaintManager.repaintRoot = param1JComponent;
      } else {
        param1JComponent.repaint();
      } 
    }
    
    protected boolean isRepaintingRoot() { return this.isRepaintingRoot; }
    
    protected void dispose() {}
  }
  
  private final class ProcessingRunnable implements Runnable {
    private boolean pending;
    
    private ProcessingRunnable() {}
    
    public boolean markPending() {
      if (!this.pending) {
        this.pending = true;
        return true;
      } 
      return false;
    }
    
    public void run() {
      synchronized (this) {
        this.pending = false;
      } 
      RepaintManager.this.scheduleHeavyWeightPaints();
      RepaintManager.this.validateInvalidComponents();
      RepaintManager.this.prePaintDirtyRegions();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\RepaintManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */