package javax.swing;

import java.io.Serializable;

public class SizeRequirements implements Serializable {
  public int minimum = 0;
  
  public int preferred = 0;
  
  public int maximum = 0;
  
  public float alignment = 0.5F;
  
  public SizeRequirements() {}
  
  public SizeRequirements(int paramInt1, int paramInt2, int paramInt3, float paramFloat) {}
  
  public String toString() { return "[" + this.minimum + "," + this.preferred + "," + this.maximum + "]@" + this.alignment; }
  
  public static SizeRequirements getTiledSizeRequirements(SizeRequirements[] paramArrayOfSizeRequirements) {
    SizeRequirements sizeRequirements = new SizeRequirements();
    for (byte b = 0; b < paramArrayOfSizeRequirements.length; b++) {
      SizeRequirements sizeRequirements1 = paramArrayOfSizeRequirements[b];
      sizeRequirements.minimum = (int)Math.min(sizeRequirements.minimum + sizeRequirements1.minimum, 2147483647L);
      sizeRequirements.preferred = (int)Math.min(sizeRequirements.preferred + sizeRequirements1.preferred, 2147483647L);
      sizeRequirements.maximum = (int)Math.min(sizeRequirements.maximum + sizeRequirements1.maximum, 2147483647L);
    } 
    return sizeRequirements;
  }
  
  public static SizeRequirements getAlignedSizeRequirements(SizeRequirements[] paramArrayOfSizeRequirements) {
    SizeRequirements sizeRequirements1 = new SizeRequirements();
    SizeRequirements sizeRequirements2 = new SizeRequirements();
    int i;
    for (i = 0; i < paramArrayOfSizeRequirements.length; i++) {
      SizeRequirements sizeRequirements = paramArrayOfSizeRequirements[i];
      int m = (int)(sizeRequirements.alignment * sizeRequirements.minimum);
      int n = sizeRequirements.minimum - m;
      sizeRequirements1.minimum = Math.max(m, sizeRequirements1.minimum);
      sizeRequirements2.minimum = Math.max(n, sizeRequirements2.minimum);
      m = (int)(sizeRequirements.alignment * sizeRequirements.preferred);
      n = sizeRequirements.preferred - m;
      sizeRequirements1.preferred = Math.max(m, sizeRequirements1.preferred);
      sizeRequirements2.preferred = Math.max(n, sizeRequirements2.preferred);
      m = (int)(sizeRequirements.alignment * sizeRequirements.maximum);
      n = sizeRequirements.maximum - m;
      sizeRequirements1.maximum = Math.max(m, sizeRequirements1.maximum);
      sizeRequirements2.maximum = Math.max(n, sizeRequirements2.maximum);
    } 
    i = (int)Math.min(sizeRequirements1.minimum + sizeRequirements2.minimum, 2147483647L);
    int j = (int)Math.min(sizeRequirements1.preferred + sizeRequirements2.preferred, 2147483647L);
    int k = (int)Math.min(sizeRequirements1.maximum + sizeRequirements2.maximum, 2147483647L);
    float f = 0.0F;
    if (i > 0) {
      f = sizeRequirements1.minimum / i;
      f = (f > 1.0F) ? 1.0F : ((f < 0.0F) ? 0.0F : f);
    } 
    return new SizeRequirements(i, j, k, f);
  }
  
  public static void calculateTiledPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2) { calculateTiledPositions(paramInt, paramSizeRequirements, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, true); }
  
  public static void calculateTiledPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean) {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    for (byte b = 0; b < paramArrayOfSizeRequirements.length; b++) {
      l1 += (paramArrayOfSizeRequirements[b]).minimum;
      l2 += (paramArrayOfSizeRequirements[b]).preferred;
      l3 += (paramArrayOfSizeRequirements[b]).maximum;
    } 
    if (paramInt >= l2) {
      expandedTile(paramInt, l1, l2, l3, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, paramBoolean);
    } else {
      compressedTile(paramInt, l1, l2, l3, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, paramBoolean);
    } 
  }
  
  private static void compressedTile(int paramInt, long paramLong1, long paramLong2, long paramLong3, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean) {
    float f1 = (float)Math.min(paramLong2 - paramInt, paramLong2 - paramLong1);
    float f2 = (paramLong2 - paramLong1 == 0L) ? 0.0F : (f1 / (float)(paramLong2 - paramLong1));
    if (paramBoolean) {
      int i = 0;
      for (byte b = 0; b < paramArrayOfInt2.length; b++) {
        paramArrayOfInt1[b] = i;
        SizeRequirements sizeRequirements = paramArrayOfSizeRequirements[b];
        float f = f2 * (sizeRequirements.preferred - sizeRequirements.minimum);
        paramArrayOfInt2[b] = (int)(sizeRequirements.preferred - f);
        i = (int)Math.min(i + paramArrayOfInt2[b], 2147483647L);
      } 
    } else {
      int i = paramInt;
      for (byte b = 0; b < paramArrayOfInt2.length; b++) {
        SizeRequirements sizeRequirements = paramArrayOfSizeRequirements[b];
        float f = f2 * (sizeRequirements.preferred - sizeRequirements.minimum);
        paramArrayOfInt2[b] = (int)(sizeRequirements.preferred - f);
        paramArrayOfInt1[b] = i - paramArrayOfInt2[b];
        i = (int)Math.max(i - paramArrayOfInt2[b], 0L);
      } 
    } 
  }
  
  private static void expandedTile(int paramInt, long paramLong1, long paramLong2, long paramLong3, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean) {
    float f1 = (float)Math.min(paramInt - paramLong2, paramLong3 - paramLong2);
    float f2 = (paramLong3 - paramLong2 == 0L) ? 0.0F : (f1 / (float)(paramLong3 - paramLong2));
    if (paramBoolean) {
      int i = 0;
      for (byte b = 0; b < paramArrayOfInt2.length; b++) {
        paramArrayOfInt1[b] = i;
        SizeRequirements sizeRequirements = paramArrayOfSizeRequirements[b];
        int j = (int)(f2 * (sizeRequirements.maximum - sizeRequirements.preferred));
        paramArrayOfInt2[b] = (int)Math.min(sizeRequirements.preferred + j, 2147483647L);
        i = (int)Math.min(i + paramArrayOfInt2[b], 2147483647L);
      } 
    } else {
      int i = paramInt;
      for (byte b = 0; b < paramArrayOfInt2.length; b++) {
        SizeRequirements sizeRequirements = paramArrayOfSizeRequirements[b];
        int j = (int)(f2 * (sizeRequirements.maximum - sizeRequirements.preferred));
        paramArrayOfInt2[b] = (int)Math.min(sizeRequirements.preferred + j, 2147483647L);
        paramArrayOfInt1[b] = i - paramArrayOfInt2[b];
        i = (int)Math.max(i - paramArrayOfInt2[b], 0L);
      } 
    } 
  }
  
  public static void calculateAlignedPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2) { calculateAlignedPositions(paramInt, paramSizeRequirements, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2, true); }
  
  public static void calculateAlignedPositions(int paramInt, SizeRequirements paramSizeRequirements, SizeRequirements[] paramArrayOfSizeRequirements, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean) {
    float f = paramBoolean ? paramSizeRequirements.alignment : (1.0F - paramSizeRequirements.alignment);
    int i = (int)(paramInt * f);
    int j = paramInt - i;
    for (byte b = 0; b < paramArrayOfSizeRequirements.length; b++) {
      SizeRequirements sizeRequirements = paramArrayOfSizeRequirements[b];
      float f1 = paramBoolean ? sizeRequirements.alignment : (1.0F - sizeRequirements.alignment);
      int k = (int)(sizeRequirements.maximum * f1);
      int m = sizeRequirements.maximum - k;
      int n = Math.min(i, k);
      int i1 = Math.min(j, m);
      paramArrayOfInt1[b] = i - n;
      paramArrayOfInt2[b] = (int)Math.min(n + i1, 2147483647L);
    } 
  }
  
  public static int[] adjustSizes(int paramInt, SizeRequirements[] paramArrayOfSizeRequirements) { return new int[0]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SizeRequirements.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */