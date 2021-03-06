package net.frodwith.jaque.runtime;

import net.frodwith.jaque.data.Cell;
import net.frodwith.jaque.data.Trel;
import net.frodwith.jaque.data.Qual;
import net.frodwith.jaque.data.Motes;
import net.frodwith.jaque.runtime.Lists;
import net.frodwith.jaque.runtime.Tapes;
import net.frodwith.jaque.exception.ExitException;

/* Support for washing leaf/rose/palm tanks. Other kinds (plums, future kinds)
 * will, for now, throw an ExitException. In the future, it may be updated to
 * support plums, etc., but +wash from HoonLanguage should be preferred if
 * available. Use this code only under duress (for example, while booting an
 * ivory pill for HoonLanguage).
 *
 * p.s. I am truly, truly sorry about the naming in this file. */

public final class Tanks {
  private static final long ACE = ' ',
                            BAS = '\\',
                            FAS = '/';

  private static boolean isSpace(Object atom) {
    return (atom instanceof Long) && (ACE == ((long) atom));
  }

  private static final Object re_ram(Object tac) throws ExitException {
    Cell c = Cell.require(tac);

    switch ( Atom.requireInt(c.head) ) {
      case Motes.LEAF:
        return c.tail;
      case Motes.PALM:
        return re_ram_palm(c.tail);
      case Motes.ROSE:
        return re_ram_rose(c.tail);
      default:
        throw new ExitException("unknown tank tag");
    }
  }
  
  private static final Object re_ram_palm(Object noun) throws ExitException {
    Cell bub = Cell.require(noun);
    Qual qua = Qual.require(bub.head);
    Trel pur = new Trel(qua.p, Lists.weld(qua.q, qua.r), qua.s);
    Cell rob = new Cell(pur.toNoun(), bub.tail);

    return re_ram_rose(rob);
  }
  
  private static final Object re_ram_rose(Object noun) throws ExitException {
    Cell bub = Cell.require(noun);
    Trel tre = Trel.require(bub.head);
    
    return Lists.weld(tre.q, re_ram_rose_in(tre.p, tre.r, bub.tail));
  }
  
  private static final Object re_ram_rose_in(Object p, Object r, Object res) 
    throws ExitException {
    if ( Atom.isZero(res) ) {
      return r;
    }
    Cell rec = Cell.require(res);
    Object voz = re_ram_rose_in(p, r, rec.tail),
           dex = re_ram(rec.head),
           sin = Atom.isZero(rec.tail) ? voz : Lists.weld(p, voz);
    
    return Lists.weld(dex, sin);
  }
  
  private static final Object 
    re_win_buc(Object tac, Object tab, Object edg, Object lug)
      throws ExitException {
    Cell c = Cell.require(tac);
    
    switch ( Atom.requireInt(c.head) ) {
      case Motes.LEAF:
        return re_win_leaf(tac, tab, edg, lug);
      case Motes.PALM:
        return re_win_palm(tac, tab, edg, lug);
      case Motes.ROSE:
        return re_win_rose(tac, tab, edg, lug);
      default:
        throw new ExitException("unknown tank tag");
    }
  }
  
  private static final Object re_win_din(Object tab, Object edg)
    throws ExitException {
    return HoonMath.mod(HoonMath.add(2L, tab), 
                        HoonMath.mul(2L, HoonMath.div(edg, 3L)));
  }
  
  private static final Object re_win_fit(Object tac, Object tab, Object edg) 
    throws ExitException {
    Object ram = re_ram(tac),
           len = Lists.lent(ram),
           dif = HoonMath.sub(edg, tab);
    
    return (Atom.compare(len, dif) != 1) ? Atom.YES : Atom.NO;
  }
  
  private static final Object re_win_leaf(Object tac, Object tab, Object edg, Object lug)
    throws ExitException {
    Cell c = Cell.require(tac);
    return re_win_rig(c.tail, tab, edg, lug);
  }
  
  private static final Object re_win_palm(Object tac, Object tab, Object edg, Object lug)
    throws ExitException {
    if ( Atom.isYes(re_win_fit(tac, tab, edg)) ) {
      return re_win_rig(re_ram(tac), tab, edg, lug);
    }
    else {
      Cell c = Cell.require(tac);
      Cell bub = Cell.require(c.tail);
      Qual qua = Qual.require(bub.head);
      
      if ( Atom.isZero(bub.tail) ) {
        return re_win_rig(qua.q, tab, edg, lug);
      }
      Cell res = Cell.require(bub.tail);
      if ( Atom.isZero(res.tail) ) {
        Object bat = HoonMath.add(2L, tab),
               gul = re_win_buc(res.head, tab, edg, lug);
        return re_win_rig(qua.q, bat, edg, gul);
      }
      else {
        Object lyn = HoonMath.mul(2L,  Lists.lent(res)),
               qyr = re_win_palm_qyr(tab, edg, lyn, res, lug);
        return re_win_wig(qua.q, tab, edg, qyr);
      }
    }
  }
  
  private static final Object 
    re_win_palm_qyr(Object tab, Object edg, Object lyn, Object res, Object lug)
      throws ExitException {
    if ( Atom.isZero(res) ) {
      return lug;
    }
    Cell c = Cell.require(res);
    Object cat = c.head,
           sub = HoonMath.sub(lyn, 2L),
           bat = HoonMath.add(tab, sub),
           gul = re_win_palm_qyr(tab, edg, sub, c.tail, lug);

    return re_win_buc(cat, bat, edg, gul);
  }

  private static final Object
    re_win_rig_in(Object hom, Object tab, Object edg, Object lug, Cell mut)
      throws ExitException {
    if ( Atom.isZero(hom) ) {
      return new Cell(Tapes.runt(HoonMath.sub(tab, 2L), ACE,
        new Trel(BAS, FAS,
          Tapes.runt(HoonMath.sub(edg, tab), ACE,
            Lists.make(BAS, FAS))).toNoun()), lug);
    }
    else {
      mut = Tapes.trim(HoonMath.sub(edg, tab), hom);
      return new Cell(Tapes.runt(tab, ACE, mut.head),
        re_win_rig_in(mut.tail, tab, edg, lug, mut));
    }
  }

  private static final Object re_win_rig(Object hom, Object tab, Object edg, Object lug)
    throws ExitException {
    if ( 1 != Atom.compare(Lists.lent(hom), HoonMath.sub(edg, tab)) ) {
      return new Cell(Tapes.runt(tab, ACE, hom), lug);
    }
    else {
      tab = HoonMath.add(tab, 2L);
      edg = HoonMath.sub(edg, 2L);
      Cell mut = Tapes.trim(HoonMath.sub(edg, tab), hom);
      return new Cell(
        Tapes.runt(HoonMath.sub(tab, 2L), ACE,
          new Trel(BAS, FAS,
            Lists.weld(mut.head, Lists.make(BAS, FAS))).toNoun()),
        re_win_rig_in(mut.tail, tab, edg, lug, mut));
    }
  }

  private static final Object re_win_rose(Object tac, Object tab, Object edg, Object lug)
    throws ExitException {
    Cell c = Cell.require(tac);
    Cell bub = Cell.require(c.tail);
    Trel tre = Trel.require(bub.head);
    
    if ( Atom.isYes(re_win_fit(tac, tab, edg)) ) {
      return re_win_rig(re_ram(tac), tab, edg, lug);
    }
    else {
      Object gul = re_win_rose_lug(tre.r, tab, edg, bub.tail, lug);
      if ( Atom.isZero(tre.q) ) {
        return gul;
      }
      else {
        return re_win_wig(tre.q, tab, edg, gul);
      }
    }
  }
  
  private static final Object
    re_win_rose_lug(Object r, Object tab, Object edg, Object res, Object lug)
      throws ExitException {
    if ( Atom.isZero(res) ) {
      if ( Atom.isZero(r) ) {
        return lug;
      }
      else {
        return re_win_rig(r, tab, edg, lug);
      }
    }
    else {
      Cell c = Cell.require(res);
      Object cat = c.head,
             gul = re_win_rose_lug(r, tab, edg, c.tail, lug),
             bat = re_win_din(tab, edg);
      return re_win_buc(cat, bat, edg, gul);
    }
  }
  
  private static final Object 
    re_win_wig(Object hom, Object tab, Object edg, Object lug) 
      throws ExitException {

    if ( Atom.isZero(lug) ) {
      return re_win_rig(hom, tab, edg, lug);
    }

    Cell c = Cell.require(lug);
    Object lin = Lists.lent(hom),
           wug = HoonMath.increment(HoonMath.add(tab, lin));

    if ( Atom.isNo(re_win_wig_mir(c.head, wug)) ) {
      return re_win_rig(hom, tab, edg, lug);
    }
    else {
      Object sin = new Cell(ACE, Lists.slag(wug, c.head)),
             moh = Lists.weld(hom,  sin),
             dex = Tapes.runt(tab, ACE, moh);
      return new Cell(dex, c.tail);
    }
  }
  
  private static final Object re_win_wig_mir(Object mir, Object wug)
    throws ExitException {
    if ( Atom.isZero(mir) ) {
      return Atom.NO;
    }
    if ( Atom.isZero(wug) ) {
      return Atom.YES;
    }
    Cell c = Cell.require(mir);
    if ( !isSpace(c.head) ) {
      return Atom.NO;
    }
    return re_win_wig_mir(c.tail, HoonMath.dec(wug));
  }
  
  public static Object wash(Object tab, Object edg, Object tac) 
    throws ExitException{
    return re_win_buc(tac, tab, edg, 0L);
  }
}
