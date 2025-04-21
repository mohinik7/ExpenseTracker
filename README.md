# Expense Tracker Application

A comprehensive financial management system designed to help users track expenses, manage accounts, set budgets, and gain insights into their spending habits.

## Features

### User Management

- Secure registration and login system


### Account Management

- Create and manage multiple accounts (checking, savings, credit cards, etc.)
- Track balances across all accounts


### Category Management

- Create custom expense and income categories
- Organize transactions by category for better insights
- Modify or delete categories as needed

### Budget Management

- Set monthly budgets for different spending categories

### Transaction Recording

- Log income and expense transactions
- Categorize transactions for better organization
- Add notes and details to transactions


### Screenshots
![Screenshot (199)](https://github.com/user-attachments/assets/0ffc413d-e0e2-4d34-b1d9-f6830c70bc2a)
![Screenshot (198)](https://github.com/user-attachments/assets/322716d5-c638-4d09-9000-e41a1322bfe0)
![Screenshot (193)](https://github.com/user-attachments/assets/894fcca8-014c-4624-9bb3-c8160cf314c9)
![Screenshot (194)](https://github.com/user-attachments/assets/5b4d8653-de41-4ee4-8df8-98e6ae9ae583)
![Screenshot (195)](https://github.com/user-attachments/assets/efc45538-9a49-4281-922e-0e3ff1a79a5f)
![Screenshot (196)](https://github.com/user-attachments/assets/fa77c6e6-52b9-4aea-85e4-7470bfb81693)
![Screenshot (197)](https://github.com/user-attachments/assets/3a15886c-73b5-4274-b294-00e2d9268d0c)







## Technical Details

### Technology Stack

- **Frontend**: Java Swing for desktop UI
- **Backend**: Java with JDBC
- **Database**: MySQL
- **Architecture**: DAO pattern for data access

### Key Improvements

- Transaction support for data integrity
- Input validation to prevent data errors
- Proper error handling
- High-contrast UI for better accessibility
- Referential integrity across all database operations

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- MySQL 8.0 or higher
- Eclipse IDE (recommended)

### Installation

1. Clone this repository
2. Import the project into Eclipse
3. Set up the MySQL database using the SQL scripts provided
4. Configure the database connection in `DatabaseConnection.java`
5. Run the application from `LoginFrame.java`

### Database Setup

The application requires a MySQL database with the following tables:

- users (user_id, username, email, password)
- accounts (account_id, user_id, account_name, initial_amount)
- categories (category_id, user_id, category_name)
- records (record_id, category_id, account_id, amount, notes, date, time)
- budgets (budget_id, user_id, category_id, amount, start_date, end_date)

## Future Enhancements

- Data visualization with charts and graphs
- Export functionality for reports (PDF, Excel)
- Mobile application with cloud synchronization
- Multi-currency support
- Recurring transaction setup
