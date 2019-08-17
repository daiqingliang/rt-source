package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public class SpanClipRenderer implements CompositePipe {
  CompositePipe outpipe;
  
  static Class RegionClass = Region.class;
  
  static Class RegionIteratorClass = RegionIterator.class;
  
  static native void initIDs(Class paramClass1, Class paramClass2);
  
  public SpanClipRenderer(CompositePipe paramCompositePipe) { this.outpipe = paramCompositePipe; }
  
  public Object startSequence(SunGraphics2D paramSunGraphics2D, Shape paramShape, Rectangle paramRectangle, int[] paramArrayOfInt) {
    RegionIterator regionIterator = paramSunGraphics2D.clipRegion.getIterator();
    return new SCRcontext(regionIterator, this.outpipe.startSequence(paramSunGraphics2D, paramShape, paramRectangle, paramArrayOfInt));
  }
  
  public boolean needTile(Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    SCRcontext sCRcontext = (SCRcontext)paramObject;
    return this.outpipe.needTile(sCRcontext.outcontext, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, ShapeSpanIterator paramShapeSpanIterator) { renderPathTile(paramObject, paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6); }
  
  public void renderPathTile(Object paramObject, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    SCRcontext sCRcontext = (SCRcontext)paramObject;
    RegionIterator regionIterator = sCRcontext.iterator.createCopy();
    int[] arrayOfInt = sCRcontext.band;
    arrayOfInt[0] = paramInt3;
    arrayOfInt[1] = paramInt4;
    arrayOfInt[2] = paramInt3 + paramInt5;
    arrayOfInt[3] = paramInt4 + paramInt6;
    if (paramArrayOfByte == null) {
      int i = paramInt5 * paramInt6;
      paramArrayOfByte = sCRcontext.tile;
      if (paramArrayOfByte != null && paramArrayOfByte.length < i)
        paramArrayOfByte = null; 
      if (paramArrayOfByte == null) {
        paramArrayOfByte = new byte[i];
        sCRcontext.tile = paramArrayOfByte;
      } 
      paramInt1 = 0;
      paramInt2 = paramInt5;
      fillTile(regionIterator, paramArrayOfByte, paramInt1, paramInt2, arrayOfInt);
    } else {
      eraseTile(regionIterator, paramArrayOfByte, paramInt1, paramInt2, arrayOfInt);
    } 
    if (arrayOfInt[2] > arrayOfInt[0] && arrayOfInt[3] > arrayOfInt[1]) {
      paramInt1 += (arrayOfInt[1] - paramInt4) * paramInt2 + arrayOfInt[0] - paramInt3;
      this.outpipe.renderPathTile(sCRcontext.outcontext, paramArrayOfByte, paramInt1, paramInt2, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]);
    } 
  }
  
  public native void fillTile(RegionIterator paramRegionIterator, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public native void eraseTile(RegionIterator paramRegionIterator, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public void skipTile(Object paramObject, int paramInt1, int paramInt2) {
    SCRcontext sCRcontext = (SCRcontext)paramObject;
    this.outpipe.skipTile(sCRcontext.outcontext, paramInt1, paramInt2);
  }
  
  public void endSequence(Object paramObject) {
    SCRcontext sCRcontext = (SCRcontext)paramObject;
    this.outpipe.endSequence(sCRcontext.outcontext);
  }
  
  static  {
    initIDs(RegionClass, RegionIteratorClass);
  }
  
  class SCRcontext {
    RegionIterator iterator;
    
    Object outcontext;
    
    int[] band;
    
    byte[] tile;
    
    public SCRcontext(RegionIterator param1RegionIterator, Object param1Object) {
      this.iterator = param1RegionIterator;
      this.outcontext = param1Object;
      this.band = new int[4];
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\SpanClipRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */