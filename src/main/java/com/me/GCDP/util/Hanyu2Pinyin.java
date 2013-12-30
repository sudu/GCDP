package com.me.GCDP.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class Hanyu2Pinyin {
	
	/*public static void main(String[] args){
		Set ret =  Hanyu2Pinyin.getJianpin("行业");
		Set ret2 =  Hanyu2Pinyin.getPinyin("行业");
		Object[] os =ret2.toArray();
		for(Object o : os){
			System.out.println(o);
		}
		
		JSONArray array = new JSONArray(ret2);
		System.out.println(array.toString());
	}*/

	// 汉语拼音格式输出类
	public static HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
	public static final String PINYIN_KEY = "PINYIN";
	public static final String JIANPIN_KEY = "JIANPIN";

	static {
		// 输出设置，大小写，音标方式等
		hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
	}

	public static String[] getPinyin(char c) {
		return getPinyin(c, false);
	}

	public static String[] getPinyin(char c, boolean once) {
		String[] pinyins = new String[0];

		// 是中文0-9或者a-z或者A-Z转换拼音(我的需求，是保留中文或者a-z或者A-Z)
		if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
			try {
				pinyins = PinyinHelper.toHanyuPinyinStringArray(c,
						Hanyu2Pinyin.hanYuPinOutputFormat);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else if (((int) c >= 65 && (int) c <= 90)
				|| ((int) c >= 97 && (int) c <= 122)
				|| ((int) c >= 48 && (int) c <= 57)) {
			pinyins = new String[] { String.valueOf(c) };
		} else {
			pinyins = null;
		}

		if (once && pinyins != null) {
			pinyins = new String[] { pinyins[0] };
		}
		return pinyins;
	}

	public static String[] getJianpin(char c) {
		return getJianpin(c, false);
	}

	public static String[] getJianpin(char c, boolean once) {
		return getJianpin(getPinyin(c, once), once);
	}

	public static String[] getJianpin(String[] pinyins) {
		return getJianpin(pinyins, false);
	}

	public static String[] getJianpin(String[] pinyins, boolean once) {
		if (pinyins == null) {
			return null;
		}
		int len = (once && pinyins.length >= 1) ? 1 : pinyins.length;

		String[] jianpins = new String[len];

		for (int i = 0; i < len; i++) {
			jianpins[i] = pinyins[i].substring(0, 1);
		}

		return jianpins;
	}

	public static Set<String> getPinyin(String src) {
		return getPinyin(src, false);
	}

	public static Set<String> getPinyin(String src, boolean once) {
		char[] srcChar;
		srcChar = src.toCharArray();

		String[][] pinyins = new String[src.length()][];
		for (int i = 0; i < srcChar.length; i++) {
			char c = srcChar[i];
			String[] t = getPinyin(c, once);
			if (t != null)
				pinyins[i] = t;
			else
				pinyins[i] = new String[] { "" };
		}

		Set<String> pinyinSet = new HashSet<String>();

		if (once) {
			String t = "";
			for (String[] s : pinyins) {
				t += s[0];
			}
			pinyinSet.add(t);
		} else {
			String[] pingyinArray = Exchange(pinyins);

			for (int i = 0; i < pingyinArray.length; i++) {
				pinyinSet.add(pingyinArray[i]);
			}
		}

		return pinyinSet;
	}

	public static Set<String> getJianpin(String src) {
		return getJianpin(src, false);
	}

	public static Set<String> getJianpin(String src, boolean once) {
		char[] srcChar;
		srcChar = src.toCharArray();

		String[][] jianpins = new String[src.length()][];
		for (int i = 0; i < srcChar.length; i++) {
			char c = srcChar[i];
			String[] t = getJianpin(c, once);
			if (t != null)
				jianpins[i] = t;
			else
				jianpins[i] = new String[] { "" };
		}

		Set<String> pinyinSet = new HashSet<String>();

		if (once) {
			String t = "";
			for (String[] s : jianpins) {
				t += s[0];
			}
			pinyinSet.add(t);
		} else {
			String[] pingyinArray = Exchange(jianpins);

			for (int i = 0; i < pingyinArray.length; i++) {
				pinyinSet.add(pingyinArray[i]);
			}
		}

		return pinyinSet;
	}

	public static HashMap<String, String> getPinyinAndJianpin(String src) {
		HashMap<String, String> pjMap = new HashMap<String, String>();
		char[] srcChar;
		srcChar = src.toCharArray();
		boolean justOnce = true;

		String[][] pinyins = new String[src.length()][];
		String[][] jianpins = new String[src.length()][];
		for (int i = 0; i < srcChar.length; i++) {
			char c = srcChar[i];
			String[] t = getPinyin(c, justOnce);
			if (t != null) {
				pinyins[i] = t;
				jianpins[i] = getJianpin(t, justOnce);
			} else {
				pinyins[i] = new String[] { "" };
				jianpins[i] = new String[] { "" };
			}
		}

		String tmp = "";
		for (String[] s : pinyins) {
			tmp += s[0];
			pjMap.put(PINYIN_KEY, tmp);
		}

		tmp = "";
		for (String[] s : jianpins) {
			tmp += s[0];
			pjMap.put(JIANPIN_KEY, tmp);
		}

		return pjMap;
	}

	public static String[] Exchange(String[][] strJaggedArray) {
		String[][] pinyins = DoExchange(strJaggedArray);
		return pinyins[0];
	}

	private static String[][] DoExchange(String[][] strJaggedArray) {
		int len = strJaggedArray.length;
		if (len >= 2) {
			int len1 = strJaggedArray[0].length;
			int len2 = strJaggedArray[1].length;
			int newlen = len1 * len2;
			String[] temp = new String[newlen];
			int Index = 0;
			for (int i = 0; i < len1; i++) {
				for (int j = 0; j < len2; j++) {
					temp[Index] = strJaggedArray[0][i] + strJaggedArray[1][j];
					Index++;
				}
			}
			String[][] newArray = new String[len - 1][];
			for (int i = 2; i < len; i++) {
				newArray[i - 1] = strJaggedArray[i];
			}
			newArray[0] = temp;
			return DoExchange(newArray);
		} else {
			return strJaggedArray;
		}
	}

	public static String makeJsonArrayByStringSet(Set<String> stringSet) {
		return makeJsonArrayByStringSet(stringSet, false);
	}

	public static String makeJsonArrayByStringSet(Set<String> stringSet,
			boolean isLowCase) {
		StringBuilder str = new StringBuilder("[");
		int i = 0;
		for (String s : stringSet) {
			if (i == stringSet.size() - 1) {
				str.append("\"" + s + "\"");
			} else {
				str.append("\"" + s + "\",");
			}
			i++;
		}
		str.append("]");

		if (isLowCase) {
			return str.toString().toLowerCase();
		} else {
			return str.toString();
		}
	}
}
