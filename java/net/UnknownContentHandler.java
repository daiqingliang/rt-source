package java.net;

import java.io.IOException;

class UnknownContentHandler extends ContentHandler {
  static final ContentHandler INSTANCE = new UnknownContentHandler();
  
  public Object getContent(URLConnection paramURLConnection) throws IOException { return paramURLConnection.getInputStream(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\UnknownContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */