package com.maylinda.notepad.ui.ui
//TODO 4 : Karena target awal untuk ditampilkan adalah pada  MainActivity maka setelah dari AndroidManifest menuju MainActivity,dimana file kotlin ini dimasukkan kedalam sebuah package dengan nama ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.maylinda.notepad.R
import com.maylinda.notepad.ui.Adapter.NoteAdapter
import com.maylinda.notepad.ui.Listener.Listener
import com.maylinda.notepad.ui.Model.Note
import com.maylinda.notepad.ui.ViewModel.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
//MainActivity ini memanggil activity_main.xml sebagai layoutnya.

class MainActivity : AppCompatActivity(),
    Listener {
    //Dibawah ini merupakan penginisalisasian atau pendeklarasian  variabel yakni recyclerView,noteViewModel,noteAdapter,noteList serta searchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteList: ArrayList<Note>
    private lateinit var searchView:SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        noteAdapter = NoteAdapter(applicationContext, ArrayList<Note>(), this)
        initialiseRecyclerView()


        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.getCardsData(this)?.observe(this, Observer {
            noteAdapter.setData(it as ArrayList<Note>)
            noteList = it
        })
        //Pada file ini juga memuat variabel konstant yakni intent yang menggunakan intent eksplisit yang menargetkan tujuan dari MainActivity menuju ke AnotherActivity
        floatingActionButton.setOnClickListener {
            val intent = Intent(this, AnotherActivity::class.java)
            startActivity(intent)
        }

        val itemTouchHelper=ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }
    //Fungsi recyclerview menggunakan findViewById dimana yang panggil adalah id recyclerView
    //Serta untuk menerapkannya menggunakan apply
    //Terdapat adapter yang berfungsi untuk menyediakan akses ke item data yang ada pada noteAdapter
    private fun initialiseRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            adapter = noteAdapter


        }
    }
    //Untuk  mencari catatan yang telah dibuat maka dapat menekan icon search, dimana fungsi yang berjalan adalah actionnView mencari data yang sesuai kata yang ditulis.
    //Jika query tidak null maka data dari database akan diambil untuk ditampilkan, sehingga user dapat melihat hasil pencarian
    //Jika qery teks tidak null maka dapat mengambil data baru dari database, ini terjadi ketika user melakukan edit data dan menekan icon refresh yang ada
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menus, menu)
        val search= menu?.findItem(R.id.searchItems)
        searchView=search?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null)
                    getItemsFromDB(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null)
                    getItemsFromDB(newText)
                return true
            }

        })
        return true
    }
    //Fungsi dibawah ini berupakan fungsi untuk pengambilan data ke dalam string dan ditampilkan dalam arraylist
    private fun getItemsFromDB(data:String)
    {
        var searchText=data
        searchText="%$data%"
        noteViewModel.search(this,searchText)?.observe(this, Observer {
            Log.d("main", "$it")
            noteAdapter.setData(it as ArrayList<Note>)
        })
    }


    override fun onClickListener(position: Int) {
        val intent=Intent(this, UpdateActivity::class.java)
        intent.putExtra("note",noteList[position])
        startActivity(intent)
    }
    val simpleCallback= object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position=viewHolder.adapterPosition
            val note=noteList[position]
            when(direction)
            {
                ItemTouchHelper.RIGHT->{
                    noteViewModel.delete(this@MainActivity,note)
                    noteAdapter.notifyDataSetChanged()
                }
                ItemTouchHelper.LEFT->{
                    noteViewModel.delete(this@MainActivity,note)
                    noteAdapter.notifyDataSetChanged()
                }
            }
        }

    }

}