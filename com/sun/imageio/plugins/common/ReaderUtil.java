package com.sun.imageio.plugins.common;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public class ReaderUtil {
  private static void computeUpdatedPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int[] paramArrayOfInt, int paramInt10) {
    boolean bool = false;
    int i = -1;
    int j = -1;
    int k = -1;
    for (int m = 0; m < paramInt8; m++) {
      int n = paramInt7 + m * paramInt9;
      if (n >= paramInt1 && (n - paramInt1) % paramInt6 == 0) {
        if (n >= paramInt1 + paramInt2)
          break; 
        int i1 = paramInt3 + (n - paramInt1) / paramInt6;
        if (i1 >= paramInt4) {
          if (i1 > paramInt5)
            break; 
          if (!bool) {
            i = i1;
            bool = true;
          } else if (j == -1) {
            j = i1;
          } 
          k = i1;
        } 
      } 
    } 
    paramArrayOfInt[paramInt10] = i;
    if (!bool) {
      paramArrayOfInt[paramInt10 + 2] = 0;
    } else {
      paramArrayOfInt[paramInt10 + 2] = k - i + 1;
    } 
    paramArrayOfInt[paramInt10 + 4] = Math.max(j - i, 1);
  }
  
  public static int[] computeUpdatedPixels(Rectangle paramRectangle, Point paramPoint, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12) {
    int[] arrayOfInt = new int[6];
    computeUpdatedPixels(paramRectangle.x, paramRectangle.width, paramPoint.x, paramInt1, paramInt3, paramInt5, paramInt7, paramInt9, paramInt11, arrayOfInt, 0);
    computeUpdatedPixels(paramRectangle.y, paramRectangle.height, paramPoint.y, paramInt2, paramInt4, paramInt6, paramInt8, paramInt10, paramInt12, arrayOfInt, 1);
    return arrayOfInt;
  }
  
  public static int readMultiByteInteger(ImageInputStream paramImageInputStream) throws IOException {
    byte b1 = paramImageInputStream.readByte();
    byte b2;
    for (b2 = b1 & 0x7F; (b1 & 0x80) == 128; b2 |= b1 & 0x7F) {
      b2 <<= 7;
      b1 = paramImageInputStream.readByte();
    } 
    return b2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\ReaderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */