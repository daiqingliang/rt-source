package javax.sound.midi;

import java.util.EventListener;

public interface ControllerEventListener extends EventListener {
  void controlChange(ShortMessage paramShortMessage);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\ControllerEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */