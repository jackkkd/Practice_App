package com.example.practiceapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvRecentBooks;
    private RecentBookAdapter adapter;
    private List<BookModel> recentBooksList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvRecentBooks = view.findViewById(R.id.rvRecentBooks);
        // Use a standard vertical list instead of a grid
        rvRecentBooks.setLayoutManager(new LinearLayoutManager(requireContext()));

        recentBooksList = new ArrayList<>();
        loadRecentBooks();

        adapter = new RecentBookAdapter(recentBooksList);
        rvRecentBooks.setAdapter(adapter);

        return view;
    }

    private void loadRecentBooks() {
        DBH dbHelper = new DBH(requireContext());
        Cursor cursor = dbHelper.GetRecentBooks(); // Uses our new method!

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("book_id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                int imageRes = cursor.getInt(cursor.getColumnIndexOrThrow("image_res"));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date_published"));
                String genre = cursor.getString(cursor.getColumnIndexOrThrow("genre"));
                String purpose = cursor.getString(cursor.getColumnIndexOrThrow("purpose"));
                int views = cursor.getInt(cursor.getColumnIndexOrThrow("view_count")); // Grab views!

                recentBooksList.add(new BookModel(id, title, author, imageRes, desc, date, genre, purpose, views));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    // --- INNER CLASS ADAPTER FOR THE RECENT LIST ---
    private class RecentBookAdapter extends RecyclerView.Adapter<RecentBookAdapter.RecentViewHolder> {

        private List<BookModel> books;

        public RecentBookAdapter(List<BookModel> books) {
            this.books = books;
        }

        @NonNull
        @Override
        public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_book, parent, false);
            return new RecentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
            BookModel book = books.get(position);
            holder.tvTitle.setText(book.getTitle());
            holder.tvAuthor.setText(book.getAuthor());
            holder.tvViews.setText(String.valueOf(book.getViewCount()));
            holder.ivCover.setImageResource(book.getCoverImageResourceId());
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        class RecentViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvAuthor, tvViews;
            ImageView ivCover;

            public RecentViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvRecentTitle);
                tvAuthor = itemView.findViewById(R.id.tvRecentAuthor);
                tvViews = itemView.findViewById(R.id.tvRecentViews);
                ivCover = itemView.findViewById(R.id.ivRecentCover);
            }
        }
    }
}