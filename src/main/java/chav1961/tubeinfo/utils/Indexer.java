package chav1961.tubeinfo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import chav1961.purelib.basic.Utils;

public class Indexer {

	public static void main(String[] args) throws IOException {
		final File	root = new File("./src/main/resources/chav1961/tubeinfo/builtin");
		final File	index = new File(root, "index.txt");

		index.delete();
		try(final ByteArrayOutputStream	baos = new ByteArrayOutputStream()) {
			try(final PrintWriter	pw = new PrintWriter(baos)) {
				walk(root, "", pw);
			}
			try(final OutputStream	os = new FileOutputStream(index)) {
				Utils.copyStream(new ByteArrayInputStream(baos.toByteArray()), os);
			}
		}
		System.err.println("Completed");
	}

	private static void walk(final File current, final String path, final PrintWriter pw) {
		if (current.isFile() && current.getName().endsWith(".xml")) {
			pw.println(path);
		}
		else if (current.isDirectory()) {
			final File[]	list = current.listFiles();
			
			if (list != null) {
				for(File item : list) {
					walk(item, path + '/' + item.getName(), pw);
				}
			}
		}
	}

}
