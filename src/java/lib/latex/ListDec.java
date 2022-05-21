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
import com.dickimawbooks.texparserlib.primitives.RomanNumeral;

public class ListDec extends TrivListDec
{
   public ListDec()
   {
      this("list");
   }

   public ListDec(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ListDec(getName());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getControlSequence("@nmbrlistfalse").process(parser);
      setup(parser, parser.popNextArg(), parser.popNextArg());
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      parser.getListener().getControlSequence("@nmbrlistfalse").process(parser);
      setup(parser, stack.popArg(parser), stack.popArg(parser));
   }

   public void setup(TeXParser parser, TeXObject labelCs, 
     TeXObject listsettings)
   throws IOException
   {
      parser.putControlSequence(true, new GenericCommand(true, "@itemlabel",
         null, labelCs));

      TeXSettings settings = parser.getSettings();

      NumericRegister listdepth = settings.globalAdvanceRegister("@listdepth",
         LaTeXParserListener.ONE);

      ControlSequence cs = parser.getControlSequence(String.format("@list%s",
         RomanNumeral.romannumeral(listdepth.number(parser))));

      if (cs == null)
      {
         cs = parser.getListener().getControlSequence("relax");
      }

      listsettings.process(parser);

      ((LaTeXParserListener)parser.getListener()).startList(this);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXSettings settings = parser.getSettings();

      Register listdepth = settings.globalAdvanceRegister("@listdepth",
         LaTeXParserListener.MINUS_ONE);

      super.end(parser, stack);
   }
}
