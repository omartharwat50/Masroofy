
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {

    private StackPane contentArea;

    // Repositories
    private CycleRepository        cycleRepo;
    private GoalRepository         goalRepo;
    private TransactionRepository  transactionRepo;
    private CategoryRepository     categoryRepo;

    // Services
    private NotificationService    notificationService;
    private CycleService           cycleService;
    private GoalService            goalService;
    private TransactionService     transactionService;
    private ReportService          reportService;

    // Controllers
    private DashboardController    dashboardController;
    private CycleController        cycleController;
    private GoalController         goalController;
    private TransactionController  transactionController;
    private ReportController       reportController;

    @Override
    public void start(Stage stage) {

        // 1. Init DB schema
        SchemaInitializer.initialize();

        // 2. Repositories
        cycleRepo       = new CycleRepositoryImpl();
        goalRepo        = new GoalRepositoryImpl();
        transactionRepo = new TransactionRepositoryImpl();
        categoryRepo    = new CategoryRepositoryImpl();

        // 3. Services (order matters — NotificationService has no deps)
        notificationService = new NotificationService();
        cycleService        = new CycleService(cycleRepo);
        goalService         = new GoalService(goalRepo);
        transactionService  = new TransactionService(transactionRepo, categoryRepo, cycleRepo, notificationService);
        reportService       = new ReportService(transactionService);

        // 4. Controllers
        dashboardController   = new DashboardController(cycleService, transactionService, goalService, notificationService);
        cycleController       = new CycleController(cycleService);
        goalController        = new GoalController(goalService);
        transactionController = new TransactionController(transactionService, categoryRepo);
        reportController      = new ReportController(reportService, cycleService, transactionService);

        // 5. Layout
        contentArea = new StackPane();
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + Theme.BG_DEEP + ";");

        Sidebar sidebar = new Sidebar(this::navigate, () -> {
            DatabaseManager.shutdown();
            Platform.exit();
        });

        root.setLeft(sidebar);
        root.setCenter(contentArea);

        // 6. Default screen
        navigate("dashboard");

        Scene scene = new Scene(root, 1280, 780);
        stage.setTitle("BudgetFlow – Personal Finance");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    private void navigate(String page) {
        contentArea.getChildren().clear();
        switch (page) {
            case "dashboard"    -> contentArea.getChildren().add(new DashboardScreen(dashboardController));
            case "transactions" -> contentArea.getChildren().add(new TransactionsScreen(transactionController));
            case "cycle"        -> contentArea.getChildren().add(new CycleScreen(cycleController));
            case "goals"        -> contentArea.getChildren().add(new GoalsScreen(goalController));
            default             -> contentArea.getChildren().add(new DashboardScreen(dashboardController));
        }
    }

    @Override
    public void stop() {
        DatabaseManager.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}