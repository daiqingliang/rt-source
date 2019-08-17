package sun.security.timestamp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import sun.misc.IOUtils;
import sun.security.util.Debug;

public class HttpTimestamper implements Timestamper {
  private static final int CONNECT_TIMEOUT = 15000;
  
  private static final String TS_QUERY_MIME_TYPE = "application/timestamp-query";
  
  private static final String TS_REPLY_MIME_TYPE = "application/timestamp-reply";
  
  private static final Debug debug = Debug.getInstance("ts");
  
  private URI tsaURI = null;
  
  public HttpTimestamper(URI paramURI) {
    if (!paramURI.getScheme().equalsIgnoreCase("http") && !paramURI.getScheme().equalsIgnoreCase("https"))
      throw new IllegalArgumentException("TSA must be an HTTP or HTTPS URI"); 
    this.tsaURI = paramURI;
  }
  
  public TSResponse generateTimestamp(TSRequest paramTSRequest) throws IOException {
    HttpURLConnection httpURLConnection = (HttpURLConnection)this.tsaURI.toURL().openConnection();
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setUseCaches(false);
    httpURLConnection.setRequestProperty("Content-Type", "application/timestamp-query");
    httpURLConnection.setRequestMethod("POST");
    httpURLConnection.setConnectTimeout(15000);
    if (debug != null) {
      Set set = httpURLConnection.getRequestProperties().entrySet();
      debug.println(httpURLConnection.getRequestMethod() + " " + this.tsaURI + " HTTP/1.1");
      for (Map.Entry entry : set)
        debug.println("  " + entry); 
      debug.println();
    } 
    httpURLConnection.connect();
    dataOutputStream = null;
    try {
      dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
      byte[] arrayOfByte1 = paramTSRequest.encode();
      dataOutputStream.write(arrayOfByte1, 0, arrayOfByte1.length);
      dataOutputStream.flush();
      if (debug != null)
        debug.println("sent timestamp query (length=" + arrayOfByte1.length + ")"); 
    } finally {
      if (dataOutputStream != null)
        dataOutputStream.close(); 
    } 
    bufferedInputStream = null;
    byte[] arrayOfByte = null;
    try {
      bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
      if (debug != null) {
        String str = httpURLConnection.getHeaderField(0);
        debug.println(str);
        for (byte b = 1; (str = httpURLConnection.getHeaderField(b)) != null; b++) {
          String str1 = httpURLConnection.getHeaderFieldKey(b);
          debug.println("  " + ((str1 == null) ? "" : (str1 + ": ")) + str);
        } 
        debug.println();
      } 
      verifyMimeType(httpURLConnection.getContentType());
      int i = httpURLConnection.getContentLength();
      arrayOfByte = IOUtils.readFully(bufferedInputStream, i, false);
      if (debug != null)
        debug.println("received timestamp response (length=" + arrayOfByte.length + ")"); 
    } finally {
      if (bufferedInputStream != null)
        bufferedInputStream.close(); 
    } 
    return new TSResponse(arrayOfByte);
  }
  
  private static void verifyMimeType(String paramString) throws IOException {
    if (!"application/timestamp-reply".equalsIgnoreCase(paramString))
      throw new IOException("MIME Content-Type is not application/timestamp-reply"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\timestamp\HttpTimestamper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */