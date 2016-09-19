package ru.javaops.masterjava.matrix;

import ru.javaops.masterjava.service.MailService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final CompletionService<int[]> completionService = new ExecutorCompletionService<>(executor);
        List<Future<int[]>> futures = new ArrayList<>();
        for(int i = 0; i < matrixA.length ; i++) {
            final int a = i;
            futures.add(completionService.submit(() -> {
                    int[] matrix = new int[matrixSize + 1];
                    matrix[0] = a;
                    for (int j = 0; j < matrixSize; j++) {
                        int sum = 0;
                        for (int k = 0; k < matrixSize; k++) {
                            sum += matrixA[a][k] * matrixB[k][j];
                        }
                        matrix[j + 1] = sum;
                    }
                    return matrix;
            }));
        }
        while (!futures.isEmpty()) {
            Future<int[]> future = completionService.poll(1, TimeUnit.MILLISECONDS);
            if (future == null) continue;
            futures.remove(future);
            int[] f = future.get();
            matrixC[f[0]] = Arrays.copyOfRange(f, 1, f.length);
        }
        /*for (Future<int[]> future : futures){
            while (!future.isDone()) {
                Thread.currentThread().sleep(1);
            }
            int[] f = future.get();
            matrixC[f[0]] = Arrays.copyOfRange(f, 1, f.length);
        }*/
        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
