package java.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TimeZone;
import sun.misc.JavaNetHttpCookieAccess;
import sun.misc.SharedSecrets;

public final class HttpCookie implements Cloneable {
  private final String name;
  
  private String value;
  
  private String comment;
  
  private String commentURL;
  
  private boolean toDiscard;
  
  private String domain;
  
  private long maxAge = -1L;
  
  private String path;
  
  private String portlist;
  
  private boolean secure;
  
  private boolean httpOnly;
  
  private int version = 1;
  
  private final String header;
  
  private final long whenCreated;
  
  private static final long MAX_AGE_UNSPECIFIED = -1L;
  
  private static final String[] COOKIE_DATE_FORMATS = { "EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'", "EEE',' dd MMM yyyy HH:mm:ss 'GMT'", "EEE MMM dd yyyy HH:mm:ss 'GMT'Z", "EEE',' dd-MMM-yy HH:mm:ss 'GMT'", "EEE',' dd MMM yy HH:mm:ss 'GMT'", "EEE MMM dd yy HH:mm:ss 'GMT'Z" };
  
  private static final String SET_COOKIE = "set-cookie:";
  
  private static final String SET_COOKIE2 = "set-cookie2:";
  
  private static final String tspecials = ",; ";
  
  static final Map<String, CookieAttributeAssignor> assignors = new HashMap();
  
  static final TimeZone GMT;
  
  public HttpCookie(String paramString1, String paramString2) { this(paramString1, paramString2, null); }
  
  private HttpCookie(String paramString1, String paramString2, String paramString3) {
    paramString1 = paramString1.trim();
    if (paramString1.length() == 0 || !isToken(paramString1) || paramString1.charAt(0) == '$')
      throw new IllegalArgumentException("Illegal cookie name"); 
    this.name = paramString1;
    this.value = paramString2;
    this.toDiscard = false;
    this.secure = false;
    this.whenCreated = System.currentTimeMillis();
    this.portlist = null;
    this.header = paramString3;
  }
  
  public static List<HttpCookie> parse(String paramString) { return parse(paramString, false); }
  
  private static List<HttpCookie> parse(String paramString, boolean paramBoolean) {
    int i = guessCookieVersion(paramString);
    if (startsWithIgnoreCase(paramString, "set-cookie2:")) {
      paramString = paramString.substring("set-cookie2:".length());
    } else if (startsWithIgnoreCase(paramString, "set-cookie:")) {
      paramString = paramString.substring("set-cookie:".length());
    } 
    ArrayList arrayList = new ArrayList();
    if (i == 0) {
      HttpCookie httpCookie = parseInternal(paramString, paramBoolean);
      httpCookie.setVersion(0);
      arrayList.add(httpCookie);
    } else {
      List list = splitMultiCookies(paramString);
      for (String str : list) {
        HttpCookie httpCookie = parseInternal(str, paramBoolean);
        httpCookie.setVersion(1);
        arrayList.add(httpCookie);
      } 
    } 
    return arrayList;
  }
  
  public boolean hasExpired() {
    if (this.maxAge == 0L)
      return true; 
    if (this.maxAge == -1L)
      return false; 
    long l = (System.currentTimeMillis() - this.whenCreated) / 1000L;
    return (l > this.maxAge);
  }
  
  public void setComment(String paramString) { this.comment = paramString; }
  
  public String getComment() { return this.comment; }
  
  public void setCommentURL(String paramString) { this.commentURL = paramString; }
  
  public String getCommentURL() { return this.commentURL; }
  
  public void setDiscard(boolean paramBoolean) { this.toDiscard = paramBoolean; }
  
  public boolean getDiscard() { return this.toDiscard; }
  
  public void setPortlist(String paramString) { this.portlist = paramString; }
  
  public String getPortlist() { return this.portlist; }
  
  public void setDomain(String paramString) {
    if (paramString != null) {
      this.domain = paramString.toLowerCase();
    } else {
      this.domain = paramString;
    } 
  }
  
  public String getDomain() { return this.domain; }
  
  public void setMaxAge(long paramLong) { this.maxAge = paramLong; }
  
  public long getMaxAge() { return this.maxAge; }
  
  public void setPath(String paramString) { this.path = paramString; }
  
  public String getPath() { return this.path; }
  
  public void setSecure(boolean paramBoolean) { this.secure = paramBoolean; }
  
  public boolean getSecure() { return this.secure; }
  
  public String getName() { return this.name; }
  
  public void setValue(String paramString) { this.value = paramString; }
  
  public String getValue() { return this.value; }
  
  public int getVersion() { return this.version; }
  
  public void setVersion(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("cookie version should be 0 or 1"); 
    this.version = paramInt;
  }
  
  public boolean isHttpOnly() { return this.httpOnly; }
  
  public void setHttpOnly(boolean paramBoolean) { this.httpOnly = paramBoolean; }
  
  public static boolean domainMatches(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return false; 
    boolean bool = ".local".equalsIgnoreCase(paramString1);
    int i = paramString1.indexOf('.');
    if (i == 0)
      i = paramString1.indexOf('.', 1); 
    if (!bool && (i == -1 || i == paramString1.length() - 1))
      return false; 
    int j = paramString2.indexOf('.');
    if (j == -1 && (bool || paramString1.equalsIgnoreCase(paramString2 + ".local")))
      return true; 
    int k = paramString1.length();
    int m = paramString2.length() - k;
    if (m == 0)
      return paramString2.equalsIgnoreCase(paramString1); 
    if (m > 0) {
      String str1 = paramString2.substring(0, m);
      String str2 = paramString2.substring(m);
      return (str1.indexOf('.') == -1 && str2.equalsIgnoreCase(paramString1));
    } 
    return (m == -1) ? ((paramString1.charAt(0) == '.' && paramString2.equalsIgnoreCase(paramString1.substring(1)))) : false;
  }
  
  public String toString() { return (getVersion() > 0) ? toRFC2965HeaderString() : toNetscapeHeaderString(); }
  
  public boolean equals(Object paramObject) {
    HttpCookie httpCookie;
    return (paramObject == this) ? true : (!(paramObject instanceof HttpCookie) ? false : (((httpCookie = (HttpCookie)paramObject).equalsIgnoreCase(getName(), httpCookie.getName()) && equalsIgnoreCase(getDomain(), httpCookie.getDomain()) && Objects.equals(getPath(), httpCookie.getPath()))));
  }
  
  public int hashCode() {
    int i = this.name.toLowerCase().hashCode();
    int j = (this.domain != null) ? this.domain.toLowerCase().hashCode() : 0;
    int k = (this.path != null) ? this.path.hashCode() : 0;
    return i + j + k;
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new RuntimeException(cloneNotSupportedException.getMessage());
    } 
  }
  
  private static boolean isToken(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c < ' ' || c >= '' || ",; ".indexOf(c) != -1)
        return false; 
    } 
    return true;
  }
  
  private static HttpCookie parseInternal(String paramString, boolean paramBoolean) {
    HttpCookie httpCookie = null;
    String str = null;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ";");
    try {
      str = stringTokenizer.nextToken();
      int i = str.indexOf('=');
      if (i != -1) {
        String str1 = str.substring(0, i).trim();
        String str2 = str.substring(i + 1).trim();
        if (paramBoolean) {
          httpCookie = new HttpCookie(str1, stripOffSurroundingQuote(str2), paramString);
        } else {
          httpCookie = new HttpCookie(str1, stripOffSurroundingQuote(str2));
        } 
      } else {
        throw new IllegalArgumentException("Invalid cookie name-value pair");
      } 
    } catch (NoSuchElementException noSuchElementException) {
      throw new IllegalArgumentException("Empty cookie header string");
    } 
    while (stringTokenizer.hasMoreTokens()) {
      String str2;
      String str1;
      str = stringTokenizer.nextToken();
      int i = str.indexOf('=');
      if (i != -1) {
        str1 = str.substring(0, i).trim();
        str2 = str.substring(i + 1).trim();
      } else {
        str1 = str.trim();
        str2 = null;
      } 
      assignAttribute(httpCookie, str1, str2);
    } 
    return httpCookie;
  }
  
  private static void assignAttribute(HttpCookie paramHttpCookie, String paramString1, String paramString2) {
    paramString2 = stripOffSurroundingQuote(paramString2);
    CookieAttributeAssignor cookieAttributeAssignor = (CookieAttributeAssignor)assignors.get(paramString1.toLowerCase());
    if (cookieAttributeAssignor != null)
      cookieAttributeAssignor.assign(paramHttpCookie, paramString1, paramString2); 
  }
  
  private String header() { return this.header; }
  
  private String toNetscapeHeaderString() { return getName() + "=" + getValue(); }
  
  private String toRFC2965HeaderString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getName()).append("=\"").append(getValue()).append('"');
    if (getPath() != null)
      stringBuilder.append(";$Path=\"").append(getPath()).append('"'); 
    if (getDomain() != null)
      stringBuilder.append(";$Domain=\"").append(getDomain()).append('"'); 
    if (getPortlist() != null)
      stringBuilder.append(";$Port=\"").append(getPortlist()).append('"'); 
    return stringBuilder.toString();
  }
  
  private long expiryDate2DeltaSeconds(String paramString) {
    GregorianCalendar gregorianCalendar = new GregorianCalendar(GMT);
    byte b = 0;
    while (b < COOKIE_DATE_FORMATS.length) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COOKIE_DATE_FORMATS[b], Locale.US);
      gregorianCalendar.set(1970, 0, 1, 0, 0, 0);
      simpleDateFormat.setTimeZone(GMT);
      simpleDateFormat.setLenient(false);
      simpleDateFormat.set2DigitYearStart(gregorianCalendar.getTime());
      try {
        gregorianCalendar.setTime(simpleDateFormat.parse(paramString));
        if (!COOKIE_DATE_FORMATS[b].contains("yyyy")) {
          int i = gregorianCalendar.get(1);
          i %= 100;
          if (i < 70) {
            i += 2000;
          } else {
            i += 1900;
          } 
          gregorianCalendar.set(1, i);
        } 
        return (gregorianCalendar.getTimeInMillis() - this.whenCreated) / 1000L;
      } catch (Exception exception) {
        b++;
      } 
    } 
    return 0L;
  }
  
  private static int guessCookieVersion(String paramString) {
    byte b = 0;
    paramString = paramString.toLowerCase();
    if (paramString.indexOf("expires=") != -1) {
      b = 0;
    } else if (paramString.indexOf("version=") != -1) {
      b = 1;
    } else if (paramString.indexOf("max-age") != -1) {
      b = 1;
    } else if (startsWithIgnoreCase(paramString, "set-cookie2:")) {
      b = 1;
    } 
    return b;
  }
  
  private static String stripOffSurroundingQuote(String paramString) { return (paramString != null && paramString.length() > 2 && paramString.charAt(0) == '"' && paramString.charAt(paramString.length() - 1) == '"') ? paramString.substring(1, paramString.length() - 1) : ((paramString != null && paramString.length() > 2 && paramString.charAt(0) == '\'' && paramString.charAt(paramString.length() - 1) == '\'') ? paramString.substring(1, paramString.length() - 1) : paramString); }
  
  private static boolean equalsIgnoreCase(String paramString1, String paramString2) { return (paramString1 == paramString2) ? true : ((paramString1 != null && paramString2 != null) ? paramString1.equalsIgnoreCase(paramString2) : 0); }
  
  private static boolean startsWithIgnoreCase(String paramString1, String paramString2) { return (paramString1 == null || paramString2 == null) ? false : ((paramString1.length() >= paramString2.length() && paramString2.equalsIgnoreCase(paramString1.substring(0, paramString2.length())))); }
  
  private static List<String> splitMultiCookies(String paramString) {
    ArrayList arrayList = new ArrayList();
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    while (b2 < paramString.length()) {
      char c = paramString.charAt(b2);
      if (c == '"')
        b1++; 
      if (c == ',' && b1 % 2 == 0) {
        arrayList.add(paramString.substring(b3, b2));
        b3 = b2 + 1;
      } 
      b2++;
    } 
    arrayList.add(paramString.substring(b3));
    return arrayList;
  }
  
  static  {
    assignors.put("comment", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) {
            if (param1HttpCookie.getComment() == null)
              param1HttpCookie.setComment(param1String2); 
          }
        });
    assignors.put("commenturl", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) {
            if (param1HttpCookie.getCommentURL() == null)
              param1HttpCookie.setCommentURL(param1String2); 
          }
        });
    assignors.put("discard", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) { param1HttpCookie.setDiscard(true); }
        });
    assignors.put("domain", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) {
            if (param1HttpCookie.getDomain() == null)
              param1HttpCookie.setDomain(param1String2); 
          }
        });
    assignors.put("max-age", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) {
            try {
              long l = Long.parseLong(param1String2);
              if (param1HttpCookie.getMaxAge() == -1L)
                param1HttpCookie.setMaxAge(l); 
            } catch (NumberFormatException numberFormatException) {
              throw new IllegalArgumentException("Illegal cookie max-age attribute");
            } 
          }
        });
    assignors.put("path", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) {
            if (param1HttpCookie.getPath() == null)
              param1HttpCookie.setPath(param1String2); 
          }
        });
    assignors.put("port", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) {
            if (param1HttpCookie.getPortlist() == null)
              param1HttpCookie.setPortlist((param1String2 == null) ? "" : param1String2); 
          }
        });
    assignors.put("secure", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) { param1HttpCookie.setSecure(true); }
        });
    assignors.put("httponly", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) { param1HttpCookie.setHttpOnly(true); }
        });
    assignors.put("version", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) {
            try {
              int i = Integer.parseInt(param1String2);
              param1HttpCookie.setVersion(i);
            } catch (NumberFormatException numberFormatException) {}
          }
        });
    assignors.put("expires", new CookieAttributeAssignor() {
          public void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2) {
            if (param1HttpCookie.getMaxAge() == -1L)
              param1HttpCookie.setMaxAge(param1HttpCookie.expiryDate2DeltaSeconds(param1String2)); 
          }
        });
    SharedSecrets.setJavaNetHttpCookieAccess(new JavaNetHttpCookieAccess() {
          public List<HttpCookie> parse(String param1String) { return HttpCookie.parse(param1String, true); }
          
          public String header(HttpCookie param1HttpCookie) { return param1HttpCookie.header; }
        });
    GMT = TimeZone.getTimeZone("GMT");
  }
  
  static interface CookieAttributeAssignor {
    void assign(HttpCookie param1HttpCookie, String param1String1, String param1String2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\HttpCookie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */