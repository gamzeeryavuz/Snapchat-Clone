package com.example.snapchat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*




class CreateSnapActivity : AppCompatActivity() {

    var createSnapImageView:ImageView? =null
    var messageEditText:EditText? =null
    val imageName= UUID.randomUUID().toString() +".jpg"


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        createSnapImageView =findViewById(R.id.createSnapImageView)
        messageEditText =findViewById(R.id.messageEditText)

    }
    fun getPhoto(){
        val intent =Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,1)

    }
    fun chooseImageClicked(view: View){
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            getPhoto()
        }
         }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage=data!!.data
        if(requestCode==1 && resultCode == Activity.RESULT_OK && data !=null){
            try{
                val bitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)





            }catch(e:Exception){
                e.printStackTrace()

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode ==1){
            if(grantResults.size>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                getPhoto()
            }
        }
    }
    fun nextClicked(view:View){
        createSnapImageView?.isDrawingCacheEnabled()
        createSnapImageView?.buildDrawingCache()
        val bitmap =createSnapImageView?.getDrawingCache()
        val baos= ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data =baos.toByteArray()


        val uploadTask = FirebaseStorage.getInstance().getReference().child("images").child("abc.jpg").putBytes(data)
        uploadTask.addOnFailureListener(OnFailureListener{
            Toast.makeText(this,"UploadFailed", Toast.LENGTH_SHORT).show()
        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot>{taskSnapshot ->

             val downloadUrl=taskSnapshot.uploadSessionUri
            Log.i("URL",downloadUrl.toString())
            val intent =Intent(this,ChooseUserActivity::class.java)
            intent.putExtra("imageURL",downloadUrl.toString())
            intent.putExtra("imageName",imageName)
           startActivity(intent)








        })



    }

}