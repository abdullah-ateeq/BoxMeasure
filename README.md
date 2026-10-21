# BoxMeasure 📦

**BoxMeasure** is a modern, intuitive, and feature-rich Android application designed to calculate box volume, surface area, space diagonals, edge lengths, and missing dimensions with extreme precision and instant unit conversions. Built with modern Android development best practices using **Jetpack Compose** and **Material 3**.

---

## ✨ Features

- **📦 Comprehensive Box Calculations**
  - **Volume ($V = L \times W \times H$)**
  - **Total Surface Area ($TSA = 2(LW + LH + WH)$)**
  - **Lateral Surface Area ($LSA = 2H(L + W)$)**
  - **Individual Face Areas** ($L \times W$, $L \times H$, $W \times H$)
  - **Space Diagonal** ($d = \sqrt{L^2 + W^2 + H^2}$)
  - **Base Perimeter** ($P = 2(L + W)$)
  - **Total Edge Length** ($E = 4(L + W + H)$)

- **🔍 Missing Dimension Solver**
  - Calculate missing **Length**, **Width**, or **Height** given the volume and remaining two dimensions.

- **🔄 Multi-Unit Support & Conversion**
  - **Length Units**: Millimeters (mm), Centimeters (cm), Meters (m), Kilometers (km), Inches (in), Feet (ft), Yards (yd).
  - **Area Units**: $\text{mm}^2$, $\text{cm}^2$, $\text{m}^2$, $\text{km}^2$, $\text{in}^2$, $\text{ft}^2$, $\text{yd}^2$.
  - **Volume Units**: $\text{mm}^3$, $\text{cm}^3$, $\text{m}^3$, $\text{km}^3$, $\text{in}^3$, $\text{ft}^3$, $\text{yd}^3$.
  - Seamless independent unit selections for inputs and result outputs.

- **📐 Visual 3D Box Diagram**
  - Dynamic 3D interactive preview of box dimensions with live labels and proportional scaling.

- **📝 Step-by-Step Solution Breakdown**
  - Detailed formula breakdowns and step-by-step math steps showing exact intermediate calculations.

- **📜 History & Saved Calculations**
  - Save calculation snapshots with timestamps.
  - Search, filter, copy, share, or re-load calculations directly into the calculator.

- **⚙️ High Precision & Settings Customization**
  - Powered by `BigDecimal` for arbitrary-precision arithmetic without floating-point inaccuracies.
  - Customizable decimal precision (0 to 6 places) and rounding modes (Half-Up, Half-Even, Floor, Ceiling).
  - Toggles for 3D diagram, step-by-step display, formulas card, dark theme, and haptic feedback.

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin](https://kotlinlang.org/) 2.2.10
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Navigation:** Jetpack Navigation Compose
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture Principles
- **State Management:** Kotlin Coroutines & `StateFlow`
- **Precision Engine:** Custom `BigDecimalMath` provider
- **Build System:** Gradle with Version Catalogs (`gradle/libs.versions.toml`) & AGP 9.4.1

---

## 📁 Project Structure

```
com.example.boxmeasure/
├── data/
│   ├── model/          # History data models
│   └── repository/     # History & Settings Data Repositories
├── domain/
│   ├── math/           # BigDecimal calculation engine, validators, & step generators
│   └── model/          # Dimension units, calculation result models, solve modes
├── ui/
│   ├── about/          # About screen & app information
│   ├── components/     # Reusable Compose UI components (3D BoxDiagram, ResultCards, Inputs, Unit sheets)
│   ├── history/        # History list screen with search & filtering
│   ├── home/           # Main Box Calculator UI & ViewModel
│   ├── navigation/     # App Navigation host & bottom nav bar
│   ├── settings/       # App preferences & calculation settings screen
│   ├── splash/         # Animated splash screen
│   └── theme/          # Material 3 colors, typography, & theme configuration
└── MainActivity.kt     # App entry point
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer recommended.
- JDK 11 or higher.
- Android SDK 37 (Min SDK 26 / Android 8.0 Oreo).

### Installation & Build
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/BoxMeasure.git
   cd BoxMeasure
   ```
2. Open the project in Android Studio.
3. Sync Gradle and build the app:
   ```bash
   ./gradlew assembleDebug
   ```
4. Run on an emulator or connected Android device:
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Running Unit Tests

The repository includes test suites covering math precision, unit conversions, box formulas, and settings persistence:

```bash
./gradlew test
```

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
