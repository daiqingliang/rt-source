package java.awt.image;

import java.awt.Point;

public interface WritableRenderedImage extends RenderedImage {
  void addTileObserver(TileObserver paramTileObserver);
  
  void removeTileObserver(TileObserver paramTileObserver);
  
  WritableRaster getWritableTile(int paramInt1, int paramInt2);
  
  void releaseWritableTile(int paramInt1, int paramInt2);
  
  boolean isTileWritable(int paramInt1, int paramInt2);
  
  Point[] getWritableTileIndices();
  
  boolean hasTileWriters();
  
  void setData(Raster paramRaster);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\WritableRenderedImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */