import entity.User;
import service.AuthService;
import service.MainService;

import java.sql.SQLException;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws SQLException {
        while (true) {
            Optional<User> currentUser = AuthService.auth();
            if (currentUser.isEmpty()) {
                break;
            }
            MainService.run(currentUser.get());
        }
    }
}
