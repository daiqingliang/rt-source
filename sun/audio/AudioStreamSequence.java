package sun.audio;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;

public final class AudioStreamSequence extends SequenceInputStream {
  Enumeration e;
  
  InputStream in;
  
  public AudioStreamSequence(Enumeration paramEnumeration) { super(paramEnumeration); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\audio\AudioStreamSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */