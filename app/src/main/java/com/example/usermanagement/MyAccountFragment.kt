package com.example.usermanagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccountFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        firebaseAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_my_account, container, false)
        database = FirebaseDatabase.getInstance("https://usermanagement-7e80f-default-rtdb.europe-west1.firebasedatabase.app").getReference()

        view.findViewById<Button>(R.id.signOut).setOnClickListener{
            firebaseAuth.signOut()
            activity?.let{
                val intent = Intent(it, SignInActivity::class.java)
                it.startActivity(intent)
            }
        }

        view.findViewById<Button>(R.id.shareDetails).setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "I have an account on UserManagement app! And my username is: "+firebaseAuth.currentUser?.email)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            activity?.let{
                it.startActivity(shareIntent)
            }
        }

        view.findViewById<Button>(R.id.save).setOnClickListener{
            val email = view.findViewById<EditText>(R.id.email).text.toString()
            val firstName = view.findViewById<EditText>(R.id.firstName).text.toString()
            val lastName = view.findViewById<EditText>(R.id.lastName).text.toString()
            val address = view.findViewById<EditText>(R.id.address).text.toString()
            Log.e("message", email+firstName+lastName+address)

            val User = User(firstName, lastName, email, address)
            database.child(email.replace('.', '~')).setValue(User).addOnSuccessListener {
                Toast.makeText(requireContext(), "Successfully Saved", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(requireContext(), "Save Failed", Toast.LENGTH_SHORT).show()
            }
        }
        val user = database.child(firebaseAuth.currentUser?.email.toString().replace('.','~')).get().addOnSuccessListener {

            val emailEditText = view.findViewById<EditText>(R.id.email).setText(firebaseAuth.currentUser?.email.toString())
            val firstNameEditText = view.findViewById<EditText>(R.id.firstName).setText(if(it.child("firstName").value.toString()!="null") it.child("firstName").value.toString() else "")
            val lastNameEditText = view.findViewById<EditText>(R.id.lastName).setText(if(it.child("lastName").value.toString()!="null") it.child("lastName").value.toString() else "")
            val addressEditText = view.findViewById<EditText>(R.id.address).setText(if(it.child("address").value.toString()!="address") it.child("address").value.toString() else "")
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}