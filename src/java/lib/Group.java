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

public class Group extends TeXObjectList
{
   public Group()
   {
      super();
   }

   public Group(int capacity)
   {
      super(capacity);
   }

   public Group(TeXParserListener listener, String text)
   {
      super(listener, text);
   }

   public TeXObjectList toList()
   {
      TeXObjectList list = new TeXObjectList(size());

      list.addAll(this);

      return list;
   }

   public TeXObjectList createList()
   {
      return new Group(capacity());
   }

   // stack is outside this group
   // TODO: Check for \\expandafter at the end of this

   public void process(TeXParser parser, TeXObjectList stack)
      throws IOException
   {
      parser.startGroup();

      processList(parser, stack);

      parser.endGroup();
   }

   public void process(TeXParser parser)
    throws IOException
   {
      process(parser, parser);
   }

   protected void processList(TeXParser parser, TeXObjectList stack)
    throws IOException
   {
      TeXObjectList before = new TeXObjectList();
      TeXObjectList after = new TeXObjectList();

      MidControlSequence midcs = null;

      for (int i = 0; i < size(); i++)
      {
         TeXObject object = get(i);

         if (object instanceof TeXCsRef)
         {
            object = parser.getListener().getControlSequence(
               ((TeXCsRef)object).getName());
         }

         if (object == null)
         {
            break;
         }

         if (object instanceof MidControlSequence)
         {
            midcs = (MidControlSequence)object;
            continue;
         }

         if (midcs == null)
         {
            before.add(object);
         }
         else
         {
            after.add(object);
         }
      }

      if (midcs == null)
      {
         before = null;
         after = null;

         while (size() != 0)
         {
            TeXObject object = remove(0);

            if (object == null)
            {
               break;
            }

            if (object instanceof TeXCsRef)
            {
               object = parser.getListener().getControlSequence(
                  ((TeXCsRef)object).getName());
            }

            if (object instanceof Declaration)
            {
               pushDeclaration((Declaration)object);
            }

            if (size() == 0 && stack != parser)
            {
               object.process(parser, stack);
            }
            else
            {
               object.process(parser, this);
            }
         }
      }
      else
      {
         clear();
         midcs.process(parser, before, after);
      }

      processEndDeclarations(parser);

   }

   public TeXObjectList expandonce(TeXParser parser)
     throws IOException
   {
      return expandonce(parser, null);
   }

   public TeXObjectList expandonce(TeXParser parser, 
        TeXObjectList stack)
     throws IOException
   {
      TeXObjectList list = createList();

      TeXObjectList remaining = (TeXObjectList)clone();

      if (stack != null)
      {
         while (stack.size() > 0)
         {
            remaining.add(stack.remove(0));
         }
      }

      while (remaining.size() > 0)
      {
         TeXObject object = remaining.remove(0);

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandonce(parser,
                remaining);

            if (expanded == null)
            {
               list.add(object);
            }
            else if (expanded instanceof Group)
            {
               list.add(expanded);
            }
            else
            {
               list.addAll(expanded);
            }
         }
         else
         {
            list.add(object);
         }
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser) throws IOException
   {
      TeXObjectList list = createList();

      TeXObjectList remaining = (TeXObjectList)clone();

      while (remaining.size() > 0)
      {
         TeXObject object = remaining.popStack();

         if (object instanceof Expandable)
         {
            TeXObjectList expanded = ((Expandable)object).expandfully(parser,
                remaining);

            if (expanded == null)
            {
               list.add(object);
            }
            else if (expanded instanceof Group)
            {
               list.add(expanded);
            }
            else
            {
               list.addAll(expanded);
            }
         }
         else
         {
            list.add(object);
         }
      }

      return list;
   }

   public TeXObjectList expandfully(TeXParser parser,
        TeXObjectList stack) throws IOException
   {
      return expandfully(parser);
   }

   public String toString(TeXParser parser)
   {
      return ""+parser.getBgChar()+super.toString(parser)+parser.getEgChar();
   }

   public String toString()
   {
      return "{"+super.toString()+"}";
   }

}

