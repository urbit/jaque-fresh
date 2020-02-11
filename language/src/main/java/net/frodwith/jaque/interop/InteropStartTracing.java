package net.frodwith.jaque.interop;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;

import net.frodwith.jaque.Tracer;

import java.io.FileNotFoundException;

@ExportLibrary(InteropLibrary.class)
public final class InteropStartTracing implements TruffleObject {
  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  @TruffleBoundary
  private void start(String filename) throws FileNotFoundException {
    Tracer.StartTracing(filename);
  }

  @ExportMessage
  public Object execute(Object[] arguments,
    @CachedLibrary(limit="3") InteropLibrary interop)
      throws ArityException, UnsupportedTypeException {
    if ( 1 != arguments.length ) {
      throw ArityException.create(1, arguments.length);
    }
    else {
      try {
        start((String)arguments[0]);
        // Must return a value. Cannot return null or truffle crashes.
        return 1L;
      } catch (FileNotFoundException e) {
        throw UnsupportedTypeException.create(arguments);
      }
    }
  }
}
