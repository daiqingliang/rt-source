package sun.print;

import javax.print.DocFlavor;
import javax.print.FlavorException;
import javax.print.PrintException;

class PrintJobFlavorException extends PrintException implements FlavorException {
  private DocFlavor flavor;
  
  PrintJobFlavorException(String paramString, DocFlavor paramDocFlavor) {
    super(paramString);
    this.flavor = paramDocFlavor;
  }
  
  public DocFlavor[] getUnsupportedFlavors() { return new DocFlavor[] { this.flavor }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PrintJobFlavorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */