package com.wildbitsfoundry.etk4j.math.linearalgebra;

import com.wildbitsfoundry.etk4j.constants.ConstantsETK;
import com.wildbitsfoundry.etk4j.math.MathETK;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixTest {

    @Test
    public void allTests() {
        Matrix A, B, C, Z, O, I, R, S, X, SUB, M, T, SQ, DEF, SOL;
        // Uncomment this to test IO in a different locale.
        // Locale.setDefault(Locale.GERMAN);
        int errorCount = 0;
        int warningCount = 0;
        double tmp, s;
        double[] columnwise = {1., 2., 3., 4., 5., 6., 7., 8., 9., 10., 11., 12.};
        double[] rowwise = {1., 4., 7., 10., 2., 5., 8., 11., 3., 6., 9., 12.};
        double[][] avals = {{1., 4., 7., 10.}, {2., 5., 8., 11.}, {3., 6., 9., 12.}};
        double[][] rankdef = avals;
        double[][] tvals = {{1., 2., 3.}, {4., 5., 6.}, {7., 8., 9.}, {10., 11., 12.}};
        double[][] subavals = {{5., 8., 11.}, {6., 9., 12.}};
        double[][] rvals = {{1., 4., 7.}, {2., 5., 8., 11.}, {3., 6., 9., 12.}};
        double[][] pvals = {{4., 1., 1.}, {1., 2., 3.}, {1., 3., 6.}};
        double[][] ivals = {{1., 0., 0., 0.}, {0., 1., 0., 0.}, {0., 0., 1., 0.}};
        double[][] evals =
                {{0., 1., 0., 0.}, {1., 0., 2.e-7, 0.}, {0., -2.e-7, 0., 1.}, {0., 0., 1., 0.}};
        double[][] square = {{166., 188., 210.}, {188., 214., 240.}, {210., 240., 270.}};
        double[][] sqSolution = {{13.}, {15.}};
        double[][] condmat = {{1., 3.}, {7., 9.}};
        int rows = 3, cols = 4;
        int invalidld = 5;/* should trigger bad shape for construction with val */
        int raggedr = 0; /* (raggedr,raggedc) should be out of bounds in ragged array */
        int raggedc = 4;
        int validld = 3; /* leading dimension of intended test Matrices */
        int nonconformld = 4; /* leading dimension which is valid, but nonconforming */
        int ib = 1, ie = 2, jb = 1, je = 3; /* index ranges for sub Matrix */
        int[] rowindexset = {1, 2};
        int[] badrowindexset = {1, 3};
        int[] columnindexset = {1, 2, 3};
        int[] badcolumnindexset = {1, 2, 4};
        double columnsummax = 33.;
        double rowsummax = 30.;
        double sumofdiagonals = 15;
        double sumofsquares = 650;

/**
 Constructors and constructor-like methods:
 double[], int
 double[][]  
 int, int
 int, int, double
 int, int, double[][]
 constructWithCopy(double[][])
 Random(int,int)
 identity(int)
 **/

        print("\nTesting constructors and constructor-like methods...\n");
        try {
            /** check that exception is thrown in packed constructor with invalid length **/
            A = new Matrix(columnwise, invalidld);
            errorCount = try_failure(errorCount, "Catch invalid length in packed constructor... ",
                    "exception not thrown for invalid input");
        } catch (IllegalArgumentException e) {
            try_success("Catch invalid length in packed constructor... ",
                    e.getMessage());
        }
        try {
            /** check that exception is thrown in default constructor
             if input array is 'ragged' **/
            A = new Matrix(rvals);
            tmp = A.get(raggedr, raggedc);
        } catch (IllegalArgumentException e) {
            try_success("Catch ragged input to default constructor... ",
                    e.getMessage());
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "Catch ragged input to constructor... ",
                    "exception not thrown in construction...ArrayIndexOutOfBoundsException thrown later");
        }
        try {
            /** check that exception is thrown in constructWithCopy
             if input array is 'ragged' **/
            A = new Matrix(rvals);
            tmp = A.get(raggedr, raggedc);
        } catch (IllegalArgumentException e) {
            try_success("Catch ragged input to constructWithCopy... ", e.getMessage());
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "Catch ragged input to constructWithCopy... ", "exception not thrown in construction...ArrayIndexOutOfBoundsException thrown later");
        }

        A = new Matrix(columnwise, validld);
        B = new Matrix(avals);
        tmp = B.get(0, 0);
        avals[0][0] = 0.0;
        C = B.subtract(A);
        avals[0][0] = tmp;
        B = new Matrix(avals);
        tmp = B.get(0, 0);
        avals[0][0] = 0.0;
        if ((tmp - B.get(0, 0)) != 0.0) {
            /** check that constructWithCopy behaves properly **/
            errorCount = try_failure(errorCount, "constructWithCopy... ", "copy not effected... data visible outside");
        } else {
            try_success("constructWithCopy... ", "");
        }
        avals[0][0] = columnwise[0];
        I = new Matrix(ivals);
        try {
            check(I, Matrices.Identity(3, 4));
            try_success("identity... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "identity... ", "identity Matrix not successfully created");
        }

/**
 Access Methods:
 getColumnCount()
 getRowCount()
 getArray()
 getArrayCopy()
 getColumnPackedCopy()
 getRowPackedCopy()
 get(int,int)
 subMatrix(int,int,int,int)
 subMatrix(int,int,int[])
 subMatrix(int[],int,int)
 subMatrix(int[],int[])
 set(int,int,double)
 setMatrix(int,int,int,int,Matrix)
 setMatrix(int,int,int[],Matrix)
 setMatrix(int[],int,int,Matrix)
 setMatrix(int[],int[],Matrix)
 **/

        print("\nTesting access methods...\n");

/**
 Various get methods:
 **/

        B = new Matrix(avals);
        if (B.getRowCount() != rows) {
            errorCount = try_failure(errorCount, "getRowCount... ", "");
        } else {
            try_success("getRowCount... ", "");
        }
        if (B.getColumnCount() != cols) {
            errorCount = try_failure(errorCount, "getColumnCount... ", "");
        } else {
            try_success("getColumnCount... ", "");
        }
        B = new Matrix(avals);
        double[][] barray = B.getAs2DArray();
        if (barray == avals) {
            errorCount = try_failure(errorCount, "getArray... ", "");
        } else {
            try_success("getArray... ", "");
        }
        double[] bpacked = B.getColumnPackedCopy();
        try {
            check(bpacked, columnwise);
            try_success("getColumnPackedCopy... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "getColumnPackedCopy... ", "data not successfully (deep) copied by columns");
        }
        bpacked = B.getRowPackedCopy();
        try {
            check(bpacked, rowwise);
            try_success("getRowPackedCopy... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "getRowPackedCopy... ", "data not successfully (deep) copied by rows");
        }
        try {
            tmp = B.get(B.getRowCount(), B.getColumnCount() - 1);
            errorCount = try_failure(errorCount, "get(int,int)... ", "OutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                tmp = B.get(B.getRowCount() - 1, B.getColumnCount());
                errorCount = try_failure(errorCount, "get(int,int)... ", "OutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("get(int,int)... OutofBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "get(int,int)... ", "OutOfBoundsException expected but not thrown");
        }
        try {
            if (B.get(B.getRowCount() - 1, B.getColumnCount() - 1) !=
                    avals[B.getRowCount() - 1][B.getColumnCount() - 1]) {
                errorCount = try_failure(errorCount, "get(int,int)... ", "Matrix entry (i,j) not successfully retreived");
            } else {
                try_success("get(int,int)... ", "");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "get(int,int)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        SUB = new Matrix(subavals);
        try {
            M = B.subMatrix(ib, ie + B.getRowCount() + 1, jb, je);
            errorCount = try_failure(errorCount, "subMatrix(int,int,int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                M = B.subMatrix(ib, ie, jb, je + B.getColumnCount() + 1);
                errorCount = try_failure(errorCount, "subMatrix(int,int,int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("subMatrix(int,int,int,int)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "subMatrix(int,int,int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            M = B.subMatrix(ib, ie, jb, je);
            try {
                check(SUB, M);
                try_success("subMatrix(int,int,int,int)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "subMatrix(int,int,int,int)... ", "submatrix not successfully retreived");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "subMatrix(int,int,int,int)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }

        try {
            M = B.subMatrix(ib, ie, badcolumnindexset);
            errorCount = try_failure(errorCount, "subMatrix(int,int,int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                M = B.subMatrix(ib, ie + B.getRowCount() + 1, columnindexset);
                errorCount = try_failure(errorCount, "subMatrix(int,int,int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("subMatrix(int,int,int[])... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "subMatrix(int,int,int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            M = B.subMatrix(ib, ie, columnindexset);
            try {
                check(SUB, M);
                try_success("subMatrix(int,int,int[])... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "subMatrix(int,int,int[])... ", "submatrix not successfully retreived");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "subMatrix(int,int,int[])... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            M = B.subMatrix(badrowindexset, jb, je);
            errorCount = try_failure(errorCount, "subMatrix(int[],int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                M = B.subMatrix(rowindexset, jb, je + B.getColumnCount() + 1);
                errorCount = try_failure(errorCount, "subMatrix(int[],int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("subMatrix(int[],int,int)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "subMatrix(int[],int,int)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            M = B.subMatrix(rowindexset, jb, je);
            try {
                check(SUB, M);
                try_success("subMatrix(int[],int,int)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "subMatrix(int[],int,int)... ", "submatrix not successfully retreived");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "subMatrix(int[],int,int)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            M = B.subMatrix(badrowindexset, columnindexset);
            errorCount = try_failure(errorCount, "subMatrix(int[],int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                M = B.subMatrix(rowindexset, badcolumnindexset);
                errorCount = try_failure(errorCount, "subMatrix(int[],int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("subMatrix(int[],int[])... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "subMatrix(int[],int[])... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            M = B.subMatrix(rowindexset, columnindexset);
            try {
                check(SUB, M);
                try_success("subMatrix(int[],int[])... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "subMatrix(int[],int[])... ", "submatrix not successfully retreived");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            errorCount = try_failure(errorCount, "subMatrix(int[],int[])... ", "Unexpected ArrayIndexOutOfBoundsException");
        }

/**
 Various set methods:
 **/

        try {
            B.set(B.getRowCount(), B.getColumnCount() - 1, 0.);
            errorCount = try_failure(errorCount, "set(int,int,double)... ", "OutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.set(B.getRowCount() - 1, B.getColumnCount(), 0.);
                errorCount = try_failure(errorCount, "set(int,int,double)... ", "OutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("set(int,int,double)... OutofBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "set(int,int,double)... ", "OutOfBoundsException expected but not thrown");
        }
        try {
            B.set(ib, jb, 0.);
            tmp = B.get(ib, jb);
            try {
                check(tmp, 0.);
                try_success("set(int,int,double)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "set(int,int,double)... ", "Matrix element not successfully set");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "set(int,int,double)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        M = new Matrix(2, 3, 0.);
        try {
            B.setMatrix(ib, ie + B.getRowCount() + 1, jb, je, M);
            errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.setMatrix(ib, ie, jb, je + B.getColumnCount() + 1, M);
                errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("setMatrix(int,int,int,int,Matrix)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            B.setMatrix(ib, ie, jb, je, M);
            try {
                check(M.subtract(B.subMatrix(ib, ie, jb, je)), M);
                try_success("setMatrix(int,int,int,int,Matrix)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "submatrix not successfully set");
            }
            B.setMatrix(ib, ie, jb, je, SUB);
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int,int,int,int,Matrix)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            B.setMatrix(ib, ie + B.getRowCount() + 1, columnindexset, M);
            errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.setMatrix(ib, ie, badcolumnindexset, M);
                errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("setMatrix(int,int,int[],Matrix)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            B.setMatrix(ib, ie, columnindexset, M);
            try {
                check(M.subtract(B.subMatrix(ib, ie, columnindexset)), M);
                try_success("setMatrix(int,int,int[],Matrix)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "submatrix not successfully set");
            }
            B.setMatrix(ib, ie, jb, je, SUB);
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int,int,int[],Matrix)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            B.setMatrix(rowindexset, jb, je + B.getColumnCount() + 1, M);
            errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.setMatrix(badrowindexset, jb, je, M);
                errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("setMatrix(int[],int,int,Matrix)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            B.setMatrix(rowindexset, jb, je, M);
            try {
                check(M.subtract(B.subMatrix(rowindexset, jb, je)), M);
                try_success("setMatrix(int[],int,int,Matrix)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "submatrix not successfully set");
            }
            B.setMatrix(ib, ie, jb, je, SUB);
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int[],int,int,Matrix)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }
        try {
            B.setMatrix(rowindexset, badcolumnindexset, M);
            errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            try {
                B.setMatrix(badrowindexset, columnindexset, M);
                errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
            } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
                try_success("setMatrix(int[],int[],Matrix)... ArrayIndexOutOfBoundsException... ", "");
            }
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "ArrayIndexOutOfBoundsException expected but not thrown");
        }
        try {
            B.setMatrix(rowindexset, columnindexset, M);
            try {
                check(M.subtract(B.subMatrix(rowindexset, columnindexset)), M);
                try_success("setMatrix(int[],int[],Matrix)... ", "");
            } catch (java.lang.RuntimeException e) {
                errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "submatrix not successfully set");
            }
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            errorCount = try_failure(errorCount, "setMatrix(int[],int[],Matrix)... ", "Unexpected ArrayIndexOutOfBoundsException");
        }

/**
 Array-like methods:
 subtract
 subtractEquals
 plus
 addEquals
 arrayLeftDivide
 arrayLeftDivideEquals
 arrayRightDivide
 arrayRightDivideEquals
 arrayMultiply
 arrayMultiplyEquals
 usubtract
 **/

        print("\nTesting array-like methods...\n");
        S = new Matrix(columnwise, nonconformld);
        R = Matrices.Random(A.getRowCount(), A.getColumnCount());
        A = R;
        try {
            S = A.subtract(S);
            errorCount = try_failure(errorCount, "subtract conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("subtract conformance check... ", "");
        }
        if (A.subtract(R).norm1() != 0.) {
            errorCount = try_failure(errorCount, "subtract... ", "(difference of identical Matrices is nonzero,\nSubsequent use of subtract should be suspect)");
        } else {
            try_success("subtract... ", "");
        }
        A = R.copy();
        A.subtractEquals(R);
        Z = new Matrix(A.getRowCount(), A.getColumnCount());
        try {
            A.subtractEquals(S);
            errorCount = try_failure(errorCount, "subtractEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("subtractEquals conformance check... ", "");
        }
        if (A.subtract(Z).norm1() != 0.) {
            errorCount = try_failure(errorCount, "subtractEquals... ", "(difference of identical Matrices is nonzero,\nSubsequent use of subtract should be suspect)");
        } else {
            try_success("subtractEquals... ", "");
        }

        A = R.copy();
        B = Matrices.Random(A.getRowCount(), A.getColumnCount());
        C = A.subtract(B);
        try {
            S = A.add(S);
            errorCount = try_failure(errorCount, "plus conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("plus conformance check... ", "");
        }
        try {
            check(C.add(B), A);
            try_success("plus... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "plus... ", "(C = A - B, but C + B != A)");
        }
        C = A.subtract(B);
        C.addEquals(B);
        try {
            A.addEquals(S);
            errorCount = try_failure(errorCount, "addEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("addEquals conformance check... ", "");
        }
        try {
            check(C, A);
            try_success("addEquals... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "addEquals... ", "(C = A - B, but C = C + B != A)");
        }
        A = R.uminus();
        try {
            check(A.add(R), Z);
            try_success("usubtract... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "usubtract... ", "(-A + A != zeros)");
        }
        A = R.copy();
        O = new Matrix(A.getRowCount(), A.getColumnCount(), 1.0);
        C = A.arrayLeftDivide(R);
        try {
            S = A.arrayLeftDivide(S);
            errorCount = try_failure(errorCount, "arrayLeftDivide conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayLeftDivide conformance check... ", "");
        }
        try {
            check(C, O);
            try_success("arrayLeftDivide... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayLeftDivide... ", "(M.\\M != ones)");
        }
        try {
            A.arrayLeftDivideEquals(S);
            errorCount = try_failure(errorCount, "arrayLeftDivideEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayLeftDivideEquals conformance check... ", "");
        }
        A.arrayLeftDivideEquals(R);
        try {
            check(A, O);
            try_success("arrayLeftDivideEquals... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayLeftDivideEquals... ", "(M.\\M != ones)");
        }
        A = R.copy();
        try {
            A.arrayRightDivide(S);
            errorCount = try_failure(errorCount, "arrayRightDivide conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayRightDivide conformance check... ", "");
        }
        C = A.arrayRightDivide(R);
        try {
            check(C, O);
            try_success("arrayRightDivide... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayRightDivide... ", "(M./M != ones)");
        }
        try {
            A.arrayRightDivideEquals(S);
            errorCount = try_failure(errorCount, "arrayRightDivideEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayRightDivideEquals conformance check... ", "");
        }
        A.arrayRightDivideEquals(R);
        try {
            check(A, O);
            try_success("arrayRightDivideEquals... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayRightDivideEquals... ", "(M./M != ones)");
        }
        A = R.copy();
        B = Matrices.Random(A.getRowCount(), A.getColumnCount());
        try {
            S = A.arrayMultiply(S);
            errorCount = try_failure(errorCount, "arrayMultiply conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayMultiply conformance check... ", "");
        }
        C = A.arrayMultiply(B);
        try {
            C.arrayRightDivideEquals(B);
            check(C, A);
            try_success("arrayMultiply... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayMultiply... ", "(A = R, C = A.*B, but C./B != A)");
        }
        try {
            A.arrayMultiplyEquals(S);
            errorCount = try_failure(errorCount, "arrayMultiplyEquals conformance check... ", "nonconformance not raised");
        } catch (IllegalArgumentException e) {
            try_success("arrayMultiplyEquals conformance check... ", "");
        }
        A.arrayMultiplyEquals(B);
        try {
            A.arrayRightDivideEquals(B);
            check(A, R);
            try_success("arrayMultiplyEquals... ", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "arrayMultiplyEquals... ", "(A = R, A = A.*B, but A./B != R)");
        }


/**
 LA methods:
 transpose
 multiply
 cond
 rank
 det
 trace
 norm1
 norm2
 normF
 normInf
 solve
 solveTranspose
 inverse
 chol
 eig
 lu
 qr
 svd 
 **/

        print("\nTesting linear algebra methods...\n");
        A = new Matrix(columnwise, 3);
        T = new Matrix(tvals);
        T = A.transpose();
        try {
            check(A.transpose(), T);
            try_success("transpose...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "transpose()...", "transpose unsuccessful");
        }
        A.transpose();
        try {
            check(A.norm1(), columnsummax);
            try_success("norm1...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "norm1()...", "incorrect norm calculation");
        }
        try {
            check(A.normInf(), rowsummax);
            try_success("normInf()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "normInf()...", "incorrect norm calculation");
        }
        try {
            check(A.normFrob(), Math.sqrt(sumofsquares));
            try_success("normF...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "normF()...", "incorrect norm calculation");
        }
        try {
            check(A.trace(), sumofdiagonals);
            try_success("trace()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "trace()...", "incorrect trace calculation");
        }
        try {
            check(A.subMatrix(0, A.getRowCount() - 1, 0, A.getRowCount() - 1).det(), 0.);
            try_success("det()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "det()...", "incorrect determinant calculation");
        }
        SQ = new Matrix(square);
        try {
            check(A.multiply(A.transpose()), SQ);
            try_success("multiply(Matrix)...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "multiply(Matrix)...", "incorrect Matrix-Matrix product calculation");
        }
        try {
            check(A.multiply(0.), Z);
            try_success("multiply(double)...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "multiply(double)...", "incorrect Matrix-scalar product calculation");
        }

        A = new Matrix(columnwise, 4);
        QRDecomposition QR = A.QR();
        R = QR.getR();
        try {
            check(A, QR.getQThin().multiply(R));
            try_success("QRDecomposition...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "QRDecomposition...", "incorrect QR decomposition calculation");
        }
        SingularValueDecomposition SVD = A.SVD();
        try {
            check(A, SVD.getU().multiply(SVD.getS().multiply(SVD.getV().transpose())));
            try_success("SingularValueDecomposition...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "SingularValueDecomposition...", "incorrect singular value decomposition calculation");
        }
        DEF = new Matrix(rankdef);
        try {
            check(DEF.rank(), Math.min(DEF.getRowCount(), DEF.getColumnCount()) - 1);
            try_success("rank()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "rank()...", "incorrect rank calculation");
        }
        B = new Matrix(condmat);
        SVD = B.SVD();
        double[] singularvalues = SVD.getSingularValues();
        try {
            check(B.cond(), singularvalues[0] / singularvalues[Math.min(B.getRowCount(), B.getColumnCount()) - 1]);
            try_success("cond()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "cond()...", "incorrect condition number calculation");
        }
        int n = A.getColumnCount();
        A = A.subMatrix(0, n - 1, 0, n - 1);
        A.set(0, 0, 0.);
        LUDecomposition LU = A.LU();
        try {
            check(A.subMatrix(LU.getPivot(), 0, n - 1), LU.getL().multiply(LU.getU()));
            try_success("LUDecomposition...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "LUDecomposition...", "incorrect LU decomposition calculation");
        }
        X = A.inv();
        try {
            check(A.multiply(X), Matrices.Identity(3, 3));
            try_success("inverse()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "inverse()...", "incorrect inverse calculation");
        }
        O = new Matrix(SUB.getRowCount(), 1, 1.0);
        SOL = new Matrix(sqSolution);
        SQ = SUB.subMatrix(0, SUB.getRowCount() - 1, 0, SUB.getRowCount() - 1);
        try {
            check(SQ.solve(SOL), O);
            try_success("solve()...", "");
        } catch (java.lang.IllegalArgumentException e1) {
            errorCount = try_failure(errorCount, "solve()...", e1.getMessage());
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "solve()...", e.getMessage());
        }
        A = new Matrix(pvals);
        CholeskyDecomposition Chol = A.Chol();
        Matrix L = Chol.getL();
        try {
            check(A, L.multiply(L.transpose()));
            try_success("CholeskyDecomposition...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "CholeskyDecomposition...", "incorrect Cholesky decomposition calculation");
        }
        X = Chol.solve(Matrices.Identity(3, 3));
        try {
            check(A.multiply(X), Matrices.Identity(3, 3));
            try_success("CholeskyDecomposition solve()...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "CholeskyDecomposition solve()...", "incorrect Choleskydecomposition solve calculation");
        }
        EigenvalueDecomposition Eig = A.eig();
        Matrix D = Eig.getD();
        Matrix V = Eig.getV();
        try {
            check(A.multiply(V), V.multiply(D));
            try_success("EigenvalueDecomposition (symmetric)...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "EigenvalueDecomposition (symmetric)...", "incorrect symmetric Eigenvalue decomposition calculation");
        }
        A = new Matrix(evals);
        Eig = A.eig();
        D = Eig.getD();
        V = Eig.getV();
        try {
            check(A.multiply(V), V.multiply(D));
            try_success("EigenvalueDecomposition (nonsymmetric)...", "");
        } catch (java.lang.RuntimeException e) {
            errorCount = try_failure(errorCount, "EigenvalueDecomposition (nonsymmetric)...", "incorrect nonsymmetric Eigenvalue decomposition calculation");
        }

        assertEquals( 0, errorCount);

        print("\nTestMatrix completed.\n");
        print("Total errors reported: " + Integer.toString(errorCount) + "\n");
        print("Total warnings reported: " + Integer.toString(warningCount) + "\n");
    }

    /** private utility routines **/

    /**
     * Check magnitude of difference of scalars.
     **/

    private static void check(double x, double y) {
        double eps = ConstantsETK.DOUBLE_EPS;
        if (x == 0 & Math.abs(y) < 10 * eps) return;
        if (y == 0 & Math.abs(x) < 10 * eps) return;
        if (Math.abs(x - y) > 10 * eps * Math.max(Math.abs(x), Math.abs(y))) {
            throw new RuntimeException("The difference x-y is too large: x = " + Double.toString(x) + "  y = " + Double.toString(y));
        }
    }

    /**
     * Check norm of difference of "vectors".
     **/

    private static void check(double[] x, double[] y) {
        if (x.length == y.length) {
            for (int i = 0; i < x.length; i++) {
                check(x[i], y[i]);
            }
        } else {
            throw new RuntimeException("Attempt to compare vectors of different lengths");
        }
    }

    /**
     * Check norm of difference of arrays.
     **/

    private static void check(double[][] x, double[][] y) {
        Matrix A = new Matrix(x);
        Matrix B = new Matrix(y);
        check(A, B);
    }

    /**
     * Check norm of difference of Matrices.
     **/

    private static void check(Matrix X, Matrix Y) {
        double eps = ConstantsETK.DOUBLE_EPS;
        if (X.norm1() == 0. & Y.norm1() < 10 * eps) return;
        if (Y.norm1() == 0. & X.norm1() < 10 * eps) return;
        if (X.subtract(Y).norm1() > 1000 * eps * Math.max(X.norm1(), Y.norm1())) {
            throw new RuntimeException("The norm of (X-Y) is too large: " + Double.toString(X.subtract(Y).norm1()));
        }
    }

    /**
     * Shorten spelling of print.
     **/

    private static void print(String s) {
        System.out.print(s);
    }

    /**
     * Print appropriate messages for successful outcome try
     **/

    private static void try_success(String s, String e) {
        print(">    " + s + "success\n");
        if (e != "") {
            print(">      Message: " + e + "\n");
        }
    }

    /**
     * Print appropriate messages for unsuccessful outcome try
     **/

    private static int try_failure(int count, String s, String e) {
        print(">    " + s + "*** failure ***\n>      Message: " + e + "\n");
        return ++count;
    }

    /**
     * Print appropriate messages for unsuccessful outcome try
     **/

    private static int try_warning(int count, String s, String e) {
        print(">    " + s + "*** warning ***\n>      Message: " + e + "\n");
        return ++count;
    }
}
