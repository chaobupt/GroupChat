package com.example.groupchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameActivity extends AppCompatActivity {

    private Button btnJoin;
    private EditText txtName;
    private EditText txtGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        btnJoin = (Button) findViewById(R.id.btnJoin);
        txtName = (EditText) findViewById(R.id.name);
        txtGroupId = (EditText) findViewById(R.id.groupId);

        btnJoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().trim().length() > 0) {

                    String name = txtName.getText().toString().trim();
                    int groupId = Integer.parseInt(txtGroupId.getText().toString().trim());
                    Intent intent = new Intent(NameActivity.this,
                            MainActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "请输入你的账号", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
