package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public class BufferedImageDevice extends GraphicsDevice {
  GraphicsConfiguration gc;
  
  public BufferedImageDevice(BufferedImageGraphicsConfig paramBufferedImageGraphicsConfig) { this.gc = paramBufferedImageGraphicsConfig; }
  
  public int getType() { return 2; }
  
  public String getIDstring() { return "BufferedImage"; }
  
  public GraphicsConfiguration[] getConfigurations() { return new GraphicsConfiguration[] { this.gc }; }
  
  public GraphicsConfiguration getDefaultConfiguration() { return this.gc; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\BufferedImageDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */