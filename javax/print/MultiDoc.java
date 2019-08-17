package javax.print;

import java.io.IOException;

public interface MultiDoc {
  Doc getDoc() throws IOException;
  
  MultiDoc next() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\MultiDoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */