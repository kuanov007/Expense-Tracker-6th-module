package constant;

import service.DBHelper;

import java.util.Scanner;

public interface Tools {
    Scanner intScanner = new Scanner(System.in);
    Scanner strScanner = new Scanner(System.in);
    Scanner doubleScanner = new Scanner(System.in);
    DBHelper dbHelper = new DBHelper();
}
