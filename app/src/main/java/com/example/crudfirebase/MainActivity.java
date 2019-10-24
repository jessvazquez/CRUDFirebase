package com.example.crudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crudfirebase.modeloBD.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText enombre, eapellido, eemail, epassword;
    ListView listV_personas;
    //Creamos un array list para las personas
    private List<Persona> listperson = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    //dependencias de Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enombre = findViewById(R.id.enombre);
        eapellido = findViewById(R.id.eapellido);
        eemail = findViewById(R.id.ecorreo);
        epassword = findViewById(R.id.epassword);

        //obtenemos caracteristicas que tenemos en la interfaz
        listV_personas = findViewById(R.id.lista);


        inicializarFirebase();
        listarDatos();


        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posicion, long id) {
                personaSelected = (Persona) parent.getItemAtPosition(posicion);
                enombre.setText(personaSelected.getNombre());
                eapellido.setText(personaSelected.getApellido());
                eemail.setText(personaSelected.getEmail());
                epassword.setText(personaSelected.getPassword());

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listperson.clear();
                //obtenemos datos desde firebase
                for (DataSnapshot objSnapShot : dataSnapshot.getChildren()) {
                    Persona p = objSnapShot.getValue(Persona.class);
                    listperson.add(p);
                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listperson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void limpiarCajas() {
        enombre.setText("");
        eapellido.setText("");
        eemail.setText("");
        epassword.setText("");
    }

    private void validacion() {
        String nombre = enombre.getText().toString();
        String apellido = eapellido.getText().toString();
        String correo = eemail.getText().toString();
        String password = epassword.getText().toString();
        if (nombre.equals("")) {
            enombre.setError("campo obligatorio");
        } else if (apellido.equals("")) {
            eapellido.setError("campo obligatorio");
        } else if (correo.equals("")) {
            eemail.setError("campo obligatorio");
        } else if (password.equals("")) {
            epassword.setError("campo obligatorio");
        }
    }
//public boolean OnCreateOptionMenu(){
//
//        getMenuInflater().inflate(R.id.);
//}

    public boolean onOptionsItemSelected(MenuItem item) {
        String nombre = enombre.getText().toString();
        String apellido = eapellido.getText().toString();
        String correo = eemail.getText().toString();
        String password = epassword.getText().toString();

        switch (item.getItemId()) {
            case R.id.icono_add: {
                Toast.makeText(this, "agregar", Toast.LENGTH_SHORT).show();
                if (nombre.equals("") || apellido.equals("") || correo.equals("") || password.equals("")) {
                    validacion();
                } else {
                    Persona p = new Persona();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellido(apellido);
                    p.setEmail(correo);
                    p.setPassword(password);
                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "Agregado", Toast.LENGTH_SHORT).show();
                    limpiarCajas();
                }


            }
            break;

            case R.id.icono_save: {
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                p.setNombre(enombre.getText().toString().trim());
                p.setApellido(eapellido.getText().toString().trim());
                p.setEmail(eemail.getText().toString().trim());
                p.setPassword(epassword.getText().toString().trim());
                databaseReference.child("Persona").child(p.getUid()).setValue(p);
                Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
                limpiarCajas();
            }
            break;

            case R.id.icono_delete: {
                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
                limpiarCajas();
            }
            break;


        }
        return false;
    }
}
