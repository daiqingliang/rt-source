package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.XORComposite;

public class SunCompositeContext implements CompositeContext {
  ColorModel srcCM;
  
  ColorModel dstCM;
  
  Composite composite;
  
  CompositeType comptype;
  
  public SunCompositeContext(AlphaComposite paramAlphaComposite, ColorModel paramColorModel1, ColorModel paramColorModel2) {
    if (paramColorModel1 == null)
      throw new NullPointerException("Source color model cannot be null"); 
    if (paramColorModel2 == null)
      throw new NullPointerException("Destination color model cannot be null"); 
    this.srcCM = paramColorModel1;
    this.dstCM = paramColorModel2;
    this.composite = paramAlphaComposite;
    this.comptype = CompositeType.forAlphaComposite(paramAlphaComposite);
  }
  
  public SunCompositeContext(XORComposite paramXORComposite, ColorModel paramColorModel1, ColorModel paramColorModel2) {
    if (paramColorModel1 == null)
      throw new NullPointerException("Source color model cannot be null"); 
    if (paramColorModel2 == null)
      throw new NullPointerException("Destination color model cannot be null"); 
    this.srcCM = paramColorModel1;
    this.dstCM = paramColorModel2;
    this.composite = paramXORComposite;
    this.comptype = CompositeType.Xor;
  }
  
  public void dispose() {}
  
  public void compose(Raster paramRaster1, Raster paramRaster2, WritableRaster paramWritableRaster) {
    WritableRaster writableRaster;
    if (paramRaster2 != paramWritableRaster)
      paramWritableRaster.setDataElements(0, 0, paramRaster2); 
    if (paramRaster1 instanceof WritableRaster) {
      writableRaster = (WritableRaster)paramRaster1;
    } else {
      writableRaster = paramRaster1.createCompatibleWritableRaster();
      writableRaster.setDataElements(0, 0, paramRaster1);
    } 
    int i = Math.min(writableRaster.getWidth(), paramRaster2.getWidth());
    int j = Math.min(writableRaster.getHeight(), paramRaster2.getHeight());
    BufferedImage bufferedImage1 = new BufferedImage(this.srcCM, writableRaster, this.srcCM.isAlphaPremultiplied(), null);
    BufferedImage bufferedImage2 = new BufferedImage(this.dstCM, paramWritableRaster, this.dstCM.isAlphaPremultiplied(), null);
    SurfaceData surfaceData1 = BufImgSurfaceData.createData(bufferedImage1);
    SurfaceData surfaceData2 = BufImgSurfaceData.createData(bufferedImage2);
    Blit blit = Blit.getFromCache(surfaceData1.getSurfaceType(), this.comptype, surfaceData2.getSurfaceType());
    blit.Blit(surfaceData1, surfaceData2, this.composite, null, 0, 0, 0, 0, i, j);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\SunCompositeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */