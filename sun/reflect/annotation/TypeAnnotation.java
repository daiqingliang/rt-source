package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.AnnotatedElement;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class TypeAnnotation {
  private final TypeAnnotationTargetInfo targetInfo;
  
  private final LocationInfo loc;
  
  private final Annotation annotation;
  
  private final AnnotatedElement baseDeclaration;
  
  public TypeAnnotation(TypeAnnotationTargetInfo paramTypeAnnotationTargetInfo, LocationInfo paramLocationInfo, Annotation paramAnnotation, AnnotatedElement paramAnnotatedElement) {
    this.targetInfo = paramTypeAnnotationTargetInfo;
    this.loc = paramLocationInfo;
    this.annotation = paramAnnotation;
    this.baseDeclaration = paramAnnotatedElement;
  }
  
  public TypeAnnotationTargetInfo getTargetInfo() { return this.targetInfo; }
  
  public Annotation getAnnotation() { return this.annotation; }
  
  public AnnotatedElement getBaseDeclaration() { return this.baseDeclaration; }
  
  public LocationInfo getLocationInfo() { return this.loc; }
  
  public static List<TypeAnnotation> filter(TypeAnnotation[] paramArrayOfTypeAnnotation, TypeAnnotationTarget paramTypeAnnotationTarget) {
    ArrayList arrayList = new ArrayList(paramArrayOfTypeAnnotation.length);
    for (TypeAnnotation typeAnnotation : paramArrayOfTypeAnnotation) {
      if (typeAnnotation.getTargetInfo().getTarget() == paramTypeAnnotationTarget)
        arrayList.add(typeAnnotation); 
    } 
    arrayList.trimToSize();
    return arrayList;
  }
  
  public String toString() { return this.annotation.toString() + " with Targetnfo: " + this.targetInfo.toString() + " on base declaration: " + this.baseDeclaration.toString(); }
  
  public static final class LocationInfo {
    private final int depth;
    
    private final Location[] locations;
    
    public static final LocationInfo BASE_LOCATION = new LocationInfo();
    
    private LocationInfo() { this(0, new Location[0]); }
    
    private LocationInfo(int param1Int, Location[] param1ArrayOfLocation) {
      this.depth = param1Int;
      this.locations = param1ArrayOfLocation;
    }
    
    public static LocationInfo parseLocationInfo(ByteBuffer param1ByteBuffer) {
      byte b = param1ByteBuffer.get() & 0xFF;
      if (b == 0)
        return BASE_LOCATION; 
      Location[] arrayOfLocation = new Location[b];
      for (byte b1 = 0; b1 < b; b1++) {
        byte b2 = param1ByteBuffer.get();
        short s = (short)(param1ByteBuffer.get() & 0xFF);
        if (b2 != 0)
          if (!(((b2 == 1) ? 1 : 0) | ((b2 == 2) ? 1 : 0)) && b2 != 3)
            throw new AnnotationFormatError("Bad Location encoding in Type Annotation");  
        if (b2 != 3 && s != 0)
          throw new AnnotationFormatError("Bad Location encoding in Type Annotation"); 
        arrayOfLocation[b1] = new Location(b2, s);
      } 
      return new LocationInfo(b, arrayOfLocation);
    }
    
    public LocationInfo pushArray() { return pushLocation((byte)0, (short)0); }
    
    public LocationInfo pushInner() { return pushLocation((byte)1, (short)0); }
    
    public LocationInfo pushWildcard() { return pushLocation((byte)2, (short)0); }
    
    public LocationInfo pushTypeArg(short param1Short) { return pushLocation((byte)3, param1Short); }
    
    public LocationInfo pushLocation(byte param1Byte, short param1Short) {
      int i = this.depth + 1;
      Location[] arrayOfLocation = new Location[i];
      System.arraycopy(this.locations, 0, arrayOfLocation, 0, this.depth);
      arrayOfLocation[i - 1] = new Location(param1Byte, (short)(param1Short & 0xFF));
      return new LocationInfo(i, arrayOfLocation);
    }
    
    public TypeAnnotation[] filter(TypeAnnotation[] param1ArrayOfTypeAnnotation) {
      ArrayList arrayList = new ArrayList(param1ArrayOfTypeAnnotation.length);
      for (TypeAnnotation typeAnnotation : param1ArrayOfTypeAnnotation) {
        if (isSameLocationInfo(typeAnnotation.getLocationInfo()))
          arrayList.add(typeAnnotation); 
      } 
      return (TypeAnnotation[])arrayList.toArray(new TypeAnnotation[0]);
    }
    
    boolean isSameLocationInfo(LocationInfo param1LocationInfo) {
      if (this.depth != param1LocationInfo.depth)
        return false; 
      for (byte b = 0; b < this.depth; b++) {
        if (!this.locations[b].isSameLocation(param1LocationInfo.locations[b]))
          return false; 
      } 
      return true;
    }
    
    public static final class Location {
      public final byte tag;
      
      public final short index;
      
      boolean isSameLocation(Location param2Location) { return (this.tag == param2Location.tag && this.index == param2Location.index); }
      
      public Location(byte param2Byte, short param2Short) {
        this.tag = param2Byte;
        this.index = param2Short;
      }
    }
  }
  
  public enum TypeAnnotationTarget {
    CLASS_TYPE_PARAMETER, METHOD_TYPE_PARAMETER, CLASS_EXTENDS, CLASS_IMPLEMENTS, CLASS_TYPE_PARAMETER_BOUND, METHOD_TYPE_PARAMETER_BOUND, FIELD, METHOD_RETURN, METHOD_RECEIVER, METHOD_FORMAL_PARAMETER, THROWS;
  }
  
  public static final class TypeAnnotationTargetInfo {
    private final TypeAnnotation.TypeAnnotationTarget target;
    
    private final int count;
    
    private final int secondaryIndex;
    
    private static final int UNUSED_INDEX = -2;
    
    public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget param1TypeAnnotationTarget) { this(param1TypeAnnotationTarget, -2, -2); }
    
    public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget param1TypeAnnotationTarget, int param1Int) { this(param1TypeAnnotationTarget, param1Int, -2); }
    
    public TypeAnnotationTargetInfo(TypeAnnotation.TypeAnnotationTarget param1TypeAnnotationTarget, int param1Int1, int param1Int2) {
      this.target = param1TypeAnnotationTarget;
      this.count = param1Int1;
      this.secondaryIndex = param1Int2;
    }
    
    public TypeAnnotation.TypeAnnotationTarget getTarget() { return this.target; }
    
    public int getCount() { return this.count; }
    
    public int getSecondaryIndex() { return this.secondaryIndex; }
    
    public String toString() { return "" + this.target + ": " + this.count + ", " + this.secondaryIndex; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\TypeAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */