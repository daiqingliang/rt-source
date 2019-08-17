package sun.security.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.security.URIParameter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.security.auth.AuthPermission;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.ConfigurationSpi;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;
import sun.security.util.ResourcesMgr;

public final class ConfigFile extends Configuration {
  private final Spi spi = new Spi();
  
  public AppConfigurationEntry[] getAppConfigurationEntry(String paramString) { return this.spi.engineGetAppConfigurationEntry(paramString); }
  
  public void refresh() { this.spi.engineRefresh(); }
  
  public static final class Spi extends ConfigurationSpi {
    private URL url;
    
    private boolean expandProp = true;
    
    private Map<String, List<AppConfigurationEntry>> configuration;
    
    private int linenum;
    
    private StreamTokenizer st;
    
    private int lookahead;
    
    private static Debug debugConfig;
    
    private static Debug debugParser = (debugConfig = Debug.getInstance("configfile")).getInstance("configparser");
    
    public Spi() {
      try {
        init();
      } catch (IOException iOException) {
        throw new SecurityException(iOException);
      } 
    }
    
    public Spi(URI param1URI) {
      try {
        this.url = param1URI.toURL();
        init();
      } catch (IOException iOException) {
        throw new SecurityException(iOException);
      } 
    }
    
    public Spi(final Configuration.Parameters params) throws IOException {
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
              public Void run() throws IOException {
                if (params == null) {
                  ConfigFile.Spi.this.init();
                } else {
                  if (!(params instanceof URIParameter))
                    throw new IllegalArgumentException("Unrecognized parameter: " + params); 
                  URIParameter uRIParameter = (URIParameter)params;
                  ConfigFile.Spi.this.url = uRIParameter.getURI().toURL();
                  ConfigFile.Spi.this.init();
                } 
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (IOException)privilegedActionException.getException();
      } 
    }
    
    private void init() {
      boolean bool = false;
      String str1 = Security.getProperty("policy.expandProperties");
      if (str1 == null)
        str1 = System.getProperty("policy.expandProperties"); 
      if ("false".equals(str1))
        this.expandProp = false; 
      HashMap hashMap = new HashMap();
      if (this.url != null) {
        if (debugConfig != null)
          debugConfig.println("reading " + this.url); 
        init(this.url, hashMap);
        this.configuration = hashMap;
        return;
      } 
      String str2 = Security.getProperty("policy.allowSystemProperty");
      if ("true".equalsIgnoreCase(str2)) {
        String str = System.getProperty("java.security.auth.login.config");
        if (str != null) {
          boolean bool1 = false;
          if (str.startsWith("=")) {
            bool1 = true;
            str = str.substring(1);
          } 
          try {
            str = PropertyExpander.expand(str);
          } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
            throw ioException("Unable.to.properly.expand.config", new Object[] { str });
          } 
          URL uRL = null;
          try {
            uRL = new URL(str);
          } catch (MalformedURLException malformedURLException) {
            File file = new File(str);
            if (file.exists()) {
              uRL = file.toURI().toURL();
            } else {
              throw ioException("extra.config.No.such.file.or.directory.", new Object[] { str });
            } 
          } 
          if (debugConfig != null)
            debugConfig.println("reading " + uRL); 
          init(uRL, hashMap);
          bool = true;
          if (bool1) {
            if (debugConfig != null)
              debugConfig.println("overriding other policies!"); 
            this.configuration = hashMap;
            return;
          } 
        } 
      } 
      byte b;
      String str3;
      for (b = 1; (str3 = Security.getProperty("login.config.url." + b)) != null; b++) {
        try {
          str3 = PropertyExpander.expand(str3).replace(File.separatorChar, '/');
          if (debugConfig != null)
            debugConfig.println("\tReading config: " + str3); 
          init(new URL(str3), hashMap);
          bool = true;
        } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
          throw ioException("Unable.to.properly.expand.config", new Object[] { str3 });
        } 
      } 
      if (!bool && b == 1 && str3 == null) {
        if (debugConfig != null)
          debugConfig.println("\tReading Policy from ~/.java.login.config"); 
        str3 = System.getProperty("user.home");
        String str = str3 + File.separatorChar + ".java.login.config";
        if ((new File(str)).exists())
          init((new File(str)).toURI().toURL(), hashMap); 
      } 
      this.configuration = hashMap;
    }
    
    private void init(URL param1URL, Map<String, List<AppConfigurationEntry>> param1Map) throws IOException {
      try (InputStreamReader null = new InputStreamReader(getInputStream(param1URL), "UTF-8")) {
        readConfig(inputStreamReader, param1Map);
      } catch (FileNotFoundException fileNotFoundException) {
        if (debugConfig != null)
          debugConfig.println(fileNotFoundException.toString()); 
        throw new IOException(ResourcesMgr.getString("Configuration.Error.No.such.file.or.directory", "sun.security.util.AuthResources"));
      } 
    }
    
    public AppConfigurationEntry[] engineGetAppConfigurationEntry(String param1String) {
      List list = null;
      synchronized (this.configuration) {
        list = (List)this.configuration.get(param1String);
      } 
      if (list == null || list.size() == 0)
        return null; 
      AppConfigurationEntry[] arrayOfAppConfigurationEntry = new AppConfigurationEntry[list.size()];
      Iterator iterator = list.iterator();
      for (byte b = 0; iterator.hasNext(); b++) {
        AppConfigurationEntry appConfigurationEntry = (AppConfigurationEntry)iterator.next();
        arrayOfAppConfigurationEntry[b] = new AppConfigurationEntry(appConfigurationEntry.getLoginModuleName(), appConfigurationEntry.getControlFlag(), appConfigurationEntry.getOptions());
      } 
      return arrayOfAppConfigurationEntry;
    }
    
    public void engineRefresh() {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(new AuthPermission("refreshLoginConfiguration")); 
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() throws IOException {
              try {
                ConfigFile.Spi.this.init();
              } catch (IOException iOException) {
                throw new SecurityException(iOException.getLocalizedMessage(), iOException);
              } 
              return null;
            }
          });
    }
    
    private void readConfig(Reader param1Reader, Map<String, List<AppConfigurationEntry>> param1Map) throws IOException {
      this.linenum = 1;
      if (!(param1Reader instanceof BufferedReader))
        param1Reader = new BufferedReader(param1Reader); 
      this.st = new StreamTokenizer(param1Reader);
      this.st.quoteChar(34);
      this.st.wordChars(36, 36);
      this.st.wordChars(95, 95);
      this.st.wordChars(45, 45);
      this.st.wordChars(42, 42);
      this.st.lowerCaseMode(false);
      this.st.slashSlashComments(true);
      this.st.slashStarComments(true);
      this.st.eolIsSignificant(true);
      this.lookahead = nextToken();
      while (this.lookahead != -1)
        parseLoginEntry(param1Map); 
    }
    
    private void parseLoginEntry(Map<String, List<AppConfigurationEntry>> param1Map) throws IOException {
      LinkedList linkedList = new LinkedList();
      String str = this.st.sval;
      this.lookahead = nextToken();
      if (debugParser != null)
        debugParser.println("\tReading next config entry: " + str); 
      match("{");
      while (!peek("}")) {
        AppConfigurationEntry.LoginModuleControlFlag loginModuleControlFlag;
        String str1 = match("module class name");
        String str2 = match("controlFlag").toUpperCase(Locale.ENGLISH);
        switch (str2) {
          case "REQUIRED":
            loginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
            break;
          case "REQUISITE":
            loginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
            break;
          case "SUFFICIENT":
            loginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
            break;
          case "OPTIONAL":
            loginModuleControlFlag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
            break;
          default:
            throw ioException("Configuration.Error.Invalid.control.flag.flag", new Object[] { str2 });
        } 
        HashMap hashMap = new HashMap();
        while (!peek(";")) {
          String str3 = match("option key");
          match("=");
          try {
            hashMap.put(str3, expand(match("option value")));
          } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
            throw new IOException(expandException.getLocalizedMessage());
          } 
        } 
        this.lookahead = nextToken();
        if (debugParser != null) {
          debugParser.println("\t\t" + str1 + ", " + str2);
          for (String str3 : hashMap.keySet())
            debugParser.println("\t\t\t" + str3 + "=" + (String)hashMap.get(str3)); 
        } 
        linkedList.add(new AppConfigurationEntry(str1, loginModuleControlFlag, hashMap));
      } 
      match("}");
      match(";");
      if (param1Map.containsKey(str))
        throw ioException("Configuration.Error.Can.not.specify.multiple.entries.for.appName", new Object[] { str }); 
      param1Map.put(str, linkedList);
    }
    
    private String match(String param1String) throws IOException {
      String str = null;
      switch (this.lookahead) {
        case -1:
          throw ioException("Configuration.Error.expected.expect.read.end.of.file.", new Object[] { param1String });
        case -3:
        case 34:
          if (param1String.equalsIgnoreCase("module class name") || param1String.equalsIgnoreCase("controlFlag") || param1String.equalsIgnoreCase("option key") || param1String.equalsIgnoreCase("option value")) {
            str = this.st.sval;
            this.lookahead = nextToken();
          } else {
            throw ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Object[] { new Integer(this.linenum), param1String, this.st.sval });
          } 
          return str;
        case 123:
          if (param1String.equalsIgnoreCase("{")) {
            this.lookahead = nextToken();
          } else {
            throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(this.linenum), param1String, this.st.sval });
          } 
          return str;
        case 59:
          if (param1String.equalsIgnoreCase(";")) {
            this.lookahead = nextToken();
          } else {
            throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(this.linenum), param1String, this.st.sval });
          } 
          return str;
        case 125:
          if (param1String.equalsIgnoreCase("}")) {
            this.lookahead = nextToken();
          } else {
            throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(this.linenum), param1String, this.st.sval });
          } 
          return str;
        case 61:
          if (param1String.equalsIgnoreCase("=")) {
            this.lookahead = nextToken();
          } else {
            throw ioException("Configuration.Error.Line.line.expected.expect.", new Object[] { new Integer(this.linenum), param1String, this.st.sval });
          } 
          return str;
      } 
      throw ioException("Configuration.Error.Line.line.expected.expect.found.value.", new Object[] { new Integer(this.linenum), param1String, this.st.sval });
    }
    
    private boolean peek(String param1String) {
      switch (this.lookahead) {
        case 44:
          return param1String.equalsIgnoreCase(",");
        case 59:
          return param1String.equalsIgnoreCase(";");
        case 123:
          return param1String.equalsIgnoreCase("{");
        case 125:
          return param1String.equalsIgnoreCase("}");
      } 
      return false;
    }
    
    private int nextToken() throws IOException {
      int i;
      while ((i = this.st.nextToken()) == 10)
        this.linenum++; 
      return i;
    }
    
    private InputStream getInputStream(URL param1URL) throws IOException {
      if ("file".equalsIgnoreCase(param1URL.getProtocol()))
        try {
          return param1URL.openStream();
        } catch (Exception exception) {
          String str = param1URL.getPath();
          if (param1URL.getHost().length() > 0)
            str = "//" + param1URL.getHost() + str; 
          if (debugConfig != null)
            debugConfig.println("cannot read " + param1URL + ", try " + str); 
          return new FileInputStream(str);
        }  
      return param1URL.openStream();
    }
    
    private String expand(String param1String) throws IOException {
      if (param1String.isEmpty())
        return param1String; 
      if (!this.expandProp)
        return param1String; 
      String str = PropertyExpander.expand(param1String);
      if (str == null || str.length() == 0)
        throw ioException("Configuration.Error.Line.line.system.property.value.expanded.to.empty.value", new Object[] { new Integer(this.linenum), param1String }); 
      return str;
    }
    
    private IOException ioException(String param1String, Object... param1VarArgs) {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString(param1String, "sun.security.util.AuthResources"));
      return new IOException(messageFormat.format(param1VarArgs));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\ConfigFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */