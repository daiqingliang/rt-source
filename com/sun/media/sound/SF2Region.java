package com.sun.media.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SF2Region {
  public static final int GENERATOR_STARTADDRSOFFSET = 0;
  
  public static final int GENERATOR_ENDADDRSOFFSET = 1;
  
  public static final int GENERATOR_STARTLOOPADDRSOFFSET = 2;
  
  public static final int GENERATOR_ENDLOOPADDRSOFFSET = 3;
  
  public static final int GENERATOR_STARTADDRSCOARSEOFFSET = 4;
  
  public static final int GENERATOR_MODLFOTOPITCH = 5;
  
  public static final int GENERATOR_VIBLFOTOPITCH = 6;
  
  public static final int GENERATOR_MODENVTOPITCH = 7;
  
  public static final int GENERATOR_INITIALFILTERFC = 8;
  
  public static final int GENERATOR_INITIALFILTERQ = 9;
  
  public static final int GENERATOR_MODLFOTOFILTERFC = 10;
  
  public static final int GENERATOR_MODENVTOFILTERFC = 11;
  
  public static final int GENERATOR_ENDADDRSCOARSEOFFSET = 12;
  
  public static final int GENERATOR_MODLFOTOVOLUME = 13;
  
  public static final int GENERATOR_UNUSED1 = 14;
  
  public static final int GENERATOR_CHORUSEFFECTSSEND = 15;
  
  public static final int GENERATOR_REVERBEFFECTSSEND = 16;
  
  public static final int GENERATOR_PAN = 17;
  
  public static final int GENERATOR_UNUSED2 = 18;
  
  public static final int GENERATOR_UNUSED3 = 19;
  
  public static final int GENERATOR_UNUSED4 = 20;
  
  public static final int GENERATOR_DELAYMODLFO = 21;
  
  public static final int GENERATOR_FREQMODLFO = 22;
  
  public static final int GENERATOR_DELAYVIBLFO = 23;
  
  public static final int GENERATOR_FREQVIBLFO = 24;
  
  public static final int GENERATOR_DELAYMODENV = 25;
  
  public static final int GENERATOR_ATTACKMODENV = 26;
  
  public static final int GENERATOR_HOLDMODENV = 27;
  
  public static final int GENERATOR_DECAYMODENV = 28;
  
  public static final int GENERATOR_SUSTAINMODENV = 29;
  
  public static final int GENERATOR_RELEASEMODENV = 30;
  
  public static final int GENERATOR_KEYNUMTOMODENVHOLD = 31;
  
  public static final int GENERATOR_KEYNUMTOMODENVDECAY = 32;
  
  public static final int GENERATOR_DELAYVOLENV = 33;
  
  public static final int GENERATOR_ATTACKVOLENV = 34;
  
  public static final int GENERATOR_HOLDVOLENV = 35;
  
  public static final int GENERATOR_DECAYVOLENV = 36;
  
  public static final int GENERATOR_SUSTAINVOLENV = 37;
  
  public static final int GENERATOR_RELEASEVOLENV = 38;
  
  public static final int GENERATOR_KEYNUMTOVOLENVHOLD = 39;
  
  public static final int GENERATOR_KEYNUMTOVOLENVDECAY = 40;
  
  public static final int GENERATOR_INSTRUMENT = 41;
  
  public static final int GENERATOR_RESERVED1 = 42;
  
  public static final int GENERATOR_KEYRANGE = 43;
  
  public static final int GENERATOR_VELRANGE = 44;
  
  public static final int GENERATOR_STARTLOOPADDRSCOARSEOFFSET = 45;
  
  public static final int GENERATOR_KEYNUM = 46;
  
  public static final int GENERATOR_VELOCITY = 47;
  
  public static final int GENERATOR_INITIALATTENUATION = 48;
  
  public static final int GENERATOR_RESERVED2 = 49;
  
  public static final int GENERATOR_ENDLOOPADDRSCOARSEOFFSET = 50;
  
  public static final int GENERATOR_COARSETUNE = 51;
  
  public static final int GENERATOR_FINETUNE = 52;
  
  public static final int GENERATOR_SAMPLEID = 53;
  
  public static final int GENERATOR_SAMPLEMODES = 54;
  
  public static final int GENERATOR_RESERVED3 = 55;
  
  public static final int GENERATOR_SCALETUNING = 56;
  
  public static final int GENERATOR_EXCLUSIVECLASS = 57;
  
  public static final int GENERATOR_OVERRIDINGROOTKEY = 58;
  
  public static final int GENERATOR_UNUSED5 = 59;
  
  public static final int GENERATOR_ENDOPR = 60;
  
  protected Map<Integer, Short> generators = new HashMap();
  
  protected List<SF2Modulator> modulators = new ArrayList();
  
  public Map<Integer, Short> getGenerators() { return this.generators; }
  
  public boolean contains(int paramInt) { return this.generators.containsKey(Integer.valueOf(paramInt)); }
  
  public static short getDefaultValue(int paramInt) { return (paramInt == 8) ? 13500 : ((paramInt == 21) ? -12000 : ((paramInt == 23) ? -12000 : ((paramInt == 25) ? -12000 : ((paramInt == 26) ? -12000 : ((paramInt == 27) ? -12000 : ((paramInt == 28) ? -12000 : ((paramInt == 30) ? -12000 : ((paramInt == 33) ? -12000 : ((paramInt == 34) ? -12000 : ((paramInt == 35) ? -12000 : ((paramInt == 36) ? -12000 : ((paramInt == 38) ? -12000 : ((paramInt == 43) ? 32512 : ((paramInt == 44) ? 32512 : ((paramInt == 46) ? -1 : ((paramInt == 47) ? -1 : ((paramInt == 56) ? 100 : ((paramInt == 58) ? -1 : 0)))))))))))))))))); }
  
  public short getShort(int paramInt) { return !contains(paramInt) ? getDefaultValue(paramInt) : ((Short)this.generators.get(Integer.valueOf(paramInt))).shortValue(); }
  
  public void putShort(int paramInt, short paramShort) { this.generators.put(Integer.valueOf(paramInt), Short.valueOf(paramShort)); }
  
  public byte[] getBytes(int paramInt) {
    int i = getInteger(paramInt);
    byte[] arrayOfByte = new byte[2];
    arrayOfByte[0] = (byte)(0xFF & i);
    arrayOfByte[1] = (byte)((0xFF00 & i) >> 8);
    return arrayOfByte;
  }
  
  public void putBytes(int paramInt, byte[] paramArrayOfByte) { this.generators.put(Integer.valueOf(paramInt), Short.valueOf((short)(paramArrayOfByte[0] + (paramArrayOfByte[1] << 8)))); }
  
  public int getInteger(int paramInt) { return 0xFFFF & getShort(paramInt); }
  
  public void putInteger(int paramInt1, int paramInt2) { this.generators.put(Integer.valueOf(paramInt1), Short.valueOf((short)paramInt2)); }
  
  public List<SF2Modulator> getModulators() { return this.modulators; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SF2Region.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */