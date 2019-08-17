package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageWriter;

public interface IIOWriteProgressListener extends EventListener {
  void imageStarted(ImageWriter paramImageWriter, int paramInt);
  
  void imageProgress(ImageWriter paramImageWriter, float paramFloat);
  
  void imageComplete(ImageWriter paramImageWriter);
  
  void thumbnailStarted(ImageWriter paramImageWriter, int paramInt1, int paramInt2);
  
  void thumbnailProgress(ImageWriter paramImageWriter, float paramFloat);
  
  void thumbnailComplete(ImageWriter paramImageWriter);
  
  void writeAborted(ImageWriter paramImageWriter);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\event\IIOWriteProgressListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */