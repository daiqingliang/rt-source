package com.sun.media.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public final class DLSSoundbank implements Soundbank {
  private static final int DLS_CDL_AND = 1;
  
  private static final int DLS_CDL_OR = 2;
  
  private static final int DLS_CDL_XOR = 3;
  
  private static final int DLS_CDL_ADD = 4;
  
  private static final int DLS_CDL_SUBTRACT = 5;
  
  private static final int DLS_CDL_MULTIPLY = 6;
  
  private static final int DLS_CDL_DIVIDE = 7;
  
  private static final int DLS_CDL_LOGICAL_AND = 8;
  
  private static final int DLS_CDL_LOGICAL_OR = 9;
  
  private static final int DLS_CDL_LT = 10;
  
  private static final int DLS_CDL_LE = 11;
  
  private static final int DLS_CDL_GT = 12;
  
  private static final int DLS_CDL_GE = 13;
  
  private static final int DLS_CDL_EQ = 14;
  
  private static final int DLS_CDL_NOT = 15;
  
  private static final int DLS_CDL_CONST = 16;
  
  private static final int DLS_CDL_QUERY = 17;
  
  private static final int DLS_CDL_QUERYSUPPORTED = 18;
  
  private static final DLSID DLSID_GMInHardware = new DLSID(395259684L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  
  private static final DLSID DLSID_GSInHardware = new DLSID(395259685L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  
  private static final DLSID DLSID_XGInHardware = new DLSID(395259686L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  
  private static final DLSID DLSID_SupportsDLS1 = new DLSID(395259687L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  
  private static final DLSID DLSID_SupportsDLS2 = new DLSID(-247096859L, 18057, 4562, 175, 166, 0, 170, 0, 36, 216, 182);
  
  private static final DLSID DLSID_SampleMemorySize = new DLSID(395259688L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
  
  private static final DLSID DLSID_ManufacturersID = new DLSID(-1338109567L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
  
  private static final DLSID DLSID_ProductID = new DLSID(-1338109566L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
  
  private static final DLSID DLSID_SamplePlaybackRate = new DLSID(714209043L, 42175, 4562, 187, 223, 0, 96, 8, 51, 219, 216);
  
  private long major = -1L;
  
  private long minor = -1L;
  
  private final DLSInfo info = new DLSInfo();
  
  private final List<DLSInstrument> instruments = new ArrayList();
  
  private final List<DLSSample> samples = new ArrayList();
  
  private boolean largeFormat = false;
  
  private File sampleFile;
  
  private Map<DLSRegion, Long> temp_rgnassign = new HashMap();
  
  public DLSSoundbank() {}
  
  public DLSSoundbank(URL paramURL) throws IOException {
    inputStream = paramURL.openStream();
    try {
      readSoundbank(inputStream);
    } finally {
      inputStream.close();
    } 
  }
  
  public DLSSoundbank(File paramFile) throws IOException {
    this.largeFormat = true;
    this.sampleFile = paramFile;
    fileInputStream = new FileInputStream(paramFile);
    try {
      readSoundbank(fileInputStream);
    } finally {
      fileInputStream.close();
    } 
  }
  
  public DLSSoundbank(InputStream paramInputStream) throws IOException { readSoundbank(paramInputStream); }
  
  private void readSoundbank(InputStream paramInputStream) throws IOException { // Byte code:
    //   0: new com/sun/media/sound/RIFFReader
    //   3: dup
    //   4: aload_1
    //   5: invokespecial <init> : (Ljava/io/InputStream;)V
    //   8: astore_2
    //   9: aload_2
    //   10: invokevirtual getFormat : ()Ljava/lang/String;
    //   13: ldc 'RIFF'
    //   15: invokevirtual equals : (Ljava/lang/Object;)Z
    //   18: ifne -> 31
    //   21: new com/sun/media/sound/RIFFInvalidFormatException
    //   24: dup
    //   25: ldc 'Input stream is not a valid RIFF stream!'
    //   27: invokespecial <init> : (Ljava/lang/String;)V
    //   30: athrow
    //   31: aload_2
    //   32: invokevirtual getType : ()Ljava/lang/String;
    //   35: ldc 'DLS '
    //   37: invokevirtual equals : (Ljava/lang/Object;)Z
    //   40: ifne -> 53
    //   43: new com/sun/media/sound/RIFFInvalidFormatException
    //   46: dup
    //   47: ldc 'Input stream is not a valid DLS soundbank!'
    //   49: invokespecial <init> : (Ljava/lang/String;)V
    //   52: athrow
    //   53: aload_2
    //   54: invokevirtual hasNextChunk : ()Z
    //   57: ifeq -> 216
    //   60: aload_2
    //   61: invokevirtual nextChunk : ()Lcom/sun/media/sound/RIFFReader;
    //   64: astore_3
    //   65: aload_3
    //   66: invokevirtual getFormat : ()Ljava/lang/String;
    //   69: ldc 'LIST'
    //   71: invokevirtual equals : (Ljava/lang/Object;)Z
    //   74: ifeq -> 131
    //   77: aload_3
    //   78: invokevirtual getType : ()Ljava/lang/String;
    //   81: ldc 'INFO'
    //   83: invokevirtual equals : (Ljava/lang/Object;)Z
    //   86: ifeq -> 94
    //   89: aload_0
    //   90: aload_3
    //   91: invokespecial readInfoChunk : (Lcom/sun/media/sound/RIFFReader;)V
    //   94: aload_3
    //   95: invokevirtual getType : ()Ljava/lang/String;
    //   98: ldc 'lins'
    //   100: invokevirtual equals : (Ljava/lang/Object;)Z
    //   103: ifeq -> 111
    //   106: aload_0
    //   107: aload_3
    //   108: invokespecial readLinsChunk : (Lcom/sun/media/sound/RIFFReader;)V
    //   111: aload_3
    //   112: invokevirtual getType : ()Ljava/lang/String;
    //   115: ldc 'wvpl'
    //   117: invokevirtual equals : (Ljava/lang/Object;)Z
    //   120: ifeq -> 213
    //   123: aload_0
    //   124: aload_3
    //   125: invokespecial readWvplChunk : (Lcom/sun/media/sound/RIFFReader;)V
    //   128: goto -> 213
    //   131: aload_3
    //   132: invokevirtual getFormat : ()Ljava/lang/String;
    //   135: ldc 'cdl '
    //   137: invokevirtual equals : (Ljava/lang/Object;)Z
    //   140: ifeq -> 161
    //   143: aload_0
    //   144: aload_3
    //   145: invokespecial readCdlChunk : (Lcom/sun/media/sound/RIFFReader;)Z
    //   148: ifne -> 161
    //   151: new com/sun/media/sound/RIFFInvalidFormatException
    //   154: dup
    //   155: ldc 'DLS file isn't supported!'
    //   157: invokespecial <init> : (Ljava/lang/String;)V
    //   160: athrow
    //   161: aload_3
    //   162: invokevirtual getFormat : ()Ljava/lang/String;
    //   165: ldc 'colh'
    //   167: invokevirtual equals : (Ljava/lang/Object;)Z
    //   170: ifeq -> 173
    //   173: aload_3
    //   174: invokevirtual getFormat : ()Ljava/lang/String;
    //   177: ldc 'ptbl'
    //   179: invokevirtual equals : (Ljava/lang/Object;)Z
    //   182: ifeq -> 185
    //   185: aload_3
    //   186: invokevirtual getFormat : ()Ljava/lang/String;
    //   189: ldc 'vers'
    //   191: invokevirtual equals : (Ljava/lang/Object;)Z
    //   194: ifeq -> 213
    //   197: aload_0
    //   198: aload_3
    //   199: invokevirtual readUnsignedInt : ()J
    //   202: putfield major : J
    //   205: aload_0
    //   206: aload_3
    //   207: invokevirtual readUnsignedInt : ()J
    //   210: putfield minor : J
    //   213: goto -> 53
    //   216: aload_0
    //   217: getfield temp_rgnassign : Ljava/util/Map;
    //   220: invokeinterface entrySet : ()Ljava/util/Set;
    //   225: invokeinterface iterator : ()Ljava/util/Iterator;
    //   230: astore_3
    //   231: aload_3
    //   232: invokeinterface hasNext : ()Z
    //   237: ifeq -> 293
    //   240: aload_3
    //   241: invokeinterface next : ()Ljava/lang/Object;
    //   246: checkcast java/util/Map$Entry
    //   249: astore #4
    //   251: aload #4
    //   253: invokeinterface getKey : ()Ljava/lang/Object;
    //   258: checkcast com/sun/media/sound/DLSRegion
    //   261: aload_0
    //   262: getfield samples : Ljava/util/List;
    //   265: aload #4
    //   267: invokeinterface getValue : ()Ljava/lang/Object;
    //   272: checkcast java/lang/Long
    //   275: invokevirtual longValue : ()J
    //   278: l2i
    //   279: invokeinterface get : (I)Ljava/lang/Object;
    //   284: checkcast com/sun/media/sound/DLSSample
    //   287: putfield sample : Lcom/sun/media/sound/DLSSample;
    //   290: goto -> 231
    //   293: aload_0
    //   294: aconst_null
    //   295: putfield temp_rgnassign : Ljava/util/Map;
    //   298: return }
  
  private boolean cdlIsQuerySupported(DLSID paramDLSID) { return (paramDLSID.equals(DLSID_GMInHardware) || paramDLSID.equals(DLSID_GSInHardware) || paramDLSID.equals(DLSID_XGInHardware) || paramDLSID.equals(DLSID_SupportsDLS1) || paramDLSID.equals(DLSID_SupportsDLS2) || paramDLSID.equals(DLSID_SampleMemorySize) || paramDLSID.equals(DLSID_ManufacturersID) || paramDLSID.equals(DLSID_ProductID) || paramDLSID.equals(DLSID_SamplePlaybackRate)); }
  
  private long cdlQuery(DLSID paramDLSID) { return paramDLSID.equals(DLSID_GMInHardware) ? 1L : (paramDLSID.equals(DLSID_GSInHardware) ? 0L : (paramDLSID.equals(DLSID_XGInHardware) ? 0L : (paramDLSID.equals(DLSID_SupportsDLS1) ? 1L : (paramDLSID.equals(DLSID_SupportsDLS2) ? 1L : (paramDLSID.equals(DLSID_SampleMemorySize) ? Runtime.getRuntime().totalMemory() : (paramDLSID.equals(DLSID_ManufacturersID) ? 0L : (paramDLSID.equals(DLSID_ProductID) ? 0L : (paramDLSID.equals(DLSID_SamplePlaybackRate) ? 44100L : 0L)))))))); }
  
  private boolean readCdlChunk(RIFFReader paramRIFFReader) throws IOException {
    Stack stack = new Stack();
    while (paramRIFFReader.available() != 0) {
      long l2;
      long l1;
      DLSID dLSID;
      int i = paramRIFFReader.readUnsignedShort();
      switch (i) {
        case 1:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 != 0L && l2 != 0L) ? 1L : 0L));
        case 2:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 != 0L || l2 != 0L) ? 1L : 0L));
        case 3:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((((l1 != 0L) ? 1 : 0) ^ ((l2 != 0L) ? 1 : 0)) ? 1L : 0L));
        case 4:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf(l1 + l2));
        case 5:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf(l1 - l2));
        case 6:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf(l1 * l2));
        case 7:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf(l1 / l2));
        case 8:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 != 0L && l2 != 0L) ? 1L : 0L));
        case 9:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 != 0L || l2 != 0L) ? 1L : 0L));
        case 10:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 < l2) ? 1L : 0L));
        case 11:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 <= l2) ? 1L : 0L));
        case 12:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 > l2) ? 1L : 0L));
        case 13:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 >= l2) ? 1L : 0L));
        case 14:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 == l2) ? 1L : 0L));
        case 15:
          l1 = ((Long)stack.pop()).longValue();
          l2 = ((Long)stack.pop()).longValue();
          stack.push(Long.valueOf((l1 == 0L) ? 1L : 0L));
        case 16:
          stack.push(Long.valueOf(paramRIFFReader.readUnsignedInt()));
        case 17:
          dLSID = DLSID.read(paramRIFFReader);
          stack.push(Long.valueOf(cdlQuery(dLSID)));
        case 18:
          dLSID = DLSID.read(paramRIFFReader);
          stack.push(Long.valueOf(cdlIsQuerySupported(dLSID) ? 1L : 0L));
      } 
    } 
    return stack.isEmpty() ? false : ((((Long)stack.pop()).longValue() == 1L));
  }
  
  private void readInfoChunk(RIFFReader paramRIFFReader) throws IOException {
    this.info.name = null;
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      String str = rIFFReader.getFormat();
      if (str.equals("INAM")) {
        this.info.name = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICRD")) {
        this.info.creationDate = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IENG")) {
        this.info.engineers = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IPRD")) {
        this.info.product = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICOP")) {
        this.info.copyright = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICMT")) {
        this.info.comments = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISFT")) {
        this.info.tools = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IARL")) {
        this.info.archival_location = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IART")) {
        this.info.artist = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICMS")) {
        this.info.commissioned = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IGNR")) {
        this.info.genre = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IKEY")) {
        this.info.keywords = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IMED")) {
        this.info.medium = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISBJ")) {
        this.info.subject = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISRC")) {
        this.info.source = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISRF")) {
        this.info.source_form = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ITCH"))
        this.info.technician = rIFFReader.readString(rIFFReader.available()); 
    } 
  }
  
  private void readLinsChunk(RIFFReader paramRIFFReader) throws IOException {
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      if (rIFFReader.getFormat().equals("LIST") && rIFFReader.getType().equals("ins "))
        readInsChunk(rIFFReader); 
    } 
  }
  
  private void readInsChunk(RIFFReader paramRIFFReader) throws IOException {
    DLSInstrument dLSInstrument = new DLSInstrument(this);
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      String str = rIFFReader.getFormat();
      if (str.equals("LIST")) {
        if (rIFFReader.getType().equals("INFO"))
          readInsInfoChunk(dLSInstrument, rIFFReader); 
        if (rIFFReader.getType().equals("lrgn"))
          while (rIFFReader.hasNextChunk()) {
            RIFFReader rIFFReader1 = rIFFReader.nextChunk();
            if (rIFFReader1.getFormat().equals("LIST")) {
              if (rIFFReader1.getType().equals("rgn ")) {
                DLSRegion dLSRegion = new DLSRegion();
                if (readRgnChunk(dLSRegion, rIFFReader1))
                  dLSInstrument.getRegions().add(dLSRegion); 
              } 
              if (rIFFReader1.getType().equals("rgn2")) {
                DLSRegion dLSRegion = new DLSRegion();
                if (readRgnChunk(dLSRegion, rIFFReader1))
                  dLSInstrument.getRegions().add(dLSRegion); 
              } 
            } 
          }  
        if (rIFFReader.getType().equals("lart")) {
          ArrayList arrayList = new ArrayList();
          while (rIFFReader.hasNextChunk()) {
            RIFFReader rIFFReader1 = rIFFReader.nextChunk();
            if (rIFFReader.getFormat().equals("cdl ") && !readCdlChunk(rIFFReader)) {
              arrayList.clear();
              break;
            } 
            if (rIFFReader1.getFormat().equals("art1"))
              readArt1Chunk(arrayList, rIFFReader1); 
          } 
          dLSInstrument.getModulators().addAll(arrayList);
        } 
        if (rIFFReader.getType().equals("lar2")) {
          ArrayList arrayList = new ArrayList();
          while (rIFFReader.hasNextChunk()) {
            RIFFReader rIFFReader1 = rIFFReader.nextChunk();
            if (rIFFReader.getFormat().equals("cdl ") && !readCdlChunk(rIFFReader)) {
              arrayList.clear();
              break;
            } 
            if (rIFFReader1.getFormat().equals("art2"))
              readArt2Chunk(arrayList, rIFFReader1); 
          } 
          dLSInstrument.getModulators().addAll(arrayList);
        } 
        continue;
      } 
      if (str.equals("dlid")) {
        dLSInstrument.guid = new byte[16];
        rIFFReader.readFully(dLSInstrument.guid);
      } 
      if (str.equals("insh")) {
        rIFFReader.readUnsignedInt();
        int i = rIFFReader.read();
        i += ((rIFFReader.read() & 0x7F) << 7);
        rIFFReader.read();
        int j = rIFFReader.read();
        int k = rIFFReader.read() & 0x7F;
        rIFFReader.read();
        rIFFReader.read();
        rIFFReader.read();
        dLSInstrument.bank = i;
        dLSInstrument.preset = k;
        dLSInstrument.druminstrument = ((j & 0x80) > 0);
      } 
    } 
    this.instruments.add(dLSInstrument);
  }
  
  private void readArt1Chunk(List<DLSModulator> paramList, RIFFReader paramRIFFReader) throws IOException {
    long l1 = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readUnsignedInt();
    if (l1 - 8L != 0L)
      paramRIFFReader.skip(l1 - 8L); 
    for (byte b = 0; b < l2; b++) {
      DLSModulator dLSModulator = new DLSModulator();
      dLSModulator.version = 1;
      dLSModulator.source = paramRIFFReader.readUnsignedShort();
      dLSModulator.control = paramRIFFReader.readUnsignedShort();
      dLSModulator.destination = paramRIFFReader.readUnsignedShort();
      dLSModulator.transform = paramRIFFReader.readUnsignedShort();
      dLSModulator.scale = paramRIFFReader.readInt();
      paramList.add(dLSModulator);
    } 
  }
  
  private void readArt2Chunk(List<DLSModulator> paramList, RIFFReader paramRIFFReader) throws IOException {
    long l1 = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readUnsignedInt();
    if (l1 - 8L != 0L)
      paramRIFFReader.skip(l1 - 8L); 
    for (byte b = 0; b < l2; b++) {
      DLSModulator dLSModulator = new DLSModulator();
      dLSModulator.version = 2;
      dLSModulator.source = paramRIFFReader.readUnsignedShort();
      dLSModulator.control = paramRIFFReader.readUnsignedShort();
      dLSModulator.destination = paramRIFFReader.readUnsignedShort();
      dLSModulator.transform = paramRIFFReader.readUnsignedShort();
      dLSModulator.scale = paramRIFFReader.readInt();
      paramList.add(dLSModulator);
    } 
  }
  
  private boolean readRgnChunk(DLSRegion paramDLSRegion, RIFFReader paramRIFFReader) throws IOException {
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      String str = rIFFReader.getFormat();
      if (str.equals("LIST")) {
        if (rIFFReader.getType().equals("lart")) {
          ArrayList arrayList = new ArrayList();
          while (rIFFReader.hasNextChunk()) {
            RIFFReader rIFFReader1 = rIFFReader.nextChunk();
            if (rIFFReader.getFormat().equals("cdl ") && !readCdlChunk(rIFFReader)) {
              arrayList.clear();
              break;
            } 
            if (rIFFReader1.getFormat().equals("art1"))
              readArt1Chunk(arrayList, rIFFReader1); 
          } 
          paramDLSRegion.getModulators().addAll(arrayList);
        } 
        if (rIFFReader.getType().equals("lar2")) {
          ArrayList arrayList = new ArrayList();
          while (rIFFReader.hasNextChunk()) {
            RIFFReader rIFFReader1 = rIFFReader.nextChunk();
            if (rIFFReader.getFormat().equals("cdl ") && !readCdlChunk(rIFFReader)) {
              arrayList.clear();
              break;
            } 
            if (rIFFReader1.getFormat().equals("art2"))
              readArt2Chunk(arrayList, rIFFReader1); 
          } 
          paramDLSRegion.getModulators().addAll(arrayList);
        } 
        continue;
      } 
      if (str.equals("cdl ") && !readCdlChunk(rIFFReader))
        return false; 
      if (str.equals("rgnh")) {
        paramDLSRegion.keyfrom = rIFFReader.readUnsignedShort();
        paramDLSRegion.keyto = rIFFReader.readUnsignedShort();
        paramDLSRegion.velfrom = rIFFReader.readUnsignedShort();
        paramDLSRegion.velto = rIFFReader.readUnsignedShort();
        paramDLSRegion.options = rIFFReader.readUnsignedShort();
        paramDLSRegion.exclusiveClass = rIFFReader.readUnsignedShort();
      } 
      if (str.equals("wlnk")) {
        paramDLSRegion.fusoptions = rIFFReader.readUnsignedShort();
        paramDLSRegion.phasegroup = rIFFReader.readUnsignedShort();
        paramDLSRegion.channel = rIFFReader.readUnsignedInt();
        long l = rIFFReader.readUnsignedInt();
        this.temp_rgnassign.put(paramDLSRegion, Long.valueOf(l));
      } 
      if (str.equals("wsmp")) {
        paramDLSRegion.sampleoptions = new DLSSampleOptions();
        readWsmpChunk(paramDLSRegion.sampleoptions, rIFFReader);
      } 
    } 
    return true;
  }
  
  private void readWsmpChunk(DLSSampleOptions paramDLSSampleOptions, RIFFReader paramRIFFReader) throws IOException {
    long l1 = paramRIFFReader.readUnsignedInt();
    paramDLSSampleOptions.unitynote = paramRIFFReader.readUnsignedShort();
    paramDLSSampleOptions.finetune = paramRIFFReader.readShort();
    paramDLSSampleOptions.attenuation = paramRIFFReader.readInt();
    paramDLSSampleOptions.options = paramRIFFReader.readUnsignedInt();
    long l2 = paramRIFFReader.readInt();
    if (l1 > 20L)
      paramRIFFReader.skip(l1 - 20L); 
    for (byte b = 0; b < l2; b++) {
      DLSSampleLoop dLSSampleLoop = new DLSSampleLoop();
      long l = paramRIFFReader.readUnsignedInt();
      dLSSampleLoop.type = paramRIFFReader.readUnsignedInt();
      dLSSampleLoop.start = paramRIFFReader.readUnsignedInt();
      dLSSampleLoop.length = paramRIFFReader.readUnsignedInt();
      paramDLSSampleOptions.loops.add(dLSSampleLoop);
      if (l > 16L)
        paramRIFFReader.skip(l - 16L); 
    } 
  }
  
  private void readInsInfoChunk(DLSInstrument paramDLSInstrument, RIFFReader paramRIFFReader) throws IOException {
    paramDLSInstrument.info.name = null;
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      String str = rIFFReader.getFormat();
      if (str.equals("INAM")) {
        paramDLSInstrument.info.name = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICRD")) {
        paramDLSInstrument.info.creationDate = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IENG")) {
        paramDLSInstrument.info.engineers = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IPRD")) {
        paramDLSInstrument.info.product = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICOP")) {
        paramDLSInstrument.info.copyright = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICMT")) {
        paramDLSInstrument.info.comments = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISFT")) {
        paramDLSInstrument.info.tools = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IARL")) {
        paramDLSInstrument.info.archival_location = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IART")) {
        paramDLSInstrument.info.artist = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICMS")) {
        paramDLSInstrument.info.commissioned = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IGNR")) {
        paramDLSInstrument.info.genre = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IKEY")) {
        paramDLSInstrument.info.keywords = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IMED")) {
        paramDLSInstrument.info.medium = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISBJ")) {
        paramDLSInstrument.info.subject = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISRC")) {
        paramDLSInstrument.info.source = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISRF")) {
        paramDLSInstrument.info.source_form = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ITCH"))
        paramDLSInstrument.info.technician = rIFFReader.readString(rIFFReader.available()); 
    } 
  }
  
  private void readWvplChunk(RIFFReader paramRIFFReader) throws IOException {
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      if (rIFFReader.getFormat().equals("LIST") && rIFFReader.getType().equals("wave"))
        readWaveChunk(rIFFReader); 
    } 
  }
  
  private void readWaveChunk(RIFFReader paramRIFFReader) throws IOException {
    DLSSample dLSSample = new DLSSample(this);
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      String str = rIFFReader.getFormat();
      if (str.equals("LIST")) {
        if (rIFFReader.getType().equals("INFO"))
          readWaveInfoChunk(dLSSample, rIFFReader); 
        continue;
      } 
      if (str.equals("dlid")) {
        dLSSample.guid = new byte[16];
        rIFFReader.readFully(dLSSample.guid);
      } 
      if (str.equals("fmt ")) {
        int i = rIFFReader.readUnsignedShort();
        if (i != 1 && i != 3)
          throw new RIFFInvalidDataException("Only PCM samples are supported!"); 
        int j = rIFFReader.readUnsignedShort();
        long l = rIFFReader.readUnsignedInt();
        rIFFReader.readUnsignedInt();
        int k = rIFFReader.readUnsignedShort();
        int m = rIFFReader.readUnsignedShort();
        AudioFormat audioFormat = null;
        if (i == 1)
          if (m == 8) {
            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)l, m, j, k, (float)l, false);
          } else {
            audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)l, m, j, k, (float)l, false);
          }  
        if (i == 3)
          audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)l, m, j, k, (float)l, false); 
        dLSSample.format = audioFormat;
      } 
      if (str.equals("data"))
        if (this.largeFormat) {
          dLSSample.setData(new ModelByteBuffer(this.sampleFile, rIFFReader.getFilePointer(), rIFFReader.available()));
        } else {
          byte[] arrayOfByte = new byte[rIFFReader.available()];
          dLSSample.setData(arrayOfByte);
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
        }  
      if (str.equals("wsmp")) {
        dLSSample.sampleoptions = new DLSSampleOptions();
        readWsmpChunk(dLSSample.sampleoptions, rIFFReader);
      } 
    } 
    this.samples.add(dLSSample);
  }
  
  private void readWaveInfoChunk(DLSSample paramDLSSample, RIFFReader paramRIFFReader) throws IOException {
    paramDLSSample.info.name = null;
    while (paramRIFFReader.hasNextChunk()) {
      RIFFReader rIFFReader = paramRIFFReader.nextChunk();
      String str = rIFFReader.getFormat();
      if (str.equals("INAM")) {
        paramDLSSample.info.name = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICRD")) {
        paramDLSSample.info.creationDate = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IENG")) {
        paramDLSSample.info.engineers = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IPRD")) {
        paramDLSSample.info.product = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICOP")) {
        paramDLSSample.info.copyright = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICMT")) {
        paramDLSSample.info.comments = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISFT")) {
        paramDLSSample.info.tools = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IARL")) {
        paramDLSSample.info.archival_location = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IART")) {
        paramDLSSample.info.artist = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ICMS")) {
        paramDLSSample.info.commissioned = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IGNR")) {
        paramDLSSample.info.genre = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IKEY")) {
        paramDLSSample.info.keywords = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("IMED")) {
        paramDLSSample.info.medium = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISBJ")) {
        paramDLSSample.info.subject = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISRC")) {
        paramDLSSample.info.source = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ISRF")) {
        paramDLSSample.info.source_form = rIFFReader.readString(rIFFReader.available());
        continue;
      } 
      if (str.equals("ITCH"))
        paramDLSSample.info.technician = rIFFReader.readString(rIFFReader.available()); 
    } 
  }
  
  public void save(String paramString) throws IOException { writeSoundbank(new RIFFWriter(paramString, "DLS ")); }
  
  public void save(File paramFile) throws IOException { writeSoundbank(new RIFFWriter(paramFile, "DLS ")); }
  
  public void save(OutputStream paramOutputStream) throws IOException { writeSoundbank(new RIFFWriter(paramOutputStream, "DLS ")); }
  
  private void writeSoundbank(RIFFWriter paramRIFFWriter) throws IOException {
    RIFFWriter rIFFWriter1 = paramRIFFWriter.writeChunk("colh");
    rIFFWriter1.writeUnsignedInt(this.instruments.size());
    if (this.major != -1L && this.minor != -1L) {
      RIFFWriter rIFFWriter = paramRIFFWriter.writeChunk("vers");
      rIFFWriter.writeUnsignedInt(this.major);
      rIFFWriter.writeUnsignedInt(this.minor);
    } 
    writeInstruments(paramRIFFWriter.writeList("lins"));
    RIFFWriter rIFFWriter2 = paramRIFFWriter.writeChunk("ptbl");
    rIFFWriter2.writeUnsignedInt(8L);
    rIFFWriter2.writeUnsignedInt(this.samples.size());
    long l1 = paramRIFFWriter.getFilePointer();
    for (byte b = 0; b < this.samples.size(); b++)
      rIFFWriter2.writeUnsignedInt(0L); 
    RIFFWriter rIFFWriter3 = paramRIFFWriter.writeList("wvpl");
    long l2 = rIFFWriter3.getFilePointer();
    ArrayList arrayList = new ArrayList();
    for (DLSSample dLSSample : this.samples) {
      arrayList.add(Long.valueOf(rIFFWriter3.getFilePointer() - l2));
      writeSample(rIFFWriter3.writeList("wave"), dLSSample);
    } 
    long l3 = paramRIFFWriter.getFilePointer();
    paramRIFFWriter.seek(l1);
    paramRIFFWriter.setWriteOverride(true);
    for (Long long : arrayList)
      paramRIFFWriter.writeUnsignedInt(long.longValue()); 
    paramRIFFWriter.setWriteOverride(false);
    paramRIFFWriter.seek(l3);
    writeInfo(paramRIFFWriter.writeList("INFO"), this.info);
    paramRIFFWriter.close();
  }
  
  private void writeSample(RIFFWriter paramRIFFWriter, DLSSample paramDLSSample) throws IOException {
    AudioFormat audioFormat = paramDLSSample.getFormat();
    AudioFormat.Encoding encoding = audioFormat.getEncoding();
    float f1 = audioFormat.getSampleRate();
    int i = audioFormat.getSampleSizeInBits();
    int j = audioFormat.getChannels();
    int k = audioFormat.getFrameSize();
    float f2 = audioFormat.getFrameRate();
    boolean bool = audioFormat.isBigEndian();
    boolean bool1 = false;
    if (audioFormat.getSampleSizeInBits() == 8) {
      if (!encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
        encoding = AudioFormat.Encoding.PCM_UNSIGNED;
        bool1 = true;
      } 
    } else {
      if (!encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
        encoding = AudioFormat.Encoding.PCM_SIGNED;
        bool1 = true;
      } 
      if (bool) {
        bool = false;
        bool1 = true;
      } 
    } 
    if (bool1)
      audioFormat = new AudioFormat(encoding, f1, i, j, k, f2, bool); 
    RIFFWriter rIFFWriter = paramRIFFWriter.writeChunk("fmt ");
    byte b = 0;
    if (audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
      b = 1;
    } else if (audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
      b = 1;
    } else if (audioFormat.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
      b = 3;
    } 
    rIFFWriter.writeUnsignedShort(b);
    rIFFWriter.writeUnsignedShort(audioFormat.getChannels());
    rIFFWriter.writeUnsignedInt((long)audioFormat.getSampleRate());
    long l = (long)audioFormat.getFrameRate() * audioFormat.getFrameSize();
    rIFFWriter.writeUnsignedInt(l);
    rIFFWriter.writeUnsignedShort(audioFormat.getFrameSize());
    rIFFWriter.writeUnsignedShort(audioFormat.getSampleSizeInBits());
    rIFFWriter.write(0);
    rIFFWriter.write(0);
    writeSampleOptions(paramRIFFWriter.writeChunk("wsmp"), paramDLSSample.sampleoptions);
    if (bool1) {
      RIFFWriter rIFFWriter1 = paramRIFFWriter.writeChunk("data");
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFormat, (AudioInputStream)paramDLSSample.getData());
      byte[] arrayOfByte = new byte[1024];
      int m;
      while ((m = audioInputStream.read(arrayOfByte)) != -1)
        rIFFWriter1.write(arrayOfByte, 0, m); 
    } else {
      RIFFWriter rIFFWriter1 = paramRIFFWriter.writeChunk("data");
      ModelByteBuffer modelByteBuffer = paramDLSSample.getDataBuffer();
      modelByteBuffer.writeTo(rIFFWriter1);
    } 
    writeInfo(paramRIFFWriter.writeList("INFO"), paramDLSSample.info);
  }
  
  private void writeInstruments(RIFFWriter paramRIFFWriter) throws IOException {
    for (DLSInstrument dLSInstrument : this.instruments)
      writeInstrument(paramRIFFWriter.writeList("ins "), dLSInstrument); 
  }
  
  private void writeInstrument(RIFFWriter paramRIFFWriter, DLSInstrument paramDLSInstrument) throws IOException {
    byte b1 = 0;
    byte b2 = 0;
    for (DLSModulator dLSModulator : paramDLSInstrument.getModulators()) {
      if (dLSModulator.version == 1)
        b1++; 
      if (dLSModulator.version == 2)
        b2++; 
    } 
    for (DLSRegion dLSRegion : paramDLSInstrument.regions) {
      for (DLSModulator dLSModulator : dLSRegion.getModulators()) {
        if (dLSModulator.version == 1)
          b1++; 
        if (dLSModulator.version == 2)
          b2++; 
      } 
    } 
    byte b3 = 1;
    if (b2 > 0)
      b3 = 2; 
    RIFFWriter rIFFWriter1 = paramRIFFWriter.writeChunk("insh");
    rIFFWriter1.writeUnsignedInt(paramDLSInstrument.getRegions().size());
    rIFFWriter1.writeUnsignedInt(paramDLSInstrument.bank + (paramDLSInstrument.druminstrument ? 2147483648L : 0L));
    rIFFWriter1.writeUnsignedInt(paramDLSInstrument.preset);
    RIFFWriter rIFFWriter2 = paramRIFFWriter.writeList("lrgn");
    for (DLSRegion dLSRegion : paramDLSInstrument.regions)
      writeRegion(rIFFWriter2, dLSRegion, b3); 
    writeArticulators(paramRIFFWriter, paramDLSInstrument.getModulators());
    writeInfo(paramRIFFWriter.writeList("INFO"), paramDLSInstrument.info);
  }
  
  private void writeArticulators(RIFFWriter paramRIFFWriter, List<DLSModulator> paramList) throws IOException {
    byte b1 = 0;
    byte b2 = 0;
    for (DLSModulator dLSModulator : paramList) {
      if (dLSModulator.version == 1)
        b1++; 
      if (dLSModulator.version == 2)
        b2++; 
    } 
    if (b1 > 0) {
      RIFFWriter rIFFWriter1 = paramRIFFWriter.writeList("lart");
      RIFFWriter rIFFWriter2 = rIFFWriter1.writeChunk("art1");
      rIFFWriter2.writeUnsignedInt(8L);
      rIFFWriter2.writeUnsignedInt(b1);
      for (DLSModulator dLSModulator : paramList) {
        if (dLSModulator.version == 1) {
          rIFFWriter2.writeUnsignedShort(dLSModulator.source);
          rIFFWriter2.writeUnsignedShort(dLSModulator.control);
          rIFFWriter2.writeUnsignedShort(dLSModulator.destination);
          rIFFWriter2.writeUnsignedShort(dLSModulator.transform);
          rIFFWriter2.writeInt(dLSModulator.scale);
        } 
      } 
    } 
    if (b2 > 0) {
      RIFFWriter rIFFWriter1 = paramRIFFWriter.writeList("lar2");
      RIFFWriter rIFFWriter2 = rIFFWriter1.writeChunk("art2");
      rIFFWriter2.writeUnsignedInt(8L);
      rIFFWriter2.writeUnsignedInt(b2);
      for (DLSModulator dLSModulator : paramList) {
        if (dLSModulator.version == 2) {
          rIFFWriter2.writeUnsignedShort(dLSModulator.source);
          rIFFWriter2.writeUnsignedShort(dLSModulator.control);
          rIFFWriter2.writeUnsignedShort(dLSModulator.destination);
          rIFFWriter2.writeUnsignedShort(dLSModulator.transform);
          rIFFWriter2.writeInt(dLSModulator.scale);
        } 
      } 
    } 
  }
  
  private void writeRegion(RIFFWriter paramRIFFWriter, DLSRegion paramDLSRegion, int paramInt) throws IOException {
    RIFFWriter rIFFWriter1 = null;
    if (paramInt == 1)
      rIFFWriter1 = paramRIFFWriter.writeList("rgn "); 
    if (paramInt == 2)
      rIFFWriter1 = paramRIFFWriter.writeList("rgn2"); 
    if (rIFFWriter1 == null)
      return; 
    RIFFWriter rIFFWriter2 = rIFFWriter1.writeChunk("rgnh");
    rIFFWriter2.writeUnsignedShort(paramDLSRegion.keyfrom);
    rIFFWriter2.writeUnsignedShort(paramDLSRegion.keyto);
    rIFFWriter2.writeUnsignedShort(paramDLSRegion.velfrom);
    rIFFWriter2.writeUnsignedShort(paramDLSRegion.velto);
    rIFFWriter2.writeUnsignedShort(paramDLSRegion.options);
    rIFFWriter2.writeUnsignedShort(paramDLSRegion.exclusiveClass);
    if (paramDLSRegion.sampleoptions != null)
      writeSampleOptions(rIFFWriter1.writeChunk("wsmp"), paramDLSRegion.sampleoptions); 
    if (paramDLSRegion.sample != null && this.samples.indexOf(paramDLSRegion.sample) != -1) {
      RIFFWriter rIFFWriter = rIFFWriter1.writeChunk("wlnk");
      rIFFWriter.writeUnsignedShort(paramDLSRegion.fusoptions);
      rIFFWriter.writeUnsignedShort(paramDLSRegion.phasegroup);
      rIFFWriter.writeUnsignedInt(paramDLSRegion.channel);
      rIFFWriter.writeUnsignedInt(this.samples.indexOf(paramDLSRegion.sample));
    } 
    writeArticulators(rIFFWriter1, paramDLSRegion.getModulators());
    rIFFWriter1.close();
  }
  
  private void writeSampleOptions(RIFFWriter paramRIFFWriter, DLSSampleOptions paramDLSSampleOptions) throws IOException {
    paramRIFFWriter.writeUnsignedInt(20L);
    paramRIFFWriter.writeUnsignedShort(paramDLSSampleOptions.unitynote);
    paramRIFFWriter.writeShort(paramDLSSampleOptions.finetune);
    paramRIFFWriter.writeInt(paramDLSSampleOptions.attenuation);
    paramRIFFWriter.writeUnsignedInt(paramDLSSampleOptions.options);
    paramRIFFWriter.writeInt(paramDLSSampleOptions.loops.size());
    for (DLSSampleLoop dLSSampleLoop : paramDLSSampleOptions.loops) {
      paramRIFFWriter.writeUnsignedInt(16L);
      paramRIFFWriter.writeUnsignedInt(dLSSampleLoop.type);
      paramRIFFWriter.writeUnsignedInt(dLSSampleLoop.start);
      paramRIFFWriter.writeUnsignedInt(dLSSampleLoop.length);
    } 
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
  
  private void writeInfo(RIFFWriter paramRIFFWriter, DLSInfo paramDLSInfo) throws IOException {
    writeInfoStringChunk(paramRIFFWriter, "INAM", paramDLSInfo.name);
    writeInfoStringChunk(paramRIFFWriter, "ICRD", paramDLSInfo.creationDate);
    writeInfoStringChunk(paramRIFFWriter, "IENG", paramDLSInfo.engineers);
    writeInfoStringChunk(paramRIFFWriter, "IPRD", paramDLSInfo.product);
    writeInfoStringChunk(paramRIFFWriter, "ICOP", paramDLSInfo.copyright);
    writeInfoStringChunk(paramRIFFWriter, "ICMT", paramDLSInfo.comments);
    writeInfoStringChunk(paramRIFFWriter, "ISFT", paramDLSInfo.tools);
    writeInfoStringChunk(paramRIFFWriter, "IARL", paramDLSInfo.archival_location);
    writeInfoStringChunk(paramRIFFWriter, "IART", paramDLSInfo.artist);
    writeInfoStringChunk(paramRIFFWriter, "ICMS", paramDLSInfo.commissioned);
    writeInfoStringChunk(paramRIFFWriter, "IGNR", paramDLSInfo.genre);
    writeInfoStringChunk(paramRIFFWriter, "IKEY", paramDLSInfo.keywords);
    writeInfoStringChunk(paramRIFFWriter, "IMED", paramDLSInfo.medium);
    writeInfoStringChunk(paramRIFFWriter, "ISBJ", paramDLSInfo.subject);
    writeInfoStringChunk(paramRIFFWriter, "ISRC", paramDLSInfo.source);
    writeInfoStringChunk(paramRIFFWriter, "ISRF", paramDLSInfo.source_form);
    writeInfoStringChunk(paramRIFFWriter, "ITCH", paramDLSInfo.technician);
  }
  
  public DLSInfo getInfo() { return this.info; }
  
  public String getName() { return this.info.name; }
  
  public String getVersion() { return this.major + "." + this.minor; }
  
  public String getVendor() { return this.info.engineers; }
  
  public String getDescription() { return this.info.comments; }
  
  public void setName(String paramString) throws IOException { this.info.name = paramString; }
  
  public void setVendor(String paramString) throws IOException { this.info.engineers = paramString; }
  
  public void setDescription(String paramString) throws IOException { this.info.comments = paramString; }
  
  public SoundbankResource[] getResources() {
    SoundbankResource[] arrayOfSoundbankResource = new SoundbankResource[this.samples.size()];
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.samples.size(); b2++)
      arrayOfSoundbankResource[b1++] = (SoundbankResource)this.samples.get(b2); 
    return arrayOfSoundbankResource;
  }
  
  public DLSInstrument[] getInstruments() {
    DLSInstrument[] arrayOfDLSInstrument = (DLSInstrument[])this.instruments.toArray(new DLSInstrument[this.instruments.size()]);
    Arrays.sort(arrayOfDLSInstrument, new ModelInstrumentComparator());
    return arrayOfDLSInstrument;
  }
  
  public DLSSample[] getSamples() { return (DLSSample[])this.samples.toArray(new DLSSample[this.samples.size()]); }
  
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
  
  public void addResource(SoundbankResource paramSoundbankResource) {
    if (paramSoundbankResource instanceof DLSInstrument)
      this.instruments.add((DLSInstrument)paramSoundbankResource); 
    if (paramSoundbankResource instanceof DLSSample)
      this.samples.add((DLSSample)paramSoundbankResource); 
  }
  
  public void removeResource(SoundbankResource paramSoundbankResource) {
    if (paramSoundbankResource instanceof DLSInstrument)
      this.instruments.remove((DLSInstrument)paramSoundbankResource); 
    if (paramSoundbankResource instanceof DLSSample)
      this.samples.remove((DLSSample)paramSoundbankResource); 
  }
  
  public void addInstrument(DLSInstrument paramDLSInstrument) { this.instruments.add(paramDLSInstrument); }
  
  public void removeInstrument(DLSInstrument paramDLSInstrument) { this.instruments.remove(paramDLSInstrument); }
  
  public long getMajor() { return this.major; }
  
  public void setMajor(long paramLong) { this.major = paramLong; }
  
  public long getMinor() { return this.minor; }
  
  public void setMinor(long paramLong) { this.minor = paramLong; }
  
  private static class DLSID {
    long i1;
    
    int s1;
    
    int s2;
    
    int x1;
    
    int x2;
    
    int x3;
    
    int x4;
    
    int x5;
    
    int x6;
    
    int x7;
    
    int x8;
    
    private DLSID() {}
    
    DLSID(long param1Long, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8, int param1Int9, int param1Int10) {
      this.i1 = param1Long;
      this.s1 = param1Int1;
      this.s2 = param1Int2;
      this.x1 = param1Int3;
      this.x2 = param1Int4;
      this.x3 = param1Int5;
      this.x4 = param1Int6;
      this.x5 = param1Int7;
      this.x6 = param1Int8;
      this.x7 = param1Int9;
      this.x8 = param1Int10;
    }
    
    public static DLSID read(RIFFReader param1RIFFReader) throws IOException {
      DLSID dLSID = new DLSID();
      dLSID.i1 = param1RIFFReader.readUnsignedInt();
      dLSID.s1 = param1RIFFReader.readUnsignedShort();
      dLSID.s2 = param1RIFFReader.readUnsignedShort();
      dLSID.x1 = param1RIFFReader.readUnsignedByte();
      dLSID.x2 = param1RIFFReader.readUnsignedByte();
      dLSID.x3 = param1RIFFReader.readUnsignedByte();
      dLSID.x4 = param1RIFFReader.readUnsignedByte();
      dLSID.x5 = param1RIFFReader.readUnsignedByte();
      dLSID.x6 = param1RIFFReader.readUnsignedByte();
      dLSID.x7 = param1RIFFReader.readUnsignedByte();
      dLSID.x8 = param1RIFFReader.readUnsignedByte();
      return dLSID;
    }
    
    public int hashCode() { return (int)this.i1; }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof DLSID))
        return false; 
      DLSID dLSID = (DLSID)param1Object;
      return (this.i1 == dLSID.i1 && this.s1 == dLSID.s1 && this.s2 == dLSID.s2 && this.x1 == dLSID.x1 && this.x2 == dLSID.x2 && this.x3 == dLSID.x3 && this.x4 == dLSID.x4 && this.x5 == dLSID.x5 && this.x6 == dLSID.x6 && this.x7 == dLSID.x7 && this.x8 == dLSID.x8);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DLSSoundbank.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */