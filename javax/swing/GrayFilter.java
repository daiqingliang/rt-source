package javax.swing;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

public class GrayFilter extends RGBImageFilter {
  private boolean brighter;
  
  private int percent;
  
  public static Image createDisabledImage(Image paramImage) {
    GrayFilter grayFilter = new GrayFilter(true, 50);
    FilteredImageSource filteredImageSource = new FilteredImageSource(paramImage.getSource(), grayFilter);
    return Toolkit.getDefaultToolkit().createImage(filteredImageSource);
  }
  
  public GrayFilter(boolean paramBoolean, int paramInt) {
    this.brighter = paramBoolean;
    this.percent = paramInt;
    this.canFilterIndexColorModel = true;
  }
  
  public int filterRGB(int paramInt1, int paramInt2, int paramInt3) {
    int i = (int)((0.3D * (paramInt3 >> 16 & 0xFF) + 0.59D * (paramInt3 >> 8 & 0xFF) + 0.11D * (paramInt3 & 0xFF)) / 3.0D);
    if (this.brighter) {
      i = 255 - (255 - i) * (100 - this.percent) / 100;
    } else {
      i = i * (100 - this.percent) / 100;
    } 
    if (i < 0)
      i = 0; 
    if (i > 255)
      i = 255; 
    return paramInt3 & 0xFF000000 | i << 16 | i << 8 | i << 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\GrayFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */