package wilbur;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;

import com.dickimawbooks.texparserlib.TeXApp;
import com.dickimawbooks.texparserlib.TeXParser;
import com.dickimawbooks.texparserlib.TeXParserListener;
import com.dickimawbooks.texparserlib.TeXReader;
import com.dickimawbooks.texparsertest.TeXParserApp;

import wilbur.lib.LaTeXDomBuilder;

/**
 * Unfinished, got stuck ...
 * @author wilbur
 *
 */
public class Test2  {
	
//	static String texFileName = "tex/test-newline.tex"; 
//	static String texFileName = "tex/test-dirty2.tex";
//	static String texFileName = "tex/test-obs.tex";
//	static String texFileName = "tex/test-minimal.tex";
	static String texFileName = "tex/test-math-nested.tex";
	
	
	static String outDirPath = "C:/tmp/";
//	static String outDirPath =  System.getProperty("java.io.tmpdir");

	public static void main(String[] args) throws IOException {
		Path path = FileSystems.getDefault().getPath(texFileName);
		System.out.println("tex path = " + path.toAbsolutePath().toString());
		
		File file = path.toFile();
		File outDir = FileSystems.getDefault().getPath(outDirPath).toFile();
		
		
		System.out.println("Creating TeXParserApp");
		TeXApp app = new TeXParserApp();
		
		TeXParserListener listener = new LaTeXDomBuilder(app, outDir);  //(TeXApp texApp, File outDir)
//		System.out.println("Created Listener: " + listener.getClass().getSimpleName());
		
		TeXParser parser = new TeXParser(listener);
//		System.out.println("Created Parser: " + parser.getClass().getSimpleName());
		
//		printCsTable(parser);
//		System.out.println("atan cs = " + listener.getControlSequence("atan"));

		TeXReader reader = new TeXReader(file);
		System.out.println("Created Reader: " + reader.getClass().getSimpleName());
		
		System.out.println("Starting Parser");
		parser.parse(reader);	// did not call beginParse(), not set up writer
//		parser.parse(file);	
		System.out.println("done.");

	}
	
	static void printCsTable(TeXParser parser) {
//		String[] names = parser.csTable.keySet().toArray(new String[0]);
//		Arrays.sort(names);
//		
////		Hashtable<String, ControlSequence> csTable
//		for (String s : names) {
//			System.out.println("   " + s);
//		}
	}

}
