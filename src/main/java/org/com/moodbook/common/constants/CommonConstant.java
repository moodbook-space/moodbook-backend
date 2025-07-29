package org.com.moodbook.common.constants;

public class CommonConstant {

	/**
	 * 테스트 권장값 (10, 3~5, 10)
	 * MAX_RESULTS: 1페이지에 10권
	 * MAX_PAGES_PER_KEYWORD: 키워드당 최대 5페이지 요청
	 * TOTAL_COUNT: 총 수집할 도서 수 제한
	 */

	/**
	 * 실제 운영시 추천 값
	 * MAX_RESULTS: 20 또는 50
	 * MAX_PAGES_PER_KEYWORD: 10 ~ 20
	 * TOTAL_COUNT: 500~1000
	 */

	public static final int MAX_RESULTS = 5;
	public static final int MAX_PAGES_PER_KEYWORD = 5;
	public static final int TOTAL_COUNT = 5;

}
