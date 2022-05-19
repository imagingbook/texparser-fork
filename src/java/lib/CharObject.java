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
package com.dickimawbooks.texparserlib;

import java.io.IOException;

public abstract class CharObject implements TeXObject
{
   public CharObject(int charCode)
   {
      setCharCode(charCode);
   }

   public boolean equals(Object obj)
   {
      if (this == obj) return true;

      if (!(obj instanceof CharObject) || obj == null) return false;
      return ((CharObject)obj).getCharCode() == getCharCode();
   }

   public abstract Object clone();

   public String toString(TeXParser parser)
   {
      return parser.getSettings().getCharString(charCode);
   }

   public String toString()
   {
      return String.format("%s[%s]", getClass().getSimpleName(), 
        format());
   }

   public String format()
   {
      return new String(Character.toChars(getCharCode()));
   }

   public TeXObjectList string(TeXParser parser)
     throws IOException
   {
      TeXObjectList list = new TeXObjectList();
      list.add(this);
      return list;
   }

   public int getCharCode()
   {
      return charCode;
   }

   public void setCharCode(int charCode)
   {
      this.charCode = charCode;
   }

   public void process(TeXParser parser) throws IOException
   {
      parser.getListener().getWriteable().write(toString(parser));
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      process(parser);
   }

   public boolean isPar()
   {
      return false;
   }

   public boolean isEmpty()
   {
      return false;
   }

   protected int charCode;
}
