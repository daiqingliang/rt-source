package javax.sound.midi.spi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.midi.Sequence;

public abstract class MidiFileWriter {
  public abstract int[] getMidiFileTypes();
  
  public abstract int[] getMidiFileTypes(Sequence paramSequence);
  
  public boolean isFileTypeSupported(int paramInt) {
    int[] arrayOfInt = getMidiFileTypes();
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if (paramInt == arrayOfInt[b])
        return true; 
    } 
    return false;
  }
  
  public boolean isFileTypeSupported(int paramInt, Sequence paramSequence) {
    int[] arrayOfInt = getMidiFileTypes(paramSequence);
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if (paramInt == arrayOfInt[b])
        return true; 
    } 
    return false;
  }
  
  public abstract int write(Sequence paramSequence, int paramInt, OutputStream paramOutputStream) throws IOException;
  
  public abstract int write(Sequence paramSequence, int paramInt, File paramFile) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sound\midi\spi\MidiFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */