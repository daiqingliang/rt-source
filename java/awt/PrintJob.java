package java.awt;

public abstract class PrintJob {
  public abstract Graphics getGraphics();
  
  public abstract Dimension getPageDimension();
  
  public abstract int getPageResolution();
  
  public abstract boolean lastPageFirst();
  
  public abstract void end();
  
  public void finalize() { end(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\PrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */