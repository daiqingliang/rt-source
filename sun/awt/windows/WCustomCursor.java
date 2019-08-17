package sun.awt.windows;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import sun.awt.CustomCursor;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.IntegerComponentRaster;
import sun.awt.image.ToolkitImage;

final class WCustomCursor extends CustomCursor {
  WCustomCursor(Image paramImage, Point paramPoint, String paramString) throws IndexOutOfBoundsException { super(paramImage, paramPoint, paramString); }
  
  protected void createNativeCursor(Image paramImage, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    BufferedImage bufferedImage = new BufferedImage(paramInt1, paramInt2, 1);
    graphics = bufferedImage.getGraphics();
    try {
      if (paramImage instanceof ToolkitImage) {
        ImageRepresentation imageRepresentation = ((ToolkitImage)paramImage).getImageRep();
        imageRepresentation.reconstruct(32);
      } 
      graphics.drawImage(paramImage, 0, 0, paramInt1, paramInt2, null);
    } finally {
      graphics.dispose();
    } 
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBuffer dataBuffer = writableRaster.getDataBuffer();
    int[] arrayOfInt = ((DataBufferInt)dataBuffer).getData();
    byte[] arrayOfByte = new byte[paramInt1 * paramInt2 / 8];
    int i = paramArrayOfInt.length;
    int j;
    for (j = 0; j < i; j++) {
      byte b = j / 8;
      byte b1 = 1 << 7 - j % 8;
      if ((paramArrayOfInt[j] & 0xFF000000) == 0)
        arrayOfByte[b] = (byte)(arrayOfByte[b] | b1); 
    } 
    j = writableRaster.getWidth();
    if (writableRaster instanceof IntegerComponentRaster)
      j = ((IntegerComponentRaster)writableRaster).getScanlineStride(); 
    createCursorIndirect(((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData(), arrayOfByte, j, writableRaster.getWidth(), writableRaster.getHeight(), paramInt3, paramInt4);
  }
  
  private native void createCursorIndirect(int[] paramArrayOfInt, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  static native int getCursorWidth();
  
  static native int getCursorHeight();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WCustomCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */