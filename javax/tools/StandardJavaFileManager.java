package javax.tools;

import java.io.File;
import java.io.IOException;

public interface StandardJavaFileManager extends JavaFileManager {
  boolean isSameFile(FileObject paramFileObject1, FileObject paramFileObject2);
  
  Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> paramIterable);
  
  Iterable<? extends JavaFileObject> getJavaFileObjects(File... paramVarArgs);
  
  Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> paramIterable);
  
  Iterable<? extends JavaFileObject> getJavaFileObjects(String... paramVarArgs);
  
  void setLocation(JavaFileManager.Location paramLocation, Iterable<? extends File> paramIterable) throws IOException;
  
  Iterable<? extends File> getLocation(JavaFileManager.Location paramLocation);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\StandardJavaFileManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */