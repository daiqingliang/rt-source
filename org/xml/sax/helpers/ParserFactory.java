package org.xml.sax.helpers;

import org.xml.sax.Parser;

public class ParserFactory {
  private static SecuritySupport ss = new SecuritySupport();
  
  public static Parser makeParser() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NullPointerException, ClassCastException {
    String str = ss.getSystemProperty("org.xml.sax.parser");
    if (str == null)
      throw new NullPointerException("No value for sax.parser property"); 
    return makeParser(str);
  }
  
  public static Parser makeParser(String paramString) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ClassCastException { return (Parser)NewInstance.newInstance(ss.getContextClassLoader(), paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\xml\sax\helpers\ParserFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */