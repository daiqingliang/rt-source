package javax.print;

import java.io.OutputStream;

public abstract class StreamPrintService implements PrintService {
  private OutputStream outStream;
  
  private boolean disposed = false;
  
  private StreamPrintService() {}
  
  protected StreamPrintService(OutputStream paramOutputStream) { this.outStream = paramOutputStream; }
  
  public OutputStream getOutputStream() { return this.outStream; }
  
  public abstract String getOutputFormat();
  
  public void dispose() { this.disposed = true; }
  
  public boolean isDisposed() { return this.disposed; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\StreamPrintService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */