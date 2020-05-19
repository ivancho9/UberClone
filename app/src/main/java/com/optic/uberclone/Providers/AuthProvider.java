package com.optic.uberclone.Providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {

    FirebaseAuth mAuth;


    public AuthProvider() {
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> registrar(String correo , String clave)
    {
      return mAuth.createUserWithEmailAndPassword(correo,clave);
    }

    public Task<AuthResult> login(String correo , String clave)
    {
        return mAuth.signInWithEmailAndPassword(correo,clave);
    }

    public void CerrarSesion(){
        mAuth.signOut();
    }

    public String retornarId()
    {
        return mAuth.getCurrentUser().getUid();
    }

   public boolean existeSession()
   {
       boolean existe = false;
       if(mAuth.getCurrentUser() != null){
           existe =true;
       }
       return  existe;
   }
}
