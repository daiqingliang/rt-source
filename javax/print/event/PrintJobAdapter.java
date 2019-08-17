package javax.print.event;

public abstract class PrintJobAdapter implements PrintJobListener {
  public void printDataTransferCompleted(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobCompleted(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobFailed(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobCanceled(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobNoMoreEvents(PrintJobEvent paramPrintJobEvent) {}
  
  public void printJobRequiresAttention(PrintJobEvent paramPrintJobEvent) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\event\PrintJobAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */