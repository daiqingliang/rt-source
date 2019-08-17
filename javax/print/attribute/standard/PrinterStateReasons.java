package javax.print.attribute.standard;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;

public final class PrinterStateReasons extends HashMap<PrinterStateReason, Severity> implements PrintServiceAttribute {
  private static final long serialVersionUID = -3731791085163619457L;
  
  public PrinterStateReasons() {}
  
  public PrinterStateReasons(int paramInt) { super(paramInt); }
  
  public PrinterStateReasons(int paramInt, float paramFloat) { super(paramInt, paramFloat); }
  
  public PrinterStateReasons(Map<PrinterStateReason, Severity> paramMap) {
    this();
    for (Map.Entry entry : paramMap.entrySet())
      put((PrinterStateReason)entry.getKey(), (Severity)entry.getValue()); 
  }
  
  public Severity put(PrinterStateReason paramPrinterStateReason, Severity paramSeverity) {
    if (paramPrinterStateReason == null)
      throw new NullPointerException("reason is null"); 
    if (paramSeverity == null)
      throw new NullPointerException("severity is null"); 
    return (Severity)super.put(paramPrinterStateReason, paramSeverity);
  }
  
  public final Class<? extends Attribute> getCategory() { return PrinterStateReasons.class; }
  
  public final String getName() { return "printer-state-reasons"; }
  
  public Set<PrinterStateReason> printerStateReasonSet(Severity paramSeverity) {
    if (paramSeverity == null)
      throw new NullPointerException("severity is null"); 
    return new PrinterStateReasonSet(paramSeverity, entrySet());
  }
  
  private class PrinterStateReasonSet extends AbstractSet<PrinterStateReason> {
    private Severity mySeverity;
    
    private Set myEntrySet;
    
    public PrinterStateReasonSet(Severity param1Severity, Set param1Set) {
      this.mySeverity = param1Severity;
      this.myEntrySet = param1Set;
    }
    
    public int size() {
      byte b = 0;
      Iterator iterator = iterator();
      while (iterator.hasNext()) {
        iterator.next();
        b++;
      } 
      return b;
    }
    
    public Iterator iterator() { return new PrinterStateReasons.PrinterStateReasonSetIterator(PrinterStateReasons.this, this.mySeverity, this.myEntrySet.iterator()); }
  }
  
  private class PrinterStateReasonSetIterator implements Iterator {
    private Severity mySeverity;
    
    private Iterator myIterator;
    
    private Map.Entry myEntry;
    
    public PrinterStateReasonSetIterator(Severity param1Severity, Iterator param1Iterator) {
      this.mySeverity = param1Severity;
      this.myIterator = param1Iterator;
      goToNext();
    }
    
    private void goToNext() {
      this.myEntry = null;
      while (this.myEntry == null && this.myIterator.hasNext()) {
        this.myEntry = (Map.Entry)this.myIterator.next();
        if ((Severity)this.myEntry.getValue() != this.mySeverity)
          this.myEntry = null; 
      } 
    }
    
    public boolean hasNext() { return (this.myEntry != null); }
    
    public Object next() {
      if (this.myEntry == null)
        throw new NoSuchElementException(); 
      Object object = this.myEntry.getKey();
      goToNext();
      return object;
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PrinterStateReasons.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */