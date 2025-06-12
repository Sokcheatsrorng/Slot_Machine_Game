import java.util.*;

// Enum to represent different slot symbols
enum Symbol {
    CHERRY("🍒", 2),
    LEMON("🍋", 3),
    ORANGE("🍊", 4),
    BELL("🔔", 5),
    BAR("⭐", 10),
    SEVEN("7️⃣", 20);

    private final String display;
    private final int multiplier;

    Symbol(String display, int multiplier) {
        this.display = display;
        this.multiplier = multiplier;
    }

    public String getDisplay() {
        return display;
    }

    public int getMultiplier() {
        return multiplier;
    }
}

// Player class to manage credits and bets
class Player {
    private String name;
    private int credits;
    private int currentBet;

    public Player(String name, int initialCredits) {
        this.name = name;
        this.credits = initialCredits;
        this.currentBet = 0;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public boolean placeBet(int amount) {
        if (amount > 0 && amount <= credits) {
            currentBet = amount;
            credits -= amount;
            return true;
        }
        return false;
    }

    public void addWinnings(int amount) {
        credits += amount;
    }

    public boolean hasCredits() {
        return credits > 0;
    }

    public void displayStats() {
        System.out.println("Player: " + name + " | Credits: " + credits);
    }
}

// WinLine class to represent different winning combinations
class WinLine {
    private int[] positions;
    private String name;

    public WinLine(String name, int[] positions) {
        this.name = name;
        this.positions = positions;
    }

    public int[] getPositions() {
        return positions;
    }

    public String getName() {
        return name;
    }
}

// Main SlotMachine class
class SlotMachine {
    private static final int NUM_REELS = 3;
    private static final int REEL_SIZE = 50; // Each reel has 50 symbols
    private Symbol[][] reels;
    private Symbol[] currentSpin;
    private WinLine[] winLines;
    private Random random;

    public SlotMachine() {
        random = new Random();
        initializeReels();
        initializeWinLines();
        currentSpin = new Symbol[NUM_REELS];
    }

    private void initializeReels() {
        reels = new Symbol[NUM_REELS][REEL_SIZE];
        Symbol[] symbols = Symbol.values();

        // Fill each reel with symbols (higher value symbols appear less frequently)
        for (int reel = 0; reel < NUM_REELS; reel++) {
            int index = 0;

            // CHERRY appears 15 times (most common)
            for (int i = 0; i < 15; i++) {
                reels[reel][index++] = Symbol.CHERRY;
            }

            // LEMON appears 12 times
            for (int i = 0; i < 12; i++) {
                reels[reel][index++] = Symbol.LEMON;
            }

            // ORANGE appears 10 times
            for (int i = 0; i < 10; i++) {
                reels[reel][index++] = Symbol.ORANGE;
            }

            // BELL appears 8 times
            for (int i = 0; i < 8; i++) {
                reels[reel][index++] = Symbol.BELL;
            }

            // BAR appears 4 times
            for (int i = 0; i < 4; i++) {
                reels[reel][index++] = Symbol.BAR;
            }

            // SEVEN appears 1 time (rarest)
            reels[reel][index] = Symbol.SEVEN;
        }
    }

    private void initializeWinLines() {
        // Define win lines (in this simple 3-reel game, we only have horizontal line)
        winLines = new WinLine[] {
                new WinLine("Horizontal", new int[]{0, 1, 2})
        };
    }

    public void spin() {
        System.out.println("\n🎰 Spinning the reels... 🎰");

        // Simulate spinning animation
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
            System.out.println("\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Generate random positions for each reel
        for (int i = 0; i < NUM_REELS; i++) {
            int position = random.nextInt(REEL_SIZE);
            currentSpin[i] = reels[i][position];
        }
    }

    public void displayResults() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║              SLOT MACHINE             ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.printf("║      %s    %s    %s      ║%n",
                currentSpin[0].getDisplay(),
                currentSpin[1].getDisplay(),
                currentSpin[2].getDisplay());
        System.out.println("╚═══════════════════════════════════════╝");
    }

    public int checkWins(int bet) {
        int totalWinnings = 0;

        for (WinLine winLine : winLines) {
            int[] positions = winLine.getPositions();
            Symbol firstSymbol = currentSpin[positions[0]];
            boolean isWinningLine = true;

            // Check if all symbols in the line match
            for (int pos : positions) {
                if (currentSpin[pos] != firstSymbol) {
                    isWinningLine = false;
                    break;
                }
            }

            if (isWinningLine) {
                int lineWinnings = bet * firstSymbol.getMultiplier();
                totalWinnings += lineWinnings;
                System.out.println("🎉 WINNER! " + winLine.getName() + " line - " +
                        firstSymbol.getDisplay() + " x3");
                System.out.println("💰 Line winnings: " + lineWinnings + " credits");

                // Special jackpot for three 7s
                if (firstSymbol == Symbol.SEVEN) {
                    int jackpot = bet * 50;
                    totalWinnings += jackpot;
                    System.out.println("🏆 JACKPOT BONUS! +" + jackpot + " credits!");
                }
            }
        }

        if (totalWinnings == 0) {
            System.out.println("😞 No winning combinations. Better luck next time!");
        }

        return totalWinnings;
    }

    public Symbol[] getCurrentSpin() {
        return currentSpin.clone();
    }
}

// Main game class
public class SlotMachineGame {
    private SlotMachine slotMachine;
    private Player player;
    private Scanner scanner;

    public SlotMachineGame() {
        slotMachine = new SlotMachine();
        scanner = new Scanner(System.in);
    }

    public void startGame() {
        displayWelcome();
        createPlayer();
        gameLoop();
        endGame();
    }

    private void displayWelcome() {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    🎰 SLOT MACHINE GAME 🎰                    ║");
        System.out.println("║                        Welcome to Vegas!                     ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Symbols and Multipliers:                                     ║");
        System.out.println("║ 🍒 Cherry x2  🍋 Lemon x3  🍊 Orange x4                     ║");
        System.out.println("║ 🔔 Bell x5    ⭐ Bar x10   7️⃣ Seven x20 (+Jackpot!)         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }

    private void createPlayer() {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine();
        player = new Player(name, 100); // Start with 100 credits
        System.out.println("Welcome, " + name + "! You start with 100 credits.");
    }

    private void gameLoop() {
        while (player.hasCredits()) {
            System.out.println("\n" + "=".repeat(50));
            player.displayStats();

            if (!getBet()) {
                continue;
            }

            slotMachine.spin();
            slotMachine.displayResults();

            int winnings = slotMachine.checkWins(player.getCurrentBet());

            if (winnings > 0) {
                player.addWinnings(winnings);
                System.out.println("🎊 Total winnings: " + winnings + " credits!");
            }

            if (!playAgain()) {
                break;
            }
        }
    }

    private boolean getBet() {
        System.out.print("Enter your bet (1-" + player.getCredits() + ") or 0 to quit: ");

        try {
            int bet = scanner.nextInt();

            if (bet == 0) {
                return false;
            }

            if (player.placeBet(bet)) {
                System.out.println("Bet placed: " + bet + " credits");
                return true;
            } else {
                System.out.println("❌ Invalid bet amount. Please try again.");
                return false;
            }
        } catch (InputMismatchException e) {
            System.out.println("❌ Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
            return false;
        }
    }

    private boolean playAgain() {
        if (!player.hasCredits()) {
            System.out.println("\n💸 You're out of credits! Game Over!");
            return false;
        }

        System.out.print("\nPlay again? (y/n): ");
        scanner.nextLine(); // Consume newline
        String choice = scanner.nextLine().toLowerCase();
        return choice.equals("y") || choice.equals("yes");
    }

    private void endGame() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎰 Thanks for playing! 🎰");
        player.displayStats();

        if (player.getCredits() > 100) {
            System.out.println("🎉 Congratulations! You finished with a profit!");
        } else if (player.getCredits() == 100) {
            System.out.println("😊 You broke even! Not bad!");
        } else {
            System.out.println("😅 Better luck next time!");
        }

        System.out.println("Come back soon!");
        scanner.close();
    }

    public static void main(String[] args) {
        SlotMachineGame game = new SlotMachineGame();
        game.startGame();
    }
}