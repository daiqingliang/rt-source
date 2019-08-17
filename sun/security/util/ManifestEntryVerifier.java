package sun.security.util;

import java.io.IOException;
import java.security.CodeSigner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.Manifest;
import sun.security.jca.Providers;

public class ManifestEntryVerifier {
  private static final Debug debug = Debug.getInstance("jar");
  
  HashMap<String, MessageDigest> createdDigests = new HashMap(11);
  
  ArrayList<MessageDigest> digests = new ArrayList();
  
  ArrayList<byte[]> manifestHashes = new ArrayList();
  
  private String name = null;
  
  private Manifest man;
  
  private boolean skip = true;
  
  private JarEntry entry;
  
  private CodeSigner[] signers = null;
  
  private static final char[] hexc = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'a', 'b', 'c', 'd', 'e', 'f' };
  
  public ManifestEntryVerifier(Manifest paramManifest) { this.man = paramManifest; }
  
  public void setEntry(String paramString, JarEntry paramJarEntry) throws IOException {
    this.digests.clear();
    this.manifestHashes.clear();
    this.name = paramString;
    this.entry = paramJarEntry;
    this.skip = true;
    this.signers = null;
    if (this.man == null || paramString == null)
      return; 
    this.skip = false;
    Attributes attributes = this.man.getAttributes(paramString);
    if (attributes == null) {
      attributes = this.man.getAttributes("./" + paramString);
      if (attributes == null) {
        attributes = this.man.getAttributes("/" + paramString);
        if (attributes == null)
          return; 
      } 
    } 
    for (Map.Entry entry1 : attributes.entrySet()) {
      String str = entry1.getKey().toString();
      if (str.toUpperCase(Locale.ENGLISH).endsWith("-DIGEST")) {
        String str1 = str.substring(0, str.length() - 7);
        MessageDigest messageDigest = (MessageDigest)this.createdDigests.get(str1);
        if (messageDigest == null)
          try {
            messageDigest = MessageDigest.getInstance(str1, instance);
            this.createdDigests.put(str1, messageDigest);
          } catch (NoSuchAlgorithmException noSuchAlgorithmException) {} 
        if (messageDigest != null) {
          messageDigest.reset();
          this.digests.add(messageDigest);
          this.manifestHashes.add(Base64.getMimeDecoder().decode((String)entry1.getValue()));
        } 
      } 
    } 
  }
  
  public void update(byte paramByte) {
    if (this.skip)
      return; 
    for (byte b = 0; b < this.digests.size(); b++)
      ((MessageDigest)this.digests.get(b)).update(paramByte); 
  }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (this.skip)
      return; 
    for (byte b = 0; b < this.digests.size(); b++)
      ((MessageDigest)this.digests.get(b)).update(paramArrayOfByte, paramInt1, paramInt2); 
  }
  
  public JarEntry getEntry() { return this.entry; }
  
  public CodeSigner[] verify(Hashtable<String, CodeSigner[]> paramHashtable1, Hashtable<String, CodeSigner[]> paramHashtable2) throws JarException {
    if (this.skip)
      return null; 
    if (this.digests.isEmpty())
      throw new SecurityException("digest missing for " + this.name); 
    if (this.signers != null)
      return this.signers; 
    for (byte b = 0; b < this.digests.size(); b++) {
      MessageDigest messageDigest = (MessageDigest)this.digests.get(b);
      byte[] arrayOfByte1 = (byte[])this.manifestHashes.get(b);
      byte[] arrayOfByte2 = messageDigest.digest();
      if (debug != null) {
        debug.println("Manifest Entry: " + this.name + " digest=" + messageDigest.getAlgorithm());
        debug.println("  manifest " + toHex(arrayOfByte1));
        debug.println("  computed " + toHex(arrayOfByte2));
        debug.println();
      } 
      if (!MessageDigest.isEqual(arrayOfByte2, arrayOfByte1))
        throw new SecurityException(messageDigest.getAlgorithm() + " digest error for " + this.name); 
    } 
    this.signers = (CodeSigner[])paramHashtable2.remove(this.name);
    if (this.signers != null)
      paramHashtable1.put(this.name, this.signers); 
    return this.signers;
  }
  
  static String toHex(byte[] paramArrayOfByte) {
    StringBuffer stringBuffer = new StringBuffer(paramArrayOfByte.length * 2);
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      stringBuffer.append(hexc[paramArrayOfByte[b] >> 4 & 0xF]);
      stringBuffer.append(hexc[paramArrayOfByte[b] & 0xF]);
    } 
    return stringBuffer.toString();
  }
  
  private static class SunProviderHolder {
    private static final Provider instance = Providers.getSunProvider();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\ManifestEntryVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */