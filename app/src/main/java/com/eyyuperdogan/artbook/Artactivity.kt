package com.eyyuperdogan.artbook

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.eyyuperdogan.artbook.databinding.ActivityArtactivityBinding
import com.eyyuperdogan.artbook.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

private lateinit var binding: ActivityArtactivityBinding
private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
private lateinit var permissionLauncher: ActivityResultLauncher<String>
 var selectedBitmap:Bitmap?=null
private lateinit var dataBase:SQLiteDatabase


class Artactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtactivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dataBase=this.openOrCreateDatabase("Arts", AppCompatActivity.MODE_PRIVATE,null)
        registerLauncher()

        val intent=intent
        val info=intent.getStringExtra("info")
        if (info.equals("new")){
            binding.textArtist.setText("")
            binding.textYear.setText("")
            binding.textName.setText("")
            binding.buttonSave.visibility=View.VISIBLE
            binding.imageAdd.setImageResource(R.drawable.image)
        }else
        {
            binding.buttonSave.visibility=View.INVISIBLE
            val selectedId=intent.getIntExtra("id",1)
            val cursor= dataBase.rawQuery("SELECT*FROM Arts WHERE id=?", arrayOf(selectedId.toString()))
            var artNameIx=cursor.getColumnIndex("artname")
            var artistNameIx=cursor.getColumnIndex("artistname")
            var yearIx=cursor.getColumnIndex("year")
            var ımageIx=cursor.getColumnIndex("image")

            while (cursor.moveToNext()){
                binding.textName.setText(cursor.getString(artistNameIx))
                binding.textArtist.setText(cursor.getString(artNameIx))
                binding.textYear.setText(cursor.getString(yearIx))
                val byteArray=cursor.getBlob(ımageIx)
                val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                binding.imageAdd.setImageBitmap(bitmap)
            }
            cursor.close()

        }


    }
    fun imageSelect(view:View){
       //snackbar
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
               //rationale
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give permission", View.OnClickListener {
               //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                }).show()
            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{
            //intent
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)

        }

    }
    fun save(view: View){
        val artName= binding.textArtist.text.toString()
        val artistName= binding.textName.text.toString()
        val year= binding.textYear.text.toString()

        if (selectedBitmap!=null){
            var samalBitmap=makesmallerBitmap(selectedBitmap!!,300)
            //image 1 ve 0 lara döüştürüyorum
            val outputstream=ByteArrayOutputStream()
            samalBitmap.compress(Bitmap.CompressFormat.PNG,50,outputstream)
            val bytArray=outputstream.toByteArray()

            try {

                dataBase.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY ,artname VARCHAR,artistname VARCHAR,year VARCHAR,image BLOB)")
                val sqlString="INSERT INTO arts(artname,artistname,year,image) VALUES(?,?,?,?)"
                val statement=dataBase.compileStatement(sqlString)
                statement.bindString(1,artName)
                statement.bindString(2,artistName)
                statement.bindString(3,year)
                statement.bindBlob(4,bytArray)
                statement.execute()
                val intent =Intent(this@Artactivity,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)

            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }
 private  fun registerLauncher(){
       activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
           result ->
           if (result.resultCode== RESULT_OK){
               val intentfromResult=result.data
               if (intentfromResult!=null){
                   val imagedata=intentfromResult.data
                   if (imagedata!=null){
                       try {
                           if (Build.VERSION.SDK_INT>=31){
                               val sourse=ImageDecoder.createSource(this@Artactivity.contentResolver,imagedata)
                                selectedBitmap=ImageDecoder.decodeBitmap(sourse)
                                binding.imageAdd.setImageBitmap(selectedBitmap)
                           }
                           else{
                               selectedBitmap=MediaStore.Images.Media.getBitmap(contentResolver,imagedata)
                               binding.imageAdd.setImageBitmap(selectedBitmap)
                           }

                       }catch (e:java.lang.Exception){
                       e.printStackTrace()

                       }
                   }
               }

           }
       }
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if (result){
                val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(this@Artactivity,"permission needed!",Toast.LENGTH_LONG).show()

            }
        }
    }
//image küçültmek
   private fun makesmallerBitmap(image : Bitmap,maxsimumsize:Int):Bitmap{
       var  width=image.width
       var height=image.height
       var result :Double=width.toDouble()/ height.toDouble()
       if (result>1){
         //yatay image
           width=maxsimumsize
           var scaledHeight=width/result
           height=scaledHeight.toInt()
       }
       else{
           //dikey image
           height=maxsimumsize
           var scaleWidht=height*result
           width=scaleWidht.toInt()

       }


    return Bitmap.createScaledBitmap(image,height,width,true)
   }
}
