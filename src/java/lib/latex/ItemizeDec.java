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

public class ItemizeDec extends ListDec
{
   public ItemizeDec()
   {
      this("itemize");
   }

   public ItemizeDec(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ItemizeDec(getName());
   }

   @Override
   public void process(TeXParser parser) throws IOException
   {
      setup(parser);
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack) throws IOException
   {
      setup(parser);
   }

   @Override
   public void setup(TeXParser parser) throws IOException
   {
      super.setup(parser);

      TeXSettings settings = parser.getSettings();

      NumericRegister itemdepth = settings.globalAdvanceRegister("@itemdepth",
         LaTeXParserListener.ONE);

      String labelitem = "labelitem"
        +RomanNumeral.romannumeral(itemdepth.number(parser));

      parser.putControlSequence(true, new GenericCommand(true, 
          "@itemitem", null,
          parser.getListener().createString(labelitem))
      );

      ControlSequence labelCs = parser.getControlSequence(labelitem);

      if (labelCs == null)
      {
         labelCs = parser.getListener().getControlSequence("relax");
      }

      TeXObjectList listsettings = new TeXObjectList();

      setup(parser, labelCs, listsettings);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXSettings settings = parser.getSettings();

      Register enumdepth = settings.globalAdvanceRegister("@itemdepth",
         LaTeXParserListener.MINUS_ONE);

      super.end(parser, stack);
   }

}
