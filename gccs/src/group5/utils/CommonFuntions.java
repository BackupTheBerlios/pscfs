//$Id: CommonFuntions.java,v 1.3 2005/06/12 22:33:05 huuhoa Exp $
package group5.utils;

public class CommonFuntions {
	// -------------------------------------------------------------------
	// The following contains utilities for Number Base Conversions.
	// -------------------------------------------------------------------

	/**
	 * This array is used to convert from bytes to hexadecimal numbers
	 */
	static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * The following method is to convert an array of bytes to a String.
	 */
	static public String hexBytesToString(byte[] bytes) {
		StringBuffer s = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			s.append(digits[(b & 0xf0) >> 4]);
			s.append(digits[b & 0x0f]);
		}
		return s.toString().toUpperCase();
	}

	/**
	 * The following method is to convert from a String of hexidecimal digits to
	 * an array of bytes.
	 */
	static public byte[] stringToHexBytes(String s)
			throws IllegalArgumentException {
		try {
			s = s.toLowerCase();
			int len = s.length();
			byte[] r = new byte[len / 2];
			for (int i = 0; i < r.length; i++) {
				int digit1 = s.charAt(i * 2), digit2 = s.charAt(i * 2 + 1);
				if ((digit1 >= '0') && (digit1 <= '9'))
					digit1 -= '0';
				else if ((digit1 >= 'a') && (digit1 <= 'f'))
					digit1 -= 'a' - 10;
				if ((digit2 >= '0') && (digit2 <= '9'))
					digit2 -= '0';
				else if ((digit2 >= 'a') && (digit2 <= 'f'))
					digit2 -= 'a' - 10;
				r[i] = (byte) ((digit1 << 4) + digit2);
			}
			return r;
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"stringToHexBytes(): invalid input");
		}
	}
}
