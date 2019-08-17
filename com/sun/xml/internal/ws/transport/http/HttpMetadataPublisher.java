package com.sun.xml.internal.ws.transport.http;

import com.sun.istack.internal.NotNull;
import java.io.IOException;

public abstract class HttpMetadataPublisher {
  public abstract boolean handleMetadataRequest(@NotNull HttpAdapter paramHttpAdapter, @NotNull WSHTTPConnection paramWSHTTPConnection) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\HttpMetadataPublisher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */