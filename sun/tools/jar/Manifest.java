package sun.tools.jar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.net.www.MessageHeader;

public class Manifest {
  private Vector<MessageHeader> entries = new Vector();
  
  private byte[] tmpbuf = new byte[512];
  
  private Hashtable<String, MessageHeader> tableEntries = new Hashtable();
  
  static final String[] hashes = { "SHA" };
  
  static final byte[] EOL = { 13, 10 };
  
  static final boolean debug = false;
  
  static final String VERSION = "1.0";
  
  static final void debug(String paramString) {}
  
  public Manifest() {}
  
  public Manifest(byte[] paramArrayOfByte) throws IOException { this(new ByteArrayInputStream(paramArrayOfByte), false); }
  
  public Manifest(InputStream paramInputStream) throws IOException { this(paramInputStream, true); }
  
  public Manifest(InputStream paramInputStream, boolean paramBoolean) throws IOException {
    if (!paramInputStream.markSupported())
      paramInputStream = new BufferedInputStream(paramInputStream); 
    while (true) {
      paramInputStream.mark(1);
      if (paramInputStream.read() == -1)
        break; 
      paramInputStream.reset();
      MessageHeader messageHeader = new MessageHeader(paramInputStream);
      if (paramBoolean)
        doHashes(messageHeader); 
      addEntry(messageHeader);
    } 
  }
  
  public Manifest(String[] paramArrayOfString) throws IOException {
    MessageHeader messageHeader = new MessageHeader();
    messageHeader.add("Manifest-Version", "1.0");
    String str = System.getProperty("java.version");
    messageHeader.add("Created-By", "Manifest JDK " + str);
    addEntry(messageHeader);
    addFiles(null, paramArrayOfString);
  }
  
  public void addEntry(MessageHeader paramMessageHeader) {
    this.entries.addElement(paramMessageHeader);
    String str = paramMessageHeader.findValue("Name");
    debug("addEntry for name: " + str);
    if (str != null)
      this.tableEntries.put(str, paramMessageHeader); 
  }
  
  public MessageHeader getEntry(String paramString) { return (MessageHeader)this.tableEntries.get(paramString); }
  
  public MessageHeader entryAt(int paramInt) { return (MessageHeader)this.entries.elementAt(paramInt); }
  
  public Enumeration<MessageHeader> entries() { return this.entries.elements(); }
  
  public void addFiles(File paramFile, String[] paramArrayOfString) throws IOException {
    if (paramArrayOfString == null)
      return; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      File file;
      if (paramFile == null) {
        file = new File(paramArrayOfString[b]);
      } else {
        file = new File(paramFile, paramArrayOfString[b]);
      } 
      if (file.isDirectory()) {
        addFiles(file, file.list());
      } else {
        addFile(file);
      } 
    } 
  }
  
  private final String stdToLocal(String paramString) { return paramString.replace('/', File.separatorChar); }
  
  private final String localToStd(String paramString) {
    paramString = paramString.replace(File.separatorChar, '/');
    if (paramString.startsWith("./")) {
      paramString = paramString.substring(2);
    } else if (paramString.startsWith("/")) {
      paramString = paramString.substring(1);
    } 
    return paramString;
  }
  
  public void addFile(File paramFile) throws IOException {
    String str = localToStd(paramFile.getPath());
    if (this.tableEntries.get(str) == null) {
      MessageHeader messageHeader = new MessageHeader();
      messageHeader.add("Name", str);
      addEntry(messageHeader);
    } 
  }
  
  public void doHashes(MessageHeader paramMessageHeader) {
    String str = paramMessageHeader.findValue("Name");
    if (str == null || str.endsWith("/"))
      return; 
    for (byte b = 0; b < hashes.length; b++) {
      fileInputStream = new FileInputStream(stdToLocal(str));
      try {
        MessageDigest messageDigest = MessageDigest.getInstance(hashes[b]);
        int i;
        while ((i = fileInputStream.read(this.tmpbuf, 0, this.tmpbuf.length)) != -1)
          messageDigest.update(this.tmpbuf, 0, i); 
        paramMessageHeader.set(hashes[b] + "-Digest", Base64.getMimeEncoder().encodeToString(messageDigest.digest()));
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new JarException("Digest algorithm " + hashes[b] + " not available.");
      } finally {
        fileInputStream.close();
      } 
    } 
  }
  
  public void stream(OutputStream paramOutputStream) throws IOException {
    PrintStream printStream;
    if (paramOutputStream instanceof PrintStream) {
      printStream = (PrintStream)paramOutputStream;
    } else {
      printStream = new PrintStream(paramOutputStream);
    } 
    MessageHeader messageHeader = (MessageHeader)this.entries.elementAt(0);
    if (messageHeader.findValue("Manifest-Version") == null) {
      String str = System.getProperty("java.version");
      if (messageHeader.findValue("Name") == null) {
        messageHeader.prepend("Manifest-Version", "1.0");
        messageHeader.add("Created-By", "Manifest JDK " + str);
      } else {
        printStream.print("Manifest-Version: 1.0\r\nCreated-By: " + str + "\r\n\r\n");
      } 
      printStream.flush();
    } 
    messageHeader.print(printStream);
    for (byte b = 1; b < this.entries.size(); b++) {
      MessageHeader messageHeader1 = (MessageHeader)this.entries.elementAt(b);
      messageHeader1.print(printStream);
    } 
  }
  
  public static boolean isManifestName(String paramString) {
    if (paramString.charAt(0) == '/')
      paramString = paramString.substring(1, paramString.length()); 
    paramString = paramString.toUpperCase();
    return paramString.equals("META-INF/MANIFEST.MF");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tools\jar\Manifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */