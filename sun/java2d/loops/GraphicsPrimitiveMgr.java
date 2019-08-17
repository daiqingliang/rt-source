package sun.java2d.loops;

import java.util.Arrays;
import java.util.Comparator;

public final class GraphicsPrimitiveMgr {
  private static final boolean debugTrace = false;
  
  private static GraphicsPrimitive[] primitives;
  
  private static GraphicsPrimitive[] generalPrimitives;
  
  private static boolean needssort = true;
  
  private static Comparator primSorter;
  
  private static Comparator primFinder;
  
  private static native void initIDs(Class paramClass1, Class paramClass2, Class paramClass3, Class paramClass4, Class paramClass5, Class paramClass6, Class paramClass7, Class paramClass8, Class paramClass9, Class paramClass10, Class paramClass11);
  
  private static native void registerNativeLoops();
  
  public static void register(GraphicsPrimitive[] paramArrayOfGraphicsPrimitive) {
    GraphicsPrimitive[] arrayOfGraphicsPrimitive1 = primitives;
    int i = 0;
    int j = paramArrayOfGraphicsPrimitive.length;
    if (arrayOfGraphicsPrimitive1 != null)
      i = arrayOfGraphicsPrimitive1.length; 
    GraphicsPrimitive[] arrayOfGraphicsPrimitive2 = new GraphicsPrimitive[i + j];
    if (arrayOfGraphicsPrimitive1 != null)
      System.arraycopy(arrayOfGraphicsPrimitive1, 0, arrayOfGraphicsPrimitive2, 0, i); 
    System.arraycopy(paramArrayOfGraphicsPrimitive, 0, arrayOfGraphicsPrimitive2, i, j);
    needssort = true;
    primitives = arrayOfGraphicsPrimitive2;
  }
  
  public static void registerGeneral(GraphicsPrimitive paramGraphicsPrimitive) {
    if (generalPrimitives == null) {
      generalPrimitives = new GraphicsPrimitive[] { paramGraphicsPrimitive };
      return;
    } 
    int i = generalPrimitives.length;
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = new GraphicsPrimitive[i + 1];
    System.arraycopy(generalPrimitives, 0, arrayOfGraphicsPrimitive, 0, i);
    arrayOfGraphicsPrimitive[i] = paramGraphicsPrimitive;
    generalPrimitives = arrayOfGraphicsPrimitive;
  }
  
  public static GraphicsPrimitive locate(int paramInt, SurfaceType paramSurfaceType) { return locate(paramInt, SurfaceType.OpaqueColor, CompositeType.Src, paramSurfaceType); }
  
  public static GraphicsPrimitive locate(int paramInt, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    GraphicsPrimitive graphicsPrimitive = locatePrim(paramInt, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (graphicsPrimitive == null) {
      graphicsPrimitive = locateGeneral(paramInt);
      if (graphicsPrimitive != null) {
        graphicsPrimitive = graphicsPrimitive.makePrimitive(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
        if (graphicsPrimitive != null && GraphicsPrimitive.traceflags != 0)
          graphicsPrimitive = graphicsPrimitive.traceWrap(); 
      } 
    } 
    return graphicsPrimitive;
  }
  
  public static GraphicsPrimitive locatePrim(int paramInt, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    PrimitiveSpec primitiveSpec = new PrimitiveSpec(null);
    for (SurfaceType surfaceType = paramSurfaceType2; surfaceType != null; surfaceType = surfaceType.getSuperType()) {
      for (SurfaceType surfaceType1 = paramSurfaceType1; surfaceType1 != null; surfaceType1 = surfaceType1.getSuperType()) {
        for (CompositeType compositeType = paramCompositeType; compositeType != null; compositeType = compositeType.getSuperType()) {
          primitiveSpec.uniqueID = GraphicsPrimitive.makeUniqueID(paramInt, surfaceType1, compositeType, surfaceType);
          GraphicsPrimitive graphicsPrimitive = locate(primitiveSpec);
          if (graphicsPrimitive != null)
            return graphicsPrimitive; 
        } 
      } 
    } 
    return null;
  }
  
  private static GraphicsPrimitive locateGeneral(int paramInt) {
    if (generalPrimitives == null)
      return null; 
    for (byte b = 0; b < generalPrimitives.length; b++) {
      GraphicsPrimitive graphicsPrimitive = generalPrimitives[b];
      if (graphicsPrimitive.getPrimTypeID() == paramInt)
        return graphicsPrimitive; 
    } 
    return null;
  }
  
  private static GraphicsPrimitive locate(PrimitiveSpec paramPrimitiveSpec) {
    if (needssort) {
      if (GraphicsPrimitive.traceflags != 0)
        for (byte b = 0; b < primitives.length; b++)
          primitives[b] = primitives[b].traceWrap();  
      Arrays.sort(primitives, primSorter);
      needssort = false;
    } 
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = primitives;
    if (arrayOfGraphicsPrimitive == null)
      return null; 
    int i = Arrays.binarySearch(arrayOfGraphicsPrimitive, paramPrimitiveSpec, primFinder);
    if (i >= 0) {
      GraphicsPrimitive graphicsPrimitive = arrayOfGraphicsPrimitive[i];
      if (graphicsPrimitive instanceof GraphicsPrimitiveProxy) {
        graphicsPrimitive = ((GraphicsPrimitiveProxy)graphicsPrimitive).instantiate();
        arrayOfGraphicsPrimitive[i] = graphicsPrimitive;
      } 
      return graphicsPrimitive;
    } 
    return null;
  }
  
  private static void writeLog(String paramString) {}
  
  public static void testPrimitiveInstantiation() { testPrimitiveInstantiation(false); }
  
  public static void testPrimitiveInstantiation(boolean paramBoolean) {
    byte b1 = 0;
    byte b2 = 0;
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = primitives;
    for (byte b3 = 0; b3 < arrayOfGraphicsPrimitive.length; b3++) {
      GraphicsPrimitive graphicsPrimitive = arrayOfGraphicsPrimitive[b3];
      if (graphicsPrimitive instanceof GraphicsPrimitiveProxy) {
        GraphicsPrimitive graphicsPrimitive1 = ((GraphicsPrimitiveProxy)graphicsPrimitive).instantiate();
        if (!graphicsPrimitive1.getSignature().equals(graphicsPrimitive.getSignature()) || graphicsPrimitive1.getUniqueID() != graphicsPrimitive.getUniqueID()) {
          System.out.println("r.getSignature == " + graphicsPrimitive1.getSignature());
          System.out.println("r.getUniqueID == " + graphicsPrimitive1.getUniqueID());
          System.out.println("p.getSignature == " + graphicsPrimitive.getSignature());
          System.out.println("p.getUniqueID == " + graphicsPrimitive.getUniqueID());
          throw new RuntimeException("Primitive " + graphicsPrimitive + " returns wrong signature for " + graphicsPrimitive1.getClass());
        } 
        b2++;
        graphicsPrimitive = graphicsPrimitive1;
        if (paramBoolean)
          System.out.println(graphicsPrimitive); 
      } else {
        if (paramBoolean)
          System.out.println(graphicsPrimitive + " (not proxied)."); 
        b1++;
      } 
    } 
    System.out.println(b1 + " graphics primitives were not proxied.");
    System.out.println(b2 + " proxied graphics primitives resolved correctly.");
    System.out.println((b1 + b2) + " total graphics primitives");
  }
  
  public static void main(String[] paramArrayOfString) {
    if (needssort) {
      Arrays.sort(primitives, primSorter);
      needssort = false;
    } 
    testPrimitiveInstantiation((paramArrayOfString.length > 0));
  }
  
  static  {
    initIDs(GraphicsPrimitive.class, SurfaceType.class, CompositeType.class, sun.java2d.SunGraphics2D.class, java.awt.Color.class, java.awt.geom.AffineTransform.class, XORComposite.class, java.awt.AlphaComposite.class, java.awt.geom.Path2D.class, java.awt.geom.Path2D.Float.class, sun.awt.SunHints.class);
    CustomComponent.register();
    GeneralRenderer.register();
    registerNativeLoops();
    primSorter = new Comparator() {
        public int compare(Object param1Object1, Object param1Object2) {
          int i = ((GraphicsPrimitive)param1Object1).getUniqueID();
          int j = ((GraphicsPrimitive)param1Object2).getUniqueID();
          return (i == j) ? 0 : ((i < j) ? -1 : 1);
        }
      };
    primFinder = new Comparator() {
        public int compare(Object param1Object1, Object param1Object2) {
          int i = ((GraphicsPrimitive)param1Object1).getUniqueID();
          int j = ((GraphicsPrimitiveMgr.PrimitiveSpec)param1Object2).uniqueID;
          return (i == j) ? 0 : ((i < j) ? -1 : 1);
        }
      };
  }
  
  private static class PrimitiveSpec {
    public int uniqueID;
    
    private PrimitiveSpec() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\GraphicsPrimitiveMgr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */