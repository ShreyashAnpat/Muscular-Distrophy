package com.example.musculardistrophy.ui.newPost;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.musculardistrophy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class NewPostFragment extends Fragment {

    CircleImageView profile ;
    TextView userName  , post  ;
    String userID ;
    Uri imageUri ;
    ImageView camera  ,postImage;
    FirebaseFirestore firebaseFirestore;
    String profileUri  , userNameString ,isText ="";
    EditText caption ;
    FirebaseAuth auth ;
    StorageReference Folder , ImageName ;
    ConstraintLayout writeThought , AddImage , AddVideo ;
    ProgressDialog pd ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_newpost, container, false);
        profile  = root.findViewById(R.id.circleImageView2);
        userName = root.findViewById(R.id.username);
        camera = root.findViewById(R.id.camera);
        postImage = root.findViewById(R.id.postImage);
        caption = root.findViewById(R.id.captions);
        post = root.findViewById(R.id.post);
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth  = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();
        pd   =  new ProgressDialog(getContext());
        pd.setMessage("Uploading Post ...");
        pd.setCanceledOnTouchOutside(false);


        firebaseFirestore.collection("user").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null){
                    profileUri = value.getString("Profile") ;
                    userNameString = value.getString("userName") ;
                    Picasso.get().load(profileUri).placeholder(R.drawable.ic_baseline_account_circle_24).into(profile);
                    userName.setText(userNameString);
                }
            }
        });

        BottomSheetDialog bottomSheetDialog =  new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.bottomsheet);
        bottomSheetDialog.getDismissWithAnimation();
        bottomSheetDialog.show();

        writeThought = bottomSheetDialog.findViewById(R.id.constraintLayout3);
        writeThought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
                caption.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        AddImage = bottomSheetDialog.findViewById(R.id.constraintLayout4);
        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
                selectImage();
            }
        });

        AddVideo = bottomSheetDialog.findViewById(R.id.addVideo);
        AddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
                Toast.makeText(root.getContext(), "Coming Soon !", Toast.LENGTH_SHORT).show();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(caption.getWindowToken(), 0);
                selectImage();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(caption.getWindowToken(), 0);

                if (isText.equals("No")){
                    uploadPostWithImage(imageUri);
                }
                else {
                    if (caption.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "Empty post", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        uploadPost();
                    }
                }
            }
        });


        return root;
    }



    private void selectImage() {

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getActivity(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 imageUri = result.getUri();
                 postImage.setVisibility(View.VISIBLE);
                 postImage.setImageURI(imageUri);
                 if (imageUri != null){
                     isText = "No";
                 }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadPostWithImage(Uri imageUri) {
        pd.show();
        Folder = FirebaseStorage.getInstance().getReference().child("userPost");
        ImageName = Folder.child(auth.getCurrentUser().getUid() + imageUri.getLastPathSegment());
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        ImageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String , Object> upload = new HashMap<>();
                        upload.put("Caption" , caption.getText().toString());
                        upload.put("PostType" , "Image");
                        upload.put("Profile" , profileUri);
                        upload.put("TimeStamp" ,timeStamp );
                        upload.put("UID" , userID);
                        upload.put("post" , uri.toString());
                        upload.put("username" , userName.getText().toString());
                        upload.put("postID", timeStamp+userID);

                        firebaseFirestore.collection("post").document(timeStamp+userID).set(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();

                                NavController navController = Navigation.findNavController(getView());;
                                navController.navigate(R.id.navigation_home);
                            }
                        });
                    }
                });

            }
        });
    }

    private void uploadPost() {
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));

        HashMap<String , Object> upload = new HashMap<>();
        upload.put("Caption" , caption.getText().toString());
        upload.put("PostType" , "Text");
        upload.put("Profile" , profileUri);
        upload.put("TimeStamp" ,timeStamp );
        upload.put("UID" , userID);
        upload.put("post" , "");
        upload.put("username" , userName.getText().toString());
        upload.put("postID", timeStamp+userID);
        firebaseFirestore.collection("post").document().set(upload).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.cancel();
                NavController navController = Navigation.findNavController(getView());;
                navController.navigate(R.id.navigation_home);
            }
        });
    }
}