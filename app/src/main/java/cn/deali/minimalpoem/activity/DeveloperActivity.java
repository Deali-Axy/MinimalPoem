package cn.deali.minimalpoem.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.deali.minimalpoem.R;
import cn.deali.minimalpoem.view.FlaskView;

public class DeveloperActivity extends AppCompatActivity {
    private FlaskView flaskView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        flaskView = findViewById(R.id.fv);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        flaskView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            flaskView.release();
        else
            flaskView.stop();
    }

    public void onStart(View view) {
        ((FlaskView) findViewById(R.id.fv)).start();
    }

    public void onStop(View view) {
        ((FlaskView) findViewById(R.id.fv)).stop();
    }
}
