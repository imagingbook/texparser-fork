/*
    Copyright (C) 2013-2022 Nicola L.C. Talbot
    www.dickimaw-books.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.dickimawbooks.texparserlib.latex;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;

public class ProcessOptions extends ControlSequence
{
   public ProcessOptions()
   {
      this("ProcessOptions");
   }

   public ProcessOptions(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new ProcessOptions(getName());
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      boolean isStar = false;
      byte popStyle = TeXObjectList.POP_IGNORE_LEADING_SPACE;

      TeXObject arg = stack.peekStack(popStyle);

      if (arg instanceof CharObject
           && ((CharObject)arg).getCharCode() == (int)'*')
      {
         if (parser == stack)
         {
            arg = parser.popStack(popStyle);
         }
         else
         {
            arg = stack.popStack(parser, popStyle);
         }

         isStar = true;// not implemented
      }

      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      LaTeXFile sty = listener.getCurrentSty();

      sty.processOptions(stack);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
