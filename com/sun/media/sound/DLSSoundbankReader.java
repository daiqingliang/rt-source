package com.sun.media.sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;

public final class DLSSoundbankReader extends SoundbankReader {
  public Soundbank getSoundbank(URL paramURL) throws InvalidMidiDataException, IOException {
    try {
      return new DLSSoundbank(paramURL);
    } catch (RIFFInvalidFormatException rIFFInvalidFormatException) {
      return null;
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public Soundbank getSoundbank(InputStream paramInputStream) throws InvalidMidiDataException, IOException {
    try {
      paramInputStream.mark(512);
      return new DLSSoundbank(paramInputStream);
    } catch (RIFFInvalidFormatException rIFFInvalidFormatException) {
      paramInputStream.reset();
      return null;
    } 
  }
  
  public Soundbank getSoundbank(File paramFile) throws InvalidMidiDataException, IOException {
    try {
      return new DLSSoundbank(paramFile);
    } catch (RIFFInvalidFormatException rIFFInvalidFormatException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DLSSoundbankReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */