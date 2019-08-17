package sun.net.www.protocol.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import sun.net.ProgressMonitor;
import sun.net.ProgressSource;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;

public class FileURLConnection extends URLConnection {
  static String CONTENT_LENGTH = "content-length";
  
  static String CONTENT_TYPE = "content-type";
  
  static String TEXT_PLAIN = "text/plain";
  
  static String LAST_MODIFIED = "last-modified";
  
  String contentType;
  
  InputStream is;
  
  File file;
  
  String filename;
  
  boolean isDirectory = false;
  
  boolean exists = false;
  
  List<String> files;
  
  long length = -1L;
  
  long lastModified = 0L;
  
  private boolean initializedHeaders = false;
  
  Permission permission;
  
  protected FileURLConnection(URL paramURL, File paramFile) {
    super(paramURL);
    this.file = paramFile;
  }
  
  public void connect() throws IOException {
    if (!this.connected) {
      try {
        this.filename = this.file.toString();
        this.isDirectory = this.file.isDirectory();
        if (this.isDirectory) {
          String[] arrayOfString = this.file.list();
          if (arrayOfString == null)
            throw new FileNotFoundException(this.filename + " exists, but is not accessible"); 
          this.files = Arrays.asList(arrayOfString);
        } else {
          this.is = new BufferedInputStream(new FileInputStream(this.filename));
          boolean bool = ProgressMonitor.getDefault().shouldMeterInput(this.url, "GET");
          if (bool) {
            ProgressSource progressSource = new ProgressSource(this.url, "GET", this.file.length());
            this.is = new MeteredStream(this.is, progressSource, this.file.length());
          } 
        } 
      } catch (IOException iOException) {
        throw iOException;
      } 
      this.connected = true;
    } 
  }
  
  private void initializeHeaders() throws IOException {
    try {
      connect();
      this.exists = this.file.exists();
    } catch (IOException iOException) {}
    if (!this.initializedHeaders || !this.exists) {
      this.length = this.file.length();
      this.lastModified = this.file.lastModified();
      if (!this.isDirectory) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        this.contentType = fileNameMap.getContentTypeFor(this.filename);
        if (this.contentType != null)
          this.properties.add(CONTENT_TYPE, this.contentType); 
        this.properties.add(CONTENT_LENGTH, String.valueOf(this.length));
        if (this.lastModified != 0L) {
          Date date = new Date(this.lastModified);
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
          simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
          this.properties.add(LAST_MODIFIED, simpleDateFormat.format(date));
        } 
      } else {
        this.properties.add(CONTENT_TYPE, TEXT_PLAIN);
      } 
      this.initializedHeaders = true;
    } 
  }
  
  public String getHeaderField(String paramString) {
    initializeHeaders();
    return super.getHeaderField(paramString);
  }
  
  public String getHeaderField(int paramInt) {
    initializeHeaders();
    return super.getHeaderField(paramInt);
  }
  
  public int getContentLength() {
    initializeHeaders();
    return (this.length > 2147483647L) ? -1 : (int)this.length;
  }
  
  public long getContentLengthLong() {
    initializeHeaders();
    return this.length;
  }
  
  public String getHeaderFieldKey(int paramInt) {
    initializeHeaders();
    return super.getHeaderFieldKey(paramInt);
  }
  
  public MessageHeader getProperties() {
    initializeHeaders();
    return super.getProperties();
  }
  
  public long getLastModified() {
    initializeHeaders();
    return this.lastModified;
  }
  
  public InputStream getInputStream() throws IOException {
    connect();
    if (this.is == null)
      if (this.isDirectory) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        StringBuffer stringBuffer = new StringBuffer();
        if (this.files == null)
          throw new FileNotFoundException(this.filename); 
        Collections.sort(this.files, Collator.getInstance());
        for (byte b = 0; b < this.files.size(); b++) {
          String str = (String)this.files.get(b);
          stringBuffer.append(str);
          stringBuffer.append("\n");
        } 
        this.is = new ByteArrayInputStream(stringBuffer.toString().getBytes());
      } else {
        throw new FileNotFoundException(this.filename);
      }  
    return this.is;
  }
  
  public Permission getPermission() throws IOException {
    if (this.permission == null) {
      String str = ParseUtil.decode(this.url.getPath());
      if (File.separatorChar == '/') {
        this.permission = new FilePermission(str, "read");
      } else {
        this.permission = new FilePermission(str.replace('/', File.separatorChar), "read");
      } 
    } 
    return this.permission;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\file\FileURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */