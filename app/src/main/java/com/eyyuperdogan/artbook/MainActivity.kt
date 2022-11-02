package com.eyyuperdogan.artbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.eyyuperdogan.artbook.databinding.ActivityMainBinding

private lateinit var binding:ActivityMainBinding
private lateinit var arrayList: ArrayList<art>
private lateinit var artadapter: ArtAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        arrayList=ArrayList<art>()
        artadapter= ArtAdapter(arrayList)
        binding.recylerView.layoutManager=LinearLayoutManager(this)
        binding.recylerView.adapter= artadapter



        try {
            var database=this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
            var cursor=database.rawQuery("SELECT*FROM arts",null)
            val artnameIx=cursor.getColumnIndex("artname")
            val ıdIx=cursor.getColumnIndex("id")
            while (cursor.moveToNext()){
                val name=cursor.getString(artnameIx)
                val id=cursor.getInt(ıdIx)
                val art=art(name,id)
              arrayList.add(art)
            }
            artadapter.notifyDataSetChanged()
           cursor.close()


        }catch (e:Exception){
            e.printStackTrace()
        }


    }
 //menu ile activityi birbirine bağlama
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
     val menuİnflater=menuInflater
     menuİnflater.inflate(R.menu.art_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.add_menu){
            var intent=Intent(this@MainActivity,Artactivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}