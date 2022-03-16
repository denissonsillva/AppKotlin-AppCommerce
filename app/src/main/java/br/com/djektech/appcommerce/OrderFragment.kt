package br.com.djektech.appcommerce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.djektech.appcommerce.adapter.OrderAdapter
import br.com.djektech.appcommerce.model.User
import br.com.djektech.appcommerce.viewmodel.OrderViewModel

class OrderFragment : Fragment() {

    lateinit var recyclerOrder: RecyclerView
    lateinit var user: User

    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null)
            user = (arguments?.getSerializable("USER") as User)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_order, container, false)

        recyclerOrder = view.findViewById(R.id.rv_order)

        val adapterOrder = OrderAdapter(requireContext())

        orderViewModel.getOrdersByUser(user.id).observe(viewLifecycleOwner, Observer { orders ->
            adapterOrder.list = orders
            adapterOrder.notifyDataSetChanged()
        })

        recyclerOrder.adapter = adapterOrder
        recyclerOrder.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        return view
    }

}