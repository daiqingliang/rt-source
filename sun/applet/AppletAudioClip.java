package sun.applet;

import com.sun.media.sound.JavaSoundAudioClip;
import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AppletAudioClip implements AudioClip {
  private URL url = null;
  
  private AudioClip audioClip = null;
  
  boolean DEBUG = false;
  
  public AppletAudioClip(URL paramURL) {
    this.url = paramURL;
    try {
      InputStream inputStream = paramURL.openStream();
      createAppletAudioClip(inputStream);
    } catch (IOException iOException) {
      if (this.DEBUG)
        System.err.println("IOException creating AppletAudioClip" + iOException); 
    } 
  }
  
  public AppletAudioClip(URLConnection paramURLConnection) {
    try {
      createAppletAudioClip(paramURLConnection.getInputStream());
    } catch (IOException iOException) {
      if (this.DEBUG)
        System.err.println("IOException creating AppletAudioClip" + iOException); 
    } 
  }
  
  public AppletAudioClip(byte[] paramArrayOfByte) {
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      createAppletAudioClip(byteArrayInputStream);
    } catch (IOException iOException) {
      if (this.DEBUG)
        System.err.println("IOException creating AppletAudioClip " + iOException); 
    } 
  }
  
  void createAppletAudioClip(InputStream paramInputStream) throws IOException {
    try {
      this.audioClip = new JavaSoundAudioClip(paramInputStream);
    } catch (Exception exception) {
      throw new IOException("Failed to construct the AudioClip: " + exception);
    } 
  }
  
  public void play() {
    if (this.audioClip != null)
      this.audioClip.play(); 
  }
  
  public void loop() {
    if (this.audioClip != null)
      this.audioClip.loop(); 
  }
  
  public void stop() {
    if (this.audioClip != null)
      this.audioClip.stop(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletAudioClip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */