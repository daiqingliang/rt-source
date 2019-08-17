package sun.awt.windows;

import java.awt.datatransfer.DataFlavor;

static enum EHTMLReadMode {
  HTML_READ_ALL, HTML_READ_FRAGMENT, HTML_READ_SELECTION;
  
  public static EHTMLReadMode getEHTMLReadMode(DataFlavor paramDataFlavor) {
    EHTMLReadMode eHTMLReadMode = HTML_READ_SELECTION;
    String str = paramDataFlavor.getParameter("document");
    if ("all".equals(str)) {
      eHTMLReadMode = HTML_READ_ALL;
    } else if ("fragment".equals(str)) {
      eHTMLReadMode = HTML_READ_FRAGMENT;
    } 
    return eHTMLReadMode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\EHTMLReadMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */