package com.example.tutorial07_nav;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    EditText test ;
    String share = "file";
    //EditText 객체 test
    // String share 변수 생성
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // xml id 와 연결하고  sharedPreferences 객체 활용해 디스트로이 주기에서  text 를 불러와 주고 받는다
        test = (EditText)findViewById(R.id.test);
        SharedPreferences sharedPreferences= getSharedPreferences(share,  0);
        String value = sharedPreferences.getString("hong", "");
        // 먼저 onDestroy 함수로  뒤로 가기나 꺼졌을때 가져올 text 정보를 담아 줘야 한다
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // sharedPreferences 객체 활용해 임의로 선언한 share 라는 String 변수를 연동 시켜준다. 기본 모드= 0으로 하자
        SharedPreferences sharedPreferences= getSharedPreferences(share,  0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // sharedPrefeerence Editor 활용해 editor 객체를 생성하고 edit 정보를 담을 수 있게 활성 화 해준다
        // EditText에 입력된 값을 받아 value에 넘겨주고
        String value = test.getText().toString();
        // editor 객체의 putString 메서드를 활용해 값을 담아준다 (key값 , 담을 text 정보)
        editor.putString("hong", value);
        // 커밋 시켜준다
        editor.commit();
    }
//위의 onCreate 생명 주기에서 같은 방식으로 sahredPreference 객체를 생성하고 getString 메서드를 통해/
// 지정한 key값을 통해 text 값을 불러온다 deflaut 인다로 빈 문자열을 설정해준다.
}