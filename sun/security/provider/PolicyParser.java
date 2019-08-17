package sun.security.provider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import sun.net.www.ParseUtil;
import sun.security.util.Debug;
import sun.security.util.PropertyExpander;
import sun.security.util.ResourcesMgr;

public class PolicyParser {
  private static final String EXTDIRS_PROPERTY = "java.ext.dirs";
  
  private static final String OLD_EXTDIRS_EXPANSION = "${java.ext.dirs}";
  
  static final String EXTDIRS_EXPANSION = "${{java.ext.dirs}}";
  
  private Vector<GrantEntry> grantEntries = new Vector();
  
  private Map<String, DomainEntry> domainEntries;
  
  private static final Debug debug = Debug.getInstance("parser", "\t[Policy Parser]");
  
  private StreamTokenizer st;
  
  private int lookahead;
  
  private boolean expandProp = false;
  
  private String keyStoreUrlString = null;
  
  private String keyStoreType = null;
  
  private String keyStoreProvider = null;
  
  private String storePassURL = null;
  
  private String expand(String paramString) throws PropertyExpander.ExpandException { return expand(paramString, false); }
  
  private String expand(String paramString, boolean paramBoolean) throws PropertyExpander.ExpandException { return !this.expandProp ? paramString : PropertyExpander.expand(paramString, paramBoolean); }
  
  public PolicyParser() {}
  
  public PolicyParser(boolean paramBoolean) {
    this();
    this.expandProp = paramBoolean;
  }
  
  public void read(Reader paramReader) throws ParsingException, IOException {
    if (!(paramReader instanceof BufferedReader))
      paramReader = new BufferedReader(paramReader); 
    this.st = new StreamTokenizer(paramReader);
    this.st.resetSyntax();
    this.st.wordChars(97, 122);
    this.st.wordChars(65, 90);
    this.st.wordChars(46, 46);
    this.st.wordChars(48, 57);
    this.st.wordChars(95, 95);
    this.st.wordChars(36, 36);
    this.st.wordChars(160, 255);
    this.st.whitespaceChars(0, 32);
    this.st.commentChar(47);
    this.st.quoteChar(39);
    this.st.quoteChar(34);
    this.st.lowerCaseMode(false);
    this.st.ordinaryChar(47);
    this.st.slashSlashComments(true);
    this.st.slashStarComments(true);
    this.lookahead = this.st.nextToken();
    GrantEntry grantEntry = null;
    while (this.lookahead != -1) {
      if (peek("grant")) {
        grantEntry = parseGrantEntry();
        if (grantEntry != null)
          add(grantEntry); 
      } else if (peek("keystore") && this.keyStoreUrlString == null) {
        parseKeyStoreEntry();
      } else if (peek("keystorePasswordURL") && this.storePassURL == null) {
        parseStorePassURL();
      } else if (grantEntry == null && this.keyStoreUrlString == null && this.storePassURL == null && peek("domain")) {
        if (this.domainEntries == null)
          this.domainEntries = new TreeMap(); 
        DomainEntry domainEntry = parseDomainEntry();
        if (domainEntry != null) {
          String str = domainEntry.getName();
          if (!this.domainEntries.containsKey(str)) {
            this.domainEntries.put(str, domainEntry);
          } else {
            MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("duplicate.keystore.domain.name"));
            Object[] arrayOfObject = { str };
            throw new ParsingException(messageFormat.format(arrayOfObject));
          } 
        } 
      } 
      match(";");
    } 
    if (this.keyStoreUrlString == null && this.storePassURL != null)
      throw new ParsingException(ResourcesMgr.getString("keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore")); 
  }
  
  public void add(GrantEntry paramGrantEntry) { this.grantEntries.addElement(paramGrantEntry); }
  
  public void replace(GrantEntry paramGrantEntry1, GrantEntry paramGrantEntry2) { this.grantEntries.setElementAt(paramGrantEntry2, this.grantEntries.indexOf(paramGrantEntry1)); }
  
  public boolean remove(GrantEntry paramGrantEntry) { return this.grantEntries.removeElement(paramGrantEntry); }
  
  public String getKeyStoreUrl() {
    try {
      if (this.keyStoreUrlString != null && this.keyStoreUrlString.length() != 0)
        return expand(this.keyStoreUrlString, true).replace(File.separatorChar, '/'); 
    } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
      if (debug != null)
        debug.println(expandException.toString()); 
      return null;
    } 
    return null;
  }
  
  public void setKeyStoreUrl(String paramString) { this.keyStoreUrlString = paramString; }
  
  public String getKeyStoreType() { return this.keyStoreType; }
  
  public void setKeyStoreType(String paramString) { this.keyStoreType = paramString; }
  
  public String getKeyStoreProvider() { return this.keyStoreProvider; }
  
  public void setKeyStoreProvider(String paramString) { this.keyStoreProvider = paramString; }
  
  public String getStorePassURL() {
    try {
      if (this.storePassURL != null && this.storePassURL.length() != 0)
        return expand(this.storePassURL, true).replace(File.separatorChar, '/'); 
    } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
      if (debug != null)
        debug.println(expandException.toString()); 
      return null;
    } 
    return null;
  }
  
  public void setStorePassURL(String paramString) { this.storePassURL = paramString; }
  
  public Enumeration<GrantEntry> grantElements() { return this.grantEntries.elements(); }
  
  public Collection<DomainEntry> getDomainEntries() { return this.domainEntries.values(); }
  
  public void write(Writer paramWriter) {
    PrintWriter printWriter = new PrintWriter(new BufferedWriter(paramWriter));
    Enumeration enumeration = grantElements();
    printWriter.println("/* AUTOMATICALLY GENERATED ON " + new Date() + "*/");
    printWriter.println("/* DO NOT EDIT */");
    printWriter.println();
    if (this.keyStoreUrlString != null)
      writeKeyStoreEntry(printWriter); 
    if (this.storePassURL != null)
      writeStorePassURL(printWriter); 
    while (enumeration.hasMoreElements()) {
      GrantEntry grantEntry = (GrantEntry)enumeration.nextElement();
      grantEntry.write(printWriter);
      printWriter.println();
    } 
    printWriter.flush();
  }
  
  private void parseKeyStoreEntry() {
    match("keystore");
    this.keyStoreUrlString = match("quoted string");
    if (!peek(","))
      return; 
    match(",");
    if (peek("\"")) {
      this.keyStoreType = match("quoted string");
    } else {
      throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.keystore.type"));
    } 
    if (!peek(","))
      return; 
    match(",");
    if (peek("\"")) {
      this.keyStoreProvider = match("quoted string");
    } else {
      throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.keystore.provider"));
    } 
  }
  
  private void parseStorePassURL() {
    match("keyStorePasswordURL");
    this.storePassURL = match("quoted string");
  }
  
  private void writeKeyStoreEntry(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("keystore \"");
    paramPrintWriter.print(this.keyStoreUrlString);
    paramPrintWriter.print('"');
    if (this.keyStoreType != null && this.keyStoreType.length() > 0)
      paramPrintWriter.print(", \"" + this.keyStoreType + "\""); 
    if (this.keyStoreProvider != null && this.keyStoreProvider.length() > 0)
      paramPrintWriter.print(", \"" + this.keyStoreProvider + "\""); 
    paramPrintWriter.println(";");
    paramPrintWriter.println();
  }
  
  private void writeStorePassURL(PrintWriter paramPrintWriter) {
    paramPrintWriter.print("keystorePasswordURL \"");
    paramPrintWriter.print(this.storePassURL);
    paramPrintWriter.print('"');
    paramPrintWriter.println(";");
    paramPrintWriter.println();
  }
  
  private GrantEntry parseGrantEntry() throws ParsingException, IOException {
    GrantEntry grantEntry = new GrantEntry();
    LinkedList linkedList = null;
    boolean bool = false;
    match("grant");
    while (!peek("{")) {
      if (peekAndMatch("Codebase")) {
        if (grantEntry.codeBase != null)
          throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("multiple.Codebase.expressions")); 
        grantEntry.codeBase = match("quoted string");
        peekAndMatch(",");
        continue;
      } 
      if (peekAndMatch("SignedBy")) {
        if (grantEntry.signedBy != null)
          throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("multiple.SignedBy.expressions")); 
        grantEntry.signedBy = match("quoted string");
        StringTokenizer stringTokenizer = new StringTokenizer(grantEntry.signedBy, ",", true);
        byte b1 = 0;
        byte b2 = 0;
        while (stringTokenizer.hasMoreTokens()) {
          String str = stringTokenizer.nextToken().trim();
          if (str.equals(",")) {
            b2++;
            continue;
          } 
          if (str.length() > 0)
            b1++; 
        } 
        if (b1 <= b2)
          throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("SignedBy.has.empty.alias")); 
        peekAndMatch(",");
        continue;
      } 
      if (peekAndMatch("Principal")) {
        String str2;
        String str1;
        if (linkedList == null)
          linkedList = new LinkedList(); 
        if (peek("\"")) {
          str1 = "PolicyParser.REPLACE_NAME";
          str2 = match("principal type");
        } else {
          if (peek("*")) {
            match("*");
            str1 = "WILDCARD_PRINCIPAL_CLASS";
          } else {
            str1 = match("principal type");
          } 
          if (peek("*")) {
            match("*");
            str2 = "WILDCARD_PRINCIPAL_NAME";
          } else {
            str2 = match("quoted string");
          } 
          if (str1.equals("WILDCARD_PRINCIPAL_CLASS") && !str2.equals("WILDCARD_PRINCIPAL_NAME")) {
            if (debug != null)
              debug.println("disallowing principal that has WILDCARD class but no WILDCARD name"); 
            throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name"));
          } 
        } 
        try {
          str2 = expand(str2);
          if (str1.equals("javax.security.auth.x500.X500Principal") && !str2.equals("WILDCARD_PRINCIPAL_NAME")) {
            X500Principal x500Principal = new X500Principal((new X500Principal(str2)).toString());
            str2 = x500Principal.getName();
          } 
          linkedList.add(new PrincipalEntry(str1, str2));
        } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
          if (debug != null)
            debug.println("principal name expansion failed: " + str2); 
          bool = true;
        } 
        peekAndMatch(",");
        continue;
      } 
      throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.codeBase.or.SignedBy.or.Principal"));
    } 
    if (linkedList != null)
      grantEntry.principals = linkedList; 
    match("{");
    while (!peek("}")) {
      if (peek("Permission")) {
        try {
          PermissionEntry permissionEntry = parsePermissionEntry();
          grantEntry.add(permissionEntry);
        } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
          if (debug != null)
            debug.println(expandException.toString()); 
          skipEntry();
        } 
        match(";");
        continue;
      } 
      throw new ParsingException(this.st.lineno(), ResourcesMgr.getString("expected.permission.entry"));
    } 
    match("}");
    try {
      if (grantEntry.signedBy != null)
        grantEntry.signedBy = expand(grantEntry.signedBy); 
      if (grantEntry.codeBase != null) {
        if (grantEntry.codeBase.equals("${java.ext.dirs}"))
          grantEntry.codeBase = "${{java.ext.dirs}}"; 
        int i;
        if ((i = grantEntry.codeBase.indexOf("${{java.ext.dirs}}")) < 0) {
          grantEntry.codeBase = expand(grantEntry.codeBase, true).replace(File.separatorChar, '/');
        } else {
          String[] arrayOfString = parseExtDirs(grantEntry.codeBase, i);
          if (arrayOfString != null && arrayOfString.length > 0)
            for (byte b = 0; b < arrayOfString.length; b++) {
              GrantEntry grantEntry1 = (GrantEntry)grantEntry.clone();
              grantEntry1.codeBase = arrayOfString[b];
              add(grantEntry1);
              if (debug != null)
                debug.println("creating policy entry for expanded java.ext.dirs path:\n\t\t" + arrayOfString[b]); 
            }  
          bool = true;
        } 
      } 
    } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
      if (debug != null)
        debug.println(expandException.toString()); 
      return null;
    } 
    return (bool == true) ? null : grantEntry;
  }
  
  private PermissionEntry parsePermissionEntry() throws ParsingException, IOException, PropertyExpander.ExpandException {
    PermissionEntry permissionEntry = new PermissionEntry();
    match("Permission");
    permissionEntry.permission = match("permission type");
    if (peek("\""))
      permissionEntry.name = expand(match("quoted string")); 
    if (!peek(","))
      return permissionEntry; 
    match(",");
    if (peek("\"")) {
      permissionEntry.action = expand(match("quoted string"));
      if (!peek(","))
        return permissionEntry; 
      match(",");
    } 
    if (peekAndMatch("SignedBy"))
      permissionEntry.signedBy = expand(match("quoted string")); 
    return permissionEntry;
  }
  
  private DomainEntry parseDomainEntry() throws ParsingException, IOException {
    boolean bool = false;
    String str = null;
    Map map = new HashMap();
    match("domain");
    str = match("domain name");
    while (!peek("{"))
      map = parseProperties("{"); 
    match("{");
    DomainEntry domainEntry = new DomainEntry(str, map);
    while (!peek("}")) {
      match("keystore");
      str = match("keystore name");
      if (!peek("}"))
        map = parseProperties(";"); 
      match(";");
      domainEntry.add(new KeyStoreEntry(str, map));
    } 
    match("}");
    return (bool == true) ? null : domainEntry;
  }
  
  private Map<String, String> parseProperties(String paramString) throws ParsingException, IOException {
    HashMap hashMap = new HashMap();
    while (!peek(paramString)) {
      String str2;
      String str1 = match("property name");
      match("=");
      try {
        str2 = expand(match("quoted string"));
      } catch (sun.security.util.PropertyExpander.ExpandException expandException) {
        throw new IOException(expandException.getLocalizedMessage());
      } 
      hashMap.put(str1.toLowerCase(Locale.ENGLISH), str2);
    } 
    return hashMap;
  }
  
  static String[] parseExtDirs(String paramString, int paramInt) {
    String str1 = System.getProperty("java.ext.dirs");
    String str2 = (paramInt > 0) ? paramString.substring(0, paramInt) : "file:";
    int i = paramInt + "${{java.ext.dirs}}".length();
    String str3 = (i < paramString.length()) ? paramString.substring(i) : (String)null;
    String[] arrayOfString = null;
    if (str1 != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str1, File.pathSeparator);
      int j = stringTokenizer.countTokens();
      arrayOfString = new String[j];
      for (byte b = 0; b < j; b++) {
        File file = new File(stringTokenizer.nextToken());
        arrayOfString[b] = ParseUtil.encodePath(file.getAbsolutePath());
        if (!arrayOfString[b].startsWith("/"))
          arrayOfString[b] = "/" + arrayOfString[b]; 
        String str = (str3 == null) ? (arrayOfString[b].endsWith("/") ? "*" : "/*") : str3;
        arrayOfString[b] = str2 + arrayOfString[b] + str;
      } 
    } 
    return arrayOfString;
  }
  
  private boolean peekAndMatch(String paramString) throws ParsingException, IOException {
    if (peek(paramString)) {
      match(paramString);
      return true;
    } 
    return false;
  }
  
  private boolean peek(String paramString) throws ParsingException, IOException {
    boolean bool = false;
    switch (this.lookahead) {
      case -3:
        if (paramString.equalsIgnoreCase(this.st.sval))
          bool = true; 
        break;
      case 44:
        if (paramString.equalsIgnoreCase(","))
          bool = true; 
        break;
      case 123:
        if (paramString.equalsIgnoreCase("{"))
          bool = true; 
        break;
      case 125:
        if (paramString.equalsIgnoreCase("}"))
          bool = true; 
        break;
      case 34:
        if (paramString.equalsIgnoreCase("\""))
          bool = true; 
        break;
      case 42:
        if (paramString.equalsIgnoreCase("*"))
          bool = true; 
        break;
      case 59:
        if (paramString.equalsIgnoreCase(";"))
          bool = true; 
        break;
    } 
    return bool;
  }
  
  private String match(String paramString) throws PropertyExpander.ExpandException {
    Object[] arrayOfObject;
    MessageFormat messageFormat;
    String str = null;
    switch (this.lookahead) {
      case -2:
        throw new ParsingException(this.st.lineno(), paramString, ResourcesMgr.getString("number.") + String.valueOf(this.st.nval));
      case -1:
        messageFormat = new MessageFormat(ResourcesMgr.getString("expected.expect.read.end.of.file."));
        arrayOfObject = new Object[] { paramString };
        throw new ParsingException(messageFormat.format(arrayOfObject));
      case -3:
        if (paramString.equalsIgnoreCase(this.st.sval)) {
          this.lookahead = this.st.nextToken();
        } else if (paramString.equalsIgnoreCase("permission type")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else if (paramString.equalsIgnoreCase("principal type")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else if (paramString.equalsIgnoreCase("domain name") || paramString.equalsIgnoreCase("keystore name") || paramString.equalsIgnoreCase("property name")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, this.st.sval);
        } 
        return str;
      case 34:
        if (paramString.equalsIgnoreCase("quoted string")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else if (paramString.equalsIgnoreCase("permission type")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else if (paramString.equalsIgnoreCase("principal type")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, this.st.sval);
        } 
        return str;
      case 44:
        if (paramString.equalsIgnoreCase(",")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, ",");
        } 
        return str;
      case 123:
        if (paramString.equalsIgnoreCase("{")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, "{");
        } 
        return str;
      case 125:
        if (paramString.equalsIgnoreCase("}")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, "}");
        } 
        return str;
      case 59:
        if (paramString.equalsIgnoreCase(";")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, ";");
        } 
        return str;
      case 42:
        if (paramString.equalsIgnoreCase("*")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, "*");
        } 
        return str;
      case 61:
        if (paramString.equalsIgnoreCase("=")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, "=");
        } 
        return str;
    } 
    throw new ParsingException(this.st.lineno(), paramString, new String(new char[] { (char)this.lookahead }));
  }
  
  private void skipEntry() {
    while (this.lookahead != 59) {
      switch (this.lookahead) {
        case -2:
          throw new ParsingException(this.st.lineno(), ";", ResourcesMgr.getString("number.") + String.valueOf(this.st.nval));
        case -1:
          throw new ParsingException(ResourcesMgr.getString("expected.read.end.of.file."));
      } 
      this.lookahead = this.st.nextToken();
    } 
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    try(FileReader null = new FileReader(paramArrayOfString[0]); FileWriter null = new FileWriter(paramArrayOfString[1])) {
      policyParser = new PolicyParser(true);
      policyParser.read(fileReader);
      policyParser.write(fileWriter);
    } 
  }
  
  static class DomainEntry {
    private final String name;
    
    private final Map<String, String> properties;
    
    private final Map<String, PolicyParser.KeyStoreEntry> entries;
    
    DomainEntry(String param1String, Map<String, String> param1Map) {
      this.name = param1String;
      this.properties = param1Map;
      this.entries = new HashMap();
    }
    
    String getName() { return this.name; }
    
    Map<String, String> getProperties() { return this.properties; }
    
    Collection<PolicyParser.KeyStoreEntry> getEntries() { return this.entries.values(); }
    
    void add(PolicyParser.KeyStoreEntry param1KeyStoreEntry) throws PolicyParser.ParsingException {
      String str = param1KeyStoreEntry.getName();
      if (!this.entries.containsKey(str)) {
        this.entries.put(str, param1KeyStoreEntry);
      } else {
        MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("duplicate.keystore.name"));
        Object[] arrayOfObject = { str };
        throw new PolicyParser.ParsingException(messageFormat.format(arrayOfObject));
      } 
    }
    
    public String toString() {
      StringBuilder stringBuilder = (new StringBuilder("\ndomain ")).append(this.name);
      if (this.properties != null)
        for (Map.Entry entry : this.properties.entrySet())
          stringBuilder.append("\n        ").append((String)entry.getKey()).append('=').append((String)entry.getValue());  
      stringBuilder.append(" {\n");
      if (this.entries != null)
        for (PolicyParser.KeyStoreEntry keyStoreEntry : this.entries.values())
          stringBuilder.append(keyStoreEntry).append("\n");  
      stringBuilder.append("}");
      return stringBuilder.toString();
    }
  }
  
  public static class GrantEntry {
    public String signedBy;
    
    public String codeBase;
    
    public LinkedList<PolicyParser.PrincipalEntry> principals;
    
    public Vector<PolicyParser.PermissionEntry> permissionEntries;
    
    public GrantEntry() {
      this.principals = new LinkedList();
      this.permissionEntries = new Vector();
    }
    
    public GrantEntry(String param1String1, String param1String2) {
      this.codeBase = param1String2;
      this.signedBy = param1String1;
      this.principals = new LinkedList();
      this.permissionEntries = new Vector();
    }
    
    public void add(PolicyParser.PermissionEntry param1PermissionEntry) { this.permissionEntries.addElement(param1PermissionEntry); }
    
    public boolean remove(PolicyParser.PrincipalEntry param1PrincipalEntry) { return this.principals.remove(param1PrincipalEntry); }
    
    public boolean remove(PolicyParser.PermissionEntry param1PermissionEntry) { return this.permissionEntries.removeElement(param1PermissionEntry); }
    
    public boolean contains(PolicyParser.PrincipalEntry param1PrincipalEntry) { return this.principals.contains(param1PrincipalEntry); }
    
    public boolean contains(PolicyParser.PermissionEntry param1PermissionEntry) { return this.permissionEntries.contains(param1PermissionEntry); }
    
    public Enumeration<PolicyParser.PermissionEntry> permissionElements() { return this.permissionEntries.elements(); }
    
    public void write(PrintWriter param1PrintWriter) {
      param1PrintWriter.print("grant");
      if (this.signedBy != null) {
        param1PrintWriter.print(" signedBy \"");
        param1PrintWriter.print(this.signedBy);
        param1PrintWriter.print('"');
        if (this.codeBase != null)
          param1PrintWriter.print(", "); 
      } 
      if (this.codeBase != null) {
        param1PrintWriter.print(" codeBase \"");
        param1PrintWriter.print(this.codeBase);
        param1PrintWriter.print('"');
        if (this.principals != null && this.principals.size() > 0)
          param1PrintWriter.print(",\n"); 
      } 
      if (this.principals != null && this.principals.size() > 0) {
        Iterator iterator = this.principals.iterator();
        while (iterator.hasNext()) {
          param1PrintWriter.print("      ");
          PolicyParser.PrincipalEntry principalEntry = (PolicyParser.PrincipalEntry)iterator.next();
          principalEntry.write(param1PrintWriter);
          if (iterator.hasNext())
            param1PrintWriter.print(",\n"); 
        } 
      } 
      param1PrintWriter.println(" {");
      Enumeration enumeration = this.permissionEntries.elements();
      while (enumeration.hasMoreElements()) {
        PolicyParser.PermissionEntry permissionEntry = (PolicyParser.PermissionEntry)enumeration.nextElement();
        param1PrintWriter.write("  ");
        permissionEntry.write(param1PrintWriter);
      } 
      param1PrintWriter.println("};");
    }
    
    public Object clone() {
      GrantEntry grantEntry = new GrantEntry();
      grantEntry.codeBase = this.codeBase;
      grantEntry.signedBy = this.signedBy;
      grantEntry.principals = new LinkedList(this.principals);
      grantEntry.permissionEntries = new Vector(this.permissionEntries);
      return grantEntry;
    }
  }
  
  static class KeyStoreEntry {
    private final String name;
    
    private final Map<String, String> properties;
    
    KeyStoreEntry(String param1String, Map<String, String> param1Map) {
      this.name = param1String;
      this.properties = param1Map;
    }
    
    String getName() { return this.name; }
    
    Map<String, String> getProperties() { return this.properties; }
    
    public String toString() {
      StringBuilder stringBuilder = (new StringBuilder("\n    keystore ")).append(this.name);
      if (this.properties != null)
        for (Map.Entry entry : this.properties.entrySet())
          stringBuilder.append("\n        ").append((String)entry.getKey()).append('=').append((String)entry.getValue());  
      stringBuilder.append(";");
      return stringBuilder.toString();
    }
  }
  
  public static class ParsingException extends GeneralSecurityException {
    private static final long serialVersionUID = -4330692689482574072L;
    
    private String i18nMessage;
    
    public ParsingException(String param1String) {
      super(param1String);
      this.i18nMessage = param1String;
    }
    
    public ParsingException(int param1Int, String param1String) {
      super("line " + param1Int + ": " + param1String);
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("line.number.msg"));
      Object[] arrayOfObject = { new Integer(param1Int), param1String };
      this.i18nMessage = messageFormat.format(arrayOfObject);
    }
    
    public ParsingException(int param1Int, String param1String1, String param1String2) {
      super("line " + param1Int + ": expected [" + param1String1 + "], found [" + param1String2 + "]");
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("line.number.expected.expect.found.actual."));
      Object[] arrayOfObject = { new Integer(param1Int), param1String1, param1String2 };
      this.i18nMessage = messageFormat.format(arrayOfObject);
    }
    
    public String getLocalizedMessage() { return this.i18nMessage; }
  }
  
  public static class PermissionEntry {
    public String permission;
    
    public String name;
    
    public String action;
    
    public String signedBy;
    
    public PermissionEntry() {}
    
    public PermissionEntry(String param1String1, String param1String2, String param1String3) {
      this.permission = param1String1;
      this.name = param1String2;
      this.action = param1String3;
    }
    
    public int hashCode() {
      int i = this.permission.hashCode();
      if (this.name != null)
        i ^= this.name.hashCode(); 
      if (this.action != null)
        i ^= this.action.hashCode(); 
      return i;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof PermissionEntry))
        return false; 
      PermissionEntry permissionEntry = (PermissionEntry)param1Object;
      if (this.permission == null) {
        if (permissionEntry.permission != null)
          return false; 
      } else if (!this.permission.equals(permissionEntry.permission)) {
        return false;
      } 
      if (this.name == null) {
        if (permissionEntry.name != null)
          return false; 
      } else if (!this.name.equals(permissionEntry.name)) {
        return false;
      } 
      if (this.action == null) {
        if (permissionEntry.action != null)
          return false; 
      } else if (!this.action.equals(permissionEntry.action)) {
        return false;
      } 
      if (this.signedBy == null) {
        if (permissionEntry.signedBy != null)
          return false; 
      } else if (!this.signedBy.equals(permissionEntry.signedBy)) {
        return false;
      } 
      return true;
    }
    
    public void write(PrintWriter param1PrintWriter) {
      param1PrintWriter.print("permission ");
      param1PrintWriter.print(this.permission);
      if (this.name != null) {
        param1PrintWriter.print(" \"");
        param1PrintWriter.print(this.name.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\\\""));
        param1PrintWriter.print('"');
      } 
      if (this.action != null) {
        param1PrintWriter.print(", \"");
        param1PrintWriter.print(this.action);
        param1PrintWriter.print('"');
      } 
      if (this.signedBy != null) {
        param1PrintWriter.print(", signedBy \"");
        param1PrintWriter.print(this.signedBy);
        param1PrintWriter.print('"');
      } 
      param1PrintWriter.println(";");
    }
  }
  
  public static class PrincipalEntry implements Principal {
    public static final String WILDCARD_CLASS = "WILDCARD_PRINCIPAL_CLASS";
    
    public static final String WILDCARD_NAME = "WILDCARD_PRINCIPAL_NAME";
    
    public static final String REPLACE_NAME = "PolicyParser.REPLACE_NAME";
    
    String principalClass;
    
    String principalName;
    
    public PrincipalEntry(String param1String1, String param1String2) {
      if (param1String1 == null || param1String2 == null)
        throw new NullPointerException(ResourcesMgr.getString("null.principalClass.or.principalName")); 
      this.principalClass = param1String1;
      this.principalName = param1String2;
    }
    
    boolean isWildcardName() { return this.principalName.equals("WILDCARD_PRINCIPAL_NAME"); }
    
    boolean isWildcardClass() { return this.principalClass.equals("WILDCARD_PRINCIPAL_CLASS"); }
    
    boolean isReplaceName() { return this.principalClass.equals("PolicyParser.REPLACE_NAME"); }
    
    public String getPrincipalClass() { return this.principalClass; }
    
    public String getPrincipalName() { return this.principalName; }
    
    public String getDisplayClass() { return isWildcardClass() ? "*" : (isReplaceName() ? "" : this.principalClass); }
    
    public String getDisplayName() { return getDisplayName(false); }
    
    public String getDisplayName(boolean param1Boolean) { return isWildcardName() ? "*" : (param1Boolean ? ("\"" + this.principalName + "\"") : this.principalName); }
    
    public String getName() { return this.principalName; }
    
    public String toString() { return !isReplaceName() ? (getDisplayClass() + "/" + getDisplayName()) : getDisplayName(); }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof PrincipalEntry))
        return false; 
      PrincipalEntry principalEntry = (PrincipalEntry)param1Object;
      return (this.principalClass.equals(principalEntry.principalClass) && this.principalName.equals(principalEntry.principalName));
    }
    
    public int hashCode() { return this.principalClass.hashCode(); }
    
    public void write(PrintWriter param1PrintWriter) { param1PrintWriter.print("principal " + getDisplayClass() + " " + getDisplayName(true)); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\PolicyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */