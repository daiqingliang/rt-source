package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XbmImageDecoder extends ImageDecoder {
  private static byte[] XbmColormap = { -1, -1, -1, 0, 0, 0 };
  
  private static int XbmHints = 30;
  
  public XbmImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream) {
    super(paramInputStreamImageSource, paramInputStream);
    if (!(this.input instanceof BufferedInputStream))
      this.input = new BufferedInputStream(this.input, 80); 
  }
  
  private static void error(String paramString) throws ImageFormatException { throw new ImageFormatException(paramString); }
  
  public void produceImage() throws IOException, ImageFormatException {
    char[] arrayOfChar = new char[80];
    byte b1 = 0;
    byte b = 0;
    int j = 0;
    int k = 0;
    byte b2 = 0;
    byte b3 = 0;
    boolean bool = true;
    byte[] arrayOfByte = null;
    ColorModel colorModel = null;
    int i;
    while (!this.aborted && (i = this.input.read()) != -1) {
      if ((97 <= i && i <= 122) || (65 <= i && i <= 90) || (48 <= i && i <= 57) || i == 35 || i == 95) {
        if (b1 < 78)
          arrayOfChar[b1++] = (char)i; 
        continue;
      } 
      if (b1 > 0) {
        byte b4 = b1;
        b1 = 0;
        if (bool) {
          if (b4 != 7 || arrayOfChar[0] != '#' || arrayOfChar[1] != 'd' || arrayOfChar[2] != 'e' || arrayOfChar[3] != 'f' || arrayOfChar[4] != 'i' || arrayOfChar[5] != 'n' || arrayOfChar[6] != 'e')
            error("Not an XBM file"); 
          bool = false;
        } 
        if (arrayOfChar[b4 - 1] == 'h') {
          b = 1;
          continue;
        } 
        if (arrayOfChar[b4 - 1] == 't' && b4 > 1 && arrayOfChar[b4 - 2] == 'h') {
          b = 2;
          continue;
        } 
        if (b4 > 2 && b < 0 && arrayOfChar[0] == '0' && arrayOfChar[1] == 'x') {
          int n = 0;
          int i1;
          for (i1 = 2; i1 < b4; i1++) {
            i = arrayOfChar[i1];
            if (48 <= i && i <= 57) {
              i -= 48;
            } else if (65 <= i && i <= 90) {
              i = i - 65 + 10;
            } else if (97 <= i && i <= 122) {
              i = i - 97 + 10;
            } else {
              i = 0;
            } 
            n = n * 16 + i;
          } 
          for (i1 = 1; i1 <= 128; i1 <<= 1) {
            if (b2 < k)
              if ((n & i1) != 0) {
                arrayOfByte[b2] = true;
              } else {
                arrayOfByte[b2] = false;
              }  
            b2++;
          } 
          if (b2 >= k) {
            if (setPixels(0, b3, k, 1, colorModel, arrayOfByte, 0, k) <= 0)
              return; 
            b2 = 0;
            if (b3++ >= j)
              break; 
          } 
          continue;
        } 
        int m = 0;
        for (byte b5 = 0; b5 < b4; b5++) {
          if (48 <= (i = arrayOfChar[b5]) && i <= 57) {
            m = m * 10 + i - 48;
          } else {
            m = -1;
            break;
          } 
        } 
        if (m > 0 && b > 0) {
          if (b == 1) {
            k = m;
          } else {
            j = m;
          } 
          if (k == 0 || j == 0) {
            b = 0;
            continue;
          } 
          colorModel = new IndexColorModel(8, 2, XbmColormap, 0, false, 0);
          setDimensions(k, j);
          setColorModel(colorModel);
          setHints(XbmHints);
          headerComplete();
          arrayOfByte = new byte[k];
          b = -1;
        } 
      } 
    } 
    this.input.close();
    imageComplete(3, true);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\XbmImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */