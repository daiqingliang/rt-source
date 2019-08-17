package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.security.AccessController;
import sun.awt.DisplayChangedListener;
import sun.awt.image.SurfaceManager;
import sun.java2d.loops.Blit;
import sun.java2d.loops.BlitBg;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.security.action.GetPropertyAction;

public abstract class SurfaceDataProxy implements DisplayChangedListener, SurfaceManager.FlushableCacheData {
  private static boolean cachingAllowed = true;
  
  private static int defaultThreshold;
  
  public static SurfaceDataProxy UNCACHED;
  
  private int threshold;
  
  private StateTracker srcTracker;
  
  private int numtries;
  
  private SurfaceData cachedSD;
  
  private StateTracker cacheTracker;
  
  private boolean valid;
  
  public static boolean isCachingAllowed() { return cachingAllowed; }
  
  public abstract boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor);
  
  public abstract SurfaceData validateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2);
  
  public StateTracker getRetryTracker(SurfaceData paramSurfaceData) { return new CountdownTracker(this.threshold); }
  
  public SurfaceDataProxy() { this(defaultThreshold); }
  
  public SurfaceDataProxy(int paramInt) {
    this.threshold = paramInt;
    this.srcTracker = StateTracker.NEVER_CURRENT;
    this.cacheTracker = StateTracker.NEVER_CURRENT;
    this.valid = true;
  }
  
  public boolean isValid() { return this.valid; }
  
  public void invalidate() { this.valid = false; }
  
  public boolean flush(boolean paramBoolean) {
    if (paramBoolean)
      invalidate(); 
    flush();
    return !isValid();
  }
  
  public void flush() {
    SurfaceData surfaceData = this.cachedSD;
    this.cachedSD = null;
    this.cacheTracker = StateTracker.NEVER_CURRENT;
    if (surfaceData != null)
      surfaceData.flush(); 
  }
  
  public boolean isAccelerated() { return (isValid() && this.srcTracker.isCurrent() && this.cacheTracker.isCurrent()); }
  
  protected void activateDisplayListener() {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (graphicsEnvironment instanceof SunGraphicsEnvironment)
      ((SunGraphicsEnvironment)graphicsEnvironment).addDisplayChangedListener(this); 
  }
  
  public void displayChanged() { flush(); }
  
  public void paletteChanged() { this.srcTracker = StateTracker.NEVER_CURRENT; }
  
  public SurfaceData replaceData(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor) {
    if (isSupportedOperation(paramSurfaceData, paramInt, paramCompositeType, paramColor)) {
      if (!this.srcTracker.isCurrent()) {
        synchronized (this) {
          this.numtries = this.threshold;
          this.srcTracker = paramSurfaceData.getStateTracker();
          this.cacheTracker = StateTracker.NEVER_CURRENT;
        } 
        if (!this.srcTracker.isCurrent()) {
          if (paramSurfaceData.getState() == StateTrackable.State.UNTRACKABLE) {
            invalidate();
            flush();
          } 
          return paramSurfaceData;
        } 
      } 
      SurfaceData surfaceData = this.cachedSD;
      if (!this.cacheTracker.isCurrent()) {
        synchronized (this) {
          if (this.numtries > 0) {
            this.numtries--;
            return paramSurfaceData;
          } 
        } 
        Rectangle rectangle = paramSurfaceData.getBounds();
        int i = rectangle.width;
        int j = rectangle.height;
        StateTracker stateTracker = this.srcTracker;
        surfaceData = validateSurfaceData(paramSurfaceData, surfaceData, i, j);
        if (surfaceData == null) {
          synchronized (this) {
            if (stateTracker == this.srcTracker) {
              this.cacheTracker = getRetryTracker(paramSurfaceData);
              this.cachedSD = null;
            } 
          } 
          return paramSurfaceData;
        } 
        updateSurfaceData(paramSurfaceData, surfaceData, i, j);
        if (!surfaceData.isValid())
          return paramSurfaceData; 
        synchronized (this) {
          if (stateTracker == this.srcTracker && stateTracker.isCurrent()) {
            this.cacheTracker = surfaceData.getStateTracker();
            this.cachedSD = surfaceData;
          } 
        } 
      } 
      if (surfaceData != null)
        return surfaceData; 
    } 
    return paramSurfaceData;
  }
  
  public void updateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2) {
    SurfaceType surfaceType1 = paramSurfaceData1.getSurfaceType();
    SurfaceType surfaceType2 = paramSurfaceData2.getSurfaceType();
    Blit blit = Blit.getFromCache(surfaceType1, CompositeType.SrcNoEa, surfaceType2);
    blit.Blit(paramSurfaceData1, paramSurfaceData2, AlphaComposite.Src, null, 0, 0, 0, 0, paramInt1, paramInt2);
    paramSurfaceData2.markDirty();
  }
  
  public void updateSurfaceDataBg(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2, Color paramColor) {
    SurfaceType surfaceType1 = paramSurfaceData1.getSurfaceType();
    SurfaceType surfaceType2 = paramSurfaceData2.getSurfaceType();
    BlitBg blitBg = BlitBg.getFromCache(surfaceType1, CompositeType.SrcNoEa, surfaceType2);
    blitBg.BlitBg(paramSurfaceData1, paramSurfaceData2, AlphaComposite.Src, null, paramColor.getRGB(), 0, 0, 0, 0, paramInt1, paramInt2);
    paramSurfaceData2.markDirty();
  }
  
  static  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.managedimages"));
    if (str1 != null && str1.equals("false")) {
      cachingAllowed = false;
      System.out.println("Disabling managed images");
    } 
    defaultThreshold = 1;
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.accthreshold"));
    if (str2 != null)
      try {
        int i = Integer.parseInt(str2);
        if (i >= 0) {
          defaultThreshold = i;
          System.out.println("New Default Acceleration Threshold: " + defaultThreshold);
        } 
      } catch (NumberFormatException numberFormatException) {
        System.err.println("Error setting new threshold:" + numberFormatException);
      }  
    UNCACHED = new SurfaceDataProxy(0) {
        public boolean isAccelerated() { return false; }
        
        public boolean isSupportedOperation(SurfaceData param1SurfaceData, int param1Int, CompositeType param1CompositeType, Color param1Color) { return false; }
        
        public SurfaceData validateSurfaceData(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, int param1Int1, int param1Int2) { throw new InternalError("UNCACHED should never validate SDs"); }
        
        public SurfaceData replaceData(SurfaceData param1SurfaceData, int param1Int, CompositeType param1CompositeType, Color param1Color) { return param1SurfaceData; }
      };
  }
  
  public static class CountdownTracker implements StateTracker {
    private int countdown;
    
    public CountdownTracker(int param1Int) { this.countdown = param1Int; }
    
    public boolean isCurrent() { return (--this.countdown >= 0); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\SurfaceDataProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */