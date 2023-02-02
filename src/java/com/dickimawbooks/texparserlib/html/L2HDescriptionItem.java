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
package com.dickimawbooks.texparserlib.html;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class L2HDescriptionItem extends DescriptionItem
{
   public L2HDescriptionItem()
   {
      this("descriptionitem");
   }

   public L2HDescriptionItem(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new L2HDescriptionItem(getName());
   }

   public void makelabel(TeXParser parser, TrivListDec trivList, 
     TeXObject label)
    throws IOException
   {
      L2HConverter listener = (L2HConverter)parser.getListener();

      Group grp = listener.createGroup();

      grp.add(new StartElement("dt"));
      grp.add(listener.getControlSequence("descriptionlabel"));
      grp.add(label);
      grp.add(new EndElement("dt"));
      grp.add(new StartElement("<dd>"));

      grp.process(parser);
   }

}
