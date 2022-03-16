package br.com.djektech.appcommerce.repository

import android.app.Application
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.djektech.appcommerce.R
import br.com.djektech.appcommerce.model.User
import br.com.djektech.appcommerce.model.UserAddress
import br.com.djektech.appcommerce.model.UserWithAddresses
import br.com.djektech.appcommerce.viewmodel.UserViewModel
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import org.json.JSONObject

class UsersRepository (application: Application) {

    private val firestore = FirebaseFirestore.getInstance()

    private val queue = Volley.newRequestQueue(application)

    private val preference = PreferenceManager.getDefaultSharedPreferences(application)

    private val glide = Glide.with(application)

    private val storage = Firebase.storage(Firebase.app)

    fun login(email: String, password: String) : LiveData<User> {

        val liveData = MutableLiveData<User>(null)

        val params = JSONObject().also {
            it.put("email", email)
            it.put("password", password)
            it.put("returnSecureToken", true)
        }

        val request = JsonObjectRequest(Request.Method.POST
            , BASE_URL + SIGNIN + KEY
            , params
            , Response.Listener { response ->
                val localId = response.getString("localId")
                val idToken = response.getString("idToken")

                firestore.collection("users")
                    .document(localId).get().addOnSuccessListener {
                        val user = it.toObject(User::class.java)
                        user?.id = localId
                        user?.password = idToken

                        liveData.value = user!!

                        preference.edit().putString(UserViewModel.USER_ID, localId).apply()

                        firestore.collection("users")
                            .document(localId).set(user!!)
                    }
            }
            , Response.ErrorListener { error ->
                Log.e(this.toString(), error.message ?: "Error")
            }
        )

        queue.add(request)

        return liveData
    }

    fun createUser(user: User) {

        val params = JSONObject().also {
            it.put("email", user.email)
            it.put("password", user.password)
            it.put("returnSecureToken", true)
        }

        val request = JsonObjectRequest(Request.Method.POST
            , BASE_URL + SIGNUP + KEY
            , params
            , Response.Listener { response ->
                user.id = response.getString("localId")
                user.password = response.getString("idToken")

                firestore.collection("users")
                    .document(user.id).set(user).addOnSuccessListener {
                        Log.d(this.toString(), "Usuário ${user.email} cadastrado com sucesso.")
                    }
            }
            , Response.ErrorListener { error ->
                Log.e(this.toString(), error.message ?: "Error")
            }
        )

        queue.add(request)
    }

    fun load(userId: String) : LiveData<UserWithAddresses> {
        val userWithAddresses = UserWithAddresses()
        val liveData = MutableLiveData<UserWithAddresses>()

        val userRef = firestore.collection("users").document(userId)

        userRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            user?.id = it.id

            userWithAddresses.user = user!!

            userRef.collection("addresses").get().addOnCompleteListener { snap ->
                snap.result?.forEach { doc ->
                    val address = doc.toObject(UserAddress::class.java)
                    address.id = doc.id
                    userWithAddresses.addresses.add(address)
                }

                liveData.value = userWithAddresses
            }
        }

        return liveData
    }

    fun update(userWithAddresses: UserWithAddresses) : Boolean {

        var updated = false

        val userRef = firestore.collection("users").document(userWithAddresses.user.id)

        userRef.set(userWithAddresses.user).addOnSuccessListener { updated = true }

        val addressRef = userRef.collection("addresses")

        val address = userWithAddresses.addresses.first()

        if(address.id.isEmpty()) {
            addressRef.add(address).addOnSuccessListener {
                address.id = it.id
                updated = true
            }
        } else {
            addressRef.document(address.id).set(address).addOnSuccessListener { updated = true }
        }

        return updated
    }

    fun resetPassword(email: String) {

        val params = JSONObject().also {
            it.put("email", email)
            it.put("requestType", "PASSWORD_RESET")
        }

        val request = JsonObjectRequest(Request.Method.POST
            , BASE_URL + PASSWORD_RESET + KEY
            , params
            , Response.Listener { response ->
                Log.d(this.toString(), response.keys().toString())
            }
            , Response.ErrorListener { error ->
                Log.e(this.toString(), error.message ?: "Error")
            }
        )

        queue.add(request)

    }

    fun uploadProfileImage(userId: String, photoUri:Uri) : LiveData<String> {

        val liveData = MutableLiveData<String>()

        storage.reference.child("users/$userId/profile.jpg").putFile(photoUri).addOnSuccessListener {
            preference.edit().putString(MediaStore.EXTRA_OUTPUT, it.metadata?.path).apply()
            liveData.value = it.metadata?.path
        }

        return liveData
    }

    fun loadProfile(userId: String, imageView: ImageView) = storage.reference.child("users/$userId/profile.jpg")
        .downloadUrl.addOnSuccessListener {
            glide.load(it)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.profile_image)
                .placeholder(R.drawable.profile_image)
                .into(imageView)
        }

    companion object {

        const val BASE_URL = "https://identitytoolkit.googleapis.com/v1/"

        const val SIGNUP = "accounts:signUp"

        const val SIGNIN = "accounts:signInWithPassword"

        const val PASSWORD_RESET = "accounts:sendOobCode"

        const val KEY = "?key=AIzaSyC_BM4rOvCEJHxne_zljKvarSQEGk5yyS8"
    }

}