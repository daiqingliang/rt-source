package com.sun.management.jmx;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Deprecated
public class JMProperties {
  @Deprecated
  public static final String MLET_LIB_DIR = "jmx.mlet.library.dir";
  
  @Deprecated
  public static final String JMX_SPEC_NAME = "jmx.specification.name";
  
  @Deprecated
  public static final String JMX_SPEC_VERSION = "jmx.specification.version";
  
  @Deprecated
  public static final String JMX_SPEC_VENDOR = "jmx.specification.vendor";
  
  @Deprecated
  public static final String JMX_IMPL_NAME = "jmx.implementation.name";
  
  @Deprecated
  public static final String JMX_IMPL_VENDOR = "jmx.implementation.vendor";
  
  @Deprecated
  public static final String JMX_IMPL_VERSION = "jmx.implementation.version";
  
  @Deprecated
  public static void load(String paramString) throws IOException {
    Properties properties = new Properties(System.getProperties());
    FileInputStream fileInputStream = new FileInputStream(paramString);
    properties.load(fileInputStream);
    fileInputStream.close();
    System.setProperties(properties);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\jmx\JMProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */