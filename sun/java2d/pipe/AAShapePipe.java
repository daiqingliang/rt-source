package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import sun.java2d.SunGraphics2D;

public class AAShapePipe implements ShapeDrawPipe, ParallelogramPipe {
  static RenderingEngine renderengine = RenderingEngine.getInstance();
  
  CompositePipe outpipe;
  
  private static byte[] theTile;
  
  public AAShapePipe(CompositePipe paramCompositePipe) { this.outpipe = paramCompositePipe; }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    BasicStroke basicStroke;
    if (paramSunGraphics2D.stroke instanceof BasicStroke) {
      basicStroke = (BasicStroke)paramSunGraphics2D.stroke;
    } else {
      paramShape = paramSunGraphics2D.stroke.createStrokedShape(paramShape);
      basicStroke = null;
    } 
    renderPath(paramSunGraphics2D, paramShape, basicStroke);
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) { renderPath(paramSunGraphics2D, paramShape, null); }
  
  private static Rectangle2D computeBBox(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (paramDouble3 -= paramDouble1 < 0.0D) {
      paramDouble1 += paramDouble3;
      paramDouble3 = -paramDouble3;
    } 
    if (paramDouble4 -= paramDouble2 < 0.0D) {
      paramDouble2 += paramDouble4;
      paramDouble4 = -paramDouble4;
    } 
    return new Rectangle2D.Double(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
  }
  
  public void fillParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10) {
    Region region = paramSunGraphics2D.getCompClip();
    int[] arrayOfInt = new int[4];
    AATileGenerator aATileGenerator = renderengine.getAATileGenerator(paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, 0.0D, 0.0D, region, arrayOfInt);
    if (aATileGenerator == null)
      return; 
    renderTiles(paramSunGraphics2D, computeBBox(paramDouble1, paramDouble2, paramDouble3, paramDouble4), aATileGenerator, arrayOfInt);
  }
  
  public void drawParallelogram(SunGraphics2D paramSunGraphics2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12) {
    Region region = paramSunGraphics2D.getCompClip();
    int[] arrayOfInt = new int[4];
    AATileGenerator aATileGenerator = renderengine.getAATileGenerator(paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12, region, arrayOfInt);
    if (aATileGenerator == null)
      return; 
    renderTiles(paramSunGraphics2D, computeBBox(paramDouble1, paramDouble2, paramDouble3, paramDouble4), aATileGenerator, arrayOfInt);
  }
  
  private static byte[] getAlphaTile(int paramInt) {
    byte[] arrayOfByte = theTile;
    if (arrayOfByte == null || arrayOfByte.length < paramInt) {
      arrayOfByte = new byte[paramInt];
    } else {
      theTile = null;
    } 
    return arrayOfByte;
  }
  
  private static void dropAlphaTile(byte[] paramArrayOfByte) { theTile = paramArrayOfByte; }
  
  public void renderPath(SunGraphics2D paramSunGraphics2D, Shape paramShape, BasicStroke paramBasicStroke) {
    boolean bool1 = (paramBasicStroke != null && paramSunGraphics2D.strokeHint != 2);
    boolean bool2 = (paramSunGraphics2D.strokeState <= 1);
    Region region = paramSunGraphics2D.getCompClip();
    int[] arrayOfInt = new int[4];
    AATileGenerator aATileGenerator = renderengine.getAATileGenerator(paramShape, paramSunGraphics2D.transform, region, paramBasicStroke, bool2, bool1, arrayOfInt);
    if (aATileGenerator == null)
      return; 
    renderTiles(paramSunGraphics2D, paramShape, aATileGenerator, arrayOfInt);
  }
  
  public void renderTiles(SunGraphics2D paramSunGraphics2D, Shape paramShape, AATileGenerator paramAATileGenerator, int[] paramArrayOfInt) {
    object = null;
    arrayOfByte = null;
    try {
      object = this.outpipe.startSequence(paramSunGraphics2D, paramShape, new Rectangle(paramArrayOfInt[0], paramArrayOfInt[1], paramArrayOfInt[2] - paramArrayOfInt[0], paramArrayOfInt[3] - paramArrayOfInt[1]), paramArrayOfInt);
      int i = paramAATileGenerator.getTileWidth();
      int j = paramAATileGenerator.getTileHeight();
      arrayOfByte = getAlphaTile(i * j);
      int k;
      for (k = paramArrayOfInt[1]; k < paramArrayOfInt[3]; k += j) {
        int m;
        for (m = paramArrayOfInt[0]; m < paramArrayOfInt[2]; m += i) {
          int n = Math.min(i, paramArrayOfInt[2] - m);
          int i1 = Math.min(j, paramArrayOfInt[3] - k);
          int i2 = paramAATileGenerator.getTypicalAlpha();
          if (i2 == 0 || !this.outpipe.needTile(object, m, k, n, i1)) {
            paramAATileGenerator.nextTile();
            this.outpipe.skipTile(object, m, k);
          } else {
            byte[] arrayOfByte1;
            if (i2 == 255) {
              arrayOfByte1 = null;
              paramAATileGenerator.nextTile();
            } else {
              arrayOfByte1 = arrayOfByte;
              paramAATileGenerator.getAlpha(arrayOfByte, 0, i);
            } 
            this.outpipe.renderPathTile(object, arrayOfByte1, 0, i, m, k, n, i1);
          } 
        } 
      } 
    } finally {
      paramAATileGenerator.dispose();
      if (object != null)
        this.outpipe.endSequence(object); 
      if (arrayOfByte != null)
        dropAlphaTile(arrayOfByte); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\AAShapePipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */