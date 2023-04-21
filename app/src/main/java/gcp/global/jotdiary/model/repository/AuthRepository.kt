package gcp.global.jotdiary.model.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * AuthRepository class.
 *
 * This class has three methods.
 * It checks if the user is currently signed in.
 * It can create a user.
 * And login based on input.
 */
class AuthRepository {

    /**
     * hasUser()
     * Checks the currently signed-in FirebaseUser.
     * Is null if there is none.
     * Returns true if != null and false = null.
     *
     * @param void
     * @return Boolean - true or false
     */
    fun hasUser():Boolean = Firebase.auth.currentUser != null

    /**
     * createUser()
     * Creates a Task creating a user with and email and passsword.
     * It returns a authResult that has a completeListner added on.
     * The completeListner returns true if it successful and false if not.
     *
     * @param email - String
     * @param password - String
     *
     * @return Unit = OnComplete - Boolean
     */
    suspend fun createUser(
        email:String,
        password:String,
        onComplete:(Boolean) ->Unit
    ) = withContext(Dispatchers.IO){
        Firebase.auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (it.isSuccessful){
                    onComplete.invoke(true)
                }else{
                    onComplete.invoke(false)
                }

            }.await()
    }

    /**
     * login()
     * Logs a user in.
     * It calls on a method from the FirebaseAuth class checking the email and password.
     * If the CompleteListener returns Success.
     * It onComplete Unit is true, if not it´s false.
     *
     * @param email - String
     * @param password - String
     * @return Unit = OnComplete - Boolean
     */
    suspend fun login(
        email:String,
        password:String,
        onComplete:(Boolean) ->Unit
    ) = withContext(Dispatchers.IO){
        Firebase.auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (it.isSuccessful){
                    onComplete.invoke(true)
                }else{
                    onComplete.invoke(false)
                }

            }.await()
    }

}