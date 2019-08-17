package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.util.HashMap;

public final class CompositeType {
  private static int unusedUID = 1;
  
  private static final HashMap<String, Integer> compositeUIDMap = new HashMap(100);
  
  public static final String DESC_ANY = "Any CompositeContext";
  
  public static final String DESC_XOR = "XOR mode";
  
  public static final String DESC_CLEAR = "Porter-Duff Clear";
  
  public static final String DESC_SRC = "Porter-Duff Src";
  
  public static final String DESC_DST = "Porter-Duff Dst";
  
  public static final String DESC_SRC_OVER = "Porter-Duff Src Over Dst";
  
  public static final String DESC_DST_OVER = "Porter-Duff Dst Over Src";
  
  public static final String DESC_SRC_IN = "Porter-Duff Src In Dst";
  
  public static final String DESC_DST_IN = "Porter-Duff Dst In Src";
  
  public static final String DESC_SRC_OUT = "Porter-Duff Src HeldOutBy Dst";
  
  public static final String DESC_DST_OUT = "Porter-Duff Dst HeldOutBy Src";
  
  public static final String DESC_SRC_ATOP = "Porter-Duff Src Atop Dst";
  
  public static final String DESC_DST_ATOP = "Porter-Duff Dst Atop Src";
  
  public static final String DESC_ALPHA_XOR = "Porter-Duff Xor";
  
  public static final String DESC_SRC_NO_EA = "Porter-Duff Src, No Extra Alpha";
  
  public static final String DESC_SRC_OVER_NO_EA = "Porter-Duff SrcOverDst, No Extra Alpha";
  
  public static final String DESC_ANY_ALPHA = "Any AlphaComposite Rule";
  
  public static final CompositeType Any = new CompositeType(null, "Any CompositeContext");
  
  public static final CompositeType General = Any;
  
  public static final CompositeType AnyAlpha = General.deriveSubType("Any AlphaComposite Rule");
  
  public static final CompositeType Xor = General.deriveSubType("XOR mode");
  
  public static final CompositeType Clear = AnyAlpha.deriveSubType("Porter-Duff Clear");
  
  public static final CompositeType Src = AnyAlpha.deriveSubType("Porter-Duff Src");
  
  public static final CompositeType Dst = AnyAlpha.deriveSubType("Porter-Duff Dst");
  
  public static final CompositeType SrcOver = AnyAlpha.deriveSubType("Porter-Duff Src Over Dst");
  
  public static final CompositeType DstOver = AnyAlpha.deriveSubType("Porter-Duff Dst Over Src");
  
  public static final CompositeType SrcIn = AnyAlpha.deriveSubType("Porter-Duff Src In Dst");
  
  public static final CompositeType DstIn = AnyAlpha.deriveSubType("Porter-Duff Dst In Src");
  
  public static final CompositeType SrcOut = AnyAlpha.deriveSubType("Porter-Duff Src HeldOutBy Dst");
  
  public static final CompositeType DstOut = AnyAlpha.deriveSubType("Porter-Duff Dst HeldOutBy Src");
  
  public static final CompositeType SrcAtop = AnyAlpha.deriveSubType("Porter-Duff Src Atop Dst");
  
  public static final CompositeType DstAtop = AnyAlpha.deriveSubType("Porter-Duff Dst Atop Src");
  
  public static final CompositeType AlphaXor = AnyAlpha.deriveSubType("Porter-Duff Xor");
  
  public static final CompositeType SrcNoEa = Src.deriveSubType("Porter-Duff Src, No Extra Alpha");
  
  public static final CompositeType SrcOverNoEa = SrcOver.deriveSubType("Porter-Duff SrcOverDst, No Extra Alpha");
  
  public static final CompositeType OpaqueSrcOverNoEa = SrcOverNoEa.deriveSubType("Porter-Duff Src").deriveSubType("Porter-Duff Src, No Extra Alpha");
  
  private int uniqueID;
  
  private String desc;
  
  private CompositeType next;
  
  public CompositeType deriveSubType(String paramString) { return new CompositeType(this, paramString); }
  
  public static CompositeType forAlphaComposite(AlphaComposite paramAlphaComposite) {
    switch (paramAlphaComposite.getRule()) {
      case 1:
        return Clear;
      case 2:
        return (paramAlphaComposite.getAlpha() >= 1.0F) ? SrcNoEa : Src;
      case 9:
        return Dst;
      case 3:
        return (paramAlphaComposite.getAlpha() >= 1.0F) ? SrcOverNoEa : SrcOver;
      case 4:
        return DstOver;
      case 5:
        return SrcIn;
      case 6:
        return DstIn;
      case 7:
        return SrcOut;
      case 8:
        return DstOut;
      case 10:
        return SrcAtop;
      case 11:
        return DstAtop;
      case 12:
        return AlphaXor;
    } 
    throw new InternalError("Unrecognized alpha rule");
  }
  
  private CompositeType(CompositeType paramCompositeType, String paramString) {
    this.next = paramCompositeType;
    this.desc = paramString;
    this.uniqueID = makeUniqueID(paramString);
  }
  
  public static final int makeUniqueID(String paramString) {
    Integer integer = (Integer)compositeUIDMap.get(paramString);
    if (integer == null) {
      if (unusedUID > 255)
        throw new InternalError("composite type id overflow"); 
      integer = Integer.valueOf(unusedUID++);
      compositeUIDMap.put(paramString, integer);
    } 
    return integer.intValue();
  }
  
  public int getUniqueID() { return this.uniqueID; }
  
  public String getDescriptor() { return this.desc; }
  
  public CompositeType getSuperType() { return this.next; }
  
  public int hashCode() { return this.desc.hashCode(); }
  
  public boolean isDerivedFrom(CompositeType paramCompositeType) {
    CompositeType compositeType = this;
    do {
      if (compositeType.desc == paramCompositeType.desc)
        return true; 
      compositeType = compositeType.next;
    } while (compositeType != null);
    return false;
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof CompositeType) ? ((((CompositeType)paramObject).uniqueID == this.uniqueID)) : false; }
  
  public String toString() { return this.desc; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\CompositeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */