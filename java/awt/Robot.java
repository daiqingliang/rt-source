package java.awt;

import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.peer.RobotPeer;
import java.lang.reflect.InvocationTargetException;
import sun.awt.ComponentFactory;
import sun.awt.SunToolkit;
import sun.awt.image.SunWritableRaster;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.security.util.SecurityConstants;

public class Robot {
  private static final int MAX_DELAY = 60000;
  
  private RobotPeer peer;
  
  private boolean isAutoWaitForIdle = false;
  
  private int autoDelay = 0;
  
  private static int LEGAL_BUTTON_MASK = 0;
  
  private DirectColorModel screenCapCM = null;
  
  private Object anchor = new Object();
  
  private RobotDisposer disposer;
  
  public Robot() throws AWTException {
    if (GraphicsEnvironment.isHeadless())
      throw new AWTException("headless environment"); 
    init(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
  }
  
  public Robot(GraphicsDevice paramGraphicsDevice) throws AWTException {
    checkIsScreenDevice(paramGraphicsDevice);
    init(paramGraphicsDevice);
  }
  
  private void init(GraphicsDevice paramGraphicsDevice) throws AWTException {
    checkRobotAllowed();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (toolkit instanceof ComponentFactory) {
      this.peer = ((ComponentFactory)toolkit).createRobot(this, paramGraphicsDevice);
      this.disposer = new RobotDisposer(this.peer);
      Disposer.addRecord(this.anchor, this.disposer);
    } 
    initLegalButtonMask();
  }
  
  private static void initLegalButtonMask() throws AWTException {
    if (LEGAL_BUTTON_MASK != 0)
      return; 
    int i = 0;
    if (Toolkit.getDefaultToolkit().areExtraMouseButtonsEnabled() && Toolkit.getDefaultToolkit() instanceof SunToolkit) {
      int j = ((SunToolkit)Toolkit.getDefaultToolkit()).getNumberOfButtons();
      for (byte b = 0; b < j; b++)
        i |= InputEvent.getMaskForButton(b + true); 
    } 
    i |= 0x1C1C;
    LEGAL_BUTTON_MASK = i;
  }
  
  private void checkRobotAllowed() throws AWTException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.CREATE_ROBOT_PERMISSION); 
  }
  
  private void checkIsScreenDevice(GraphicsDevice paramGraphicsDevice) throws AWTException {
    if (paramGraphicsDevice == null || paramGraphicsDevice.getType() != 0)
      throw new IllegalArgumentException("not a valid screen device"); 
  }
  
  public void mouseMove(int paramInt1, int paramInt2) {
    this.peer.mouseMove(paramInt1, paramInt2);
    afterEvent();
  }
  
  public void mousePress(int paramInt) {
    checkButtonsArgument(paramInt);
    this.peer.mousePress(paramInt);
    afterEvent();
  }
  
  public void mouseRelease(int paramInt) {
    checkButtonsArgument(paramInt);
    this.peer.mouseRelease(paramInt);
    afterEvent();
  }
  
  private void checkButtonsArgument(int paramInt) {
    if ((paramInt | LEGAL_BUTTON_MASK) != LEGAL_BUTTON_MASK)
      throw new IllegalArgumentException("Invalid combination of button flags"); 
  }
  
  public void mouseWheel(int paramInt) {
    this.peer.mouseWheel(paramInt);
    afterEvent();
  }
  
  public void keyPress(int paramInt) {
    checkKeycodeArgument(paramInt);
    this.peer.keyPress(paramInt);
    afterEvent();
  }
  
  public void keyRelease(int paramInt) {
    checkKeycodeArgument(paramInt);
    this.peer.keyRelease(paramInt);
    afterEvent();
  }
  
  private void checkKeycodeArgument(int paramInt) {
    if (paramInt == 0)
      throw new IllegalArgumentException("Invalid key code"); 
  }
  
  public Color getPixelColor(int paramInt1, int paramInt2) { return new Color(this.peer.getRGBPixel(paramInt1, paramInt2)); }
  
  public BufferedImage createScreenCapture(Rectangle paramRectangle) {
    checkScreenCaptureAllowed();
    checkValidRect(paramRectangle);
    if (this.screenCapCM == null)
      this.screenCapCM = new DirectColorModel(24, 16711680, 65280, 255); 
    Toolkit.getDefaultToolkit().sync();
    int[] arrayOfInt2 = new int[3];
    int[] arrayOfInt1 = this.peer.getRGBPixels(paramRectangle);
    DataBufferInt dataBufferInt = new DataBufferInt(arrayOfInt1, arrayOfInt1.length);
    arrayOfInt2[0] = this.screenCapCM.getRedMask();
    arrayOfInt2[1] = this.screenCapCM.getGreenMask();
    arrayOfInt2[2] = this.screenCapCM.getBlueMask();
    WritableRaster writableRaster = Raster.createPackedRaster(dataBufferInt, paramRectangle.width, paramRectangle.height, paramRectangle.width, arrayOfInt2, null);
    SunWritableRaster.makeTrackable(dataBufferInt);
    return new BufferedImage(this.screenCapCM, writableRaster, false, null);
  }
  
  private static void checkValidRect(Rectangle paramRectangle) {
    if (paramRectangle.width <= 0 || paramRectangle.height <= 0)
      throw new IllegalArgumentException("Rectangle width and height must be > 0"); 
  }
  
  private static void checkScreenCaptureAllowed() throws AWTException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.AWT.READ_DISPLAY_PIXELS_PERMISSION); 
  }
  
  private void afterEvent() throws AWTException {
    autoWaitForIdle();
    autoDelay();
  }
  
  public boolean isAutoWaitForIdle() { return this.isAutoWaitForIdle; }
  
  public void setAutoWaitForIdle(boolean paramBoolean) { this.isAutoWaitForIdle = paramBoolean; }
  
  private void autoWaitForIdle() throws AWTException {
    if (this.isAutoWaitForIdle)
      waitForIdle(); 
  }
  
  public int getAutoDelay() { return this.autoDelay; }
  
  public void setAutoDelay(int paramInt) {
    checkDelayArgument(paramInt);
    this.autoDelay = paramInt;
  }
  
  private void autoDelay() throws AWTException { delay(this.autoDelay); }
  
  public void delay(int paramInt) {
    checkDelayArgument(paramInt);
    try {
      Thread.sleep(paramInt);
    } catch (InterruptedException interruptedException) {
      interruptedException.printStackTrace();
    } 
  }
  
  private void checkDelayArgument(int paramInt) {
    if (paramInt < 0 || paramInt > 60000)
      throw new IllegalArgumentException("Delay must be to 0 to 60,000ms"); 
  }
  
  public void waitForIdle() throws AWTException {
    checkNotDispatchThread();
    try {
      SunToolkit.flushPendingEvents();
      EventQueue.invokeAndWait(new Runnable() {
            public void run() throws AWTException {}
          });
    } catch (InterruptedException interruptedException) {
      System.err.println("Robot.waitForIdle, non-fatal exception caught:");
      interruptedException.printStackTrace();
    } catch (InvocationTargetException invocationTargetException) {
      System.err.println("Robot.waitForIdle, non-fatal exception caught:");
      invocationTargetException.printStackTrace();
    } 
  }
  
  private void checkNotDispatchThread() throws AWTException {
    if (EventQueue.isDispatchThread())
      throw new IllegalThreadStateException("Cannot call method from the event dispatcher thread"); 
  }
  
  public String toString() {
    String str = "autoDelay = " + getAutoDelay() + ", autoWaitForIdle = " + isAutoWaitForIdle();
    return getClass().getName() + "[ " + str + " ]";
  }
  
  static class RobotDisposer implements DisposerRecord {
    private final RobotPeer peer;
    
    public RobotDisposer(RobotPeer param1RobotPeer) { this.peer = param1RobotPeer; }
    
    public void dispose() throws AWTException {
      if (this.peer != null)
        this.peer.dispose(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Robot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */