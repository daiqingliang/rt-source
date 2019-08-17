package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

public class ToolTipManager extends MouseAdapter implements MouseMotionListener {
  Timer enterTimer = new Timer(750, new insideTimerAction(this));
  
  Timer exitTimer;
  
  Timer insideTimer;
  
  String toolTipText;
  
  Point preferredLocation;
  
  JComponent insideComponent;
  
  MouseEvent mouseEvent;
  
  boolean showImmediately;
  
  private static final Object TOOL_TIP_MANAGER_KEY = new Object();
  
  Popup tipWindow;
  
  private Window window;
  
  JToolTip tip;
  
  private Rectangle popupRect = null;
  
  private Rectangle popupFrameRect = null;
  
  boolean enabled = true;
  
  private boolean tipShowing = false;
  
  private FocusListener focusChangeListener = null;
  
  private MouseMotionListener moveBeforeEnterListener = null;
  
  private KeyListener accessibilityKeyListener = null;
  
  private KeyStroke postTip;
  
  private KeyStroke hideTip;
  
  protected boolean lightWeightPopupEnabled = true;
  
  protected boolean heavyWeightPopupEnabled = false;
  
  ToolTipManager() {
    this.enterTimer.setRepeats(false);
    this.exitTimer = new Timer(500, new outsideTimerAction(this));
    this.exitTimer.setRepeats(false);
    this.insideTimer = new Timer(4000, new stillInsideTimerAction(this));
    this.insideTimer.setRepeats(false);
    this.moveBeforeEnterListener = new MoveBeforeEnterListener(null);
    this.accessibilityKeyListener = new AccessibilityKeyListener(null);
    this.hideTip = (this.postTip = KeyStroke.getKeyStroke(112, 2)).getKeyStroke(27, 0);
  }
  
  public void setEnabled(boolean paramBoolean) {
    this.enabled = paramBoolean;
    if (!paramBoolean)
      hideTipWindow(); 
  }
  
  public boolean isEnabled() { return this.enabled; }
  
  public void setLightWeightPopupEnabled(boolean paramBoolean) { this.lightWeightPopupEnabled = paramBoolean; }
  
  public boolean isLightWeightPopupEnabled() { return this.lightWeightPopupEnabled; }
  
  public void setInitialDelay(int paramInt) { this.enterTimer.setInitialDelay(paramInt); }
  
  public int getInitialDelay() { return this.enterTimer.getInitialDelay(); }
  
  public void setDismissDelay(int paramInt) { this.insideTimer.setInitialDelay(paramInt); }
  
  public int getDismissDelay() { return this.insideTimer.getInitialDelay(); }
  
  public void setReshowDelay(int paramInt) { this.exitTimer.setInitialDelay(paramInt); }
  
  public int getReshowDelay() { return this.exitTimer.getInitialDelay(); }
  
  private GraphicsConfiguration getDrawingGC(Point paramPoint) {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] arrayOfGraphicsDevice = graphicsEnvironment.getScreenDevices();
    for (GraphicsDevice graphicsDevice : arrayOfGraphicsDevice) {
      GraphicsConfiguration[] arrayOfGraphicsConfiguration = graphicsDevice.getConfigurations();
      for (GraphicsConfiguration graphicsConfiguration : arrayOfGraphicsConfiguration) {
        Rectangle rectangle = graphicsConfiguration.getBounds();
        if (rectangle.contains(paramPoint))
          return graphicsConfiguration; 
      } 
    } 
    return null;
  }
  
  void showTipWindow() {
    if (this.insideComponent == null || !this.insideComponent.isShowing())
      return; 
    String str = UIManager.getString("ToolTipManager.enableToolTipMode");
    if ("activeApplication".equals(str)) {
      KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      if (keyboardFocusManager.getFocusedWindow() == null)
        return; 
    } 
    if (this.enabled) {
      Point point3;
      Point point2;
      Point point1 = this.insideComponent.getLocationOnScreen();
      if (this.preferredLocation != null) {
        point3 = new Point(point1.x + this.preferredLocation.x, point1.y + this.preferredLocation.y);
      } else {
        point3 = this.mouseEvent.getLocationOnScreen();
      } 
      GraphicsConfiguration graphicsConfiguration = getDrawingGC(point3);
      if (graphicsConfiguration == null) {
        point3 = this.mouseEvent.getLocationOnScreen();
        graphicsConfiguration = getDrawingGC(point3);
        if (graphicsConfiguration == null)
          graphicsConfiguration = this.insideComponent.getGraphicsConfiguration(); 
      } 
      Rectangle rectangle = graphicsConfiguration.getBounds();
      Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration);
      rectangle.x += insets.left;
      rectangle.y += insets.top;
      rectangle.width -= insets.left + insets.right;
      rectangle.height -= insets.top + insets.bottom;
      boolean bool = SwingUtilities.isLeftToRight(this.insideComponent);
      hideTipWindow();
      this.tip = this.insideComponent.createToolTip();
      this.tip.setTipText(this.toolTipText);
      Dimension dimension = this.tip.getPreferredSize();
      if (this.preferredLocation != null) {
        point2 = point3;
        if (!bool)
          point2.x -= dimension.width; 
      } else {
        point2 = new Point(point1.x + this.mouseEvent.getX(), point1.y + this.mouseEvent.getY() + 20);
        if (!bool && point2.x - dimension.width >= 0)
          point2.x -= dimension.width; 
      } 
      if (this.popupRect == null)
        this.popupRect = new Rectangle(); 
      this.popupRect.setBounds(point2.x, point2.y, dimension.width, dimension.height);
      if (point2.x < rectangle.x) {
        point2.x = rectangle.x;
      } else if (point2.x - rectangle.x + dimension.width > rectangle.width) {
        point2.x = rectangle.x + Math.max(0, rectangle.width - dimension.width);
      } 
      if (point2.y < rectangle.y) {
        point2.y = rectangle.y;
      } else if (point2.y - rectangle.y + dimension.height > rectangle.height) {
        point2.y = rectangle.y + Math.max(0, rectangle.height - dimension.height);
      } 
      PopupFactory popupFactory = PopupFactory.getSharedInstance();
      if (this.lightWeightPopupEnabled) {
        int i = getPopupFitHeight(this.popupRect, this.insideComponent);
        int j = getPopupFitWidth(this.popupRect, this.insideComponent);
        if (j > 0 || i > 0) {
          popupFactory.setPopupType(1);
        } else {
          popupFactory.setPopupType(0);
        } 
      } else {
        popupFactory.setPopupType(1);
      } 
      this.tipWindow = popupFactory.getPopup(this.insideComponent, this.tip, point2.x, point2.y);
      popupFactory.setPopupType(0);
      this.tipWindow.show();
      Window window1 = SwingUtilities.windowForComponent(this.insideComponent);
      this.window = SwingUtilities.windowForComponent(this.tip);
      if (this.window != null && this.window != window1) {
        this.window.addMouseListener(this);
      } else {
        this.window = null;
      } 
      this.insideTimer.start();
      this.tipShowing = true;
    } 
  }
  
  void hideTipWindow() {
    if (this.tipWindow != null) {
      if (this.window != null) {
        this.window.removeMouseListener(this);
        this.window = null;
      } 
      this.tipWindow.hide();
      this.tipWindow = null;
      this.tipShowing = false;
      this.tip = null;
      this.insideTimer.stop();
    } 
  }
  
  public static ToolTipManager sharedInstance() {
    Object object = SwingUtilities.appContextGet(TOOL_TIP_MANAGER_KEY);
    if (object instanceof ToolTipManager)
      return (ToolTipManager)object; 
    ToolTipManager toolTipManager = new ToolTipManager();
    SwingUtilities.appContextPut(TOOL_TIP_MANAGER_KEY, toolTipManager);
    return toolTipManager;
  }
  
  public void registerComponent(JComponent paramJComponent) {
    paramJComponent.removeMouseListener(this);
    paramJComponent.addMouseListener(this);
    paramJComponent.removeMouseMotionListener(this.moveBeforeEnterListener);
    paramJComponent.addMouseMotionListener(this.moveBeforeEnterListener);
    paramJComponent.removeKeyListener(this.accessibilityKeyListener);
    paramJComponent.addKeyListener(this.accessibilityKeyListener);
  }
  
  public void unregisterComponent(JComponent paramJComponent) {
    paramJComponent.removeMouseListener(this);
    paramJComponent.removeMouseMotionListener(this.moveBeforeEnterListener);
    paramJComponent.removeKeyListener(this.accessibilityKeyListener);
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) { initiateToolTip(paramMouseEvent); }
  
  private void initiateToolTip(MouseEvent paramMouseEvent) {
    if (paramMouseEvent.getSource() == this.window)
      return; 
    JComponent jComponent = (JComponent)paramMouseEvent.getSource();
    jComponent.removeMouseMotionListener(this.moveBeforeEnterListener);
    this.exitTimer.stop();
    Point point = paramMouseEvent.getPoint();
    if (point.x < 0 || point.x >= jComponent.getWidth() || point.y < 0 || point.y >= jComponent.getHeight())
      return; 
    if (this.insideComponent != null)
      this.enterTimer.stop(); 
    jComponent.removeMouseMotionListener(this);
    jComponent.addMouseMotionListener(this);
    boolean bool = (this.insideComponent == jComponent) ? 1 : 0;
    this.insideComponent = jComponent;
    if (this.tipWindow != null) {
      this.mouseEvent = paramMouseEvent;
      if (this.showImmediately) {
        String str = jComponent.getToolTipText(paramMouseEvent);
        Point point1 = jComponent.getToolTipLocation(paramMouseEvent);
        boolean bool1 = (this.preferredLocation != null) ? this.preferredLocation.equals(point1) : ((point1 == null) ? 1 : 0);
        if (!bool || !this.toolTipText.equals(str) || !bool1) {
          this.toolTipText = str;
          this.preferredLocation = point1;
          showTipWindow();
        } 
      } else {
        this.enterTimer.start();
      } 
    } 
  }
  
  public void mouseExited(MouseEvent paramMouseEvent) { // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: getfield insideComponent : Ljavax/swing/JComponent;
    //   6: ifnonnull -> 9
    //   9: aload_0
    //   10: getfield window : Ljava/awt/Window;
    //   13: ifnull -> 157
    //   16: aload_1
    //   17: invokevirtual getSource : ()Ljava/lang/Object;
    //   20: aload_0
    //   21: getfield window : Ljava/awt/Window;
    //   24: if_acmpne -> 157
    //   27: aload_0
    //   28: getfield insideComponent : Ljavax/swing/JComponent;
    //   31: ifnull -> 157
    //   34: aload_0
    //   35: getfield insideComponent : Ljavax/swing/JComponent;
    //   38: invokevirtual getTopLevelAncestor : ()Ljava/awt/Container;
    //   41: astore_3
    //   42: aload_3
    //   43: ifnull -> 154
    //   46: aload_1
    //   47: invokevirtual getPoint : ()Ljava/awt/Point;
    //   50: astore #4
    //   52: aload #4
    //   54: aload_0
    //   55: getfield window : Ljava/awt/Window;
    //   58: invokestatic convertPointToScreen : (Ljava/awt/Point;Ljava/awt/Component;)V
    //   61: aload #4
    //   63: dup
    //   64: getfield x : I
    //   67: aload_3
    //   68: invokevirtual getX : ()I
    //   71: isub
    //   72: putfield x : I
    //   75: aload #4
    //   77: dup
    //   78: getfield y : I
    //   81: aload_3
    //   82: invokevirtual getY : ()I
    //   85: isub
    //   86: putfield y : I
    //   89: aconst_null
    //   90: aload #4
    //   92: aload_0
    //   93: getfield insideComponent : Ljavax/swing/JComponent;
    //   96: invokestatic convertPoint : (Ljava/awt/Component;Ljava/awt/Point;Ljava/awt/Component;)Ljava/awt/Point;
    //   99: astore #4
    //   101: aload #4
    //   103: getfield x : I
    //   106: iflt -> 152
    //   109: aload #4
    //   111: getfield x : I
    //   114: aload_0
    //   115: getfield insideComponent : Ljavax/swing/JComponent;
    //   118: invokevirtual getWidth : ()I
    //   121: if_icmpge -> 152
    //   124: aload #4
    //   126: getfield y : I
    //   129: iflt -> 152
    //   132: aload #4
    //   134: getfield y : I
    //   137: aload_0
    //   138: getfield insideComponent : Ljavax/swing/JComponent;
    //   141: invokevirtual getHeight : ()I
    //   144: if_icmpge -> 152
    //   147: iconst_0
    //   148: istore_2
    //   149: goto -> 154
    //   152: iconst_1
    //   153: istore_2
    //   154: goto -> 378
    //   157: aload_1
    //   158: invokevirtual getSource : ()Ljava/lang/Object;
    //   161: aload_0
    //   162: getfield insideComponent : Ljavax/swing/JComponent;
    //   165: if_acmpne -> 378
    //   168: aload_0
    //   169: getfield tipWindow : Ljavax/swing/Popup;
    //   172: ifnull -> 378
    //   175: aload_0
    //   176: getfield insideComponent : Ljavax/swing/JComponent;
    //   179: invokestatic getWindowAncestor : (Ljava/awt/Component;)Ljava/awt/Window;
    //   182: astore_3
    //   183: aload_3
    //   184: ifnull -> 378
    //   187: aload_0
    //   188: getfield insideComponent : Ljavax/swing/JComponent;
    //   191: aload_1
    //   192: invokevirtual getPoint : ()Ljava/awt/Point;
    //   195: aload_3
    //   196: invokestatic convertPoint : (Ljava/awt/Component;Ljava/awt/Point;Ljava/awt/Component;)Ljava/awt/Point;
    //   199: astore #4
    //   201: aload_0
    //   202: getfield insideComponent : Ljavax/swing/JComponent;
    //   205: invokevirtual getTopLevelAncestor : ()Ljava/awt/Container;
    //   208: invokevirtual getBounds : ()Ljava/awt/Rectangle;
    //   211: astore #5
    //   213: aload #4
    //   215: dup
    //   216: getfield x : I
    //   219: aload #5
    //   221: getfield x : I
    //   224: iadd
    //   225: putfield x : I
    //   228: aload #4
    //   230: dup
    //   231: getfield y : I
    //   234: aload #5
    //   236: getfield y : I
    //   239: iadd
    //   240: putfield y : I
    //   243: new java/awt/Point
    //   246: dup
    //   247: iconst_0
    //   248: iconst_0
    //   249: invokespecial <init> : (II)V
    //   252: astore #6
    //   254: aload #6
    //   256: aload_0
    //   257: getfield tip : Ljavax/swing/JToolTip;
    //   260: invokestatic convertPointToScreen : (Ljava/awt/Point;Ljava/awt/Component;)V
    //   263: aload #5
    //   265: aload #6
    //   267: getfield x : I
    //   270: putfield x : I
    //   273: aload #5
    //   275: aload #6
    //   277: getfield y : I
    //   280: putfield y : I
    //   283: aload #5
    //   285: aload_0
    //   286: getfield tip : Ljavax/swing/JToolTip;
    //   289: invokevirtual getWidth : ()I
    //   292: putfield width : I
    //   295: aload #5
    //   297: aload_0
    //   298: getfield tip : Ljavax/swing/JToolTip;
    //   301: invokevirtual getHeight : ()I
    //   304: putfield height : I
    //   307: aload #4
    //   309: getfield x : I
    //   312: aload #5
    //   314: getfield x : I
    //   317: if_icmplt -> 376
    //   320: aload #4
    //   322: getfield x : I
    //   325: aload #5
    //   327: getfield x : I
    //   330: aload #5
    //   332: getfield width : I
    //   335: iadd
    //   336: if_icmpge -> 376
    //   339: aload #4
    //   341: getfield y : I
    //   344: aload #5
    //   346: getfield y : I
    //   349: if_icmplt -> 376
    //   352: aload #4
    //   354: getfield y : I
    //   357: aload #5
    //   359: getfield y : I
    //   362: aload #5
    //   364: getfield height : I
    //   367: iadd
    //   368: if_icmpge -> 376
    //   371: iconst_0
    //   372: istore_2
    //   373: goto -> 378
    //   376: iconst_1
    //   377: istore_2
    //   378: iload_2
    //   379: ifeq -> 430
    //   382: aload_0
    //   383: getfield enterTimer : Ljavax/swing/Timer;
    //   386: invokevirtual stop : ()V
    //   389: aload_0
    //   390: getfield insideComponent : Ljavax/swing/JComponent;
    //   393: ifnull -> 404
    //   396: aload_0
    //   397: getfield insideComponent : Ljavax/swing/JComponent;
    //   400: aload_0
    //   401: invokevirtual removeMouseMotionListener : (Ljava/awt/event/MouseMotionListener;)V
    //   404: aload_0
    //   405: aconst_null
    //   406: putfield insideComponent : Ljavax/swing/JComponent;
    //   409: aload_0
    //   410: aconst_null
    //   411: putfield toolTipText : Ljava/lang/String;
    //   414: aload_0
    //   415: aconst_null
    //   416: putfield mouseEvent : Ljava/awt/event/MouseEvent;
    //   419: aload_0
    //   420: invokevirtual hideTipWindow : ()V
    //   423: aload_0
    //   424: getfield exitTimer : Ljavax/swing/Timer;
    //   427: invokevirtual restart : ()V
    //   430: return }
  
  public void mousePressed(MouseEvent paramMouseEvent) {
    hideTipWindow();
    this.enterTimer.stop();
    this.showImmediately = false;
    this.insideComponent = null;
    this.mouseEvent = null;
  }
  
  public void mouseDragged(MouseEvent paramMouseEvent) {}
  
  public void mouseMoved(MouseEvent paramMouseEvent) {
    if (this.tipShowing) {
      checkForTipChange(paramMouseEvent);
    } else if (this.showImmediately) {
      JComponent jComponent = (JComponent)paramMouseEvent.getSource();
      this.toolTipText = jComponent.getToolTipText(paramMouseEvent);
      if (this.toolTipText != null) {
        this.preferredLocation = jComponent.getToolTipLocation(paramMouseEvent);
        this.mouseEvent = paramMouseEvent;
        this.insideComponent = jComponent;
        this.exitTimer.stop();
        showTipWindow();
      } 
    } else {
      this.insideComponent = (JComponent)paramMouseEvent.getSource();
      this.mouseEvent = paramMouseEvent;
      this.toolTipText = null;
      this.enterTimer.restart();
    } 
  }
  
  private void checkForTipChange(MouseEvent paramMouseEvent) {
    JComponent jComponent = (JComponent)paramMouseEvent.getSource();
    String str = jComponent.getToolTipText(paramMouseEvent);
    Point point = jComponent.getToolTipLocation(paramMouseEvent);
    if (str != null || point != null) {
      this.mouseEvent = paramMouseEvent;
      if (((str != null && str.equals(this.toolTipText)) || str == null) && ((point != null && point.equals(this.preferredLocation)) || point == null)) {
        if (this.tipWindow != null) {
          this.insideTimer.restart();
        } else {
          this.enterTimer.restart();
        } 
      } else {
        this.toolTipText = str;
        this.preferredLocation = point;
        if (this.showImmediately) {
          hideTipWindow();
          showTipWindow();
          this.exitTimer.stop();
        } else {
          this.enterTimer.restart();
        } 
      } 
    } else {
      this.toolTipText = null;
      this.preferredLocation = null;
      this.mouseEvent = null;
      this.insideComponent = null;
      hideTipWindow();
      this.enterTimer.stop();
      this.exitTimer.restart();
    } 
  }
  
  static Frame frameForComponent(Component paramComponent) {
    while (!(paramComponent instanceof Frame))
      paramComponent = paramComponent.getParent(); 
    return (Frame)paramComponent;
  }
  
  private FocusListener createFocusChangeListener() { return new FocusAdapter() {
        public void focusLost(FocusEvent param1FocusEvent) {
          ToolTipManager.this.hideTipWindow();
          ToolTipManager.this.insideComponent = null;
          JComponent jComponent = (JComponent)param1FocusEvent.getSource();
          jComponent.removeFocusListener(ToolTipManager.this.focusChangeListener);
        }
      }; }
  
  private int getPopupFitWidth(Rectangle paramRectangle, Component paramComponent) {
    if (paramComponent != null)
      for (Container container = paramComponent.getParent(); container != null; container = container.getParent()) {
        if (container instanceof JFrame || container instanceof JDialog || container instanceof JWindow)
          return getWidthAdjust(container.getBounds(), paramRectangle); 
        if (container instanceof JApplet || container instanceof JInternalFrame) {
          if (this.popupFrameRect == null)
            this.popupFrameRect = new Rectangle(); 
          Point point = container.getLocationOnScreen();
          this.popupFrameRect.setBounds(point.x, point.y, (container.getBounds()).width, (container.getBounds()).height);
          return getWidthAdjust(this.popupFrameRect, paramRectangle);
        } 
      }  
    return 0;
  }
  
  private int getPopupFitHeight(Rectangle paramRectangle, Component paramComponent) {
    if (paramComponent != null)
      for (Container container = paramComponent.getParent(); container != null; container = container.getParent()) {
        if (container instanceof JFrame || container instanceof JDialog || container instanceof JWindow)
          return getHeightAdjust(container.getBounds(), paramRectangle); 
        if (container instanceof JApplet || container instanceof JInternalFrame) {
          if (this.popupFrameRect == null)
            this.popupFrameRect = new Rectangle(); 
          Point point = container.getLocationOnScreen();
          this.popupFrameRect.setBounds(point.x, point.y, (container.getBounds()).width, (container.getBounds()).height);
          return getHeightAdjust(this.popupFrameRect, paramRectangle);
        } 
      }  
    return 0;
  }
  
  private int getHeightAdjust(Rectangle paramRectangle1, Rectangle paramRectangle2) { return (paramRectangle2.y >= paramRectangle1.y && paramRectangle2.y + paramRectangle2.height <= paramRectangle1.y + paramRectangle1.height) ? 0 : (paramRectangle2.y + paramRectangle2.height - paramRectangle1.y + paramRectangle1.height + 5); }
  
  private int getWidthAdjust(Rectangle paramRectangle1, Rectangle paramRectangle2) { return (paramRectangle2.x >= paramRectangle1.x && paramRectangle2.x + paramRectangle2.width <= paramRectangle1.x + paramRectangle1.width) ? 0 : (paramRectangle2.x + paramRectangle2.width - paramRectangle1.x + paramRectangle1.width + 5); }
  
  private void show(JComponent paramJComponent) {
    if (this.tipWindow != null) {
      hideTipWindow();
      this.insideComponent = null;
    } else {
      hideTipWindow();
      this.enterTimer.stop();
      this.exitTimer.stop();
      this.insideTimer.stop();
      this.insideComponent = paramJComponent;
      if (this.insideComponent != null) {
        this.toolTipText = this.insideComponent.getToolTipText();
        this.preferredLocation = new Point(10, this.insideComponent.getHeight() + 10);
        showTipWindow();
        if (this.focusChangeListener == null)
          this.focusChangeListener = createFocusChangeListener(); 
        this.insideComponent.addFocusListener(this.focusChangeListener);
      } 
    } 
  }
  
  private void hide(JComponent paramJComponent) {
    hideTipWindow();
    paramJComponent.removeFocusListener(this.focusChangeListener);
    this.preferredLocation = null;
    this.insideComponent = null;
  }
  
  private class AccessibilityKeyListener extends KeyAdapter {
    private AccessibilityKeyListener() {}
    
    public void keyPressed(KeyEvent param1KeyEvent) {
      if (!param1KeyEvent.isConsumed()) {
        JComponent jComponent = (JComponent)param1KeyEvent.getComponent();
        KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(param1KeyEvent);
        if (ToolTipManager.this.hideTip.equals(keyStroke)) {
          if (ToolTipManager.this.tipWindow != null) {
            ToolTipManager.this.hide(jComponent);
            param1KeyEvent.consume();
          } 
        } else if (ToolTipManager.this.postTip.equals(keyStroke)) {
          ToolTipManager.this.show(jComponent);
          param1KeyEvent.consume();
        } 
      } 
    }
  }
  
  private class MoveBeforeEnterListener extends MouseMotionAdapter {
    private MoveBeforeEnterListener() {}
    
    public void mouseMoved(MouseEvent param1MouseEvent) { ToolTipManager.this.initiateToolTip(param1MouseEvent); }
  }
  
  protected class insideTimerAction implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (ToolTipManager.this.insideComponent != null && ToolTipManager.this.insideComponent.isShowing()) {
        if (ToolTipManager.this.toolTipText == null && ToolTipManager.this.mouseEvent != null) {
          ToolTipManager.this.toolTipText = ToolTipManager.this.insideComponent.getToolTipText(ToolTipManager.this.mouseEvent);
          ToolTipManager.this.preferredLocation = ToolTipManager.this.insideComponent.getToolTipLocation(ToolTipManager.this.mouseEvent);
        } 
        if (ToolTipManager.this.toolTipText != null) {
          ToolTipManager.this.showImmediately = true;
          ToolTipManager.this.showTipWindow();
        } else {
          ToolTipManager.this.insideComponent = null;
          ToolTipManager.this.toolTipText = null;
          ToolTipManager.this.preferredLocation = null;
          ToolTipManager.this.mouseEvent = null;
          ToolTipManager.this.hideTipWindow();
        } 
      } 
    }
  }
  
  protected class outsideTimerAction implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) { ToolTipManager.this.showImmediately = false; }
  }
  
  protected class stillInsideTimerAction implements ActionListener {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      ToolTipManager.this.hideTipWindow();
      ToolTipManager.this.enterTimer.stop();
      ToolTipManager.this.showImmediately = false;
      ToolTipManager.this.insideComponent = null;
      ToolTipManager.this.mouseEvent = null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ToolTipManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */