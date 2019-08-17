package javax.rmi.CORBA;

import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

class GetORBPropertiesFileAction implements PrivilegedAction {
  private boolean debug = false;
  
  private String getSystemProperty(final String name) { return (String)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperty(name); }
        }); }
  
  private void getPropertiesFromFile(Properties paramProperties, String paramString) {
    try {
      File file = new File(paramString);
      if (!file.exists())
        return; 
      fileInputStream = new FileInputStream(file);
      try {
        paramProperties.load(fileInputStream);
      } finally {
        fileInputStream.close();
      } 
    } catch (Exception exception) {
      if (this.debug)
        System.out.println("ORB properties file " + paramString + " not found: " + exception); 
    } 
  }
  
  public Object run() {
    Properties properties1 = new Properties();
    String str1 = getSystemProperty("java.home");
    String str2 = str1 + File.separator + "lib" + File.separator + "orb.properties";
    getPropertiesFromFile(properties1, str2);
    Properties properties2 = new Properties(properties1);
    String str3 = getSystemProperty("user.home");
    str2 = str3 + File.separator + "orb.properties";
    getPropertiesFromFile(properties2, str2);
    return properties2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\CORBA\GetORBPropertiesFileAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */