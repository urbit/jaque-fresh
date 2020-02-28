package net.frodwith.jaque.dashboard;

import java.util.function.Supplier;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Equality;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

public abstract class UnlocatedFine extends FineCheck {
  protected final Cell battery;

  protected UnlocatedFine(Cell battery) {
    this.battery = battery;
  }

  protected abstract boolean extraChecks(Cell core, Dashboard dashboard);

  @TruffleBoundary
  public final boolean check(Cell core, Dashboard dashboard) {
    return Equality.equals(core.head, battery) && extraChecks(core, dashboard);
  }
}
