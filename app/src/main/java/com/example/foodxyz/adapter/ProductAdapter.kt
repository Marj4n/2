import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodxyz.MainActivity
import com.example.foodxyz.R
import com.example.foodxyz.model.CartItem
import com.example.foodxyz.model.Product
import com.example.foodxyz.ui.InvoiceFragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProductAdapter(private val context: Context, private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private val cartItems = mutableListOf<CartItem>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.recImage)
        val productName: TextView = itemView.findViewById(R.id.recProductName)
        val productPrice: TextView = itemView.findViewById(R.id.recPrice)
        val productRating: TextView = itemView.findViewById(R.id.recRating)
        val productQuantity: TextView = itemView.findViewById(R.id.recQuantity)
        val addButton: ImageButton = itemView.findViewById(R.id.btnAdd)
        val removeButton: ImageButton = itemView.findViewById(R.id.btnRemove)
        val cartButton: ImageButton = itemView.findViewById(R.id.btnCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]

        holder.productName.text = product.productName
        holder.productPrice.text = "Rp. " + product.price.toString().reversed().chunked(3)
            .joinToString(".").reversed()
        holder.productQuantity.text = "0"
        holder.productRating.text = "⭐ " + product.rating.toString()

        // Load the product image using Glide
        Glide.with(context)
            .load(product.image)
            .placeholder(R.drawable.baseline_image_24)
            .into(holder.productImage)

        holder.addButton.setOnClickListener {
            // Increase quantity of product when the add button is clicked
            var quantity = holder.productQuantity.text.toString().toInt()
            quantity++
            holder.productQuantity.text = quantity.toString()
        }

        holder.removeButton.setOnClickListener {
            // Decrease quantity of product when the remove button is clicked
            var quantity = holder.productQuantity.text.toString().toInt()
            if (quantity > 0) {
                quantity--
                holder.productQuantity.text = quantity.toString()
            }
        }

        holder.cartButton.setOnClickListener {
            val quantity = holder.productQuantity.text.toString().toInt()
            if (quantity > 0) {
                // Cek apakah item produk sudah ada di keranjang
                val existingCartItem = cartItems.find { it.product == product }
                if (existingCartItem != null) {
                    // Jika item produk sudah ada di keranjang, tambahkan quantity
                    existingCartItem.quantity += quantity
                } else {
                    // Jika item produk belum ada di keranjang, tambahkan ke keranjang
                    cartItems.add(CartItem(product, quantity))
                }
                // Reset quantity ke 0
                holder.productQuantity.text = "0"
                // Update text pada tombol purchase
                val total = cartItems.sumOf { it.product.price?.times(it.quantity) ?: 0 }
                val totalFormatted =
                    "Rp. " + total.toString().reversed().chunked(3).joinToString(".").reversed()
                val purchaseButton =
                    (context as MainActivity).findViewById<Button>(R.id.btnPurchase)
                purchaseButton.text =
                    "Bayar Sekarang                                $totalFormatted"

                // Save cart items to Realtime Database
                val database = Firebase.database.reference
                val cartItemsRef = database.child("cartItems")
                for (item in cartItems) {
                    cartItemsRef.child(item.product.id.toString()).setValue(item)
                }

                // Navigate to InvoiceFragment
                val fragmentManager = context.supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, InvoiceFragment())
                    .commit()
            } else {
                Toast.makeText(context, "Please add at least one item to cart", Toast.LENGTH_SHORT)
                    .show()
            }

        }


    }

    override fun getItemCount(): Int {
        return productList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProductList(productList: List<Product>) {
        this.productList = productList
        notifyDataSetChanged()
    }
}
