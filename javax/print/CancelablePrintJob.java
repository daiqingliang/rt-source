package javax.print;

public interface CancelablePrintJob extends DocPrintJob {
  void cancel() throws PrintException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\CancelablePrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */