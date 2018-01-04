package liyi.com.test4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtScore;
    private EditText mEtId;
    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEtScore = (EditText) findViewById(R.id.et_score);
        mEtId = (EditText) findViewById(R.id.et_id);
        mBtn = (Button) findViewById(R.id.btn_confirm);
        mBtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        final String score = mEtScore.getText().toString().trim();
        final String id = mEtId.getText().toString().trim();
        if (score.isEmpty() || id.isEmpty()) {
            Toast.makeText(this , "不能为空" , Toast.LENGTH_SHORT).show();
        } else {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Util.postData(id , score);
                }
            };
            thread.start();
        }
    }
}
