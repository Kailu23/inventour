package com.kailu.inventour.repository

import com.google.firebase.auth.FirebaseAuth
import com.kailu.inventour.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: User?
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    fun logout()
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: User?
        get() = firebaseAuth.currentUser?.let {
            User(it.uid, it.email, it.displayName)
        }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.let { User(it.uid, it.email, it.displayName) }
            if (user != null) Result.success(user)
            else Result.failure(Exception("Failed to get user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                                    val user = result.user?.let { User(it.uid, it.email, name) }
            if (user != null) Result.success(user)
            else Result.failure(Exception("Failed to create user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}
