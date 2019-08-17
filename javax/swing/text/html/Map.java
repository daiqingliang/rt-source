package javax.swing.text.html;

import java.awt.Polygon;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.text.AttributeSet;

class Map implements Serializable {
  private String name;
  
  private Vector<AttributeSet> areaAttributes;
  
  private Vector<RegionContainment> areas;
  
  public Map() {}
  
  public Map(String paramString) { this.name = paramString; }
  
  public String getName() { return this.name; }
  
  public void addArea(AttributeSet paramAttributeSet) {
    if (paramAttributeSet == null)
      return; 
    if (this.areaAttributes == null)
      this.areaAttributes = new Vector(2); 
    this.areaAttributes.addElement(paramAttributeSet.copyAttributes());
  }
  
  public void removeArea(AttributeSet paramAttributeSet) {
    if (paramAttributeSet != null && this.areaAttributes != null) {
      int i = (this.areas != null) ? this.areas.size() : 0;
      for (int j = this.areaAttributes.size() - 1; j >= 0; j--) {
        if (((AttributeSet)this.areaAttributes.elementAt(j)).isEqual(paramAttributeSet)) {
          this.areaAttributes.removeElementAt(j);
          if (j < i)
            this.areas.removeElementAt(j); 
        } 
      } 
    } 
  }
  
  public AttributeSet[] getAreas() {
    int i = (this.areaAttributes != null) ? this.areaAttributes.size() : 0;
    if (i != 0) {
      AttributeSet[] arrayOfAttributeSet = new AttributeSet[i];
      this.areaAttributes.copyInto(arrayOfAttributeSet);
      return arrayOfAttributeSet;
    } 
    return null;
  }
  
  public AttributeSet getArea(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = (this.areaAttributes != null) ? this.areaAttributes.size() : 0;
    if (i > 0) {
      int j = (this.areas != null) ? this.areas.size() : 0;
      if (this.areas == null)
        this.areas = new Vector(i); 
      for (byte b = 0; b < i; b++) {
        if (b >= j)
          this.areas.addElement(createRegionContainment((AttributeSet)this.areaAttributes.elementAt(b))); 
        RegionContainment regionContainment = (RegionContainment)this.areas.elementAt(b);
        if (regionContainment != null && regionContainment.contains(paramInt1, paramInt2, paramInt3, paramInt4))
          return (AttributeSet)this.areaAttributes.elementAt(b); 
      } 
    } 
    return null;
  }
  
  protected RegionContainment createRegionContainment(AttributeSet paramAttributeSet) {
    Object object = paramAttributeSet.getAttribute(HTML.Attribute.SHAPE);
    if (object == null)
      object = "rect"; 
    if (object instanceof String) {
      String str = ((String)object).toLowerCase();
      RectangleRegionContainment rectangleRegionContainment = null;
      try {
        if (str.equals("rect")) {
          rectangleRegionContainment = new RectangleRegionContainment(paramAttributeSet);
        } else if (str.equals("circle")) {
          CircleRegionContainment circleRegionContainment = new CircleRegionContainment(paramAttributeSet);
        } else if (str.equals("poly")) {
          PolygonRegionContainment polygonRegionContainment = new PolygonRegionContainment(paramAttributeSet);
        } else if (str.equals("default")) {
          DefaultRegionContainment defaultRegionContainment = DefaultRegionContainment.sharedInstance();
        } 
      } catch (RuntimeException runtimeException) {
        rectangleRegionContainment = null;
      } 
      return rectangleRegionContainment;
    } 
    return null;
  }
  
  protected static int[] extractCoords(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof String))
      return null; 
    StringTokenizer stringTokenizer = new StringTokenizer((String)paramObject, ", \t\n\r");
    int[] arrayOfInt = null;
    byte b = 0;
    while (stringTokenizer.hasMoreElements()) {
      int i;
      String str = stringTokenizer.nextToken();
      if (str.endsWith("%")) {
        i = -1;
        str = str.substring(0, str.length() - 1);
      } else {
        i = 1;
      } 
      try {
        int j = Integer.parseInt(str);
        if (arrayOfInt == null) {
          arrayOfInt = new int[4];
        } else if (b == arrayOfInt.length) {
          int[] arrayOfInt1 = new int[arrayOfInt.length * 2];
          System.arraycopy(arrayOfInt, 0, arrayOfInt1, 0, arrayOfInt.length);
          arrayOfInt = arrayOfInt1;
        } 
        arrayOfInt[b++] = j * i;
      } catch (NumberFormatException numberFormatException) {
        return null;
      } 
    } 
    if (b > 0 && b != arrayOfInt.length) {
      int[] arrayOfInt1 = new int[b];
      System.arraycopy(arrayOfInt, 0, arrayOfInt1, 0, b);
      arrayOfInt = arrayOfInt1;
    } 
    return arrayOfInt;
  }
  
  static class CircleRegionContainment implements RegionContainment {
    int x;
    
    int y;
    
    int radiusSquared;
    
    float[] percentValues;
    
    int lastWidth;
    
    int lastHeight;
    
    public CircleRegionContainment(AttributeSet param1AttributeSet) {
      int[] arrayOfInt = Map.extractCoords(param1AttributeSet.getAttribute(HTML.Attribute.COORDS));
      if (arrayOfInt == null || arrayOfInt.length != 3)
        throw new RuntimeException("Unable to parse circular area"); 
      this.x = arrayOfInt[0];
      this.y = arrayOfInt[1];
      this.radiusSquared = arrayOfInt[2] * arrayOfInt[2];
      if (arrayOfInt[0] < 0 || arrayOfInt[1] < 0 || arrayOfInt[2] < 0) {
        this.lastWidth = this.lastHeight = -1;
        this.percentValues = new float[3];
        for (byte b = 0; b < 3; b++) {
          if (arrayOfInt[b] < 0) {
            this.percentValues[b] = arrayOfInt[b] / -100.0F;
          } else {
            this.percentValues[b] = -1.0F;
          } 
        } 
      } else {
        this.percentValues = null;
      } 
    }
    
    public boolean contains(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (this.percentValues != null && (this.lastWidth != param1Int3 || this.lastHeight != param1Int4)) {
        int i = Math.min(param1Int3, param1Int4) / 2;
        this.lastWidth = param1Int3;
        this.lastHeight = param1Int4;
        if (this.percentValues[0] != -1.0F)
          this.x = (int)(this.percentValues[0] * param1Int3); 
        if (this.percentValues[1] != -1.0F)
          this.y = (int)(this.percentValues[1] * param1Int4); 
        if (this.percentValues[2] != -1.0F) {
          this.radiusSquared = (int)(this.percentValues[2] * Math.min(param1Int3, param1Int4));
          this.radiusSquared *= this.radiusSquared;
        } 
      } 
      return ((param1Int1 - this.x) * (param1Int1 - this.x) + (param1Int2 - this.y) * (param1Int2 - this.y) <= this.radiusSquared);
    }
  }
  
  static class DefaultRegionContainment implements RegionContainment {
    static DefaultRegionContainment si = null;
    
    public static DefaultRegionContainment sharedInstance() {
      if (si == null)
        si = new DefaultRegionContainment(); 
      return si;
    }
    
    public boolean contains(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { return (param1Int1 <= param1Int3 && param1Int1 >= 0 && param1Int2 >= 0 && param1Int2 <= param1Int3); }
  }
  
  static class PolygonRegionContainment extends Polygon implements RegionContainment {
    float[] percentValues;
    
    int[] percentIndexs;
    
    int lastWidth;
    
    int lastHeight;
    
    public PolygonRegionContainment(AttributeSet param1AttributeSet) {
      int[] arrayOfInt = Map.extractCoords(param1AttributeSet.getAttribute(HTML.Attribute.COORDS));
      if (arrayOfInt == null || arrayOfInt.length == 0 || arrayOfInt.length % 2 != 0)
        throw new RuntimeException("Unable to parse polygon area"); 
      byte b = 0;
      this.lastWidth = this.lastHeight = -1;
      int i;
      for (i = arrayOfInt.length - 1; i >= 0; i--) {
        if (arrayOfInt[i] < 0)
          b++; 
      } 
      if (b > 0) {
        this.percentIndexs = new int[b];
        this.percentValues = new float[b];
        i = arrayOfInt.length - 1;
        byte b1 = 0;
        while (i >= 0) {
          if (arrayOfInt[i] < 0) {
            this.percentValues[b1] = arrayOfInt[i] / -100.0F;
            this.percentIndexs[b1] = i;
            b1++;
          } 
          i--;
        } 
      } else {
        this.percentIndexs = null;
        this.percentValues = null;
      } 
      this.npoints = arrayOfInt.length / 2;
      this.xpoints = new int[this.npoints];
      this.ypoints = new int[this.npoints];
      for (i = 0; i < this.npoints; i++) {
        this.xpoints[i] = arrayOfInt[i + i];
        this.ypoints[i] = arrayOfInt[i + i + 1];
      } 
    }
    
    public boolean contains(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (this.percentValues == null || (this.lastWidth == param1Int3 && this.lastHeight == param1Int4))
        return contains(param1Int1, param1Int2); 
      this.bounds = null;
      this.lastWidth = param1Int3;
      this.lastHeight = param1Int4;
      float f1 = param1Int3;
      float f2 = param1Int4;
      for (int i = this.percentValues.length - 1; i >= 0; i--) {
        if (this.percentIndexs[i] % 2 == 0) {
          this.xpoints[this.percentIndexs[i] / 2] = (int)(this.percentValues[i] * f1);
        } else {
          this.ypoints[this.percentIndexs[i] / 2] = (int)(this.percentValues[i] * f2);
        } 
      } 
      return contains(param1Int1, param1Int2);
    }
  }
  
  static class RectangleRegionContainment implements RegionContainment {
    float[] percents;
    
    int lastWidth;
    
    int lastHeight;
    
    int x0;
    
    int y0;
    
    int x1;
    
    int y1;
    
    public RectangleRegionContainment(AttributeSet param1AttributeSet) {
      int[] arrayOfInt = Map.extractCoords(param1AttributeSet.getAttribute(HTML.Attribute.COORDS));
      this.percents = null;
      if (arrayOfInt == null || arrayOfInt.length != 4)
        throw new RuntimeException("Unable to parse rectangular area"); 
      this.x0 = arrayOfInt[0];
      this.y0 = arrayOfInt[1];
      this.x1 = arrayOfInt[2];
      this.y1 = arrayOfInt[3];
      if (this.x0 < 0 || this.y0 < 0 || this.x1 < 0 || this.y1 < 0) {
        this.percents = new float[4];
        this.lastWidth = this.lastHeight = -1;
        for (byte b = 0; b < 4; b++) {
          if (arrayOfInt[b] < 0) {
            this.percents[b] = Math.abs(arrayOfInt[b]) / 100.0F;
          } else {
            this.percents[b] = -1.0F;
          } 
        } 
      } 
    }
    
    public boolean contains(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      if (this.percents == null)
        return contains(param1Int1, param1Int2); 
      if (this.lastWidth != param1Int3 || this.lastHeight != param1Int4) {
        this.lastWidth = param1Int3;
        this.lastHeight = param1Int4;
        if (this.percents[0] != -1.0F)
          this.x0 = (int)(this.percents[0] * param1Int3); 
        if (this.percents[1] != -1.0F)
          this.y0 = (int)(this.percents[1] * param1Int4); 
        if (this.percents[2] != -1.0F)
          this.x1 = (int)(this.percents[2] * param1Int3); 
        if (this.percents[3] != -1.0F)
          this.y1 = (int)(this.percents[3] * param1Int4); 
      } 
      return contains(param1Int1, param1Int2);
    }
    
    public boolean contains(int param1Int1, int param1Int2) { return (param1Int1 >= this.x0 && param1Int1 <= this.x1 && param1Int2 >= this.y0 && param1Int2 <= this.y1); }
  }
  
  static interface RegionContainment {
    boolean contains(int param1Int1, int param1Int2, int param1Int3, int param1Int4);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\Map.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */