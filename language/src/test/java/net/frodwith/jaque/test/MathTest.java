package net.frodwith.jaque.test;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;

import net.frodwith.jaque.runtime.Atom;
import net.frodwith.jaque.runtime.HoonMath;
import net.frodwith.jaque.exception.ExitException;

import net.frodwith.jaque.test.quickcheck.AtomGenerator;

import static net.frodwith.jaque.parser.CustomParser.simple;
import static net.frodwith.jaque.test.Util.assertNounEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(JUnitQuickcheck.class)
public class MathTest {
  @Test
  public void addExamples() throws ExitException {
    assertNounEquals("ltuae", 42L, HoonMath.add(40L, 2L));
    assertNounEquals("add to max long", simple("18.446.744.073.709.551.620"),
      HoonMath.add(5L, 0xFFFFFFFFFFFFFFFFL));
  }

  @Test
  public void subExamples() throws ExitException {
    assertNounEquals("ltuae", 40L, HoonMath.sub(42L, 2L));
    assertNounEquals("sub to max long", 0xFFFFFFFFFFFFFFFFL,
      HoonMath.sub(simple("18.446.744.073.709.551.620"), 5L));
  }

  @Test
  public void mulExamples() throws ExitException {
    assertNounEquals("ltuae", 42L, HoonMath.mul(6L, 7L));
    assertNounEquals("overmul", simple("510.015.580.149.921.683.079.168"),
      HoonMath.mul(805306368L, 633318697598976L));
  }

  @Test
  public void divExamples() throws ExitException {
    assertNounEquals("ltuae", 7L, HoonMath.div(42L, 6L));
    assertNounEquals("underdiv", 633318697598976L,
      HoonMath.div(simple("510.015.580.149.921.683.079.168"), 805306368L));
  }

  @Test
  public void modExamples() throws ExitException {
    assertNounEquals("smol", 2L, HoonMath.mod(44L, 6L));
    assertNounEquals("undermod", 1000L,
      HoonMath.mod(simple("510.015.580.149.921.683.080.168"), 805306368L));
  }

  @Test
  public void shalExamples() throws ExitException {
    assertNounEquals("(shal 64 0)",
                     simple("6.756.433.270.247.411.179.955.344.271.290.860.428.196.519.276.306.619.770.861.134.681.094.926.084.110.998.387.270.479.862.864.333.985.199.987.953.846.750.880.615.535.480.720.605.515.890.594.991.962.974.587"),
                     HoonMath.shal(64L, 0L));
  }

  @Test
  public void shanExamples() throws ExitException {
    assertNounEquals("(shan 1)",
                     simple("1.093.523.068.179.437.076.151.307.713.719.293.877.757.258.358.775"),
                     HoonMath.shan(1L));
  }

  @Test
  public void shayExamples() throws ExitException {
    assertNounEquals("(shay 64 0)",
                     simple("34.367.557.581.685.318.425.726.328.262.320.917.488.681.687.905.898.731.619.088.842.829.451.262.535.157"),
                     HoonMath.shay(64L, 0L));
  }

  @Test
  public void ripemd160Examples() throws ExitException {
    assertNounEquals("(ripemd-160 3 'abc')",
                     simple("306.825.316.781.432.014.490.141.398.192.080.703.376.357.407.818"),
                     HoonMath.ripemd160(3L, simple("6.513.249")));
  }

  @Test
  public void blake2bExamples() throws ExitException {
    assertNounEquals("(blake2b:blake:crypto [3 'msg'] [3 'key'] 64)",
                     simple("5.044.497.255.341.091.859.203.473.184.154.881.211.109.563.697.098.563.303.175.175.160.892.428.918.284.300.138.977.603.011.582.951.105.353.679.179.092.445.480.852.392.661.809.456.306.547.771.949.898.381.540"),
                     HoonMath.blake2b(3L, simple("6.779.757"),
                                      3L, simple("7.955.819"),
                                      64L));

    assertNounEquals("(blake2b:blake:crypto [3 'msg'] [3 'key'] 16)",
                     simple("124.103.998.576.575.927.634.254.325.900.101.595.959"),
                     HoonMath.blake2b(3L, simple("6.779.757"),
                                      3L, simple("7.955.819"),
                                      16L));

    assertNounEquals("(blake2b:blake:crypto 0^0x0 0^0x0 16)",
                     simple("269.700.418.296.303.037.649.326.473.687.131.911.792"),
                     HoonMath.blake2b(0L, simple("0"),
                                      0L, simple("0"),
                                      16L));
  }

  // subtraction reverses addition
  @Property
  public void addSub(@From(AtomGenerator.class) Object a,
                     @From(AtomGenerator.class) Object b)
    throws ExitException {
    Object c = HoonMath.add(a, b);
    assertNounEquals(a, HoonMath.sub(c, b));
    assertNounEquals(b, HoonMath.sub(c, a));
  }

  // division reverses multiplication
  @Property
  public void mulDiv(@From(AtomGenerator.class) Object a,
                     @From(AtomGenerator.class) Object b)
    throws ExitException {
    assumeTrue(!Atom.isZero(a) && !Atom.isZero(b));
    Object c = HoonMath.mul(a, b);
    assertNounEquals(a, HoonMath.div(c, b));
    assertNounEquals(b, HoonMath.div(c, a));
  }

  // (5x + 1) % 5 == 1
  @Property
  public void addMod(@From(AtomGenerator.class) Object a) throws ExitException {
    assumeTrue(!Atom.isZero(a));
    assertNounEquals(1L, HoonMath.mod(
          HoonMath.add(1L, HoonMath.mul(a, 5L)),
          5L));
  }
}
