package com.sun.java.swing.plaf.windows;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import sun.awt.AppContext;
import sun.security.action.GetBooleanAction;
import sun.swing.UIClientPropertyKey;

class AnimationController implements ActionListener, PropertyChangeListener {
  private static final boolean VISTA_ANIMATION_DISABLED = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("swing.disablevistaanimation"))).booleanValue();
  
  private static final Object ANIMATION_CONTROLLER_KEY = new StringBuilder("ANIMATION_CONTROLLER_KEY");
  
  private final Map<JComponent, Map<TMSchema.Part, AnimationState>> animationStateMap = new WeakHashMap();
  
  private final Timer timer = new Timer(33, this);
  
  private static AnimationController getAnimationController() {
    AppContext appContext = AppContext.getAppContext();
    Object object = appContext.get(ANIMATION_CONTROLLER_KEY);
    if (object == null) {
      object = new AnimationController();
      appContext.put(ANIMATION_CONTROLLER_KEY, object);
    } 
    return (AnimationController)object;
  }
  
  private AnimationController() {
    this.timer.setRepeats(true);
    this.timer.setCoalesce(true);
    UIManager.addPropertyChangeListener(this);
  }
  
  private static void triggerAnimation(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState) {
    if (paramJComponent instanceof javax.swing.JTabbedPane || paramPart == TMSchema.Part.TP_BUTTON)
      return; 
    AnimationController animationController = getAnimationController();
    TMSchema.State state = animationController.getState(paramJComponent, paramPart);
    if (state != paramState) {
      animationController.putState(paramJComponent, paramPart, paramState);
      if (paramState == TMSchema.State.DEFAULTED)
        state = TMSchema.State.HOT; 
      if (state != null) {
        long l;
        if (paramState == TMSchema.State.DEFAULTED) {
          l = 1000L;
        } else {
          XPStyle xPStyle = XPStyle.getXP();
          l = (xPStyle != null) ? xPStyle.getThemeTransitionDuration(paramJComponent, paramPart, normalizeState(state), normalizeState(paramState), TMSchema.Prop.TRANSITIONDURATIONS) : 1000L;
        } 
        animationController.startAnimation(paramJComponent, paramPart, state, paramState, l);
      } 
    } 
  }
  
  private static TMSchema.State normalizeState(TMSchema.State paramState) {
    switch (paramState) {
      case DOWNPRESSED:
      case LEFTPRESSED:
      case RIGHTPRESSED:
        return TMSchema.State.UPPRESSED;
      case DOWNDISABLED:
      case LEFTDISABLED:
      case RIGHTDISABLED:
        return TMSchema.State.UPDISABLED;
      case DOWNHOT:
      case LEFTHOT:
      case RIGHTHOT:
        return TMSchema.State.UPHOT;
      case DOWNNORMAL:
      case LEFTNORMAL:
      case RIGHTNORMAL:
        return TMSchema.State.UPNORMAL;
    } 
    return paramState;
  }
  
  private TMSchema.State getState(JComponent paramJComponent, TMSchema.Part paramPart) {
    TMSchema.State state = null;
    Object object = paramJComponent.getClientProperty(PartUIClientPropertyKey.getKey(paramPart));
    if (object instanceof TMSchema.State)
      state = (TMSchema.State)object; 
    return state;
  }
  
  private void putState(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState) { paramJComponent.putClientProperty(PartUIClientPropertyKey.getKey(paramPart), paramState); }
  
  private void startAnimation(JComponent paramJComponent, TMSchema.Part paramPart, TMSchema.State paramState1, TMSchema.State paramState2, long paramLong) {
    boolean bool = false;
    if (paramState2 == TMSchema.State.DEFAULTED)
      bool = true; 
    Map map = (Map)this.animationStateMap.get(paramJComponent);
    if (paramLong <= 0L) {
      if (map != null) {
        map.remove(paramPart);
        if (map.size() == 0)
          this.animationStateMap.remove(paramJComponent); 
      } 
      return;
    } 
    if (map == null) {
      map = new EnumMap(TMSchema.Part.class);
      this.animationStateMap.put(paramJComponent, map);
    } 
    map.put(paramPart, new AnimationState(paramState1, paramLong, bool));
    if (!this.timer.isRunning())
      this.timer.start(); 
  }
  
  static void paintSkin(JComponent paramJComponent, XPStyle.Skin paramSkin, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, TMSchema.State paramState) {
    if (VISTA_ANIMATION_DISABLED) {
      paramSkin.paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      return;
    } 
    triggerAnimation(paramJComponent, paramSkin.part, paramState);
    AnimationController animationController = getAnimationController();
    synchronized (animationController) {
      AnimationState animationState = null;
      Map map = (Map)animationController.animationStateMap.get(paramJComponent);
      if (map != null)
        animationState = (AnimationState)map.get(paramSkin.part); 
      if (animationState != null) {
        animationState.paintSkin(paramSkin, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      } else {
        paramSkin.paintSkinRaw(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramState);
      } 
    } 
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if ("lookAndFeel" == paramPropertyChangeEvent.getPropertyName() && !(paramPropertyChangeEvent.getNewValue() instanceof WindowsLookAndFeel))
      dispose(); 
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    ArrayList arrayList1 = null;
    ArrayList arrayList2 = null;
    for (JComponent jComponent : this.animationStateMap.keySet()) {
      jComponent.repaint();
      if (arrayList2 != null)
        arrayList2.clear(); 
      Map map = (Map)this.animationStateMap.get(jComponent);
      if (!jComponent.isShowing() || map == null || map.size() == 0) {
        if (arrayList1 == null)
          arrayList1 = new ArrayList(); 
        arrayList1.add(jComponent);
        continue;
      } 
      for (TMSchema.Part part : map.keySet()) {
        if (((AnimationState)map.get(part)).isDone()) {
          if (arrayList2 == null)
            arrayList2 = new ArrayList(); 
          arrayList2.add(part);
        } 
      } 
      if (arrayList2 != null) {
        if (arrayList2.size() == map.size()) {
          if (arrayList1 == null)
            arrayList1 = new ArrayList(); 
          arrayList1.add(jComponent);
          continue;
        } 
        for (TMSchema.Part part : arrayList2)
          map.remove(part); 
      } 
    } 
    if (arrayList1 != null)
      for (JComponent jComponent : arrayList1)
        this.animationStateMap.remove(jComponent);  
    if (this.animationStateMap.size() == 0)
      this.timer.stop(); 
  }
  
  private void dispose() {
    this.timer.stop();
    UIManager.removePropertyChangeListener(this);
    synchronized (AnimationController.class) {
      AppContext.getAppContext().put(ANIMATION_CONTROLLER_KEY, null);
    } 
  }
  
  private static class AnimationState {
    private final TMSchema.State startState;
    
    private final long duration;
    
    private long startTime;
    
    private boolean isForward = true;
    
    private boolean isForwardAndReverse;
    
    private float progress;
    
    AnimationState(TMSchema.State param1State, long param1Long, boolean param1Boolean) {
      assert param1State != null && param1Long > 0L;
      assert SwingUtilities.isEventDispatchThread();
      this.startState = param1State;
      this.duration = param1Long * 1000000L;
      this.startTime = System.nanoTime();
      this.isForwardAndReverse = param1Boolean;
      this.progress = 0.0F;
    }
    
    private void updateProgress() {
      assert SwingUtilities.isEventDispatchThread();
      if (isDone())
        return; 
      long l = System.nanoTime();
      this.progress = (float)(l - this.startTime) / (float)this.duration;
      this.progress = Math.max(this.progress, 0.0F);
      if (this.progress >= 1.0F) {
        this.progress = 1.0F;
        if (this.isForwardAndReverse) {
          this.startTime = l;
          this.progress = 0.0F;
          this.isForward = !this.isForward;
        } 
      } 
    }
    
    void paintSkin(XPStyle.Skin param1Skin, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, TMSchema.State param1State) {
      assert SwingUtilities.isEventDispatchThread();
      updateProgress();
      if (!isDone()) {
        float f;
        Graphics2D graphics2D = (Graphics2D)param1Graphics.create();
        param1Skin.paintSkinRaw(graphics2D, param1Int1, param1Int2, param1Int3, param1Int4, this.startState);
        if (this.isForward) {
          f = this.progress;
        } else {
          f = 1.0F - this.progress;
        } 
        graphics2D.setComposite(AlphaComposite.SrcOver.derive(f));
        param1Skin.paintSkinRaw(graphics2D, param1Int1, param1Int2, param1Int3, param1Int4, param1State);
        graphics2D.dispose();
      } else {
        param1Skin.paintSkinRaw(param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1State);
      } 
    }
    
    boolean isDone() {
      assert SwingUtilities.isEventDispatchThread();
      return (this.progress >= 1.0F);
    }
  }
  
  private static class PartUIClientPropertyKey implements UIClientPropertyKey {
    private static final Map<TMSchema.Part, PartUIClientPropertyKey> map = new EnumMap(TMSchema.Part.class);
    
    private final TMSchema.Part part;
    
    static PartUIClientPropertyKey getKey(TMSchema.Part param1Part) {
      PartUIClientPropertyKey partUIClientPropertyKey = (PartUIClientPropertyKey)map.get(param1Part);
      if (partUIClientPropertyKey == null) {
        partUIClientPropertyKey = new PartUIClientPropertyKey(param1Part);
        map.put(param1Part, partUIClientPropertyKey);
      } 
      return partUIClientPropertyKey;
    }
    
    private PartUIClientPropertyKey(TMSchema.Part param1Part) { this.part = param1Part; }
    
    public String toString() { return this.part.toString(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\AnimationController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */