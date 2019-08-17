package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;

public abstract class SoundbankReader {
  public abstract Soundbank getSoundbank(URL paramURL) throws InvalidMidiDataException, IOException;
  
  public abstract Soundbank getSoundbank(InputStream paramInputStream) throws InvalidMidiDataException, IOException;
  
  public abstract Soundbank getSoundbank(File paramFile) throws InvalidMidiDataException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\spi\SoundbankReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */