package com.example.kalculator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private TextView txtExpression;         // 계산 과정 텍스트
    private TextView txtResult;             // 결과 값 텍스트
    private List<Integer> checkList;        // -1: 이콜, 0: 연산자, 1: 숫자, 2: . / 예외 발생을 막는 리스트
    private Stack<String> operatorStack;    // 연산자를 위한 스택
    private List<String> infixList;         // 중위 표기
    private List<String> postfixList;       // 후위 표기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.init();
    }

    // 필드 초기화
    void init() {
        // 해당 변수 아이디 불러오기
        txtExpression = findViewById(R.id.txt_expression);
        txtResult = findViewById(R.id.txt_result);
        checkList = new ArrayList<>();
        operatorStack = new Stack<>();
        infixList = new ArrayList<>();
        postfixList = new ArrayList<>();

        // 타이틀 바 없애기
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
    }

    // 숫자, 연산자 버튼 이벤트 처리
    public void buttonClick(View v) {
        // 체크리스트가 비어있지 않고 마지막이 이콜(=)일 때
        if (!checkList.isEmpty() && checkList.get(checkList.size() - 1) == -1) {
            txtExpression.setText(txtResult.getText().toString());
            checkList.clear();  // 체크리스트 클리어
            checkList.add(1);   // 정수
            checkList.add(2);   // .
            checkList.add(1);   // 소수점
            txtResult.setText("");
        }
        switch (v.getId()) {
            case R.id.btn_one:
                addNumber("1");
                break;
            case R.id.btn_two:
                addNumber("2");
                break;
            case R.id.btn_three:
                addNumber("3");
                break;
            case R.id.btn_four:
                addNumber("4");
                break;
            case R.id.btn_five:
                addNumber("5");
                break;
            case R.id.btn_six:
                addNumber("6");
                break;
            case R.id.btn_seven:
                addNumber("7");
                break;
            case R.id.btn_eight:
                addNumber("8");
                break;
            case R.id.btn_nine:
                addNumber("9");
                break;
            case R.id.btn_zero:
                addNumber("0");
                break;
            case R.id.btn_dot:
                addDot(".");
                break;

            case R.id.btn_division:
                addOperator("/");
                break;

            case R.id.btn_multi:
                addOperator("X");
                break;

            case R.id.btn_plus:
                addOperator("+");
                break;

            case R.id.btn_minus:
                addOperator("-");
                break;
        }
    }

    // 클리어 버튼 이벤트 처리
    public void clearClick(View v) {
        infixList.clear();
        checkList.clear();
        txtExpression.setText("");
        txtResult.setText("");
        operatorStack.clear();
        postfixList.clear();
    }

    // 지우기 버튼 이벤트 처리
    public void deleteClick(View v) {
        // 계산 과정 텍스트의 길이가 0이 아닐 때
        if (txtExpression.length() != 0) {
            // 체크리스트의 사이즈 = -1
            checkList.remove(checkList.size() - 1);
            // 계산 과정에 있는 텍스트를 띄어쓰기를 기준으로 나눈다
            String[] ex = txtExpression.getText().toString().split(" ");
            List<String> li = new ArrayList<String>();
            Collections.addAll(li, ex);
            li.remove(li.size() - 1);
            // li사이즈가 0보다 크고 && 리스트의 마지막이 숫자가 아니라면
            if (li.size() > 0 && !isNumber(li.get(li.size() - 1))) {
                // " " 빈칸 추가
                li.add(li.remove(li.size() - 1) + " ");
            }
            // 계산 텍스트에 li 값을 쉼표 표시 없이 변경한다
            txtExpression.setText(TextUtils.join(" ", li));
        }
        // 결과 텍스트에 ""으로 변경한다
        txtResult.setText("");
    }

    // 숫자 버튼 이벤트 처리
    void addNumber(String str) {
        checkList.add(1); // 체크리스트의 배열에 1(숫자) 추가
        txtExpression.append(str); // append는 setText와 달리 기존내용을 유지한채 뒤에 붙여준다
    }

    // . 버튼 이벤트 처리
    void addDot(String str) {
        // 만약 텍스트뷰가 비어있을 때
        if (checkList.isEmpty()) {
            Toast.makeText(getApplicationContext(), ". 을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 만약 마지막이 숫자가 아닐때
        else if (checkList.get(checkList.size() - 1) != 1) {
            Toast.makeText(getApplicationContext(), ". 을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 하나의 수에 . 이 여러 개 오는 것을 막기
        // 한자리 수는 .이 올 수 있는 장소가 정해져 있어서 for문에 들어오지 않는다
        for (int i = checkList.size() - 2; i >= 0; i--) {
            int check = checkList.get(i);
            // 만약 하나의 수에 .이 2개 이상 올때
            if (check == 2) {
                Toast.makeText(getApplicationContext(), ". 을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (check == 0) break;
            if (check == 1) continue;
        }
        // 체크리스트의 배열에 2(".") 추가
        checkList.add(2);
        txtExpression.append(str); // append는 setText와 달리 기존내용을 유지한채 뒤에 붙여준다
    }

    // 연산자 버튼 이벤트 처리
    void addOperator(String str) {
        try {

            // 만약 텍스트 뷰가 비어있을 때
            if (checkList.isEmpty()) {
                // 처음 연산자 사용 막기
                Toast.makeText(getApplicationContext(), "연산자가 올 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            // 만약 마지막이 연산자거나 "."일 때
            if (checkList.get(checkList.size() - 1) == 0 && checkList.get(checkList.size() - 1) == 2) {
                // 연산자 두 번 사용, 완벽한 수가 오지 않았을 때 막기
                System.out.println("값 : " + checkList);
                Toast.makeText(getApplicationContext(), "연산자가 올 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (checkList.size() > 0) {
                if (checkList.get(checkList.size() - 1) == 0) {
                    return;
                }
            }

            checkList.add(0);                       // 체크리스트의 배열에 0(연산자) 추가
            txtExpression.append(" " + str + " ");  // append는 setText와 달리 기존내용을 유지한채 뒤에 붙여준다
            System.out.println("리스트 값 : " + checkList);

        } catch (Exception e) {
            Log.e("addOperator", e.toString());
        }
    }

    // 이콜 버튼 이벤트 처리
    public void equalClick(View v) {
        // 만약 텍스트뷰가 비어있다면
        if (txtExpression.length() == 0) return;
        // 만약 마지막이 숫자가 아닐 때
        if (checkList.get(checkList.size() - 1) != 1) {
            Toast.makeText(getApplicationContext(), "숫자를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Collections.addAll(infixList, txtExpression.getText().toString().split(" "));
        // 체크리스트의 배열에 -1(이콜) 추가
        checkList.add(-1);
        result();
    }


    // 연산자 가중치 (우선순위 *,/,%,+,-)
    // 연산자의 우선순위에 따라 가중치를 부여한다
    int getWeight(String operator) {
        int weight = 0;
        switch (operator) {
            case "X":
            case "/":
                weight = 5;
                break;
            case "%":
                weight = 3;
                break;
            case "+":
            case "-":
                weight = 1;
                break;
        }
        return weight;
    }

    // 숫자 판별
    boolean isNumber(String str) {
        boolean result = true;
        try {
            Double.parseDouble(str);        // 문자열을 실수로 변환
        } catch (NumberFormatException e) {
            result = false;                 // 에러가 날 경우 false를 반환한다
        }
        return result;                      // 에러가 나지 않을 경우 true를 반환한다.
    }

    // 중위 -> 후위
    void infixToPostfix() {
        // item에 infixList의 리스트값을 넣어준다
        // infixList의 리스트 개수만큼 for문 반복
        for (String item : infixList) {
            // item 값이 숫자라면 후위연산 리스트에 item값 삽입
            if (isNumber(item)) postfixList.add(String.valueOf(Double.parseDouble(item)));
            else {  // item 값이 연산자라면
                if (operatorStack.isEmpty())
                    operatorStack.push(item);  // 만약 연산자 스택이 비어있다면 item 값을 operator스택에 삽입
                else {  // 만약 연산자 스택이 비어있지 않다면
                    // 연산자 스택의 맨 위에 저장된 객체가 아이템보다 우선순위가 높다면 해당 객체를 후위연산 리스트에 삽입한다.
                    if (getWeight(operatorStack.peek()) >= getWeight(item))
                        postfixList.add(operatorStack.pop());
                    operatorStack.push(item);
                }
            }
        }
        // 연산자 스택이 비어있지 않을때 연산자 스택값을 빼서 후위연산 리스트에 추가
        while (!operatorStack.isEmpty()) postfixList.add(operatorStack.pop());
    }

    // 계산
    String calculate(String num1, String num2, String op) {
        // 첫번째 숫자
        double first = Double.parseDouble(num1);
        // 두번째 숫자
        double second = Double.parseDouble(num2);
        // 결과 값
        double result = 0.0;
        try {
            switch (op) {   // 계산
                case "X":
                    result = first * second;
                    break;
                case "/":
                    result = first / second;
                    break;
                case "%":
                    result = first % second;
                    break;
                case "+":
                    result = first + second;
                    break;
                case "-":
                    result = first - second;
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "연산할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
        return String.valueOf(result);  // 결과 값 반환
    }

    // 최종 결과
    void result() {
        int i = 0;
        // 중위 연산 -> 후위 연산
        infixToPostfix();
        // 후위 연산 리스트 사이즈가 1이 아닐 때
        while (postfixList.size() != 1) {
            // 후위 연산 리스트 i번째가 숫자가 아닐 때
            if (!isNumber(postfixList.get(i))) {
                // i-2번째의 리스트 아이템을 삭제(삭제될시 i번째 이후의 아이템 포지션이 당겨진다)하며 인자값으로 전달하여 calculate를 불러온다
                postfixList.add(i - 2, calculate(postfixList.remove(i - 2), postfixList.remove(i - 2), postfixList.remove(i - 2)));
                // 처음부터 다시 찾기 위해 i를 초기화해준다
                i = -1;
            }
            i++;
        }
        // 최종 결과값을 텍스트에 지정
        txtResult.setText(postfixList.remove(0));
        // 중위 연산 리스트 클리어
        infixList.clear();
    }
}