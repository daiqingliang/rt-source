package javax.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

public class ForwardingFileObject<F extends FileObject> extends Object implements FileObject {
  protected final F fileObject;
  
  protected ForwardingFileObject(F paramF) {
    paramF.getClass();
    this.fileObject = paramF;
  }
  
  public URI toUri() { return this.fileObject.toUri(); }
  
  public String getName() { return this.fileObject.getName(); }
  
  public InputStream openInputStream() throws IOException { return this.fileObject.openInputStream(); }
  
  public OutputStream openOutputStream() throws IOException { return this.fileObject.openOutputStream(); }
  
  public Reader openReader(boolean paramBoolean) throws IOException { return this.fileObject.openReader(paramBoolean); }
  
  public CharSequence getCharContent(boolean paramBoolean) throws IOException { return this.fileObject.getCharContent(paramBoolean); }
  
  public Writer openWriter() throws IOException { return this.fileObject.openWriter(); }
  
  public long getLastModified() { return this.fileObject.getLastModified(); }
  
  public boolean delete() { return this.fileObject.delete(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\ForwardingFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */