package sun.java2d.loops;

public class GraphicsPrimitiveProxy extends GraphicsPrimitive {
  private Class owner;
  
  private String relativeClassName;
  
  public GraphicsPrimitiveProxy(Class paramClass, String paramString1, String paramString2, int paramInt, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    super(paramString2, paramInt, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    this.owner = paramClass;
    this.relativeClassName = paramString1;
  }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("makePrimitive called on a Proxy!"); }
  
  GraphicsPrimitive instantiate() {
    String str = getPackageName(this.owner.getName()) + "." + this.relativeClassName;
    try {
      Class clazz = Class.forName(str);
      GraphicsPrimitive graphicsPrimitive = (GraphicsPrimitive)clazz.newInstance();
      if (!satisfiesSameAs(graphicsPrimitive))
        throw new RuntimeException("Primitive " + graphicsPrimitive + " incompatible with proxy for " + str); 
      return graphicsPrimitive;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new RuntimeException(classNotFoundException.toString());
    } catch (InstantiationException instantiationException) {
      throw new RuntimeException(instantiationException.toString());
    } catch (IllegalAccessException illegalAccessException) {
      throw new RuntimeException(illegalAccessException.toString());
    } 
  }
  
  private static String getPackageName(String paramString) {
    int i = paramString.lastIndexOf('.');
    return (i < 0) ? paramString : paramString.substring(0, i);
  }
  
  public GraphicsPrimitive traceWrap() { return instantiate().traceWrap(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\GraphicsPrimitiveProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */