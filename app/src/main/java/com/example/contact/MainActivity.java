package com.example.contact;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contact.AppExecutor;
import com.example.contact.R;
import com.example.contact.ContactAdapter;
import com.example.contact.DBHelper;
import com.example.contact.Contact;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactAdapter.OnContactClickListener {
    private RecyclerView recyclerView;
    private DBHelper dbHelper;
    private ContactAdapter adapter;
    private List<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        dbHelper = new DBHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddEditContactActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    private void loadContacts() {
        AppExecutor.getInstance().diskIO().execute(() -> {
            contacts = dbHelper.getAllContacts();
            AppExecutor.getInstance().mainThread().execute(() -> {
                if (adapter == null) {
                    adapter = new ContactAdapter(contacts, this);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.updateList(contacts);
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public void onContactClick(Contact contact) {
        Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
        intent.putExtra("contact_id", contact.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Contact contact) {
        Intent intent = new Intent(MainActivity.this, AddEditContactActivity.class);
        intent.putExtra("contact_id", contact.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Contact contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    AppExecutor.getInstance().diskIO().execute(() -> {
                        dbHelper.deleteContact(contact.getId());
                        AppExecutor.getInstance().mainThread().execute(() -> loadContacts());
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }
}