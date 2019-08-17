package sun.net.www.protocol.jar;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

public interface URLJarFileCallBack {
  JarFile retrieve(URL paramURL) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\jar\URLJarFileCallBack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */