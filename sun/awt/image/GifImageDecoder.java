package sun.awt.image;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class GifImageDecoder extends ImageDecoder {
  private static final boolean verbose = false;
  
  private static final int IMAGESEP = 44;
  
  private static final int EXBLOCK = 33;
  
  private static final int EX_GRAPHICS_CONTROL = 249;
  
  private static final int EX_COMMENT = 254;
  
  private static final int EX_APPLICATION = 255;
  
  private static final int TERMINATOR = 59;
  
  private static final int TRANSPARENCYMASK = 1;
  
  private static final int INTERLACEMASK = 64;
  
  private static final int COLORMAPMASK = 128;
  
  int num_global_colors;
  
  byte[] global_colormap;
  
  int trans_pixel = -1;
  
  IndexColorModel global_model;
  
  Hashtable props = new Hashtable();
  
  byte[] saved_image;
  
  IndexColorModel saved_model;
  
  int global_width;
  
  int global_height;
  
  int global_bgpixel;
  
  GifFrame curframe;
  
  private static final int normalflags = 30;
  
  private static final int interlaceflags = 29;
  
  private short[] prefix = new short[4096];
  
  private byte[] suffix = new byte[4096];
  
  private byte[] outCode = new byte[4097];
  
  public GifImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream) { super(paramInputStreamImageSource, paramInputStream); }
  
  private static void error(String paramString) throws ImageFormatException { throw new ImageFormatException(paramString); }
  
  private int readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    while (paramInt2 > 0) {
      try {
        int i = this.input.read(paramArrayOfByte, paramInt1, paramInt2);
        if (i < 0)
          break; 
        paramInt1 += i;
        paramInt2 -= i;
      } catch (IOException iOException) {
        break;
      } 
    } 
    return paramInt2;
  }
  
  private static final int ExtractByte(byte[] paramArrayOfByte, int paramInt) { return paramArrayOfByte[paramInt] & 0xFF; }
  
  private static final int ExtractWord(byte[] paramArrayOfByte, int paramInt) { return paramArrayOfByte[paramInt] & 0xFF | (paramArrayOfByte[paramInt + 1] & 0xFF) << 8; }
  
  public void produceImage() throws IOException, ImageFormatException {
    try {
      readHeader();
      byte b1 = 0;
      byte b2 = 0;
      int i = -1;
      byte b = 0;
      int j = -1;
      boolean bool1 = false;
      boolean bool2 = false;
      while (!this.aborted) {
        String str;
        byte[] arrayOfByte;
        int k;
        switch (k = this.input.read()) {
          case 33:
            switch (k = this.input.read()) {
              case 249:
                arrayOfByte = new byte[6];
                if (readBytes(arrayOfByte, 0, 6) != 0)
                  return; 
                if (arrayOfByte[0] != 4 || arrayOfByte[5] != 0)
                  return; 
                j = ExtractWord(arrayOfByte, 2) * 10;
                if (j > 0 && !bool2) {
                  bool2 = true;
                  ImageFetcher.startingAnimation();
                } 
                b = arrayOfByte[1] >> 2 & 0x7;
                if ((arrayOfByte[1] & true) != 0) {
                  this.trans_pixel = ExtractByte(arrayOfByte, 4);
                  continue;
                } 
                this.trans_pixel = -1;
                continue;
              default:
                bool = false;
                str = "";
                while (true) {
                  int m = this.input.read();
                  if (m <= 0)
                    break; 
                  byte[] arrayOfByte1 = new byte[m];
                  if (readBytes(arrayOfByte1, 0, m) != 0)
                    return; 
                  if (k == 254) {
                    str = str + new String(arrayOfByte1, 0);
                    continue;
                  } 
                  if (k == 255) {
                    if (bool)
                      if (m == 3 && arrayOfByte1[0] == 1) {
                        if (bool1) {
                          ExtractWord(arrayOfByte1, 1);
                        } else {
                          i = ExtractWord(arrayOfByte1, 1);
                          bool1 = true;
                        } 
                      } else {
                        bool = false;
                      }  
                    if ("NETSCAPE2.0".equals(new String(arrayOfByte1, 0)))
                      bool = true; 
                  } 
                } 
                if (k == 254)
                  this.props.put("comment", str); 
                if (bool && !bool2) {
                  bool2 = true;
                  ImageFetcher.startingAnimation();
                } 
                continue;
              case -1:
                break;
            } 
            return;
          case 44:
            if (!bool2)
              this.input.mark(0); 
            try {
              if (!readImage(!b1, b, j))
                return; 
            } catch (Exception bool) {
              Exception exception;
              return;
            } 
            b2++;
            b1++;
            continue;
          default:
            if (b2 == 0)
              return; 
            break;
          case 59:
            break;
        } 
        if (i == 0 || i-- >= 0)
          try {
            if (this.curframe != null) {
              this.curframe.dispose();
              this.curframe = null;
            } 
            this.input.reset();
            this.saved_image = null;
            this.saved_model = null;
            b2 = 0;
            continue;
          } catch (IOException bool) {
            IOException iOException;
            return;
          }  
        imageComplete(3, true);
        return;
      } 
    } finally {
      close();
    } 
  }
  
  private void readHeader() throws IOException, ImageFormatException {
    byte[] arrayOfByte = new byte[13];
    if (readBytes(arrayOfByte, 0, 13) != 0)
      throw new IOException(); 
    if (arrayOfByte[0] != 71 || arrayOfByte[1] != 73 || arrayOfByte[2] != 70)
      error("not a GIF file."); 
    this.global_width = ExtractWord(arrayOfByte, 6);
    this.global_height = ExtractWord(arrayOfByte, 8);
    int i = ExtractByte(arrayOfByte, 10);
    if ((i & 0x80) == 0) {
      this.num_global_colors = 2;
      this.global_bgpixel = 0;
      this.global_colormap = new byte[6];
      this.global_colormap[2] = 0;
      this.global_colormap[1] = 0;
      this.global_colormap[0] = 0;
      this.global_colormap[5] = -1;
      this.global_colormap[4] = -1;
      this.global_colormap[3] = -1;
    } else {
      this.num_global_colors = 1 << (i & 0x7) + 1;
      this.global_bgpixel = ExtractByte(arrayOfByte, 11);
      if (arrayOfByte[12] != 0)
        this.props.put("aspectratio", "" + ((ExtractByte(arrayOfByte, 12) + 15) / 64.0D)); 
      this.global_colormap = new byte[this.num_global_colors * 3];
      if (readBytes(this.global_colormap, 0, this.num_global_colors * 3) != 0)
        throw new IOException(); 
    } 
    this.input.mark(2147483647);
  }
  
  private static native void initIDs() throws IOException, ImageFormatException;
  
  private native boolean parseImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, IndexColorModel paramIndexColorModel);
  
  private int sendPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, ColorModel paramColorModel) {
    int k;
    int i;
    if (paramInt2 < 0) {
      paramInt4 += paramInt2;
      paramInt2 = 0;
    } 
    if (paramInt2 + paramInt4 > this.global_height)
      paramInt4 = this.global_height - paramInt2; 
    if (paramInt4 <= 0)
      return 1; 
    if (paramInt1 < 0) {
      i = -paramInt1;
      paramInt3 += paramInt1;
      k = 0;
    } else {
      i = 0;
      k = paramInt1;
    } 
    if (k + paramInt3 > this.global_width)
      paramInt3 = this.global_width - k; 
    if (paramInt3 <= 0)
      return 1; 
    int j = i + paramInt3;
    int m = paramInt2 * this.global_width + k;
    boolean bool = (this.curframe.disposal_method == 1) ? 1 : 0;
    if (this.trans_pixel >= 0 && !this.curframe.initialframe) {
      if (this.saved_image != null && paramColorModel.equals(this.saved_model)) {
        int n = i;
        while (n < j) {
          byte b = paramArrayOfByte[n];
          if ((b & 0xFF) == this.trans_pixel) {
            paramArrayOfByte[n] = this.saved_image[m];
          } else if (bool) {
            this.saved_image[m] = b;
          } 
          n++;
          m++;
        } 
      } else {
        int n = -1;
        int i1 = 1;
        int i2 = i;
        while (i2 < j) {
          byte b = paramArrayOfByte[i2];
          if ((b & 0xFF) == this.trans_pixel) {
            if (n >= 0) {
              i1 = setPixels(paramInt1 + n, paramInt2, i2 - n, 1, paramColorModel, paramArrayOfByte, n, 0);
              if (i1 == 0)
                break; 
            } 
            n = -1;
          } else {
            if (n < 0)
              n = i2; 
            if (bool)
              this.saved_image[m] = b; 
          } 
          i2++;
          m++;
        } 
        if (n >= 0)
          i1 = setPixels(paramInt1 + n, paramInt2, j - n, 1, paramColorModel, paramArrayOfByte, n, 0); 
        return i1;
      } 
    } else if (bool) {
      System.arraycopy(paramArrayOfByte, i, this.saved_image, m, paramInt3);
    } 
    return setPixels(k, paramInt2, paramInt3, paramInt4, paramColorModel, paramArrayOfByte, i, 0);
  }
  
  private boolean readImage(boolean paramBoolean, int paramInt1, int paramInt2) throws IOException {
    if (this.curframe != null && !this.curframe.dispose()) {
      abort();
      return false;
    } 
    long l = 0L;
    byte[] arrayOfByte1 = new byte[259];
    if (readBytes(arrayOfByte1, 0, 10) != 0)
      throw new IOException(); 
    int i = ExtractWord(arrayOfByte1, 0);
    int j = ExtractWord(arrayOfByte1, 2);
    int k = ExtractWord(arrayOfByte1, 4);
    int m = ExtractWord(arrayOfByte1, 6);
    if (k == 0 && this.global_width != 0)
      k = this.global_width - i; 
    if (m == 0 && this.global_height != 0)
      m = this.global_height - j; 
    boolean bool1 = ((arrayOfByte1[8] & 0x40) != 0);
    IndexColorModel indexColorModel = this.global_model;
    if ((arrayOfByte1[8] & 0x80) != 0) {
      int i1 = 1 << (arrayOfByte1[8] & 0x7) + 1;
      byte[] arrayOfByte = new byte[i1 * 3];
      arrayOfByte[0] = arrayOfByte1[9];
      if (readBytes(arrayOfByte, 1, i1 * 3 - 1) != 0)
        throw new IOException(); 
      if (readBytes(arrayOfByte1, 9, 1) != 0)
        throw new IOException(); 
      if (this.trans_pixel >= i1) {
        i1 = this.trans_pixel + 1;
        arrayOfByte = grow_colormap(arrayOfByte, i1);
      } 
      indexColorModel = new IndexColorModel(8, i1, arrayOfByte, 0, false, this.trans_pixel);
    } else if (indexColorModel == null || this.trans_pixel != indexColorModel.getTransparentPixel()) {
      if (this.trans_pixel >= this.num_global_colors) {
        this.num_global_colors = this.trans_pixel + 1;
        this.global_colormap = grow_colormap(this.global_colormap, this.num_global_colors);
      } 
      indexColorModel = new IndexColorModel(8, this.num_global_colors, this.global_colormap, 0, false, this.trans_pixel);
      this.global_model = indexColorModel;
    } 
    if (paramBoolean) {
      if (this.global_width == 0)
        this.global_width = k; 
      if (this.global_height == 0)
        this.global_height = m; 
      setDimensions(this.global_width, this.global_height);
      setProperties(this.props);
      setColorModel(indexColorModel);
      headerComplete();
    } 
    if (paramInt1 == 1 && this.saved_image == null) {
      this.saved_image = new byte[this.global_width * this.global_height];
      if (m < this.global_height && indexColorModel != null) {
        byte b1 = (byte)indexColorModel.getTransparentPixel();
        if (b1 >= 0) {
          byte[] arrayOfByte = new byte[this.global_width];
          for (byte b2 = 0; b2 < this.global_width; b2++)
            arrayOfByte[b2] = b1; 
          setPixels(0, 0, this.global_width, j, indexColorModel, arrayOfByte, 0, 0);
          setPixels(0, j + m, this.global_width, this.global_height - m - j, indexColorModel, arrayOfByte, 0, 0);
        } 
      } 
    } 
    byte b = bool1 ? 29 : 30;
    setHints(b);
    this.curframe = new GifFrame(this, paramInt1, paramInt2, (this.curframe == null), indexColorModel, i, j, k, m);
    byte[] arrayOfByte2 = new byte[k];
    int n = ExtractByte(arrayOfByte1, 9);
    if (n >= 12)
      return false; 
    boolean bool2 = parseImage(i, j, k, m, bool1, n, arrayOfByte1, arrayOfByte2, indexColorModel);
    if (!bool2)
      abort(); 
    return bool2;
  }
  
  public static byte[] grow_colormap(byte[] paramArrayOfByte, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt * 3];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
    return arrayOfByte;
  }
  
  static  {
    NativeLibLoader.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\GifImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */