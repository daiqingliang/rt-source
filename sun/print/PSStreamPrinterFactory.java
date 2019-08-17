package sun.print;

import java.io.OutputStream;
import javax.print.DocFlavor;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;

public class PSStreamPrinterFactory extends StreamPrintServiceFactory {
  static final String psMimeType = "application/postscript";
  
  static final DocFlavor[] supportedDocFlavors = { 
      DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.SERVICE_FORMATTED.PRINTABLE, DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF, DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG, DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, 
      DocFlavor.URL.PNG };
  
  public String getOutputFormat() { return "application/postscript"; }
  
  public DocFlavor[] getSupportedDocFlavors() { return getFlavors(); }
  
  static DocFlavor[] getFlavors() {
    DocFlavor[] arrayOfDocFlavor = new DocFlavor[supportedDocFlavors.length];
    System.arraycopy(supportedDocFlavors, 0, arrayOfDocFlavor, 0, arrayOfDocFlavor.length);
    return arrayOfDocFlavor;
  }
  
  public StreamPrintService getPrintService(OutputStream paramOutputStream) { return new PSStreamPrintService(paramOutputStream); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PSStreamPrinterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */