import com.mikesotoc.matrices.MatrixOps;

public class MatrixOpsCheck {
    private static void equal(double expected,double actual) {
        if(Math.abs(expected-actual)>1e-8*Math.max(1,Math.abs(expected)))
            throw new AssertionError("Esperado "+expected+", recibido "+actual);
    }
    private static void invalid(Runnable action) {
        try {action.run();throw new AssertionError("Debió rechazar dimensiones incompatibles");}
        catch(IllegalArgumentException expected) { /* correcto */ }
    }
    public static void main(String[] args) {
        double[][] a={{1,2},{3,4}},b={{5,6},{7,8}};
        equal(19,MatrixOps.multiply(a,b)[0][0]);
        equal(23,MatrixOps.multiply(b,a)[0][0]);
        equal(4,MatrixOps.subtract(b,a)[0][0]);
        equal(-2,MatrixOps.determinant(a));
        equal(-2,MatrixOps.inverse(a)[0][0]);
        equal(1,MatrixOps.multiply(a,MatrixOps.inverse(a))[0][0]);
        equal(5,MatrixOps.trace(a));
        equal(2,MatrixOps.rank(a));
        equal(1,MatrixOps.rref(new double[][]{{1,2,3},{2,4,6}})[0][0]);
        equal(1,MatrixOps.rank(new double[][]{{1,2,3},{2,4,6}}));
        equal(3,MatrixOps.transpose(new double[][]{{1,2,3},{4,5,6}}).length);
        equal(1,MatrixOps.multiply(new double[][]{{1,2,3},{4,5,6}},new double[][]{{1,0},{0,1},{0,0}})[0][0]);
        invalid(()->MatrixOps.multiply(new double[][]{{1,2,3}},new double[][]{{1,2}}));
        invalid(()->MatrixOps.inverse(new double[][]{{1,2},{2,4}}));
        double[][] small={{1e-15,0},{0,2e-15}};
        equal(1e15,MatrixOps.inverse(small)[0][0]);
        if(MatrixOps.determinant(small)==0)throw new AssertionError("Determinante pequeño perdido");
        System.out.println("MatrixOps: OK");
    }
}
