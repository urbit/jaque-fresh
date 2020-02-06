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
  @NodeChild(value="msgLen", type=SlotExpressionNode.class),
  @NodeChild(value="msg", type=SlotExpressionNode.class),
  @NodeChild(value="keyLen", type=SlotExpressionNode.class),
  @NodeChild(value="key", type=SlotExpressionNode.class),
  @NodeChild(value="maxOutSize", type=SlotExpressionNode.class)
})
public abstract class Blake2bNode extends SubjectNode {
  @Specialization
  protected Object blake2b(Object msgLen, Object msg, Object keyLen, Object key,
                           Object maxOutSize) {
    try {
      return HoonMath.blake2b(msgLen, msg, keyLen, key, maxOutSize);
    } catch (ExitException e) {
      throw new NockException(e.getMessage(), this);
    }
  }
}
