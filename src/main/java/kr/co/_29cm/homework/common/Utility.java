package kr.co._29cm.homework.common;

import java.text.NumberFormat;
import java.util.Locale;

public class Utility {
    /**
     * 29,000원 -> 29000 변환 정규식
     * @param price
     * @return 가격의 숫자
     */
    public static Long convertPriceToNumber(String price) {
        String priceNumberString = price.replaceAll("[^0-9]", "");
        return Long.parseLong(priceNumberString);
    }

    /**
     * 29000 -> 29,000원
     * @param number
     * @return DB값, 뷰단에서 보여 줄 가격
     */
    public static String convertNumberToPrice(Long number) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
        String formattedNumber = formatter.format(number);
        return formattedNumber + "원";
    }
}
