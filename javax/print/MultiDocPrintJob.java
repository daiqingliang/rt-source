package javax.print;

import javax.print.attribute.PrintRequestAttributeSet;

public interface MultiDocPrintJob extends DocPrintJob {
  void print(MultiDoc paramMultiDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrintException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\MultiDocPrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */