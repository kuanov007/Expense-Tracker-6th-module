package service;

import constant.Tools;
import entity.Category;
import entity.Expense;
import entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DBHelper {

    public Optional<User> login(String email, String password) throws SQLException {
        String checkSql = "select * from users where email = ? and password = ?";
        User user = new User();
        try (PreparedStatement preparedStatement = DBConnection.getPreparedStatement(checkSql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            user.setId(resultSet.getInt("id"));
            user.setName(resultSet.getString("name"));
            user.setEmail(email);
            user.setPassword(password);
        }
        return Optional.of(user);
    }

    public Optional<User> register(String name, String email, String password) throws SQLException {
        String checkSql = "select * from users where email = ?";
        try (PreparedStatement preparedStatement = DBConnection.getPreparedStatement(checkSql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.empty();
            }
        }

        String insertSql = "insert into users (name, email, password) values (?, ?, ?)";
        try (PreparedStatement preparedStatement = DBConnection.getPreparedStatement(insertSql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        }

        String getSql = "select * from users where email = ?";
        User user = new User();
        try (PreparedStatement preparedStatement = DBConnection.getPreparedStatement(getSql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(email);
                user.setPassword(password);
            }
        }
        return Optional.of(user);
    }

    public Optional<Category> checkCategory(int user_Id, String name) throws SQLException {
        String checkSql = "select * from category where name = ? and user_id = ?";
        try (PreparedStatement statement = DBConnection.getPreparedStatement(checkSql);) {
            statement.setString(1, name);
            statement.setInt(2, user_Id);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }

            Category category = new Category();
            category.setId(resultSet.getInt("id"));
            category.setUserId(resultSet.getInt("user_id"));
            category.setName(resultSet.getString("name"));

            return Optional.of(category);
        }
    }

    public void addCategory(Category category) throws SQLException {
        String insertQuery = "insert into category (name, user_id) values (?, ?)";
        try (PreparedStatement ps = DBConnection.getPreparedStatement(insertQuery)) {
            ps.setString(1, category.getName());
            ps.setInt(2, category.getUserId());
            ps.executeUpdate();
        }
    }

    public List<Category> getAllCategories(int userId) throws SQLException {
        List<Category> categories = new ArrayList<>();
        String selectQuery = "select * from category where  user_id = ? order by name";
        try (PreparedStatement ps = DBConnection.getPreparedStatement(selectQuery)) {
            ps.setInt(1, userId);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                categories.add(getCategoryFromRs(resultSet));
            }
        }
        return categories;
    }


    private Category getCategoryFromRs(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setUserId(rs.getInt("user_id"));
        category.setName(rs.getString("name"));
        return category;
    }

    public Optional<Category> getCategoryById(int userId, int categoryId) throws SQLException {
        String selectQuery = "select * from category where id = ? and user_id = ?";
        try (PreparedStatement ps = DBConnection.getPreparedStatement(selectQuery)) {
            ps.setInt(1, categoryId);
            ps.setInt(2, userId);
            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            return Optional.of(getCategoryFromRs(resultSet));
        }
    }

    public void addExpense(Expense expense) throws SQLException {
        String insertQuery = "insert into expense (category_id, name,  amount) values (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getPreparedStatement(insertQuery)) {
            ps.setInt(1, expense.getCategoryId());
            ps.setString(2, expense.getName());
            ps.setDouble(3, expense.getAmount());
            ps.executeUpdate();
        }
    }

    public List<Expense> getExpensesByCategoryID(int categoryId) throws SQLException {
        String selectQuery = "select * from expense where category_id = ? order by amount";
        List<Expense> expenses = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getPreparedStatement(selectQuery)) {
            ps.setInt(1, categoryId);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                expenses.add(getExpenseFromRs(resultSet));
            }
        }
        return expenses;
    }

    private static Expense getExpenseFromRs(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setCategoryId(rs.getInt("category_id"));
        expense.setName(rs.getString("name"));
        expense.setAmount(rs.getDouble("amount"));
        expense.setDate(rs.getDate("created_at"));
        return expense;
    }

    public List<String> getExpensesOfCategories(int userId) throws SQLException {
        String selectQuery = "select category_id, SUM(amount) as total_price from expense where category_id IN (select category_id from category where user_id = ?)  group by category_id";
        List<String> expensesStr = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getPreparedStatement(selectQuery)) {
            ps.setInt(1, userId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int categoryId = resultSet.getInt(1);
                String categorySUM = resultSet.getString(2);
                Category category = getCategoryById(userId, categoryId).get();
                String name = category.getName();

                expensesStr.add(name + " " + categorySUM);
            }
        }

        return expensesStr;
    }

    public Double overallPrice(int userId) throws SQLException {
        String selectQuery = "select SUM(amount) from expense where category_id IN (select category_id from category where user_id = ?)";
        try (PreparedStatement ps = DBConnection.getPreparedStatement(selectQuery)) {
            ps.setInt(1, userId);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        }
        return 0.0;
    }
}
