package sun.awt.image;

import java.awt.Image;
import java.util.List;

public interface MultiResolutionImage {
  Image getResolutionVariant(int paramInt1, int paramInt2);
  
  List<Image> getResolutionVariants();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\MultiResolutionImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */