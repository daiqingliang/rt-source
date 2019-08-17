package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.InputStream;

final class MimeUtility {
  private static final boolean ignoreUnknownEncoding = PropUtil.getBooleanSystemProperty("mail.mime.ignoreunknownencoding", false);
  
  public static InputStream decode(InputStream paramInputStream, String paramString) throws DecodingException {
    if (paramString.equalsIgnoreCase("base64"))
      return new BASE64DecoderStream(paramInputStream); 
    if (paramString.equalsIgnoreCase("quoted-printable"))
      return new QPDecoderStream(paramInputStream); 
    if (paramString.equalsIgnoreCase("uuencode") || paramString.equalsIgnoreCase("x-uuencode") || paramString.equalsIgnoreCase("x-uue"))
      return new UUDecoderStream(paramInputStream); 
    if (paramString.equalsIgnoreCase("binary") || paramString.equalsIgnoreCase("7bit") || paramString.equalsIgnoreCase("8bit"))
      return paramInputStream; 
    if (!ignoreUnknownEncoding)
      throw new DecodingException("Unknown encoding: " + paramString); 
    return paramInputStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MimeUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */