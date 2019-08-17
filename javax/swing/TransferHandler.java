package javax.swing;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.TooManyListenersException;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.reflect.misc.MethodUtil;
import sun.swing.SwingAccessor;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class TransferHandler implements Serializable {
  public static final int NONE = 0;
  
  public static final int COPY = 1;
  
  public static final int MOVE = 2;
  
  public static final int COPY_OR_MOVE = 3;
  
  public static final int LINK = 1073741824;
  
  private Image dragImage;
  
  private Point dragImageOffset;
  
  private String propertyName;
  
  private static SwingDragGestureRecognizer recognizer = null;
  
  static final Action cutAction = new TransferAction("cut");
  
  static final Action copyAction = new TransferAction("copy");
  
  static final Action pasteAction = new TransferAction("paste");
  
  public static Action getCutAction() { return cutAction; }
  
  public static Action getCopyAction() { return copyAction; }
  
  public static Action getPasteAction() { return pasteAction; }
  
  public TransferHandler(String paramString) { this.propertyName = paramString; }
  
  protected TransferHandler() { this(null); }
  
  public void setDragImage(Image paramImage) { this.dragImage = paramImage; }
  
  public Image getDragImage() { return this.dragImage; }
  
  public void setDragImageOffset(Point paramPoint) { this.dragImageOffset = new Point(paramPoint); }
  
  public Point getDragImageOffset() { return (this.dragImageOffset == null) ? new Point(0, 0) : new Point(this.dragImageOffset); }
  
  public void exportAsDrag(JComponent paramJComponent, InputEvent paramInputEvent, int paramInt) {
    int i = getSourceActions(paramJComponent);
    if (!(paramInputEvent instanceof MouseEvent) || (paramInt != 1 && paramInt != 2 && paramInt != 1073741824) || (i & paramInt) == 0)
      paramInt = 0; 
    if (paramInt != 0 && !GraphicsEnvironment.isHeadless()) {
      if (recognizer == null)
        recognizer = new SwingDragGestureRecognizer(new DragHandler(null)); 
      recognizer.gestured(paramJComponent, (MouseEvent)paramInputEvent, i, paramInt);
    } else {
      exportDone(paramJComponent, null, 0);
    } 
  }
  
  public void exportToClipboard(JComponent paramJComponent, Clipboard paramClipboard, int paramInt) throws IllegalStateException {
    if ((paramInt == 1 || paramInt == 2) && (getSourceActions(paramJComponent) & paramInt) != 0) {
      Transferable transferable = createTransferable(paramJComponent);
      if (transferable != null)
        try {
          paramClipboard.setContents(transferable, null);
          exportDone(paramJComponent, transferable, paramInt);
          return;
        } catch (IllegalStateException illegalStateException) {
          exportDone(paramJComponent, transferable, 0);
          throw illegalStateException;
        }  
    } 
    exportDone(paramJComponent, null, 0);
  }
  
  public boolean importData(TransferSupport paramTransferSupport) { return (paramTransferSupport.getComponent() instanceof JComponent) ? importData((JComponent)paramTransferSupport.getComponent(), paramTransferSupport.getTransferable()) : 0; }
  
  public boolean importData(JComponent paramJComponent, Transferable paramTransferable) {
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(paramJComponent);
    if (propertyDescriptor != null) {
      Method method = propertyDescriptor.getWriteMethod();
      if (method == null)
        return false; 
      Class[] arrayOfClass = method.getParameterTypes();
      if (arrayOfClass.length != 1)
        return false; 
      DataFlavor dataFlavor = getPropertyDataFlavor(arrayOfClass[0], paramTransferable.getTransferDataFlavors());
      if (dataFlavor != null)
        try {
          Object object = paramTransferable.getTransferData(dataFlavor);
          Object[] arrayOfObject = { object };
          MethodUtil.invoke(method, paramJComponent, arrayOfObject);
          return true;
        } catch (Exception exception) {
          System.err.println("Invocation failed");
        }  
    } 
    return false;
  }
  
  public boolean canImport(TransferSupport paramTransferSupport) { return (paramTransferSupport.getComponent() instanceof JComponent) ? canImport((JComponent)paramTransferSupport.getComponent(), paramTransferSupport.getDataFlavors()) : 0; }
  
  public boolean canImport(JComponent paramJComponent, DataFlavor[] paramArrayOfDataFlavor) {
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(paramJComponent);
    if (propertyDescriptor != null) {
      Method method = propertyDescriptor.getWriteMethod();
      if (method == null)
        return false; 
      Class[] arrayOfClass = method.getParameterTypes();
      if (arrayOfClass.length != 1)
        return false; 
      DataFlavor dataFlavor = getPropertyDataFlavor(arrayOfClass[0], paramArrayOfDataFlavor);
      if (dataFlavor != null)
        return true; 
    } 
    return false;
  }
  
  public int getSourceActions(JComponent paramJComponent) {
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(paramJComponent);
    return (propertyDescriptor != null) ? 1 : 0;
  }
  
  public Icon getVisualRepresentation(Transferable paramTransferable) { return null; }
  
  protected Transferable createTransferable(JComponent paramJComponent) {
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(paramJComponent);
    return (propertyDescriptor != null) ? new PropertyTransferable(propertyDescriptor, paramJComponent) : null;
  }
  
  protected void exportDone(JComponent paramJComponent, Transferable paramTransferable, int paramInt) {}
  
  private PropertyDescriptor getPropertyDescriptor(JComponent paramJComponent) {
    BeanInfo beanInfo;
    if (this.propertyName == null)
      return null; 
    Class clazz = paramJComponent.getClass();
    try {
      beanInfo = Introspector.getBeanInfo(clazz);
    } catch (IntrospectionException introspectionException) {
      return null;
    } 
    PropertyDescriptor[] arrayOfPropertyDescriptor = beanInfo.getPropertyDescriptors();
    for (byte b = 0; b < arrayOfPropertyDescriptor.length; b++) {
      if (this.propertyName.equals(arrayOfPropertyDescriptor[b].getName())) {
        Method method = arrayOfPropertyDescriptor[b].getReadMethod();
        if (method != null) {
          Class[] arrayOfClass = method.getParameterTypes();
          if (arrayOfClass == null || arrayOfClass.length == 0)
            return arrayOfPropertyDescriptor[b]; 
        } 
      } 
    } 
    return null;
  }
  
  private DataFlavor getPropertyDataFlavor(Class<?> paramClass, DataFlavor[] paramArrayOfDataFlavor) {
    for (byte b = 0; b < paramArrayOfDataFlavor.length; b++) {
      DataFlavor dataFlavor = paramArrayOfDataFlavor[b];
      if ("application".equals(dataFlavor.getPrimaryType()) && "x-java-jvm-local-objectref".equals(dataFlavor.getSubType()) && paramClass.isAssignableFrom(dataFlavor.getRepresentationClass()))
        return dataFlavor; 
    } 
    return null;
  }
  
  private static DropTargetListener getDropTargetListener() {
    synchronized (DropHandler.class) {
      DropHandler dropHandler = (DropHandler)AppContext.getAppContext().get(DropHandler.class);
      if (dropHandler == null) {
        dropHandler = new DropHandler(null);
        AppContext.getAppContext().put(DropHandler.class, dropHandler);
      } 
      return dropHandler;
    } 
  }
  
  private static class DragHandler implements DragGestureListener, DragSourceListener {
    private boolean scrolls;
    
    private DragHandler() {}
    
    public void dragGestureRecognized(DragGestureEvent param1DragGestureEvent) {
      JComponent jComponent = (JComponent)param1DragGestureEvent.getComponent();
      TransferHandler transferHandler = jComponent.getTransferHandler();
      Transferable transferable = transferHandler.createTransferable(jComponent);
      if (transferable != null) {
        this.scrolls = jComponent.getAutoscrolls();
        jComponent.setAutoscrolls(false);
        try {
          Image image = transferHandler.getDragImage();
          if (image == null) {
            param1DragGestureEvent.startDrag(null, transferable, this);
          } else {
            param1DragGestureEvent.startDrag(null, image, transferHandler.getDragImageOffset(), transferable, this);
          } 
          return;
        } catch (RuntimeException runtimeException) {
          jComponent.setAutoscrolls(this.scrolls);
        } 
      } 
      transferHandler.exportDone(jComponent, transferable, 0);
    }
    
    public void dragEnter(DragSourceDragEvent param1DragSourceDragEvent) {}
    
    public void dragOver(DragSourceDragEvent param1DragSourceDragEvent) {}
    
    public void dragExit(DragSourceEvent param1DragSourceEvent) {}
    
    public void dragDropEnd(DragSourceDropEvent param1DragSourceDropEvent) {
      DragSourceContext dragSourceContext = param1DragSourceDropEvent.getDragSourceContext();
      JComponent jComponent = (JComponent)dragSourceContext.getComponent();
      if (param1DragSourceDropEvent.getDropSuccess()) {
        jComponent.getTransferHandler().exportDone(jComponent, dragSourceContext.getTransferable(), param1DragSourceDropEvent.getDropAction());
      } else {
        jComponent.getTransferHandler().exportDone(jComponent, dragSourceContext.getTransferable(), 0);
      } 
      jComponent.setAutoscrolls(this.scrolls);
    }
    
    public void dropActionChanged(DragSourceDragEvent param1DragSourceDragEvent) {}
  }
  
  private static class DropHandler implements DropTargetListener, Serializable, ActionListener {
    private Timer timer;
    
    private Point lastPosition;
    
    private Rectangle outer = new Rectangle();
    
    private Rectangle inner = new Rectangle();
    
    private int hysteresis = 10;
    
    private Component component;
    
    private Object state;
    
    private TransferHandler.TransferSupport support = new TransferHandler.TransferSupport(null, (DropTargetEvent)null, null);
    
    private static final int AUTOSCROLL_INSET = 10;
    
    private DropHandler() {}
    
    private void updateAutoscrollRegion(JComponent param1JComponent) {
      Rectangle rectangle = param1JComponent.getVisibleRect();
      this.outer.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      Insets insets = new Insets(0, 0, 0, 0);
      if (param1JComponent instanceof Scrollable) {
        byte b = 20;
        if (rectangle.width >= b)
          insets.left = insets.right = 10; 
        if (rectangle.height >= b)
          insets.top = insets.bottom = 10; 
      } 
      this.inner.setBounds(rectangle.x + insets.left, rectangle.y + insets.top, rectangle.width - insets.left + insets.right, rectangle.height - insets.top + insets.bottom);
    }
    
    private void autoscroll(JComponent param1JComponent, Point param1Point) {
      if (param1JComponent instanceof Scrollable) {
        Scrollable scrollable = (Scrollable)param1JComponent;
        if (param1Point.y < this.inner.y) {
          int i = scrollable.getScrollableUnitIncrement(this.outer, 1, -1);
          Rectangle rectangle = new Rectangle(this.inner.x, this.outer.y - i, this.inner.width, i);
          param1JComponent.scrollRectToVisible(rectangle);
        } else if (param1Point.y > this.inner.y + this.inner.height) {
          int i = scrollable.getScrollableUnitIncrement(this.outer, 1, 1);
          Rectangle rectangle = new Rectangle(this.inner.x, this.outer.y + this.outer.height, this.inner.width, i);
          param1JComponent.scrollRectToVisible(rectangle);
        } 
        if (param1Point.x < this.inner.x) {
          int i = scrollable.getScrollableUnitIncrement(this.outer, 0, -1);
          Rectangle rectangle = new Rectangle(this.outer.x - i, this.inner.y, i, this.inner.height);
          param1JComponent.scrollRectToVisible(rectangle);
        } else if (param1Point.x > this.inner.x + this.inner.width) {
          int i = scrollable.getScrollableUnitIncrement(this.outer, 0, 1);
          Rectangle rectangle = new Rectangle(this.outer.x + this.outer.width, this.inner.y, i, this.inner.height);
          param1JComponent.scrollRectToVisible(rectangle);
        } 
      } 
    }
    
    private void initPropertiesIfNecessary() {
      if (this.timer == null) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Integer integer = (Integer)toolkit.getDesktopProperty("DnD.Autoscroll.interval");
        this.timer = new Timer((integer == null) ? 100 : integer.intValue(), this);
        integer = (Integer)toolkit.getDesktopProperty("DnD.Autoscroll.initialDelay");
        this.timer.setInitialDelay((integer == null) ? 100 : integer.intValue());
        integer = (Integer)toolkit.getDesktopProperty("DnD.Autoscroll.cursorHysteresis");
        if (integer != null)
          this.hysteresis = integer.intValue(); 
      } 
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      updateAutoscrollRegion((JComponent)this.component);
      if (this.outer.contains(this.lastPosition) && !this.inner.contains(this.lastPosition))
        autoscroll((JComponent)this.component, this.lastPosition); 
    }
    
    private void setComponentDropLocation(TransferHandler.TransferSupport param1TransferSupport, boolean param1Boolean) {
      TransferHandler.DropLocation dropLocation = (param1TransferSupport == null) ? null : param1TransferSupport.getDropLocation();
      if (SunToolkit.isInstanceOf(this.component, "javax.swing.text.JTextComponent")) {
        this.state = SwingAccessor.getJTextComponentAccessor().setDropLocation((JTextComponent)this.component, dropLocation, this.state, param1Boolean);
      } else if (this.component instanceof JComponent) {
        this.state = ((JComponent)this.component).setDropLocation(dropLocation, this.state, param1Boolean);
      } 
    }
    
    private void handleDrag(DropTargetDragEvent param1DropTargetDragEvent) {
      TransferHandler transferHandler = ((TransferHandler.HasGetTransferHandler)this.component).getTransferHandler();
      if (transferHandler == null) {
        param1DropTargetDragEvent.rejectDrag();
        setComponentDropLocation(null, false);
        return;
      } 
      this.support.setDNDVariables(this.component, param1DropTargetDragEvent);
      boolean bool1 = transferHandler.canImport(this.support);
      if (bool1) {
        param1DropTargetDragEvent.acceptDrag(this.support.getDropAction());
      } else {
        param1DropTargetDragEvent.rejectDrag();
      } 
      boolean bool2 = this.support.showDropLocationIsSet ? this.support.showDropLocation : bool1;
      setComponentDropLocation(bool2 ? this.support : null, false);
    }
    
    public void dragEnter(DropTargetDragEvent param1DropTargetDragEvent) {
      this.state = null;
      this.component = param1DropTargetDragEvent.getDropTargetContext().getComponent();
      handleDrag(param1DropTargetDragEvent);
      if (this.component instanceof JComponent) {
        this.lastPosition = param1DropTargetDragEvent.getLocation();
        updateAutoscrollRegion((JComponent)this.component);
        initPropertiesIfNecessary();
      } 
    }
    
    public void dragOver(DropTargetDragEvent param1DropTargetDragEvent) {
      handleDrag(param1DropTargetDragEvent);
      if (!(this.component instanceof JComponent))
        return; 
      Point point = param1DropTargetDragEvent.getLocation();
      if (Math.abs(point.x - this.lastPosition.x) > this.hysteresis || Math.abs(point.y - this.lastPosition.y) > this.hysteresis) {
        if (this.timer.isRunning())
          this.timer.stop(); 
      } else if (!this.timer.isRunning()) {
        this.timer.start();
      } 
      this.lastPosition = point;
    }
    
    public void dragExit(DropTargetEvent param1DropTargetEvent) { cleanup(false); }
    
    public void drop(DropTargetDropEvent param1DropTargetDropEvent) {
      TransferHandler transferHandler = ((TransferHandler.HasGetTransferHandler)this.component).getTransferHandler();
      if (transferHandler == null) {
        param1DropTargetDropEvent.rejectDrop();
        cleanup(false);
        return;
      } 
      this.support.setDNDVariables(this.component, param1DropTargetDropEvent);
      boolean bool = transferHandler.canImport(this.support);
      if (bool) {
        boolean bool2;
        param1DropTargetDropEvent.acceptDrop(this.support.getDropAction());
        boolean bool1 = this.support.showDropLocationIsSet ? this.support.showDropLocation : bool;
        setComponentDropLocation(bool1 ? this.support : null, false);
        try {
          bool2 = transferHandler.importData(this.support);
        } catch (RuntimeException runtimeException) {
          bool2 = false;
        } 
        param1DropTargetDropEvent.dropComplete(bool2);
        cleanup(bool2);
      } else {
        param1DropTargetDropEvent.rejectDrop();
        cleanup(false);
      } 
    }
    
    public void dropActionChanged(DropTargetDragEvent param1DropTargetDragEvent) {
      if (this.component == null)
        return; 
      handleDrag(param1DropTargetDragEvent);
    }
    
    private void cleanup(boolean param1Boolean) {
      setComponentDropLocation(null, param1Boolean);
      if (this.component instanceof JComponent)
        ((JComponent)this.component).dndDone(); 
      if (this.timer != null)
        this.timer.stop(); 
      this.state = null;
      this.component = null;
      this.lastPosition = null;
    }
  }
  
  public static class DropLocation {
    private final Point dropPoint;
    
    protected DropLocation(Point param1Point) {
      if (param1Point == null)
        throw new IllegalArgumentException("Point cannot be null"); 
      this.dropPoint = new Point(param1Point);
    }
    
    public final Point getDropPoint() { return new Point(this.dropPoint); }
    
    public String toString() { return getClass().getName() + "[dropPoint=" + this.dropPoint + "]"; }
  }
  
  static interface HasGetTransferHandler {
    TransferHandler getTransferHandler();
  }
  
  static class PropertyTransferable implements Transferable {
    JComponent component;
    
    PropertyDescriptor property;
    
    PropertyTransferable(PropertyDescriptor param1PropertyDescriptor, JComponent param1JComponent) {
      this.property = param1PropertyDescriptor;
      this.component = param1JComponent;
    }
    
    public DataFlavor[] getTransferDataFlavors() {
      DataFlavor[] arrayOfDataFlavor = new DataFlavor[1];
      Class clazz = this.property.getPropertyType();
      String str = "application/x-java-jvm-local-objectref;class=" + clazz.getName();
      try {
        arrayOfDataFlavor[0] = new DataFlavor(str);
      } catch (ClassNotFoundException classNotFoundException) {
        arrayOfDataFlavor = new DataFlavor[0];
      } 
      return arrayOfDataFlavor;
    }
    
    public boolean isDataFlavorSupported(DataFlavor param1DataFlavor) {
      Class clazz = this.property.getPropertyType();
      return ("application".equals(param1DataFlavor.getPrimaryType()) && "x-java-jvm-local-objectref".equals(param1DataFlavor.getSubType()) && param1DataFlavor.getRepresentationClass().isAssignableFrom(clazz));
    }
    
    public Object getTransferData(DataFlavor param1DataFlavor) throws UnsupportedFlavorException, IOException {
      if (!isDataFlavorSupported(param1DataFlavor))
        throw new UnsupportedFlavorException(param1DataFlavor); 
      Method method = this.property.getReadMethod();
      Object object = null;
      try {
        object = MethodUtil.invoke(method, this.component, (Object[])null);
      } catch (Exception exception) {
        throw new IOException("Property read failed: " + this.property.getName());
      } 
      return object;
    }
  }
  
  private static class SwingDragGestureRecognizer extends DragGestureRecognizer {
    SwingDragGestureRecognizer(DragGestureListener param1DragGestureListener) { super(DragSource.getDefaultDragSource(), null, 0, param1DragGestureListener); }
    
    void gestured(JComponent param1JComponent, MouseEvent param1MouseEvent, int param1Int1, int param1Int2) {
      setComponent(param1JComponent);
      setSourceActions(param1Int1);
      appendEvent(param1MouseEvent);
      fireDragGestureRecognized(param1Int2, param1MouseEvent.getPoint());
    }
    
    protected void registerListeners() {}
    
    protected void unregisterListeners() {}
  }
  
  static class SwingDropTarget extends DropTarget implements UIResource {
    private EventListenerList listenerList;
    
    SwingDropTarget(Component param1Component) {
      super(param1Component, 1073741827, null);
      try {
        super.addDropTargetListener(TransferHandler.getDropTargetListener());
      } catch (TooManyListenersException tooManyListenersException) {}
    }
    
    public void addDropTargetListener(DropTargetListener param1DropTargetListener) throws TooManyListenersException {
      if (this.listenerList == null)
        this.listenerList = new EventListenerList(); 
      this.listenerList.add(DropTargetListener.class, param1DropTargetListener);
    }
    
    public void removeDropTargetListener(DropTargetListener param1DropTargetListener) throws TooManyListenersException {
      if (this.listenerList != null)
        this.listenerList.remove(DropTargetListener.class, param1DropTargetListener); 
    }
    
    public void dragEnter(DropTargetDragEvent param1DropTargetDragEvent) {
      super.dragEnter(param1DropTargetDragEvent);
      if (this.listenerList != null) {
        Object[] arrayOfObject = this.listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class)
            ((DropTargetListener)arrayOfObject[i + 1]).dragEnter(param1DropTargetDragEvent); 
        } 
      } 
    }
    
    public void dragOver(DropTargetDragEvent param1DropTargetDragEvent) {
      super.dragOver(param1DropTargetDragEvent);
      if (this.listenerList != null) {
        Object[] arrayOfObject = this.listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class)
            ((DropTargetListener)arrayOfObject[i + 1]).dragOver(param1DropTargetDragEvent); 
        } 
      } 
    }
    
    public void dragExit(DropTargetEvent param1DropTargetEvent) {
      super.dragExit(param1DropTargetEvent);
      if (this.listenerList != null) {
        Object[] arrayOfObject = this.listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class)
            ((DropTargetListener)arrayOfObject[i + 1]).dragExit(param1DropTargetEvent); 
        } 
      } 
      if (!isActive()) {
        DropTargetListener dropTargetListener = TransferHandler.getDropTargetListener();
        if (dropTargetListener != null && dropTargetListener instanceof TransferHandler.DropHandler)
          ((TransferHandler.DropHandler)dropTargetListener).cleanup(false); 
      } 
    }
    
    public void drop(DropTargetDropEvent param1DropTargetDropEvent) {
      super.drop(param1DropTargetDropEvent);
      if (this.listenerList != null) {
        Object[] arrayOfObject = this.listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class)
            ((DropTargetListener)arrayOfObject[i + 1]).drop(param1DropTargetDropEvent); 
        } 
      } 
    }
    
    public void dropActionChanged(DropTargetDragEvent param1DropTargetDragEvent) {
      super.dropActionChanged(param1DropTargetDragEvent);
      if (this.listenerList != null) {
        Object[] arrayOfObject = this.listenerList.getListenerList();
        for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
          if (arrayOfObject[i] == DropTargetListener.class)
            ((DropTargetListener)arrayOfObject[i + 1]).dropActionChanged(param1DropTargetDragEvent); 
        } 
      } 
    }
  }
  
  static class TransferAction extends UIAction implements UIResource {
    private static final JavaSecurityAccess javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
    
    private static Object SandboxClipboardKey = new Object();
    
    TransferAction(String param1String) { super(param1String); }
    
    public boolean isEnabled(Object param1Object) { return !(param1Object instanceof JComponent && ((JComponent)param1Object).getTransferHandler() == null); }
    
    public void actionPerformed(final ActionEvent e) {
      Object object = param1ActionEvent.getSource();
      final PrivilegedAction<Void> action = new PrivilegedAction<Void>() {
          public Void run() {
            TransferHandler.TransferAction.this.actionPerformedImpl(e);
            return null;
          }
        };
      AccessControlContext accessControlContext1 = AccessController.getContext();
      AccessControlContext accessControlContext2 = AWTAccessor.getComponentAccessor().getAccessControlContext((Component)object);
      final AccessControlContext eventAcc = AWTAccessor.getAWTEventAccessor().getAccessControlContext(param1ActionEvent);
      if (accessControlContext2 == null) {
        javaSecurityAccess.doIntersectionPrivilege(privilegedAction, accessControlContext1, accessControlContext3);
      } else {
        javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
              public Void run() {
                javaSecurityAccess.doIntersectionPrivilege(action, eventAcc);
                return null;
              }
            }accessControlContext1, accessControlContext2);
      } 
    }
    
    private void actionPerformedImpl(ActionEvent param1ActionEvent) {
      Object object = param1ActionEvent.getSource();
      if (object instanceof JComponent) {
        JComponent jComponent = (JComponent)object;
        TransferHandler transferHandler = jComponent.getTransferHandler();
        Clipboard clipboard = getClipboard(jComponent);
        String str = (String)getValue("Name");
        Transferable transferable = null;
        try {
          if (clipboard != null && transferHandler != null && str != null)
            if ("cut".equals(str)) {
              transferHandler.exportToClipboard(jComponent, clipboard, 2);
            } else if ("copy".equals(str)) {
              transferHandler.exportToClipboard(jComponent, clipboard, 1);
            } else if ("paste".equals(str)) {
              transferable = clipboard.getContents(null);
            }  
        } catch (IllegalStateException illegalStateException) {
          UIManager.getLookAndFeel().provideErrorFeedback(jComponent);
          return;
        } 
        if (transferable != null)
          transferHandler.importData(new TransferHandler.TransferSupport(jComponent, transferable)); 
      } 
    }
    
    private Clipboard getClipboard(JComponent param1JComponent) {
      if (SwingUtilities2.canAccessSystemClipboard())
        return param1JComponent.getToolkit().getSystemClipboard(); 
      Clipboard clipboard = (Clipboard)AppContext.getAppContext().get(SandboxClipboardKey);
      if (clipboard == null) {
        clipboard = new Clipboard("Sandboxed Component Clipboard");
        AppContext.getAppContext().put(SandboxClipboardKey, clipboard);
      } 
      return clipboard;
    }
  }
  
  public static final class TransferSupport {
    private boolean isDrop;
    
    private Component component;
    
    private boolean showDropLocationIsSet;
    
    private boolean showDropLocation;
    
    private int dropAction = -1;
    
    private Object source;
    
    private TransferHandler.DropLocation dropLocation;
    
    private TransferSupport(Component param1Component, DropTargetEvent param1DropTargetEvent) {
      this.isDrop = true;
      setDNDVariables(param1Component, param1DropTargetEvent);
    }
    
    public TransferSupport(Component param1Component, Transferable param1Transferable) {
      if (param1Component == null)
        throw new NullPointerException("component is null"); 
      if (param1Transferable == null)
        throw new NullPointerException("transferable is null"); 
      this.isDrop = false;
      this.component = param1Component;
      this.source = param1Transferable;
    }
    
    private void setDNDVariables(Component param1Component, DropTargetEvent param1DropTargetEvent) {
      assert this.isDrop;
      this.component = param1Component;
      this.source = param1DropTargetEvent;
      this.dropLocation = null;
      this.dropAction = -1;
      this.showDropLocationIsSet = false;
      if (this.source == null)
        return; 
      assert this.source instanceof DropTargetDragEvent || this.source instanceof DropTargetDropEvent;
      Point point = (this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getLocation() : ((DropTargetDropEvent)this.source).getLocation();
      if (SunToolkit.isInstanceOf(param1Component, "javax.swing.text.JTextComponent")) {
        this.dropLocation = SwingAccessor.getJTextComponentAccessor().dropLocationForPoint((JTextComponent)param1Component, point);
      } else if (param1Component instanceof JComponent) {
        this.dropLocation = ((JComponent)param1Component).dropLocationForPoint(point);
      } 
    }
    
    public boolean isDrop() { return this.isDrop; }
    
    public Component getComponent() { return this.component; }
    
    private void assureIsDrop() {
      if (!this.isDrop)
        throw new IllegalStateException("Not a drop"); 
    }
    
    public TransferHandler.DropLocation getDropLocation() {
      assureIsDrop();
      if (this.dropLocation == null) {
        Point point = (this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getLocation() : ((DropTargetDropEvent)this.source).getLocation();
        this.dropLocation = new TransferHandler.DropLocation(point);
      } 
      return this.dropLocation;
    }
    
    public void setShowDropLocation(boolean param1Boolean) {
      assureIsDrop();
      this.showDropLocation = param1Boolean;
      this.showDropLocationIsSet = true;
    }
    
    public void setDropAction(int param1Int) {
      assureIsDrop();
      int i = param1Int & getSourceDropActions();
      if (i != 1 && i != 2 && i != 1073741824)
        throw new IllegalArgumentException("unsupported drop action: " + param1Int); 
      this.dropAction = param1Int;
    }
    
    public int getDropAction() { return (this.dropAction == -1) ? getUserDropAction() : this.dropAction; }
    
    public int getUserDropAction() {
      assureIsDrop();
      return (this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getDropAction() : ((DropTargetDropEvent)this.source).getDropAction();
    }
    
    public int getSourceDropActions() {
      assureIsDrop();
      return (this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getSourceActions() : ((DropTargetDropEvent)this.source).getSourceActions();
    }
    
    public DataFlavor[] getDataFlavors() { return this.isDrop ? ((this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getCurrentDataFlavors() : ((DropTargetDropEvent)this.source).getCurrentDataFlavors()) : ((Transferable)this.source).getTransferDataFlavors(); }
    
    public boolean isDataFlavorSupported(DataFlavor param1DataFlavor) { return this.isDrop ? ((this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).isDataFlavorSupported(param1DataFlavor) : ((DropTargetDropEvent)this.source).isDataFlavorSupported(param1DataFlavor)) : ((Transferable)this.source).isDataFlavorSupported(param1DataFlavor); }
    
    public Transferable getTransferable() { return this.isDrop ? ((this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getTransferable() : ((DropTargetDropEvent)this.source).getTransferable()) : (Transferable)this.source; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\TransferHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */