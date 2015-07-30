package com.example.jth.calculadora;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView txtResultado;
    private Button boton;
    private boolean nohaycoma = true;
    private boolean nohayoperador = true;
    private boolean sepuedeejecutar = false;

    public static double eval(final String str) {
        class Parser {
            int pos = -1, c;

            void eatChar() {
                c = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            void eatSpace() {
                while (Character.isWhitespace(c)) eatChar();
            }

            double parse() {
                eatChar();
                double v = parseExpression();
                if (c != -1) throw new RuntimeException("Unexpected: " + (char) c);
                return v;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor | term brackets
            // factor = brackets | number | factor `^` factor
            // brackets = `(` expression `)`

            double parseExpression() {
                double v = parseTerm();
                for (; ; ) {
                    eatSpace();
                    if (c == '+') { // addition
                        eatChar();
                        v += parseTerm();
                    } else if (c == '-') { // subtraction
                        eatChar();
                        v -= parseTerm();
                    } else {
                        return v;
                    }
                }
            }

            double parseTerm() {
                double v = parseFactor();
                for (; ; ) {
                    eatSpace();
                    if (c == '/') { // division
                        eatChar();
                        v /= parseFactor();
                    } else if (c == '*' || c == '(') { // multiplication
                        if (c == '*') eatChar();
                        v *= parseFactor();
                    } else {
                        return v;
                    }
                }
            }

            double parseFactor() {
                double v;
                boolean negate = false;
                eatSpace();
                if (c == '+' || c == '-') { // unary plus & minus
                    negate = c == '-';
                    eatChar();
                    eatSpace();
                }
                if (c == '(') { // brackets
                    eatChar();
                    v = parseExpression();
                    if (c == ')') eatChar();
                } else { // numbers
                    StringBuilder sb = new StringBuilder();
                    while ((c >= '0' && c <= '9') || c == '.') {
                        sb.append((char) c);
                        eatChar();
                    }
                    if (sb.length() == 0) throw new RuntimeException("Unexpected: " + (char) c);
                    v = Double.parseDouble(sb.toString());
                }
                eatSpace();
                if (c == '^') { // exponentiation
                    eatChar();
                    v = Math.pow(v, parseFactor());
                }
                if (negate) v = -v; // unary minus is applied after exponentiation; e.g. -3^2=-9
                return v;
            }
        }
        return new Parser().parse();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtResultado = (TextView) findViewById(R.id.txtResultado);
        findViewById(R.id.btnSalir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickC(View V) {
        nohaycoma = true;
        nohayoperador = true;
        sepuedeejecutar = false;
        txtResultado.setText("0");
    }

    public void clickNumero(View V) {
        boton = (Button) V;
        if (txtResultado.getText().toString().equals("0")) txtResultado.setText("");
        txtResultado.append(boton.getText().toString());
        sepuedeejecutar = true;
    }

    public void clickComa(View V) {
        if (nohaycoma) {
            nohaycoma = false;
            boton = (Button) V;
            txtResultado.append(boton.getText().toString());
            sepuedeejecutar = false;
        }
    }

    public void clickOperador(View V) {
        if (nohayoperador) {
            nohayoperador = false;
            nohaycoma = true;
            boton = (Button) V;
            txtResultado.append(boton.getText().toString());
            sepuedeejecutar = false;
        }
    }

    public void clickEjecutar(View V) {
        if (sepuedeejecutar) {
            double Resul = 0;
            String txtResul;
            nohaycoma = false;
            nohayoperador = true;
            txtResul = txtResultado.getText().toString();
            Resul = eval(txtResul);
            DecimalFormat formato = new DecimalFormat("0.##");
            txtResultado.setText(formato.format(Resul).replace(",", "."));
            //txtResultado.setText(String.valueOf(Resul));
        }

    }
}
