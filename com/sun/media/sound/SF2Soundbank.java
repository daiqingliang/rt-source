package com.sun.media.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;

public final class SF2Soundbank implements Soundbank {
  int major = 2;
  
  int minor = 1;
  
  String targetEngine = "EMU8000";
  
  String name = "untitled";
  
  String romName = null;
  
  int romVersionMajor = -1;
  
  int romVersionMinor = -1;
  
  String creationDate = null;
  
  String engineers = null;
  
  String product = null;
  
  String copyright = null;
  
  String comments = null;
  
  String tools = null;
  
  private ModelByteBuffer sampleData = null;
  
  private ModelByteBuffer sampleData24 = null;
  
  private File sampleFile = null;
  
  private boolean largeFormat = false;
  
  private final List<SF2Instrument> instruments = new ArrayList();
  
  private final List<SF2Layer> layers = new ArrayList();
  
  private final List<SF2Sample> samples = new ArrayList();
  
  public SF2Soundbank() {}
  
  public SF2Soundbank(URL paramURL) throws IOException {
    inputStream = paramURL.openStream();
    try {
      readSoundbank(inputStream);
    } finally {
      inputStream.close();
    } 
  }
  
  public SF2Soundbank(File paramFile) throws IOException {
    this.largeFormat = true;
    this.sampleFile = paramFile;
    fileInputStream = new FileInputStream(paramFile);
    try {
      readSoundbank(fileInputStream);
    } finally {
      fileInputStream.close();
    } 
  }
  
  public SF2Soundbank(InputStream paramInputStream) throws IOException { readSoundbank(paramInputStream); }
  
  private void readSoundbank(InputStream paramInputStream) throws IOException {
    RIFFReader rIFFReader = new RIFFReader(paramInputStream);
    if (!rIFFReader.getFormat().equals("RIFF"))
      throw new RIFFInvalidFormatException("Input stream is not a valid RIFF stream!"); 
    if (!rIFFReader.getType().equals("sfbk"))
      throw new RIFFInvalidFormatException("Input stream is not a valid SoundFont!"); 
    while (rIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader1 = rIFFReader.nextChunk();
      if (rIFFReader1.getFormat().equals("LIST")) {
        if (rIFFReader1.getType().equals("INFO"))
          readInfoChunk(rIFFReader1); 
        if (rIFFReader1.getType().equals("sdta"))
          readSdtaChunk(rIFFReader1); 
        if (rIFFReader1.getType().equals("pdta"))
          readPdtaChunk(rIFFReader1); 
      } 
    } 
  }
  
  private void readInfoChunk(RIFFReader paramRIFFReader) throws IOException {
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      String str = rIFFReader.getFormat();
      if (str.equals("ifil")) {
        this.major = rIFFReader.readUnsignedShort();
        this.minor = rIFFReader.readUnsignedShort();
        continue;
      } 
      if (str.equals("isng")) {
        this.targetEngine = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("INAM")) {
        this.name = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("irom")) {
        this.romName = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("iver")) {
        this.romVersionMajor = rIFFReader.readUnsignedShort();
        this.romVersionMinor = rIFFReader.readUnsignedShort();
        continue;
      } 
      if (str.equals("ICRD")) {
        this.creationDate = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IENG")) {
        this.engineers = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IPRD")) {
        this.product = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICOP")) {
        this.copyright = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICMT")) {
        this.comments = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISFT"))
        this.tools = rIFFReader.readString(rIFFReader.available()); 
    } 
  }
  
  private void readSdtaChunk(RIFFReader paramRIFFReader) throws IOException {
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      if (rIFFReader.getFormat().equals("smpl"))
        if (!this.largeFormat) {
          byte[] arrayOfByte = new byte[rIFFReader.available()];
          int i = 0;
          int j = rIFFReader.available();
          while (i != j) {
            if (j - i > 65536) {
              rIFFReader.readFully(arrayOfByte, i, 65536);
              i += 65536;
              continue;
            } 
            rIFFReader.readFully(arrayOfByte, i, j - i);
            i = j;
          } 
          this.sampleData = new ModelByteBuffer(arrayOfByte);
        } else {
          this.sampleData = new ModelByteBuffer(this.sampleFile, rIFFReader.getFilePointer(), rIFFReader.available());
        }  
      if (rIFFReader.getFormat().equals("sm24")) {
        if (!this.largeFormat) {
          byte[] arrayOfByte = new byte[rIFFReader.available()];
          int i = 0;
          int j = rIFFReader.available();
          while (i != j) {
            if (j - i > 65536) {
              rIFFReader.readFully(arrayOfByte, i, 65536);
              i += 65536;
              continue;
            } 
            rIFFReader.readFully(arrayOfByte, i, j - i);
            i = j;
          } 
          this.sampleData24 = new ModelByteBuffer(arrayOfByte);
          continue;
        } 
        this.sampleData24 = new ModelByteBuffer(this.sampleFile, rIFFReader.getFilePointer(), rIFFReader.available());
      } 
    } 
  }
  
  private void readPdtaChunk(RIFFReader paramRIFFReader) throws IOException {
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    ArrayList arrayList3 = new ArrayList();
    ArrayList arrayList4 = new ArrayList();
    ArrayList arrayList5 = new ArrayList();
    ArrayList arrayList6 = new ArrayList();
    ArrayList arrayList7 = new ArrayList();
    ArrayList arrayList8 = new ArrayList();
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      String str = rIFFReader.getFormat();
      if (str.equals("phdr")) {
        if (rIFFReader.available() % 38 != 0)
          throw new RIFFInvalidDataException(); 
        int i = rIFFReader.available() / 38;
        for (byte b = 0; b < i; b++) {
          SF2Instrument sF2Instrument = new SF2Instrument(this);
          sF2Instrument.name = rIFFReader.readString(20);
          sF2Instrument.preset = rIFFReader.readUnsignedShort();
          sF2Instrument.bank = rIFFReader.readUnsignedShort();
          arrayList2.add(Integer.valueOf(rIFFReader.readUnsignedShort()));
          sF2Instrument.library = rIFFReader.readUnsignedInt();
          sF2Instrument.genre = rIFFReader.readUnsignedInt();
          sF2Instrument.morphology = rIFFReader.readUnsignedInt();
          arrayList1.add(sF2Instrument);
          if (b != i - 1)
            this.instruments.add(sF2Instrument); 
        } 
        continue;
      } 
      if (str.equals("pbag")) {
        if (rIFFReader.available() % 4 != 0)
          throw new RIFFInvalidDataException(); 
        int i = rIFFReader.available() / 4;
        int j = rIFFReader.readUnsignedShort();
        int k = rIFFReader.readUnsignedShort();
        while (arrayList3.size() < j)
          arrayList3.add(null); 
        while (arrayList4.size() < k)
          arrayList4.add(null); 
        i--;
        if (arrayList2.isEmpty())
          throw new RIFFInvalidDataException(); 
        j = ((Integer)arrayList2.get(0)).intValue();
        for (k = 0; k < j; k++) {
          if (i == 0)
            throw new RIFFInvalidDataException(); 
          int m = rIFFReader.readUnsignedShort();
          int n = rIFFReader.readUnsignedShort();
          while (arrayList3.size() < m)
            arrayList3.add(null); 
          while (arrayList4.size() < n)
            arrayList4.add(null); 
          i--;
        } 
        for (k = 0; k < arrayList2.size() - 1; k++) {
          int m = ((Integer)arrayList2.get(k + 1)).intValue() - ((Integer)arrayList2.get(k)).intValue();
          SF2Instrument sF2Instrument = (SF2Instrument)arrayList1.get(k);
          for (byte b = 0; b < m; b++) {
            if (i == 0)
              throw new RIFFInvalidDataException(); 
            int n = rIFFReader.readUnsignedShort();
            int i1 = rIFFReader.readUnsignedShort();
            SF2InstrumentRegion sF2InstrumentRegion = new SF2InstrumentRegion();
            sF2Instrument.regions.add(sF2InstrumentRegion);
            while (arrayList3.size() < n)
              arrayList3.add(sF2InstrumentRegion); 
            while (arrayList4.size() < i1)
              arrayList4.add(sF2InstrumentRegion); 
            i--;
          } 
        } 
        continue;
      } 
      if (str.equals("pmod")) {
        for (byte b = 0; b < arrayList4.size(); b++) {
          SF2Modulator sF2Modulator = new SF2Modulator();
          sF2Modulator.sourceOperator = rIFFReader.readUnsignedShort();
          sF2Modulator.destinationOperator = rIFFReader.readUnsignedShort();
          sF2Modulator.amount = rIFFReader.readShort();
          sF2Modulator.amountSourceOperator = rIFFReader.readUnsignedShort();
          sF2Modulator.transportOperator = rIFFReader.readUnsignedShort();
          SF2InstrumentRegion sF2InstrumentRegion = (SF2InstrumentRegion)arrayList4.get(b);
          if (sF2InstrumentRegion != null)
            sF2InstrumentRegion.modulators.add(sF2Modulator); 
        } 
        continue;
      } 
      if (str.equals("pgen")) {
        for (byte b = 0; b < arrayList3.size(); b++) {
          int i = rIFFReader.readUnsignedShort();
          short s = rIFFReader.readShort();
          SF2InstrumentRegion sF2InstrumentRegion = (SF2InstrumentRegion)arrayList3.get(b);
          if (sF2InstrumentRegion != null)
            sF2InstrumentRegion.generators.put(Integer.valueOf(i), Short.valueOf(s)); 
        } 
        continue;
      } 
      if (str.equals("inst")) {
        if (rIFFReader.available() % 22 != 0)
          throw new RIFFInvalidDataException(); 
        int i = rIFFReader.available() / 22;
        for (byte b = 0; b < i; b++) {
          SF2Layer sF2Layer = new SF2Layer(this);
          sF2Layer.name = rIFFReader.readString(20);
          arrayList6.add(Integer.valueOf(rIFFReader.readUnsignedShort()));
          arrayList5.add(sF2Layer);
          if (b != i - 1)
            this.layers.add(sF2Layer); 
        } 
        continue;
      } 
      if (str.equals("ibag")) {
        if (rIFFReader.available() % 4 != 0)
          throw new RIFFInvalidDataException(); 
        int i = rIFFReader.available() / 4;
        int j = rIFFReader.readUnsignedShort();
        int k = rIFFReader.readUnsignedShort();
        while (arrayList7.size() < j)
          arrayList7.add(null); 
        while (arrayList8.size() < k)
          arrayList8.add(null); 
        i--;
        if (arrayList6.isEmpty())
          throw new RIFFInvalidDataException(); 
        j = ((Integer)arrayList6.get(0)).intValue();
        for (k = 0; k < j; k++) {
          if (i == 0)
            throw new RIFFInvalidDataException(); 
          int m = rIFFReader.readUnsignedShort();
          int n = rIFFReader.readUnsignedShort();
          while (arrayList7.size() < m)
            arrayList7.add(null); 
          while (arrayList8.size() < n)
            arrayList8.add(null); 
          i--;
        } 
        for (k = 0; k < arrayList6.size() - 1; k++) {
          int m = ((Integer)arrayList6.get(k + 1)).intValue() - ((Integer)arrayList6.get(k)).intValue();
          SF2Layer sF2Layer = (SF2Layer)this.layers.get(k);
          for (byte b = 0; b < m; b++) {
            if (i == 0)
              throw new RIFFInvalidDataException(); 
            int n = rIFFReader.readUnsignedShort();
            int i1 = rIFFReader.readUnsignedShort();
            SF2LayerRegion sF2LayerRegion = new SF2LayerRegion();
            sF2Layer.regions.add(sF2LayerRegion);
            while (arrayList7.size() < n)
              arrayList7.add(sF2LayerRegion); 
            while (arrayList8.size() < i1)
              arrayList8.add(sF2LayerRegion); 
            i--;
          } 
        } 
        continue;
      } 
      if (str.equals("imod")) {
        for (byte b = 0; b < arrayList8.size(); b++) {
          SF2Modulator sF2Modulator = new SF2Modulator();
          sF2Modulator.sourceOperator = rIFFReader.readUnsignedShort();
          sF2Modulator.destinationOperator = rIFFReader.readUnsignedShort();
          sF2Modulator.amount = rIFFReader.readShort();
          sF2Modulator.amountSourceOperator = rIFFReader.readUnsignedShort();
          sF2Modulator.transportOperator = rIFFReader.readUnsignedShort();
          if (!b || b >= arrayList7.size())
            throw new RIFFInvalidDataException(); 
          SF2LayerRegion sF2LayerRegion = (SF2LayerRegion)arrayList7.get(b);
          if (sF2LayerRegion != null)
            sF2LayerRegion.modulators.add(sF2Modulator); 
        } 
        continue;
      } 
      if (str.equals("igen")) {
        for (byte b = 0; b < arrayList7.size(); b++) {
          int i = rIFFReader.readUnsignedShort();
          short s = rIFFReader.readShort();
          SF2LayerRegion sF2LayerRegion = (SF2LayerRegion)arrayList7.get(b);
          if (sF2LayerRegion != null)
            sF2LayerRegion.generators.put(Integer.valueOf(i), Short.valueOf(s)); 
        } 
        continue;
      } 
      if (str.equals("shdr")) {
        if (rIFFReader.available() % 46 != 0)
          throw new RIFFInvalidDataException(); 
        int i = rIFFReader.available() / 46;
        for (byte b = 0; b < i; b++) {
          SF2Sample sF2Sample = new SF2Sample(this);
          sF2Sample.name = rIFFReader.readString(20);
          long l1 = rIFFReader.readUnsignedInt();
          long l2 = rIFFReader.readUnsignedInt();
          if (this.sampleData != null)
            sF2Sample.data = this.sampleData.subbuffer(l1 * 2L, l2 * 2L, true); 
          if (this.sampleData24 != null)
            sF2Sample.data24 = this.sampleData24.subbuffer(l1, l2, true); 
          sF2Sample.startLoop = rIFFReader.readUnsignedInt() - l1;
          sF2Sample.endLoop = rIFFReader.readUnsignedInt() - l1;
          if (sF2Sample.startLoop < 0L)
            sF2Sample.startLoop = -1L; 
          if (sF2Sample.endLoop < 0L)
            sF2Sample.endLoop = -1L; 
          sF2Sample.sampleRate = rIFFReader.readUnsignedInt();
          sF2Sample.originalPitch = rIFFReader.readUnsignedByte();
          sF2Sample.pitchCorrection = rIFFReader.readByte();
          sF2Sample.sampleLink = rIFFReader.readUnsignedShort();
          sF2Sample.sampleType = rIFFReader.readUnsignedShort();
          if (b != i - 1)
            this.samples.add(sF2Sample); 
        } 
      } 
    } 
    for (SF2Layer sF2Layer : this.layers) {
      Iterator iterator = sF2Layer.regions.iterator();
      SF2LayerRegion sF2LayerRegion;
      for (sF2LayerRegion = null; iterator.hasNext(); sF2LayerRegion = sF2LayerRegion1) {
        SF2LayerRegion sF2LayerRegion1 = (SF2LayerRegion)iterator.next();
        if (sF2LayerRegion1.generators.get(Integer.valueOf(53)) != null) {
          short s = ((Short)sF2LayerRegion1.generators.get(Integer.valueOf(53))).shortValue();
          sF2LayerRegion1.generators.remove(Integer.valueOf(53));
          if (s < 0 || s >= this.samples.size())
            throw new RIFFInvalidDataException(); 
          sF2LayerRegion1.sample = (SF2Sample)this.samples.get(s);
          continue;
        } 
      } 
      if (sF2LayerRegion != null) {
        sF2Layer.getRegions().remove(sF2LayerRegion);
        SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
        sF2GlobalRegion.generators = sF2LayerRegion.generators;
        sF2GlobalRegion.modulators = sF2LayerRegion.modulators;
        sF2Layer.setGlobalZone(sF2GlobalRegion);
      } 
    } 
    for (SF2Instrument sF2Instrument : this.instruments) {
      Iterator iterator = sF2Instrument.regions.iterator();
      SF2InstrumentRegion sF2InstrumentRegion;
      for (sF2InstrumentRegion = null; iterator.hasNext(); sF2InstrumentRegion = sF2InstrumentRegion1) {
        SF2InstrumentRegion sF2InstrumentRegion1 = (SF2InstrumentRegion)iterator.next();
        if (sF2InstrumentRegion1.generators.get(Integer.valueOf(41)) != null) {
          short s = ((Short)sF2InstrumentRegion1.generators.get(Integer.valueOf(41))).shortValue();
          sF2InstrumentRegion1.generators.remove(Integer.valueOf(41));
          if (s < 0 || s >= this.layers.size())
            throw new RIFFInvalidDataException(); 
          sF2InstrumentRegion1.layer = (SF2Layer)this.layers.get(s);
          continue;
        } 
      } 
      if (sF2InstrumentRegion != null) {
        sF2Instrument.getRegions().remove(sF2InstrumentRegion);
        SF2GlobalRegion sF2GlobalRegion = new SF2GlobalRegion();
        sF2GlobalRegion.generators = sF2InstrumentRegion.generators;
        sF2GlobalRegion.modulators = sF2InstrumentRegion.modulators;
        sF2Instrument.setGlobalZone(sF2GlobalRegion);
      } 
    } 
  }
  
  public void save(String paramString) throws IOException { writeSoundbank(new RIFFWriter(paramString, "sfbk")); }
  
  public void save(File paramFile) throws IOException { writeSoundbank(new RIFFWriter(paramFile, "sfbk")); }
  
  public void save(OutputStream paramOutputStream) throws IOException { writeSoundbank(new RIFFWriter(paramOutputStream, "sfbk")); }
  
  private void writeSoundbank(RIFFWriter paramRIFFWriter) throws IOException {
    writeInfo(paramRIFFWriter.writeList("INFO"));
    writeSdtaChunk(paramRIFFWriter.writeList("sdta"));
    writePdtaChunk(paramRIFFWriter.writeList("pdta"));
    paramRIFFWriter.close();
  }
  
  private void writeInfoStringChunk(RIFFWriter paramRIFFWriter, String paramString1, String paramString2) throws IOException {
    if (paramString2 == null)
      return; 
    RIFFWriter rIFFWriter = paramRIFFWriter.writeChunk(paramString1);
    rIFFWriter.writeString(paramString2);
    int i = paramString2.getBytes("ascii").length;
    rIFFWriter.write(0);
    if (++i % 2 != 0)
      rIFFWriter.write(0); 
  }
  
  private void writeInfo(RIFFWriter paramRIFFWriter) throws IOException {
    if (this.targetEngine == null)
      this.targetEngine = "EMU8000"; 
    if (this.name == null)
      this.name = ""; 
    RIFFWriter rIFFWriter = paramRIFFWriter.writeChunk("ifil");
    rIFFWriter.writeUnsignedShort(this.major);
    rIFFWriter.writeUnsignedShort(this.minor);
    writeInfoStringChunk(paramRIFFWriter, "isng", this.targetEngine);
    writeInfoStringChunk(paramRIFFWriter, "INAM", this.name);
    writeInfoStringChunk(paramRIFFWriter, "irom", this.romName);
    if (this.romVersionMajor != -1) {
      RIFFWriter rIFFWriter1 = paramRIFFWriter.writeChunk("iver");
      rIFFWriter1.writeUnsignedShort(this.romVersionMajor);
      rIFFWriter1.writeUnsignedShort(this.romVersionMinor);
    } 
    writeInfoStringChunk(paramRIFFWriter, "ICRD", this.creationDate);
    writeInfoStringChunk(paramRIFFWriter, "IENG", this.engineers);
    writeInfoStringChunk(paramRIFFWriter, "IPRD", this.product);
    writeInfoStringChunk(paramRIFFWriter, "ICOP", this.copyright);
    writeInfoStringChunk(paramRIFFWriter, "ICMT", this.comments);
    writeInfoStringChunk(paramRIFFWriter, "ISFT", this.tools);
    paramRIFFWriter.close();
  }
  
  private void writeSdtaChunk(RIFFWriter paramRIFFWriter) throws IOException {
    byte[] arrayOfByte = new byte[32];
    RIFFWriter rIFFWriter1 = paramRIFFWriter.writeChunk("smpl");
    for (SF2Sample sF2Sample : this.samples) {
      ModelByteBuffer modelByteBuffer = sF2Sample.getDataBuffer();
      modelByteBuffer.writeTo(rIFFWriter1);
      rIFFWriter1.write(arrayOfByte);
      rIFFWriter1.write(arrayOfByte);
    } 
    if (this.major < 2)
      return; 
    if (this.major == 2 && this.minor < 4)
      return; 
    for (SF2Sample sF2Sample : this.samples) {
      ModelByteBuffer modelByteBuffer = sF2Sample.getData24Buffer();
      if (modelByteBuffer == null)
        return; 
    } 
    RIFFWriter rIFFWriter2 = paramRIFFWriter.writeChunk("sm24");
    for (SF2Sample sF2Sample : this.samples) {
      ModelByteBuffer modelByteBuffer = sF2Sample.getData24Buffer();
      modelByteBuffer.writeTo(rIFFWriter2);
      rIFFWriter1.write(arrayOfByte);
    } 
  }
  
  private void writeModulators(RIFFWriter paramRIFFWriter, List<SF2Modulator> paramList) throws IOException {
    for (SF2Modulator sF2Modulator : paramList) {
      paramRIFFWriter.writeUnsignedShort(sF2Modulator.sourceOperator);
      paramRIFFWriter.writeUnsignedShort(sF2Modulator.destinationOperator);
      paramRIFFWriter.writeShort(sF2Modulator.amount);
      paramRIFFWriter.writeUnsignedShort(sF2Modulator.amountSourceOperator);
      paramRIFFWriter.writeUnsignedShort(sF2Modulator.transportOperator);
    } 
  }
  
  private void writeGenerators(RIFFWriter paramRIFFWriter, Map<Integer, Short> paramMap) throws IOException {
    Short short1 = (Short)paramMap.get(Integer.valueOf(43));
    Short short2 = (Short)paramMap.get(Integer.valueOf(44));
    if (short1 != null) {
      paramRIFFWriter.writeUnsignedShort(43);
      paramRIFFWriter.writeShort(short1.shortValue());
    } 
    if (short2 != null) {
      paramRIFFWriter.writeUnsignedShort(44);
      paramRIFFWriter.writeShort(short2.shortValue());
    } 
    for (Map.Entry entry : paramMap.entrySet()) {
      if (((Integer)entry.getKey()).intValue() == 43 || ((Integer)entry.getKey()).intValue() == 44)
        continue; 
      paramRIFFWriter.writeUnsignedShort(((Integer)entry.getKey()).intValue());
      paramRIFFWriter.writeShort(((Short)entry.getValue()).shortValue());
    } 
  }
  
  private void writePdtaChunk(RIFFWriter paramRIFFWriter) throws IOException {
    RIFFWriter rIFFWriter1 = paramRIFFWriter.writeChunk("phdr");
    int i = 0;
    for (SF2Instrument sF2Instrument : this.instruments) {
      rIFFWriter1.writeString(sF2Instrument.name, 20);
      rIFFWriter1.writeUnsignedShort(sF2Instrument.preset);
      rIFFWriter1.writeUnsignedShort(sF2Instrument.bank);
      rIFFWriter1.writeUnsignedShort(i);
      if (sF2Instrument.getGlobalRegion() != null)
        i++; 
      i += sF2Instrument.getRegions().size();
      rIFFWriter1.writeUnsignedInt(sF2Instrument.library);
      rIFFWriter1.writeUnsignedInt(sF2Instrument.genre);
      rIFFWriter1.writeUnsignedInt(sF2Instrument.morphology);
    } 
    rIFFWriter1.writeString("EOP", 20);
    rIFFWriter1.writeUnsignedShort(0);
    rIFFWriter1.writeUnsignedShort(0);
    rIFFWriter1.writeUnsignedShort(i);
    rIFFWriter1.writeUnsignedInt(0L);
    rIFFWriter1.writeUnsignedInt(0L);
    rIFFWriter1.writeUnsignedInt(0L);
    RIFFWriter rIFFWriter2 = paramRIFFWriter.writeChunk("pbag");
    int j = 0;
    int k = 0;
    for (SF2Instrument sF2Instrument : this.instruments) {
      if (sF2Instrument.getGlobalRegion() != null) {
        rIFFWriter2.writeUnsignedShort(j);
        rIFFWriter2.writeUnsignedShort(k);
        j += sF2Instrument.getGlobalRegion().getGenerators().size();
        k += sF2Instrument.getGlobalRegion().getModulators().size();
      } 
      for (SF2InstrumentRegion sF2InstrumentRegion : sF2Instrument.getRegions()) {
        rIFFWriter2.writeUnsignedShort(j);
        rIFFWriter2.writeUnsignedShort(k);
        if (this.layers.indexOf(sF2InstrumentRegion.layer) != -1)
          j++; 
        j += sF2InstrumentRegion.getGenerators().size();
        k += sF2InstrumentRegion.getModulators().size();
      } 
    } 
    rIFFWriter2.writeUnsignedShort(j);
    rIFFWriter2.writeUnsignedShort(k);
    RIFFWriter rIFFWriter3 = paramRIFFWriter.writeChunk("pmod");
    for (SF2Instrument sF2Instrument : this.instruments) {
      if (sF2Instrument.getGlobalRegion() != null)
        writeModulators(rIFFWriter3, sF2Instrument.getGlobalRegion().getModulators()); 
      for (SF2InstrumentRegion sF2InstrumentRegion : sF2Instrument.getRegions())
        writeModulators(rIFFWriter3, sF2InstrumentRegion.getModulators()); 
    } 
    rIFFWriter3.write(new byte[10]);
    RIFFWriter rIFFWriter4 = paramRIFFWriter.writeChunk("pgen");
    for (SF2Instrument sF2Instrument : this.instruments) {
      if (sF2Instrument.getGlobalRegion() != null)
        writeGenerators(rIFFWriter4, sF2Instrument.getGlobalRegion().getGenerators()); 
      for (SF2InstrumentRegion sF2InstrumentRegion : sF2Instrument.getRegions()) {
        writeGenerators(rIFFWriter4, sF2InstrumentRegion.getGenerators());
        int i2 = this.layers.indexOf(sF2InstrumentRegion.layer);
        if (i2 != -1) {
          rIFFWriter4.writeUnsignedShort(41);
          rIFFWriter4.writeShort((short)i2);
        } 
      } 
    } 
    rIFFWriter4.write(new byte[4]);
    RIFFWriter rIFFWriter5 = paramRIFFWriter.writeChunk("inst");
    int m = 0;
    for (SF2Layer sF2Layer : this.layers) {
      rIFFWriter5.writeString(sF2Layer.name, 20);
      rIFFWriter5.writeUnsignedShort(m);
      if (sF2Layer.getGlobalRegion() != null)
        m++; 
      m += sF2Layer.getRegions().size();
    } 
    rIFFWriter5.writeString("EOI", 20);
    rIFFWriter5.writeUnsignedShort(m);
    RIFFWriter rIFFWriter6 = paramRIFFWriter.writeChunk("ibag");
    int n = 0;
    int i1 = 0;
    for (SF2Layer sF2Layer : this.layers) {
      if (sF2Layer.getGlobalRegion() != null) {
        rIFFWriter6.writeUnsignedShort(n);
        rIFFWriter6.writeUnsignedShort(i1);
        n += sF2Layer.getGlobalRegion().getGenerators().size();
        i1 += sF2Layer.getGlobalRegion().getModulators().size();
      } 
      for (SF2LayerRegion sF2LayerRegion : sF2Layer.getRegions()) {
        rIFFWriter6.writeUnsignedShort(n);
        rIFFWriter6.writeUnsignedShort(i1);
        if (this.samples.indexOf(sF2LayerRegion.sample) != -1)
          n++; 
        n += sF2LayerRegion.getGenerators().size();
        i1 += sF2LayerRegion.getModulators().size();
      } 
    } 
    rIFFWriter6.writeUnsignedShort(n);
    rIFFWriter6.writeUnsignedShort(i1);
    RIFFWriter rIFFWriter7 = paramRIFFWriter.writeChunk("imod");
    for (SF2Layer sF2Layer : this.layers) {
      if (sF2Layer.getGlobalRegion() != null)
        writeModulators(rIFFWriter7, sF2Layer.getGlobalRegion().getModulators()); 
      for (SF2LayerRegion sF2LayerRegion : sF2Layer.getRegions())
        writeModulators(rIFFWriter7, sF2LayerRegion.getModulators()); 
    } 
    rIFFWriter7.write(new byte[10]);
    RIFFWriter rIFFWriter8 = paramRIFFWriter.writeChunk("igen");
    for (SF2Layer sF2Layer : this.layers) {
      if (sF2Layer.getGlobalRegion() != null)
        writeGenerators(rIFFWriter8, sF2Layer.getGlobalRegion().getGenerators()); 
      for (SF2LayerRegion sF2LayerRegion : sF2Layer.getRegions()) {
        writeGenerators(rIFFWriter8, sF2LayerRegion.getGenerators());
        int i2 = this.samples.indexOf(sF2LayerRegion.sample);
        if (i2 != -1) {
          rIFFWriter8.writeUnsignedShort(53);
          rIFFWriter8.writeShort((short)i2);
        } 
      } 
    } 
    rIFFWriter8.write(new byte[4]);
    RIFFWriter rIFFWriter9 = paramRIFFWriter.writeChunk("shdr");
    long l = 0L;
    for (SF2Sample sF2Sample : this.samples) {
      rIFFWriter9.writeString(sF2Sample.name, 20);
      long l1 = l;
      l += sF2Sample.data.capacity() / 2L;
      long l2 = l;
      long l3 = sF2Sample.startLoop + l1;
      long l4 = sF2Sample.endLoop + l1;
      if (l3 < l1)
        l3 = l1; 
      if (l4 > l2)
        l4 = l2; 
      rIFFWriter9.writeUnsignedInt(l1);
      rIFFWriter9.writeUnsignedInt(l2);
      rIFFWriter9.writeUnsignedInt(l3);
      rIFFWriter9.writeUnsignedInt(l4);
      rIFFWriter9.writeUnsignedInt(sF2Sample.sampleRate);
      rIFFWriter9.writeUnsignedByte(sF2Sample.originalPitch);
      rIFFWriter9.writeByte(sF2Sample.pitchCorrection);
      rIFFWriter9.writeUnsignedShort(sF2Sample.sampleLink);
      rIFFWriter9.writeUnsignedShort(sF2Sample.sampleType);
      l += 32L;
    } 
    rIFFWriter9.writeString("EOS", 20);
    rIFFWriter9.write(new byte[26]);
  }
  
  public String getName() { return this.name; }
  
  public String getVersion() { return this.major + "." + this.minor; }
  
  public String getVendor() { return this.engineers; }
  
  public String getDescription() { return this.comments; }
  
  public void setName(String paramString) throws IOException { this.name = paramString; }
  
  public void setVendor(String paramString) throws IOException { this.engineers = paramString; }
  
  public void setDescription(String paramString) throws IOException { this.comments = paramString; }
  
  public SoundbankResource[] getResources() {
    SoundbankResource[] arrayOfSoundbankResource = new SoundbankResource[this.layers.size() + this.samples.size()];
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < this.layers.size(); b2++)
      arrayOfSoundbankResource[b1++] = (SoundbankResource)this.layers.get(b2); 
    for (b2 = 0; b2 < this.samples.size(); b2++)
      arrayOfSoundbankResource[b1++] = (SoundbankResource)this.samples.get(b2); 
    return arrayOfSoundbankResource;
  }
  
  public SF2Instrument[] getInstruments() {
    SF2Instrument[] arrayOfSF2Instrument = (SF2Instrument[])this.instruments.toArray(new SF2Instrument[this.instruments.size()]);
    Arrays.sort(arrayOfSF2Instrument, new ModelInstrumentComparator());
    return arrayOfSF2Instrument;
  }
  
  public SF2Layer[] getLayers() { return (SF2Layer[])this.layers.toArray(new SF2Layer[this.layers.size()]); }
  
  public SF2Sample[] getSamples() { return (SF2Sample[])this.samples.toArray(new SF2Sample[this.samples.size()]); }
  
  public Instrument getInstrument(Patch paramPatch) {
    int i = paramPatch.getProgram();
    int j = paramPatch.getBank();
    boolean bool = false;
    if (paramPatch instanceof ModelPatch)
      bool = ((ModelPatch)paramPatch).isPercussion(); 
    for (Instrument instrument : this.instruments) {
      Patch patch = instrument.getPatch();
      int k = patch.getProgram();
      int m = patch.getBank();
      if (i == k && j == m) {
        boolean bool1 = false;
        if (patch instanceof ModelPatch)
          bool1 = ((ModelPatch)patch).isPercussion(); 
        if (bool == bool1)
          return instrument; 
      } 
    } 
    return null;
  }
  
  public String getCreationDate() { return this.creationDate; }
  
  public void setCreationDate(String paramString) throws IOException { this.creationDate = paramString; }
  
  public String getProduct() { return this.product; }
  
  public void setProduct(String paramString) throws IOException { this.product = paramString; }
  
  public String getRomName() { return this.romName; }
  
  public void setRomName(String paramString) throws IOException { this.romName = paramString; }
  
  public int getRomVersionMajor() { return this.romVersionMajor; }
  
  public void setRomVersionMajor(int paramInt) { this.romVersionMajor = paramInt; }
  
  public int getRomVersionMinor() { return this.romVersionMinor; }
  
  public void setRomVersionMinor(int paramInt) { this.romVersionMinor = paramInt; }
  
  public String getTargetEngine() { return this.targetEngine; }
  
  public void setTargetEngine(String paramString) throws IOException { this.targetEngine = paramString; }
  
  public String getTools() { return this.tools; }
  
  public void setTools(String paramString) throws IOException { this.tools = paramString; }
  
  public void addResource(SoundbankResource paramSoundbankResource) {
    if (paramSoundbankResource instanceof SF2Instrument)
      this.instruments.add((SF2Instrument)paramSoundbankResource); 
    if (paramSoundbankResource instanceof SF2Layer)
      this.layers.add((SF2Layer)paramSoundbankResource); 
    if (paramSoundbankResource instanceof SF2Sample)
      this.samples.add((SF2Sample)paramSoundbankResource); 
  }
  
  public void removeResource(SoundbankResource paramSoundbankResource) {
    if (paramSoundbankResource instanceof SF2Instrument)
      this.instruments.remove((SF2Instrument)paramSoundbankResource); 
    if (paramSoundbankResource instanceof SF2Layer)
      this.layers.remove((SF2Layer)paramSoundbankResource); 
    if (paramSoundbankResource instanceof SF2Sample)
      this.samples.remove((SF2Sample)paramSoundbankResource); 
  }
  
  public void addInstrument(SF2Instrument paramSF2Instrument) { this.instruments.add(paramSF2Instrument); }
  
  public void removeInstrument(SF2Instrument paramSF2Instrument) { this.instruments.remove(paramSF2Instrument); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SF2Soundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */