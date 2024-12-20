package activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cite_ims.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button enterButton = findViewById(R.id.enterButton);

        enterButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }
}
