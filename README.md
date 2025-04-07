# Budget Tracker Application

A comprehensive financial management system designed to help users track expenses, manage accounts, set budgets, and gain insights into their spending habits.

## Features

### User Management

- Secure registration and login system
- Personalized dashboard for each user

### Account Management

- Create and manage multiple accounts (checking, savings, credit cards, etc.)
- Track balances across all accounts
- View transaction history for each account

### Category Management

- Create custom expense and income categories
- Organize transactions by category for better insights
- Modify or delete categories as needed

### Budget Management

- Set monthly budgets for different spending categories
- Track budget utilization with visual indicators
- Receive alerts when approaching budget limits

### Transaction Recording

- Log income and expense transactions
- Categorize transactions for better organization
- Add notes and details to transactions
- Filter transactions by date, category, or account

### Reporting and Analytics

- View spending patterns by category
- Track income vs. expenses over time
- Calculate total balance across all accounts

## Technical Details

### Technology Stack

- **Frontend**: Java Swing for desktop UI
- **Backend**: Java with JDBC
- **Database**: MySQL
- **Architecture**: DAO pattern for data access

### Key Improvements

- Transaction support for data integrity
- Input validation to prevent data errors
- Proper error handling and user feedback
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
