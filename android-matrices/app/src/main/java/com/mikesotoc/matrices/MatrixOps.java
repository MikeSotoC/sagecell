package com.mikesotoc.matrices;

import java.util.Locale;

/** Pure Java engine. Arrays must be rectangular and contain finite values. */
public final class MatrixOps {
    private MatrixOps() {}
    private static void check(double[][] a) {
        if (a == null || a.length == 0 || a[0] == null || a[0].length == 0) throw new IllegalArgumentException("Matriz vacía");
        int n = a[0].length;
        for (double[] row : a) {
            if (row == null || row.length != n) throw new IllegalArgumentException("Matriz irregular");
            for (double v : row) if (!Double.isFinite(v)) throw new IllegalArgumentException("Solo se admiten números finitos");
        }
    }
    private static void same(double[][] a, double[][] b) {
        check(a); check(b);
        if (a.length != b.length || a[0].length != b[0].length) throw new IllegalArgumentException("A y B deben tener las mismas dimensiones");
    }
    public static double[][] add(double[][] a, double[][] b) {
        same(a,b); double[][] out = new double[a.length][a[0].length];
        for (int i=0;i<a.length;i++) for (int j=0;j<a[0].length;j++) out[i][j]=a[i][j]+b[i][j];
        return out;
    }
    public static double[][] subtract(double[][] a, double[][] b) {
        same(a,b); double[][] out = new double[a.length][a[0].length];
        for (int i=0;i<a.length;i++) for (int j=0;j<a[0].length;j++) out[i][j]=a[i][j]-b[i][j];
        return out;
    }
    public static double[][] multiply(double[][] a, double[][] b) {
        check(a); check(b);
        if (a[0].length != b.length) throw new IllegalArgumentException("Para A × B, columnas de A = filas de B");
        double[][] out=new double[a.length][b[0].length];
        for(int i=0;i<a.length;i++) for(int k=0;k<b.length;k++) for(int j=0;j<b[0].length;j++) out[i][j]+=a[i][k]*b[k][j];
        return out;
    }
    public static double[][] transpose(double[][] a) {
        check(a); double[][] out=new double[a[0].length][a.length];
        for(int i=0;i<a.length;i++) for(int j=0;j<a[0].length;j++) out[j][i]=a[i][j];
        return out;
    }
    private static double[][] elimination(double[][] a, boolean inverse) {
        check(a);
        int n=a.length;
        if(n!=a[0].length) throw new IllegalArgumentException("La matriz A debe ser cuadrada");
        double[][] m=new double[n][inverse ? 2*n : n];
        double scale=0;
        for(int i=0;i<n;i++) for(int j=0;j<n;j++) { m[i][j]=a[i][j]; scale=Math.max(scale,Math.abs(a[i][j])); }
        if(inverse) for(int i=0;i<n;i++) m[i][n+i]=1;
        double det=1; int sign=1;
        for(int k=0;k<n;k++) {
            int p=k;
            for(int i=k+1;i<n;i++) if(Math.abs(m[i][k])>Math.abs(m[p][k])) p=i;
            if(scale==0 || Math.abs(m[p][k])<=1e-12*scale) {
                if(inverse) throw new IllegalArgumentException("La matriz es singular o numéricamente inestable");
                return new double[][]{{0}};
            }
            if(p!=k) { double[] t=m[p];m[p]=m[k];m[k]=t;sign=-sign; }
            double pivot=m[k][k];det*=pivot;
            if(inverse) {
                for(int j=0;j<2*n;j++) m[k][j]/=pivot;
                for(int i=0;i<n;i++) if(i!=k) {
                    double factor=m[i][k];
                    for(int j=0;j<2*n;j++) m[i][j]-=factor*m[k][j];
                }
            } else for(int i=k+1;i<n;i++) {
                double factor=m[i][k]/pivot;
                for(int j=k+1;j<n;j++) m[i][j]-=factor*m[k][j];
                m[i][k]=0;
            }
        }
        if(!inverse) return new double[][]{{sign*det}};
        double[][] out=new double[n][n];
        for(int i=0;i<n;i++) System.arraycopy(m[i],n,out[i],0,n);
        return out;
    }
    public static double determinant(double[][] a) { return elimination(a,false)[0][0]; }
    public static double[][] inverse(double[][] a) { return elimination(a,true); }
    public static String format(double v) { return String.format(Locale.US,"%.8g",v==0 ? 0 : v); }
}
