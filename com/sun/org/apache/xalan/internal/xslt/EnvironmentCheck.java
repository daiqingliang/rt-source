package com.sun.org.apache.xalan.internal.xslt;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EnvironmentCheck {
  public static final String ERROR = "ERROR.";
  
  public static final String WARNING = "WARNING.";
  
  public static final String ERROR_FOUND = "At least one error was found!";
  
  public static final String VERSION = "version.";
  
  public static final String FOUNDCLASSES = "foundclasses.";
  
  public static final String CLASS_PRESENT = "present-unknown-version";
  
  public static final String CLASS_NOTPRESENT = "not-present";
  
  public String[] jarNames = { 
      "xalan.jar", "xalansamples.jar", "xalanj1compat.jar", "xalanservlet.jar", "serializer.jar", "xerces.jar", "xercesImpl.jar", "testxsl.jar", "crimson.jar", "lotusxsl.jar", 
      "jaxp.jar", "parser.jar", "dom.jar", "sax.jar", "xml.jar", "xml-apis.jar", "xsltc.jar" };
  
  private static final Map<Long, String> JARVERSIONS;
  
  protected PrintWriter outWriter = new PrintWriter(System.out, true);
  
  public static void main(String[] paramArrayOfString) {
    PrintWriter printWriter = new PrintWriter(System.out, true);
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if ("-out".equalsIgnoreCase(paramArrayOfString[b]))
        if (++b < paramArrayOfString.length) {
          try {
            printWriter = new PrintWriter(new FileWriter(paramArrayOfString[b], true));
          } catch (Exception exception) {
            System.err.println("# WARNING: -out " + paramArrayOfString[b] + " threw " + exception.toString());
          } 
        } else {
          System.err.println("# WARNING: -out argument should have a filename, output sent to console");
        }  
    } 
    EnvironmentCheck environmentCheck = new EnvironmentCheck();
    environmentCheck.checkEnvironment(printWriter);
  }
  
  public boolean checkEnvironment(PrintWriter paramPrintWriter) {
    if (null != paramPrintWriter)
      this.outWriter = paramPrintWriter; 
    Map map = getEnvironmentHash();
    boolean bool = writeEnvironmentReport(map);
    if (bool) {
      logMsg("# WARNING: Potential problems found in your environment!");
      logMsg("#    Check any 'ERROR' items above against the Xalan FAQs");
      logMsg("#    to correct potential problems with your classes/jars");
      logMsg("#    http://xml.apache.org/xalan-j/faq.html");
      if (null != this.outWriter)
        this.outWriter.flush(); 
      return false;
    } 
    logMsg("# YAHOO! Your environment seems to be OK.");
    if (null != this.outWriter)
      this.outWriter.flush(); 
    return true;
  }
  
  public Map<String, Object> getEnvironmentHash() {
    HashMap hashMap = new HashMap();
    checkJAXPVersion(hashMap);
    checkProcessorVersion(hashMap);
    checkParserVersion(hashMap);
    checkAntVersion(hashMap);
    if (!checkDOML3(hashMap))
      checkDOMVersion(hashMap); 
    checkSAXVersion(hashMap);
    checkSystemProperties(hashMap);
    return hashMap;
  }
  
  protected boolean writeEnvironmentReport(Map<String, Object> paramMap) {
    if (null == paramMap) {
      logMsg("# ERROR: writeEnvironmentReport called with null Map");
      return false;
    } 
    boolean bool = false;
    logMsg("#---- BEGIN writeEnvironmentReport($Revision: 1.10 $): Useful stuff found: ----");
    for (Map.Entry entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      try {
        if (str.startsWith("foundclasses.")) {
          ArrayList arrayList = (ArrayList)entry.getValue();
          bool |= logFoundJars(arrayList, str);
          continue;
        } 
        if (str.startsWith("ERROR."))
          bool = true; 
        logMsg(str + "=" + paramMap.get(str));
      } catch (Exception exception) {
        logMsg("Reading-" + str + "= threw: " + exception.toString());
      } 
    } 
    logMsg("#----- END writeEnvironmentReport: Useful properties found: -----");
    return bool;
  }
  
  protected boolean logFoundJars(List<Map> paramList, String paramString) {
    if (null == paramList || paramList.size() < 1)
      return false; 
    boolean bool = false;
    logMsg("#---- BEGIN Listing XML-related jars in: " + paramString + " ----");
    for (Map map : paramList) {
      for (Map.Entry entry : map.entrySet()) {
        String str = (String)entry.getKey();
        try {
          if (str.startsWith("ERROR."))
            bool = true; 
          logMsg(str + "=" + (String)entry.getValue());
        } catch (Exception exception) {
          bool = true;
          logMsg("Reading-" + str + "= threw: " + exception.toString());
        } 
      } 
    } 
    logMsg("#----- END Listing XML-related jars in: " + paramString + " -----");
    return bool;
  }
  
  public void appendEnvironmentReport(Node paramNode, Document paramDocument, Map<String, Object> paramMap) {
    if (null == paramNode || null == paramDocument)
      return; 
    try {
      Element element1 = paramDocument.createElement("EnvironmentCheck");
      element1.setAttribute("version", "$Revision: 1.10 $");
      paramNode.appendChild(element1);
      if (null == paramMap) {
        Element element = paramDocument.createElement("status");
        element.setAttribute("result", "ERROR");
        element.appendChild(paramDocument.createTextNode("appendEnvironmentReport called with null Map!"));
        element1.appendChild(element);
        return;
      } 
      boolean bool = false;
      Element element2 = paramDocument.createElement("environment");
      element1.appendChild(element2);
      for (Map.Entry entry : paramMap.entrySet()) {
        String str = (String)entry.getKey();
        try {
          if (str.startsWith("foundclasses.")) {
            List list = (List)entry.getValue();
            bool |= appendFoundJars(element2, paramDocument, list, str);
            continue;
          } 
          if (str.startsWith("ERROR."))
            bool = true; 
          Element element = paramDocument.createElement("item");
          element.setAttribute("key", str);
          element.appendChild(paramDocument.createTextNode((String)paramMap.get(str)));
          element2.appendChild(element);
        } catch (Exception exception) {
          bool = true;
          Element element = paramDocument.createElement("item");
          element.setAttribute("key", str);
          element.appendChild(paramDocument.createTextNode("ERROR. Reading " + str + " threw: " + exception.toString()));
          element2.appendChild(element);
        } 
      } 
      Element element3 = paramDocument.createElement("status");
      element3.setAttribute("result", bool ? "ERROR" : "OK");
      element1.appendChild(element3);
    } catch (Exception exception) {
      System.err.println("appendEnvironmentReport threw: " + exception.toString());
      exception.printStackTrace();
    } 
  }
  
  protected boolean appendFoundJars(Node paramNode, Document paramDocument, List<Map> paramList, String paramString) {
    if (null == paramList || paramList.size() < 1)
      return false; 
    boolean bool = false;
    for (Map map : paramList) {
      for (Map.Entry entry : map.entrySet()) {
        String str = (String)entry.getKey();
        try {
          if (str.startsWith("ERROR."))
            bool = true; 
          Element element = paramDocument.createElement("foundJar");
          element.setAttribute("name", str.substring(0, str.indexOf("-")));
          element.setAttribute("desc", str.substring(str.indexOf("-") + 1));
          element.appendChild(paramDocument.createTextNode((String)entry.getValue()));
          paramNode.appendChild(element);
        } catch (Exception exception) {
          bool = true;
          Element element = paramDocument.createElement("foundJar");
          element.appendChild(paramDocument.createTextNode("ERROR. Reading " + str + " threw: " + exception.toString()));
          paramNode.appendChild(element);
        } 
      } 
    } 
    return bool;
  }
  
  protected void checkSystemProperties(Map<String, Object> paramMap) {
    if (null == paramMap)
      paramMap = new HashMap<String, Object>(); 
    try {
      String str = SecuritySupport.getSystemProperty("java.version");
      paramMap.put("java.version", str);
    } catch (SecurityException securityException) {
      paramMap.put("java.version", "WARNING: SecurityException thrown accessing system version properties");
    } 
    try {
      String str1 = SecuritySupport.getSystemProperty("java.class.path");
      paramMap.put("java.class.path", str1);
      List list = checkPathForJars(str1, this.jarNames);
      if (null != list)
        paramMap.put("foundclasses.java.class.path", list); 
      String str2 = SecuritySupport.getSystemProperty("sun.boot.class.path");
      if (null != str2) {
        paramMap.put("sun.boot.class.path", str2);
        list = checkPathForJars(str2, this.jarNames);
        if (null != list)
          paramMap.put("foundclasses.sun.boot.class.path", list); 
      } 
      str2 = SecuritySupport.getSystemProperty("java.ext.dirs");
      if (null != str2) {
        paramMap.put("java.ext.dirs", str2);
        list = checkPathForJars(str2, this.jarNames);
        if (null != list)
          paramMap.put("foundclasses.java.ext.dirs", list); 
      } 
    } catch (SecurityException securityException) {
      paramMap.put("java.class.path", "WARNING: SecurityException thrown accessing system classpath properties");
    } 
  }
  
  protected List<Map> checkPathForJars(String paramString, String[] paramArrayOfString) {
    if (null == paramString || null == paramArrayOfString || 0 == paramString.length() || 0 == paramArrayOfString.length)
      return null; 
    ArrayList arrayList = new ArrayList();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        if (str.indexOf(paramArrayOfString[b]) > -1) {
          File file = new File(str);
          if (file.exists()) {
            try {
              HashMap hashMap = new HashMap(2);
              hashMap.put(paramArrayOfString[b] + "-path", file.getAbsolutePath());
              if (!"xalan.jar".equalsIgnoreCase(paramArrayOfString[b]))
                hashMap.put(paramArrayOfString[b] + "-apparent.version", getApparentVersion(paramArrayOfString[b], file.length())); 
              arrayList.add(hashMap);
            } catch (Exception exception) {}
          } else {
            HashMap hashMap = new HashMap(2);
            hashMap.put(paramArrayOfString[b] + "-path", "WARNING. Classpath entry: " + str + " does not exist");
            hashMap.put(paramArrayOfString[b] + "-apparent.version", "not-present");
            arrayList.add(hashMap);
          } 
        } 
      } 
    } 
    return arrayList;
  }
  
  protected String getApparentVersion(String paramString, long paramLong) {
    String str = (String)JARVERSIONS.get(new Long(paramLong));
    return (null != str && str.startsWith(paramString)) ? str : (("xerces.jar".equalsIgnoreCase(paramString) || "xercesImpl.jar".equalsIgnoreCase(paramString)) ? (paramString + " " + "WARNING." + "present-unknown-version") : (paramString + " " + "present-unknown-version"));
  }
  
  protected void checkJAXPVersion(Map<String, Object> paramMap) {
    if (null == paramMap)
      paramMap = new HashMap<String, Object>(); 
    Class clazz = null;
    try {
      String str = "javax.xml.stream.XMLStreamConstants";
      clazz = ObjectFactory.findProviderClass("javax.xml.stream.XMLStreamConstants", true);
      paramMap.put("version.JAXP", "1.4");
    } catch (Exception exception) {
      paramMap.put("ERROR.version.JAXP", "1.3");
      paramMap.put("ERROR.", "At least one error was found!");
    } 
  }
  
  protected void checkProcessorVersion(Map<String, Object> paramMap) {
    if (null == paramMap)
      paramMap = new HashMap<String, Object>(); 
    try {
      String str = "com.sun.org.apache.xalan.internal.xslt.XSLProcessorVersion";
      Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.xslt.XSLProcessorVersion", true);
      StringBuffer stringBuffer = new StringBuffer();
      Field field = clazz.getField("PRODUCT");
      stringBuffer.append(field.get(null));
      stringBuffer.append(';');
      field = clazz.getField("LANGUAGE");
      stringBuffer.append(field.get(null));
      stringBuffer.append(';');
      field = clazz.getField("S_VERSION");
      stringBuffer.append(field.get(null));
      stringBuffer.append(';');
      paramMap.put("version.xalan1", stringBuffer.toString());
    } catch (Exception exception) {
      paramMap.put("version.xalan1", "not-present");
    } 
    try {
      String str = "com.sun.org.apache.xalan.internal.processor.XSLProcessorVersion";
      Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.processor.XSLProcessorVersion", true);
      StringBuffer stringBuffer = new StringBuffer();
      Field field = clazz.getField("S_VERSION");
      stringBuffer.append(field.get(null));
      paramMap.put("version.xalan2x", stringBuffer.toString());
    } catch (Exception exception) {
      paramMap.put("version.xalan2x", "not-present");
    } 
    try {
      String str1 = "com.sun.org.apache.xalan.internal.Version";
      String str2 = "getVersion";
      Class[] arrayOfClass = new Class[0];
      Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xalan.internal.Version", true);
      Method method = clazz.getMethod("getVersion", arrayOfClass);
      Object object = method.invoke(null, new Object[0]);
      paramMap.put("version.xalan2_2", (String)object);
    } catch (Exception exception) {
      paramMap.put("version.xalan2_2", "not-present");
    } 
  }
  
  protected void checkParserVersion(Map<String, Object> paramMap) {
    if (null == paramMap)
      paramMap = new HashMap<String, Object>(); 
    try {
      String str1 = "com.sun.org.apache.xerces.internal.framework.Version";
      Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.framework.Version", true);
      Field field = clazz.getField("fVersion");
      String str2 = (String)field.get(null);
      paramMap.put("version.xerces1", str2);
    } catch (Exception exception) {
      paramMap.put("version.xerces1", "not-present");
    } 
    try {
      String str1 = "com.sun.org.apache.xerces.internal.impl.Version";
      Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.impl.Version", true);
      Field field = clazz.getField("fVersion");
      String str2 = (String)field.get(null);
      paramMap.put("version.xerces2", str2);
    } catch (Exception exception) {
      paramMap.put("version.xerces2", "not-present");
    } 
    try {
      String str = "org.apache.crimson.parser.Parser2";
      Class clazz = ObjectFactory.findProviderClass("org.apache.crimson.parser.Parser2", true);
      paramMap.put("version.crimson", "present-unknown-version");
    } catch (Exception exception) {
      paramMap.put("version.crimson", "not-present");
    } 
  }
  
  protected void checkAntVersion(Map<String, Object> paramMap) {
    if (null == paramMap)
      paramMap = new HashMap<String, Object>(); 
    try {
      String str1 = "org.apache.tools.ant.Main";
      String str2 = "getAntVersion";
      Class[] arrayOfClass = new Class[0];
      Class clazz = ObjectFactory.findProviderClass("org.apache.tools.ant.Main", true);
      Method method = clazz.getMethod("getAntVersion", arrayOfClass);
      Object object = method.invoke(null, new Object[0]);
      paramMap.put("version.ant", (String)object);
    } catch (Exception exception) {
      paramMap.put("version.ant", "not-present");
    } 
  }
  
  protected boolean checkDOML3(Map<String, Object> paramMap) {
    if (null == paramMap)
      paramMap = new HashMap<String, Object>(); 
    String str1 = "org.w3c.dom.Document";
    String str2 = "getDoctype";
    try {
      Class clazz = ObjectFactory.findProviderClass("org.w3c.dom.Document", true);
      Method method = clazz.getMethod("getDoctype", (Class[])null);
      paramMap.put("version.DOM", "3.0");
      return true;
    } catch (Exception exception) {
      return false;
    } 
  }
  
  protected void checkDOMVersion(Map<String, Object> paramMap) {
    if (null == paramMap)
      paramMap = new HashMap<String, Object>(); 
    String str1 = "org.w3c.dom.Document";
    String str2 = "createElementNS";
    String str3 = "getDoctype";
    String str4 = "org.w3c.dom.Node";
    String str5 = "supported";
    String str6 = "org.w3c.dom.Node";
    String str7 = "isSupported";
    Class[] arrayOfClass = { String.class, String.class };
    try {
      Class clazz = ObjectFactory.findProviderClass("org.w3c.dom.Document", true);
      Method method = clazz.getMethod("createElementNS", arrayOfClass);
      paramMap.put("version.DOM", "2.0");
      try {
        clazz = ObjectFactory.findProviderClass("org.w3c.dom.Node", true);
        method = clazz.getMethod("supported", arrayOfClass);
        paramMap.put("ERROR.version.DOM.draftlevel", "2.0wd");
        paramMap.put("ERROR.", "At least one error was found!");
      } catch (Exception exception) {
        try {
          clazz = ObjectFactory.findProviderClass("org.w3c.dom.Node", true);
          method = clazz.getMethod("isSupported", arrayOfClass);
          paramMap.put("version.DOM.draftlevel", "2.0fd");
        } catch (Exception exception1) {
          paramMap.put("ERROR.version.DOM.draftlevel", "2.0unknown");
          paramMap.put("ERROR.", "At least one error was found!");
        } 
      } 
    } catch (Exception exception) {
      paramMap.put("ERROR.version.DOM", "ERROR attempting to load DOM level 2 class: " + exception.toString());
      paramMap.put("ERROR.", "At least one error was found!");
    } 
  }
  
  protected void checkSAXVersion(Map<String, Object> paramMap) {
    if (null == paramMap)
      paramMap = new HashMap<String, Object>(); 
    String str1 = "org.xml.sax.Parser";
    String str2 = "parse";
    String str3 = "org.xml.sax.XMLReader";
    String str4 = "parse";
    String str5 = "org.xml.sax.helpers.AttributesImpl";
    String str6 = "setAttributes";
    Class[] arrayOfClass1 = { String.class };
    Class[] arrayOfClass2 = { org.xml.sax.Attributes.class };
    try {
      Class clazz = ObjectFactory.findProviderClass("org.xml.sax.helpers.AttributesImpl", true);
      Method method = clazz.getMethod("setAttributes", arrayOfClass2);
      paramMap.put("version.SAX", "2.0");
    } catch (Exception exception) {
      paramMap.put("ERROR.version.SAX", "ERROR attempting to load SAX version 2 class: " + exception.toString());
      paramMap.put("ERROR.", "At least one error was found!");
      try {
        Class clazz = ObjectFactory.findProviderClass("org.xml.sax.XMLReader", true);
        Method method = clazz.getMethod("parse", arrayOfClass1);
        paramMap.put("version.SAX-backlevel", "2.0beta2-or-earlier");
      } catch (Exception exception1) {
        paramMap.put("ERROR.version.SAX", "ERROR attempting to load SAX version 2 class: " + exception.toString());
        paramMap.put("ERROR.", "At least one error was found!");
        try {
          Class clazz = ObjectFactory.findProviderClass("org.xml.sax.Parser", true);
          Method method = clazz.getMethod("parse", arrayOfClass1);
          paramMap.put("version.SAX-backlevel", "1.0");
        } catch (Exception exception2) {
          paramMap.put("ERROR.version.SAX-backlevel", "ERROR attempting to load SAX version 1 class: " + exception2.toString());
        } 
      } 
    } 
  }
  
  protected void logMsg(String paramString) { this.outWriter.println(paramString); }
  
  static  {
    HashMap hashMap = new HashMap();
    hashMap.put(new Long(857192L), "xalan.jar from xalan-j_1_1");
    hashMap.put(new Long(440237L), "xalan.jar from xalan-j_1_2");
    hashMap.put(new Long(436094L), "xalan.jar from xalan-j_1_2_1");
    hashMap.put(new Long(426249L), "xalan.jar from xalan-j_1_2_2");
    hashMap.put(new Long(702536L), "xalan.jar from xalan-j_2_0_0");
    hashMap.put(new Long(720930L), "xalan.jar from xalan-j_2_0_1");
    hashMap.put(new Long(732330L), "xalan.jar from xalan-j_2_1_0");
    hashMap.put(new Long(872241L), "xalan.jar from xalan-j_2_2_D10");
    hashMap.put(new Long(882739L), "xalan.jar from xalan-j_2_2_D11");
    hashMap.put(new Long(923866L), "xalan.jar from xalan-j_2_2_0");
    hashMap.put(new Long(905872L), "xalan.jar from xalan-j_2_3_D1");
    hashMap.put(new Long(906122L), "xalan.jar from xalan-j_2_3_0");
    hashMap.put(new Long(906248L), "xalan.jar from xalan-j_2_3_1");
    hashMap.put(new Long(983377L), "xalan.jar from xalan-j_2_4_D1");
    hashMap.put(new Long(997276L), "xalan.jar from xalan-j_2_4_0");
    hashMap.put(new Long(1031036L), "xalan.jar from xalan-j_2_4_1");
    hashMap.put(new Long(596540L), "xsltc.jar from xalan-j_2_2_0");
    hashMap.put(new Long(590247L), "xsltc.jar from xalan-j_2_3_D1");
    hashMap.put(new Long(589914L), "xsltc.jar from xalan-j_2_3_0");
    hashMap.put(new Long(589915L), "xsltc.jar from xalan-j_2_3_1");
    hashMap.put(new Long(1306667L), "xsltc.jar from xalan-j_2_4_D1");
    hashMap.put(new Long(1328227L), "xsltc.jar from xalan-j_2_4_0");
    hashMap.put(new Long(1344009L), "xsltc.jar from xalan-j_2_4_1");
    hashMap.put(new Long(1348361L), "xsltc.jar from xalan-j_2_5_D1");
    hashMap.put(new Long(1268634L), "xsltc.jar-bundled from xalan-j_2_3_0");
    hashMap.put(new Long(100196L), "xml-apis.jar from xalan-j_2_2_0 or xalan-j_2_3_D1");
    hashMap.put(new Long(108484L), "xml-apis.jar from xalan-j_2_3_0, or xalan-j_2_3_1 from xml-commons-1.0.b2");
    hashMap.put(new Long(109049L), "xml-apis.jar from xalan-j_2_4_0 from xml-commons RIVERCOURT1 branch");
    hashMap.put(new Long(113749L), "xml-apis.jar from xalan-j_2_4_1 from factoryfinder-build of xml-commons RIVERCOURT1");
    hashMap.put(new Long(124704L), "xml-apis.jar from tck-jaxp-1_2_0 branch of xml-commons");
    hashMap.put(new Long(124724L), "xml-apis.jar from tck-jaxp-1_2_0 branch of xml-commons, tag: xml-commons-external_1_2_01");
    hashMap.put(new Long(194205L), "xml-apis.jar from head branch of xml-commons, tag: xml-commons-external_1_3_02");
    hashMap.put(new Long(424490L), "xalan.jar from Xerces Tools releases - ERROR:DO NOT USE!");
    hashMap.put(new Long(1591855L), "xerces.jar from xalan-j_1_1 from xerces-1...");
    hashMap.put(new Long(1498679L), "xerces.jar from xalan-j_1_2 from xerces-1_2_0.bin");
    hashMap.put(new Long(1484896L), "xerces.jar from xalan-j_1_2_1 from xerces-1_2_1.bin");
    hashMap.put(new Long(804460L), "xerces.jar from xalan-j_1_2_2 from xerces-1_2_2.bin");
    hashMap.put(new Long(1499244L), "xerces.jar from xalan-j_2_0_0 from xerces-1_2_3.bin");
    hashMap.put(new Long(1605266L), "xerces.jar from xalan-j_2_0_1 from xerces-1_3_0.bin");
    hashMap.put(new Long(904030L), "xerces.jar from xalan-j_2_1_0 from xerces-1_4.bin");
    hashMap.put(new Long(904030L), "xerces.jar from xerces-1_4_0.bin");
    hashMap.put(new Long(1802885L), "xerces.jar from xerces-1_4_2.bin");
    hashMap.put(new Long(1734594L), "xerces.jar from Xerces-J-bin.2.0.0.beta3");
    hashMap.put(new Long(1808883L), "xerces.jar from xalan-j_2_2_D10,D11,D12 or xerces-1_4_3.bin");
    hashMap.put(new Long(1812019L), "xerces.jar from xalan-j_2_2_0");
    hashMap.put(new Long(1720292L), "xercesImpl.jar from xalan-j_2_3_D1");
    hashMap.put(new Long(1730053L), "xercesImpl.jar from xalan-j_2_3_0 or xalan-j_2_3_1 from xerces-2_0_0");
    hashMap.put(new Long(1728861L), "xercesImpl.jar from xalan-j_2_4_D1 from xerces-2_0_1");
    hashMap.put(new Long(972027L), "xercesImpl.jar from xalan-j_2_4_0 from xerces-2_1");
    hashMap.put(new Long(831587L), "xercesImpl.jar from xalan-j_2_4_1 from xerces-2_2");
    hashMap.put(new Long(891817L), "xercesImpl.jar from xalan-j_2_5_D1 from xerces-2_3");
    hashMap.put(new Long(895924L), "xercesImpl.jar from xerces-2_4");
    hashMap.put(new Long(1010806L), "xercesImpl.jar from Xerces-J-bin.2.6.2");
    hashMap.put(new Long(1203860L), "xercesImpl.jar from Xerces-J-bin.2.7.1");
    hashMap.put(new Long(37485L), "xalanj1compat.jar from xalan-j_2_0_0");
    hashMap.put(new Long(38100L), "xalanj1compat.jar from xalan-j_2_0_1");
    hashMap.put(new Long(18779L), "xalanservlet.jar from xalan-j_2_0_0");
    hashMap.put(new Long(21453L), "xalanservlet.jar from xalan-j_2_0_1");
    hashMap.put(new Long(24826L), "xalanservlet.jar from xalan-j_2_3_1 or xalan-j_2_4_1");
    hashMap.put(new Long(24831L), "xalanservlet.jar from xalan-j_2_4_1");
    hashMap.put(new Long(5618L), "jaxp.jar from jaxp1.0.1");
    hashMap.put(new Long(136133L), "parser.jar from jaxp1.0.1");
    hashMap.put(new Long(28404L), "jaxp.jar from jaxp-1.1");
    hashMap.put(new Long(187162L), "crimson.jar from jaxp-1.1");
    hashMap.put(new Long(801714L), "xalan.jar from jaxp-1.1");
    hashMap.put(new Long(196399L), "crimson.jar from crimson-1.1.1");
    hashMap.put(new Long(33323L), "jaxp.jar from crimson-1.1.1 or jakarta-ant-1.4.1b1");
    hashMap.put(new Long(152717L), "crimson.jar from crimson-1.1.2beta2");
    hashMap.put(new Long(88143L), "xml-apis.jar from crimson-1.1.2beta2");
    hashMap.put(new Long(206384L), "crimson.jar from crimson-1.1.3 or jakarta-ant-1.4.1b1");
    hashMap.put(new Long(136198L), "parser.jar from jakarta-ant-1.3 or 1.2");
    hashMap.put(new Long(5537L), "jaxp.jar from jakarta-ant-1.3 or 1.2");
    JARVERSIONS = Collections.unmodifiableMap(hashMap);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xslt\EnvironmentCheck.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */