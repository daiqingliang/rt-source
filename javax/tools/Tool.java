package javax.tools;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.lang.model.SourceVersion;

public interface Tool {
  int run(InputStream paramInputStream, OutputStream paramOutputStream1, OutputStream paramOutputStream2, String... paramVarArgs);
  
  Set<SourceVersion> getSourceVersions();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\Tool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */