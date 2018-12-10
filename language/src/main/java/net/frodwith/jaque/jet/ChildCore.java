package net.frodwith.jaque.jet;

import java.util.Map;

import us.bpsm.edn.Keyword;

import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.dashboard.Hook;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.StaticLocation;
import net.frodwith.jaque.dashboard.StaticChildLocation;
import net.frodwith.jaque.dashboard.DynamicChildLocation;
import net.frodwith.jaque.dashboard.BatteryHash;

public final class ChildCore extends JetCore {
  public final Axis toParent;
  private static final Keyword axisKey = Keyword.newKeyword("parent");

  public ChildCore(String name,
                   Axis toParent,
                   BatteryHash[] hashes,
                   JetArm[] arms,
                   JetHook[] hooks,
                   ChildCore[] children) {
    super(name, hashes, arms, hooks, children);
    this.toParent = toParent;
  }

  @Override
  protected Location getLocation(Location parent, Map<String,Hook> hooks) {
    assert( null != parent );
    return ( Axis.TAIL == toParent && parent instanceof StaticLocation )
      ? new StaticChildLocation(name, hooks, (StaticLocation) parent)
      : new DynamicChildLocation(name, hooks, parent, toParent);
  }

  public static ChildCore parseOption(Object option) {
    Map<?,?> m = (Map<?,?>) option;
    String name = (String) m.get(nameKey);
    Object ao = m.get(axisKey);
    Axis toParent = Axis.parseOption(m.get(axisKey));

    BatteryHash[] hashes = parseHashes(m.get(hashKey));
    JetArm[] arms = parseArms(m.get(armKey));
    JetHook[] hooks = parseHooks(m.get(hookKey));
    ChildCore[] children = parseChildren(m.get(childKey));

    return new ChildCore(name, toParent, hashes, arms, hooks, children);
  }
}
