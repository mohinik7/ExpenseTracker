package dbms_minip;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Scanner;

public class Budget_Tracker {

	public static void main(String[] args) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		// Instantiate DAO classes
		UsersDAO usersDAO = new UsersDAO();
		AccountsDAO accountsDAO = new AccountsDAO();
		CategoriesDAO categoriesDAO = new CategoriesDAO();
		BudgetsDAO budgetsDAO = new BudgetsDAO();
		RecordsDAO recordsDAO = new RecordsDAO();

		while (running) {
			// Display Menu
			System.out.println("---- Budget Tracker Menu ----");
			System.out.println("1. User Operations");
			System.out.println("2. Account Operations");
			System.out.println("3. Category Operations");
			System.out.println("4. Budget Operations");
			System.out.println("5. Record Operations");
			System.out.println("6. Exit");
			System.out.print("Choose an option: ");
			int choice = scanner.nextInt();

			switch (choice) {
			case 1:
				userOperations(scanner, usersDAO);
				break;
			case 2:
				accountOperations(scanner, accountsDAO);
				break;
			case 3:
				categoryOperations(scanner, categoriesDAO);
				break;
			case 4:
				budgetOperations(scanner, budgetsDAO);
				break;
			case 5:
				recordOperations(scanner, recordsDAO);
				break;
			case 6:
				running = false;
				System.out.println("Exiting application...");
				break;
			default:
				System.out.println("Invalid choice! Please try again.");
			}
		}

		scanner.close();
	}

	// User Operations Menu
	private static void userOperations(Scanner scanner, UsersDAO usersDAO) throws SQLException {
		System.out.println("--- User Operations ---");
		System.out.println("1. Add User");
		System.out.println("2. Get User by ID");
		System.out.println("3. Update User Details");
		System.out.println("4. Delete User");
		System.out.print("Choose an option: ");
		int choice = scanner.nextInt();

		switch (choice) {
		case 1:
			// Add User
			System.out.print("Enter Username: ");
			String username = scanner.next();
			System.out.print("Enter Email: ");
			String email = scanner.next();
			System.out.print("Enter Password: ");
			String password = scanner.next();
			usersDAO.addUser(username, email, password);
			System.out.println("User added successfully!");
			break;
		case 2:
			// Get User by ID
			System.out.print("Enter User ID: ");
			int userId = scanner.nextInt();
			usersDAO.getUserById(userId);
			break;
		case 3:
			// Update User Email
			System.out.print("Enter User ID: ");
			int updateUserId = scanner.nextInt();
			usersDAO.updateUser(updateUserId);
			break;
		case 4:
			// Delete User
			System.out.print("Enter User ID to delete: ");
			int deleteUserId = scanner.nextInt();
			usersDAO.deleteUser(deleteUserId);
			System.out.println("User deleted successfully!");
			break;
		default:
			System.out.println("Invalid choice!");
		}
	}

	// Account Operations Menu
	private static void accountOperations(Scanner scanner, AccountsDAO accountsDAO) throws SQLException {
		System.out.println("--- Account Operations ---");
		System.out.println("1. Add Account");
		System.out.println("2. Get Account by ID");
		System.out.println("3. Update Account Details");
		System.out.println("4. Delete Account");
		System.out.print("Choose an option: ");
		int choice = scanner.nextInt();

		switch (choice) {
		case 1:
			// Add Account
			System.out.print("Enter User ID: ");
			int userId = scanner.nextInt();
			System.out.print("Enter Account Name: ");
			String accountName = scanner.next();
			System.out.print("Enter Initial Amount: ");
			double initialAmount = scanner.nextDouble();
			accountsDAO.addAccount(userId, accountName, initialAmount);
			System.out.println("Account added successfully!");
			break;
		case 2:
			// Get Account by ID
			System.out.print("Enter Account ID: ");
			int accountId = scanner.nextInt();
			accountsDAO.getAccountById(accountId);
			break;
		case 3:
			// Update Account Name
			System.out.print("Enter Account ID: ");
			int updateAccountId = scanner.nextInt();
			accountsDAO.updateAccount(updateAccountId);
			break;
		case 4:
			// Delete Account
			System.out.print("Enter Account ID to delete: ");
			int deleteAccountId = scanner.nextInt();
			accountsDAO.deleteAccount(deleteAccountId);

			break;
		default:
			System.out.println("Invalid choice!");
		}
	}

	// Category Operations Menu
	private static void categoryOperations(Scanner scanner, CategoriesDAO categoriesDAO) throws SQLException {
		System.out.println("--- Category Operations ---");
		System.out.println("1. Add Category");
		System.out.println("2. Get Category by ID");
		System.out.println("3. Update Category Name");
		System.out.println("4. Delete Category");
		System.out.print("Choose an option: ");
		int choice = scanner.nextInt();

		switch (choice) {
		case 1:
			// Add Category
			System.out.print("Enter User ID: ");
			int userId = scanner.nextInt();
			System.out.print("Enter Category Name: ");
			String categoryName = scanner.next();
			categoriesDAO.addCategory(userId, categoryName);
			System.out.println("Category added successfully!");
			break;
		case 2:
			// Get Category by ID
			System.out.print("Enter Category ID: ");
			int categoryId = scanner.nextInt();
			categoriesDAO.getCategoryById(categoryId);
			break;
		case 3:
			// Update Category Name
			System.out.print("Enter Category ID: ");
			int updateCategoryId = scanner.nextInt();
			System.out.print("Enter New Category Name: ");
			String newCategoryName = scanner.next();
			categoriesDAO.updateCategoryName(updateCategoryId, newCategoryName);
			System.out.println("Category name updated successfully!");
			break;
		case 4:
			// Delete Category
			System.out.print("Enter Category ID to delete: ");
			int deleteCategoryId = scanner.nextInt();
			categoriesDAO.deleteCategory(deleteCategoryId);
			System.out.println("Category deleted successfully!");
			break;
		default:
			System.out.println("Invalid choice!");
		}
	}

	// Budget Operations Menu
	private static void budgetOperations(Scanner scanner, BudgetsDAO budgetsDAO) throws SQLException {
		System.out.println("--- Budget Operations ---");
		System.out.println("1. Add Budget");
		System.out.println("2. Get Budget by ID");
		System.out.println("3. Update Budget Amount");
		System.out.println("4. Delete Budget");
		System.out.print("Choose an option: ");
		int choice = scanner.nextInt();

		switch (choice) {
		case 1:
			// Add Budget
			System.out.print("Enter User ID: ");
			int userId = scanner.nextInt();
			System.out.print("Enter Category ID: ");
			int categoryId = scanner.nextInt();
			System.out.print("Enter Budget Amount: ");
			double budgetAmount = scanner.nextDouble();
			System.out.print("Enter Start Date (YYYY-MM-DD): ");
			String startDateStr = scanner.next();
			Date startDate = Date.valueOf(startDateStr);
			System.out.print("Enter End Date (YYYY-MM-DD): ");
			String endDateStr = scanner.next();
			Date endDate = Date.valueOf(endDateStr);
			budgetsDAO.addBudget(userId, categoryId, budgetAmount, startDate, endDate);
			System.out.println("Budget added successfully!");
			break;
		case 2:
			// Get Budget by ID
			System.out.print("Enter User ID: ");
			int user_Id = scanner.nextInt();
			System.out.print("Enter Category ID: ");
			int CategoryId = scanner.nextInt();
			budgetsDAO.getBudget(user_Id, CategoryId);
			break;
		case 3:
			// Update Budget Amount
			System.out.print("Enter User ID: ");
			int updateuserId = scanner.nextInt();
			System.out.print("Enter Category ID: ");
			int updatecategoryId = scanner.nextInt();
			System.out.print("Enter New Budget Amount: ");
			double newBudgetAmount = scanner.nextDouble();
			budgetsDAO.updateBudgetAmount(updateuserId, updatecategoryId, newBudgetAmount);
			System.out.println("Budget amount updated successfully!");
			break;
		case 4:
			// Delete Budget
			System.out.print("Enter User ID to delete: ");
			int deleteUserId = scanner.nextInt();
			System.out.print("Enter Category ID to delete: ");
			int deleteCategoryId = scanner.nextInt();
			budgetsDAO.deleteBudget(deleteUserId, deleteCategoryId);
			System.out.println("Budget deleted successfully!");
			break;
		default:
			System.out.println("Invalid choice!");
		}
	}

	// Record Operations Menu
	private static void recordOperations(Scanner scanner, RecordsDAO recordsDAO) throws SQLException {
		System.out.println("--- Record Operations ---");
		System.out.println("1. Add Record");
		System.out.println("2. Get Record by ID");
		System.out.println("3. Update Record Details");
		System.out.println("4. Delete Record");
		System.out.print("Choose an option: ");
		int choice = scanner.nextInt();

		switch (choice) {
		case 1:
			System.out.print("Enter Category ID: ");
			int categoryId = scanner.nextInt();
			System.out.print("Enter Account ID: ");
			int accountId = scanner.nextInt();
			System.out.print("Enter Amount: ");
			double amount = scanner.nextDouble();
			scanner.nextLine();
			System.out.print("Enter Notes: ");
			String notes = scanner.nextLine();
			recordsDAO.addRecord(categoryId, accountId, amount, notes);
			System.out.println("Record added successfully!");
			break;
		case 2:
			// Get Record by ID
			System.out.print("Enter Record ID: ");
			int recordId = scanner.nextInt();
			recordsDAO.getRecordById(recordId);
			break;
		case 3:
			// Update Record Amount
			System.out.print("Enter Record ID: ");
			int updateRecordId = scanner.nextInt();
			recordsDAO.updateRecord(updateRecordId);
			System.out.println("Record amount updated successfully!");
			break;
		case 4:
			// Delete Record
			System.out.print("Enter Record ID to delete: ");
			int deleteRecordId = scanner.nextInt();
			recordsDAO.deleteRecord(deleteRecordId);
			System.out.println("Record deleted successfully!");
			break;
		default:
			System.out.println("Invalid choice!");
		}
	}
}
