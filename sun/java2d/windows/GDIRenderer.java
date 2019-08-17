package sun.java2d.windows;

import java.awt.Composite;
import java.awt.Shape;
import java.awt.geom.Path2D;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.SpanIterator;

public class GDIRenderer implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe {
  native void doDrawLine(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void drawLine(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramSunGraphics2D.transX;
    int j = paramSunGraphics2D.transY;
    try {
      doDrawLine((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + i, paramInt2 + j, paramInt3 + i, paramInt4 + j);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doDrawRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void drawRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      doDrawRect((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doDrawRoundRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public void drawRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      doDrawRoundRect((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doDrawOval(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void drawOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      doDrawOval((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doDrawArc(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public void drawArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      doDrawArc((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doDrawPoly(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt4, boolean paramBoolean);
  
  public void drawPolyline(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    try {
      doDrawPoly((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramSunGraphics2D.transX, paramSunGraphics2D.transY, paramArrayOfInt1, paramArrayOfInt2, paramInt, false);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  public void drawPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    try {
      doDrawPoly((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramSunGraphics2D.transX, paramSunGraphics2D.transY, paramArrayOfInt1, paramArrayOfInt2, paramInt, true);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doFillRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void fillRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      doFillRect((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doFillRoundRect(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public void fillRoundRect(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      doFillRoundRect((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doFillOval(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  public void fillOval(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    try {
      doFillOval((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doFillArc(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public void fillArc(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    try {
      doFillArc((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramInt1 + paramSunGraphics2D.transX, paramInt2 + paramSunGraphics2D.transY, paramInt3, paramInt4, paramInt5, paramInt6);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doFillPoly(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt4);
  
  public void fillPolygon(SunGraphics2D paramSunGraphics2D, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    try {
      doFillPoly((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, paramSunGraphics2D.transX, paramSunGraphics2D.transY, paramArrayOfInt1, paramArrayOfInt2, paramInt);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  native void doShape(GDIWindowSurfaceData paramGDIWindowSurfaceData, Region paramRegion, Composite paramComposite, int paramInt1, int paramInt2, int paramInt3, Path2D.Float paramFloat, boolean paramBoolean);
  
  void doShape(SunGraphics2D paramSunGraphics2D, Shape paramShape, boolean paramBoolean) {
    byte b2;
    byte b1;
    Path2D.Float float;
    paramSunGraphics2D;
    if (paramSunGraphics2D.transformState <= 1) {
      if (paramShape instanceof Path2D.Float) {
        float = (Path2D.Float)paramShape;
      } else {
        float = new Path2D.Float(paramShape);
      } 
      b1 = paramSunGraphics2D.transX;
      b2 = paramSunGraphics2D.transY;
    } else {
      float = new Path2D.Float(paramShape, paramSunGraphics2D.transform);
      b1 = 0;
      b2 = 0;
    } 
    try {
      doShape((GDIWindowSurfaceData)paramSunGraphics2D.surfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, paramSunGraphics2D.eargb, b1, b2, float, paramBoolean);
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
  }
  
  public void doFillSpans(SunGraphics2D paramSunGraphics2D, SpanIterator paramSpanIterator) {
    GDIWindowSurfaceData gDIWindowSurfaceData;
    int[] arrayOfInt = new int[4];
    try {
      gDIWindowSurfaceData = (GDIWindowSurfaceData)paramSunGraphics2D.surfaceData;
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
    Region region = paramSunGraphics2D.getCompClip();
    Composite composite = paramSunGraphics2D.composite;
    int i = paramSunGraphics2D.eargb;
    while (paramSpanIterator.nextSpan(arrayOfInt))
      doFillRect(gDIWindowSurfaceData, region, composite, i, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2] - arrayOfInt[0], arrayOfInt[3] - arrayOfInt[1]); 
  }
  
  public void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape) {
    paramSunGraphics2D;
    if (paramSunGraphics2D.strokeState == 0) {
      doShape(paramSunGraphics2D, paramShape, false);
    } else {
      paramSunGraphics2D;
      if (paramSunGraphics2D.strokeState < 3) {
        shapeSpanIterator = LoopPipe.getStrokeSpans(paramSunGraphics2D, paramShape);
        try {
          doFillSpans(paramSunGraphics2D, shapeSpanIterator);
        } finally {
          shapeSpanIterator.dispose();
        } 
      } else {
        doShape(paramSunGraphics2D, paramSunGraphics2D.stroke.createStrokedShape(paramShape), true);
      } 
    } 
  }
  
  public void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape) { doShape(paramSunGraphics2D, paramShape, true); }
  
  public native void devCopyArea(GDIWindowSurfaceData paramGDIWindowSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public GDIRenderer traceWrap() { return new Tracer(); }
  
  public static class Tracer extends GDIRenderer {
    void doDrawLine(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      GraphicsPrimitive.tracePrimitive("GDIDrawLine");
      super.doDrawLine(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5);
    }
    
    void doDrawRect(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      GraphicsPrimitive.tracePrimitive("GDIDrawRect");
      super.doDrawRect(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5);
    }
    
    void doDrawRoundRect(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7) {
      GraphicsPrimitive.tracePrimitive("GDIDrawRoundRect");
      super.doDrawRoundRect(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1Int7);
    }
    
    void doDrawOval(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      GraphicsPrimitive.tracePrimitive("GDIDrawOval");
      super.doDrawOval(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5);
    }
    
    void doDrawArc(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7) {
      GraphicsPrimitive.tracePrimitive("GDIDrawArc");
      super.doDrawArc(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1Int7);
    }
    
    void doDrawPoly(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, int param1Int4, boolean param1Boolean) {
      GraphicsPrimitive.tracePrimitive("GDIDrawPoly");
      super.doDrawPoly(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1ArrayOfInt1, param1ArrayOfInt2, param1Int4, param1Boolean);
    }
    
    void doFillRect(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      GraphicsPrimitive.tracePrimitive("GDIFillRect");
      super.doFillRect(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5);
    }
    
    void doFillRoundRect(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7) {
      GraphicsPrimitive.tracePrimitive("GDIFillRoundRect");
      super.doFillRoundRect(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1Int7);
    }
    
    void doFillOval(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      GraphicsPrimitive.tracePrimitive("GDIFillOval");
      super.doFillOval(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5);
    }
    
    void doFillArc(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7) {
      GraphicsPrimitive.tracePrimitive("GDIFillArc");
      super.doFillArc(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1Int7);
    }
    
    void doFillPoly(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, int param1Int4) {
      GraphicsPrimitive.tracePrimitive("GDIFillPoly");
      super.doFillPoly(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1ArrayOfInt1, param1ArrayOfInt2, param1Int4);
    }
    
    void doShape(GDIWindowSurfaceData param1GDIWindowSurfaceData, Region param1Region, Composite param1Composite, int param1Int1, int param1Int2, int param1Int3, Path2D.Float param1Float, boolean param1Boolean) {
      GraphicsPrimitive.tracePrimitive(param1Boolean ? "GDIFillShape" : "GDIDrawShape");
      super.doShape(param1GDIWindowSurfaceData, param1Region, param1Composite, param1Int1, param1Int2, param1Int3, param1Float, param1Boolean);
    }
    
    public void devCopyArea(GDIWindowSurfaceData param1GDIWindowSurfaceData, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
      GraphicsPrimitive.tracePrimitive("GDICopyArea");
      super.devCopyArea(param1GDIWindowSurfaceData, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\windows\GDIRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */