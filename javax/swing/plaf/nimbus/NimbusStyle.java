package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JComponent;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public final class NimbusStyle extends SynthStyle {
  public static final String LARGE_KEY = "large";
  
  public static final String SMALL_KEY = "small";
  
  public static final String MINI_KEY = "mini";
  
  public static final double LARGE_SCALE = 1.15D;
  
  public static final double SMALL_SCALE = 0.857D;
  
  public static final double MINI_SCALE = 0.714D;
  
  private static final Object NULL = Character.valueOf(false);
  
  private static final Color DEFAULT_COLOR = new ColorUIResource(Color.BLACK);
  
  private static final Comparator<RuntimeState> STATE_COMPARATOR = new Comparator<RuntimeState>() {
      public int compare(NimbusStyle.RuntimeState param1RuntimeState1, NimbusStyle.RuntimeState param1RuntimeState2) { return param1RuntimeState1.state - param1RuntimeState2.state; }
    };
  
  private String prefix;
  
  private SynthPainter painter;
  
  private Values values;
  
  private CacheKey tmpKey = new CacheKey("", 0);
  
  private WeakReference<JComponent> component;
  
  NimbusStyle(String paramString, JComponent paramJComponent) {
    if (paramJComponent != null)
      this.component = new WeakReference(paramJComponent); 
    this.prefix = paramString;
    this.painter = new SynthPainterImpl(this);
  }
  
  public void installDefaults(SynthContext paramSynthContext) {
    validate();
    super.installDefaults(paramSynthContext);
  }
  
  private void validate() {
    if (this.values != null)
      return; 
    this.values = new Values(null);
    Map map = ((NimbusLookAndFeel)UIManager.getLookAndFeel()).getDefaultsForPrefix(this.prefix);
    if (this.component != null) {
      Object object = ((JComponent)this.component.get()).getClientProperty("Nimbus.Overrides");
      if (object instanceof UIDefaults) {
        Object object1 = ((JComponent)this.component.get()).getClientProperty("Nimbus.Overrides.InheritDefaults");
        boolean bool = (object1 instanceof Boolean) ? ((Boolean)object1).booleanValue() : 1;
        UIDefaults uIDefaults = (UIDefaults)object;
        TreeMap treeMap = new TreeMap();
        for (Object object2 : uIDefaults.keySet()) {
          if (object2 instanceof String) {
            String str1 = (String)object2;
            if (str1.startsWith(this.prefix))
              treeMap.put(str1, uIDefaults.get(str1)); 
          } 
        } 
        if (bool) {
          map.putAll(treeMap);
        } else {
          map = treeMap;
        } 
      } 
    } 
    ArrayList arrayList1 = new ArrayList();
    HashMap hashMap = new HashMap();
    ArrayList arrayList2 = new ArrayList();
    String str = (String)map.get(this.prefix + ".States");
    if (str != null) {
      String[] arrayOfString = str.split(",");
      byte b;
      for (b = 0; b < arrayOfString.length; b++) {
        arrayOfString[b] = arrayOfString[b].trim();
        if (!State.isStandardStateName(arrayOfString[b])) {
          String str1 = this.prefix + "." + arrayOfString[b];
          State state = (State)map.get(str1);
          if (state != null)
            arrayList1.add(state); 
        } else {
          arrayList1.add(State.getStandardState(arrayOfString[b]));
        } 
      } 
      if (arrayList1.size() > 0)
        this.values.stateTypes = (State[])arrayList1.toArray(new State[arrayList1.size()]); 
      b = 1;
      for (State state : arrayList1) {
        hashMap.put(state.getName(), Integer.valueOf(b));
        b <<= 1;
      } 
    } else {
      arrayList1.add(State.Enabled);
      arrayList1.add(State.MouseOver);
      arrayList1.add(State.Pressed);
      arrayList1.add(State.Disabled);
      arrayList1.add(State.Focused);
      arrayList1.add(State.Selected);
      arrayList1.add(State.Default);
      hashMap.put("Enabled", Integer.valueOf(1));
      hashMap.put("MouseOver", Integer.valueOf(2));
      hashMap.put("Pressed", Integer.valueOf(4));
      hashMap.put("Disabled", Integer.valueOf(8));
      hashMap.put("Focused", Integer.valueOf(256));
      hashMap.put("Selected", Integer.valueOf(512));
      hashMap.put("Default", Integer.valueOf(1024));
    } 
    for (String str1 : map.keySet()) {
      String str2 = str1.substring(this.prefix.length());
      if (str2.indexOf('"') != -1 || str2.indexOf(':') != -1)
        continue; 
      str2 = str2.substring(1);
      String str3 = null;
      String str4 = null;
      int i = str2.indexOf(']');
      if (i < 0) {
        str4 = str2;
      } else {
        str3 = str2.substring(0, i);
        str4 = str2.substring(i + 2);
      } 
      if (str3 == null) {
        if ("contentMargins".equals(str4)) {
          this.values.contentMargins = (Insets)map.get(str1);
          continue;
        } 
        if ("States".equals(str4))
          continue; 
        this.values.defaults.put(str4, map.get(str1));
        continue;
      } 
      boolean bool = false;
      int j = 0;
      String[] arrayOfString = str3.split("\\+");
      for (String str5 : arrayOfString) {
        if (hashMap.containsKey(str5)) {
          j |= ((Integer)hashMap.get(str5)).intValue();
        } else {
          bool = true;
          break;
        } 
      } 
      if (bool)
        continue; 
      RuntimeState runtimeState = null;
      for (RuntimeState runtimeState1 : arrayList2) {
        if (runtimeState1.state == j) {
          runtimeState = runtimeState1;
          break;
        } 
      } 
      if (runtimeState == null) {
        runtimeState = new RuntimeState(j, str3, null);
        arrayList2.add(runtimeState);
      } 
      if ("backgroundPainter".equals(str4)) {
        runtimeState.backgroundPainter = getPainter(map, str1);
        continue;
      } 
      if ("foregroundPainter".equals(str4)) {
        runtimeState.foregroundPainter = getPainter(map, str1);
        continue;
      } 
      if ("borderPainter".equals(str4)) {
        runtimeState.borderPainter = getPainter(map, str1);
        continue;
      } 
      runtimeState.defaults.put(str4, map.get(str1));
    } 
    Collections.sort(arrayList2, STATE_COMPARATOR);
    this.values.states = (RuntimeState[])arrayList2.toArray(new RuntimeState[arrayList2.size()]);
  }
  
  private Painter getPainter(Map<String, Object> paramMap, String paramString) {
    Object object = paramMap.get(paramString);
    if (object instanceof UIDefaults.LazyValue)
      object = ((UIDefaults.LazyValue)object).createValue(UIManager.getDefaults()); 
    return (object instanceof Painter) ? (Painter)object : null;
  }
  
  public Insets getInsets(SynthContext paramSynthContext, Insets paramInsets) {
    if (paramInsets == null)
      paramInsets = new Insets(0, 0, 0, 0); 
    Values values1 = getValues(paramSynthContext);
    if (values1.contentMargins == null) {
      paramInsets.bottom = paramInsets.top = paramInsets.left = paramInsets.right = 0;
      return paramInsets;
    } 
    paramInsets.bottom = values1.contentMargins.bottom;
    paramInsets.top = values1.contentMargins.top;
    paramInsets.left = values1.contentMargins.left;
    paramInsets.right = values1.contentMargins.right;
    String str = (String)paramSynthContext.getComponent().getClientProperty("JComponent.sizeVariant");
    if (str != null)
      if ("large".equals(str)) {
        paramInsets.bottom = (int)(paramInsets.bottom * 1.15D);
        paramInsets.top = (int)(paramInsets.top * 1.15D);
        paramInsets.left = (int)(paramInsets.left * 1.15D);
        paramInsets.right = (int)(paramInsets.right * 1.15D);
      } else if ("small".equals(str)) {
        paramInsets.bottom = (int)(paramInsets.bottom * 0.857D);
        paramInsets.top = (int)(paramInsets.top * 0.857D);
        paramInsets.left = (int)(paramInsets.left * 0.857D);
        paramInsets.right = (int)(paramInsets.right * 0.857D);
      } else if ("mini".equals(str)) {
        paramInsets.bottom = (int)(paramInsets.bottom * 0.714D);
        paramInsets.top = (int)(paramInsets.top * 0.714D);
        paramInsets.left = (int)(paramInsets.left * 0.714D);
        paramInsets.right = (int)(paramInsets.right * 0.714D);
      }  
    return paramInsets;
  }
  
  protected Color getColorForState(SynthContext paramSynthContext, ColorType paramColorType) {
    String str = null;
    if (paramColorType == ColorType.BACKGROUND) {
      str = "background";
    } else if (paramColorType == ColorType.FOREGROUND) {
      str = "textForeground";
    } else if (paramColorType == ColorType.TEXT_BACKGROUND) {
      str = "textBackground";
    } else if (paramColorType == ColorType.TEXT_FOREGROUND) {
      str = "textForeground";
    } else if (paramColorType == ColorType.FOCUS) {
      str = "focus";
    } else if (paramColorType != null) {
      str = paramColorType.toString();
    } else {
      return DEFAULT_COLOR;
    } 
    Color color = (Color)get(paramSynthContext, str);
    if (color == null)
      color = DEFAULT_COLOR; 
    return color;
  }
  
  protected Font getFontForState(SynthContext paramSynthContext) {
    Font font = (Font)get(paramSynthContext, "font");
    if (font == null)
      font = UIManager.getFont("defaultFont"); 
    String str = (String)paramSynthContext.getComponent().getClientProperty("JComponent.sizeVariant");
    if (str != null)
      if ("large".equals(str)) {
        font = font.deriveFont((float)Math.round(font.getSize2D() * 1.15D));
      } else if ("small".equals(str)) {
        font = font.deriveFont((float)Math.round(font.getSize2D() * 0.857D));
      } else if ("mini".equals(str)) {
        font = font.deriveFont((float)Math.round(font.getSize2D() * 0.714D));
      }  
    return font;
  }
  
  public SynthPainter getPainter(SynthContext paramSynthContext) { return this.painter; }
  
  public boolean isOpaque(SynthContext paramSynthContext) {
    if ("Table.cellRenderer".equals(paramSynthContext.getComponent().getName()))
      return true; 
    Boolean bool = (Boolean)get(paramSynthContext, "opaque");
    return (bool == null) ? false : bool.booleanValue();
  }
  
  public Object get(SynthContext paramSynthContext, Object paramObject) {
    Values values1 = getValues(paramSynthContext);
    String str1 = paramObject.toString();
    String str2 = str1.substring(str1.indexOf(".") + 1);
    Object object = null;
    int i = getExtendedState(paramSynthContext, values1);
    this.tmpKey.init(str2, i);
    object = values1.cache.get(this.tmpKey);
    boolean bool = (object != null) ? 1 : 0;
    if (!bool) {
      RuntimeState runtimeState = null;
      int[] arrayOfInt = { -1 };
      while (object == null && (runtimeState = getNextState(values1.states, arrayOfInt, i)) != null)
        object = runtimeState.defaults.get(str2); 
      if (object == null && values1.defaults != null)
        object = values1.defaults.get(str2); 
      if (object == null)
        object = UIManager.get(str1); 
      if (object == null && str2.equals("focusInputMap"))
        object = super.get(paramSynthContext, str1); 
      values1.cache.put(new CacheKey(str2, i), (object == null) ? NULL : object);
    } 
    return (object == NULL) ? null : object;
  }
  
  public Painter getBackgroundPainter(SynthContext paramSynthContext) {
    Values values1 = getValues(paramSynthContext);
    int i = getExtendedState(paramSynthContext, values1);
    Painter painter1 = null;
    this.tmpKey.init("backgroundPainter$$instance", i);
    painter1 = (Painter)values1.cache.get(this.tmpKey);
    if (painter1 != null)
      return painter1; 
    RuntimeState runtimeState = null;
    int[] arrayOfInt = { -1 };
    while ((runtimeState = getNextState(values1.states, arrayOfInt, i)) != null) {
      if (runtimeState.backgroundPainter != null) {
        painter1 = runtimeState.backgroundPainter;
        break;
      } 
    } 
    if (painter1 == null)
      painter1 = (Painter)get(paramSynthContext, "backgroundPainter"); 
    if (painter1 != null)
      values1.cache.put(new CacheKey("backgroundPainter$$instance", i), painter1); 
    return painter1;
  }
  
  public Painter getForegroundPainter(SynthContext paramSynthContext) {
    Values values1 = getValues(paramSynthContext);
    int i = getExtendedState(paramSynthContext, values1);
    Painter painter1 = null;
    this.tmpKey.init("foregroundPainter$$instance", i);
    painter1 = (Painter)values1.cache.get(this.tmpKey);
    if (painter1 != null)
      return painter1; 
    RuntimeState runtimeState = null;
    int[] arrayOfInt = { -1 };
    while ((runtimeState = getNextState(values1.states, arrayOfInt, i)) != null) {
      if (runtimeState.foregroundPainter != null) {
        painter1 = runtimeState.foregroundPainter;
        break;
      } 
    } 
    if (painter1 == null)
      painter1 = (Painter)get(paramSynthContext, "foregroundPainter"); 
    if (painter1 != null)
      values1.cache.put(new CacheKey("foregroundPainter$$instance", i), painter1); 
    return painter1;
  }
  
  public Painter getBorderPainter(SynthContext paramSynthContext) {
    Values values1 = getValues(paramSynthContext);
    int i = getExtendedState(paramSynthContext, values1);
    Painter painter1 = null;
    this.tmpKey.init("borderPainter$$instance", i);
    painter1 = (Painter)values1.cache.get(this.tmpKey);
    if (painter1 != null)
      return painter1; 
    RuntimeState runtimeState = null;
    int[] arrayOfInt = { -1 };
    while ((runtimeState = getNextState(values1.states, arrayOfInt, i)) != null) {
      if (runtimeState.borderPainter != null) {
        painter1 = runtimeState.borderPainter;
        break;
      } 
    } 
    if (painter1 == null)
      painter1 = (Painter)get(paramSynthContext, "borderPainter"); 
    if (painter1 != null)
      values1.cache.put(new CacheKey("borderPainter$$instance", i), painter1); 
    return painter1;
  }
  
  private Values getValues(SynthContext paramSynthContext) {
    validate();
    return this.values;
  }
  
  private boolean contains(String[] paramArrayOfString, String paramString) {
    assert paramString != null;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramString.equals(paramArrayOfString[b]))
        return true; 
    } 
    return false;
  }
  
  private int getExtendedState(SynthContext paramSynthContext, Values paramValues) {
    JComponent jComponent = paramSynthContext.getComponent();
    int i = 0;
    int j = 1;
    Object object = jComponent.getClientProperty("Nimbus.State");
    if (object != null) {
      String str = object.toString();
      String[] arrayOfString = str.split("\\+");
      if (paramValues.stateTypes == null) {
        for (String str1 : arrayOfString) {
          State.StandardState standardState = State.getStandardState(str1);
          if (standardState != null)
            i |= standardState.getState(); 
        } 
      } else {
        for (State state : paramValues.stateTypes) {
          if (contains(arrayOfString, state.getName()))
            i |= j; 
          j <<= 1;
        } 
      } 
    } else {
      if (paramValues.stateTypes == null)
        return paramSynthContext.getComponentState(); 
      int k = paramSynthContext.getComponentState();
      for (State state : paramValues.stateTypes) {
        if (state.isInState(jComponent, k))
          i |= j; 
        j <<= 1;
      } 
    } 
    return i;
  }
  
  private RuntimeState getNextState(RuntimeState[] paramArrayOfRuntimeState, int[] paramArrayOfInt, int paramInt) {
    if (paramArrayOfRuntimeState != null && paramArrayOfRuntimeState.length > 0) {
      int i = 0;
      int j = -1;
      int k = -1;
      if (paramInt == 0) {
        for (int i1 = paramArrayOfRuntimeState.length - 1; i1 >= 0; i1--) {
          if ((paramArrayOfRuntimeState[i1]).state == 0) {
            paramArrayOfInt[0] = i1;
            return paramArrayOfRuntimeState[i1];
          } 
        } 
        paramArrayOfInt[0] = -1;
        return null;
      } 
      int m = (paramArrayOfInt == null || paramArrayOfInt[0] == -1) ? paramArrayOfRuntimeState.length : paramArrayOfInt[0];
      for (int n = m - 1; n >= 0; n--) {
        int i1 = (paramArrayOfRuntimeState[n]).state;
        if (i1 == 0) {
          if (k == -1)
            k = n; 
        } else if ((paramInt & i1) == i1) {
          int i2 = i1;
          i2 -= ((0xAAAAAAAA & i2) >>> 1);
          i2 = (i2 & 0x33333333) + (i2 >>> 2 & 0x33333333);
          i2 = i2 + (i2 >>> 4) & 0xF0F0F0F;
          i2 += (i2 >>> 8);
          i2 += (i2 >>> 16);
          i2 &= 0xFF;
          if (i2 > i) {
            j = n;
            i = i2;
          } 
        } 
      } 
      if (j != -1) {
        paramArrayOfInt[0] = j;
        return paramArrayOfRuntimeState[j];
      } 
      if (k != -1) {
        paramArrayOfInt[0] = k;
        return paramArrayOfRuntimeState[k];
      } 
    } 
    paramArrayOfInt[0] = -1;
    return null;
  }
  
  private static final class CacheKey {
    private String key;
    
    private int xstate;
    
    CacheKey(Object param1Object, int param1Int) { init(param1Object, param1Int); }
    
    void init(Object param1Object, int param1Int) {
      this.key = param1Object.toString();
      this.xstate = param1Int;
    }
    
    public boolean equals(Object param1Object) {
      CacheKey cacheKey = (CacheKey)param1Object;
      return (param1Object == null) ? false : ((this.xstate != cacheKey.xstate) ? false : (!!this.key.equals(cacheKey.key)));
    }
    
    public int hashCode() {
      null = 3;
      null = 29 * null + this.key.hashCode();
      return 29 * null + this.xstate;
    }
  }
  
  private final class RuntimeState implements Cloneable {
    int state;
    
    Painter backgroundPainter;
    
    Painter foregroundPainter;
    
    Painter borderPainter;
    
    String stateName;
    
    UIDefaults defaults = new UIDefaults(10, 0.7F);
    
    private RuntimeState(int param1Int, String param1String) {
      this.state = param1Int;
      this.stateName = param1String;
    }
    
    public String toString() { return this.stateName; }
    
    public RuntimeState clone() {
      RuntimeState runtimeState = new RuntimeState(NimbusStyle.this, this.state, this.stateName);
      runtimeState.backgroundPainter = this.backgroundPainter;
      runtimeState.foregroundPainter = this.foregroundPainter;
      runtimeState.borderPainter = this.borderPainter;
      runtimeState.defaults.putAll(this.defaults);
      return runtimeState;
    }
  }
  
  private static final class Values {
    State[] stateTypes = null;
    
    NimbusStyle.RuntimeState[] states = null;
    
    Insets contentMargins;
    
    UIDefaults defaults = new UIDefaults(10, 0.7F);
    
    Map<NimbusStyle.CacheKey, Object> cache = new HashMap();
    
    private Values() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\NimbusStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */