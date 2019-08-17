package javax.swing.plaf.nimbus;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import sun.awt.AppContext;

abstract class Effect {
  abstract EffectType getEffectType();
  
  abstract float getOpacity();
  
  abstract BufferedImage applyEffect(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2, int paramInt1, int paramInt2);
  
  protected static ArrayCache getArrayCache() {
    ArrayCache arrayCache = (ArrayCache)AppContext.getAppContext().get(ArrayCache.class);
    if (arrayCache == null) {
      arrayCache = new ArrayCache();
      AppContext.getAppContext().put(ArrayCache.class, arrayCache);
    } 
    return arrayCache;
  }
  
  protected static class ArrayCache {
    private SoftReference<int[]> tmpIntArray = null;
    
    private SoftReference<byte[]> tmpByteArray1 = null;
    
    private SoftReference<byte[]> tmpByteArray2 = null;
    
    private SoftReference<byte[]> tmpByteArray3 = null;
    
    protected int[] getTmpIntArray(int param1Int) {
      int[] arrayOfInt;
      if (this.tmpIntArray == null || (arrayOfInt = (int[])this.tmpIntArray.get()) == null || arrayOfInt.length < param1Int) {
        arrayOfInt = new int[param1Int];
        this.tmpIntArray = new SoftReference(arrayOfInt);
      } 
      return arrayOfInt;
    }
    
    protected byte[] getTmpByteArray1(int param1Int) {
      byte[] arrayOfByte;
      if (this.tmpByteArray1 == null || (arrayOfByte = (byte[])this.tmpByteArray1.get()) == null || arrayOfByte.length < param1Int) {
        arrayOfByte = new byte[param1Int];
        this.tmpByteArray1 = new SoftReference(arrayOfByte);
      } 
      return arrayOfByte;
    }
    
    protected byte[] getTmpByteArray2(int param1Int) {
      byte[] arrayOfByte;
      if (this.tmpByteArray2 == null || (arrayOfByte = (byte[])this.tmpByteArray2.get()) == null || arrayOfByte.length < param1Int) {
        arrayOfByte = new byte[param1Int];
        this.tmpByteArray2 = new SoftReference(arrayOfByte);
      } 
      return arrayOfByte;
    }
    
    protected byte[] getTmpByteArray3(int param1Int) {
      byte[] arrayOfByte;
      if (this.tmpByteArray3 == null || (arrayOfByte = (byte[])this.tmpByteArray3.get()) == null || arrayOfByte.length < param1Int) {
        arrayOfByte = new byte[param1Int];
        this.tmpByteArray3 = new SoftReference(arrayOfByte);
      } 
      return arrayOfByte;
    }
  }
  
  enum EffectType {
    UNDER, BLENDED, OVER;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\Effect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */