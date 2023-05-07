    package com.example.foodxyz.ui

    import android.content.Intent
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.Menu
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import android.widget.Toolbar
    import androidx.fragment.app.Fragment
    import com.example.foodxyz.R
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import java.text.DecimalFormat

    class InvoiceFragment : Fragment() {
        private lateinit var nameProduct: TextView
        private lateinit var priceProduct: TextView
        private lateinit var quantityProduct: TextView
        private lateinit var subTotalProduct: TextView
        private lateinit var totalProduct: TextView
        private lateinit var saveBtn : Button
        private lateinit var shareBtn: Button
        private lateinit var doneBtn: Button

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_invoice, container, false)

            // Initialize the TextViews
            nameProduct = view.findViewById(R.id.invoiceNameProduct)
            priceProduct = view.findViewById(R.id.invoicePriceProduct)
            quantityProduct = view.findViewById(R.id.invoiceQtyProduct)
            subTotalProduct = view.findViewById(R.id.invoiceSubtotal)
            totalProduct = view.findViewById(R.id.invoiceTotal)
            saveBtn = view.findViewById(R.id.btnSave)
            shareBtn = view.findViewById(R.id.btnShare)
            doneBtn = view.findViewById(R.id.btnSelesai)

            // Get a reference to the "inovice" node in your Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val cartItemsRef = database.getReference("cartItems")

            cartItemsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Get the child nodes from the "cartItems" node
                    for (itemSnapshot in snapshot.children) {
                        val itemId = itemSnapshot.key
                        val productSnapshot = itemSnapshot.child("product")
                        val quantity = itemSnapshot.child("quantity").getValue(Int::class.java)

                        // Get the product details from the "product" child node
                        val productId = productSnapshot.child("id").getValue(String::class.java)
                        val productName = productSnapshot.child("productName").getValue(String::class.java)
                        val image = productSnapshot.child("image").getValue(String::class.java)
                        val price = productSnapshot.child("price").getValue(Int::class.java)
                        val rating = productSnapshot.child("rating").getValue(Int::class.java)
                        val stock = productSnapshot.child("stock").getValue(Int::class.java)

                        // Calculate the subtotal and total
                        val subtotal = price?.times(quantity!!)
                        val total = subtotal

                        // Set the values of the TextViews
                        nameProduct.text = productName
                        priceProduct.text = price?.let { formatRupiah(it) }
                        quantityProduct.text = quantity.toString()
                        subTotalProduct.text = subtotal?.let { formatRupiah(it) }
                        totalProduct.text = total?.let { formatRupiah(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show()
                }
            })

            // Set the click listeners for the buttons
            saveBtn.setOnClickListener {
                Toast.makeText(context, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
            }

            shareBtn.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                val shareText = "Product Name: ${nameProduct.text}\n" +
                        "Price: ${priceProduct.text}\n" +
                        "Quantity: ${quantityProduct.text}\n" +
                        "Subtotal: ${subTotalProduct.text}\n" +
                        "Total: ${totalProduct.text}"
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
                startActivity(Intent.createChooser(shareIntent, "Share Invoice"))
            }

            doneBtn.setOnClickListener {
                Toast.makeText(context, "Terima kasih telah berbelanja", Toast.LENGTH_SHORT).show()
                val fragment = MenuFragment()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
                // delete cartItem
                cartItemsRef.removeValue()
            }

            return view
        }

        fun formatRupiah(number: Int): String {
            val formatter = DecimalFormat("#,###")
            val formatted = formatter.format(number)
            return "Rp $formatted"
        }
    }