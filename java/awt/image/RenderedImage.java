package java.awt.image;

import java.awt.Rectangle;
import java.util.Vector;

public interface RenderedImage {
  Vector<RenderedImage> getSources();
  
  Object getProperty(String paramString);
  
  String[] getPropertyNames();
  
  ColorModel getColorModel();
  
  SampleModel getSampleModel();
  
  int getWidth();
  
  int getHeight();
  
  int getMinX();
  
  int getMinY();
  
  int getNumXTiles();
  
  int getNumYTiles();
  
  int getMinTileX();
  
  int getMinTileY();
  
  int getTileWidth();
  
  int getTileHeight();
  
  int getTileGridXOffset();
  
  int getTileGridYOffset();
  
  Raster getTile(int paramInt1, int paramInt2);
  
  Raster getData();
  
  Raster getData(Rectangle paramRectangle);
  
  WritableRaster copyData(WritableRaster paramWritableRaster);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\RenderedImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */