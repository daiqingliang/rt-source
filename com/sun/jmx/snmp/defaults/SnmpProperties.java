package com.sun.jmx.snmp.defaults;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class SnmpProperties {
  public static final String MLET_LIB_DIR = "jmx.mlet.library.dir";
  
  public static final String ACL_FILE = "jdmk.acl.file";
  
  public static final String SECURITY_FILE = "jdmk.security.file";
  
  public static final String UACL_FILE = "jdmk.uacl.file";
  
  public static final String MIB_CORE_FILE = "mibcore.file";
  
  public static final String JMX_SPEC_NAME = "jmx.specification.name";
  
  public static final String JMX_SPEC_VERSION = "jmx.specification.version";
  
  public static final String JMX_SPEC_VENDOR = "jmx.specification.vendor";
  
  public static final String JMX_IMPL_NAME = "jmx.implementation.name";
  
  public static final String JMX_IMPL_VENDOR = "jmx.implementation.vendor";
  
  public static final String JMX_IMPL_VERSION = "jmx.implementation.version";
  
  public static final String SSL_CIPHER_SUITE = "jdmk.ssl.cipher.suite.";
  
  public static void load(String paramString) throws IOException {
    Properties properties = new Properties();
    FileInputStream fileInputStream = new FileInputStream(paramString);
    properties.load(fileInputStream);
    fileInputStream.close();
    Enumeration enumeration = properties.keys();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      System.setProperty(str, properties.getProperty(str));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\defaults\SnmpProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */