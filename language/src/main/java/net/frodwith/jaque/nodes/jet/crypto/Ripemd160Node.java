package net.frodwith.jaque.nodes.jet.crypto;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.Fallback;

import net.frodwith.jaque.nodes.expression.SlotExpressionNode;
import net.frodwith.jaque.nodes.SubjectNode;
import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.exception.NockException;

import com.google.common.io.BaseEncoding;

@NodeChildren({
  @NodeChild(value="len", type=SlotExpressionNode.class),
  @NodeChild(value="data", type=SlotExpressionNode.class)
})
public abstract class Ripemd160Node extends SubjectNode {
  @Specialization
  protected Object ripemd160(Object len, Object data) {
    try {
      return HoonMath.ripemd160(len, data);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
