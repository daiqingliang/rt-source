package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RectangularShape;

public class Region {
  static final int INIT_SIZE = 50;
  
  static final int GROW_SIZE = 50;
  
  public static final Region EMPTY_REGION = new ImmutableRegion(0, 0, 0, 0);
  
  public static final Region WHOLE_REGION;
  
  int lox;
  
  int loy;
  
  int hix;
  
  int hiy;
  
  int endIndex;
  
  int[] bands;
  
  static final int INCLUDE_A = 1;
  
  static final int INCLUDE_B = 2;
  
  static final int INCLUDE_COMMON = 4;
  
  private static native void initIDs();
  
  public static int dimAdd(int paramInt1, int paramInt2) { return (paramInt2 <= 0) ? paramInt1 : ((paramInt2 += paramInt1 < paramInt1) ? Integer.MAX_VALUE : paramInt2); }
  
  public static int clipAdd(int paramInt1, int paramInt2) {
    int i = paramInt1 + paramInt2;
    if (((i > paramInt1) ? 1 : 0) != ((paramInt2 > 0) ? 1 : 0))
      i = (paramInt2 < 0) ? Integer.MIN_VALUE : Integer.MAX_VALUE; 
    return i;
  }
  
  public static int clipScale(int paramInt, double paramDouble) {
    if (paramDouble == 1.0D)
      return paramInt; 
    double d = paramInt * paramDouble;
    return (d < -2.147483648E9D) ? Integer.MIN_VALUE : ((d > 2.147483647E9D) ? Integer.MAX_VALUE : (int)Math.round(d));
  }
  
  protected Region(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.lox = paramInt1;
    this.loy = paramInt2;
    this.hix = paramInt3;
    this.hiy = paramInt4;
  }
  
  private Region(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5) {
    this.lox = paramInt1;
    this.loy = paramInt2;
    this.hix = paramInt3;
    this.hiy = paramInt4;
    this.bands = paramArrayOfInt;
    this.endIndex = paramInt5;
  }
  
  public static Region getInstance(Shape paramShape, AffineTransform paramAffineTransform) { return getInstance(WHOLE_REGION, false, paramShape, paramAffineTransform); }
  
  public static Region getInstance(Region paramRegion, Shape paramShape, AffineTransform paramAffineTransform) { return getInstance(paramRegion, false, paramShape, paramAffineTransform); }
  
  public static Region getInstance(Region paramRegion, boolean paramBoolean, Shape paramShape, AffineTransform paramAffineTransform) {
    if (paramShape instanceof RectangularShape && ((RectangularShape)paramShape).isEmpty())
      return EMPTY_REGION; 
    int[] arrayOfInt = new int[4];
    shapeSpanIterator = new ShapeSpanIterator(paramBoolean);
    try {
      shapeSpanIterator.setOutputArea(paramRegion);
      shapeSpanIterator.appendPath(paramShape.getPathIterator(paramAffineTransform));
      shapeSpanIterator.getPathBox(arrayOfInt);
      Region region = getInstance(arrayOfInt);
      region.appendSpans(shapeSpanIterator);
      return region;
    } finally {
      shapeSpanIterator.dispose();
    } 
  }
  
  static Region getInstance(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) {
    int i = paramArrayOfInt[0];
    int j = paramArrayOfInt[1];
    if (paramInt4 <= paramInt2 || paramInt3 <= paramInt1 || j <= i)
      return EMPTY_REGION; 
    int[] arrayOfInt = new int[(j - i) * 5];
    byte b1 = 0;
    byte b2 = 2;
    for (int k = i; k < j; k++) {
      int m = Math.max(clipAdd(paramInt1, paramArrayOfInt[b2++]), paramInt1);
      int n = Math.min(clipAdd(paramInt1, paramArrayOfInt[b2++]), paramInt3);
      if (m < n) {
        int i1 = Math.max(clipAdd(paramInt2, k), paramInt2);
        int i2 = Math.min(clipAdd(i1, 1), paramInt4);
        if (i1 < i2) {
          arrayOfInt[b1++] = i1;
          arrayOfInt[b1++] = i2;
          arrayOfInt[b1++] = 1;
          arrayOfInt[b1++] = m;
          arrayOfInt[b1++] = n;
        } 
      } 
    } 
    return (b1 != 0) ? new Region(paramInt1, paramInt2, paramInt3, paramInt4, arrayOfInt, b1) : EMPTY_REGION;
  }
  
  public static Region getInstance(Rectangle paramRectangle) { return getInstanceXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  public static Region getInstanceXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return getInstanceXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4)); }
  
  public static Region getInstance(int[] paramArrayOfInt) { return new Region(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2], paramArrayOfInt[3]); }
  
  public static Region getInstanceXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return new Region(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void setOutputArea(Rectangle paramRectangle) { setOutputAreaXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  public void setOutputAreaXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { setOutputAreaXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4)); }
  
  public void setOutputArea(int[] paramArrayOfInt) {
    this.lox = paramArrayOfInt[0];
    this.loy = paramArrayOfInt[1];
    this.hix = paramArrayOfInt[2];
    this.hiy = paramArrayOfInt[3];
  }
  
  public void setOutputAreaXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.lox = paramInt1;
    this.loy = paramInt2;
    this.hix = paramInt3;
    this.hiy = paramInt4;
  }
  
  public void appendSpans(SpanIterator paramSpanIterator) {
    int[] arrayOfInt = new int[6];
    while (paramSpanIterator.nextSpan(arrayOfInt))
      appendSpan(arrayOfInt); 
    endRow(arrayOfInt);
    calcBBox();
  }
  
  public Region getScaledRegion(double paramDouble1, double paramDouble2) {
    if (paramDouble1 == 0.0D || paramDouble2 == 0.0D || this == EMPTY_REGION)
      return EMPTY_REGION; 
    if ((paramDouble1 == 1.0D && paramDouble2 == 1.0D) || this == WHOLE_REGION)
      return this; 
    int i = clipScale(this.lox, paramDouble1);
    int j = clipScale(this.loy, paramDouble2);
    int k = clipScale(this.hix, paramDouble1);
    int m = clipScale(this.hiy, paramDouble2);
    Region region = new Region(i, j, k, m);
    int[] arrayOfInt = this.bands;
    if (arrayOfInt != null) {
      int n = this.endIndex;
      int[] arrayOfInt1 = new int[n];
      int i1 = 0;
      byte b;
      for (b = 0; i1 < n; b = b1 - 3) {
        int i3 = clipScale(arrayOfInt[i1++], paramDouble2);
        arrayOfInt1[b++] = i3;
        int i4 = clipScale(arrayOfInt[i1++], paramDouble2);
        arrayOfInt1[b++] = i4;
        int i2 = arrayOfInt[i1++];
        arrayOfInt1[b++] = i2;
        byte b1 = b;
        if (i3 < i4) {
          while (--i2 >= 0) {
            int i5 = clipScale(arrayOfInt[i1++], paramDouble1);
            int i6 = clipScale(arrayOfInt[i1++], paramDouble1);
            if (i5 < i6) {
              arrayOfInt1[b++] = i5;
              arrayOfInt1[b++] = i6;
            } 
          } 
        } else {
          i1 += i2 * 2;
        } 
        if (b > b1) {
          arrayOfInt1[b1 - 1] = (b - b1) / 2;
          continue;
        } 
      } 
      if (b <= 5) {
        if (b < 5) {
          region.lox = region.loy = region.hix = region.hiy = 0;
        } else {
          region.loy = arrayOfInt1[0];
          region.hiy = arrayOfInt1[1];
          region.lox = arrayOfInt1[3];
          region.hix = arrayOfInt1[4];
        } 
      } else {
        region.endIndex = b;
        region.bands = arrayOfInt1;
      } 
    } 
    return region;
  }
  
  public Region getTranslatedRegion(int paramInt1, int paramInt2) {
    if ((paramInt1 | paramInt2) == 0)
      return this; 
    int i = this.lox + paramInt1;
    int j = this.loy + paramInt2;
    int k = this.hix + paramInt1;
    int m = this.hiy + paramInt2;
    if (((i > this.lox) ? 1 : 0) == ((paramInt1 > 0) ? 1 : 0))
      if (((j > this.loy) ? 1 : 0) == ((paramInt2 > 0) ? 1 : 0))
        if (((k > this.hix) ? 1 : 0) == ((paramInt1 > 0) ? 1 : 0))
          if (((m > this.hiy) ? 1 : 0) == ((paramInt2 > 0) ? 1 : 0)) {
            Region region = new Region(i, j, k, m);
            int[] arrayOfInt = this.bands;
            if (arrayOfInt != null) {
              int n = this.endIndex;
              region.endIndex = n;
              int[] arrayOfInt1 = new int[n];
              region.bands = arrayOfInt1;
              byte b = 0;
              while (b < n) {
                arrayOfInt1[b] = arrayOfInt[b] + paramInt2;
                arrayOfInt1[++b] = arrayOfInt[b] + paramInt2;
                int i1 = arrayOfInt[b];
                arrayOfInt1[++b] = i1;
                b++;
                while (--i1 >= 0) {
                  arrayOfInt1[b] = arrayOfInt[b] + paramInt1;
                  arrayOfInt1[++b] = arrayOfInt[b] + paramInt1;
                  b++;
                } 
              } 
            } 
            return region;
          }    
    return getSafeTranslatedRegion(paramInt1, paramInt2);
  }
  
  private Region getSafeTranslatedRegion(int paramInt1, int paramInt2) {
    int i = clipAdd(this.lox, paramInt1);
    int j = clipAdd(this.loy, paramInt2);
    int k = clipAdd(this.hix, paramInt1);
    int m = clipAdd(this.hiy, paramInt2);
    Region region = new Region(i, j, k, m);
    int[] arrayOfInt = this.bands;
    if (arrayOfInt != null) {
      int n = this.endIndex;
      int[] arrayOfInt1 = new int[n];
      int i1 = 0;
      byte b;
      for (b = 0; i1 < n; b = b1 - 3) {
        int i3 = clipAdd(arrayOfInt[i1++], paramInt2);
        arrayOfInt1[b++] = i3;
        int i4 = clipAdd(arrayOfInt[i1++], paramInt2);
        arrayOfInt1[b++] = i4;
        int i2 = arrayOfInt[i1++];
        arrayOfInt1[b++] = i2;
        byte b1 = b;
        if (i3 < i4) {
          while (--i2 >= 0) {
            int i5 = clipAdd(arrayOfInt[i1++], paramInt1);
            int i6 = clipAdd(arrayOfInt[i1++], paramInt1);
            if (i5 < i6) {
              arrayOfInt1[b++] = i5;
              arrayOfInt1[b++] = i6;
            } 
          } 
        } else {
          i1 += i2 * 2;
        } 
        if (b > b1) {
          arrayOfInt1[b1 - 1] = (b - b1) / 2;
          continue;
        } 
      } 
      if (b <= 5) {
        if (b < 5) {
          region.lox = region.loy = region.hix = region.hiy = 0;
        } else {
          region.loy = arrayOfInt1[0];
          region.hiy = arrayOfInt1[1];
          region.lox = arrayOfInt1[3];
          region.hix = arrayOfInt1[4];
        } 
      } else {
        region.endIndex = b;
        region.bands = arrayOfInt1;
      } 
    } 
    return region;
  }
  
  public Region getIntersection(Rectangle paramRectangle) { return getIntersectionXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  public Region getIntersectionXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return getIntersectionXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4)); }
  
  public Region getIntersectionXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (isInsideXYXY(paramInt1, paramInt2, paramInt3, paramInt4))
      return this; 
    Region region = new Region((paramInt1 < this.lox) ? this.lox : paramInt1, (paramInt2 < this.loy) ? this.loy : paramInt2, (paramInt3 > this.hix) ? this.hix : paramInt3, (paramInt4 > this.hiy) ? this.hiy : paramInt4);
    if (this.bands != null)
      region.appendSpans(getSpanIterator()); 
    return region;
  }
  
  public Region getIntersection(Region paramRegion) {
    if (isInsideQuickCheck(paramRegion))
      return this; 
    if (paramRegion.isInsideQuickCheck(this))
      return paramRegion; 
    Region region = new Region((paramRegion.lox < this.lox) ? this.lox : paramRegion.lox, (paramRegion.loy < this.loy) ? this.loy : paramRegion.loy, (paramRegion.hix > this.hix) ? this.hix : paramRegion.hix, (paramRegion.hiy > this.hiy) ? this.hiy : paramRegion.hiy);
    if (!region.isEmpty())
      region.filterSpans(this, paramRegion, 4); 
    return region;
  }
  
  public Region getUnion(Region paramRegion) {
    if (paramRegion.isEmpty() || paramRegion.isInsideQuickCheck(this))
      return this; 
    if (isEmpty() || isInsideQuickCheck(paramRegion))
      return paramRegion; 
    Region region = new Region((paramRegion.lox > this.lox) ? this.lox : paramRegion.lox, (paramRegion.loy > this.loy) ? this.loy : paramRegion.loy, (paramRegion.hix < this.hix) ? this.hix : paramRegion.hix, (paramRegion.hiy < this.hiy) ? this.hiy : paramRegion.hiy);
    region.filterSpans(this, paramRegion, 7);
    return region;
  }
  
  public Region getDifference(Region paramRegion) {
    if (!paramRegion.intersectsQuickCheck(this))
      return this; 
    if (isInsideQuickCheck(paramRegion))
      return EMPTY_REGION; 
    Region region = new Region(this.lox, this.loy, this.hix, this.hiy);
    region.filterSpans(this, paramRegion, 1);
    return region;
  }
  
  public Region getExclusiveOr(Region paramRegion) {
    if (paramRegion.isEmpty())
      return this; 
    if (isEmpty())
      return paramRegion; 
    Region region = new Region((paramRegion.lox > this.lox) ? this.lox : paramRegion.lox, (paramRegion.loy > this.loy) ? this.loy : paramRegion.loy, (paramRegion.hix < this.hix) ? this.hix : paramRegion.hix, (paramRegion.hiy < this.hiy) ? this.hiy : paramRegion.hiy);
    region.filterSpans(this, paramRegion, 3);
    return region;
  }
  
  private void filterSpans(Region paramRegion1, Region paramRegion2, int paramInt) {
    int[] arrayOfInt1 = paramRegion1.bands;
    int[] arrayOfInt2 = paramRegion2.bands;
    if (arrayOfInt1 == null)
      arrayOfInt1 = new int[] { paramRegion1.loy, paramRegion1.hiy, 1, paramRegion1.lox, paramRegion1.hix }; 
    if (arrayOfInt2 == null)
      arrayOfInt2 = new int[] { paramRegion2.loy, paramRegion2.hiy, 1, paramRegion2.lox, paramRegion2.hix }; 
    int[] arrayOfInt3 = new int[6];
    int i = 0;
    int j = arrayOfInt1[i++];
    int k = arrayOfInt1[i++];
    int m = arrayOfInt1[i++];
    m = i + 2 * m;
    int n = 0;
    int i1 = arrayOfInt2[n++];
    int i2 = arrayOfInt2[n++];
    int i3 = arrayOfInt2[n++];
    i3 = n + 2 * i3;
    int i4;
    for (i4 = this.loy; i4 < this.hiy; i4 = i5) {
      int i5;
      if (i4 >= k) {
        if (m < paramRegion1.endIndex) {
          i = m;
          j = arrayOfInt1[i++];
          k = arrayOfInt1[i++];
          m = arrayOfInt1[i++];
          m = i + 2 * m;
          continue;
        } 
        if ((paramInt & 0x2) == 0)
          break; 
        j = k = this.hiy;
        continue;
      } 
      if (i4 >= i2) {
        if (i3 < paramRegion2.endIndex) {
          n = i3;
          i1 = arrayOfInt2[n++];
          i2 = arrayOfInt2[n++];
          i3 = arrayOfInt2[n++];
          i3 = n + 2 * i3;
          continue;
        } 
        if ((paramInt & true) == 0)
          break; 
        i1 = i2 = this.hiy;
        continue;
      } 
      if (i4 < i1) {
        if (i4 < j) {
          i4 = Math.min(j, i1);
          continue;
        } 
        i5 = Math.min(k, i1);
        if ((paramInt & true) != 0) {
          arrayOfInt3[1] = i4;
          arrayOfInt3[3] = i5;
          int i6 = i;
          while (i6 < m) {
            arrayOfInt3[0] = arrayOfInt1[i6++];
            arrayOfInt3[2] = arrayOfInt1[i6++];
            appendSpan(arrayOfInt3);
          } 
        } 
      } else if (i4 < j) {
        i5 = Math.min(i2, j);
        if ((paramInt & 0x2) != 0) {
          arrayOfInt3[1] = i4;
          arrayOfInt3[3] = i5;
          int i6 = n;
          while (i6 < i3) {
            arrayOfInt3[0] = arrayOfInt2[i6++];
            arrayOfInt3[2] = arrayOfInt2[i6++];
            appendSpan(arrayOfInt3);
          } 
        } 
      } else {
        i5 = Math.min(k, i2);
        arrayOfInt3[1] = i4;
        arrayOfInt3[3] = i5;
        int i6 = i;
        int i7 = n;
        int i8 = arrayOfInt1[i6++];
        int i9 = arrayOfInt1[i6++];
        int i10 = arrayOfInt2[i7++];
        int i11 = arrayOfInt2[i7++];
        int i12 = Math.min(i8, i10);
        if (i12 < this.lox)
          i12 = this.lox; 
        while (i12 < this.hix) {
          boolean bool;
          int i13;
          if (i12 >= i9) {
            if (i6 < m) {
              i8 = arrayOfInt1[i6++];
              i9 = arrayOfInt1[i6++];
              continue;
            } 
            if ((paramInt & 0x2) == 0)
              break; 
            i8 = i9 = this.hix;
            continue;
          } 
          if (i12 >= i11) {
            if (i7 < i3) {
              i10 = arrayOfInt2[i7++];
              i11 = arrayOfInt2[i7++];
              continue;
            } 
            if ((paramInt & true) == 0)
              break; 
            i10 = i11 = this.hix;
            continue;
          } 
          if (i12 < i10) {
            if (i12 < i8) {
              i13 = Math.min(i8, i10);
              bool = false;
            } else {
              i13 = Math.min(i9, i10);
              bool = ((paramInt & true) != 0) ? 1 : 0;
            } 
          } else if (i12 < i8) {
            i13 = Math.min(i8, i11);
            bool = ((paramInt & 0x2) != 0) ? 1 : 0;
          } else {
            i13 = Math.min(i9, i11);
            bool = ((paramInt & 0x4) != 0) ? 1 : 0;
          } 
          if (bool) {
            arrayOfInt3[0] = i12;
            arrayOfInt3[2] = i13;
            appendSpan(arrayOfInt3);
          } 
          i12 = i13;
        } 
      } 
    } 
    endRow(arrayOfInt3);
    calcBBox();
  }
  
  public Region getBoundsIntersection(Rectangle paramRectangle) { return getBoundsIntersectionXYWH(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  public Region getBoundsIntersectionXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return getBoundsIntersectionXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4)); }
  
  public Region getBoundsIntersectionXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return (this.bands == null && this.lox >= paramInt1 && this.loy >= paramInt2 && this.hix <= paramInt3 && this.hiy <= paramInt4) ? this : new Region((paramInt1 < this.lox) ? this.lox : paramInt1, (paramInt2 < this.loy) ? this.loy : paramInt2, (paramInt3 > this.hix) ? this.hix : paramInt3, (paramInt4 > this.hiy) ? this.hiy : paramInt4); }
  
  public Region getBoundsIntersection(Region paramRegion) { return encompasses(paramRegion) ? paramRegion : (paramRegion.encompasses(this) ? this : new Region((paramRegion.lox < this.lox) ? this.lox : paramRegion.lox, (paramRegion.loy < this.loy) ? this.loy : paramRegion.loy, (paramRegion.hix > this.hix) ? this.hix : paramRegion.hix, (paramRegion.hiy > this.hiy) ? this.hiy : paramRegion.hiy)); }
  
  private void appendSpan(int[] paramArrayOfInt) {
    int i;
    if ((i = paramArrayOfInt[0]) < this.lox)
      i = this.lox; 
    int j;
    if ((j = paramArrayOfInt[1]) < this.loy)
      j = this.loy; 
    int k;
    if ((k = paramArrayOfInt[2]) > this.hix)
      k = this.hix; 
    int m;
    if ((m = paramArrayOfInt[3]) > this.hiy)
      m = this.hiy; 
    if (k <= i || m <= j)
      return; 
    int n = paramArrayOfInt[4];
    if (this.endIndex == 0 || j >= this.bands[n + 1]) {
      if (this.bands == null) {
        this.bands = new int[50];
      } else {
        needSpace(5);
        endRow(paramArrayOfInt);
        n = paramArrayOfInt[4];
      } 
      this.bands[this.endIndex++] = j;
      this.bands[this.endIndex++] = m;
      this.bands[this.endIndex++] = 0;
    } else if (j == this.bands[n] && m == this.bands[n + 1] && i >= this.bands[this.endIndex - 1]) {
      if (i == this.bands[this.endIndex - 1]) {
        this.bands[this.endIndex - 1] = k;
        return;
      } 
      needSpace(2);
    } else {
      throw new InternalError("bad span");
    } 
    this.bands[this.endIndex++] = i;
    this.bands[this.endIndex++] = k;
    this.bands[n + 2] = this.bands[n + 2] + 1;
  }
  
  private void needSpace(int paramInt) {
    if (this.endIndex + paramInt >= this.bands.length) {
      int[] arrayOfInt = new int[this.bands.length + 50];
      System.arraycopy(this.bands, 0, arrayOfInt, 0, this.endIndex);
      this.bands = arrayOfInt;
    } 
  }
  
  private void endRow(int[] paramArrayOfInt) {
    int i = paramArrayOfInt[4];
    int j = paramArrayOfInt[5];
    if (i > j) {
      int[] arrayOfInt = this.bands;
      if (arrayOfInt[j + 1] == arrayOfInt[i] && arrayOfInt[j + 2] == arrayOfInt[i + 2]) {
        int k = arrayOfInt[i + 2] * 2;
        i += 3;
        j += 3;
        while (k > 0 && arrayOfInt[i++] == arrayOfInt[j++])
          k--; 
        if (k == 0) {
          arrayOfInt[paramArrayOfInt[5] + 1] = arrayOfInt[j + 1];
          this.endIndex = j;
          return;
        } 
      } 
    } 
    paramArrayOfInt[5] = paramArrayOfInt[4];
    paramArrayOfInt[4] = this.endIndex;
  }
  
  private void calcBBox() {
    int[] arrayOfInt = this.bands;
    if (this.endIndex <= 5) {
      if (this.endIndex == 0) {
        this.lox = this.loy = this.hix = this.hiy = 0;
      } else {
        this.loy = arrayOfInt[0];
        this.hiy = arrayOfInt[1];
        this.lox = arrayOfInt[3];
        this.hix = arrayOfInt[4];
        this.endIndex = 0;
      } 
      this.bands = null;
      return;
    } 
    int i = this.hix;
    int j = this.lox;
    byte b = 0;
    int k = 0;
    while (k < this.endIndex) {
      b = k;
      int m = arrayOfInt[k + 2];
      k += 3;
      if (i > arrayOfInt[k])
        i = arrayOfInt[k]; 
      k += m * 2;
      if (j < arrayOfInt[k - 1])
        j = arrayOfInt[k - 1]; 
    } 
    this.lox = i;
    this.loy = arrayOfInt[0];
    this.hix = j;
    this.hiy = arrayOfInt[b + 1];
  }
  
  public final int getLoX() { return this.lox; }
  
  public final int getLoY() { return this.loy; }
  
  public final int getHiX() { return this.hix; }
  
  public final int getHiY() { return this.hiy; }
  
  public final int getWidth() {
    if (this.hix < this.lox)
      return 0; 
    int i;
    if ((i = this.hix - this.lox) < 0)
      i = Integer.MAX_VALUE; 
    return i;
  }
  
  public final int getHeight() {
    if (this.hiy < this.loy)
      return 0; 
    int i;
    if ((i = this.hiy - this.loy) < 0)
      i = Integer.MAX_VALUE; 
    return i;
  }
  
  public boolean isEmpty() { return (this.hix <= this.lox || this.hiy <= this.loy); }
  
  public boolean isRectangular() { return (this.bands == null); }
  
  public boolean contains(int paramInt1, int paramInt2) {
    if (paramInt1 < this.lox || paramInt1 >= this.hix || paramInt2 < this.loy || paramInt2 >= this.hiy)
      return false; 
    if (this.bands == null)
      return true; 
    int i = 0;
    while (i < this.endIndex) {
      if (paramInt2 < this.bands[i++])
        return false; 
      if (paramInt2 >= this.bands[i++]) {
        int k = this.bands[i++];
        i += k * 2;
        continue;
      } 
      int j = this.bands[i++];
      j = i + j * 2;
      while (i < j) {
        if (paramInt1 < this.bands[i++])
          return false; 
        if (paramInt1 < this.bands[i++])
          return true; 
      } 
      return false;
    } 
    return false;
  }
  
  public boolean isInsideXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return isInsideXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4)); }
  
  public boolean isInsideXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return (this.lox >= paramInt1 && this.loy >= paramInt2 && this.hix <= paramInt3 && this.hiy <= paramInt4); }
  
  public boolean isInsideQuickCheck(Region paramRegion) { return (paramRegion.bands == null && paramRegion.lox <= this.lox && paramRegion.loy <= this.loy && paramRegion.hix >= this.hix && paramRegion.hiy >= this.hiy); }
  
  public boolean intersectsQuickCheckXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return (paramInt3 > this.lox && paramInt1 < this.hix && paramInt4 > this.loy && paramInt2 < this.hiy); }
  
  public boolean intersectsQuickCheck(Region paramRegion) { return (paramRegion.hix > this.lox && paramRegion.lox < this.hix && paramRegion.hiy > this.loy && paramRegion.loy < this.hiy); }
  
  public boolean encompasses(Region paramRegion) { return (this.bands == null && this.lox <= paramRegion.lox && this.loy <= paramRegion.loy && this.hix >= paramRegion.hix && this.hiy >= paramRegion.hiy); }
  
  public boolean encompassesXYWH(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return encompassesXYXY(paramInt1, paramInt2, dimAdd(paramInt1, paramInt3), dimAdd(paramInt2, paramInt4)); }
  
  public boolean encompassesXYXY(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return (this.bands == null && this.lox <= paramInt1 && this.loy <= paramInt2 && this.hix >= paramInt3 && this.hiy >= paramInt4); }
  
  public void getBounds(int[] paramArrayOfInt) {
    paramArrayOfInt[0] = this.lox;
    paramArrayOfInt[1] = this.loy;
    paramArrayOfInt[2] = this.hix;
    paramArrayOfInt[3] = this.hiy;
  }
  
  public void clipBoxToBounds(int[] paramArrayOfInt) {
    if (paramArrayOfInt[0] < this.lox)
      paramArrayOfInt[0] = this.lox; 
    if (paramArrayOfInt[1] < this.loy)
      paramArrayOfInt[1] = this.loy; 
    if (paramArrayOfInt[2] > this.hix)
      paramArrayOfInt[2] = this.hix; 
    if (paramArrayOfInt[3] > this.hiy)
      paramArrayOfInt[3] = this.hiy; 
  }
  
  public RegionIterator getIterator() { return new RegionIterator(this); }
  
  public SpanIterator getSpanIterator() { return new RegionSpanIterator(this); }
  
  public SpanIterator getSpanIterator(int[] paramArrayOfInt) {
    SpanIterator spanIterator = getSpanIterator();
    spanIterator.intersectClipBox(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2], paramArrayOfInt[3]);
    return spanIterator;
  }
  
  public SpanIterator filter(SpanIterator paramSpanIterator) {
    if (this.bands == null) {
      paramSpanIterator.intersectClipBox(this.lox, this.loy, this.hix, this.hiy);
    } else {
      paramSpanIterator = new RegionClipSpanIterator(this, paramSpanIterator);
    } 
    return paramSpanIterator;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Region[[");
    stringBuffer.append(this.lox);
    stringBuffer.append(", ");
    stringBuffer.append(this.loy);
    stringBuffer.append(" => ");
    stringBuffer.append(this.hix);
    stringBuffer.append(", ");
    stringBuffer.append(this.hiy);
    stringBuffer.append("]");
    if (this.bands != null) {
      int i = 0;
      while (i < this.endIndex) {
        stringBuffer.append("y{");
        stringBuffer.append(this.bands[i++]);
        stringBuffer.append(",");
        stringBuffer.append(this.bands[i++]);
        stringBuffer.append("}[");
        int j = this.bands[i++];
        j = i + j * 2;
        while (i < j) {
          stringBuffer.append("x(");
          stringBuffer.append(this.bands[i++]);
          stringBuffer.append(", ");
          stringBuffer.append(this.bands[i++]);
          stringBuffer.append(")");
        } 
        stringBuffer.append("]");
      } 
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  public int hashCode() { return isEmpty() ? 0 : (this.lox * 3 + this.loy * 5 + this.hix * 7 + this.hiy * 9); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Region))
      return false; 
    Region region = (Region)paramObject;
    if (isEmpty())
      return region.isEmpty(); 
    if (region.isEmpty())
      return false; 
    if (region.lox != this.lox || region.loy != this.loy || region.hix != this.hix || region.hiy != this.hiy)
      return false; 
    if (this.bands == null)
      return (region.bands == null); 
    if (region.bands == null)
      return false; 
    if (this.endIndex != region.endIndex)
      return false; 
    int[] arrayOfInt1 = this.bands;
    int[] arrayOfInt2 = region.bands;
    for (byte b = 0; b < this.endIndex; b++) {
      if (arrayOfInt1[b] != arrayOfInt2[b])
        return false; 
    } 
    return true;
  }
  
  static  {
    (WHOLE_REGION = new ImmutableRegion(-2147483648, -2147483648, 2147483647, 2147483647)).initIDs();
  }
  
  private static final class ImmutableRegion extends Region {
    protected ImmutableRegion(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void appendSpans(SpanIterator param1SpanIterator) {}
    
    public void setOutputArea(Rectangle param1Rectangle) {}
    
    public void setOutputAreaXYWH(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {}
    
    public void setOutputArea(int[] param1ArrayOfInt) {}
    
    public void setOutputAreaXYXY(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\Region.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */