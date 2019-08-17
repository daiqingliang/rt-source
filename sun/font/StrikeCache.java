package sun.font;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import sun.java2d.Disposer;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import sun.misc.Unsafe;

public final class StrikeCache {
  static final Unsafe unsafe = Unsafe.getUnsafe();
  
  static ReferenceQueue refQueue = Disposer.getQueue();
  
  static ArrayList<GlyphDisposedListener> disposeListeners = new ArrayList(1);
  
  static int MINSTRIKES = 8;
  
  static int recentStrikeIndex = 0;
  
  static FontStrike[] recentStrikes;
  
  static boolean cacheRefTypeWeak;
  
  static int nativeAddressSize;
  
  static int glyphInfoSize;
  
  static int xAdvanceOffset;
  
  static int yAdvanceOffset;
  
  static int boundsOffset;
  
  static int widthOffset;
  
  static int heightOffset;
  
  static int rowBytesOffset;
  
  static int topLeftXOffset;
  
  static int topLeftYOffset;
  
  static int pixelDataOffset;
  
  static int cacheCellOffset;
  
  static int managedOffset;
  
  static long invisibleGlyphPtr;
  
  static native void getGlyphCacheDescription(long[] paramArrayOfLong);
  
  static void refStrike(FontStrike paramFontStrike) {
    int i = recentStrikeIndex;
    recentStrikes[i] = paramFontStrike;
    if (++i == MINSTRIKES)
      i = 0; 
    recentStrikeIndex = i;
  }
  
  private static final void doDispose(FontStrikeDisposer paramFontStrikeDisposer) {
    if (paramFontStrikeDisposer.intGlyphImages != null) {
      freeCachedIntMemory(paramFontStrikeDisposer.intGlyphImages, paramFontStrikeDisposer.pScalerContext);
    } else if (paramFontStrikeDisposer.longGlyphImages != null) {
      freeCachedLongMemory(paramFontStrikeDisposer.longGlyphImages, paramFontStrikeDisposer.pScalerContext);
    } else if (paramFontStrikeDisposer.segIntGlyphImages != null) {
      for (byte b = 0; b < paramFontStrikeDisposer.segIntGlyphImages.length; b++) {
        if (paramFontStrikeDisposer.segIntGlyphImages[b] != null) {
          freeCachedIntMemory(paramFontStrikeDisposer.segIntGlyphImages[b], paramFontStrikeDisposer.pScalerContext);
          paramFontStrikeDisposer.pScalerContext = 0L;
          paramFontStrikeDisposer.segIntGlyphImages[b] = null;
        } 
      } 
      if (paramFontStrikeDisposer.pScalerContext != 0L)
        freeCachedIntMemory(new int[0], paramFontStrikeDisposer.pScalerContext); 
    } else if (paramFontStrikeDisposer.segLongGlyphImages != null) {
      for (byte b = 0; b < paramFontStrikeDisposer.segLongGlyphImages.length; b++) {
        if (paramFontStrikeDisposer.segLongGlyphImages[b] != null) {
          freeCachedLongMemory(paramFontStrikeDisposer.segLongGlyphImages[b], paramFontStrikeDisposer.pScalerContext);
          paramFontStrikeDisposer.pScalerContext = 0L;
          paramFontStrikeDisposer.segLongGlyphImages[b] = null;
        } 
      } 
      if (paramFontStrikeDisposer.pScalerContext != 0L)
        freeCachedLongMemory(new long[0], paramFontStrikeDisposer.pScalerContext); 
    } else if (paramFontStrikeDisposer.pScalerContext != 0L) {
      if (longAddresses()) {
        freeCachedLongMemory(new long[0], paramFontStrikeDisposer.pScalerContext);
      } else {
        freeCachedIntMemory(new int[0], paramFontStrikeDisposer.pScalerContext);
      } 
    } 
  }
  
  private static boolean longAddresses() { return (nativeAddressSize == 8); }
  
  static void disposeStrike(final FontStrikeDisposer disposer) {
    if (Disposer.pollingQueue) {
      doDispose(paramFontStrikeDisposer);
      return;
    } 
    renderQueue = null;
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (!graphicsEnvironment.isHeadless()) {
      GraphicsConfiguration graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
      if (graphicsConfiguration instanceof AccelGraphicsConfig) {
        AccelGraphicsConfig accelGraphicsConfig = (AccelGraphicsConfig)graphicsConfiguration;
        BufferedContext bufferedContext = accelGraphicsConfig.getContext();
        if (bufferedContext != null)
          renderQueue = bufferedContext.getRenderQueue(); 
      } 
    } 
    if (renderQueue != null) {
      renderQueue.lock();
      try {
        renderQueue.flushAndInvokeNow(new Runnable() {
              public void run() {
                StrikeCache.doDispose(disposer);
                Disposer.pollRemove();
              }
            });
      } finally {
        renderQueue.unlock();
      } 
    } else {
      doDispose(paramFontStrikeDisposer);
    } 
  }
  
  static native void freeIntPointer(int paramInt);
  
  static native void freeLongPointer(long paramLong);
  
  private static native void freeIntMemory(int[] paramArrayOfInt, long paramLong);
  
  private static native void freeLongMemory(long[] paramArrayOfLong, long paramLong);
  
  private static void freeCachedIntMemory(int[] paramArrayOfInt, long paramLong) {
    synchronized (disposeListeners) {
      if (disposeListeners.size() > 0) {
        ArrayList arrayList = null;
        for (byte b = 0; b < paramArrayOfInt.length; b++) {
          if (paramArrayOfInt[b] != 0 && unsafe.getByte((paramArrayOfInt[b] + managedOffset)) == 0) {
            if (arrayList == null)
              arrayList = new ArrayList(); 
            arrayList.add(Long.valueOf(paramArrayOfInt[b]));
          } 
        } 
        if (arrayList != null)
          notifyDisposeListeners(arrayList); 
      } 
    } 
    freeIntMemory(paramArrayOfInt, paramLong);
  }
  
  private static void freeCachedLongMemory(long[] paramArrayOfLong, long paramLong) {
    synchronized (disposeListeners) {
      if (disposeListeners.size() > 0) {
        ArrayList arrayList = null;
        for (byte b = 0; b < paramArrayOfLong.length; b++) {
          if (paramArrayOfLong[b] != 0L && unsafe.getByte(paramArrayOfLong[b] + managedOffset) == 0) {
            if (arrayList == null)
              arrayList = new ArrayList(); 
            arrayList.add(Long.valueOf(paramArrayOfLong[b]));
          } 
        } 
        if (arrayList != null)
          notifyDisposeListeners(arrayList); 
      } 
    } 
    freeLongMemory(paramArrayOfLong, paramLong);
  }
  
  public static void addGlyphDisposedListener(GlyphDisposedListener paramGlyphDisposedListener) {
    synchronized (disposeListeners) {
      disposeListeners.add(paramGlyphDisposedListener);
    } 
  }
  
  private static void notifyDisposeListeners(ArrayList<Long> paramArrayList) {
    for (GlyphDisposedListener glyphDisposedListener : disposeListeners)
      glyphDisposedListener.glyphDisposed(paramArrayList); 
  }
  
  public static Reference getStrikeRef(FontStrike paramFontStrike) { return getStrikeRef(paramFontStrike, cacheRefTypeWeak); }
  
  public static Reference getStrikeRef(FontStrike paramFontStrike, boolean paramBoolean) { return (paramFontStrike.disposer == null) ? (paramBoolean ? new WeakReference(paramFontStrike) : new SoftReference(paramFontStrike)) : (paramBoolean ? new WeakDisposerRef(paramFontStrike) : new SoftDisposerRef(paramFontStrike)); }
  
  static  {
    long[] arrayOfLong = new long[13];
    getGlyphCacheDescription(arrayOfLong);
    nativeAddressSize = (int)arrayOfLong[0];
    glyphInfoSize = (int)arrayOfLong[1];
    xAdvanceOffset = (int)arrayOfLong[2];
    yAdvanceOffset = (int)arrayOfLong[3];
    widthOffset = (int)arrayOfLong[4];
    heightOffset = (int)arrayOfLong[5];
    rowBytesOffset = (int)arrayOfLong[6];
    topLeftXOffset = (int)arrayOfLong[7];
    topLeftYOffset = (int)arrayOfLong[8];
    pixelDataOffset = (int)arrayOfLong[9];
    invisibleGlyphPtr = arrayOfLong[10];
    cacheCellOffset = (int)arrayOfLong[11];
    managedOffset = (int)arrayOfLong[12];
    if (nativeAddressSize < 4)
      throw new InternalError("Unexpected address size for font data: " + nativeAddressSize); 
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            String str1 = System.getProperty("sun.java2d.font.reftype", "soft");
            StrikeCache.cacheRefTypeWeak = str1.equals("weak");
            String str2 = System.getProperty("sun.java2d.font.minstrikes");
            if (str2 != null)
              try {
                StrikeCache.MINSTRIKES = Integer.parseInt(str2);
                if (StrikeCache.MINSTRIKES <= 0)
                  StrikeCache.MINSTRIKES = 1; 
              } catch (NumberFormatException numberFormatException) {} 
            StrikeCache.recentStrikes = new FontStrike[StrikeCache.MINSTRIKES];
            return null;
          }
        });
  }
  
  static interface DisposableStrike {
    FontStrikeDisposer getDisposer();
  }
  
  static class SoftDisposerRef extends SoftReference implements DisposableStrike {
    private FontStrikeDisposer disposer;
    
    public FontStrikeDisposer getDisposer() { return this.disposer; }
    
    SoftDisposerRef(FontStrike param1FontStrike) {
      super(param1FontStrike, StrikeCache.refQueue);
      this.disposer = param1FontStrike.disposer;
      Disposer.addReference(this, this.disposer);
    }
  }
  
  static class WeakDisposerRef extends WeakReference implements DisposableStrike {
    private FontStrikeDisposer disposer;
    
    public FontStrikeDisposer getDisposer() { return this.disposer; }
    
    WeakDisposerRef(FontStrike param1FontStrike) {
      super(param1FontStrike, StrikeCache.refQueue);
      this.disposer = param1FontStrike.disposer;
      Disposer.addReference(this, this.disposer);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\StrikeCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */