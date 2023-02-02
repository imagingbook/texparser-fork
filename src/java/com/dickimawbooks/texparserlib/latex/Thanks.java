/*
    Copyright (C) 2013 Nicola L.C. Talbot
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
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;

public class Thanks extends Footnote
{
   public Thanks()
   {
      this("thanks");
   }

   public Thanks(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Thanks(getName());
   }

   public String getTargetName(TeXParser parser, String counter, TeXObject thempfn)
   throws IOException
   {
      TeXObject thehcounter = parser.getControlSequence("theH"+counter);

      if (thehcounter == null)
      {
         return "thanks."
          +((LaTeXParserListener)parser.getListener()).getcountervalue(counter);
      }

      if (thehcounter instanceof Expandable)
      {
         TeXObjectList expanded = ((Expandable)thehcounter).expandfully(parser);

         if (expanded != null)
         {
            thehcounter = expanded;
         }
      }

      return "thanks."+thehcounter.toString(parser);
   }

}
