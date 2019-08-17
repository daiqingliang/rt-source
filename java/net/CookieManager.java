package java.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.util.logging.PlatformLogger;

public class CookieManager extends CookieHandler {
  private CookiePolicy policyCallback;
  
  private CookieStore cookieJar = null;
  
  public CookieManager() { this(null, null); }
  
  public CookieManager(CookieStore paramCookieStore, CookiePolicy paramCookiePolicy) {
    this.policyCallback = (paramCookiePolicy == null) ? CookiePolicy.ACCEPT_ORIGINAL_SERVER : paramCookiePolicy;
    if (paramCookieStore == null) {
      this.cookieJar = new InMemoryCookieStore();
    } else {
      this.cookieJar = paramCookieStore;
    } 
  }
  
  public void setCookiePolicy(CookiePolicy paramCookiePolicy) {
    if (paramCookiePolicy != null)
      this.policyCallback = paramCookiePolicy; 
  }
  
  public CookieStore getCookieStore() { return this.cookieJar; }
  
  public Map<String, List<String>> get(URI paramURI, Map<String, List<String>> paramMap) throws IOException {
    if (paramURI == null || paramMap == null)
      throw new IllegalArgumentException("Argument is null"); 
    HashMap hashMap = new HashMap();
    if (this.cookieJar == null)
      return Collections.unmodifiableMap(hashMap); 
    boolean bool = "https".equalsIgnoreCase(paramURI.getScheme());
    ArrayList arrayList = new ArrayList();
    String str = paramURI.getPath();
    if (str == null || str.isEmpty())
      str = "/"; 
    for (HttpCookie httpCookie : this.cookieJar.get(paramURI)) {
      if (pathMatches(str, httpCookie.getPath()) && (bool || !httpCookie.getSecure())) {
        if (httpCookie.isHttpOnly()) {
          String str2 = paramURI.getScheme();
          if (!"http".equalsIgnoreCase(str2) && !"https".equalsIgnoreCase(str2))
            continue; 
        } 
        String str1 = httpCookie.getPortlist();
        if (str1 != null && !str1.isEmpty()) {
          int i = paramURI.getPort();
          if (i == -1)
            i = "https".equals(paramURI.getScheme()) ? 443 : 80; 
          if (isInPortList(str1, i))
            arrayList.add(httpCookie); 
          continue;
        } 
        arrayList.add(httpCookie);
      } 
    } 
    List list = sortByPath(arrayList);
    hashMap.put("Cookie", list);
    return Collections.unmodifiableMap(hashMap);
  }
  
  public void put(URI paramURI, Map<String, List<String>> paramMap) throws IOException {
    if (paramURI == null || paramMap == null)
      throw new IllegalArgumentException("Argument is null"); 
    if (this.cookieJar == null)
      return; 
    PlatformLogger platformLogger = PlatformLogger.getLogger("java.net.CookieManager");
    for (String str : paramMap.keySet()) {
      if (str == null || (!str.equalsIgnoreCase("Set-Cookie2") && !str.equalsIgnoreCase("Set-Cookie")))
        continue; 
      for (String str1 : (List)paramMap.get(str)) {
        try {
          List list;
          try {
            list = HttpCookie.parse(str1);
          } catch (IllegalArgumentException illegalArgumentException) {
            list = Collections.emptyList();
            if (platformLogger.isLoggable(PlatformLogger.Level.SEVERE))
              platformLogger.severe("Invalid cookie for " + paramURI + ": " + str1); 
          } 
          for (HttpCookie httpCookie : list) {
            if (httpCookie.getPath() == null) {
              String str3 = paramURI.getPath();
              if (!str3.endsWith("/")) {
                int i = str3.lastIndexOf("/");
                if (i > 0) {
                  str3 = str3.substring(0, i + 1);
                } else {
                  str3 = "/";
                } 
              } 
              httpCookie.setPath(str3);
            } 
            if (httpCookie.getDomain() == null) {
              String str3 = paramURI.getHost();
              if (str3 != null && !str3.contains("."))
                str3 = str3 + ".local"; 
              httpCookie.setDomain(str3);
            } 
            String str2 = httpCookie.getPortlist();
            if (str2 != null) {
              int i = paramURI.getPort();
              if (i == -1)
                i = "https".equals(paramURI.getScheme()) ? 443 : 80; 
              if (str2.isEmpty()) {
                httpCookie.setPortlist("" + i);
                if (shouldAcceptInternal(paramURI, httpCookie))
                  this.cookieJar.add(paramURI, httpCookie); 
                continue;
              } 
              if (isInPortList(str2, i) && shouldAcceptInternal(paramURI, httpCookie))
                this.cookieJar.add(paramURI, httpCookie); 
              continue;
            } 
            if (shouldAcceptInternal(paramURI, httpCookie))
              this.cookieJar.add(paramURI, httpCookie); 
          } 
        } catch (IllegalArgumentException illegalArgumentException) {}
      } 
    } 
  }
  
  private boolean shouldAcceptInternal(URI paramURI, HttpCookie paramHttpCookie) {
    try {
      return this.policyCallback.shouldAccept(paramURI, paramHttpCookie);
    } catch (Exception exception) {
      return false;
    } 
  }
  
  private static boolean isInPortList(String paramString, int paramInt) {
    int i = paramString.indexOf(",");
    int j = -1;
    while (i > 0) {
      try {
        j = Integer.parseInt(paramString.substring(0, i));
        if (j == paramInt)
          return true; 
      } catch (NumberFormatException numberFormatException) {}
      paramString = paramString.substring(i + 1);
      i = paramString.indexOf(",");
    } 
    if (!paramString.isEmpty())
      try {
        j = Integer.parseInt(paramString);
        if (j == paramInt)
          return true; 
      } catch (NumberFormatException numberFormatException) {} 
    return false;
  }
  
  private boolean pathMatches(String paramString1, String paramString2) { return (paramString1 == paramString2) ? true : ((paramString1 == null || paramString2 == null) ? false : (paramString1.startsWith(paramString2))); }
  
  private List<String> sortByPath(List<HttpCookie> paramList) {
    Collections.sort(paramList, new CookiePathComparator());
    ArrayList arrayList = new ArrayList();
    for (HttpCookie httpCookie : paramList) {
      if (paramList.indexOf(httpCookie) == 0 && httpCookie.getVersion() > 0)
        arrayList.add("$Version=\"1\""); 
      arrayList.add(httpCookie.toString());
    } 
    return arrayList;
  }
  
  static class CookiePathComparator extends Object implements Comparator<HttpCookie> {
    public int compare(HttpCookie param1HttpCookie1, HttpCookie param1HttpCookie2) { return (param1HttpCookie1 == param1HttpCookie2) ? 0 : ((param1HttpCookie1 == null) ? -1 : ((param1HttpCookie2 == null) ? 1 : (!param1HttpCookie1.getName().equals(param1HttpCookie2.getName()) ? 0 : (param1HttpCookie1.getPath().startsWith(param1HttpCookie2.getPath()) ? -1 : (param1HttpCookie2.getPath().startsWith(param1HttpCookie1.getPath()) ? 1 : 0))))); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\CookieManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */