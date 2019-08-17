package sun.audio;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class AudioPlayer extends Thread {
  private final AudioDevice devAudio = AudioDevice.device;
  
  private static final boolean DEBUG = false;
  
  public static final AudioPlayer player = getAudioPlayer();
  
  private static ThreadGroup getAudioThreadGroup() {
    ThreadGroup threadGroup;
    for (threadGroup = currentThread().getThreadGroup(); threadGroup.getParent() != null && threadGroup.getParent().getParent() != null; threadGroup = threadGroup.getParent());
    return threadGroup;
  }
  
  private static AudioPlayer getAudioPlayer() {
    PrivilegedAction privilegedAction = new PrivilegedAction() {
        public Object run() {
          AudioPlayer audioPlayer = new AudioPlayer(null);
          audioPlayer.setPriority(10);
          audioPlayer.setDaemon(true);
          audioPlayer.start();
          return audioPlayer;
        }
      };
    return (AudioPlayer)AccessController.doPrivileged(privilegedAction);
  }
  
  private AudioPlayer() {
    super(getAudioThreadGroup(), "Audio Player");
    this.devAudio.open();
  }
  
  public void start(InputStream paramInputStream) {
    this.devAudio.openChannel(paramInputStream);
    notify();
  }
  
  public void stop(InputStream paramInputStream) { this.devAudio.closeChannel(paramInputStream); }
  
  public void run() {
    this.devAudio.play();
    try {
      while (true)
        Thread.sleep(5000L); 
    } catch (Exception exception) {
      return;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\audio\AudioPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */