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
package com.dickimawbooks.texparserlib.primitives;

import java.io.IOException;
import java.io.EOFException;

import com.dickimawbooks.texparserlib.*;

public class Unexpanded extends Primitive implements Expandable
{
   public Unexpanded()
   {
      this("unexpanded");
   }

   public Unexpanded(String name)
   {
      super(name);
   }

   public Object clone()
   {
      return new Unexpanded(getName());
   }

   @Override
   public boolean canExpand()
   {
      return true;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObject arg = parser.popNextArg();

      if (parser.isStack(arg)) return (TeXObjectList)arg;

      TeXObjectList list = parser.getListener().createStack();
      list.add(arg);

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject arg = stack.popArg(parser);

      if (parser.isStack(arg)) return (TeXObjectList)arg;

      TeXObjectList list = parser.getListener().createStack();
      list.add(arg);

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      return expandonce(parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      return expandonce(parser, stack);
   }

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      TeXParserUtils.process(popArg(parser, stack), parser, stack);
   }

   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }
}
