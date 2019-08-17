package sun.java2d.loops;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ProcessPath {
  public static final int PH_MODE_DRAW_CLIP = 0;
  
  public static final int PH_MODE_FILL_CLIP = 1;
  
  public static EndSubPathHandler noopEndSubPathHandler = new EndSubPathHandler() {
      public void processEndSubPath() {}
    };
  
  private static final float UPPER_BND = 8.5070587E37F;
  
  private static final float LOWER_BND = -8.5070587E37F;
  
  private static final int FWD_PREC = 7;
  
  private static final int MDP_PREC = 10;
  
  private static final int MDP_MULT = 1024;
  
  private static final int MDP_HALF_MULT = 512;
  
  private static final int UPPER_OUT_BND = 1048576;
  
  private static final int LOWER_OUT_BND = -1048576;
  
  private static final float CALC_UBND = 1048576.0F;
  
  private static final float CALC_LBND = -1048576.0F;
  
  public static final int EPSFX = 1;
  
  public static final float EPSF = 9.765625E-4F;
  
  private static final int MDP_W_MASK = -1024;
  
  private static final int MDP_F_MASK = 1023;
  
  private static final int MAX_CUB_SIZE = 256;
  
  private static final int MAX_QUAD_SIZE = 1024;
  
  private static final int DF_CUB_STEPS = 3;
  
  private static final int DF_QUAD_STEPS = 2;
  
  private static final int DF_CUB_SHIFT = 6;
  
  private static final int DF_QUAD_SHIFT = 1;
  
  private static final int DF_CUB_COUNT = 8;
  
  private static final int DF_QUAD_COUNT = 4;
  
  private static final int DF_CUB_DEC_BND = 262144;
  
  private static final int DF_CUB_INC_BND = 32768;
  
  private static final int DF_QUAD_DEC_BND = 8192;
  
  private static final int DF_QUAD_INC_BND = 1024;
  
  private static final int CUB_A_SHIFT = 7;
  
  private static final int CUB_B_SHIFT = 11;
  
  private static final int CUB_C_SHIFT = 13;
  
  private static final int CUB_A_MDP_MULT = 128;
  
  private static final int CUB_B_MDP_MULT = 2048;
  
  private static final int CUB_C_MDP_MULT = 8192;
  
  private static final int QUAD_A_SHIFT = 7;
  
  private static final int QUAD_B_SHIFT = 9;
  
  private static final int QUAD_A_MDP_MULT = 128;
  
  private static final int QUAD_B_MDP_MULT = 512;
  
  private static final int CRES_MIN_CLIPPED = 0;
  
  private static final int CRES_MAX_CLIPPED = 1;
  
  private static final int CRES_NOT_CLIPPED = 3;
  
  private static final int CRES_INVISIBLE = 4;
  
  private static final int DF_MAX_POINT = 256;
  
  public static boolean fillPath(DrawHandler paramDrawHandler, Path2D.Float paramFloat, int paramInt1, int paramInt2) {
    FillProcessHandler fillProcessHandler = new FillProcessHandler(paramDrawHandler);
    if (!doProcessPath(fillProcessHandler, paramFloat, paramInt1, paramInt2))
      return false; 
    FillPolygon(fillProcessHandler, paramFloat.getWindingRule());
    return true;
  }
  
  public static boolean drawPath(DrawHandler paramDrawHandler, EndSubPathHandler paramEndSubPathHandler, Path2D.Float paramFloat, int paramInt1, int paramInt2) { return doProcessPath(new DrawProcessHandler(paramDrawHandler, paramEndSubPathHandler), paramFloat, paramInt1, paramInt2); }
  
  public static boolean drawPath(DrawHandler paramDrawHandler, Path2D.Float paramFloat, int paramInt1, int paramInt2) { return doProcessPath(new DrawProcessHandler(paramDrawHandler, noopEndSubPathHandler), paramFloat, paramInt1, paramInt2); }
  
  private static float CLIP(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, double paramDouble) { return (float)(paramFloat2 + (paramDouble - paramFloat1) * (paramFloat4 - paramFloat2) / (paramFloat3 - paramFloat1)); }
  
  private static int CLIP(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble) { return (int)(paramInt2 + (paramDouble - paramInt1) * (paramInt4 - paramInt2) / (paramInt3 - paramInt1)); }
  
  private static boolean IS_CLIPPED(int paramInt) { return (paramInt == 0 || paramInt == 1); }
  
  private static int TESTANDCLIP(float paramFloat1, float paramFloat2, float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    byte b = 3;
    if (paramArrayOfFloat[paramInt1] < paramFloat1 || paramArrayOfFloat[paramInt1] > paramFloat2) {
      double d;
      if (paramArrayOfFloat[paramInt1] < paramFloat1) {
        if (paramArrayOfFloat[paramInt3] < paramFloat1)
          return 4; 
        b = 0;
        d = paramFloat1;
      } else {
        if (paramArrayOfFloat[paramInt3] > paramFloat2)
          return 4; 
        b = 1;
        d = paramFloat2;
      } 
      paramArrayOfFloat[paramInt2] = CLIP(paramArrayOfFloat[paramInt1], paramArrayOfFloat[paramInt2], paramArrayOfFloat[paramInt3], paramArrayOfFloat[paramInt4], d);
      paramArrayOfFloat[paramInt1] = (float)d;
    } 
    return b;
  }
  
  private static int TESTANDCLIP(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    byte b = 3;
    if (paramArrayOfInt[paramInt3] < paramInt1 || paramArrayOfInt[paramInt3] > paramInt2) {
      double d;
      if (paramArrayOfInt[paramInt3] < paramInt1) {
        if (paramArrayOfInt[paramInt5] < paramInt1)
          return 4; 
        b = 0;
        d = paramInt1;
      } else {
        if (paramArrayOfInt[paramInt5] > paramInt2)
          return 4; 
        b = 1;
        d = paramInt2;
      } 
      paramArrayOfInt[paramInt4] = CLIP(paramArrayOfInt[paramInt3], paramArrayOfInt[paramInt4], paramArrayOfInt[paramInt5], paramArrayOfInt[paramInt6], d);
      paramArrayOfInt[paramInt3] = (int)d;
    } 
    return b;
  }
  
  private static int CLIPCLAMP(float paramFloat1, float paramFloat2, float[] paramArrayOfFloat, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    paramArrayOfFloat[paramInt5] = paramArrayOfFloat[paramInt1];
    paramArrayOfFloat[paramInt6] = paramArrayOfFloat[paramInt2];
    int i = TESTANDCLIP(paramFloat1, paramFloat2, paramArrayOfFloat, paramInt1, paramInt2, paramInt3, paramInt4);
    if (i == 0) {
      paramArrayOfFloat[paramInt5] = paramArrayOfFloat[paramInt1];
    } else if (i == 1) {
      paramArrayOfFloat[paramInt5] = paramArrayOfFloat[paramInt1];
      i = 1;
    } else if (i == 4) {
      if (paramArrayOfFloat[paramInt1] > paramFloat2) {
        i = 4;
      } else {
        paramArrayOfFloat[paramInt1] = paramFloat1;
        paramArrayOfFloat[paramInt3] = paramFloat1;
        i = 3;
      } 
    } 
    return i;
  }
  
  private static int CLIPCLAMP(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
    paramArrayOfInt[paramInt7] = paramArrayOfInt[paramInt3];
    paramArrayOfInt[paramInt8] = paramArrayOfInt[paramInt4];
    int i = TESTANDCLIP(paramInt1, paramInt2, paramArrayOfInt, paramInt3, paramInt4, paramInt5, paramInt6);
    if (i == 0) {
      paramArrayOfInt[paramInt7] = paramArrayOfInt[paramInt3];
    } else if (i == 1) {
      paramArrayOfInt[paramInt7] = paramArrayOfInt[paramInt3];
      i = 1;
    } else if (i == 4) {
      if (paramArrayOfInt[paramInt3] > paramInt2) {
        i = 4;
      } else {
        paramArrayOfInt[paramInt3] = paramInt1;
        paramArrayOfInt[paramInt5] = paramInt1;
        i = 3;
      } 
    } 
    return i;
  }
  
  private static void DrawMonotonicQuad(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, boolean paramBoolean, int[] paramArrayOfInt) {
    int i = (int)(paramArrayOfFloat[0] * 1024.0F);
    int j = (int)(paramArrayOfFloat[1] * 1024.0F);
    int k = (int)(paramArrayOfFloat[4] * 1024.0F);
    int m = (int)(paramArrayOfFloat[5] * 1024.0F);
    int n = (i & 0x3FF) << 1;
    int i1 = (j & 0x3FF) << 1;
    byte b = 4;
    int i2 = 1;
    int i3 = (int)((paramArrayOfFloat[0] - 2.0F * paramArrayOfFloat[2] + paramArrayOfFloat[4]) * 128.0F);
    int i4 = (int)((paramArrayOfFloat[1] - 2.0F * paramArrayOfFloat[3] + paramArrayOfFloat[5]) * 128.0F);
    int i5 = (int)((-2.0F * paramArrayOfFloat[0] + 2.0F * paramArrayOfFloat[2]) * 512.0F);
    int i6 = (int)((-2.0F * paramArrayOfFloat[1] + 2.0F * paramArrayOfFloat[3]) * 512.0F);
    int i7 = 2 * i3;
    int i8 = 2 * i4;
    int i9 = i3 + i5;
    int i10 = i4 + i6;
    int i11 = i;
    int i12 = j;
    int i13 = Math.max(Math.abs(i7), Math.abs(i8));
    int i14 = k - i;
    int i15 = m - j;
    int i16 = i & 0xFFFFFC00;
    int i17 = j & 0xFFFFFC00;
    while (i13 > 8192) {
      i9 = (i9 << 1) - i3;
      i10 = (i10 << 1) - i4;
      b <<= 1;
      i13 >>= 2;
      n <<= 2;
      i1 <<= 2;
      i2 += true;
    } 
    while (b-- > 1) {
      n += i9;
      i1 += i10;
      i9 += i7;
      i10 += i8;
      int i18 = i11;
      int i19 = i12;
      i11 = i16 + (n >> i2);
      i12 = i17 + (i1 >> i2);
      if ((k - i11 ^ i14) < 0)
        i11 = k; 
      if ((m - i12 ^ i15) < 0)
        i12 = m; 
      paramProcessHandler.processFixedLine(i18, i19, i11, i12, paramArrayOfInt, paramBoolean, false);
    } 
    paramProcessHandler.processFixedLine(i11, i12, k, m, paramArrayOfInt, paramBoolean, false);
  }
  
  private static void ProcessMonotonicQuad(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt) {
    float[] arrayOfFloat = new float[6];
    float f3 = paramArrayOfFloat[0];
    float f1 = f3;
    float f4 = paramArrayOfFloat[1];
    float f2 = f4;
    for (byte b = 2; b < 6; b += 2) {
      f1 = (f1 > paramArrayOfFloat[b]) ? paramArrayOfFloat[b] : f1;
      f3 = (f3 < paramArrayOfFloat[b]) ? paramArrayOfFloat[b] : f3;
      f2 = (f2 > paramArrayOfFloat[b + 1]) ? paramArrayOfFloat[b + 1] : f2;
      f4 = (f4 < paramArrayOfFloat[b + 1]) ? paramArrayOfFloat[b + 1] : f4;
    } 
    if (paramProcessHandler.clipMode == 0) {
      if (paramProcessHandler.dhnd.xMaxf < f1 || paramProcessHandler.dhnd.xMinf > f3 || paramProcessHandler.dhnd.yMaxf < f2 || paramProcessHandler.dhnd.yMinf > f4)
        return; 
    } else {
      if (paramProcessHandler.dhnd.yMaxf < f2 || paramProcessHandler.dhnd.yMinf > f4 || paramProcessHandler.dhnd.xMaxf < f1)
        return; 
      if (paramProcessHandler.dhnd.xMinf > f3) {
        paramArrayOfFloat[4] = paramProcessHandler.dhnd.xMinf;
        paramArrayOfFloat[2] = paramProcessHandler.dhnd.xMinf;
        paramArrayOfFloat[0] = paramProcessHandler.dhnd.xMinf;
      } 
    } 
    if (f3 - f1 > 1024.0F || f4 - f2 > 1024.0F) {
      arrayOfFloat[4] = paramArrayOfFloat[4];
      arrayOfFloat[5] = paramArrayOfFloat[5];
      arrayOfFloat[2] = (paramArrayOfFloat[2] + paramArrayOfFloat[4]) / 2.0F;
      arrayOfFloat[3] = (paramArrayOfFloat[3] + paramArrayOfFloat[5]) / 2.0F;
      paramArrayOfFloat[2] = (paramArrayOfFloat[0] + paramArrayOfFloat[2]) / 2.0F;
      paramArrayOfFloat[3] = (paramArrayOfFloat[1] + paramArrayOfFloat[3]) / 2.0F;
      arrayOfFloat[0] = (paramArrayOfFloat[2] + arrayOfFloat[2]) / 2.0F;
      paramArrayOfFloat[4] = (paramArrayOfFloat[2] + arrayOfFloat[2]) / 2.0F;
      arrayOfFloat[1] = (paramArrayOfFloat[3] + arrayOfFloat[3]) / 2.0F;
      paramArrayOfFloat[5] = (paramArrayOfFloat[3] + arrayOfFloat[3]) / 2.0F;
      ProcessMonotonicQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt);
      ProcessMonotonicQuad(paramProcessHandler, arrayOfFloat, paramArrayOfInt);
    } else {
      DrawMonotonicQuad(paramProcessHandler, paramArrayOfFloat, (paramProcessHandler.dhnd.xMinf >= f1 || paramProcessHandler.dhnd.xMaxf <= f3 || paramProcessHandler.dhnd.yMinf >= f2 || paramProcessHandler.dhnd.yMaxf <= f4), paramArrayOfInt);
    } 
  }
  
  private static void ProcessQuad(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt) {
    double d;
    double[] arrayOfDouble = new double[2];
    byte b = 0;
    if ((paramArrayOfFloat[0] > paramArrayOfFloat[2] || paramArrayOfFloat[2] > paramArrayOfFloat[4]) && (paramArrayOfFloat[0] < paramArrayOfFloat[2] || paramArrayOfFloat[2] < paramArrayOfFloat[4])) {
      double d1 = (paramArrayOfFloat[0] - 2.0F * paramArrayOfFloat[2] + paramArrayOfFloat[4]);
      if (d1 != 0.0D) {
        double d3 = (paramArrayOfFloat[0] - paramArrayOfFloat[2]);
        double d2 = d3 / d1;
        if (d2 < 1.0D && d2 > 0.0D)
          arrayOfDouble[b++] = d2; 
      } 
    } 
    if ((paramArrayOfFloat[1] > paramArrayOfFloat[3] || paramArrayOfFloat[3] > paramArrayOfFloat[5]) && (paramArrayOfFloat[1] < paramArrayOfFloat[3] || paramArrayOfFloat[3] < paramArrayOfFloat[5])) {
      double d1 = (paramArrayOfFloat[1] - 2.0F * paramArrayOfFloat[3] + paramArrayOfFloat[5]);
      if (d1 != 0.0D) {
        double d3 = (paramArrayOfFloat[1] - paramArrayOfFloat[3]);
        double d2 = d3 / d1;
        if (d2 < 1.0D && d2 > 0.0D)
          if (b > 0) {
            if (arrayOfDouble[0] > d2) {
              arrayOfDouble[b++] = arrayOfDouble[0];
              arrayOfDouble[0] = d2;
            } else if (arrayOfDouble[0] < d2) {
              arrayOfDouble[b++] = d2;
            } 
          } else {
            arrayOfDouble[b++] = d2;
          }  
      } 
    } 
    switch (b) {
      case 1:
        ProcessFirstMonotonicPartOfQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)arrayOfDouble[0]);
        break;
      case 2:
        ProcessFirstMonotonicPartOfQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)arrayOfDouble[0]);
        d = arrayOfDouble[1] - arrayOfDouble[0];
        if (d > 0.0D)
          ProcessFirstMonotonicPartOfQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)(d / (1.0D - arrayOfDouble[0]))); 
        break;
    } 
    ProcessMonotonicQuad(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt);
  }
  
  private static void ProcessFirstMonotonicPartOfQuad(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt, float paramFloat) {
    float[] arrayOfFloat = new float[6];
    arrayOfFloat[0] = paramArrayOfFloat[0];
    arrayOfFloat[1] = paramArrayOfFloat[1];
    arrayOfFloat[2] = paramArrayOfFloat[0] + paramFloat * (paramArrayOfFloat[2] - paramArrayOfFloat[0]);
    arrayOfFloat[3] = paramArrayOfFloat[1] + paramFloat * (paramArrayOfFloat[3] - paramArrayOfFloat[1]);
    paramArrayOfFloat[2] = paramArrayOfFloat[2] + paramFloat * (paramArrayOfFloat[4] - paramArrayOfFloat[2]);
    paramArrayOfFloat[3] = paramArrayOfFloat[3] + paramFloat * (paramArrayOfFloat[5] - paramArrayOfFloat[3]);
    arrayOfFloat[4] = arrayOfFloat[2] + paramFloat * (paramArrayOfFloat[2] - arrayOfFloat[2]);
    paramArrayOfFloat[0] = arrayOfFloat[2] + paramFloat * (paramArrayOfFloat[2] - arrayOfFloat[2]);
    arrayOfFloat[5] = arrayOfFloat[3] + paramFloat * (paramArrayOfFloat[3] - arrayOfFloat[3]);
    paramArrayOfFloat[1] = arrayOfFloat[3] + paramFloat * (paramArrayOfFloat[3] - arrayOfFloat[3]);
    ProcessMonotonicQuad(paramProcessHandler, arrayOfFloat, paramArrayOfInt);
  }
  
  private static void DrawMonotonicCubic(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, boolean paramBoolean, int[] paramArrayOfInt) {
    int i = (int)(paramArrayOfFloat[0] * 1024.0F);
    int j = (int)(paramArrayOfFloat[1] * 1024.0F);
    int k = (int)(paramArrayOfFloat[6] * 1024.0F);
    int m = (int)(paramArrayOfFloat[7] * 1024.0F);
    int n = (i & 0x3FF) << 6;
    int i1 = (j & 0x3FF) << 6;
    char c = '耀';
    int i2 = 262144;
    byte b = 8;
    int i3 = 6;
    int i4 = (int)((-paramArrayOfFloat[0] + 3.0F * paramArrayOfFloat[2] - 3.0F * paramArrayOfFloat[4] + paramArrayOfFloat[6]) * 128.0F);
    int i5 = (int)((-paramArrayOfFloat[1] + 3.0F * paramArrayOfFloat[3] - 3.0F * paramArrayOfFloat[5] + paramArrayOfFloat[7]) * 128.0F);
    int i6 = (int)((3.0F * paramArrayOfFloat[0] - 6.0F * paramArrayOfFloat[2] + 3.0F * paramArrayOfFloat[4]) * 2048.0F);
    int i7 = (int)((3.0F * paramArrayOfFloat[1] - 6.0F * paramArrayOfFloat[3] + 3.0F * paramArrayOfFloat[5]) * 2048.0F);
    int i8 = (int)((-3.0F * paramArrayOfFloat[0] + 3.0F * paramArrayOfFloat[2]) * 8192.0F);
    int i9 = (int)((-3.0F * paramArrayOfFloat[1] + 3.0F * paramArrayOfFloat[3]) * 8192.0F);
    int i10 = 6 * i4;
    int i11 = 6 * i5;
    int i12 = i10 + i6;
    int i13 = i11 + i7;
    int i14 = i4 + (i6 >> 1) + i8;
    int i15 = i5 + (i7 >> 1) + i9;
    int i16 = i;
    int i17 = j;
    int i18 = i & 0xFFFFFC00;
    int i19 = j & 0xFFFFFC00;
    int i20 = k - i;
    int i21 = m - j;
    while (b > 0) {
      while (Math.abs(i12) > i2 || Math.abs(i13) > i2) {
        i12 = (i12 << 1) - i10;
        i13 = (i13 << 1) - i11;
        i14 = (i14 << 2) - (i12 >> 1);
        i15 = (i15 << 2) - (i13 >> 1);
        b <<= 1;
        i2 <<= 3;
        c <<= '\003';
        n <<= 3;
        i1 <<= 3;
        i3 += 3;
      } 
      while ((b & true) == 0 && i3 > 6 && Math.abs(i14) <= c && Math.abs(i15) <= c) {
        i14 = (i14 >> 2) + (i12 >> 3);
        i15 = (i15 >> 2) + (i13 >> 3);
        i12 = i12 + i10 >> 1;
        i13 = i13 + i11 >> 1;
        b >>= 1;
        i2 >>= 3;
        c >>= '\003';
        n >>= 3;
        i1 >>= 3;
        i3 -= 3;
      } 
      if (--b > 0) {
        n += i14;
        i1 += i15;
        i14 += i12;
        i15 += i13;
        i12 += i10;
        i13 += i11;
        int i22 = i16;
        int i23 = i17;
        i16 = i18 + (n >> i3);
        i17 = i19 + (i1 >> i3);
        if ((k - i16 ^ i20) < 0)
          i16 = k; 
        if ((m - i17 ^ i21) < 0)
          i17 = m; 
        paramProcessHandler.processFixedLine(i22, i23, i16, i17, paramArrayOfInt, paramBoolean, false);
        continue;
      } 
      paramProcessHandler.processFixedLine(i16, i17, k, m, paramArrayOfInt, paramBoolean, false);
    } 
  }
  
  private static void ProcessMonotonicCubic(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt) {
    float[] arrayOfFloat = new float[8];
    float f2 = paramArrayOfFloat[0];
    float f1 = f2;
    float f4 = paramArrayOfFloat[1];
    float f3 = f4;
    for (byte b = 2; b < 8; b += 2) {
      f1 = (f1 > paramArrayOfFloat[b]) ? paramArrayOfFloat[b] : f1;
      f2 = (f2 < paramArrayOfFloat[b]) ? paramArrayOfFloat[b] : f2;
      f3 = (f3 > paramArrayOfFloat[b + 1]) ? paramArrayOfFloat[b + 1] : f3;
      f4 = (f4 < paramArrayOfFloat[b + 1]) ? paramArrayOfFloat[b + 1] : f4;
    } 
    if (paramProcessHandler.clipMode == 0) {
      if (paramProcessHandler.dhnd.xMaxf < f1 || paramProcessHandler.dhnd.xMinf > f2 || paramProcessHandler.dhnd.yMaxf < f3 || paramProcessHandler.dhnd.yMinf > f4)
        return; 
    } else {
      if (paramProcessHandler.dhnd.yMaxf < f3 || paramProcessHandler.dhnd.yMinf > f4 || paramProcessHandler.dhnd.xMaxf < f1)
        return; 
      if (paramProcessHandler.dhnd.xMinf > f2) {
        paramArrayOfFloat[6] = paramProcessHandler.dhnd.xMinf;
        paramArrayOfFloat[4] = paramProcessHandler.dhnd.xMinf;
        paramArrayOfFloat[2] = paramProcessHandler.dhnd.xMinf;
        paramArrayOfFloat[0] = paramProcessHandler.dhnd.xMinf;
      } 
    } 
    if (f2 - f1 > 256.0F || f4 - f3 > 256.0F) {
      arrayOfFloat[6] = paramArrayOfFloat[6];
      arrayOfFloat[7] = paramArrayOfFloat[7];
      arrayOfFloat[4] = (paramArrayOfFloat[4] + paramArrayOfFloat[6]) / 2.0F;
      arrayOfFloat[5] = (paramArrayOfFloat[5] + paramArrayOfFloat[7]) / 2.0F;
      float f5 = (paramArrayOfFloat[2] + paramArrayOfFloat[4]) / 2.0F;
      float f6 = (paramArrayOfFloat[3] + paramArrayOfFloat[5]) / 2.0F;
      arrayOfFloat[2] = (f5 + arrayOfFloat[4]) / 2.0F;
      arrayOfFloat[3] = (f6 + arrayOfFloat[5]) / 2.0F;
      paramArrayOfFloat[2] = (paramArrayOfFloat[0] + paramArrayOfFloat[2]) / 2.0F;
      paramArrayOfFloat[3] = (paramArrayOfFloat[1] + paramArrayOfFloat[3]) / 2.0F;
      paramArrayOfFloat[4] = (paramArrayOfFloat[2] + f5) / 2.0F;
      paramArrayOfFloat[5] = (paramArrayOfFloat[3] + f6) / 2.0F;
      arrayOfFloat[0] = (paramArrayOfFloat[4] + arrayOfFloat[2]) / 2.0F;
      paramArrayOfFloat[6] = (paramArrayOfFloat[4] + arrayOfFloat[2]) / 2.0F;
      arrayOfFloat[1] = (paramArrayOfFloat[5] + arrayOfFloat[3]) / 2.0F;
      paramArrayOfFloat[7] = (paramArrayOfFloat[5] + arrayOfFloat[3]) / 2.0F;
      ProcessMonotonicCubic(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt);
      ProcessMonotonicCubic(paramProcessHandler, arrayOfFloat, paramArrayOfInt);
    } else {
      DrawMonotonicCubic(paramProcessHandler, paramArrayOfFloat, (paramProcessHandler.dhnd.xMinf > f1 || paramProcessHandler.dhnd.xMaxf < f2 || paramProcessHandler.dhnd.yMinf > f3 || paramProcessHandler.dhnd.yMaxf < f4), paramArrayOfInt);
    } 
  }
  
  private static void ProcessCubic(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt) {
    double[] arrayOfDouble1 = new double[4];
    double[] arrayOfDouble2 = new double[3];
    double[] arrayOfDouble3 = new double[2];
    byte b = 0;
    if ((paramArrayOfFloat[0] > paramArrayOfFloat[2] || paramArrayOfFloat[2] > paramArrayOfFloat[4] || paramArrayOfFloat[4] > paramArrayOfFloat[6]) && (paramArrayOfFloat[0] < paramArrayOfFloat[2] || paramArrayOfFloat[2] < paramArrayOfFloat[4] || paramArrayOfFloat[4] < paramArrayOfFloat[6])) {
      arrayOfDouble2[2] = (-paramArrayOfFloat[0] + 3.0F * paramArrayOfFloat[2] - 3.0F * paramArrayOfFloat[4] + paramArrayOfFloat[6]);
      arrayOfDouble2[1] = (2.0F * (paramArrayOfFloat[0] - 2.0F * paramArrayOfFloat[2] + paramArrayOfFloat[4]));
      arrayOfDouble2[0] = (-paramArrayOfFloat[0] + paramArrayOfFloat[2]);
      int i = QuadCurve2D.solveQuadratic(arrayOfDouble2, arrayOfDouble3);
      for (byte b1 = 0; b1 < i; b1++) {
        if (arrayOfDouble3[b1] > 0.0D && arrayOfDouble3[b1] < 1.0D)
          arrayOfDouble1[b++] = arrayOfDouble3[b1]; 
      } 
    } 
    if ((paramArrayOfFloat[1] > paramArrayOfFloat[3] || paramArrayOfFloat[3] > paramArrayOfFloat[5] || paramArrayOfFloat[5] > paramArrayOfFloat[7]) && (paramArrayOfFloat[1] < paramArrayOfFloat[3] || paramArrayOfFloat[3] < paramArrayOfFloat[5] || paramArrayOfFloat[5] < paramArrayOfFloat[7])) {
      arrayOfDouble2[2] = (-paramArrayOfFloat[1] + 3.0F * paramArrayOfFloat[3] - 3.0F * paramArrayOfFloat[5] + paramArrayOfFloat[7]);
      arrayOfDouble2[1] = (2.0F * (paramArrayOfFloat[1] - 2.0F * paramArrayOfFloat[3] + paramArrayOfFloat[5]));
      arrayOfDouble2[0] = (-paramArrayOfFloat[1] + paramArrayOfFloat[3]);
      int i = QuadCurve2D.solveQuadratic(arrayOfDouble2, arrayOfDouble3);
      for (byte b1 = 0; b1 < i; b1++) {
        if (arrayOfDouble3[b1] > 0.0D && arrayOfDouble3[b1] < 1.0D)
          arrayOfDouble1[b++] = arrayOfDouble3[b1]; 
      } 
    } 
    if (b > 0) {
      Arrays.sort(arrayOfDouble1, 0, b);
      ProcessFirstMonotonicPartOfCubic(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)arrayOfDouble1[0]);
      for (byte b1 = 1; b1 < b; b1++) {
        double d = arrayOfDouble1[b1] - arrayOfDouble1[b1 - true];
        if (d > 0.0D)
          ProcessFirstMonotonicPartOfCubic(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt, (float)(d / (1.0D - arrayOfDouble1[b1 - true]))); 
      } 
    } 
    ProcessMonotonicCubic(paramProcessHandler, paramArrayOfFloat, paramArrayOfInt);
  }
  
  private static void ProcessFirstMonotonicPartOfCubic(ProcessHandler paramProcessHandler, float[] paramArrayOfFloat, int[] paramArrayOfInt, float paramFloat) {
    float[] arrayOfFloat = new float[8];
    arrayOfFloat[0] = paramArrayOfFloat[0];
    arrayOfFloat[1] = paramArrayOfFloat[1];
    float f1 = paramArrayOfFloat[2] + paramFloat * (paramArrayOfFloat[4] - paramArrayOfFloat[2]);
    float f2 = paramArrayOfFloat[3] + paramFloat * (paramArrayOfFloat[5] - paramArrayOfFloat[3]);
    arrayOfFloat[2] = paramArrayOfFloat[0] + paramFloat * (paramArrayOfFloat[2] - paramArrayOfFloat[0]);
    arrayOfFloat[3] = paramArrayOfFloat[1] + paramFloat * (paramArrayOfFloat[3] - paramArrayOfFloat[1]);
    arrayOfFloat[4] = arrayOfFloat[2] + paramFloat * (f1 - arrayOfFloat[2]);
    arrayOfFloat[5] = arrayOfFloat[3] + paramFloat * (f2 - arrayOfFloat[3]);
    paramArrayOfFloat[4] = paramArrayOfFloat[4] + paramFloat * (paramArrayOfFloat[6] - paramArrayOfFloat[4]);
    paramArrayOfFloat[5] = paramArrayOfFloat[5] + paramFloat * (paramArrayOfFloat[7] - paramArrayOfFloat[5]);
    paramArrayOfFloat[2] = f1 + paramFloat * (paramArrayOfFloat[4] - f1);
    paramArrayOfFloat[3] = f2 + paramFloat * (paramArrayOfFloat[5] - f2);
    arrayOfFloat[6] = arrayOfFloat[4] + paramFloat * (paramArrayOfFloat[2] - arrayOfFloat[4]);
    paramArrayOfFloat[0] = arrayOfFloat[4] + paramFloat * (paramArrayOfFloat[2] - arrayOfFloat[4]);
    arrayOfFloat[7] = arrayOfFloat[5] + paramFloat * (paramArrayOfFloat[3] - arrayOfFloat[5]);
    paramArrayOfFloat[1] = arrayOfFloat[5] + paramFloat * (paramArrayOfFloat[3] - arrayOfFloat[5]);
    ProcessMonotonicCubic(paramProcessHandler, arrayOfFloat, paramArrayOfInt);
  }
  
  private static void ProcessLine(ProcessHandler paramProcessHandler, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int[] paramArrayOfInt) {
    boolean bool1 = false;
    float[] arrayOfFloat = { paramFloat1, paramFloat2, paramFloat3, paramFloat4, 0.0F, 0.0F };
    float f1 = paramProcessHandler.dhnd.xMinf;
    float f2 = paramProcessHandler.dhnd.yMinf;
    float f3 = paramProcessHandler.dhnd.xMaxf;
    float f4 = paramProcessHandler.dhnd.yMaxf;
    int i = TESTANDCLIP(f2, f4, arrayOfFloat, 1, 0, 3, 2);
    if (i == 4)
      return; 
    bool1 = IS_CLIPPED(i);
    i = TESTANDCLIP(f2, f4, arrayOfFloat, 3, 2, 1, 0);
    if (i == 4)
      return; 
    boolean bool2 = IS_CLIPPED(i);
    bool1 = (bool1 || bool2);
    if (paramProcessHandler.clipMode == 0) {
      i = TESTANDCLIP(f1, f3, arrayOfFloat, 0, 1, 2, 3);
      if (i == 4)
        return; 
      bool1 = (bool1 || IS_CLIPPED(i));
      i = TESTANDCLIP(f1, f3, arrayOfFloat, 2, 3, 0, 1);
      if (i == 4)
        return; 
      bool2 = (bool2 || IS_CLIPPED(i));
      bool1 = (bool1 || bool2);
      int j = (int)(arrayOfFloat[0] * 1024.0F);
      int k = (int)(arrayOfFloat[1] * 1024.0F);
      int m = (int)(arrayOfFloat[2] * 1024.0F);
      int n = (int)(arrayOfFloat[3] * 1024.0F);
      paramProcessHandler.processFixedLine(j, k, m, n, paramArrayOfInt, bool1, bool2);
    } else {
      i = CLIPCLAMP(f1, f3, arrayOfFloat, 0, 1, 2, 3, 4, 5);
      int j = (int)(arrayOfFloat[0] * 1024.0F);
      int k = (int)(arrayOfFloat[1] * 1024.0F);
      if (i == 0) {
        int i1 = (int)(arrayOfFloat[4] * 1024.0F);
        int i2 = (int)(arrayOfFloat[5] * 1024.0F);
        paramProcessHandler.processFixedLine(i1, i2, j, k, paramArrayOfInt, false, bool2);
      } else if (i == 4) {
        return;
      } 
      i = CLIPCLAMP(f1, f3, arrayOfFloat, 2, 3, 0, 1, 4, 5);
      bool2 = (bool2 || i == 1);
      int m = (int)(arrayOfFloat[2] * 1024.0F);
      int n = (int)(arrayOfFloat[3] * 1024.0F);
      paramProcessHandler.processFixedLine(j, k, m, n, paramArrayOfInt, false, bool2);
      if (i == 0) {
        int i1 = (int)(arrayOfFloat[4] * 1024.0F);
        int i2 = (int)(arrayOfFloat[5] * 1024.0F);
        paramProcessHandler.processFixedLine(m, n, i1, i2, paramArrayOfInt, false, bool2);
      } 
    } 
  }
  
  private static boolean doProcessPath(ProcessHandler paramProcessHandler, Path2D.Float paramFloat, float paramFloat1, float paramFloat2) {
    float[] arrayOfFloat1 = new float[8];
    float[] arrayOfFloat2 = new float[8];
    float[] arrayOfFloat3 = { 0.0F, 0.0F };
    float[] arrayOfFloat4 = new float[2];
    int[] arrayOfInt = new int[5];
    boolean bool1 = false;
    boolean bool2 = false;
    arrayOfInt[0] = 0;
    paramProcessHandler.dhnd.adjustBounds(-1048576, -1048576, 1048576, 1048576);
    if (paramProcessHandler.dhnd.strokeControl == 2) {
      arrayOfFloat3[0] = -0.5F;
      arrayOfFloat3[1] = -0.5F;
      paramFloat1 = (float)(paramFloat1 - 0.5D);
      paramFloat2 = (float)(paramFloat2 - 0.5D);
    } 
    PathIterator pathIterator = paramFloat.getPathIterator(null);
    while (!pathIterator.isDone()) {
      float f2;
      float f1;
      switch (pathIterator.currentSegment(arrayOfFloat1)) {
        case 0:
          if (bool1 && !bool2) {
            if (paramProcessHandler.clipMode == 1 && (arrayOfFloat2[0] != arrayOfFloat3[0] || arrayOfFloat2[1] != arrayOfFloat3[1]))
              ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat3[0], arrayOfFloat3[1], arrayOfInt); 
            paramProcessHandler.processEndSubPath();
          } 
          arrayOfFloat2[0] = arrayOfFloat1[0] + paramFloat1;
          arrayOfFloat2[1] = arrayOfFloat1[1] + paramFloat2;
          if (arrayOfFloat2[0] < 8.5070587E37F && arrayOfFloat2[0] > -8.5070587E37F && arrayOfFloat2[1] < 8.5070587E37F && arrayOfFloat2[1] > -8.5070587E37F) {
            bool1 = true;
            bool2 = false;
            arrayOfFloat3[0] = arrayOfFloat2[0];
            arrayOfFloat3[1] = arrayOfFloat2[1];
          } else {
            bool2 = true;
          } 
          arrayOfInt[0] = 0;
          break;
        case 1:
          f1 = arrayOfFloat2[2] = arrayOfFloat1[0] + paramFloat1;
          f2 = arrayOfFloat2[3] = arrayOfFloat1[1] + paramFloat2;
          if (f1 < 8.5070587E37F && f1 > -8.5070587E37F && f2 < 8.5070587E37F && f2 > -8.5070587E37F) {
            if (bool2) {
              arrayOfFloat3[0] = f1;
              arrayOfFloat2[0] = f1;
              arrayOfFloat3[1] = f2;
              arrayOfFloat2[1] = f2;
              bool1 = true;
              bool2 = false;
              break;
            } 
            ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat2[2], arrayOfFloat2[3], arrayOfInt);
            arrayOfFloat2[0] = f1;
            arrayOfFloat2[1] = f2;
          } 
          break;
        case 2:
          arrayOfFloat2[2] = arrayOfFloat1[0] + paramFloat1;
          arrayOfFloat2[3] = arrayOfFloat1[1] + paramFloat2;
          f1 = arrayOfFloat2[4] = arrayOfFloat1[2] + paramFloat1;
          f2 = arrayOfFloat2[5] = arrayOfFloat1[3] + paramFloat2;
          if (f1 < 8.5070587E37F && f1 > -8.5070587E37F && f2 < 8.5070587E37F && f2 > -8.5070587E37F) {
            if (bool2) {
              arrayOfFloat3[0] = f1;
              arrayOfFloat2[0] = f1;
              arrayOfFloat3[1] = f2;
              arrayOfFloat2[1] = f2;
              bool1 = true;
              bool2 = false;
              break;
            } 
            if (arrayOfFloat2[2] < 8.5070587E37F && arrayOfFloat2[2] > -8.5070587E37F && arrayOfFloat2[3] < 8.5070587E37F && arrayOfFloat2[3] > -8.5070587E37F) {
              ProcessQuad(paramProcessHandler, arrayOfFloat2, arrayOfInt);
            } else {
              ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat2[4], arrayOfFloat2[5], arrayOfInt);
            } 
            arrayOfFloat2[0] = f1;
            arrayOfFloat2[1] = f2;
          } 
          break;
        case 3:
          arrayOfFloat2[2] = arrayOfFloat1[0] + paramFloat1;
          arrayOfFloat2[3] = arrayOfFloat1[1] + paramFloat2;
          arrayOfFloat2[4] = arrayOfFloat1[2] + paramFloat1;
          arrayOfFloat2[5] = arrayOfFloat1[3] + paramFloat2;
          f1 = arrayOfFloat2[6] = arrayOfFloat1[4] + paramFloat1;
          f2 = arrayOfFloat2[7] = arrayOfFloat1[5] + paramFloat2;
          if (f1 < 8.5070587E37F && f1 > -8.5070587E37F && f2 < 8.5070587E37F && f2 > -8.5070587E37F) {
            if (bool2) {
              arrayOfFloat3[0] = arrayOfFloat2[6];
              arrayOfFloat2[0] = arrayOfFloat2[6];
              arrayOfFloat3[1] = arrayOfFloat2[7];
              arrayOfFloat2[1] = arrayOfFloat2[7];
              bool1 = true;
              bool2 = false;
              break;
            } 
            if (arrayOfFloat2[2] < 8.5070587E37F && arrayOfFloat2[2] > -8.5070587E37F && arrayOfFloat2[3] < 8.5070587E37F && arrayOfFloat2[3] > -8.5070587E37F && arrayOfFloat2[4] < 8.5070587E37F && arrayOfFloat2[4] > -8.5070587E37F && arrayOfFloat2[5] < 8.5070587E37F && arrayOfFloat2[5] > -8.5070587E37F) {
              ProcessCubic(paramProcessHandler, arrayOfFloat2, arrayOfInt);
            } else {
              ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat2[6], arrayOfFloat2[7], arrayOfInt);
            } 
            arrayOfFloat2[0] = f1;
            arrayOfFloat2[1] = f2;
          } 
          break;
        case 4:
          if (bool1 && !bool2) {
            bool2 = false;
            if (arrayOfFloat2[0] != arrayOfFloat3[0] || arrayOfFloat2[1] != arrayOfFloat3[1]) {
              ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat3[0], arrayOfFloat3[1], arrayOfInt);
              arrayOfFloat2[0] = arrayOfFloat3[0];
              arrayOfFloat2[1] = arrayOfFloat3[1];
            } 
            paramProcessHandler.processEndSubPath();
          } 
          break;
      } 
      pathIterator.next();
    } 
    if (bool1 & (!bool2 ? 1 : 0)) {
      if (paramProcessHandler.clipMode == 1 && (arrayOfFloat2[0] != arrayOfFloat3[0] || arrayOfFloat2[1] != arrayOfFloat3[1]))
        ProcessLine(paramProcessHandler, arrayOfFloat2[0], arrayOfFloat2[1], arrayOfFloat3[0], arrayOfFloat3[1], arrayOfInt); 
      paramProcessHandler.processEndSubPath();
    } 
    return true;
  }
  
  private static void FillPolygon(FillProcessHandler paramFillProcessHandler, int paramInt) {
    int k = paramFillProcessHandler.dhnd.xMax - 1;
    FillData fillData = paramFillProcessHandler.fd;
    int m = fillData.plgYMin;
    int n = fillData.plgYMax;
    int i1 = (n - m >> 10) + 4;
    int i2 = m - 1 & 0xFFFFFC00;
    int i3 = (paramInt == 1) ? -1 : 1;
    List list = fillData.plgPnts;
    int j = list.size();
    if (j <= 1)
      return; 
    Point[] arrayOfPoint = new Point[i1];
    Point point1 = (Point)list.get(0);
    point1.prev = null;
    for (byte b2 = 0; b2 < j - 1; b2++) {
      point1 = (Point)list.get(b2);
      Point point = (Point)list.get(b2 + 1);
      int i5 = point1.y - i2 - 1 >> 10;
      point1.nextByY = arrayOfPoint[i5];
      arrayOfPoint[i5] = point1;
      point1.next = point;
      point.prev = point1;
    } 
    Point point2 = (Point)list.get(j - 1);
    int i4 = point2.y - i2 - 1 >> 10;
    point2.nextByY = arrayOfPoint[i4];
    arrayOfPoint[i4] = point2;
    ActiveEdgeList activeEdgeList = new ActiveEdgeList(null);
    int i = i2 + 1024;
    for (byte b1 = 0; i <= n && b1 < i1; b1++) {
      for (Point point = arrayOfPoint[b1]; point != null; point = point.nextByY) {
        if (point.prev != null && !point.prev.lastPoint)
          if (point.prev.edge != null && point.prev.y <= i) {
            activeEdgeList.delete(point.prev.edge);
            point.prev.edge = null;
          } else if (point.prev.y > i) {
            activeEdgeList.insert(point.prev, i);
          }  
        if (!point.lastPoint && point.next != null)
          if (point.edge != null && point.next.y <= i) {
            activeEdgeList.delete(point.edge);
            point.edge = null;
          } else if (point.next.y > i) {
            activeEdgeList.insert(point, i);
          }  
      } 
      if (!activeEdgeList.isEmpty()) {
        activeEdgeList.sort();
        int i5 = 0;
        boolean bool = false;
        int i7 = paramFillProcessHandler.dhnd.xMin;
        int i6 = i7;
        for (Edge edge = activeEdgeList.head; edge != null; edge = edge.next) {
          i5 += edge.dir;
          if ((i5 & i3) != 0 && !bool) {
            i6 = edge.x + 1024 - 1 >> 10;
            bool = true;
          } 
          if ((i5 & i3) == 0 && bool) {
            i7 = edge.x - 1 >> 10;
            if (i6 <= i7)
              paramFillProcessHandler.dhnd.drawScanline(i6, i7, i >> 10); 
            bool = false;
          } 
          edge.x += edge.dx;
        } 
        if (bool && i6 <= k)
          paramFillProcessHandler.dhnd.drawScanline(i6, k, i >> 10); 
      } 
      i += 1024;
    } 
  }
  
  private static class ActiveEdgeList {
    ProcessPath.Edge head;
    
    private ActiveEdgeList() {}
    
    public boolean isEmpty() { return (this.head == null); }
    
    public void insert(ProcessPath.Point param1Point, int param1Int) {
      byte b;
      int i4;
      int i3;
      int i2;
      ProcessPath.Point point = param1Point.next;
      int i = param1Point.x;
      int j = param1Point.y;
      int k = point.x;
      int m = point.y;
      if (j == m)
        return; 
      int n = k - i;
      int i1 = m - j;
      if (j < m) {
        i3 = i;
        i4 = param1Int - j;
        b = -1;
      } else {
        i3 = k;
        i4 = param1Int - m;
        b = 1;
      } 
      if (n > 1048576.0F || n < -1048576.0F) {
        i2 = (int)(n * 1024.0D / i1);
        i3 += (int)(n * i4 / i1);
      } else {
        i2 = (n << 10) / i1;
        i3 += n * i4 / i1;
      } 
      ProcessPath.Edge edge = new ProcessPath.Edge(param1Point, i3, i2, b);
      edge.next = this.head;
      edge.prev = null;
      if (this.head != null)
        this.head.prev = edge; 
      this.head = param1Point.edge = edge;
    }
    
    public void delete(ProcessPath.Edge param1Edge) {
      ProcessPath.Edge edge1 = param1Edge.prev;
      ProcessPath.Edge edge2 = param1Edge.next;
      if (edge1 != null) {
        edge1.next = edge2;
      } else {
        this.head = edge2;
      } 
      if (edge2 != null)
        edge2.prev = edge1; 
    }
    
    public void sort() {
      ProcessPath.Edge edge3 = null;
      boolean bool = true;
      while (edge3 != this.head.next && bool) {
        ProcessPath.Edge edge4 = this.head;
        ProcessPath.Edge edge6 = edge4;
        ProcessPath.Edge edge5 = edge4.next;
        bool = false;
        while (edge4 != edge3) {
          if (edge4.x >= edge5.x) {
            bool = true;
            if (edge4 == this.head) {
              ProcessPath.Edge edge = edge5.next;
              edge5.next = edge4;
              edge4.next = edge;
              this.head = edge5;
              edge6 = edge5;
            } else {
              ProcessPath.Edge edge = edge5.next;
              edge5.next = edge4;
              edge4.next = edge;
              edge6.next = edge5;
              edge6 = edge5;
            } 
          } else {
            edge6 = edge4;
            edge4 = edge4.next;
          } 
          edge5 = edge4.next;
          if (edge5 == edge3)
            edge3 = edge4; 
        } 
      } 
      ProcessPath.Edge edge1 = this.head;
      ProcessPath.Edge edge2 = null;
      while (edge1 != null) {
        edge1.prev = edge2;
        edge2 = edge1;
        edge1 = edge1.next;
      } 
    }
  }
  
  public static abstract class DrawHandler {
    public int xMin;
    
    public int yMin;
    
    public int xMax;
    
    public int yMax;
    
    public float xMinf;
    
    public float yMinf;
    
    public float xMaxf;
    
    public float yMaxf;
    
    public int strokeControl;
    
    public DrawHandler(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { setBounds(param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void setBounds(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.xMin = param1Int1;
      this.yMin = param1Int2;
      this.xMax = param1Int3;
      this.yMax = param1Int4;
      this.xMinf = param1Int1 - 0.5F;
      this.yMinf = param1Int2 - 0.5F;
      this.xMaxf = param1Int3 - 0.5F - 9.765625E-4F;
      this.yMaxf = param1Int4 - 0.5F - 9.765625E-4F;
    }
    
    public void setBounds(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      this.strokeControl = param1Int5;
      setBounds(param1Int1, param1Int2, param1Int3, param1Int4);
    }
    
    public void adjustBounds(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (this.xMin > param1Int1)
        param1Int1 = this.xMin; 
      if (this.xMax < param1Int3)
        param1Int3 = this.xMax; 
      if (this.yMin > param1Int2)
        param1Int2 = this.yMin; 
      if (this.yMax < param1Int4)
        param1Int4 = this.yMax; 
      setBounds(param1Int1, param1Int2, param1Int3, param1Int4);
    }
    
    public DrawHandler(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { this(param1Int1, param1Int2, param1Int3, param1Int4, 0); }
    
    public abstract void drawLine(int param1Int1, int param1Int2, int param1Int3, int param1Int4);
    
    public abstract void drawPixel(int param1Int1, int param1Int2);
    
    public abstract void drawScanline(int param1Int1, int param1Int2, int param1Int3);
  }
  
  private static class DrawProcessHandler extends ProcessHandler {
    ProcessPath.EndSubPathHandler processESP;
    
    public DrawProcessHandler(ProcessPath.DrawHandler param1DrawHandler, ProcessPath.EndSubPathHandler param1EndSubPathHandler) {
      super(param1DrawHandler, 0);
      this.dhnd = param1DrawHandler;
      this.processESP = param1EndSubPathHandler;
    }
    
    public void processEndSubPath() { this.processESP.processEndSubPath(); }
    
    void PROCESS_LINE(int param1Int1, int param1Int2, int param1Int3, int param1Int4, boolean param1Boolean, int[] param1ArrayOfInt) {
      int i = param1Int1 >> 10;
      int j = param1Int2 >> 10;
      int k = param1Int3 >> 10;
      int m = param1Int4 >> 10;
      if ((i ^ k | j ^ m) == 0) {
        if (param1Boolean && (this.dhnd.yMin > j || this.dhnd.yMax <= j || this.dhnd.xMin > i || this.dhnd.xMax <= i))
          return; 
        if (param1ArrayOfInt[0] == 0) {
          param1ArrayOfInt[0] = 1;
          param1ArrayOfInt[1] = i;
          param1ArrayOfInt[2] = j;
          param1ArrayOfInt[3] = i;
          param1ArrayOfInt[4] = j;
          this.dhnd.drawPixel(i, j);
        } else if ((i != param1ArrayOfInt[3] || j != param1ArrayOfInt[4]) && (i != param1ArrayOfInt[1] || j != param1ArrayOfInt[2])) {
          this.dhnd.drawPixel(i, j);
          param1ArrayOfInt[3] = i;
          param1ArrayOfInt[4] = j;
        } 
        return;
      } 
      if ((!param1Boolean || (this.dhnd.yMin <= j && this.dhnd.yMax > j && this.dhnd.xMin <= i && this.dhnd.xMax > i)) && param1ArrayOfInt[0] == 1 && ((param1ArrayOfInt[1] == i && param1ArrayOfInt[2] == j) || (param1ArrayOfInt[3] == i && param1ArrayOfInt[4] == j)))
        this.dhnd.drawPixel(i, j); 
      this.dhnd.drawLine(i, j, k, m);
      if (param1ArrayOfInt[0] == 0) {
        param1ArrayOfInt[0] = 1;
        param1ArrayOfInt[1] = i;
        param1ArrayOfInt[2] = j;
        param1ArrayOfInt[3] = i;
        param1ArrayOfInt[4] = j;
      } 
      if ((param1ArrayOfInt[1] == k && param1ArrayOfInt[2] == m) || (param1ArrayOfInt[3] == k && param1ArrayOfInt[4] == m)) {
        if (param1Boolean && (this.dhnd.yMin > m || this.dhnd.yMax <= m || this.dhnd.xMin > k || this.dhnd.xMax <= k))
          return; 
        this.dhnd.drawPixel(k, m);
      } 
      param1ArrayOfInt[3] = k;
      param1ArrayOfInt[4] = m;
    }
    
    void PROCESS_POINT(int param1Int1, int param1Int2, boolean param1Boolean, int[] param1ArrayOfInt) {
      int i = param1Int1 >> 10;
      int j = param1Int2 >> 10;
      if (param1Boolean && (this.dhnd.yMin > j || this.dhnd.yMax <= j || this.dhnd.xMin > i || this.dhnd.xMax <= i))
        return; 
      if (param1ArrayOfInt[0] == 0) {
        param1ArrayOfInt[0] = 1;
        param1ArrayOfInt[1] = i;
        param1ArrayOfInt[2] = j;
        param1ArrayOfInt[3] = i;
        param1ArrayOfInt[4] = j;
        this.dhnd.drawPixel(i, j);
      } else if ((i != param1ArrayOfInt[3] || j != param1ArrayOfInt[4]) && (i != param1ArrayOfInt[1] || j != param1ArrayOfInt[2])) {
        this.dhnd.drawPixel(i, j);
        param1ArrayOfInt[3] = i;
        param1ArrayOfInt[4] = j;
      } 
    }
    
    public void processFixedLine(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int[] param1ArrayOfInt, boolean param1Boolean1, boolean param1Boolean2) {
      int n;
      int m;
      int k;
      int j;
      int i = param1Int1 ^ param1Int3 | param1Int2 ^ param1Int4;
      if ((i & 0xFFFFFC00) == 0) {
        if (i == 0)
          PROCESS_POINT(param1Int1 + 512, param1Int2 + 512, param1Boolean1, param1ArrayOfInt); 
        return;
      } 
      if (param1Int1 == param1Int3 || param1Int2 == param1Int4) {
        j = param1Int1 + 512;
        m = param1Int3 + 512;
        k = param1Int2 + 512;
        n = param1Int4 + 512;
      } else {
        int i1 = param1Int3 - param1Int1;
        int i2 = param1Int4 - param1Int2;
        int i3 = param1Int1 & 0xFFFFFC00;
        int i4 = param1Int2 & 0xFFFFFC00;
        int i5 = param1Int3 & 0xFFFFFC00;
        int i6 = param1Int4 & 0xFFFFFC00;
        if (i3 == param1Int1 || i4 == param1Int2) {
          j = param1Int1 + 512;
          k = param1Int2 + 512;
        } else {
          int i7 = (param1Int1 < param1Int3) ? (i3 + 1024) : i3;
          int i8 = (param1Int2 < param1Int4) ? (i4 + 1024) : i4;
          int i9 = param1Int2 + (i7 - param1Int1) * i2 / i1;
          if (i9 >= i4 && i9 <= i4 + 1024) {
            j = i7;
            k = i9 + 512;
          } else {
            i9 = param1Int1 + (i8 - param1Int2) * i1 / i2;
            j = i9 + 512;
            k = i8;
          } 
        } 
        if (i5 == param1Int3 || i6 == param1Int4) {
          m = param1Int3 + 512;
          n = param1Int4 + 512;
        } else {
          int i7 = (param1Int1 > param1Int3) ? (i5 + 1024) : i5;
          int i8 = (param1Int2 > param1Int4) ? (i6 + 1024) : i6;
          int i9 = param1Int4 + (i7 - param1Int3) * i2 / i1;
          if (i9 >= i6 && i9 <= i6 + 1024) {
            m = i7;
            n = i9 + 512;
          } else {
            i9 = param1Int3 + (i8 - param1Int4) * i1 / i2;
            m = i9 + 512;
            n = i8;
          } 
        } 
      } 
      PROCESS_LINE(j, k, m, n, param1Boolean1, param1ArrayOfInt);
    }
  }
  
  private static class Edge {
    int x;
    
    int dx;
    
    ProcessPath.Point p;
    
    int dir;
    
    Edge prev;
    
    Edge next;
    
    public Edge(ProcessPath.Point param1Point, int param1Int1, int param1Int2, int param1Int3) {
      this.p = param1Point;
      this.x = param1Int1;
      this.dx = param1Int2;
      this.dir = param1Int3;
    }
  }
  
  public static interface EndSubPathHandler {
    void processEndSubPath();
  }
  
  private static class FillData {
    List<ProcessPath.Point> plgPnts = new Vector(256);
    
    public int plgYMin;
    
    public int plgYMax;
    
    public void addPoint(int param1Int1, int param1Int2, boolean param1Boolean) {
      if (this.plgPnts.size() == 0) {
        this.plgYMin = this.plgYMax = param1Int2;
      } else {
        this.plgYMin = (this.plgYMin > param1Int2) ? param1Int2 : this.plgYMin;
        this.plgYMax = (this.plgYMax < param1Int2) ? param1Int2 : this.plgYMax;
      } 
      this.plgPnts.add(new ProcessPath.Point(param1Int1, param1Int2, param1Boolean));
    }
    
    public boolean isEmpty() { return (this.plgPnts.size() == 0); }
    
    public boolean isEnded() { return ((ProcessPath.Point)this.plgPnts.get(this.plgPnts.size() - 1)).lastPoint; }
    
    public boolean setEnded() { return ((ProcessPath.Point)this.plgPnts.get(this.plgPnts.size() - 1)).lastPoint = true; }
  }
  
  private static class FillProcessHandler extends ProcessHandler {
    ProcessPath.FillData fd = new ProcessPath.FillData();
    
    public void processFixedLine(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int[] param1ArrayOfInt, boolean param1Boolean1, boolean param1Boolean2) {
      if (param1Boolean1) {
        int[] arrayOfInt = { param1Int1, param1Int2, param1Int3, param1Int4, 0, 0 };
        int i = (int)(this.dhnd.xMinf * 1024.0F);
        int j = (int)(this.dhnd.xMaxf * 1024.0F);
        int k = (int)(this.dhnd.yMinf * 1024.0F);
        int m = (int)(this.dhnd.yMaxf * 1024.0F);
        int n = ProcessPath.TESTANDCLIP(k, m, arrayOfInt, 1, 0, 3, 2);
        if (n == 4)
          return; 
        n = ProcessPath.TESTANDCLIP(k, m, arrayOfInt, 3, 2, 1, 0);
        if (n == 4)
          return; 
        boolean bool = ProcessPath.IS_CLIPPED(n);
        n = ProcessPath.CLIPCLAMP(i, j, arrayOfInt, 0, 1, 2, 3, 4, 5);
        if (n == 0) {
          processFixedLine(arrayOfInt[4], arrayOfInt[5], arrayOfInt[0], arrayOfInt[1], param1ArrayOfInt, false, bool);
        } else if (n == 4) {
          return;
        } 
        n = ProcessPath.CLIPCLAMP(i, j, arrayOfInt, 2, 3, 0, 1, 4, 5);
        bool = (bool || n == 1);
        processFixedLine(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3], param1ArrayOfInt, false, bool);
        if (n == 0)
          processFixedLine(arrayOfInt[2], arrayOfInt[3], arrayOfInt[4], arrayOfInt[5], param1ArrayOfInt, false, bool); 
        return;
      } 
      if (this.fd.isEmpty() || this.fd.isEnded())
        this.fd.addPoint(param1Int1, param1Int2, false); 
      this.fd.addPoint(param1Int3, param1Int4, false);
      if (param1Boolean2)
        this.fd.setEnded(); 
    }
    
    FillProcessHandler(ProcessPath.DrawHandler param1DrawHandler) { super(param1DrawHandler, 1); }
    
    public void processEndSubPath() {
      if (!this.fd.isEmpty())
        this.fd.setEnded(); 
    }
  }
  
  private static class Point {
    public int x;
    
    public int y;
    
    public boolean lastPoint;
    
    public Point prev;
    
    public Point next;
    
    public Point nextByY;
    
    public ProcessPath.Edge edge;
    
    public Point(int param1Int1, int param1Int2, boolean param1Boolean) {
      this.x = param1Int1;
      this.y = param1Int2;
      this.lastPoint = param1Boolean;
    }
  }
  
  public static abstract class ProcessHandler implements EndSubPathHandler {
    ProcessPath.DrawHandler dhnd;
    
    int clipMode;
    
    public ProcessHandler(ProcessPath.DrawHandler param1DrawHandler, int param1Int) {
      this.dhnd = param1DrawHandler;
      this.clipMode = param1Int;
    }
    
    public abstract void processFixedLine(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int[] param1ArrayOfInt, boolean param1Boolean1, boolean param1Boolean2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\ProcessPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */