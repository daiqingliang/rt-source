package sun.awt.windows;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.EmbeddedFrame;
import sun.awt.image.ByteInterleavedRaster;
import sun.security.action.GetPropertyAction;

public class WEmbeddedFrame extends EmbeddedFrame {
  private long handle;
  
  private int bandWidth = 0;
  
  private int bandHeight = 0;
  
  private int imgWid = 0;
  
  private int imgHgt = 0;
  
  private static int pScale;
  
  private static final int MAX_BAND_SIZE = 30720;
  
  private boolean isEmbeddedInIE = false;
  
  private static String printScale;
  
  public WEmbeddedFrame() { this(0L); }
  
  @Deprecated
  public WEmbeddedFrame(int paramInt) { this(paramInt); }
  
  public WEmbeddedFrame(long paramLong) {
    this.handle = paramLong;
    if (paramLong != 0L) {
      addNotify();
      show();
    } 
  }
  
  public void addNotify() {
    if (getPeer() == null) {
      WToolkit wToolkit = (WToolkit)Toolkit.getDefaultToolkit();
      setPeer(wToolkit.createEmbeddedFrame(this));
    } 
    super.addNotify();
  }
  
  public long getEmbedderHandle() { return this.handle; }
  
  void print(long paramLong) {
    BufferedImage bufferedImage = null;
    int i = 1;
    int j = 1;
    if (isPrinterDC(paramLong))
      i = j = getPrintScaleFactor(); 
    int k = getHeight();
    if (bufferedImage == null) {
      this.bandWidth = getWidth();
      if (this.bandWidth % 4 != 0)
        this.bandWidth += 4 - this.bandWidth % 4; 
      if (this.bandWidth <= 0)
        return; 
      this.bandHeight = Math.min(30720 / this.bandWidth, k);
      this.imgWid = this.bandWidth * i;
      this.imgHgt = this.bandHeight * j;
      bufferedImage = new BufferedImage(this.imgWid, this.imgHgt, 5);
    } 
    Graphics graphics = bufferedImage.getGraphics();
    graphics.setColor(Color.white);
    Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
    graphics2D.translate(0, this.imgHgt);
    graphics2D.scale(i, -j);
    ByteInterleavedRaster byteInterleavedRaster = (ByteInterleavedRaster)bufferedImage.getRaster();
    byte[] arrayOfByte = byteInterleavedRaster.getDataStorage();
    int m;
    for (m = 0; m < k; m += this.bandHeight) {
      graphics.fillRect(0, 0, this.bandWidth, this.bandHeight);
      printComponents(graphics2D);
      int n = 0;
      int i1 = this.bandHeight;
      int i2 = this.imgHgt;
      if (m + this.bandHeight > k) {
        i1 = k - m;
        i2 = i1 * j;
        n = this.imgWid * (this.imgHgt - i2) * 3;
      } 
      printBand(paramLong, arrayOfByte, n, 0, 0, this.imgWid, i2, 0, m, this.bandWidth, i1);
      graphics2D.translate(0, -this.bandHeight);
    } 
  }
  
  protected static int getPrintScaleFactor() {
    if (pScale != 0)
      return pScale; 
    if (printScale == null)
      printScale = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() { return System.getenv("JAVA2D_PLUGIN_PRINT_SCALE"); }
          }); 
    int i = 4;
    int j = i;
    if (printScale != null)
      try {
        j = Integer.parseInt(printScale);
        if (j > 8 || j < 1)
          j = i; 
      } catch (NumberFormatException numberFormatException) {} 
    pScale = j;
    return pScale;
  }
  
  private native boolean isPrinterDC(long paramLong);
  
  private native void printBand(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9);
  
  private static native void initIDs();
  
  public void activateEmbeddingTopLevel() {}
  
  public void synthesizeWindowActivation(boolean paramBoolean) {
    if (!paramBoolean || EventQueue.isDispatchThread()) {
      ((WFramePeer)getPeer()).emulateActivation(paramBoolean);
    } else {
      Runnable runnable = new Runnable() {
          public void run() { ((WFramePeer)WEmbeddedFrame.this.getPeer()).emulateActivation(true); }
        };
      WToolkit.postEvent(WToolkit.targetToAppContext(this), new InvocationEvent(this, runnable));
    } 
  }
  
  public void registerAccelerator(AWTKeyStroke paramAWTKeyStroke) {}
  
  public void unregisterAccelerator(AWTKeyStroke paramAWTKeyStroke) {}
  
  public void notifyModalBlocked(Dialog paramDialog, boolean paramBoolean) {
    try {
      ComponentPeer componentPeer1 = (ComponentPeer)WToolkit.targetToPeer(this);
      ComponentPeer componentPeer2 = (ComponentPeer)WToolkit.targetToPeer(paramDialog);
      notifyModalBlockedImpl((WEmbeddedFramePeer)componentPeer1, (WWindowPeer)componentPeer2, paramBoolean);
    } catch (Exception exception) {
      exception.printStackTrace(System.err);
    } 
  }
  
  native void notifyModalBlockedImpl(WEmbeddedFramePeer paramWEmbeddedFramePeer, WWindowPeer paramWWindowPeer, boolean paramBoolean);
  
  static  {
    initIDs();
    pScale = 0;
    printScale = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.pluginscalefactor"));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WEmbeddedFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */