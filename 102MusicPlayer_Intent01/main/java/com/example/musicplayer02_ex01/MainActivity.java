package com.example.musicplayer02_ex01;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1000;
    private EditText nameEdit;
    private EditText ageEdit;
    private Context thisContext;

    ActivityResultLauncher<Intent> secondActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent resultData = result.getData();
                        String dataResultString = resultData.getStringExtra("result");
                        Toast.makeText(thisContext, dataResultString, Toast.LENGTH_SHORT).show();
                        // Context parameter = 전역에 선역해서 onCreate 주기내 따로 설정해준 값으로 할당 하는 방법이 우선 내가 생각한 최선.
                        // 전역 field에 선언된 member 변수의 callback override 이기 때문에 Activity.getApplicationContext(), this 등이 사용되지 못한다.
                        // getApplicationContext()는 singleton 을 유지 하고 있는 최상위 container 객체 이라고 볼 수 있기 때문에, Activity 지정할때 절대 사용하지 않아야 한다.
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEdit = findViewById(R.id.nameEdit);
        ageEdit = findViewById(R.id.ageEdit);
        findViewById(R.id.submitBtn).setOnClickListener(this);
        thisContext = this; //Activity context


    }

    @Override
    public void onClick(View v) {
        // intent 의 Extra 활용 (json key:val 쌍을 주로 put )
        //Extras [Android intent]
        //Key-value pairs that carry additional information required to accomplish the requested action.
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("name", nameEdit.getText().toString());
        intent.putExtra("age", ageEdit.getText().toString());

        secondActivityLauncher.launch(intent); //결과값을 다시 돌려 받고 싶을대, ActivityResultLauncher<Intent> 객체를 필드로 생성해서,
        // Callback 함수를 override 해줘야 한다.
        //StartActivityForResult(Intent) <--deprecated

    }
}