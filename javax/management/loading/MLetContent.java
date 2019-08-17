package javax.management.loading;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MLetContent {
  private Map<String, String> attributes;
  
  private List<String> types;
  
  private List<String> values;
  
  private URL documentURL;
  
  private URL baseURL;
  
  public MLetContent(URL paramURL, Map<String, String> paramMap, List<String> paramList1, List<String> paramList2) {
    this.documentURL = paramURL;
    this.attributes = Collections.unmodifiableMap(paramMap);
    this.types = Collections.unmodifiableList(paramList1);
    this.values = Collections.unmodifiableList(paramList2);
    String str = getParameter("codebase");
    if (str != null) {
      if (!str.endsWith("/"))
        str = str + "/"; 
      try {
        this.baseURL = new URL(this.documentURL, str);
      } catch (MalformedURLException malformedURLException) {}
    } 
    if (this.baseURL == null) {
      String str1 = this.documentURL.getFile();
      int i = str1.lastIndexOf('/');
      if (i >= 0 && i < str1.length() - 1)
        try {
          this.baseURL = new URL(this.documentURL, str1.substring(0, i + 1));
        } catch (MalformedURLException malformedURLException) {} 
    } 
    if (this.baseURL == null)
      this.baseURL = this.documentURL; 
  }
  
  public Map<String, String> getAttributes() { return this.attributes; }
  
  public URL getDocumentBase() { return this.documentURL; }
  
  public URL getCodeBase() { return this.baseURL; }
  
  public String getJarFiles() { return getParameter("archive"); }
  
  public String getCode() { return getParameter("code"); }
  
  public String getSerializedObject() { return getParameter("object"); }
  
  public String getName() { return getParameter("name"); }
  
  public String getVersion() { return getParameter("version"); }
  
  public List<String> getParameterTypes() { return this.types; }
  
  public List<String> getParameterValues() { return this.values; }
  
  private String getParameter(String paramString) { return (String)this.attributes.get(paramString.toLowerCase()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\loading\MLetContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */