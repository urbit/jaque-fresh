package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.data.Axis;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.nodes.op.PullOpNode;
import net.frodwith.jaque.nodes.op.PullOpNodeGen;
import net.frodwith.jaque.nodes.op.SlamOpNode;

import net.frodwith.jaque.runtime.Murmug;

/* +mule is a typed wrapper around mute. +mule is a type checking trick, whose
 * formal definition runs the gate twice, first virtualized, and if it works,
 * then unvirtualized so that we can get the typed return value. In practice,
 * it is exactly equivalent to +mute. The vere jet just dispatches to the mute
 * gate.
 */
@NodeChildren({
  @NodeChild(value="context", type=SlotExpressionNode.class),
  @NodeChild(value="tap", type=SlotExpressionNode.class)
})
public abstract class MuleNode extends SubjectNode {
  private static final long MUTE_JET_HOOK = 0x2fbabeL;
  private @Child SlamOpNode slamNode;
  private @Child PullOpNode pullGateNode;

  protected MuleNode(AstContext astContext) {
    this.slamNode = new SlamOpNode(astContext, false);
    this.pullGateNode = PullOpNodeGen.create(
        astContext, Axis.get(MUTE_JET_HOOK), false);
  }

  @Specialization
  public Object mule(Object context, Object tap) {
    Object mute = pullGateNode.executePull(context);
    return slamNode.executeSlam(mute, tap);
  }
}
