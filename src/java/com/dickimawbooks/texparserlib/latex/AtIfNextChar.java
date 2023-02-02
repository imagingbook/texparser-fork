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

import com.dickimawbooks.texparserlib.*;

public class AtIfNextChar extends Command
{
   public AtIfNextChar()
   {
      this("@ifnextchar");
   }

   public AtIfNextChar(String name)
   {
      super(name);
      setShort(false);
   }

   public Object clone()
   {
      return new AtIfNextChar(getName());
   }

   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject obj = stack.popArg(parser);

      String chr = obj.toString(parser);

      TeXObject trueObj = stack.popArg(parser);
      TeXObject falseObj = stack.popArg(parser);

      TeXObject arg = stack.popArg(parser);

      TeXObject result = (arg.equals(obj) ? trueObj : falseObj);

      if (result instanceof Expandable)
      {
         return ((Expandable)result).expandonce(parser, stack);
      }

      return null;
   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      TeXObject obj = parser.popNextArg();

      String chr = obj.toString(parser);

      TeXObject trueObj = parser.popNextArg();
      TeXObject falseObj = parser.popNextArg();

      TeXObject arg = parser.popNextArg();

      TeXObject result = (arg.equals(obj) ? trueObj : falseObj);

      if (result instanceof Expandable)
      {
         return ((Expandable)result).expandonce(parser);
      }

      return null;
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject obj = stack.popArg(parser);

      String chr = obj.toString(parser);

      TeXObject trueObj = stack.popArg(parser);
      TeXObject falseObj = stack.popArg(parser);

      TeXObject arg = stack.popArg(parser);

      TeXObject result = (arg.equals(obj) ? trueObj : falseObj);

      if (result instanceof Expandable)
      {
         return ((Expandable)result).expandfully(parser, stack);
      }

      return null;
   }

   public TeXObjectList expandfully(TeXParser parser)
     throws IOException
   {
      TeXObject obj = parser.popNextArg();

      String chr = obj.toString(parser);

      TeXObject trueObj = parser.popNextArg();
      TeXObject falseObj = parser.popNextArg();

      TeXObject arg = parser.popNextArg();

      TeXObject result = (arg.equals(obj) ? trueObj : falseObj);

      if (result instanceof Expandable)
      {
         return ((Expandable)result).expandfully(parser);
      }

      return null;
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      TeXObject obj = stack.popArg(parser);

      String chr = obj.toString(parser);

      TeXObject trueObj = stack.popArg(parser);
      TeXObject falseObj = stack.popArg(parser);

      TeXObject arg = stack.popArg(parser);

      if (arg.equals(obj))
      {
         trueObj.process(parser);
      }
      else
      {
         falseObj.process(parser);
      }
   }

   public void process(TeXParser parser)
     throws IOException
   {
      TeXObject obj = parser.popNextArg();

      String chr = obj.toString(parser);

      TeXObject trueObj = parser.popNextArg();
      TeXObject falseObj = parser.popNextArg();

      TeXObject arg = parser.popNextArg();

      if (arg.equals(obj))
      {
         trueObj.process(parser);
      }
      else
      {
         falseObj.process(parser);
      }
   }
}
