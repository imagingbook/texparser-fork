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
package wilbur.lib;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import com.dickimawbooks.texparserlib.ActiveChar;
import com.dickimawbooks.texparserlib.Comment;
import com.dickimawbooks.texparserlib.ControlSequence;
import com.dickimawbooks.texparserlib.DoubleParam;
import com.dickimawbooks.texparserlib.EndDeclaration;
import com.dickimawbooks.texparserlib.Eol;
import com.dickimawbooks.texparserlib.FontEncoding;
import com.dickimawbooks.texparserlib.Group;
import com.dickimawbooks.texparserlib.Ignoreable;
import com.dickimawbooks.texparserlib.MathGroup;
import com.dickimawbooks.texparserlib.Other;
import com.dickimawbooks.texparserlib.Par;
import com.dickimawbooks.texparserlib.Param;
import com.dickimawbooks.texparserlib.ParameterToken;
import com.dickimawbooks.texparserlib.SkippedEols;
import com.dickimawbooks.texparserlib.SkippedSpaces;
import com.dickimawbooks.texparserlib.Space;
import com.dickimawbooks.texparserlib.Tab;
import com.dickimawbooks.texparserlib.TeXApp;
import com.dickimawbooks.texparserlib.TeXDimension;
import com.dickimawbooks.texparserlib.TeXObject;
import com.dickimawbooks.texparserlib.TeXObjectList;
import com.dickimawbooks.texparserlib.TeXPath;
import com.dickimawbooks.texparserlib.TeXReader;
import com.dickimawbooks.texparserlib.TeXSettings;
import com.dickimawbooks.texparserlib.Writeable;
import com.dickimawbooks.texparserlib.generic.BigOperator;
import com.dickimawbooks.texparserlib.generic.BinarySymbol;
import com.dickimawbooks.texparserlib.generic.GreekSymbol;
import com.dickimawbooks.texparserlib.generic.MathSymbol;
import com.dickimawbooks.texparserlib.generic.Symbol;
import com.dickimawbooks.texparserlib.latex.Input;
import com.dickimawbooks.texparserlib.latex.KeyValList;
import com.dickimawbooks.texparserlib.latex.LaTeXParserListener;
import com.dickimawbooks.texparserlib.latex.LaTeXSty;
import com.dickimawbooks.texparserlib.latex.LaTeXSyntaxException;
import com.dickimawbooks.texparserlib.latex.Verb;
import com.dickimawbooks.texparserlib.latex.graphics.Epsfig;
import com.dickimawbooks.texparserlib.latex.graphics.GraphicsSty;
import com.dickimawbooks.texparserlib.latex.graphics.IncludeGraphics;
import com.dickimawbooks.texparserlib.latex.inputenc.InputEncSty;
import com.dickimawbooks.texparserlib.latex2latex.L2LActiveChar;
import com.dickimawbooks.texparserlib.latex2latex.L2LBegin;
import com.dickimawbooks.texparserlib.latex2latex.L2LBibliography;
import com.dickimawbooks.texparserlib.latex2latex.L2LBigOperator;
import com.dickimawbooks.texparserlib.latex2latex.L2LBinarySymbol;
import com.dickimawbooks.texparserlib.latex2latex.L2LComment;
import com.dickimawbooks.texparserlib.latex2latex.L2LControlSequence;
import com.dickimawbooks.texparserlib.latex2latex.L2LDoubleParam;
import com.dickimawbooks.texparserlib.latex2latex.L2LEnd;
import com.dickimawbooks.texparserlib.latex2latex.L2LEol;
import com.dickimawbooks.texparserlib.latex2latex.L2LGreekSymbol;
import com.dickimawbooks.texparserlib.latex2latex.L2LGroup;
import com.dickimawbooks.texparserlib.latex2latex.L2LIgnoreable;
import com.dickimawbooks.texparserlib.latex2latex.L2LMathDeclaration;
import com.dickimawbooks.texparserlib.latex2latex.L2LMathFontCommand;
import com.dickimawbooks.texparserlib.latex2latex.L2LMathGroup;
import com.dickimawbooks.texparserlib.latex2latex.L2LMathSymbol;
import com.dickimawbooks.texparserlib.latex2latex.L2LOther;
import com.dickimawbooks.texparserlib.latex2latex.L2LPar;
import com.dickimawbooks.texparserlib.latex2latex.L2LParam;
import com.dickimawbooks.texparserlib.latex2latex.L2LSkippedEols;
import com.dickimawbooks.texparserlib.latex2latex.L2LSkippedSpaces;
import com.dickimawbooks.texparserlib.latex2latex.L2LSpace;
import com.dickimawbooks.texparserlib.latex2latex.L2LSymbol;
import com.dickimawbooks.texparserlib.latex2latex.L2LTab;
import com.dickimawbooks.texparserlib.latex2latex.L2LVerbatim;
import com.dickimawbooks.texparserlib.latex2latex.LaTeX2LaTeX;

/**
 * Reads in and writes out LaTeX code, replacing obsolete/problematic commands.
 */

public class LaTeXDomBuilder extends LaTeXParserListener implements Writeable {	//LaTeX2LaTeX

	public LaTeXDomBuilder(TeXApp texApp, File outDir) throws IOException {
		super(null);
		this.outPath = outDir.toPath();
		System.out.println("wilbur - LaTeX2LaTeX: outPath = " + outPath);
		System.out.println("wilbur - LaTeX2LaTeX: parsePackages = " + this.isParsePackageSupportOn());
		this.texApp = texApp;
		
		this.writer = new PrintWriter(System.out, true);	// wilbur
		setWriteable(this);

		writer.write("*** Hello, this is the output stream from " + this.getClass().getSimpleName());
		writer.write(" ***\n");
	}
	

	@Override
	public ControlSequence getControlSequence(String name) {	// wilbur: this is only for filtering ignorable and replaceable commands (irrelevant for parsing)
//		System.out.println("************* LaTeXDomBuilder.getControlSequence() " + name);
//		if (isSkipCmd(name)) {
//			return new L2LIgnoreable(name);
//		}
//
//		if (isReplaceCmd(name)) {
//			return super.getControlSequence(name);
//		}
		return new L2LControlSequence(name);
	}

	@Override
	public ControlSequence createUndefinedCs(String name) {
		return new L2LControlSequence(name);
	}

	@Override
	public Comment createComment() {
		return new L2LComment();
	}

	@Override
	public SkippedSpaces createSkippedSpaces() {
		return new L2LSkippedSpaces();
	}

	@Override
	public SkippedEols createSkippedEols() {
		return new L2LSkippedEols();
	}

	@Override
	public Eol getEol() {
		return new L2LEol();
	}

	@Override
	public Space getSpace() {
		return new L2LSpace();
	}

	@Override
	public ActiveChar getActiveChar(int charCode) {
		return new L2LActiveChar(charCode);
	}

	@Override
	public Param getParam(int digit) {
		return new L2LParam(digit);
	}

	@Override
	public DoubleParam getDoubleParam(ParameterToken param) {
		return new L2LDoubleParam(param);
	}

	@Override
	public Other getOther(int charCode) {
		return new L2LOther(charCode);
	}

	@Override
	public Par getPar() {
		return new L2LPar();
	}

	@Override
	public Tab getTab() {
		return new L2LTab();
	}

	@Override
	public BigOperator createBigOperator(String name, int code1, int code2) {
		return new L2LBigOperator(name, code1, code2);
	}

	@Override
	public Symbol createSymbol(String name, int code) {
		return new L2LSymbol(name, code);
	}

	@Override
	public ControlSequence createSymbol(String name, int code, FontEncoding enc) {
		return new L2LSymbol(name, code);
	}

	@Override
	public GreekSymbol createGreekSymbol(String name, int code) {
		return new L2LGreekSymbol(name, code);
	}

	@Override
	public BinarySymbol createBinarySymbol(String name, int code) {
		return new L2LBinarySymbol(name, code);
	}

	@Override
	public MathSymbol createMathSymbol(String name, int code) {
		return new L2LMathSymbol(name, code);
	}

	@Override
	public Group createGroup() {
		return new L2LGroup();
	}

	@Override
	public Group createGroup(String text) {
		return new L2LGroup(this, text);
	}

	@Override
	public MathGroup createMathGroup() {
		return new L2LMathGroup();
	}
	
	// -----------------------------------------------------------------------------------------------------------------

	@Override
	public void beginDocument() throws IOException {
		super.beginDocument();
		writeCodePoint(parser.getEscChar());
		write("begin");
		writeCodePoint(parser.getBgChar());
		write("document");
		writeCodePoint(parser.getEgChar());
	}

	@Override
	public void endDocument() throws IOException {
		try {
			writeCodePoint(parser.getEscChar());
			write("end");
			writeCodePoint(parser.getBgChar());
			write("document");
			writeCodePoint(parser.getEgChar());
			writeln("");			

			super.endDocument();
		} finally {
			if (writer != null) {
				writer.close();
				writer = null;
			}
		}
	}

//	@Override
//	public void documentclass(KeyValList options, String clsName, boolean loadParentOptions) throws IOException {
//		if (docCls != null) {
//			throw new LaTeXSyntaxException(parser, LaTeXSyntaxException.ERROR_MULTI_CLS);
//		}
//
//		docCls = getLaTeXCls(options, clsName, loadParentOptions);
//
//		addFileReference(docCls);
//
//		writeCodePoint(parser.getEscChar());
//		write("documentclass");
//
//		if (options != null) {
//			write('[');
//			write(options.toString(parser));
//			write(']');
//		}
//
//		writeCodePoint(parser.getBgChar());
//		write(clsName);
//		writeCodePoint(parser.getEgChar());
//	}

//	@Override
//	public LaTeXSty requirepackage(KeyValList options, String styName, boolean loadParentOptions) throws IOException {
//		LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions);
//
//		addFileReference(sty);
//		loadedPackages.add(sty);
//
//		writeCodePoint(parser.getEscChar());
//		write("RequirePackage");
//
//		if (options != null) {
//			write('[');
//			write(options.toString(parser));
//			write(']');
//		}
//
//		writeCodePoint(parser.getBgChar());
//		write(styName);
//		writeCodePoint(parser.getEgChar());
//
//		return sty;
//	}

//	@Override
//	public LaTeXSty usepackage(KeyValList options, String styName, boolean loadParentOptions) throws IOException {
//		if (isStyLoaded(styName)) {
//			return null;
//		}
//
//		GraphicsSty graphicsSty = null;
//
//		if (styName.equals("epsfig")) {
//			graphicsSty = new GraphicsSty(options, this, false);
//
//			graphicsSty.registerControlSequence(new Epsfig("epsfig"));
//			graphicsSty.registerControlSequence(new Epsfig("psfig"));
//
//			getTeXApp().substituting(parser, styName, "graphicx");
//
//			styName = "graphicx";
//		} else if (styName.equals("graphics")) {
//			graphicsSty = new GraphicsSty(options, this, false);
//			getTeXApp().substituting(parser, styName, "graphicx");
//
//			styName = "graphicx";
//		} else if (styName.equals("graphicx")) {
//			graphicsSty = new GraphicsSty(options, this, false);
//		}
//
//		if (graphicsSty != null) {
//			graphicsSty.registerControlSequence(new IncludeGraphics(graphicsSty));
//		}
//
//		LaTeXSty sty = getLaTeXSty(options, styName, loadParentOptions);
//		;
//		addFileReference(sty);
//		loadedPackages.add(sty);
//
//		if (styName.equals("inputenc")) {
//			try {
//				String enc = InputEncSty.getOption(parser, outCharset == null ? Charset.defaultCharset() : outCharset);
//
//				if (options == null || options.get(enc) == null) {
//					substituting(
//							String.format("\\usepackage[%s]{inputenc}",
//									options == null ? "" : options.toString(parser)),
//							String.format("\\usepackage[%s]{inputenc}", enc));
//				}
//
//				writeCodePoint(parser.getEscChar());
//				write("usepackage[");
//				write(enc);
//				write(']');
//				writeCodePoint(parser.getBgChar());
//				write(styName);
//				writeCodePoint(parser.getEgChar());
//			} catch (LaTeXSyntaxException e) {
//				getTeXApp().error(e);
//			}
//
//			return sty;
//		}
//
//		writeCodePoint(parser.getEscChar());
//		write("usepackage");
//
//		if (options != null) {
//			write('[');
//			write(options.toString(parser));
//			write(']');
//		}
//
//		writeCodePoint(parser.getBgChar());
//		write(styName);
//		writeCodePoint(parser.getEgChar());
//
//		return sty;
//	}

//	@Override
//	public void substituting(String original, String replacement) throws IOException {
//		getTeXApp().substituting(parser, original, replacement);
//	}


//	protected void copyImageFile(File file, File destFile) throws IOException, InterruptedException {
//		getTeXApp().copyFile(file, destFile);
//
//		String name = destFile.getName();
//
//		if (name.toLowerCase().endsWith(".wmf")) {
//			File epsFile = new File(destFile.getParentFile(), name.substring(0, name.length() - 3) + "eps");
//
//			getTeXApp().wmftoeps(destFile, epsFile);
//
//			destFile = epsFile;
//			name = destFile.getName();
//		}
//
//		if (name.toLowerCase().endsWith(".eps")) {
//			File pdfFile = new File(destFile.getParentFile(), name.substring(0, name.length() - 3) + "pdf");
//
//			getTeXApp().epstopdf(destFile, pdfFile);
//		}
//	}
//
//
//	public Path copyImageFile(String[] grpaths, TeXPath path) throws IOException, InterruptedException {
//		if (grpaths == null) {
//			File file = path.getFile();
//
//			if (file.exists()) {
//				File destFile = outPath.resolve(path.getRelative()).toFile();
//
//				copyImageFile(file, destFile);
//
//				return path.getRelative();
//			}
//		} else {
//			Path basePath = path.getBaseDir();
//
//			for (int i = 0; i < grpaths.length; i++) {
//				Path subPath = (new File(
//						File.separatorChar == '/' ? grpaths[i] : grpaths[i].replaceAll("/", File.separator)).toPath())
//								.resolve(path.getRelative());
//
//				File file = (basePath == null ? subPath : basePath.resolve(subPath)).toFile();
//
//				if (file.exists()) {
//					File destFile = outPath.resolve(subPath).toFile();
//
//					copyImageFile(file, destFile);
//
//					return subPath;
//				}
//			}
//		}
//		return null;
//	}
	
//	@Override
//	public void includegraphics(KeyValList options, String imgName) throws IOException {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public void includegraphics(KeyValList options, String imgName) throws IOException {
//		String[] grpaths = getGraphicsPaths();
//
//		Path imagePath = null;
//
//		try {
//			if (imgName.contains(".")) {
//				TeXPath path = new TeXPath(parser, imgName);
//
//				imagePath = copyImageFile(grpaths, path);
//			} else {
//				for (int i = 0; i < IMAGE_EXT.length; i++) {
//					String name = imgName + "." + IMAGE_EXT[i];
//
//					TeXPath path = new TeXPath(parser, name);
//
//					imagePath = copyImageFile(grpaths, path);
//
//					if (imagePath != null) {
//						break;
//					}
//				}
//			}
//		} catch (InterruptedException e) {
//			getTeXApp().error(e);
//		}
//
//		if (isReplaceGraphicsPathEnabled() && imagePath != null) {
//			StringBuilder builder = new StringBuilder();
//
//			Iterator<Path> it = imagePath.iterator();
//
//			while (it.hasNext()) {
//				if (builder.length() > 0) {
//					builder.append('/');
//				}
//
//				builder.append(it.next().toString());
//			}
//
//			imgName = builder.toString();
//		}
//
//		writeCodePoint(parser.getEscChar());
//		write("includegraphics");
//
//		if (options != null && options.size() > 0) {
//			write('[');
//			write(options.toString(parser));
//			write(']');
//		}
//
//		writeCodePoint(parser.getBgChar());
//
//		String lc = imgName.toLowerCase();
//
//		if (lc.endsWith(".eps") || lc.endsWith(".ps") || lc.endsWith(".wmf")) {
//			write(imgName.substring(0, imgName.lastIndexOf(".")));
//		} else {
//			write(imgName);
//		}
//
//		writeCodePoint(parser.getEgChar());
//	}

//	@Override
//	public void setGraphicsPath(TeXObjectList paths) throws IOException {
//		super.setGraphicsPath(paths);
//
//		if (!isReplaceGraphicsPathEnabled()) {
//			writeCodePoint(parser.getEscChar());
//			write("graphicspath");
//
//			int bg = parser.getBgChar();
//			int eg = parser.getEgChar();
//
//			writeCodePoint(bg);
//
//			for (TeXObject path : paths) {
//				writeCodePoint(bg);
//				write(path.toString(parser));
//				writeCodePoint(eg);
//			}
//
//			writeCodePoint(eg);
//		}
//	}


//	public void bibliography(TeXPath[] bibPaths) throws IOException {
//		for (int i = 0; i < bibPaths.length; i++) {
//			if (bibPaths[i].wasFoundByKpsewhich()) {
//				continue;
//			}
//
//			File file = bibPaths[i].getFile();
//
//			if (file.exists()) {
//				Path dest = bibPaths[i].getRelative();
//
//				if (dest.isAbsolute()) {
//					dest = outPath.resolve(bibPaths[i].getLeaf());
//				} else {
//					dest = outPath.resolve(bibPaths[i].getRelative());
//				}
//
//				try {
//					getTeXApp().copyFile(file, dest.toFile());
//				} catch (InterruptedException e) {
//					getTeXApp().error(e);
//				}
//			}
//		}
//	}

	// required by interface Writable 
	
	@Override
	public void writeCodePoint(int charCode) throws IOException {
		if (writer != null) {
			if (charCode <= Character.MAX_VALUE) {
				writer.print((char) charCode);
			} else {
				for (char c : Character.toChars(charCode)) {
					writer.print(c);
				}
			}
		} else {
			getTeXApp().warning(getParser(), "null writer");
		}
	}

	@Override
	public void write(char c) throws IOException {
		if (writer != null) {
			writer.print(c);
		} else {
			getTeXApp().warning(getParser(), "null writer");
		}
	}

	@Override
	public void write(String string) throws IOException {
		if (writer != null) {
			writer.print(string);
		} else {
			getTeXApp().warning(getParser(), "null writer");
		}
	}

	@Override
	public void writeln(String string) throws IOException {
		if (writer != null) {
			writer.println(string);
		} else {
			getTeXApp().warning(getParser(), "null writer");
		}
	}

	@Override
	public void writeliteral(String string) throws IOException {
		throw new RuntimeException("writeliteral(String) not implemented");
		
	}

	@Override
	public void writeliteralln(String string) throws IOException {
		throw new RuntimeException("writeliteralln(String) not implemented");
		
	}

//	public void writeln(char c) throws IOException {
//		if (writer != null) {
//			writer.println(c);
//		} else {
//			getTeXApp().warning(getParser(), "null writer");
//		}
//	}


//	public void writeln() throws IOException {
//		if (writer != null) {
//			writer.println();
//		} else {
//			getTeXApp().warning(getParser(), "null writer");
//		}
//	}
	
	
	// -----------------------------------------------------------------------

	@Override
	public void overwithdelims(TeXObject firstDelim, TeXObject secondDelim, TeXObject before, TeXObject after)
			throws IOException {
		int esc = parser.getEscChar();
		int bg = parser.getBgChar();
		int eg = parser.getEgChar();

		if (firstDelim instanceof Other && secondDelim instanceof Other && ((Other) firstDelim).getCharCode() == '.'
				&& ((Other) secondDelim).getCharCode() == '.') {
			writeCodePoint(esc);
			write("frac");
			writeCodePoint(bg);
			write(before.toString(parser));
			writeCodePoint(eg);
			writeCodePoint(bg);
			write(after.toString(parser));
			writeCodePoint(eg);
		} else if (isStyLoaded("amsmath")) {
			writeCodePoint(esc);
			write("genfrac");

			// left-delim:
			writeCodePoint(bg);
			write(firstDelim.toString(parser));
			writeCodePoint(eg);

			// right-delim:
			writeCodePoint(bg);
			write(secondDelim.toString(parser));
			writeCodePoint(eg);

			// thickness:
			writeCodePoint(bg);
			writeCodePoint(eg);

			// mathstyle:
			writeCodePoint(bg);
			writeCodePoint(eg);

			// numerator:
			writeCodePoint(bg);
			write(before.toString(parser));
			writeCodePoint(eg);

			// denominator:
			writeCodePoint(bg);
			write(after.toString(parser));
			writeCodePoint(eg);
		} else {
			writeCodePoint(esc);
			write("left");
			write(firstDelim.toString(parser));
			writeCodePoint(esc);
			write("frac");
			writeCodePoint(bg);
			write(before.toString(parser));
			writeCodePoint(eg);
			writeCodePoint(bg);
			write(after.toString(parser));
			writeCodePoint(eg);
			writeCodePoint(esc);
			write("right");
			write(secondDelim.toString(parser));
		}
	}

	@Override
	public void abovewithdelims(TeXObject firstDelim, TeXObject secondDelim, TeXDimension thickness, TeXObject before,
			TeXObject after) throws IOException {
		int esc = parser.getEscChar();
		int bg = parser.getBgChar();
		int eg = parser.getEgChar();

		if (isStyLoaded("amsmath")) {
			writeCodePoint(esc);
			write("genfrac");

			// left-delim:
			writeCodePoint(bg);
			write(firstDelim.toString(parser));
			writeCodePoint(eg);

			// right-delim:
			writeCodePoint(bg);
			write(secondDelim.toString(parser));
			writeCodePoint(eg);

			// thickness:
			writeCodePoint(bg);
			write(thickness.toString(parser));
			writeCodePoint(eg);

			// mathstyle:
			writeCodePoint(bg);
			writeCodePoint(eg);

			// numerator:
			writeCodePoint(bg);
			write(before.toString(parser));
			writeCodePoint(eg);

			// denominator:
			writeCodePoint(bg);
			write(after.toString(parser));
			writeCodePoint(eg);
		} else if (firstDelim instanceof Other && secondDelim instanceof Other
				&& ((Other) firstDelim).getCharCode() == '.' && ((Other) secondDelim).getCharCode() == '.') {
			writeCodePoint(bg);
			write(before.toString(parser));
			writeCodePoint(esc);
			write("above ");
			write(thickness.toString(parser));
			write(after.toString(parser));
			writeCodePoint(eg);
		} else {
			writeCodePoint(bg);
			write(before.toString(parser));
			writeCodePoint(esc);
			write("abovewithdelims ");
			write(firstDelim.toString(parser));
			write(secondDelim.toString(parser));
			write(thickness.toString(parser));
			write(after.toString(parser));
			writeCodePoint(eg);
		}
	}

	@Override
	public void skipping(Ignoreable ignoreable) throws IOException {
		write(ignoreable.toString(getParser()));
	}

	@Override
	public void subscript(TeXObject arg) throws IOException {
		writeCodePoint(parser.getSbChar());
		writeCodePoint(parser.getBgChar());
		write(arg.toString(parser));
		writeCodePoint(parser.getEgChar());
	}

	@Override
	public void superscript(TeXObject arg) throws IOException {
		writeCodePoint(parser.getSpChar());
		writeCodePoint(parser.getBgChar());
		write(arg.toString(parser));
		writeCodePoint(parser.getEgChar());
	}

	@Override
	public TeXApp getTeXApp() {
		return texApp;
	}


	public boolean isReplaceCmd(String name) {
		for (int i = 0; i < CHECK_CMDS.length; i++) {
			if (CHECK_CMDS[i].equals(name))
				return true;
		}

		return false;
	}


	public boolean isSkipCmd(String name) {
		for (int i = 0; i < SKIP_CMDS.length; i++) {
			if (SKIP_CMDS[i].equals(name))
				return true;
		}

		return false;
	}

	@Override
	public void beginParse(File file, Charset encoding) throws IOException {
		System.out.println("wilbur - beginParse");
		getTeXApp().message(getTeXApp().getMessage(TeXApp.MESSAGE_READING, file));

		if (encoding != null) {
			getTeXApp().message(getTeXApp().getMessage(TeXApp.MESSAGE_ENCODING, encoding));
		}

		basePath = file.getParentFile().toPath();

		if (writer == null) {
			System.out.println("wilbur - creating writer");
			Files.createDirectories(outPath);

			File outFile = new File(outPath.toFile(), getOutFileName(file));

			getTeXApp().message(getTeXApp().getMessage(TeXApp.MESSAGE_WRITING, outFile));

			if (outCharset == null) {
				writer = new PrintWriter(outFile);
			} else {
				getTeXApp().message(getTeXApp().getMessage(TeXApp.MESSAGE_ENCODING, outCharset));

				writer = new PrintWriter(outFile, outCharset.name());
			}
		}
	}


	public String getOutFileName(File inFile) {
		return inFile.getName();
	}

	@Override
	public void endParse(File file) throws IOException {
		System.out.println("wilbur - endParse");
		TeXReader reader = getParser().getReader();

		if (reader != null) {
			reader = reader.getParent();
		}

		if (writer != null && reader == null) {
			writer.flush();
			writer.close();
			writer = null;
		}
	}

	@Override
	public void href(String url, TeXObject text) throws IOException {
		int bg = parser.getBgChar();
		int eg = parser.getEgChar();

		writeCodePoint(parser.getEscChar());
		write("href");
		writeCodePoint(bg);
		write(url);
		writeCodePoint(eg);
		writeCodePoint(bg);
		write(text.toString(parser));
		writeCodePoint(eg);
	}

	@Override
	public void verb(String name, boolean isStar, int delim, String text) throws IOException {
		writeCodePoint(parser.getEscChar());
		write(name);

		if (isStar) {
			write("*");
		} else if (parser.isLetter(delim)) {
			write(" ");
		}

		writeCodePoint(delim);
		write(text);
		writeCodePoint(delim);
	}

//	@Override
//	public void newcommand(byte overwrite, String type, String csName, boolean isShort, int numParams,
//			TeXObject defValue, TeXObject definition) throws IOException {
//		String bg = new String(Character.toChars(parser.getBgChar()));
//		String eg = new String(Character.toChars(parser.getEgChar()));
//		String esc = new String(Character.toChars(parser.getEscChar()));
//
//		write(esc);
//		write(type);
//
//		if (isShort) {
//			write('*');
//		}
//
//		write(String.format("%s%s%s%s", bg, esc, csName, eg));
//
//		if (numParams > 0) {
//			write("[" + numParams + "]");
//
//			if (defValue != null) {
//				write("[" + defValue.toString(parser) + "]");
//			}
//		}
//
//		write(String.format("%s%s%s", bg, definition.toString(parser), eg));
//	}

	private Path outPath, basePath;
	private PrintWriter writer;

	private Charset outCharset = null;

	private boolean replaceGraphicsPath = false;

	private TeXApp texApp;

	public static final String[] CHECK_CMDS = new String[] { "epsfig", "psfig", "centerline", "special", "rm", "tt",
			"sf", "bf", "it", "sl", "sc", "cal", "includegraphics", "usepackage", "graphicspath", "documentclass",
			"documentstyle", "begin", "end", "FRAME", "Qcb", "verb", "lstinline", "verbatim", "endverbatim",
			"lstlistings", "lstlistings*", "[", "]", "(", ")", "displaymath", "enddisplaymath", "math", "endmath",
			"equation", "endequation", "equation*", "endequation*", "align", "endalign", "align*", "endalign*", "input",
			"newcommand", "renewcommand", "providecommand", "bibliography" };

	public static final String[] SKIP_CMDS = new String[] { "bigskip" };

	



	@Override
	public void includegraphics(TeXObjectList stack, KeyValList options, String imgName) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void substituting(String original, String replacement) throws IOException {
		// TODO Auto-generated method stub
		
	}


}
