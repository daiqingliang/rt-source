package sun.awt;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;

public abstract class CustomCursor extends Cursor {
  protected Image image;
  
  public CustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException {
    super(paramString);
    this.image = paramImage;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Canvas canvas = new Canvas();
    MediaTracker mediaTracker = new MediaTracker(canvas);
    mediaTracker.addImage(paramImage, 0);
    try {
      mediaTracker.waitForAll();
    } catch (InterruptedException interruptedException) {}
    int i = paramImage.getWidth(canvas);
    int j = paramImage.getHeight(canvas);
    if (mediaTracker.isErrorAny() || i < 0 || j < 0)
      paramPoint.x = paramPoint.y = 0; 
    Dimension dimension = toolkit.getBestCursorSize(i, j);
    if ((dimension.width != i || dimension.height != j) && dimension.width != 0 && dimension.height != 0) {
      paramImage = paramImage.getScaledInstance(dimension.width, dimension.height, 1);
      i = dimension.width;
      j = dimension.height;
    } 
    if (paramPoint.x >= i || paramPoint.y >= j || paramPoint.x < 0 || paramPoint.y < 0)
      throw new IndexOutOfBoundsException("invalid hotSpot"); 
    int[] arrayOfInt = new int[i * j];
    ImageProducer imageProducer = paramImage.getSource();
    PixelGrabber pixelGrabber = new PixelGrabber(imageProducer, 0, 0, i, j, arrayOfInt, 0, i);
    try {
      pixelGrabber.grabPixels();
    } catch (InterruptedException interruptedException) {}
    createNativeCursor(this.image, arrayOfInt, i, j, paramPoint.x, paramPoint.y);
  }
  
  protected abstract void createNativeCursor(Image paramImage, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\CustomCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */