package service;

import constant.Tools;
import entity.Category;
import entity.Expense;
import entity.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MainService {
    private static User currentUser = null;

    public static void run(User user) throws SQLException {
        currentUser = user;
        mainWhile:
        while (true) {
            System.out.print("""
                    [Main menu]
                    1. Add category
                    2. Show categories
                    3. Add expense
                    4. Show expenses by category
                    5. Show overall price
                    
                    0. Exit
                    """);
            switch (Tools.intScanner.nextInt()) {
                case 1 -> {
                    addCategory();
                }
                case 2 -> {
                    showCategories();
                }
                case 3 -> {
                    addExpense();
                }
                case 4 -> {
                    showExpensesByCategory();
                }
                case 5 -> {
                    showOverallPrice();
                }
                case 0 -> {
                    break mainWhile;
                }
                default -> {
                    System.err.println("Invalid input!");
                }
            }

        }
    }

    private static void addCategory() throws SQLException {
        System.out.print("Enter category name: ");
        String categoryName = Tools.strScanner.nextLine();
        Optional<Category> optionalCategory = Tools.dbHelper.checkCategory(currentUser.getId(), categoryName);
        if (optionalCategory.isPresent()) {
            System.out.println(categoryName + " already exists!");
            return;
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setUserId(currentUser.getId());
        Tools.dbHelper.addCategory(category);
        System.out.println("Category added successfully!");
    }

    private static void showCategories() throws SQLException {
        List<Category> categories = Tools.dbHelper.getAllCategories(currentUser.getId());
        if (categories.isEmpty()) {
            System.out.println("No categories found!");
            return;
        }
        categories.forEach(System.out::println);
    }

    private static void addExpense() throws SQLException {
        List<Category> categories = Tools.dbHelper.getAllCategories(currentUser.getId());
        if (categories.isEmpty()) {
            System.out.println("No categories found!");
            return;
        }
        categories.forEach(System.out::println);
        System.out.print("Enter category id: ");
        int categoryId = Tools.intScanner.nextInt();
        Optional<Category> optionalCategory = Tools.dbHelper.getCategoryById(currentUser.getId(), categoryId);

        if (optionalCategory.isEmpty()) {
            System.out.println("Category with id " + categoryId + " not found!");
            return;
        }

        System.out.print("Enter expense reason: ");
        String expenseName = Tools.strScanner.nextLine();

        System.out.print("Enter expense amount: ");
        double expenseAmount = Tools.doubleScanner.nextDouble();

        Expense expense = new Expense();
        expense.setCategoryId(categoryId);
        expense.setName(expenseName);
        expense.setAmount(expenseAmount);

        Tools.dbHelper.addExpense(expense);
        System.out.println("Expense added successfully!");
    }

    private static void showExpensesByCategory() throws SQLException {
        while (true) {
            List<Category> categories = Tools.dbHelper.getAllCategories(currentUser.getId());
            if (categories.isEmpty()) {
                System.out.println("No categories found!");
                return;
            }
            categories.forEach(System.out::println);
            System.out.print("Enter category id (0 -> back): ");
            int categoryId = Tools.intScanner.nextInt();
            if (categoryId == 0) {
                break;
            }
            Optional<Category> optionalCategory = Tools.dbHelper.getCategoryById(currentUser.getId(), categoryId);
            if (optionalCategory.isEmpty()) {
                continue;
            }
            List<Expense> expenses = Tools.dbHelper.getExpensesByCategoryID(categoryId);
            if (expenses.isEmpty()) {
                System.out.println("No expenses found!");
                continue;
            }
            expenses.forEach(System.out::println);
        }
    }

    private static void showOverallPrice() throws SQLException {
        System.out.println("Overall price: " + Tools.dbHelper.overallPrice(currentUser.getId()));
        List<String> categoryAndOverallPrices = Tools.dbHelper.getExpensesOfCategories(currentUser.getId());
        if (categoryAndOverallPrices.isEmpty()) {
            System.out.println("No categories found!");
            return;
        }
        categoryAndOverallPrices.forEach(System.out::println);

    }
}
