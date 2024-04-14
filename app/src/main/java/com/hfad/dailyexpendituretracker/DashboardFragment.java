package com.hfad.dailyexpendituretracker;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.app.AlertDialog;
import android.health.connect.datatypes.StepsCadenceRecord;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.text.DateFormat;

import Model.Data;


public class DashboardFragment extends Fragment {

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //Animation Class
    private Animation FadeOpen ,FadeCLose;

    private boolean isOpen = false;

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    // FireBase

    private FirebaseAuth mAuth;
    private DatabaseReference IncomeDatabase;
    private DatabaseReference ExpenseDatabase;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_dashboard, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        IncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        ExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);



        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);

        //DashBoard Income And Expense
        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);


        //Animation Connect
        FadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeCLose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);


        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();

                if(isOpen) {
                    fab_income_btn.startAnimation(FadeCLose);
                    fab_expense_btn.startAnimation(FadeCLose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeCLose);
                    fab_expense_txt.startAnimation(FadeCLose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                }
                else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }


            }

        });

        //total income
        IncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum = 0;
                for(DataSnapshot mysnap : snapshot.getChildren()){
                    Data data = mysnap.getValue(Data.class);
                    totalsum = totalsum + data.getAmount();
                    String stResult = String.valueOf(totalsum);
                    totalIncomeResult.setText(stResult);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //total Expense
        ExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalexpense = 0;
                for (DataSnapshot snap : snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalexpense += data.getAmount();
                }
                String stTotalExpense = String.valueOf(totalexpense);
                totalExpenseResult.setText(stTotalExpense);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;


    }



    private void addData(){
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();


            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expensedataInsert();

            }
        });



    }

    public void incomeDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_insertdata, null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        EditText editAmount = myview.findViewById(R.id.amount_edit);
        EditText editType = myview.findViewById(R.id.type_edit);
        EditText editNote = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnsave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String type = editType.getText().toString().trim();
                String amount = editAmount.getText().toString().trim();
                String note = editNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    editType.setError("Required Field");
                    return;
                }

                if (TextUtils.isEmpty(note)) {
                    editNote.setError("Required Field");
                    return;
                }


                if (TextUtils.isEmpty(amount)) {
                    editAmount.setError("Required Field");
                    return;
                }

                int ouramountint = Integer.parseInt(amount);

                String id = IncomeDatabase.push().getKey();

                String mDate = DateFormat.getDateInstance().format(new Date());


                Data data = new Data(ouramountint, type, note, id, mDate);

                IncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

            }


        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    public void expensedataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_insertdata,null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();

        EditText amount = myview.findViewById(R.id.amount_edit);
        EditText type = myview.findViewById(R.id.type_edit);
        EditText note = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnsave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmount = amount.getText().toString().trim();
                String tmType = type.getText().toString().trim();
                String tmNote = note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Required Field");
                    return;
                }

                int inamount = Integer.parseInt(tmAmount);
                if (TextUtils.isEmpty(tmNote)){
                    note.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(tmType)){
                    type.setError("Required Field");
                    return;
                }

                String id = ExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inamount, tmType, tmNote, id, mDate);
                ExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}