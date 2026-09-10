# Quick Retail — Point of Sale (POS) & Inventory Management

**Quick Retail** is a modern, offline-first Android Point of Sale (POS) and Inventory Management application built with Kotlin and Jetpack Compose. Designed for small-to-medium retail businesses, supermarkets, grocery stores, pharmacies, and boutiques, Quick Retail provides an intuitive, high-performance checkout experience, comprehensive stock control, and detailed sales reporting with both PDF and Excel export capabilities.

---

## 🌟 Key Features

### 🛒 1. Point of Sale (POS) & Billing
- **Fast Cart Management**: Easily search and add products to the checkout cart, adjust quantities, or remove items with real-time total recalculation.
- **Integrated Barcode Scanner**: Instant camera-based barcode and QR scanning powered by ML Kit for rapid item lookup and scanning during checkout.
- **Customer Information**: Record customer name, phone number, and delivery/billing address directly on the bill.
- **Discounts & Taxes**: Apply custom discounts and automatic tax calculations configurable from settings.
- **Flexible Payments**: Support for multiple payment methods including Cash, Card, Mobile Banking/UPI, and Store Credit, with live calculation of change and balance.
- **Instant Stock Sync**: Finalizing a transaction automatically deducts purchased items from inventory stock levels.

### 📦 2. Product & Inventory Control
- **Complete Product Catalog**: Manage products with Name, SKU / Barcode, Category, Cost Price, Selling Price, and Stock Quantity.
- **Low Stock & Out of Stock Alerts**: Real-time visual alerts for items running low or out of stock.
- **Search & Filter**: Quickly find products by name, category, or barcode.
- **Stock Adjustments**: Update stock quantities anytime to reflect new inventory deliveries or returns.

### 🧾 3. Professional Receipt & Bill Generation
- **PDF Receipt Generation**: Generates clean, formatted 80mm/standard receipts ready for printing, sharing, or archiving.
- **Receipt Customization**:
  - Store Name, Address, and Contact Number
  - Customer Name, Phone Number, and Customer Address
  - Itemized table with quantity, unit price, and line totals
  - Subtotal, discounts, tax breakdown, grand total, paid amount, and change
  - Customizable receipt footer note (e.g., "Thank you for shopping with us!")

### 📊 4. Comprehensive Business Reports & Analytics
- **Diverse Report Types**:
  - **Time-based**: Daily, Weekly, Monthly, and Yearly Sales Reports
  - **Product Analysis**: Product Sales, Top Selling Products, Slow Moving Products, and Full Product List
  - **Inventory**: Stock Summary, Low Stock Alerts, and Inventory Valuation
  - **Financial**: Profit & Loss calculations and Sales History
  - **Customer**: Customer Purchase History & Summary
- **Multi-Format Export**:
  - **PDF Export**: Clean document layouts with headers and summary metrics.
  - **Microsoft Excel (.xls) Export**: Formatted spreadsheets featuring colored headers, structured tables, and columnized data ready for business accounting.

### ⚙️ 5. Store Settings & Preferences
- Configure store branding (Shop Name, Address, Phone Number).
- Select preferred currency symbol (e.g., $, €, £, ₹, ৳).
- Set default tax rates.
- Customize receipt footer messages.
- Dark mode and light mode visual themes.

---

## 🏗️ Architecture & Technology Stack

Quick Retail follows Android development best practices and modern reactive architecture:

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3) components.
- **Language**: 100% [Kotlin](https://kotlinlang.org/).
- **Architecture Pattern**: MVVM (Model-View-ViewModel) with unidirectional data flow (UDF).
- **Local Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room) for robust, ACID-compliant SQLite offline storage.
- **Asynchronous & Reactive**: Kotlin Coroutines & StateFlow for reactive, non-blocking UI state management.
- **Barcode & QR Scanning**: Google ML Kit Barcode Scanning & ZXing for high-speed camera scanning.
- **Document Generation**: Android Native `PdfDocument` and HTML/XML-based Excel workbook export.
- **Design & Layout**: Edge-to-edge layout support with dynamic window insets.

---

## 📁 Project Structure

```text
app/src/main/java/com/example/
├── data/
│   ├── AppDatabase.kt          # Room Database definition & migrations
│   ├── Product.kt              # Product entity schema
│   ├── RetailDao.kt            # Room DAO queries for products, sales, and analytics
│   ├── RetailRepository.kt     # Repository abstraction layer
│   ├── Sale.kt                 # Sale/Transaction entity schema
│   └── SaleItem.kt             # Individual line items per sale
├── ui/
│   ├── navigation/             # Navigation graph & type-safe screen destinations
│   ├── screens/
│   │   ├── DashboardScreen.kt  # KPI overview & quick action tiles
│   │   ├── PosScreen.kt        # Point of sale cart, customer details & checkout
│   │   ├── ProductScreen.kt    # Inventory listing, add/edit/delete product dialogs
│   │   ├── ReportsScreen.kt    # Report selection, preview, PDF & Excel export
│   │   ├── ScannerScreen.kt    # Camera barcode scanner preview & detector
│   │   └── SettingsScreen.kt   # Store information, tax, currency & theme settings
│   ├── theme/                  # Material 3 color schemes, typography & shapes
│   └── viewmodels/
│       └── RetailViewModel.kt  # Unified business logic, state flows & DB operations
└── utils/
    ├── ExcelUtils.kt           # Excel spreadsheet report builder (.xls)
    └── PdfUtils.kt             # PDF receipt & report document generator
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or higher
- Android SDK with `minSdk = 26` and `targetSdk = 35`
- An Android device or emulator running Android 8.0 (Oreo) or higher
- Camera permission (for barcode scanning)

### Installation & Build
1. Clone or download the repository to your local machine:
   ```bash
   git clone https://github.com/your-username/quick-retail.git
   ```
2. Open the project in **Android Studio**.
3. Allow Gradle to sync dependencies.
4. Connect an Android device with USB debugging enabled or start an Android Virtual Device (AVD).
5. Build and run the app:
   ```bash
   ./gradlew assembleDebug
   ```
   Or press **Run** (`Shift + F10`) in Android Studio.

---

## 🔒 Privacy & Offline Reliability

Quick Retail is built with an **offline-first** design:
- All business transactions, product databases, customer records, and sales history remain stored entirely on your device.
- No continuous internet connection is required to scan barcodes, make sales, or generate receipts.
- Fast, secure, and resilient for daily commercial operations.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
