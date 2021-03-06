/*
 * Jatha - a Common LISP-compatible LISP library in Java.
 * Copyright (C) 1997-2005 Micheal Scott Hewett
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * For further information, please contact Micheal Hewett at
 *   hewett@cs.stanford.edu
 *
 */

package org.jatha.compile;

import org.jatha.Jatha;
import org.jatha.dynatype.*;
import org.jatha.machine.*;


public class QuotePrimitive extends LispPrimitive
{
  public QuotePrimitive(Jatha lisp)
  {
    super(lisp, "QUOTE", 1);

    inlineP = true;
  }

  public void Execute(SECDMachine machine)
  {
    System.err.println("QUOTE was compiled - shouldn't have been.");
    // do nothing
    machine.C.pop();
  }

  public LispValue CompileArgs (LispCompiler compiler, SECDMachine machine, LispValue args,
				LispValue valueList, LispValue code)
  {
    // Don't evaluate the arg. (load it as a constant)
    return f_lisp.makeCons(machine.LDC,
                           f_lisp.makeCons(args.first(), code));
  }
}

