package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class WsaActionUtil {
  private static final Logger LOGGER = Logger.getLogger(WsaActionUtil.class.getName());
  
  public static final String getDefaultFaultAction(JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    String str1 = paramJavaMethod.getOwner().getTargetNamespace();
    String str2 = getDelimiter(str1);
    if (str1.endsWith(str2))
      str1 = str1.substring(0, str1.length() - 1); 
    return str1 + str2 + paramJavaMethod.getOwner().getPortTypeName().getLocalPart() + str2 + paramJavaMethod.getOperationName() + str2 + "Fault" + str2 + paramCheckedException.getExceptionClass().getSimpleName();
  }
  
  private static String getDelimiter(String paramString) {
    String str = "/";
    try {
      URI uRI = new URI(paramString);
      if (uRI.getScheme() != null && uRI.getScheme().equalsIgnoreCase("urn"))
        str = ":"; 
    } catch (URISyntaxException uRISyntaxException) {
      LOGGER.warning("TargetNamespace of WebService is not a valid URI");
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\WsaActionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */