package java.awt.font;

public interface OpenType {
  public static final int TAG_CMAP = 1668112752;
  
  public static final int TAG_HEAD = 1751474532;
  
  public static final int TAG_NAME = 1851878757;
  
  public static final int TAG_GLYF = 1735162214;
  
  public static final int TAG_MAXP = 1835104368;
  
  public static final int TAG_PREP = 1886545264;
  
  public static final int TAG_HMTX = 1752003704;
  
  public static final int TAG_KERN = 1801810542;
  
  public static final int TAG_HDMX = 1751412088;
  
  public static final int TAG_LOCA = 1819239265;
  
  public static final int TAG_POST = 1886352244;
  
  public static final int TAG_OS2 = 1330851634;
  
  public static final int TAG_CVT = 1668707360;
  
  public static final int TAG_GASP = 1734439792;
  
  public static final int TAG_VDMX = 1447316824;
  
  public static final int TAG_VMTX = 1986884728;
  
  public static final int TAG_VHEA = 1986553185;
  
  public static final int TAG_HHEA = 1751672161;
  
  public static final int TAG_TYP1 = 1954115633;
  
  public static final int TAG_BSLN = 1651731566;
  
  public static final int TAG_GSUB = 1196643650;
  
  public static final int TAG_DSIG = 1146308935;
  
  public static final int TAG_FPGM = 1718642541;
  
  public static final int TAG_FVAR = 1719034226;
  
  public static final int TAG_GVAR = 1735811442;
  
  public static final int TAG_CFF = 1128678944;
  
  public static final int TAG_MMSD = 1296913220;
  
  public static final int TAG_MMFX = 1296909912;
  
  public static final int TAG_BASE = 1111577413;
  
  public static final int TAG_GDEF = 1195656518;
  
  public static final int TAG_GPOS = 1196445523;
  
  public static final int TAG_JSTF = 1246975046;
  
  public static final int TAG_EBDT = 1161970772;
  
  public static final int TAG_EBLC = 1161972803;
  
  public static final int TAG_EBSC = 1161974595;
  
  public static final int TAG_LTSH = 1280594760;
  
  public static final int TAG_PCLT = 1346587732;
  
  public static final int TAG_ACNT = 1633906292;
  
  public static final int TAG_AVAR = 1635148146;
  
  public static final int TAG_BDAT = 1650745716;
  
  public static final int TAG_BLOC = 1651273571;
  
  public static final int TAG_CVAR = 1668702578;
  
  public static final int TAG_FEAT = 1717920116;
  
  public static final int TAG_FDSC = 1717859171;
  
  public static final int TAG_FMTX = 1718449272;
  
  public static final int TAG_JUST = 1786082164;
  
  public static final int TAG_LCAR = 1818452338;
  
  public static final int TAG_MORT = 1836020340;
  
  public static final int TAG_OPBD = 1836020340;
  
  public static final int TAG_PROP = 1886547824;
  
  public static final int TAG_TRAK = 1953653099;
  
  int getVersion();
  
  byte[] getFontTable(int paramInt);
  
  byte[] getFontTable(String paramString);
  
  byte[] getFontTable(int paramInt1, int paramInt2, int paramInt3);
  
  byte[] getFontTable(String paramString, int paramInt1, int paramInt2);
  
  int getFontTableSize(int paramInt);
  
  int getFontTableSize(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\OpenType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */