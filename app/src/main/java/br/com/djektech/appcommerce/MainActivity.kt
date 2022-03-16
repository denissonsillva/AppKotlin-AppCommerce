package br.com.djektech.appcommerce

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.djektech.appcommerce.adapter.ProductAdapter
import br.com.djektech.appcommerce.adapter.ProductCategoryAdapter
import br.com.djektech.appcommerce.model.ProductCategory
import br.com.djektech.appcommerce.viewmodel.HomeBannerViewModel
import br.com.djektech.appcommerce.viewmodel.ProductViewModel
import br.com.djektech.appcommerce.viewmodel.UserViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ProductCategoryFragment.Callback {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var textTitle: TextView
    lateinit var textLogin: TextView
    lateinit var recyclerCategory: RecyclerView
    lateinit var recyclerProduct: RecyclerView
    lateinit var imageProfile: ImageView
    lateinit var bannerImage: ImageView
    lateinit var bannerTitle: TextView
    lateinit var bannerSubtitle: TextView

    private val productViewModel by viewModels<ProductViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val homeBannerViewModel by viewModels<HomeBannerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)

        textTitle = findViewById(R.id.toolbar_title)
        textTitle.text = getString(R.string.app_name)

        bannerImage = findViewById(R.id.iv_slider_img)
        bannerTitle = findViewById(R.id.tv_slider_title)
        bannerSubtitle = findViewById(R.id.tv_slider_subtitle)

        homeBannerViewModel.load(bannerImage).observe(this, Observer {
            bannerTitle.text = it.title
            bannerSubtitle.text = it.subtitle
        })

        drawerLayout = findViewById(R.id.nav_drawer_layout)

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.toggle_open, R.string.toggle_close)
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        textLogin = navigationView.getHeaderView(0).findViewById(R.id.header_profile_name)
        textLogin.setOnClickListener {
            val intent = Intent(this, UserLoginActivity::class.java)
            startActivity(intent)
        }

        imageProfile = navigationView.getHeaderView(0).findViewById(R.id.header_profile_image)

        recyclerCategory = findViewById(R.id.rv_main_product_category)

        val adapterCategory = ProductCategoryAdapter( this)

        productViewModel.featuredCategories.observe(this, Observer {
            adapterCategory.list = it
            adapterCategory.notifyDataSetChanged()
        })

        recyclerCategory.adapter = adapterCategory
        recyclerCategory.layoutManager = LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false)

        recyclerProduct = findViewById(R.id.rv_main_product)

        val adapterProduct = ProductAdapter(this)

        productViewModel.featuredProducts.observe(this, Observer {
            adapterProduct.list = it
            adapterProduct.notifyDataSetChanged()
        })

        recyclerProduct.adapter = adapterProduct
        recyclerProduct.layoutManager = LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_account -> {
                val intent = Intent(this, UserProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_category -> {
                val intent = Intent(this, ProductCategoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_orders -> {
                val intent = Intent(this, OrderActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_cart -> {
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                userViewModel.logout()
                finish()
                startActivity(intent)
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun itemSelected(category: ProductCategory) {
        val intent = Intent(this, ProductActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        userViewModel.isLogged().observe(this, Observer { user ->
            user?.let {
                textLogin.text = "${it.user.name} ${it.user.surname}"
                userViewModel.loadProfile(it.user.id, imageProfile)
            }
        })
    }
}