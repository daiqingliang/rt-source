package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class URLPermission extends Permission {
  private static final long serialVersionUID = -2702463814894478682L;
  
  private String scheme;
  
  private String ssp;
  
  private String path;
  
  private List<String> methods;
  
  private List<String> requestHeaders;
  
  private Authority authority;
  
  private String actions;
  
  public URLPermission(String paramString1, String paramString2) {
    super(paramString1);
    init(paramString2);
  }
  
  private void init(String paramString) {
    String str2;
    String str1;
    parseURI(getName());
    int i = paramString.indexOf(':');
    if (paramString.lastIndexOf(':') != i)
      throw new IllegalArgumentException("Invalid actions string: \"" + paramString + "\""); 
    if (i == -1) {
      str1 = paramString;
      str2 = "";
    } else {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    } 
    List list = normalizeMethods(str1);
    Collections.sort(list);
    this.methods = Collections.unmodifiableList(list);
    list = normalizeHeaders(str2);
    Collections.sort(list);
    this.requestHeaders = Collections.unmodifiableList(list);
    this.actions = actions();
  }
  
  public URLPermission(String paramString) { this(paramString, "*:*"); }
  
  public String getActions() { return this.actions; }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof URLPermission))
      return false; 
    URLPermission uRLPermission = (URLPermission)paramPermission;
    if (!((String)this.methods.get(0)).equals("*") && Collections.indexOfSubList(this.methods, uRLPermission.methods) == -1)
      return false; 
    if (this.requestHeaders.isEmpty() && !uRLPermission.requestHeaders.isEmpty())
      return false; 
    if (!this.requestHeaders.isEmpty() && !((String)this.requestHeaders.get(0)).equals("*") && Collections.indexOfSubList(this.requestHeaders, uRLPermission.requestHeaders) == -1)
      return false; 
    if (!this.scheme.equals(uRLPermission.scheme))
      return false; 
    if (this.ssp.equals("*"))
      return true; 
    if (!this.authority.implies(uRLPermission.authority))
      return false; 
    if (this.path == null)
      return (uRLPermission.path == null); 
    if (uRLPermission.path == null)
      return false; 
    if (this.path.endsWith("/-")) {
      String str = this.path.substring(0, this.path.length() - 1);
      return uRLPermission.path.startsWith(str);
    } 
    if (this.path.endsWith("/*")) {
      String str1 = this.path.substring(0, this.path.length() - 1);
      if (!uRLPermission.path.startsWith(str1))
        return false; 
      String str2 = uRLPermission.path.substring(str1.length());
      return (str2.indexOf('/') != -1) ? false : (!str2.equals("-"));
    } 
    return this.path.equals(uRLPermission.path);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof URLPermission))
      return false; 
    URLPermission uRLPermission = (URLPermission)paramObject;
    return !this.scheme.equals(uRLPermission.scheme) ? false : (!getActions().equals(uRLPermission.getActions()) ? false : (!this.authority.equals(uRLPermission.authority) ? false : ((this.path != null) ? this.path.equals(uRLPermission.path) : ((uRLPermission.path == null) ? 1 : 0))));
  }
  
  public int hashCode() { return getActions().hashCode() + this.scheme.hashCode() + this.authority.hashCode() + ((this.path == null) ? 0 : this.path.hashCode()); }
  
  private List<String> normalizeMethods(String paramString) {
    ArrayList arrayList = new ArrayList();
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == ',') {
        String str1 = stringBuilder.toString();
        if (str1.length() > 0)
          arrayList.add(str1); 
        stringBuilder = new StringBuilder();
      } else {
        if (c == ' ' || c == '\t')
          throw new IllegalArgumentException("White space not allowed in methods: \"" + paramString + "\""); 
        if (c >= 'a' && c <= 'z')
          c = (char)(c - ' '); 
        stringBuilder.append(c);
      } 
    } 
    String str = stringBuilder.toString();
    if (str.length() > 0)
      arrayList.add(str); 
    return arrayList;
  }
  
  private List<String> normalizeHeaders(String paramString) {
    ArrayList arrayList = new ArrayList();
    StringBuilder stringBuilder = new StringBuilder();
    boolean bool = true;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c >= 'a' && c <= 'z') {
        if (bool) {
          c = (char)(c - ' ');
          bool = false;
        } 
        stringBuilder.append(c);
      } else {
        if (c == ' ' || c == '\t')
          throw new IllegalArgumentException("White space not allowed in headers: \"" + paramString + "\""); 
        if (c == '-') {
          bool = true;
          stringBuilder.append(c);
        } else if (c == ',') {
          String str1 = stringBuilder.toString();
          if (str1.length() > 0)
            arrayList.add(str1); 
          stringBuilder = new StringBuilder();
          bool = true;
        } else {
          bool = false;
          stringBuilder.append(c);
        } 
      } 
    } 
    String str = stringBuilder.toString();
    if (str.length() > 0)
      arrayList.add(str); 
    return arrayList;
  }
  
  private void parseURI(String paramString) {
    String str2;
    int i = paramString.length();
    int j = paramString.indexOf(':');
    if (j == -1 || j + 1 == i)
      throw new IllegalArgumentException("Invalid URL string: \"" + paramString + "\""); 
    this.scheme = paramString.substring(0, j).toLowerCase();
    this.ssp = paramString.substring(j + 1);
    if (!this.ssp.startsWith("//")) {
      if (!this.ssp.equals("*"))
        throw new IllegalArgumentException("Invalid URL string: \"" + paramString + "\""); 
      this.authority = new Authority(this.scheme, "*");
      return;
    } 
    String str1 = this.ssp.substring(2);
    j = str1.indexOf('/');
    if (j == -1) {
      this.path = "";
      str2 = str1;
    } else {
      str2 = str1.substring(0, j);
      this.path = str1.substring(j);
    } 
    this.authority = new Authority(this.scheme, str2.toLowerCase());
  }
  
  private String actions() {
    StringBuilder stringBuilder = new StringBuilder();
    for (String str : this.methods)
      stringBuilder.append(str); 
    stringBuilder.append(":");
    for (String str : this.requestHeaders)
      stringBuilder.append(str); 
    return stringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String str = (String)getField.get("actions", null);
    init(str);
  }
  
  static class Authority {
    HostPortrange p;
    
    Authority(String param1String1, String param1String2) {
      int i = param1String2.indexOf('@');
      if (i == -1) {
        this.p = new HostPortrange(param1String1, param1String2);
      } else {
        this.p = new HostPortrange(param1String1, param1String2.substring(i + 1));
      } 
    }
    
    boolean implies(Authority param1Authority) { return (impliesHostrange(param1Authority) && impliesPortrange(param1Authority)); }
    
    private boolean impliesHostrange(Authority param1Authority) {
      String str1 = this.p.hostname();
      String str2 = param1Authority.p.hostname();
      return (this.p.wildcard() && str1.equals("")) ? true : ((param1Authority.p.wildcard() && str2.equals("")) ? false : (str1.equals(str2) ? true : (this.p.wildcard() ? str2.endsWith(str1) : 0)));
    }
    
    private boolean impliesPortrange(Authority param1Authority) {
      int[] arrayOfInt1 = this.p.portrange();
      int[] arrayOfInt2 = param1Authority.p.portrange();
      return (arrayOfInt1[0] == -1) ? true : ((arrayOfInt1[0] <= arrayOfInt2[0] && arrayOfInt1[1] >= arrayOfInt2[1]));
    }
    
    boolean equals(Authority param1Authority) { return this.p.equals(param1Authority.p); }
    
    public int hashCode() { return this.p.hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\URLPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */