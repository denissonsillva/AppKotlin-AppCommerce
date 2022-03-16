package br.com.djektech.appcommerce

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.djektech.appcommerce.adapter.ProductAdapter
import br.com.djektech.appcommerce.model.ProductCategory
import br.com.djektech.appcommerce.viewmodel.ProductViewModel

class ProductFragment : Fragment() {

    lateinit var recyclerProduct: RecyclerView
    lateinit var category: ProductCategory

    private val productViewModel by viewModels<ProductViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            category = (arguments?.getSerializable("CATEGORY") as ProductCategory)
        }
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_product, container, false)

        recyclerProduct = view.findViewById(R.id.rv_product)

        val adapterProduct = ProductAdapter(requireContext())

        productViewModel.getProductsByCategory(category.id).observe(this, Observer {
            adapterProduct.list = it
            adapterProduct.notifyDataSetChanged()
        })

        recyclerProduct.adapter = adapterProduct
        recyclerProduct.layoutManager = GridLayoutManager(requireContext(), 3)

        return view
    }

}