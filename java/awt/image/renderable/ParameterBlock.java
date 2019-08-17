package java.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.io.Serializable;
import java.util.Vector;

public class ParameterBlock implements Cloneable, Serializable {
  protected Vector<Object> sources = new Vector();
  
  protected Vector<Object> parameters = new Vector();
  
  public ParameterBlock() {}
  
  public ParameterBlock(Vector<Object> paramVector) { setSources(paramVector); }
  
  public ParameterBlock(Vector<Object> paramVector1, Vector<Object> paramVector2) {
    setSources(paramVector1);
    setParameters(paramVector2);
  }
  
  public Object shallowClone() {
    try {
      return super.clone();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Object clone() {
    ParameterBlock parameterBlock;
    try {
      parameterBlock = (ParameterBlock)super.clone();
    } catch (Exception exception) {
      return null;
    } 
    if (this.sources != null)
      parameterBlock.setSources((Vector)this.sources.clone()); 
    if (this.parameters != null)
      parameterBlock.setParameters((Vector)this.parameters.clone()); 
    return parameterBlock;
  }
  
  public ParameterBlock addSource(Object paramObject) {
    this.sources.addElement(paramObject);
    return this;
  }
  
  public Object getSource(int paramInt) { return this.sources.elementAt(paramInt); }
  
  public ParameterBlock setSource(Object paramObject, int paramInt) {
    int i = this.sources.size();
    int j = paramInt + 1;
    if (i < j)
      this.sources.setSize(j); 
    this.sources.setElementAt(paramObject, paramInt);
    return this;
  }
  
  public RenderedImage getRenderedSource(int paramInt) { return (RenderedImage)this.sources.elementAt(paramInt); }
  
  public RenderableImage getRenderableSource(int paramInt) { return (RenderableImage)this.sources.elementAt(paramInt); }
  
  public int getNumSources() { return this.sources.size(); }
  
  public Vector<Object> getSources() { return this.sources; }
  
  public void setSources(Vector<Object> paramVector) { this.sources = paramVector; }
  
  public void removeSources() { this.sources = new Vector(); }
  
  public int getNumParameters() { return this.parameters.size(); }
  
  public Vector<Object> getParameters() { return this.parameters; }
  
  public void setParameters(Vector<Object> paramVector) { this.parameters = paramVector; }
  
  public void removeParameters() { this.parameters = new Vector(); }
  
  public ParameterBlock add(Object paramObject) {
    this.parameters.addElement(paramObject);
    return this;
  }
  
  public ParameterBlock add(byte paramByte) { return add(new Byte(paramByte)); }
  
  public ParameterBlock add(char paramChar) { return add(new Character(paramChar)); }
  
  public ParameterBlock add(short paramShort) { return add(new Short(paramShort)); }
  
  public ParameterBlock add(int paramInt) { return add(new Integer(paramInt)); }
  
  public ParameterBlock add(long paramLong) { return add(new Long(paramLong)); }
  
  public ParameterBlock add(float paramFloat) { return add(new Float(paramFloat)); }
  
  public ParameterBlock add(double paramDouble) { return add(new Double(paramDouble)); }
  
  public ParameterBlock set(Object paramObject, int paramInt) {
    int i = this.parameters.size();
    int j = paramInt + 1;
    if (i < j)
      this.parameters.setSize(j); 
    this.parameters.setElementAt(paramObject, paramInt);
    return this;
  }
  
  public ParameterBlock set(byte paramByte, int paramInt) { return set(new Byte(paramByte), paramInt); }
  
  public ParameterBlock set(char paramChar, int paramInt) { return set(new Character(paramChar), paramInt); }
  
  public ParameterBlock set(short paramShort, int paramInt) { return set(new Short(paramShort), paramInt); }
  
  public ParameterBlock set(int paramInt1, int paramInt2) { return set(new Integer(paramInt1), paramInt2); }
  
  public ParameterBlock set(long paramLong, int paramInt) { return set(new Long(paramLong), paramInt); }
  
  public ParameterBlock set(float paramFloat, int paramInt) { return set(new Float(paramFloat), paramInt); }
  
  public ParameterBlock set(double paramDouble, int paramInt) { return set(new Double(paramDouble), paramInt); }
  
  public Object getObjectParameter(int paramInt) { return this.parameters.elementAt(paramInt); }
  
  public byte getByteParameter(int paramInt) { return ((Byte)this.parameters.elementAt(paramInt)).byteValue(); }
  
  public char getCharParameter(int paramInt) { return ((Character)this.parameters.elementAt(paramInt)).charValue(); }
  
  public short getShortParameter(int paramInt) { return ((Short)this.parameters.elementAt(paramInt)).shortValue(); }
  
  public int getIntParameter(int paramInt) { return ((Integer)this.parameters.elementAt(paramInt)).intValue(); }
  
  public long getLongParameter(int paramInt) { return ((Long)this.parameters.elementAt(paramInt)).longValue(); }
  
  public float getFloatParameter(int paramInt) { return ((Float)this.parameters.elementAt(paramInt)).floatValue(); }
  
  public double getDoubleParameter(int paramInt) { return ((Double)this.parameters.elementAt(paramInt)).doubleValue(); }
  
  public Class[] getParamClasses() {
    int i = getNumParameters();
    Class[] arrayOfClass = new Class[i];
    for (byte b = 0; b < i; b++) {
      Object object = getObjectParameter(b);
      if (object instanceof Byte) {
        arrayOfClass[b] = byte.class;
      } else if (object instanceof Character) {
        arrayOfClass[b] = char.class;
      } else if (object instanceof Short) {
        arrayOfClass[b] = short.class;
      } else if (object instanceof Integer) {
        arrayOfClass[b] = int.class;
      } else if (object instanceof Long) {
        arrayOfClass[b] = long.class;
      } else if (object instanceof Float) {
        arrayOfClass[b] = float.class;
      } else if (object instanceof Double) {
        arrayOfClass[b] = double.class;
      } else {
        arrayOfClass[b] = object.getClass();
      } 
    } 
    return arrayOfClass;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\renderable\ParameterBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */