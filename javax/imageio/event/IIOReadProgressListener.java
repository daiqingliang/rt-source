package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;

public interface IIOReadProgressListener extends EventListener {
  void sequenceStarted(ImageReader paramImageReader, int paramInt);
  
  void sequenceComplete(ImageReader paramImageReader);
  
  void imageStarted(ImageReader paramImageReader, int paramInt);
  
  void imageProgress(ImageReader paramImageReader, float paramFloat);
  
  void imageComplete(ImageReader paramImageReader);
  
  void thumbnailStarted(ImageReader paramImageReader, int paramInt1, int paramInt2);
  
  void thumbnailProgress(ImageReader paramImageReader, float paramFloat);
  
  void thumbnailComplete(ImageReader paramImageReader);
  
  void readAborted(ImageReader paramImageReader);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\event\IIOReadProgressListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */