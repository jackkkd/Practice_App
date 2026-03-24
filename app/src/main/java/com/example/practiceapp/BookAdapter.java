package com.example.practiceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<BookModel> bookList;
    private OnBookClickListener listener;

    // 1. We create an interface. This is our "bridge" back to the Dashboard.
    public interface OnBookClickListener {
        void onBookClick(BookModel book);
    }

    // 2. We update the constructor to require this listener when the adapter is created.
    public BookAdapter(List<BookModel> bookList, OnBookClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    public void setFilteredList(List<BookModel> filteredList) {
        this.bookList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookModel currentBook = bookList.get(position);
        holder.tvBookTitle.setText(currentBook.getTitle());
        holder.ivBookCover.setImageResource(currentBook.getCoverImageResourceId());

        // 3. When the card is clicked, we send the specific book over the bridge!
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBookClick(currentBook);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle;
        ImageView ivBookCover;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
        }
    }
}