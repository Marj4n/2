package com.example.foodxyz.ui

import ProductAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodxyz.R
import com.example.foodxyz.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Locale

class MenuFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var productList: MutableList<Product>
    private lateinit var adapter: ProductAdapter
    private lateinit var searchView: SearchView
    private lateinit var btnPurchase: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchView = view.findViewById(R.id.search)
        searchView.clearFocus()

        val gridLayoutManager = GridLayoutManager(context, 1)
        recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        productList = mutableListOf()
        adapter = ProductAdapter(requireContext(), productList)
        recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("products")
        eventListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productList.clear()
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        product!!.id = productSnapshot.key
                        productList.add(product)
                    }
                    adapter.notifyDataSetChanged()
                }
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        }
        databaseReference.addValueEventListener(eventListener)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })
//
//        btnPurchase = view.findViewById(R.id.btnPurchase)
//        btnPurchase.setOnClickListener {
//            val fragment = InvoiceFragment()
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.frame_layout, fragment)
//            transaction.addToBackStack(null)
//            transaction.commit() //
//        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        databaseReference.removeEventListener(eventListener)
    }

    private fun searchList(text: String) {
        val searchList = productList.filter { product ->
            product.productName?.lowercase(Locale.getDefault())
                ?.contains(text.lowercase(Locale.getDefault()))
                ?: false
        }
        adapter.setProductList(searchList)
    }
}