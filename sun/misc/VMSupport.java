package sun.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class VMSupport {
  private static Properties agentProps = null;
  
  public static Properties getAgentProperties() {
    if (agentProps == null) {
      agentProps = new Properties();
      initAgentProperties(agentProps);
    } 
    return agentProps;
  }
  
  private static native Properties initAgentProperties(Properties paramProperties);
  
  private static byte[] serializePropertiesToByteArray(Properties paramProperties) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096);
    Properties properties = new Properties();
    Set set = paramProperties.stringPropertyNames();
    for (String str1 : set) {
      String str2 = paramProperties.getProperty(str1);
      properties.put(str1, str2);
    } 
    properties.store(byteArrayOutputStream, null);
    return byteArrayOutputStream.toByteArray();
  }
  
  public static byte[] serializePropertiesToByteArray() throws IOException { return serializePropertiesToByteArray(System.getProperties()); }
  
  public static byte[] serializeAgentPropertiesToByteArray() throws IOException { return serializePropertiesToByteArray(getAgentProperties()); }
  
  public static boolean isClassPathAttributePresent(String paramString) {
    try {
      Manifest manifest = (new JarFile(paramString)).getManifest();
      return (manifest != null && manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH) != null);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException.getMessage());
    } 
  }
  
  public static native String getVMTemporaryDirectory();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\VMSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */