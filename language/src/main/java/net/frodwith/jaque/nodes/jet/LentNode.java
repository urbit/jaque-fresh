package net.frodwith.jaque.nodes.jet;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.data.List;
import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

@NodeChild(value="l", type=SlotExpressionNode.class)
public abstract class LentNode extends SubjectNode {
  @Specialization
  protected Object lent(Object l) {
    return List.lent(l);
  }
}
