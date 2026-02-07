# Bank Account Management System

A simple Java Swing application for employees to perform CRUD operations on customer accounts. This application allows you to create new accounts, fetch existing details, update customer information, and delete accounts, all while maintaining a transaction history.

## Features

-   **Create Account**: Add new bank accounts with details like Name, DOB, Phone, and Initial Balance.
-   **Read Account**: Fetch account details using an Account Number.
-   **Update Account**: Modify customer details and balance.
-   **Delete Account**: Remove closed or erroneous accounts.
-   **Transaction History**: View a log of account creation and updates.

## Prerequisites

-   **Java Development Kit (JDK)**: Ensure Java is installed and configured.
-   **MySQL Server**: A running MySQL instance.
-   **MySQL JDBC Driver**: The Connector/J JAR file (e.g., `mysql-connector-j-8.x.x.jar`).

## Database Setup

Since the `database_setup.sql` script is not included, please run the following SQL commands in your MySQL Workbench or command line interface to set up the database:

```sql
CREATE DATABASE IF NOT EXISTS bank_db;
USE bank_db;

CREATE TABLE IF NOT EXISTS accounts (
    account_number VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    phone_number VARCHAR(15),
    balance DOUBLE DEFAULT 0.0,
    transaction_history TEXT
);
```

## Configuration

1.  Open `src/com/BankingApp/BankApp.java`.
2.  Locate the database connection constants:
    ```java
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password";
    ```
3.  Update generic placeholders with your actual MySQL username and password.

## How to Compile and Run

1.  Open a terminal or command prompt.
2.  Navigate to the `src` directory of the project.
3.  Compile the application (ensure the MySQL connector JAR is in your classpath):
    ```bash
    # Linux/macOS
    javac -cp ".:/path/to/mysql-connector-j-8.x.x.jar" com/BankingApp/BankApp.java

    # Windows
    javac -cp ".;path\to\mysql-connector-j-8.x.x.jar" com\BankingApp\BankApp.java
    ```
4.  Run the application:
    ```bash
    # Linux/macOS
    java -cp ".:/path/to/mysql-connector-j-8.x.x.jar" com.BankingApp.BankApp

    # Windows
    java -cp ".;path\to\mysql-connector-j-8.x.x.jar" com.BankingApp.BankApp
    ```

*Note: If you are using an IDE like Eclipse or IntelliJ IDEA, simply import the project, add the MySQL connector JAR to the project's build path/libraries, and run `BankApp.java`.*
