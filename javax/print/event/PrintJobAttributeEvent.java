package javax.print.event;

import javax.print.DocPrintJob;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.PrintJobAttributeSet;

public class PrintJobAttributeEvent extends PrintEvent {
  private static final long serialVersionUID = -6534469883874742101L;
  
  private PrintJobAttributeSet attributes;
  
  public PrintJobAttributeEvent(DocPrintJob paramDocPrintJob, PrintJobAttributeSet paramPrintJobAttributeSet) {
    super(paramDocPrintJob);
    this.attributes = AttributeSetUtilities.unmodifiableView(paramPrintJobAttributeSet);
  }
  
  public DocPrintJob getPrintJob() { return (DocPrintJob)getSource(); }
  
  public PrintJobAttributeSet getAttributes() { return this.attributes; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\event\PrintJobAttributeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */