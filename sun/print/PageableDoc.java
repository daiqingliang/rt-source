package sun.print;

import java.awt.print.Pageable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;

public class PageableDoc implements Doc {
  private Pageable pageable;
  
  public PageableDoc(Pageable paramPageable) { this.pageable = paramPageable; }
  
  public DocFlavor getDocFlavor() { return DocFlavor.SERVICE_FORMATTED.PAGEABLE; }
  
  public DocAttributeSet getAttributes() { return new HashDocAttributeSet(); }
  
  public Object getPrintData() throws IOException { return this.pageable; }
  
  public Reader getReaderForText() throws UnsupportedEncodingException, IOException { return null; }
  
  public InputStream getStreamForBytes() throws IOException { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PageableDoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */