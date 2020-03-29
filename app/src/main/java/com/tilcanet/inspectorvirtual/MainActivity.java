// CODIGO ESCRITO CON MUSICA DE FONDO -  https://www.youtube.com/watch?v=xw20n4pO258&t=1285s
// PROYECTO CREAR UNA APP QUE LEVANTE CODIGO DE BARRAS DE LOS PRODUCTOS QUE ESTAN DENTRO
// DE LOS PRECIOS CUIDADOS Y SI SE ENCUENTRA MUESTRE EN UN texview NOMBRE, MARCA Y PRECIO
// LA BASE DE DATOS SE CONECTARA A UN MYSQL QUE TIENE LOS CAMPOS EAN , PRODUCTO, MARCA Y PRECIO
// SALUDOS DESDE TILCARA - JUJUY 28/03/2020

package com.tilcanet.inspectorvirtual;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

// Identifico el Editext con el id que puse en cada widget
    EditText txtcodigo;
    TextView txtnombre, txtmarca, txtprecio;
    Button btncaptar, btnbuscar, btndenuncia;

    RequestQueue requestQueue;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// uno los id con el find

        txtcodigo = findViewById(R.id.txtcodigo);
        txtnombre = findViewById(R.id.txtnombre);
        txtmarca = findViewById(R.id.txtmarca);
        txtprecio = findViewById(R.id.txtprecio);
        btncaptar = findViewById(R.id.btncaptar);
        btnbuscar = findViewById(R.id.btnbuscar);
        btndenuncia = findViewById(R.id.btndenuncia);

        btncaptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escaner();
            }
        });
        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarProducto("https://tilcanet.com.ar/inspector/buscarproducto.php?ean="+txtcodigo.getText()+"");
            }
        });
    }

// Vamos a crear el metodo para que pueda escanear el codigo de barra
    public void escaner(){
        IntentIntegrator intent = new IntentIntegrator(this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);

        intent.setPrompt("CAPTAR");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result !=null){
            if (result.getContents() == null){
                Toast.makeText(this, "Cancelaste el escaneo", Toast.LENGTH_LONG).show();
            } else {
                txtcodigo.setText(result.getContents().toString());
            }
        } else {
             super.onActivityResult(requestCode, resultCode, data);
        }

    }
    private void buscarProducto(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        txtnombre.setText(jsonObject.getString("producto"));
                        txtmarca.setText(jsonObject.getString("marca"));
                        txtprecio.setText(jsonObject.getString("precio"));
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Producto NO Resgistrado", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
