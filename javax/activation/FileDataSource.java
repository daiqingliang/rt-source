package javax.activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDataSource implements DataSource {
  private File _file = null;
  
  private FileTypeMap typeMap = null;
  
  public FileDataSource(File paramFile) { this._file = paramFile; }
  
  public FileDataSource(String paramString) { this(new File(paramString)); }
  
  public InputStream getInputStream() throws IOException { return new FileInputStream(this._file); }
  
  public OutputStream getOutputStream() throws IOException { return new FileOutputStream(this._file); }
  
  public String getContentType() { return (this.typeMap == null) ? FileTypeMap.getDefaultFileTypeMap().getContentType(this._file) : this.typeMap.getContentType(this._file); }
  
  public String getName() { return this._file.getName(); }
  
  public File getFile() { return this._file; }
  
  public void setFileTypeMap(FileTypeMap paramFileTypeMap) { this.typeMap = paramFileTypeMap; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\FileDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */