package net.frodwith.jaque.jet;

import java.util.Map;
import java.util.HashMap;

import com.google.common.hash.HashCode;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.NockFunction;

public abstract class JetCore {
  public final String name;
  public final HashCode[] hashes;
  public final JetArm[] arms;
  public final JetHook[] hooks;
  public final ChildCore[] children;

  protected abstract Location getLocation(Location parent,
                                          Map<String,Hook> hooks);

  protected JetCore(String name,
                    HashCode[] hashes,
                    JetArm[] arms,
                    JetHook[] hooks,
                    ChildCore[] children) {
    this.name     = name;
    this.hashes   = hashes;
    this.arms     = arms;
    this.hooks    = hooks;
    this.children = children;
  }

  public final void addToMaps(Location parent,
                              NockLanguage language,
                              Dashboard dashboard,
                              Map<HashCode,Registration> hot,
                              Map<Location,AxisMap<CallTarget>> driver) {
    Map<String,Hook> hookMap = new HashMap<>();
    for ( JetHook h : hooks ) {
      hookMap.put(h.name, h.hook);
    }

    Registration r = new Registration();
    Location loc = getLocation(parent, hookMap);
    loc.register(r);
    for ( HashCode h : hashes ) {
      hot.put(h, r);
    }

    AxisMap functions = AxisMap.EMPTY;
    for ( JetArm arm : arms ) {
      Axis ax = arm.getAxis(hookMap);
      JetContext jetContext = new JetContext(ax, dashboard, language);
      functions = functions.insert(ax, arm.getFunction(jetContext));
    }
    driver.put(loc, functions);

    for ( ChildCore child : children ) {
      child.addToMaps(loc, language, dashboard, hot, driver);
    }
  }
}
