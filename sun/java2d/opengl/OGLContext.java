package sun.java2d.opengl;

import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.ContextCapabilities;

public class OGLContext extends BufferedContext {
  private final OGLGraphicsConfig config;
  
  OGLContext(RenderQueue paramRenderQueue, OGLGraphicsConfig paramOGLGraphicsConfig) {
    super(paramRenderQueue);
    this.config = paramOGLGraphicsConfig;
  }
  
  static void setScratchSurface(OGLGraphicsConfig paramOGLGraphicsConfig) { setScratchSurface(paramOGLGraphicsConfig.getNativeConfigInfo()); }
  
  static void setScratchSurface(long paramLong) {
    currentContext = null;
    OGLRenderQueue oGLRenderQueue = OGLRenderQueue.getInstance();
    RenderBuffer renderBuffer = oGLRenderQueue.getBuffer();
    oGLRenderQueue.ensureCapacityAndAlignment(12, 4);
    renderBuffer.putInt(71);
    renderBuffer.putLong(paramLong);
  }
  
  static void invalidateCurrentContext() {
    if (currentContext != null) {
      currentContext.invalidateContext();
      currentContext = null;
    } 
    OGLRenderQueue oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.ensureCapacity(4);
    oGLRenderQueue.getBuffer().putInt(75);
    oGLRenderQueue.flushNow();
  }
  
  public RenderQueue getRenderQueue() { return OGLRenderQueue.getInstance(); }
  
  static final native String getOGLIdString();
  
  public void saveState() {
    invalidateContext();
    invalidateCurrentContext();
    setScratchSurface(this.config);
    this.rq.ensureCapacity(4);
    this.buf.putInt(78);
    this.rq.flushNow();
  }
  
  public void restoreState() {
    invalidateContext();
    invalidateCurrentContext();
    setScratchSurface(this.config);
    this.rq.ensureCapacity(4);
    this.buf.putInt(79);
    this.rq.flushNow();
  }
  
  static class OGLContextCaps extends ContextCapabilities {
    static final int CAPS_EXT_FBOBJECT = 12;
    
    static final int CAPS_STORED_ALPHA = 2;
    
    static final int CAPS_DOUBLEBUFFERED = 65536;
    
    static final int CAPS_EXT_LCD_SHADER = 131072;
    
    static final int CAPS_EXT_BIOP_SHADER = 262144;
    
    static final int CAPS_EXT_GRAD_SHADER = 524288;
    
    static final int CAPS_EXT_TEXRECT = 1048576;
    
    static final int CAPS_EXT_TEXBARRIER = 2097152;
    
    OGLContextCaps(int param1Int, String param1String) { super(param1Int, param1String); }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer(super.toString());
      if ((this.caps & 0xC) != 0)
        stringBuffer.append("CAPS_EXT_FBOBJECT|"); 
      if ((this.caps & 0x2) != 0)
        stringBuffer.append("CAPS_STORED_ALPHA|"); 
      if ((this.caps & 0x10000) != 0)
        stringBuffer.append("CAPS_DOUBLEBUFFERED|"); 
      if ((this.caps & 0x20000) != 0)
        stringBuffer.append("CAPS_EXT_LCD_SHADER|"); 
      if ((this.caps & 0x40000) != 0)
        stringBuffer.append("CAPS_BIOP_SHADER|"); 
      if ((this.caps & 0x80000) != 0)
        stringBuffer.append("CAPS_EXT_GRAD_SHADER|"); 
      if ((this.caps & 0x100000) != 0)
        stringBuffer.append("CAPS_EXT_TEXRECT|"); 
      if ((this.caps & 0x200000) != 0)
        stringBuffer.append("CAPS_EXT_TEXBARRIER|"); 
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */