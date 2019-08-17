package javax.swing.text.html.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.text.html.HTMLEditorKit;
import sun.awt.AppContext;

public class ParserDelegator extends HTMLEditorKit.Parser implements Serializable {
  private static final Object DTD_KEY = new Object();
  
  protected static void setDefaultDTD() { getDefaultDTD(); }
  
  private static DTD getDefaultDTD() {
    AppContext appContext = AppContext.getAppContext();
    DTD dTD = (DTD)appContext.get(DTD_KEY);
    if (dTD == null) {
      DTD dTD1 = null;
      String str = "html32";
      try {
        dTD1 = DTD.getDTD(str);
      } catch (IOException iOException) {
        System.out.println("Throw an exception: could not get default dtd: " + str);
      } 
      dTD = createDTD(dTD1, str);
      appContext.put(DTD_KEY, dTD);
    } 
    return dTD;
  }
  
  protected static DTD createDTD(DTD paramDTD, String paramString) {
    InputStream inputStream = null;
    boolean bool = true;
    try {
      String str = paramString + ".bdtd";
      inputStream = getResourceAsStream(str);
      if (inputStream != null) {
        paramDTD.read(new DataInputStream(new BufferedInputStream(inputStream)));
        paramDTD.putDTDHash(paramString, paramDTD);
      } 
    } catch (Exception exception) {
      System.out.println(exception);
    } 
    return paramDTD;
  }
  
  public ParserDelegator() { setDefaultDTD(); }
  
  public void parse(Reader paramReader, HTMLEditorKit.ParserCallback paramParserCallback, boolean paramBoolean) throws IOException { (new DocumentParser(getDefaultDTD())).parse(paramReader, paramParserCallback, paramBoolean); }
  
  static InputStream getResourceAsStream(final String name) { return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
          public InputStream run() { return ParserDelegator.class.getResourceAsStream(name); }
        }); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    setDefaultDTD();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\ParserDelegator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */