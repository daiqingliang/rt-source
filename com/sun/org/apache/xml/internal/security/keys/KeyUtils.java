package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.KeyName;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;
import com.sun.org.apache.xml.internal.security.keys.content.MgmtData;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import java.io.PrintStream;
import java.security.PublicKey;

public class KeyUtils {
  public static void prinoutKeyInfo(KeyInfo paramKeyInfo, PrintStream paramPrintStream) throws XMLSecurityException {
    byte b;
    for (b = 0; b < paramKeyInfo.lengthKeyName(); b++) {
      KeyName keyName = paramKeyInfo.itemKeyName(b);
      paramPrintStream.println("KeyName(" + b + ")=\"" + keyName.getKeyName() + "\"");
    } 
    for (b = 0; b < paramKeyInfo.lengthKeyValue(); b++) {
      KeyValue keyValue = paramKeyInfo.itemKeyValue(b);
      PublicKey publicKey = keyValue.getPublicKey();
      paramPrintStream.println("KeyValue Nr. " + b);
      paramPrintStream.println(publicKey);
    } 
    for (b = 0; b < paramKeyInfo.lengthMgmtData(); b++) {
      MgmtData mgmtData = paramKeyInfo.itemMgmtData(b);
      paramPrintStream.println("MgmtData(" + b + ")=\"" + mgmtData.getMgmtData() + "\"");
    } 
    for (b = 0; b < paramKeyInfo.lengthX509Data(); b++) {
      X509Data x509Data = paramKeyInfo.itemX509Data(b);
      paramPrintStream.println("X509Data(" + b + ")=\"" + (x509Data.containsCertificate() ? "Certificate " : "") + (x509Data.containsIssuerSerial() ? "IssuerSerial " : "") + "\"");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\KeyUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */