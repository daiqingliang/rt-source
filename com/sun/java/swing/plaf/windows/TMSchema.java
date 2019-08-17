package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.util.EnumMap;
import javax.swing.JComponent;
import sun.awt.windows.ThemeReader;

class TMSchema {
  public enum Control {
    BUTTON, COMBOBOX, EDIT, HEADER, LISTBOX, LISTVIEW, MENU, PROGRESS, REBAR, SCROLLBAR, SPIN, TAB, TOOLBAR, TRACKBAR, TREEVIEW, WINDOW;
  }
  
  public enum Part {
    MENU(TMSchema.Control.MENU, 0),
    MP_BARBACKGROUND(TMSchema.Control.MENU, 7),
    MP_BARITEM(TMSchema.Control.MENU, 8),
    MP_POPUPBACKGROUND(TMSchema.Control.MENU, 9),
    MP_POPUPBORDERS(TMSchema.Control.MENU, 10),
    MP_POPUPCHECK(TMSchema.Control.MENU, 11),
    MP_POPUPCHECKBACKGROUND(TMSchema.Control.MENU, 12),
    MP_POPUPGUTTER(TMSchema.Control.MENU, 13),
    MP_POPUPITEM(TMSchema.Control.MENU, 14),
    MP_POPUPSEPARATOR(TMSchema.Control.MENU, 15),
    MP_POPUPSUBMENU(TMSchema.Control.MENU, 16),
    BP_PUSHBUTTON(TMSchema.Control.BUTTON, 1),
    BP_RADIOBUTTON(TMSchema.Control.BUTTON, 2),
    BP_CHECKBOX(TMSchema.Control.BUTTON, 3),
    BP_GROUPBOX(TMSchema.Control.BUTTON, 4),
    CP_COMBOBOX(TMSchema.Control.COMBOBOX, 0),
    CP_DROPDOWNBUTTON(TMSchema.Control.COMBOBOX, 1),
    CP_BACKGROUND(TMSchema.Control.COMBOBOX, 2),
    CP_TRANSPARENTBACKGROUND(TMSchema.Control.COMBOBOX, 3),
    CP_BORDER(TMSchema.Control.COMBOBOX, 4),
    CP_READONLY(TMSchema.Control.COMBOBOX, 5),
    CP_DROPDOWNBUTTONRIGHT(TMSchema.Control.COMBOBOX, 6),
    CP_DROPDOWNBUTTONLEFT(TMSchema.Control.COMBOBOX, 7),
    CP_CUEBANNER(TMSchema.Control.COMBOBOX, 8),
    EP_EDIT(TMSchema.Control.EDIT, 0),
    EP_EDITTEXT(TMSchema.Control.EDIT, 1),
    HP_HEADERITEM(TMSchema.Control.HEADER, 1),
    HP_HEADERSORTARROW(TMSchema.Control.HEADER, 4),
    LBP_LISTBOX(TMSchema.Control.LISTBOX, 0),
    LVP_LISTVIEW(TMSchema.Control.LISTVIEW, 0),
    PP_PROGRESS(TMSchema.Control.PROGRESS, 0),
    PP_BAR(TMSchema.Control.PROGRESS, 1),
    PP_BARVERT(TMSchema.Control.PROGRESS, 2),
    PP_CHUNK(TMSchema.Control.PROGRESS, 3),
    PP_CHUNKVERT(TMSchema.Control.PROGRESS, 4),
    RP_GRIPPER(TMSchema.Control.REBAR, 1),
    RP_GRIPPERVERT(TMSchema.Control.REBAR, 2),
    SBP_SCROLLBAR(TMSchema.Control.SCROLLBAR, 0),
    SBP_ARROWBTN(TMSchema.Control.SCROLLBAR, 1),
    SBP_THUMBBTNHORZ(TMSchema.Control.SCROLLBAR, 2),
    SBP_THUMBBTNVERT(TMSchema.Control.SCROLLBAR, 3),
    SBP_LOWERTRACKHORZ(TMSchema.Control.SCROLLBAR, 4),
    SBP_UPPERTRACKHORZ(TMSchema.Control.SCROLLBAR, 5),
    SBP_LOWERTRACKVERT(TMSchema.Control.SCROLLBAR, 6),
    SBP_UPPERTRACKVERT(TMSchema.Control.SCROLLBAR, 7),
    SBP_GRIPPERHORZ(TMSchema.Control.SCROLLBAR, 8),
    SBP_GRIPPERVERT(TMSchema.Control.SCROLLBAR, 9),
    SBP_SIZEBOX(TMSchema.Control.SCROLLBAR, 10),
    SPNP_UP(TMSchema.Control.SPIN, 1),
    SPNP_DOWN(TMSchema.Control.SPIN, 2),
    TABP_TABITEM(TMSchema.Control.TAB, 1),
    TABP_TABITEMLEFTEDGE(TMSchema.Control.TAB, 2),
    TABP_TABITEMRIGHTEDGE(TMSchema.Control.TAB, 3),
    TABP_PANE(TMSchema.Control.TAB, 9),
    TP_TOOLBAR(TMSchema.Control.TOOLBAR, 0),
    TP_BUTTON(TMSchema.Control.TOOLBAR, 1),
    TP_SEPARATOR(TMSchema.Control.TOOLBAR, 5),
    TP_SEPARATORVERT(TMSchema.Control.TOOLBAR, 6),
    TKP_TRACK(TMSchema.Control.TRACKBAR, 1),
    TKP_TRACKVERT(TMSchema.Control.TRACKBAR, 2),
    TKP_THUMB(TMSchema.Control.TRACKBAR, 3),
    TKP_THUMBBOTTOM(TMSchema.Control.TRACKBAR, 4),
    TKP_THUMBTOP(TMSchema.Control.TRACKBAR, 5),
    TKP_THUMBVERT(TMSchema.Control.TRACKBAR, 6),
    TKP_THUMBLEFT(TMSchema.Control.TRACKBAR, 7),
    TKP_THUMBRIGHT(TMSchema.Control.TRACKBAR, 8),
    TKP_TICS(TMSchema.Control.TRACKBAR, 9),
    TKP_TICSVERT(TMSchema.Control.TRACKBAR, 10),
    TVP_TREEVIEW(TMSchema.Control.TREEVIEW, 0),
    TVP_GLYPH(TMSchema.Control.TREEVIEW, 2),
    WP_WINDOW(TMSchema.Control.WINDOW, 0),
    WP_CAPTION(TMSchema.Control.WINDOW, 1),
    WP_MINCAPTION(TMSchema.Control.WINDOW, 3),
    WP_MAXCAPTION(TMSchema.Control.WINDOW, 5),
    WP_FRAMELEFT(TMSchema.Control.WINDOW, 7),
    WP_FRAMERIGHT(TMSchema.Control.WINDOW, 8),
    WP_FRAMEBOTTOM(TMSchema.Control.WINDOW, 9),
    WP_SYSBUTTON(TMSchema.Control.WINDOW, 13),
    WP_MDISYSBUTTON(TMSchema.Control.WINDOW, 14),
    WP_MINBUTTON(TMSchema.Control.WINDOW, 15),
    WP_MDIMINBUTTON(TMSchema.Control.WINDOW, 16),
    WP_MAXBUTTON(TMSchema.Control.WINDOW, 17),
    WP_CLOSEBUTTON(TMSchema.Control.WINDOW, 18),
    WP_MDICLOSEBUTTON(TMSchema.Control.WINDOW, 20),
    WP_RESTOREBUTTON(TMSchema.Control.WINDOW, 21),
    WP_MDIRESTOREBUTTON(TMSchema.Control.WINDOW, 22);
    
    private final TMSchema.Control control;
    
    private final int value;
    
    Part(int param1Int1, int param1Int2) {
      this.control = param1Int1;
      this.value = param1Int2;
    }
    
    public int getValue() { return this.value; }
    
    public String getControlName(Component param1Component) {
      String str = "";
      if (param1Component instanceof JComponent) {
        JComponent jComponent = (JComponent)param1Component;
        String str1 = (String)jComponent.getClientProperty("XPStyle.subAppName");
        if (str1 != null)
          str = str1 + "::"; 
      } 
      return str + this.control.toString();
    }
    
    public String toString() { return this.control.toString() + "." + name(); }
  }
  
  public enum Prop {
    COLOR(java.awt.Color.class, 204),
    SIZE(java.awt.Dimension.class, 207),
    FLATMENUS(Boolean.class, 1001),
    BORDERONLY(Boolean.class, 2203),
    IMAGECOUNT(Integer.class, 2401),
    BORDERSIZE(Integer.class, 2403),
    PROGRESSCHUNKSIZE(Integer.class, 2411),
    PROGRESSSPACESIZE(Integer.class, 2412),
    TEXTSHADOWOFFSET(java.awt.Point.class, 3402),
    NORMALSIZE(java.awt.Dimension.class, 3409),
    SIZINGMARGINS(java.awt.Insets.class, 3601),
    CONTENTMARGINS(java.awt.Insets.class, 3602),
    CAPTIONMARGINS(java.awt.Insets.class, 3603),
    BORDERCOLOR(java.awt.Color.class, 3801),
    FILLCOLOR(java.awt.Color.class, 3802),
    TEXTCOLOR(java.awt.Color.class, 3803),
    TEXTSHADOWCOLOR(java.awt.Color.class, 3818),
    BGTYPE(Integer.class, 4001),
    TEXTSHADOWTYPE(Integer.class, 4010),
    TRANSITIONDURATIONS(Integer.class, 6000);
    
    private final Class type;
    
    private final int value;
    
    Prop(int param1Int1, int param1Int2) {
      this.type = param1Int1;
      this.value = param1Int2;
    }
    
    public int getValue() { return this.value; }
    
    public String toString() { return name() + "[" + this.type.getName() + "] = " + this.value; }
  }
  
  public enum State {
    ACTIVE, ASSIST, BITMAP, CHECKED, CHECKEDDISABLED, CHECKEDHOT, CHECKEDNORMAL, CHECKEDPRESSED, CHECKMARKNORMAL, CHECKMARKDISABLED, BULLETNORMAL, BULLETDISABLED, CLOSED, DEFAULTED, DISABLED, DISABLEDHOT, DISABLEDPUSHED, DOWNDISABLED, DOWNHOT, DOWNNORMAL, DOWNPRESSED, FOCUSED, HOT, HOTCHECKED, ICONHOT, ICONNORMAL, ICONPRESSED, ICONSORTEDHOT, ICONSORTEDNORMAL, ICONSORTEDPRESSED, INACTIVE, INACTIVENORMAL, INACTIVEHOT, INACTIVEPUSHED, INACTIVEDISABLED, LEFTDISABLED, LEFTHOT, LEFTNORMAL, LEFTPRESSED, MIXEDDISABLED, MIXEDHOT, MIXEDNORMAL, MIXEDPRESSED, NORMAL, PRESSED, OPENED, PUSHED, READONLY, RIGHTDISABLED, RIGHTHOT, RIGHTNORMAL, RIGHTPRESSED, SELECTED, UNCHECKEDDISABLED, UNCHECKEDHOT, UNCHECKEDNORMAL, UNCHECKEDPRESSED, UPDISABLED, UPHOT, UPNORMAL, UPPRESSED, HOVER, UPHOVER, DOWNHOVER, LEFTHOVER, RIGHTHOVER, SORTEDDOWN, SORTEDHOT, SORTEDNORMAL, SORTEDPRESSED, SORTEDUP;
    
    private static EnumMap<TMSchema.Part, State[]> stateMap;
    
    private static void initStates() {
      stateMap = new EnumMap(TMSchema.Part.class);
      stateMap.put(TMSchema.Part.EP_EDITTEXT, new State[] { NORMAL, HOT, SELECTED, DISABLED, FOCUSED, READONLY, ASSIST });
      stateMap.put(TMSchema.Part.BP_PUSHBUTTON, new State[] { NORMAL, HOT, PRESSED, DISABLED, DEFAULTED });
      stateMap.put(TMSchema.Part.BP_RADIOBUTTON, new State[] { UNCHECKEDNORMAL, UNCHECKEDHOT, UNCHECKEDPRESSED, UNCHECKEDDISABLED, CHECKEDNORMAL, CHECKEDHOT, CHECKEDPRESSED, CHECKEDDISABLED });
      stateMap.put(TMSchema.Part.BP_CHECKBOX, new State[] { 
            UNCHECKEDNORMAL, UNCHECKEDHOT, UNCHECKEDPRESSED, UNCHECKEDDISABLED, CHECKEDNORMAL, CHECKEDHOT, CHECKEDPRESSED, CHECKEDDISABLED, MIXEDNORMAL, MIXEDHOT, 
            MIXEDPRESSED, MIXEDDISABLED });
      State[] arrayOfState1 = { NORMAL, HOT, PRESSED, DISABLED };
      stateMap.put(TMSchema.Part.CP_COMBOBOX, arrayOfState1);
      stateMap.put(TMSchema.Part.CP_DROPDOWNBUTTON, arrayOfState1);
      stateMap.put(TMSchema.Part.CP_BACKGROUND, arrayOfState1);
      stateMap.put(TMSchema.Part.CP_TRANSPARENTBACKGROUND, arrayOfState1);
      stateMap.put(TMSchema.Part.CP_BORDER, arrayOfState1);
      stateMap.put(TMSchema.Part.CP_READONLY, arrayOfState1);
      stateMap.put(TMSchema.Part.CP_DROPDOWNBUTTONRIGHT, arrayOfState1);
      stateMap.put(TMSchema.Part.CP_DROPDOWNBUTTONLEFT, arrayOfState1);
      stateMap.put(TMSchema.Part.CP_CUEBANNER, arrayOfState1);
      stateMap.put(TMSchema.Part.HP_HEADERITEM, new State[] { 
            NORMAL, HOT, PRESSED, SORTEDNORMAL, SORTEDHOT, SORTEDPRESSED, ICONNORMAL, ICONHOT, ICONPRESSED, ICONSORTEDNORMAL, 
            ICONSORTEDHOT, ICONSORTEDPRESSED });
      stateMap.put(TMSchema.Part.HP_HEADERSORTARROW, new State[] { SORTEDDOWN, SORTEDUP });
      State[] arrayOfState2 = { NORMAL, HOT, PRESSED, DISABLED, HOVER };
      stateMap.put(TMSchema.Part.SBP_SCROLLBAR, arrayOfState2);
      stateMap.put(TMSchema.Part.SBP_THUMBBTNVERT, arrayOfState2);
      stateMap.put(TMSchema.Part.SBP_THUMBBTNHORZ, arrayOfState2);
      stateMap.put(TMSchema.Part.SBP_GRIPPERVERT, arrayOfState2);
      stateMap.put(TMSchema.Part.SBP_GRIPPERHORZ, arrayOfState2);
      stateMap.put(TMSchema.Part.SBP_ARROWBTN, new State[] { 
            UPNORMAL, UPHOT, UPPRESSED, UPDISABLED, DOWNNORMAL, DOWNHOT, DOWNPRESSED, DOWNDISABLED, LEFTNORMAL, LEFTHOT, 
            LEFTPRESSED, LEFTDISABLED, RIGHTNORMAL, RIGHTHOT, RIGHTPRESSED, RIGHTDISABLED, UPHOVER, DOWNHOVER, LEFTHOVER, RIGHTHOVER });
      State[] arrayOfState3 = { NORMAL, HOT, PRESSED, DISABLED };
      stateMap.put(TMSchema.Part.SPNP_UP, arrayOfState3);
      stateMap.put(TMSchema.Part.SPNP_DOWN, arrayOfState3);
      stateMap.put(TMSchema.Part.TVP_GLYPH, new State[] { CLOSED, OPENED });
      State[] arrayOfState4 = { NORMAL, HOT, PUSHED, DISABLED, INACTIVENORMAL, INACTIVEHOT, INACTIVEPUSHED, INACTIVEDISABLED };
      if (ThemeReader.getInt(TMSchema.Control.WINDOW.toString(), TMSchema.Part.WP_CLOSEBUTTON.getValue(), 1, TMSchema.Prop.IMAGECOUNT.getValue()) == 10)
        arrayOfState4 = new State[] { NORMAL, HOT, PUSHED, DISABLED, null, INACTIVENORMAL, INACTIVEHOT, INACTIVEPUSHED, INACTIVEDISABLED, null }; 
      stateMap.put(TMSchema.Part.WP_MINBUTTON, arrayOfState4);
      stateMap.put(TMSchema.Part.WP_MAXBUTTON, arrayOfState4);
      stateMap.put(TMSchema.Part.WP_RESTOREBUTTON, arrayOfState4);
      stateMap.put(TMSchema.Part.WP_CLOSEBUTTON, arrayOfState4);
      stateMap.put(TMSchema.Part.TKP_TRACK, new State[] { NORMAL });
      stateMap.put(TMSchema.Part.TKP_TRACKVERT, new State[] { NORMAL });
      State[] arrayOfState5 = { NORMAL, HOT, PRESSED, FOCUSED, DISABLED };
      stateMap.put(TMSchema.Part.TKP_THUMB, arrayOfState5);
      stateMap.put(TMSchema.Part.TKP_THUMBBOTTOM, arrayOfState5);
      stateMap.put(TMSchema.Part.TKP_THUMBTOP, arrayOfState5);
      stateMap.put(TMSchema.Part.TKP_THUMBVERT, arrayOfState5);
      stateMap.put(TMSchema.Part.TKP_THUMBRIGHT, arrayOfState5);
      State[] arrayOfState6 = { NORMAL, HOT, SELECTED, DISABLED, FOCUSED };
      stateMap.put(TMSchema.Part.TABP_TABITEM, arrayOfState6);
      stateMap.put(TMSchema.Part.TABP_TABITEMLEFTEDGE, arrayOfState6);
      stateMap.put(TMSchema.Part.TABP_TABITEMRIGHTEDGE, arrayOfState6);
      stateMap.put(TMSchema.Part.TP_BUTTON, new State[] { NORMAL, HOT, PRESSED, DISABLED, CHECKED, HOTCHECKED });
      State[] arrayOfState7 = { ACTIVE, INACTIVE };
      stateMap.put(TMSchema.Part.WP_WINDOW, arrayOfState7);
      stateMap.put(TMSchema.Part.WP_FRAMELEFT, arrayOfState7);
      stateMap.put(TMSchema.Part.WP_FRAMERIGHT, arrayOfState7);
      stateMap.put(TMSchema.Part.WP_FRAMEBOTTOM, arrayOfState7);
      State[] arrayOfState8 = { ACTIVE, INACTIVE, DISABLED };
      stateMap.put(TMSchema.Part.WP_CAPTION, arrayOfState8);
      stateMap.put(TMSchema.Part.WP_MINCAPTION, arrayOfState8);
      stateMap.put(TMSchema.Part.WP_MAXCAPTION, arrayOfState8);
      stateMap.put(TMSchema.Part.MP_BARBACKGROUND, new State[] { ACTIVE, INACTIVE });
      stateMap.put(TMSchema.Part.MP_BARITEM, new State[] { NORMAL, HOT, PUSHED, DISABLED, DISABLEDHOT, DISABLEDPUSHED });
      stateMap.put(TMSchema.Part.MP_POPUPCHECK, new State[] { CHECKMARKNORMAL, CHECKMARKDISABLED, BULLETNORMAL, BULLETDISABLED });
      stateMap.put(TMSchema.Part.MP_POPUPCHECKBACKGROUND, new State[] { DISABLEDPUSHED, NORMAL, BITMAP });
      stateMap.put(TMSchema.Part.MP_POPUPITEM, new State[] { NORMAL, HOT, DISABLED, DISABLEDHOT });
      stateMap.put(TMSchema.Part.MP_POPUPSUBMENU, new State[] { NORMAL, DISABLED });
    }
    
    public static int getValue(TMSchema.Part param1Part, State param1State) {
      if (stateMap == null)
        initStates(); 
      Enum[] arrayOfEnum = (Enum[])stateMap.get(param1Part);
      if (arrayOfEnum != null)
        for (byte b = 0; b < arrayOfEnum.length; b++) {
          if (param1State == arrayOfEnum[b])
            return b + true; 
        }  
      return (param1State == null || param1State == NORMAL) ? 1 : 0;
    }
  }
  
  public enum TypeEnum {
    BT_IMAGEFILE(TMSchema.Prop.BGTYPE, "imagefile", 0),
    BT_BORDERFILL(TMSchema.Prop.BGTYPE, "borderfill", 1),
    TST_NONE(TMSchema.Prop.TEXTSHADOWTYPE, "none", 0),
    TST_SINGLE(TMSchema.Prop.TEXTSHADOWTYPE, "single", 1),
    TST_CONTINUOUS(TMSchema.Prop.TEXTSHADOWTYPE, "continuous", 2);
    
    private final TMSchema.Prop prop;
    
    private final String enumName;
    
    private final int value;
    
    TypeEnum(String param1String1, int param1Int1, int param1Int2) {
      this.prop = param1String1;
      this.enumName = param1Int1;
      this.value = param1Int2;
    }
    
    public String toString() { return this.prop + "=" + this.enumName + "=" + this.value; }
    
    String getName() { return this.enumName; }
    
    static TypeEnum getTypeEnum(TMSchema.Prop param1Prop, int param1Int) {
      for (TypeEnum typeEnum : values()) {
        if (typeEnum.prop == param1Prop && typeEnum.value == param1Int)
          return typeEnum; 
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\TMSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */