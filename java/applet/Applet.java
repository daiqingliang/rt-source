package java.applet;

import java.awt.AWTPermission;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Panel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.applet.AppletAudioClip;

public class Applet extends Panel {
  private AppletStub stub;
  
  private static final long serialVersionUID = -5836846270535785031L;
  
  AccessibleContext accessibleContext = null;
  
  public Applet() throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    paramObjectInputStream.defaultReadObject();
  }
  
  public final void setStub(AppletStub paramAppletStub) {
    if (this.stub != null) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(new AWTPermission("setAppletStub")); 
    } 
    this.stub = paramAppletStub;
  }
  
  public boolean isActive() { return (this.stub != null) ? this.stub.isActive() : 0; }
  
  public URL getDocumentBase() { return this.stub.getDocumentBase(); }
  
  public URL getCodeBase() { return this.stub.getCodeBase(); }
  
  public String getParameter(String paramString) { return this.stub.getParameter(paramString); }
  
  public AppletContext getAppletContext() { return this.stub.getAppletContext(); }
  
  public void resize(int paramInt1, int paramInt2) {
    Dimension dimension = size();
    if (dimension.width != paramInt1 || dimension.height != paramInt2) {
      super.resize(paramInt1, paramInt2);
      if (this.stub != null)
        this.stub.appletResize(paramInt1, paramInt2); 
    } 
  }
  
  public void resize(Dimension paramDimension) { resize(paramDimension.width, paramDimension.height); }
  
  public boolean isValidateRoot() { return true; }
  
  public void showStatus(String paramString) { getAppletContext().showStatus(paramString); }
  
  public Image getImage(URL paramURL) { return getAppletContext().getImage(paramURL); }
  
  public Image getImage(URL paramURL, String paramString) {
    try {
      return getImage(new URL(paramURL, paramString));
    } catch (MalformedURLException malformedURLException) {
      return null;
    } 
  }
  
  public static final AudioClip newAudioClip(URL paramURL) { return new AppletAudioClip(paramURL); }
  
  public AudioClip getAudioClip(URL paramURL) { return getAppletContext().getAudioClip(paramURL); }
  
  public AudioClip getAudioClip(URL paramURL, String paramString) {
    try {
      return getAudioClip(new URL(paramURL, paramString));
    } catch (MalformedURLException malformedURLException) {
      return null;
    } 
  }
  
  public String getAppletInfo() { return null; }
  
  public Locale getLocale() {
    Locale locale = super.getLocale();
    return (locale == null) ? Locale.getDefault() : locale;
  }
  
  public String[][] getParameterInfo() { return (String[][])null; }
  
  public void play(URL paramURL) {
    AudioClip audioClip = getAudioClip(paramURL);
    if (audioClip != null)
      audioClip.play(); 
  }
  
  public void play(URL paramURL, String paramString) {
    AudioClip audioClip = getAudioClip(paramURL, paramString);
    if (audioClip != null)
      audioClip.play(); 
  }
  
  public void init() throws HeadlessException {}
  
  public void start() throws HeadlessException {}
  
  public void stop() throws HeadlessException {}
  
  public void destroy() throws HeadlessException {}
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleApplet(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleApplet extends Panel.AccessibleAWTPanel {
    private static final long serialVersionUID = 8127374778187708896L;
    
    protected AccessibleApplet() { super(Applet.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.FRAME; }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      accessibleStateSet.add(AccessibleState.ACTIVE);
      return accessibleStateSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\applet\Applet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */