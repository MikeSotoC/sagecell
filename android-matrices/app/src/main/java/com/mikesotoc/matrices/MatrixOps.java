package com.mikesotoc.matrices;

import java.util.Locale;

/** Motor numérico independiente de Android; nunca modifica las matrices de entrada. */
public final class MatrixOps {
    private MatrixOps() {}
    private static final double EPS=1e-12;
    private static void check(double[][] a) {
        if(a==null||a.length==0||a[0]==null||a[0].length==0) throw new IllegalArgumentException("Matriz vacía");
        int n=a[0].length;
        for(double[] row:a) {
            if(row==null||row.length!=n) throw new IllegalArgumentException("Matriz irregular");
            for(double v:row) if(!Double.isFinite(v)) throw new IllegalArgumentException("Solo se admiten números finitos");
        }
    }
    private static void square(double[][] a) {
        check(a);if(a.length!=a[0].length) throw new IllegalArgumentException("Esta operación requiere una matriz cuadrada");
    }
    private static void same(double[][] a,double[][] b) {
        check(a);check(b);
        if(a.length!=b.length||a[0].length!=b[0].length) throw new IllegalArgumentException("Suma y resta requieren matrices del mismo tamaño");
    }
    private static double[][] copy(double[][] a) {
        double[][] out=new double[a.length][a[0].length];
        for(int i=0;i<a.length;i++) System.arraycopy(a[i],0,out[i],0,a[i].length);
        return out;
    }
    private static double scale(double[][] a) {
        double max=0;for(double[] row:a)for(double v:row)max=Math.max(max,Math.abs(v));return max;
    }
    public static double[][] add(double[][] a,double[][] b) {
        same(a,b);double[][] out=copy(a);
        for(int i=0;i<a.length;i++)for(int j=0;j<a[0].length;j++)out[i][j]+=b[i][j];return out;
    }
    public static double[][] subtract(double[][] a,double[][] b) {
        same(a,b);double[][] out=copy(a);
        for(int i=0;i<a.length;i++)for(int j=0;j<a[0].length;j++)out[i][j]-=b[i][j];return out;
    }
    public static double[][] multiply(double[][] a,double[][] b) {
        check(a);check(b);
        if(a[0].length!=b.length) throw new IllegalArgumentException("Producto incompatible: columnas de la primera ("+a[0].length+") ≠ filas de la segunda ("+b.length+")");
        double[][] out=new double[a.length][b[0].length];
        for(int i=0;i<a.length;i++)for(int j=0;j<b[0].length;j++)for(int k=0;k<b.length;k++)out[i][j]+=a[i][k]*b[k][j];
        return out;
    }
    public static double[][] transpose(double[][] a) {
        check(a);double[][] out=new double[a[0].length][a.length];
        for(int i=0;i<a.length;i++)for(int j=0;j<a[0].length;j++)out[j][i]=a[i][j];return out;
    }
    public static double trace(double[][] a) {
        square(a);double result=0;for(int i=0;i<a.length;i++)result+=a[i][i];return result;
    }
    public static double determinant(double[][] a) {
        square(a);int n=a.length;double[][] m=copy(a);double det=1;
        for(int k=0;k<n;k++) {
            int p=k;for(int i=k+1;i<n;i++)if(Math.abs(m[i][k])>Math.abs(m[p][k]))p=i;
            if(m[p][k]==0)return 0;
            if(p!=k){double[] t=m[p];m[p]=m[k];m[k]=t;det=-det;}
            double pivot=m[k][k];det*=pivot;
            for(int i=k+1;i<n;i++){
                double factor=m[i][k]/pivot;
                for(int j=k+1;j<n;j++)m[i][j]-=factor*m[k][j];
                m[i][k]=0;
            }
        }
        return det;
    }
    public static double[][] inverse(double[][] a) {
        square(a);int n=a.length;double[][] m=new double[n][2*n];double max=scale(a);
        for(int i=0;i<n;i++){System.arraycopy(a[i],0,m[i],0,n);m[i][n+i]=1;}
        for(int k=0;k<n;k++){
            int p=k;for(int i=k+1;i<n;i++)if(Math.abs(m[i][k])>Math.abs(m[p][k]))p=i;
            if(max==0||Math.abs(m[p][k])<=EPS*max)throw new IllegalArgumentException("Matriz singular o casi singular: no tiene inversa numérica fiable");
            if(p!=k){double[] t=m[p];m[p]=m[k];m[k]=t;}
            double pivot=m[k][k];for(int j=0;j<2*n;j++)m[k][j]/=pivot;
            for(int i=0;i<n;i++)if(i!=k){
                double factor=m[i][k];for(int j=0;j<2*n;j++)m[i][j]-=factor*m[k][j];m[i][k]=0;
            }
        }
        double[][] out=new double[n][n];for(int i=0;i<n;i++)System.arraycopy(m[i],n,out[i],0,n);return out;
    }
    public static double[][] rref(double[][] a) {
        check(a);double[][] m=copy(a);int rows=a.length,cols=a[0].length,pivotRow=0;
        double threshold=EPS*scale(a);
        for(int col=0;col<cols&&pivotRow<rows;col++){
            int p=pivotRow;for(int i=pivotRow+1;i<rows;i++)if(Math.abs(m[i][col])>Math.abs(m[p][col]))p=i;
            if(Math.abs(m[p][col])<=threshold)continue;
            if(p!=pivotRow){double[] t=m[p];m[p]=m[pivotRow];m[pivotRow]=t;}
            double pivot=m[pivotRow][col];for(int j=0;j<cols;j++)m[pivotRow][j]/=pivot;
            for(int i=0;i<rows;i++)if(i!=pivotRow){
                double factor=m[i][col];for(int j=0;j<cols;j++)m[i][j]-=factor*m[pivotRow][j];m[i][col]=0;
            }
            pivotRow++;
        }
        // Limpieza respecto a la escala original; evita borrar soluciones pequeñas válidas.
        for(int i=0;i<rows;i++)for(int j=0;j<cols;j++)if(Math.abs(m[i][j])<=EPS)m[i][j]=0;
        return m;
    }
    public static int rank(double[][] a) {
        double[][] m=rref(a);int rank=0;
        for(double[] row:m)for(double v:row)if(v!=0){rank++;break;}
        return rank;
    }
    public static String format(double v){return String.format(Locale.US,"%.10g",v==0?0:v);}
}
