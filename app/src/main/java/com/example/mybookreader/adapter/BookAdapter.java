package com.example.mybookreader.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybookreader.MainScreen;
import com.example.mybookreader.PDFOpener;
import com.example.mybookreader.R;
import com.example.mybookreader.model.Book;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> implements Filterable {
    private final Context mContext;
    private List<Book> mListBook;

    public BookAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Book> mListBook) {
        this.mListBook = mListBook;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_book_view, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Book book = mListBook.get(position);
        if (book == null)
            return;

        File imgFile = new File(book.getCoverPath());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imgCover.setImageBitmap(myBitmap);
        }
        holder.tvName.setText(book.getName());

        holder.layoutItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOpenBook(book.getPath());
            }
        });

        holder.layoutItems.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.read:
                                onClickOpenBook(book.getPath());
                                break;
                            case R.id.delete:
                                deleteItem(position, view);
                                break;
                        }
                        return false;
                    }
                });
                return false;
            }
        });
    }

    private void deleteItem(int position, View view) {
        Book tempBook = mListBook.get(position);
        int id = tempBook.getId();
        //int tempId = 0;

        mListBook.remove(position);
        for (int i = 0; i < MainScreen.listBook.size(); i++) {
            if (MainScreen.listBook.get(i).getId() == id) {
                MainScreen.listBook.remove(i);
                //tempId = i;
                break;
            }
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mListBook.size());
        notifyItemRangeChanged(position, MainScreen.listBook.size());
    }

    private void onClickOpenBook(String uri) {
        Intent intent = new Intent(mContext, PDFOpener.class);
        intent.putExtra("FileUri", uri);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if (mListBook != null)
            return mListBook.size();
        return 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        private CardView layoutItems;
        private ImageView imgCover;
        private TextView tvName;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutItems = itemView.findViewById(R.id.layoutItem);
            imgCover = itemView.findViewById(R.id.im_cover);
            tvName = itemView.findViewById(R.id.tv_name);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    mListBook = MainScreen.listBook;
                } else {
                    List<Book> list = new ArrayList<>();
                    for (Book it : MainScreen.listBook) {
                        if (it.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(it);
                        }
                    }

                    mListBook = list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mListBook;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mListBook = (List<Book>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
