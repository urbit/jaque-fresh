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

import net.frodwith.jaque.data.BigAtom;
import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.runtime.Murmug;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Dashboard;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;

@ExportLibrary(InteropLibrary.class)
public final class InteropSaveImage implements TruffleObject {
  @TruffleBoundary
  private static void saveImage(String filename, Dashboard dashboard, Object obj)
      throws IOException {
    FileOutputStream fileOutputStream
      = new FileOutputStream(filename);
    ObjectOutputStream objectOutputStream
      = new ObjectOutputStream(fileOutputStream);
    objectOutputStream.writeObject(dashboard.saveRegistrationRecord());
    objectOutputStream.writeObject(obj);

    objectOutputStream.flush();
    objectOutputStream.close();
  }

  @ExportMessage
  public boolean isExecutable() {
    return true;
  }

  @ExportMessage
  public Object execute(Object[] arguments)
      throws ArityException, UnsupportedMessageException {
    if ( 2 != arguments.length ) {
      throw ArityException.create(2, arguments.length);
    }
    else {
      try {
        NockContext context = (NockContext)arguments[0];
        Object[] origArguments = (Object[])arguments[1];
        saveImage((String)origArguments[0], context.getDashboard(), origArguments[1]);
      } catch (IOException e) {
        e.printStackTrace();
        throw UnsupportedMessageException.create();
      }
      return 1L;
    }
  }
}
