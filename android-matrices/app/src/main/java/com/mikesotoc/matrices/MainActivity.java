package com.mikesotoc.matrices;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.text.InputType;

public class MainActivity extends Activity {
    private final MatrixEditor[] editors = new MatrixEditor[2];
    private TextView result;
    private LinearLayout resultGrid;
    private final String[] sizes={"1","2","3","4","5","6"};
    private LinearLayout page;
    private ScrollView scroll;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(16),dp(16),dp(16),dp(24));
        page.setBackgroundColor(0xfff5f7fb);
        scroll.addView(page);setContentView(scroll);
        TextView title=label("Matrices Offline",26,true);page.addView(title);
        page.addView(label("Cálculo local · decimales y enteros · matrices hasta 6 × 6",14,false));
        editors[0]=new MatrixEditor("A");editors[1]=new MatrixEditor("B");
        result=label("Elige una operación para ver el resultado.",17,true);page.addView(result);
        resultGrid=new LinearLayout(this);resultGrid.setOrientation(LinearLayout.VERTICAL);page.addView(resultGrid);
        page.addView(label("Operaciones con A y B",18,true));
        rowButtons(new String[]{"A + B","A − B"},new int[]{0,1});
        rowButtons(new String[]{"B − A","A × B"},new int[]{2,3});
        rowButtons(new String[]{"B × A"},new int[]{4});
        page.addView(label("Operaciones con A",18,true));
        rowButtons(new String[]{"Aᵀ","det(A)"},new int[]{5,6});
        rowButtons(new String[]{"A⁻¹","rango(A)"},new int[]{7,8});
        rowButtons(new String[]{"Reducir A","traza(A)"},new int[]{9,10});
        page.addView(label("Operaciones con B",18,true));
        rowButtons(new String[]{"Bᵀ","det(B)"},new int[]{11,12});
        rowButtons(new String[]{"B⁻¹","rango(B)"},new int[]{13,14});
        rowButtons(new String[]{"Reducir B","traza(B)"},new int[]{15,16});
    }
    private void rowButtons(String[] captions,int[] operations) {
        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);page.addView(row);
        for(int i=0;i<captions.length;i++) {
            final int op=operations[i];Button button=new Button(this);button.setText(captions[i]);button.setAllCaps(false);
            row.addView(button,new LinearLayout.LayoutParams(0,dp(52),1));button.setOnClickListener(v->calculate(op));
        }
    }
    private void calculate(int op) {
        resultGrid.removeAllViews();
        try {
            double[][] a=null,b=null,answer;
            if(op<=10)a=editors[0].read();
            if(op<=4||op>=11)b=editors[1].read();
            switch(op) {
                case 0: answer=MatrixOps.add(a,b);break;
                case 1: answer=MatrixOps.subtract(a,b);break;
                case 2: answer=MatrixOps.subtract(b,a);break;
                case 3: answer=MatrixOps.multiply(a,b);break;
                case 4: answer=MatrixOps.multiply(b,a);break;
                case 5: answer=MatrixOps.transpose(a);break;
                case 6: answer=new double[][]{{MatrixOps.determinant(a)}};break;
                case 7: answer=MatrixOps.inverse(a);break;
                case 8: answer=new double[][]{{MatrixOps.rank(a)}};break;
                case 9: answer=MatrixOps.rref(a);break;
                case 10: answer=new double[][]{{MatrixOps.trace(a)}};break;
                case 11: answer=MatrixOps.transpose(b);break;
                case 12: answer=new double[][]{{MatrixOps.determinant(b)}};break;
                case 13: answer=MatrixOps.inverse(b);break;
                case 14: answer=new double[][]{{MatrixOps.rank(b)}};break;
                case 15: answer=MatrixOps.rref(b);break;
                default: answer=new double[][]{{MatrixOps.trace(b)}};
            }
            String[] names={"A + B","A − B","B − A","A × B","B × A","Aᵀ","det(A)","A⁻¹","rango(A)","forma reducida A","traza(A)","Bᵀ","det(B)","B⁻¹","rango(B)","forma reducida B","traza(B)"};
            result.setText(names[op]+"  ·  "+answer.length+" × "+answer[0].length);
            for(double[] values:answer) {
                LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);resultGrid.addView(row);
                for(double value:values) {
                    TextView cell=label(MatrixOps.format(value),16,false);cell.setGravity(Gravity.CENTER);
                    cell.setBackgroundColor(Color.WHITE);cell.setPadding(dp(5),dp(12),dp(5),dp(12));
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,-2,1);params.setMargins(dp(2),dp(2),dp(2),dp(2));row.addView(cell,params);
                }
            }
        } catch(IllegalArgumentException ex) { result.setText(ex.getMessage()); }
        scroll.post(()->scroll.smoothScrollTo(0,result.getTop()));
    }
    private TextView label(String text,int sp,boolean bold) {
        TextView view=new TextView(this);view.setText(text);view.setTextSize(sp);view.setTextColor(0xff17243b);
        if(bold)view.setTypeface(null,1);
        view.setPadding(0,dp(9),0,dp(9));return view;
    }
    private Spinner sizeSpinner() {
        Spinner spinner=new Spinner(this);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,sizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spinner.setAdapter(adapter);spinner.setSelection(1);
        return spinner;
    }
    private int dp(int value) { return (int)(value*getResources().getDisplayMetrics().density+0.5f); }
    private final class MatrixEditor {
        final String name;final Spinner rows=sizeSpinner(),cols=sizeSpinner();
        final LinearLayout grid=new LinearLayout(MainActivity.this);
        EditText[][] cells;
        MatrixEditor(String name) {
            this.name=name;page.addView(label("Matriz "+name,20,true));
            LinearLayout controls=new LinearLayout(MainActivity.this);controls.setGravity(Gravity.CENTER_VERTICAL);page.addView(controls);
            controls.addView(label("Filas",14,false));controls.addView(rows);controls.addView(label("Columnas",14,false));controls.addView(cols);
            Button resize=new Button(MainActivity.this);resize.setText("Aplicar");resize.setAllCaps(false);controls.addView(resize);
            resize.setOnClickListener(v->build(rows.getSelectedItemPosition()+1,cols.getSelectedItemPosition()+1));
            grid.setOrientation(LinearLayout.VERTICAL);page.addView(grid);build(2,2);
        }
        void build(int r,int c) {
            String[][] previous=null;
            if(cells!=null) {previous=new String[cells.length][cells[0].length];for(int i=0;i<cells.length;i++)for(int j=0;j<cells[i].length;j++)previous[i][j]=cells[i][j].getText().toString();}
            grid.removeAllViews();cells=new EditText[r][c];
            for(int i=0;i<r;i++) {
                LinearLayout row=new LinearLayout(MainActivity.this);grid.addView(row);
                for(int j=0;j<c;j++) {
                    EditText cell=new EditText(MainActivity.this);cell.setSingleLine(true);cell.setTextSize(16);cell.setGravity(Gravity.CENTER);
                    cell.setSelectAllOnFocus(true);cell.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    cell.setText(previous!=null&&i<previous.length&&j<previous[0].length?previous[i][j]:"0");
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,dp(52),1);params.setMargins(dp(2),dp(2),dp(2),dp(2));row.addView(cell,params);cells[i][j]=cell;
                }
            }
        }
        double[][] read() {
            double[][] values=new double[cells.length][cells[0].length];
            for(int i=0;i<cells.length;i++)for(int j=0;j<cells[i].length;j++) {
                String input=cells[i][j].getText().toString().trim().replace(',','.');
                try {values[i][j]=Double.parseDouble(input);if(!Double.isFinite(values[i][j]))throw new NumberFormatException();}
                catch(NumberFormatException e) {throw new IllegalArgumentException("Revisa "+name+"["+(i+1)+","+(j+1)+"]: escribe un número válido");}
            }
            return values;
        }
    }
}
