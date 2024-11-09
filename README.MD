# **README**

# **Features**

### **1. Examine Gain or Loss of a Stock Over a Specified Period**

* **Status**:Complete
* **Description**: This feature allows the user to specify a stock and a date range to examine the
  gain or loss over that period. The program calculates the difference between the closing prices on
  the start and end dates.

### **2. Calculate x-Day Moving Average of a Stock for a Specified Date**

* **Status**: Complete
* **Description**: This feature enables the user to calculate the x-day moving average for a
  specified stock on a given date. The moving average is computed based on the closing prices of the
  last x days.

### **3. Determine x-Day Crossovers for a Stock Over a Specified Date Range**

* **Status**: Complete
* **Description**: This feature allows the user to identify days within a specified date range where
  the closing price of a stock is greater than its x-day moving average, signaling a "buy"
  opportunity.

* ***x-Day default crossovers for a stock also implemented***

### **4. Create and Manage Portfolios**

* **Status**: Complete
* **Description**: Users can create one or more portfolios containing shares of various stocks. This
  program can calculate the total value of a portfolio on a specific date.

### **5. Managing plotting data - Stocks and Portfolios**

* **Status**: Complete
* **Description**: Users are able to plot data, it takes the max value and divides by 50 for length
* takes the amount days to figure out proper input amounts.

### **6. Better Portfolio**

* **Status**: Complete
* **Description**: Users are able to buy stocks at specific dates and times.
* can see composition / distribution / value of a portfolio

#### Multiple Portfolios

* **Status**: Complete
* **Description**: Users can create one or more portfolios containing shares of various stocks. This
  program can calculate the total value of a portfolio on a specific date.

## **User Interface / Interactivity**

* **Status**: Complete
* **Description**: The program includes an interactive text-based interface that guides the user
  through the different features. The interface handles user inputs and errors gracefully.

## **Design and Testing**

* **Status**: Complete
* **Description**: The program follows the model-view-controller design pattern. Each component is
  well-documented, and the code is structured for readability and maintainability. Comprehensive
  testing has been conducted to ensure functionality and reliability.

## **Stock Data Source**

* **Status**: Complete
* **Description**: The program integrates with the Alpha Vantage API to fetch historical stock data.
* and stores stock data previously used locally. We WANT users to be in control of their use of
  limited
* API calls, for that reason there is no auto downloading, users must explicitly use our download
  command available in the user interface.

# 7.Added GUI Display System



* **Status**: In Progress
* **Description**: A graphical user interface (GUI) has been added.

### **Buy and Sell Stocks**



* **Status**: Complete
* **Description**: Users can buy and sell stocks through the GUI, specifying the date of transactions.


### **View All Portfolios**



* **Status**: Complete
* **Description**: Users can view all their portfolios through the GUI.


# **Create, Logout, Set, and Save Portfolios**



* **Status**: Complete
* **Description**: Users can create new portfolios, log out, set portfolio, and save their portfolios through the GUI.


### **View Total, Distribution, and Composition of Portfolios on a Certain Date**



* **Status**: Complete
* **Description**: Users can view the total value, distribution, and composition of their portfolios on a specific date.
