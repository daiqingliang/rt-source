package com.sun.xml.internal.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Version {
  public final String BUILD_ID;
  
  public final String BUILD_VERSION;
  
  public final String MAJOR_VERSION;
  
  public final String SVN_REVISION;
  
  public static final Version RUNTIME_VERSION = create(Version.class.getResourceAsStream("version.properties"));
  
  private Version(String paramString1, String paramString2, String paramString3, String paramString4) {
    this.BUILD_ID = fixNull(paramString1);
    this.BUILD_VERSION = fixNull(paramString2);
    this.MAJOR_VERSION = fixNull(paramString3);
    this.SVN_REVISION = fixNull(paramString4);
  }
  
  public static Version create(InputStream paramInputStream) {
    Properties properties = new Properties();
    try {
      properties.load(paramInputStream);
    } catch (IOException iOException) {
    
    } catch (Exception exception) {}
    return new Version(properties.getProperty("build-id"), properties.getProperty("build-version"), properties.getProperty("major-version"), properties.getProperty("svn-revision"));
  }
  
  private String fixNull(String paramString) { return (paramString == null) ? "unknown" : paramString; }
  
  public String toString() { return this.BUILD_VERSION + " svn-revision#" + this.SVN_REVISION; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\Version.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */