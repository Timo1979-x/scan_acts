package gto.by.acts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectActionActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bProcessActs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_action);

        bProcessActs = (Button)findViewById(R.id.bProcessActs);
        bProcessActs.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bProcessActs:
                Intent intent = new Intent(this, ScanActsActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
