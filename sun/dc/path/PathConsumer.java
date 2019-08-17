package sun.dc.path;

public interface PathConsumer {
  void beginPath() throws PathError;
  
  void beginSubpath(float paramFloat1, float paramFloat2) throws PathError;
  
  void appendLine(float paramFloat1, float paramFloat2) throws PathError;
  
  void appendQuadratic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) throws PathError;
  
  void appendCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) throws PathError;
  
  void closedSubpath() throws PathError;
  
  void endPath() throws PathError;
  
  void useProxy(FastPathProducer paramFastPathProducer) throws PathError, PathException;
  
  long getCPathConsumer();
  
  void dispose() throws PathError;
  
  PathConsumer getConsumer();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\dc\path\PathConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */