package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import sun.awt.geom.PathConsumer2D;
import sun.security.action.GetPropertyAction;

public abstract class RenderingEngine {
  private static RenderingEngine reImpl;
  
  public static RenderingEngine getInstance() {
    if (reImpl != null)
      return reImpl; 
    reImpl = (RenderingEngine)AccessController.doPrivileged(new PrivilegedAction<RenderingEngine>() {
          public RenderingEngine run() {
            String str = System.getProperty("sun.java2d.renderer", "sun.dc.DuctusRenderingEngine");
            if (str.equals("sun.dc.DuctusRenderingEngine"))
              try {
                Class clazz = Class.forName("sun.dc.DuctusRenderingEngine");
                return (RenderingEngine)clazz.newInstance();
              } catch (ReflectiveOperationException reflectiveOperationException) {} 
            ServiceLoader serviceLoader = ServiceLoader.loadInstalled(RenderingEngine.class);
            RenderingEngine renderingEngine = null;
            for (RenderingEngine renderingEngine1 : serviceLoader) {
              renderingEngine = renderingEngine1;
              if (renderingEngine1.getClass().getName().equals(str))
                break; 
            } 
            return renderingEngine;
          }
        });
    if (reImpl == null)
      throw new InternalError("No RenderingEngine module found"); 
    GetPropertyAction getPropertyAction = new GetPropertyAction("sun.java2d.renderer.trace");
    String str = (String)AccessController.doPrivileged(getPropertyAction);
    if (str != null)
      reImpl = new Tracer(reImpl); 
    return reImpl;
  }
  
  public abstract Shape createStrokedShape(Shape paramShape, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3);
  
  public abstract void strokeTo(Shape paramShape, AffineTransform paramAffineTransform, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PathConsumer2D paramPathConsumer2D);
  
  public abstract AATileGenerator getAATileGenerator(Shape paramShape, AffineTransform paramAffineTransform, Region paramRegion, BasicStroke paramBasicStroke, boolean paramBoolean1, boolean paramBoolean2, int[] paramArrayOfInt);
  
  public abstract AATileGenerator getAATileGenerator(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Region paramRegion, int[] paramArrayOfInt);
  
  public abstract float getMinimumAAPenSize();
  
  public static void feedConsumer(PathIterator paramPathIterator, PathConsumer2D paramPathConsumer2D) {
    float[] arrayOfFloat = new float[6];
    while (!paramPathIterator.isDone()) {
      switch (paramPathIterator.currentSegment(arrayOfFloat)) {
        case 0:
          paramPathConsumer2D.moveTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 1:
          paramPathConsumer2D.lineTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 2:
          paramPathConsumer2D.quadTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
          break;
        case 3:
          paramPathConsumer2D.curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
          break;
        case 4:
          paramPathConsumer2D.closePath();
          break;
      } 
      paramPathIterator.next();
    } 
  }
  
  static class Tracer extends RenderingEngine {
    RenderingEngine target;
    
    String name;
    
    public Tracer(RenderingEngine param1RenderingEngine) {
      this.target = param1RenderingEngine;
      this.name = param1RenderingEngine.getClass().getName();
    }
    
    public Shape createStrokedShape(Shape param1Shape, float param1Float1, int param1Int1, int param1Int2, float param1Float2, float[] param1ArrayOfFloat, float param1Float3) {
      System.out.println(this.name + ".createStrokedShape(" + param1Shape.getClass().getName() + ", width = " + param1Float1 + ", caps = " + param1Int1 + ", join = " + param1Int2 + ", miter = " + param1Float2 + ", dashes = " + param1ArrayOfFloat + ", dashphase = " + param1Float3 + ")");
      return this.target.createStrokedShape(param1Shape, param1Float1, param1Int1, param1Int2, param1Float2, param1ArrayOfFloat, param1Float3);
    }
    
    public void strokeTo(Shape param1Shape, AffineTransform param1AffineTransform, BasicStroke param1BasicStroke, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3, PathConsumer2D param1PathConsumer2D) {
      System.out.println(this.name + ".strokeTo(" + param1Shape.getClass().getName() + ", " + param1AffineTransform + ", " + param1BasicStroke + ", " + (param1Boolean1 ? "thin" : "wide") + ", " + (param1Boolean2 ? "normalized" : "pure") + ", " + (param1Boolean3 ? "AA" : "non-AA") + ", " + param1PathConsumer2D.getClass().getName() + ")");
      this.target.strokeTo(param1Shape, param1AffineTransform, param1BasicStroke, param1Boolean1, param1Boolean2, param1Boolean3, param1PathConsumer2D);
    }
    
    public float getMinimumAAPenSize() {
      System.out.println(this.name + ".getMinimumAAPenSize()");
      return this.target.getMinimumAAPenSize();
    }
    
    public AATileGenerator getAATileGenerator(Shape param1Shape, AffineTransform param1AffineTransform, Region param1Region, BasicStroke param1BasicStroke, boolean param1Boolean1, boolean param1Boolean2, int[] param1ArrayOfInt) {
      System.out.println(this.name + ".getAATileGenerator(" + param1Shape.getClass().getName() + ", " + param1AffineTransform + ", " + param1Region + ", " + param1BasicStroke + ", " + (param1Boolean1 ? "thin" : "wide") + ", " + (param1Boolean2 ? "normalized" : "pure") + ")");
      return this.target.getAATileGenerator(param1Shape, param1AffineTransform, param1Region, param1BasicStroke, param1Boolean1, param1Boolean2, param1ArrayOfInt);
    }
    
    public AATileGenerator getAATileGenerator(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8, Region param1Region, int[] param1ArrayOfInt) {
      System.out.println(this.name + ".getAATileGenerator(" + param1Double1 + ", " + param1Double2 + ", " + param1Double3 + ", " + param1Double4 + ", " + param1Double5 + ", " + param1Double6 + ", " + param1Double7 + ", " + param1Double8 + ", " + param1Region + ")");
      return this.target.getAATileGenerator(param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6, param1Double7, param1Double8, param1Region, param1ArrayOfInt);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\RenderingEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */