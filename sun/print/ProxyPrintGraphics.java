package sun.print;

import java.awt.Graphics;
import java.awt.PrintGraphics;
import java.awt.PrintJob;

public class ProxyPrintGraphics extends ProxyGraphics implements PrintGraphics {
  private PrintJob printJob;
  
  public ProxyPrintGraphics(Graphics paramGraphics, PrintJob paramPrintJob) {
    super(paramGraphics);
    this.printJob = paramPrintJob;
  }
  
  public PrintJob getPrintJob() { return this.printJob; }
  
  public Graphics create() { return new ProxyPrintGraphics(getGraphics().create(), this.printJob); }
  
  public Graphics create(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Graphics graphics = getGraphics().create(paramInt1, paramInt2, paramInt3, paramInt4);
    return new ProxyPrintGraphics(graphics, this.printJob);
  }
  
  public Graphics getGraphics() { return super.getGraphics(); }
  
  public void dispose() { super.dispose(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\ProxyPrintGraphics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */