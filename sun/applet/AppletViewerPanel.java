package sun.applet;

import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

class AppletViewerPanel extends AppletPanel {
  static boolean debug = false;
  
  URL documentURL;
  
  URL baseURL;
  
  Hashtable atts;
  
  private static final long serialVersionUID = 8890989370785545619L;
  
  AppletViewerPanel(URL paramURL, Hashtable paramHashtable) {
    this.documentURL = paramURL;
    this.atts = paramHashtable;
    String str = getParameter("codebase");
    if (str != null) {
      if (!str.endsWith("/"))
        str = str + "/"; 
      try {
        this.baseURL = new URL(paramURL, str);
      } catch (MalformedURLException malformedURLException) {}
    } 
    if (this.baseURL == null) {
      String str1 = paramURL.getFile();
      int i = str1.lastIndexOf('/');
      if (i >= 0 && i < str1.length() - 1)
        try {
          this.baseURL = new URL(paramURL, str1.substring(0, i + 1));
        } catch (MalformedURLException malformedURLException) {} 
    } 
    if (this.baseURL == null)
      this.baseURL = paramURL; 
  }
  
  public String getParameter(String paramString) { return (String)this.atts.get(paramString.toLowerCase()); }
  
  public URL getDocumentBase() { return this.documentURL; }
  
  public URL getCodeBase() { return this.baseURL; }
  
  public int getWidth() {
    String str = getParameter("width");
    return (str != null) ? Integer.valueOf(str).intValue() : 0;
  }
  
  public int getHeight() {
    String str = getParameter("height");
    return (str != null) ? Integer.valueOf(str).intValue() : 0;
  }
  
  public boolean hasInitialFocus() {
    if (isJDK11Applet() || isJDK12Applet())
      return false; 
    String str = getParameter("initial_focus");
    return !(str != null && str.toLowerCase().equals("false"));
  }
  
  public String getCode() { return getParameter("code"); }
  
  public String getJarFiles() { return getParameter("archive"); }
  
  public String getSerializedObject() { return getParameter("object"); }
  
  public AppletContext getAppletContext() { return (AppletContext)getParent(); }
  
  static void debug(String paramString) {
    if (debug)
      System.err.println("AppletViewerPanel:::" + paramString); 
  }
  
  static void debug(String paramString, Throwable paramThrowable) {
    if (debug) {
      paramThrowable.printStackTrace();
      debug(paramString);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletViewerPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */