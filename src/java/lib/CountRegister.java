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
import java.util.Vector;

public class CountRegister extends Register implements TeXNumber
{
   public CountRegister(String name)
   {
      this(name, 0);
   }

   public CountRegister(String name, int value)
   {
      this.name = name;
      setValue(value);
   }

   public String getName()
   {
      return name;
   }

   public void setValue(int value)
   {
      this.value = value;
   }

   public int getValue()
   {
      return value;
   }

   public int number(TeXParser parser)
   {
      return value;
   }

   public void advance()
   {
      advance(1);
   }

   public void advance(int increment)
   {
      value += increment;
   }

   public void divide(int divisor)
   {
      value /= divisor;
   }

   public void multiply(int factor)
   {
      value *= factor;
   }

   public void process(TeXParser parser)
      throws IOException
   {
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
   }

   public Object clone()
   {
      return new CountRegister(getName(), value);
   }

   private String name;

   private int value = 0;
}
