package com.teman.plurkdog;

public class StringUtil {

    @Deprecated
	public static String fixJSON(String jsontext) {
		jsontext = jsontext.replaceAll("\\(\"", "('");
		jsontext = jsontext.replaceAll("\"\\)", "')");
		jsontext = jsontext.replaceAll("new", "\"new");
		jsontext = jsontext.replaceAll("GMT'\\)", "GMT')\"");
		return jsontext;
	}
	
	/**  
	 * @param jsontext
	 * @return
	 */
	public static String fixJSONEncode(String jsontext) {
		jsontext = jsontext.replaceAll("new Date\\(\"", "\"new Date(\\\\\"");
		jsontext = jsontext.replaceAll("GMT\"\\)", "GMT\\\\\")\"");		
		return jsontext;
	}
	
	public static void main(String[] args) {
		String str = "new Date(\"Sun, 18 Sep 1983 00:01:00 GMT\"),";
		System.out.println(fixJSONEncode(str));
	}
	
	 /**
     * �Ǧ^���pattern���᪽��end���r��A�Y�S���ŦX�}�l�P�����h�^��null
     * @param str
     * @param pattern
     * @param end
     * @return String
     */
    public static String findString(String str, String pattern, String end) {

        int pos = str.indexOf(pattern);
        if (pos >= 0) { // ���h�Y
            str = str.substring(pos + pattern.length());

            if (end.length() == 0)
                return str;

            int last = str.indexOf(end);

            if (last < 0) { // �S���쵲�� ��null���q�L
                return null;
            } else {
                return str.substring(0, last); // ���h��
            }
        } else {
            return null;
        }
    }

}
