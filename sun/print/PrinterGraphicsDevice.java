package sun.print;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Window;

public final class PrinterGraphicsDevice extends GraphicsDevice {
  String printerID;
  
  GraphicsConfiguration graphicsConf;
  
  protected PrinterGraphicsDevice(GraphicsConfiguration paramGraphicsConfiguration, String paramString) {
    this.printerID = paramString;
    this.graphicsConf = paramGraphicsConfiguration;
  }
  
  public int getType() { return 1; }
  
  public String getIDstring() { return this.printerID; }
  
  public GraphicsConfiguration[] getConfigurations() {
    GraphicsConfiguration[] arrayOfGraphicsConfiguration = new GraphicsConfiguration[1];
    arrayOfGraphicsConfiguration[0] = this.graphicsConf;
    return arrayOfGraphicsConfiguration;
  }
  
  public GraphicsConfiguration getDefaultConfiguration() { return this.graphicsConf; }
  
  public void setFullScreenWindow(Window paramWindow) {}
  
  public Window getFullScreenWindow() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PrinterGraphicsDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */