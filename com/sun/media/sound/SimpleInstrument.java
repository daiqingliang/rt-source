package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.Patch;

public class SimpleInstrument extends ModelInstrument {
  protected int preset = 0;
  
  protected int bank = 0;
  
  protected boolean percussion = false;
  
  protected String name = "";
  
  protected List<SimpleInstrumentPart> parts = new ArrayList();
  
  public SimpleInstrument() { super(null, null, null, null); }
  
  public void clear() { this.parts.clear(); }
  
  public void add(ModelPerformer[] paramArrayOfModelPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    SimpleInstrumentPart simpleInstrumentPart = new SimpleInstrumentPart(null);
    simpleInstrumentPart.performers = paramArrayOfModelPerformer;
    simpleInstrumentPart.keyFrom = paramInt1;
    simpleInstrumentPart.keyTo = paramInt2;
    simpleInstrumentPart.velFrom = paramInt3;
    simpleInstrumentPart.velTo = paramInt4;
    simpleInstrumentPart.exclusiveClass = paramInt5;
    this.parts.add(simpleInstrumentPart);
  }
  
  public void add(ModelPerformer[] paramArrayOfModelPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { add(paramArrayOfModelPerformer, paramInt1, paramInt2, paramInt3, paramInt4, -1); }
  
  public void add(ModelPerformer[] paramArrayOfModelPerformer, int paramInt1, int paramInt2) { add(paramArrayOfModelPerformer, paramInt1, paramInt2, 0, 127, -1); }
  
  public void add(ModelPerformer[] paramArrayOfModelPerformer) { add(paramArrayOfModelPerformer, 0, 127, 0, 127, -1); }
  
  public void add(ModelPerformer paramModelPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { add(new ModelPerformer[] { paramModelPerformer }, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void add(ModelPerformer paramModelPerformer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { add(new ModelPerformer[] { paramModelPerformer }, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void add(ModelPerformer paramModelPerformer, int paramInt1, int paramInt2) { add(new ModelPerformer[] { paramModelPerformer }, paramInt1, paramInt2); }
  
  public void add(ModelPerformer paramModelPerformer) { add(new ModelPerformer[] { paramModelPerformer }); }
  
  public void add(ModelInstrument paramModelInstrument, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { add(paramModelInstrument.getPerformers(), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  public void add(ModelInstrument paramModelInstrument, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { add(paramModelInstrument.getPerformers(), paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void add(ModelInstrument paramModelInstrument, int paramInt1, int paramInt2) { add(paramModelInstrument.getPerformers(), paramInt1, paramInt2); }
  
  public void add(ModelInstrument paramModelInstrument) { add(paramModelInstrument.getPerformers()); }
  
  public ModelPerformer[] getPerformers() {
    int i = 0;
    for (SimpleInstrumentPart simpleInstrumentPart : this.parts) {
      if (simpleInstrumentPart.performers != null)
        i += simpleInstrumentPart.performers.length; 
    } 
    ModelPerformer[] arrayOfModelPerformer = new ModelPerformer[i];
    byte b = 0;
    for (SimpleInstrumentPart simpleInstrumentPart : this.parts) {
      if (simpleInstrumentPart.performers != null)
        for (ModelPerformer modelPerformer1 : simpleInstrumentPart.performers) {
          ModelPerformer modelPerformer2 = new ModelPerformer();
          modelPerformer2.setName(getName());
          arrayOfModelPerformer[b++] = modelPerformer2;
          modelPerformer2.setDefaultConnectionsEnabled(modelPerformer1.isDefaultConnectionsEnabled());
          modelPerformer2.setKeyFrom(modelPerformer1.getKeyFrom());
          modelPerformer2.setKeyTo(modelPerformer1.getKeyTo());
          modelPerformer2.setVelFrom(modelPerformer1.getVelFrom());
          modelPerformer2.setVelTo(modelPerformer1.getVelTo());
          modelPerformer2.setExclusiveClass(modelPerformer1.getExclusiveClass());
          modelPerformer2.setSelfNonExclusive(modelPerformer1.isSelfNonExclusive());
          modelPerformer2.setReleaseTriggered(modelPerformer1.isReleaseTriggered());
          if (simpleInstrumentPart.exclusiveClass != -1)
            modelPerformer2.setExclusiveClass(simpleInstrumentPart.exclusiveClass); 
          if (simpleInstrumentPart.keyFrom > modelPerformer2.getKeyFrom())
            modelPerformer2.setKeyFrom(simpleInstrumentPart.keyFrom); 
          if (simpleInstrumentPart.keyTo < modelPerformer2.getKeyTo())
            modelPerformer2.setKeyTo(simpleInstrumentPart.keyTo); 
          if (simpleInstrumentPart.velFrom > modelPerformer2.getVelFrom())
            modelPerformer2.setVelFrom(simpleInstrumentPart.velFrom); 
          if (simpleInstrumentPart.velTo < modelPerformer2.getVelTo())
            modelPerformer2.setVelTo(simpleInstrumentPart.velTo); 
          modelPerformer2.getOscillators().addAll(modelPerformer1.getOscillators());
          modelPerformer2.getConnectionBlocks().addAll(modelPerformer1.getConnectionBlocks());
        }  
    } 
    return arrayOfModelPerformer;
  }
  
  public Object getData() { return null; }
  
  public String getName() { return this.name; }
  
  public void setName(String paramString) { this.name = paramString; }
  
  public ModelPatch getPatch() { return new ModelPatch(this.bank, this.preset, this.percussion); }
  
  public void setPatch(Patch paramPatch) {
    if (paramPatch instanceof ModelPatch && ((ModelPatch)paramPatch).isPercussion()) {
      this.percussion = true;
      this.bank = paramPatch.getBank();
      this.preset = paramPatch.getProgram();
    } else {
      this.percussion = false;
      this.bank = paramPatch.getBank();
      this.preset = paramPatch.getProgram();
    } 
  }
  
  private static class SimpleInstrumentPart {
    ModelPerformer[] performers;
    
    int keyFrom;
    
    int keyTo;
    
    int velFrom;
    
    int velTo;
    
    int exclusiveClass;
    
    private SimpleInstrumentPart() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SimpleInstrument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */