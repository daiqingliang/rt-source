package sun.awt.geom;

public interface PathConsumer2D {
  void moveTo(float paramFloat1, float paramFloat2);
  
  void lineTo(float paramFloat1, float paramFloat2);
  
  void quadTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  void curveTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);
  
  void closePath();
  
  void pathDone();
  
  long getNativeConsumer();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\PathConsumer2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */