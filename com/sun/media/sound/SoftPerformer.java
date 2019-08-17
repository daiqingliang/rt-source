package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SoftPerformer {
  static ModelConnectionBlock[] defaultconnections = new ModelConnectionBlock[42];
  
  public int keyFrom = 0;
  
  public int keyTo = 127;
  
  public int velFrom = 0;
  
  public int velTo = 127;
  
  public int exclusiveClass = 0;
  
  public boolean selfNonExclusive = false;
  
  public boolean forcedVelocity = false;
  
  public boolean forcedKeynumber = false;
  
  public ModelPerformer performer;
  
  public ModelConnectionBlock[] connections;
  
  public ModelOscillator[] oscillators;
  
  public Map<Integer, int[]> midi_rpn_connections = new HashMap();
  
  public Map<Integer, int[]> midi_nrpn_connections = new HashMap();
  
  public int[][] midi_ctrl_connections;
  
  public int[][] midi_connections;
  
  public int[] ctrl_connections;
  
  private List<Integer> ctrl_connections_list = new ArrayList();
  
  private static KeySortComparator keySortComparator;
  
  private String extractKeys(ModelConnectionBlock paramModelConnectionBlock) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramModelConnectionBlock.getSources() != null) {
      stringBuffer.append("[");
      ModelSource[] arrayOfModelSource1 = paramModelConnectionBlock.getSources();
      ModelSource[] arrayOfModelSource2 = new ModelSource[arrayOfModelSource1.length];
      byte b;
      for (b = 0; b < arrayOfModelSource1.length; b++)
        arrayOfModelSource2[b] = arrayOfModelSource1[b]; 
      Arrays.sort(arrayOfModelSource2, keySortComparator);
      for (b = 0; b < arrayOfModelSource1.length; b++) {
        stringBuffer.append(arrayOfModelSource1[b].getIdentifier());
        stringBuffer.append(";");
      } 
      stringBuffer.append("]");
    } 
    stringBuffer.append(";");
    if (paramModelConnectionBlock.getDestination() != null)
      stringBuffer.append(paramModelConnectionBlock.getDestination().getIdentifier()); 
    stringBuffer.append(";");
    return stringBuffer.toString();
  }
  
  private void processSource(ModelSource paramModelSource, int paramInt) {
    ModelIdentifier modelIdentifier = paramModelSource.getIdentifier();
    String str = modelIdentifier.getObject();
    if (str.equals("midi_cc")) {
      processMidiControlSource(paramModelSource, paramInt);
    } else if (str.equals("midi_rpn")) {
      processMidiRpnSource(paramModelSource, paramInt);
    } else if (str.equals("midi_nrpn")) {
      processMidiNrpnSource(paramModelSource, paramInt);
    } else if (str.equals("midi")) {
      processMidiSource(paramModelSource, paramInt);
    } else if (str.equals("noteon")) {
      processNoteOnSource(paramModelSource, paramInt);
    } else {
      if (str.equals("osc"))
        return; 
      if (str.equals("mixer"))
        return; 
      this.ctrl_connections_list.add(Integer.valueOf(paramInt));
    } 
  }
  
  private void processMidiControlSource(ModelSource paramModelSource, int paramInt) {
    String str = paramModelSource.getIdentifier().getVariable();
    if (str == null)
      return; 
    int i = Integer.parseInt(str);
    if (this.midi_ctrl_connections[i] == null) {
      new int[1][0] = paramInt;
      this.midi_ctrl_connections[i] = new int[1];
    } else {
      int[] arrayOfInt1 = this.midi_ctrl_connections[i];
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (byte b = 0; b < arrayOfInt1.length; b++)
        arrayOfInt2[b] = arrayOfInt1[b]; 
      arrayOfInt2[arrayOfInt2.length - 1] = paramInt;
      this.midi_ctrl_connections[i] = arrayOfInt2;
    } 
  }
  
  private void processNoteOnSource(ModelSource paramModelSource, int paramInt) {
    String str = paramModelSource.getIdentifier().getVariable();
    byte b = -1;
    if (str.equals("on"))
      b = 3; 
    if (str.equals("keynumber"))
      b = 4; 
    if (b == -1)
      return; 
    if (this.midi_connections[b] == null) {
      new int[1][0] = paramInt;
      this.midi_connections[b] = new int[1];
    } else {
      int[] arrayOfInt1 = this.midi_connections[b];
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (byte b1 = 0; b1 < arrayOfInt1.length; b1++)
        arrayOfInt2[b1] = arrayOfInt1[b1]; 
      arrayOfInt2[arrayOfInt2.length - 1] = paramInt;
      this.midi_connections[b] = arrayOfInt2;
    } 
  }
  
  private void processMidiSource(ModelSource paramModelSource, int paramInt) {
    String str = paramModelSource.getIdentifier().getVariable();
    byte b = -1;
    if (str.equals("pitch"))
      b = 0; 
    if (str.equals("channel_pressure"))
      b = 1; 
    if (str.equals("poly_pressure"))
      b = 2; 
    if (b == -1)
      return; 
    if (this.midi_connections[b] == null) {
      new int[1][0] = paramInt;
      this.midi_connections[b] = new int[1];
    } else {
      int[] arrayOfInt1 = this.midi_connections[b];
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (byte b1 = 0; b1 < arrayOfInt1.length; b1++)
        arrayOfInt2[b1] = arrayOfInt1[b1]; 
      arrayOfInt2[arrayOfInt2.length - 1] = paramInt;
      this.midi_connections[b] = arrayOfInt2;
    } 
  }
  
  private void processMidiRpnSource(ModelSource paramModelSource, int paramInt) {
    String str = paramModelSource.getIdentifier().getVariable();
    if (str == null)
      return; 
    int i = Integer.parseInt(str);
    if (this.midi_rpn_connections.get(Integer.valueOf(i)) == null) {
      this.midi_rpn_connections.put(Integer.valueOf(i), new int[] { paramInt });
    } else {
      int[] arrayOfInt1 = (int[])this.midi_rpn_connections.get(Integer.valueOf(i));
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (byte b = 0; b < arrayOfInt1.length; b++)
        arrayOfInt2[b] = arrayOfInt1[b]; 
      arrayOfInt2[arrayOfInt2.length - 1] = paramInt;
      this.midi_rpn_connections.put(Integer.valueOf(i), arrayOfInt2);
    } 
  }
  
  private void processMidiNrpnSource(ModelSource paramModelSource, int paramInt) {
    String str = paramModelSource.getIdentifier().getVariable();
    if (str == null)
      return; 
    int i = Integer.parseInt(str);
    if (this.midi_nrpn_connections.get(Integer.valueOf(i)) == null) {
      this.midi_nrpn_connections.put(Integer.valueOf(i), new int[] { paramInt });
    } else {
      int[] arrayOfInt1 = (int[])this.midi_nrpn_connections.get(Integer.valueOf(i));
      int[] arrayOfInt2 = new int[arrayOfInt1.length + 1];
      for (byte b = 0; b < arrayOfInt1.length; b++)
        arrayOfInt2[b] = arrayOfInt1[b]; 
      arrayOfInt2[arrayOfInt2.length - 1] = paramInt;
      this.midi_nrpn_connections.put(Integer.valueOf(i), arrayOfInt2);
    } 
  }
  
  public SoftPerformer(ModelPerformer paramModelPerformer) {
    this.performer = paramModelPerformer;
    this.keyFrom = paramModelPerformer.getKeyFrom();
    this.keyTo = paramModelPerformer.getKeyTo();
    this.velFrom = paramModelPerformer.getVelFrom();
    this.velTo = paramModelPerformer.getVelTo();
    this.exclusiveClass = paramModelPerformer.getExclusiveClass();
    this.selfNonExclusive = paramModelPerformer.isSelfNonExclusive();
    HashMap hashMap = new HashMap();
    ArrayList arrayList1 = new ArrayList();
    arrayList1.addAll(paramModelPerformer.getConnectionBlocks());
    if (paramModelPerformer.isDefaultConnectionsEnabled()) {
      boolean bool1 = false;
      byte b3;
      for (b3 = 0; b3 < arrayList1.size(); b3++) {
        ModelConnectionBlock modelConnectionBlock = (ModelConnectionBlock)arrayList1.get(b3);
        ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
        ModelDestination modelDestination = modelConnectionBlock.getDestination();
        boolean bool3 = false;
        if (modelDestination != null && arrayOfModelSource != null && arrayOfModelSource.length > 1)
          for (byte b = 0; b < arrayOfModelSource.length; b++) {
            if (arrayOfModelSource[b].getIdentifier().getObject().equals("midi_cc") && arrayOfModelSource[b].getIdentifier().getVariable().equals("1")) {
              bool3 = true;
              bool1 = true;
              break;
            } 
          }  
        if (bool3) {
          ModelConnectionBlock modelConnectionBlock4 = new ModelConnectionBlock();
          modelConnectionBlock4.setSources(modelConnectionBlock.getSources());
          modelConnectionBlock4.setDestination(modelConnectionBlock.getDestination());
          modelConnectionBlock4.addSource(new ModelSource(new ModelIdentifier("midi_rpn", "5")));
          modelConnectionBlock4.setScale(modelConnectionBlock.getScale() * 256.0D);
          arrayList1.set(b3, modelConnectionBlock4);
        } 
      } 
      if (!bool1) {
        ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(new ModelIdentifier("midi_cc", "1", 0), false, false, 0), 50.0D, new ModelDestination(ModelDestination.DESTINATION_PITCH));
        modelConnectionBlock.addSource(new ModelSource(new ModelIdentifier("midi_rpn", "5")));
        modelConnectionBlock.setScale(modelConnectionBlock.getScale() * 256.0D);
        arrayList1.add(modelConnectionBlock);
      } 
      b3 = 0;
      boolean bool2 = false;
      ModelConnectionBlock modelConnectionBlock1 = null;
      byte b4 = 0;
      for (ModelConnectionBlock modelConnectionBlock : arrayList1) {
        ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
        ModelDestination modelDestination = modelConnectionBlock.getDestination();
        if (modelDestination != null && arrayOfModelSource != null)
          for (byte b = 0; b < arrayOfModelSource.length; b++) {
            ModelIdentifier modelIdentifier = arrayOfModelSource[b].getIdentifier();
            if (modelIdentifier.getObject().equals("midi_cc") && modelIdentifier.getVariable().equals("1")) {
              modelConnectionBlock1 = modelConnectionBlock;
              b4 = b;
            } 
            if (modelIdentifier.getObject().equals("midi")) {
              if (modelIdentifier.getVariable().equals("channel_pressure"))
                b3 = 1; 
              if (modelIdentifier.getVariable().equals("poly_pressure"))
                bool2 = true; 
            } 
          }  
      } 
      if (modelConnectionBlock1 != null) {
        if (b3 == 0) {
          ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock();
          modelConnectionBlock.setDestination(modelConnectionBlock1.getDestination());
          modelConnectionBlock.setScale(modelConnectionBlock1.getScale());
          ModelSource[] arrayOfModelSource1 = modelConnectionBlock1.getSources();
          ModelSource[] arrayOfModelSource2 = new ModelSource[arrayOfModelSource1.length];
          for (byte b = 0; b < arrayOfModelSource2.length; b++)
            arrayOfModelSource2[b] = arrayOfModelSource1[b]; 
          arrayOfModelSource2[b4] = new ModelSource(new ModelIdentifier("midi", "channel_pressure"));
          modelConnectionBlock.setSources(arrayOfModelSource2);
          hashMap.put(extractKeys(modelConnectionBlock), modelConnectionBlock);
        } 
        if (!bool2) {
          ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock();
          modelConnectionBlock.setDestination(modelConnectionBlock1.getDestination());
          modelConnectionBlock.setScale(modelConnectionBlock1.getScale());
          ModelSource[] arrayOfModelSource1 = modelConnectionBlock1.getSources();
          ModelSource[] arrayOfModelSource2 = new ModelSource[arrayOfModelSource1.length];
          for (byte b = 0; b < arrayOfModelSource2.length; b++)
            arrayOfModelSource2[b] = arrayOfModelSource1[b]; 
          arrayOfModelSource2[b4] = new ModelSource(new ModelIdentifier("midi", "poly_pressure"));
          modelConnectionBlock.setSources(arrayOfModelSource2);
          hashMap.put(extractKeys(modelConnectionBlock), modelConnectionBlock);
        } 
      } 
      ModelConnectionBlock modelConnectionBlock2 = null;
      for (ModelConnectionBlock modelConnectionBlock : arrayList1) {
        ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
        if (arrayOfModelSource.length != 0 && arrayOfModelSource[0].getIdentifier().getObject().equals("lfo") && modelConnectionBlock.getDestination().getIdentifier().equals(ModelDestination.DESTINATION_PITCH)) {
          if (modelConnectionBlock2 == null) {
            modelConnectionBlock2 = modelConnectionBlock;
            continue;
          } 
          if (modelConnectionBlock2.getSources().length > arrayOfModelSource.length) {
            modelConnectionBlock2 = modelConnectionBlock;
            continue;
          } 
          if (modelConnectionBlock2.getSources()[0].getIdentifier().getInstance() < 1 && modelConnectionBlock2.getSources()[0].getIdentifier().getInstance() > arrayOfModelSource[0].getIdentifier().getInstance())
            modelConnectionBlock2 = modelConnectionBlock; 
        } 
      } 
      int i = 1;
      if (modelConnectionBlock2 != null)
        i = modelConnectionBlock2.getSources()[0].getIdentifier().getInstance(); 
      ModelConnectionBlock modelConnectionBlock3 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "78"), false, true, 0), 2000.0D, new ModelDestination(new ModelIdentifier("lfo", "delay2", i)));
      hashMap.put(extractKeys(modelConnectionBlock3), modelConnectionBlock3);
      double d = (modelConnectionBlock2 == null) ? 0.0D : modelConnectionBlock2.getScale();
      modelConnectionBlock3 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("lfo", i)), new ModelSource(new ModelIdentifier("midi_cc", "77"), new ModelTransform(this, d) {
              double s = scale;
              
              public double transform(double param1Double) {
                param1Double = param1Double * 2.0D - 1.0D;
                param1Double *= 600.0D;
                if (this.s == 0.0D)
                  return param1Double; 
                if (this.s > 0.0D) {
                  if (param1Double < -this.s)
                    param1Double = -this.s; 
                  return param1Double;
                } 
                if (param1Double < this.s)
                  param1Double = -this.s; 
                return -param1Double;
              }
            }), new ModelDestination(ModelDestination.DESTINATION_PITCH));
      hashMap.put(extractKeys(modelConnectionBlock3), modelConnectionBlock3);
      modelConnectionBlock3 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "76"), false, true, 0), 2400.0D, new ModelDestination(new ModelIdentifier("lfo", "freq", i)));
      hashMap.put(extractKeys(modelConnectionBlock3), modelConnectionBlock3);
    } 
    if (paramModelPerformer.isDefaultConnectionsEnabled())
      for (ModelConnectionBlock modelConnectionBlock : defaultconnections)
        hashMap.put(extractKeys(modelConnectionBlock), modelConnectionBlock);  
    for (ModelConnectionBlock modelConnectionBlock : arrayList1)
      hashMap.put(extractKeys(modelConnectionBlock), modelConnectionBlock); 
    ArrayList arrayList2 = new ArrayList();
    this.midi_ctrl_connections = new int[128][];
    byte b1;
    for (b1 = 0; b1 < this.midi_ctrl_connections.length; b1++)
      this.midi_ctrl_connections[b1] = null; 
    this.midi_connections = new int[5][];
    for (b1 = 0; b1 < this.midi_connections.length; b1++)
      this.midi_connections[b1] = null; 
    b1 = 0;
    boolean bool = false;
    for (ModelConnectionBlock modelConnectionBlock : hashMap.values()) {
      if (modelConnectionBlock.getDestination() != null) {
        ModelDestination modelDestination = modelConnectionBlock.getDestination();
        ModelIdentifier modelIdentifier = modelDestination.getIdentifier();
        if (modelIdentifier.getObject().equals("noteon")) {
          bool = true;
          if (modelIdentifier.getVariable().equals("keynumber"))
            this.forcedKeynumber = true; 
          if (modelIdentifier.getVariable().equals("velocity"))
            this.forcedVelocity = true; 
        } 
      } 
      if (bool) {
        arrayList2.add(0, modelConnectionBlock);
        bool = false;
        continue;
      } 
      arrayList2.add(modelConnectionBlock);
    } 
    for (ModelConnectionBlock modelConnectionBlock : arrayList2) {
      if (modelConnectionBlock.getSources() != null) {
        ModelSource[] arrayOfModelSource = modelConnectionBlock.getSources();
        for (byte b = 0; b < arrayOfModelSource.length; b++)
          processSource(arrayOfModelSource[b], b1); 
      } 
      b1++;
    } 
    this.connections = new ModelConnectionBlock[arrayList2.size()];
    arrayList2.toArray(this.connections);
    this.ctrl_connections = new int[this.ctrl_connections_list.size()];
    for (byte b2 = 0; b2 < this.ctrl_connections.length; b2++)
      this.ctrl_connections[b2] = ((Integer)this.ctrl_connections_list.get(b2)).intValue(); 
    this.oscillators = new ModelOscillator[paramModelPerformer.getOscillators().size()];
    paramModelPerformer.getOscillators().toArray(this.oscillators);
    for (ModelConnectionBlock modelConnectionBlock : arrayList2) {
      if (modelConnectionBlock.getDestination() != null && isUnnecessaryTransform(modelConnectionBlock.getDestination().getTransform()))
        modelConnectionBlock.getDestination().setTransform(null); 
      if (modelConnectionBlock.getSources() != null)
        for (ModelSource modelSource : modelConnectionBlock.getSources()) {
          if (isUnnecessaryTransform(modelSource.getTransform()))
            modelSource.setTransform(null); 
        }  
    } 
  }
  
  private static boolean isUnnecessaryTransform(ModelTransform paramModelTransform) {
    if (paramModelTransform == null)
      return false; 
    if (!(paramModelTransform instanceof ModelStandardTransform))
      return false; 
    ModelStandardTransform modelStandardTransform = (ModelStandardTransform)paramModelTransform;
    return modelStandardTransform.getDirection() ? false : (modelStandardTransform.getPolarity() ? false : ((modelStandardTransform.getTransform() != 0) ? false : false));
  }
  
  static  {
    byte b = 0;
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "on", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("eg", "on", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "on", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("eg", "on", 1)));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("eg", "active", 0), false, false, 0), 1.0D, new ModelDestination(new ModelIdentifier("mixer", "active", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("eg", 0), true, false, 0), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "velocity"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi", "pitch"), false, true, 0), new ModelSource(new ModelIdentifier("midi_rpn", "0"), new ModelTransform() {
            public double transform(double param1Double) {
              int i = (int)(param1Double * 16384.0D);
              int j = i >> 7;
              int k = i & 0x7F;
              return (j * 100 + k);
            }
          }), new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "keynumber"), false, false, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "7"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "8"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "balance")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "10"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "pan")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "11"), true, false, 1), -960.0D, new ModelDestination(new ModelIdentifier("mixer", "gain")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "91"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "reverb")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "93"), false, false, 0), 1000.0D, new ModelDestination(new ModelIdentifier("mixer", "chorus")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "71"), false, true, 0), 200.0D, new ModelDestination(new ModelIdentifier("filter", "q")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "74"), false, true, 0), 9600.0D, new ModelDestination(new ModelIdentifier("filter", "freq")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "72"), false, true, 0), 6000.0D, new ModelDestination(new ModelIdentifier("eg", "release2")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "73"), false, true, 0), 2000.0D, new ModelDestination(new ModelIdentifier("eg", "attack2")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "75"), false, true, 0), 6000.0D, new ModelDestination(new ModelIdentifier("eg", "decay2")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "67"), false, false, 3), -50.0D, new ModelDestination(ModelDestination.DESTINATION_GAIN));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "67"), false, false, 3), -2400.0D, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_rpn", "1"), false, true, 0), 100.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_rpn", "2"), false, true, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("master", "fine_tuning"), false, true, 0), 100.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[b++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("master", "coarse_tuning"), false, true, 0), 12800.0D, new ModelDestination(new ModelIdentifier("osc", "pitch")));
    defaultconnections[b++] = new ModelConnectionBlock(13500.0D, new ModelDestination(new ModelIdentifier("filter", "freq", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "delay", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "attack", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "hold", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "decay", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(1000.0D, new ModelDestination(new ModelIdentifier("eg", "sustain", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "release", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(1200.0D * Math.log(0.015D) / Math.log(2.0D), new ModelDestination(new ModelIdentifier("eg", "shutdown", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "delay", 1)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "attack", 1)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "hold", 1)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "decay", 1)));
    defaultconnections[b++] = new ModelConnectionBlock(1000.0D, new ModelDestination(new ModelIdentifier("eg", "sustain", 1)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "release", 1)));
    defaultconnections[b++] = new ModelConnectionBlock(-8.51318D, new ModelDestination(new ModelIdentifier("lfo", "freq", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("lfo", "delay", 0)));
    defaultconnections[b++] = new ModelConnectionBlock(-8.51318D, new ModelDestination(new ModelIdentifier("lfo", "freq", 1)));
    defaultconnections[b++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("lfo", "delay", 1)));
    keySortComparator = new KeySortComparator(null);
  }
  
  private static class KeySortComparator extends Object implements Comparator<ModelSource> {
    private KeySortComparator() {}
    
    public int compare(ModelSource param1ModelSource1, ModelSource param1ModelSource2) { return param1ModelSource1.getIdentifier().toString().compareTo(param1ModelSource2.getIdentifier().toString()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SoftPerformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */