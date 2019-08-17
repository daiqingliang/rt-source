package javax.management.loading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.management.ServiceNotFoundException;

public interface MLetMBean {
  Set<Object> getMBeansFromURL(String paramString) throws ServiceNotFoundException;
  
  Set<Object> getMBeansFromURL(URL paramURL) throws ServiceNotFoundException;
  
  void addURL(URL paramURL);
  
  void addURL(String paramString) throws ServiceNotFoundException;
  
  URL[] getURLs();
  
  URL getResource(String paramString);
  
  InputStream getResourceAsStream(String paramString);
  
  Enumeration<URL> getResources(String paramString) throws IOException;
  
  String getLibraryDirectory();
  
  void setLibraryDirectory(String paramString) throws ServiceNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\loading\MLetMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */