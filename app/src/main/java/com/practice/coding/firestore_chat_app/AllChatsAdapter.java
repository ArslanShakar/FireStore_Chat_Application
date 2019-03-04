package com.practice.coding.firestore_chat_app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AllChatsAdapter extends RecyclerView.Adapter<AllChatsAdapter.VH> {

    private Context context;
    private ArrayList<ChatModel> arrayList;
    private String currentUser;

    public AllChatsAdapter(Context context, ArrayList<ChatModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_chat, viewGroup, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {
        if (currentUser.equals(arrayList.get(i).getReceiverId())) {
            vh.tvName.setText(arrayList.get(i).getUserId());
        } else {
            vh.tvName.setText(arrayList.get(i).getReceiverId());
        }

        vh.tvChatDescription.setText(arrayList.get(i).getMessage());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName, tvChatDescription;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvChatDescription = itemView.findViewById(R.id.tvChatDescription);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ChatActivity.class);
            if (currentUser.equals(arrayList.get(getAdapterPosition()).getReceiverId())) {
                intent.putExtra(Constants.SENDER_ID_KEY, arrayList.get(getAdapterPosition()).getReceiverId());
                intent.putExtra(Constants.RECEIVER_ID_KEY, arrayList.get(getAdapterPosition()).getUserId());
            } else {
                intent.putExtra(Constants.SENDER_ID_KEY, arrayList.get(getAdapterPosition()).getUserId());
                intent.putExtra(Constants.RECEIVER_ID_KEY, arrayList.get(getAdapterPosition()).getReceiverId());
            }
            context.startActivity(intent);
        }
    }
}
