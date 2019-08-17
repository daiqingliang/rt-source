package sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.TransformBlit;
import sun.java2d.pipe.Region;

class OGLTextureToSurfaceTransform extends TransformBlit {
  OGLTextureToSurfaceTransform() { super(OGLSurfaceData.OpenGLTexture, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface); }
  
  public void Transform(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) { OGLBlitLoops.IsoBlit(paramSurfaceData1, paramSurfaceData2, null, null, paramComposite, paramRegion, paramAffineTransform, paramInt1, paramInt2, paramInt3, paramInt2 + paramInt6, paramInt3 + paramInt7, paramInt4, paramInt5, (paramInt4 + paramInt6), (paramInt5 + paramInt7), true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLTextureToSurfaceTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */