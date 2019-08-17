package javax.net.ssl;

import java.net.IDN;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public final class SNIHostName extends SNIServerName {
  private final String hostname;
  
  public SNIHostName(String paramString) {
    super(0, (paramString = IDN.toASCII((String)Objects.requireNonNull(paramString, "Server name value of host_name cannot be null"), 2)).getBytes(StandardCharsets.US_ASCII));
    this.hostname = paramString;
    checkHostName();
  }
  
  public SNIHostName(byte[] paramArrayOfByte) {
    super(0, paramArrayOfByte);
    try {
      CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
      this.hostname = IDN.toASCII(charsetDecoder.decode(ByteBuffer.wrap(paramArrayOfByte)).toString());
    } catch (RuntimeException|java.nio.charset.CharacterCodingException runtimeException) {
      throw new IllegalArgumentException("The encoded server name value is invalid", runtimeException);
    } 
    checkHostName();
  }
  
  public String getAsciiName() { return this.hostname; }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof SNIHostName) ? this.hostname.equalsIgnoreCase(((SNIHostName)paramObject).hostname) : 0); }
  
  public int hashCode() {
    null = 17;
    return 31 * null + this.hostname.toUpperCase(Locale.ENGLISH).hashCode();
  }
  
  public String toString() { return "type=host_name (0), value=" + this.hostname; }
  
  public static SNIMatcher createSNIMatcher(String paramString) {
    if (paramString == null)
      throw new NullPointerException("The regular expression cannot be null"); 
    return new SNIHostNameMatcher(paramString);
  }
  
  private void checkHostName() {
    if (this.hostname.isEmpty())
      throw new IllegalArgumentException("Server name value of host_name cannot be empty"); 
    if (this.hostname.endsWith("."))
      throw new IllegalArgumentException("Server name value of host_name cannot have the trailing dot"); 
  }
  
  private static final class SNIHostNameMatcher extends SNIMatcher {
    private final Pattern pattern;
    
    SNIHostNameMatcher(String param1String) {
      super(0);
      this.pattern = Pattern.compile(param1String, 2);
    }
    
    public boolean matches(SNIServerName param1SNIServerName) {
      SNIHostName sNIHostName;
      if (param1SNIServerName == null)
        throw new NullPointerException("The SNIServerName argument cannot be null"); 
      if (!(param1SNIServerName instanceof SNIHostName)) {
        if (param1SNIServerName.getType() != 0)
          throw new IllegalArgumentException("The server name type is not host_name"); 
        try {
          sNIHostName = new SNIHostName(param1SNIServerName.getEncoded());
        } catch (NullPointerException|IllegalArgumentException nullPointerException) {
          return false;
        } 
      } else {
        sNIHostName = (SNIHostName)param1SNIServerName;
      } 
      String str = sNIHostName.getAsciiName();
      return this.pattern.matcher(str).matches() ? true : this.pattern.matcher(IDN.toUnicode(str)).matches();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SNIHostName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */