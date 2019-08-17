package com.sun.media.sound;

import java.util.Comparator;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;

public final class ModelInstrumentComparator extends Object implements Comparator<Instrument> {
  public int compare(Instrument paramInstrument1, Instrument paramInstrument2) {
    Patch patch1 = paramInstrument1.getPatch();
    Patch patch2 = paramInstrument2.getPatch();
    int i = patch1.getBank() * 128 + patch1.getProgram();
    int j = patch2.getBank() * 128 + patch2.getProgram();
    if (patch1 instanceof ModelPatch)
      i += (((ModelPatch)patch1).isPercussion() ? 2097152 : 0); 
    if (patch2 instanceof ModelPatch)
      j += (((ModelPatch)patch2).isPercussion() ? 2097152 : 0); 
    return i - j;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelInstrumentComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */