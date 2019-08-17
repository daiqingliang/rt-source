package sun.net.www.content.audio;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;
import sun.applet.AppletAudioClip;

public class x_wav extends ContentHandler {
  public Object getContent(URLConnection paramURLConnection) throws IOException { return new AppletAudioClip(paramURLConnection); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\content\audio\x_wav.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */