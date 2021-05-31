package br.com.bergmannsoft.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.regex.Pattern;

import android.util.Log;

public class TextHelper {

	public static final String CSS_PATTERN = "(https?:\\/\\/)([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)css*";
	public static final String URL_PATTERN = "(((f|ht){1}tps?://)[-a-zA-Z0-9@:%_\\+.~#?&//=]+)";

	public static String formatSize(long size) {
		if (size < 1024) {
			return size + " bytes";
		} else if (size < (1 << 20)) {
			long i = (size >> 10);
			long d = size % (1 << 10);
			long p = (d * 100) >> 10;
			return "" + i + "," + p + " KB";
		} else {
			long i = (size >> 20);
			long d = size % (1 << 20);
			long p = (d * 100) >> 20;
			return "" + i + "," + p + " MB";
		}
	}

//	public static String removeAcentuacoes(CharSequence texto) {
//		if (texto == null)
//			return "";
//		String novoTexto = texto.toString().toUpperCase();
//		novoTexto = novoTexto.replace('�', 'A');
//		novoTexto = novoTexto.replace('�', 'A');
//		novoTexto = novoTexto.replace('�', 'A');
//		novoTexto = novoTexto.replace('�', 'A');
//		novoTexto = novoTexto.replace('�', 'A');
//		novoTexto = novoTexto.replace('�', 'E');
//		novoTexto = novoTexto.replace('�', 'E');
//		novoTexto = novoTexto.replace('�', 'E');
//		novoTexto = novoTexto.replace('�', 'E');
//		novoTexto = novoTexto.replace('�', 'I');
//		novoTexto = novoTexto.replace('�', 'I');
//		novoTexto = novoTexto.replace('�', 'I');
//		novoTexto = novoTexto.replace('�', 'I');
//		novoTexto = novoTexto.replace('�', 'O');
//		novoTexto = novoTexto.replace('�', 'O');
//		novoTexto = novoTexto.replace('�', 'O');
//		novoTexto = novoTexto.replace('�', 'O');
//		novoTexto = novoTexto.replace('�', 'O');
//		novoTexto = novoTexto.replace('�', 'U');
//		novoTexto = novoTexto.replace('�', 'U');
//		novoTexto = novoTexto.replace('�', 'U');
//		novoTexto = novoTexto.replace('�', 'U');
//		novoTexto = novoTexto.replace('�', 'C');
//		return novoTexto.trim().toLowerCase();
//	}

	public static String removeCSSUrls(String baseUrl) {
		if (baseUrl == null)
			return "";
		return baseUrl.replaceAll(URL_PATTERN, "");
	}

	public static boolean containsImgs(String baseUrl, Pattern imagePattern) {
		if (baseUrl == null)
			return false;
		return imagePattern.matcher(baseUrl).find();
	}

	public static String filterNonAscii(String inString) {
		// Create the encoder and decoder for the character encoding
		Charset charset = Charset.forName("US-ASCII");
		CharsetDecoder decoder = charset.newDecoder();
		CharsetEncoder encoder = charset.newEncoder();
		// This line is the key to removing "unmappable" characters.
		encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
		String result = inString;

		try {
			// Convert a string to bytes in a ByteBuffer
			ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(inString));

			// Convert bytes in a ByteBuffer to a character ByteBuffer and then
			// to a string.
			CharBuffer cbuf = decoder.decode(bbuf);
			result = cbuf.toString();
		} catch (CharacterCodingException cce) {
			Log.d("Non-ASCII-Filter", cce.toString());
		}

		return result;
	}

	/**
	 * Mirror of the unicode table from 00c0 to 017f without diacritics.
	 */
	private static final String tab00c0 = "AAAAAAACEEEEIIII"
			+ "DNOOOOO\u00d7\u00d8UUUUYI\u00df" + "aaaaaaaceeeeiiii"
			+ "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" + "AaAaAaCcCcCcCcDd"
			+ "DdEeEeEeEeEeGgGg" + "GgGgHhHhIiIiIiIi" + "IiJjJjKkkLlLlLlL"
			+ "lLlNnNnNnnNnOoOo" + "OoOoRrRrRrSsSsSs" + "SsTtTtTtUuUuUuUu"
			+ "UuUuWwYyYZzZzZzF";

	/**
	 * Returns string without diacritics - 7 bit approximation.
	 * 
	 * @param source
	 *            string to convert
	 * @return corresponding string without diacritics
	 */
	public static String removeDiacritic(String source) {
		char[] vysl = new char[source.length()];
		char one;
		for (int i = 0; i < source.length(); i++) {
			one = source.charAt(i);
			if (one >= '\u00c0' && one <= '\u017f') {
				one = tab00c0.charAt((int) one - '\u00c0');
			}
			vysl[i] = one;
		}
		return new String(vysl);
	}
}