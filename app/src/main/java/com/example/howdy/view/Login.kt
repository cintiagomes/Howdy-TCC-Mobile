package com.example.howdy.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.howdy.CadastroActivity
import com.example.howdy.CadastroIncompletoActivity
import com.example.howdy.R
import com.example.howdy.databinding.ActivityLoginBinding
import com.example.howdy.model.UserTypes.User
import com.example.howdy.remote.APIUtil
import com.example.howdy.remote.RouterInterface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Login : AppCompatActivity() {

    private lateinit var routerInterface: RouterInterface

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    lateinit var gso: GoogleSignInOptions
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val RC_SIGN_IN_GOOGLE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** CONFIGURAÇÃO DO routerInterface **/
        routerInterface = APIUtil.`interface`

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar!!.hide()
        setContentView(binding.root)

        val esqueciSenha = findViewById<TextView>(R.id.esqueci_senha)
        val registrarConta = findViewById<TextView>(R.id.link_registar)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.firebasse_default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
        binding.buttonEntrar.setOnClickListener { login() }

        binding.btnLoginWithGoogle.setOnClickListener { loginWithGoogle() }


        esqueciSenha.setOnClickListener {
            val RecuperarSenha =
                Intent(this, RecuperarSenha::class.java)
            startActivity(RecuperarSenha)
        }

        registrarConta.setOnClickListener {
            val ActivityTesteBinding =
                Intent(this, CadastroActivity::class.java)
            startActivity(ActivityTesteBinding)
        }

    }

    private fun login() {
        val email = binding.textEmail.text.toString()
        val senha = binding.textSenha.text.toString()

        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener {
            if (it.isSuccessful){

                //RESGATANDO IDTOKEN DO USUÁRIO LOGADO NO FIREBASE
                auth.currentUser?.getIdToken(true)
                    ?.addOnSuccessListener { result ->
                        val idToken = result.token

                        if (idToken != null) {
                            //O USUÁRIO SE LOGOU NO FIREBASE, E AGORA IRÁ VER SE REALMENTE ESTÁ CADASTRADO NO BANCO SQL
                            isMyUidExternalRegistered(idToken)
                        }
                    }
            }else{
                Toast.makeText(applicationContext,"Houve um erro no login", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun isMyUidExternalRegistered(idToken: String) {
        val call: Call<List<User>> = routerInterface.isMyUidExternalRegistered(idToken)
        /** EXECUÇÃO CHAMADA DA ROTA  */
        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userLogged = response.body()!![0]

                    val userLoggedFile = getSharedPreferences(
                        "userLogged", Context.MODE_PRIVATE)

                    // EDIÇÃO DE DADOS DO ARQUIVO SHARED PREFERENCES
                    val editor = userLoggedFile.edit()
                    editor.putInt("idUser", userLogged.idUser)
                    editor.putString("profilePhoto", userLogged.profilePhoto)
                    editor.putString("userName", userLogged.userName)
                    editor.putString("description", userLogged.description)
                    editor.putString("backgroundImage", userLogged.backgroundImage)
                    editor.putString("subscriptionEndDate", userLogged.subscriptionEndDate.toString())
                    editor.putInt("howdyCoin", userLogged.howdyCoin)
                    editor.putInt("idTargetLanguage", userLogged.idTargetLanguage)
                    editor.putString("targetLanguageName", userLogged.targetLanguageName)
                    editor.putString("targetLanguageTranslatorName", userLogged.targetLanguageTranslatorName)
                    editor.putInt("idNativeLanguage", userLogged.idNativeLanguage)
                    editor.putString("nativeLanguageName", userLogged.nativeLanguageName)
                    editor.putString("nativeLanguageTranslatorName", userLogged.nativeLanguageTranslatorName)
                    editor.apply()

                    //REDIRECIONANDO O USUÁRIO PARA A PÁGINA DE POSTAGENS
                    navigateToPostPage()
                } else {
                    if (response.code() == 404){
                        navigateToIncompleteRegisterPage()
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                println("ERRO-API: " + t.message)
            }
        })
    }

    private fun loginWithGoogle(){
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            Log.d("ERRO", task.exception.toString())
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                } catch (e: ApiException) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                }
            } else {
                throw Exception(exception)
            }
        }else {
            Toast.makeText(this, "Problem in execution order :(", Toast.LENGTH_LONG).show()
        }
    }
    private fun handleResult (completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            println("DEBUGANDO LOGOU COM O GOOGLE")
        } catch (e: ApiException) {
            println("DEBUGANDO "+ e.toString())
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToPostPage() {
        val targetPage = Intent(this, paginaDePostagem::class.java)
        startActivity(targetPage)
    }

    private fun navigateToIncompleteRegisterPage() {
        val targetPage = Intent(this, CadastroIncompletoActivity::class.java)
        startActivity(targetPage)
    }
}