package sun.awt.image;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class PNGImageDecoder extends ImageDecoder {
  private static final int GRAY = 0;
  
  private static final int PALETTE = 1;
  
  private static final int COLOR = 2;
  
  private static final int ALPHA = 4;
  
  private static final int bKGDChunk = 1649100612;
  
  private static final int cHRMChunk = 1665684045;
  
  private static final int gAMAChunk = 1732332865;
  
  private static final int hISTChunk = 1749635924;
  
  private static final int IDATChunk = 1229209940;
  
  private static final int IENDChunk = 1229278788;
  
  private static final int IHDRChunk = 1229472850;
  
  private static final int PLTEChunk = 1347179589;
  
  private static final int pHYsChunk = 1883789683;
  
  private static final int sBITChunk = 1933723988;
  
  private static final int tEXtChunk = 1950701684;
  
  private static final int tIMEChunk = 1950960965;
  
  private static final int tRNSChunk = 1951551059;
  
  private static final int zTXtChunk = 2052348020;
  
  private int width;
  
  private int height;
  
  private int bitDepth;
  
  private int colorType;
  
  private int compressionMethod;
  
  private int filterMethod;
  
  private int interlaceMethod;
  
  private int gamma = 100000;
  
  private Hashtable properties;
  
  private ColorModel cm;
  
  private byte[] red_map;
  
  private byte[] green_map;
  
  private byte[] blue_map;
  
  private byte[] alpha_map;
  
  private int transparentPixel = -1;
  
  private byte[] transparentPixel_16 = null;
  
  private static ColorModel[] greyModels = new ColorModel[4];
  
  private static final byte[] startingRow = { 0, 0, 0, 4, 0, 2, 0, 1 };
  
  private static final byte[] startingCol = { 0, 0, 4, 0, 2, 0, 1, 0 };
  
  private static final byte[] rowIncrement = { 1, 8, 8, 8, 4, 4, 2, 2 };
  
  private static final byte[] colIncrement = { 1, 8, 8, 4, 4, 2, 2, 1 };
  
  private static final byte[] blockHeight = { 1, 8, 8, 4, 4, 2, 2, 1 };
  
  private static final byte[] blockWidth = { 1, 8, 4, 4, 2, 2, 1, 1 };
  
  int pos;
  
  int limit;
  
  int chunkStart;
  
  int chunkKey;
  
  int chunkLength;
  
  int chunkCRC;
  
  boolean seenEOF;
  
  private static final byte[] signature = { -119, 80, 78, 71, 13, 10, 26, 10 };
  
  PNGFilterInputStream inputStream;
  
  InputStream underlyingInputStream;
  
  byte[] inbuf = new byte[4096];
  
  private static boolean checkCRC = true;
  
  private static final int[] crc_table = new int[256];
  
  private void property(String paramString, Object paramObject) {
    if (paramObject == null)
      return; 
    if (this.properties == null)
      this.properties = new Hashtable(); 
    this.properties.put(paramString, paramObject);
  }
  
  private void property(String paramString, float paramFloat) { property(paramString, new Float(paramFloat)); }
  
  private final void pngassert(boolean paramBoolean) throws IOException {
    if (!paramBoolean) {
      PNGException pNGException = new PNGException("Broken file");
      pNGException.printStackTrace();
      throw pNGException;
    } 
  }
  
  protected boolean handleChunk(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) throws IOException {
    int k;
    int j;
    byte b2;
    byte b1;
    int i;
    Color color;
    switch (paramInt1) {
      case 1649100612:
        color = null;
        switch (this.colorType) {
          case 2:
          case 6:
            pngassert((paramInt3 == 6));
            color = new Color(paramArrayOfByte[paramInt2] & 0xFF, paramArrayOfByte[paramInt2 + 2] & 0xFF, paramArrayOfByte[paramInt2 + 4] & 0xFF);
            break;
          case 3:
          case 7:
            pngassert((paramInt3 == 1));
            b1 = paramArrayOfByte[paramInt2] & 0xFF;
            pngassert((this.red_map != null && b1 < this.red_map.length));
            color = new Color(this.red_map[b1] & 0xFF, this.green_map[b1] & 0xFF, this.blue_map[b1] & 0xFF);
            break;
          case 0:
          case 4:
            pngassert((paramInt3 == 2));
            b2 = paramArrayOfByte[paramInt2] & 0xFF;
            color = new Color(b2, b2, b2);
            break;
        } 
        if (color != null)
          property("background", color); 
        break;
      case 1665684045:
        property("chromaticities", new Chromaticities(getInt(paramInt2), getInt(paramInt2 + 4), getInt(paramInt2 + 8), getInt(paramInt2 + 12), getInt(paramInt2 + 16), getInt(paramInt2 + 20), getInt(paramInt2 + 24), getInt(paramInt2 + 28)));
        break;
      case 1732332865:
        if (paramInt3 != 4)
          throw new PNGException("bogus gAMA"); 
        this.gamma = getInt(paramInt2);
        if (this.gamma != 100000)
          property("gamma", this.gamma / 100000.0F); 
        break;
      case 1229209940:
        return false;
      case 1229472850:
        if (paramInt3 != 13 || (this.width = getInt(paramInt2)) == 0 || (this.height = getInt(paramInt2 + 4)) == 0)
          throw new PNGException("bogus IHDR"); 
        this.bitDepth = getByte(paramInt2 + 8);
        this.colorType = getByte(paramInt2 + 9);
        this.compressionMethod = getByte(paramInt2 + 10);
        this.filterMethod = getByte(paramInt2 + 11);
        this.interlaceMethod = getByte(paramInt2 + 12);
        break;
      case 1347179589:
        i = paramInt3 / 3;
        this.red_map = new byte[i];
        this.green_map = new byte[i];
        this.blue_map = new byte[i];
        b2 = 0;
        for (k = paramInt2; b2 < i; k += 3) {
          this.red_map[b2] = paramArrayOfByte[k];
          this.green_map[b2] = paramArrayOfByte[k + 1];
          this.blue_map[b2] = paramArrayOfByte[k + 2];
          b2++;
        } 
        break;
      case 1950701684:
        for (i = 0; i < paramInt3 && paramArrayOfByte[paramInt2 + i] != 0; i++);
        if (i < paramInt3) {
          String str1 = new String(paramArrayOfByte, paramInt2, i);
          String str2 = new String(paramArrayOfByte, paramInt2 + i + 1, paramInt3 - i - 1);
          property(str1, str2);
        } 
        break;
      case 1950960965:
        property("modtime", (new GregorianCalendar(getShort(paramInt2 + 0), getByte(paramInt2 + 2) - 1, getByte(paramInt2 + 3), getByte(paramInt2 + 4), getByte(paramInt2 + 5), getByte(paramInt2 + 6))).getTime());
        break;
      case 1951551059:
        switch (this.colorType) {
          case 3:
          case 7:
            j = paramInt3;
            if (this.red_map != null)
              j = this.red_map.length; 
            this.alpha_map = new byte[j];
            System.arraycopy(paramArrayOfByte, paramInt2, this.alpha_map, 0, (paramInt3 < j) ? paramInt3 : j);
            while (--j >= paramInt3)
              this.alpha_map[j] = -1; 
            break;
          case 2:
          case 6:
            pngassert((paramInt3 == 6));
            if (this.bitDepth == 16) {
              this.transparentPixel_16 = new byte[6];
              for (k = 0; k < 6; k++)
                this.transparentPixel_16[k] = (byte)getByte(paramInt2 + k); 
              break;
            } 
            this.transparentPixel = (getShort(paramInt2 + 0) & 0xFF) << 16 | (getShort(paramInt2 + 2) & 0xFF) << 8 | getShort(paramInt2 + 4) & 0xFF;
            break;
          case 0:
          case 4:
            pngassert((paramInt3 == 2));
            k = getShort(paramInt2);
            k = 0xFF & ((this.bitDepth == 16) ? (k >> 8) : k);
            this.transparentPixel = k << 16 | k << 8 | k;
            break;
        } 
        break;
    } 
    return true;
  }
  
  public void produceImage() throws IOException, ImageFormatException {
    try {
      byte b6;
      byte b5;
      byte b;
      for (byte b1 = 0; b1 < signature.length; b1++) {
        if ((signature[b1] & 0xFF) != this.underlyingInputStream.read())
          throw new PNGException("Chunk signature mismatch"); 
      } 
      bufferedInputStream = new BufferedInputStream(new InflaterInputStream(this.inputStream, new Inflater()));
      getData();
      byte[] arrayOfByte = null;
      int[] arrayOfInt = null;
      int i = this.width;
      byte b2 = 0;
      switch (this.bitDepth) {
        case 1:
          b2 = 0;
          break;
        case 2:
          b2 = 1;
          break;
        case 4:
          b2 = 2;
          break;
        case 8:
          b2 = 3;
          break;
        case 16:
          b2 = 4;
          break;
        default:
          throw new PNGException("invalid depth");
      } 
      if (this.interlaceMethod != 0) {
        i *= this.height;
        b = this.width;
      } else {
        b = 0;
      } 
      int j = this.colorType | this.bitDepth << 3;
      byte b3 = (1 << ((this.bitDepth >= 8) ? 8 : this.bitDepth)) - 1;
      switch (this.colorType) {
        case 3:
        case 7:
          if (this.red_map == null)
            throw new PNGException("palette expected"); 
          if (this.alpha_map == null) {
            this.cm = new IndexColorModel(this.bitDepth, this.red_map.length, this.red_map, this.green_map, this.blue_map);
          } else {
            this.cm = new IndexColorModel(this.bitDepth, this.red_map.length, this.red_map, this.green_map, this.blue_map, this.alpha_map);
          } 
          arrayOfByte = new byte[i];
          break;
        case 0:
          b4 = (b2 >= 4) ? 3 : b2;
          if ((this.cm = greyModels[b4]) == null) {
            byte b7 = 1 << 1 << b4;
            byte[] arrayOfByte1 = new byte[b7];
            for (byte b8 = 0; b8 < b7; b8++)
              arrayOfByte1[b8] = (byte)('ÿ' * b8 / (b7 - 1)); 
            if (this.transparentPixel == -1) {
              this.cm = new IndexColorModel(this.bitDepth, arrayOfByte1.length, arrayOfByte1, arrayOfByte1, arrayOfByte1);
            } else {
              this.cm = new IndexColorModel(this.bitDepth, arrayOfByte1.length, arrayOfByte1, arrayOfByte1, arrayOfByte1, this.transparentPixel & 0xFF);
            } 
            greyModels[b4] = this.cm;
          } 
          arrayOfByte = new byte[i];
          break;
        case 2:
        case 4:
        case 6:
          this.cm = ColorModel.getRGBdefault();
          arrayOfInt = new int[i];
          break;
        default:
          throw new PNGException("invalid color type");
      } 
      setDimensions(this.width, this.height);
      setColorModel(this.cm);
      byte b4 = (this.interlaceMethod != 0) ? 6 : 30;
      setHints(b4);
      headerComplete();
      int k = ((this.colorType & true) != 0) ? 1 : ((((this.colorType & 0x2) != 0) ? 3 : 1) + (((this.colorType & 0x4) != 0) ? 1 : 0));
      int m = k * this.bitDepth;
      int n = m + 7 >> 3;
      if (this.interlaceMethod == 0) {
        b5 = -1;
        b6 = 0;
      } else {
        b5 = 0;
        b6 = 7;
      } 
      while (++b5 <= b6) {
        byte b7 = startingRow[b5];
        byte b8 = rowIncrement[b5];
        byte b9 = colIncrement[b5];
        byte b10 = blockWidth[b5];
        byte b11 = blockHeight[b5];
        byte b12 = startingCol[b5];
        int i1 = (this.width - b12 + b9 - 1) / b9;
        int i2 = i1 * m + 7 >> 3;
        if (i2 == 0)
          continue; 
        byte b13 = (this.interlaceMethod == 0) ? (b8 * this.width) : 0;
        byte b14 = b * b7;
        boolean bool = true;
        byte[] arrayOfByte1 = new byte[i2];
        byte[] arrayOfByte2 = new byte[i2];
        while (b7 < this.height) {
          int i3 = bufferedInputStream.read();
          int i4;
          for (i4 = 0; i4 < i2; i4 += i6) {
            int i6 = bufferedInputStream.read(arrayOfByte1, i4, i2 - i4);
            if (i6 <= 0)
              throw new PNGException("missing data"); 
          } 
          filterRow(arrayOfByte1, bool ? null : arrayOfByte2, i3, i2, n);
          byte b15 = b12;
          byte b16 = 0;
          int i5 = 0;
          while (b15 < this.width) {
            if (arrayOfInt != null) {
              byte b17;
              boolean bool1;
              switch (j) {
                case 70:
                  arrayOfInt[b15 + b14] = (arrayOfByte1[b16] & 0xFF) << 16 | (arrayOfByte1[b16 + true] & 0xFF) << 8 | arrayOfByte1[b16 + 2] & 0xFF | (arrayOfByte1[b16 + 3] & 0xFF) << 24;
                  b16 += true;
                  break;
                case 134:
                  arrayOfInt[b15 + b14] = (arrayOfByte1[b16] & 0xFF) << 16 | (arrayOfByte1[b16 + 2] & 0xFF) << 8 | arrayOfByte1[b16 + 4] & 0xFF | (arrayOfByte1[b16 + 6] & 0xFF) << 24;
                  b16 += true;
                  break;
                case 66:
                  i5 = (arrayOfByte1[b16] & 0xFF) << 16 | (arrayOfByte1[b16 + true] & 0xFF) << 8 | arrayOfByte1[b16 + 2] & 0xFF;
                  if (i5 != this.transparentPixel)
                    i5 |= 0xFF000000; 
                  arrayOfInt[b15 + b14] = i5;
                  b16 += true;
                  break;
                case 130:
                  i5 = (arrayOfByte1[b16] & 0xFF) << 16 | (arrayOfByte1[b16 + 2] & 0xFF) << 8 | arrayOfByte1[b16 + 4] & 0xFF;
                  bool1 = (this.transparentPixel_16 != null) ? 1 : 0;
                  for (b17 = 0; bool1 && b17 < 6; b17++)
                    bool1 &= (((arrayOfByte1[b16 + b17] & 0xFF) == (this.transparentPixel_16[b17] & 0xFF)) ? 1 : 0); 
                  if (!bool1)
                    i5 |= 0xFF000000; 
                  arrayOfInt[b15 + b14] = i5;
                  b16 += 6;
                  break;
                case 68:
                  b17 = arrayOfByte1[b16] & 0xFF;
                  arrayOfInt[b15 + b14] = b17 << 16 | b17 << 8 | b17 | (arrayOfByte1[b16 + 1] & 0xFF) << 24;
                  b16 += 2;
                  break;
                case 132:
                  b17 = arrayOfByte1[b16] & 0xFF;
                  arrayOfInt[b15 + b14] = b17 << 16 | b17 << 8 | b17 | (arrayOfByte1[b16 + 2] & 0xFF) << 24;
                  b16 += 4;
                  break;
                default:
                  throw new PNGException("illegal type/depth");
              } 
            } else {
              switch (this.bitDepth) {
                case 1:
                  arrayOfByte[b15 + b14] = (byte)(arrayOfByte1[b16 >> 3] >> 7 - (b16 & 0x7) & true);
                  b16++;
                  break;
                case 2:
                  arrayOfByte[b15 + b14] = (byte)(arrayOfByte1[b16 >> 2] >> (3 - (b16 & 0x3)) * 2 & 0x3);
                  b16++;
                  break;
                case 4:
                  arrayOfByte[b15 + b14] = (byte)(arrayOfByte1[b16 >> 1] >> (1 - (b16 & true)) * 4 & 0xF);
                  b16++;
                  break;
                case 8:
                  arrayOfByte[b15 + b14] = arrayOfByte1[b16++];
                  break;
                case 16:
                  arrayOfByte[b15 + b14] = arrayOfByte1[b16];
                  b16 += 2;
                  break;
                default:
                  throw new PNGException("illegal type/depth");
              } 
            } 
            b15 += b9;
          } 
          if (this.interlaceMethod == 0)
            if (arrayOfInt != null) {
              sendPixels(0, b7, this.width, 1, arrayOfInt, 0, this.width);
            } else {
              sendPixels(0, b7, this.width, 1, arrayOfByte, 0, this.width);
            }  
          b7 += b8;
          b14 += b8 * b;
          byte[] arrayOfByte3 = arrayOfByte1;
          arrayOfByte1 = arrayOfByte2;
          arrayOfByte2 = arrayOfByte3;
          bool = false;
        } 
        if (this.interlaceMethod != 0) {
          if (arrayOfInt != null) {
            sendPixels(0, 0, this.width, this.height, arrayOfInt, 0, this.width);
            continue;
          } 
          sendPixels(0, 0, this.width, this.height, arrayOfByte, 0, this.width);
        } 
      } 
      imageComplete(3, true);
    } catch (IOException iOException) {
      if (!this.aborted) {
        property("error", iOException);
        imageComplete(3, true);
        throw iOException;
      } 
    } finally {
      try {
        close();
      } catch (Throwable throwable) {}
    } 
  }
  
  private boolean sendPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5, int paramInt6) {
    int i = setPixels(paramInt1, paramInt2, paramInt3, paramInt4, this.cm, paramArrayOfInt, paramInt5, paramInt6);
    if (i <= 0)
      this.aborted = true; 
    return !this.aborted;
  }
  
  private boolean sendPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6) {
    int i = setPixels(paramInt1, paramInt2, paramInt3, paramInt4, this.cm, paramArrayOfByte, paramInt5, paramInt6);
    if (i <= 0)
      this.aborted = true; 
    return !this.aborted;
  }
  
  private void filterRow(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    int i = 0;
    switch (paramInt1) {
      case 0:
        return;
      case 1:
        for (i = paramInt3; i < paramInt2; i++)
          paramArrayOfByte1[i] = (byte)(paramArrayOfByte1[i] + paramArrayOfByte1[i - paramInt3]); 
      case 2:
        if (paramArrayOfByte2 != null)
          while (i < paramInt2) {
            paramArrayOfByte1[i] = (byte)(paramArrayOfByte1[i] + paramArrayOfByte2[i]);
            i++;
          }  
      case 3:
        if (paramArrayOfByte2 != null) {
          while (i < paramInt3) {
            paramArrayOfByte1[i] = (byte)(paramArrayOfByte1[i] + ((0xFF & paramArrayOfByte2[i]) >> '\001'));
            i++;
          } 
          while (i < paramInt2) {
            paramArrayOfByte1[i] = (byte)(paramArrayOfByte1[i] + ((paramArrayOfByte2[i] & 0xFF) + (paramArrayOfByte1[i - paramInt3] & 0xFF) >> 1));
            i++;
          } 
        } else {
          for (i = paramInt3; i < paramInt2; i++)
            paramArrayOfByte1[i] = (byte)(paramArrayOfByte1[i] + ((paramArrayOfByte1[i - paramInt3] & 0xFF) >> 1)); 
        } 
      case 4:
        if (paramArrayOfByte2 != null) {
          while (i < paramInt3) {
            paramArrayOfByte1[i] = (byte)(paramArrayOfByte1[i] + paramArrayOfByte2[i]);
            i++;
          } 
          while (i < paramInt2) {
            byte b1 = paramArrayOfByte1[i - paramInt3] & 0xFF;
            byte b2 = paramArrayOfByte2[i] & 0xFF;
            byte b3 = paramArrayOfByte2[i - paramInt3] & 0xFF;
            byte b4 = b1 + b2 - b3;
            byte b5 = (b4 > b1) ? (b4 - b1) : (b1 - b4);
            byte b6 = (b4 > b2) ? (b4 - b2) : (b2 - b4);
            byte b7 = (b4 > b3) ? (b4 - b3) : (b3 - b4);
            paramArrayOfByte1[i] = (byte)(paramArrayOfByte1[i] + ((b5 <= b6 && b5 <= b7) ? b1 : ((b6 <= b7) ? b2 : b3)));
            i++;
          } 
        } else {
          for (i = paramInt3; i < paramInt2; i++)
            paramArrayOfByte1[i] = (byte)(paramArrayOfByte1[i] + paramArrayOfByte1[i - paramInt3]); 
        } 
    } 
    throw new PNGException("Illegal filter");
  }
  
  public PNGImageDecoder(InputStreamImageSource paramInputStreamImageSource, InputStream paramInputStream) throws IOException {
    super(paramInputStreamImageSource, paramInputStream);
    this.inputStream = new PNGFilterInputStream(this, paramInputStream);
    this.underlyingInputStream = this.inputStream.underlyingInputStream;
  }
  
  private void fill() throws IOException, ImageFormatException {
    if (!this.seenEOF) {
      if (this.pos > 0 && this.pos < this.limit) {
        System.arraycopy(this.inbuf, this.pos, this.inbuf, 0, this.limit - this.pos);
        this.limit -= this.pos;
        this.pos = 0;
      } else if (this.pos >= this.limit) {
        this.pos = 0;
        this.limit = 0;
      } 
      int i = this.inbuf.length;
      while (this.limit < i) {
        int j = this.underlyingInputStream.read(this.inbuf, this.limit, i - this.limit);
        if (j <= 0) {
          this.seenEOF = true;
          break;
        } 
        this.limit += j;
      } 
    } 
  }
  
  private boolean need(int paramInt) throws IOException {
    if (this.limit - this.pos >= paramInt)
      return true; 
    fill();
    if (this.limit - this.pos >= paramInt)
      return true; 
    if (this.seenEOF)
      return false; 
    byte[] arrayOfByte = new byte[paramInt + 100];
    System.arraycopy(this.inbuf, this.pos, arrayOfByte, 0, this.limit - this.pos);
    this.limit -= this.pos;
    this.pos = 0;
    this.inbuf = arrayOfByte;
    fill();
    return (this.limit - this.pos >= paramInt);
  }
  
  private final int getInt(int paramInt) { return (this.inbuf[paramInt] & 0xFF) << 24 | (this.inbuf[paramInt + 1] & 0xFF) << 16 | (this.inbuf[paramInt + 2] & 0xFF) << 8 | this.inbuf[paramInt + 3] & 0xFF; }
  
  private final int getShort(int paramInt) { return (short)((this.inbuf[paramInt] & 0xFF) << 8 | this.inbuf[paramInt + 1] & 0xFF); }
  
  private final int getByte(int paramInt) { return this.inbuf[paramInt] & 0xFF; }
  
  private final boolean getChunk() throws IOException {
    this.chunkLength = 0;
    if (!need(8))
      return false; 
    this.chunkLength = getInt(this.pos);
    this.chunkKey = getInt(this.pos + 4);
    if (this.chunkLength < 0)
      throw new PNGException("bogus length: " + this.chunkLength); 
    if (!need(this.chunkLength + 12))
      return false; 
    this.chunkCRC = getInt(this.pos + 8 + this.chunkLength);
    this.chunkStart = this.pos + 8;
    int i = crc(this.inbuf, this.pos + 4, this.chunkLength + 4);
    if (this.chunkCRC != i && checkCRC)
      throw new PNGException("crc corruption"); 
    this.pos += this.chunkLength + 12;
    return true;
  }
  
  private void readAll() throws IOException, ImageFormatException {
    while (getChunk())
      handleChunk(this.chunkKey, this.inbuf, this.chunkStart, this.chunkLength); 
  }
  
  boolean getData() throws IOException {
    while (this.chunkLength == 0 && getChunk()) {
      if (handleChunk(this.chunkKey, this.inbuf, this.chunkStart, this.chunkLength))
        this.chunkLength = 0; 
    } 
    return (this.chunkLength > 0);
  }
  
  public static boolean getCheckCRC() throws IOException { return checkCRC; }
  
  public static void setCheckCRC(boolean paramBoolean) throws IOException { checkCRC = paramBoolean; }
  
  protected void wrc(int paramInt) {
    paramInt &= 0xFF;
    if (paramInt <= 32 || paramInt > 122)
      paramInt = 63; 
    System.out.write(paramInt);
  }
  
  protected void wrk(int paramInt) {
    wrc(paramInt >> 24);
    wrc(paramInt >> 16);
    wrc(paramInt >> 8);
    wrc(paramInt);
  }
  
  public void print() throws IOException, ImageFormatException {
    wrk(this.chunkKey);
    System.out.print(" " + this.chunkLength + "\n");
  }
  
  private static int update_crc(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3) {
    int i;
    for (i = paramInt1; --paramInt3 >= 0; i = crc_table[(i ^ paramArrayOfByte[paramInt2++]) & 0xFF] ^ i >>> 8);
    return i;
  }
  
  private static int crc(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { return update_crc(-1, paramArrayOfByte, paramInt1, paramInt2) ^ 0xFFFFFFFF; }
  
  static  {
    for (byte b = 0; b < 'Ā'; b++) {
      int i = b;
      for (byte b1 = 0; b1 < 8; b1++) {
        if (i & true) {
          i = 0xEDB88320 ^ i >>> true;
        } else {
          i >>>= 1;
        } 
      } 
      crc_table[b] = i;
    } 
  }
  
  public static class Chromaticities {
    public float whiteX;
    
    public float whiteY;
    
    public float redX;
    
    public float redY;
    
    public float greenX;
    
    public float greenY;
    
    public float blueX;
    
    public float blueY;
    
    Chromaticities(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8) {
      this.whiteX = param1Int1 / 100000.0F;
      this.whiteY = param1Int2 / 100000.0F;
      this.redX = param1Int3 / 100000.0F;
      this.redY = param1Int4 / 100000.0F;
      this.greenX = param1Int5 / 100000.0F;
      this.greenY = param1Int6 / 100000.0F;
      this.blueX = param1Int7 / 100000.0F;
      this.blueY = param1Int8 / 100000.0F;
    }
    
    public String toString() { return "Chromaticities(white=" + this.whiteX + "," + this.whiteY + ";red=" + this.redX + "," + this.redY + ";green=" + this.greenX + "," + this.greenY + ";blue=" + this.blueX + "," + this.blueY + ")"; }
  }
  
  public class PNGException extends IOException {
    PNGException(String param1String) { super(param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\PNGImageDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */