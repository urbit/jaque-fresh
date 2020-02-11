package net.frodwith.jaque;

import java.io.OutputStream;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

/* Prints trace events to an OutputStream.
 *
 * This is a simple port of trace.c from vere to jaque. This outputs a
 * chrome://tracing compatible json file, with (mostly) the same output. (The
 * main difference is that we have individual fast hints instead of full fast
 * hint paths.)
 */
public final class Tracer {
  private final java.util.Stack<String> hintStack;
  private final java.util.Stack<Long> startTimeStack;
  private JsonGenerator generator;

  // I cannot figure out where to put this or how to massage this into
  // NockContext vs AstContext vs NockLanguage. Screw it. It's a singleton.
  private static Tracer singleton;

  public static void StartTracing(String filename) throws FileNotFoundException {
    singleton = new Tracer(new FileOutputStream(filename));
  }

  public static Tracer Get() {
    return singleton;
  }

  private Tracer(OutputStream stream) {
    this.hintStack = new java.util.Stack<String>();
    this.startTimeStack = new java.util.Stack<Long>();
    this.generator = Json.createGenerator(stream);

    this.generator.writeStartArray();
  }

  public void close() {
    generator.writeEnd();
    generator.close();
    generator = null;
  }

  @TruffleBoundary
  public void push(String fastHint) {
    hintStack.push(fastHint);
    startTimeStack.push(System.nanoTime());
  }

  @TruffleBoundary
  public void pop() {
    String hint = hintStack.pop();
    long startTimeNanos = startTimeStack.pop();


    if (hint == "") {
      return;
    }

    long endTimeNanos = System.nanoTime();
    long durationNanos = endTimeNanos - startTimeNanos;
    long durationMicros = TimeUnit.NANOSECONDS.toMicros(durationNanos);

    // 33 microseconds (a 30th of a millisecond).
    if (durationMicros > 33) {
      long startTimeMicros = TimeUnit.NANOSECONDS.toMicros(startTimeNanos);

      generator
          .writeStartObject()
            .write("cat", "nock")
            .write("name", hint)
            .write("ph", "X")
            .write("pid", 1)
            .write("tid", 2)
            .write("ts", startTimeMicros)
            .write("dur", durationMicros)
          .writeEnd();
    }
  }
}
