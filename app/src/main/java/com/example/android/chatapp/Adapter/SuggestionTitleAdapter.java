package com.example.android.chatapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.chatapp.Model.Title;
import com.example.android.chatapp.R;

import java.util.List;

public class SuggestionTitleAdapter extends RecyclerView.Adapter<SuggestionTitleAdapter.ViewHolder> {


    private Context mContext;
    private List<Title> mTitle;
    private SuggestionTitleAdapter.SuggestionTitleListRecyclerClickListener mClickListener;

    public SuggestionTitleAdapter(Context mContext, List<Title> mTitle, SuggestionTitleAdapter.SuggestionTitleListRecyclerClickListener mClickListener){
        this.mTitle = mTitle;
        this.mContext = mContext;
        this.mClickListener = mClickListener;}


    @NonNull
    @Override
    public SuggestionTitleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.title_item, parent, false);
        return new SuggestionTitleAdapter.ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionTitleAdapter.ViewHolder holder, int position) {

        final Title title = mTitle.get(position);
        holder.title.setText(title.getTitle());

    }

    @Override
    public int getItemCount() {
        return mTitle.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        SuggestionTitleAdapter.SuggestionTitleListRecyclerClickListener mClickListener;

        public ViewHolder(View itemView, SuggestionTitleAdapter.SuggestionTitleListRecyclerClickListener clickListener) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            mClickListener = clickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mClickListener.onSuggestionTitleClicked(getAdapterPosition());
        }
    }

    public interface SuggestionTitleListRecyclerClickListener{
        void onSuggestionTitleClicked(int position);
    }
}
