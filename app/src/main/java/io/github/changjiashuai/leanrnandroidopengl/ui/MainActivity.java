package io.github.changjiashuai.leanrnandroidopengl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import io.github.changjiashuai.leanrnandroidopengl.R;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                navigateToActivity(FrameBufferActivity.class);
                break;
            case 1:
                navigateToActivity(HelloTriangleActivity.class);
                break;
            case 2:
                navigateToActivity(ShadersActivity.class);
                break;
            case 3:
                navigateToActivity(TextureActivity.class);
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    private void navigateToActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}