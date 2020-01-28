package net.frodwith.jaque.nodes.dispatch;

import com.oracle.truffle.api.CallTarget;

import net.frodwith.jaque.nodes.NockNode;

public abstract class DispatchNode extends NockNode {
  public abstract Object executeDispatch(CallTarget target, Object subject);
}
