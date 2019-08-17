package sun.java2d.pipe.hw;

import java.awt.BufferCapabilities;
import java.awt.ImageCapabilities;

public class ExtendedBufferCapabilities extends BufferCapabilities {
  private VSyncType vsync = VSyncType.VSYNC_DEFAULT;
  
  public ExtendedBufferCapabilities(BufferCapabilities paramBufferCapabilities) { super(paramBufferCapabilities.getFrontBufferCapabilities(), paramBufferCapabilities.getBackBufferCapabilities(), paramBufferCapabilities.getFlipContents()); }
  
  public ExtendedBufferCapabilities(ImageCapabilities paramImageCapabilities1, ImageCapabilities paramImageCapabilities2, BufferCapabilities.FlipContents paramFlipContents) { super(paramImageCapabilities1, paramImageCapabilities2, paramFlipContents); }
  
  public ExtendedBufferCapabilities(ImageCapabilities paramImageCapabilities1, ImageCapabilities paramImageCapabilities2, BufferCapabilities.FlipContents paramFlipContents, VSyncType paramVSyncType) { super(paramImageCapabilities1, paramImageCapabilities2, paramFlipContents); }
  
  public ExtendedBufferCapabilities(BufferCapabilities paramBufferCapabilities, VSyncType paramVSyncType) { super(paramBufferCapabilities.getFrontBufferCapabilities(), paramBufferCapabilities.getBackBufferCapabilities(), paramBufferCapabilities.getFlipContents()); }
  
  public ExtendedBufferCapabilities derive(VSyncType paramVSyncType) { return new ExtendedBufferCapabilities(this, paramVSyncType); }
  
  public VSyncType getVSync() { return this.vsync; }
  
  public final boolean isPageFlipping() { return true; }
  
  public enum VSyncType {
    VSYNC_DEFAULT(0),
    VSYNC_ON(1),
    VSYNC_OFF(2);
    
    private int id;
    
    public int id() { return this.id; }
    
    VSyncType(int param1Int1) { this.id = param1Int1; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\hw\ExtendedBufferCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */