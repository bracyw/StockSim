## **How to Run Your Program from the JAR File**

## Please Unzip StockInfo (and the folder inside of it... please put the contents of the inner zip in the StockInfo Folder not in a new folder inside StockInfo), PortfolioInfo, and TestData before running any tests. DO THIS FIRST THEN PROCEED TO PRE-REQS

### **Prerequisites**

1. Ensure you have Java installed on your machine.
2. Place the `StockInfo` file in the same directory as your JAR file. The program searches
   for `StockInfo` in the user's current directory as the client-side database.
3. Place the `PortfolioInfo` file in the same directory as your JAR. Ensure any user added txt file
   that wants to add is imported into `PortfolioInfo`.

### **Running the Program**

1. Open a terminal or command prompt.
2. Navigate to the directory containing your JAR file, the `StockInfo` folder and
   the `PortfolioInfo` folder.
3. Run the JAR file using the following command:
## *Running the Jar*
Run the jar by either not adding a command or adding -text to the end
By not running a command you open the GUI implementation

## **After running the jar **

### **Create a portfolio and add three different stocks:**

create-portfolio costAapl

buy-date COST 15 2020-01-01

buy-date AAPL 15 2020-06-01

buy-date MSFT 15 2021-01-01

composition 2020-01-01

distribution 2020-01-01

total-value 2020-01-01

composition 2020-06-01

distribution 2020-06-01

total-value 2020-06-01

composition 2021-01-01

distribution 2021-01-01

total-value 2021-01-01

sell-date AAPL 5 2022-01-01

composition 2022-02-01

distribution 2022-02-01

total-value 2022-02-01

save

# The user is now able to fully quit the program and then to re-enter the program and load the previous portfolio recently created the user can type

set-portfolio costAapl

## View different plots

plot-portfolio 2020-01-01 2024-01-01

plot-portfolio 2020-01-01 2020-05-30

plot-portfolio 2020-01-01 2020-01-07

### **Data Availability**

* All stocks listed can be queried for values up to 2024-06-03.
* To update or download more current data, use the `download &lt;Stock ticker>` method, which calls
  the API to fetch the latest data. Note that there is a limited number of API calls available per
  day.

#### **Current Supported Stocks**

* **MSFT** - Microsoft Corporation
* **AAPL** - Apple Inc.
* **NVDA** - NVIDIA Corporation
* **GOOGL** - Alphabet Inc.
* **GOOG** - Alphabet Inc.
* **AMZN** - Amazon.com, Inc.
* **META** - Meta Platforms, Inc.
* **BRK-B** - Berkshire Hathaway Inc. New
* **LLY** - Eli Lilly and Company
* **AVGO** - Broadcom Inc.
* **JPM** - JP Morgan Chase & Co.
* **TSLA** - Tesla, Inc.
* **V** - Visa Inc.
* **WMT** - Walmart Inc.
* **XOM** - Exxon Mobil Corporation
* **UNH** - UnitedHealth Group Incorporated
* **MA** - Mastercard Incorporated
* **PG** - Procter & Gamble Company (The)
* **COST** - Costco Wholesale Corporation
* **JNJ** - Johnson & Johnson
* **HD** - Home Depot, Inc. (The)
* **ORCL** - Oracle Corporation
* **MRK** - Merck & Company, Inc.
* **BAC** - Bank of America Corporation
* **CVX** - Chevron Corporation
* **ABBV** - AbbVie Inc.
* **NFLX** - Netflix, Inc.
* **KO** - Coca-Cola Company (The)
* **AMD** - Advanced Micro Devices, Inc.
* **PEP** - Pepsico, Inc.
* **QCOM** - QUALCOMM Incorporated
* **CRM** - Salesforce, Inc.
* **TMO** - Thermo Fisher Scientific Inc
* **LIN** - Linde plc
* **WFC** - Wells Fargo & Company
* **TMUS** - T-Mobile US, Inc.
* **ADBE** - Adobe Inc.
* **DHR** - Danaher Corporation
* **DIS** - Walt Disney Company (The)
* **CSCO** - Cisco Systems, Inc.
* **MCD** - McDonald's Corporation
* **GE** - GE Aerospace
* **AMAT** - Applied Materials, Inc.
* **ABT** - Abbott Laboratories
* **TXN** - Texas Instruments Incorporated
* **ACN** - Accenture plc
* **VZ** - Verizon Communications Inc.
* **AXP** - American Express Company
* **CAT** - Caterpillar, Inc.