/*
    Copyright (C) 2022 Nicola L.C. Talbot
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

import com.dickimawbooks.texparserlib.*;

/*
 * A command that simply expands to a given unformatted string. For example,
 * a command used to a store label. If the label references an
 * object, the object can also be stored to save repeatedly looking
 * it up.
 */
public class IntegerContentCommand extends TextualContentCommand implements TeXNumber
{
   public IntegerContentCommand(String name, int num)
   {
      this(name, ""+num, new UserNumber(num));
   }

   protected IntegerContentCommand(String name, String text, UserNumber num)
   {
      super(name, text, num);
   }

   @Override
   public Object clone()
   {
      return new IntegerContentCommand(getName(), getText(), getNumber());
   }

   @Override
   public TextualContentCommand duplicate(String newcsname)
   {
      TextualContentCommand copy = (TextualContentCommand)clone();
      copy.name = newcsname;
      copy.data = new UserNumber(getValue());
      return copy;
   }

   @Override
   public int getValue()
   {
      return getNumber().getValue();
   }

   public void setValue(int val)
   {
      text = ""+val;
      getNumber().setValue(val);
   }

   public UserNumber getNumber()
   {
      return (UserNumber)data;
   }

   @Override
   public int number(TeXParser parser) throws TeXSyntaxException
   {
      return getNumber().number(parser);
   }

   @Override
   public void multiply(int factor)
   {
      getNumber().multiply(factor);
      text = ""+getValue();
   }

   @Override
   public void divide(int divisor)
   {
      getNumber().divide(divisor);
      text = ""+getValue();
   }

   @Override
   public void advance(TeXParser parser, Numerical increment)
    throws TeXSyntaxException
   {
      getNumber().advance(parser, increment);
      text = ""+getValue();
   }

}
