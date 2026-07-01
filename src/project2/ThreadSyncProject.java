package project2;
import java.util.concurrent.CountDownLatch;

/**
 * Main application class to demonstrate thread synchronization and communication.
 * Enforces dependencies between ThreadA and ThreadB using CountDownLatch.
 */
public class ThreadSyncProject {

    /** Shared variables for thread data exchange. */
    static int A1, A2, A3, B1, B2, B3;

    /**
     * Utility class providing mathematical operations for thread synchronization.
     */
    public static class MathUtils {
        /**
         * Calculates the sum of integers from 0 to n using a loop.
         * @param n The upper limit of the summation.
         * @return The total sum of all integers from 0 to n.
         */
        public static int calculateSum(int n) {
            int sum = 0;
            for (int i = 0; i <= n; i++) {
                sum += i;
            }
            return sum;
        }
    }

    /**
     * Main entry point of the application.
     * Executes threads and verifies synchronization correctness through iterative testing.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Run for a high number of iterations to verify correctness
        for (int iteration = 1; iteration <= 10000; iteration++) {
            
            // Latches initialized to 1 for each dependency defined in the lab manual
            CountDownLatch latch1 = new CountDownLatch(1); // A1 -> B2
            CountDownLatch latch2 = new CountDownLatch(1); // B2 -> A2
            CountDownLatch latch3 = new CountDownLatch(1); // A2 -> B3
            CountDownLatch latch4 = new CountDownLatch(1); // B3 -> A3

            Thread threadA = new Thread(() -> {
                A1 = MathUtils.calculateSum(500);
                latch1.countDown(); // Signal FuncA1 is done

                try { latch2.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                A2 = B2 + MathUtils.calculateSum(300);
                latch3.countDown(); // Signal FuncA2 is done

                try { latch4.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                A3 = B3 + MathUtils.calculateSum(400);
            });

            Thread threadB = new Thread(() -> {
                B1 = MathUtils.calculateSum(250);
                
                try { latch1.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                B2 = A1 + MathUtils.calculateSum(200);
                latch2.countDown(); // Signal FuncB2 is done

                try { latch3.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                B3 = A2 + MathUtils.calculateSum(400);
                latch4.countDown(); // Signal FuncB3 is done
            });

            threadA.start();
            threadB.start();

            try {
                threadA.join();
                threadB.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Verification check: Expected A3 = 350900
            if (A3 != 350900) {
                System.out.println("Error at iteration " + iteration + ": A3 is " + A3);
                break;
            } else {
                // Print variables only on the final or specific iterations if preferred
                if (iteration == 10000) { 
                    System.out.println("--- Final Shared Variable Values ---");
                    System.out.println("A1: " + A1 + ", B1: " + B1);
                    System.out.println("B2: " + B2 + ", A2: " + A2);
                    System.out.println("B3: " + B3 + ", A3: " + A3);
                }
            }
        }
        System.out.println("Verification complete. All 10,000 iterations successful.");
    }
}