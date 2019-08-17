package javax.print.event;

public interface PrintJobListener {
  void printDataTransferCompleted(PrintJobEvent paramPrintJobEvent);
  
  void printJobCompleted(PrintJobEvent paramPrintJobEvent);
  
  void printJobFailed(PrintJobEvent paramPrintJobEvent);
  
  void printJobCanceled(PrintJobEvent paramPrintJobEvent);
  
  void printJobNoMoreEvents(PrintJobEvent paramPrintJobEvent);
  
  void printJobRequiresAttention(PrintJobEvent paramPrintJobEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\event\PrintJobListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */