import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static final int MAX = 100;

    static String[] foodNames = new String[MAX];
    static int[] quantities = new int[MAX];
    static String[] locations = new String[MAX];
    static int foodCount = 0;

    static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid integer: ");
            sc.next();
        }
        int x = sc.nextInt();
        sc.nextLine();
        return x;
    }

    static void addFood() {
        if (foodCount == MAX) {
            System.out.println("Storage full.");
            return;
        }

        System.out.print("Enter food name: ");
        String name = sc.nextLine().trim().toLowerCase();

        System.out.print("Enter quantity in kg: ");
        int quantity = readInt();

        if (name.isEmpty() || quantity <= 0) {
            System.out.println("Invalid food name or quantity.");
            return;
        }

        System.out.print("Enter location: ");
        String location = sc.nextLine().trim();

        foodNames[foodCount] = name;
        quantities[foodCount] = quantity;
        locations[foodCount] = location;
        foodCount++;

        System.out.println("Food supply added successfully.");
    }

    static void displayFood() {
        if (foodCount == 0) {
            System.out.println("No food supplies available.");
            return;
        }

        System.out.println("\n--- AVAILABLE FOOD SUPPLIES ---");

        for (int i = 0; i < foodCount; i++) {
            System.out.println((i + 1) + ". " + foodNames[i]
                    + " | Quantity: " + quantities[i]
                    + " kg | Location: " + locations[i]);
        }
    }

    // Module 2: Naive String Matching
    static int naiveSearch(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        for (int i = 0; i <= n - m; i++) {
            int j = 0;

            while (j < m && text.charAt(i + j) == pattern.charAt(j))
                j++;

            if (j == m)
                return i;
        }

        return -1;
    }

    // Module 2: KMP
    static int[] buildLPS(String p) {
        int[] lps = new int[p.length()];
        int len = 0;

        for (int i = 1; i < p.length();) {
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }

        return lps;
    }

    static int kmpSearch(String text, String pattern) {
        if (pattern.isEmpty()) return 0;

        int[] lps = buildLPS(pattern);
        int i = 0, j = 0;

        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }

            if (j == pattern.length())
                return i - j;

            if (i < text.length()
                    && text.charAt(i) != pattern.charAt(j)) {
                if (j > 0)
                    j = lps[j - 1];
                else
                    i++;
            }
        }

        return -1;
    }

    // Module 2: Z-function
    static int[] buildZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        int left = 0, right = 0;

        for (int i = 1; i < n; i++) {
            if (i <= right)
                z[i] = Math.min(right - i + 1, z[i - left]);

            while (i + z[i] < n
                    && s.charAt(z[i]) == s.charAt(i + z[i]))
                z[i]++;

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }

        return z;
    }

    static int zSearch(String text, String pattern) {
        if (pattern.isEmpty()) return 0;

        char sep = '#';
        while (text.indexOf(sep) >= 0 || pattern.indexOf(sep) >= 0)
            sep++;

        String combined = pattern + sep + text;
        int[] z = buildZ(combined);

        for (int i = pattern.length() + 1; i < combined.length(); i++)
            if (z[i] >= pattern.length())
                return i - pattern.length() - 1;

        return -1;
    }

    // Module 2: Rabin-Karp
    static int rabinKarp(String text, String pattern) {
        int n = text.length(), m = pattern.length();

        if (m == 0) return 0;
        if (m > n) return -1;

        long base = 257;
        long mod = 1000000007L;
        long power = 1, ph = 0, th = 0;

        for (int i = 1; i < m; i++)
            power = power * base % mod;

        for (int i = 0; i < m; i++) {
            ph = (ph * base + pattern.charAt(i)) % mod;
            th = (th * base + text.charAt(i)) % mod;
        }

        for (int i = 0; i <= n - m; i++) {
            if (ph == th && text.regionMatches(i, pattern, 0, m))
                return i;

            if (i < n - m) {
                th = (th - text.charAt(i) * power % mod + mod) % mod;
                th = (th * base + text.charAt(i + m)) % mod;
            }
        }

        return -1;
    }

    static void searchFood() {
        if (foodCount == 0) {
            System.out.println("No food supplies available.");
            return;
        }

        System.out.print("Enter food name or keyword: ");
        String pattern = sc.nextLine().trim().toLowerCase();

        System.out.println("1. Naive");
        System.out.println("2. KMP");
        System.out.println("3. Z-function");
        System.out.println("4. Rabin-Karp");

        int choice = readInt();
        boolean found = false;

        for (int i = 0; i < foodCount; i++) {
            int index;

            switch (choice) {
                case 1:
                    index = naiveSearch(foodNames[i], pattern);
                    break;
                case 2:
                    index = kmpSearch(foodNames[i], pattern);
                    break;
                case 3:
                    index = zSearch(foodNames[i], pattern);
                    break;
                case 4:
                    index = rabinKarp(foodNames[i], pattern);
                    break;
                default:
                    System.out.println("Invalid algorithm.");
                    return;
            }

            if (index >= 0) {
                System.out.println(foodNames[i] + " | "
                        + quantities[i] + " kg | " + locations[i]);
                found = true;
            }
        }

        if (!found)
            System.out.println("Food not found.");
    }

    // Module 3: Wagner-Fischer Edit Distance
    static int editDistance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++)
            dp[i][0] = i;

        for (int j = 0; j <= m; j++)
            dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }

        return dp[n][m];
    }

    static void similarFoodSearch() {
        System.out.print("Enter food name: ");
        String query = sc.nextLine().trim().toLowerCase();

        boolean found = false;

        for (int i = 0; i < foodCount; i++) {
            int distance = editDistance(query, foodNames[i]);

            if (distance <= 2) {
                System.out.println(foodNames[i]
                        + " | Edit distance: " + distance);
                found = true;
            }
        }

        if (!found)
            System.out.println("No similar food names found.");
    }

    // Module 3: Display the DP table
    static void displayDPTable() {
        System.out.print("Enter first string: ");
        String a = sc.nextLine().toLowerCase();

        System.out.print("Enter second string: ");
        String b = sc.nextLine().toLowerCase();

        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
            }
        }

        System.out.println("Edit Distance DP Table:");

        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++)
                System.out.print(dp[i][j] + "\t");
            System.out.println();
        }

        System.out.println("Minimum Edit Distance: " + dp[n][m]);
    }

    // Module 3: Matrix Chain Multiplication
    static void matrixChain() {
        System.out.print("Number of matrices (2-15): ");
        int n = readInt();

        if (n < 2 || n > 15) {
            System.out.println("Invalid number of matrices.");
            return;
        }

        int[] p = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            System.out.print("Dimension p[" + i + "]: ");
            p[i] = readInt();

            if (p[i] <= 0) {
                System.out.println("Dimensions must be positive.");
                return;
            }
        }

        long[][] dp = new long[n + 1][n + 1];

        for (int len = 2; len <= n; len++) {
            for (int i = 1; i <= n - len + 1; i++) {
                int j = i + len - 1;
                dp[i][j] = Long.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j]
                            + (long) p[i - 1] * p[k] * p[j];

                    if (cost < dp[i][j])
                        dp[i][j] = cost;
                }
            }
        }

        System.out.println("Minimum multiplication cost: " + dp[1][n]);
    }

    // Module 3: Bitmask DP for a small distribution tour
    static void tsp() {
        System.out.print("Number of locations (2-12): ");
        int n = readInt();

        if (n < 2 || n > 12) {
            System.out.println("Choose 2 to 12 locations.");
            return;
        }

        int[][] cost = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    System.out.print("Cost from " + i + " to " + j + ": ");
                    cost[i][j] = readInt();

                    if (cost[i][j] < 0) {
                        System.out.println("Cost cannot be negative.");
                        return;
                    }
                }
            }
        }

        int states = 1 << n;
        long INF = Long.MAX_VALUE / 4;
        long[][] dp = new long[states][n];

        for (int mask = 0; mask < states; mask++)
            for (int j = 0; j < n; j++)
                dp[mask][j] = INF;

        dp[1][0] = 0;

        for (int mask = 1; mask < states; mask++) {
            if ((mask & 1) == 0) continue;

            for (int u = 0; u < n; u++) {
                if (dp[mask][u] == INF) continue;

                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0) {
                        int nextMask = mask | (1 << v);
                        long value = dp[mask][u] + cost[u][v];

                        if (value < dp[nextMask][v])
                            dp[nextMask][v] = value;
                    }
                }
            }
        }

        long answer = INF;

        for (int j = 1; j < n; j++)
            answer = Math.min(answer,
                    dp[states - 1][j] + cost[j][0]);

        System.out.println("Minimum tour cost: " + answer);
    }

    // Module 4: Dinic Maximum Flow
    static class Dinic {
        int n, edgeCount;
        int[] head, to, next, capacity, level, ptr, queue;

        Dinic(int n, int maxEdges) {
            this.n = n;
            head = new int[n];
            level = new int[n];
            ptr = new int[n];
            queue = new int[n];
            to = new int[maxEdges];
            next = new int[maxEdges];
            capacity = new int[maxEdges];

            for (int i = 0; i < n; i++)
                head[i] = -1;
        }

        void addEdge(int u, int v, int c) {
            to[edgeCount] = v;
            capacity[edgeCount] = c;
            next[edgeCount] = head[u];
            head[u] = edgeCount++;

            to[edgeCount] = u;
            capacity[edgeCount] = 0;
            next[edgeCount] = head[v];
            head[v] = edgeCount++;
        }

        boolean bfs(int s, int t) {
            for (int i = 0; i < n; i++)
                level[i] = -1;

            int front = 0, rear = 0;
            queue[rear++] = s;
            level[s] = 0;

            while (front < rear) {
                int u = queue[front++];

                for (int e = head[u]; e != -1; e = next[e]) {
                    int v = to[e];

                    if (capacity[e] > 0 && level[v] == -1) {
                        level[v] = level[u] + 1;
                        queue[rear++] = v;
                    }
                }
            }

            return level[t] != -1;
        }

        int dfs(int u, int t, int pushed) {
            if (pushed == 0) return 0;
            if (u == t) return pushed;

            for (int e = ptr[u]; e != -1; e = next[e]) {
                ptr[u] = e;
                int v = to[e];

                if (capacity[e] > 0 && level[v] == level[u] + 1) {
                    int flow = dfs(v, t,
                            Math.min(pushed, capacity[e]));

                    if (flow > 0) {
                        capacity[e] -= flow;
                        capacity[e ^ 1] += flow;
                        return flow;
                    }
                }

                ptr[u] = next[e];
            }

            return 0;
        }

        int maxFlow(int s, int t) {
            int flow = 0;

            while (bfs(s, t)) {
                for (int i = 0; i < n; i++)
                    ptr[i] = head[i];

                int pushed;

                while ((pushed = dfs(s, t, Integer.MAX_VALUE)) > 0)
                    flow += pushed;
            }

            return flow;
        }
    }

    static void flowDemo() {
        System.out.print("Vertices (source=0, sink=n-1): ");
        int n = readInt();

        if (n < 2 || n > 50) {
            System.out.println("Choose 2 to 50 vertices.");
            return;
        }

        System.out.print("Number of directed edges: ");
        int m = readInt();

        if (m < 1 || m > 500) {
            System.out.println("Choose 1 to 500 edges.");
            return;
        }

        Dinic d = new Dinic(n, 2 * m);

        for (int i = 0; i < m; i++) {
            System.out.print("Enter from, to, capacity: ");
            int u = readInt();
            int v = readInt();
            int c = readInt();

            if (u < 0 || u >= n || v < 0 || v >= n || c < 0) {
                System.out.println("Invalid edge.");
                return;
            }

            d.addEdge(u, v, c);
        }

        System.out.println("Maximum flow: " + d.maxFlow(0, n - 1));
    }

    // Module 5: Vertex Cover 2-Approximation
    static void vertexCover() {
        System.out.print("Number of vertices: ");
        int n = readInt();

        System.out.print("Number of edges: ");
        int m = readInt();

        if (n < 1 || n > 100 || m < 0 || m > 500) {
            System.out.println("Input out of range.");
            return;
        }

        int[] a = new int[m];
        int[] b = new int[m];
        boolean[] chosen = new boolean[n];
        boolean[] covered = new boolean[m];

        for (int i = 0; i < m; i++) {
            System.out.print("Enter edge endpoints: ");
            a[i] = readInt();
            b[i] = readInt();

            if (a[i] < 0 || a[i] >= n || b[i] < 0 || b[i] >= n) {
                System.out.println("Invalid endpoint.");
                return;
            }
        }

        for (int i = 0; i < m; i++) {
            if (!covered[i] && a[i] != b[i]) {
                int u = a[i], v = b[i];
                chosen[u] = true;
                chosen[v] = true;

                for (int j = 0; j < m; j++) {
                    if (a[j] == u || b[j] == u
                            || a[j] == v || b[j] == v)
                        covered[j] = true;
                }
            }
        }

        System.out.print("Approximate vertex cover: ");
        for (int i = 0; i < n; i++)
            if (chosen[i]) System.out.print(i + " ");

        System.out.println();
    }

    // Module 6: Miller-Rabin demonstration for modest positive values
    static long modPow(long a, long e, long mod) {
        long result = 1;
        a %= mod;

        while (e > 0) {
            if ((e & 1) == 1)
                result = result * a % mod;

            a = a * a % mod;
            e >>= 1;
        }

        return result;
    }

    static boolean millerRabin(long n) {
        if (n < 2) return false;

        int[] small = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

        for (int p : small) {
            if (n == p) return true;
            if (n % p == 0) return false;
        }

        long d = n - 1;
        int s = 0;

        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        int[] bases = {2, 3, 5, 7, 11};

        for (int base : bases) {
            if (base >= n) continue;

            long x = modPow(base, d, n);

            if (x == 1 || x == n - 1) continue;

            boolean passed = false;

            for (int r = 1; r < s; r++) {
                x = x * x % n;

                if (x == n - 1) {
                    passed = true;
                    break;
                }
            }

            if (!passed) return false;
        }

        return true;
    }

    static void primeTest() {
        System.out.print("Enter a positive integer: ");
        long n = sc.nextLong();
        sc.nextLine();

        System.out.println(n + (millerRabin(n)
                ? " is probably prime."
                : " is composite or less than 2."));
    }

    // Module 6: Reservoir Sampling
    static void reservoirSampling() {
        System.out.print("Number of items: ");
        int n = readInt();

        if (n < 1 || n > 10000) {
            System.out.println("Choose 1 to 10000.");
            return;
        }

        String selected = "";

        for (int i = 1; i <= n; i++) {
            System.out.print("Enter item " + i + ": ");
            String item = sc.nextLine();

            if (Math.random() < 1.0 / i)
                selected = item;
        }

        System.out.println("Randomly selected item: " + selected);
    }

    // Module 6: Randomized Quicksort
    static void quickSort(int[] a, int low, int high) {
        if (low >= high) return;

        int pivotIndex = low
                + (int) (Math.random() * (high - low + 1));

        int temp = a[pivotIndex];
        a[pivotIndex] = a[high];
        a[high] = temp;

        int pivot = a[high];
        int i = low;

        for (int j = low; j < high; j++) {
            if (a[j] <= pivot) {
                temp = a[i];
                a[i] = a[j];
                a[j] = temp;
                i++;
            }
        }

        temp = a[i];
        a[i] = a[high];
        a[high] = temp;

        quickSort(a, low, i - 1);
        quickSort(a, i + 1, high);
    }

    static void sortDemo() {
        System.out.print("Number of quantities: ");
        int n = readInt();

        if (n < 1 || n > 10000) {
            System.out.println("Choose 1 to 10000.");
            return;
        }

        int[] a = new int[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter quantity: ");
            a[i] = readInt();
        }

        quickSort(a, 0, n - 1);

        System.out.print("Sorted quantities: ");

        for (int x : a)
            System.out.print(x + " ");

        System.out.println();
    }

    // Module 6: Prefix Sum
    static void prefixSum() {
        System.out.print("Number of quantities: ");
        int n = readInt();

        if (n < 1 || n > 10000) {
            System.out.println("Choose 1 to 10000.");
            return;
        }

        long[] prefix = new long[n + 1];

        for (int i = 1; i <= n; i++) {
            System.out.print("Quantity " + i + ": ");
            prefix[i] = prefix[i - 1] + readInt();
        }

        System.out.print("Prefix sums: ");

        for (int i = 1; i <= n; i++)
            System.out.print(prefix[i] + " ");

        System.out.println();
    }

    // Food request: update inventory after successful allocation
    static void foodRequest() {
        System.out.print("Enter requested food name: ");
        String name = sc.nextLine().trim().toLowerCase();

        System.out.print("Required quantity in kg: ");
        int need = readInt();

        if (need <= 0) {
            System.out.println("Quantity must be positive.");
            return;
        }

        int total = 0;

        for (int i = 0; i < foodCount; i++)
            if (foodNames[i].equals(name))
                total += quantities[i];

        System.out.println("Total available: " + total + " kg");

        if (total < need) {
            System.out.println("Insufficient supply.");
            System.out.println("Additional quantity needed: "
                    + (need - total) + " kg");
            return;
        }

        int remaining = need;

        for (int i = 0; i < foodCount && remaining > 0; i++) {
            if (foodNames[i].equals(name)) {
                int allocated = Math.min(quantities[i], remaining);

                quantities[i] -= allocated;
                remaining -= allocated;

                System.out.println("Allocated " + allocated
                        + " kg from " + locations[i]);
            }
        }

        System.out.println("Request fulfilled successfully.");
    }

    static void testAlgorithms() {
        String text = "rice is available";
        String pattern = "rice";

        System.out.println("Naive result: "
                + naiveSearch(text, pattern));
        System.out.println("KMP result: "
                + kmpSearch(text, pattern));
        System.out.println("Z-function result: "
                + zSearch(text, pattern));
        System.out.println("Rabin-Karp result: "
                + rabinKarp(text, pattern));
        System.out.println("Edit distance (rce, rice): "
                + editDistance("rce", "rice"));
    }

    static void menu() {
        System.out.println("\n========== FOODCHAIN ==========");
        System.out.println("1. Add food supply");
        System.out.println("2. Display food supplies");
        System.out.println("3. Search food");
        System.out.println("4. Food request and allocation");
        System.out.println("5. Similar food search");
        System.out.println("6. Edit Distance DP table");
        System.out.println("7. Matrix Chain Multiplication");
        System.out.println("8. Distribution Tour using Bitmask DP");
        System.out.println("9. Maximum Flow using Dinic");
        System.out.println("10. Vertex Cover Approximation");
        System.out.println("11. Miller-Rabin Primality Test");
        System.out.println("12. Reservoir Sampling");
        System.out.println("13. Randomized Quicksort");
        System.out.println("14. Prefix Sum");
        System.out.println("15. Test String Algorithms");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    public static void main(String[] args) {
        int choice;

        do {
            menu();
            choice = readInt();

            switch (choice) {
                case 1:
                    addFood();
                    break;
                case 2:
                    displayFood();
                    break;
                case 3:
                    searchFood();
                    break;
                case 4:
                    foodRequest();
                    break;
                case 5:
                    similarFoodSearch();
                    break;
                case 6:
                    displayDPTable();
                    break;
                case 7:
                    matrixChain();
                    break;
                case 8:
                    tsp();
                    break;
                case 9:
                    flowDemo();
                    break;
                case 10:
                    vertexCover();
                    break;
                case 11:
                    primeTest();
                    break;
                case 12:
                    reservoirSampling();
                    break;
                case 13:
                    sortDemo();
                    break;
                case 14:
                    prefixSum();
                    break;
                case 15:
                    testAlgorithms();
                    break;
                case 0:
                    System.out.println("Thank you for using FoodChain.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);

        sc.close();
    }
}