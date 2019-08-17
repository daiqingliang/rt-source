package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.awt.peer.TrayIconPeer;
import sun.awt.SunToolkit;
import sun.awt.image.IntegerComponentRaster;

final class WTrayIconPeer extends WObjectPeer implements TrayIconPeer {
  static final int TRAY_ICON_WIDTH = 16;
  
  static final int TRAY_ICON_HEIGHT = 16;
  
  static final int TRAY_ICON_MASK_SIZE = 32;
  
  IconObserver observer = new IconObserver();
  
  boolean firstUpdate = true;
  
  Frame popupParent = new Frame("PopupMessageWindow");
  
  PopupMenu popup;
  
  protected void disposeImpl() {
    if (this.popupParent != null)
      this.popupParent.dispose(); 
    this.popupParent.dispose();
    _dispose();
    WToolkit.targetDisposedPeer(this.target, this);
  }
  
  WTrayIconPeer(TrayIcon paramTrayIcon) {
    this.target = paramTrayIcon;
    this.popupParent.addNotify();
    create();
    updateImage();
  }
  
  public void updateImage() {
    Image image = ((TrayIcon)this.target).getImage();
    if (image != null)
      updateNativeImage(image); 
  }
  
  public native void setToolTip(String paramString);
  
  public void showPopupMenu(final int x, final int y) {
    if (isDisposed())
      return; 
    SunToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
          public void run() {
            PopupMenu popupMenu = ((TrayIcon)WTrayIconPeer.this.target).getPopupMenu();
            if (WTrayIconPeer.this.popup != popupMenu) {
              if (WTrayIconPeer.this.popup != null)
                WTrayIconPeer.this.popupParent.remove(WTrayIconPeer.this.popup); 
              if (popupMenu != null)
                WTrayIconPeer.this.popupParent.add(popupMenu); 
              WTrayIconPeer.this.popup = popupMenu;
            } 
            if (WTrayIconPeer.this.popup != null)
              ((WPopupMenuPeer)WTrayIconPeer.this.popup.getPeer()).show(WTrayIconPeer.this.popupParent, new Point(x, y)); 
          }
        });
  }
  
  public void displayMessage(String paramString1, String paramString2, String paramString3) {
    if (paramString1 == null)
      paramString1 = ""; 
    if (paramString2 == null)
      paramString2 = ""; 
    _displayMessage(paramString1, paramString2, paramString3);
  }
  
  void updateNativeImage(Image paramImage) {
    if (isDisposed())
      return; 
    boolean bool = ((TrayIcon)this.target).isImageAutoSize();
    BufferedImage bufferedImage = new BufferedImage(16, 16, 2);
    graphics2D = bufferedImage.createGraphics();
    if (graphics2D != null)
      try {
        graphics2D.setPaintMode();
        graphics2D.drawImage(paramImage, 0, 0, bool ? 16 : paramImage.getWidth(this.observer), bool ? 16 : paramImage.getHeight(this.observer), this.observer);
        createNativeImage(bufferedImage);
        updateNativeIcon(!this.firstUpdate);
        if (this.firstUpdate)
          this.firstUpdate = false; 
      } finally {
        graphics2D.dispose();
      }  
  }
  
  void createNativeImage(BufferedImage paramBufferedImage) {
    WritableRaster writableRaster = paramBufferedImage.getRaster();
    byte[] arrayOfByte = new byte[32];
    int[] arrayOfInt = ((DataBufferInt)writableRaster.getDataBuffer()).getData();
    int i = arrayOfInt.length;
    int j = writableRaster.getWidth();
    for (byte b = 0; b < i; b++) {
      byte b1 = b / 8;
      byte b2 = 1 << 7 - b % 8;
      if ((arrayOfInt[b] & 0xFF000000) == 0 && b1 < arrayOfByte.length)
        arrayOfByte[b1] = (byte)(arrayOfByte[b1] | b2); 
    } 
    if (writableRaster instanceof IntegerComponentRaster)
      j = ((IntegerComponentRaster)writableRaster).getScanlineStride(); 
    setNativeIcon(((DataBufferInt)paramBufferedImage.getRaster().getDataBuffer()).getData(), arrayOfByte, j, writableRaster.getWidth(), writableRaster.getHeight());
  }
  
  void postEvent(AWTEvent paramAWTEvent) { WToolkit.postEvent(WToolkit.targetToAppContext(this.target), paramAWTEvent); }
  
  native void create();
  
  native void _dispose();
  
  native void updateNativeIcon(boolean paramBoolean);
  
  native void setNativeIcon(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
  
  native void _displayMessage(String paramString1, String paramString2, String paramString3);
  
  class IconObserver implements ImageObserver {
    public boolean imageUpdate(Image param1Image, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      if (param1Image != ((TrayIcon)WTrayIconPeer.this.target).getImage() || WTrayIconPeer.this.isDisposed())
        return false; 
      if ((param1Int1 & 0x33) != 0)
        WTrayIconPeer.this.updateNativeImage(param1Image); 
      return ((param1Int1 & 0x20) == 0);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WTrayIconPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */