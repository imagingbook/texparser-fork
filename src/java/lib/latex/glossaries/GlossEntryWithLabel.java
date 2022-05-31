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
package com.dickimawbooks.texparserlib.latex.glossaries;

import java.io.IOException;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class GlossEntryWithLabel extends AbstractGlsCommand
{
   public GlossEntryWithLabel(GlossariesSty sty)
   {
      this("glossentry", sty);
   }

   public GlossEntryWithLabel(String name, GlossariesSty sty)
   {
      super(name, sty);
   }

   public Object clone()
   {
      return new GlossEntryWithLabel(getName(), getSty());
   }

   @Override
   public TeXObjectList expandonce(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      GlsLabel glslabel = popEntryLabel(parser, stack);

      listener.putControlSequence(glslabel.duplicate("glscurrententrylabel"));

      TeXObjectList list = listener.createStack();

      list.add(listener.getControlSequence("gls@org@glossaryentryfield"));
      list.add(glslabel);

      Group grp = listener.createGroup();
      list.add(grp);
      grp.add(popArg(parser, stack));

      return list;
   }

   @Override
   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      GlsLabel glslabel = popEntryLabel(parser, stack);

      listener.putControlSequence(glslabel.duplicate("glscurrententrylabel"));

      stack.push(glslabel);

      ControlSequence cs =
        listener.getControlSequence("gls@org@glossaryentryfield");

      if (stack == parser || stack == null)
      {
         cs.process(parser);
      }
      else
      {
         cs.process(parser, stack);
      }
   }

   @Override
   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }
}
