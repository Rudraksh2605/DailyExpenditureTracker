package com.hfad.dailyexpendituretracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Data;


public class IncomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference IncomeDatabase;
    private TextView IncomeSumResult;
    private RecyclerView recyclerview;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);
        mAuth =FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String Uid = mUser.getUid();
        IncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(Uid);
        IncomeSumResult = view.findViewById(R.id.income_txt_result);
        recyclerview = view.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(layoutManager);
        IncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int incomeSum = 0;
                for(DataSnapshot mydataSnapshot : snapshot.getChildren()){
                    Data data = mydataSnapshot.getValue(Data.class);
                    incomeSum+= data.getAmount();

                    String stIncomeSum = String.valueOf(incomeSum);
                    IncomeSumResult.setText(stIncomeSum);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

   @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(IncomeDatabase, Data.class)
                .setLifecycleOwner(this)
                .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i, @NonNull Data data) {

                viewHolder.setType(data.getType());
                viewHolder.setNote(data.getNote());
                viewHolder.setDate(data.getDate());
                viewHolder.setAmount(data.getAmount());
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
                return new MyViewHolder(view);
            }
        };
        recyclerview.setAdapter(adapter);

    }


  public static class MyViewHolder extends RecyclerView.ViewHolder {

      View mView;

      public MyViewHolder(@NonNull View itemView) {
          super(itemView);
          mView = itemView;
      }

      public void setDate(String date) {
          TextView mDate = mView.findViewById(R.id.date_txt_income);
          mDate.setText(date);
      }

      public void setType(String type) {
          TextView mType = mView.findViewById(R.id.type_txt_income);
          mType.setText(type);
      }

      public void setNote(String note) {
          TextView mNote = mView.findViewById(R.id.note_txt_income);
          mNote.setText(note);
      }

      public void setAmount(int amount) {
          TextView mAmount = mView.findViewById(R.id.amount_txt_income);
          String stAmount = String.valueOf(amount);
          mAmount.setText(stAmount);
      }
  }


}