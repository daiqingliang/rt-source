package sun.misc;

import java.net.InetAddress;
import java.net.URLClassLoader;

public interface JavaNetAccess {
  URLClassPath getURLClassPath(URLClassLoader paramURLClassLoader);
  
  String getOriginalHostName(InetAddress paramInetAddress);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\JavaNetAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */