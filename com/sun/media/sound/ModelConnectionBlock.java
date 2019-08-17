package com.sun.media.sound;

import java.util.Arrays;

public final class ModelConnectionBlock {
  private static final ModelSource[] no_sources = new ModelSource[0];
  
  private ModelSource[] sources = no_sources;
  
  private double scale = 1.0D;
  
  private ModelDestination destination;
  
  public ModelConnectionBlock() {}
  
  public ModelConnectionBlock(double paramDouble, ModelDestination paramModelDestination) {
    this.scale = paramDouble;
    this.destination = paramModelDestination;
  }
  
  public ModelConnectionBlock(ModelSource paramModelSource, ModelDestination paramModelDestination) {
    if (paramModelSource != null) {
      this.sources = new ModelSource[1];
      this.sources[0] = paramModelSource;
    } 
    this.destination = paramModelDestination;
  }
  
  public ModelConnectionBlock(ModelSource paramModelSource, double paramDouble, ModelDestination paramModelDestination) {
    if (paramModelSource != null) {
      this.sources = new ModelSource[1];
      this.sources[0] = paramModelSource;
    } 
    this.scale = paramDouble;
    this.destination = paramModelDestination;
  }
  
  public ModelConnectionBlock(ModelSource paramModelSource1, ModelSource paramModelSource2, ModelDestination paramModelDestination) {
    if (paramModelSource1 != null)
      if (paramModelSource2 == null) {
        this.sources = new ModelSource[1];
        this.sources[0] = paramModelSource1;
      } else {
        this.sources = new ModelSource[2];
        this.sources[0] = paramModelSource1;
        this.sources[1] = paramModelSource2;
      }  
    this.destination = paramModelDestination;
  }
  
  public ModelConnectionBlock(ModelSource paramModelSource1, ModelSource paramModelSource2, double paramDouble, ModelDestination paramModelDestination) {
    if (paramModelSource1 != null)
      if (paramModelSource2 == null) {
        this.sources = new ModelSource[1];
        this.sources[0] = paramModelSource1;
      } else {
        this.sources = new ModelSource[2];
        this.sources[0] = paramModelSource1;
        this.sources[1] = paramModelSource2;
      }  
    this.scale = paramDouble;
    this.destination = paramModelDestination;
  }
  
  public ModelDestination getDestination() { return this.destination; }
  
  public void setDestination(ModelDestination paramModelDestination) { this.destination = paramModelDestination; }
  
  public double getScale() { return this.scale; }
  
  public void setScale(double paramDouble) { this.scale = paramDouble; }
  
  public ModelSource[] getSources() { return (ModelSource[])Arrays.copyOf(this.sources, this.sources.length); }
  
  public void setSources(ModelSource[] paramArrayOfModelSource) { this.sources = (paramArrayOfModelSource == null) ? no_sources : (ModelSource[])Arrays.copyOf(paramArrayOfModelSource, paramArrayOfModelSource.length); }
  
  public void addSource(ModelSource paramModelSource) {
    ModelSource[] arrayOfModelSource = this.sources;
    this.sources = new ModelSource[arrayOfModelSource.length + 1];
    System.arraycopy(arrayOfModelSource, 0, this.sources, 0, arrayOfModelSource.length);
    this.sources[this.sources.length - 1] = paramModelSource;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelConnectionBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */