package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class TransformInputOutput {
  private static URI currentJavaWorkingDirectory = (new File(System.getProperty("user.dir"))).toURI();
  
  public void parse(String[] paramArrayOfString) throws Exception {
    BufferedInputStream bufferedInputStream = null;
    BufferedOutputStream bufferedOutputStream = null;
    if (paramArrayOfString.length == 0) {
      bufferedInputStream = new BufferedInputStream(System.in);
      bufferedOutputStream = new BufferedOutputStream(System.out);
    } else if (paramArrayOfString.length == 1) {
      bufferedInputStream = new BufferedInputStream(new FileInputStream(paramArrayOfString[0]));
      bufferedOutputStream = new BufferedOutputStream(System.out);
    } else if (paramArrayOfString.length == 2) {
      bufferedInputStream = new BufferedInputStream(new FileInputStream(paramArrayOfString[0]));
      bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramArrayOfString[1]));
    } else {
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.optinalFileNotSpecified"));
    } 
    parse(bufferedInputStream, bufferedOutputStream);
  }
  
  public abstract void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception;
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws Exception { throw new UnsupportedOperationException(); }
  
  protected static EntityResolver createRelativePathResolver(final String workingDirectory) { return new EntityResolver() {
        public InputSource resolveEntity(String param1String1, String param1String2) throws SAXException, IOException {
          if (param1String2 != null && param1String2.startsWith("file:/")) {
            URI uRI = (new File(workingDirectory)).toURI();
            try {
              URI uRI1 = TransformInputOutput.convertToNewWorkingDirectory(currentJavaWorkingDirectory, uRI, (new File(new URI(param1String2))).toURI());
              return new InputSource(uRI1.toString());
            } catch (URISyntaxException uRISyntaxException) {}
          } 
          return null;
        }
      }; }
  
  private static URI convertToNewWorkingDirectory(URI paramURI1, URI paramURI2, URI paramURI3) throws IOException, URISyntaxException {
    String str1 = paramURI1.toString();
    String str2 = paramURI2.toString();
    String str3 = paramURI3.toString();
    String str4 = null;
    if (str3.startsWith(str1) && (str4 = str3.substring(str1.length())).indexOf('/') == -1)
      return new URI(str2 + '/' + str4); 
    String[] arrayOfString1 = str1.split("/");
    String[] arrayOfString2 = str2.split("/");
    String[] arrayOfString3 = str3.split("/");
    int i;
    for (i = 0; i < arrayOfString1.length && i < arrayOfString3.length && arrayOfString1[i].equals(arrayOfString3[i]); i++);
    byte b;
    for (b = 0; b < arrayOfString2.length && b < arrayOfString3.length && arrayOfString2[b].equals(arrayOfString3[b]); b++);
    if (b > i)
      return paramURI3; 
    int j = arrayOfString1.length - i;
    StringBuffer stringBuffer = new StringBuffer(100);
    int k;
    for (k = 0; k < arrayOfString2.length - j; k++) {
      stringBuffer.append(arrayOfString2[k]);
      stringBuffer.append('/');
    } 
    for (k = i; k < arrayOfString3.length; k++) {
      stringBuffer.append(arrayOfString3[k]);
      if (k < arrayOfString3.length - 1)
        stringBuffer.append('/'); 
    } 
    return new URI(stringBuffer.toString());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\TransformInputOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */