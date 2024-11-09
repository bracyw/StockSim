# **README**

# **Features**

### **1. Examine Gain or Loss of a Stock Over a Specified Period**

* **Status**:Complete
* **Description**: This feature allows the user to specify a stock and a date range to examine the
  gain or loss over that period. The program calculates the difference between the closing prices on
  the start and end dates.

### **2. Calculate x-Day Moving Average of a Stock for a Specified Date**


* **Description**: This feature enables the user to calculate the x-day moving average for a
  specified stock on a given date. The moving average is computed based on the closing prices of the
  last x days.

### **3. Determine x-Day Crossovers for a Stock Over a Specified Date Range**


* **Description**: This feature allows the user to identify days within a specified date range where
  the closing price of a stock is greater than its x-day moving average, signaling a "buy"
  opportunity.

* ***x-Day default crossovers for a stock also implemented***

### **4. Create and Manage Portfolios**

* **Description**: Users can create one or more portfolios containing shares of various stocks. This
  program can calculate the total value of a portfolio on a specific date.

### **5. Managing plotting data - Stocks and Portfolios**


* **Description**: Users are able to plot data, it takes the max value and divides by 50 for length
* takes the amount days to figure out proper input amounts.

### **6. Better Portfolio**


* **Description**: Users are able to buy stocks at specific dates and times.
* can see composition / distribution / value of a portfolio

#### **7. Multiple Portfolios


* **Description**: Users can create one or more portfolios containing shares of various stocks. This
  program can calculate the total value of a portfolio on a specific date.


## **Stock Data Source**


* **Description**: The program integrates with the Alpha Vantage API to fetch historical stock data.
* and stores stock data previously used locally. We WANT users to be in control of their use of
  limited
* API calls, for that reason there is no auto downloading, users must explicitly use our download
  command available in the user interface.

# GUI Display System

* **Description**: A graphical user interface (GUI) has been added.

### **Buy and Sell Stocks**

* **Description**: Users can buy and sell stocks through the GUI, specifying the date of transactions.


### **View All Portfolios**

* **Description**: Users can view all their portfolios through the GUI.


# **Create, Logout, Set, and Save Portfolios**


* **Description**: Users can create new portfolios, log out, set portfolio, and save their portfolios through the GUI.


### **View Total, Distribution, and Composition of Portfolios on a Certain Date**

* **Description**: Users can view the total value, distribution, and composition of their portfolios on a specific date.
