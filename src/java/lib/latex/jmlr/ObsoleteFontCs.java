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
package com.dickimawbooks.texparserlib.latex.jmlr;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ObsoleteFontCs extends Declaration
{
   public ObsoleteFontCs()
   {
      this("obsoletefontcs");
   }

   public ObsoleteFontCs(String name)
   {
      super(name);
   }

   @Override
   public Object clone()
   {
      return new ObsoleteFontCs(getName());
   }

   @Override
   public boolean isModeSwitcher()
   {
      return false;
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      String obsname = popLabelString(parser, stack);

      TeXObjectList list = new TeXObjectList();

      list.add(parser.getListener().getControlSequence(obsname));

      return list;
   }

   public TeXObjectList expandonce(TeXParser parser)
      throws IOException
   {
      return expandonce(parser, parser);
   }

   public TeXObjectList expandfully(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      String obsname = popLabelString(parser, stack);

      TeXObjectList list = null;

      ControlSequence cs = parser.getListener().getControlSequence(obsname);

      if (cs instanceof Expandable)
      {
         list = ((Expandable)cs).expandfully(parser, stack);
      }

      if (list == null)
      {
         list = new TeXObjectList();
         list.add(cs);
      }

      return list;
   }

   @Override
   public TeXObjectList expandfully(TeXParser parser)
      throws IOException
   {
      String obsname = popLabelString(parser, parser);

      TeXObjectList list = null;

      ControlSequence cs = parser.getListener().getControlSequence(obsname);

      if (cs instanceof Expandable)
      {
         list = ((Expandable)cs).expandfully(parser);
      }

      if (list == null)
      {
         list = new TeXObjectList();
         list.add(cs);
      }

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      String obsname = popLabelString(parser, stack);

      ControlSequence cs = parser.getListener().getControlSequence(obsname);

      stack.push(cs);
   }

   @Override
   public void process(TeXParser parser)
      throws IOException
   {
      process(parser, parser);
   }

   @Override
   public void end(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
   }
}
