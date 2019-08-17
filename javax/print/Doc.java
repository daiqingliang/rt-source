package javax.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import javax.print.attribute.DocAttributeSet;

public interface Doc {
  DocFlavor getDocFlavor();
  
  Object getPrintData() throws IOException;
  
  DocAttributeSet getAttributes();
  
  Reader getReaderForText() throws IOException;
  
  InputStream getStreamForBytes() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\Doc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */