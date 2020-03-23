package net.frodwith.jaque.test;

import java.util.Random;

import org.junit.Ignore;
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
  public void bigModExamples() throws ExitException {
    assertNounEquals(
        "big1",
        simple("4.640.806.609.425.050.133.821.326.691.901.339.258.472.696.025.979.029.648.965.093.743.515.999.954.541"),
        HoonMath.mod(
            simple("4.640.806.609.425.050.133.821.326.691.901.339.258.472.696.025.979.029.648.965.093.743.515.999.954.541"),
            simple("7.237.005.577.332.262.213.973.186.563.042.994.240.857.116.359.379.907.606.001.950.938.285.454.250.989")));
  }

  @Test
  public void bigModExamples2() throws ExitException {
    assertNounEquals(
        "big2",
        simple("96.699.849.181.615.118.444.928.212.134.322.955.296.562.434.741.841.770.111.732.904.765.866.106.786.687.894.106.380.424.913.922.611.076.744.692.643.448.622.984.978.556.553.997.334.863.613.648.192.956.109.704.683.544.426.204.292.209.922.932.924.428.972.285.565.774.743.662.814.054.428.628.154.185.626.347.425.536.265.479.128.983.694.207.597.066.627.479.609.609.905.204.559.713.343.130.170.868.092.560.017.697.054"),
        HoonMath.mod(
            simple("986.236.757.547.332.986.472.011.617.696.226.561.292.849.812.918.563.355.472.727.826.767.720.188.564.083.584.387.121.625.107.510.786.855.734.801.053.524.719.833.194.566.624.465.665.316.622.563.244.215.340.671.405.971.599.343.902.468.620.306.327.831.715.457.360.719.532.421.388.780.770.165.778.156.818.229.863.337.344.187.575.566.725.786.793.391.480.600.129.482.653.072.861.971.002.459.947.277.805.295.727.097.226.389.568.776.499.707.662.505.334.062.639.449.916.265.137.796.823.793.276.300.221.537.201.727.072.401.742.985.542.559.596.685.092.673.521.228.140.822.200.236.743.113.743.661.549.252.453.726.123.450.722.876.929.538.747.702.356.573.783.116.197.523.966.334.991.563.351.853.851.212.597.377.279.504.828.784.751.426.614.832.106.748.796.649.141.394.653.377.243.368.114.757.983.129.918.820.738.877.027.914.769.664"),
            simple("178.288.001.912.986.633.116.615.689.909.551.435.984.774.517.402.157.285.114.275.627.676.406.568.567.058.690.550.720.861.647.008.613.261.527.527.726.263.386.088.950.917.328.085.115.791.632.270.161.310.257.418.421.423.961.720.328.904.814.554.814.527.533.594.034.160.712.876.846.408.026.054.561.312.581.406.823.994.944.154.806.310.480.089.068.670.384.065.681.717.470.163.462.277.806.175.475.753.796.041.262.475.987")));
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

  @Test
  public void dor() throws ExitException {
    assertNounEquals(Atom.YES, HoonMath.dor(6L, 6L));
  }

  @Test
  public void mor() throws ExitException {
    assertNounEquals(Atom.YES, HoonMath.mor(6L, 6L));
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
