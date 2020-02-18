package com.ydh.bookui.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.ydh.bookui.listeners.BookItemClickListener;
import com.ydh.bookui.listeners.FavoriteClickListener;
import com.ydh.bookui.R;
import com.ydh.bookui.model.Book;

import java.util.ArrayList;
import java.util.List;

// TODO: update all class related to book model changes
// TODO: create published year in detail activity, change date in main activity
// TODO: add filtered by favorite

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.bookViewHolder> implements Filterable {

    private List<Book> mdata;
    private List<Book> mdataFiltered;
    Context mContext;

    private BookItemClickListener bookItemClickListener;
    private FavoriteClickListener favoriteClickListener;

    private int id;


    public BookAdapter(Context mContext, List<Book> mdata, BookItemClickListener listener, FavoriteClickListener favlistener) {
        this.mContext = mContext;
        this.mdata = mdata;
        this.mdataFiltered = mdata;
        bookItemClickListener = listener;
        favoriteClickListener = favlistener;
    }

    @NonNull
    @Override
    public bookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book,parent,false);

        return new bookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookViewHolder holder, int position) {

        int fav = mdataFiltered.get(position).getFavorite();
        String author = "by "+ mdataFiltered.get(position).getAuthor();
        String date = getSubString(mdataFiltered.get(position).getDate()," ", "");
        String pagesReviews = mdataFiltered.get(position).getPages()+" pages";// +date;



        //bind book item data
        Glide.with(holder.itemView.getContext())
                .load(mdataFiltered.get(position).getCover())
                .transforms(new CenterCrop(),new RoundedCorners(32))
                .into(holder.imgBook);

        holder.ratingBar.setRating(mdataFiltered.get(position).getRating());
        holder.title.setText(mdataFiltered.get(position).getTitle());
        holder.rate.setText(String.valueOf(mdataFiltered.get(position).getRating()));
        holder.pages.setText(pagesReviews);
        holder.author.setText(author);
        holder.date.setText(date);
        if (fav == 1){
            holder.fav.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return mdataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String key = constraint.toString();
                if (key.isEmpty()){
                    mdataFiltered = mdata;
                }else {
                    List<Book> listFiltered = new ArrayList<>();
                    for (Book row: mdata){
                        if (row.getTitle().toLowerCase().contains(key.toLowerCase())){
                            listFiltered.add(row);
                        }
                    }
                    mdataFiltered = listFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mdataFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mdataFiltered = (List<Book>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class bookViewHolder extends RecyclerView.ViewHolder{

        ImageView imgBook,imgFav,background;
        TextView title,author,pages,rate, date;
        RatingBar ratingBar;
        CheckBox fav;
        int checked;

        bookViewHolder(@NonNull View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.item_book_img);
            //imgFav = itemView.findViewById(R.id.item_book_fav);
            title = itemView.findViewById(R.id.item_book_title);
            author = itemView.findViewById(R.id.item_book_author);
            pages = itemView.findViewById(R.id.item_book_pagesreviews);
            rate = itemView.findViewById(R.id.item_book_score);
            ratingBar = itemView.findViewById(R.id.item_book_ratingBar);
            background = itemView.findViewById(R.id.item_book_bg);
            fav = itemView.findViewById(R.id.item_book_fav);
            date = itemView.findViewById(R.id.item_book_date);

            imgBook.setOnClickListener(v -> bookItemClickListener.onBookClick(mdata.get(getAdapterPosition()),imgBook, title, author,pages,rate,ratingBar, background,date));
//
            fav.setOnClickListener(v -> {
                checked = 0;
                if (fav.isChecked()){
                    checked = 1;
                }
                //Message.message(itemView.getContext(),"pos: "+getAdapterPosition()+" stat:" +checked);
                favoriteClickListener.onFavoriteClick(mdata.get(getAdapterPosition()).getId(),checked);

            });
        }
    }

    private static String getSubString(String mainString, String lastString, String startString) {
        String endString;
        int endIndex = mainString.indexOf(lastString);
        int startIndex = mainString.indexOf(startString);
        Log.d("message", "" + mainString.substring(startIndex, endIndex));
        endString = mainString.substring(startIndex, endIndex);
        return endString;
    }

}
