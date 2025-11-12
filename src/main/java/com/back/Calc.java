package com.back;

import java.util.*;

/**
 * 단순한 사칙연산(+,-,*) 및 괄호를 포함한 수식을 계산하는 유틸리티 클래스입니다.
 * <p>
 * 동작 방식:
 * 1. 단항 마이너스(-(...))를 (0 - (...)) 형태로 변환
 * 2. 수식을 공백 기준으로 토크나이징
 * 3. 괄호 내부부터 재귀적으로 계산
 * 4. 남은 식은 * → +, - 순서로 계산
 */
public class Calc {

    public static void main(String[] args) {
        run("3 + 4 * (4 + 2)");
    }

    /**
     * 문자열 기반 수식을 계산합니다.
     * - 괄호와 단항 마이너스 처리
     * - 재귀 계산 호출
     * @param expression 입력 수식 문자열
     * @return 계산 결과
     */
    public static int run(String expression) {

        // 단항 마이너스 처리 (-(...) → (0 - (...))
        expression = unaryMinus(expression);

        // 괄호 앞뒤 공백 추가 후 분할
        String[] values = expression
                .replace("(", "( ")
                .replace(")", " )")
                .replace("-(", "- (")
                .split("\\s+");

        // 배열을 리스트로 변환
        List<String> listed = new ArrayList<>(Arrays.asList(values));
        System.out.println(listed);

        // 재귀 계산 시작
        return calculate(listed);
    }

    /**
     * 괄호를 포함한 수식을 계산합니다.
     * 괄호가 존재하면 내부부터 재귀적으로 계산하고, 모두 제거될 때까지 반복합니다.
     * @param exps 토큰 리스트
     * @return 괄호 제거 후 단순식 계산 결과
     */
    private static int calculate(List<String> exps) {

        for (int i = 0; i < exps.size(); i++) {
            if ("(".equals(exps.get(i))) {
                int depth = 1;
                for (int j = i + 1; j < exps.size(); j++) {
                    if ("(".equals(exps.get(j))) depth++;
                    else if (")".equals(exps.get(j))) depth--;
                    if (depth == 0) {
                        // 가장 안쪽 괄호 발견 시 내부 수식 재귀 계산
                        int inner = calculate(new ArrayList<>(exps.subList(i + 1, j)));

                        // 괄호 포함 구간을 결과값으로 치환
                        replaceList(exps, String.valueOf(inner), i, j);

                        // 리스트가 변경되었으므로 다시 전체 계산 수행
                        return calculate(exps);
                    }
                }
            }
        }

        // 괄호가 없는 단순식 계산
        return simpleCalculate(exps);
    }

    /**
     * 괄호가 없는 단순 수식(+,-,*)을 계산합니다.
     * 1. 곱셈 우선 계산
     * 2. 덧셈/뺄셈 순차 계산
     * @param exps 수식 토큰 리스트
     * @return 계산 결과
     */
    private static int simpleCalculate(List<String> exps) {
        System.out.println("simple calc with " + exps);

        // 1) 곱셈(*) 먼저 계산
        for (int i = 0; i < exps.size(); i++) {
            if ("*".equals(exps.get(i))) {
                int val1 = Integer.parseInt(exps.get(i - 1));
                int val2 = Integer.parseInt(exps.get(i + 1));

                // [피연산자1, *, 피연산자2] → 결과값으로 치환
                replaceList(exps, String.valueOf(val1 * val2), i - 1, i + 1);
                i = 0; // 리스트가 변경되었으므로 다시 처음부터 탐색
            }
        }

        // 2) 덧셈, 뺄셈 순차 계산 (왼쪽에서 오른쪽)
        for (int i = 0; i < exps.size(); i++) {
            if ("+".equals(exps.get(i))) {
                int val1 = Integer.parseInt(exps.get(i - 1));
                int val2 = Integer.parseInt(exps.get(i + 1));
                replaceList(exps, String.valueOf(val1 + val2), i - 1, i + 1);
                i = 0;
            } else if ("-".equals(exps.get(i))) {
                int val1 = Integer.parseInt(exps.get(i - 1));
                int val2 = Integer.parseInt(exps.get(i + 1));
                replaceList(exps, String.valueOf(val1 - val2), i - 1, i + 1);
                i = 0;
            }
            System.out.println(exps);
        }

        // 최종 결과 반환
        return Integer.parseInt(exps.get(0));
    }

    /**
     * 리스트 내 특정 구간([startIdx, endIdx])을 제거하고
     * 그 자리에 새 문자열 값을 삽입합니다.
     */
    private static void replaceList(List<String> exp, String value, int startIdx, int endIdx) {
        exp.subList(startIdx, endIdx + 1).clear();
        exp.add(startIdx, value);
    }

    /**
     * 문자열에서 단항 마이너스(-(...))를 (0 - (...)) 형태로 변환합니다.
     * 괄호 중첩까지 처리합니다.
     */
    private static String unaryMinus(String expression) {
        StringBuilder sb = new StringBuilder(expression);
        int depth = 0;

        for (int i = 0; i < sb.length(); i++) {
            int startI = i;
            if (sb.charAt(i) == '-' && i + 1 < sb.length() && sb.charAt(i + 1) == '(') {
                depth = 1;
                sb.replace(i, i + 2, "(0 - ("); // -( → (0 - (
                i = i + 6;
                startI = i;
                while (i < sb.length()) {
                    if (sb.charAt(i) == '(') depth++;
                    if (sb.charAt(i) == ')') depth--;
                    if (depth == 0) {
                        sb.replace(i, i + 1, "))");
                        break;
                    }
                    i++;
                }
            }
            i = startI;
        }
        return sb.toString();
    }

}
