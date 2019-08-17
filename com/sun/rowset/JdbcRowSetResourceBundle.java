package com.sun.rowset;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class JdbcRowSetResourceBundle implements Serializable {
  private static String fileName;
  
  private PropertyResourceBundle propResBundle;
  
  private static final String PROPERTIES = "properties";
  
  private static final String UNDERSCORE = "_";
  
  private static final String DOT = ".";
  
  private static final String SLASH = "/";
  
  private static final String PATH = "com/sun/rowset/RowSetResourceBundle";
  
  static final long serialVersionUID = 436199386225359954L;
  
  private JdbcRowSetResourceBundle() throws IOException {
    Locale locale = Locale.getDefault();
    this.propResBundle = (PropertyResourceBundle)ResourceBundle.getBundle("com/sun/rowset/RowSetResourceBundle", locale, Thread.currentThread().getContextClassLoader());
  }
  
  public static JdbcRowSetResourceBundle getJdbcRowSetResourceBundle() throws IOException {
    if (jpResBundle == null)
      synchronized (JdbcRowSetResourceBundle.class) {
        if (jpResBundle == null)
          jpResBundle = new JdbcRowSetResourceBundle(); 
      }  
    return jpResBundle;
  }
  
  public Enumeration getKeys() { return this.propResBundle.getKeys(); }
  
  public Object handleGetObject(String paramString) { return this.propResBundle.handleGetObject(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\JdbcRowSetResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */