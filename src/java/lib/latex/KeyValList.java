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
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;

public class KeyValList extends HashMap<String,TeXObject>
  implements TeXObject
{
   public KeyValList()
   {
      super();
   }

   
   public static KeyValList getList(TeXParser parser, TeXObject object)
     throws IOException
   {
      KeyValList keyValList = new KeyValList();

      if (object instanceof TeXObjectList)
      {
         TeXObjectList list = (TeXObjectList)object;

         StringBuilder keyBuilder = new StringBuilder();
         TeXObjectList valBuilder = null;

         while (list.size() > 0)
         {
            TeXObject obj = list.remove(0);

            if ((obj instanceof Space || obj instanceof Eol 
                || obj instanceof Ignoreable)
             && (keyBuilder.length() == 0 
                  || (valBuilder != null && valBuilder.size() == 0)))
            {
               continue;
            }

            if (obj instanceof BgChar)
            {
               obj.process(parser, list);
               obj = list.remove(0);
            }

            if (obj instanceof CharObject)
            {
               int charCode = ((CharObject)obj).getCharCode();

               if (valBuilder == null)
               {
                  // Still building the key

                  if (charCode == (int)'=')
                  {
                     valBuilder = new TeXObjectList();
                     continue;
                  }
                  else if (charCode == (int)',')
                  {
                     String key = keyBuilder.toString().trim();

                     // Don't allow empty keys

                     if (!key.isEmpty())
                     {
                        keyValList.put(key, new TeXObjectList());
                     }

                     keyBuilder.setLength(0);
                     continue;
                  }
               }
               else if (charCode == ',') // building the value
               {
                  String key = keyBuilder.toString().trim();

                  // Don't allow empty keys

                  if (!key.isEmpty())
                  {
                     keyValList.put(key, valBuilder);
                  }

                  keyBuilder.setLength(0);
                  valBuilder = null;
                  continue;
               }
            }

            if (valBuilder == null)
            {
               TeXObjectList expanded = null;

               if (obj instanceof Expandable)
               {
                  expanded = ((Expandable)obj).expandfully(parser, list);
               }

               if (expanded == null)
               {
                  keyBuilder.append(obj.toString(parser));
               }
               else
               {
                  keyBuilder.append(expanded.toString(parser));
               }
            }
            else
            {
               valBuilder.add(obj);
            }
         }

         String key = keyBuilder.toString().trim();

         if (!key.isEmpty())
         {
            keyValList.put(key, valBuilder == null ? 
             new TeXObjectList() : valBuilder);
         }
      }
      else
      {
         String key = object.toString(parser).trim();

         if (!key.isEmpty())
         {
            keyValList.put(key, new TeXObjectList());
         }
      }

      return keyValList;
   }

   public TeXObject getValue(String key)
   {
      TeXObject value = get(key);

      if (value == null || !(value instanceof TeXObjectList))
      {
         return value;
      }

      TeXObjectList list = (TeXObjectList)value;

      int n = list.size();

      if (n == 0) return value;

      if (n == 1)
      {
         value = list.get(0);

         if (value instanceof Group
           &&!(value instanceof MathGroup))
         {
            return ((Group)value).toList();
         }
         else
         {
            return value;
         }
      }

      int start = 0;
      int end = n-1;

      for (int i = 0; i < n; i++)
      {
         TeXObject obj = list.get(i);

         if (!(obj instanceof Ignoreable))
         {
            start = i;
            break;
         }
      }

      for (int i = end; i >= 0; i--)
      {
         TeXObject obj = list.get(i);

         if (!(obj instanceof Ignoreable))
         {
            end = i;
            break;
         }
      }

      if (start == end)
      {
         value = list.get(start);

         if (value instanceof Group
          &&!(value instanceof MathGroup))
         {
            return ((Group)value).toList();
         }
         else
         {
            return value;
         }
      }

      TeXObjectList valList = new TeXObjectList();

      for (int i = start; i <= end; i++)
      {
         valList.add(list.get(i));
      }

      return valList;
   }

   public TeXObjectList string(TeXParser parser) throws IOException
   {
      return new TeXObjectList();
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      process(parser);
   }

   public void process(TeXParser parser)
     throws IOException
   {
      Set<String> keySet = keySet();

      Iterator<String> it = keySet.iterator();

      Writeable writeable = parser.getListener().getWriteable();

      while (it.hasNext())
      {
         String key = it.next();

         writeable.write(key);
         TeXObject value = get(key);

         if (value != null)
         {
            writeable.write('=');
            writeable.writeCodePoint(parser.getBgChar());

            value.process(parser);

            writeable.writeCodePoint(parser.getEgChar());
         }

         if (it.hasNext())
         {
            writeable.write(',');
         }
      }
   }

   public String toString(TeXParser parser)
   {
      StringBuilder builder = new StringBuilder();

      Set<String> keySet = keySet();

      Iterator<String> it = keySet.iterator();

      while (it.hasNext())
      {
         String key = it.next();

         builder.append(key);
         TeXObject value = get(key);

         if (value instanceof TeXObjectList
          && ((TeXObjectList)value).isEmpty())
         {
            value = null;
         }

         if (value != null)
         {
            builder.append('=');

            String strVal = value.toString(parser);

            if (strVal.matches(".*[^\\w].*"))
            {
               builder.appendCodePoint(parser.getBgChar());
               builder.append(strVal);
               builder.appendCodePoint(parser.getEgChar());
            }
            else
            {
               builder.append(strVal);
            }
         }

         if (it.hasNext())
         {
            builder.append(',');
         }
      }

      return builder.toString();
   }

   public String format()
   {
      StringBuilder builder = new StringBuilder();

      Set<String> keySet = keySet();

      Iterator<String> it = keySet.iterator();

      while (it.hasNext())
      {
         String key = it.next();

         builder.append(key);
         TeXObject value = get(key);

         if (value instanceof TeXObjectList
          && ((TeXObjectList)value).isEmpty())
         {
            value = null;
         }

         if (value != null)
         {
            builder.append('=');

            String strVal = value.toString();

            if (strVal.matches(".*[^\\w].*"))
            {
               builder.append("{");
               builder.append(strVal);
               builder.append("}");
            }
            else
            {
               builder.append(strVal);
            }
         }

         if (it.hasNext())
         {
            builder.append(',');
         }
      }

      return builder.toString();
   }

   public boolean isPar()
   {
      return false;
   }

}
