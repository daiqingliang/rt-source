package sun.swing;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.plaf.UIResource;

public class ImageIconUIResource extends ImageIcon implements UIResource {
  public ImageIconUIResource(byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public ImageIconUIResource(Image paramImage) { super(paramImage); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\ImageIconUIResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */