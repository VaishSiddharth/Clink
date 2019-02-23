package com.testlabic.datenearu.NewUserSetupUtils;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.testlabic.datenearu.Models.ModelUser;
import com.testlabic.datenearu.R;
import com.testlabic.datenearu.Utils.Constants;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Name extends Fragment implements BlockingStep {
    EditText inputName, inputLastName;
    ImageView next;
    DatabaseReference reference;
    View rootView;
    SweetAlertDialog dialog;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.activity_name, container, false);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        next = rootView.findViewById(R.id.next);
        inputName = rootView.findViewById(R.id.input_name);
        inputLastName = rootView.findViewById(R.id.input_name_last);
    
        dialog =  new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("In progress")
                .setContentText(".....");
        
        @SuppressLint("RestrictedApi") String uid = mAuth.getUid();
        if (uid != null) {
            reference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.userInfo)
                    .child(uid);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                    ModelUser user = dataSnapshot.getValue(ModelUser.class);
                    if(user!=null)
                    {
                        String firstName = user.getUserName();
                        String lastName = user.getUserLastName();
                        inputName.setText(firstName);
                        inputLastName.setText(lastName);
                    }
                }
            
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                
                }
            });
            String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        
        }
        return rootView;
    }
    
  
    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }
    
    @Override
    public void onSelected() {
    
    }
    
    @Override
    public void onError(@NonNull VerificationError error) {
    
    }
    
    @Override
    public void onNextClicked(final StepperLayout.OnNextClickedCallback callback) {

        dialog.show();
        callback.getStepperLayout().showProgress("Operation in progress, please wait...");
        String name = inputName.getText().toString();
        String lastName = inputLastName.getText().toString();
        if(name.matches("") && lastName.matches(""))
        {
            Toast.makeText(getActivity(), "Your name can't be nothing, type it first", Toast.LENGTH_SHORT).show();
        }
        else {
            if(reference!=null)
            {
                HashMap<String, Object> userNameToUpdate = new HashMap<>();
                HashMap<String, Object> userLastNameToUpdate = new HashMap<>();
                userNameToUpdate.put("userName", name);
                userLastNameToUpdate.put("userLastName", lastName);
                reference.updateChildren(userLastNameToUpdate);
                reference.updateChildren(userNameToUpdate)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                     callback.goToNextStep();
                                dialog.dismiss();
                            }
                        });
            }
        }
        
    }
    
    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
    
    }
    
    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
    
    }
}
