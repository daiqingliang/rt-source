package javax.tools;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public interface JavaFileManager extends Closeable, Flushable, OptionChecker {
  ClassLoader getClassLoader(Location paramLocation);
  
  Iterable<JavaFileObject> list(Location paramLocation, String paramString, Set<JavaFileObject.Kind> paramSet, boolean paramBoolean) throws IOException;
  
  String inferBinaryName(Location paramLocation, JavaFileObject paramJavaFileObject);
  
  boolean isSameFile(FileObject paramFileObject1, FileObject paramFileObject2);
  
  boolean handleOption(String paramString, Iterator<String> paramIterator);
  
  boolean hasLocation(Location paramLocation);
  
  JavaFileObject getJavaFileForInput(Location paramLocation, String paramString, JavaFileObject.Kind paramKind) throws IOException;
  
  JavaFileObject getJavaFileForOutput(Location paramLocation, String paramString, JavaFileObject.Kind paramKind, FileObject paramFileObject) throws IOException;
  
  FileObject getFileForInput(Location paramLocation, String paramString1, String paramString2) throws IOException;
  
  FileObject getFileForOutput(Location paramLocation, String paramString1, String paramString2, FileObject paramFileObject) throws IOException;
  
  void flush() throws IOException;
  
  void close() throws IOException;
  
  public static interface Location {
    String getName();
    
    boolean isOutputLocation();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\JavaFileManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */