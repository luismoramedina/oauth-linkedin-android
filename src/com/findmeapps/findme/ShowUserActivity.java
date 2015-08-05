package com.findmeapps.findme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.findmeapps.findme.data.User;

import java.io.ByteArrayOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: user
 * Date: 3/11/12
 * Time: 20:51
 * To change this template use File | Settings | File Templates.
 */
public class ShowUserActivity extends Activity {
    private ImageButton avatarImageButton;
    private Uri selectedImageUri;
    private TextView userNameTextView;
    private TextView userLastNameTextView;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_user);
        userNameTextView = (TextView) findViewById(R.id.user_name_value);
        userLastNameTextView = (TextView) findViewById(R.id.user_last_name_value);
        userEmailTextView = (TextView) findViewById(R.id.user_email_value);
        avatarImageButton = (ImageButton) findViewById(R.id.user_avatar);
        Button saveButton = (Button) findViewById(R.id.save_user_button);

        if (selectedImageUri != null) {
            avatarImageButton.setImageURI(selectedImageUri);
        }

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            String firstName = bundle.getString(LinkedInActivity.FIRST_NAME);
            String summary = bundle.getString(LinkedInActivity.SUMMARY);
            userNameTextView.setText(firstName);
            userLastNameTextView.setText(summary);
        } else {
            //TODO TESTS
            userNameTextView.setText("Test");
            userLastNameTextView.setText("Last Test");
            userEmailTextView.setText("test@test.com");
            avatarImageButton.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.star_blue));
        }

        avatarImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_text)), 1);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Image from the ImageView into bytes
                byte[] imageRaw = getRawBytesFromImageView(avatarImageButton);

                String base64EncodedAvatar = Base64.encodeToString(imageRaw, Base64.DEFAULT);
                Log.d("FINDME", "Avatar toBase64: " + base64EncodedAvatar);

                User user = new User();
                user.email = userEmailTextView.getText().toString();
                user.name = userNameTextView.getText().toString();
                user.lastName = userLastNameTextView.getText().toString();
                user.base64Avatar = base64EncodedAvatar;

                user.save(view.getContext());
                //TODO make long toast
            }
        });

    }

    private byte[] getRawBytesFromImageView(ImageView imageView) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        selectedImageUri = data.getData();
        avatarImageButton.setImageURI(selectedImageUri);
        Log.d("FINDME", "selectedImageUri = " + selectedImageUri);
    }


}
