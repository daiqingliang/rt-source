package sun.swing.text;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class CompoundPrintable implements CountingPrintable {
  private final Queue<CountingPrintable> printables;
  
  private int offset = 0;
  
  public CompoundPrintable(List<CountingPrintable> paramList) { this.printables = new LinkedList(paramList); }
  
  public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt) throws PrinterException {
    int i = 1;
    while (this.printables.peek() != null) {
      i = ((CountingPrintable)this.printables.peek()).print(paramGraphics, paramPageFormat, paramInt - this.offset);
      if (i == 0)
        break; 
      this.offset += ((CountingPrintable)this.printables.poll()).getNumberOfPages();
    } 
    return i;
  }
  
  public int getNumberOfPages() { return this.offset; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\text\CompoundPrintable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */