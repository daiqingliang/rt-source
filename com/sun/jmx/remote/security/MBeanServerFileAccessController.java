package com.sun.jmx.remote.security;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.Subject;

public class MBeanServerFileAccessController extends MBeanServerAccessController {
  static final String READONLY = "readonly";
  
  static final String READWRITE = "readwrite";
  
  static final String CREATE = "create";
  
  static final String UNREGISTER = "unregister";
  
  private Map<String, Access> accessMap;
  
  private Properties originalProps;
  
  private String accessFileName;
  
  public MBeanServerFileAccessController(String paramString) throws IOException {
    this.accessFileName = paramString;
    Properties properties = propertiesFromFile(paramString);
    parseProperties(properties);
  }
  
  public MBeanServerFileAccessController(String paramString, MBeanServer paramMBeanServer) throws IOException {
    this(paramString);
    setMBeanServer(paramMBeanServer);
  }
  
  public MBeanServerFileAccessController(Properties paramProperties) throws IOException {
    if (paramProperties == null)
      throw new IllegalArgumentException("Null properties"); 
    this.originalProps = paramProperties;
    parseProperties(paramProperties);
  }
  
  public MBeanServerFileAccessController(Properties paramProperties, MBeanServer paramMBeanServer) throws IOException {
    this(paramProperties);
    setMBeanServer(paramMBeanServer);
  }
  
  public void checkRead() { checkAccess(AccessType.READ, null); }
  
  public void checkWrite() { checkAccess(AccessType.WRITE, null); }
  
  public void checkCreate(String paramString) throws IOException { checkAccess(AccessType.CREATE, paramString); }
  
  public void checkUnregister(ObjectName paramObjectName) { checkAccess(AccessType.UNREGISTER, null); }
  
  public void refresh() {
    Properties properties;
    if (this.accessFileName == null) {
      properties = this.originalProps;
    } else {
      properties = propertiesFromFile(this.accessFileName);
    } 
    parseProperties(properties);
  }
  
  private static Properties propertiesFromFile(String paramString) throws IOException {
    fileInputStream = new FileInputStream(paramString);
    try {
      Properties properties = new Properties();
      properties.load(fileInputStream);
      return properties;
    } finally {
      fileInputStream.close();
    } 
  }
  
  private void checkAccess(AccessType paramAccessType, String paramString) {
    final AccessControlContext acc = AccessController.getContext();
    Subject subject = (Subject)AccessController.doPrivileged(new PrivilegedAction<Subject>() {
          public Subject run() { return Subject.getSubject(acc); }
        });
    if (subject == null)
      return; 
    Set set = subject.getPrincipals();
    String str = null;
    for (Principal principal : set) {
      Access access = (Access)this.accessMap.get(principal.getName());
      if (access != null) {
        boolean bool;
        switch (paramAccessType) {
          case READ:
            bool = true;
            break;
          case WRITE:
            bool = access.write;
            break;
          case UNREGISTER:
            bool = access.unregister;
            if (!bool && access.write)
              str = "unregister"; 
            break;
          case CREATE:
            bool = checkCreateAccess(access, paramString);
            if (!bool && access.write)
              str = "create " + paramString; 
            break;
          default:
            throw new AssertionError();
        } 
        if (bool)
          return; 
      } 
    } 
    SecurityException securityException = new SecurityException("Access denied! Invalid access level for requested MBeanServer operation.");
    if (str != null) {
      SecurityException securityException1 = new SecurityException("Access property for this identity should be similar to: readwrite " + str);
      securityException.initCause(securityException1);
    } 
    throw securityException;
  }
  
  private static boolean checkCreateAccess(Access paramAccess, String paramString) {
    for (String str : paramAccess.createPatterns) {
      if (classNameMatch(str, paramString))
        return true; 
    } 
    return false;
  }
  
  private static boolean classNameMatch(String paramString1, String paramString2) {
    StringBuilder stringBuilder = new StringBuilder();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString1, "*", true);
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      if (str.equals("*")) {
        stringBuilder.append("[^.]*");
        continue;
      } 
      stringBuilder.append(Pattern.quote(str));
    } 
    return paramString2.matches(stringBuilder.toString());
  }
  
  private void parseProperties(Properties paramProperties) throws IOException {
    this.accessMap = new HashMap();
    for (Map.Entry entry : paramProperties.entrySet()) {
      String str1 = (String)entry.getKey();
      String str2 = (String)entry.getValue();
      Access access = Parser.parseAccess(str1, str2);
      this.accessMap.put(str1, access);
    } 
  }
  
  private static class Access {
    final boolean write;
    
    final String[] createPatterns;
    
    private boolean unregister;
    
    private final String[] NO_STRINGS = new String[0];
    
    Access(boolean param1Boolean1, boolean param1Boolean2, List<String> param1List) {
      this.write = param1Boolean1;
      boolean bool = (param1List == null) ? 0 : param1List.size();
      if (!bool) {
        this.createPatterns = this.NO_STRINGS;
      } else {
        this.createPatterns = (String[])param1List.toArray(new String[bool]);
      } 
      this.unregister = param1Boolean2;
    }
  }
  
  private enum AccessType {
    READ, WRITE, CREATE, UNREGISTER;
  }
  
  private static class Parser {
    private static final int EOS = -1;
    
    private final String identity;
    
    private final String s;
    
    private final int len;
    
    private int i;
    
    private int c;
    
    private Parser(String param1String1, String param1String2) {
      this.identity = param1String1;
      this.s = param1String2;
      this.len = param1String2.length();
      this.i = 0;
      if (this.i < this.len) {
        this.c = param1String2.codePointAt(this.i);
      } else {
        this.c = -1;
      } 
    }
    
    static MBeanServerFileAccessController.Access parseAccess(String param1String1, String param1String2) { return (new Parser(param1String1, param1String2)).parseAccess(); }
    
    private MBeanServerFileAccessController.Access parseAccess() {
      MBeanServerFileAccessController.Access access;
      skipSpace();
      String str = parseWord();
      if (str.equals("readonly")) {
        access = new MBeanServerFileAccessController.Access(false, false, null);
      } else if (str.equals("readwrite")) {
        access = parseReadWrite();
      } else {
        throw syntax("Expected readonly or readwrite: " + str);
      } 
      if (this.c != -1)
        throw syntax("Extra text at end of line"); 
      return access;
    }
    
    private MBeanServerFileAccessController.Access parseReadWrite() {
      ArrayList arrayList = new ArrayList();
      boolean bool = false;
      while (true) {
        skipSpace();
        if (this.c == -1)
          break; 
        String str = parseWord();
        if (str.equals("unregister")) {
          bool = true;
          continue;
        } 
        if (str.equals("create")) {
          parseCreate(arrayList);
          continue;
        } 
        throw syntax("Unrecognized keyword " + str);
      } 
      return new MBeanServerFileAccessController.Access(true, bool, arrayList);
    }
    
    private void parseCreate(List<String> param1List) {
      while (true) {
        skipSpace();
        param1List.add(parseClassName());
        skipSpace();
        if (this.c == 44) {
          next();
          continue;
        } 
        break;
      } 
    }
    
    private String parseClassName() {
      int j = this.i;
      boolean bool = false;
      while (true) {
        if (this.c == 46) {
          if (!bool)
            throw syntax("Bad . in class name"); 
          bool = false;
        } else if (this.c == 42 || Character.isJavaIdentifierPart(this.c)) {
          bool = true;
        } else {
          break;
        } 
        next();
      } 
      String str = this.s.substring(j, this.i);
      if (!bool)
        throw syntax("Bad class name " + str); 
      return str;
    }
    
    private void next() {
      if (this.c != -1) {
        this.i += Character.charCount(this.c);
        if (this.i < this.len) {
          this.c = this.s.codePointAt(this.i);
        } else {
          this.c = -1;
        } 
      } 
    }
    
    private void skipSpace() {
      while (Character.isWhitespace(this.c))
        next(); 
    }
    
    private String parseWord() {
      skipSpace();
      if (this.c == -1)
        throw syntax("Expected word at end of line"); 
      int j = this.i;
      while (this.c != -1 && !Character.isWhitespace(this.c))
        next(); 
      String str = this.s.substring(j, this.i);
      skipSpace();
      return str;
    }
    
    private IllegalArgumentException syntax(String param1String) { return new IllegalArgumentException(param1String + " [" + this.identity + " " + this.s + "]"); }
    
    static  {
      assert !Character.isWhitespace(-1);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\security\MBeanServerFileAccessController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */