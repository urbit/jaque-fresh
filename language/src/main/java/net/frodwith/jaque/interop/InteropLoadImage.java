package net.frodwith.jaque.interop;

import java.util.Map;
import net.frodwith.jaque.runtime.StrongCellGrainKey;

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
import net.frodwith.jaque.runtime.HoonSerial;
import net.frodwith.jaque.runtime.Murmug;
import net.frodwith.jaque.runtime.NockContext;
import net.frodwith.jaque.exception.ExitException;
import net.frodwith.jaque.dashboard.Dashboard;
import net.frodwith.jaque.dashboard.Registration;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;

@ExportLibrary(InteropLibrary.class)
public final class InteropLoadImage implements TruffleObject {
  @TruffleBoundary
  private static Object loadImage(String filename, NockContext context)
      throws IOException, ClassNotFoundException, ExitException {
    FileInputStream fileInputStream
      = new FileInputStream(filename);
    ObjectInputStream objectInputStream
      = new ObjectInputStream(fileInputStream);
    Object registrationRecords = objectInputStream.readObject();

    // Reconstitute the cold registrations.
    context.getDashboard().rerunRegistrationRecords(registrationRecords);

    Object nounTree = objectInputStream.readObject();
    objectInputStream.close();

    return HoonSerial.cue(nounTree);
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
        return loadImage((String)origArguments[0], context);
      } catch (ExitException | ClassNotFoundException | IOException e) {
        e.printStackTrace();
        throw UnsupportedMessageException.create();
      }
    }
  }
}
