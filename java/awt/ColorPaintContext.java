package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import sun.awt.image.IntegerComponentRaster;

class ColorPaintContext implements PaintContext {
  int color;
  
  WritableRaster savedTile;
  
  protected ColorPaintContext(int paramInt, ColorModel paramColorModel) { this.color = paramInt; }
  
  public void dispose() {}
  
  int getRGB() { return this.color; }
  
  public ColorModel getColorModel() { return ColorModel.getRGBdefault(); }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    WritableRaster writableRaster = this.savedTile;
    if (writableRaster == null || paramInt3 > writableRaster.getWidth() || paramInt4 > writableRaster.getHeight()) {
      writableRaster = getColorModel().createCompatibleWritableRaster(paramInt3, paramInt4);
      IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)writableRaster;
      Arrays.fill(integerComponentRaster.getDataStorage(), this.color);
      integerComponentRaster.markDirty();
      if (paramInt3 <= 64 && paramInt4 <= 64)
        this.savedTile = writableRaster; 
    } 
    return writableRaster;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\ColorPaintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */