package net.frodwith.jaque.nodes;


import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.FastClue;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Dashboard;

public final class FullyDynamicRegistrationNode extends RegistrationNode {

  public FullyDynamicRegistrationNode(Dashboard dashboard) {
    super(dashboard);
  }

  @Override
  public void executeRegister(Object core, Object clue) {
    try {
      register(Cell.require(core), FastClue.parse(clue));
    }
    catch ( ExitException e ) {
    }
  }
}
