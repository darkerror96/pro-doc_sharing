package sem8.integrate.app.mainapp_1.Login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sem8.integrate.app.mainapp_1.Admin.Admin_Home;
import sem8.integrate.app.mainapp_1.R;

public class Admin_Login extends AppCompatActivity {

    FirebaseAuth mAuth;

    Button login_btn;
    EditText pwd_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_admin_login_activity);

        mAuth = FirebaseAuth.getInstance();

        pwd_et = (EditText)findViewById(R.id.pwd_et);
        pwd_et.requestFocus();

        login_btn = (Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwd_et.getText().length() >= 6)
                {
                    mAuth.signInWithEmailAndPassword("admin@ddu.com", pwd_et.getText().toString())
                            .addOnCompleteListener(Admin_Login.this, new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Intent i = new Intent(Admin_Login.this, Admin_Home.class);
                                        startActivity(i);
                                    }
                                    else
                                    {
                                        Toast.makeText(Admin_Login.this, "Login Failed. Try again...", Toast.LENGTH_SHORT).show();
                                        pwd_et.setText("");
                                        pwd_et.requestFocus();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(Admin_Login.this, "Enter Password Correctly...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
