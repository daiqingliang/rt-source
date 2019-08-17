package sun.tools.jar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import sun.net.www.MessageHeader;
import sun.security.pkcs.PKCS7;
import sun.security.pkcs.SignerInfo;
import sun.security.x509.AlgorithmId;

public class SignatureFile {
  static final boolean debug = false;
  
  private Vector<MessageHeader> entries = new Vector();
  
  static final String[] hashes = { "SHA" };
  
  private Manifest manifest;
  
  private String rawName;
  
  private PKCS7 signatureBlock;
  
  private Hashtable<String, MessageDigest> digests = new Hashtable();
  
  static final void debug(String paramString) {}
  
  private SignatureFile(String paramString) {
    this.entries = new Vector();
    if (paramString != null) {
      if (paramString.length() > 8 || paramString.indexOf('.') != -1)
        throw new JarException("invalid file name"); 
      this.rawName = paramString.toUpperCase(Locale.ENGLISH);
    } 
  }
  
  private SignatureFile(String paramString, boolean paramBoolean) throws JarException {
    this(paramString);
    if (paramBoolean) {
      MessageHeader messageHeader = new MessageHeader();
      messageHeader.set("Signature-Version", "1.0");
      this.entries.addElement(messageHeader);
    } 
  }
  
  public SignatureFile(Manifest paramManifest, String paramString) throws JarException {
    this(paramString, true);
    this.manifest = paramManifest;
    Enumeration enumeration = paramManifest.entries();
    while (enumeration.hasMoreElements()) {
      MessageHeader messageHeader = (MessageHeader)enumeration.nextElement();
      String str = messageHeader.findValue("Name");
      if (str != null)
        add(str); 
    } 
  }
  
  public SignatureFile(Manifest paramManifest, String[] paramArrayOfString, String paramString) throws JarException {
    this(paramString, true);
    this.manifest = paramManifest;
    add(paramArrayOfString);
  }
  
  public SignatureFile(InputStream paramInputStream, String paramString) throws IOException {
    this(paramString);
    while (paramInputStream.available() > 0) {
      MessageHeader messageHeader = new MessageHeader(paramInputStream);
      this.entries.addElement(messageHeader);
    } 
  }
  
  public SignatureFile(InputStream paramInputStream) throws IOException { this(paramInputStream, null); }
  
  public SignatureFile(byte[] paramArrayOfByte) throws IOException { this(new ByteArrayInputStream(paramArrayOfByte)); }
  
  public String getName() { return "META-INF/" + this.rawName + ".SF"; }
  
  public String getBlockName() {
    String str = "DSA";
    if (this.signatureBlock != null) {
      SignerInfo signerInfo = this.signatureBlock.getSignerInfos()[0];
      str = signerInfo.getDigestEncryptionAlgorithmId().getName();
      String str1 = AlgorithmId.getEncAlgFromSigAlg(str);
      if (str1 != null)
        str = str1; 
    } 
    return "META-INF/" + this.rawName + "." + str;
  }
  
  public PKCS7 getBlock() { return this.signatureBlock; }
  
  public void setBlock(PKCS7 paramPKCS7) { this.signatureBlock = paramPKCS7; }
  
  public void add(String[] paramArrayOfString) throws JarException {
    for (byte b = 0; b < paramArrayOfString.length; b++)
      add(paramArrayOfString[b]); 
  }
  
  public void add(String paramString) {
    MessageHeader messageHeader2;
    MessageHeader messageHeader1 = this.manifest.getEntry(paramString);
    if (messageHeader1 == null)
      throw new JarException("entry " + paramString + " not in manifest"); 
    try {
      messageHeader2 = computeEntry(messageHeader1);
    } catch (IOException iOException) {
      throw new JarException(iOException.getMessage());
    } 
    this.entries.addElement(messageHeader2);
  }
  
  public MessageHeader getEntry(String paramString) {
    Enumeration enumeration = entries();
    while (enumeration.hasMoreElements()) {
      MessageHeader messageHeader = (MessageHeader)enumeration.nextElement();
      if (paramString.equals(messageHeader.findValue("Name")))
        return messageHeader; 
    } 
    return null;
  }
  
  public MessageHeader entryAt(int paramInt) { return (MessageHeader)this.entries.elementAt(paramInt); }
  
  public Enumeration<MessageHeader> entries() { return this.entries.elements(); }
  
  private MessageHeader computeEntry(MessageHeader paramMessageHeader) throws IOException {
    MessageHeader messageHeader = new MessageHeader();
    String str = paramMessageHeader.findValue("Name");
    if (str == null)
      return null; 
    messageHeader.set("Name", str);
    try {
      for (byte b = 0; b < hashes.length; b++) {
        MessageDigest messageDigest = getDigest(hashes[b]);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteArrayOutputStream);
        paramMessageHeader.print(printStream);
        byte[] arrayOfByte1 = byteArrayOutputStream.toByteArray();
        byte[] arrayOfByte2 = messageDigest.digest(arrayOfByte1);
        messageHeader.set(hashes[b] + "-Digest", Base64.getMimeEncoder().encodeToString(arrayOfByte2));
      } 
      return messageHeader;
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new JarException(noSuchAlgorithmException.getMessage());
    } 
  }
  
  private MessageDigest getDigest(String paramString) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = (MessageDigest)this.digests.get(paramString);
    if (messageDigest == null) {
      messageDigest = MessageDigest.getInstance(paramString);
      this.digests.put(paramString, messageDigest);
    } 
    messageDigest.reset();
    return messageDigest;
  }
  
  public void stream(OutputStream paramOutputStream) throws IOException {
    MessageHeader messageHeader = (MessageHeader)this.entries.elementAt(0);
    if (messageHeader.findValue("Signature-Version") == null)
      throw new JarException("Signature file requires Signature-Version: 1.0 in 1st header"); 
    PrintStream printStream = new PrintStream(paramOutputStream);
    messageHeader.print(printStream);
    for (byte b = 1; b < this.entries.size(); b++) {
      MessageHeader messageHeader1 = (MessageHeader)this.entries.elementAt(b);
      messageHeader1.print(printStream);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tools\jar\SignatureFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */