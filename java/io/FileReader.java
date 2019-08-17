package java.io;

public class FileReader extends InputStreamReader {
  public FileReader(String paramString) throws FileNotFoundException { super(new FileInputStream(paramString)); }
  
  public FileReader(File paramFile) throws FileNotFoundException { super(new FileInputStream(paramFile)); }
  
  public FileReader(FileDescriptor paramFileDescriptor) { super(new FileInputStream(paramFileDescriptor)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\FileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */