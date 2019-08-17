package sun.misc;

import java.io.File;
import java.net.URL;
import sun.net.www.ParseUtil;

public class FileURLMapper {
  URL url;
  
  String file;
  
  public FileURLMapper(URL paramURL) { this.url = paramURL; }
  
  public String getPath() {
    if (this.file != null)
      return this.file; 
    String str1 = this.url.getHost();
    if (str1 != null && !str1.equals("") && !"localhost".equalsIgnoreCase(str1)) {
      String str3 = this.url.getFile();
      String str4 = str1 + ParseUtil.decode(this.url.getFile());
      this.file = "\\\\" + str4.replace('/', '\\');
      return this.file;
    } 
    String str2 = this.url.getFile().replace('/', '\\');
    this.file = ParseUtil.decode(str2);
    return this.file;
  }
  
  public boolean exists() {
    String str = getPath();
    File file1 = new File(str);
    return file1.exists();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\FileURLMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */