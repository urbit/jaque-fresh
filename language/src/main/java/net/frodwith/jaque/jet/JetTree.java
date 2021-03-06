package net.frodwith.jaque.jet;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.data.AxisMap;
import net.frodwith.jaque.data.NockFunction;
import net.frodwith.jaque.dashboard.Location;
import net.frodwith.jaque.dashboard.BatteryHash;
import net.frodwith.jaque.dashboard.Registration;
import net.frodwith.jaque.runtime.NockContext;

public final class JetTree {
  public final RootCore[] roots;

  public JetTree(RootCore[] roots) {
    this.roots = roots;
  }

  public void addToMaps(NockLanguage language,
                        NockContext context,
                        Map<BatteryHash,Registration> hot,
                        Map<Location, AxisMap<NockFunction>> driver) {
    for ( RootCore r : roots ) {
      r.addToMaps(null, language, context, hot, driver);
    }
  }
}
