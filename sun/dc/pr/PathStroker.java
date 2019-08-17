package sun.dc.pr;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.geom.PathConsumer2D;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathError;
import sun.dc.path.PathException;

public class PathStroker implements PathConsumer {
  public static final int ROUND = 10;
  
  public static final int SQUARE = 20;
  
  public static final int BUTT = 30;
  
  public static final int BEVEL = 40;
  
  public static final int MITER = 50;
  
  private PathConsumer dest;
  
  private PathConsumer2D dest2D;
  
  private long cData;
  
  public PathStroker(PathConsumer paramPathConsumer) {
    if (paramPathConsumer == null)
      throw new InternalError("null dest for path"); 
    this.dest = paramPathConsumer;
    cInitialize(paramPathConsumer);
    reset();
  }
  
  public PathStroker(PathConsumer2D paramPathConsumer2D) {
    if (paramPathConsumer2D == null)
      throw new InternalError("null dest for path"); 
    this.dest2D = paramPathConsumer2D;
    cInitialize2D(paramPathConsumer2D);
    reset();
  }
  
  public native void dispose();
  
  protected static void classFinalize() { cClassFinalize(); }
  
  public PathConsumer getConsumer() { return this.dest; }
  
  public native void setPenDiameter(float paramFloat) throws PRError;
  
  public native void setPenT4(float[] paramArrayOfFloat) throws PRError;
  
  public native void setPenFitting(float paramFloat, int paramInt) throws PRError;
  
  public native void setCaps(int paramInt) throws PRError;
  
  public native void setCorners(int paramInt, float paramFloat) throws PRError;
  
  public native void setOutputT6(float[] paramArrayOfFloat) throws PRError;
  
  public native void setOutputConsumer(PathConsumer paramPathConsumer);
  
  public native void reset();
  
  public native void beginPath();
  
  public native void beginSubpath(float paramFloat1, float paramFloat2) throws PathError;
  
  public native void appendLine(float paramFloat1, float paramFloat2) throws PathError;
  
  public native void appendQuadratic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) throws PathError;
  
  public native void appendCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) throws PathError;
  
  public native void closedSubpath();
  
  public native void endPath();
  
  public void useProxy(FastPathProducer paramFastPathProducer) throws PathError, PathException { paramFastPathProducer.sendTo(this); }
  
  public native long getCPathConsumer();
  
  private static native void cClassInitialize();
  
  private static native void cClassFinalize();
  
  private native void cInitialize(PathConsumer paramPathConsumer);
  
  private native void cInitialize2D(PathConsumer2D paramPathConsumer2D);
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("dcpr");
            return null;
          }
        });
    cClassInitialize();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\dc\pr\PathStroker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */