package com.sun.org.apache.xml.internal.security.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecurityPermission;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtils {
  private static Logger log = Logger.getLogger(JavaUtils.class.getName());
  
  private static final SecurityPermission REGISTER_PERMISSION = new SecurityPermission("com.sun.org.apache.xml.internal.security.register");
  
  public static byte[] getBytesFromFile(String paramString) throws FileNotFoundException, IOException {
    byte[] arrayOfByte = null;
    fileInputStream = null;
    unsyncByteArrayOutputStream = null;
    try {
      fileInputStream = new FileInputStream(paramString);
      unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
      byte[] arrayOfByte1 = new byte[1024];
      int i;
      while ((i = fileInputStream.read(arrayOfByte1)) > 0)
        unsyncByteArrayOutputStream.write(arrayOfByte1, 0, i); 
      arrayOfByte = unsyncByteArrayOutputStream.toByteArray();
    } finally {
      if (unsyncByteArrayOutputStream != null)
        unsyncByteArrayOutputStream.close(); 
      if (fileInputStream != null)
        fileInputStream.close(); 
    } 
    return arrayOfByte;
  }
  
  public static void writeBytesToFilename(String paramString, byte[] paramArrayOfByte) {
    FileOutputStream fileOutputStream = null;
    try {
      if (paramString != null && paramArrayOfByte != null) {
        File file = new File(paramString);
        fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(paramArrayOfByte);
        fileOutputStream.close();
      } else if (log.isLoggable(Level.FINE)) {
        log.log(Level.FINE, "writeBytesToFilename got null byte[] pointed");
      } 
    } catch (IOException iOException) {
      if (fileOutputStream != null)
        try {
          fileOutputStream.close();
        } catch (IOException iOException1) {
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, iOException1.getMessage(), iOException1); 
        }  
    } 
  }
  
  public static byte[] getBytesFromStream(InputStream paramInputStream) throws IOException {
    unsyncByteArrayOutputStream = null;
    byte[] arrayOfByte = null;
    try {
      unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
      byte[] arrayOfByte1 = new byte[4096];
      int i;
      while ((i = paramInputStream.read(arrayOfByte1)) > 0)
        unsyncByteArrayOutputStream.write(arrayOfByte1, 0, i); 
      arrayOfByte = unsyncByteArrayOutputStream.toByteArray();
    } finally {
      unsyncByteArrayOutputStream.close();
    } 
    return arrayOfByte;
  }
  
  public static byte[] convertDsaASN1toXMLDSIG(byte[] paramArrayOfByte, int paramInt) throws IOException {
    if (paramArrayOfByte[0] != 48 || paramArrayOfByte[1] != paramArrayOfByte.length - 2 || paramArrayOfByte[2] != 2)
      throw new IOException("Invalid ASN.1 format of DSA signature"); 
    byte b1 = paramArrayOfByte[3];
    int i;
    for (i = b1; i > 0 && paramArrayOfByte[4 + b1 - i] == 0; i--);
    byte b2 = paramArrayOfByte[5 + b1];
    int j;
    for (j = b2; j > 0 && paramArrayOfByte[6 + b1 + b2 - j] == 0; j--);
    if (i > paramInt || paramArrayOfByte[4 + b1] != 2 || j > paramInt)
      throw new IOException("Invalid ASN.1 format of DSA signature"); 
    byte[] arrayOfByte = new byte[paramInt * 2];
    System.arraycopy(paramArrayOfByte, 4 + b1 - i, arrayOfByte, paramInt - i, i);
    System.arraycopy(paramArrayOfByte, 6 + b1 + b2 - j, arrayOfByte, paramInt * 2 - j, j);
    return arrayOfByte;
  }
  
  public static byte[] convertDsaXMLDSIGtoASN1(byte[] paramArrayOfByte, int paramInt) throws IOException {
    int i = paramInt * 2;
    if (paramArrayOfByte.length != i)
      throw new IOException("Invalid XMLDSIG format of DSA signature"); 
    int j;
    for (j = paramInt; j > 0 && paramArrayOfByte[paramInt - j] == 0; j--);
    int k = j;
    if (paramArrayOfByte[paramInt - j] < 0)
      k++; 
    int m;
    for (m = paramInt; m > 0 && paramArrayOfByte[i - m] == 0; m--);
    int n = m;
    if (paramArrayOfByte[i - m] < 0)
      n++; 
    byte[] arrayOfByte = new byte[6 + k + n];
    arrayOfByte[0] = 48;
    arrayOfByte[1] = (byte)(4 + k + n);
    arrayOfByte[2] = 2;
    arrayOfByte[3] = (byte)k;
    System.arraycopy(paramArrayOfByte, paramInt - j, arrayOfByte, 4 + k - j, j);
    arrayOfByte[4 + k] = 2;
    arrayOfByte[5 + k] = (byte)n;
    System.arraycopy(paramArrayOfByte, i - m, arrayOfByte, 6 + k + n - m, m);
    return arrayOfByte;
  }
  
  public static void checkRegisterPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(REGISTER_PERMISSION); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\JavaUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */