package br.com.djektech.appcommerce

import android.content.Intent
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
import br.com.djektech.appcommerce.viewmodel.OrderViewModel
import br.com.djektech.appcommerce.viewmodel.UserViewModel

class OrderFragment : Fragment() {

    lateinit var recyclerOrder: RecyclerView

    private val orderViewModel by viewModels<OrderViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_order, container, false)

        recyclerOrder = view.findViewById(R.id.rv_order)

        val adapterOrder = OrderAdapter(requireContext())

        userViewModel.isLogged().observe(viewLifecycleOwner, Observer {
            if (it != null)
                orderViewModel.getOrdersByUser(it.user.id).observe(viewLifecycleOwner, Observer { orders ->
                    adapterOrder.list = orders
                    adapterOrder.notifyDataSetChanged()
                })
            else {
                activity?.finish()
                startActivity(Intent(activity, UserLoginActivity::class.java))
            }
        })

        recyclerOrder.adapter = adapterOrder
        recyclerOrder.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        return view
    }

}