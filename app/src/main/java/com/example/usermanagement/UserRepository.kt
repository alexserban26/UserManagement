package com.example.usermanagement
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class UserRepository {

    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance("https://usermanagement-7e80f-default-rtdb.europe-west1.firebasedatabase.app").getReference("")

    @Volatile private var INSTANCE : UserRepository ?= null

    fun getInstance() : UserRepository{
        return INSTANCE ?: synchronized(this){

            val instance = UserRepository()
            INSTANCE = instance
            instance
        }


    }


    fun loadUsers(userList : MutableLiveData<List<User>>){

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                try {

                    val _userList : List<User> = snapshot.children.map { dataSnapshot ->
                        Log.e("Message", userList.toString())
                        dataSnapshot.getValue(User::class.java)!!

                    }

                    userList.postValue(_userList)

                }catch (e : Exception){


                }


            }

            override fun onCancelled(error: DatabaseError) {
            }


        })


    }

}