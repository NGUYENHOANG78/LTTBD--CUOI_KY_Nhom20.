package com.example.movie.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.movie.MyApplication;
import com.example.movie.R;
import com.example.movie.activity.PlayMovieActivity;
import com.example.movie.adapter.MovieAdapter;
import com.example.movie.model.Movie;
import com.example.movie.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private MaterialDialog progressDialog;
    private List<Movie> listMovies;
    private MovieAdapter movieAdapter;
    private EditText edtSearchName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rcvHome = view.findViewById(R.id.rcv_home);
        edtSearchName = view.findViewById(R.id.edt_search_name);
        ImageView imgSearch = view.findViewById(R.id.img_search);
        if (getActivity() != null) {
            progressDialog = new MaterialDialog.Builder(getActivity())
                    .content(R.string.waiting_message)
                    .progress(true, 0)
                    .build();
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rcvHome.setLayoutManager(gridLayoutManager);
        listMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(listMovies, getActivity(), new MovieAdapter.IClickItemListener() {
            @Override
            public void onClickItem(Movie movie) {
                onClickItemMovie(movie);
            }

            @Override
            public void onClickFavorite(int id, boolean favorite) {
                onClickFavoriteMovie(id, favorite);
            }
        });
        rcvHome.setAdapter(movieAdapter);

        getListMovies("");

        imgSearch.setOnClickListener(view1 -> searchMovie());

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMovie();
                return true;
            }
            return false;
        });

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.isEmpty()) {
                    listMovies.clear();
                    getListMovies("");
                }
            }
        });

        return view;
    }

    private void searchMovie() {
        String strKey = edtSearchName.getText().toString().trim();
        listMovies.clear();
        getListMovies(strKey);
        Utils.hideSoftKeyboard(getActivity());
    }

    private void getListMovies(String key) {
        if (getActivity() == null) return;
        if (progressDialog != null) progressDialog.show();
        MyApplication.get(getActivity()).getDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        if (progressDialog != null) progressDialog.dismiss();
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || movieAdapter == null) {
                            return;
                        }

                        if (key == null || key.isEmpty()) {
                            listMovies.add(0, movie);
                        } else {
                            if (movie.getTitle().toLowerCase().trim().contains(key.toLowerCase().trim())) {
                                listMovies.add(0, movie);
                            }
                        }

                        movieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || listMovies.isEmpty() || movieAdapter == null) {
                            return;
                        }
                        for (Movie movieEntity : listMovies) {
                            if (movie.getId() == movieEntity.getId()) {
                                movieEntity.setImage(movie.getImage());
                                movieEntity.setTitle(movie.getTitle());
                                movieEntity.setUrl(movie.getUrl());
                                movieEntity.setFavorite(movie.isFavorite());
                                movieEntity.setHistory(movie.isHistory());
                                break;
                            }
                        }
                        movieAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Movie movie = dataSnapshot.getValue(Movie.class);
                        if (movie == null || listMovies == null || listMovies.isEmpty() || movieAdapter == null) {
                            return;
                        }
                        for (Movie movieDelete : listMovies) {
                            if (movie.getId() == movieDelete.getId()) {
                                listMovies.remove(movieDelete);
                                break;
                            }
                        }
                        movieAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void onClickItemMovie(Movie movie) {
        Intent intent = new Intent(getActivity(), PlayMovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_movie", movie);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void onClickFavoriteMovie(int id, boolean favorite) {
        if (getActivity() == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("favorite", favorite);
        MyApplication.get(getActivity()).getDatabaseReference()
                .child(String.valueOf(id)).updateChildren(map);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (movieAdapter != null) {
            movieAdapter.release();
        }
    }
}
