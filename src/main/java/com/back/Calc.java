package com.back;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Calc {
    public static void main(String[] args) {

    }

    /**
     * 입력받은 문자열을 파싱하여, 후위 표기법으로 변환하고, stack 을 통해 계산합니다.
     * @param expression 수식 문자열
     * @return 반환값
     */
    public static int run(String expression) {

        expression = unaryMinus(expression);

        String[] values = expression
                .replace("(",  "( ")
                .replace(")", " )")
                .split(" ");

        System.out.println(expression);

        List<String> postFix = toPostFix(values);
        Deque<Integer> stack = new ArrayDeque<>();
        for(String each : postFix) {
            if(isDigit(each)) stack.push(Integer.parseInt(each));
            else {
                int val1 = stack.pop();
                int val2 = stack.pop();
                int result = switch (each) {
                    case "+" -> val1 + val2;
                    case "-" -> val2 - val1;
                    case "*" -> val1 * val2;
                    default -> throw new IllegalArgumentException();
                };

                stack.push(result);
            }
        }
        return stack.pop();
    }

    /**
     * 문자열에서 단항 마이너스에 대한 처리를 수행합니다.<br>
     * 대표적으로 -(3-2) 는 다음과 같이 변환됩니다. -> (0 - (3 - 2)) <br>
     * 이를 통하여 수식 처리의 일관성을 부여합니다.
     * @param expression raw expression string
     * @return expression string
     */
    private static String unaryMinus(String expression) {
        StringBuilder sb = new StringBuilder(expression);
        int depth = 0;

        //문자열을 탐색하며 순회
        for(int i = 0; i < sb.length(); i++) {
            int startI = i; //-( 가 중첩되어 나타나는 경우, 재탐색이 필요하므로 초기 인덱스 번호를 저장
            if(sb.charAt(i) == '-' && sb.charAt(i + 1) == '(') {
                depth = 1;
                sb.replace(i, i + 2, "(0 - ("); //-( 를 (0 - ( 로 변환

                i = i + 6; //변환 후 i 인덱스
                startI = i;
                while(i < sb.length()) {
                    if(sb.charAt(i) == '(') depth++; //내부에서 여는 괄호를 만나면 깊이 1 증가
                    if(sb.charAt(i) == ')') {  //내부에서 닫는 괄호를 만나면 깊이 1 감소
                        depth--;
                    }
                    if(depth == 0) { //깊이가 0이면, 최초 인식한 ( 에 대한 짝이므로
                        sb.replace(i, i + 1, "))");
                        break;
                    }
                    i++;
                }

            }
            i = startI; //중첩인 경우를 대비하여 인덱스 초기화
        }
        return sb.toString();
    }

    /**
     * 숫자 여부를 판별하여 boolean 으로 반환합니다.
     * @param each 각 문자열 토큰
     * @return 해당 문자열이 숫자인 경우 true, 그 외 false
     */
    private static boolean isDigit(String each) {
        return each.matches("[+-]?\\d+");
    }

    /**
     * 일반 중위 표기법의 수식을 후위 표기법의 수식으로 변환하여, 각 토큰을 리스트에 담아 반환합니다.
     * @param values 중위 표기법 기반의 문자열 배열
     * @return 후위 표기법 기반의 문자열 리스트
     */
    private static List<String> toPostFix(String[] values) {
        List<String> output = new ArrayList<>();
        Deque<String> operator = new ArrayDeque<>();

        for(String each : values) {
            if(each.isBlank()) continue;

            if(isDigit(each)) output.add(each);
            else if(each.equals("(")) operator.push(each);
            else if(each.equals(")")) {
                while(!operator.isEmpty() && !operator.peek().equals("(")) {
                    output.add(operator.pop());
                }
                if(!operator.isEmpty()) operator.pop();
            } else {
                while(!operator.isEmpty() && !operator.peek().equals("(") &&
                      !(isPriority(each) && !isPriority(operator.peek()))) {
                    output.add(operator.pop());
                }
                operator.push(each);
            }
        }
        while (!operator.isEmpty()) output.add(operator.pop());
        return output;
    }

    /**
     * 특정 토큰이 우선순위 연산자인지의 여부를 반환합니다.
     * @param operator 연산자 문자열
     * @return 우선순위 연산자(/, *) 인 경우 true, 그 외 false
     */
    private static boolean isPriority(String operator) {
        return operator.equals("/") || operator.equals("*");
    }
}
