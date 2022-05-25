package com.example.vocabularygame

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_toplist.*

class ToplistActivity : AppCompatActivity() {

    public fun getContacts():List<ContactDTO>
    {
        val contactList: MutableList<ContactDTO> = ArrayList()

        val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        val displayNameURI = contacts!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val numberURI = contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

        while(contacts.moveToNext())
        {
            val name = contacts.getString(displayNameURI)
            val number = contacts.getString(numberURI)
            val obj = ContactDTO()
            obj.name = name;
            obj.number = number;

            contactList.add(obj)
        }
        contacts.close()

        return contactList
    }

    public fun setViewMatchesUsers( recyclerView: RecyclerView )
    {
        val userControl = UsersControl(this)
        userControl.setViewMatchesUsers(recyclerView, getContacts())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toplist)




        layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager
        //adapter = RecyclerAdapter( getContacts() )

        setViewMatchesUsers(recyclerView)

    }


    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onResume() {
        super.onResume()



    }

}