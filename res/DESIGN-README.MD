# **MVC Architecture for Stock Management Program Initial Overview**

Our program is designed around the basic MVC (Model-View-Controller) architecture. The primary goal
is to minimize reliance on API calls by utilizing a local database that updates as needed. This
design choice is reflected in the implementation of our **OODStocks** class, which acts as a central
controller of the database (i.e., the StockInfo file contents) and keeps track of data when the
program is running.

## **Model**

### **OODStocks Class**

* **Function**: Acts as a central controller of the database.
* **Purpose**: Keeps track of all stocks created, preventing duplicate stocks.
* **Data Handling**: Limits unnecessary API calls by using a pre-downloaded list of the top 25
  stocks in the S&P500.

### **SimpleStock Class**

* **Function**: Reports its creation to the **OODStocks** class.
* **Purpose**: Ensures no duplicate stock information is read, thereby optimizing performance.

### **Portfolio Interface**

* **Functionality**: Allows users to buy/sell stocks, get the total value of their portfolio on a
  given date, and see all their investments.

### **StockModel Interface**

* **Purpose**: Offers all the functionality required for the controller to interact with the model.
* **Design**: The controller interacts with this single interface/class in the model.

## **Controller**

### **Controller Interface**

* **Function**: Reads inputs (in the form of an appendable), processes those inputs, and calls the
  appropriate methods in the **StockModel**.
* **Design**: Passes information from the model to the **StockView**.

## **View**

### **StockView Interface**

* **Purpose**: Designed to allow different types of view implementations in the future.
* **Implementation**: The **SimpleStockView** displays information formatted for the terminal (i.e.,
  System.out).

## **User Control**

* **Data Updates**: Users have control over when stock data is updated. The program relies on
  downloaded stock data.
* **Real-time Data**: Although the current implementation uses downloaded data, the **download()**
  method in the **StockModel** allows for future implementations that require real-time data.

## **Summary**

Our design adheres to a strict MVC implementation, maintaining strict connections from the
controller to the model and the controller to the view. The primary goal is to minimize reliance on
the API and focus on using downloaded files.


---

# **New Additions and Changes for Stock Management Program Overview - Assignment 5**

## **Additions and Changes**

### **"Better" Controller Class**

* **Purpose**: The better controller class simply extended the original functionality of the
  previous controller, and while adding the ability to execute new types of commands, with changing
  the controller interface.

### **Controller Case Statements (ABSTRACTED)**

* **Change**: Abstracted the functionality inside each case statement for better parsing of new
  commands, in both the old controller and new controller, as the new controller could then override
  shared command usage that needed to change.

### **SimplePortfolio toString() Components (ABSTRACTED)**

* **Change**: Abstracted the creation of columns to avoid code reuse in the new composition and
  distribution functions, required (our toString used to do both in one table).

### **FormatingUtils (ADDITION)**

* **Purpose**: The formating utils class was made so that we could abstract common functionality
  between classes in the model (specifcially, creating tables for both our old string Function (see
  above) and for plotting the bar graph that was required).

### **Sell Method**

* **Change**: Ensures stocks are removed from investments if their value hits zero (our first build
  didn't do this which made storing BetterPortfolios more complicated)

---

## **Implementation Choices for Buy, Sell, and Re-balance**

### **Command based Date Storage in BetterPortfolio**

* **Purpose**:
    * Easier to store data in a text file.
    * Easier for readability and making text files to upload.
    * The MAIN REASON for the string storage was to be able to more simply calculate the value of
      re-balance, at anytime, regardless of buying in the past (the exact implementation being
      clearer from javaDocs on BetterPortfolio and involved methods) .
    * We favored this approach in contrast to storing the SimplePortfolios as snapshots for given
      dates, as buy/sell commands in the past would mean updating SimplePortfolios stored in the
      future.
    * With our implementation, we simply store commands and compute those commands when required,
      executing them on a SimplePortfolio

### **Buy Method**

* **Function**: Adds a string to the information.

### **Sell Method**

* **Function**: Adds a string, checks if any stock goes below zero (buy calculating up to that date,
  using getPortfolio() method in BetterPortfolioImpl), and throws an error if it does.

### **Re-balance Method**

* **Function**: Adds a string to the information.
* **Logic**: Calls buy based on the given amount, calculates buy, sell, and re-balance in
  chronological order.

---

## **Balancing**

* **Feature**: Allows users to bulk buy a certain amount of stocks with the balance command.
* **Restriction**: Can only be applied to stocks in the current portfolio to avoid accidental
  purchases.

---

### **SimplePortfolio getAmount**

* **Feature**: Added a getter that returns a shallow copy of its stored investments (using a Map).

### **Note on Helpers for BetterPortfolioImpl**

* **Change**: Return Portfolios instead of BetterPortfolios due to the nature of computing values on
  a given date.

---

## **Additional Changes and Explanations**

### **reBalance String Return**

* **Purpose**: Allows for more versatile messages for future implementations.

### **Added getStockOrMakeNew to OODStocks**

* **Reason**: made a new method in the OODStocks interface to abstract behavior we often found when
  implementing new methods.

### **Added fetchClosestPrice Method to Stock**

* **Purpose**: Avoids error messages while maintaining getPrice functionality, something we needed
  more as we expanded functionality.

### **totalValue Method Change**

* **Change**: Now fetches the most recent date price instead of throwing an error if the value is
  given on the weekend.

### **Design Choice - Viewing all made portfolios**

* **Reason**: we began with the feeling that it wouldn't make sense for a user to have access to all
  the portfolio names in the program as there is no other type of security other than knowing a
  portfolio name. So, we have decided not add a feature to view all portfolio's until we can
  implement passwords.

New Changes to the original command line capability
  list-all-portfolios in BetterStockModel and implementations of 
  Added the ability to view all made portfolio names 


How the gui view Connects to the model:
 In order to properly communicate between the view and the controller, we decided to build off
the idea of our initial controller design. It takes in the Readable where it get's input from.
In order to be able to add to that readable from the view (where the input now comes from) we 
created a custom extension of the InputStream class. 

We used the initial prompt message command in the view, to ensure the controller does not continue looking 
for inputs until we add to the custom input stream (which is past into the view). We made command
utils for formatting commands in the fashion our old controller took them. 

While this set up was slightly complicated, it allows for any type of view that takes inputs, to be 
able to use the custom input stream and command utils to seamlessly use the old controller without
having to change any of its methods.

The one drawback to this approach is that we have to ensure that we collect the correct order of 
inputs from the user. This is not saying that errors on the model side or unsupported... they are.
Only errors that originally came from faulty user input are not supported because the order of 
arguments is now controlled by the view.


Minor Change to the Old Controller:
the above being said we made one minor change in the old control() method... simply create
a new Scanner  if the current Scanner has run out of inputs 
(this allows for the custom reader input to work). This was two lines of code and didn't change
original functionality just made the controller more versatile.


Decision:
  Button presses are caught in the view, if they aren't possible (in relation )



