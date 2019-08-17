package sun.dc.path;

public interface FastPathProducer {
  void getBox(float[] paramArrayOfFloat) throws PathError;
  
  void sendTo(PathConsumer paramPathConsumer) throws PathError, PathException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\dc\path\FastPathProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */