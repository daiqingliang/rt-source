package javax.tools;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class ForwardingJavaFileManager<M extends JavaFileManager> extends Object implements JavaFileManager {
  protected final M fileManager;
  
  protected ForwardingJavaFileManager(M paramM) {
    paramM.getClass();
    this.fileManager = paramM;
  }
  
  public ClassLoader getClassLoader(JavaFileManager.Location paramLocation) { return this.fileManager.getClassLoader(paramLocation); }
  
  public Iterable<JavaFileObject> list(JavaFileManager.Location paramLocation, String paramString, Set<JavaFileObject.Kind> paramSet, boolean paramBoolean) throws IOException { return this.fileManager.list(paramLocation, paramString, paramSet, paramBoolean); }
  
  public String inferBinaryName(JavaFileManager.Location paramLocation, JavaFileObject paramJavaFileObject) { return this.fileManager.inferBinaryName(paramLocation, paramJavaFileObject); }
  
  public boolean isSameFile(FileObject paramFileObject1, FileObject paramFileObject2) { return this.fileManager.isSameFile(paramFileObject1, paramFileObject2); }
  
  public boolean handleOption(String paramString, Iterator<String> paramIterator) { return this.fileManager.handleOption(paramString, paramIterator); }
  
  public boolean hasLocation(JavaFileManager.Location paramLocation) { return this.fileManager.hasLocation(paramLocation); }
  
  public int isSupportedOption(String paramString) { return this.fileManager.isSupportedOption(paramString); }
  
  public JavaFileObject getJavaFileForInput(JavaFileManager.Location paramLocation, String paramString, JavaFileObject.Kind paramKind) throws IOException { return this.fileManager.getJavaFileForInput(paramLocation, paramString, paramKind); }
  
  public JavaFileObject getJavaFileForOutput(JavaFileManager.Location paramLocation, String paramString, JavaFileObject.Kind paramKind, FileObject paramFileObject) throws IOException { return this.fileManager.getJavaFileForOutput(paramLocation, paramString, paramKind, paramFileObject); }
  
  public FileObject getFileForInput(JavaFileManager.Location paramLocation, String paramString1, String paramString2) throws IOException { return this.fileManager.getFileForInput(paramLocation, paramString1, paramString2); }
  
  public FileObject getFileForOutput(JavaFileManager.Location paramLocation, String paramString1, String paramString2, FileObject paramFileObject) throws IOException { return this.fileManager.getFileForOutput(paramLocation, paramString1, paramString2, paramFileObject); }
  
  public void flush() throws IOException { this.fileManager.flush(); }
  
  public void close() throws IOException { this.fileManager.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\ForwardingJavaFileManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */