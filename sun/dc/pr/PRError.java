package sun.dc.pr;

public class PRError extends RuntimeException {
  public static final String UNEX_setUsage = "setUsage: unexpected";
  
  public static final String UNEX_setFillMode = "setFillMode: unexpected";
  
  public static final String UNEX_setPenDiameter = "setPenDiameter: unexpected";
  
  public static final String UNEX_setPenT4 = "setPenT4: unexpected";
  
  public static final String UNEX_setPenDisplacement = "setPenDisplacement: unexpected";
  
  public static final String UNEX_setPenFitting = "setPenFitting: unexpected";
  
  public static final String UNEX_setCaps = "setCaps: unexpected";
  
  public static final String UNEX_setCorners = "setCorners: unexpected";
  
  public static final String UNEX_setDash = "setDash: unexpected";
  
  public static final String UNEX_setDashT4 = "setDashT4: unexpected";
  
  public static final String UNEX_beginPath = "beginPath: unexpected";
  
  public static final String UNEX_beginSubpath = "beginSubpath: unexpected";
  
  public static final String UNEX_appendCubic = "appendCubic: unexpected";
  
  public static final String UNEX_appendLine = "appendLine: unexpected";
  
  public static final String UNEX_appendQuadratic = "appendQuadratic: unexpected";
  
  public static final String UNEX_closedSubpath = "closedSubpath: unexpected";
  
  public static final String UNEX_endPath = "endPath: unexpected";
  
  public static final String UNEX_useProxy = "useProxy: unexpected";
  
  public static final String UNEX_setOutputConsumer = "setOutputConsumer: unexpected";
  
  public static final String UNEX_setOutputT6 = "setOutputT6: unexpected";
  
  public static final String UNEX_getAlphaBox = "getAlphaBox: unexpected";
  
  public static final String UNEX_setOutputArea = "setOutputArea: unexpected";
  
  public static final String UNEX_getTileState = "getTileState: unexpected";
  
  public static final String UNEX_writeAlpha = "writeAlpha: unexpected";
  
  public static final String UNEX_nextTile = "nextTile: unexpected";
  
  public static final String UNK_usage = "setUsage: unknown usage type";
  
  public static final String UNK_fillmode = "setFillMode: unknown fill mode";
  
  public static final String BAD_pendiam = "setPenDiameter: Invalid pen diameter";
  
  public static final String BAD_pent4 = "setPenT4: invalid pen transformation";
  
  public static final String BAD_pent4_singular = "setPenT4: invalid pen transformation (singular)";
  
  public static final String BAD_penfit = "setPenFitting: invalid pen fitting specification";
  
  public static final String UNK_caps = "setCaps: unknown cap type";
  
  public static final String UNK_corners = "setCorners: unknown corner type";
  
  public static final String BAD_miterlimit = "setCorners: invalid miter limit";
  
  public static final String BAD_dashpattern = "setDash: invalid dash pattern";
  
  public static final String BAD_dasht4 = "setDashT4: invalid dash transformation";
  
  public static final String BAD_dasht4_singular = "setDashT4: invalid dash transformation (singular)";
  
  public static final String BAD_pathbox = "beginPath: invalid path box";
  
  public static final String BAD_outputt6 = "setOutputT6: invalid output transformation";
  
  public static final String BAD_outputt6_singular = "setOutputT6: invalid output transformation (singular)";
  
  public static final String BAD_boxdest = "getAlphaBox: invalid box destination array";
  
  public static final String BAD_outputarea = "setOutputArea: invalid output area";
  
  public static final String BAD_alphadest = "writeAlpha: invalid alpha destination array and/or strides";
  
  public static final String DUMMY = "";
  
  public PRError() {}
  
  public PRError(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\dc\pr\PRError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */