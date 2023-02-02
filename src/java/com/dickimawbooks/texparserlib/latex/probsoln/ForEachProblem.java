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
package com.dickimawbooks.texparserlib.latex.probsoln;

import java.io.IOException;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.latex.*;

public class ForEachProblem extends ControlSequence
{
   public ForEachProblem(ProbSolnSty sty)
   {
      this("foreachproblem", sty);
   }

   public ForEachProblem(String name, ProbSolnSty sty)
   {
      super(name);
      this.sty = sty;
   }

   public Object clone()
   {
      return new ForEachProblem(getName(), sty);
   }

   public void process(TeXParser parser, TeXObjectList stack)
     throws IOException
   {
      String db = popOptLabelString(parser, stack);

      if (db == null)
      {
         db = "default";
      }

      TeXObject body = popArg(parser, stack);

      ProbSolnDatabase database = sty.getDatabase(db);

      Iterator<String> it = database.keySet().iterator();

      while (it.hasNext())
      {
         String key = it.next();

         TeXObject contents = (TeXObject)body.clone();

         ProbSolnData data = database.get(key);

         ProblemLabel label = new ProblemLabel(data);

         parser.putControlSequence(true, label);

         TeXObjectList useproblem = new TeXObjectList();

         useproblem.add(parser.getListener().getControlSequence("useproblem"));
         useproblem.add(label);

         int numArgs = data.getNumArgs();

         if (numArgs > 0)
         {
            TeXObjectList args = data.getDefaultArgs();

            if (args == null || !sty.useDefaultArgs())
            {
               TeXApp app = parser.getListener().getTeXApp();
               String response;

               if (numArgs == 1)
               {
                  response = app.requestUserInput(app.getMessage(
                    PROBSOLN_REQUEST_ARG, key, db));
               }
               else
               {
                  response = app.requestUserInput(app.getMessage(
                    PROBSOLN_REQUEST_ARGS, key, db, numArgs));
               }

               TeXObjectList list = new TeXObjectList();

               if (response != null)
               {
                  parser.scan(response+"\\relax", list);

                  for (int i = 0; i < numArgs; i++)
                  {
                     useproblem.add(list.popStack(parser, 
                       TeXObjectList.POP_SHORT));
                  }
               }
            }
            else
            {
               useproblem.addAll(args);
            }
         }

         parser.putControlSequence(true, 
           new GenericCommand("thisproblem", null, useproblem));

         if (parser == stack || stack == null)
         {
            contents.process(parser);
         }
         else
         {
            contents.process(parser, stack);
         }
      }

   }

   public void process(TeXParser parser)
     throws IOException
   {
      process(parser, parser);
   }

   private ProbSolnSty sty;

   public final static String PROBSOLN_REQUEST_ARGS = "probsoln.request_args";
   public final static String PROBSOLN_REQUEST_ARG = "probsoln.request_arg";
}
