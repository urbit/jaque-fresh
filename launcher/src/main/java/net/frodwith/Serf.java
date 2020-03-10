package net.frodwith.jaque;

import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.ConsoleHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ArrayIndexOutOfBoundsException;
import java.io.StringWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.lang.UnsupportedOperationException;
import java.lang.StringBuilder;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotAccess;
import org.graalvm.polyglot.PolyglotException;

class NounShapeException extends Exception {
  public NounShapeException(String msg) {
    super(msg);
  }

  public NounShapeException(String msg, Throwable cause) {
    super(msg, cause);
  }
}

/**
 * Subordinate Nock interpreter that's connected to a separate daemon control
 * process.
 */
public class Serf implements Thread.UncaughtExceptionHandler
{
  // Mote definitions:
  //
  public final static long C3__PLAY = 2_036_427_888L;
  public final static long C3__BOOT = 1_953_460_066L;
  public final static long C3__WORK = 1_802_661_751L;
  public final static long C3__DONE = 1_701_736_292L;
  public final static long C3__STDR = 1_919_186_035L;
  public final static long C3__SLOG = 1_735_355_507L;
  public final static long C3__HEAR = 1_918_985_576L;
  public final static long C3__EXIT = 1_953_069_157L;
  public final static long C3__HOLE = 1_701_605_224L;
  public final static long C3__CRUD = 1_685_418_595L;
  public final static long C3__LEAF = 1_717_658_988L;
  public final static long C3__ARVO = 1_870_033_505L;
  public final static long C3__WARN = 1_852_989_815L;
  public final static long C3__MASS = 1_936_941_421L;
  public final static long C3__VEGA = 1_634_166_134L;
  public final static long C3__BELT = 1_953_260_898L;
  public final static long C3__TRIM = 1_835_627_124L;
  public final static long C3__SAVE = 1_702_257_011L;

  // The lifecycle function with an additional axis lookup of +7 instead of
  // just doing .tail.tail in code.
  private final static String LIFECYCLE_SOURCE_STRING = "[7 [2 [0 3] [0 2]] 0 7]";
  private final static Source lifecycleSource =
      Source.newBuilder("nock", LIFECYCLE_SOURCE_STRING, "lifecycle.nock")
      .buildLiteral();

  private final static String CALL_POKE_SOURCE_STRING =
      "[9 2 10 [6 0 3] 9 47 0 2]";
  private final static Source callPokeSource =
      Source.newBuilder("nock", CALL_POKE_SOURCE_STRING, "call-poke.nock")
      .buildLiteral();

  private final static String IMG_LOCATION = "/.urb/chk/jaque.img";

  private final Context truffleContext;
  private final Value nockRuntime;

  private DataInputStream inputStream;
  private DataOutputStream outputStream;

  private Value callPoke = null;

  // Corresponds to u3v_arvo in include/noun/vortex.h.
  private Value who;
  private boolean isFake = false;
  private long bootSequenceLength = 0;
  private Value kernelCore; // c3_noun roc;

  // Corresponds to u3V in worker/main.c.
  private ArrayList<Value> lifecycleFormulas;     // u3_noun roe;
  private long lastEventRequested = 0; // c3_d sen_d;
  private long lastEventProcessed = 0; // c3_d dun_d;
  private long currentMug = 0;         // c3_l mug_l;

  public static void main( String[] args )
      throws IOException
  {
    if (args.length != 3) {
      System.err.println("serf must be started with three arguments.");
      System.exit(-1);
    }

    String pierDir = args[0];
    // args[1] (argv[2]) doesn't matter; it's the dead encryption key, and it's
    // good that its unused since it's passed over the command line and that's
    // visible to everyone.
    //
    // args[2] (argv[3]) doesn't matter for now, it's the packed interpreter
    // options thing, most of which will be ignored.

    Serf serf = new Serf();
    serf.run(pierDir);
  }

  private final static Handler logHandler;

  static {
    logHandler = new ConsoleHandler();
    logHandler.setFormatter(new Formatter() {
      public String format(LogRecord r) {
        return r.getMessage() + "\r\n";
      }
    });
  }

  public Serf() throws FileNotFoundException {
    this.truffleContext = Context
                        .newBuilder("nock")
                        .logHandler(logHandler)
                        .option("nock.memo", "99999")
                        .allowAllAccess(true)
                        .build();
    this.truffleContext.initialize("nock");
    this.nockRuntime = this.truffleContext
                     .getPolyglotBindings()
                     .getMember("nock");

    this.nockRuntime.invokeMember("installArvoJets");

    fixupFileDescriptors();

    // Several of our values need to be truffle values, even if they start off
    // as 0.
    this.who = this.truffleContext.asValue(0L);
    this.lifecycleFormulas = new ArrayList<Value>();
    this.kernelCore = this.truffleContext.asValue(0L);
  }

  public void run(String pierDir) throws IOException {
    ensurePierCheckpointDirectoryExist(pierDir + "/.urb/chk/");
    attemptToReloadImage(pierDir);
    sendBoot();

    // Read until EOF
    try {
      boolean done = false;
      while (!done) {
        //System.err.println("Waiting for message...");
        Value message = readNoun();

        long tag = getHeadTag(message);
        if (tag == C3__BOOT) {
          // System.err.println("poke boot");
          onBootMessage(message);
        } else if (tag == C3__WORK) {
          // System.err.println("poke work");
          onWorkMessage(message);
        } else if (tag == C3__EXIT) {
          //System.err.println("poke exit");
          onExitMessage(message);
        } else if (tag == C3__SAVE) {
          //System.err.println("poke save");
          onSaveMessage(pierDir, message);
        } else {
          // TODO: long -> String
          throw new NounShapeException("Invalid request tag: " + tag);
        }
      }
    } catch (NounShapeException e) {
      System.err.println("(noun shape exception; shutting down)");
      e.printStackTrace(System.err);
    } catch (Throwable e) {
      System.err.println("THROWABLE");
      e.printStackTrace(System.err);
      try {
        // Wait half a second for things to write to stderr before our parent
        // process murders our connection to stderr when our process ded.
        Thread.sleep(500);
      } catch (InterruptedException ie) {}
    }
  }

  /**
   * In the C urbit-worker, we do a bunch of stuff with dup() and file
   * descriptors to ensure that unrelated pieces of the program don't
   * read/write from stdin/stdout. As a aserf, stdin is a binary stream of
   * serialized events and stdout is a binary stream of . To make sure uses
   * of System.out don't interfere with this, we set
   */
  private void fixupFileDescriptors() throws FileNotFoundException {
    Thread.setDefaultUncaughtExceptionHandler(this);

    this.inputStream = new DataInputStream(System.in);
    this.outputStream = new DataOutputStream(System.out);

    // TODO: Do something nicer with System.in.
    System.setIn(null);
    // FileOutputStream f = new FileOutputStream("serf.txt");
    // System.setErr(new PrintStream(f));
    System.setOut(null);
  }

  private void onBootMessage(Value message)
      throws NounShapeException
  {
    try {
      Value bootSequenceLength = message.getArrayElement(1);
      this.bootSequenceLength = bootSequenceLength.asLong();

      // The boot sequence length is 1 with solid pills and 5 with brass pills.
      System.err.println("poke boot: bootLen=" + this.bootSequenceLength + "\r\n");
    } catch (UnsupportedOperationException e) {
      throw new NounShapeException("Couldn't unpack boot message", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new NounShapeException("Couldn't unpack boot message", e);
    }
  }

  private void onWorkMessage(Value message)
      throws NounShapeException, IOException
  {
    try {
      Value tail = message.getArrayElement(1);
      long eventNum = tail.getArrayElement(0).asLong();
      Value jammedValue = tail.getArrayElement(1);

      // Unpack the packed work format.
      Value mugOvum = nockRuntime.invokeMember("cue", jammedValue);
      long expectedMug = mugOvum.getArrayElement(0).asLong();
      Value job = mugOvum.getArrayElement(1);

      if (expectedMug != this.currentMug) {
        StringBuilder b = new StringBuilder();
        b.append("Work message for event #");
        b.append(eventNum);
        b.append(" expects mug ");
        b.append(expectedMug);
        b.append(" but current state has mug ");
        b.append(this.currentMug);
        throw new NounShapeException(b.toString());
      }

      if (eventNum <= bootSequenceLength) {
        doWorkBoot(eventNum, job);
      } else {
        doWorkLive(eventNum, job);
      }
    } catch (PolyglotException e ) {
      throw new NounShapeException("Couldn't execute work message", e);
    } catch (UnsupportedOperationException e) {
      throw new NounShapeException("Couldn't unpack work message", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new NounShapeException("Couldn't unpack work message", e);
    }
  }

  private void onExitMessage(Value message)
      throws NounShapeException
  {
    try {
      int exitCode = message.getArrayElement(1).asInt();
      System.exit(exitCode);
    } catch (UnsupportedOperationException e) {
      throw new NounShapeException("Couldn't unpack exit message", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new NounShapeException("Couldn't unpack exit message", e);
    }
  }

  private void onSaveMessage(String pierDir, Value message)
      throws NounShapeException
  {
    // TODO: Saving an image is much slower in jaque than it is in vere; we
    // probably want vere to block until there's confirmation that the image
    // has completed writing.
    System.err.println("Beginning image save for event " +
                       this.lastEventProcessed + " with mug " + this.currentMug +
                       "...");
    nockRuntime.invokeMember("saveImage", pierDir + IMG_LOCATION,
                             this.lastEventProcessed,
                             this.kernelCore);
    System.err.println("Completed image save...");
  }

  private void doWorkBoot(long eventNum, Value job)
      throws NounShapeException, IOException
  {
    if (eventNum != this.lastEventRequested + 1L) {
      StringBuilder b = new StringBuilder();
      b.append("Boot message specifies event #");
      b.append(eventNum);
      b.append(" but lastEventRequested was ");
      b.append(this.lastEventRequested);
      throw new NounShapeException(b.toString());
    }

    long jobMug = nockRuntime.invokeMember("mug", job).asLong();

    this.lastEventRequested = eventNum;
    this.lifecycleFormulas.add(job);

    if ( this.bootSequenceLength == eventNum ) {
      this.lifecycleFormulas.add(this.truffleContext.asValue(0L));
      Value eve = nockRuntime.invokeMember("toNoun", this.lifecycleFormulas.toArray());
      this.lifecycleFormulas = new ArrayList<Value>();

      long eveMug = nockRuntime.invokeMember("mug", eve).asLong();

      // "u3v_boot()"
      //
      // TODO: OK, u3m_soft() in vere could return error listings. We'd want to
      // rebuild that functionality here.
      //
      this.kernelCore = performBoot(eve);

      this.currentMug = nockRuntime.invokeMember("mug", this.kernelCore).asLong();
      this.lastEventProcessed = eventNum;
    } else {
      // Prior to the evaluation of the entire lifecycle sequence, we simply
      // use the mug of the formula as the kernel mug.
      this.currentMug = jobMug;
    }

    sendDone(eventNum, this.currentMug, this.truffleContext.asValue(0L));
  }

  /**
   * Given the boot sequence eve, apply the lifecycle function to it.
   */
  private Value performBoot(Value eve) {
    Value lifeCycle = this.truffleContext.eval(lifecycleSource);
    Value gat = lifeCycle.execute(eve);
    return gat;
  }

  /**
   * Handle each normal event.
   */
  private void doWorkLive(long eventNum, Value job)
      throws NounShapeException, IOException
  {
    // We perform a poke with the current ovo. Performing a poke first computes
    // a function gate where we grab the poke arm with a context of the rest of
    // roc. Then we actually slam that produced function with [date ovo]. And
    // interpret the return of it.
    if (this.callPoke == null) {
      // Lazily compile the poke function
      this.callPoke = this.truffleContext.eval(callPokeSource);
    }

    if (eventNum != this.lastEventProcessed + 1L) {
      StringBuilder b = new StringBuilder();
      b.append("Work message specifies event #");
      b.append(eventNum);
      b.append(" but lastEventRequested was ");
      b.append(this.lastEventRequested);
      throw new NounShapeException(b.toString());
    }

    this.lastEventRequested = eventNum;

    // In vere, _worker_work_live() takes a job noun, immediately unpacks it
    // into [now ovo], and indirectly passes ovo to _cv_nock_poke(), which
    // reconstructs sam out of [now ovo]. I believe there's no reason for that.
    //
    Value product = this.callPoke.execute(this.kernelCore, job);

    // Now that we have the product, we
    Value listOvum = product.getArrayElement(0);
    this.kernelCore = product.getArrayElement(1);

    this.lastEventProcessed = eventNum;
    this.currentMug = nockRuntime.invokeMember("mug", this.kernelCore).asLong();

    sendDone(eventNum, this.currentMug, listOvum);
  }

  /**
   * Sends the initial [%play ~] atom to the king to specify that we're ready
   * for pleas.
   */
  private void sendBoot() throws IOException {
    // We return the next event we want to see.
    long nex = 1L;
    if (0 != lastEventProcessed) {
      nex += lastEventProcessed;
    }

    writeNoun(nockRuntime.invokeMember("toNoun", C3__PLAY, nex, this.currentMug));
  }

  private void sendDone(long event, long mug, Value effects) throws IOException {
    logHandler.publish(new LogRecord(Level.FINE, "Sending DONE(" + event + ", " +
                                     mug + ", " + effects + ")"));
    writeNoun(nockRuntime.invokeMember("toNoun", C3__DONE, event, mug, effects));
  }

  /**
   * Fetches the head tag, assuming that v is a cell where the head is an atom
   * that will fit in a long.
   */
  private long getHeadTag(Value v) throws NounShapeException {
    try {
      Value head = v.getArrayElement(0);
      return head.asLong();
    } catch (UnsupportedOperationException e) {
      throw new NounShapeException("v doesn't appear to be a Cell", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new NounShapeException("Couldn't access head", e);
    }
  }

  /**
   * Reads an atom from the input stream and returns it.
   */
  private Value readNoun() throws IOException, EOFException {
    int length = (int)Long.reverseBytes(inputStream.readLong());
    byte[] bytes = new byte[length];
    inputStream.readFully(bytes, 0, length);

    Value atom = nockRuntime.invokeMember("fromBytes", bytes);
    return nockRuntime.invokeMember("cue", atom);
  }

  /**
   * Writes the length of the serialized atom bytes and the bytes themselves.
   */
  private void writeNoun(Value arbitraryNoun) throws IOException {
    Value jammedValue = nockRuntime.invokeMember("jam", arbitraryNoun);
    Value vBytes = nockRuntime.invokeMember("toBytes", jammedValue);
    byte[] bytes = vBytes.as(byte[].class);
    outputStream.writeLong(Long.reverseBytes(bytes.length));
    outputStream.write(bytes, 0, bytes.length);
    outputStream.flush();
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    // We mustn't write to stdout ever.
    e.printStackTrace(System.err);
    System.exit(-1);
  }

  private void ensurePierCheckpointDirectoryExist(String pierDir) {
    File directory = new File(pierDir);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  private void attemptToReloadImage(String pierDir) {
    String imgPath = pierDir + "/.urb/chk/jaque.img";
    File snapshot = new File(imgPath);
    if (snapshot.exists()) {
      Value tuple = nockRuntime.invokeMember("loadImage", imgPath);
      this.lastEventProcessed = tuple.getArrayElement(0).asLong();
      this.kernelCore = tuple.getArrayElement(1);
      this.currentMug =
          nockRuntime.invokeMember("mug", this.kernelCore).asLong();
    }
  }
}
