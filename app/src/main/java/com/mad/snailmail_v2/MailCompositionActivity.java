package com.mad.snailmail_v2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class MailCompositionActivity extends AppCompatActivity {


    private Button mSendMailButton;
    private FirebaseUser mFirebaseUser;

    private EditText titleET;
    private EditText messageET;
    private DataSnapshot mFirebaseDatabaseReference;

    private static final String USER_FILTER = "user";
    private static final String USER_MAIL_FILTER = "mail";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_composition);

        titleET = (EditText)findViewById(R.id.composition_title);
        messageET = (EditText)findViewById(R.id.composition_message);

        mSendMailButton = (Button) findViewById(R.id.send);
        mSendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mail snailMail = new Mail();
                snailMail.senderId = mFirebaseUser.getDisplayName();
                snailMail.mailTitle = titleET.getText().toString();
                snailMail.mailMessage = messageET.getText().toString();

                // start intent to send back new snailmail object

            }
        });


    }
}
