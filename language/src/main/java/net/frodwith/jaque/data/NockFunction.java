package net.frodwith.jaque.data;

import java.util.function.Function;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.RootCallTarget;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.ForeignAccess;

import net.frodwith.jaque.AstContext;
import net.frodwith.jaque.NockLanguage;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.parser.FormulaParser;

import net.frodwith.jaque.nodes.NockRootNode;

public final class NockFunction {
  private final AstContext context;
  private final Function<AstContext,FormulaParser.ParseResult> factory;
  public final RootCallTarget callTarget;

  public NockFunction(AstContext context, 
                      Function<AstContext,FormulaParser.ParseResult> factory) {
    this.context = context;
    this.factory = factory;

    FormulaParser.ParseResult r = factory.apply(context);
    this.callTarget = Truffle.getRuntime().createCallTarget(
        new NockRootNode(r.context.language, r.sup, r.node));
  }

  public NockFunction forContext(AstContext context) {
    return new NockFunction(context, factory);
  }

  public boolean compatible(AstContext context) {
    return context.compatible(context);
  }
}
