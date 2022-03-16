package br.com.djektech.appcommerce.viewmodel

import android.app.Application
import android.net.Uri
import android.preference.PreferenceManager
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.djektech.appcommerce.model.User
import br.com.djektech.appcommerce.model.UserWithAddresses
import br.com.djektech.appcommerce.repository.UsersRepository

class UserViewModel (application: Application) : AndroidViewModel(application) {

    private val usersRepository = UsersRepository(getApplication())

    fun createUser(user: User) = usersRepository.createUser(user)

    fun update(userWithAddresses: UserWithAddresses) = usersRepository.update(userWithAddresses)

    fun login(email: String, password: String) : LiveData<User> = usersRepository.login(email, password)

    fun logout() = PreferenceManager.getDefaultSharedPreferences(getApplication()).let {
        it.edit().remove(USER_ID).apply()
    }

    fun isLogged(): LiveData<UserWithAddresses> = PreferenceManager.getDefaultSharedPreferences(getApplication()).let {
        val id = it.getString(USER_ID, null)

        if(id.isNullOrEmpty())
            return MutableLiveData(null)

        return usersRepository.load(id)
    }

    fun resetPassword(email: String) = usersRepository.resetPassword(email)

    fun uploadProfileImage(userId: String, photoUri: Uri) = usersRepository.uploadProfileImage(userId, photoUri)

    fun loadProfile(userId: String, imageView: ImageView) = usersRepository.loadProfile(userId, imageView)

    companion object {
        const val USER_ID = "USER_ID"
    }
}