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
package com.dickimawbooks.texparserlib.latex.datatool;

import java.io.IOException;
import java.util.Vector;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class DataIntElement extends UserNumber implements DataNumericElement
{
   public DataIntElement(DataToolSty sty)
   {
      this(sty, 0);
   }

   public DataIntElement(DataToolSty sty, int value)
   {
      super(value);
      this.sty = sty;
   }

   public DataIntElement(DataToolSty sty, TeXNumber num)
   {
      super(num.getValue());
      this.sty = sty;
   }

   @Override
   public Object clone()
   {
      return new DataIntElement(sty, getValue());
   }

   public byte getDataType()
   {
      return DataToolHeader.TYPE_INT;
   }

   @Override
   public double doubleValue()
   {
      return (double)intValue();
   }

   @Override
   public float floatValue()
   {
      return (float)intValue();
   }

   @Override
   public int intValue()
   {
      return getValue();
   }

   @Override
   public String format()
   {
      return String.format("%d", getValue());
   }

   protected DataToolSty sty;
}
