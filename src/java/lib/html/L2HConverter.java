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
package com.dickimawbooks.texparserlib.html;

import java.io.*;
import java.nio.file.Path;

import com.dickimawbooks.texparserlib.*;
import com.dickimawbooks.texparserlib.generic.*;
import com.dickimawbooks.texparserlib.latex.*;

public abstract class L2HConverter extends LaTeXParserListener
   implements Writeable
{
   public L2HConverter(TeXApp app)
   {
      this(app, null);
   }

   public L2HConverter(TeXApp app, File outDir)
   {
      super(null);
      this.texApp = app;
      this.outPath = (outDir == null ? null : outDir.toPath());

      setWriteable(this);
   }

   protected void addPredefined()
   {
      super.addPredefined();
      putControlSequence(new L2HCr("\\"));
      putControlSequence(new L2HAmp());
      putControlSequence(new L2HNoBreakSpace());

      parser.putControlSequence(new L2LMathDeclaration("math"));

      MathDeclaration begMathDecl = new L2LMathDeclaration("(");
      parser.putControlSequence(begMathDecl);
      parser.putControlSequence(new EndDeclaration(")", begMathDecl));
      parser.putControlSequence(
         new L2LMathDeclaration("displaymath", TeXSettings.MODE_DISPLAY_MATH));

      MathDeclaration begDispDecl = new L2LMathDeclaration("[", TeXSettings.MODE_DISPLAY_MATH);

      parser.putControlSequence(begDispDecl);
      parser.putControlSequence(new EndDeclaration("]", begDispDecl));
      parser.putControlSequence(
         new L2LMathDeclaration("equation", TeXSettings.MODE_DISPLAY_MATH, true));

      try
      {
         LaTeXSty sty = getLaTeXSty("hyperref");

         if (sty != null)
         {
            sty.load(this, parser, null);
         }
      }
      catch (IOException e)
      {
      }
   }

   public void setWriter(Writer writer)
   {
      this.writer = writer;
   }

   public Writer getWriter()
   {
      return writer;
   }

   public String getStyle()
   {
      String style = "";

      if (parser != null)
      {
         TeXSettings settings = parser.getSettings();

         switch (settings.getCurrentFontFamily())
         {
            case TeXSettings.FAMILY_RM:
               style = "font-family: serif; ";
               break;
            case TeXSettings.FAMILY_SF:
               style = "font-family: sans-serif; ";
               break;
            case TeXSettings.FAMILY_TT:
               style = "font-family: monospace; ";
               break;
         }

         switch (settings.getCurrentFontShape())
         {
            case TeXSettings.SHAPE_UP:
               style += "font-style: normal; font-variant: normal; ";
               break;
            case TeXSettings.SHAPE_IT:
               style += "font-style: italic; font-variant: normal; ";
               break;
            case TeXSettings.SHAPE_SL:
               style += "font-style: oblique; font-variant: normal; ";
               break;
            case TeXSettings.SHAPE_EM:
               TeXSettings parent = settings.getParent();

               if (parent != null)
               {
                  int parentStyle = parent.getFontShape();

                  if (parentStyle == TeXSettings.SHAPE_UP
                    ||parentStyle == TeXSettings.INHERIT)
                  {
                     if (settings.getFontFamily() == TeXSettings.FAMILY_SF)
                     {
                        style += "font-style: oblique; ";
                     }
                     else
                     {
                        style += "font-style: italic; ";
                     }
                  }
                  else
                  {
                     style += "font-style: normal; ";
                  }
               }
               else
               {
                  if (settings.getFontFamily() == TeXSettings.FAMILY_SF)
                  {
                     style += "font-style: oblique; ";
                  }
                  else
                  {
                     style += "font-style: italic; ";
                  }
               }

               style += "font-variant: normal; ";

               break;
            case TeXSettings.SHAPE_SC:
               style += "font-style: normal; font-variant: small-caps; ";
               break;
         }

         switch (settings.getCurrentFontWeight())
         {
            case TeXSettings.WEIGHT_MD:
               style += "font-weight: normal; ";
               break;
            case TeXSettings.WEIGHT_BF:
               style += "font-weight: bold; ";
               break;
         }
      }

      return style;
   }

   public void writeCodePoint(int codePoint)
     throws IOException
   {
      if (writer == null) return;

      String style = getStyle();

      if (!style.isEmpty())
      {
         writer.write("<span style=\""+style+"\">");
      }

      if (codePoint >= 32 && codePoint <= 126)
      {
         writer.write((char)codePoint);
      }
      else
      {
         writer.write("&#x"+Integer.toHexString(codePoint)+";");
      }

      if (!style.isEmpty())
      {
         writer.write("</span>");
      }
   }

   public void write(String str)
     throws IOException
   {
      if (writer == null) return;

      String style = getStyle();

      if (!style.isEmpty())
      {
         writer.write("<span style=\""+style+"\">");
      }

      writer.write(str);

      if (!style.isEmpty())
      {
         writer.write("</span>");
      }
   }

   public void write(char c)
     throws IOException
   {
      if (writer == null) return;

      write(""+c);
   }

   public void writeln(String str)
     throws IOException
   {
      if (writer == null) return;

      write(String.format("%s%n", str));
   }

   public void href(String url, TeXObject text)
     throws IOException
   {
      if (writer == null) return;

      writer.write("<a href=\""+url+"\">");

      text.process(parser);

      writer.write("</a>");
   }

   public void substituting(String original, String replacement)
   {
      texApp.substituting(parser.getLineNumber(), original, replacement);
   }

   public void skipping(Ignoreable ignoreable)
     throws IOException
   {
   }

   public boolean useMathJax()
   {
      return useMathJax;
   }

   public void setUseMathJax(boolean useMathJax)
   {
      this.useMathJax = useMathJax;
   }

   public void writeMathJaxHeader()
     throws IOException
   {
      setUseMathJax(true);

      writeable.writeln("<!-- MathJax -->");
      writeable.writeln("<script type=\"text/x-mathjax-config\">");
      writeable.writeln("MathJax.Hub.Config({tex2jax: { inlineMath: [['$','$'],['\\\\(','\\\\)']] }});");
      writeable.writeln("</script>");

      writeable.write("<script type=\"text/javascript\" src=");
      writeable.writeln(
       "\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\">");
      writeable.writeln("</script>");
   }

   public void overwithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXObject before, TeXObject after)
    throws IOException
   {
      if (useMathJax)
      {
         if (firstDelim != null || secondDelim != null)
         {
            write("\\left");
            write(firstDelim==null?".":firstDelim.toString(getParser()));
         }

         write("\\frac{");
         write(before.toString(getParser()));
         write("}{");
         write(after.toString(getParser()));
         write("}");

         if (firstDelim != null || secondDelim != null)
         {
            write("\\right");
            write(secondDelim==null?".":secondDelim.toString(getParser()));
         }

         return;
      }

      if (firstDelim != null)
      {
        firstDelim.process(parser);
      }

      write("<table style=\"display: inline;\"><tr style=\"border-bottom-style: solid;\"><td>");
      before.process(parser);
      write("</td></tr>");
      write("<tr><td>");
      after.process(parser);
      write("</td></tr><table>");

      if (secondDelim != null)
      {
         secondDelim.process(parser);
      }
   }

   public void abovewithdelims(TeXObject firstDelim,
     TeXObject secondDelim, TeXDimension thickness,
     TeXObject before, TeXObject after)
    throws IOException
   {
      if (useMathJax())
      {
         write(before.toString(getParser()));
         write("\\abovewithdelims ");
         write(firstDelim==null?".":firstDelim.toString(getParser()));
         write(secondDelim==null?".":secondDelim.toString(getParser()));
         write(thickness.toString(getParser()));

         write(after.toString(getParser()));

         return;
      }

      if (firstDelim != null)
      {
         firstDelim.process(parser);
      }

      write("<table><tr><td>");
      before.process(parser);
      write("</td></tr>");
      write("<tr><td>");
      after.process(parser);
      write("</td></tr><table>");

      if (secondDelim != null)
      {
         secondDelim.process(parser);
      }
   }

   public void subscript(TeXObject arg)
    throws IOException
   {
      if (useMathJax())
      {
         write("_{"+arg.toString(getParser())+"}");
      }
      else
      {
         write("<sb>");
         arg.process(parser);
         write("</sb>");
      }
   }

   public void superscript(TeXObject arg)
    throws IOException
   {
      if (useMathJax())
      {
         write("^{"+arg.toString(getParser())+"}");
      }
      else
      {
         write("<sp>");
         arg.process(parser);
         write("</sp>");
      }
   }

   public void tab()
     throws IOException
   {
      // TODO
   }

   public void par() throws IOException
   {
      write("<p>");
   }

   public void verb(boolean isStar, char delim,
     String text)
    throws IOException
   {
      write("<tt>"+text+"</tt>");
   }

   public void includegraphics(KeyValList options, String file)
     throws IOException
   {
      // This doesn't take the options or file format into account.
      write(String.format("<img src=\"%s\"/>", file));
   }

   public TeXApp getTeXApp()
   {
      return texApp;
   }

   public void endParse(File file)
    throws IOException
   {
      this.file = null;
   }

   public void beginParse(File file)
    throws IOException
   {
      this.file = file;
   }

   public File getFile()
   {
      return file;
   }

   private Writer writer;

   private TeXApp texApp;

   private File file;

   private Path outPath;

   private boolean useMathJax=true;
}
