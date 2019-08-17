package java.beans;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;

class BeansAppletStub implements AppletStub {
  boolean active;
  
  Applet target;
  
  AppletContext context;
  
  URL codeBase;
  
  URL docBase;
  
  BeansAppletStub(Applet paramApplet, AppletContext paramAppletContext, URL paramURL1, URL paramURL2) {
    this.target = paramApplet;
    this.context = paramAppletContext;
    this.codeBase = paramURL1;
    this.docBase = paramURL2;
  }
  
  public boolean isActive() { return this.active; }
  
  public URL getDocumentBase() { return this.docBase; }
  
  public URL getCodeBase() { return this.codeBase; }
  
  public String getParameter(String paramString) { return null; }
  
  public AppletContext getAppletContext() { return this.context; }
  
  public void appletResize(int paramInt1, int paramInt2) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\BeansAppletStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */