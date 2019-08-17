package sun.java2d.d3d;

import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.ContextCapabilities;

class D3DContext extends BufferedContext {
  private final D3DGraphicsDevice device;
  
  D3DContext(RenderQueue paramRenderQueue, D3DGraphicsDevice paramD3DGraphicsDevice) {
    super(paramRenderQueue);
    this.device = paramD3DGraphicsDevice;
  }
  
  static void invalidateCurrentContext() {
    if (currentContext != null) {
      currentContext.invalidateContext();
      currentContext = null;
    } 
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.ensureCapacity(4);
    d3DRenderQueue.getBuffer().putInt(75);
    d3DRenderQueue.flushNow();
  }
  
  static void setScratchSurface(D3DContext paramD3DContext) {
    if (paramD3DContext != currentContext)
      currentContext = null; 
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    RenderBuffer renderBuffer = d3DRenderQueue.getBuffer();
    d3DRenderQueue.ensureCapacity(8);
    renderBuffer.putInt(71);
    renderBuffer.putInt(paramD3DContext.getDevice().getScreen());
  }
  
  public RenderQueue getRenderQueue() { return D3DRenderQueue.getInstance(); }
  
  public void saveState() {
    invalidateContext();
    invalidateCurrentContext();
    setScratchSurface(this);
    this.rq.ensureCapacity(4);
    this.buf.putInt(78);
    this.rq.flushNow();
  }
  
  public void restoreState() {
    invalidateContext();
    invalidateCurrentContext();
    setScratchSurface(this);
    this.rq.ensureCapacity(4);
    this.buf.putInt(79);
    this.rq.flushNow();
  }
  
  D3DGraphicsDevice getDevice() { return this.device; }
  
  static class D3DContextCaps extends ContextCapabilities {
    static final int CAPS_LCD_SHADER = 65536;
    
    static final int CAPS_BIOP_SHADER = 131072;
    
    static final int CAPS_DEVICE_OK = 262144;
    
    static final int CAPS_AA_SHADER = 524288;
    
    D3DContextCaps(int param1Int, String param1String) { super(param1Int, param1String); }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer(super.toString());
      if ((this.caps & 0x10000) != 0)
        stringBuffer.append("CAPS_LCD_SHADER|"); 
      if ((this.caps & 0x20000) != 0)
        stringBuffer.append("CAPS_BIOP_SHADER|"); 
      if ((this.caps & 0x80000) != 0)
        stringBuffer.append("CAPS_AA_SHADER|"); 
      if ((this.caps & 0x40000) != 0)
        stringBuffer.append("CAPS_DEVICE_OK|"); 
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */