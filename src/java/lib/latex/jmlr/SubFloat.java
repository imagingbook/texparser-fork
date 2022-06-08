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
import com.dickimawbooks.texparserlib.primitives.IfTrue;

public class SubFloat extends FrameBox
{
   public SubFloat(String floatname)
   {
      this("sub"+floatname, floatname);
   }

   public SubFloat(String name, String floatname)
   {
      super(name, BorderStyle.NONE, AlignHStyle.DEFAULT, AlignVStyle.DEFAULT, true, true,
        null, null);
      this.floatname = floatname;
   }

   @Override
   public FrameBox createBox()
   {
      return new SubFloat(getName(), floatname);
   }

   protected void popSettings(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      caption = popOptArg(parser, stack);
      String pos = popOptLabelString(parser, stack);

      if (pos != null)
      {
         pos = pos.trim();

         if (pos.equals("c"))
         {
            valign = AlignVStyle.MIDDLE;
         }
         else if (pos.equals("t"))
         {
            valign = AlignVStyle.TOP;
         }
         else if (pos.equals("b"))
         {
            valign = AlignVStyle.BOTTOM;
         }
         else
         {
            TeXApp texApp = parser.getListener().getTeXApp();

            texApp.warning(parser, texApp.getMessage(
              LaTeXSyntaxException.ILLEGAL_ARG_TYPE, pos));
         }
      }
   }

   protected TeXObject popContents(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      LaTeXParserListener listener = (LaTeXParserListener)parser.getListener();

      TeXObject arg = popArg(parser, stack);

      TeXObjectList list = new TeXObjectList();

      ControlSequence cs = parser.getControlSequence(
         String.format("if%scaptiontop", floatname));

      boolean top=false;

      if (cs instanceof IfTrue)
      {
         top = true;
      }

      // Does arg contain \label ?

      TeXObject label = null;
      TeXObject contents;

      if (arg instanceof TeXObjectList)
      {
         contents = new TeXObjectList();

         TeXObject obj = ((TeXObjectList)arg).popStack(parser);

         while (obj != null)
         {
            if (obj instanceof TeXCsRef)
            {
               obj = listener.getControlSequence(((TeXCsRef)obj).getName());
            }

            if (obj instanceof Label)
            {
               label = ((TeXObjectList)arg).popArg(parser);

               if (label instanceof Expandable)
               {
                  TeXObjectList expanded = 
                    ((Expandable)label).expandfully(parser, 
                        (TeXObjectList)arg);

                  if (expanded != null)
                  {
                     label = expanded;
                  }
               }
            }
            else
            {
               ((TeXObjectList)contents).add(obj);
            }

            obj = ((TeXObjectList)arg).popStack(parser);
         }
      }
      else
      {
         contents = arg;
      }

      if (label != null)
      {
         list.add(listener.getAnchor(label.toString(parser)));
      }

      listener.stepcounter(getName());

      Group capGrp = listener.createGroup();

      capGrp.add(new TeXCsRef(getName()+"label"));
      capGrp.add(new TeXCsRef("the"+getName()));

      if (caption != null)
      {
         capGrp.add(listener.getSpace());
         capGrp.add(caption);
      }

      if (top)
      {
         list.add(capGrp);
         list.add(new TeXCsRef("medskip"));
      }

      list.add(contents);

      if (!top)
      {
         list.add(new TeXCsRef("medskip"));
         list.add(capGrp);
      }

      return list;
   }

   protected String floatname;

   private TeXObject caption = null;
}
