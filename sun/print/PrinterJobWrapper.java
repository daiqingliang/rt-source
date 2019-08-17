package sun.print;

import java.awt.print.PrinterJob;
import javax.print.attribute.PrintRequestAttribute;

public class PrinterJobWrapper implements PrintRequestAttribute {
  private static final long serialVersionUID = -8792124426995707237L;
  
  private PrinterJob job;
  
  public PrinterJobWrapper(PrinterJob paramPrinterJob) { this.job = paramPrinterJob; }
  
  public PrinterJob getPrinterJob() { return this.job; }
  
  public final Class getCategory() { return PrinterJobWrapper.class; }
  
  public final String getName() { return "printerjob-wrapper"; }
  
  public String toString() { return "printerjob-wrapper: " + this.job.toString(); }
  
  public int hashCode() { return this.job.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PrinterJobWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */