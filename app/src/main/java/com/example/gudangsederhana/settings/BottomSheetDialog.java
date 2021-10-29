package com.example.gudangsederhana.settings;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gudangsederhana.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.gudangsederhana.R.attr.alertDialogTheme;
import static com.example.gudangsederhana.R.attr.cardForegroundColor;
import static com.example.gudangsederhana.R.attr.colorOnBackground;
import static com.example.gudangsederhana.R.attr.colorSecondary;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    Integer max = 25;
    EditText editText;
    TextView addedKet_sp, tvEdLength;
    Button btSimpan, btBatal;
    String nonEdit, data, auth;
    DatabaseReference ref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = getLayoutInflater().inflate(R.layout.custom_dialog_settings_profil, null);
        //v.setBackgroundColor(colorSecondary);

        //View v = inflater.inflate(R.layout.custom_dialog_settings_profil, container, false);

        addedKet_sp = v.findViewById(R.id.addedKet_sp);
        tvEdLength = v.findViewById(R.id.tvEdLength);
        editText = v.findViewById(R.id.editText);
        btBatal = v.findViewById(R.id.btBatal_sp);
        btSimpan = v.findViewById(R.id.btSimpan_sp);
        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("Sellers").child(auth);

        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Sellers").child(auth);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (MenuSettingsProfile.getEdit.equals("shopName")){
                        max = 50;
                        data = "nama toko";
                        addedKet_sp.setText("Masukkan nama toko Anda");
                        editText.setText(snapshot.child("shopName").getValue().toString());
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                    } else if (MenuSettingsProfile.getEdit.equals("owner")){
                        max = 25;
                        data = "nama pemilik";
                        addedKet_sp.setText("Masukkan nama Anda");
                        editText.setText(snapshot.child("owner").getValue().toString());
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                    } else if (MenuSettingsProfile.getEdit.equals("address")){
                        max = 50;
                        data = "alamat";
                        addedKet_sp.setText("Masukkan alamat Anda");
                        editText.setText(snapshot.child("address").getValue().toString());
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                    } else if (MenuSettingsProfile.getEdit.equals("phoneNumber")){
                        max = 12;
                        data = "nomor telepon";
                        addedKet_sp.setText("Masukkan nomor telepon Anda");
                        editText.setText(snapshot.child("phoneNumber").getValue().toString());
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                        editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    } else {
                        editText.setText("-");
                    }
                    nonEdit = editText.getText().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btBatal.setOnClickListener(v1 -> {
            dismiss();
        });

        btSimpan.setOnClickListener(v1 -> {
            String edit = editText.getText().toString().trim();
            if (edit.isEmpty()) {
                editText.setError("Harus diisi..");
                editText.requestFocus();
            } else {
                if (nonEdit.equals(edit)){
                    editText.setError("Ubah terlebih dahulu..");
                    editText.requestFocus();
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put(MenuSettingsProfile.getEdit, edit);
                    ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(v.getContext(), "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Toast.makeText(v.getContext(), "Data gagal diubah", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }
                    });
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Integer l = max - s.length();
                if (s.toString().isEmpty()){
                    tvEdLength.setVisibility(View.INVISIBLE);
                } else {
                    tvEdLength.setVisibility(View.VISIBLE);
                    tvEdLength.setText(l.toString());
                }
            }
        });

        //return super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }
}
