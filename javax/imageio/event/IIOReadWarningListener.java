package javax.imageio.event;

import java.util.EventListener;
import javax.imageio.ImageReader;

public interface IIOReadWarningListener extends EventListener {
  void warningOccurred(ImageReader paramImageReader, String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\event\IIOReadWarningListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */