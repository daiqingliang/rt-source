package java.awt;

import java.awt.peer.DesktopPeer;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import sun.awt.AppContext;
import sun.awt.DesktopBrowse;
import sun.awt.SunToolkit;

public class Desktop {
  private DesktopPeer peer = Toolkit.getDefaultToolkit().createDesktopPeer(this);
  
  public static Desktop getDesktop() {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    if (!isDesktopSupported())
      throw new UnsupportedOperationException("Desktop API is not supported on the current platform"); 
    AppContext appContext = AppContext.getAppContext();
    Desktop desktop = (Desktop)appContext.get(Desktop.class);
    if (desktop == null) {
      desktop = new Desktop();
      appContext.put(Desktop.class, desktop);
    } 
    return desktop;
  }
  
  public static boolean isDesktopSupported() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return (toolkit instanceof SunToolkit) ? ((SunToolkit)toolkit).isDesktopSupported() : 0;
  }
  
  public boolean isSupported(Action paramAction) { return this.peer.isSupported(paramAction); }
  
  private static void checkFileValidation(File paramFile) {
    if (!paramFile.exists())
      throw new IllegalArgumentException("The file: " + paramFile.getPath() + " doesn't exist."); 
  }
  
  private void checkActionSupport(Action paramAction) {
    if (!isSupported(paramAction))
      throw new UnsupportedOperationException("The " + paramAction.name() + " action is not supported on the current platform!"); 
  }
  
  private void checkAWTPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AWTPermission("showWindowWithoutWarningBanner")); 
  }
  
  public void open(File paramFile) {
    paramFile = new File(paramFile.getPath());
    checkAWTPermission();
    checkExec();
    checkActionSupport(Action.OPEN);
    checkFileValidation(paramFile);
    this.peer.open(paramFile);
  }
  
  public void edit(File paramFile) {
    paramFile = new File(paramFile.getPath());
    checkAWTPermission();
    checkExec();
    checkActionSupport(Action.EDIT);
    paramFile.canWrite();
    checkFileValidation(paramFile);
    this.peer.edit(paramFile);
  }
  
  public void print(File paramFile) {
    paramFile = new File(paramFile.getPath());
    checkExec();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPrintJobAccess(); 
    checkActionSupport(Action.PRINT);
    checkFileValidation(paramFile);
    this.peer.print(paramFile);
  }
  
  public void browse(URI paramURI) throws IOException {
    SecurityException securityException = null;
    try {
      checkAWTPermission();
      checkExec();
    } catch (SecurityException securityException1) {
      securityException = securityException1;
    } 
    checkActionSupport(Action.BROWSE);
    if (paramURI == null)
      throw new NullPointerException(); 
    if (securityException == null) {
      this.peer.browse(paramURI);
      return;
    } 
    URL uRL = null;
    try {
      uRL = paramURI.toURL();
    } catch (MalformedURLException malformedURLException) {
      throw new IllegalArgumentException("Unable to convert URI to URL", malformedURLException);
    } 
    DesktopBrowse desktopBrowse = DesktopBrowse.getInstance();
    if (desktopBrowse == null)
      throw securityException; 
    desktopBrowse.browse(uRL);
  }
  
  public void mail() {
    checkAWTPermission();
    checkExec();
    checkActionSupport(Action.MAIL);
    URI uRI = null;
    try {
      uRI = new URI("mailto:?");
      this.peer.mail(uRI);
    } catch (URISyntaxException uRISyntaxException) {}
  }
  
  public void mail(URI paramURI) throws IOException {
    checkAWTPermission();
    checkExec();
    checkActionSupport(Action.MAIL);
    if (paramURI == null)
      throw new NullPointerException(); 
    if (!"mailto".equalsIgnoreCase(paramURI.getScheme()))
      throw new IllegalArgumentException("URI scheme is not \"mailto\""); 
    this.peer.mail(paramURI);
  }
  
  private void checkExec() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new FilePermission("<<ALL FILES>>", "execute")); 
  }
  
  public enum Action {
    OPEN, EDIT, PRINT, MAIL, BROWSE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Desktop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */