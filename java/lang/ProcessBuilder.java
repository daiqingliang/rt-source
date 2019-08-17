package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class ProcessBuilder {
  private List<String> command;
  
  private File directory;
  
  private Map<String, String> environment;
  
  private boolean redirectErrorStream;
  
  private Redirect[] redirects;
  
  public ProcessBuilder(List<String> paramList) {
    if (paramList == null)
      throw new NullPointerException(); 
    this.command = paramList;
  }
  
  public ProcessBuilder(String... paramVarArgs) {
    this.command = new ArrayList(paramVarArgs.length);
    for (String str : paramVarArgs)
      this.command.add(str); 
  }
  
  public ProcessBuilder command(List<String> paramList) {
    if (paramList == null)
      throw new NullPointerException(); 
    this.command = paramList;
    return this;
  }
  
  public ProcessBuilder command(String... paramVarArgs) {
    this.command = new ArrayList(paramVarArgs.length);
    for (String str : paramVarArgs)
      this.command.add(str); 
    return this;
  }
  
  public List<String> command() { return this.command; }
  
  public Map<String, String> environment() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("getenv.*")); 
    if (this.environment == null)
      this.environment = ProcessEnvironment.environment(); 
    assert this.environment != null;
    return this.environment;
  }
  
  ProcessBuilder environment(String[] paramArrayOfString) {
    assert this.environment == null;
    if (paramArrayOfString != null) {
      this.environment = ProcessEnvironment.emptyEnvironment(paramArrayOfString.length);
      assert this.environment != null;
      for (String str : paramArrayOfString) {
        if (str.indexOf(false) != -1)
          str = str.replaceFirst("\000.*", ""); 
        int i = str.indexOf('=', 1);
        if (i != -1)
          this.environment.put(str.substring(0, i), str.substring(i + 1)); 
      } 
    } 
    return this;
  }
  
  public File directory() { return this.directory; }
  
  public ProcessBuilder directory(File paramFile) {
    this.directory = paramFile;
    return this;
  }
  
  private Redirect[] redirects() {
    if (this.redirects == null)
      this.redirects = new Redirect[] { Redirect.PIPE, Redirect.PIPE, Redirect.PIPE }; 
    return this.redirects;
  }
  
  public ProcessBuilder redirectInput(Redirect paramRedirect) {
    if (paramRedirect.type() == Redirect.Type.WRITE || paramRedirect.type() == Redirect.Type.APPEND)
      throw new IllegalArgumentException("Redirect invalid for reading: " + paramRedirect); 
    redirects()[0] = paramRedirect;
    return this;
  }
  
  public ProcessBuilder redirectOutput(Redirect paramRedirect) {
    if (paramRedirect.type() == Redirect.Type.READ)
      throw new IllegalArgumentException("Redirect invalid for writing: " + paramRedirect); 
    redirects()[1] = paramRedirect;
    return this;
  }
  
  public ProcessBuilder redirectError(Redirect paramRedirect) {
    if (paramRedirect.type() == Redirect.Type.READ)
      throw new IllegalArgumentException("Redirect invalid for writing: " + paramRedirect); 
    redirects()[2] = paramRedirect;
    return this;
  }
  
  public ProcessBuilder redirectInput(File paramFile) { return redirectInput(Redirect.from(paramFile)); }
  
  public ProcessBuilder redirectOutput(File paramFile) { return redirectOutput(Redirect.to(paramFile)); }
  
  public ProcessBuilder redirectError(File paramFile) { return redirectError(Redirect.to(paramFile)); }
  
  public Redirect redirectInput() { return (this.redirects == null) ? Redirect.PIPE : this.redirects[0]; }
  
  public Redirect redirectOutput() { return (this.redirects == null) ? Redirect.PIPE : this.redirects[1]; }
  
  public Redirect redirectError() { return (this.redirects == null) ? Redirect.PIPE : this.redirects[2]; }
  
  public ProcessBuilder inheritIO() {
    Arrays.fill(redirects(), Redirect.INHERIT);
    return this;
  }
  
  public boolean redirectErrorStream() { return this.redirectErrorStream; }
  
  public ProcessBuilder redirectErrorStream(boolean paramBoolean) {
    this.redirectErrorStream = paramBoolean;
    return this;
  }
  
  public Process start() throws IOException {
    String[] arrayOfString = (String[])this.command.toArray(new String[this.command.size()]);
    arrayOfString = (String[])arrayOfString.clone();
    for (String str : arrayOfString) {
      if (str == null)
        throw new NullPointerException(); 
    } 
    String str1 = arrayOfString[0];
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkExec(str1); 
    String str2 = (this.directory == null) ? null : this.directory.toString();
    for (b = 1; b < arrayOfString.length; b++) {
      if (arrayOfString[b].indexOf(false) >= 0)
        throw new IOException("invalid null character in command"); 
    } 
    try {
      return ProcessImpl.start(arrayOfString, this.environment, str2, this.redirects, this.redirectErrorStream);
    } catch (IOException|IllegalArgumentException b) {
      IOException iOException;
      String str = ": " + iOException.getMessage();
      SecurityException securityException = iOException;
      if (iOException instanceof IOException && securityManager != null)
        try {
          securityManager.checkRead(str1);
        } catch (SecurityException securityException1) {
          str = "";
          securityException = securityException1;
        }  
      throw new IOException("Cannot run program \"" + str1 + "\"" + ((str2 == null) ? "" : (" (in directory \"" + str2 + "\")")) + str, securityException);
    } 
  }
  
  static class NullInputStream extends InputStream {
    static final NullInputStream INSTANCE = new NullInputStream();
    
    public int read() { return -1; }
    
    public int available() { return 0; }
  }
  
  static class NullOutputStream extends OutputStream {
    static final NullOutputStream INSTANCE = new NullOutputStream();
    
    public void write(int param1Int) throws IOException { throw new IOException("Stream closed"); }
  }
  
  public static abstract class Redirect {
    public static final Redirect PIPE = new Redirect() {
        public Type type() { return Type.PIPE; }
        
        public String toString() { return type().toString(); }
      };
    
    public static final Redirect INHERIT = new Redirect() {
        public Type type() { return Type.INHERIT; }
        
        public String toString() { return type().toString(); }
      };
    
    public abstract Type type();
    
    public File file() { return null; }
    
    boolean append() { throw new UnsupportedOperationException(); }
    
    public static Redirect from(final File file) {
      if (param1File == null)
        throw new NullPointerException(); 
      return new Redirect() {
          public Type type() { return Type.READ; }
          
          public File file() { return file; }
          
          public String toString() { return "redirect to read from file \"" + file + "\""; }
        };
    }
    
    public static Redirect to(final File file) {
      if (param1File == null)
        throw new NullPointerException(); 
      return new Redirect() {
          public Type type() { return Type.WRITE; }
          
          public File file() { return file; }
          
          public String toString() { return "redirect to write to file \"" + file + "\""; }
          
          boolean append() { return false; }
        };
    }
    
    public static Redirect appendTo(final File file) {
      if (param1File == null)
        throw new NullPointerException(); 
      return new Redirect() {
          public Type type() { return Type.APPEND; }
          
          public File file() { return file; }
          
          public String toString() { return "redirect to append to file \"" + file + "\""; }
          
          boolean append() { return true; }
        };
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof Redirect))
        return false; 
      Redirect redirect = (Redirect)param1Object;
      if (redirect.type() != type())
        return false; 
      assert file() != null;
      return file().equals(redirect.file());
    }
    
    public int hashCode() {
      File file = file();
      return (file == null) ? super.hashCode() : file.hashCode();
    }
    
    private Redirect() {}
    
    public enum Type {
      PIPE, INHERIT, READ, WRITE, APPEND;
    }
  }
  
  public enum Type {
    PIPE, INHERIT, READ, WRITE, APPEND;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ProcessBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */