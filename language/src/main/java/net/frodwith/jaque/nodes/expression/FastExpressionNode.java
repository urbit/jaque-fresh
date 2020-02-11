package net.frodwith.jaque.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.nodes.op.RegisterOpNode;
import net.frodwith.jaque.nodes.op.InitialRegisterOpNode;
import net.frodwith.jaque.data.FastClue;

import java.io.StringWriter;
import net.frodwith.jaque.interop.InteropDebugDump;

import net.frodwith.jaque.Tracer;

public final class FastExpressionNode extends NockExpressionNode {
  private @Child NockExpressionNode hintNode;
  private @Child NockExpressionNode nextNode;
  private @Child RegisterOpNode registerNode;

  public FastExpressionNode(Dashboard dashboard,
      NockExpressionNode hintNode, NockExpressionNode nextNode) {
    this.hintNode = hintNode;
    this.nextNode = nextNode;
    this.registerNode = new InitialRegisterOpNode(dashboard);
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object clue = hintNode.executeGeneric(frame);
    Object core = nextNode.executeGeneric(frame);

    // TODO: We might need to parse out the clue name here? If not, this can go
    // away.
    //
    Tracer tracer = Tracer.Get();
    String clueName = null;
    if (tracer != null) {
      clueName = parseClue(clue);
    }

    // try {
    //   FastClue fc = FastClue.parse(clue);
    //   System.err.print(fc.name);
    //   System.err.print(" - ");
    //   // StringWriter w = new StringWriter();
    //   // InteropDebugDump.debugDump(w, clue, false, 0, 5);
    //   // System.err.println(w.toString());
    // } catch (Exception e) {}

    registerNode.executeRegister(core, clue);
    return core;
  }

  @TruffleBoundary
  String parseClue(Object clue) {
    try {
      return FastClue.parse(clue).name;
    } catch (Exception e) {
      return null;
    }
  }
}
