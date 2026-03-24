package com.example.practiceapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView rvBookGrid;
    private TextView tvNoBooksFound;
    private BookAdapter adapter;
    private List<BookModel> realBooks;
    private String currentRole; // Store role here

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        // --- READ THE ROLE ---
        currentRole = getArguments() != null ? getArguments().getString("ROLE") : "USER";

        rvBookGrid = view.findViewById(R.id.rvBookGrid);
        tvNoBooksFound = view.findViewById(R.id.tvNoBooksFound);
        rvBookGrid.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        realBooks = new ArrayList<>();
        loadBooksFromDatabase();

        adapter = new BookAdapter(realBooks, this::showBookDetailsDialog);
        rvBookGrid.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextSize(16);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        FloatingActionButton fabAddBook = view.findViewById(R.id.fabAddBook);

        // --- ROLE-BASED FAB VISIBILITY ---
        if ("ADMIN".equals(currentRole)) {
            fabAddBook.setVisibility(View.VISIBLE);
            fabAddBook.setOnClickListener(v -> showAddBookMenu());
        } else {
            fabAddBook.setVisibility(View.GONE); // Hide Add button for Users
        }

        return view;
    }

    private void showBookDetailsDialog(BookModel book) {
        DBH dbHelper = new DBH(requireContext());
        dbHelper.IncrementBookView(book.getId());
        book.setViewCount(book.getViewCount() + 1);

        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_book_detail);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView ivCover = dialog.findViewById(R.id.dialogImage);
        TextView tvTitle = dialog.findViewById(R.id.dialogTitle);
        TextView tvGenreDate = dialog.findViewById(R.id.dialogGenreDate);
        TextView tvPurpose = dialog.findViewById(R.id.dialogPurpose);
        TextView tvDescription = dialog.findViewById(R.id.dialogDescription);
        android.widget.Button btnClose = dialog.findViewById(R.id.dialogBtnClose);
        android.widget.Button btnDelete = dialog.findViewById(R.id.dialogBtnDelete);

        ivCover.setImageResource(book.getCoverImageResourceId());
        tvTitle.setText(book.getTitle());
        tvGenreDate.setText("By " + book.getAuthor() + " • " + book.getGenre() + " • " + book.getDatePublished());
        tvPurpose.setText(book.getPurpose());
        tvDescription.setText(book.getDescription());

        // Get LayoutParams to adjust margins dynamically
        android.widget.LinearLayout.LayoutParams closeParams = (android.widget.LinearLayout.LayoutParams) btnClose.getLayoutParams();

        // --- ROLE-BASED DELETE VISIBILITY & MARGIN FIX ---
        if ("ADMIN".equals(currentRole)) {
            btnDelete.setVisibility(View.VISIBLE);

            // ADMIN: Keep the 8dp gap between the two buttons
            closeParams.setMarginStart((int) (8 * getResources().getDisplayMetrics().density));

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Book")
                        .setMessage("Are you sure you want to delete '" + book.getTitle() + "'?")
                        .setPositiveButton("Delete", (confirmDialog, which) -> {
                            if (dbHelper.DeleteBook(book.getId())) {
                                realBooks.remove(book);
                                adapter.setFilteredList(realBooks);
                                dialog.dismiss();
                                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Book Deleted", Snackbar.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        } else {
            // USER: Hide Delete button
            btnDelete.setVisibility(View.GONE);

            // Remove the left margin so the Close button centers perfectly
            closeParams.setMarginStart(0);
        }

        // Apply the updated margin to the Close button
        btnClose.setLayoutParams(closeParams);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void loadBooksFromDatabase() {
        DBH dbHelper = new DBH(requireContext());
        Cursor cursor = dbHelper.ReadAllBooks();
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
                int views = cursor.getInt(cursor.getColumnIndexOrThrow("view_count"));
                realBooks.add(new BookModel(id, title, author, imageRes, desc, date, genre, purpose, views));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void filterBooks(String text) {
        List<BookModel> filteredList = new ArrayList<>();
        for (BookModel book : realBooks) {
            if (book.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(book);
            }
        }
        if (filteredList.isEmpty()) {
            tvNoBooksFound.setVisibility(View.VISIBLE);
            rvBookGrid.setVisibility(View.GONE);
        } else {
            tvNoBooksFound.setVisibility(View.GONE);
            rvBookGrid.setVisibility(View.VISIBLE);
        }
        adapter.setFilteredList(filteredList);
    }

    private void showAddBookMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add a New Book");
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.add_book_layout, null);
        builder.setView(view);

        EditText etTitle = view.findViewById(R.id.etAddTitle);
        EditText etAuthor = view.findViewById(R.id.etAddAuthor);
        EditText etGenre = view.findViewById(R.id.etAddGenre);
        EditText etDate = view.findViewById(R.id.etAddDate);
        EditText etPurpose = view.findViewById(R.id.etAddPurpose);
        EditText etDesc = view.findViewById(R.id.etAddDesc);
        Spinner spinnerImageSelect = view.findViewById(R.id.spinnerImageSelect);
        ImageView ivAddBookPreview = view.findViewById(R.id.ivAddBookPreview);

        String[] imageOptions = {"Cookbook Cover", "Cryptography Cover", "Default Placeholder"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_flex, imageOptions);
        spinnerImageSelect.setAdapter(spinnerAdapter);

        spinnerImageSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = imageOptions[position];
                if (selection.equals("Cookbook Cover")) ivAddBookPreview.setImageResource(R.drawable.cookbook);
                else if (selection.equals("Cryptography Cover")) ivAddBookPreview.setImageResource(R.drawable.cryptography);
                else ivAddBookPreview.setImageResource(R.drawable.ic_launcher_background);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newTitle = etTitle.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                int selectedImage = R.drawable.cookbook;
                String selection = spinnerImageSelect.getSelectedItem().toString();
                if (selection.equals("Cryptography Cover")) selectedImage = R.drawable.cryptography;
                else if (selection.equals("Default Placeholder")) selectedImage = R.drawable.ic_launcher_background;

                DBH dbHelper = new DBH(requireContext());
                long newRowId = dbHelper.InsertBook(newTitle, etAuthor.getText().toString(), selectedImage, etDesc.getText().toString(), etDate.getText().toString(), etGenre.getText().toString(), etPurpose.getText().toString());

                if (newRowId != -1) {
                    realBooks.add(new BookModel((int) newRowId, newTitle, etAuthor.getText().toString(), selectedImage, etDesc.getText().toString(), etDate.getText().toString(), etGenre.getText().toString(), etPurpose.getText().toString(), 0));
                    adapter.setFilteredList(realBooks);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}