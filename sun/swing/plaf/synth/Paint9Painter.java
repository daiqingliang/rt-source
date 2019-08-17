package sun.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import sun.swing.CachedPainter;

public class Paint9Painter extends CachedPainter {
  private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
  
  public static final int PAINT_TOP_LEFT = 1;
  
  public static final int PAINT_TOP = 2;
  
  public static final int PAINT_TOP_RIGHT = 4;
  
  public static final int PAINT_LEFT = 8;
  
  public static final int PAINT_CENTER = 16;
  
  public static final int PAINT_RIGHT = 32;
  
  public static final int PAINT_BOTTOM_RIGHT = 64;
  
  public static final int PAINT_BOTTOM = 128;
  
  public static final int PAINT_BOTTOM_LEFT = 256;
  
  public static final int PAINT_ALL = 512;
  
  public static boolean validImage(Image paramImage) { return (paramImage != null && paramImage.getWidth(null) > 0 && paramImage.getHeight(null) > 0); }
  
  public Paint9Painter(int paramInt) { super(paramInt); }
  
  public void paint(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image paramImage, Insets paramInsets1, Insets paramInsets2, PaintType paramPaintType, int paramInt5) {
    if (paramImage == null)
      return; 
    paint(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, new Object[] { paramImage, paramInsets1, paramInsets2, paramPaintType, Integer.valueOf(paramInt5) });
  }
  
  protected void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    byte b = 0;
    while (b < paramArrayOfObject.length) {
      Image image = (Image)paramArrayOfObject[b++];
      Insets insets1 = (Insets)paramArrayOfObject[b++];
      Insets insets2 = (Insets)paramArrayOfObject[b++];
      PaintType paintType = (PaintType)paramArrayOfObject[b++];
      int i = ((Integer)paramArrayOfObject[b++]).intValue();
      paint9(paramGraphics, 0, 0, paramInt1, paramInt2, image, insets1, insets2, paintType, i);
    } 
  }
  
  protected void paint9(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image paramImage, Insets paramInsets1, Insets paramInsets2, PaintType paramPaintType, int paramInt5) {
    if (!validImage(paramImage))
      return; 
    if (paramInsets1 == null)
      paramInsets1 = EMPTY_INSETS; 
    if (paramInsets2 == null)
      paramInsets2 = EMPTY_INSETS; 
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    if (paramPaintType == PaintType.CENTER) {
      paramGraphics.drawImage(paramImage, paramInt1 + (paramInt3 - i) / 2, paramInt2 + (paramInt4 - j) / 2, null);
    } else if (paramPaintType == PaintType.TILE) {
      int k = 0;
      int m = paramInt2;
      int n = paramInt2 + paramInt4;
      while (m < n) {
        int i1 = 0;
        int i2 = paramInt1;
        int i3 = paramInt1 + paramInt3;
        while (i2 < i3) {
          int i4 = Math.min(i3, i2 + i - i1);
          int i5 = Math.min(n, m + j - k);
          paramGraphics.drawImage(paramImage, i2, m, i4, i5, i1, k, i1 + i4 - i2, k + i5 - m, null);
          i2 += i - i1;
          i1 = 0;
        } 
        m += j - k;
        k = 0;
      } 
    } else {
      int k = paramInsets1.top;
      int m = paramInsets1.left;
      int n = paramInsets1.bottom;
      int i1 = paramInsets1.right;
      int i2 = paramInsets2.top;
      int i3 = paramInsets2.left;
      int i4 = paramInsets2.bottom;
      int i5 = paramInsets2.right;
      if (k + n > j)
        i4 = i2 = n = k = Math.max(0, j / 2); 
      if (m + i1 > i)
        i3 = i5 = m = i1 = Math.max(0, i / 2); 
      if (i2 + i4 > paramInt4)
        i2 = i4 = Math.max(0, paramInt4 / 2 - 1); 
      if (i3 + i5 > paramInt3)
        i3 = i5 = Math.max(0, paramInt3 / 2 - 1); 
      boolean bool = (paramPaintType == PaintType.PAINT9_STRETCH);
      if ((paramInt5 & 0x200) != 0)
        paramInt5 = 0x1FF & (paramInt5 ^ 0xFFFFFFFF); 
      if ((paramInt5 & 0x8) != 0)
        drawChunk(paramImage, paramGraphics, bool, paramInt1, paramInt2 + i2, paramInt1 + i3, paramInt2 + paramInt4 - i4, 0, k, m, j - n, false); 
      if ((paramInt5 & true) != 0)
        drawImage(paramImage, paramGraphics, paramInt1, paramInt2, paramInt1 + i3, paramInt2 + i2, 0, 0, m, k); 
      if ((paramInt5 & 0x2) != 0)
        drawChunk(paramImage, paramGraphics, bool, paramInt1 + i3, paramInt2, paramInt1 + paramInt3 - i5, paramInt2 + i2, m, 0, i - i1, k, true); 
      if ((paramInt5 & 0x4) != 0)
        drawImage(paramImage, paramGraphics, paramInt1 + paramInt3 - i5, paramInt2, paramInt1 + paramInt3, paramInt2 + i2, i - i1, 0, i, k); 
      if ((paramInt5 & 0x20) != 0)
        drawChunk(paramImage, paramGraphics, bool, paramInt1 + paramInt3 - i5, paramInt2 + i2, paramInt1 + paramInt3, paramInt2 + paramInt4 - i4, i - i1, k, i, j - n, false); 
      if ((paramInt5 & 0x40) != 0)
        drawImage(paramImage, paramGraphics, paramInt1 + paramInt3 - i5, paramInt2 + paramInt4 - i4, paramInt1 + paramInt3, paramInt2 + paramInt4, i - i1, j - n, i, j); 
      if ((paramInt5 & 0x80) != 0)
        drawChunk(paramImage, paramGraphics, bool, paramInt1 + i3, paramInt2 + paramInt4 - i4, paramInt1 + paramInt3 - i5, paramInt2 + paramInt4, m, j - n, i - i1, j, true); 
      if ((paramInt5 & 0x100) != 0)
        drawImage(paramImage, paramGraphics, paramInt1, paramInt2 + paramInt4 - i4, paramInt1 + i3, paramInt2 + paramInt4, 0, j - n, m, j); 
      if ((paramInt5 & 0x10) != 0)
        drawImage(paramImage, paramGraphics, paramInt1 + i3, paramInt2 + i2, paramInt1 + paramInt3 - i5, paramInt2 + paramInt4 - i4, m, k, i - i1, j - n); 
    } 
  }
  
  private void drawImage(Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
    if (paramInt3 - paramInt1 <= 0 || paramInt4 - paramInt2 <= 0 || paramInt7 - paramInt5 <= 0 || paramInt8 - paramInt6 <= 0)
      return; 
    paramGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, null);
  }
  
  private void drawChunk(Image paramImage, Graphics paramGraphics, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean2) {
    if (paramInt3 - paramInt1 <= 0 || paramInt4 - paramInt2 <= 0 || paramInt7 - paramInt5 <= 0 || paramInt8 - paramInt6 <= 0)
      return; 
    if (paramBoolean1) {
      paramGraphics.drawImage(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, null);
    } else {
      int m;
      int k;
      int i = paramInt7 - paramInt5;
      int j = paramInt8 - paramInt6;
      if (paramBoolean2) {
        k = i;
        m = 0;
      } else {
        k = 0;
        m = j;
      } 
      while (paramInt1 < paramInt3 && paramInt2 < paramInt4) {
        int n = Math.min(paramInt3, paramInt1 + i);
        int i1 = Math.min(paramInt4, paramInt2 + j);
        paramGraphics.drawImage(paramImage, paramInt1, paramInt2, n, i1, paramInt5, paramInt6, paramInt5 + n - paramInt1, paramInt6 + i1 - paramInt2, null);
        paramInt1 += k;
        paramInt2 += m;
      } 
    } 
  }
  
  protected Image createImage(Component paramComponent, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, Object[] paramArrayOfObject) { return (paramGraphicsConfiguration == null) ? new BufferedImage(paramInt1, paramInt2, 2) : paramGraphicsConfiguration.createCompatibleImage(paramInt1, paramInt2, 3); }
  
  public enum PaintType {
    CENTER, TILE, PAINT9_STRETCH, PAINT9_TILE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\plaf\synth\Paint9Painter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */