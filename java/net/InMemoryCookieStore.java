package java.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class InMemoryCookieStore implements CookieStore {
  private List<HttpCookie> cookieJar = null;
  
  private Map<String, List<HttpCookie>> domainIndex = null;
  
  private Map<URI, List<HttpCookie>> uriIndex = null;
  
  private ReentrantLock lock = null;
  
  public InMemoryCookieStore() {
    this.cookieJar = new ArrayList();
    this.domainIndex = new HashMap();
    this.uriIndex = new HashMap();
    this.lock = new ReentrantLock(false);
  }
  
  public void add(URI paramURI, HttpCookie paramHttpCookie) {
    if (paramHttpCookie == null)
      throw new NullPointerException("cookie is null"); 
    this.lock.lock();
    try {
      this.cookieJar.remove(paramHttpCookie);
      if (paramHttpCookie.getMaxAge() != 0L) {
        this.cookieJar.add(paramHttpCookie);
        if (paramHttpCookie.getDomain() != null)
          addIndex(this.domainIndex, paramHttpCookie.getDomain(), paramHttpCookie); 
        if (paramURI != null)
          addIndex(this.uriIndex, getEffectiveURI(paramURI), paramHttpCookie); 
      } 
    } finally {
      this.lock.unlock();
    } 
  }
  
  public List<HttpCookie> get(URI paramURI) {
    if (paramURI == null)
      throw new NullPointerException("uri is null"); 
    ArrayList arrayList = new ArrayList();
    boolean bool = "https".equalsIgnoreCase(paramURI.getScheme());
    this.lock.lock();
    try {
      getInternal1(arrayList, this.domainIndex, paramURI.getHost(), bool);
      getInternal2(arrayList, this.uriIndex, getEffectiveURI(paramURI), bool);
    } finally {
      this.lock.unlock();
    } 
    return arrayList;
  }
  
  public List<HttpCookie> getCookies() {
    this.lock.lock();
    try {
      Iterator iterator = this.cookieJar.iterator();
      while (iterator.hasNext()) {
        if (((HttpCookie)iterator.next()).hasExpired())
          iterator.remove(); 
      } 
    } finally {
      list = Collections.unmodifiableList(this.cookieJar);
      this.lock.unlock();
    } 
    return list;
  }
  
  public List<URI> getURIs() {
    arrayList = new ArrayList();
    this.lock.lock();
    try {
      Iterator iterator = this.uriIndex.keySet().iterator();
      while (iterator.hasNext()) {
        URI uRI = (URI)iterator.next();
        List list = (List)this.uriIndex.get(uRI);
        if (list == null || list.size() == 0)
          iterator.remove(); 
      } 
    } finally {
      arrayList.addAll(this.uriIndex.keySet());
      this.lock.unlock();
    } 
    return arrayList;
  }
  
  public boolean remove(URI paramURI, HttpCookie paramHttpCookie) {
    if (paramHttpCookie == null)
      throw new NullPointerException("cookie is null"); 
    boolean bool = false;
    this.lock.lock();
    try {
      bool = this.cookieJar.remove(paramHttpCookie);
    } finally {
      this.lock.unlock();
    } 
    return bool;
  }
  
  public boolean removeAll() {
    this.lock.lock();
    try {
      if (this.cookieJar.isEmpty())
        return false; 
      this.cookieJar.clear();
      this.domainIndex.clear();
      this.uriIndex.clear();
    } finally {
      this.lock.unlock();
    } 
    return true;
  }
  
  private boolean netscapeDomainMatches(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return false; 
    boolean bool = ".local".equalsIgnoreCase(paramString1);
    int i = paramString1.indexOf('.');
    if (i == 0)
      i = paramString1.indexOf('.', 1); 
    if (!bool && (i == -1 || i == paramString1.length() - 1))
      return false; 
    int j = paramString2.indexOf('.');
    if (j == -1 && bool)
      return true; 
    int k = paramString1.length();
    int m = paramString2.length() - k;
    if (m == 0)
      return paramString2.equalsIgnoreCase(paramString1); 
    if (m > 0) {
      String str1 = paramString2.substring(0, m);
      String str2 = paramString2.substring(m);
      return str2.equalsIgnoreCase(paramString1);
    } 
    return (m == -1) ? ((paramString1.charAt(0) == '.' && paramString2.equalsIgnoreCase(paramString1.substring(1)))) : false;
  }
  
  private void getInternal1(List<HttpCookie> paramList, Map<String, List<HttpCookie>> paramMap, String paramString, boolean paramBoolean) {
    ArrayList arrayList = new ArrayList();
    for (Map.Entry entry : paramMap.entrySet()) {
      String str = (String)entry.getKey();
      List list = (List)entry.getValue();
      null = list.iterator();
      while (null.hasNext()) {
        HttpCookie httpCookie;
        if ((httpCookie.getVersion() == 0 && netscapeDomainMatches(str, paramString)) || (httpCookie.getVersion() == 1 && (httpCookie = (HttpCookie)null.next()).domainMatches(str, paramString))) {
          if (this.cookieJar.indexOf(httpCookie) != -1) {
            if (!httpCookie.hasExpired()) {
              if ((paramBoolean || !httpCookie.getSecure()) && !paramList.contains(httpCookie))
                paramList.add(httpCookie); 
              continue;
            } 
            arrayList.add(httpCookie);
            continue;
          } 
          arrayList.add(httpCookie);
        } 
      } 
      for (HttpCookie httpCookie : arrayList) {
        list.remove(httpCookie);
        this.cookieJar.remove(httpCookie);
      } 
      arrayList.clear();
    } 
  }
  
  private <T> void getInternal2(List<HttpCookie> paramList, Map<T, List<HttpCookie>> paramMap, Comparable<T> paramComparable, boolean paramBoolean) {
    for (Object object : paramMap.keySet()) {
      if (paramComparable.compareTo(object) == 0) {
        List list = (List)paramMap.get(object);
        if (list != null) {
          Iterator iterator = list.iterator();
          while (iterator.hasNext()) {
            HttpCookie httpCookie = (HttpCookie)iterator.next();
            if (this.cookieJar.indexOf(httpCookie) != -1) {
              if (!httpCookie.hasExpired()) {
                if ((paramBoolean || !httpCookie.getSecure()) && !paramList.contains(httpCookie))
                  paramList.add(httpCookie); 
                continue;
              } 
              iterator.remove();
              this.cookieJar.remove(httpCookie);
              continue;
            } 
            iterator.remove();
          } 
        } 
      } 
    } 
  }
  
  private <T> void addIndex(Map<T, List<HttpCookie>> paramMap, T paramT, HttpCookie paramHttpCookie) {
    if (paramT != null) {
      List list = (List)paramMap.get(paramT);
      if (list != null) {
        list.remove(paramHttpCookie);
        list.add(paramHttpCookie);
      } else {
        list = new ArrayList();
        list.add(paramHttpCookie);
        paramMap.put(paramT, list);
      } 
    } 
  }
  
  private URI getEffectiveURI(URI paramURI) {
    URI uRI = null;
    try {
      uRI = new URI("http", paramURI.getHost(), null, null, null);
    } catch (URISyntaxException uRISyntaxException) {
      uRI = paramURI;
    } 
    return uRI;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\InMemoryCookieStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */